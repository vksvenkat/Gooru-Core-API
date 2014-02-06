/**
 * 
 */
package org.ednovo.gooru.controllers.api;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.CollectionService;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;


/**
 * @author Search Team
 * 
 */
@Controller
@RequestMapping(value = { "/scollection", "/folder" })
public class CollectionRestController extends BaseController implements ConstantProperties {

	@Autowired
	private CollectionService collectionService;

	@Autowired
	private BaseRepository baseRepository;

	@Autowired
	private RedisService redisService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ModelAndView createCollection(@RequestBody MultiValueMap<String, String> body, @RequestParam(value = ADD_TO_SHELF, required = false, defaultValue = FALSE) boolean addToShelf, @RequestParam(value = RESOURCE_ID, required = false) String resourceId,
			@RequestParam(value = PARENT_ID, required = false) String parentId, @RequestParam(value = TAXONOMY_CODE, required = false) String taxonomyCode, @RequestParam(value = UPDATE_TAXONOMY_BY_CODE, required = false, defaultValue = FALSE) boolean updateTaxonomyByCode,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		Collection collection = this.buildCollectionFromInputParameters(body.getFirst(DATA_OBJECT), user);
		ActionResponseDTO<Collection> responseDTO = getCollectionService().createCollection(collection, addToShelf, resourceId, taxonomyCode, updateTaxonomyByCode, parentId);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
			// To capture activity log
			SessionContextSupport.putLogParameter(EVENT_NAME, S_COLLECTION_CREATE);
			SessionContextSupport.putLogParameter(GOORU_OID, collection.getGooruOid());
			SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());

		}
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_INCLUDE);
		includes = (String[]) ArrayUtils.addAll(includes, ERROR_INCLUDE);
		return toModelAndView(serialize(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}", method = { RequestMethod.PUT })
	public ModelAndView updateCollection(@PathVariable(value = ID) String collectionId, @RequestBody MultiValueMap<String, String> body, @RequestParam(value = TAXONOMY_CODE, required = false) String taxonomyCode, @RequestParam(value = CREATOR_UID, required = false) String creatorUId,
			@RequestParam(value = OWNER_UID, required = false) String ownerUId, @RequestParam(value = RELATED_CONTENT_ID, required = false) String relatedContentId, @RequestParam(value = UPDATE_TAXONOMY_BY_CODE, required = false, defaultValue = FALSE) boolean updateTaxonomyByCode,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		Collection newCollection = this.buildCollectionFromInputParameters(body.getFirst(DATA_OBJECT), user);
		ActionResponseDTO<Collection> responseDTO = getCollectionService().updateCollection(newCollection, collectionId, taxonomyCode, ownerUId, creatorUId, hasUnrestrictedContentAccess(), relatedContentId, updateTaxonomyByCode);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			// To capture activity log
			SessionContextSupport.putLogParameter(EVENT_NAME, SCOLLECTION_UPDATE);
			SessionContextSupport.putLogParameter(GOORU_OID, newCollection.getGooruOid());
			SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());
			SessionContextSupport.putLogParameter(COLLECTION_ID, collectionId);
		}

		String[] includes = (String[]) ArrayUtils.addAll(COLLECTION_INCLUDE_FIELDS, ERROR_INCLUDE);
		if (taxonomyCode != null) {
			includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_TAXONOMY);
		}

		if (relatedContentId != null) {
			includes = (String[]) ArrayUtils.add(includes, "*.contentAssociation.associateContent");
		}
		this.getRedisService().deleteKey(COLLECTION_DATA_LIB + collectionId);
		return toModelAndView(serialize(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ModelAndView getCollection(@PathVariable(value = ID) String collectionId, @RequestParam(value = INCLUDE_COLLECTION_ITEM, required = false, defaultValue = TRUE) boolean includeCollectionItem,
			@RequestParam(value = INLCLUDE_META_INFO, required = false, defaultValue = FALSE) boolean includeMetaInfo, @RequestParam(value = INCLUDE_COLLABORATOR, required = false, defaultValue = FALSE) boolean includeCollaborator,
			@RequestParam(value = INCLUDE_RELATED_CONTENT, required = false, defaultValue = FALSE) boolean includeRelatedContent, @RequestParam(value = MERGE, required = false) String merge, @RequestParam(value = REQ_CONTEXT, required = false, defaultValue=EDIT_PLAY) String requestContext, HttpServletRequest request,
			HttpServletResponse response) {

		User user = (User) request.getAttribute(Constants.USER);
		Collection collection = null;
		String includes[] = null;
		if (requestContext != null && requestContext.equalsIgnoreCase(LIBRARY)) {
			includes = (String[]) ArrayUtils.addAll(LIBRARY_RESOURCE_INCLUDE_FIELDS, COLLECTION_ITEM_INCLUDE_FILEDS);
			includes = (String[]) ArrayUtils.addAll(includes, LIBRARY_COLLECTION_INCLUDE_FIELDS);
			includes = (String[]) ArrayUtils.addAll(includes, LIBRARY_CODE_INCLUDES);
			final String cacheKey = COLLECTION_DATA + requestContext + "-" + collectionId;
			String data = null;
			data = getRedisService().getValue(cacheKey);
			if (data == null) {
				data = serialize(this.getCollectionService().getCollection(collectionId, new HashMap<String, Object>()), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, false, true, includes);
				getRedisService().putValue(cacheKey, data, 86400);
			}
			return toModelAndView(data);
		} else {
			includes = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
			if (includeCollectionItem) {
				includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_INCLUDE_FILEDS);
			}
			if (includeMetaInfo) {
				includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_META_INFO);
				includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_TAXONOMY);
			}
			if (includeRelatedContent) {
				includes = (String[]) ArrayUtils.add(includes, "*.contentAssociation");
			}
			includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_TAGS);
			collection = getCollectionService().getCollection(collectionId, includeMetaInfo, includeCollaborator, includeRelatedContent, user, merge);
			return toModelAndViewWithIoFilter(collection, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
		}

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteCollection(@PathVariable(value = ID) String collectionId, HttpServletRequest request, HttpServletResponse response) {
		SessionContextSupport.putLogParameter(EVENT_NAME, SCOLLECTION_DEL);
		getCollectionService().deleteCollection(collectionId);

		// To capture activity log
		SessionContextSupport.putLogParameter(EVENT_NAME, SCOLLECTION_DEL);
		SessionContextSupport.putLogParameter(COLLECTION_ID, collectionId);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/item", method = RequestMethod.POST)
	public ModelAndView createCollectionItem(@RequestParam(value = RESOURCE_ID) String resourceId, @RequestParam(value = COLLECTION_ID) String collectionId, @RequestBody MultiValueMap<String, String> body, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		CollectionItem collectionItem = this.getCollectionService().buildCollectionItemFromInputParameters(body.getFirst(DATA_OBJECT), user);
		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().createCollectionItem(resourceId, collectionId, collectionItem, user, CollectionType.COLLECTION.getCollectionType(), false);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, ERROR_INCLUDE);
		return toModelAndView(serialize(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/item/{id}", method = RequestMethod.PUT)
	public ModelAndView updateCollectionItem(@PathVariable(value = ID) String collectionItemId, @RequestBody MultiValueMap<String, String> body, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		CollectionItem newCollectionItem = this.getCollectionService().buildCollectionItemFromInputParameters(body.getFirst(DATA_OBJECT), user);
		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().updateCollectionItem(newCollectionItem, collectionItemId, user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			// To capture activity log
			SessionContextSupport.putLogParameter(EVENT_NAME, SCOLLECTION_ITEM_UPDATE);
			SessionContextSupport.putLogParameter(COLLECTION_ITEM_ID, newCollectionItem.getCollectionItemId());
		}
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_CREATE_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, ERROR_INCLUDE);
		this.getRedisService().deleteKey(COLLECTION_DATA_LIB + responseDTO.getModel().getCollection().getGooruOid());
		return toModelAndView(serialize(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_READ })
	@RequestMapping(value = "/item/{id}", method = RequestMethod.GET)
	public ModelAndView getCollectionItem(@PathVariable(value = ID) String collectionItemId, @RequestParam(value = INCLUDE_ADDITIONAL_INFO, required = false, defaultValue = FALSE) boolean includeAdditionalInfo, HttpServletRequest request, HttpServletResponse response) {
		User user = (User) request.getAttribute(Constants.USER);
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_META_INFO);
		return toModelAndViewWithIoFilter(getCollectionService().getCollectionItem(collectionItemId, includeAdditionalInfo, user), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_LIST })
	@RequestMapping(value = "/{id}/item", method = RequestMethod.GET)
	public ModelAndView getCollectionItems(@PathVariable(value = ID) String collectionId, @RequestParam(value = PAGE_SIZE, required = false, defaultValue = TEN) Integer pageSize, @RequestParam(value = PAGE_NUM, required = false, defaultValue = ONE) Integer pageNo, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, String> filters = new HashMap<String, String>();
		filters.put(PAGE_SIZE, pageSize + "");
		filters.put(PAGE_NUM, pageNo + "");
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_META_INFO);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_WORKSPACE);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_CREATE_ITEM_INCLUDE_FILEDS);
		return toModelAndView(serialize(getCollectionService().setCollectionItemMetaInfo(getCollectionService().getCollectionItems(collectionId, filters)), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes));

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/item/{id}", method = RequestMethod.DELETE)
	public void deleteCollectionItem(@PathVariable(value = ID) String collectionItemId, HttpServletRequest request, HttpServletResponse response) {
		// To capture activity log
		SessionContextSupport.putLogParameter(EVENT_NAME, SCOLLECTION_ITEM_DEL);
		SessionContextSupport.putLogParameter(COLLECTION_ITEM_ID, collectionItemId);
		getCollectionService().deleteCollectionItem(collectionItemId);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/item/{id}/reorder", method = RequestMethod.PUT)
	public ModelAndView reorderCollectionItemSequence(@PathVariable(value = ID) String collectionItemId, @RequestParam(value = NEW_SEQUENCE) int newSequence, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().reorderCollectionItem(collectionItemId, newSequence);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			// To capture activity log
			SessionContextSupport.putLogParameter(EVENT_NAME, SCOLLECTION_ITEM_REORDER);
			SessionContextSupport.putLogParameter(COLLECTION_ITEM_ID, responseDTO.getModel().getCollectionItemId());
		}
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_CREATE_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, ERROR_INCLUDE);
		return toModelAndView(serialize(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}/copy", method = RequestMethod.PUT)
	public ModelAndView copyCollection(@PathVariable(value = ID) String collectionId, @RequestParam(value = TITLE, required = false) String title, @RequestParam(value = TAXONOMY_CODE, required = false) String taxonomyCode, @RequestParam(value = GRADE, required = false) String grade,
			@RequestParam(value = SKIP_COLLECTION_ITEM, required = false, defaultValue = FALSE) boolean skipCollectionItem, @RequestParam(value = ADD_TO_SHELF, required = false, defaultValue = FALSE) boolean addToShelf, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		if (!skipCollectionItem) {
			includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_INCLUDE_FILEDS);
		}
		User user = (User) request.getAttribute(Constants.USER);
		Collection collection = getCollectionService().copyCollection(collectionId, title, addToShelf, user, taxonomyCode, grade);
		// To capture activity log
		SessionContextSupport.putLogParameter(EVENT_NAME, SCOLLECTION_COPY);
		SessionContextSupport.putLogParameter(COLLECTION_ID, collectionId);
		SessionContextSupport.putLogParameter(COPY_COLLECTION_ID, collection.getGooruOid());
		SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());
		return toModelAndViewWithIoFilter(collection, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView listCollections(@RequestParam(value = PAGE_NUM, required = false, defaultValue = ONE) Integer pageNum, @RequestParam(value = PAGE_SIZE, required = false, defaultValue = TEN) Integer pageSize,
			@RequestParam(value = S_COLLECTION, required = false, defaultValue = SIMPLE) String sCollection, @RequestParam(value = FETCH_TYPE, required = false, defaultValue = ALL) String fetchType, HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> filters = new HashMap<String, String>();
		User user = (User) request.getAttribute(Constants.USER);
		filters.put(PAGE_NUM, pageNum + "");
		filters.put(PAGE_SIZE, pageSize + "");
		filters.put(Constants.FETCH_TYPE, fetchType);

		if (sCollection.equalsIgnoreCase(DEEP)) {
			return toModelAndViewWithInFilter(getCollectionService().getCollections(filters, user), RESPONSE_FORMAT_JSON, "*.collectionItems");
		}
		return toModelAndViewWithIoFilter(getCollectionService().getCollections(filters, user), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, COLLECTION_INCLUDE_FIELDS);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_READ })
	@RequestMapping(value = RESOURCE_MOREINFO, method = RequestMethod.GET)
	public ModelAndView getResourceMoreInfo(@RequestParam(value = RESOURCE_ID) String resourceId, HttpServletRequest request, HttpServletResponse response) {
		return toModelAndViewWithIoFilter(this.getCollectionService().getResourceMoreInfo(resourceId), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, COLLECTION_INCLUDE_FIELDS);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/addCollaborators/{id}", method = { RequestMethod.PUT })
	public ModelAndView addCollborators(@PathVariable(value = ID) String collectionId, HttpServletRequest request, HttpServletResponse response, @RequestParam(value = COLLABORATOR, required = true) String collaboratorId) {
		User user = (User) request.getAttribute(Constants.USER);

		// To capture activity log
		SessionContextSupport.putLogParameter(EVENT_NAME, ADD_COLLABORATORS);
		SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());
		SessionContextSupport.putLogParameter(COLLECTION_ID, collectionId);
		return toModelAndViewWithIoFilter(this.collectionService.addCollaborator(collectionId, user, collaboratorId, ADD), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, COLLABORATORI_INCLUDE);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@RequestMapping(value = "/getCollaborators/{id}", method = RequestMethod.GET)
	public ModelAndView getCollaborators(@PathVariable(value = ID) String collectionId, HttpServletRequest request, HttpServletResponse response) {

		// To capture activity log
		SessionContextSupport.putLogParameter(EVENT_NAME, GET_COLLABORATORS);
		SessionContextSupport.putLogParameter(COLLECTION_ID, collectionId);
		return toModelAndViewWithIoFilter(this.getCollectionService().getCollaborators(collectionId), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, COLLABORATORI_INCLUDE);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_DELETE })
	@RequestMapping(value = "/deleteCollaborators/{id}", method = RequestMethod.DELETE)
	public ModelAndView deleteCollaborators(@PathVariable(value = ID) String collectionId, HttpServletRequest request, HttpServletResponse response, @RequestParam(value = COLLABORATOR, required = true) String collaboratorId) {
		User user = (User) request.getAttribute(Constants.USER);

		// To capture activity log
		SessionContextSupport.putLogParameter(EVENT_NAME, DEL_COLLABORATORS);
		SessionContextSupport.putLogParameter(COLLECTION_ID, collectionId);
		SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());

		return toModelAndViewWithIoFilter(this.collectionService.addCollaborator(collectionId, user, collaboratorId, DELETE), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, COLLABORATORI_INCLUDE);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}/metadata", method = { RequestMethod.PUT })
	public ModelAndView updateCollectionMetadata(@PathVariable(value = ID) String collectionId, @RequestParam(value = CREATOR_UID, required = false) String creatorUId, @RequestParam(value = OWNER_UID, required = false) String ownerUId, @RequestBody MultiValueMap<String, String> body,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		Collection collection = getCollectionService().updateCollectionMetadata(collectionId, creatorUId, ownerUId, hasUnrestrictedContentAccess(), body);
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_META_INFO);
		includes = (String[]) ArrayUtils.addAll(includes, ERROR_INCLUDE);

		// To capture activity log
		SessionContextSupport.putLogParameter(EVENT_NAME, SCOLLECTION_METADATA_UPDATE);
		SessionContextSupport.putLogParameter(GOORU_OID, collection.getGooruOid());
		return toModelAndView(serialize(collection, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/item/{id}/metadata", method = RequestMethod.PUT)
	public ModelAndView updateCollectionItemMetadata(@PathVariable(value = ID) String collectionItemId, @RequestBody MultiValueMap<String, String> body, HttpServletRequest request, HttpServletResponse response) throws Exception {

		CollectionItem collectionItem = getCollectionService().updateCollectionItemMetadata(collectionItemId, body);

		// To capture activity log
		SessionContextSupport.putLogParameter(EVENT_NAME, SCOLLECTION_ITEM_METADATA_UPDATE);
		SessionContextSupport.putLogParameter(COLLECTION_ITEM_ID, collectionItem.getCollectionItemId());

		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_INCLUDE_FILEDS);
		return toModelAndView(serialize(collectionItem, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ITEM_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/item/{id}/copy", method = RequestMethod.PUT)
	public ModelAndView copyCollectionItem(@PathVariable(value = ID) String collectionItemId, @RequestParam(value = COLLECTION_ID, required = false) String collectionId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);
		CollectionItem collectionItem = getCollectionService().copyCollectionItem(collectionItemId, collectionId);
		if (collectionItem != null) {
			// To capture activity log
			SessionContextSupport.putLogParameter(EVENT_NAME, SCOLLECTION_ITEM_COPY);
			SessionContextSupport.putLogParameter(COLLECTION_ITEM_ID, collectionItem.getCollectionItemId());
			SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());
		}
		return toModelAndViewWithIoFilter(collectionItem, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_ITEM_INCLUDE_FILEDS));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@RequestMapping(value = "/{id}/workspace", method = RequestMethod.GET)
	public ModelAndView getMyWorkspace(@PathVariable(value = ID) String partyUid, HttpServletRequest request, @RequestParam(value = PAGE_NUM, required = false, defaultValue = ONE) Integer pageNum, @RequestParam(value = PAGE_SIZE, required = false, defaultValue = TEN) Integer pageSize,
			@RequestParam(value = FILTER_NAME, required = false, defaultValue = ALL) String filterName, @RequestParam(value = ORDER_BY, required = false, defaultValue = DESC) String orderBy, @RequestParam(value = SHARING, required = false) String sharing,
			@RequestParam(value = SKIP_PAGINATION, required = false, defaultValue = "false") boolean skipPagination, HttpServletResponse resHttpServletResponse) {
		User user = (User) request.getAttribute(Constants.USER);
		Map<String, String> filters = new HashMap<String, String>();
		filters.put(PAGE_NUM, pageNum + "");
		filters.put(PAGE_SIZE, pageSize + "");
		filters.put(SKIP_PAGINATION, skipPagination ? YES : NO);
		filters.put(Constants.FETCH_TYPE, CollectionType.SHElf.getCollectionType());
		filters.put(FILTER_NAME, filterName);
		if (sharing != null) {
			filters.put(SHARING, sharing);
		}
		filters.put(ORDER_BY, orderBy);
		List<CollectionItem> collectionItems = getCollectionService().setCollectionItemMetaInfo(getCollectionService().getMyCollectionItems(partyUid, filters, user));
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_META_INFO);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_WORKSPACE);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_ITEM_INCLUDE_FILEDS);
		return toModelAndView(serialize(collectionItems, RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}/resource", method = RequestMethod.POST)
	public ModelAndView createResourceWithCollectionItem(HttpServletRequest request, @PathVariable(ID) String collectionId, @RequestParam(value = COLLECTION_ITEM_ID, required = false) String collectionItemId, @RequestParam(value = TITLE, required = false) String title,
			@RequestParam(value = DESCRIPTION, required = false) String description, @RequestParam(value = URL, required = false) String url, @RequestParam(value = START, required = false) String start, @RequestParam(value = STOP, required = false) String stop,
			@RequestParam(value = THUMBNAIL_IMG_SRC, required = false) String thumbnailImgSrc, @RequestParam(value = RESOURCE_TYPE, required = false) String resourceType, @RequestParam(value = CATEGORY, required = false) String category, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);

		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().createResourceWithCollectionItem(collectionId, title, description, url, start, stop, thumbnailImgSrc, resourceType, category, user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			// To capture activity log
			SessionContextSupport.putLogParameter(EVENT_NAME, RESOURCE_CREATE_COLLECTION_ITEM);
			SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());
			SessionContextSupport.putLogParameter(COLLECTION_ID, collectionId);
			SessionContextSupport.putLogParameter(RESOURCE_ID, responseDTO.getModel().getResource().getGooruOid());
		}
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_CREATE_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, ERROR_INCLUDE);
		return toModelAndView(serialize(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{id}/question", method = RequestMethod.POST)
	public ModelAndView createQuestionWithCollectionItem(HttpServletRequest request, @RequestParam(value = MEDIA_FILE_NAME, required = false) String mediaFileName, @PathVariable(ID) String collectionId, @RequestParam(value = DATA_OBJECT) String data, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);

		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().createQuestionWithCollectionItem(collectionId, data, user, mediaFileName);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			// To capture activity log
			SessionContextSupport.putLogParameter(EVENT_NAME, QUES_CREATE_COLLECTION_ITEM);
			SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());
			SessionContextSupport.putLogParameter(COLLECTION_ID, collectionId);
			SessionContextSupport.putLogParameter(RESOURCE_ID, responseDTO.getModel().getResource().getGooruOid());
		}
		String[] includes = (String[]) ArrayUtils.addAll(COLLECTION_CREATE_ITEM_INCLUDE_FILEDS, ERROR_INCLUDE);
		includes = (String[]) ArrayUtils.addAll(includes, RESOURCE_INCLUDE_FIELDS);
		return toModelAndView(serializeToJson(responseDTO.getModelData(), includes));

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/move", method = RequestMethod.PUT)
	public ModelAndView moveCollectionToFolder(HttpServletRequest request, @RequestParam(value = SOURCE_ITEM_ID) String sourceItemId, @RequestParam(value = SOURCE_ID) String sourceId, @RequestParam(value = TARGET_ID) String targetId, HttpServletResponse response) throws Exception {
		User user = (User) request.getAttribute(Constants.USER);

		ActionResponseDTO<CollectionItem> responseDTO = getCollectionService().moveCollectionToFolder(sourceItemId, sourceId, targetId, user);
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			// To capture activity log
			SessionContextSupport.putLogParameter(EVENT_NAME, MOVE_COLLECTION_FOLDER);
			SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());
			SessionContextSupport.putLogParameter(SOURCE_ID, sourceId);
			SessionContextSupport.putLogParameter(TARGET_ID, targetId);
		}
		String includes[] = (String[]) ArrayUtils.addAll(RESOURCE_INCLUDE_FIELDS, COLLECTION_INCLUDE_FIELDS);
		includes = (String[]) ArrayUtils.addAll(includes, COLLECTION_CREATE_ITEM_INCLUDE_FILEDS);
		includes = (String[]) ArrayUtils.addAll(includes, ERROR_INCLUDE);
		return toModelAndView(serialize(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes));

	}

	private Collection buildCollectionFromInputParameters(String jsonData, User user) {
		XStream xstream = new XStream(new JettisonMappedXmlDriver());
		xstream.alias(COLLECTION, Collection.class);
		Collection collection = (Collection) xstream.fromXML(jsonData);
		collection.setGooruOid(UUID.randomUUID().toString());
		ContentType contentType = getCollectionService().getContentType(ContentType.RESOURCE);
		collection.setContentType(contentType);
		ResourceType resourceType = getCollectionService().getResourceType(ResourceType.Type.SCOLLECTION.getType());
		if (collection.getCollectionType() != null && collection.getCollectionType().equalsIgnoreCase(ResourceType.Type.FOLDER.getType())) {
			resourceType = getCollectionService().getResourceType(ResourceType.Type.FOLDER.getType());
		}
		collection.setResourceType(resourceType);
		collection.setLastModified(new Date(System.currentTimeMillis()));
		collection.setCreatedOn(new Date(System.currentTimeMillis()));
		collection
				.setSharing(collection.getSharing() != null && (collection.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || collection.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()) || collection.getSharing().equalsIgnoreCase(Sharing.ANYONEWITHLINK.getSharing())) ? collection
						.getSharing() : Sharing.PUBLIC.getSharing());
		collection.setUser(user);
		collection.setOrganization(user.getPrimaryOrganization());
		collection.setCreator(user);
		collection.setDistinguish(Short.valueOf(ZERO));
		collection.setRecordSource(NOT_ADDED);
		collection.setIsFeatured(0);
		collection.setLastUpdatedUserUid(user.getGooruUId());
		return collection;
	}

	public BaseRepository getBaseRepository() {
		return baseRepository;
	}

	public CollectionService getCollectionService() {
		return collectionService;
	}

	public RedisService getRedisService() {
		return redisService;
	}

}
