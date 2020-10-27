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

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.configurer.AbstractCasMultifactorWebflowConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;


public class EsupNfcMultifactorWebflowConfigurer extends AbstractCasMultifactorWebflowConfigurer {
	
    public static final String MFA_EVENT_ID = "mfa-esup-nfc";
    
    private final FlowDefinitionRegistry flowDefinitionRegistry;
    
    public EsupNfcMultifactorWebflowConfigurer(final FlowBuilderServices flowBuilderServices,
            final FlowDefinitionRegistry loginFlowDefinitionRegistry,
            final FlowDefinitionRegistry flowDefinitionRegistry,
            final ApplicationContext applicationContext,
            final CasConfigurationProperties casProperties) {
		super(flowBuilderServices, loginFlowDefinitionRegistry,
		applicationContext, casProperties);
		this.flowDefinitionRegistry = flowDefinitionRegistry;
		}
		
		@Override
		protected void doInitialize() {
			registerMultifactorProviderAuthenticationWebflow(getLoginFlow(), MFA_EVENT_ID, this.flowDefinitionRegistry, MFA_EVENT_ID);
		}

}
