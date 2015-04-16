/**
 * 
 */
package com.saroj.dao;

import java.util.Date;
import java.util.List;

import com.saroj.controller.ServiceEventRequest;

/**
 * @author sarojrout
 *
 */
public interface DataStore<T> {

	public int getDailyCount(String eventName, Date date);

	public int getHourlyCount(String eventName, Date date, Integer hour);
	
	public T getAll();
	public boolean storeRawEvent(String jsonData);

	public void increment(String eventName, long epochTimeStamp);

}
