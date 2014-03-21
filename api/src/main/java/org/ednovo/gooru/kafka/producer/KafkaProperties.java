package org.ednovo.gooru.kafka.producer;

import javax.annotation.PostConstruct;

import org.ednovo.gooru.infrastructure.persistence.hibernate.ConfigSettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KafkaProperties {

	@Autowired
	private ConfigSettingRepository configSettingRepository;
	  
	 public static final String ZK_CONNECT = "zk.connect";
	 public static final String GROUP_ID = "groupid";
	 public static final String TOPIC = "topic";
	 public static final String KAFKA_SERVER_URL = "kafkaServerURL";
	 public static final String KAFKA_SERVER_PORT = "kafkaServerPort";
	 public static final int KAFKA_SERVER_PORT_VALUE = 9092;
	 public static final String KAFKA_PRODUCER_BUFFER_SIZE = "kafkaProducerBufferSize";
	 public static final int KAFKA_PRODUCER_BUFFER_SIZE_VALUE = 64*1024;
	 public static final String CONNECTION_TIME_OUT = "connectionTimeOut";
	 public static final int CONNECTION_TIME_OUT_VALUE = 100000;
	 public static final String RECONNECT_INTERVAL = "reconnectInterval";
	 public static final int RECONNECT_INTERVAL_VALUE = 10000;
	 public static final String SERIALIZER_CLASS = "serializer.class";
	 public static final String SERIALIZER_CLASS_VALUE = "kafka.serializer.StringEncoder";
	 public static final String PRODUCER_TYPE = "producer.type";
	 public static final String PRODUCER_TYPE_VALUE = "async";
	 public static final String COMPRESSION_CODEC = "compression.codec";
	 public static final String COMPRESSION_CODEC_VALUE = "1";
	 public static final String ZK_SESSION_TIME_OUT_MS = "zk.sessiontimeout.ms";
	 public static final String ZK_SESSION_TIME_OUT_MS_VALUE = "10000";
	 public static final String ZK_SYNCTIME_MS = "zk.synctime.ms";
	 public static final String ZK_SYNCTIME_MS_VALUE = "200";
	 public static final String AUTOCOMMIT_INTERVAL_MS = "autocommit.interval.ms";
	 public static final String AUTOCOMMIT_INTERVAL_MS_VALUE = "1000";
	 public static final String FETCH_SIZE = "fetch.size";
	 public static final String FETCH_SIZE_VALUE = "1048576";
	 public static final String AUTO_OFFSET_RESET = "auto.offset.reset";
	 public static final String AUTO_OFFSET_RESET_VALUE = "smallest";
	 public static final String KAFKA_PREFIX = "kafka.";
	  
	  
	 public String ZK_CONNECT_VALUE;
	 public String GROUP_ID_VALUE;
	 public String TOPIC_VALUE;
	 public String KAFKA_SERVER_URL_VALUE;
	  
	  private static final Logger logger = LoggerFactory.getLogger(KafkaProperties.class);
	  
	  @PostConstruct
	  public void init(){
		  try{
			  ZK_CONNECT_VALUE = configSettingRepository.getSetting(KAFKA_PREFIX+ZK_CONNECT);
			  GROUP_ID_VALUE = configSettingRepository.getSetting(KAFKA_PREFIX+GROUP_ID);
			  TOPIC_VALUE = configSettingRepository.getSetting(KAFKA_PREFIX+TOPIC);
			  KAFKA_SERVER_URL_VALUE = configSettingRepository.getSetting(KAFKA_PREFIX+KAFKA_SERVER_URL);
		  } catch(Exception e){
			  logger.info("kafka error while getting config setting fields value :" + e);
		  }
	  }
}
