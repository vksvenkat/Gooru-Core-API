package org.ednovo.gooru.controllers.api;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.ednovo.gooru.application.converter.FileProcessor;
import org.ednovo.gooru.application.converter.ImageScaler;
import org.ednovo.gooru.application.util.CollectionUtil;
import org.ednovo.gooru.application.util.GooruImageUtil;
import org.ednovo.gooru.application.util.ResourceImageUtil;
import org.ednovo.gooru.application.util.ResourceInstanceFormatter;
import org.ednovo.gooru.application.util.SerializerUtil;
import org.ednovo.gooru.application.util.UserContentRelationshipUtil;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.ConverterDTO;
import org.ednovo.gooru.core.api.model.CsvCrawler;
import org.ednovo.gooru.core.api.model.FileMeta;
import org.ednovo.gooru.core.api.model.Job;
import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.Rating;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceDTO;
import org.ednovo.gooru.core.api.model.ResourceInfo;
import org.ednovo.gooru.core.api.model.ResourceInstance;
import org.ednovo.gooru.core.api.model.ResourceSource;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.Segment;
import org.ednovo.gooru.core.api.model.SessionActivityType;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.ShelfItem;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserContentAssoc;
import org.ednovo.gooru.core.api.model.UserContentAssoc.RELATIONSHIP;
import org.ednovo.gooru.core.application.util.ImageUtil;
import org.ednovo.gooru.core.application.util.RequestUtil;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.cassandra.service.ResourceCassandraService;
import org.ednovo.gooru.domain.service.assessment.AssessmentService;
import org.ednovo.gooru.domain.service.classplan.LearnguideService;
import org.ednovo.gooru.domain.service.job.JobService;
import org.ednovo.gooru.domain.service.partner.CustomFieldsService;
import org.ednovo.gooru.domain.service.rating.RatingService;
import org.ednovo.gooru.domain.service.resource.ResourceManager;
import org.ednovo.gooru.domain.service.resource.ResourceService;
import org.ednovo.gooru.domain.service.sessionActivity.SessionActivityService;
import org.ednovo.gooru.domain.service.shelf.ShelfService;
import org.ednovo.gooru.domain.service.storage.S3ResourceApiHandler;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserContentRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.thoughtworks.xstream.XStream;

import flexjson.JSONSerializer;

@Controller
@RequestMapping(value = { "/resource", "" })
public class ResourceRestController extends BaseController implements ConstantProperties {

	private static final Logger logger = LoggerFactory.getLogger(ResourceRestController.class);

	@Autowired
	private LearnguideService learnguideService;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private JobService jobService;

	@Autowired
	@javax.annotation.Resource(name = "resourceManager")
	private ResourceManager resourceManager;

	@Autowired
	@javax.annotation.Resource(name = "classplanConstants")
	private Properties classPlanConstants;

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
	private SessionActivityService sessionActivityService;

	@Autowired
	private CustomFieldsService customFieldService;

	@Autowired
	private ShelfService shelfService;

	@Autowired
	private RatingService ratingService;

	@Autowired
	private ResourceCassandraService resourceCassandraService;

