package com.kokhrimenko.trainings.eas_026.kafka.fundamentals.lesson3;

import java.io.IOException;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import lombok.extern.slf4j.Slf4j;

/**
 * Simple kafka producer with retry strategy.
 * To see any differences - topic should be created with `--config min.insync.replicas=2` and,
 * at a time when this code will be running, should be less than 2 nodes connected to a cluster.
 *
 * @author kokhrime
 *
 */
@Slf4j
public class SimpleProducerWithRetry {
	private static final String KAFKA_PROPERTIES = "kafka.properties";
	private static final String KAFKA_TOPIC = "SimpleMessageProducerWithRetytopic";

	public static void main(String[] args) throws IOException {
		log.info("Start of SimpleMessageProducer");
		Properties prop = new Properties();
		prop.load(SimpleMessageProducer.class.getResourceAsStream(KAFKA_PROPERTIES));
		prop.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
		prop.setProperty(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, "300");
		prop.setProperty(ProducerConfig.ACKS_CONFIG, "-1");

		try (Producer<String, String> producer = new KafkaProducer<>(prop)) {
			producer.send(new ProducerRecord<>(KAFKA_TOPIC, "msg.key", "hello world"));
			log.info("Message was successfully sent");
		}
		log.info("End of SimpleMessageProducer");
	}
	
}
