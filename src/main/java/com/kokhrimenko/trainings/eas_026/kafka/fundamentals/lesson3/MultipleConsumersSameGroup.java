package com.kokhrimenko.trainings.eas_026.kafka.fundamentals.lesson3;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import lombok.extern.slf4j.Slf4j;

/**
 * Multiple consumers in a same consumer group example.
 *
 * @author kokhrime
 *
 */
@Slf4j
public class MultipleConsumersSameGroup {
	private static final String KAFKA_PROPERTIES = "kafka.properties";
	private static final String KAFKA_TOPIC = "MultipleConsumersSameGroup";
	private static final int CUSTOMERS_COUNT = 3;

	public static void main(String[] args) throws IOException, InterruptedException {
		log.info("Start of MultipleConsumersSameGroup");
		Properties prop = new Properties();
		prop.load(SingleConsumer.class.getResourceAsStream(KAFKA_PROPERTIES));
		prop.put(ConsumerConfig.GROUP_ID_CONFIG, MultipleConsumersSameGroup.class.getSimpleName());
		ExecutorService executor = Executors.newFixedThreadPool(CUSTOMERS_COUNT);
		IntStream.range(0, CUSTOMERS_COUNT).forEach(index -> {
			executor.submit(() -> {
				final String clientId = UUID.randomUUID().toString();
				Properties localProperties = (Properties) prop.clone();
				localProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
				try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(localProperties)) {
					log.info("Subscriber {} subscribed to the kafka topic", clientId);
					consumer.subscribe(Collections.singletonList(KAFKA_TOPIC));
					
					while (true) {
				        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
				        for (ConsumerRecord<String, String> data : records) {
							log.warn("consumer = {} got data: {topic = {}, partition = {}, offset = {}, key = {}, data = {}}", clientId, data.topic(),
									data.partition(), data.offset(), data.key(), data.value());
				        }
				    }
				} catch (Exception e) {
					log.error("Some problems with the consumer: {}, error: {}", clientId, e);
				}		
			});
		});
		TimeUnit.MINUTES.sleep(5);
		log.info("End of MultipleConsumersSameGroup");
	}
	
}
