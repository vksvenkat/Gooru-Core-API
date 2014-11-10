/////////////////////////////////////////////////////////////
// AsyncExecutor.java
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
package org.ednovo.gooru.application.util;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.application.util.RequestUtil;
import org.ednovo.gooru.domain.service.resource.ResourceManager;
import org.ednovo.gooru.domain.service.revision_history.RevisionHistoryService;
import org.ednovo.gooru.domain.service.storage.S3ResourceApiHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@Async
@Transactional(propagation = Propagation.NEVER)
public class AsyncExecutor {

	@Autowired
	private CollectionUtil collectionUtil;

	private TransactionTemplate transactionTemplate;

	@Autowired
	private RevisionHistoryService revisionHistoryService;

	@Autowired
	private HibernateTransactionManager transactionManager;

	@Autowired
	@javax.annotation.Resource(name = "resourceManager")
	private ResourceManager resourceManager;

	private Logger logger = LoggerFactory.getLogger(AsyncExecutor.class);

	@Autowired
	private S3ResourceApiHandler s3ResourceApiHandler;

	@PostConstruct
	public void init() {
		transactionTemplate = new TransactionTemplate(transactionManager);
	}

	public void createVersion(final Collection collection, final String type, final String gooruUid) {

		transactionTemplate.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				try {
					getRevisionHistoryService().createVersion(collection, type, gooruUid);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}

	public void copyResourceFolder(final Resource srcResource, final Resource destResource) {
		transactionTemplate.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				try {
					logger.debug("coping resource folder");
					getResourceManager().copyResourceRepository(srcResource, destResource);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}

	void executeRestAPI(final Map<String, Object> param, final String requestUrl, final String requestType) {
		transactionTemplate.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				RequestUtil.executeRestAPI(param, requestUrl, requestType);
				return null;
			}
		});

	}

	public void executeRestAPI(final String data, final String requestUrl, final String requestType) {
		transactionTemplate.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				RequestUtil.executeRestAPI(data, requestUrl, requestType);
				return null;
			}
		});

	}

	public void uploadResourceFolder(final Resource resource) {
		transactionTemplate.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				getS3ResourceApiHandler().uploadResourceFolder(resource);
				return null;
			}
		});
	}

	public void uploadResourceFile(final Resource resource, final String url) {
		transactionTemplate.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				getS3ResourceApiHandler().uploadResourceFile(resource, url);
				return null;
			}
		});
	}

	public void deleteResourceFile(final Resource resource, final String fileName) {
		transactionTemplate.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				getS3ResourceApiHandler().deleteResourceFile(resource, fileName);
				return null;
			}
		});
	}
	
	public void updateResourceFileInS3(final String fileName,final String sourcePath, final String gooruContentId) {
		transactionTemplate.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				try {
					Thread.sleep(500);
					getS3ResourceApiHandler().moveFileToS3(fileName, sourcePath, gooruContentId);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}

	public void clearCache(final String gooruOid) {
		transactionTemplate.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				getCollectionUtil().clearResourceCache(gooruOid);
				return null;
			}
		});
	}
	
	public void deleteFromCache(final String key) {
		transactionTemplate.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				getCollectionUtil().deleteFromCache(key);
				return null;
			}
		});
	}

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public CollectionUtil getCollectionUtil() {
		return collectionUtil;
	}

	public RevisionHistoryService getRevisionHistoryService() {
		return revisionHistoryService;
	}

	public S3ResourceApiHandler getS3ResourceApiHandler() {
		return s3ResourceApiHandler;
	}

}
