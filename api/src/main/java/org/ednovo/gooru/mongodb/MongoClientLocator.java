package org.ednovo.gooru.mongodb;

import com.mongodb.MongoClient;

public interface MongoClientLocator {
	public MongoClient locate();
}
