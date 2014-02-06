package org.ednovo.gooru.controllers.api;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.application.converter.ImageScaler;
import org.ednovo.gooru.application.util.ConfigProperties;
import org.ednovo.gooru.application.util.ProfileImageUtil;
import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ApiKey;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentPermission;
import org.ednovo.gooru.core.api.model.Credential;
import org.ednovo.gooru.core.api.model.Gender;
import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.Organization;
import org.ednovo.gooru.core.api.model.Profile;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.RoleEntityOperation;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserAccountType;
import org.ednovo.gooru.core.api.model.UserAvailability.CheckUser;
import org.ednovo.gooru.core.api.model.UserRelationship;
import org.ednovo.gooru.core.api.model.UserRole.UserRoleType;
import org.ednovo.gooru.core.api.model.UserToken;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.core.application.util.RequestUtil;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.PartyService;
import org.ednovo.gooru.domain.service.apitracker.ApiTrackerService;
import org.ednovo.gooru.domain.service.classplan.LearnguideService;
import org.ednovo.gooru.domain.service.content.ContentService;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.gooru.domain.service.subscription.SubscriptionService;
import org.ednovo.gooru.domain.service.user.UserService;
import org.ednovo.gooru.domain.service.userToken.UserTokenService;
import org.ednovo.gooru.domain.service.usercontent.UserContentService;
import org.ednovo.gooru.infrastructure.mail.MailHandler;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.OrganizationSettingRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserTokenRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.activity.ActivityRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.annotation.SubscriptionRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.classplan.LearnguideRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.content.ContentRepository;
import org.ednovo.gooru.json.serializer.util.JsonSerializer;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.resource.ClientResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.thoughtworks.xstream.XStream;

@Controller
@RequestMapping(value = { "/user", "" })
public class UserRestController extends BaseController implements ConstantProperties {

	@Autowired
	private RedisService redisService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BaseRepository baseRepository;

	@Autowired
	private LearnguideRepository classplanRepository;

	@Autowired
	private ContentRepository contentRepository;

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private ProfileImageUtil profileImageUtil;

	@Autowired
	private UserTokenRepository userTokenRepository;

	@Autowired
	private ActivityRepository activityRepository;

	@Autowired
	private IndexProcessor indexProcessor;

	@Autowired
	private MailHandler mailHandler;

	@Autowired
	@Resource(name = "userService")
	private UserService userService;

	@Autowired
	@Resource(name = "serverConstants")
	private Properties serverConstants;

	@Autowired
	@Resource(name = "classplanConstants")
	private Properties configConstants;

	@Autowired
	private LearnguideService learnguideService;

	@Autowired
	@Resource(name = "subscriptionService")
	private SubscriptionService subscriptionService;

	@Autowired
	@Resource(name = "userContentService")
	private UserContentService userContentService;

	@Autowired
	@Resource(name = "contentService")
	private ContentService contentService;

	@Autowired
	@Resource(name = "userTokenService")
	private UserTokenService userTokenService;

	@Autowired
	private SettingService settingService;

	@Autowired
	private OrganizationSettingRepository organizationSettingRepository;

	private static final Logger logger = LoggerFactory.getLogger(UserRestController.class);

	@Autowired
	private ApiTrackerService apiTrackerService;

	@Autowired
	private PartyService partyService;

	@Autowired
	private ConfigProperties configProperties;
	
	

	/**
	 * Check if the user has a valid session. Will throw an exception if user is
	 * not logged-in, and is not anonymous
	 * 
	 * @param sessionToken
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SESSION_CHECK })
	@RequestMapping(method = RequestMethod.GET, value = "/user/check-session")
	public ModelAndView checkSession(@RequestParam(value = SESSIONTOKEN) String sessionToken, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		JSONObject jsonObj = new JSONObject();
		jsonmodel.addObject(MODEL, jsonObj.put(IS_AUTHENTICATED, TRUE));

		SessionContextSupport.putLogParameter(RATING_EVENT_NAME, CHECK_SESSION);
		SessionContextSupport.putLogParameter(SESSIONTOKEN, sessionToken);

		return jsonmodel;

	}

	/**
	 * Retrieve profile information for a user
	 * 
	 * @param userId
	 *            - gooru user id
	 * @param format
	 *            - supported format (currently only supports JSON)
	 * @param sessionToken
	 *            - session token that authenticates the request
	 * @param request
	 *            - HttpServletRequest object
	 * @param response
	 *            - HttpServletResponse object
	 * @return
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/user/{userId}/profile.{format}")
	public ModelAndView getProfile(@PathVariable(value = USER_ID) String userId, @PathVariable(value = FORMAT) String format, @RequestParam(value = CUR_USER_ID, required = false) String currentUserId, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_GET_PROFILE);
		/*
		 * Step 1 - Get the user object from database.
		 */
		// User user = this.getUserRepository().findByGooruId(userId);

		User user = this.getUserService().findByGooruId(userId);

		Profile profile = this.getUserService().getProfile(user);

		String externalId = null;

		if (user.getAccountTypeId() != null && (user.getAccountTypeId().equals(UserAccountType.ACCOUNT_CHILD))) {

			externalId = getUserService().findUserByGooruId(user.getParentUser().getGooruUId()).getExternalId();
		} else {
			externalId = getUserService().findUserByGooruId(user.getGooruUId()).getExternalId();
		}

		/*
		 * Step 2 - Get the segmentXML from classplan.
		 */

		int classplanListSize = this.getLearnguideService().findByUser(user, ResourceType.Type.CLASSPLAN).size();
		int classbookListSize = this.getLearnguideService().findByUser(user, ResourceType.Type.CLASSBOOK).size();
		int subscribersSize = this.getSubscriptionService().countSubscriptionsForUserContent(user);

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		if (format.equals(FORMAT_JSON)) {
			JSONObject resultsJSON = serializeToJsonObject(profile);
			if (currentUserId != null) {
				resultsJSON.put(IS_FOLLOWING, this.getUserService().getFollowedOnUsers(currentUserId).size() > 0);
			}
			jsonmodel.addObject(MODEL, resultsJSON.put(CLASS_PLANS, classplanListSize).put(CLASS_BOOKS, classbookListSize).put(SUBSCRIBERS, subscribersSize).put(EXTERNAL_ID, externalId));
			if (profile.getThumbnailBlob() == null) {
				resultsJSON.put(THUMBNAIL_BLOB, _NULL);
			} else {
				resultsJSON.put(THUMBNAIL_BLOB, VALID);
			}
		}
		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/collection/{userId}/common/{compareUserId}.{format}")
	public ModelAndView getUsersCollectionsInCommon(@PathVariable(value = USER_ID) String userId, @PathVariable(value = COMPARE_USER_ID) String compareUserId, @PathVariable(value = FORMAT) String format, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_GET_PROFILE);

		List<Learnguide> collections = this.getUserContentService().listCommonCollections(userId, compareUserId);

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		// Step 4 - Generate the appropriate object for serialization purposes.
		if (format.equalsIgnoreCase(FORMAT_JSON)) {
			JSONArray resourcesArray = new JSONArray();
			for (Learnguide collection : collections) {
				resourcesArray.put(serializeToJsonObject(collection, TAXONOMY_SET));
			}
			jsonmodel.addObject(MODEL, resourcesArray);
		} else if (format.equalsIgnoreCase(FORMAT_XML)) {
			XStream xstream = new XStream();
			xstream.alias(LEARN_GUIDE, Learnguide.class);
			String classPlanXML = xstream.toXML(collections);
			jsonmodel.addObject(MODEL, classPlanXML);
		}

