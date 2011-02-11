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
 * thor : org.jini.projects.org.jini.projects.thor.service.leasing
 * 
 * 
 * ChangeLandlord.java Created on 22-Dec-2003
 * 
 * ChangeLandlord
 *  
 */

package org.jini.projects.thor.service.leasing;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.lease.LeaseMapException;
import net.jini.core.lease.UnknownLeaseException;
import net.jini.export.Exporter;
import net.jini.id.ReferentUuid;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;

import com.sun.jini.constants.TimeConstants;
import com.sun.jini.landlord.FixedLeasePeriodPolicy;
import com.sun.jini.landlord.Landlord;
import com.sun.jini.landlord.LeaseFactory;
import com.sun.jini.landlord.LeasePeriodPolicy;
import com.sun.jini.landlord.LeasedResource;

/**
 * @author calum
 */
public class ChangeLandlord implements Landlord, ReferentUuid, Remote {
	protected Map leases = new HashMap();
	protected Map handlers = new HashMap();
	private Logger l = Logger.getLogger("org.jini.projects.thor.leasing");
	Uuid myId = null;
	public static boolean DEBUG = true;
	protected LeaseFactory myFactory;
	protected LeasePeriodPolicy myGrantPolicy = new FixedLeasePeriodPolicy(1 * TimeConstants.MINUTES, 30 * TimeConstants.SECONDS);
	private Reaper reaper = new Reaper();
	private static int LeaseCount = 0;

	/**
	 *  
	 */
	public ChangeLandlord() {
		super();
		// URGENT Complete constructor stub for ChangeLandlord
		myId = UuidFactory.generate();
		Exporter exp = new BasicJeriExporter(TcpServerEndpoint.getInstance(0), new BasicILFactory());
		try {
			myFactory = new LeaseFactory((Landlord) exp.export(this), myId);
		} catch (ExportException e) {
			e.printStackTrace();
		}
		l.fine("MessageLandlord Initialised");
		reaper.start();
	}

	public LeaseHolder newLease(RemoteEventListener interestedParty, LeaseHandler handler, long reqduration) throws LeaseDeniedException {
		l.finer("New Lease Requested");
		long now = System.currentTimeMillis();
//		Integer cookie;
//		synchronized (ChangeLandlord.class) {
//			cookie = new Integer(LeaseCount);
//			LeaseCount++;
//		}
		Uuid regCookie = UuidFactory.generate();
		ChangeEventResource reg = new ChangeEventResource(interestedParty, regCookie);
		LeasePeriodPolicy.Result r = myGrantPolicy.grant(reg, reqduration);
		reg.setExpiration(r.expiration);
		leases.put(regCookie, reg);
		handlers.put(regCookie, handler);
		
		if (interestedParty == null)
			l.warning("No Listener sent");
		if (handler == null)
			l.warning("No handler passed");
		handler.register(regCookie, interestedParty);
		l.finest("Creating new Lease from Policy now");
		Lease lease =myFactory.newLease(regCookie, reg.getExpiration());
        return new LeaseHolder(lease,regCookie);
	}

	/*
	 * @see com.sun.jini.landlord.Landlord#renew(net.jini.id.Uuid, long)
	 */
	public long renew(Uuid cookie, long duration) throws LeaseDeniedException, UnknownLeaseException, RemoteException {
		// TODO Complete method stub for renew
		if (!leases.containsKey(cookie))
			throw new UnknownLeaseException();
		LeasedResource res = (LeasedResource) leases.get(cookie);
		LeasePeriodPolicy policy = myGrantPolicy;
		synchronized (res) {
			if (res.getExpiration() <= System.currentTimeMillis()) {
				// Lease has expired, don't renew
				throw new UnknownLeaseException("Lease expired");
			}
			// No one can expire the lease, so it is safe to update.
			final LeasePeriodPolicy.Result r = policy.renew(res, duration);
			res.setExpiration(r.expiration);
			return r.duration;
		}
	}

