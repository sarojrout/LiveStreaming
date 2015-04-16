/**
 * 
 */
package com.saroj.dao;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
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
	private static DBCollection dailyMetricsColl;
	private static DBCollection rawEventsColl;
	public static final String COLLECTION_NAME = "raw_events";
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
				dailyMetricsColl = db.getCollection("daily_metrics");
				rawEventsColl = db.getCollection("raw_events");
				mongoDataStore = new MongoDataStore();
			}
		}

		return mongoDataStore;
	}

	/**
	 * returns Daily count
	 */
	@Override
	public int getDailyCount(String eventName, Date date) {
		DateTime utcDate = new DateTime(date, DateTimeZone.UTC);
		DateTime dateBucket = utcDate.withTimeAtStartOfDay();

		BasicDBObject allQuery = new BasicDBObject();
		allQuery.put("event_name", eventName);
		allQuery.put("date", dateBucket.toDate());

		DBObject data = dailyMetricsColl.findOne(allQuery);
		int count = 0;
		if (data != null) {
			count = (int) data.get("daily");
		}

		return count;
	}
	
//	@Override
//	public List<ServiceEventRequest> getAll() {
//		return mongoTemplate.findAll(ServiceEventRequest.class, COLLECTION_NAME);
//	}
	
	@Override
	public List<ServiceEventRequest> getAll() {

		BasicDBObject query = new BasicDBObject();
		query.put("timeInMs", new BasicDBObject("$ne","18221796784"));
		DBCursor cursor = rawEventsColl.find(query);
		DBObject data = null;
		List<ServiceEventRequest> list = new ArrayList<ServiceEventRequest>();
		while(cursor.hasNext()){
			data=cursor.next();
			
			System.out.println("PRINT data::"+data);
			ServiceEventRequest request = new ServiceEventRequest();
			request.setName((String) data.get("name"));
			request.setTimeInMs((String) data.get("timeInMs"));
			list.add(request);
		}
		System.out.println("PRINT data after while::"+list);
		return list;
	}

	/**
	 * Returns hourly count
	 */

	@Override
	public int getHourlyCount(String eventName, Date date, Integer hour) {
		if (hour < 0 && hour > 23) {
			throw new IllegalArgumentException();
		}

		DateTime utcDate = new DateTime(date, DateTimeZone.UTC);
		DateTime dateBucket = utcDate.withTimeAtStartOfDay();

		BasicDBObject allQuery = new BasicDBObject();
		allQuery.put("event_name", eventName);
		allQuery.put("date", dateBucket.toDate());

		DBObject data = dailyMetricsColl.findOne(allQuery);
		int count = 0;
		if (data != null) {
			DBObject hourlyData = (BasicDBObject) data.get("hourly");
			if (hourlyData != null
					&& hourlyData.get(String.valueOf(hour)) != null) {
				count = (int) hourlyData.get(String.valueOf(hour));
			}
		}

		return count;
	}

	@Override
	public boolean storeRawEvent(String jsonData) {
		DBObject rawEvent = (DBObject) JSON.parse(jsonData);
		boolean success = false;
		if (rawEventsColl.insert(rawEvent) != null) {
			success = true;
		}
		return success;
	}

	/**
	 * 
	 */

	@Override
	public void increment(String eventName, long epochTimeStamp) {
		DateTime date = new DateTime(new Long(epochTimeStamp * 1000),
				DateTimeZone.UTC);
		int hr = date.getHourOfDay();
		DateTime dateBucket = date.withTimeAtStartOfDay();

		BasicDBObject allQuery = new BasicDBObject();
		allQuery.put("date", dateBucket.toDate());
		allQuery.put("event_name", eventName);

		DBObject data = dailyMetricsColl.findOne(allQuery);

		if (data != null) {
			DBObject hourlyQuery = new BasicDBObject("$inc", new BasicDBObject(
					"hourly." + hr, 1));
			dailyMetricsColl.update(data, hourlyQuery);

			data = dailyMetricsColl.findOne(allQuery);
			DBObject dailyQuery = new BasicDBObject("$inc", new BasicDBObject(
					"daily", 1));
			dailyMetricsColl.update(data, dailyQuery);

		} else {
			BasicDBObjectBuilder daily = BasicDBObjectBuilder.start().add(
					"date", dateBucket.toDate());

			BasicDBObjectBuilder hourSubArray = BasicDBObjectBuilder.start();
			for (int i = 0; i <= 23; i++) {
				if (i == hr) {
					hourSubArray.add(String.valueOf(i), 1);
				} else {
					hourSubArray.add(String.valueOf(i), 0);
				}
			}

			daily.add("hourly", hourSubArray.get());
			daily.add("daily", 1);
			daily.add("event_name", eventName);

			dailyMetricsColl.insert(daily.get());
		}
	}

}
