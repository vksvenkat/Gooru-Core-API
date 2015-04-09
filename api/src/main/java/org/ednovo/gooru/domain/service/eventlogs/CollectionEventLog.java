package org.ednovo.gooru.domain.service.eventlogs;

import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CollectionEventLog implements ParameterProperties, ConstantProperties {

	private static final Logger LOGGER = LoggerFactory.getLogger(CollectionEventLog.class);

	public void getEventLogs(Collection collection, User user, boolean isCreate, boolean isUpdate, String data) throws Exception {
		try {
		if (isCreate) {
			SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_CREATE);
		} else if (isUpdate) {
			SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_EDIT);
		}
		JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
		if (collection != null) {
			context.put(CONTENT_GOORU_ID, collection != null ? collection.getGooruOid() : null);
		}
		SessionContextSupport.putLogParameter(CONTEXT, context.toString());
		JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
		if (isCreate) {
			payLoadObject.put(MODE, CREATE);
		} else if (isUpdate) {
			payLoadObject.put(MODE, EDIT);
		} else {
			payLoadObject.put(MODE, _COPY);
		}
		if (collection != null && collection.getCollectionType() != null && collection.getCollectionType().equalsIgnoreCase("collection")) {
			payLoadObject.put(ITEM_TYPE, SHELF_COLLECTION);
		} else if (collection != null && collection.getCollectionType() != null && collection.getCollectionType().equalsIgnoreCase("classpage")) {
			payLoadObject.put(ITEM_TYPE, CLASSPAGE);
		} else if (collection != null && collection.getCollectionType() != null && collection.getCollectionType().equalsIgnoreCase("folder")) {
			payLoadObject.put(ITEM_TYPE, SHELF_FOLDER);
		}
		if (data != null) {
			payLoadObject.put(_ITEM_DATA, data);
		}
		SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
		JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
		session.put(ORGANIZATION_UID, user != null && user.getOrganization() != null ? user.getOrganization().getPartyUid() : null);
		SessionContextSupport.putLogParameter(SESSION, session.toString());
		
	} catch (JSONException e) {
		LOGGER.error(_ERROR, e);
	}
	}

	public void getEventLogs(CollectionItem collectionItem, boolean isCreate, boolean isAdd, User user, boolean isCopy, boolean isEdit, String data) {
		try {
			String collectionType = collectionItem.getCollection().getCollectionType();
			if (isCreate) {
				SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_CREATE);
			} else {
				SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_EDIT);
			}
			JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
			context.put(PARENT_GOORU_ID, collectionItem != null && collectionItem.getCollection() != null ? collectionItem.getCollection().getGooruOid() : null);
			context.put(CONTENT_GOORU_ID, collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getGooruOid() : null);
			context.put(CONTENT_ITEM_ID, collectionItem != null ? collectionItem.getCollectionItemId() : null);
			if (isCopy) {
				context.put(SOURCE_GOORU_ID, collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getCopiedResourceId() : null);
			}
			SessionContextSupport.putLogParameter(CONTEXT, context.toString());
			JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();

			if (isCopy) {
				payLoadObject.put(MODE, _COPY);
			} else if (isAdd) {
				payLoadObject.put(MODE, ADD);
			} else if (isCreate) {
				payLoadObject.put(MODE, CREATE);
			} else if (isEdit) {
				payLoadObject.put(MODE, EDIT);
			}
			if (data != null) {
				payLoadObject.put(_ITEM_DATA, data);
			}
			payLoadObject.put(ITEM_SEQUENCE, collectionItem != null ? collectionItem.getItemSequence() : null);
			payLoadObject.put(ITEM_ID, collectionItem != null ? collectionItem.getCollectionItemId() : null);
			payLoadObject.put(ASSOCIATION_DATE, collectionItem != null && collectionItem.getAssociationDate() != null ? collectionItem.getAssociationDate().getTime() : null);

			if (collectionType != null && collectionItem != null) {
				if (collectionType.equalsIgnoreCase(CollectionType.SHElf.getCollectionType())) {
					if (collectionItem != null && collectionItem.getResource() != null) {
						String typeName = collectionItem.getResource().getResourceType().getName();
						if (typeName.equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())) {
							payLoadObject.put(ITEM_TYPE, SHELF_COLLECTION);
						} else if (typeName.equalsIgnoreCase(ResourceType.Type.FOLDER.getType())) {
							payLoadObject.put(ITEM_TYPE, SHELF_FOLDER);
						}
					}
				} else if (collectionType.equalsIgnoreCase(CollectionType.COLLECTION.getCollectionType())) {
					payLoadObject.put(ITEM_TYPE, COLLECTION_RESOURCE);
				} else if (collectionType.equalsIgnoreCase(CollectionType.FOLDER.getCollectionType())) {
					if (collectionItem != null && collectionItem.getResource() != null) {
						String itemTypeName = collectionItem.getResource().getResourceType().getName();
						if (itemTypeName.equalsIgnoreCase(ResourceType.Type.FOLDER.getType())) {
							payLoadObject.put(ITEM_TYPE, FOLDER_FOLDER);
						} else if (itemTypeName.equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())) {
							payLoadObject.put(ITEM_TYPE, FOLDER_COLLECTION);
						}
					}
				} else if (collectionType.equalsIgnoreCase(CollectionType.CLASSPAGE.getCollectionType())) {
					payLoadObject.put(ITEM_TYPE, CLASSPAGE_COLLECTION);
				}
			}
			payLoadObject.put(PARENT_CONTENT_ID, collectionItem != null && collectionItem.getCollection() != null ? collectionItem.getCollection().getContentId() : null);
			payLoadObject.put(CONTENTID, collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getContentId() : null);
			payLoadObject.put(TITLE, collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getTitle() : null);
			payLoadObject.put(DESCRIPTION, collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getDescription() : null);
			SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
			JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
			session.put(ORGANIZATION_UID, user != null && user.getOrganization() != null ? user.getOrganization().getPartyUid() : null);
			SessionContextSupport.putLogParameter(SESSION, session.toString());
		} catch (Exception e) {
			LOGGER.error(_ERROR, e);
		}
	}

	public void getEventLogs(CollectionItem collectionItem, boolean isCreate, boolean isAdd, User user, boolean isCopy, boolean isEdit, CollectionItem sourceCollectionItem, String data) {
		try {
			getEventLogs(collectionItem, isCreate, isAdd, user, isCopy, isEdit, data);
			JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
			context.put(SOURCE_GOORU_ID, sourceCollectionItem.getResource().getGooruOid());
			SessionContextSupport.putLogParameter(CONTEXT, context.toString());
		} catch (Exception e) {
			LOGGER.error(_ERROR, e);
		}
	}

	public void getEventLogs(CollectionItem collectionItem, String data, User user) {
		try {
		SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_EDIT);
		JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
		if (collectionItem != null) {
			context.put(CONTENT_GOORU_ID, collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getGooruOid() : null);
			context.put(CONTENT_ITEM_ID, collectionItem != null ? collectionItem.getCollectionItemId() : null);
			context.put(PARENT_GOORU_ID, collectionItem != null && collectionItem.getCollection() != null ? collectionItem.getCollection().getGooruOid() : null);
		}
		SessionContextSupport.putLogParameter(CONTEXT, context.toString());
		JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
		payLoadObject.put(MODE, EDIT);
		payLoadObject.put(ITEM_TYPE, COLLECTION_RESOURCE);
		if (data != null) {
			payLoadObject.put(_ITEM_DATA, data);			
		}
		SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
		JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
		session.put(ORGANIZATION_UID, user.getOrganization().getPartyUid());
		SessionContextSupport.putLogParameter(SESSION, session.toString());
		} catch (Exception e) {
			LOGGER.error(_ERROR, e);
		}
	}

	public void getEventLogs(CollectionItem collectionItem, User user, String collectionType) throws JSONException {
		SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_DELETE);
		JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
		context.put(PARENT_GOORU_ID, collectionItem != null && collectionItem.getCollection() != null ? collectionItem.getCollection().getGooruOid() : null);
		context.put(CONTENT_GOORU_ID, collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getGooruOid() : null);
		SessionContextSupport.putLogParameter(CONTEXT, context.toString());
		JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
		if (collectionType != null && collectionItem != null) {
			if (collectionType.equalsIgnoreCase(CollectionType.SHElf.getCollectionType())) {
				if (collectionItem.getResource() != null) {
					String typeName = collectionItem.getResource().getResourceType().getName();
					if (typeName.equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())) {
						payLoadObject.put(ITEM_TYPE, SHELF_COLLECTION);
					} else if (typeName.equalsIgnoreCase(ResourceType.Type.FOLDER.getType())) {
						payLoadObject.put(ITEM_TYPE, SHELF_FOLDER);
					}
				}
			} else if (collectionType.equalsIgnoreCase(CollectionType.COLLECTION.getCollectionType())) {
				payLoadObject.put(ITEM_TYPE, COLLECTION_RESOURCE);
			} else if (collectionType.equalsIgnoreCase(CollectionType.FOLDER.getCollectionType())) {
				if (collectionItem.getResource() != null) {
					String itemTypeName = collectionItem.getResource().getResourceType().getName();
					if (itemTypeName.equalsIgnoreCase(ResourceType.Type.FOLDER.getType())) {
						payLoadObject.put(ITEM_TYPE, FOLDER_FOLDER);
					} else if (itemTypeName.equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())) {
						payLoadObject.put(ITEM_TYPE, FOLDER_COLLECTION);
					}
				}
			} else if (collectionType.equalsIgnoreCase(CollectionType.CLASSPAGE.getCollectionType())) {
				payLoadObject.put(ITEM_TYPE, CLASSPAGE_COLLECTION);
			}
			if (collectionItem != null && collectionItem.getResource() != null && collectionItem.getResource().getResourceType() != null && collectionItem.getResource().getResourceType().getName().equalsIgnoreCase(ResourceType.Type.PATHWAY.getType())) {
				payLoadObject.put(ITEM_TYPE, CLASSPAGE_PATHWAY);
			}
		}
		payLoadObject.put(MODE, DELETE);
		payLoadObject.put(ITEM_ID, collectionItem != null ? collectionItem.getCollectionItemId() : null);
		payLoadObject.put(PARENT_CONTENT_ID, collectionItem != null && collectionItem.getCollection() != null ? collectionItem.getCollection().getContentId() : null);
		payLoadObject.put(CONTENTID, collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getContentId() : null);
		SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
		JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
		session.put(ORGANIZATION_UID, user != null && user.getOrganization() != null ? user.getOrganization().getPartyUid() : null);
		SessionContextSupport.putLogParameter(SESSION, session.toString());
	}

	public void getEventLogs(CollectionItem collectionItem, boolean isMoveMode, boolean isCreate, User user, String collectionType) throws JSONException {
		SessionContextSupport.putLogParameter(EVENT_NAME, ITEM_CREATE);
		JSONObject context = SessionContextSupport.getLog().get(CONTEXT) != null ? new JSONObject(SessionContextSupport.getLog().get(CONTEXT).toString()) : new JSONObject();
		context.put(PARENT_GOORU_ID, collectionItem != null && collectionItem.getCollection() != null ? collectionItem.getCollection().getGooruOid() : null);
		context.put(CONTENT_GOORU_ID, collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getGooruOid() : null);
		context.put(SOURCE_GOORU_ID, collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getGooruOid() : null);
		context.put(CONTENT_ITEM_ID, collectionItem != null ? collectionItem.getCollectionItemId() : null);
		SessionContextSupport.putLogParameter(CONTEXT, context.toString());
		JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
		if (isMoveMode) {
			payLoadObject.put(MODE, MOVE);
		} else if (isCreate) {
			payLoadObject.put(MODE, CREATE);
		} else {
			payLoadObject.put(MODE, ADD);
		}
		payLoadObject.put(ITEM_SEQUENCE, collectionItem != null ? collectionItem.getItemSequence() : null);
		payLoadObject.put(ITEM_ID, collectionItem != null ? collectionItem.getCollectionItemId() : null);
		payLoadObject.put(ASSOCIATION_DATE, collectionItem != null ? collectionItem.getAssociationDate().getTime() : null);

		if (collectionType != null && collectionItem != null) {
			if (collectionType.equalsIgnoreCase(CollectionType.SHElf.getCollectionType())) {
				if (collectionItem.getResource() != null) {
					String typeName = collectionItem.getResource().getResourceType().getName();
					if (typeName.equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())) {
						payLoadObject.put(ITEM_TYPE, SHELF_COLLECTION);
					} else if (typeName.equalsIgnoreCase(ResourceType.Type.FOLDER.getType())) {
						payLoadObject.put(ITEM_TYPE, SHELF_FOLDER);
					}
				}
			} else if (collectionType.equalsIgnoreCase(CollectionType.COLLECTION.getCollectionType())) {
				payLoadObject.put(ITEM_TYPE, COLLECTION_RESOURCE);
			} else if (collectionType.equalsIgnoreCase(CollectionType.FOLDER.getCollectionType())) {
				if (collectionItem.getResource() != null) {
					String itemTypeName = collectionItem.getResource().getResourceType().getName();
					if (itemTypeName.equalsIgnoreCase(ResourceType.Type.FOLDER.getType())) {
						payLoadObject.put(ITEM_TYPE, FOLDER_FOLDER);
					} else if (itemTypeName.equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())) {
						payLoadObject.put(ITEM_TYPE, FOLDER_COLLECTION);
					}
				}
			} else if (collectionType.equalsIgnoreCase(CollectionType.CLASSPAGE.getCollectionType())) {
				payLoadObject.put(ITEM_TYPE, CLASSPAGE_COLLECTION);
			} else if (collectionType.equalsIgnoreCase(CollectionType.PATHWAY.getCollectionType())) {
				payLoadObject.put(ITEM_TYPE, PATHWAY_COLLECTION);
			}
		}
		payLoadObject.put(PARENT_CONTENT_ID, collectionItem != null && collectionItem.getCollection() != null ? collectionItem.getCollection().getContentId() : null);
		payLoadObject.put(CONTENT_ID, collectionItem != null && collectionItem.getResource() != null ? collectionItem.getResource().getContentId() : null);
		payLoadObject.put(TITLE, collectionItem != null && collectionItem.getResource() != null && collectionItem.getResource().getTitle() != null ? collectionItem.getResource().getTitle() : null);
		payLoadObject.put(DESCRIPTION, collectionItem != null && collectionItem.getResource() != null && collectionItem.getResource().getDescription() != null ? collectionItem.getResource().getDescription() : null);
		SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
		JSONObject session = SessionContextSupport.getLog().get(SESSION) != null ? new JSONObject(SessionContextSupport.getLog().get(SESSION).toString()) : new JSONObject();
		session.put(ORGANIZATION_UID, user.getOrganization().getPartyUid());
		SessionContextSupport.putLogParameter(SESSION, session.toString());
	}

	public void getEventLogs(CollectionItem collectionItem, boolean isMoveMode, boolean isCreate, User user, String collectionType, CollectionItem sourceCollectionItem) throws Exception {
		this.getEventLogs(collectionItem, isMoveMode, isCreate, user, collectionType);
		JSONObject payLoadObject = SessionContextSupport.getLog().get(PAY_LOAD_OBJECT) != null ? new JSONObject(SessionContextSupport.getLog().get(PAY_LOAD_OBJECT).toString()) : new JSONObject();
		payLoadObject.put(SOURCE_ITEM_ID, sourceCollectionItem != null ? sourceCollectionItem.getCollectionItemId() : null);
		SessionContextSupport.putLogParameter(PAY_LOAD_OBJECT, payLoadObject.toString());
	}
}
