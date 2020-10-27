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

import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.authentication.principal.WebApplicationService;
import org.apereo.cas.ticket.TransientSessionTicket;
import org.apereo.cas.ticket.TransientSessionTicketFactory;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.action.EventFactorySupport;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class EsupNfcGenerateTokenAction extends AbstractAction {
    
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final TicketRegistry ticketRegistry;

    private final TransientSessionTicketFactory ticketFactory;
    
    private final EsupNfcTokenService esupNfcTokenService;

    public EsupNfcGenerateTokenAction(TicketRegistry ticketRegistry, TransientSessionTicketFactory ticketFactory, EsupNfcTokenService esupNfcTokenService) {
		super();
		this.ticketRegistry = ticketRegistry;
		this.ticketFactory = ticketFactory;
		this.esupNfcTokenService = esupNfcTokenService;
	}

	@Override
    protected Event doExecute(final RequestContext requestContext) {
        Authentication authentication = WebUtils.getInProgressAuthentication();
        Principal principal = authentication.getPrincipal();
        WebApplicationService service = WebUtils.getService(requestContext);
        TransientSessionTicket token = ticketFactory.create(service, CollectionUtils.wrap("principal", principal));
        LOGGER.debug("Created multifactor authentication token [{}] for service [{}]", token, service);

        ticketRegistry.addTicket(token);
        esupNfcTokenService.setToken(principal.getId(), token.getId());
        LOGGER.debug("Successfully generating token for [{}] to display on esup-nfc-tag", principal.getId());
       
        LocalAttributeMap attributes = new LocalAttributeMap("token", token.getId());
        return new EventFactorySupport().event(this, CasWebflowConstants.TRANSITION_ID_SUCCESS, attributes);
    }

}
