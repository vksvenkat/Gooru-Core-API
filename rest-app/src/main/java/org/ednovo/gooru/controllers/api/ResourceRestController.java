package org.ednovo.gooru.controllers.api;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.application.converter.ImageScaler;
import org.ednovo.gooru.application.util.CollectionUtil;
import org.ednovo.gooru.application.util.GooruImageUtil;
import org.ednovo.gooru.application.util.ResourceImageUtil;
import org.ednovo.gooru.application.util.SerializerUtil;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceSource;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.application.util.RequestUtil;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.cassandra.service.DashboardCassandraService;
import org.ednovo.gooru.domain.cassandra.service.ResourceCassandraService;
import org.ednovo.gooru.domain.service.assessment.AssessmentService;
import org.ednovo.gooru.domain.service.job.JobService;
import org.ednovo.gooru.domain.service.partner.CustomFieldsService;
import org.ednovo.gooru.domain.service.resource.ResourceManager;
import org.ednovo.gooru.domain.service.resource.ResourceService;
import org.ednovo.gooru.domain.service.storage.S3ResourceApiHandler;
import org.ednovo.gooru.infrastructure.messenger.IndexHandler;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserContentRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import flexjson.JSONSerializer;

@Controller
@RequestMapping(value = { "/resource", "" })
public class ResourceRestController extends BaseController implements ParameterProperties, ConstantProperties {

	private static final Logger logger = LoggerFactory.getLogger(ResourceRestController.class);

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private JobService jobService;

	@Autowired
	@javax.annotation.Resource(name = "resourceManager")
	private ResourceManager resourceManager;

	@Autowired
	private CollectionUtil collectionUtil;

	@Autowired
	private ResourceImageUtil resourceImageUtil;

	@Autowired
	private IndexProcessor indexProcessor;

	@Autowired
	private AssessmentService assessmentService;

	@Autowired
	private S3ResourceApiHandler s3ResourceApiHandler;

	@Autowired
	private CustomFieldsService customFieldService;

	@Autowired
	private ResourceCassandraService resourceCassandraService;

	@Autowired
	private UserContentRepository userContentRepository;
	
	@Autowired
	private DashboardCassandraService dashboardCassandraService;

	@Autowired
	private IndexHandler indexHandler;


	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/resource/signed-url/{gooruResourceId}/get")
	public ModelAndView getSignedResourceUrl(HttpServletRequest request, HttpServletResponse response, @PathVariable(GOORU_RESOURCE_ID) String gooruResourceId, @RequestParam String file) throws Exception {

		String targetUrl = s3ResourceApiHandler.generateSignedResourceUrl(gooruResourceId, file, true);
		String encodedUrl = URLEncoder.encode(targetUrl, "UTF-8");
		logger.warn("Signed-URL: returning the URL: " + encodedUrl);
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		jsonmodel.addObject(MODEL, encodedUrl);

		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/resource/signed-url/{gooruResourceId}")
	public void downloadSignedResourceAsset(HttpServletRequest request, HttpServletResponse response, @PathVariable(GOORU_RESOURCE_ID) String gooruResourceId, @RequestParam String file) throws Exception {

		/*
		 * if(s3ResourceApiHandler.isResourceInGooruS3(gooruResourceId)) {
		 * response.setContentType("application/pdf");
		 * logger.warn("application/pdf Write file stream "); OutputStream os =
		 * response.getOutputStream(); byte[] byteData =
		 * s3ResourceApiHandler.downloadSignedResourceUrl(gooruResourceId,
		 * file); os.write(byteData); os.close(); } else {
		 */
		String targetUrl = response.encodeRedirectURL(s3ResourceApiHandler.generateSignedResourceUrl(gooruResourceId, file, true));
		logger.warn("Signed-URL: Redirecting to:" + targetUrl);
		response.sendRedirect(targetUrl);
		// }
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = { "/signed/resource/url/{gooruResourceId}" })
	public void getSignedResourceAsset(HttpServletRequest request, HttpServletResponse response, @PathVariable(GOORU_RESOURCE_ID) String gooruResourceId, @RequestParam String file, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken) throws Exception {
		String targetUrl = response.encodeRedirectURL(s3ResourceApiHandler.generateSignedResourceUrl(gooruResourceId, file));
		logger.warn("Signed-URL: Redirecting to:" + targetUrl);
		response.sendRedirect(targetUrl);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_VALIDATE })
	@RequestMapping(method = RequestMethod.GET, value = "/resource/url/validate")
	public void validateResourceUrls(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, RESOURCE_URL_VALIDATE);

