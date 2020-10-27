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

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.AbstractMultifactorAuthenticationProvider;
import org.apereo.cas.services.RegisteredService;

public class EsupNfcMultifactorAuthenticationProvider extends AbstractMultifactorAuthenticationProvider {

	private static final long serialVersionUID = 1L;
	
    @Override
    public String getId() {
        return StringUtils.defaultIfBlank(super.getId(), "mfa-esup-nfc");
    }

    @Override
    public boolean isAvailable(final RegisteredService service) {
        return true;
    }
    
	@Override
	public String getFriendlyName() {
		return "esup-nfc";
	}

}
