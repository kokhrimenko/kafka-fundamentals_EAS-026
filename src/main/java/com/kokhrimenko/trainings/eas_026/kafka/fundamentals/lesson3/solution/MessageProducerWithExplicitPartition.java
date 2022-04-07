package com.kokhrimenko.trainings.eas_026.kafka.fundamentals.lesson3.solution;

import java.io.IOException;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.kokhrimenko.trainings.eas_026.kafka.fundamentals.lesson3.SimpleMessageProducer;

import lombok.extern.slf4j.Slf4j;

/**
 * Example: how to send a message to a kafka cluster and explicitly provide a partition.
 *
 * @author kokhrime
 *
 */
@Slf4j
public class MessageProducerWithExplicitPartition {
	private static final String KAFKA_PROPERTIES = "kafka.properties";
	private static final String KAFKA_TOPIC = "SimpleMessageProducertopic";
	private static final int KAFKA_PARTITION = 2;

	public static void main(String[] args) throws IOException {
		log.info("Start of SimpleMessageProducer");
		Properties prop = new Properties();
		prop.load(SimpleMessageProducer.class.getResourceAsStream(KAFKA_PROPERTIES));

		try (Producer<String, String> producer = new KafkaProducer<>(prop)) {
			producer.send(new ProducerRecord<>(KAFKA_TOPIC, KAFKA_PARTITION, "msg.key_1", "hello world from partition: " + KAFKA_PARTITION));
			log.info("Message was successfully sent");
		}
		log.info("End of SimpleMessageProducer");
	}
	
}
