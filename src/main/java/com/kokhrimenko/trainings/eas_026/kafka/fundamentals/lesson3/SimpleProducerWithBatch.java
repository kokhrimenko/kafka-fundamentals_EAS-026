package com.kokhrimenko.trainings.eas_026.kafka.fundamentals.lesson3;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import lombok.extern.slf4j.Slf4j;

/**
 * Simple kafka producer with batch mode enabled
 *
 * @author kokhrime
 *
 */
@Slf4j
public class SimpleProducerWithBatch {
	private static final String KAFKA_PROPERTIES = "kafka.properties";
	private static final String KAFKA_TOPIC = "SimpleProducerWithBatch";

	public static void main(String[] args) throws IOException, InterruptedException {
		log.info("Start of SimpleMessageProducer");
		Properties prop = new Properties();
		prop.load(SimpleMessageProducer.class.getResourceAsStream(KAFKA_PROPERTIES));
		prop.setProperty(ProducerConfig.LINGER_MS_CONFIG, "300");
		prop.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, "1000");

		try (Producer<String, String> producer = new KafkaProducer<>(prop)) {
			for (int i = 0; i < 20; i++) {
				producer.send(new ProducerRecord<>(KAFKA_TOPIC, "msg.key_" + i, "hello world_" + i), new KafkaProducerCallback());
				TimeUnit.MILLISECONDS.sleep(50);
				log.info("Message was successfully sent");
			}
		}
		log.info("End of SimpleMessageProducer");
	}
	
	private static class KafkaProducerCallback implements Callback {
		@Override
		public void onCompletion(RecordMetadata metadata, Exception exception) {
			log.info("message was sent " + metadata.serializedValueSize());
		}
	}
}
