package com.kokhrimenko.trainings.eas_026.kafka.fundamentals.lesson3;

import java.io.IOException;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import lombok.extern.slf4j.Slf4j;

/**
 * Simple example: how to send a message to a kafka cluster.
 *
 * @author kokhrime
 *
 */
@Slf4j
public class SimpleMessageProducer {
	private static final String KAFKA_PROPERTIES = "kafka.properties";
	private static final String KAFKA_TOPIC = "SimpleMessageProducertopic";

	public static void main(String[] args) throws IOException {
		log.info("Start of SimpleMessageProducer");
		Properties prop = new Properties();
		prop.load(SimpleMessageProducer.class.getResourceAsStream(KAFKA_PROPERTIES));

		try (Producer<String, String> producer = new KafkaProducer<>(prop)) {
			producer.send(new ProducerRecord<>(KAFKA_TOPIC, "msg.key", "hello world"));
			log.info("Message was successfully sent");
		}
		log.info("End of SimpleMessageProducer");
	}
	
}
