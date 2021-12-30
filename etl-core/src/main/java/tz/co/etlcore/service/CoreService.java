package tz.co.etlcore.service;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Slf4j
@RequiredArgsConstructor
public class CoreService  implements Runnable{

	@Value("#{'${registered.topics-csv}'.split(',')}")
	private List<String> topics;

	@Value("${spring.kafka.consumer.bootstrap-servers}")
	private String bootstrapServers;

	@Value("${spring.kafka.listener.poll-timeout}")
	private long pollTimeout;

	private final ScriptingService scriptingService;
	public void run() {
		Properties properties = new Properties();
		properties.put("bootstrap.servers", bootstrapServers);
		properties.put("group.id", "consumer-v1.0.0");
		properties.put("enable.auto.commit", "true");
		properties.put("auto.commit.interval.ms", "1000");
		properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
		consumer.subscribe(topics);
		while(true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(pollTimeout));
			records.forEach(record -> {
				if(!records.isEmpty()) {
					if(!record.value().isEmpty()) {
						log.info("Received data from TOPIC: [ {}, {} ]", record.topic(), record.value());
						scriptingService.scriptHandler(record.value(), record.topic());
					}
				}
			});
		}
	}

	@PostConstruct
	public void init(){
		new Thread(this).start();
	}
}
