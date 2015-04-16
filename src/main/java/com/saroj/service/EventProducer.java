/**
 * 
 */
package com.saroj.service;

/**
 * @author sarojrout
 *
 */
public interface EventProducer {

	public boolean send(String topicName, String data);
	public void disconnect();
	
}
