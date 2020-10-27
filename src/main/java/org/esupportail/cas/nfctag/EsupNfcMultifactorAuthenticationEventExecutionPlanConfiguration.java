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

import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationMetaDataPopulator;
import org.apereo.cas.authentication.MultifactorAuthenticationFailureModeEvaluator;
import org.apereo.cas.authentication.MultifactorAuthenticationProvider;
import org.apereo.cas.authentication.bypass.MultifactorAuthenticationProviderBypassEvaluator;
import org.apereo.cas.authentication.handler.ByCredentialTypeAuthenticationHandlerResolver;
import org.apereo.cas.authentication.metadata.AuthenticationContextAttributeMetaDataPopulator;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration("esupNfcMultifactorAuthenticationEventExecutionPlanConfiguration")
public class EsupNfcMultifactorAuthenticationEventExecutionPlanConfiguration {
	
    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("servicesManager")
    private ObjectProvider<ServicesManager> servicesManager;

    @Autowired
    @Qualifier("esupNfcMultifactorBypassEvaluator")
    private ObjectProvider<MultifactorAuthenticationProviderBypassEvaluator> esupNfcMultifactorBypassEvaluator;

    @Autowired
    @Qualifier("failureModeEvaluator")
    private ObjectProvider<MultifactorAuthenticationFailureModeEvaluator> failureModeEvaluator;

    @Autowired
    @Qualifier("centralAuthenticationService")
    private ObjectProvider<CentralAuthenticationService> centralAuthenticationService;

    @ConditionalOnMissingBean(name = "esupNfcMultifactorAuthenticationHandler")
    @Bean
    @RefreshScope
    public AuthenticationHandler esupNfcMultifactorAuthenticationHandler() {
        return new EsupNfcMultifactorAuthenticationHandler("mfa-esup-nfc",
            servicesManager.getObject(), esupNfcMultifactorPrincipalFactory(),
            centralAuthenticationService.getObject(), 0);
    }

    @Bean
    @RefreshScope
    public MultifactorAuthenticationProvider esupNfcMultifactorAuthenticationProvider() {
    	EsupNfcMultifactorAuthenticationProvider p = new EsupNfcMultifactorAuthenticationProvider();
        p.setBypassEvaluator(esupNfcMultifactorBypassEvaluator.getObject());
        return p;
    }

    @Bean
    @RefreshScope
    @ConditionalOnMissingBean(name = "esupNfcMultifactorAuthenticationMetaDataPopulator")
    public AuthenticationMetaDataPopulator esupNfcMultifactorAuthenticationMetaDataPopulator() {
        return new AuthenticationContextAttributeMetaDataPopulator(
            casProperties.getAuthn().getMfa().getAuthenticationContextAttribute(),
            esupNfcMultifactorAuthenticationHandler(),
            esupNfcMultifactorAuthenticationProvider().getId()
        );
    }

    @ConditionalOnMissingBean(name = "esupNfcMultifactorPrincipalFactory")
    @Bean
    public PrincipalFactory esupNfcMultifactorPrincipalFactory() {
        return PrincipalFactoryUtils.newPrincipalFactory();
    }

    @ConditionalOnMissingBean(name = "esupNfcMultifactorAuthenticationEventExecutionPlanConfigurer")
    @Bean
    @RefreshScope
    public AuthenticationEventExecutionPlanConfigurer esupNfcMultifactorAuthenticationEventExecutionPlanConfigurer() {
        return plan -> {
            plan.registerAuthenticationHandler(esupNfcMultifactorAuthenticationHandler());
            plan.registerAuthenticationMetadataPopulator(esupNfcMultifactorAuthenticationMetaDataPopulator());
            plan.registerAuthenticationHandlerResolver(new ByCredentialTypeAuthenticationHandlerResolver(EsupNfcMultifactorTokenCredential.class));
        };
    }

}
