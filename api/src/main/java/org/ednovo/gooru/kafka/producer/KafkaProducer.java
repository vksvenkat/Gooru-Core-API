package org.ednovo.gooru.kafka.producer;

import java.util.Properties;

import javax.annotation.PostConstruct;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {
	
	@Autowired
	private KafkaProperties kafkaProperties;
	
	private Producer<String, String> producer;
	protected Properties props = new Properties();
	
	private static final Logger logger = LoggerFactory.getLogger(IndexProcessor.class);
	
	@PostConstruct
	public void init() {
		props.put(KafkaProperties.SERIALIZER_CLASS, KafkaProperties.SERIALIZER_CLASS_VALUE);
		props.put(KafkaProperties.ZK_CONNECT, kafkaProperties.ZK_CONNECT_VALUE);
		props.put(KafkaProperties.PRODUCER_TYPE, KafkaProperties.PRODUCER_TYPE_VALUE);
		props.put(KafkaProperties.REQUEST_REQUIRED_ACKS, KafkaProperties.REQUEST_REQUIRED_ACKS_VALUE);
		props.put(KafkaProperties.RETRY_BACKOFF_MS, KafkaProperties.RETRY_BACKOFF_MS_VALUE);
		try{
		producer = new Producer<String, String>(
				new ProducerConfig(props));
		}
		catch (Exception e) {
			logger.info("Error while creating kafka producer :" + e);
		}
	}
	
	public void send(String message) {
		KeyedMessage<String, String> data = new KeyedMessage<String, String>(kafkaProperties.TOPIC_VALUE,message);
		try{
			producer.send(data);
		} catch (Exception e){
			logger.info("Errror while sending date from kafka producer :"+e);
		}
	}

}
