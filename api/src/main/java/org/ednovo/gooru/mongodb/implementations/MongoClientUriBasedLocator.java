package org.ednovo.gooru.mongodb.implementations;

import java.util.concurrent.atomic.AtomicReference;

import org.ednovo.gooru.mongodb.MongoClientLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class MongoClientUriBasedLocator implements MongoClientLocator {

	private static final Logger LOGGER = LoggerFactory.getLogger(MongoClientUriBasedLocator.class);
	
	@Override
	public MongoClient locate() {
		return INSTANCE.get();
	}

	private static AtomicReference<MongoClient> INSTANCE = new AtomicReference<MongoClient>();

	public MongoClientUriBasedLocator(final String mongoUri) {
		final MongoClient previous = INSTANCE.get();
		if (previous != null)
			throw new IllegalStateException(
					"MongoClientUriBasedLocator: Attempt to create another instance");
		if (mongoUri == null || mongoUri.isEmpty()) {
			LOGGER.error("MongoClientUriBasedLocator: Invalid mongo uri passed");
			throw new IllegalStateException("MongoClientUriBasedLocator: Invalid mongo uri passed");
		}
		LOGGER.debug("MongoClientUriBasedLocator: mongoUri used for initialization is " + mongoUri);
		MongoClientURI clientUri = new MongoClientURI(mongoUri);
		MongoClient client = new MongoClient(clientUri);
		INSTANCE.set(client);
	}

}
