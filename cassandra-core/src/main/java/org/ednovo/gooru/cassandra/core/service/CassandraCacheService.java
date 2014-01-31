package org.ednovo.gooru.cassandra.core.service;

public interface CassandraCacheService {

	String getValue(String key);

	void putValue(String key, String value);

	void deleteKey(String key);
}
