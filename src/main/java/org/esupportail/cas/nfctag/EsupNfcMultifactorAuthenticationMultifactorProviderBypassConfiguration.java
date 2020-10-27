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

import org.apereo.cas.authentication.bypass.DefaultChainingMultifactorAuthenticationBypassProvider;
import org.apereo.cas.authentication.bypass.MultifactorAuthenticationProviderBypassEvaluator;
import org.apereo.cas.authentication.bypass.RegisteredServiceMultifactorAuthenticationProviderBypassEvaluator;
import org.apereo.cas.authentication.bypass.RegisteredServicePrincipalAttributeMultifactorAuthenticationProviderBypassEvaluator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("esupNfcMultifactorAuthenticationMultifactorProviderBypassConfiguration")
public class EsupNfcMultifactorAuthenticationMultifactorProviderBypassConfiguration {

    @ConditionalOnMissingBean(name = "esupNfcMultifactorBypassEvaluator")
    @Bean
    @RefreshScope
    public MultifactorAuthenticationProviderBypassEvaluator esupNfcMultifactorBypassEvaluator() {
    	DefaultChainingMultifactorAuthenticationBypassProvider bypass = new DefaultChainingMultifactorAuthenticationBypassProvider();    
    	 bypass.addMultifactorAuthenticationProviderBypassEvaluator(esupNfcMultifactorRegisteredServiceMultifactorAuthenticationProviderBypass());
         bypass.addMultifactorAuthenticationProviderBypassEvaluator(
             esupNfcRegisteredServicePrincipalAttributeMultifactorAuthenticationProviderBypassEvaluator());
        return bypass;
    }
    	
    @ConditionalOnMissingBean(name = "esupNfcRegisteredServicePrincipalAttributeMultifactorAuthenticationProviderBypassEvaluator")
    @Bean
    @RefreshScope
    public MultifactorAuthenticationProviderBypassEvaluator esupNfcRegisteredServicePrincipalAttributeMultifactorAuthenticationProviderBypassEvaluator() {
        return new RegisteredServicePrincipalAttributeMultifactorAuthenticationProviderBypassEvaluator("mfa-esup-nfc");
    }
    
    @Bean
    @RefreshScope
    @ConditionalOnMissingBean(name = "esupNfcMultifactorRegisteredServiceMultifactorAuthenticationProviderBypass")
    public MultifactorAuthenticationProviderBypassEvaluator esupNfcMultifactorRegisteredServiceMultifactorAuthenticationProviderBypass() {;
        return new RegisteredServiceMultifactorAuthenticationProviderBypassEvaluator("mfa-esup-nfc");
    }
	

}