		return jsonmodel;
	}

	/**
	 * Get user's profile picture.
	 * 
	 * @param userId
	 * @param sessionToken
	 * @param useDefaultPicture
	 * @param thumbnail
	 * @param height
	 * @param width
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	// TODO: Get this method return data from a file repository as the primary
	// source.
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/user/{userId}/profile/picture")
	public ModelAndView getProfilePicture(@PathVariable(value = USER_ID) String userId, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = USE_DEFAULT_PICTURE, required = false) boolean useDefaultPicture,
			@RequestParam(value = THUMBNAIL, defaultValue = TRUE, required = false) boolean thumbnail, @RequestParam(value = HEIGHT, defaultValue = ONE_FIFTY, required = false) int height, @RequestParam(value = WIDTH, defaultValue = ONE_FIFTY, required = false) int width,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_GET_PROFILE_PICTURE);

		final String cacheKey = "profile.pict-" + userId;
		byte[] pictureData = null;

		String mapKey = ".isThumb-" + thumbnail + ".dim-" + width + "x" + height;
		Map<String, byte[]> pictureDataMap = getRedisService().getValue(cacheKey) != null ? JsonDeserializer.deserialize(getRedisService().getValue(cacheKey), new TypeReference<Map<String, byte[]>>() {
		}) : null;
		if (pictureDataMap == null) {
			pictureDataMap = new HashMap<String, byte[]>();
		}

		pictureData = pictureDataMap.get(mapKey);

		Profile profile = null;
		if (pictureData == null) {
			/*
			 * Step 1 - Get the user object from request.
			 */
			User user = userService.findByGooruId(userId);

			profile = userService.getProfile(user);
			/*
			 * Step 2 - Get the segmentXML from classplan.
			 */
			// BufferedImage profileImage = null;
			pictureData = profile.getThumbnailBlob();

			if (pictureData != null) {
				if (!thumbnail) {
					InputStream in = new ByteArrayInputStream(profile.getPictureBlob());
					BufferedImage profileImage = ImageIO.read(in);
					Integer originalImageHeight = profileImage.getHeight();
					Integer originalImageWidth = profileImage.getWidth();
					ByteArrayOutputStream baos = new ByteArrayOutputStream();

					Integer targetWidth = null;
					Integer targetHeight = null;

					if (originalImageHeight >= 320 && originalImageWidth >= 470) {
						targetWidth = 470;
						targetHeight = 320;
					} else if (originalImageHeight >= 320 && originalImageWidth <= 470) {
						targetWidth = profileImage.getWidth();
						targetHeight = 320;
					} else if (originalImageHeight <= 320 && originalImageWidth >= 470) {
						targetWidth = 470;
						targetHeight = profileImage.getHeight();
					}
					if (targetWidth != null && targetHeight != null) {
						profileImage = ImageScaler.scaleImage(profileImage, targetWidth, targetHeight);
					}
					ImageIO.write(profileImage, PNG, baos);
					baos.flush();
					pictureData = baos.toByteArray();
					baos.close();
				} else {
					if ((height != 150 || width != 150) && profile.getPictureBlob() != null) {
						InputStream in = new ByteArrayInputStream(profile.getPictureBlob());
						BufferedImage profileImage = ImageIO.read(in);
						ByteArrayOutputStream baos = new ByteArrayOutputStream();

						Integer targetWidth = width;
						Integer targetHeight = height;

						if (targetWidth != null && targetHeight != null) {
							profileImage = ImageScaler.scaleImage(profileImage, targetWidth, targetHeight);
						}
						ImageIO.write(profileImage, PNG, baos);
						baos.flush();
						pictureData = baos.toByteArray();
						baos.close();
					}
				}
				pictureDataMap.put(mapKey, pictureData);
				getRedisService().putValue(cacheKey, JsonSerializer.serializeToJson(pictureDataMap, true), RedisService.DEFAULT_PROFILE_EXP);
			}
		}
		if ((pictureData == null) && (useDefaultPicture)) {
			String imageUrl = "/images/classmate/profile/profile_picture.png";
			String cacheDefaultImageKey = "profile.pict1.default" + thumbnail + ".dim-" + width + "x" + height;

			pictureData = getRedisService().getValue(cacheKey) != null ? JsonDeserializer.deserialize(getRedisService().getValue(cacheKey), new TypeReference<byte[]>() {
			}) : null;
			if (pictureData == null) {
				File file = new File(request.getSession().getServletContext().getRealPath("/") + "../gooru/" + imageUrl);
				pictureData = FileUtils.readFileToByteArray(file);
				InputStream in = new ByteArrayInputStream(pictureData);
				BufferedImage profileImage = ImageIO.read(in);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				Integer targetWidth = width;
				Integer targetHeight = height;

				if (targetWidth != null && targetHeight != null) {
					profileImage = ImageScaler.scaleImage(profileImage, targetWidth, targetHeight);
				}
				ImageIO.write(profileImage, PNG, baos);
				baos.flush();
				pictureData = baos.toByteArray();
				baos.close();

				getRedisService().putValue(cacheDefaultImageKey, JsonSerializer.serializeToJson(pictureData, true), RedisService.DEFAULT_PROFILE_EXP);
			}

		}
		if (pictureData == null) {
			response.setStatus(404);
		} else {
			response.setContentType(IMAGE_PNG);
			OutputStream os = response.getOutputStream();
			os.write(pictureData);
			os.close();
		}
		return null;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/user/{gooruUserId}/followers")
	public ModelAndView getFollowedByUsers(@PathVariable(value = GOORU_USER_ID) String gooruUserId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_FOLLOWERS_LIST);

		return toModelAndViewWithIoFilter(getUserService().getFollowedByUsers(gooruUserId), FORMAT_JSON, EXCLUDE_USER);

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/user/{gooruUserId}/following")
	public ModelAndView getFollowedOnUsers(@PathVariable(value = GOORU_USER_ID) String gooruUserId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_FOLLOWING_LIST);

		return toModelAndViewWithIoFilter(getUserService().getFollowedOnUsers(gooruUserId), FORMAT_JSON, EXCLUDE_USER);

	}

	/*
	 * @AuthorizeOperations(operations = {
	 * GooruOperationConstants.OPERATION_USER_UPDATE })
	 * 
	 * @Transactional(readOnly = false, propagation = Propagation.REQUIRED,
	 * rollbackFor = Exception.class)
	 * 
	 * @RequestMapping(method = RequestMethod.DELETE, value =
	 * "//unfollow/{followOnUserId}")
	 * 
	 * @ApiOperation(value = "Unfollow user", notes =
	 * "Unfollow user with followOnUserId.") public void
	 * unFollowUser(@ApiParam(value = "followOnUserId", required = true)
	 * 
	 * @RequestParam String followOnUserId, HttpServletRequest request,
	 * HttpServletResponse response) throws Exception {
	 * request.setAttribute(Constants.EVENT_PREDICATE, "user.unfollow");
	 * 
	 * User user = (User) request.getAttribute(Constants.USER);
	 * 
	 * if (!getUserService().unFollowUser(user.getGooruUId(), followOnUserId)) {
	 * response.setStatus(HttpServletResponse.SC_NOT_FOUND); }
	 * 
	 * }
	 */

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/user/unfollow/{followOnUserId}")
	public void unFollowUser(@PathVariable(value = FOLLOW_ON_USER_ID) String followOnUserId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_UNFOLLOW);

		User user = (User) request.getAttribute(Constants.USER);

		if (!getUserService().unFollowUser(user.getGooruUId(), followOnUserId)) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/user/follow/{followOnUserId}")
	public ModelAndView followUser(@PathVariable(value = FOLLOW_ON_USER_ID) String followOnUserId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_FOLLOW);

		User user = (User) request.getAttribute(Constants.USER);

		UserRelationship userRelationship = getUserService().followUser(user, followOnUserId);
		return toModelAndViewWithIoFilter(userRelationship.getUser(), FORMAT_JSON, EXCLUDE_USER);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/user/{userId}/profile/picture/trueSize")
	public ModelAndView getProfilePictureTrueSize(@PathVariable(value = USER_ID) String userId, @RequestParam(value = SESSIONTOKEN) String sessionToken, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_GET_PROFILE_PICTURE_TRUE_SIZE);

		User user = this.getUserService().findByGooruId(userId);

		Profile profile = this.getUserService().getProfile(user);

		Integer originalImageHeight = null;
		Integer originalImageWidth = null;

		if (profile.getPictureBlob() != null) {
			response.setContentType(IMAGE_PNG);
			InputStream in = new ByteArrayInputStream(profile.getPictureBlob());
			BufferedImage bImage = ImageIO.read(in);
			originalImageHeight = bImage.getHeight();
			originalImageWidth = bImage.getWidth();
		} else {
			response.setStatus(404);
		}
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		JSONObject jsonObj = new JSONObject();
		jsonmodel.addObject(MODEL, jsonObj.put(ORG_IMG_HEIGHT, originalImageHeight).put(ORG_IMG_WIDTH, originalImageWidth));
		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/user/{userId}/profile/picture")
	public ModelAndView editProfilePicture(@PathVariable(value = USER_ID) String userId, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = MEDIA_FILE_NAME, required = false) String mediaFileName, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_EDIT_PROFILE_PICTURE);
		User apicaller = (User) request.getAttribute(Constants.USER);
		if (!apicaller.getGooruUId().equals(userId) && !hasUnrestrictedContentAccess()) {
			throw new AccessDeniedException(ACCESS_DENIED_EXCEPTION);
		}
		User user= this.getUserRepository().findByGooruId(userId);
		if(user == null) {
			throw new NotFoundException("User not found!!!");
		}
		Profile profile = this.getUserRepository().getProfile(user, false);
		byte[] data = null;
		if (!StringUtils.isEmpty(mediaFileName)) {
			File mediaFolder = new File(user.getOrganization().getNfsStorageArea().getInternalPath() + Constants.UPLOADED_MEDIA_FOLDER);
			File file = new File(mediaFolder.getPath() + "/" + mediaFileName);
			data = FileUtils.readFileToByteArray(file);
		} else {
			Map<String, Object> formField = RequestUtil.getMultipartItems(request);

			Map<String, byte[]> files = (Map<String, byte[]>) formField.get(RequestUtil.UPLOADED_FILE_KEY);

			// expecting only one file in the request right now
			for (byte[] fileContent : files.values()) {
				data = fileContent;
			}
		}

		profile.setPictureBlob(data);
		profileImageUtil.uploadProfileImage(profile, mediaFileName);

		String cacheKey = "profile.pict-" + userId;
		getRedisService().deleteKey(cacheKey);

		return null;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/user/profile/picture/s3upload")
	public void uploadProfilePictureToS3(@RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Profile> profiles = this.getUserRepository().getAll(Profile.class);
		for (Profile profile : profiles) {
			if (profile.getPictureBlob() != null) {
				if (profile.getPictureFormat() == null) {
					profile.setPictureFormat(PNG);
				}
				profileImageUtil.uploadProfileImage(profile, null);
			}
		}
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/user/{userId}/profile/picture")
	public ModelAndView deleteProfilePicture(@PathVariable(value = USER_ID) String userId, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_DEL_PROFILE_PICTURE);
		User user = this.getUserService().findByGooruId(userId);
		profileImageUtil.deleteS3Upload(this.getUserRepository().getProfile(user, false));
		return null;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/user/{userId}/profile/picture/crop")
	public void cropProfilePicture(@PathVariable(value = USER_ID) String userId, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = XPOSITION) int xPosition, @RequestParam(value = YPOSITION) int yPosition, @RequestParam(value = WIDTH) int width,
			@RequestParam(value = HEIGHT) int height, HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_CROP_PROFILE_PICTURE);
		User user = this.getUserService().findByGooruId(userId);

		Profile profile = this.getUserService().getProfile(user);

		if (profile.getPictureBlob() != null) {

			try {

				profile.setPictureBlob(ImageScaler.cropImage(profile.getPictureBlob(), JPG, xPosition, yPosition, width, height));

				profile.setThumbnailBlob(ImageScaler.scaleProfilePicture(profile.getPictureBlob(), JPG, 158, 158));
				this.getUserRepository().save(profile);

				profileImageUtil.uploadProfileImage(profile, null);

			} catch (Exception exception) {
				logger.error("Cannot crop Image : " + exception.getMessage());
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}

		} else {
			response.setStatus(404);
		}

	}

	/**
	 * Updates personal information of a user
	 * 
	 * @param userId
	 *            - gooru user id
	 * @param format
	 *            - format of the response (currently only supports JSON)
	 * @param firstName
	 *            - first name of the user
	 * @param lastName
	 *            - last name of the user
	 * @param gender
	 *            - either M or F
	 * @param birthDate
	 *            - date of the month (between 1 and 31)
	 * @param birthMonth
	 *            - index of month (between 1 and 12)
	 * @param birthYear
	 *            - year in XXXX format
	 * @param province
	 *            - state code of US (refer USPS codes in
	 *            http://en.wikipedia.org/wiki/List_of_U.S._state_abbreviations)
	 * @param aboutMe
	 *            - text describing user
	 * @param sessionToken
	 *            - session token for authorizing request
	 * @param request
	 *            - HttpServletRequest object
	 * @param response
	 *            - HttpServletResponse object
	 * @return
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/user/{userId}/profile/personal.{format}")
	public ModelAndView updatePersonalInformation(@PathVariable(value = USER_ID) String userId, @PathVariable String format, @RequestBody MultiValueMap<String, String> body, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_UPDATE_PERSONAL_INFO);
		String firstName = body.getFirst(FIRST_NAME);
		String lastName = body.getFirst(LAST_NAME);
		String gender = body.getFirst(GENDER);
		String aboutMe = body.getFirst(ABOUT_ME);
		String highestDegree = body.getFirst(HIGHEST_DEGREE);
		String graduation = body.getFirst(GRADUATION);
		String postGraduation = body.getFirst(POST_GRADUATION);
		String highSchool = body.getFirst(HIGH__SCHOOL);
		String website = body.getFirst(WEBSITE);
		String facebook = body.getFirst(FACE_BOOK);
		String twitter = body.getFirst(TWITTER);
		String userType = body.getFirst(_USER_TYPE);
		String subject = body.getFirst(SUBJECT);
		String grade = body.getFirst(GRADE);
		String school = body.getFirst(SCHOOL);

		/*
		 * Step 1 - Get the user object from request.
		 */
		User user = (User) request.getAttribute(Constants.USER);
		if (!user.getGooruUId().equals(userId)) {
			throw new AccessDeniedException(ACCESS_DENIED_EXCEPTION);
		}

		String userFirstName = "";
		String userLastName = "";

		Profile profile = this.getUserService().getProfile(user);

		user = profile.getUser();

		userFirstName = user.getFirstName();
		userLastName = user.getLastName();

		user.getEmailId();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		profile.setUser(user);

		if (gender != null) {
			Gender genderObject = (Gender) this.getBaseRepository().get(Gender.class, gender);
			profile.setGender(genderObject);
		}

		profile.setAboutMe(aboutMe);
		profile.setSubject(subject);
		profile.setGrade(grade);
		profile.setSchool(school);
		profile.setHighestDegree(highestDegree);
		profile.setGraduation(graduation);
		profile.setPostGraduation(postGraduation);
		profile.setHighSchool(highSchool);

		profile.setWebsite(website);
		profile.setFacebook(facebook);
		profile.setTwitter(twitter);
		profile.setUserType(userType);

		Identity identity = this.getUserRepository().findUserByGooruId(userId);
		if (identity != null) {
			if (user.getAccountTypeId() != null && user.getAccountTypeId().equals(UserAccountType.ACCOUNT_CHILD)) {
				user.setEmailId(user.getParentUser().getIdentities().iterator().next().getExternalId());
			} else {
				user.setEmailId(identity.getExternalId());
			}
		}

		this.getUserRepository().save(profile);
		indexProcessor.index(user.getPartyUid(), IndexProcessor.INDEX, USER);

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		if (format.equals(FORMAT_JSON)) {
			jsonmodel.addObject(MODEL, serializeToJson(profile));
		}

		return jsonmodel;

	}

	/**
	 * Updates teaching related data of the user
	 * 
	 * @param userId
	 *            - gooru user id
	 * @param format
	 *            - format of the response (currently only JSON supported)
	 * @param teachingExperience
	 *            - experience of the teacher
	 * @param teachingIn
	 *            - captures school / university being taught by the user
	 * @param subject
	 *            - captures subject being taught by the user
	 * @param grade
	 *            - captures grade being taught by the user
	 * @paaram teachingMethodology - captures methodology information
	 * @param sessionToken
	 *            - session token for authenticating the user
	 * @param request
	 *            - HttpServletRequest object
	 * @param response
	 *            - HttpServletResponse object
	 * @return
	 * @throws Exception
	 */

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/user/{userId}/profile/teaching.{format}")
	public ModelAndView updateTeachingInformation(@PathVariable(value = USER_ID) String userId, @PathVariable String format, @RequestBody MultiValueMap<String, String> body, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_UPDATE_TEACHING_INFO);
		String teachingExperience = body.getFirst(TEACHING_EXP);
		String teachingIn = body.getFirst(TEACHING_IN);
		String subject = body.getFirst(SUBJECT);
		String grade = body.getFirst(GRADE);
		String teachingMethodology = body.getFirst(TEACHING_METHODOLOGY);

		/*
		 * Step 1 - Get the user object from request.
		 */
		User user = (User) request.getAttribute(Constants.USER);

		if (!user.getGooruUId().equals(userId)) {
			throw new AccessDeniedException(ACCESS_DENIED_EXCEPTION);
		}

		Profile profile = this.getUserService().getProfile(user);
		profile.setTeachingExperience(teachingExperience);
		profile.setTeachingIn(teachingIn);
		profile.setSubject(subject);
		profile.setGrade(grade);
		profile.setTeachingMethodology(teachingMethodology);

		this.getUserRepository().save(profile);

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		if (format.equals(FORMAT_JSON)) {
			jsonmodel.addObject(MODEL, serializeToJson(profile));
		}
		return jsonmodel;

	}

	/**
	 * Updates education information for a user
	 * 
	 * @param userId
	 *            - gooru user id
	 * @param format
	 *            - format of the response (currently supports only JSON)
	 * @param highestDegree
	 *            - description of highest degree
	 * @param graduation
	 *            - description of graduation
	 * @param postGraduation
	 *            - description of postGraduation
	 * @param highSchool
	 *            - description of highSchool
	 * @param sessionToken
	 *            - session token that authenticates request
	 * @param request
	 *            - HttpServletRequest object
	 * @param response
	 *            - HttpServletResponse object
	 * @return
	 * @throws Exception
	 */

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/user/{userId}/profile/education.{format}")
	public ModelAndView updateEducationInformation(@PathVariable(value = USER_ID) String userId, @PathVariable String format, @RequestBody MultiValueMap<String, String> body, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_UPDATE_EDU_INFO);
		String highestDegree = body.getFirst(HIGHEST_DEGREE);
		String graduation = body.getFirst(GRADUATION);
		String postGraduation = body.getFirst(POST_GRADUATION);
		String highSchool = body.getFirst(HIGH__SCHOOL);

		/*
		 * Step 1 - Get the user object from request.
		 */
		User user = (User) request.getAttribute(Constants.USER);

		if (!user.getGooruUId().equals(userId)) {
			throw new AccessDeniedException(ACCESS_DENIED_EXCEPTION);
		}

		Profile profile = this.getUserService().getProfile(user);
		profile.setHighestDegree(highestDegree);
		profile.setGraduation(graduation);
		profile.setPostGraduation(postGraduation);
		profile.setHighSchool(highSchool);

		this.getUserRepository().save(profile);

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		if (format.equals(FORMAT_JSON)) {
			jsonmodel.addObject(MODEL, serializeToJson(profile));
		}
		return jsonmodel;

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/user/{userId}/classmate/profile.{format}")
	public ModelAndView updateProfileInformation(@PathVariable(value = USER_ID) String userId, @PathVariable String format, @RequestBody MultiValueMap<String, String> body, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_UPDATE_PROFILE_INFO);
		String aboutMe = body.getFirst(ABOUT_ME);
		String subject = body.getFirst(SUBJECT);
		String school = body.getFirst(SCHOOL);

		User user = (User) request.getAttribute(Constants.USER);

		if (!user.getGooruUId().equals(userId)) {
			throw new AccessDeniedException(ACCESS_DENIED_EXCEPTION);
		}

		Profile profile = this.getUserService().getProfile(user);
		profile.setAboutMe(aboutMe);
		profile.setSubject(subject);
		profile.setSchool(school);

		this.getUserRepository().save(profile);

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		if (format.equals(FORMAT_JSON)) {
			jsonmodel.addObject(MODEL, serializeToJson(profile));
		}
		return jsonmodel;

	}

	/**
	 * Deactivates a user from the application
	 * 
	 * Sample Request - /rest/user/deactivate.json?email=qa1@goorudemo.org
	 * 
	 * @param format
	 *            - format of the response (JSON or XML)
	 * @param registeredEmail
	 *            - email of the user
	 * @param sessionToken
	 *            - session token that authenticates a user request
	 * @param request
	 *            - HttpServletRequest object
	 * @param response
	 *            - HttpServletResponse object
	 * @return
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/user/deactivate.{format}")
	public ModelAndView deactivateUserIdentity(@PathVariable(value = FORMAT) String format, @RequestParam(value = EMAIL) String registeredEmail, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_DEACTIVATE_IDENTITY);
		Identity identity = this.getUserService().findByEmail(registeredEmail);
		userService.deactivateUser(identity);
		return null;
	}

	/**
	 * Reactivate a user
	 * 
	 * @param format
	 *            - format of the response (JSON or XML)
	 * @param registeredEmail
	 *            - email of the user
	 * @param sessionToken
	 *            - session token that authenticates a user request
	 * @param request
	 *            - HttpServletRequest object
	 * @param response
	 *            - HttpServletResponse object
	 * @return
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/user/activate.{format}")
	public ModelAndView activateUserIdentity(@PathVariable(value = FORMAT) String format, @RequestParam(value = EMAIL) final String registeredEmail, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_ACTIVATE_IDENTITY);
		Identity identity = this.getUserService().findByEmail(registeredEmail);
		identity.setDeactivatedOn(null);
		identity.setActive(Short.parseShort(ONE));

		this.getUserRepository().save(identity);

		return null;
	}

	/**
	 * Retrieve list of all the identities registered in gooru
	 * 
	 * @param format
	 *            - format of the response
	 * @param sessionToken
	 *            - session token that authenticates the user
	 * @param pageNo
	 *            - page number of the results
	 * @param pageSize
	 *            - results per page
	 * @param request
	 *            - HttpServletRequest object
	 * @param response
	 *            - HttpServletResponse object
	 * @return
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_LIST })
	@RequestMapping(method = RequestMethod.GET, value = "/users/identities/list.{format}")
	public ModelAndView getAllIdentities(@PathVariable(value = FORMAT) String format, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = PAGE_NO, required = true) final String pageNo,
			@RequestParam(value = PAGE_SIZE, required = true) final String pageSize, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_GET_ALL_IDENTTIES);
		int pageNumber = Integer.parseInt(pageNo);
		int resultPerPage = Integer.parseInt(pageSize);

		List<Identity> identityList = (List<Identity>) this.getUserRepository().getAll(Identity.class);

		String strData = "";

		JSONObject jsonObj = new JSONObject();
		jsonObj.put(_PAGE, pageNumber);
		jsonObj.put(TOTAL, identityList.size());

		JSONObject jsonRowDataObj;
		JSONArray jsonRowDataArray, jsonCellData;

		jsonRowDataArray = new JSONArray();

		int maxLimit = 0;

		if ((resultPerPage * pageNumber) < identityList.size()) {
			maxLimit = resultPerPage * pageNumber;
		} else {
			maxLimit = identityList.size();
		}

		for (int i = (resultPerPage * (pageNumber - 1)); i < maxLimit; i++) {
			Identity identity = identityList.get(i);

			jsonCellData = new JSONArray();

			jsonCellData.put(identity.getFirstName());
			jsonCellData.put(identity.getLastName());
			jsonCellData.put(identity.getExternalId());
			jsonCellData.put(identity.getRegisteredOn());
			jsonCellData.put(identity.getLastLogin());

			if (identity.getActive() == 1) {
				jsonCellData.put(_ACTIVE);
			} else {
				jsonCellData.put(INACTIVE);
			}

			jsonRowDataObj = new JSONObject();
			jsonRowDataObj.put(ID, i + 1);
			jsonRowDataObj.put(CELL, jsonCellData);

			jsonRowDataArray.put(jsonRowDataObj);
		}

		jsonObj.put(ROWS, jsonRowDataArray);

		strData = jsonObj.toString();

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		jsonmodel.addObject(MODEL, strData);
		return jsonmodel;
	}

	/**
	 * Returns the registered date of the user
	 * 
	 * @param userId
	 *            - gooru user id
	 * @param format
	 *            - format of the response
	 * @param sessionToken
	 *            - session token that authenticates the user
	 * @param request
	 *            - HttpServletRequest object
	 * @param response
	 *            - HttpServletResponse object
	 * @return
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/user/{userId}/profile/registeredOn.{format}")
	public ModelAndView memberSinceInfo(@PathVariable(value = USER_ID) String userId, @PathVariable(value = FORMAT) String format, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_MEM_SINCE_INFO);
		User user = (User) request.getAttribute(Constants.USER);

		if (!user.getGooruUId().equals(userId)) {
			throw new AccessDeniedException(ACCESS_DENIED_EXCEPTION);
		}

		user = this.getUserService().findByGooruId(user.getGooruUId());

		Set<Identity> identity = user.getIdentities();
		List<Identity> list = new ArrayList<Identity>(identity);

		Date date = list.get(0).getRegisteredOn();

		SimpleDateFormat sdf;

		sdf = new SimpleDateFormat(MMM);
		String month = sdf.format(date);
		sdf = new SimpleDateFormat(YYYY);
		String year = sdf.format(date);

		ModelAndView mView = new ModelAndView(REST_MODEL);
		mView.addObject(MODEL, month + ", " + year);

		return mView;
	}

	/**
	 * get forgot the password request of the user and sends an email
	 * 
	 * Sample Request - /user/password/reset.json?emailid=qa1@goorudemo.org
	 * Sample Response - a. If user does not exist {error:The email specified by
	 * you does not exist. Please verify your credentials and try again.} b. If
	 * user exists {token:Your new password has been sent to you email id. \n
	 * Please try sign in using your new password.}
	 * 
	 * @param format
	 *            - format of the response
	 * @param emailId
	 *            - emailid of the user
	 * @param sessionToken
	 *            - session token that authenticates the user
	 * @param request
	 *            - HttpServletRequest object
	 * @param response
	 *            - HttpServletResponse object
	 * @return
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE_PASSWORD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/user/password/reset.{format}")
	public ModelAndView forgotPassword(@PathVariable(value = FORMAT) String format, @RequestParam(value = EMAIL_ID) final String emailId, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = GOORU_CLASSIC_URL, required = false) String gooruClassicUrl,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_FORGOT_PWD);
		ModelAndView mView = new ModelAndView(REST_MODEL);
		User apicaller = (User) request.getAttribute(Constants.USER);
		Identity identity = new Identity();
		// identity.setExternalId(emailId);

		// identity = this.getUserRepository().findByEmail(emailId);
		if (apicaller != null && !apicaller.getGooruUId().equalsIgnoreCase(Constants.ANONYMOUS)) {
			identity = this.getUserService().findUserByGooruId(apicaller.getGooruUId());
		} else {
			identity = this.getUserService().findByEmailIdOrUserName(emailId, true, false);
		}
		JSONObject jsonForgotPassword = new JSONObject();
		if (identity == null) {
			jsonForgotPassword.put(ERROR, "The email or username specified by you does not exist. Please verify your credentials and try again.");
			mView.addObject(MODEL, jsonForgotPassword);
			return mView;
		}

		String token = UUID.randomUUID().toString();

		User user = this.getUserService().findByIdentity(identity);

		if (user == null) {
			jsonForgotPassword.put(ERROR, "The email  or username entered is not correct. Please verify your credentials and try again.");
			mView.addObject(MODEL, jsonForgotPassword);
			return mView;
		}

		if (user.getConfirmStatus() == 0) {
			jsonForgotPassword.put(ERROR, "We sent you a confirmation email with instructions on how to complete your Gooru registration. Please check your email, and then try again. Didn’t receive a confirmation email? Please contact us at support@goorulearning.org");
			mView.addObject(MODEL, jsonForgotPassword);
			return mView;
		}
		Credential creds = identity.getCredential();
		if (creds == null && identity.getAccountCreatedType() != null && identity.getAccountCreatedType().equalsIgnoreCase(UserAccountType.accountCreatedType.GOOGLE_APP.getType())) {
			jsonForgotPassword.put(ERROR, "Looks like this email is tied with Google!");
			mView.addObject(MODEL, jsonForgotPassword);
			return mView;
		}

		if (creds == null) {
			creds = new Credential();
			String password = UUID.randomUUID().toString();
			creds.setPassword(password);
			creds.setIdentity(identity);
		}
		creds.setToken(token);
		creds.setResetPasswordRequestDate(new Date(System.currentTimeMillis()));
		this.getUserRepository().save(creds);
		identity.setCredential(creds);
		mailHandler.sendMailToResetPassword(user.getGooruUId(), null, false, gooruClassicUrl, null);
		jsonForgotPassword.put(TOKEN, token).put(_EMAIL_ID, emailId).put(ACCOUNT_TYPE_ID, user.getAccountTypeId()).put(_FIRST_NAME, user.getFirstName()).put(_GOORU_UID, user.getGooruUId()).put(RESET_TOKEN, user.getRegisterToken()).put(USER_NAME, user.getUsername()).put(ERROR, "");
		if (format.equals(FORMAT_JSON)) {
			mView.addObject(MODEL, jsonForgotPassword);
		}

		return mView;
	}

	/**
	 * Reset user password
	 * 
	 * @param sessionToken
	 *            - session token that authenticates the user
	 * @param request
	 *            - HttpServletRequest object
	 * @param response
	 *            - HttpServletResponse object
	 * @param reset
	 *            token - resetToken StringUsername of the user
	 * @return
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE_PASSWORD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/user/reset/credential.{format}")
	public ModelAndView resetCredential(HttpServletRequest request, @PathVariable(value = FORMAT) String format, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = TOKEN, required = false) String token, @RequestParam(value = PASSWORD) String password,
			@RequestParam(value = GOORU_UID, required = false) String gooruId, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, RESET_CREDENTIAL);
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		Identity identity = null;
		if (token != null) {
			if (this.getUserService().hasResetTokenValid(token)) {
				JSONObject errorResultsJSON = new JSONObject();
				errorResultsJSON.put(TOKEN_EXPIRED, this.getUserService().hasResetTokenValid(token));
				jsonmodel.addObject(MODEL, errorResultsJSON);
				return jsonmodel;
			}
			identity = this.getUserService().findIdentityByResetToken(token);
			if (identity.getUser().getUsername().equalsIgnoreCase(password)) {
				jsonmodel.addObject(MODEL, "Password should not be same with the Username");
			}
		} else {
			User user = (User) request.getAttribute(Constants.USER);
			if (getUserService().isContentAdmin(user)) {
				identity = this.getUserService().findUserByGooruId(gooruId);
			} else {
				jsonmodel.addObject(MODEL, "Admin user can only change another user password !");
				return jsonmodel;
			}
		}

		boolean flag = false;

		String tokenIsExist = identity.getCredential().getToken();

		if (tokenIsExist != null && tokenIsExist.contains(RESET_TOKEN_SUFFIX)) {
			flag = true;
		}

		String newGenereatedToken = UUID.randomUUID().toString() + RESET_TOKEN_SUFFIX;

		String resetPasswordConfirmRestendpoint = settingService.getConfigSetting(ConfigConstants.RESET_PASSWORD_CONFIRM_RESTENDPOINT, 0, TaxonomyUtil.GOORU_ORG_UID);

		userService.validatePassword(password, identity.getUser().getUsername());
		String encryptedPassword = this.getUserService().encryptPassword(password);
		identity.setLastLogin(new Date(System.currentTimeMillis()));
		Credential credential = identity.getCredential();
		credential.setPassword(encryptedPassword);
		credential.setToken(newGenereatedToken);
		identity.setCredential(credential);
		this.getUserRepository().save(identity);

		if (!flag) {
			if (identity.getUser().getAccountTypeId() == null || identity.getUser().getAccountTypeId().equals(UserAccountType.ACCOUNT_PARENT) || identity.getUser().getAccountTypeId().equals(UserAccountType.ACCOUNT_NON_PARENT)) {
				mailHandler.sendMailToConfirmPasswordChanged(identity.getUser().getGooruUId(), password, true, resetPasswordConfirmRestendpoint, null);
			}
			if (identity.getUser().getAccountTypeId() != null && identity.getUser().getAccountTypeId().equals(UserAccountType.ACCOUNT_CHILD)) {
				mailHandler.sendMailToResetPassword(identity.getUser().getGooruUId(), password, true, resetPasswordConfirmRestendpoint, null);
			}
		}

		jsonmodel.addObject(MODEL, serializeToJson(identity));

		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE_PASSWORD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/user/reset/resetToken/check.{format}")
	public ModelAndView resetCredential(HttpServletRequest request, @PathVariable(value = FORMAT) String format, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = TOKEN) String token, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, RESET_CREDENTIAL_CHK);
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		JSONObject resultsJSON = new JSONObject();

		resultsJSON.put(TOKEN_EXPIRED, this.getUserService().hasResetTokenValid(token));
		jsonmodel.addObject(MODEL, resultsJSON);
		return jsonmodel;
	}

	/**
	 * Return user information
	 * 
	 * Sample Request - /rest/user/me.json?sessionToken=... Sample Response
	 * 
	 * { emailId: "nitashasingla@gmail.com" firstName: "Nitasha" gooruUId:
	 * "493e5db8-1b03-4b26-a014-44bed8514fda" lastName: "Singla" }
	 * 
	 * @param sessionToken
	 *            - session token that authenticates the user
	 * @param request
	 *            - HttpServletRequest object
	 * @param response
	 *            - HttpServletResponse object
	 * @return
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_INFO })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/user/me.{format}")
	public ModelAndView userInfo(HttpServletRequest request, @PathVariable(value = FORMAT) String format, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_INFO);
		User user = this.getUserService().findByToken(sessionToken);

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		if (format.equalsIgnoreCase(FORMAT_JSON)) {

			jsonmodel.addObject(MODEL, serializeToJson(user));

		} else if (format.equalsIgnoreCase(FORMAT_XML)) {
			XStream xstream = new XStream();
			xstream.alias(USER, User.class);
			user.setUserRole(null);
			user.setUserId(null);
			String userXML = xstream.toXML(user);
			jsonmodel.addObject(MODEL, userXML);
		}
		return jsonmodel;
	}

	/**
	 * Return list of users in the application
	 * 
	 * @param format
	 *            - format of the response (currently only supports JSON)
	 * @param sessionToken
	 *            - session token that authenticates the user
	 * @param request
	 *            - HttpServletRequest object
	 * @param response
	 *            - HttpServletResponse object
	 * @return
	 */

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_LIST })
	@RequestMapping(method = RequestMethod.GET, value = "/users.{format}")
	public ModelAndView getUsers(@PathVariable(value = FORMAT) String format, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_GET_USERS);
		ModelAndView mView = new ModelAndView(REST_MODEL);

		List<Identity> identityList = this.getUserService().findAllIdentities();

		if (format.equals(FORMAT_JSON)) {
			mView.addObject(MODEL, serializeToJson(identityList));
		}
		return mView;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/user/{userId}/profile/ageCheck.{format}")
	public ModelAndView getAgeCheck(HttpServletRequest request, @PathVariable(value = FORMAT) String format, @PathVariable(value = USER_ID) String userId, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken,

	HttpServletResponse response) {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_GET_AGE_CHK);
		ModelAndView mView = new ModelAndView(REST_MODEL);

		User user = this.getUserService().findByGooruId(userId);

		int value = this.getUserService().findAgeCheck(user);

		if (format.equals(FORMAT_JSON)) {
			mView.addObject(MODEL, value);
		}

		return mView;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/user/{userId}/profile/ageCheck.{format}")
	public ModelAndView updateAgeCheck(HttpServletRequest request, @PathVariable(value = FORMAT) String format, @PathVariable(value = USER_ID) String userId, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = AGE_CHK) String ageCheck,
			HttpServletResponse response) {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_UPDATE_AGE_CHK);
		ModelAndView mView = new ModelAndView(REST_MODEL);

		User user = this.getUserService().findByGooruId(userId);

		this.getUserService().updateAgeCheck(user, ageCheck);

		if (format.equals(FORMAT_JSON)) {
			mView.addObject(MODEL, ageCheck);
		}

		return mView;
	}

	/**
	 * Registers a users in the system and sends a welcome mail
	 * 
	 * @param format
	 *            - format of the response (currently only JSON supported)
	 * @param emailId
	 *            - emailid of the user
	 * @param userName
	 *            - first name of the user
	 * @param sessionToken
	 *            - session token that authenticates the user
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/user/register.{format}")
	public ModelAndView registerUser(@PathVariable(value = FORMAT) String format, @RequestParam(value = EMAIL_ID) final String emailId, @RequestParam(value = USERNAME) final String userName, @RequestParam(value = MAIL, required = false) final String mail,
			@RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_REG);
		ModelAndView mView = new ModelAndView(REST_MODEL);

		boolean isRegistered = this.getUserService().findRegisteredUser(emailId);

		Calendar currenttime = Calendar.getInstance();
		java.sql.Date sqldate = new java.sql.Date((currenttime.getTime()).getTime());

		if (isRegistered) {
			mView.addObject(MODEL, "\"User already Exists\"");
			// return mView;
		} else {
			this.getUserService().registerUser(emailId, sqldate.toString());
			mView.addObject(MODEL, "\"User Has been registered.\"");
		}

		if (mail != null && !mail.equals("")) {
			Date date = new Date((currenttime.getTime()).getTime());
			SimpleDateFormat sdf;

			sdf = new SimpleDateFormat(MMMM);
			String month = sdf.format(date);
			sdf = new SimpleDateFormat(YYYY);
			String year = sdf.format(date);

			String welcomeMailUrl = mail;// this.getServerConstants().getProperty("welcome.mail.url");

			ClientResource resource = new ClientResource(welcomeMailUrl + "?name=" + userName + "&email=" + emailId);
			resource.get();
			StringBuffer welcomeMail = new StringBuffer();
			if (resource.getStatus().isSuccess() && resource.getResponseEntity().isAvailable()) {
				welcomeMail.append(resource.getResponseEntity().getText());
			}

			Properties props = new Properties();
			props.put(MAIL_TRANSPORT_PROTOCOL, SMTP);
			props.put(MAIL_SMTP_HOST, SMTP_GMAIL_COM);
			props.put(MAIL_SMTP_START_TLS_ENABLE, TRUE);
			props.put(MAIL_SMTP_PORT, FOUR_SIXTY_FIVE);
			props.put(MAIL_SMTP_AUTH, TRUE);
			props.put(MAIL_SMTP_SKT_FACTORY_PORT, FOUR_SIXTY_FIVE);
			props.put(MAIL_SMTP_SKT_FACTORY_CLASS, JAVAX_NET_SSL_SSLSKT_FACTORY);
			props.put(MAIL_SMTP_SKT_FACTORY_FALLBK, FALSE);
			props.setProperty(MAIL_SMTP_QUIT_WAIT, FALSE);

			Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

			Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(SUPPORT_EDNOVO_ORG, SUPPORT_1234);
				}
			});

			Transport transport = session.getTransport();

			Message msg = new MimeMessage(session);

			msg.setFrom(new InternetAddress(SUPPORT_EDNOVO_ORG, GOORU_TEAM));
			msg.setRecipient(Message.RecipientType.TO, new InternetAddress(emailId));
			msg.setSubject("Gooru Launch Invitation | " + month + " " + year);
			msg.setContent(welcomeMail.toString(), TEXT_HTML);
			transport.connect(SUPPORT_EDNOVO_ORG, SUPPORT_1234);
			transport.sendMessage(msg, msg.getAllRecipients());
			transport.close();
			Identity identity = userService.findByEmail(emailId);
			if (identity != null) {
				indexProcessor.index(identity.getUser().getPartyUid(), IndexProcessor.INDEX, USER);
			}
		}

		return mView;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/user/{publisherUserId}/publisher-request/accept")
	public ModelAndView addPublisherRole(HttpServletRequest request, @PathVariable(value = PUBLISHER_USER_ID) String publisherUserId, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_UPDATE_USER_ROLE);
		ModelAndView mView = new ModelAndView(REST_MODEL);
		User user = this.getUserService().updateUserRole(publisherUserId, UserRoleType.PUBLISHER);
		mView.addObject(MODEL, serializeToJson(user));
		return mView;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/user/{publisherUserId}/publisher-request/deny")
	public ModelAndView updateUserDenyRole(HttpServletRequest request, @PathVariable(value = PUBLISHER_USER_ID) String publisherUserId, HttpServletResponse response) {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_UPDATE_USER_DENY_ROLE);
		ModelAndView mView = new ModelAndView(REST_MODEL);
		User user = this.getUserService().findByGooruId(publisherUserId);
		Profile profile = this.getUserService().getProfile(user);
		profile.setIsPublisherRequestPending(0);
		this.getUserRepository().save(profile);
		mView.addObject(MODEL, SUCCESS);
		return mView;
	}

	/**
	 * Signs in the user into application. A session token is returned in
	 * response to a valid login
	 * 
	 * @param request
	 * @param format
	 * @param userName
	 * @param password
	 * @param apiKey
	 * @param isGuestUser
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_SIGNIN })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.POST }, value = "/account/signin.{format}")
	public ModelAndView signin(HttpServletRequest request, @PathVariable(value = FORMAT) String format, @RequestParam(value = USERNAME, required = false) final String userName, @RequestParam(value = PASSWORD, required = false) final String password,
			@RequestParam(value = API_KEY, required = true) String apiKey, @RequestParam(value = IS_GUEST_USER, required = false, defaultValue = FALSE) boolean isGuestUser, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_LOGIN);

		ModelAndView mView = new ModelAndView(REST_MODEL);

		try {
			ApiKey apiKeyObj = apiTrackerService.getApiKey(apiKey);
			if (apiKeyObj == null) {
				throw new BadCredentialsException("error:Invalid API Key.");
			}
			UserToken userToken = null;
			if (isGuestUser) {

				Organization org = apiKeyObj.getOrganization();
				String partyUid = org.getPartyUid();
				String anonymousUid = organizationSettingRepository.getOrganizationSetting(Constants.ANONYMOUS, partyUid);
				userToken = userService.createSessionToken(userService.findByGooruId(anonymousUid), request.getSession().getId(), apiKeyObj);
			} else {
				userToken = this.getUserService().signIn(userName, password, apiKey, request.getSession().getId(), false);
			}
			User newUser = (User) BeanUtils.cloneBean(userToken.getUser());
			Identity identity = this.getUserRepository().findUserByGooruId(newUser.getGooruUId());
			String loginType = null;
			if (identity != null) {
				newUser.setEmailId(identity.getExternalId());
				loginType = identity.getLoginType();
			}
			newUser.setUserRoleSet(newUser.getUserRoleSet());
			request.getSession().setAttribute(Constants.USER, newUser);
			request.getSession().setAttribute(Constants.SESSION_TOKEN, userToken.getToken());

			// To capture activity log
			SessionContextSupport.putLogParameter(EVENT_NAME, USER_SIGN_IN);
			SessionContextSupport.putLogParameter(CUR_SESSION_TOKEN, userToken.getToken());
			SessionContextSupport.putLogParameter(USER_ID, newUser.getUserId());
			SessionContextSupport.putLogParameter(GOORU_UID, newUser.getGooruUId());

			RequestUtil.setCookie(request, response, GOORU_SESSION_TOKEN, userToken.getToken());

			if (format.equals(FORMAT_JSON)) {
				JSONObject resultsJSON = serializeToJsonObject(newUser);
				mView.addObject(MODEL, resultsJSON.put(TOKEN, userToken.getToken()).put(USER_ROLESET_STR, newUser.getUserRoleSetString()).put(CREATED_ON, newUser.getCreatedOn()).put(LOGIN_TYPE, loginType));
			}
		} catch (RuntimeException ex) {
			logger.error("Error While Signing in : ", ex);
			mView.addObject(MODEL, ex.getMessage());
		}

		return mView;
	}

	/**
	 * Sign-out the user from Gooru. Invalidates session.
	 * 
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_SIGNOUT })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/account/signout.{format}")
	public ModelAndView signout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, GOORU_LOG_OUT);
		String sessionToken = request.getParameter(SESSIONTOKEN);
		userService.signout(sessionToken);
		SessionContextSupport.putLogParameter(EVENT_NAME, USER_SIGN_OUT);
		request.getSession().invalidate();
		RequestUtil.deleteCookie(request, response, GOORU_SESSION_TOKEN);
		return null;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/account/user/register.{format}")
	public ModelAndView signup(@RequestParam(value = FIRST_NAME) String firstName, @RequestParam(value = LAST_NAME) String lastName, @RequestParam(value = USERNAME) String userName, @RequestParam(value = EMAIL_ID) String emailId, @RequestParam(value = PASSWORD) String password,
			@RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = ORGANIZATION_CODE) String accountCodeName, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, GOORU_USER_REG);

		JSONObject errorJSON = new JSONObject();
		ModelAndView mView = new ModelAndView(REST_MODEL);

		boolean usernameAvailability = this.getUserService().checkUserAvailability(userName, CheckUser.BYUSERNAME, false);
		if (usernameAvailability) {
			errorJSON.put(ERROR, "Someone already has taken " + userName + "!.Please pick another username.");
			mView.addObject(MODEL, errorJSON);
			return mView;
		}
		boolean emailidAvailability = this.getUserService().checkUserAvailability(emailId, CheckUser.BYEMAILID, false);
		if (emailidAvailability) {
			errorJSON.put(ERROR, "The email address specified already exists within Gooru. Please use sign-in to log in to your existing account.");
			mView.addObject(MODEL, errorJSON);
			return mView;
		}

		User user = this.getUserService().createUser(firstName, lastName, emailId, password, null, userName, null, accountCodeName, 0, null, null, null, null, null, null, null, null, null, null, null);

		JSONObject resultsJSON = serializeToJsonObject(user);

		mView.addObject(MODEL, resultsJSON);

		return mView;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_SIGNIN })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.POST }, value = "/loginas/{gooruUid}.{format}")
	public ModelAndView loginAs(@PathVariable(value = _GOORU_UID) String gooruUid, @PathVariable String format, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = IS_REFERENCE_ID, required = false, defaultValue = FALSE) Boolean isReferenceId,
			@RequestParam(value = API_KEY, required = false) String apiKey, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_LOGIN_AS);
		UserToken userToken = this.getUserService().loginAs(sessionToken, gooruUid, apiKey, isReferenceId);
		ModelAndView mView = new ModelAndView(REST_MODEL);
		try {
			if (userToken == null) {
				throw new BadCredentialsException("error:Invalid API Key/Session Token.");
			} else {
				SessionContextSupport.putLogParameter(EVENT_NAME, _USER_LOGIN_AS);
				SessionContextSupport.putLogParameter(CURRENT_SESSION_TOKEN, userToken.getToken());
				SessionContextSupport.putLogParameter(GOORU_UID, userToken.getUser().getGooruUId());
				request.getSession().setAttribute(Constants.USER, userToken.getUser());
				request.getSession().setAttribute(Constants.SESSION_TOKEN, userToken.getToken());
			}

			if (format.equals(FORMAT_JSON)) {
				JSONObject resultsJSON = serializeToJsonObject(userToken.getUser());
				mView.addObject(MODEL, resultsJSON.put(TOKEN, userToken.getToken()).put(USER_ROLESET_STR, userToken.getUser().getUserRoleSetString()));
			}
		} catch (Exception ex) {
			logger.error("Error While Signing in : ", ex);
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			mView.addObject(MODEL, ex.getMessage());
		}
		return mView;

	}

	/**
	 * Sends a confirmation mail
	 * 
	 * Sample Request - /user/register/confirm/mail
	 * 
	 * @param format
	 *            - format of the response (currently only JSON supported)
	 * @param emailid
	 *            - emailid of the user
	 * @param username
	 *            - first name of the user
	 * @param sessionToken
	 *            - session token that authenticates the user
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_CONFIRM_MAIL })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/user/register/confirm/mail.{format}")
	public ModelAndView sendRegistrationConfirmation(@PathVariable(value = FORMAT) String format, @RequestParam(value = _GOORU_UID) final String gooruUid, @RequestParam(value = ACCOUNTTYPE, required = false) String accountType,
			@RequestParam(value = DATEOFBIRTH, required = false) String dateOfBirth, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = GOORU_CLASSIC_URL, required = false) String gooruClassicUrl, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_REG_MAIL);
		String sessionId = request.getSession().getId();
		logger.warn("Sending Registration confirmation for " + gooruUid);
		userService.sendUserRegistrationConfirmationMail(gooruUid, accountType, sessionId, dateOfBirth, gooruClassicUrl);
		return null;
	}

	/**
	 * Gets availability of a username or email id.
	 * 
	 * @param request
	 * @param format
	 * @param keyword
	 * @param type
	 *            Type of field to check. Valid values are "byUsername" and
	 *            "byEmail
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_CHECK_IF_USER_EXISTS })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.GET }, value = "/user/check/availability.{format}")
	public ModelAndView getUserAvailability(HttpServletRequest request, @PathVariable(value = FORMAT) String format, @RequestParam(value = KEYWORD) final String keyword, @RequestParam(value = COLLECTION_ID, required = false) String collectionId,
			@RequestParam(value = TYPE, defaultValue = BY_USER_NAME) String type, @RequestParam(value = IS_COLLABORATOR_CHK, defaultValue = FALSE, required = false) boolean isCollaboratorCheck, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_CHK_USER_NAME_OR_EMAIL_ID_AVAILABILITY);
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		User user = (User) request.getAttribute(Constants.USER);
		Map<String, Object> userInfo = this.getUserService().getUserAvailability(keyword, type, isCollaboratorCheck, collectionId, user);
		if (!userInfo.isEmpty()) {
			jsonmodel.addObject(MODEL, serializeToJsonObject(userInfo));
		}

		SessionContextSupport.putLogParameter(RATING_EVENT_NAME, CHK_AVAILABILITY);
		SessionContextSupport.putLogParameter(TYPE, type);

		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_INFO })
	@RequestMapping(method = RequestMethod.GET, value = "/user/profile/completion/status.{format}")
	public ModelAndView profileCompletionStatus(@PathVariable(value = FORMAT) String format, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_PROFILE_STATUS);
		User user = (User) request.getAttribute(Constants.USER);
		Profile profile = this.getUserService().getProfile(user);
		boolean profileStatus = true;
		ModelAndView mView = new ModelAndView(REST_MODEL);

		if (profile == null || profile.getUser() == null) {
			profileStatus = false;
		} else {
			if (profile.getUser().getIdentities() != null) {
				Iterator<Identity> iter = profile.getUser().getIdentities().iterator();
				String emailId = iter.next().getExternalId();
				if (emailId == null || emailId.isEmpty()) {
					profileStatus = false;
				}
			}
			if (profile != null && profile.getUser() != null && profile.getUser().getFirstName() == null || profile.getUser().getFirstName().isEmpty() || profile.getUser().getLastName() == null || profile.getUser().getLastName().isEmpty() || profile.getUser().getUsername() == null
					|| profile.getUser().getUsername().isEmpty() || profile.getGender() == null || profile.getGender().getName() == null || profile.getGender().getName().isEmpty() || profile.getUserType() == null || profile.getUserType().isEmpty()) {
				profileStatus = false;
			}
		}
		if (format.equals(FORMAT_JSON)) {
			JSONObject resultsJSON = new JSONObject();
			mView.addObject(MODEL, resultsJSON.put(PROFILE_STATUS, profileStatus));
		}

		SessionContextSupport.putLogParameter(RATING_EVENT_NAME, _PROFILE_STATUS);
		SessionContextSupport.putLogParameter(USER_ID, user.getUserId());
		SessionContextSupport.putLogParameter(GOORU_UID, user.getPartyUid());

		return mView;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.GET }, value = "/user/content/{gooruContentId}/check-access.{format}")
	public ModelAndView checkContentAccess(HttpServletRequest request, @PathVariable(value = FORMAT) String format, @PathVariable(value = GOORU_CONTENT_ID) String gooruContentId, HttpServletResponse response) throws Exception {

		User authenticatedUser = (User) request.getAttribute(Constants.USER);

		Set<String> permissions = new HashSet<String>();

		if (authenticatedUser != null) {
			Content content = this.getContentService().findContentByGooruId(gooruContentId, true);
			if (content != null) {
				// For Collaborator get whatever access that is granted
				Set<ContentPermission> contentPermissions = content.getContentPermissions();
				for (ContentPermission userPermission : contentPermissions) {

					if (userPermission.getParty().getPartyUid().equals(authenticatedUser.getPartyUid())) {
						permissions.add(EDIT);
						break;
					}
				}
				// For Owner provide full access
				if (authenticatedUser.getGooruUId().equals(content.getUser().getGooruUId())) {
					permissions.add(EDIT);
					permissions.add(DELETE);
				}

				// If content access is public, allow read-only access for
				// anyone
				if (Sharing.PUBLIC.getSharing().equalsIgnoreCase(content.getSharing())) {
					permissions.add(VIEW);
				}

				// Check for full content access
				if (hasUnrestrictedContentAccess()) {
					permissions.add(EDIT);
					permissions.add(DELETE);
				}
				// Check for sub organization content access
				/*
				 * if(hasSubOrgPermission(content.getOrganization().getPartyUid()
				 * )){ permissions.add("edit"); }
				 */
			}
		}

		request.setAttribute(Constants.EVENT_PREDICATE, USER_CHK_CONTENT_ACCESS);
		JSONObject accessDetailsJSON = new JSONObject();
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		accessDetailsJSON.put(PERMISSIONS, permissions);
		jsonmodel.addObject(MODEL, accessDetailsJSON);

		SessionContextSupport.putLogParameter(RATING_EVENT_NAME, CHK_CONTENT_ACCESS);
		SessionContextSupport.putLogParameter(GOORU_CONTENT_ID, gooruContentId);
		SessionContextSupport.putLogParameter(USER_ID, authenticatedUser.getUserId());
		SessionContextSupport.putLogParameter(GOORU_UID, authenticatedUser.getPartyUid());

		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.GET }, value = "/user/content/{gooruContentId}/check-permission.{format}")
	public ModelAndView checkContentPermission(HttpServletRequest request, @PathVariable(value = FORMAT) String format, @PathVariable(value = GOORU_CONTENT_ID) String gooruContentId, HttpServletResponse response) throws Exception {

		User authenticatedUser = (User) request.getAttribute(Constants.USER);

		String permission = "";

		if (authenticatedUser != null) {
			Content content = this.getContentRepository().findContentByGooruId(gooruContentId, true);
			if (content != null) {
				// For Owner provide full access
				if (authenticatedUser.equals(content.getUser())) {
					permission = OWNER;
				}
				if (permission.length() == 0) {
					// For Collaborator get whatever access that is granted
					Set<ContentPermission> contentPermissions = content.getContentPermissions();
					for (ContentPermission userPermission : contentPermissions) {
						if (userPermission.getParty().getPartyUid().equals(authenticatedUser.getPartyUid())) {
							permission = userPermission.getPermission();
							break;
						}
					}
					if (permission.length() == 0) {
						// If content access is public, allow read-only access
						// for anyone
						if (Sharing.PUBLIC.getSharing().equalsIgnoreCase(content.getSharing())) {
							permission = VIEW;
						}

						// Check for full content access
						if (hasUnrestrictedContentAccess()) {
							permission = ALL;
						}
					}
				}
			}
		}

		request.setAttribute(Constants.EVENT_PREDICATE, USER_CHK_CONTENT_PERMISSION);
		JSONObject accessDetailsJSON = new JSONObject();
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		accessDetailsJSON.put("permission", permission);
		jsonmodel.addObject(MODEL, accessDetailsJSON);

		SessionContextSupport.putLogParameter(RATING_EVENT_NAME, CHK_CONTENT_PERMISSION);
		SessionContextSupport.putLogParameter(GOORU_CONTENT_ID, gooruContentId);
		SessionContextSupport.putLogParameter(USER_ID, authenticatedUser.getUserId());
		SessionContextSupport.putLogParameter(GOORU_UID, authenticatedUser.getPartyUid());

		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_LIST })
	@RequestMapping(method = { RequestMethod.GET }, value = "/user")
	public ModelAndView listUsers(HttpServletRequest request, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_LIST_USERS);
		return toModelAndView(serializeToJson(this.getUserService().listUsers()));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.POST }, value = "/user")
	public ModelAndView createUser(HttpServletRequest request, @RequestParam(value = FIRST_NAME) String firstName, @RequestParam(value = LAST_NAME) String lastName, @RequestParam(value = USERNAME) String userName, @RequestParam(value = EMAIL_ID) String emailId,
			@RequestParam(value = ORGANIZATION_CODE, required = true) String organizationCode, @RequestParam(value = PASSWORD, required = false) String password, @RequestParam(value = USEGENERATEDPASSWORD, required = false, defaultValue = FALSE) Boolean useGeneratedPassword,
			@RequestParam(value = SENDCONFIRMATIONMAIL, required = false, defaultValue = TRUE) Boolean sendConfirmationMail, @RequestParam(value = ACCOUNTTYPE, required = false) String accountType, @RequestParam(value = DATEOFBIRTH, required = false) String dateOfBirth,
			@RequestParam(value = CHILDDOB, required = false) String childDOB, @RequestParam(value = USERPARENTID, required = false) String userParentId, @RequestParam(value = GENDER, required = false) String gender,
			@RequestParam(value = CHILDFLAG, required = false, defaultValue = FALSE) Boolean childFlag, @RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken,
			@RequestParam(value = GOORU_CLASSIC_URL, required = false) String gooruClassicUrl, @RequestParam(value = TOKEN, required = false, defaultValue = FALSE) Boolean token, @RequestParam(value = REFERENCE_UID, required = false) String referenceUid,
			@RequestParam(value = ROLE, required = false) String role, @RequestParam(value = PEARSON_EMAIL_ID, required = false) String pearsonEmailId, HttpServletResponse response) throws Exception {

		request.setAttribute(Constants.EVENT_PREDICATE, USER_CREATE_USER);

		String sessionId = request.getSession().getId();

		User apiCaller = (User) request.getAttribute(Constants.USER);
		if (childFlag) {
			dateOfBirth = dateOfBirth != null ? dateOfBirth.replace("d", "/") : dateOfBirth;
		}

		// Check user organization permission
		if (organizationCode != null && apiCaller != null && !organizationCode.equalsIgnoreCase(GOORU)) {
			userService.validateUserOrganization(organizationCode);
		}

		Map<String, String> errors = userService.validateUserAdd(firstName, lastName, emailId, password, userName, apiCaller, childDOB, accountType, dateOfBirth, organizationCode);
		User user = null;

		if (errors.size() == 0) {
			user = this.getUserService().createUserWithValidation(firstName, lastName, emailId, password, null, userName, null, organizationCode, useGeneratedPassword, sendConfirmationMail, apiCaller, accountType, dateOfBirth, userParentId, sessionId, gender, childDOB, gooruClassicUrl,
					referenceUid, role, pearsonEmailId);
		}

		JSONObject jsonObj = new JSONObject();

		if (user != null) {
			jsonObj.put(USER, serializeToJsonObject(user));
			if (token) {
				Identity identity = this.getUserService().findUserByGooruId(user.getGooruUId());
				jsonObj.put(TOKEN, identity.getCredential().getToken());
			}
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		if (!errors.isEmpty()) {
			jsonObj.put(ERRORS, serializeToJsonObject(errors));
		}

		return toModelAndView(jsonObj);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.GET }, value = "/usertoken/user")
	public ModelAndView getUserByToken(HttpServletRequest request, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_GET_USER_BY_TOKEN);
		UserToken userToken = this.getUserTokenService().findByToken(sessionToken);
		Iterator<Identity> identity = userToken.getUser().getIdentities().iterator();
		while (identity.hasNext()) {
			Identity identit = identity.next();
			userToken.getUser().setEmailId(identit.getExternalId());
		}

		return toModelAndView(userToken.getUser(), BaseController.FORMAT_JSON);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.GET }, value = "/user/{userId}")
	public ModelAndView getUser(HttpServletRequest request, @PathVariable(value = USER_ID) String userId, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format,
			@RequestParam(value = EXCLUDE_ENTITY_OPERATION, required = false, defaultValue = FALSE) boolean excludeEntityOperation, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_GET_USER);
		User user = this.getUserService().getUser(userId);
		JSONObject json = new JSONObject();
		if (user != null) {
			JSONObject jsonObj = new JSONObject();
			List<RoleEntityOperation> operations = userService.getUserOperations(user.getUserRoleSetString());
			jsonObj.put(USER, serializeToJsonObject(user));
			JSONArray usersList = new JSONArray(serializeToJson(operations));
			if (excludeEntityOperation) {

				json.put(DATA_OBJECT, jsonObj);
			} else {
				jsonObj.put(OPERATIONS, usersList);
				json.put(DATA_OBJECT, jsonObj);
			}
		}
		return toModelAndView(json);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE }, partyOperations = { GooruOperationConstants.ORG_ADMIN, GooruOperationConstants.GROUP_ADMIN }, partyUId = USER_ID)
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.PUT }, value = "/user/{userId}")
	public ModelAndView updateUser(HttpServletRequest request, @PathVariable(value = USER_ID) String userId, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = IS_DISABLE_USER, required = false, defaultValue = FALSE) Boolean isDisableUser,
			@RequestBody MultiValueMap<String, String> body, @RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format, @RequestParam(value = ACCOUNTTYPE, required = false) String accountType, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_UPDATE_USER);
		User apiCaller = null;

		if (accountType != null) {
			if (accountType.equalsIgnoreCase(UserAccountType.userAccount.PARENT.getType()) || accountType.equalsIgnoreCase(UserAccountType.userAccount.CHILD.getType()) || accountType.equalsIgnoreCase(UserAccountType.userAccount.NON_PARENT.getType())) {
				apiCaller = this.getUserService().findByToken(sessionToken);

			}
		} else {
			apiCaller = (User) request.getAttribute(Constants.USER);
		}

		ModelAndView mView = new ModelAndView(REST_MODEL);

		try {
			Profile profile = this.getUserService().updateUserInfo(userId, body, apiCaller, isDisableUser);
			if (format.equals(FORMAT_JSON)) {
				JSONObject resultsJSON = serializeToJsonObject(profile);
				mView.addObject(MODEL, resultsJSON);
			}
		} catch (RuntimeException ex) {
			logger.error("Error While Register: ", ex);
			mView.addObject(MODEL, ex.getMessage());
		}
		return mView;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_DELETE }, partyOperations = { GooruOperationConstants.ORG_ADMIN, GooruOperationConstants.GROUP_ADMIN }, partyUId = USER_ID)
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.DELETE }, value = "/user/{userId}")
	public ModelAndView deleteUser(HttpServletRequest request, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @PathVariable(value = USER_ID) String userId, @RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format, HttpServletResponse response)
			throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_DEL_USER);

		String isDeleted = this.getUserService().deleteUser(userId);

		return toModelAndView(isDeleted, format);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE }, partyOperations = { GooruOperationConstants.ORG_ADMIN, GooruOperationConstants.GROUP_ADMIN }, partyUId = USER_ID)
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.PUT }, value = "/user/{userId}/role")
	public ModelAndView grantUserRole(HttpServletRequest request, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @PathVariable(value = USER_ID) String userId, @RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format,
			@RequestParam(value = ROLES) String roles, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_UPDATE_USER_ROLE);
		User apiCaller = (User) request.getAttribute(Constants.USER);
		return toModelAndView(serializeToJson(userService.grantUserRole(userId, roles, apiCaller), "*.userRoleSetString"));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE }, partyOperations = { GooruOperationConstants.ORG_ADMIN, GooruOperationConstants.GROUP_ADMIN }, partyUId = USER_ID)
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.DELETE }, value = "/user/{userId}/{roles}")
	public ModelAndView revokeUserRole(HttpServletRequest request, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @PathVariable(value = USER_ID) String userId, @PathVariable(value = ROLES) String roles,
			@RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_REMOVE_USER_ROLE);
		User apiCaller = (User) request.getAttribute(Constants.USER);
		return toModelAndView(serializeToJson(userService.revokeUserRole(userId, roles, apiCaller)));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_INFO })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.GET }, value = "/user/{userId}/registration")
	public ModelAndView getRegisterUserInfo(HttpServletRequest request, @PathVariable(value = USER_ID) String userId, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format, HttpServletResponse response)
			throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, USER_GET_USER);

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		Map<String, Object> registerUserInfo = this.getUserService().getRegisterUserInfo(userId);
		if (!registerUserInfo.isEmpty()) {
			jsonmodel.addObject(MODEL, serializeToJsonObject(registerUserInfo));
		}
		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE }, partyOperations = { GooruOperationConstants.ORG_ADMIN, GooruOperationConstants.GROUP_ADMIN }, partyUId = GOORU_UID)
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/user/{gooruUId}/confirmStatus")
	public ModelAndView updateUserConfirmStatus(@PathVariable(value = GOORU_UID) String gooruUId, @RequestParam(value = CONFIRM_STATUS) Integer confirmStatus, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		User apiCaller = (User) request.getAttribute(Constants.USER);
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put(EVENT_TYPE, CustomProperties.EventMapping.WELCOME_MAIL.getEvent());
		dataMap.put(_GOORU_UID, apiCaller.getGooruUId());
		this.mailHandler.handleMailEvent(dataMap);
		User user = this.getUserService().updateUserConfirmStatus(gooruUId, confirmStatus, apiCaller);
		ModelAndView mView = new ModelAndView(REST_MODEL);
		if (user != null) {
			String userJSON = serializeToJsonWithExcludes(user, new String[] { "*.class", "segment", "*.creator" });
			mView.addObject(MODEL, userJSON.toString());
			return mView;
		} else {
			mView.addObject(MODEL, "Only Admin user can change the status");
			return mView;
		}
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_SIGNIN })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/user/authenticate")
	public ModelAndView authenticateUser(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String email, @RequestParam String organizationCode, @RequestParam String tokenSecret, @RequestParam String apiKey, @RequestParam(required = false) String source,
			@RequestParam(required = false, defaultValue = FALSE) Boolean flag, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(Constants.EVENT_PREDICATE, "gooru.authenticate_user");
		logger.debug("Received request for user authentication: " + email);

		/*
		 * Step I - Validate that the request is a valid request
		 */
		String token = this.getConfigProperties().getAuthSSO() != null ? this.getConfigProperties().getAuthSSO().get(SECERT_KEY) : null;
		if (!tokenSecret.equals(token)) {

			logger.debug("Invalid authentication request with token: " + tokenSecret);

			return toModelAndView(_ERROR);
		}

		logger.debug("Valid authentication request with right token secret. Setting user object in session and returning sessionid.");

		/*
		 * Step II - Find the user in the database.
		 */
		Identity identity = new Identity();
		identity.setExternalId(email);

		logger.info("this.userRepository : " + this.getUserRepository());

		User userIdentity = this.getUserService().findByIdentity(identity);
		UserToken sessionToken = null;

		String userName = null;
		firstName = StringUtils.remove(firstName, " ");
		userName = firstName;
		if (lastName.length() > 0) {
			userName = userName + lastName.substring(0, 1);
		}

		User user = this.getUserRepository().findUserWithoutOrganization(userName);

		if (user != null && user.getUsername().equals(userName)) {
			Random randomNumber = new Random();
			userName = userName + randomNumber.nextInt(1000);

		}

		if (userIdentity == null) {
			userIdentity = this.getUserService().createUser(firstName, lastName, email, null, "", userName, 1, organizationCode, 0, null, PARENT, null, null, null, null, source, null, null);
			Map<String, String> dataMap = new HashMap<String, String>();
        	dataMap.put(GOORU_UID, user.getGooruUId());
			dataMap.put(EVENT_TYPE, CustomProperties.EventMapping.WELCOME_MAIL.getEvent());
			dataMap.put("recipient", email);
			mailHandler.handleMailEvent(dataMap);
		}

		/*
		 * Step III - Generate token using the session Id
		 */
		sessionToken = this.getUserTokenService().findBySession(request.getSession().getId());

		if (sessionToken == null) {
			sessionToken = this.getUserService().createSessionToken(userIdentity, request.getSession().getId(), apiTrackerService.getApiKey(apiKey));
		}

		logger.debug("User already exists. Setting the token in the user object.");
		request.getSession().setAttribute(Constants.SESSION_TOKEN, sessionToken.getToken());

		/*
		 * Step III - Set the session with the user object. This is the only
		 * place where an authenticated user object is set in the session. All
		 * the other requests assume that a user object exists in the session
		 */
		User newUser = (User) BeanUtils.cloneBean(userIdentity);
		request.getSession().setAttribute(Constants.USER, newUser);
		if (flag) {
			Map<String, String> data = new HashMap<String, String>();
			data.put(TOKEN, sessionToken.getToken());
			data.put(EMAIL, email);
			return toModelAndView(serialize(data, JSON));
		} else {
			return toModelAndView(sessionToken.getToken());
		}

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_UPDATE }, partyOperations = { GooruOperationConstants.ORG_ADMIN, GooruOperationConstants.GROUP_ADMIN }, partyUId = GOORU_UID)
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.POST, RequestMethod.GET }, value = "/user/{gooruUId}/view/flag")
	public ModelAndView updateUserViewFlag(@PathVariable(value = GOORU_UID) String gooruUId, @RequestParam(value = VIEW_FLAG) Integer viewFlag, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return toModelAndView(serializeToJsonWithExcludes(this.getUserService().updateViewFlagStatus(gooruUId, viewFlag), new String[] { "*.class", "segment", "*.creator" }));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/check/time")
	public ModelAndView getSystemCurrentTime(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return toModelAndView(this.getUserService().getSystemCurrentTime());
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_USER_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.POST }, value = "/user/thirdPartySignin")
	public ModelAndView partnerSignin(HttpServletRequest request, @RequestParam(value = EMAIL_ID) String emailId, @RequestParam(value = PASSWORD, required = true) String password, @RequestParam(value = API_KEY, required = true) String apiKey,
			@RequestParam(value = SIGNATURE, required = true) String signature, @RequestParam(value = expire, required = true) Long expires, @RequestParam(value = FORMAT, defaultValue = FORMAT_JSON) String format, HttpServletResponse response) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put(EMAIL_ID, emailId);
		paramMap.put(PASSWORD, password);
		paramMap.put(API_KEY, apiKey);
		paramMap.put(SIGNATURE, signature);
		request.setAttribute(Constants.EVENT_PREDICATE, USER_CREATE_USER);
		String url = request.getRequestURL().toString();
		String sessionId = request.getSession().getId();
		return toModelAndView(serializeToJson(userService.partnerSignin(paramMap, sessionId, url, expires)));
	}

	public BaseRepository getBaseRepository() {
		return baseRepository;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public UserService getUserService() {
		return userService;
	}

	public LearnguideRepository getClassplanRepository() {
		return classplanRepository;
	}

	public Properties getServerConstants() {
		return serverConstants;
	}

	public SubscriptionRepository getSubscriptionRepository() {
		return subscriptionRepository;
	}

	public Properties getConfigConstants() {
		return configConstants;
	}

	public UserTokenRepository getUserTokenRepository() {
		return userTokenRepository;
	}

	public ActivityRepository getActivityRepository() {
		return activityRepository;
	}

	public ContentRepository getContentRepository() {
		return contentRepository;
	}

	public LearnguideService getLearnguideService() {
		return learnguideService;
	}

	public SubscriptionService getSubscriptionService() {
		return subscriptionService;
	}

	public UserContentService getUserContentService() {
		return userContentService;
	}

	public ContentService getContentService() {
		return contentService;
	}

	public UserTokenService getUserTokenService() {
		return userTokenService;
	}

	public PartyService getPartyService() {
		return partyService;
	}

	public RedisService getRedisService() {
		return redisService;
	}

	public ConfigProperties getConfigProperties() {
		return configProperties;
	}

}
