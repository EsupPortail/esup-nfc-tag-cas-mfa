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
import org.apereo.cas.ticket.ExpirationPolicy;
import org.apereo.cas.ticket.TransientSessionTicketFactory;
import org.apereo.cas.ticket.UniqueTicketIdGenerator;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.ticket.support.HardTimeoutExpirationPolicy;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.CasWebflowExecutionPlan;
import org.apereo.cas.web.flow.CasWebflowExecutionPlanConfigurer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.webflow.config.FlowDefinitionRegistryBuilder;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.execution.Action;

@Configuration("esupNfcCasConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class EsupNfcCasConfiguration implements CasWebflowExecutionPlanConfigurer {
	
    @Autowired
    private CasConfigurationProperties casProperties;
	
    @Autowired
    private EsupNfcCasConfigurationProperties esupNfcCasConfigurationProperties;
    
    @Autowired
    @Qualifier("ticketRegistry")
    private ObjectProvider<TicketRegistry> ticketRegistry;
    
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    @Qualifier("loginFlowRegistry")
    private ObjectProvider<FlowDefinitionRegistry> loginFlowDefinitionRegistry;

    @Autowired
    private FlowBuilderServices flowBuilderServices;
    
    @Bean
    public FlowDefinitionRegistry esupNfcFlowRegistry() {
        final FlowDefinitionRegistryBuilder builder = new FlowDefinitionRegistryBuilder(applicationContext, flowBuilderServices);
        builder.setBasePath("classpath*:/webflow");
        builder.addFlowLocationPattern("/mfa-esup-nfc/*-webflow.xml");
        return builder.build();
    }

    @ConditionalOnMissingBean(name = "esupNfcWebflowConfigurer")
    @Bean
    @DependsOn("defaultWebflowConfigurer")
    public CasWebflowConfigurer esupNfcWebflowConfigurer() {
        return new EsupNfcMultifactorWebflowConfigurer(flowBuilderServices,
                loginFlowDefinitionRegistry.getIfAvailable(), esupNfcFlowRegistry(),
                applicationContext, casProperties);
    }
    
    @Override
    public void configureWebflowExecutionPlan(final CasWebflowExecutionPlan plan) {
        plan.registerWebflowConfigurer(esupNfcWebflowConfigurer());
    }
    
    
    @ConditionalOnMissingBean(name = "esupNfcMultifactorSendTokenAction")
    @Bean
    @RefreshScope
    public Action esupNfcMultifactorSendTokenAction() {
        return new EsupNfcGenerateTokenAction(ticketRegistry.getObject(), esupNfcMultifactorAuthenticationTicketFactory(), esupNfcTokenService());
    }

    @ConditionalOnMissingBean(name = "esupNfcMultifactorAuthenticationUniqueTicketIdGenerator")
    @Bean
    @RefreshScope
    public UniqueTicketIdGenerator esupNfcMultifactorAuthenticationUniqueTicketIdGenerator() {
        return new EsupNfcMultifactorAuthenticationUniqueTicketIdGenerator();
    }
    
    @ConditionalOnMissingBean(name = "esupNfcMultifactorAuthenticationTicketExpirationPolicy")
    @Bean
    public ExpirationPolicy esupNfcMultifactorAuthenticationTicketExpirationPolicy() {
        return new HardTimeoutExpirationPolicy(600000);
    }
    
    @ConditionalOnMissingBean(name = "esupNfcMultifactorAuthenticationTicketFactory")
    @Bean
    public TransientSessionTicketFactory esupNfcMultifactorAuthenticationTicketFactory() {
        return new EsupNfcMultifactorAuthenticationTicketFactory(esupNfcMultifactorAuthenticationTicketExpirationPolicy(),
        		esupNfcMultifactorAuthenticationUniqueTicketIdGenerator());
    }
    
    @ConditionalOnMissingBean(name = "esupNfcTokenService")
    @Bean
    public EsupNfcTokenService esupNfcTokenService() {
    	return new EsupNfcTokenService();
    }
    
    public Boolean getAutologin() {
    	return  esupNfcCasConfigurationProperties.getAutologin();
    }
    
}

