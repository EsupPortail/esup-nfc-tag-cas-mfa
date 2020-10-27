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
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apereo.cas.authentication.adaptive.UnauthorizedAuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Transactional
@RequestMapping("/esupnfc/wsrest")
@Controller 
public class EsupNfcWsRestController {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
    @Autowired
    private EsupNfcTokenService esupNfcTokenService;
    
    @Autowired
    private EsupNfcLongPollController esupNfcLongPollController;
    
    @Autowired
    private EsupNfcCasConfigurationProperties esupNfcCasConfigurationProperties;
	
	/**
	 * Example :
	 * curl -v -H "Content-Type: application/json" http://localhost:8080/esupnfc/wsrest/locations?eppn=joe@univ-ville.fr
	 */
	@RequestMapping(value="/locations",  method=RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public List<String> getLocations(@RequestParam String eppn, HttpServletRequest request) {    
		checkClientIsEsupNfcTagServer(request);
		List<String> sessionLocations = Arrays.asList(new String[] {esupNfcCasConfigurationProperties.getEsupNfcTagLocationName()});;	
	    return sessionLocations;
	}
	
    /**                                                                                                                                                                                       
     * Example :                                                                                                                                                                              
     * curl -v -X POST -H "Content-Type: application/json" -d '{"eppn":"joe@univ-ville.fr","location":"SHS - Amphi120", "eppnInit":"jack@univ-ville.fr"}' http://localhost:8080/esupnfc/wsrest/isTagable                                                                                                                                                                                       
     * @throws ParseException                                                                                                                                                                 
     */
    @RequestMapping(value="/isTagable",  method=RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ResponseEntity<String> isTagable(@RequestBody EsupNfcTagLog esupNfcTagLog, HttpServletRequest request) throws ParseException {
    	checkClientIsEsupNfcTagServer(request);
        HttpHeaders responseHeaders = new HttpHeaders();
        String eppn = esupNfcTagLog.getEppn();
        String uid = eppn.replaceAll("@.*", "");

        boolean isTagable =  esupNfcTokenService.getToken(uid) != null ;
        
        if(isTagable){
        	log.info("Initialisation du badgeage de {} OK : MFA CAS en cours ... en attente de validation de l'utilisateur", uid);
            return new ResponseEntity<String>("OK", responseHeaders, HttpStatus.OK);
        } else {
            log.error("Erreur 'isTagable' des Ws Rest pour l'eppn : " + eppn );
            return new ResponseEntity<String>("Pas de MFA CAS en cours", responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	/**
	 * Example :
	 * curl -v -X POST -H "Content-Type: application/json" -d '{"eppn":"joe@univ-ville.fr","location":"SHS - Amphi120", "eppnInit":"jack@univ-ville.fr"}' http://localhost:8080/esupnfc/wsrest/validateTag
	 * @throws ParseException 
	 */
	@RequestMapping(value="/validateTag",  method=RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public ResponseEntity<String> validateTag(@RequestBody EsupNfcTagLog esupNfcTagLog, HttpServletRequest request) throws ParseException {
		checkClientIsEsupNfcTagServer(request);
		HttpHeaders responseHeaders = new HttpHeaders();
		String eppn = esupNfcTagLog.getEppn();
		String uid = eppn.replaceAll("@.*", "");
		log.info("Validation de l'authentification de {} OK", uid);
		esupNfcLongPollController.handleCard(esupNfcTagLog);
		return new ResponseEntity<String>(eppn + " MFA CAS", responseHeaders, HttpStatus.OK);
	}
	

	@RequestMapping(value="/display",  method=RequestMethod.POST)
	@ResponseBody
	public String display(@RequestBody EsupNfcTagLog taglog, HttpServletRequest request) {
		checkClientIsEsupNfcTagServer(request);
		String uid = taglog.getEppn().replaceAll("@.*", "");
		String token = esupNfcTokenService.getToken(uid);
		log.info("Affichage du token {} après badgeage de {}", token, uid);
	    return String.format("<h1>Code :</h1><p>%s</p>", token);
	}

	protected void checkClientIsEsupNfcTagServer(HttpServletRequest request) {
		String ip = request.getRemoteAddr();
		if(ip == null || esupNfcCasConfigurationProperties.getEsupNfcTagServersIP()==null || !esupNfcCasConfigurationProperties.getEsupNfcTagServersIP().contains(ip)) {
			log.info("IP {} is not known as Esup-Nfc-Tag-Server IP ; list of EsupNfcTagServersIP is : {}", ip, esupNfcCasConfigurationProperties.getEsupNfcTagServersIP());
			throw new UnauthorizedAuthenticationException("{} is not a esup-nfc-tag server");
        } 
		
	}

	
}
