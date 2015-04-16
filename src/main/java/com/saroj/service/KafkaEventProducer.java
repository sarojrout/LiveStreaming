/**
 * 
 */
package com.saroj.service;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.slf4j.LoggerFactory;

/**
 * @author sarojrout
 *
 */
public class KafkaEventProducer implements EventProducer {
	private static KafkaEventProducer kafkaProducer = null;
	private static Producer<String, String> internalProducer;

	private KafkaEventProducer() {
	}

	private final static org.slf4j.Logger logger = LoggerFactory
			.getLogger(KafkaEventProducer.class);

	/**
	 * Establish the connection with kafka cluster
	 * @param host
	 * @param port
	 * @return
	 */
	public static EventProducer getConnection(String host, String port) {
		synchronized (KafkaEventProducer.class) {
			if (kafkaProducer == null) {
				Properties props = new Properties();
				props.put("zk.connect", host + ":" + port);

				props.put("serializer.class", "kafka.serializer.StringEncoder");
				props.put("metadata.broker.list",
						"127.0.0.1:9092,127.0.0.1:9092");
				props.put("partitioner.class",
						"com.saroj.service.SimplePartitioner");
				ProducerConfig config = new ProducerConfig(props);
				internalProducer = new Producer<String, String>(config);
				kafkaProducer = new KafkaEventProducer();
			}
		}

		return kafkaProducer;
	}

	/**
	 * Send the message to the broker
	 */

	@Override
	public boolean send(String topicName, String data) {
		boolean success = false;
		KeyedMessage<String, String> message = new KeyedMessage<String, String>(
				topicName, data);
		try {
			internalProducer.send(message);
			success = true;
		} catch (Exception e) {
			success = false;
			logger.error("Error while sending kafka message: "
					+ e.getStackTrace());
		}

		return success;
	}

	@Override
	public void disconnect() {
		internalProducer.close();

	}

}
