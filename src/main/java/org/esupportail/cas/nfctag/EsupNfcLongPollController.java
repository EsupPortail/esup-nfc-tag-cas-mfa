/*******************************************************************************
 * Licensed to ESUP-Portail under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 * 
 * ESUP-Portail licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.esupportail.cas.nfctag;

import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apereo.cas.authentication.adaptive.UnauthorizedAuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

@Transactional
@RequestMapping("/esupnfc")
@Controller("esupNfcLongPollController")
public class EsupNfcLongPollController {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
    @Autowired
    private EsupNfcTokenService esupNfcTokenService;
    
    @Autowired
    private EsupNfcCasConfiguration esupNfcCasConfiguration;
    
    // Map avec en clef l'uid de l'utilisateur manager potentiel badgeur -> pas plus d'un searchPoll par utilisateur.                                                                        
    private Map<String, DeferredResult<String>> suspendedMfaPollRequests = new ConcurrentHashMap<String, DeferredResult<String>>();

    @RequestMapping
    @ResponseBody
    public DeferredResult<String> swipePoll(@RequestParam String uid) {
       
    	if(!esupNfcCasConfiguration.getAutologin()) {
    		throw new UnauthorizedAuthenticationException("swipePoll is called but esupnfc.autologin property is not true");
    	}
    	
        final DeferredResult<String> mfaUid = new DeferredResult<String>(null, "");

        if(this.suspendedMfaPollRequests.containsKey(uid)) {
            this.suspendedMfaPollRequests.get(uid).setResult("stop");
        }
        this.suspendedMfaPollRequests.put(uid, mfaUid);

        mfaUid.onCompletion(new Runnable() {
            public void run() {
                synchronized (mfaUid) {
                    if(mfaUid.equals(suspendedMfaPollRequests.get(uid))) {
                    	suspendedMfaPollRequests.remove(uid, mfaUid);
                    }
                }
            }
        });

        // log.info("this.suspendedSearchPollRequests.size : " + this.suspendedSearchPollRequests.size());                                                                                    
        return mfaUid;
    }

    public void handleCard(EsupNfcTagLog esupNfcTagLog) throws ParseException {
    	String eppn = esupNfcTagLog.getEppn();
		String uid = eppn.replaceAll("@.*", "");
        log.debug("handleCard : " + " for " + uid);
        if(this.suspendedMfaPollRequests.containsKey(uid)) {
        	String tokenId = esupNfcTokenService.getToken(uid);
            this.suspendedMfaPollRequests.get(uid).setResult(tokenId);
        }
    }
	
}
