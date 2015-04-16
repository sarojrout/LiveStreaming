/**
 * 
 */
package com.saroj.controller;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author sarojrout
 *
 */
@Document
public class ServiceEventRequest {
	private static final long serialVersionUID = -8650616886390531568L;
	private String name;
	private String timeInMs;

	public ServiceEventRequest(){
	}
	
	public ServiceEventRequest(String name,String timeInMs){
	//	super();
		this.name = name;
		this.timeInMs = timeInMs;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTimeInMs() {
		return timeInMs;
	}

	public void setTimeInMs(String timeInMs) {
		this.timeInMs = timeInMs;
	}

	@Override
	public String toString() {
		return "Event [name=" + name + ", timeInMs=" + timeInMs + "]";
	}
}
