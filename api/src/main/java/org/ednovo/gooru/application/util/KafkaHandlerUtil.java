package org.ednovo.gooru.application.util;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.ednovo.kafka.producer.KafkaEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KafkaHandlerUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigProperties.class);
	
	private static final String CONNECTION_CONFIG = "connectionConfig";
	
	private static final String EVENT_TOPIC_CONFIG = "eventTopicConfig";

	private static final String TOPIC = "topic";
	
	private static final String KAFKA_IP = "kafkaIp";
	
	private static final String PORT_NO = "portNo";
	
	private static String DEFAULT_TOPIC;
	
	@Autowired
	private ConfigProperties configProperties;
	
	@Autowired
	private KafkaEventHandler kafkaService;

	@PostConstruct 
	private void init(){

		initKafkaConfigs();
	}
	
	private void initKafkaConfigs(){
	
		try{
			Map<String,String> connectionConfigs = new HashMap<String,String>(getConfigProperties().getInsightsKafkaProperties().get(CONNECTION_CONFIG));
			DEFAULT_TOPIC = connectionConfigs.get(TOPIC); 
			getKafkaService().init(connectionConfigs.get(KAFKA_IP), connectionConfigs.get(PORT_NO), DEFAULT_TOPIC);
		}catch(Exception e){
			LOGGER.error("insights kafka connectionConfig attributes not found");
		}
	}
	
	public void clearInsightsKafkaConfig(){
		
		getConfigProperties().clearInsightsKafkaProperties();
		initKafkaConfigs();
	}
	
	public void sendEventLog(String data){
		getKafkaService().sendEventLog(data);
	}
	
	public void sendEventLog(String eventName, String data){
		getKafkaService().sendEventLog(getKafkaTopic(eventName, DEFAULT_TOPIC),data);
	}
	
	private String getKafkaTopic(String eventName,String defaultTopic){
		
		if(getConfigProperties().getInsightsKafkaProperties().containsKey(EVENT_TOPIC_CONFIG) && getConfigProperties().getInsightsKafkaProperties().get(EVENT_TOPIC_CONFIG) != null){
			Map<String,String> topicConfigs = new HashMap<String,String>(getConfigProperties().getInsightsKafkaProperties().get(EVENT_TOPIC_CONFIG));
			for(Map.Entry<String, String> topicConfig : topicConfigs.entrySet()){
				if(topicConfig.getKey().contains(eventName)){
					return topicConfig.getValue();
				}
			}
		}		
		return defaultTopic;
	}

	public KafkaEventHandler getKafkaService() {
		return kafkaService;
	}

	public ConfigProperties getConfigProperties() {
		return configProperties;
	}

}
