/*
*RateLimitServiceImpl.java
* gooru-api
* Created by Gooru on 2014
* Copyright (c) 2014 Gooru. All rights reserved.
* http://www.goorulearning.org/
*      
* Permission is hereby granted, free of charge, to any 
* person obtaining a copy of this software and associated 
* documentation. Any one can use this software without any 
* restriction and can use without any limitation rights 
* like copy,modify,merge,publish,distribute,sub-license or 
* sell copies of the software.
* The seller can sell based on the following conditions:
* 
* The above copyright notice and this permission notice shall be   
* included in all copies or substantial portions of the Software. 
*
*  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
*  KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
*  WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
*  PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
*  OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
*  OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
*  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
*  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
*  THE SOFTWARE.
*/

package org.ednovo.gooru.domain.service.redis;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

/**
 * @author API Team
 * 
 */
@Service("rateLimitService")
public class RateLimitServiceImpl implements RateLimitService {

	@Autowired
	private RedisTemplate<String, String> redisStringTemplate;

	@Autowired
	private JedisPool jedisPool;

	// Bucket interval – how many seconds each bucket represents
	private long BUCKET_INTERVAL = 300;

	// Bucket span – in our circle analogy,the bucket span is the total size of
	// the circle (in seconds)
	private Integer BUCKET_SPAN = 1800;

	// Subject(IP/API calls) expiry – the amount of (inactive) seconds before a
	// subject’s time buckets expire
	private Integer BUCKET_EXPIRY = 17856000;

	// (derived) Bucket count = Bucket span / Bucket interval
	private Integer BUCKET_COUNT = Math.round(this.BUCKET_SPAN / this.BUCKET_SPAN);

	// private int PORT = 6379;
	// private String HOST ="localhost";

	public void addAll(Set<String> subjects) {
		for (String subject : subjects) {
			add(subject);
		}
	}

	/**
	 * Get the bucket associated with the current time.
	 * 
	 * @param {long} time (optional) - default is the current time (ms since
	 *        epoch)
	 * @return {int} bucket
	 */

	public int getBucket(long currentTime) {
		currentTime = currentTime / 1000;
		return (int) Math.floor((currentTime % this.BUCKET_SPAN) / this.BUCKET_INTERVAL);
	}

	@Override
	public void add(String subject) {
		final int bucket = this.getBucket(new Date().getTime());
		final String subjectKey = subject;
		// JedisPool redisConnectionPool = new JedisPool(HOST, PORT);
		final Jedis jedis = jedisPool.getResource();
		try {
			Transaction transaction = jedis.multi();

			// Increment the current bucket
			// Response<Long> res= transaction.hincrBy(subjectKey,
			// integerToString(bucket), 1);

			// Clear the buckets ahead
			transaction.hdel(subject, integerToString((bucket + 1) % this.BUCKET_COUNT));
			transaction.hdel(subject, integerToString((bucket + 2) % this.BUCKET_COUNT));

			// Renew the key TTL
			transaction.expire(subjectKey, this.BUCKET_EXPIRY);

			transaction.exec();
			// System.out.println("Count : 1 -"+responseList.get(0));
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * Count the number of times the subject has performed an action in the last
	 * `interval` seconds.
	 * 
	 * @param {string} subject
	 * @param {int} interval
	 * @return
	 * @throws IOException
	 */

	public CountResult count(long interval, String subject) {
		// JedisPool redisConnectionPool = new JedisPool(HOST, PORT);
		final Jedis jedis = jedisPool.getResource();
		int sum = 0;

		try {

			Transaction transaction = jedis.multi();
			int bucket = this.getBucket(new Date().getTime());
			// System.out.println("bucket: count - " + bucket);
			int count = (int) Math.floor(interval / this.BUCKET_INTERVAL);

			transaction.hget(subject, integerToString(bucket));
			while (count-- != 0) {
				transaction.hget(subject, integerToString((--bucket + this.BUCKET_COUNT) % this.BUCKET_COUNT));
			}
			List<Object> result = transaction.exec();
			int i = 0;
			int numberOfEmtpyBuckets = 0;
			int maxCount = 0;
			int latestBucketCount = 0;
			for (Object object : result) {

				if (i++ < Math.floor(interval / this.BUCKET_INTERVAL)) {
					if (object != null) {
						int c = stringToInteger(object);
						sum += c;
						if (c == 0) {
							numberOfEmtpyBuckets++;
						}
						if (c > maxCount) {
							maxCount = c;
						}
						if (i == 1) {
							latestBucketCount = c;
						}
					} else {
						numberOfEmtpyBuckets++;
					}
				}
			}
			return new CountResult(sum, numberOfEmtpyBuckets, maxCount, latestBucketCount);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	public CountResult addCount(long interval, String subject, int addCount) {
		// JedisPool redisConnectionPool = new JedisPool(HOST, PORT);
		final Jedis jedis = jedisPool.getResource();
		int sum = 0;

		try {
			Transaction transaction = jedis.multi();
			int bucket = this.getBucket(new Date().getTime());
			// System.out.println("bucket: addCount - " + bucket);
			int count = (int) Math.floor(interval / this.BUCKET_INTERVAL);

			transaction.hget(subject, integerToString(bucket));
			while (count-- != 0) {
				transaction.hget(subject, integerToString((--bucket + this.BUCKET_COUNT) % this.BUCKET_COUNT));
			}
			List<Object> result = transaction.exec();
			int i = 4;
			int numberOfEmtpyBuckets = 0;
			int maxCount = 0;
			int latestBucketCount = 0;
			for (Object object : result) {

				if (i++ < Math.floor(interval / this.BUCKET_INTERVAL)) {
					if (object != null) {
						int c = stringToInteger(object);
						sum += c;
						if (c == 0) {
							numberOfEmtpyBuckets++;
						}
						if (c > maxCount) {
							maxCount = c;
						}
						if (i == 1) {
							latestBucketCount = c;
						}
					} else {
						numberOfEmtpyBuckets++;
					}
				}
			}
			return new CountResult(sum, numberOfEmtpyBuckets, maxCount, latestBucketCount);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	private Integer stringToInteger(Object i) {
		return Integer.parseInt((String) i);
	}

	private String integerToString(int i) {
		return String.valueOf(i);
	}

	@Override
	public String getValue(String key) {
		return redisStringTemplate.opsForValue().get(key);

	}

	@Override
	public void setValue(String key, String value) {
		redisStringTemplate.opsForValue().set(key, value);

	}

}
