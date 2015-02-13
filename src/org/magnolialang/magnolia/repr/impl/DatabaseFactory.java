package org.magnolialang.magnolia.repr.impl;

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.DB;
import com.mongodb.MongoClient;

/**
 * Factory class to get a database connection.
 */
public class DatabaseFactory {

	private static DB db = null;
	private static MongoClient mongoClient;


//	private static void close() {
//		mongoClient.close();
//		mongoClient = null;
//		db = null;
//	}


	public static DB getDb() {
		if(db == null) {
			init();
		}
		return db;
	}


//	public static MongoClient getMongoClient() {
//		if(mongoClient == null) {
//			init();
//		}
//		return mongoClient;
//	}


	private static void init() {
		try {
			mongoClient = new MongoClient("localhost", 27017);
			db = mongoClient.getDB("astdb");
		}
		catch(UnknownHostException ex) {
			Logger.getLogger(DatabaseFactory.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
