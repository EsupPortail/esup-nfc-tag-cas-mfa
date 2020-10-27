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

import java.io.Serializable;
import java.util.Map;

import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.ticket.ExpirationPolicy;
import org.apereo.cas.ticket.ExpirationPolicyBuilder;
import org.apereo.cas.ticket.Ticket;
import org.apereo.cas.ticket.TicketFactory;
import org.apereo.cas.ticket.TransientSessionTicket;
import org.apereo.cas.ticket.TransientSessionTicketFactory;
import org.apereo.cas.ticket.TransientSessionTicketImpl;
import org.apereo.cas.ticket.UniqueTicketIdGenerator;


public class EsupNfcMultifactorAuthenticationTicketFactory implements TransientSessionTicketFactory {
    
    /**
     * MFA ticket prefix.
     */
    public static final String PREFIX = "";

    private final ExpirationPolicyBuilder expirationPolicyBuilder;

    private final UniqueTicketIdGenerator ticketIdGenerator;

    
    public EsupNfcMultifactorAuthenticationTicketFactory(ExpirationPolicyBuilder expirationPolicyBuilder,
			UniqueTicketIdGenerator ticketIdGenerator) {
		super();
		this.expirationPolicyBuilder = expirationPolicyBuilder;
		this.ticketIdGenerator = ticketIdGenerator;
	}

	/**
     * Create delegated authentication request ticket.
     *
     * @param service    the service
     * @param properties the properties
     * @return the delegated authentication request ticket
     */
    @Override
    public TransientSessionTicket create(final Service service, final Map<String, Serializable> properties) {
        String id = ticketIdGenerator.getNewTicketId(PREFIX);
        ExpirationPolicy expirationPolicy = TransientSessionTicketFactory.buildExpirationPolicy(this.expirationPolicyBuilder, properties);
        return new TransientSessionTicketImpl(id, expirationPolicy, service, properties);
    }

    @Override
    public TransientSessionTicket create(final String id, final Map<String, Serializable> properties) {
    	ExpirationPolicy expirationPolicy = TransientSessionTicketFactory.buildExpirationPolicy(expirationPolicyBuilder, properties);
        return new TransientSessionTicketImpl(TransientSessionTicketFactory.normalizeTicketId(id),
            expirationPolicy, null, properties);
    }

    @Override
    public TicketFactory get(final Class<? extends Ticket> clazz) {
        return this;
    }
}
