package com.kokhrimenko.trainings.eas_026.kafka.fundamentals.lesson3.solution.consume_copy_produce;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;

import com.kokhrimenko.trainings.eas_026.kafka.fundamentals.lesson3.SimpleMessageProducer;

import lombok.extern.slf4j.Slf4j;

/**
 * Sample implementation of consume-transform-produce loop.
 * To be able to see how transactions works in kafka - `consume topic` should be created with `--config min.insync.replicas=2`
 * and, at a time when this code will be running, should be less than 2 nodes connected to a cluster.
 *
 * @author kokhrime
 *
 */
@Slf4j
public class ConsumeTransformProduceLoop {
	private static final String KAFKA_PROPERTIES = "kafka.properties";
	private static final String CONSUMER_GROUP_ID = "ConsumeTransformProduceLoopconsumergroup";
	private static final String PRODUCER_TRANSACTIONAL_ID = "ConsumeTransformProduceLoopproducertrids";

	private static final String INPUT_TOPIC = "EAS026sentences";
	private static final String OUTPUT_TOPIC = "EAS026counts";

	public static void main(String[] args) throws IOException {
		log.info("Consume-transform-produce loop start");

		KafkaConsumer<String, String> consumer = createKafkaConsumer();
		consumer.subscribe(Collections.singleton(INPUT_TOPIC));
		
		KafkaProducer<String, String> producer = createKafkaProducer();
		producer.initTransactions();
		
		while (true) {
			log.info("Start processing next batch of data");
			try {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(60));
				Map<String, Integer> wordCountMap = records.records(new TopicPartition(INPUT_TOPIC, 0)).stream()
						.flatMap(rec -> Stream.of(rec.value().split("\\s+")))
						.map(word -> Tuple.of(word, 1))
						.collect(Collectors.toMap(Tuple::getKey, Tuple::getValue, (v1, v2) -> v1 + v2));

				producer.beginTransaction();

				wordCountMap.forEach(
						(key, value) -> producer.send(new ProducerRecord<>(OUTPUT_TOPIC, key, value.toString())));

				Map<TopicPartition, OffsetAndMetadata> offsetsToCommit = new HashMap<>();

				for (TopicPartition partition : records.partitions()) {
					List<ConsumerRecord<String, String>> partitionedRecords = records.records(partition);
					long offset = partitionedRecords.get(partitionedRecords.size() - 1).offset();

					offsetsToCommit.put(partition, new OffsetAndMetadata(offset + 1));
				}

				producer.sendOffsetsToTransaction(offsetsToCommit, CONSUMER_GROUP_ID);
				producer.commitTransaction();
				log.info("End processing next batch of data");
			} catch (KafkaException e) {
				log.error("Something wrong happened. Error {}", e);
				producer.abortTransaction();
			}
		}
	}
	
	private static KafkaConsumer<String, String> createKafkaConsumer() throws IOException {
		Properties props = new Properties();
		props.load(SimpleMessageProducer.class.getResourceAsStream(KAFKA_PROPERTIES));
		props.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP_ID);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");

        return new KafkaConsumer<>(props);
	}

	private static KafkaProducer<String, String> createKafkaProducer() throws IOException {
		Properties props = new Properties();
		props.load(SimpleMessageProducer.class.getResourceAsStream(KAFKA_PROPERTIES));
		props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
		props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, PRODUCER_TRANSACTIONAL_ID);
		props.put(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
		props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, "300");
		props.put(ProducerConfig.ACKS_CONFIG, "-1");

		return new KafkaProducer<>(props);
	}
	
}
