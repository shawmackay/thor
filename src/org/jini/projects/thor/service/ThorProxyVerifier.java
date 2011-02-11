/*
   Copyright 2006 thor.jini.org Project

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

/*
 * thor.jini.org : org.jini.projects.thor.service
 * 
 * 
 * ThorProxyVerifier.java
 * Created on 02-Jun-2004
 * 
 * ThorProxyVerifier
 *
 */
package org.jini.projects.thor.service;

import java.io.Serializable;
import java.rmi.RemoteException;

import net.jini.core.constraint.MethodConstraints;
import net.jini.core.constraint.RemoteMethodControl;
import net.jini.id.Uuid;
import net.jini.security.TrustVerifier;
import net.jini.security.proxytrust.TrustEquivalence;

import org.jini.projects.thor.service.constrainable.ThorServiceProxy;

/**
 * @author calum
 */
public class ThorProxyVerifier implements TrustVerifier, Serializable {
	
	private RemoteMethodControl theService;
	private Uuid proxyID;
	
	ThorProxyVerifier(ThorService server, Uuid proxyID){
		if(!(server instanceof RemoteMethodControl))
			throw new UnsupportedOperationException("Service stub does not support RemoteMethodControl");
		if(!(server instanceof TrustEquivalence))
			throw new UnsupportedOperationException("Service stub does not support TrustEquivalence");
		theService = (RemoteMethodControl) server;
		this.proxyID= proxyID;
	}
	
	/* @see net.jini.security.TrustVerifier#isTrustedObject(java.lang.Object, net.jini.security.TrustVerifier.Context)
	 */	
	public boolean isTrustedObject(Object obj, Context ctx) throws RemoteException {
		RemoteMethodControl thirdPartyService;
		Uuid thirdPartyUuid;
		if(obj instanceof ThorServiceProxy.ConstrainableProxy){
			ThorServiceProxy.ConstrainableProxy testProxy = (ThorServiceProxy.ConstrainableProxy) obj;
			thirdPartyService = (RemoteMethodControl)testProxy;
			thirdPartyUuid = testProxy.getReferentUuid();
		} else if (obj instanceof ThorService && obj instanceof RemoteMethodControl){
			thirdPartyService = (RemoteMethodControl) obj;
			thirdPartyUuid = proxyID;
		} else {
			return false;
		}
		if(! proxyID.equals(thirdPartyUuid))
			return false;
		MethodConstraints theConstraints = thirdPartyService.getConstraints();
		TrustEquivalence constrainedStub = (TrustEquivalence) theService.setConstraints(theConstraints);
		return constrainedStub.checkTrustEquivalence(thirdPartyService);		
	}
}
