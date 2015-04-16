/**
 * 
 */
package com.saroj.service;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import org.codehaus.jackson.map.ObjectMapper;

import com.saroj.dao.DataStore;
import com.saroj.dao.MongoDataStore;

/**
 * @author sarojrout
 *
 */
public class KafkaEventConsumer extends Thread implements EventConsumer{
	
	final static String clientId = "SarojKafkaClient";
	final static String TOPIC = "test-events";
	private static final String MONGO_HOST = "localhost";
	private static final int MONGO_PORT = 27017;
	private ConsumerConnector consumerConnector;

	public static void main(String[] argv) {
		KafkaEventConsumer kafkaConsumer = new KafkaEventConsumer();
		kafkaConsumer.start();
	}
	public KafkaEventConsumer() {
		Properties properties = new Properties();
		properties.put("zookeeper.connect", "localhost:2181");
		properties.put("group.id", "test-group");
		ConsumerConfig consumerConfig = new ConsumerConfig(properties);
		consumerConnector = Consumer.createJavaConsumerConnector(consumerConfig);
	}
	
	@SuppressWarnings("unchecked")
	public void run() {
		DataStore store = null;
		try {
			store = MongoDataStore.getInstance(MONGO_HOST, MONGO_PORT);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}

		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(TOPIC, new Integer(1));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerConnector.createMessageStreams(topicCountMap);
		KafkaStream<byte[], byte[]> stream = consumerMap.get(TOPIC).get(0);
		ConsumerIterator<byte[], byte[]> it = stream.iterator();
		while (it.hasNext()) {
			try {
				String data = new String(it.next().message());
				store.storeRawEvent(data);

				HashMap<String, Object> result = new ObjectMapper().readValue(
						data, HashMap.class);
				long timeStamp = Long.parseLong((String) result.get("timeInMs"));
				String eventName = (String) result.get("name");
				store.increment(eventName, timeStamp);
			} catch (Exception e) {
				// Log error and continue
			}
		}
	}

}
