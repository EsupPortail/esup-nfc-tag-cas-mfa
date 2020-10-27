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

import java.security.GeneralSecurityException;
import java.util.Map;

import javax.security.auth.login.FailedLoginException;

import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.Authentication;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.ticket.TransientSessionTicket;
import org.apereo.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EsupNfcMultifactorAuthenticationHandler extends AbstractPreAndPostProcessingAuthenticationHandler {
	
    private final CentralAuthenticationService centralAuthenticationService;
    
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    
    public EsupNfcMultifactorAuthenticationHandler(final String name,
                                                     final ServicesManager servicesManager,
                                                     final PrincipalFactory principalFactory,
                                                     final CentralAuthenticationService centralAuthenticationService,
                                                     final Integer order) {
        super(name, servicesManager, principalFactory, order);
        this.centralAuthenticationService = centralAuthenticationService;
    }

    @Override
    protected AuthenticationHandlerExecutionResult doAuthentication(final Credential credential) throws GeneralSecurityException {
    	EsupNfcMultifactorTokenCredential tokenCredential = (EsupNfcMultifactorTokenCredential) credential;
        LOGGER.debug("Received token [{}]", tokenCredential.getId());

        Authentication authentication = WebUtils.getInProgressAuthentication();
        String uid = authentication.getPrincipal().getId();

        LOGGER.debug("Received principal id [{}]. Attempting to locate token in registry...", uid);
        TransientSessionTicket acct = this.centralAuthenticationService.getTicket(tokenCredential.getId(), TransientSessionTicket.class);

        if (acct == null) {
            LOGGER.warn("Authorization of token [{}] has failed. Token is not found in registry", tokenCredential.getId());
            throw new FailedLoginException("Failed to authenticate code " + tokenCredential.getId());
        }
        Map<String,Object>  properties = acct.getProperties();
        if (!properties.containsKey("principal")) {
            LOGGER.warn("Unable to locate principal for token [{}]", tokenCredential.getId());
            deleteToken(acct);
            throw new FailedLoginException("Failed to authenticate code " + tokenCredential.getId());
        }
        Principal principal = Principal.class.cast(properties.get("principal"));
        if (!principal.equals(authentication.getPrincipal())) {
            LOGGER.warn("Principal assigned to token [{}] is unauthorized for of token [{}]", principal.getId(), tokenCredential.getId());
            deleteToken(acct);
            throw new FailedLoginException("Failed to authenticate code " + tokenCredential.getId());
        }
        if (acct.isExpired()) {
            LOGGER.warn("Authorization of token [{}] has failed. Token found in registry has expired", tokenCredential.getId());
            deleteToken(acct);
            throw new FailedLoginException("Failed to authenticate code " + tokenCredential.getId());
        }
        deleteToken(acct);

        LOGGER.debug("Validated token [{}] successfully for [{}]. Creating authentication result and building principal...", tokenCredential.getId(), uid);
        return createHandlerResult(tokenCredential, this.principalFactory.createPrincipal(uid));
    }

    /**
     * Delete token.
     *
     * @param acct the acct
     */
    protected void deleteToken(final TransientSessionTicket acct) {
        this.centralAuthenticationService.deleteTicket(acct.getId());
    }

    @Override
    public boolean supports(final Credential credential) {
        return EsupNfcMultifactorTokenCredential.class.isAssignableFrom(credential.getClass());
    }

    @Override
    public boolean supports(final Class<? extends Credential> clazz) {
        return EsupNfcMultifactorTokenCredential.class.isAssignableFrom(clazz);
    }
}
