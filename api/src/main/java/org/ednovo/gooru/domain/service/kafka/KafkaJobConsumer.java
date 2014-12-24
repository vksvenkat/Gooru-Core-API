package org.ednovo.gooru.domain.service.kafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.application.util.ResourceImageUtil;
import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.application.util.RequestUtil;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.gooru.kafka.producer.KafkaProperties;
import org.json.JSONObject;
import org.restlet.data.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class KafkaJobConsumer implements Runnable, ParameterProperties {

	@Autowired
	private KafkaProperties kafkaProperties;

	private static ConsumerConnector consumer;

	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

	@Autowired
	private SettingService settingService;

	private static KafkaStream m_stream;

	private static String conversionRestEndPoint;

	private static String restEndPoint;

	@PostConstruct
	public void init() {
		try {
			conversionRestEndPoint = settingService.getConfigSetting(ConfigConstants.GOORU_CONVERSION_RESTPOINT, 0, TaxonomyUtil.GOORU_ORG_UID);
			restEndPoint = settingService.getConfigSetting(ConfigConstants.GOORU_API_ENDPOINT);
			consumer = kafka.consumer.Consumer.createJavaConsumerConnector(createConsumerConfig());
		} catch (Exception e) {
			LOGGER.error("Serialization failed" + e);
		}
	}

	private ConsumerConfig createConsumerConfig() {
		Properties props = new Properties();
		props.put(KafkaProperties.ZK_CONSUMER_CONNECT, kafkaProperties.zkConsumerConnectValue);
		props.put(KafkaProperties.ZK_CONSUMER_GROUP, kafkaProperties.consumerGroupIdValue);
		props.put(KafkaProperties.ZK_SESSION_TIME_OUT_MS, KafkaProperties.ZK_SESSION_TIME_OUT_MS_VALUE);
		props.put(KafkaProperties.ZK_SYNCTIME_MS, KafkaProperties.ZK_SYNCTIME_MS_VALUE);
		props.put(KafkaProperties.AUTOCOMMIT_INTERVAL_MS, KafkaProperties.AUTOCOMMIT_INTERVAL_MS_VALUE);
		props.put(KafkaProperties.FETCH_SIZE, KafkaProperties.FETCH_SIZE_VALUE);
		props.put(KafkaProperties.AUTO_OFFSET_RESET, KafkaProperties.AUTO_OFFSET_RESET_VALUE);
		return new ConsumerConfig(props);

	}

	@Override
	public void run() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		System.out.print(kafkaProperties.conversionJobTopic + "testing");
		map.put(kafkaProperties.conversionJobTopic, 1);
		Map<String, List<KafkaStream<byte[], byte[]>>> listOfTopicsStreams = consumer.createMessageStreams(map);
		List<KafkaStream<byte[], byte[]>> listOfStream = listOfTopicsStreams.get(kafkaProperties.conversionJobTopic);
		m_stream = listOfStream.get(0);
		ConsumerIterator<byte[], byte[]> it = m_stream.iterator();

		while (it.hasNext()) {
			String message = new String(it.next().message());
			if (!StringUtils.isBlank(message) && message.contains("{")) {
				try {
					JSONObject data = new JSONObject(message);
					if (data.getString("eventName") != null && data.getString("eventName").equalsIgnoreCase(ResourceImageUtil.CONVERT_DOCUMENT_PDF) && data.getString("status").equalsIgnoreCase("Inprogress")) {
						RequestUtil.executeRestAPI(message, conversionRestEndPoint + "/conversion/document-to-pdf", Method.POST.getName(), data.getString(SESSIONTOKEN));
					} else if (data.getString("eventName") != null && data.getString("eventName").equalsIgnoreCase(ResourceImageUtil.CONVERT_DOCUMENT_PDF)) {
						RequestUtil.executeRestAPI(message, restEndPoint + "v2/job/" + data.getString("jobUid"), Method.PUT.getName(), data.getString(SESSIONTOKEN));
					}
				} catch (Exception e) {
					LOGGER.error("conversion document to pdf : " + e);
				}
			}
		}

	}
}
