/////////////////////////////////////////////////////////////
// RedisServiceImpl.java
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
package org.ednovo.gooru.domain.service.redis;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.ednovo.gooru.application.util.CollectionUtil;
import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.api.model.Assessment;
import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.Organization;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.assessment.AssessmentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.classplan.LearnguideRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class RedisServiceImpl implements RedisService, ParameterProperties, ConstantProperties {

	@Autowired(required = false)
	private RedisTemplate<String, Long> redisTemplate;

	@Autowired(required = false)
	private RedisTemplate<String, String> redisStringTemplate;

	@Autowired
	private SettingService settingService;

	@Autowired
	private LearnguideRepository learnguideRepository;

	@Autowired
	private AssessmentRepository assessmentRepository;

	private String redisInstanceName;

	private static final Logger logger = LoggerFactory.getLogger(RedisServiceImpl.class);

	@Autowired
	private CollectionUtil collectionUtil;

	@Autowired
	private String releaseVersion;

	@Override
	public Long getCount(String gooruOId, String type) {

		String key = getKeyByType(gooruOId, type);

		RedisTemplate<String, Long> redisLongTemplate = getRedisLongTemplate();

		ValueOperations<String, Long> values = redisLongTemplate.opsForValue();

		Long count = values.get(key);

		if (count == null) {
			count = 0L;
		}

		return count;
	}

	@Override
	public Long incrementCount(String gooruOId, String type) {

		String countKey = getKeyByType(gooruOId, type);

		RedisTemplate<String, Long> redisLongTemplate = getRedisLongTemplate();

		// Get the helper for getting and setting values
		ValueOperations<String, Long> values = redisLongTemplate.opsForValue();

		// Initialize the count if not present
		values.setIfAbsent(countKey, 0L);

		// Increment the value by 1
		Long totalCount = values.increment(countKey, 1);

		return totalCount;
	}

	@Override
	public void deleteSubcriptionCount(String gooruOId) {

		RedisTemplate<String, Long> redisLongTemplate = getRedisLongTemplate();

		ValueOperations<String, Long> values = redisLongTemplate.opsForValue();

		String key = getRedisInstance() + "-subscription-count-" + gooruOId;

		Long subscriptionCount = values.get(key);

		if (subscriptionCount != null && subscriptionCount != 0) {
			values.set(key, subscriptionCount - 1);
		}

	}

	@Override
	public void deleteEntry(String gooruOId) {

		if (gooruOId != null) {
			RedisTemplate<String, Long> redisLongTemplate = getRedisLongTemplate();

			List<String> keys = new ArrayList<String>();

			String subscribtionKey = getKeyByType(gooruOId, Constants.REDIS_SUBSCRIBTION);
			String viewsKey = getKeyByType(gooruOId, Constants.REDIS_VIEWS);

			if (redisLongTemplate.hasKey(subscribtionKey)) {
				keys.add(subscribtionKey);
			}
			if (redisLongTemplate.hasKey(viewsKey)) {
				keys.add(viewsKey);
			}

			if (keys != null && keys.size() > 0) {
				redisLongTemplate.delete(keys);
			}
		}
	}

	@Override
	public void updateAllCount(String resourceType) {
		if (resourceType != null && !resourceType.equalsIgnoreCase("")) {
			Integer page = 1;
			Integer recordsPerPage = 5000;
			Map<String, String> filters = new HashMap<String, String>();
			filters.put(PAGE_NUM, page + "");
			filters.put(PAGE_SIZE, recordsPerPage + "");

			if (resourceType.equalsIgnoreCase(COLLECTION)) {
				List<Learnguide> collectionList = learnguideRepository.listLearnguides(filters);
				if (collectionList != null && collectionList.size() > 0) {

					logger.info("Collection Redis Updating " + collectionList.size() + " collections : page " + page + " of " + recordsPerPage);

					for (Resource resource : collectionList) {
						updateRedisCount(resource.getViews(), Constants.REDIS_VIEWS, resource.getGooruOid());
					}
				}

			}

			if (resourceType.equalsIgnoreCase(QUIZ)) {
				List<Assessment> assessments = assessmentRepository.listAssessments(filters);

				logger.info("Redis Updating" + assessments.size() + " Quizzes : page " + page + " of " + recordsPerPage);

				if (assessments != null && assessments.size() > 0) {

					for (Resource resource : assessments) {
						updateRedisCount(resource.getViews(), Constants.REDIS_VIEWS, resource.getGooruOid());
					}

				}

			}
		}
	}

	@Override
	public void updateRedisCount(Long count, String type, String gooruOId) {
		RedisTemplate<String, Long> redisLongTemplate = getRedisLongTemplate();

		ValueOperations<String, Long> values = redisLongTemplate.opsForValue();
		if (type.equalsIgnoreCase(SUBSCRIPTION)) {
			values.set(getKeyByType(gooruOId, Constants.REDIS_SUBSCRIBTION), count);
		}
		if (type.equalsIgnoreCase(VIEWS)) {
			values.set(getKeyByType(gooruOId, Constants.REDIS_VIEWS), count);
		}
		if (type.equalsIgnoreCase(SESSIONTOKEN)) {
			values.set(getKeyByType(gooruOId, Constants.REDIS_TOKEN_ENTRY), count);
		}
	}

	private RedisTemplate<String, Long> getRedisLongTemplate() {

		final StringRedisSerializer STRING_SERIALIZER = new StringRedisSerializer();

		redisTemplate.setKeySerializer(STRING_SERIALIZER);

		redisTemplate.setValueSerializer(LongSerializer.INSTANCE);

		return redisTemplate;
	}

	private RedisTemplate<String, String> getRedisStringTemplate() {

		final StringRedisSerializer STRING_SERIALIZER = new StringRedisSerializer();

		redisStringTemplate.setKeySerializer(STRING_SERIALIZER);

		redisStringTemplate.setValueSerializer(STRING_SERIALIZER);

		return redisStringTemplate;
	}

	private String getKeyByType(String gooruOId, String type) {

		String countKey = getRedisInstance();

		if (type.equalsIgnoreCase(VIEWS)) {
			countKey += "-view-count-" + gooruOId;
		} else if (type.equalsIgnoreCase(SUBSCRIPTION)) {
			countKey += "-subscription-count-" + gooruOId;
		}

		else if (type.equalsIgnoreCase(SESSIONTOKEN)) {
			countKey += "-sessionToken-" + gooruOId;
		}

		return countKey;
	}

	private String getRedisInstance() {
		try {
			if (redisInstanceName == null) {
				// FIXME
				redisInstanceName = settingService.getConfigSetting(ConfigConstants.REDIS_INSTANCE_NAME, TaxonomyUtil.GOORU_ORG_UID);
			}
		} catch (Exception e) {
			System.out.println("Redis Error" + e);
		}
		return redisInstanceName;
	}

	public void setCollectionUtil(CollectionUtil collectionUtil) {
		this.collectionUtil = collectionUtil;
	}

	public CollectionUtil getCollectionUtil() {
		return collectionUtil;
	}

	private enum LongSerializer implements RedisSerializer<Long> {

		INSTANCE;

		@Override
		public byte[] serialize(Long aLong) throws SerializationException {
			if (null != aLong) {
				return aLong.toString().getBytes();
			} else {
				return new byte[0];
			}
		}

		@Override
		public Long deserialize(byte[] bytes) throws SerializationException {
			if (bytes != null && bytes.length > 0) {
				return Long.parseLong(new String(bytes));
			} else {
				return null;
			}
		}
	}

	@Override
	public void addSessionEntry(String sessionToken, Organization organization) {
		ValueOperations<String, String> valueOperations = getValueOperation();
		try {
			valueOperations.set(sessionToken, sessionToken);
		} catch (Exception e) {
			System.out.println("Reids Error" + e);
		}
		Date currentDate = new Date();
		long expiresTimes = 24;
		if (organization != null) {
			Map<String, String> expireTimeList = settingService.getOrganizationExpireTime(SESSION_EXPIRES_TIME);
			if (expireTimeList.containsKey(organization.getPartyUid())) {
				expiresTimes = Long.parseLong(expireTimeList.get(organization.getPartyUid()));
			}
		}
		Long createdTime = currentDate.getTime() + (1000 * 60 * 60 * expiresTimes);
		currentDate.setTime(createdTime);
		// currentDate.setTime(currentDate.getTime()+300000);
		try {
			redisStringTemplate.expireAt(sessionToken, currentDate);
		} catch (Exception e) {
			System.out.println("Error" + e);
		}
	}

	@Override
	public String getValue(String key) {
		ValueOperations<String, String> valueOperations = getValueOperation();
		if (valueOperations != null) {
			try {
				return valueOperations.get(returnSanitizedKey(key));
			} catch (Exception e) {
				logger.error("Get Values from redis failed!" + e.getMessage());
			}
		} else {
			return null;
		}
		return null;

	}
	
	@Override
	public String get(String key) {
		ValueOperations<String, String> valueOperations = getValueOperation();
		if (valueOperations != null) {
			try {
				return valueOperations.get(key);
			} catch (Exception e) {
				logger.error("Get Values from redis failed!" + e.getMessage());
			}
		}
		return null;
	}
	
	@Override
	public String getStandardValue(String key) {
		ValueOperations<String, String> valueOperations = getValueOperation();
		if (valueOperations != null) {
			try {
				return valueOperations.get(key);
			} catch (Exception e) {
				logger.error("Get Values from redis failed!" + e.getMessage());
			}
		} else {
			return null;
		}
		return null;

	}

	private ValueOperations<String, String> getValueOperation() {
		ValueOperations<String, String> valueOps = null;
		try {
			RedisTemplate<String, String> redisStringTemplate = getRedisStringTemplate();
			valueOps = redisStringTemplate.opsForValue();
		} catch (Exception e) {
			System.out.println("Redis Error" + e);
		}
		return valueOps;
	}

	@Override
	public void putValue(String key, String value) {
		ValueOperations<String, String> valueOperations = getValueOperation();
		try {
			valueOperations.set(returnSanitizedKey(key), value);
		} catch (Exception e) {
			System.out.println("Redis Error" + e);
		}
	}

	@Override
	public void putValue(String key, String value, long timeout) {
		ValueOperations<String, String> valueOperations = getValueOperation();
		try {
			valueOperations.set(returnSanitizedKey(key), value, timeout, TimeUnit.SECONDS);
		} catch (Exception e) {
			System.out.println("Redis Error" + e);
		}
	}

	@Override
	public void put(String key, String value) {
		ValueOperations<String, String> valueOperations = getValueOperation();
		try {
			valueOperations.set(key, value);
		} catch (Exception e) {
			System.out.println("Redis Error" + e);
		}
	}

	@Override
	public void deleteKey(String key) {
		redisStringTemplate.delete(returnSanitizedKey(key));
	}

	@Override
	public void delete(String key) {
		try {
			redisStringTemplate.delete(key);
		} catch (Exception e) {
			logger.error("Get Values from redis failed!" + e.getMessage());

		}
	}
	
	@Override
	public void bulkKeyDelete(String keyWildCard) {
		Set<String> keys = this.getkeys(keyWildCard);
		if (keys.size() > 0) {
			Iterator<String> iterator = keys.iterator();
			while (iterator.hasNext()) {
				redisStringTemplate.delete(iterator.next());
			}
		}
	}

	private String returnSanitizedKey(final String key) {
		return BaseUtil.appendProtocol(StringUtils.replace(key, " ", "")) + "_" + getReleaseVersion();
	}

	@Override
	public Set<String> getkeys(String key) {
		return redisStringTemplate.keys(key);
	}

	@Override
	public void setValuesMulti(Map<String, String> map) {
		ValueOperations<String, String> valueOperations = getValueOperation();
		try {
			valueOperations.multiSet(map);
		} catch (Exception e) {
			System.out.println("Redis Error" + e);
		}
	}

	public String getReleaseVersion() {
		return releaseVersion;
	}

	public void setReleaseVersion(String releaseVersion) {
		this.releaseVersion = releaseVersion;
	}

}
