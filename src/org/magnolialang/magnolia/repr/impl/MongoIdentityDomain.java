package org.magnolialang.magnolia.repr.impl;

import java.util.List;
import java.util.Map;

import org.magnolialang.magnolia.repr.Identity;

public class MongoIdentityDomain {

	public static MongoIdentityDomain getInstance(String domainName) {
		if(domain != null && domainName == domain && instance != null) {
			return instance;
		}

		if(instance == null) {
			instance = new MongoIdentityDomain(domainName);
		}
		return instance;
	}

	private List<Identity> idents;
	private Map<Identity, Integer> map;
	private static MongoIdentityDomain instance = null;
	private static String domain;


	protected MongoIdentityDomain(String domainName) {
		domain = domainName;
		// get the domain from MongoDB.

		// if it doesn't exist, create it
	}


	public synchronized Identity toIdentity(int i) {
		return idents.get(i);
	}


	public synchronized Identity toIdentityOrNull(int i) {
		if(i >= 0 && i < idents.size()) {
			return toIdentity(i);
		}
		else {
			return null;
		}
	}


	public synchronized int toInt(Identity id) {
		Integer i = map.get(id);
		if(i == null) {
			i = idents.size();
			idents.add(id);
			map.put(id, i);
		}
		return i;
	}


}
