package org.ednovo.gooru.controllers.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.application.util.SerializerUtil;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.Shelf;
import org.ednovo.gooru.core.api.model.ShelfItem;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.CollectionService;
import org.ednovo.gooru.domain.service.shelf.ShelfService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = { "/shelf", "" })
public class ShelfRestController extends BaseController implements ConstantProperties {

	Logger logger = LoggerFactory.getLogger(ShelfRestController.class);

	@Autowired
	private ShelfService shelfService;

	@Autowired
	private CollectionService collectionService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SHELF_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/shelf")
	public ModelAndView createShelf(@RequestParam(value = SHELF_NAME, required = false) String shelfName, @RequestParam(value = "format") String format, @RequestParam(value = "sessionToken") String sessionToken, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, SHELF_CREATE);
		User apiCaller = (User) request.getAttribute(Constants.USER);
		Shelf shelf = getShelfService().createShelf(shelfName, apiCaller, false, true);
		if (shelf != null) {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		// To capture activity log
		SessionContextSupport.putLogParameter("eventName", SHELF_CREATE);
		SessionContextSupport.putLogParameter(USER_ID, apiCaller.getUserId());
		SessionContextSupport.putLogParameter("gooruUId", apiCaller.getPartyUid());
		SessionContextSupport.putLogParameter("shelfId", shelf.getShelfId());

		return toModelAndView(serializeToJsonWithExcludes(shelf, CREATE_SHELF_EXCLUDES));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SHELF_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/shelf/resource/{gooruOId}")
	public ModelAndView addResourceToshelf(@PathVariable("gooruOId") String gooruOId, @RequestParam(value = "format") String format, @RequestParam(value = "shelfId", required = false) String shelfId, @RequestParam(value = "addType", required = true) String addType,
			@RequestParam(value = "sessionToken") String sessionToken, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, "shelf.add_resource");
		Errors errors = new BindException(Shelf.class, "error");
		User apiCaller = (User) request.getAttribute(Constants.USER);
		Shelf shelf = getShelfService().addResourceToSelf(shelfId, gooruOId, addType, errors, apiCaller);
		if (shelf != null) {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		// To capture activity log
		SessionContextSupport.putLogParameter("eventName", "shelf-add-resource");
		SessionContextSupport.putLogParameter(USER_ID, apiCaller.getUserId());
		SessionContextSupport.putLogParameter("gooruUId", apiCaller.getPartyUid());
		SessionContextSupport.putLogParameter(GOORU_OID, gooruOId);

		return toModelAndView(serializeToJsonWithExcludes(shelf, ADD_RESOURCE_TO_SHELF_EXCLUDES));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SHELF_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/shelf/{shelfId}")
	public ModelAndView getShelfResources(@PathVariable("shelfId") String shelfId, @RequestParam(value = "resourceType", required = false, defaultValue = "all") String resourceType, @RequestParam(value = "format") String format, @RequestParam(value = "orderBy", required = false) String orderBy,
			@RequestParam(value = "sessionToken") String sessionToken, @RequestParam(value = "pageNum", required = false, defaultValue = "1") String pageNum, @RequestParam(value = "pageSize", required = false, defaultValue = "10") String pageSize,
			@RequestParam(value = "addedType", required = false) String addedType, @RequestParam(value = "startAt", required = false) String startAt, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, "shelf.get_self_resource");
		User apiCaller = (User) request.getAttribute(Constants.USER);
		List<ShelfItem> shelfList = getShelfService().getShelfItems(shelfId, resourceType, apiCaller.getGooruUId(), orderBy, pageNum, pageSize, addedType, false, startAt);
		String jsonResponse = serialize(shelfList, format, GET_SHELF_RESOURCES_EXCLUDES);
		List<ShelfItem> shelfLists = getShelfService().getShelfItems(shelfId, resourceType, apiCaller.getGooruUId(), orderBy, pageNum, pageSize, addedType, true, startAt);
		int totalHit = shelfLists != null ? shelfLists.size() : 0;
		jsonResponse = "{\"shelfItems\" :" + jsonResponse + ",\"totalHitCount\": \"" + totalHit + "\", \"pageNo\": \"" + pageNum + "\", \"pageSize\": \"" + pageSize + "\"}";

		// To capture activity log..
		SessionContextSupport.putLogParameter("eventName", "shelf-get-resources");
		SessionContextSupport.putLogParameter(USER_ID, apiCaller.getUserId());
		SessionContextSupport.putLogParameter("gooruUId", apiCaller.getPartyUid());
		SessionContextSupport.putLogParameter("shelfId", shelfId);

		return toModelAndView(jsonResponse);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SHELF_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/shelf/my")
	public ModelAndView getUserSelf(@RequestParam(value = "shelfToLoad", required = false) String shelfToLoad, @RequestParam(value = "loadShelfItems", required = false, defaultValue = "false") Boolean loadShelfItems, @RequestParam(value = "format") String format,
			@RequestParam(value = "sessionToken") String sessionToken, @RequestParam(value = "fetchAll", required = false, defaultValue = "false") boolean fetchAll, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, "shelf.read");
		response.addHeader("access-control-allow-origin", "*");
		User apiCaller = (User) request.getAttribute(Constants.USER);
		List<Shelf> userShelfList = getShelfService().getUserSelf(shelfToLoad, apiCaller, fetchAll);
		JSONObject jsonObj = new JSONObject();
		if (userShelfList != null && userShelfList.size() > 0) {
			Shelf defaultShelf = userShelfList.get(0);
			JSONArray userShelfJson = new JSONArray(serializeToJsonWithExcludes(userShelfList, USER_SHELF_EXCLUDES));
			jsonObj.put("userShelfList", userShelfJson);
			if (loadShelfItems) {
				JSONObject userShelfItems = serializeToJsonObjectWithExcludes(defaultShelf, USER_SHELF_EXCLUDES);
				jsonObj.put("defaultShelf", userShelfItems);
			}
		} else {
			jsonObj.put("userShelfList", "No shelf exists ");
		}
		// list
		// To capture activity log..
		SessionContextSupport.putLogParameter("eventName", "get-users-shelf");
		SessionContextSupport.putLogParameter(USER_ID, apiCaller.getUserId());
		SessionContextSupport.putLogParameter("gooruUId", apiCaller.getPartyUid());

		return toModelAndView(jsonObj.toString());
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SHELF_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/shelf/{shelfId}")
	public ModelAndView updateSelf(@PathVariable("shelfId") String shelfId, @RequestParam(value = "format") String format, @RequestParam(value = "activeFlag", required = false) Boolean activeFlag, @RequestParam(value = "sessionToken") String sessionToken, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, "shelf.update");
		Errors errors = new BindException(Shelf.class, "error");
		Shelf shelf = getShelfService().updateShelf(shelfId, activeFlag, errors);
		// To capture activity log
		User apiCaller = (User) request.getAttribute(Constants.USER);
		SessionContextSupport.putLogParameter("eventName", "shelf-update");
		SessionContextSupport.putLogParameter("shelfId", shelfId);
		SessionContextSupport.putLogParameter(USER_ID, apiCaller.getUserId());
		SessionContextSupport.putLogParameter("gooruUId", apiCaller.getPartyUid());

		return toModelAndView(serializeToJsonWithExcludes(shelf, UPDATE_SHELF_EXCLUDES));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SHELF_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/shelf/{shelfId}/resource/{gooruOId}")
	public ModelAndView removeShelfEntry(@PathVariable("shelfId") String shelfId, @PathVariable("gooruOId") String gooruOId, @RequestParam(value = "format") String format, @RequestParam(value = "sessionToken") String sessionToken, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		request.setAttribute(PREDICATE, "shelfitem.delete");
		String isDeleted = getShelfService().deleteShelfEntry(shelfId, gooruOId);
		User apiCaller = (User) request.getAttribute(Constants.USER);
		// To capture activity log
		SessionContextSupport.putLogParameter("eventName", "shelf-resource-delete");
		SessionContextSupport.putLogParameter("shelfId", shelfId);
		SessionContextSupport.putLogParameter(GOORU_OID, gooruOId);
		SessionContextSupport.putLogParameter(USER_ID, apiCaller.getUserId());
		SessionContextSupport.putLogParameter("gooruUId", apiCaller.getPartyUid());

		return toModelAndView(isDeleted, format);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SHELF_DELETE })
	@RequestMapping(method = RequestMethod.PUT, value = "/shelf/{currentShelfId}/{targetShelfId}/resource/{resourceGooruOId}/move")
	public ModelAndView moveShelfContent(@PathVariable("currentShelfId") String currentShelfId, @PathVariable("targetShelfId") String targetShelfId, @PathVariable("resourceGooruOId") String resourceGooruOId, @RequestParam(value = "format") String format,
			@RequestParam(value = "sessionToken") String sessionToken, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, "shelfitem.move");
		User apiCaller = (User) request.getAttribute(Constants.USER);
		Shelf shelf = getShelfService().moveShelfContent(currentShelfId, targetShelfId, resourceGooruOId, apiCaller);
		// To capture activity log
		SessionContextSupport.putLogParameter("eventName", "shelf-move-content");
		SessionContextSupport.putLogParameter("shelfId", currentShelfId);
		SessionContextSupport.putLogParameter("targetShelfId", targetShelfId);
		SessionContextSupport.putLogParameter(GOORU_OID, resourceGooruOId);
		SessionContextSupport.putLogParameter(USER_ID, apiCaller.getUserId());
		SessionContextSupport.putLogParameter("gooruUId", apiCaller.getPartyUid());

		return toModelAndView(serializeToJsonWithExcludes(shelf, MOVE_SHELF_CONTENT_EXCLUDES));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SHELF_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/shelf/mark/default")
	public ModelAndView markShelfAsDefault(@RequestParam(value = SHELF_NAME) String shelfName, @RequestParam(value = "format") String format, @RequestParam(value = "sessionToken") String sessionToken, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, "shelf.mark.default");
		Errors errors = new BindException(Shelf.class, "error");
		User user = (User) request.getAttribute(Constants.USER);
		Shelf shelf = getShelfService().markDefaultShelf(shelfName, errors, user);
		User apiCaller = (User) request.getAttribute(Constants.USER);

		// To capture activity log..
		SessionContextSupport.putLogParameter("eventName", "shelf-mark-default");
		SessionContextSupport.putLogParameter("shelfId", shelf.getShelfId());
		SessionContextSupport.putLogParameter(SHELF_NAME, shelfName);
		SessionContextSupport.putLogParameter(USER_ID, apiCaller.getUserId());
		SessionContextSupport.putLogParameter("gooruUId", apiCaller.getPartyUid());

		return toModelAndView(serializeToJsonWithExcludes(shelf, MARK_SHELF_AS_DEFAULT_EXCLUDES));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SHELF_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/shelf/name/list")
	public ModelAndView getSelfNames(@RequestParam(value = "skipSuggestedShelfName", required = false, defaultValue = "false") Boolean skipSuggestedShelfName, @RequestParam(value = "format") String format, @RequestParam(value = "sessionToken") String sessionToken, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, "shelf.get.shelf.names");
		User apiCaller = (User) request.getAttribute(Constants.USER);
		List<String> suggest = null;
		JSONObject shelfNames = new JSONObject();
		if (!skipSuggestedShelfName) {
			suggest = this.getShelfService().getShelfNames("suggest", apiCaller);
			shelfNames.put("suggest", suggest);
		}
		List<String> my = this.getShelfService().getShelfNames("my", apiCaller);
		shelfNames.put("my", my);

		// To capture activity log..
		SessionContextSupport.putLogParameter("eventName", "shelf-name-list");
		SessionContextSupport.putLogParameter(USER_ID, apiCaller.getUserId());
		SessionContextSupport.putLogParameter("gooruUId", apiCaller.getPartyUid());

		return toModelAndView(shelfNames);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SHELF_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/shelf/{shelfId}/rename")
	public ModelAndView renameMyShelf(@PathVariable("shelfId") String shelfId, @RequestParam(value = "format") String format, @RequestParam(value = "newShelfName") String newShelfName, @RequestParam(value = "sessionToken") String sessionToken, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		request.setAttribute(PREDICATE, "shelf.rename");
		User apiCaller = (User) request.getAttribute(Constants.USER);
		Shelf shelf = getShelfService().renameMyShelf(shelfId, newShelfName, apiCaller.getPartyUid());
		// To capture activity log..
		SessionContextSupport.putLogParameter("eventName", "shelf-rename");
		SessionContextSupport.putLogParameter("shelfId", shelfId);
		SessionContextSupport.putLogParameter(USER_ID, apiCaller.getUserId());
		SessionContextSupport.putLogParameter("gooruUId", apiCaller.getPartyUid());

		return toModelAndView(serializeToJsonWithExcludes(shelf, RENAME_MY_SHELF_EXCLUDES));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SHELF_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/shelf/archive/first/visit")
	public ModelAndView archiveShelfFirstVisit(@RequestParam(value = "sessionToken") String sessionToken, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, "shelf.archive.first.visit");
		User apiCaller = (User) request.getAttribute(Constants.USER);
		getShelfService().archiveUserFirstVisit(apiCaller.getPartyUid());

		// To capture activity log..
		SessionContextSupport.putLogParameter("eventName", "shelf-archive-visit");
		SessionContextSupport.putLogParameter(USER_ID, apiCaller.getUserId());
		SessionContextSupport.putLogParameter("gooruUId", apiCaller.getPartyUid());
		return null;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SHELF_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/shelf/change/status")
	public ModelAndView updateShelfStatus(@PathVariable("shelfId") String shelfId, @RequestParam(value = "format") String format, @RequestParam(value = "activeFlag") boolean activeFlag, @RequestParam(value = "sessionToken") String sessionToken, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, "shelf.change.status");
		Shelf shelf = getShelfService().updateShelfStatus(shelfId, activeFlag);
		User apiCaller = (User) request.getAttribute(Constants.USER);

		// To capture activity log.
		SessionContextSupport.putLogParameter("eventName", "shelf-update-status");
		SessionContextSupport.putLogParameter("shelfId", shelfId);
		SessionContextSupport.putLogParameter(USER_ID, apiCaller.getUserId());
		SessionContextSupport.putLogParameter("gooruUId", apiCaller.getPartyUid());

		return toModelAndView(serializeToJsonWithExcludes(shelf, UPDATE_SHELF_STATUS_EXCLUDES));
	}

	/*
	 * 
	 * /gooruapi/rest/myshelf?sessionToken=be4dec3b-7290-11e2-a9e3-e8039a2ea9ff&
	 * format=json
	 */

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SHELF_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/myshelf")
	public ModelAndView getMyShelf(@RequestParam(value = "format") String format, @RequestParam(value = "orderBy", required = false, defaultValue = "desc") String orderBy, @RequestParam(value = "sessionToken") String sessionToken,
			@RequestParam(value = PAGE_NUM, required = false, defaultValue = "1") Integer pageNum, @RequestParam(value = PAGE_SIZE, required = false, defaultValue = "10") Integer pageSize, @RequestParam(value = SKIP_PAGINATION, required = false, defaultValue = "false") boolean skipPagination,
			@RequestParam(value = FILTER_BY, required = false, defaultValue = "all") String filterBy, @RequestParam(value = SHARING, required = false) String sharingType, HttpServletRequest request, HttpServletResponse response) throws Exception {
		User apiCaller = (User) request.getAttribute(Constants.USER);
		Map<String, String> filters = new HashMap<String, String>();
		filters.put(PAGE_NUM, pageNum + "");
		filters.put(PAGE_SIZE, pageSize + "");
		filters.put(SKIP_PAGINATION, skipPagination ? YES : NO);
		filters.put(Constants.FETCH_TYPE, CollectionType.SHElf.getCollectionType());
		filters.put("orderBy", orderBy);
		filters.put(FILTER_NAME, filterBy);
		filters.put(SHARING, sharingType);
		List<Collection> collection = this.getCollectionService().getMyCollection(filters, apiCaller);
		return toModelAndView(SerializerUtil.serializeToJson(collection, "collectionItems"));
	}

	public void setShelfService(ShelfService shelfService) {
		this.shelfService = shelfService;
	}

	public ShelfService getShelfService() {
		return shelfService;
	}

	public CollectionService getCollectionService() {
		return collectionService;
	}
}
