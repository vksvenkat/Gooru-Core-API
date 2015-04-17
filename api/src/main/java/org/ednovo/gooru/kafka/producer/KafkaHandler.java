/////////////////////////////////////////////////////////////
// KafkaHandler.java
// gooru-api
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.kafka.producer;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.ednovo.gooru.application.util.ConfigProperties;
import org.ednovo.kafka.producer.KafkaEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KafkaHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaHandler.class);
	
	private static final String CONNECTION = "connection";
	
	private static final String EVENT = "event";

	private static final String TOPIC = "topic";
	
	private static final String KAFKA_IP = "kafkaIp";
	
	private static final String PORT_NO = "portNo";
	
	private static String DEFAULT_TOPIC;
	
	@Autowired
	private ConfigProperties configProperties;
	
	@Autowired
	private KafkaEventHandler kafkaService;
	
	private static Map<String,String> topicConfigs;

	@PostConstruct 
	private void initKafkaConfigs(){
	
		try{
			topicConfigs = new HashMap<String,String>();
			Map<String,String> connectionConfigs = new HashMap<String,String>(getConfigProperties().getInsightsKafkaProperties().get(CONNECTION));
			DEFAULT_TOPIC = connectionConfigs.get(TOPIC); 
			getKafkaService().init(connectionConfigs.get(KAFKA_IP), connectionConfigs.get(PORT_NO), DEFAULT_TOPIC);
		}catch(Exception e){
			LOGGER.error("insights kafka connectionConfig attributes not found");
		}
		if(getConfigProperties().getInsightsKafkaProperties().containsKey(EVENT) && getConfigProperties().getInsightsKafkaProperties().get(EVENT) != null){
			topicConfigs = getConfigProperties().getInsightsKafkaProperties().get(EVENT);
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
				if(topicConfigs.containsKey(eventName)){
					defaultTopic = topicConfigs.get(eventName);
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
