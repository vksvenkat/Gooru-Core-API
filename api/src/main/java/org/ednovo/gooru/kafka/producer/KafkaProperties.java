package org.ednovo.gooru.kafka.producer;

import javax.annotation.PostConstruct;

import org.ednovo.gooru.infrastructure.persistence.hibernate.ConfigSettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class KafkaProperties {

	@Autowired
	private ConfigSettingRepository configSettingRepository;
	public static final String ZK_CONNECT = "metadata.broker.list";
	public static final String ZK_CONSUMER_CONNECT = "zookeeper.connect";
	public static final String ZK_CONSUMER_GROUP = "group.id";
	public static final String GROUP_ID = "groupid";
	public static final String TOPIC = "topic";
	public static final String KAFKA_SERVER_URL = "kafkaServerURL";
	public static final String KAFKA_SERVER_PORT = "kafkaServerPort";
	public static final int KAFKA_SERVER_PORT_VALUE = 9092;
	public static final String KAFKA_PRODUCER_BUFFER_SIZE = "kafkaProducerBufferSize";
	public static final int KAFKA_PRODUCER_BUFFER_SIZE_VALUE = 64 * 1024;
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
	public static final String ZK_SESSION_TIME_OUT_MS = "zookeeper.session.timeout.ms";
	public static final String ZK_SESSION_TIME_OUT_MS_VALUE = "10000";
	public static final String ZK_SYNCTIME_MS = "zookeeper.sync.time.ms";
	public static final String ZK_SYNCTIME_MS_VALUE = "200";
	public static final String AUTOCOMMIT_INTERVAL_MS = "auto.commit.interval.ms";
	public static final String AUTOCOMMIT_INTERVAL_MS_VALUE = "1000";
	public static final String FETCH_SIZE = "fetch.size";
	public static final String FETCH_SIZE_VALUE = "1048576";
	public static final String AUTO_OFFSET_RESET = "auto.offset.reset";
	public static final String AUTO_OFFSET_RESET_VALUE = "smallest";
	public static final String KAFKA_PREFIX = "kafka.";
	public static final String REQUEST_REQUIRED_ACKS = "request.required.acks";
	public static final String REQUEST_REQUIRED_ACKS_VALUE = "1";
	public static final String RETRY_BACKOFF_MS = "retry.backoff.ms";
	public static final String RETRY_BACKOFF_MS_VALUE = "1000";
	public static final String JOB_TOPIC = "job.topic";

	public String zkConnectValue;
	public String groupIdValue;
	public String zkConsumerConnectValue;
	public String consumerGroupIdValue;
	public String topicValue;
	public String kafaServiceUrl;
	public String conversionJobTopic;
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProperties.class);

	@PostConstruct
	public void init() {
		try {
			zkConnectValue = configSettingRepository.getSetting(KAFKA_PREFIX + ZK_CONNECT);
			groupIdValue = configSettingRepository.getSetting(KAFKA_PREFIX + GROUP_ID);
			zkConsumerConnectValue = configSettingRepository.getSetting(KAFKA_PREFIX + ZK_CONSUMER_CONNECT);
			consumerGroupIdValue = configSettingRepository.getSetting(KAFKA_PREFIX + ZK_CONSUMER_GROUP);
			topicValue = configSettingRepository.getSetting(KAFKA_PREFIX + TOPIC);
			conversionJobTopic = configSettingRepository.getSetting(KAFKA_PREFIX + JOB_TOPIC);
			kafaServiceUrl = configSettingRepository.getSetting(KAFKA_PREFIX + KAFKA_SERVER_URL);
		} catch (Exception e) {
			LOGGER.info("kafka error while getting config setting fields value :" + e);
		}
	}
}