	public void killLease(Uuid obj) throws net.jini.core.lease.UnknownLeaseException {
		
			l.finest("Killing Lease ID: " + obj.toString());
		RemoteEventListener lconn = (RemoteEventListener) leases.get(obj);
		if (lconn == null) {
			throw new UnknownLeaseException("Unknown lease");
		}
		leases.remove(obj);
	}

	/*
	 * @see com.sun.jini.landlord.Landlord#cancel(net.jini.id.Uuid)
	 */
	public void cancel(Uuid cookie) throws UnknownLeaseException, RemoteException {
		// TODO Complete method stub for cancel
		killLease(cookie);
	}

	/*
	 * @see com.sun.jini.landlord.Landlord#renewAll(net.jini.id.Uuid[], long[])
	 */
	public RenewResults renewAll(Uuid[] cookies, long[] durations) throws RemoteException {
		boolean somethingDenied = false;
		long[] granted = new long[cookies.length];
		Exception[] denied = new Exception[cookies.length + 1];
		for (int i = 0; i < cookies.length; i++) {
			try {
				granted[i] = renew(cookies[i], durations[i]);
				denied[i] = null;
			} catch (Exception ex) {
				somethingDenied = true;
				granted[i] = -1;
				denied[i] = ex;
			}
		}
		if (!somethingDenied)
			denied = null;
		return new Landlord.RenewResults(granted, denied);
	}

	/*
	 * @see com.sun.jini.landlord.Landlord#cancelAll(net.jini.id.Uuid[])
	 */
	public Map cancelAll(Uuid[] cookies) throws RemoteException {
		Map exMap = null;
		LeaseMapException lmEx = null;
		for (int i = 0; i < cookies.length; i++) {
			try {
				cancel(cookies[i]);
			} catch (Exception e) {
				if (lmEx == null) {
					exMap = new HashMap();
					lmEx = new LeaseMapException(null, exMap);
				}
				exMap.put(myFactory.newLease(cookies[i], 0), e);
			}
		}
		if (lmEx != null)
			return exMap;
		else
			return null;
	}

	class Reaper extends Thread {
		public void run() {
			for (;;) {
				synchronized (leases) {
					try {
						List keystoremove = Collections.synchronizedList(new ArrayList());
						Iterator iter = leases.entrySet().iterator();
						int i = 1;
						while (iter.hasNext()) {
							Map.Entry leaseEntry = (Map.Entry) iter.next();
							Object obj = leaseEntry.getValue();
							//System.out.println("LeaseValue name
							// "+obj.getClass().getName());
							LeasedResource lconn = (LeasedResource) obj;
							if ((lconn.getExpiration() + (15 * TimeConstants.SECONDS)) < System.currentTimeMillis()) {
								Uuid leaseKey = (Uuid) leaseEntry.getKey();
								
									l.finer("...expiring a lease with key " + leaseKey);
								keystoremove.add(leaseKey);
							}
						}
						// System.out.println("KeysToRemove size: " +
						// keystoremove.size());
						for (Iterator removeIter = keystoremove.iterator(); removeIter.hasNext();) {
							
								l.finer("Deallocating");
							Uuid ref = (Uuid) removeIter.next();
							leases.remove(ref);
							LeaseHandler handler = (LeaseHandler) handlers.remove(ref);
							handler.remove(ref);
						}
						keystoremove.clear();
					} catch (Exception ex) {
						l.severe("Err: " + ex.getMessage());
						ex.printStackTrace();
					}
                    
                    Uuid myID = UuidFactory.create(123L, 4561L);
				}
				try {
					Thread.sleep(10 * 1000L);
					//Logger.getLogger("Reaper").info("waiting....");
				} catch (Exception ex) {
				    l.severe("Err: " + ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/*
	 * @see net.jini.id.ReferentUuid#getReferentUuid()
	 */
	public Uuid getReferentUuid() {
		// TODO Complete method stub for getReferentUuid
		return myId;
	}
}
