/**
 * 
 */
package com.saroj.controller;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author sarojrout
 *
 */
@Document
public class ServiceEventRequest {
	private static final long serialVersionUID = -8650616886390531568L;
	private String name;

	public ServiceEventRequest() {
	}

	public ServiceEventRequest(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Event [name=" + name + "]";
	}
}
