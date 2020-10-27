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

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EsupNfcTokenService {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	// we keep only last 500 entries ... good timeout is already managet on tickets services
	final int maxSize = 500;
    final LinkedHashMap<String, String> tokens = new LinkedHashMap<String, String>() {
        @Override
        protected boolean removeEldestEntry(final Map.Entry eldest) {
            return size() > maxSize;
        }
    };
	
	public void setToken(String uid, String token) {
		tokens.put(uid, token);
	}
	
	public String getToken(String uid) {
		log.trace("tokens : {}", tokens);
		log.trace("token for {} -> {}", uid, tokens.get(uid));
		return tokens.get(uid);
	}

}
