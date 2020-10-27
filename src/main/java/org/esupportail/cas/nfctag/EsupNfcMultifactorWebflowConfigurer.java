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
import java.util.Optional;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.configurer.AbstractCasMultifactorWebflowConfigurer;
import org.apereo.cas.web.flow.configurer.CasMultifactorWebflowCustomizer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;


public class EsupNfcMultifactorWebflowConfigurer extends AbstractCasMultifactorWebflowConfigurer {
	
    public static final String MFA_EVENT_ID = "mfa-esup-nfc";
    
    public EsupNfcMultifactorWebflowConfigurer(final FlowBuilderServices flowBuilderServices,
            final FlowDefinitionRegistry loginFlowDefinitionRegistry,
            final FlowDefinitionRegistry flowDefinitionRegistry,
            final ConfigurableApplicationContext applicationContext,
            final CasConfigurationProperties casProperties,
            final List<CasMultifactorWebflowCustomizer> mfaFlowCustomizers) {
		super(flowBuilderServices, loginFlowDefinitionRegistry,
		applicationContext, casProperties, Optional.of(flowDefinitionRegistry),
		mfaFlowCustomizers);
		}
		
		@Override
		protected void doInitialize() {
			registerMultifactorProviderAuthenticationWebflow(getLoginFlow(), MFA_EVENT_ID, MFA_EVENT_ID);
		}

}
