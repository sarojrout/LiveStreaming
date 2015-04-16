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

	public boolean storeRawEvent(String jsonData);

	public T getAll();


}
