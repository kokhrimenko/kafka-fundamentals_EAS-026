package com.kokhrimenko.trainings.eas_026.kafka.fundamentals.lesson3;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import lombok.extern.slf4j.Slf4j;

/**
 * Single consumer in a consumer group example.
 *
 * @author kokhrime
 *
 */
@Slf4j
public class SingleConsumer {
	private static final String KAFKA_PROPERTIES = "kafka.properties";
	private static final String KAFKA_TOPIC = "SimpleMessageConsumertopic";

	public static void main(String[] args) throws IOException {
		log.info("Start of SingleConsumer");
		Properties prop = new Properties();
		prop.load(SingleConsumer.class.getResourceAsStream(KAFKA_PROPERTIES));
		prop.put("group.id", SingleConsumer.class.getSimpleName());
		
		try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(prop)) {
			log.info("subscribing to the kafka topic");
			consumer.subscribe(Collections.singletonList(KAFKA_TOPIC));
			//consumer.assign(null);
			
			while (true) {
		        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
		        for (ConsumerRecord<String, String> data : records) {
					log.warn("topic = {}, partition = {}, offset = {}, key = {}, data = {}", data.topic(),
							data.partition(), data.offset(), data.key(), data.value());
		        }
		    }
		}
	}
	
}
