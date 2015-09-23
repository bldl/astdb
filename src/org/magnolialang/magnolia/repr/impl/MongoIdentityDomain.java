package org.magnolialang.magnolia.repr.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.magnolialang.magnolia.repr.Identity;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MongoIdentityDomain {

	public static class Domain {
		private String domainName;
		private final List<Identity> idents;
		private final Map<Identity, Integer> map;
		private DBCollection dbcollection;


		private Domain(String domainName) {
			// get the domain from MongoDB
			DB db = DatabaseFactory.getDb();
			DBCollection dbcollection = db.getCollection(domainName);

			BasicDBObject dbDomain = new BasicDBObject();
			dbDomain.append("_id", domainName);
			DBCursor dbc = dbcollection.find(dbDomain);

			if(dbc == null || !dbc.hasNext()) {
				// the domain doesn't exist yet, so we need to create it
				map = new IdentityHashMap<Identity, Integer>();
				idents = new ArrayList<>();
				return;
			}

			if(dbc.count() > 1) {
				throw new RuntimeException("2 domains matched to the same domainName, which shouldn't be possible!");
			}

			// convert it to the form we need (list/map).
			DBObject dbDomainObject = dbc.next();
			map = (Map<Identity, Integer>) dbDomainObject.get(MAP_KEY);
			idents = (List<Identity>) dbDomainObject.get(IDENTITY_KEY);
		}


		public synchronized void persistChanges() {
			// TODO optimize so that it only saves changed values, appending them.
			// 		a way to do this is to store how many Ids existed when we loaded, and then
			// 		adding any new ids that have been put
			BasicDBObject dbDomain = new BasicDBObject();
			dbDomain.append("_id", domainName);
			dbDomain.append(MAP_KEY, map);
			dbDomain.append(IDENTITY_KEY, idents);
			dbcollection.save(dbDomain);
		}


		public synchronized void persistIdentities(Identity... ids) {
			for(Identity id : ids) {
				toInt(id);
			}
			persistChanges();
		}


		/**
		 * Gets the Identity associated with an integer within this domain
		 * 
		 * @param i
		 *            the integer we're looking up
		 * @return Identity of the node corresponding to 'i' within this domain
		 */
		public synchronized Identity toIdentity(int i) {
			return idents.get(i);
		}


		/**
		 * Gets the Identity associated with an integer within this domain
		 * if it exists, otherwise null
		 * 
		 * @param i
		 *            the integer we're looking up
		 * @return Identity of the node corresponding to 'i' within this domain
		 *         OR null if such an Identity does not exist
		 */
		public synchronized Identity toIdentityOrNull(int i) {
			if(i >= 0 && i < idents.size()) {
				return toIdentity(i);
			}
			else {
				return null;
			}
		}


		/**
		 * Gets (or creates and returns) the integer associated with an Identity
		 * within this domain.
		 * 
		 * @param id
		 *            the Identity we want enumerated
		 * @return integer identity correspoding to the id in this domain
		 */
		public synchronized int toInt(Identity id) {
			if(id == null) {
				return -1;
			}

			Integer i = map.get(id);
			if(i == null) {
				i = idents.size();
				idents.add(id);
				map.put(id, i);
			}
			return i;
		}

	}


	private static String IDENTITY_KEY = "IDENTITIES_KEY", MAP_KEY = "MAP_KEY";


	private static Map<String, Domain> domains;


	/**
	 * Gets or creates a domain identified by "domainName" from mongoDB
	 * 
	 * @param domainName
	 *            the name of the domain
	 * @return Domain the domain identified by domainName
	 */
	public static Domain getDomainInstance(String domainName) {
		return new Domain(domainName);
	}


	@SuppressWarnings("unchecked")
	public MongoIdentityDomain() {
		domains = new HashMap<String, Domain>();
	}


	/**
	 * Gets or creates a domain identified by "domainName" from mongoDB.
	 * 
	 * This method stores the domains which have been lookup up locally, so that
	 * less DB accesses are needed over time.
	 * 
	 * @param domainName
	 *            the name of the domain
	 * @return Domain the domain identified by domainName
	 */
	public Domain getInstance(String domainName) {
		if(domains == null) {
			domains = new HashMap<String, Domain>();
		}

		if(domains.containsKey(domainName)) {
			return domains.get(domainName);
		}
		else {
			Domain instance = new Domain(domainName);
			domains.put(domainName, instance);
			return instance;
		}
	}
}
