package com.kokhrimenko.trainings.eas_026.kafka.fundamentals.lesson3;

import java.io.IOException;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import lombok.extern.slf4j.Slf4j;

/**
 * Send message to a kafka cluster, without explicit message key and check kafka round-robin partitioning strategy.
 *
 * @author kokhrime
 *
 */
@Slf4j
public class MessageProducerNoMessageKey {
	private static final String KAFKA_PROPERTIES = "kafka.properties";
	private static final String KAFKA_TOPIC = "SimpleMessageProducertopic";

	public static void main(String[] args) throws IOException {
		log.info("Start of SimpleMessageProducer");
		Properties prop = new Properties();
		prop.load(SimpleMessageProducer.class.getResourceAsStream(KAFKA_PROPERTIES));

		final int countOfIterations = 6;
		try (Producer<String, String> producer = new KafkaProducer<>(prop)) {
			for (int i = 0; i < countOfIterations; i++) {
				producer.send(new ProducerRecord<>(KAFKA_TOPIC, String.format("Message without message key %d: hello world", i)));
				log.info("Message {} was successfully sent", i);
				producer.flush();
			}
		}
		log.info("End of SimpleMessageProducer");
	}
	
}
