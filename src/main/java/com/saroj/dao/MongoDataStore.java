/**
 * 
 */
package com.saroj.dao;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;
import com.saroj.controller.ServiceEventRequest;

/**
 * @author sarojrout
 *
 */
public class MongoDataStore implements DataStore {

	private static DataStore mongoDataStore;
	private static DBCollection rawEventsColl;
	public static final String COLLECTION_NAME = "events";
	@Autowired
	private MongoTemplate mongoTemplate;

	private MongoDataStore() {
	};

	/**
	 * 
	 * @param mongoHost
	 * @param mongoPort
	 * @return
	 * @throws UnknownHostException
	 */
	public static DataStore getInstance(String mongoHost, int mongoPort)
			throws UnknownHostException {
		synchronized (MongoDataStore.class) {
			if (mongoDataStore == null) {
				MongoClient mongoClient = new MongoClient(mongoHost, mongoPort);
				DB db = mongoClient.getDB("stream");
				rawEventsColl = db.getCollection("events");
				mongoDataStore = new MongoDataStore();
			}
		}

		return mongoDataStore;
	}

	public boolean storeRawEvent(String jsonData) {
		DBObject rawEvent = (DBObject) JSON.parse(jsonData);
		boolean success = false;
		if (rawEventsColl.insert(rawEvent) != null) {
			success = true;
		}
		return success;
	}

	@Override
	public List<ServiceEventRequest> getAll() {

		BasicDBObject query = new BasicDBObject();
		DBCursor cursor = rawEventsColl.find(query);
		DBObject data = null;
		List<ServiceEventRequest> list = new ArrayList<ServiceEventRequest>();
		while (cursor.hasNext()) {
			data = cursor.next();

			System.out.println("PRINT data::" + data);
			ServiceEventRequest request = new ServiceEventRequest();
			request.setName((String) data.get("name"));
			list.add(request);
		}
		System.out.println("PRINT data after while::" + list);
		return list;
	}

}
