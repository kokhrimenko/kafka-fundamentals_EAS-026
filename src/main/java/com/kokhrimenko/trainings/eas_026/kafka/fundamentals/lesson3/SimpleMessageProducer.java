package com.kokhrimenko.trainings.eas_026.kafka.fundamentals.lesson3;

import java.io.IOException;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleMessageProducer {
	private final static String KAFKA_PROPERTIES = "kafka.properties";
	private final static String KAFKA_TOPIC = "SimpleMessageProducertopic";

	public static void main(String[] args) throws IOException {
		log.info("Start of SimpleMessageProducer");
		Properties prop = new Properties();
		prop.load(SimpleMessageProducer.class.getResourceAsStream(KAFKA_PROPERTIES));
		
		System.out.println(prop);
		Producer<String, String> producer = new KafkaProducer<>(prop);
		
		log.info("End of SimpleMessageProducer");
	}
	
}