	@Autowired
	private UserContentRepository userContentRepository;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_LIST })
	@RequestMapping(method = RequestMethod.GET, value = "/resource/list.{format}")
	public ModelAndView getResourceList(HttpServletRequest request, @PathVariable String format, @RequestParam(value = PAGE_SIZE, required = false) Integer pageSize, @RequestParam(value = PAGE_NO, required = false) Integer pageNo,
			@RequestParam(value = ACCESS_TYPE, required = false, defaultValue = ALL) String accessType, @RequestParam(value = CONTENT_TYPE, required = false, defaultValue = ALL) String resourceType, @RequestParam(value = FEATURED, required = false) Integer featured,
			@RequestParam(value = TAXONOMY_PARENT_ID, required = false) String taxonomyParentId, @RequestParam(value = ORDER_BY, required = false, defaultValue = MINUS_ONE) String orderBy, @RequestParam(value = _IN_USE , required = false, defaultValue = MINUS_ONE) Integer inUse,
			@RequestParam(value = FETCH_TOTAL_HIT, required = false, defaultValue = FALSE) boolean fetchTotalHit, String keyword, @RequestParam(value = FETCH_FROM_SHELF, required = false, defaultValue = TRUE) boolean fetchFromShelf,
			@RequestParam(value = FETCH_FROM_MY_CONTENT, required = false, defaultValue = ZERO ) String fetchFromMyContent, @RequestParam(value = START_AT, required = false, defaultValue = ZERO) Integer startAt, String license, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, RESOURCE_LIST);
		User apiCaller = (User) request.getAttribute(Constants.USER);
		if (!accessType.equalsIgnoreCase(MY) && !hasUnrestrictedContentAccess()) {
			throw new AccessDeniedException("You Don't have permission to access");
		}
		Map<String, String> filters = new HashMap<String, String>();
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		if (pageNo == null) {
			pageNo = 1;
		}

		if (pageSize == null) {
			pageSize = 50;
		}
		if (fetchFromShelf) {
			if (accessType.equalsIgnoreCase(MY)) {
				String jsonResponse = this.getShelfService().getMyShelfResources(apiCaller.getGooruUId(), pageNo.toString(), pageSize.toString()).toString();
				if (fetchTotalHit) {
					List<ShelfItem> shelfLists = getShelfService().getShelfItems(null, RESOURCE, apiCaller.getGooruUId(), RECENT, pageNo.toString(), pageSize.toString(), null, true, null);
					int totalHit = shelfLists != null ? shelfLists.size() : 0;
					jsonResponse = "{\"resources\" :" + jsonResponse + ",\"totalHitCount\": \"" + totalHit + "\", \"pageNo\": \"" + pageNo + "\", \"pageSize\": \"" + pageSize + "\"}";
				}
				return jsonmodel.addObject(MODEL, jsonResponse);
			}
		}

		filters.put(START_AT, startAt + "");
		filters.put(PAGE_NUM, pageNo + "");
		filters.put(PAGE_SIZE, pageSize + "");
		filters.put(ACCESS_TYPE, accessType);
		filters.put(USER_ID, apiCaller.getPartyUid());
		filters.put(RESOURCE_TYPE, resourceType);
		filters.put(IN_USE, inUse + "");
		filters.put(FETCH_FROM_MY_CONTENT, fetchFromMyContent);

		if (license != null) {
			filters.put(LICENSE, license + "");
		}
		if (featured != null) {
			filters.put(FEATURED, featured + "");
		}

		filters.put(NOT_RESOURCE_TYPE, "assessment-exam,assessment-question,assessment-quiz,gooru/classbook,gooru/classplan,gooru/notebook,gooru/studyshelf");
		if (taxonomyParentId != null && taxonomyParentId.length() > 0) {
			filters.put(TAXONOMY_PARENT_ID, taxonomyParentId);
		}
		String includes = "";
		if (orderBy != null && orderBy.length() > 0) {
			filters.put(ORDER_BY, orderBy);
			includes = includes.concat(TAXONOMY_SET);
		}
		if (keyword != null) {
			filters.put(KEYWORD, keyword);
		}

		int relationshipID = (fetchFromMyContent.equals(ONE) ? 2 : 6);

		List<Resource> resources = getResourceService().listResources(filters);

		// Step 4 - Generate the appropriate object for serialization purposes.
		if (format.equalsIgnoreCase(FORMAT_JSON)) {
			JSONArray resourcesArray = new JSONArray();
			for (Resource resource : resources) {
				if (!resource.getResourceType().getName().equalsIgnoreCase(ResourceType.Type.CLASSPLAN.getType())) {
					if (!fetchFromShelf) {
						UserContentAssoc userContentAssoc = userContentRepository.getUserContentAssoc(apiCaller.getUserUid(), resource.getContentId(), relationshipID);
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						if (resource.getLastModified().compareTo(userContentAssoc.getLastActiveDate()) > 0) {
							resource.setLastModifiedString(simpleDateFormat.format(resource.getLastModified()));
						} else {
							resource.setLastModifiedString(simpleDateFormat.format(userContentAssoc.getLastActiveDate()));
						}
					}
					resource.setCustomFieldValues(customFieldService.getCustomFieldsValuesOfResource(resource.getGooruOid()));
					resourcesArray.put(serializeToJsonObjectWithExcludes(resource, RESOURCE_EXCLUDES, includes.split(",")));
				}
				jsonmodel.addObject(MODEL, resourcesArray);
			}
		} else if (format.equalsIgnoreCase(FORMAT_XML)) {
			XStream xstream = new XStream();
			xstream.alias(RESOURCE, Resource.class);
			String collectionXML = xstream.toXML(resources);
			jsonmodel.addObject(MODEL, collectionXML);
		}
		SessionContextSupport.putLogParameter(RATING_EVENT_NAME, _RESOURCE_LIST);
		SessionContextSupport.putLogParameter(USER_ID, apiCaller.getUserId());
		SessionContextSupport.putLogParameter(GOORU_UID, apiCaller.getPartyUid());
		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/{collection}/{gooruContentId}/segment/{segmentId}/resource/task/{taskId}.{format}")
	public ModelAndView getResourceTaskResource(HttpServletRequest request, @PathVariable(GOORU_CONTENT_ID) String gooruContentId, @PathVariable(SEGMENT_ID) String segmentId, @PathVariable(TASK_ID) String taskId, @PathVariable(COLLECTION) String collection,
			@PathVariable(FORMAT) String format, HttpServletResponse response, @RequestParam(value = SKP_SKELETON_SEG, defaultValue = ZERO, required = false) String skipSkeletonSegments, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken) throws Exception {
		request.setAttribute(PREDICATE, RESOURCE_GET_TASK);

		Job job = resourceService.getResourceTaskJob(taskId);
		// Job job = (Job) getClassplanRepository().get(Job.class,
		// Integer.parseInt(taskId));
		JSONObject model = null;
		if (job != null) {
			int retryAfterSeconds = 0;
			String status = Job.Status.COMPLETED.getStatus();
			if (job.getStatus().equals(Job.Status.COMPLETED.getStatus())) {
				ModelAndView view = this.getResources(request, gooruContentId, collection, format, segmentId, sessionToken, response);
				model = new JSONObject(view.getModelMap().get(MODEL).toString());
			} else if (job.getStatus().equals(Job.Status.FAILED.getStatus())) {
				model = new JSONObject();
				retryAfterSeconds = 0;
				status = Job.Status.FAILED.getStatus();
			} else {
				model = new JSONObject();
				retryAfterSeconds = 5;
				status = Job.Status.INPROGRESS.getStatus();
			}
			model.put(TASK_ID, job.getJobId());
			model.put(RETRY_AFTER_SECONDS, retryAfterSeconds);
			model.put(STATUS, status);

		} else {
			model = new JSONObject();
		}
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		jsonmodel.addObject(MODEL, model.toString());

		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_ADD })
	@RequestMapping(method = RequestMethod.POST, value = "/resource/crawled")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ModelAndView saveCrawledResource(HttpServletRequest request, @RequestParam String url, @RequestParam String title, @RequestParam(required = false) String text, @RequestParam(required = false) String parentUrl, @RequestParam(required = false) String thumbnail,
			@RequestParam(required = false) String attribution, @RequestParam(defaultValue = EXAM_PDF, required = false) String typeForPdf, @RequestParam(defaultValue = WEB_SITE) String category, @RequestParam(required = false) String siteName, @RequestParam(required = false) String tags,
			@RequestParam(required = false) String description) throws Exception {

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		resourceService.saveCrawledResource(url, title, text, parentUrl, thumbnail, attribution, typeForPdf, category, siteName, tags, description);
		return jsonmodel;
	}

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

	/**
	 * Retrieves a resource based on gooru content Id , segment Id and resource
	 * Id.
	 * 
	 * Sample Request : GET
	 * /rest/classplan/4f272470-d76c-4425-aa59-7ab1e8857ba9/
	 * segment/abc410b6-1732
	 * -4f24-82a6-23d582fb302a/resource/abc410b6-1732-4f24-82
	 * a6-23d582fb302b.json?sessionToken=fd07db5f-70bd-11e0-b5ac-cbbb4a3e5b0f
	 * 
	 * @param gooruContentId
	 *            - Gooru content id
	 * @param segmentId
	 *            - segment id of the segment
	 * @param resourceId
	 *            - resource id of the resource
	 * @param format
	 *            - either xml or json
	 * @param request
	 *            - HttpServletRequest object
	 * @param response
	 *            - HttpServletResponse object
	 * @param sessionToken
	 *            - session token that authenticates the request
	 * @return
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/{collection}/{gooruContentId}/segment/{segmentId}/resource/{resourceId}.{format}")
	public ModelAndView getResource(HttpServletRequest request, @PathVariable(GOORU_CONTENT_ID) String gooruContentId, @PathVariable(SEGMENT_ID) String segmentId, @PathVariable(RESOURCE_ID) String resourceId, @PathVariable(COLLECTION) String collection, @PathVariable(FORMAT) String format,
			HttpServletResponse response, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken) throws Exception {
		request.setAttribute(PREDICATE, RESOURCE_GET);

		ResourceInstance resourceInstance;

		resourceInstance = new ResourceInstance(new Segment(), getResourceService().findResourceByContentGooruId(resourceId));

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		String resourceXml = ResourceInstanceFormatter.getInstance().getResourceInstanceXml(resourceInstance);

		if (format.equals(FORMAT_XML)) {

			jsonmodel.addObject(MODEL, resourceXml);

		} else if (format.equals(FORMAT_JSON)) {

			JSONObject xmlJSONObj = XML.toJSONObject(resourceXml);
			jsonmodel.addObject(MODEL, xmlJSONObj.toString());
		}

		return jsonmodel;
	}

	/**
	 * Retrieves a resource based on gooru content Id , segment Id and resource
	 * Id.
	 * 
	 * Sample Request : GET
	 * /rest/classplan/4f272470-d76c-4425-aa59-7ab1e8857ba9/
	 * segment/abc410b6-1732
	 * -4f24-82a6-23d582fb302a/resource/abc410b6-1732-4f24-82
	 * a6-23d582fb302b.json?sessionToken=fd07db5f-70bd-11e0-b5ac-cbbb4a3e5b0f
	 * 
	 * @param gooruContentId
	 *            - Gooru content id
	 * @param segmentId
	 *            - segment id of the segment
	 * @param resourceId
	 *            - resource id of the resource
	 * @param format
	 *            - either xml or json
	 * @param request
	 *            - HttpServletRequest object
	 * @param response
	 *            - HttpServletResponse object
	 * @param sessionToken
	 *            - session token that authenticates the request
	 * @return
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/resource/search.{format}")
	public ModelAndView getResourceByQuery(HttpServletRequest request, @PathVariable(FORMAT) String format, HttpServletResponse response, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = URL) String url,
			@RequestParam(value = CHK_SHORTENED_URL, defaultValue = FALSE) boolean checkShortenedUrl) throws Exception {
		request.setAttribute(PREDICATE, RESOURCE_GET);
		boolean checkResourceNull = false;
		ResourceInstance resourceInstance = new ResourceInstance();
		url = URLDecoder.decode(url, "UTF-8");
		Resource resource = resourceService.findResourceByUrl(url, Sharing.PUBLIC.getSharing(), null);
		resourceInstance.setResource(resource);
		if (checkShortenedUrl) {
			resourceInstance.setShortenedUrlStatus(resourceService.shortenedUrlResourceCheck(url));
		}
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		String resourceXml = "";
		if (resourceInstance.getResource() != null) {
			resourceXml = ResourceInstanceFormatter.getInstance().getResourceInstanceXml(resourceInstance);
		} else {
			checkResourceNull = true;
			resourceInstance.getShortenedUrlStatus();

		}
		if (format.equals(FORMAT_XML)) {

			jsonmodel.addObject(MODEL, resourceXml);
		} else if (format.equals(FORMAT_JSON)) {
			if (checkResourceNull) {
				JSONObject xmlJSONObj = SerializerUtil.serializeToJsonObjectWithExcludes(resourceInstance, new String[] { "*" }, new String[] { "*.shortenedUrlStatus" });
				jsonmodel.addObject(MODEL, xmlJSONObj);
			} else {
				JSONObject xmlJSONObj = XML.toJSONObject(resourceXml);
				jsonmodel.addObject(MODEL, xmlJSONObj.toString());
			}
		}

		return jsonmodel;
	}

	/**
	 * Retrieves a resource based on gooru content Id , segment Id and resource
	 * Id.
	 * 
	 * Sample Request : GET
	 * /rest/classplan/4f272470-d76c-4425-aa59-7ab1e8857ba9/
	 * segment/abc410b6-1732
	 * -4f24-82a6-23d582fb302a/resource/abc410b6-1732-4f24-82
	 * a6-23d582fb302b.json?sessionToken=fd07db5f-70bd-11e0-b5ac-cbbb4a3e5b0f
	 * 
	 * @param gooruContentId
	 *            - Gooru content id
	 * @param segmentId
	 *            - segment id of the segment
	 * @param resourceInstanceId
	 *            - resource id of the resource
	 * @param format
	 *            - either xml or json
	 * @param request
	 *            - HttpServletRequest object
	 * @param response
	 *            - HttpServletResponse object
	 * @param sessionToken
	 *            - session token that authenticates the request
	 * @return
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/{collection}/{gooruContentId}/resourceInstance/{resourceInstanceId}.{format}")
	public ModelAndView getResourceInstance(HttpServletRequest request, @PathVariable(GOORU_CONTENT_ID) String gooruContentId, @PathVariable(RESOURCE_INSTANCE_ID) String resourceInstanceId, @PathVariable(COLLECTION) String collection, @PathVariable(FORMAT) String format,
			HttpServletResponse response, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken) throws Exception {
		request.setAttribute(PREDICATE, RESOURCE_INSTANCE_GET);
		User apiCaller = (User) request.getAttribute(Constants.USER);
		ResourceInstance resourceInstance = getResourceService().getResourceInstance(resourceInstanceId);

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		String resourceXml = ResourceInstanceFormatter.getInstance().getResourceInstanceXml(resourceInstance);

		ResourceSource resourceSource = resourceInstance.getResource().getResourceSource();

		if (format.equals(FORMAT_XML)) {

			jsonmodel.addObject(MODEL, resourceXml);

		} else if (format.equals(FORMAT_JSON)) {

			JSONObject xmlJSONObj = XML.toJSONObject(resourceXml);
			String sourceString = "";
			if (resourceSource != null) {
				sourceString = serializeToJson(resourceSource, new String[] { TAG_SET });
				if (sourceString != null && !sourceString.equals("")) {
					xmlJSONObj.put(SOURCE, new JSONObject(sourceString));
				}
			}
			JSONObject questionObject = null;
			if (resourceInstance.getResource().getResourceType().getName().equals(ASSESSMENT_QUESTION)) {
				AssessmentQuestion question = assessmentService.getQuestion(resourceInstance.getResource().getGooruOid());
				if (question != null) {
					questionObject = serializeToJsonObject(question, new String[] { "hints", "taxonomySet", "assets", "answers", "tagSet" });
				}
			}
			String resourceString = serializeToJson(resourceInstance.getResource().getLicense(), LICENSE);
			JSONObject license = new JSONObject(resourceString);
			String resourceCreatorId = resourceInstance.getResource().getCreator().getGooruUId();
			String resourceUserId = resourceInstance.getResource().getUser().getGooruUId();
			String siteName = resourceInstance.getResource().getSiteName();
			Integer resourceViews = resourceService.findViews(resourceInstance.getResource().getGooruOid());
			xmlJSONObj.put(RESOURCE_VIEWS, resourceViews);
			xmlJSONObj.put(RESOURCE_CREATOR_ID, resourceCreatorId);
			xmlJSONObj.put(RESOURCE_USER_ID, resourceUserId);
			xmlJSONObj.put(RESOURCE_SITE_NAME, siteName);
			JSONObject socialData = collectionUtil.getContentSocialData(apiCaller, resourceInstance.getResource().getGooruOid());
			xmlJSONObj.put(SOCIAL, socialData);
			JSONObject taxonomyData = collectionUtil.getContentTaxonomyData(resourceInstance.getResource().getTaxonomySet(), resourceInstance.getResource().getGooruOid());
			xmlJSONObj.put(RESOURCE_TAXONOMY_DATA, taxonomyData);
			xmlJSONObj.put(QUIZ_QUESTION, questionObject);
			xmlJSONObj.put(LICENSE, license);
			jsonmodel.addObject(MODEL, xmlJSONObj.toString());
		}

		return jsonmodel;
	}

	/**
	 * Retrieve a list of presentation resources within a classplan
	 * 
	 * @param gooruContentId
	 *            - gooru content id of the classplan
	 * @param format
	 *            - either xml or json
	 * @param sessionToken
	 *            - sesssion token for authorizing request
	 * @param request
	 *            - HttpServletRequest object
	 * @param response
	 *            - HttpServletResponse object
	 * @return
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/collection/{gooruContentId}/resources/{type}/list.{format}")
	public ModelAndView getPresentationResources(HttpServletRequest request, @PathVariable(GOORU_CONTENT_ID) String gooruContentId, @PathVariable(TYPE) String type, @PathVariable(FORMAT) String format, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletResponse response)
			throws Exception {
		request.setAttribute(PREDICATE, RES_GET_PRESENTATION_RESS);

		Learnguide collection = learnguideService.findByContent(gooruContentId);

		List<ResourceInstance> resourceInstances = getResourceService().listResourceInstances(gooruContentId, type);

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		String resourcesString = "";

		String resourceXmls = ResourceInstanceFormatter.getInstance().getResourceInstanceXmls(resourceInstances);

		if (format.equals(FORMAT_XML)) {
			resourcesString = resourceXmls;
		} else if (format.equals(FORMAT_JSON)) {
			JSONObject xmlJSONObj = XML.toJSONObject(resourceXmls);
			resourcesString = xmlJSONObj.toString();
		}
		jsonmodel.addObject(MODEL, "{\"resources\":" + resourcesString + ",\"assetURI\":\"" + collection.getAssetURI() + "\"}");
		return jsonmodel;
	}

	/**
	 * Retrieve all the resources of a segment based on gooru content Id and
	 * segment Id
	 * 
	 * Sample Request : GET
	 * /rest/classplan/4f272470-d76c-4425-aa59-7ab1e8857ba9/
	 * segment/abc410b6-1732
	 * -4f24-82a6-23d582fb302a/resources.json?sessionToken=fd07db5f
	 * -70bd-11e0-b5ac-cbbb4a3e5b0f
	 * 
	 * @param gooruContentId
	 *            - content id representing the classplan
	 * @param segmentId
	 *            - segmentId representing the segment.
	 * @param format
	 *            - format requested - supports xml or json
	 * @param request
	 *            - HttpServletRequest object
	 * @param response
	 *            - HttpServletResponse object
	 * @param sessionToken
	 *            - session token that authenticates the request
	 * @return
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_LIST })
	@RequestMapping(method = RequestMethod.GET, value = "/{collection}/{gooruContentId}/segment/{segmentId}/resources.{format}")
	public ModelAndView getResources(HttpServletRequest request, @PathVariable(GOORU_CONTENT_ID) String gooruContentId, @PathVariable(COLLECTION) String collection, @PathVariable(FORMAT) String format, @PathVariable(SEGMENT_ID) String segmentId,
			@RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, RES_GET_RESS);
		/*
		 * Step 1 - Retrieve the classplan object from the database
		 */
		List<ResourceInstance> resourceInstances = this.getResourceService().listSegmentResourceInstances(segmentId);

		/*
		 * Step 3 - Retrieve the resources from the classplan xml
		 */

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		String resourcesString = "";

		String resourceXmls = ResourceInstanceFormatter.getInstance().getResourceInstanceXmls(resourceInstances);

		if (format.equals(FORMAT_XML)) {
			resourcesString = resourceXmls;
		} else if (format.equals(FORMAT_JSON)) {
			JSONObject xmlJSONObj = XML.toJSONObject(resourceXmls);
			resourcesString = xmlJSONObj.toString();
		}
		jsonmodel.addObject(MODEL, resourcesString);
		return jsonmodel;

	}

	/**
	 * Retrieve first resource within a classplan
	 * 
	 * Sample Request : GET
	 * /rest/classplan/4f272470-d76c-4425-aa59-7ab1e8857ba9/
	 * resource/0?sessionToken=fd07db5f-70bd-11e0-b5ac-cbbb4a3e5b0f
	 * 
	 * @param gooruContentId
	 *            - content id representing the classplan
	 * @param format
	 *            - format requested - supports xml or json
	 * @param request
	 *            - HttpServletRequest object
	 * @param response
	 *            - HttpServletResponse object
	 * @return
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/collection/{gooruContentId}/resource/0.{format}")
	public ModelAndView getFirstResource(HttpServletRequest request, @PathVariable(GOORU_CONTENT_ID) String gooruContentId, @PathVariable(FORMAT) String format, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, RES_GET_FIRST);
		/*
		 * Step 1 - Retrieve the classplan object from the database
		 */
		Learnguide collection = learnguideService.findByContent(gooruContentId);
		String resourceXml = ResourceInstanceFormatter.getInstance().getResourceInstanceXml(getResourceService().getFirstResourceInstanceOfResource(gooruContentId));

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		if (resourceXml == null) {

			jsonmodel.addObject(MODEL, "{\"resource\":null}");
			return jsonmodel;
		} else {

			if (format.equals(FORMAT_XML)) {

				jsonmodel.addObject(MODEL, resourceXml);

			} else if (format.equals(FORMAT_JSON)) {

				JSONObject xmlJSONObj = XML.toJSONObject(resourceXml);
				jsonmodel.addObject(MODEL, xmlJSONObj.put(ASSET_URI, collection.getAssetURI()).toString());
			}

			return jsonmodel;
		}
	}

	/**
	 * Retrieve the list of all the resources within a classplan
	 * 
	 * Sample Request : GET
	 * /rest/classplan/4f272470-d76c-4425-aa59-7ab1e8857ba9/
	 * resources?sessionToken=fd07db5f-70bd-11e0-b5ac-cbbb4a3e5b0f
	 * 
	 * @param gooruContentId
	 *            - content id representing the classplan
	 * @param format
	 *            - format requested - supports xml or json
	 * @param request
	 *            - HttpServletRequest object
	 * @param response
	 *            - HttpServletResponse object
	 * @return
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_LIST })
	@RequestMapping(method = RequestMethod.GET, value = "/collection/{gooruContentId}/resources.{format}")
	public ModelAndView getResources(HttpServletRequest request, @PathVariable(GOORU_CONTENT_ID) String gooruContentId, @PathVariable(FORMAT) String format, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, RES_GET_RESS);
		/*
		 * Step 1 - Retrieve the classplan object from the database
		 */
		Learnguide collection = learnguideService.findByContent(gooruContentId);
		List<ResourceInstance> resourceInstances = getResourceService().listResourceInstances(gooruContentId, null);

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		String resourcesString = "";

		String resourceXmls = ResourceInstanceFormatter.getInstance().getResourceInstanceXmls(resourceInstances);

		if (format.equals(FORMAT_XML)) {
			resourcesString = resourceXmls;
		} else if (format.equals(FORMAT_JSON)) {
			JSONObject xmlJSONObj = XML.toJSONObject(resourceXmls);
			resourcesString = xmlJSONObj.toString();
		}
		jsonmodel.addObject(MODEL, "{\"resources\":" + resourcesString + ",\"assetURI\":\"" + collection.getAssetURI() + "\"}");
		return jsonmodel;
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

	private ModelAndView getResourceModel(String format, ResourceInstance resourceInstance) throws Exception {
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		String resourceXml = ResourceInstanceFormatter.getInstance().getResourceInstanceXml(resourceInstance);

		if (format.equals(FORMAT_XML)) {
			jsonmodel.addObject(MODEL, resourceXml);
		} else if (format.equals(FORMAT_JSON)) {
			JSONObject xmlJSONObj = XML.toJSONObject(resourceXml);
			jsonmodel.addObject(MODEL, xmlJSONObj.toString());
		}
		return jsonmodel;
	}

	/**
	 * Add a presentation resource to a segment based on the gooru content id
	 * and segment id
	 * 
	 * Sample Request : POST
	 * /rest/classplan/4f272470-d76c-4425-aa59-7ab1e8857ba9
	 * /segment/abc410b6-1732
	 * -4f24-82a6-23d582fb302a/resource/presentation.json?sessionToken
	 * =07db5f-70bd-11e0-b5ac-cbbb4a3e5b0f
	 * 
	 * @param gooruContentId
	 *            - content id representing the classplan
	 * @param segmentId
	 *            - segment id representing the segment.
	 * @param propName
	 *            - title of the resource
	 * @param propDesc
	 *            - description of the resource
	 * @param startPpt
	 *            - the start slide no. of the ppt type resource
	 * @param stopPpt
	 *            - the stop slide no. of the ppt type resource
	 * @param instruction
	 *            - instructions related to a resource
	 * @param resuseFolder
	 *            - folder name of the resource being reused
	 * @param reused
	 *            - boolean specifying whether resource is being reused.
	 * @param propURL
	 *            - url of the resource
	 * @param format
	 *            - either xml or json
	 * @param sessionToken
	 *            - session token for resource authorization
	 * @param request
	 *            - HttpServletRequest object
	 * @param response
	 *            - HttpServletResponse object
	 * @return
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/collection/{gooruContentId}/segment/{segmentId}/presentation/convert.{format}")
	public ModelAndView createPdfResource(HttpServletRequest request, @PathVariable(GOORU_CONTENT_ID) String gooruContentId, @PathVariable(FORMAT) String format, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = PROP_NAME, required = false) String resourceName,
			@RequestParam(value = PROP_DESC, required = false) String description, @RequestParam(value = PROP_URL, required = false) String url, @RequestParam(value = INSTRUCTION, required = false) String instruction, @RequestParam(value =START_PPT, required = false) String startPpt,
			@RequestParam(value = STOP_PPT, required = false) String stopPpt, @RequestParam(value = FOLDER, required = false) String reuseFolder, @PathVariable(SEGMENT_ID) String segmentId, HttpServletResponse response,
			@RequestParam(value = SKIP_FORM , required = false, defaultValue = FALSE) boolean skipForm) throws Exception {
		request.setAttribute(PREDICATE, RES_CREATE_PDF_RES);
		Map<String, Object> formField = RequestUtil.getMultipartItems(request);
		// Remove the collection from cache
		collectionUtil.deleteCollectionFromCache(gooruContentId, COLLECTION);

		if (formField != null && !skipForm) {
			resourceName = (String) formField.get(PROP_NAME);
			description = (String) formField.get(PROP_DESC);
			instruction = (String) formField.get(INSTRUCTION);
			url = (String) formField.get( PROP_URL);
			startPpt = (String) formField.get(START_PPT);
			stopPpt = (String) formField.get(STOP_PPT);
			reuseFolder = (String) formField.get(FOLDER);
		}

		User user = (User) request.getAttribute(Constants.USER);

		Learnguide collection = learnguideService.findByContent(gooruContentId);

		ResourceType resourceType = new ResourceType();
		resourceType.setName(ResourceType.Type.PRESENTATION.getType());

		ResourceDTO resource = new ResourceDTO();
		resource.setDescription(description);
		resource.setLabel(resourceName);
		resource.setNativeURL(url);
		resource.setFolder(reuseFolder);
		resource.setType(ResourceType.Type.PRESENTATION.getType());
		resource.setStart(startPpt);
		resource.setStop(stopPpt);

		FileMeta fileMeta = FileProcessor.extractFileData(request, null, collection.getOrganization().getNfsStorageArea().getInternalPath());
		if (fileMeta == null) {
			throw new UnsupportedOperationException("A file was expected but not uploaded");
		}

		String absoluteFilePath = collection.getOrganization().getNfsStorageArea().getInternalPath() + collection.getFolder();
		File sourceFile = FileProcessor.writeFile(absoluteFilePath, fileMeta.getOriginalFilename(), fileMeta.getFileData());

		final ConverterDTO converterDTO = new ConverterDTO();
		converterDTO.setResource(resource);
		converterDTO.setGooruContentId(gooruContentId);
		converterDTO.setSegmentId(segmentId);
		converterDTO.setLearnGuideString(COLLECTION);
		return createJob(sourceFile, converterDTO, user);

	}

	/**
	 * Add a handout resource to a segment based on the gooru content id and
	 * segment id
	 * 
	 * Sample Request : POST
	 * /rest/classplan/4f272470-d76c-4425-aa59-7ab1e8857ba9
	 * /segment/abc410b6-1732
	 * -4f24-82a6-23d582fb302a/resource/handouts.json?sessionToken
	 * =07db5f-70bd-11e0-b5ac-cbbb4a3e5b0f
	 * 
	 * @param gooruContentId
	 *            - content id representing the classplan
	 * @param segmentId
	 *            - segment id representing the segment.
	 * @param propName
	 *            - title of the resource
	 * @param propDesc
	 *            - description of the resource
	 * @param startPpt
	 *            - the start slide no. of the ppt type resource
	 * @param stopPpt
	 *            - the stop slide no. of the ppt type resource
	 * @param resuseFolder
	 *            - folder name of the resource being reused
	 * @param reused
	 *            - boolean specifying whether resource is being reused.
	 * @param propURL
	 *            - url of the resource
	 * @param format
	 *            - either xml or json
	 * @param sessionToken
	 *            - session token for resource authorization
	 * @param request
	 *            - HttpServletRequest object
	 * @param response
	 *            - HttpServletResponse object
	 * @return
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/collection/{gooruContentId}/segment/{segmentId}/presentation/split.{format}")
	public ModelAndView createSplittedSlidesResource(HttpServletRequest request, @PathVariable(GOORU_CONTENT_ID) String gooruContentId, @PathVariable(FORMAT) String format, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @PathVariable(SEGMENT_ID) String segmentId,
			HttpServletResponse response) throws Exception {
		collectionUtil.deleteCollectionFromCache(gooruContentId, COLLECTION);
		return sendToResourceConverter(request, gooruContentId, segmentId, PNG);
	}

	private ModelAndView sendToResourceConverter(HttpServletRequest request, String gooruContentId, String segmentId, String fileType) throws Exception {
		request.setAttribute(PREDICATE, RES_SPLIT_RES);

		User user = (User) request.getAttribute(Constants.USER);

		Learnguide collection = learnguideService.findByContent(gooruContentId);

		FileMeta fileMeta = FileProcessor.extractFileData(request, null, collection.getOrganization().getNfsStorageArea().getInternalPath());
		if (fileMeta == null) {
			throw new UnsupportedOperationException("A file was expected but not uploaded");
		}

		String absoluteFilePath = collection.getOrganization().getNfsStorageArea().getInternalPath() + collection.getFolder();
		File sourceFile = FileProcessor.writeFile(absoluteFilePath, fileMeta.getOriginalFilename(), fileMeta.getFileData());

		Map<String, Object> formField = RequestUtil.getMultipartItems(request);
		// Remove the collection from cache
		collectionUtil.deleteCollectionFromCache(gooruContentId, COLLECTION);
		String resourceName = request.getParameter(TITLE);
		if (resourceName == null && formField != null) {
			resourceName = (String) formField.get(PROP_NAME);
		}
		if (resourceName == null) {
			resourceName = fileMeta.getOriginalFilename();
		}

		final ConverterDTO converterDTO = new ConverterDTO();
		final ResourceDTO resourceDTO = new ResourceDTO();
		resourceDTO.setLabel(resourceName);

		converterDTO.setResource(resourceDTO);
		converterDTO.setGooruContentId(gooruContentId);
		converterDTO.setSegmentId(segmentId);
		converterDTO.setFileType(fileType);
		converterDTO.setLearnGuideString(COLLECTION);
		return createJob(sourceFile, converterDTO, user);

	}

	public ModelAndView createJob(File sourceFile, final ConverterDTO converterDTO, User user) throws Exception {
		long retryAfterSecs = 0;
		converterDTO.setSuccess(sourceFile != null);
		if (sourceFile == null) {
			logger.info("Save Resource [Job]: Cannot create resource file");
		}
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		JSONObject model = new JSONObject();
		if (converterDTO.isSuccess()) {
			converterDTO.setStartTime(new Date().getTime());

			Job job = resourceService.saveJob(sourceFile, converterDTO, user);

			/*
			 * Job job = new Job();
			 * job.setGooruOid(converterDTO.getGooruContentId());
			 * job.setUser(user); job.setFileSize(sourceFile.length());
			 * converterDTO.setSourcePath(sourceFile.getPath()); String type =
			 * JobType.Type.PPTCONVERSION.getType(); if
			 * (FileProcessor.getFileExt(sourceFile.getName()).equals("pdf")) {
			 * type = JobType.Type.PDFCONVERSION.getType(); }
			 * job.setJobType((JobType)
			 * getClassplanRepository().get(JobType.class, type));
			 * job.setStatus(Job.Status.INPROGRESS.getStatus());
			 * getClassplanRepository().save(job);
			 */
			converterDTO.setJobId(job.getJobId());

			retryAfterSecs = jobService.getAverageRetryTime(job.getFileSize());
			if (retryAfterSecs == 0) {
				retryAfterSecs = 5;
			}
			model.put(TASK_ID, job.getJobId());
			model.put(STATUS, Job.Status.INPROGRESS.getStatus());
		} else {
			model.put(TASK_ID, 0);
			model.put(STATUS, Job.Status.FAILED.getStatus());
		}
		model.put(RETRY_AFTER_SECONDS, retryAfterSecs);
		jsonmodel.addObject(MODEL, model.toString());
		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/resource/info/{resourceGooruOid}.json")
	public ModelAndView getResourceInfo(HttpServletRequest request, HttpServletResponse response, @PathVariable(RESOURCE_GOORU_OID) String resourceGooruOid, @RequestParam(value = INCLUDES, required = false) String includes,
			@RequestParam(value = RESOURCE_INSTANCE_ID, required = false) String resourceInstanceId) throws Exception {
		request.setAttribute(PREDICATE, RESOURCE_GET_INFO);
		User apiCaller = (User) request.getAttribute(Constants.USER);

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		ResourceInfo resourceInfo = resourceService.findResourceInfo(resourceGooruOid);
		JSONObject resourceObjInfo = new JSONObject();
		if (resourceInfo != null) {
			resourceObjInfo = serializeToJsonObject(resourceInfo);
		}
		if (includes != null) {
			String include[] = includes.split(",");
			for (String includeData : include) {
				if (includeData.equalsIgnoreCase(COLLECTION_TITLE)) {
					List<Learnguide> resourceCollections = learnguideService.getCollectionsOfResource(resourceGooruOid);
					List<String> collectionTitles = new ArrayList<String>();
					if (resourceCollections != null) {
						for (Learnguide learnguide : resourceCollections) {
							collectionTitles.add(learnguide.getLesson());
						}
					}
					resourceObjInfo.put(COLLECTION_TITLE, collectionTitles);
				} else if (includeData.equalsIgnoreCase(COMMENTS)) {
				} else if (includeData.equalsIgnoreCase(SUBSCRIBED_STATUS)) {
					boolean isContentAlreadySubscribed = this.getShelfService().hasContentSubscribed(apiCaller, resourceGooruOid);
					resourceObjInfo.put(SUBSCRIBED_STATUS, isContentAlreadySubscribed);
				} else if (includeData.equalsIgnoreCase(LIKES)) {
					Rating rating = ratingService.findByContent(resourceGooruOid);
					resourceObjInfo.put(LIKES, rating.getVotesUp());
				} else if (includeData.equalsIgnoreCase(ABOUT)) {
					Resource resource = getResourceService().findResourceByContentGooruId(resourceGooruOid);
					if (resource != null) {
						JSONObject about = new JSONObject();
						resourceObjInfo.put(ABOUT, about.put(TYPE, resource.getCategory()).put(DESCRIPTION, resource.getDescription()));
					}
				} else if (includeData.equalsIgnoreCase(NARRATION)) {
					if (!StringUtils.isEmpty(resourceInstanceId)) {
						resourceObjInfo.put(NARRATION, resourceService.getResourceInstanceNarration(resourceInstanceId));
					}
				}
			}
		}
		jsonmodel.addObject(MODEL, resourceObjInfo);
		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_ADD })
	@Transactional(readOnly = false, propagation = Propagation.NOT_SUPPORTED, noRollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "resource/add-new")
	public ModelAndView addNew(@RequestParam(value = URL) String url, @RequestParam(value = TITLE, required = false) String title, @RequestParam(value = TEXT, required = false) String text, @RequestParam(value = CATEGORY, required = false) String category,
			@RequestParam(value = SHARING, required = false) String sharing, @RequestParam(value = TYPENAME, required = false) String type_name, @RequestParam(value = LICENSE_NAME, required = false) String licenseName,
			@RequestParam(value = BROKEN_STATUS, required = false) Integer brokenStatus, @RequestParam(value = HAS_FRAME_BREAKER, required = false) Boolean hasFrameBreaker, @RequestParam(value = DESCRIPTION, required = false) String description,
			@RequestParam(value = IS_FEATURED, required = false) Integer isFeatured, @RequestParam(value = MEDIA_TYPE, required = false) String mediaType, @RequestParam(value = TAGS, required = false) String tags, @RequestParam(value = IS_RETURN_JSON, required = false) boolean isReturnJson,@RequestParam(value = RESOURCE_FORMAT, required = false) String resource_format,@RequestParam(value = RESOURCE_INSTRUCTIONAL, required = false) String resource_instructional,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		// request.setAttribute("predicate", "resource.add-new");

		User apiCaller = (User) request.getAttribute(Constants.USER);
		Resource resource = resourceService.addNewResource(url, title, text, category, sharing, type_name, licenseName, brokenStatus, hasFrameBreaker, description, isFeatured, tags, isReturnJson, apiCaller, mediaType, resource_format, resource_instructional);

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

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_ADD })
	@Transactional(readOnly = false, propagation = Propagation.NOT_SUPPORTED, noRollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = RESOURCE_IMPORT_CSV)
	public ModelAndView importCSVResource(@RequestParam(value = URL) String url, @RequestParam(value = SUBJECT, required = false) String subject, @RequestParam(value = TITLE, required = false) String title, @RequestParam(value = TAGS, required = false) String tags,
			@RequestParam(value = DESCRIPTION, required = false) String description, @RequestParam(value = ATTRIBUTION, required = false) String attribution, @RequestParam(value = THUMBNAIL, required = false) String thumbnail,
			@RequestParam(value = CSV_FILE_CODE, required = false) String csvFileCode, @RequestParam(value = TYPE, required = false) String type, @RequestParam(value = GRADE, required = false) String grade, @RequestParam(value = CONTENT, required = false) String content,
			@RequestParam(value = IMPORT_MODE, required = false) String importMode, @RequestParam(value = IS_RETURN_JSON, required = false) boolean isReturnJson, @RequestParam(value = INDEX_FLAG, required = false, defaultValue = TRUE) boolean indexFlag, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		User apiCaller = (User) request.getAttribute(Constants.USER);
		CsvCrawler csvCrawler = null;
		String mediaType = IPAD_FRIENDLY;
		Map<String, String> customFieldAndValueMap = collectionUtil.getCustomFieldNameAndValueAsMap(request);
		if (customFieldAndValueMap.size() > 0) {
			if (customFieldAndValueMap.containsValue(FLV) || customFieldAndValueMap.containsValue(SWF) || customFieldAndValueMap.containsValue(JAVA)) {
				mediaType = NOT_IPAD_FRIENDLY;

			}
		}
		Resource resource = resourceService.importCSVResource(url, subject, title, tags, description, attribution, thumbnail, csvFileCode, type, grade, content, importMode, csvCrawler, isReturnJson, apiCaller, indexFlag, mediaType);

		if (customFieldAndValueMap.size() > 0) {
			customFieldService.saveCustomFieldInfo(resource.getGooruOid(), customFieldAndValueMap);
		}

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
		filters.put(PAGE_NUM, pageNo + "");
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

	/*
	 * @AuthorizeOperations(operations = {
	 * GooruOperationConstants.OPERATION_RESOURCE_READ })
	 * 
	 * @Transactional(readOnly = false, propagation = Propagation.NOT_SUPPORTED,
	 * rollbackFor = Exception.class)
	 * 
	 * @RequestMapping(method = RequestMethod.GET, value =
	 * "resource/enrich-all")
	 * 
	 * @ApiOperation(value = "Enrich All", responseClass =
	 * RESPONSE_CLASS_RESOURCE) public ModelAndView enrichAll(@ApiParam(value =
	 * TYPE, required = false) @RequestParam(value = TYPE, required = false)
	 * String resourceTypeString, HttpServletRequest request,
	 * HttpServletResponse response) throws Exception {
	 * request.setAttribute(PREDICATE, "resource.enrich-all");
	 * 
	 * // enrich all. //resourceService.enrichAll(resourceTypeString);
	 * 
	 * return toModelAndView("enriched all resource");
	 * 
	 * }
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/collection/{gooruContentId}/segment/{segmentId}/resource/{resourceInstanceId}/move.{format}")
	public ModelAndView reorderResourceInstances(HttpServletRequest request, @PathVariable(GOORU_CONTENT_ID) String gooruContentId, @PathVariable(SEGMENT_ID) String segmentId, @PathVariable(RESOURCE_INSTANCE_ID) String resourceInstanceId, @PathVariable(FORMAT) String format,
			@RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = NEW_SEG_ID) String newSegmentId, @RequestParam(value = NEW_RESOURCE_POS) String newResourceInstancePos, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, RESOURCE_REORDER);

		Learnguide collection = learnguideService.findByContent(gooruContentId);
		this.getSessionActivityService().updateSessionActivityByContent(collection.getGooruOid(), SessionActivityType.Status.ARCHIVE.getStatus());
		getResourceService().reorderResourceInstace(collection, segmentId, resourceInstanceId, newResourceInstancePos, newSegmentId);
		indexProcessor.index(collection.getGooruOid(), IndexProcessor.INDEX, COLLECTION);

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		String collectionXml = learnguideService.findByContent(gooruContentId).retrieveXml();

		// Remove the collection from cache
		collectionUtil.deleteCollectionFromCache(gooruContentId, COLLECTION);

		if (format.equals(FORMAT_XML)) {
			jsonmodel.addObject(MODEL, collectionXml);
		} else if (format.equals(FORMAT_JSON)) {

			JSONObject xmlJSONObj = XML.toJSONObject(collectionXml);
			jsonmodel.addObject(MODEL, xmlJSONObj.toString());
		}

		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = { "/resource/updateThumbnail" })
	public ModelAndView updateThumbnails(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = NUM_OF_IMGS, defaultValue = STATUS_500) String numberOfImages, @RequestParam(value = DOWNLOAD_IMGS, defaultValue = ONE) String downloadImages,
			@RequestParam(value = RESOURCE_URL, required = false) String resourceUrl, @RequestParam(value = RESOURCE_TYPE, defaultValue = RES_URL) String resourceType, @RequestParam(value = LOG_LEVEL, defaultValue = INFO) String logLevel, final Model model) throws Exception {
		// request.setAttribute("predicate", "learning_guide.update_classplan");
		Integer numberOfImagesToDownload = Integer.parseInt(numberOfImages);
		int processedCount = 0, downloadedCount = 0;
		Map<String, String> filters = new HashMap<String, String>();
		filters.put(THUMBNAIL, _NULL);
		filters.put(IN_USE, ONE);
		if (!resourceType.equalsIgnoreCase(ALL)) {
			filters.put(RESOURCE_TYPE, resourceType);
		}

		filters.put(PAGE_SIZE, numberOfImages);
		logger.info("Thumbnail downloader:Starting run:");
		List<Resource> resourceList = null;
		if (resourceUrl != null && resourceUrl.startsWith("http://")) {
			resourceList = new ArrayList<Resource>();
			Resource resource = getResourceService().findWebResource(resourceUrl);
			if (resource != null) {
				resourceList.add(resource);
			}
		} else {
			resourceList = getResourceService().listResources(filters);
		}

		if (resourceList != null) {
			logger.info("Thumbnail downloader:retrieved " + resourceList.size());
		} else {
			logger.info("Thumbnail downloader:resourceList is null");
		}

		for (Resource resource : resourceList) {
			if (processedCount >= numberOfImagesToDownload) {
				break;
			}
			if (resource.getThumbnail() == null) {
				try {
					File collectionDir = new File(resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder());

					if (!collectionDir.exists()) {
						if (logLevel.equalsIgnoreCase(DEBUG)) {
							logger.info(THUMBNAIL_DOWNLOADER + resource.getGooruOid() + ":creating folder " + resource.getFolder());
						}
						collectionDir.mkdir();
					}
					String fileName = null;
					File thumbnailFolder = new File(collectionDir.getAbsolutePath() + "/slides/");
					if (thumbnailFolder.exists()) {
						FileFilter fileFilter = new WildcardFileFilter("thumbnail.~*~");
						File[] files = collectionDir.listFiles(fileFilter);
						if (files != null && files.length > 0) {
							fileName = "slides/" + files[0].getName();
							if (logLevel.equalsIgnoreCase(DEBUG)) {
								logger.info(THUMBNAIL_DOWNLOADER + resource.getGooruOid() + ":had existing files at " + resource.getFolder());
							}
						} else {
							fileFilter = new WildcardFileFilter("thumbnail1.~*~");
							files = collectionDir.listFiles(fileFilter);
							if (files != null && files.length > 0) {
								fileName = "slides/" + files[0].getName();
								if (logLevel.equalsIgnoreCase(DEBUG)) {
									logger.info(THUMBNAIL_DOWNLOADER + resource.getGooruOid() + ":had existing files at " + resource.getFolder());
								}
							}
						}
					}

					if ((fileName == null || fileName.isEmpty()) && resource.getTitle() != null && downloadImages.equalsIgnoreCase(ONE)) {
						String lesson = resource.getTitle();
						String prefix = "Interactive:";
						if (resource.getResourceType().getName().equalsIgnoreCase(ANIMATION_KMZ) && lesson.startsWith("Interactive:")) {
							lesson = lesson.substring(prefix.length());
						}
						String imageURLInfo = ImageUtil.getThumbnailUrlByQuery(lesson, null, null);

						if (imageURLInfo != null && !imageURLInfo.isEmpty()) {
							String parts[] = imageURLInfo.split("\\|");

							if (parts.length < 3) {
								logger.info(THUMBNAIL_DOWNLOADER + resource.getGooruOid() + ":partial results for " + lesson);
								continue;
							}

							String extension = "." + parts[0];
							String imageThumbnailURL = parts[2];
							fileName = resource.getGooruOid() + extension;

							String resourceImageFolder = resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder();
							logger.info("Thumbnail downloader:Resource " + resource.getGooruOid() + " didn't have image. downloading from " + resourceImageFolder + fileName);
							GooruImageUtil.downloadWebResourceToFile(imageThumbnailURL, resourceImageFolder, fileName, parts[0]);
							resourceImageUtil.sendMsgToGenerateThumbnails(resource, fileName);
							downloadedCount++;
						} else {
							logger.info(THUMBNAIL_DOWNLOADER + resource.getGooruOid() + ":no image found for " + lesson);
						}
					}
					if (fileName != null) {
						processedCount++;
						if (logLevel.equalsIgnoreCase(DEBUG)) {
							logger.info(THUMBNAIL_DOWNLOADER + resource.getGooruOid() + ":updating thumbnail " + resource.getFolder());
						}
						// A thumbnail was either found, or downloaded.
						resource.setThumbnail(fileName);

						Errors errors = new BindException(Resource.class, RESOURCE);
						this.getResourceService().saveResource(resource, errors, false);

						// add to index.
						if (errors.hasErrors()) {
							logger.error("Thumbnail downloader:Error saving resource" + errors.toString());
							continue;
						}
						indexProcessor.index(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE);
					}

				} catch (Exception e) {
					logger.warn("Thumbnail downloader:Resource " + resource.getGooruOid() + " had a problem" + ExceptionUtils.getFullStackTrace(e));
					Thread.sleep(1000);
				}
			}
		}
		logger.info("Thumbnail downloader:Finished processing:processed=" + processedCount + ":downloaded=" + downloadedCount);

		return new ModelAndView(REST_MODEL);
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
		Resource resource = (Resource) request.getAttribute(Constants.SEC_CONTENT);
		resourceService.deleteResource(resource, gooruContentId, apiCaller);

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
		request.setAttribute(PREDICATE, RES_UPDATE_RES );

		Map<String, Object> formField = RequestUtil.getMultipartItems(request);
		if (formField != null) {
			domainName = (String) formField.get(DOMAIN_NAME);
			attribution = (String) formField.get(ATTRIBUTION);
			frameBreaker = (Integer) formField.get(FRAME_BREAKER);
			isBlacklisted = (Boolean) formField.get(IS_BLACKLISTED);
		}
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

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = { "/collections/resourceInstance/order" })
	public ModelAndView orderCollectionResourceInstances(HttpServletRequest request, HttpServletResponse response) throws Exception {

		getResourceService().orderCollectionResourceInstances();

		return toModelAndView("collection resources ordered");
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = { "/resource/updateResourceSource" })
	public ModelAndView updateResourceSource(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = RESOURCE_TYPE_STR, defaultValue = ALL) String resourceTypeString) throws Exception {

		getResourceService().updateResourceSource(resourceTypeString);

		return toModelAndView("resource source updated!");
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
		JSONObject questionObject = null;
		if (!includeBrokenPdf && resource.getBrokenStatus() != 0 && resource.getBrokenStatus() != null) {
			jsonmodel.addObject(MODEL, BROKEN_PDF);
		}
		JSONObject resourceObject = serializeToJsonObject(resource, new String[] { TAG_SET });
		if (resource.getResourceType().getName().equals(ASSESSMENT_QUESTION)) {
			AssessmentQuestion question = assessmentService.getQuestion(gooruContentId);
			questionObject = serializeToJsonObject(question, new String[] { "hints", "taxonomySet", "assets", "answers", "tagSet" });
		}
		Integer resourceViews = resourceService.findViews(resource.getGooruOid());
		resourceObject.put(RESOURCE_VIEWS, resourceViews);
		JSONObject socialData = collectionUtil.getContentSocialData(apiCaller, gooruContentId);
		resourceObject.put(SOCIAL, socialData);
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
		Resource existingResource = resourceService.findResourceByContentGooruId(gooruContentId);
		long viewResource = existingResource.getViews() == null ? 0 : existingResource.getViews() + 1;
		existingResource.setViews(viewResource);
		SessionContextSupport.putLogParameter(RATING_EVENT_NAME,CONTENT_VIEWS);
		SessionContextSupport.putLogParameter(VIEWS, viewResource);
		SessionContextSupport.putLogParameter(GOORU_OID, gooruContentId);
		resourceService.saveOrUpdate(existingResource);
		this.getResourceCassandraService().saveViews(gooruContentId);
		// redisService.updateCount(gooruContentId, Constants.REDIS_VIEWS);
		// indexerMessenger.sendMessageToIndex(IndexerMessenger.SEARCH_REINDEX_MSG,
		// existingResource.getGooruOid(), existingResource.getContentId(),
		// RESOURCE);
		if (existingResource.getResourceType() != null && existingResource.getResourceType().getName().equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())) {
			indexProcessor.index(existingResource.getGooruOid(), IndexProcessor.INDEX, SCOLLECTION);
		} else
			indexProcessor.index(existingResource.getGooruOid(), IndexProcessor.INDEX, RESOURCE);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/resource/{gooruContentId}.{format}")
	public void updateResource(HttpServletRequest request, @PathVariable(GOORU_CONTENT_ID) String gooruContentId, @PathVariable(FORMAT) String format, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = RESOURCE_TITLE, required = false) String resourceTitle,
			@RequestParam(value = DISTINGUISH, required = false) String distinguish, @RequestParam(value = IS_FEATURED, required = false) Integer isFeatured, @RequestParam(value = DESCRIPTION, required = false) String description,
			@RequestParam(value = HAS_FRAME_BREAKER, required = false) Boolean hasFrameBreaker, @RequestParam(value = TAGS, required = false) String tags, @RequestParam(value = SHARING, required = false) String sharing,
			@RequestParam(value = _RESOURCE_SOURCE_ID, required = false) Integer resourceSourceId, @RequestParam(value = MEDIA_TYPE, required = false) String mediaType, @RequestParam(value = IS_BLACKLISTED, required = false) Boolean isBlacklisted,
			@RequestParam(value = ATTRIBUTION, required = false) String attribution, @RequestParam(value = CATEGORY, required = false) String category, @RequestParam(value = MEDIA_FILE_NAME, required = false) String mediaFileName, @RequestParam(value = GRADE, required = false) String grade,@RequestParam(value = RESOURCE_FORMAT, required = false) String resource_format,
			@RequestParam(value = LICENSENAME, required = false) String licenseName,HttpServletResponse response) throws Exception {
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
		}
		toModelAndView(resourceService.updateResourceByGooruContentId(gooruContentId, resourceTitle, distinguish, isFeatured, description, hasFrameBreaker, tags, sharing, resourceSourceId, user, mediaType, attribution, category, mediaFileName, isBlacklisted, grade, resource_format,licenseName), FORMAT_JSON);
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
	public ModelAndView updateResourceImage(HttpServletRequest request, @PathVariable(GOORU_CONTENT_ID) String gooruContentId, @RequestParam(value = MEDIA_FILE_NAME) String fileName, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletResponse response) throws Exception {
		String filePath = this.getResourceService().updateResourceImage(gooruContentId, fileName);
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		jsonmodel.addObject(MODEL, filePath);
		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/resource/{gooruContentId}/thumbnail")
	public ModelAndView updateResourceThumbnail(HttpServletRequest request, @PathVariable(GOORU_CONTENT_ID) String gooruContentId, @RequestParam(value = UPLOAD_FILENAME) String fileName, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletResponse response) throws Exception {
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

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/{collection}/{gooruContentId}/segment/{segmentId}/resource/upload.{format}")
	public ModelAndView uploadResource(HttpServletRequest request, @PathVariable(GOORU_CONTENT_ID) String gooruContentId, @PathVariable(COLLECTION) String collection, @PathVariable(FORMAT) String format, @RequestParam(value = SESSIONTOKEN) String sessionToken,
			@RequestParam(value = RESOURCE_INSTANCE_ID, required = false) String resourceInstanceId, @RequestParam(value = TITLE, required = false) String title, @RequestParam(value = DESCRIPTION, required = false) String description, @RequestParam(value = URL, required = false) String url,
			@RequestParam(value = START, required = false) String start, @RequestParam(value = STOP, required = false) String stop, @RequestParam(value = FOLDER, required = false) String reuseFolder, @RequestParam(value = INSTRUCTION, required = false) String instruction,
			@RequestParam(value = DOCUMENT__ID, required = false) String documentId, @RequestParam(value = DOCUMENT_KEY, required = false) String documentKey, @RequestParam(value = REUSED, required = false) String reused, @RequestParam(value = TYPE, required = false) String type,
			@RequestParam(value = THUMBNAIL_IMG_SRC, required = false) String thumbnailImgSrc, @RequestParam(value = MULTI_CHK, required = false) String multiChk, @RequestParam(value = DURATION_CHK, required = false) String durationChk,
			@RequestParam(value = OPTION_TEXT, required = false) String optionText, @RequestParam(value = _MIN, required = false) String min, @RequestParam(value = _SEC, required = false) String sec, @RequestParam(value = CORRECT_OPTION, required = false) String correctOption,
			@RequestParam(value = UPLOAD_TYPE, required = false) String uploadType, @RequestParam(value = REUSED_RESOURCE_ID, required = false) String reusedResourceId, @PathVariable(SEGMENT_ID) String segmentId, @RequestParam(value = CATEGORY, required = false) String category,
			@RequestParam(value = UPDATE_NARRATIVE, required = false, defaultValue = FALSE) boolean updateNarrative, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, RES_UPLOAD_RES);
		User user = (User) request.getAttribute(Constants.USER);
		Map<String, Object> formField = RequestUtil.getMultipartItems(request);
		Map<String, Object> resourceParam = new HashMap<String, Object>();
		resourceParam.put(SEGMENT_ID, segmentId);
		resourceParam.put(RESOURCE_INSTANCE_ID, resourceInstanceId);
		resourceParam.put(DESCRIPTION, description);
		resourceParam.put(RESOURCE_TITLE, title);
		resourceParam.put(RESOURCE_URL, url);
		resourceParam.put(RESOURCE_TYPE, type);
		resourceParam.put(START, start);
		resourceParam.put(STOP, stop);
		resourceParam.put(NARRATIVE, instruction);
		resourceParam.put(REUSED, reused);
		resourceParam.put(USER, user);
		resourceParam.put(GOORU_CONTENT_ID, gooruContentId);
		resourceParam.put(REUSED_RESOURCE_ID, reusedResourceId);
		resourceParam.put(CATEGORY, category);
		resourceParam.put(REQUEST, request);
		resourceParam.put(THUMBNAIL_IMG_SRC, thumbnailImgSrc);
		resourceParam.put(UPDATE_NARRATIVE, updateNarrative ? ONE : ZERO);
		ResourceInstance resourceInstance = this.getResourceService().buildResourceInstance(resourceParam, formField);
		SessionContextSupport.putLogParameter(RATING_EVENT_NAME, RESOURCE_ADD);
		SessionContextSupport.putLogParameter(SEGMENT_ID, segmentId);
		SessionContextSupport.putLogParameter(USER_ID, user.getUserId());
		SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());
		SessionContextSupport.putLogParameter(START, resourceInstance.getStart());
		SessionContextSupport.putLogParameter(STOP, resourceInstance.getStop());
		SessionContextSupport.putLogParameter(RESOURCE_INSTANCE_ID, resourceInstance.getResourceInstanceId());
		SessionContextSupport.putLogParameter(RESOURCE_TITLE, title);
		SessionContextSupport.putLogParameter(CONTENT_ID, resourceInstance.getResource().getContentId());
		SessionContextSupport.putLogParameter(GET_GOORU_OID, resourceInstance.getResource().getGooruOid());
		this.getShelfService().updateShelfItem(null, user, resourceInstance.getResource().getGooruOid());
		return getResourceModel(format, resourceInstance);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "myContent/upload.{format}")
	public ModelAndView uploadMyContent(HttpServletRequest request, @PathVariable String format, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = TITLE, required = false) String title, @RequestParam(value = DESCRIPTION, required = false) String description,
			@RequestParam(value = URL, required = false) String url, @RequestParam(value = INSTRUCTION, required = false) String instruction, @RequestParam(value = DOCUMENT__ID, required = false) String documentId, @RequestParam(value = _DOCUMENT_KEY, required = false) String documentKey,
			@RequestParam(value = TYPE, required = false) String type, @RequestParam(value = THUMBNAIL_IMG_SRC, required = false) String thumbnailImgSrc, @RequestParam(value = MULTI_CHK, required = false) String multiChk, @RequestParam(value = DURATION_CHK, required = false) String durationChk,
			@RequestParam(value = OPTION_TEXT, required = false) String optionText, @RequestParam(value = REUSED_RESOURCE_ID, required = false) String reusedResourceId, @RequestParam(value = _MIN, required = false) String min, @RequestParam(value = _SEC, required = false) String sec,
			@RequestParam(value = CORRECT_OPTION, required = false) String correctOption, @RequestParam(value = UPLOAD_TYPE, required = false) String uploadType, @RequestParam(value = CATEGORY, required = false) String category,
			@RequestParam(value = UPDATE_NARRATIVE, required = false, defaultValue = FALSE) boolean updateNarrative, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, RES_UPLOAD_RES);
		User user = (User) request.getAttribute(Constants.USER);
		Map<String, Object> formField = RequestUtil.getMultipartItems(request);
		Map<String, Object> resourceParam = new HashMap<String, Object>();
		Map<String, String> customFieldAndValueMap = collectionUtil.getCustomFieldNameAndValueAsMap(request);
		resourceParam.put(DESCRIPTION, description);
		resourceParam.put(RESOURCE_TITLE, title);
		resourceParam.put(RESOURCE_URL, url);
		resourceParam.put(RESOURCE_TYPE, type);
		resourceParam.put(NARRATIVE, instruction);
		resourceParam.put(USER, user);
		resourceParam.put(REUSED_RESOURCE_ID, reusedResourceId);
		resourceParam.put(CATEGORY, category);
		resourceParam.put(REQUEST, request);
		resourceParam.put(THUMBNAIL_IMG_SRC , thumbnailImgSrc);
		resourceParam.put(UPDATE_NARRATIVE, updateNarrative ? ONE : ZERO);
		Resource resource = null;
		if (url != null) {
			ModelAndView mView = new ModelAndView(REST_MODEL);
			JSONObject jsonExistingResource = new JSONObject();
			resource = this.getResourceService().findResourceByUrl(url, null, user.getGooruUId());
			if (resource != null) {
				response.setStatus(500);
				jsonExistingResource.put(_ERROR, "It looks like this resource already exists in \nBlue Sky!.").put(GOORU_OID, resource.getGooruOid()).put(FRAME_BREAKER, resource.getHasFrameBreaker() == null ? 0 : 1);
				mView.addObject(MODEL, jsonExistingResource);
				return mView;
			}

		}
		resource = this.getResourceService().buildMyContent(resourceParam, formField);
		if (customFieldAndValueMap.size() > 0) {
			customFieldService.saveCustomFieldInfo(resource.getGooruOid(), customFieldAndValueMap);
		}
		SessionContextSupport.putLogParameter(RATING_EVENT_NAME, RESOURCE_ADD);
		SessionContextSupport.putLogParameter(USER_ID, user.getUserId());
		SessionContextSupport.putLogParameter(RESOURCE_TITLE, title);
		SessionContextSupport.putLogParameter(CONTENT_ID, resource.getContentId());
		SessionContextSupport.putLogParameter(GET_GOORU_OID, resource.getGooruOid());
		UserContentRelationshipUtil.updateUserContentRelationship(resource, user, RELATIONSHIP.CREATE);
		return toModelAndView(resource, FORMAT_JSON);
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
		resourceService.deleteResourceFromGAT(gooruContentId, isThirdPartyUser, apiCaller, isMycontent);
	}

	/**
	 * Delete a resources from gooru. * Response: 200 OK
	 * 
	 * @param gooruContentIds
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
	@RequestMapping(method = RequestMethod.DELETE, value = "/resource/bulk/{gooruContentIds}")
	public void deleteResourceBulk(@PathVariable(GOORU_CONTENT_IDS) String gooruContentIds, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletRequest request, HttpServletResponse response, final ModelMap model) throws Exception {
		request.setAttribute(PREDICATE, RESOURCE_DELETE);
		resourceService.deleteResourceBulk(gooruContentIds);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/resource/set/defaultThumbnail")
	public ModelAndView setDefaultResourceThumbnail(@RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = CONTENT_TYPE) String contentType, @RequestParam(value = PAGE_NO, required = false, defaultValue = ONE) final Integer pageNo,
			@RequestParam(value = PAGE_SIZE, required = false, defaultValue = THOUSAND) final Integer pageSize, @RequestParam(value = BATCH_SIZE, required = false, defaultValue = FIVE_THOUSAND) final Integer batchSize, HttpServletRequest request, HttpServletResponse response, final ModelMap model)
			throws Exception {
		request.setAttribute(PREDICATE, RES_SET_DEFAULT_THUMBNAIL);
		User user = (User) request.getAttribute(Constants.USER);
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		if (user != null && hasUnrestrictedContentAccess()) {
			this.getResourceService().setDefaultThumbnail(contentType, batchSize, pageSize);
		} else {
			throw new AccessDeniedException("Do not have premission, login as content admin user");
		}
		jsonmodel.addObject(MODEL, SUCCESS);
		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_READ })
	@RequestMapping(method = RequestMethod.GET, value = "analytic/resource/{gooruOid}/data.{format}")
	public ModelAndView getResourceAnalyticData(@RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = CONTENT_TYPE) String contentType, @PathVariable(GOORU_OID) String gooruOid, @PathVariable(FORMAT) String format, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		request.setAttribute(PREDICATE, RES_GET_RES_ANALYTIC_DATA);
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		User user = (User) request.getAttribute(Constants.USER);
		JSONObject analyticData = this.getResourceService().getResourceAnalyticData(gooruOid, contentType, user);
		jsonmodel.addObject(MODEL, analyticData);
		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/resource/{resourceId}/count")
	public ModelAndView getResourcePageCount(@RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @PathVariable(RESOURCE_ID) String resourceId, @RequestParam(value = FORMAT, required = true) final String format, HttpServletRequest request, HttpServletResponse response) throws Exception {
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

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_UPDATE })
	@RequestMapping(method = RequestMethod.PUT, value = "/resource/update/pdf")
	public ModelAndView updateResourceInfo(@RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = PAGE_SIZE, required = false, defaultValue = THOUSAND) Integer pageSize, @RequestParam(value = PAGE_NO, required = false, defaultValue = ONE) Integer pageNo,
			@RequestParam(value = BATCH_ID, required = false) String batchId, @RequestParam(value = GET_GOORU_OID, required = false) String gooruOId, @RequestParam(value = UPDATE_ALL, required = false, defaultValue = FALSE) boolean updateAll, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String status = "";
		if (gooruOId == null) {
			Map<String, String> filters = new HashMap<String, String>();
			filters.put(PAGE_NUM, pageNo + "");
			filters.put(PAGE_SIZE, pageSize + "");
			if (batchId != null) {
				filters.put(BATCH_ID, batchId);
			}
			String resourceType = ResourceType.Type.TEXTBOOK.getType() + "," + ResourceType.Type.HANDOUTS.getType() + "," + ResourceType.Type.EXAM.getType() + "," + ResourceType.Type.PRESENTATION.getType();
			filters.put(RESOURCE_TYPE, resourceType);
			filters.put(BATCH_ID, batchId);
			if (updateAll) {
				pageNo = 1;
				while (true) {
					logger.info("resource info updates with pageSize: " + pageSize + ", pageNo: " + pageNo);
					List<Resource> resources = getResourceService().listResources(filters);
					if (resources.size() == 0) {
						status = "Completed resource info updates";
						break;
					}
					for (Resource resource : resources) {
						getResourceService().updateResourceInfo(resource);
					}
					pageNo++;
				}
			} else {
				List<Resource> resources = getResourceService().listResources(filters);
				logger.info("Started resource info updates with pageSize: " + pageSize + ", pageNo: " + pageNo);
				for (Resource resource : resources) {
					getResourceService().updateResourceInfo(resource);
				}
				logger.info("Completed resource info updates with pageSize: " + pageSize + ", pageNo: " + pageNo);
				status = "Completed resource info update of noOfPage with pageSize: " + pageSize + ", pageNo: " + pageNo;
			}
		} else if (gooruOId != null) {
			Resource resource = getResourceService().findResourceByContentGooruId(gooruOId);
			getResourceService().updateResourceInfo(resource);
			status = "Updated resource page count with gooruOid: " + gooruOId;
		}
		return toModelAndView(status, JSON);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_UPDATE })
	@RequestMapping(method = RequestMethod.PUT, value = "/resource/feeds/update")
	public ModelAndView updateResourceFeeds(@RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = PAGE_SIZE, required = false, defaultValue = THOUSAND) Integer pageSize, @RequestParam(value = PAGE_NO, required = false, defaultValue = ONE) Integer pageNo,
			@RequestParam(value = BATCH_ID, required = false) String batchId, @RequestParam(value = GET_GOORU_OID, required = false) String gooruOId, @RequestParam(value = UPDATE_ALL, required = false, defaultValue = FALSE) boolean updateAll, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String status = "";
		if (gooruOId == null) {
			Map<String, String> filters = new HashMap<String, String>();
			filters.put(PAGE_NUM , pageNo + "");
			filters.put(PAGE_SIZE, pageSize + "");
			if (batchId != null) {
				filters.put(BATCH_ID, batchId);
			}
			filters.put(RESOURCE_TYPE, ResourceType.Type.VIDEO.getType());
			filters.put(BATCH_ID, batchId);

			if (updateAll) {
				pageNo = 1;
				while (true) {
					logger.info("resource feeds updates with pageSize: " + pageSize + ", pageNo: " + pageNo);
					List<Resource> resources = getResourceService().listResources(filters);
					if (resources.size() == 0) {
						status = "Completed resource feeds updates";
						break;
					}
					for (Resource resource : resources) {
						getResourceService().updateYoutubeResourceFeeds(resource, true);
					}
					pageNo++;
				}
			} else {
				List<Resource> resources = getResourceService().listResources(filters);
				logger.info("Started resource feeds updates with pageSize: " + pageSize + ", pageNo: " + pageNo);
				for (Resource resource : resources) {
					getResourceService().updateYoutubeResourceFeeds(resource, true);
				}
				logger.info("Completed resource feeds updates with pageSize: " + pageSize + ", pageNo: " + pageNo);
				status = "Completed resource feeds updates with pageSize: " + pageSize + ", pageNo: " + pageNo;
			}
		} else if (gooruOId != null) {
			Resource resource = getResourceService().findResourceByContentGooruId(gooruOId);
			getResourceService().updateResourceInfo(resource);
			status = "updated resourceFeeds with gooruOid:" + gooruOId;
		}

		return toModelAndView(status, JSON);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/resource/suggest/meta/info")
	public ModelAndView SuggestResourceMetaData(@RequestParam(value = URL) String url, @RequestParam(value = TITLE, required = false) String title, @RequestParam(value = FETCH_THUMBNAIL, required = false, defaultValue = FALSE) boolean fetchThumbnail, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return toModelAndView(serializeToJson(getResourceService().getSuggestedResourceMetaData(url, title, fetchThumbnail), true));
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

	public Properties getClassPlanConstants() {
		return classPlanConstants;
	}

	public void setClassPlanConstants(Properties classPlanConstants) {
		this.classPlanConstants = classPlanConstants;
	}

	public void setSessionActivityService(SessionActivityService sessionActivityService) {
		this.sessionActivityService = sessionActivityService;
	}

	public SessionActivityService getSessionActivityService() {
		return sessionActivityService;
	}

	public ShelfService getShelfService() {
		return shelfService;
	}

	public ResourceCassandraService getResourceCassandraService() {
		return resourceCassandraService;
	}

}
