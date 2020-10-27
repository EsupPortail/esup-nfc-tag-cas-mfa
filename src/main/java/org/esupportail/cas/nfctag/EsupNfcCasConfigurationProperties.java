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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(ignoreResourceNotFound = true, value={"classpath:esupnfc.properties", "file:/var/cas/config/esupnfc.properties", "file:/opt/cas/config/esupnfc.properties", "file:/etc/cas/config/esupnfc.properties", "file:${cas.standalone.configurationDirectory}/esupnfc.properties"})
@ConfigurationProperties(prefix = "esupnfc", ignoreUnknownFields = false)
public class EsupNfcCasConfigurationProperties implements InitializingBean {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	List<String> esupNfcTagServersIP;
	
	Boolean autologin = true;
	
	String esupNfcTagLocationName = "MFA CAS";
	
	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("esupnfc.esupNfcTagServersIP : {}", this.getEsupNfcTagServersIP());
		log.info("esupnfc.autologin : {}", this.getAutologin());
		log.info("esupnfc.esupNfcTagLocationName : {}", this.getEsupNfcTagLocationName());
		if(this.getEsupNfcTagServersIP()==null || this.getEsupNfcTagServersIP().isEmpty()) {
			log.warn("List of EsupNfcTagServersIP is empty : Please configure the property esupnfc.esupNfcTagServersIP in esupnfc.properties file");
		} 
	}

	public List<String> getEsupNfcTagServersIP() {
		return esupNfcTagServersIP;
	}

	public void setEsupNfcTagServersIP(List<String> esupNfcTagServersIP) {
		this.esupNfcTagServersIP = esupNfcTagServersIP;
	}

	public Boolean getAutologin() {
		return autologin;
	}

	public void setAutologin(Boolean autologin) {
		this.autologin = autologin;
	}

	public String getEsupNfcTagLocationName() {
		return esupNfcTagLocationName;
	}

	public void setEsupNfcTagLocationName(String esupNfcTagLocationName) {
		this.esupNfcTagLocationName = esupNfcTagLocationName;
	}

}
