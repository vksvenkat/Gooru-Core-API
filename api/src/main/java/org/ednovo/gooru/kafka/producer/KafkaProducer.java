package org.ednovo.gooru.kafka.producer;

import java.util.Properties;

import javax.annotation.PostConstruct;

import kafka.javaapi.producer.Producer;
import kafka.javaapi.producer.ProducerData;
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
	
	
	private static final String TOPIC_RESOURCE = "resource_queue";
	
	private static final String TOPIC_SCOLLECTION = "scollection_queue";
	
	private static final String TOPIC_USER = "user_queue";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IndexProcessor.class);
	
	@PostConstruct
	public void init() {
		props.put(KafkaProperties.SERIALIZER_CLASS, KafkaProperties.SERIALIZER_CLASS_VALUE);
		props.put(KafkaProperties.ZK_CONNECT, kafkaProperties.zkConnectValue);
		props.put(KafkaProperties.PRODUCER_TYPE, KafkaProperties.PRODUCER_TYPE_VALUE);
		props.put(KafkaProperties.COMPRESSION_CODEC, KafkaProperties.COMPRESSION_CODEC_VALUE);
		
		try{
		producer = new Producer<String, String>(
				new ProducerConfig(props));
		}
		catch (Exception e) {
			LOGGER.info("Error while creating kafka producer :" + e);
		}
	}
	
	public void send(String message) {
		ProducerData<String, String> data = new ProducerData<String, String>(kafkaProperties.topicValue, message);
		try{
			producer.send(data);
		} catch (Exception e){
			LOGGER.info("Errror while sending date from kafka producer :"+e);
		}
	}
	
	public void send(String message, String indexType) {
		String topicName = null;
		if(indexType.equalsIgnoreCase("resource")){
			topicName = TOPIC_RESOURCE;
		}
		else if(indexType.equalsIgnoreCase("scollection")){
			topicName = TOPIC_SCOLLECTION;
		}
		else if(indexType.equalsIgnoreCase("user")){
			topicName = TOPIC_USER;
		}
		ProducerData<String, String> data = new ProducerData<String, String>(topicName, message);
		try{
			producer.send(data);
		} catch (Exception e){
			LOGGER.info("Errror while sending date from kafka producer :"+e);
		}
	}


}
