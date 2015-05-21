/////////////////////////////////////////////////////////////
// IndexProcessor.java
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
package org.ednovo.gooru.infrastructure.messenger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.annotation.PostConstruct;

import org.ednovo.gooru.core.api.model.GooruAuthenticationToken;
import org.ednovo.gooru.core.api.model.SearchIndexMeta;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.domain.service.content.ContentService;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.kafka.producer.KafkaProducer;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import flexjson.JSONSerializer;

@Component
public class IndexProcessor extends BaseComponent {

	@Autowired
	private HibernateTransactionManager transactionManager;

	private TransactionTemplate transactionTemplate;
	
	@Autowired
	private KafkaProducer kafkaProducer;
	
	@Autowired
	private RedisService redisService;
	
	private static final JSONSerializer SERIALIZER = new JSONSerializer();

	
	@Autowired
	private ContentService contentService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IndexProcessor.class);
	
	private static final String DEFAULT_INDEX_MODE = "kafka";
	
	private static final String INDEX_MODE = "index-mode";

	public static final String SEARCH_REINDEX_MSG = "reindex";
	public static final String SEARCH_BULK_INDEX_MSG = "bulkIndex";
	public static final String SEARCH_CREATE_INDEX_MSG = "create";
	public static final String SEARCH_DELETE_INDEX_MSG = "delete";
	
	private static final String INDEX_IDS = "indexableIds";
	private static final String INDEX_TYPE = "type";
	private static final String INDEX_ACTION = "action";
	private static final String IS_UPDATE_USER_CONTENT = "isUpdateUserContent";
	
	public static final String INDEX = "index";

	public static final String DELETE = "delete";

	@PostConstruct
	public void init() {
		transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setReadOnly(true);
	}

	public void index(final String uuids, final String action, final String type) {
		index(uuids, action, type, false, false);
	}

	public void indexStas(final String uuids, final String action, final String type) {
		String indexMode = redisService.getValue("index-mode");
		if(indexMode != null && indexMode.equalsIgnoreCase("kafka")){
			Map<String, Object> indexData = new HashMap<String, Object>();
			indexData.put("indexableIds", uuids);
			indexData.put("type", type);
			indexData.put("action", action);
			indexData.put("priority", "0");
			String indexMsg = SERIALIZER.deepSerialize(indexData);
			kafkaProducer.send(indexMsg, type, Constants.REINDEX_TYPES.get(type));
		}
		else{
			index(uuids, action, type, false, false);
		}
	}

	public void index(final String uuids, final String action, final String type, String sessionToken) {
		final GooruAuthenticationToken authentication = (GooruAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		index(uuids, action, type, sessionToken, authentication, false, false);
	}
	
	public void index(final String uuids, final String action, final String type,boolean isUpdateUserContent,  String sessionToken) {
		final GooruAuthenticationToken authentication = (GooruAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		index(uuids, action, type, sessionToken, authentication, isUpdateUserContent, false);
	}

	public void index(final String uuids, final String action, final String type, final boolean isUpdateUserContent){
		index(uuids, action, type, isUpdateUserContent, false);
	}
	
	public void index(final String uuids, final String action, final String type, final boolean isUpdateUserContent, final boolean isUpdateStas) {
		final String sessionToken = UserGroupSupport.getSessionToken();
		final GooruAuthenticationToken authentication = (GooruAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		index(uuids, action, type, sessionToken, authentication, isUpdateUserContent, isUpdateStas);
	}
	
	public void index(List<SearchIndexMeta> searchIndexMetaList){
		for(SearchIndexMeta searchIndexMeta : searchIndexMetaList){
			String sessionToken = searchIndexMeta.getSessionToken();
			if(sessionToken == null){
				sessionToken = UserGroupSupport.getSessionToken();
			}
			final GooruAuthenticationToken authentication = (GooruAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
			String indexMode = redisService.getValue(INDEX_MODE) != null ? redisService.getValue(INDEX_MODE) : DEFAULT_INDEX_MODE;
			if(indexMode.equalsIgnoreCase(DEFAULT_INDEX_MODE) && !searchIndexMeta.getUpdateUserContent()){
				indexByKafkaQueue(searchIndexMeta.getReIndexIds(), searchIndexMeta.getAction(), searchIndexMeta.getType(), sessionToken, authentication, searchIndexMeta.getUpdateUserContent(), searchIndexMeta.getUpdateStatisticsData());
			}
			else{
				index(searchIndexMeta.getReIndexIds(), searchIndexMeta.getAction(), searchIndexMeta.getType(), sessionToken, authentication, searchIndexMeta.getUpdateUserContent(), searchIndexMeta.getUpdateStatisticsData());
			}

	    }
	}
	
	private void indexByKafkaQueue(final String uuids, final String action, final String type, final String sessionToken, final GooruAuthenticationToken authentication, final boolean isUpdateUserContent, final boolean isUpdateStas){
		Map<String, Object> indexData = new HashMap<String, Object>();
		indexData.put(INDEX_IDS, uuids);
		indexData.put(INDEX_TYPE, type);
		indexData.put(INDEX_ACTION, action);
		indexData.put(IS_UPDATE_USER_CONTENT, isUpdateUserContent);
		String indexMsg = SERIALIZER.deepSerialize(indexData);
		kafkaProducer.send(indexMsg, type, Constants.REINDEX_TYPES.get(type));
	}

	public void index(final String uuids, final String action, final String type, final String sessionToken, final GooruAuthenticationToken authentication, final boolean isUpdateUserContent, final boolean isUpdateStas) {
		final String[] ids = uuids.split(",");
		try {
			final Thread indexThread = new Thread(new Runnable() {

				@Override
				public void run() {
					String url = getSearchApiPath() + "index/es-aca/" + type + "/" + action + "?sessionToken=" + sessionToken + "&ids=" + uuids ;
					if(isUpdateStas){
						url = url + "&isUpdateStats=true";
					}
					
					new ClientResourceExecuter() {    
						
						@Override
						public void run(ClientResource clientResource, Representation representation) throws Exception {
							clientResource.getLogger().setLevel(Level.WARNING);
							String url = getSearchApiPath() + "index/es-aca/" + type + "/" + action + "?sessionToken=" + sessionToken + "&ids=" + uuids ;							
							try {
								clientResource = new ClientResource(url);
								representation = clientResource.post(new Form().getWebRepresentation());
							} catch (Exception exception) {
								getLogger().error("Error in Indexing: ", exception);
								throw exception;
							} finally {
								releaseClientResources(clientResource, representation);
							}

							if (type.equalsIgnoreCase("user") && isUpdateUserContent) {
								transactionTemplate.execute(new TransactionCallbackWithoutResult() {

									@Override
									protected void doInTransactionWithoutResult(TransactionStatus status) {
										for (String userUid : ids) {
											SecurityContextHolder.getContext().setAuthentication(authentication);
											List<Object[]> userids = contentService.getIdsByUserUId(userUid, null, null, null);
											StringBuilder resourceGooruOIds = new StringBuilder();
											StringBuilder scollectionGooruOIds = new StringBuilder();
											for (Object[] value : userids) {
												if (value[2].equals("scollection")) {
													if (scollectionGooruOIds.length() > 0) {
														scollectionGooruOIds.append(",");
													}
													scollectionGooruOIds.append(value[1]);
												} else {
													if (resourceGooruOIds.length() > 0) {
														resourceGooruOIds.append(",");
													}
													resourceGooruOIds.append(value[1]);
												}
											}
											if (scollectionGooruOIds.length() > 0) {
												index(scollectionGooruOIds.toString(), IndexProcessor.INDEX, "scollection", sessionToken, authentication, false, isUpdateStas);
											}
											if (resourceGooruOIds.length() > 0) {
												index(resourceGooruOIds.toString(), IndexProcessor.INDEX, "resource", sessionToken, authentication, false, isUpdateStas);
											}
										}
									}
								});

							}
						}
					};
				}
			});
			indexThread.setDaemon(true);
			indexThread.start();
		} catch (Exception e) {
			LOGGER.info("Index Error : " + e.getMessage());
		}
}

}




