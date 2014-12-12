/////////////////////////////////////////////////////////////
// RedisService.java
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

import java.util.Map;
import java.util.Set;

import org.ednovo.gooru.cassandra.core.service.CassandraCacheService;
import org.ednovo.gooru.core.api.model.Organization;


public interface RedisService extends CassandraCacheService {

	static int DEFAULT_PROFILE_EXP = 86400;
	static int DEFAULT_FEATURED_EXP = 3600;
	static int DEFAULT_CONTENT_EXP = 600;

	Long getCount(String gooruOId, String type);

	Long incrementCount(String gooruOId, String type);

	void deleteSubcriptionCount(String gooruOId);

	void deleteEntry(String gooruOId);

	void updateAllCount(String resourceType);

	void updateRedisCount(Long count, String type, String gooruOId);

	void addSessionEntry(String sessionToken, Organization organizationUid);

	String getValue(String key);
	
	String get(String key);

	void putValue(String key, String value);
	
	void put(String key, String value);

	void putValue(String key, String value, long timeout);

	void deleteKey(String key);
	
	void delete(String key);

	
	Set<String> getkeys(String key); 
	
	void bulkKeyDelete(String key);
	
	void setValuesMulti(Map<String,String> map);

	String getStandardValue(String key);
}
