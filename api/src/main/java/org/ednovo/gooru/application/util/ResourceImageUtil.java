/////////////////////////////////////////////////////////////
// ResourceImageUtil.java
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceInfo;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.cassandra.model.ResourceMetadataCo;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.resource.MediaService;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.gooru.domain.service.storage.S3ResourceApiHandler;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy.TaxonomyRespository;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.restlet.data.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResourceImageUtil extends UserGroupSupport implements ParameterProperties {

	@Autowired
	@javax.annotation.Resource(name = "classplanConstants")
	private Properties classPlanConstants;

	@Autowired
	private MediaService mediaService;

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private S3ResourceApiHandler s3ResourceApiHandler;

	@Autowired
	private SettingService settingService;

	@Autowired
	private TaxonomyRespository taxonomyRespository;

	@Autowired
	private IndexProcessor indexProcessor;

	@Autowired
	private AsyncExecutor asyncExecutor;

	private Map<String, String> propertyMap;

	private static final Logger logger = LoggerFactory.getLogger(ResourceImageUtil.class);

	public void sendMsgToGenerateThumbnails(Resource resource) {

		sendMsgToGenerateThumbnails(resource, resource.getThumbnail());
	}

	public void sendMsgToGenerateThumbnails(Resource resource, String fileName) {
		String repoPath = resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(SOURCE_FILE_PATH, repoPath + fileName);
		param.put(TARGET_FOLDER_PATH, repoPath);
		param.put(THUMBNAIL, resource.getThumbnail());
		param.put(DIMENSIONS, getDimensions(resource));
		param.put(RESOURCE_GOORU_OID, resource.getGooruOid());
		param.put(API_END_POINT, settingService.getConfigSetting(ConfigConstants.GOORU_API_ENDPOINT, 0, TaxonomyUtil.GOORU_ORG_UID));
		logger.debug(fileName);
		logger.debug(settingService.getConfigSetting(ConfigConstants.GOORU_CONVERSION_RESTPOINT, 0, TaxonomyUtil.GOORU_ORG_UID) + "/conversion/image");
		this.getAsyncExecutor().executeRestAPI(param, settingService.getConfigSetting(ConfigConstants.GOORU_CONVERSION_RESTPOINT, 0, TaxonomyUtil.GOORU_ORG_UID) + "/conversion/image", Method.POST.getName());
	}

	public void generateThumbnails(Resource resource, String fileName) {
		String repoPath = resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(SOURCE_FILE_PATH, repoPath + fileName);
		param.put(TARGET_FOLDER_PATH, repoPath);
		param.put(THUMBNAIL, resource.getThumbnail());
		param.put(DIMENSIONS, getDimensions(resource));
		param.put(RESOURCE_GOORU_OID, resource.getGooruOid());
		param.put(API_END_POINT, settingService.getConfigSetting(ConfigConstants.GOORU_API_ENDPOINT, 0, TaxonomyUtil.GOORU_ORG_UID));
		this.getAsyncExecutor().executeRestAPI(param, settingService.getConfigSetting(ConfigConstants.GOORU_CONVERSION_RESTPOINT, 0, TaxonomyUtil.GOORU_ORG_UID) + "/conversion/image", Method.POST.getName());
	}

	public void downloadResourceImage(String repoPath, Resource resource, String webSrc) {

		String resourceTypeName = resource.getResourceType().getName();
		if (resource.getThumbnail() == null
				&& (resourceTypeName.equals(ResourceType.Type.EXAM.getType()) || resourceTypeName.equals(ResourceType.Type.PRESENTATION.getType()) || resourceTypeName.equals(ResourceType.Type.HANDOUTS.getType()) || resourceTypeName.equals(ResourceType.Type.TEXTBOOK.getType()))) {
			resource.setThumbnail("slides/slides1.jpg");
			resourceRepository.save(resource);
		}

		String thumbnail = GooruImageUtil.getFileName(GooruImageUtil.downloadWebResourceToFile(webSrc, repoPath, resource.getGooruOid()));

		if (thumbnail != null) {
			resource.setThumbnail(thumbnail);
		}

		resourceRepository.save(resource);

		this.getAsyncExecutor().uploadResourceFolder(resource);

	}

	public void downloadAndGenerateThumbnails(Resource resource, String webSrc) {
		String repoPath = resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder();
		downloadResourceImage(repoPath, resource, webSrc);
		generateThumbnails(resource, resource.getThumbnail());
	}

	public void downloadAndSendMsgToGenerateThumbnails(Resource resource, String webSrc) {
		String repoPath = resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder();
		Map<String, Object> param = new HashMap<String, Object>();
		if (resource.getResourceType() != null && resource.getResourceType().getName().equalsIgnoreCase(ResourceType.Type.HANDOUTS.getType()) && webSrc == null) {
			resource.setThumbnail("slides/slides1.jpg");
			resourceRepository.save(resource);
			param.put(RESOURCE_FILE_PATH, repoPath + resource.getUrl());
			param.put(RESOURCE_GOORU_OID, resource.getGooruOid());
			this.getAsyncExecutor().executeRestAPI(param, settingService.getConfigSetting(ConfigConstants.GOORU_CONVERSION_RESTPOINT, 0, TaxonomyUtil.GOORU_ORG_UID) + "/conversion/pdf-to-image", Method.POST.getName());
		} else {
			downloadResourceImage(repoPath, resource, webSrc);
			param.put(SOURCE_FILE_PATH, repoPath + resource.getThumbnail());
			param.put(TARGET_FOLDER_PATH, repoPath);
			param.put(THUMBNAIL, resource.getThumbnail());
			param.put(DIMENSIONS, getDimensions(resource));
			param.put(RESOURCE_GOORU_OID, resource.getGooruOid());
			this.getAsyncExecutor().executeRestAPI(param, settingService.getConfigSetting(ConfigConstants.GOORU_CONVERSION_RESTPOINT, 0, TaxonomyUtil.GOORU_ORG_UID) + "/conversion/image", Method.POST.getName());
		}
	}

	public void moveFileAndSendMsgToGenerateThumbnails(Resource resource, String fileName, Boolean isUpdateSlideResourceThumbnail) throws IOException {
		String repoPath = UserGroupSupport.getUserOrganizationNfsInternalPath();
		final String mediaFolderPath = repoPath + Constants.UPLOADED_MEDIA_FOLDER;
		final String contentGooruOid = resource.getGooruOid();

		String resourceImageFile = mediaFolderPath + "/" + fileName;
		File mediaImage = new File(resourceImageFile);
		if (!mediaImage.exists() || !mediaImage.isFile()) {
			return;
		}
		if (mediaImage.exists() && resource != null) {

			// get resource internal path
			repoPath = resource.getOrganization().getNfsStorageArea().getInternalPath();

			File resourceFolder = new File(repoPath + "/" + resource.getFolder());
			if (!resourceFolder.exists()) {
				resourceFolder.mkdir();
			}
			File newImage = new File(resourceFolder, contentGooruOid + "_" + fileName);
			if (resource.getThumbnail() != null && !resource.getThumbnail().startsWith(contentGooruOid) && !resource.getThumbnail().contains(GOORU_DEFAULT)) {
				// Collection image exists, but doesn't start with new ID.
				// migrate to new pattern
				File existingImage = new File(resourceFolder, resource.getThumbnail());
				existingImage.delete();
			}
			if (newImage.exists()) {
				newImage.delete();
			}
			FileUtils.moveFile(mediaImage, newImage);

			fileName = newImage.getName();
			String resourceTypeName = resource.getResourceType().getName();
			if (resourceTypeName.equals(ResourceType.Type.PRESENTATION.getType()) || resourceTypeName.equals(ResourceType.Type.HANDOUTS.getType()) || resourceTypeName.equals(ResourceType.Type.TEXTBOOK.getType())) {
				if (isUpdateSlideResourceThumbnail) {
					resource.setThumbnail(fileName);
				} else {
					resource.setThumbnail("slides/slides1.jpg");
				}

			} else {
				resource.setThumbnail(fileName);
			}
			this.setDefaultThumbnailImageIfFileNotExist(resource);
			if (resource.getUrl() != null && !resource.getUrl().toLowerCase().startsWith("http://") && !resource.getUrl().toLowerCase().startsWith("https://") && !resourceTypeName.equalsIgnoreCase(ResourceType.Type.RESOURCE.getType())
					&& !resourceTypeName.equalsIgnoreCase(ResourceType.Type.VIDEO.getType())) {
				if (!isUpdateSlideResourceThumbnail) {
					resource.setUrl(fileName);
				}
			}
		}

		resourceRepository.save(resource);
		resourceRepository.flush();
		if (resource.getResourceType() != null) {
			String indexType = RESOURCE;
			if (resource.getResourceType().getName().equalsIgnoreCase(SCOLLECTION)) {
				indexType = SCOLLECTION;
			}
			indexProcessor.index(resource.getGooruOid(), IndexProcessor.INDEX, indexType);
		}
		sendMsgToGenerateThumbnails(resource);
	}

	public String getDimensions(Resource resource) {
		String resourceTypeName = resource.getResourceType().getName();
		if (resourceTypeName.equalsIgnoreCase(ResourceType.Type.CLASSPLAN.getType()) || resourceTypeName.equalsIgnoreCase(ResourceType.Type.CLASSBOOK.getType()) || resourceTypeName.equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())) {
			return COLLECTION_THUMBNAIL_SIZES;
		} else if (resourceTypeName.equalsIgnoreCase(ResourceType.Type.ASSESSMENT_QUIZ.getType()) || resourceTypeName.equalsIgnoreCase(ResourceType.Type.ASSESSMENT_EXAM.getType())) {
			return QUIZ_THUMBNAIL_SIZES;
		} else {
			return RESOURCE_THUMBNAIL_SIZES;
		}
	}

	public void setDefaultThumbnailImageIfFileNotExist(Resource resource) {
		if (resource != null) {
			boolean setDefault = false;
			if (resource.getThumbnail() == null) {
				setDefault = true;
			} else if (!resource.getThumbnail().equalsIgnoreCase(GOORU_DEFAULT)) {
				String repoPath = resource.getOrganization().getNfsStorageArea().getInternalPath();
				File resourceFolder = new File(repoPath + "/" + resource.getFolder() + "/" + resource.getThumbnail());
				if (!resourceFolder.exists()) {
					setDefault = true;
				}
			}

			if (setDefault) {
				String resourceTypeName = resource.getResourceType().getName();
				if (resourceTypeName.equalsIgnoreCase(ResourceType.Type.CLASSPLAN.getType()) || resourceTypeName.equalsIgnoreCase(ResourceType.Type.CLASSBOOK.getType()) || resourceTypeName.equalsIgnoreCase(ResourceType.Type.ASSESSMENT_QUIZ.getType())
						|| resourceTypeName.equalsIgnoreCase(ResourceType.Type.ASSESSMENT_EXAM.getType())) {
					String defaultThumbnailImages = this.getSettingService().getConfigSetting(ConfigConstants.DEFAULT_IMAGES, 0, resource.getOrganization().getPartyUid());
					if (defaultThumbnailImages != null) {
						List<String> defaultThumbnailImageList = Arrays.asList(defaultThumbnailImages.split("\\s*,\\s*"));
						Integer randomNumber = new Random().nextInt(defaultThumbnailImageList.size());
						resource.setThumbnail((randomNumber != null ? defaultThumbnailImageList.get(randomNumber > 0 ? randomNumber - 1 : randomNumber) : null));
						logger.info("default thumbnail  content id: " + resource.getContentId());
					}
				}
				resourceRepository.save(resource);
			}
		}
	}

	public Map<String, Object> getResourceMetaData(String url, String resourceTitle, boolean fetchThumbnail) {
		Map<String, Object> metaData = new HashMap<String, Object>();
		ResourceMetadataCo resourceFeeds = getYoutubeResourceFeeds(url, null);
		String description = "";
		String title = "";
		String videoDuration = "";
		if (resourceFeeds.getUrlStatus() == 404) {
			Document doc = null;
			try {
				if (url != null && (url.contains("http://") || url.contains("https://"))) {
				  doc = Jsoup.connect(url).timeout(6000).get();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Set<String> images = new LinkedHashSet<String>();
			if (doc != null) {
				title = doc.title();
				Elements meta = doc.getElementsByTag(META);
				if (meta != null) {
					for (Element element : meta) {
						if (element.attr(NAME) != null && element.attr(NAME).equalsIgnoreCase(DESCRIPTION)) {
							description = element.attr(CONTENT);
							break;
						}
					}
				}
				metaData.put(DESCRIPTION, description);
				if (fetchThumbnail) {
					Elements media = doc.select("[src]");
					if (media != null) {
						for (Element src : media) {
							if (src.tagName().equals(IMG)) {
								images.add(src.attr("abs:src"));
							}
							if (images.size() >= SUGGEST_IMAGE_MAX_SIZE) {
								break;
							}
						}
					}
				}
			}
			if (fetchThumbnail) {
				metaData.put(IMAGES, images);
			}
		} else {
			title = resourceFeeds.getTitle();
			description = resourceFeeds.getDescription();
			videoDuration = resourceFeeds.getDuration().toString();
		}
		metaData.put(TITLE, title);
		metaData.put(DESCRIPTION, description);
		metaData.put(VIDEO_DURATION, videoDuration);
		return metaData;
	}

	public static String getYoutubeVideoId(String url) {
		String pattern = "youtu(?:\\.be|be\\.com)/(?:.*v(?:/|=)|(?:.*/)?)([a-zA-Z0-9-_]{11}+)";
		String videoId = null;
		Pattern compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = compiledPattern.matcher(url);
		if (matcher != null) {
			while (matcher.find()) {
				videoId = matcher.group(1);
			}
		}
		return videoId;
	}

	public void createThumbnailForCode(String codeId, String sourceFilePath, String destinationFilePath, String dimensions) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(SOURCE_FILE_PATH, sourceFilePath);
		param.put(TARGET_FOLDER_PATH, destinationFilePath);
		param.put(DIMENSIONS, dimensions);
		param.put(CODE_UID, codeId);
		param.put(API_END_POINT, settingService.getConfigSetting(ConfigConstants.GOORU_API_ENDPOINT, 0, TaxonomyUtil.GOORU_ORG_UID));
		Code code = this.taxonomyRespository.findCodeByCodeUId(codeId);
		s3ResourceApiHandler.resetS3UploadFlag(code);
		this.getAsyncExecutor().executeRestAPI(param, settingService.getConfigSetting(ConfigConstants.GOORU_CONVERSION_RESTPOINT, 0, TaxonomyUtil.GOORU_ORG_UID) + "/conversion/image", Method.POST.getName());
	}

	public static ResourceMetadataCo getYoutubeResourceFeeds(String url, ResourceMetadataCo resourceFeeds) {
		if (resourceFeeds == null) {
			resourceFeeds = new ResourceMetadataCo();
		}
		int status = 404;
		resourceFeeds.setUrlStatus(status);
		long start = System.currentTimeMillis();
		String videoId = getYoutubeVideoId(url);
		if (videoId != null) {
			String requestURL = "http://gdata.youtube.com/feeds/api/videos/" + videoId + "?alt=json&v=2";
			try {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(requestURL);
				HttpResponse httpResponse = client.execute(httpGet);
				status = httpResponse.getStatusLine().getStatusCode();
				logger.info("youtube api response code: " + status);
				if (status == 200) {
					HttpEntity entity = httpResponse.getEntity();
					InputStream inputStream = entity.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "n");
					}
					inputStream.close();
					JSONObject youtubeApiFeeds = new JSONObject(sb.toString());
					JSONObject entryJsonObject = (JSONObject) youtubeApiFeeds.get(ENTRY);
					JSONObject mediaGroupJsonObject = (JSONObject) entryJsonObject.get(MEDIA_GROUP);
					JSONObject titleJsonObject = (JSONObject) mediaGroupJsonObject.get(MEDIA_TITLE);
					JSONObject descriptionJsonObject = (JSONObject) mediaGroupJsonObject.get(MEDIA_DESCRIPTION);
					resourceFeeds.setTitle((String) titleJsonObject.get("$t"));
					resourceFeeds.setDescription((String) descriptionJsonObject.get("$t"));
					if (entryJsonObject.has(YT_STATISTICS)) {
						JSONObject statisticsJsonObject = (JSONObject) entryJsonObject.get(YT_STATISTICS);
						resourceFeeds.setFavoriteCount(Long.parseLong((String) statisticsJsonObject.get(FAVORITE_COUNT)));
						resourceFeeds.setViewCount(Long.parseLong((String) statisticsJsonObject.get(VIEW_COUNT)));
					}
					if (entryJsonObject.has(GD_RATING)) {
						JSONObject gdRatingJsonObject = (JSONObject) entryJsonObject.get(GD_RATING);
						resourceFeeds.setRatingAverage((Double) gdRatingJsonObject.get(AVERAGE));
					}
					if (entryJsonObject.has(YT_RATING)) {
						JSONObject ytRatingJsonObject = (JSONObject) entryJsonObject.get(YT_RATING);
						resourceFeeds.setDislikeCount(Long.parseLong((String) ytRatingJsonObject.get(NUM_DISLIKES)));
						resourceFeeds.setLikeCount(Long.parseLong((String) ytRatingJsonObject.get(NUM_LIKES)));
					}
					if (mediaGroupJsonObject.has(YT_DURATION)) {
						resourceFeeds.setDuration(Long.parseLong((String) ((JSONObject) mediaGroupJsonObject.getJSONObject(YT_DURATION)).get(SECONDS)));
					}
				}
				resourceFeeds.setUrlStatus(status);
				return resourceFeeds;
			} catch (Exception ex) {
				logger.error("getYoutubeResourceFeeds: " + ex);
			}
			logger.error("Total time for get youtube api data :" + (System.currentTimeMillis() - start));
		}
		return resourceFeeds;
	}

	public void moveAttachment(Resource newResource, Resource resource) {
		try {
			File parentFolderFile = new File(UserGroupSupport.getUserOrganizationNfsInternalPath() + resource.getFolder());
			if (!parentFolderFile.exists()) {
				parentFolderFile.mkdirs();
			}

			File file = new File(UserGroupSupport.getUserOrganizationNfsInternalPath() + Constants.UPLOADED_MEDIA_FOLDER + "/" + newResource.getAttach().getMediaFilename());
			String fileExtension = org.apache.commons.lang.StringUtils.substringAfterLast(newResource.getAttach().getFilename(), ".");
			if (fileExtension.equalsIgnoreCase(PDF)) {
				PDDocument doc = PDDocument.load(file);
				ResourceInfo resourceInfo = new ResourceInfo();
				resourceInfo.setResource(resource);
				resourceInfo.setNumOfPages(doc.getNumberOfPages());
				resourceInfo.setLastUpdated(resource.getLastModified());
				this.resourceRepository.save(resourceInfo);
				resource.setResourceInfo(resourceInfo);
			}

			file.renameTo(new File(UserGroupSupport.getUserOrganizationNfsInternalPath() + resource.getFolder() + "/" + newResource.getAttach().getFilename()));
			if (newResource.getThumbnail() == null) {
				this.downloadAndSendMsgToGenerateThumbnails(resource, null);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Properties getClassPlanConstants() {
		return classPlanConstants;
	}

	public SettingService getSettingService() {
		return settingService;
	}

	public MediaService getMediaService() {
		return mediaService;
	}

	public Map<String, String> getPropertyMap() {
		return propertyMap;
	}

	public AsyncExecutor getAsyncExecutor() {
		return asyncExecutor;
	}

}