		List<Resource> webResources = resourceService.findWebResourcesForBlacklisting();

		if (webResources != null) {
			logger.info("Processing validating of resource urls for " + webResources.size() + "urls");
		} else {
			logger.info("Processing validating of resource urls - nothing to do");
		}

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_ADD })
	@Transactional(readOnly = false, propagation = Propagation.NOT_SUPPORTED, noRollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "resource/add-new")
	public ModelAndView addNew(@RequestParam(value = URL) String url, @RequestParam(value = TITLE, required = false) String title, @RequestParam(value = TEXT, required = false) String text, @RequestParam(value = CATEGORY, required = false) String category,
			@RequestParam(value = SHARING, required = false) String sharing, @RequestParam(value = TYPENAME, required = false) String type_name, @RequestParam(value = LICENSE_NAME, required = false) String licenseName, @RequestParam(value = BROKEN_STATUS, required = false) Integer brokenStatus,
			@RequestParam(value = HAS_FRAME_BREAKER, required = false) Boolean hasFrameBreaker, @RequestParam(value = DESCRIPTION, required = false) String description, @RequestParam(value = IS_FEATURED, required = false) Integer isFeatured,
			@RequestParam(value = MEDIA_TYPE, required = false) String mediaType, @RequestParam(value = TAGS, required = false) String tags, @RequestParam(value = IS_RETURN_JSON, required = false) boolean isReturnJson, @RequestParam(value = "resourceFormat", required = false) String resourceFormat,
			@RequestParam(value = "resourceInstructional", required = false) String resourceInstructional, HttpServletRequest request, HttpServletResponse response) throws Exception {
		// request.setAttribute("predicate", "resource.add-new");

		User apiCaller = (User) request.getAttribute(Constants.USER);
		Resource resource = resourceService.addNewResource(url, title, text, category, sharing, type_name, licenseName, brokenStatus, hasFrameBreaker, description, isFeatured, tags, isReturnJson, apiCaller, mediaType, resourceFormat, resourceInstructional);

		if (isReturnJson) {
			ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
			JSONSerializer serializer = new JSONSerializer();
			Object serializedData = serializer.include(RESOURCE_META_DATA).serialize(resource);
			jsonmodel.addObject(MODEL, serializedData.toString());
			return jsonmodel;

		} else {
			return toModelAndView(DONE);
		}
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_SUGGEST })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/resourceSource/suggest/attribution")
	public ModelAndView suggestAttribution(@RequestParam String keyword, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, ASSESSMENT_SUGGEST_ATTRIBUTION);
		List<ResourceSource> resourceSources = (List<ResourceSource>) resourceService.getSuggestAttribution(keyword);
		List<ResourceSource> resourceSourceList = new ArrayList<ResourceSource>();
		for (ResourceSource rsSource : resourceSources) {
			ResourceSource rSource = new ResourceSource();
			rSource.setAttribution(rsSource.getAttribution());
			rSource.setResourceSourceId(rsSource.getResourceSourceId());
			resourceSourceList.add(rSource);
		}

		return toModelAndView(resourceSourceList, FORMAT_JSON);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/resourceSource/attribution")
	public ModelAndView resourceSourceAttribution(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = PAGE_SIZE, required = false) Integer pageSize, @RequestParam(value = PAGE_NO, required = false) Integer pageNo,
			@RequestParam(value = ATTRIBUTION, required = false) String attribution, @RequestParam(value = DOMAIN_NAME, required = false) String domainName, @RequestParam(value = TYPE, required = false, defaultValue = NORMAL_DOMAIN) String type) throws Exception {
		request.setAttribute(PREDICATE, RESOURCE_SOURCE_ATTRIBUTION);

		Map<String, String> filters = new HashMap<String, String>();

		if (pageNo == null) {
			pageNo = 1;
		}
		if (pageSize == null) {
			pageSize = 50;
		}
		filters.put(PAGE_NO, pageNo + "");
		filters.put(PAGE_SIZE, pageSize + "");

		if (attribution != null) {
			filters.put(ATTRIBUTION, attribution);
		}

		if (domainName != null) {
			filters.put(DOMAIN_NAME, domainName);
		}

		filters.put(TYPE, type);

		Map<String, Object> resourceSourceList = resourceService.findAllResourcesSource(filters);

		List<ResourceSource> rsSource = (List<ResourceSource>) resourceSourceList.get(ALL_RESOURCE_SOURCE);
		String resultsJSON = "{\"searchResults\":";
		resultsJSON += serializeToJson(resourceSourceList.get(FILTERED_RESOURCE_SOURCE), RESOURCE_SOURCE_EXCLUDES);
		resultsJSON += ", \"totalHitCount\" : " + String.valueOf(rsSource.size()) + "}";

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		jsonmodel.addObject(MODEL, resultsJSON);

		return jsonmodel;

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/resourceSource/suggest/{gooruContentId}/attribution")
	public ModelAndView updateSuggestAttribution(@PathVariable(GOORU_CONTENT_ID) String gooruContentId, @RequestParam(value = ATTRIBUTION) String attribution, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, RES_SRC_UPDATE_SUGGEST_ATTRIBUTION);

		ResourceSource resourceSource = resourceService.updateSuggestAttribution(gooruContentId, attribution);
		return toModelAndView(resourceSource, FORMAT_JSON);
	}

	/**
	 * Delete a resource from gooru content database. This method ONLY changes
	 * the ownership Authorization: This operation is allowed only if the
	 * resource is owned by the user or the user is a content admin.
	 * 
	 * Response: 200 OK
	 * 
	 * @param gooruContentId
	 *            - gooru content id
	 * @param sessionToken
	 *            - authorization token
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/resource/{gooruContentId}")
	public void deleteResource(@PathVariable(GOORU_CONTENT_ID) String gooruContentId, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletRequest request, HttpServletResponse response, final ModelMap model) throws Exception {
		request.setAttribute(PREDICATE, RESOURCE_DELETE);
		/*
		 * Step 1 - Get the user object from request.
		 */
		User apiCaller = (User) request.getAttribute(Constants.USER);
		// Step 2 - Ensure that the user making the request is the owner of the
		// resource
		resourceService.deleteResource(gooruContentId, apiCaller);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/attribution/{gooruAttributionId}")
	public void deleteAttribution(@PathVariable(GOORU_ATTRIBUTION_ID) String gooruAttributionId, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletRequest request, HttpServletResponse response, final ModelMap model) throws Exception {
		request.setAttribute(PREDICATE, ATTRIBUTION_DEL);
		/*
		 * Step 1 - Get the user object from request.
		 */
		User apiCaller = (User) request.getAttribute(Constants.USER);

		// Step 2 - Ensure that the user making the request is the owner of the
		// resource
		Resource resource = (Resource) request.getAttribute(Constants.SEC_CONTENT);
		resourceService.deleteAttribution(resource, gooruAttributionId, apiCaller);

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.POST, RequestMethod.PUT }, value = "/resource/update/{resourceSourceId}.{format}")
	public ModelAndView updateResourceSourceAttribution(HttpServletRequest request, @PathVariable(_RESOURCE_SOURCE_ID) Integer resourceSourceId, @PathVariable(FORMAT) String format, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken,
			@RequestParam(value = DOMAIN_NAME, required = false) String domainName, @RequestParam(value = FRAME_BREAKER, required = false) Integer frameBreaker, @RequestParam(value = ATTRIBUTION, required = false) String attribution,
			@RequestParam(value = IS_BLACKLISTED, required = false) Boolean isBlacklisted, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, RES_UPDATE_RES);
		User user = (User) request.getAttribute(Constants.USER);
		resourceService.updateResourceSourceAttribution(resourceSourceId, domainName, attribution, frameBreaker, user, isBlacklisted);
		return toModelAndView("Resource Updated Sucessfully");
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = { "/resource/update/{resourceGooruOid}" })
	public ModelAndView updateResource(HttpServletRequest request, HttpServletResponse response, @PathVariable(RESOURCE_GOORU_OID) String resourceGooruOid, @RequestParam(value = TITLE, required = false) String title, @RequestParam(value = DESCRIPTION, required = false) String description,
			@RequestParam(value = MEDIA_TYPE, required = false) String mediaType, @RequestParam(value = MEDIA_FILE_NAME, required = false) String mediaFilename) throws Exception {

		resourceService.updateResource(resourceGooruOid, title, description, mediaFilename, mediaType);
		return toModelAndView("Resource Updated");
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_READ })
	@RequestMapping(method = RequestMethod.GET, value = { "/resource/resourceSource/{gooruContentId}.json", "/resource/{gooruContentId}/play.json" })
	public ModelAndView getResourceSource(HttpServletRequest request, @PathVariable String gooruContentId, HttpServletResponse response, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken,
			@RequestParam(value = INCLUDE_BROKEN_PDF, required = false, defaultValue = TRUE) Boolean includeBrokenPdf) throws Exception {
		request.setAttribute(PREDICATE, RESOURCE_SRC_GET);
		User apiCaller = (User) request.getAttribute(Constants.USER);
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		Resource resource = getResourceService().findResourceByContentGooruId(gooruContentId);
		resource.setCustomFieldValues(customFieldService.getCustomFieldsValuesOfResource(resource.getGooruOid()));
		try {
			resource.setViews(this.dashboardCassandraService.readAsLong(ALL_+resource.getGooruOid(), COUNT_VIEWS));
			resource.setViewCount(resource.getViews());
		} catch (Exception e) {
			logger.error("parser error : " + e);
		}

		JSONObject questionObject = null;
		if (!includeBrokenPdf && resource.getBrokenStatus() != 0 && resource.getBrokenStatus() != null) {
			jsonmodel.addObject(MODEL, BROKEN_PDF);
		}
		JSONObject resourceObject = serializeToJsonObject(resource, new String[] { TAG_SET, "taxonomySet", "*.depthOfKnowledges.selected", "depthOfKnowledges.value", "momentsOfLearning.value", "momentsOfLearning.selected", "educationalUse.value", "educationalUse.selected", "*.publisher",
				"*.aggregator", "*.host" });
		if (resource.getResourceType().getName().equals(ASSESSMENT_QUESTION)) {
			AssessmentQuestion question = assessmentService.getQuestion(gooruContentId);
			questionObject = serializeToJsonObject(question, new String[] { "hints", "taxonomySet", "assets", "answers", "tagSet" });
		}
		try {
			resourceObject.put(RESOURCE_VIEWS, this.resourceCassandraService.getLong(resource.getGooruOid(), "stas.viewsCount"));
		} catch (Exception e) {
			logger.error("parser error : " + e);
		}
		
		JSONObject taxonomyData = collectionUtil.getContentTaxonomyData(resource.getTaxonomySet(), resource.getGooruOid());
		resourceObject.put(RESOURCE_TAXONOMY_DATA, taxonomyData);
		resourceObject.put(QUIZ_QUESTION, questionObject);
		resourceObject.put(PUBLIC_META_DATA, (resource.getTitle() + " , " + resource.getTags()));
		jsonmodel.addObject(MODEL, resourceObject);
		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_UPDATE_VIEW })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/resource/update/views/{gooruContentId}.{format}")
	public void updateResourceViews(HttpServletRequest request, @PathVariable(GOORU_CONTENT_ID) String gooruContentId, @PathVariable(FORMAT) String format, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletResponse response) throws Exception {
		// Resource existingResource =
		// resourceService.findResourceByContentGooruId(gooruContentId);
		// long viewResource = existingResource.getViews() == null ? 0 :
		// existingResource.getViews() + 1;
		// existingResource.setViews(viewResource);
		// SessionContextSupport.putLogParameter(RATING_EVENT_NAME,CONTENT_VIEWS);
		// SessionContextSupport.putLogParameter(VIEWS, viewResource);
		// SessionContextSupport.putLogParameter(GOORU_OID, gooruContentId);
		// resourceService.saveOrUpdate(existingResource);
		// this.getResourceCassandraService().saveViews(gooruContentId);
		// redisService.updateCount(gooruContentId, Constants.REDIS_VIEWS);
		// indexerMessenger.sendMessageToIndex(IndexerMessenger.SEARCH_REINDEX_MSG,
		// existingResource.getGooruOid(), existingResource.getContentId(),
		// RESOURCE);
		// if (existingResource.getResourceType() != null &&
		// existingResource.getResourceType().getName().equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType()))
		// {
		// indexProcessor.index(existingResource.getGooruOid(),
		// IndexProcessor.INDEX, SCOLLECTION);
		// } else
		// indexProcessor.index(existingResource.getGooruOid(),
		// IndexProcessor.INDEX, RESOURCE);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/resource/{gooruContentId}.{format}")
	public void updateResource(HttpServletRequest request, @PathVariable(GOORU_CONTENT_ID) String gooruContentId, @PathVariable(FORMAT) String format, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken,
			@RequestParam(value = RESOURCE_TITLE, required = false) String resourceTitle, @RequestParam(value = DISTINGUISH, required = false) String distinguish, @RequestParam(value = IS_FEATURED, required = false) Integer isFeatured,
			@RequestParam(value = DESCRIPTION, required = false) String description, @RequestParam(value = HAS_FRAME_BREAKER, required = false) Boolean hasFrameBreaker, @RequestParam(value = TAGS, required = false) String tags, @RequestParam(value = SHARING, required = false) String sharing,
			@RequestParam(value = _RESOURCE_SOURCE_ID, required = false) Integer resourceSourceId, @RequestParam(value = MEDIA_TYPE, required = false) String mediaType, @RequestParam(value = IS_BLACKLISTED, required = false) Boolean isBlacklisted,
			@RequestParam(value = ATTRIBUTION, required = false) String attribution, @RequestParam(value = CATEGORY, required = false) String category, @RequestParam(value = MEDIA_FILE_NAME, required = false) String mediaFileName, @RequestParam(value = GRADE, required = false) String grade,
			@RequestParam(value = RESOURCE_FORMAT, required = false) String resource_format, @RequestParam(value = LICENSENAME, required = false) String licenseName, @RequestParam(value = URL, required = false) String url, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, RES_UPDATE_RES);
		Map<String, Object> formField = RequestUtil.getMultipartItems(request);
		User user = (User) request.getAttribute(Constants.USER);

		if (formField != null) {
			resourceTitle = (String) formField.get(RESOURCE_TITLE);
			description = (String) formField.get(DESCRIPTION);
			hasFrameBreaker = (Boolean) formField.get(HAS_FRAME_BREAKER);
			sharing = (String) formField.get(SHARING);
			resourceSourceId = (Integer) formField.get(_RESOURCE_SOURCE_ID);
			distinguish = (String) formField.get(DISTINGUISH);
			tags = (String) formField.get(TAGS);
			isFeatured = (Integer) formField.get(IS_FEATURED);
			mediaType = (String) formField.get(MEDIA_TYPE);
			attribution = (String) formField.get(ATTRIBUTION);
			category = (String) formField.get(CATEGORY);
			mediaFileName = (String) formField.get(MEDIA_FILE_NAME);
			isBlacklisted = (Boolean) formField.get(IS_BLACKLISTED);
			grade = (String) formField.get(GRADE);
			url = (String) formField.get(URL);
		}
		toModelAndView(
				resourceService.updateResourceByGooruContentId(gooruContentId, resourceTitle, distinguish, isFeatured, description, hasFrameBreaker, tags, sharing, resourceSourceId, user, mediaType, attribution, category, mediaFileName, isBlacklisted, grade, resource_format, licenseName, url),
				FORMAT_JSON);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/resourceSource/new.{format}")
	public ModelAndView createResourcesourceAttribution(HttpServletRequest request, @PathVariable(FORMAT) String format, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = DOMAIN_NAME, required = false) String domainName,
			@RequestParam(value = ATTRIBUTION, required = false) String attribution, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, RES_UPDATE_RES);
		Map<String, Object> formField = RequestUtil.getMultipartItems(request);
		if (formField != null) {
			domainName = (String) formField.get(DOMAIN_NAME);
			attribution = (String) formField.get(ATTRIBUTION);
		}

		ResourceSource resourceSource = resourceService.createResourcesourceAttribution(domainName, attribution);

		if (resourceSource != null) {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}

		return toModelAndView(resourceSource, FORMAT_JSON);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/resource/{gooruContentId}/media")
	public ModelAndView updateResourceImage(HttpServletRequest request, @PathVariable(GOORU_CONTENT_ID) String gooruContentId, @RequestParam(value = MEDIA_FILE_NAME) String fileName, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletResponse response)
			throws Exception {
		String filePath = this.getResourceService().updateResourceImage(gooruContentId, fileName);
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		jsonmodel.addObject(MODEL, filePath);
		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/resource/{gooruContentId}/thumbnail")
	public ModelAndView updateResourceThumbnail(HttpServletRequest request, @PathVariable(GOORU_CONTENT_ID) String gooruContentId, @RequestParam(value = UPLOAD_FILENAME) String fileName, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletResponse response)
			throws Exception {
		request.setAttribute(PREDICATE, RES_UPDATE_THUMBNAIL);
		Map<String, Object> formField = RequestUtil.getMultipartItems(request);

		boolean isHasSlash = StringUtils.contains(fileName, '\\');

		if (isHasSlash) {
			fileName = StringUtils.substringAfterLast(fileName, Character.toString('\\'));
		}

		Resource resource = resourceService.updateResourceThumbnail(gooruContentId, fileName, formField);

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		jsonmodel.addObject(MODEL, resource.getFolder() + "/" + fileName);
		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/resource/image")
	public ModelAndView getResourceImage(@RequestParam(value = IMG_SRC) String imageSrc, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletRequest request, HttpServletResponse response) throws Exception {

		request.setAttribute(PREDICATE, RES_GET_LEARNGUIDE_IMG);
		response.setContentType(IMAGE_PNG);
		URL url = new URL(imageSrc);
		URLConnection urlConnection = url.openConnection();
		InputStream inputStream = urlConnection.getInputStream();
		BufferedImage learnguideImage = ImageIO.read(inputStream);
		if (learnguideImage != null) {
			OutputStream os = response.getOutputStream();
			Integer originalImageHeight = learnguideImage.getHeight();
			Integer originalImageWidth = learnguideImage.getWidth();
			byte[] imageInByte;

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageScaler imageScaler = new ImageScaler();
			if (originalImageHeight >= 320 && originalImageWidth >= 470) {
				learnguideImage = imageScaler.scaleImage(learnguideImage, 470, 320);
				ImageIO.write(learnguideImage, PNG, baos);
				baos.flush();
				imageInByte = baos.toByteArray();
				baos.close();
			} else if (originalImageHeight >= 320 && originalImageWidth <= 470) {
				learnguideImage = imageScaler.scaleImage(learnguideImage, originalImageWidth, 320);
				ImageIO.write(learnguideImage, PNG, baos);
				baos.flush();
				imageInByte = baos.toByteArray();
				baos.close();
			} else if (originalImageHeight <= 320 && originalImageWidth >= 470) {
				learnguideImage = imageScaler.scaleImage(learnguideImage, 470, originalImageHeight);
				ImageIO.write(learnguideImage, PNG, baos);
				baos.flush();
				imageInByte = baos.toByteArray();
				baos.close();
			} else {
				ImageIO.write(learnguideImage, PNG, baos);
				baos.flush();
				imageInByte = baos.toByteArray();
				baos.close();
			}
			os.write(imageInByte);
			os.close();
		} else {
			response.setStatus(404);
		}
		return null;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/resource/{gooruContentId}/thumbnail/crop")
	public void cropResourceImage(@RequestParam(value = GOORU_CONTENT_ID) String gooruContentId, @RequestParam(value = XPOSITION) int xPosition, @RequestParam(value = YPOSITION) int yPosition, @RequestParam(value = WIDTH) int width, @RequestParam(value = HEIGHT) int height,
			HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute(PREDICATE, RES_CROP_LEARNGUIDE_IMG);
		Resource resource = resourceService.findResourceByContentGooruId(gooruContentId);

		File collectionDir = new File(resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder());

		String fileName = resource.getThumbnail();
		File file = new File(collectionDir.getPath() + "/" + fileName);

		if (fileName != null && file.exists()) {

			try {

				GooruImageUtil.cropImage(file.getPath(), xPosition, yPosition, width, height);
				resourceImageUtil.sendMsgToGenerateThumbnails(resource);
				// Remove the collection from cache
				collectionUtil.deleteCollectionFromCache(gooruContentId, COLLECTION);
			} catch (Exception exception) {
				logger.error("Cannot crop Image : " + exception.getMessage());
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}

		} else {
			response.setStatus(404);
		}

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_READ })
	@RequestMapping(method = RequestMethod.GET, value = "resource/image/trueSize")
	public ModelAndView getResourceImageTrueSize(@RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = IMG_SRC) String imageSrc, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, RES_GET_LEARNGUIDE_IMG_TRUE_SIZE);

		Integer originalImageHeight = null;
		Integer originalImageWidth = null;

		URL url = new URL(imageSrc);
		URLConnection yc = url.openConnection();
		InputStream inputStream = yc.getInputStream();
		BufferedImage learnguideImage = ImageIO.read(inputStream);

		if (learnguideImage != null) {
			originalImageHeight = learnguideImage.getHeight();
			originalImageWidth = learnguideImage.getWidth();
		} else {
			response.setStatus(404);
		}
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		JSONObject jsonObj = new JSONObject();
		jsonmodel.addObject(MODEL, jsonObj.put(ORG_IMG_HEIGHT, originalImageHeight).put(ORG_IMG_WIDTH, originalImageWidth));
		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/resource/{gooruContentId}/thumbnail")
	public ModelAndView deleteResourceThumbnail(@PathVariable(GOORU_CONTENT_ID) String gooruContentId, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletRequest request, HttpServletResponse response, final ModelMap model) throws Exception {
		request.setAttribute(PREDICATE, RESOURCE_DELETE);
		resourceService.deleteResourceImage(gooruContentId);
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		jsonmodel.addObject(MODEL, SUCCESS);
		return jsonmodel;
	}

	/**
	 * Delete a resource from gooru. * Response: 200 OK
	 * 
	 * @param gooruContentId
	 *            - gooru content id
	 * @param sessionToken
	 *            - authorization token
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/resource/admin/{gooruContentId}")
	public void deleteResourceFromGAT(@PathVariable String gooruContentId, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = IS_THIRD_PARTY_USER, defaultValue = FALSE) boolean isThirdPartyUser,
			@RequestParam(value = IS_MY_CONTENT, defaultValue = FALSE) boolean isMycontent, HttpServletRequest request, HttpServletResponse response, final ModelMap model) throws Exception {
		User apiCaller = (User) request.getAttribute(Constants.USER);
		request.setAttribute(PREDICATE, RESOURCE_DELETE);
		resourceService.deleteResource(gooruContentId, apiCaller);
	}

	/**
	 * Delete a resources from gooru. * Response: 200 OK
	 * 
	 * - gooru content id
	 * 
	 * @param sessionToken
	 *            - authorization token
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/resource/bulk/{gooruContentIds}")
	public void deleteResourceBulk(@PathVariable(GOORU_CONTENT_IDS) String gooruContentIds, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletRequest request, HttpServletResponse response, final ModelMap model) throws Exception {
		request.setAttribute(PREDICATE, RESOURCE_DELETE);
		resourceService.deleteBulkResource(gooruContentIds);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/resource/{resourceId}/count")
	public ModelAndView getResourcePageCount(@RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @PathVariable(RESOURCE_ID) String resourceId, @RequestParam(value = FORMAT, required = true) final String format, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		request.setAttribute(PREDICATE, RES_GET_RES_ANALYTIC_DATA);
		return toModelAndView(PAGE_CNT + (getResourceService().getResourcePageCount(resourceId)).getNumOfPages(), format);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/resourceSource/check")
	public ModelAndView shortendUrlResourceCheck(@RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = DOMAIN_NAME, required = true) String domainName, @RequestParam(value = FORMAT, required = true) final String format, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		String status = SerializerUtil.serializeToJson(resourceService.shortenedUrlResourceCheck(domainName));
		jsonmodel.addObject(MODEL, status);
		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_COLLECTION_RESOURCE_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/collection/resource/list")
	public ModelAndView listResourcesUsedInCollections(@RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = FORMAT, required = true) final String format, @RequestParam(value = PAGE_NO, required = false) Integer pageNo,
			@RequestParam(value = PAGE_SIZE, required = false) Integer pageSize, HttpServletRequest request, HttpServletResponse response) throws Exception {
		// ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		Map<String, String> filters = new HashMap<String, String>();
		if (pageNo == null) {
			pageNo = 1;
		}
		if (pageSize == null) {
			pageSize = 5;
		}
		filters.put(PAGE_NUM, pageNo + "");
		filters.put(PAGE_SIZE, pageSize + "");
		List<Resource> resources = resourceService.listResourcesUsedInCollections(filters);
		return toModelAndView(resources, FORMAT_JSON);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/resource/suggest/meta/info")
	public ModelAndView SuggestResourceMetaData(@RequestParam(value = URL) String url, @RequestParam(value = TITLE, required = false) String title, @RequestParam(value = FETCH_THUMBNAIL, required = false, defaultValue = FALSE) boolean fetchThumbnail, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return toModelAndView(serializeToJson(getResourceService().getSuggestedResourceMetaData(url, title, fetchThumbnail), true));
	}
	
	 @AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_READ })
	   @RequestMapping(method = RequestMethod.GET, value = "/resource/search.{format}")
	   public ModelAndView getResourceByQuery(HttpServletRequest request, @PathVariable(FORMAT) String format, HttpServletResponse response, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = URL) String url,
	           @RequestParam(value = CHK_SHORTENED_URL, defaultValue = FALSE) boolean checkShortenedUrl) throws Exception {
			return toModelAndViewWithIoFilter(this.getResourceService().checkResourceUrlExists(url,checkShortenedUrl), RESPONSE_FORMAT_JSON,EXCLUDE_ALL, true, RESOURCE_INSTANCE_INCLUDES);
	   }

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	public ResourceService getResourceService() {
		return resourceService;
	}

	public ResourceCassandraService getResourceCassandraService() {
		return resourceCassandraService;
	}

}
