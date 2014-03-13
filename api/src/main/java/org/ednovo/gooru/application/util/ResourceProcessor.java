/////////////////////////////////////////////////////////////
// ResourceProcessor.java
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

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.ConverterDTO;
import org.ednovo.gooru.core.api.model.Job;
import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceDTO;
import org.ednovo.gooru.core.api.model.ResourceInfo;
import org.ednovo.gooru.core.api.model.ResourceInstance;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.application.util.RequestUtil;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.TransactionBox;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.domain.service.resource.ResourceService;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.gooru.domain.service.storage.S3ResourceApiHandler;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.classplan.LearnguideRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy.TaxonomyRespository;
import org.restlet.data.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.sun.pdfview.PDFFile;

public class ResourceProcessor implements ParameterProperties {

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private RedisService redisService;

	@Autowired
	private LearnguideRepository classplanRepository;

	@Autowired
	private TaxonomyRespository taxonomyRespository;

	@Autowired
	private S3ResourceApiHandler s3ResourceApiHandler;

	@Autowired
	private IndexProcessor indexProcessor;

	@Autowired
	private GooruImageUtil gooruImageUtil;

	@Autowired
	private SettingService settingService;

	private final Logger logger = LoggerFactory.getLogger(ResourceProcessor.class);

	public void saveResources(final ConverterDTO converterDTO) throws Exception {

		new TransactionBox() {

			@Override
			public void execute() {

				try {

					Job job = (Job) resourceRepository.get(Job.class, converterDTO.getJobId());

					Learnguide classplan = (Learnguide) classplanRepository.findByContent(converterDTO.getGooruContentId());

					if (converterDTO.isSuccess()) {
						if (converterDTO.getResourcePaths() != null) {

							for (String resourcePath : converterDTO.getResourcePaths()) {

								ResourceDTO resourceDTO = converterDTO.getResource();
								ResourceInstance resourceInstance = new ResourceInstance(converterDTO.getSegmentId(), new Resource());
								resourceInstance.setResourceInstanceId(UUID.randomUUID().toString());
								Resource resource = resourceInstance.getResource();
								if (resourceDTO != null) {
									resource.setTitle(resourceDTO.getLabel());
								}
								resource.setUser(classplan.getUser());
								resource.setResourceType(new ResourceType());
								resource.getResourceType().setName(ResourceType.Type.IMAGE.getType());
								resource.setSharing(PRIVATE);
								if (resourceInstance.getNarrative() == null) {
									resourceInstance.setNarrative("");
								}
								saveResourceInstance(resourceInstance, resourcePath, classplan);
							}

						} else {
							ResourceDTO resourceDto = converterDTO.getResource();
							ResourceInstance resourceInstance = new ResourceInstance(converterDTO.getSegmentId(), new Resource());
							resourceInstance.setResourceInstanceId(UUID.randomUUID().toString());
							Resource resource = resourceInstance.getResource();
							resource.setUser(classplan.getUser());
							resource.setTitle(resourceDto.getLabel());
							resource.setUrl(resourceDto.getNativeURL());
							ResourceType resourceType = new ResourceType();
							resourceType.setName(ResourceType.Type.PRESENTATION.getType());
							resource.setResourceType(resourceType);
							resourceInstance.setStart(resourceDto.getStart());
							resourceInstance.setStop(resourceDto.getStop());
							saveResourceInstance(resourceInstance, converterDTO.getResourcePath(), classplan);

						}
						long endTime = new Date().getTime();
						long starttime = converterDTO.getStartTime();
						job.setTimeToComplete((int) (endTime - starttime) / 1000);
						job.setStatus(Job.Status.COMPLETED.getStatus());
					} else {
						job.setStatus(Job.Status.FAILED.getStatus());
					}
					resourceRepository.save(job);

				} catch (Exception exception) {

					logger.error("Splitting of resource failed");
				}

			}

		};
	}

	private ResourceInstance saveResourceInstance(ResourceInstance resourceInstance, String resourcePath, Learnguide classplan) throws Exception {
		Resource resource = resourceInstance.getResource();
		if (resource.getResourceType().getName().equals(ResourceType.Type.QUIZ.getType())) {
			resourceInstance.setResource(resourceService.findResourceByContentGooruId(resource.getGooruOid()));
		} else if (resource.getResourceType().getName().equals(ResourceType.Type.TEXTBOOK.getType())) {
			resourceInstance.setResource(resourceService.findTextbookByContentGooruId(resource.getGooruOid()));
		}

		resource.setUrl(GooruImageUtil.getFileName(resourcePath));

		resourceService.saveResourceInstance(resourceInstance);

		resource.setCategory(SLIDE);

		String destPath = resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder();

		String fileHash = BaseUtil.getFileMD5Hash(resourcePath);

		resource.setFileHash(fileHash);

		File destDir = new File(destPath);

		if (!destDir.exists()) {
			destDir.mkdirs();
		}

		FileUtils.moveFileToDirectory(new File(resourcePath), destDir, false);

		resourceRepository.save(resource);

		s3ResourceApiHandler.updateOrganization(resource);

		String filePath = destPath + resource.getUrl();

		Map<String, Object> param = new HashMap<String, Object>();
		param.put( RESOURCE_FILE_PATH, filePath);
		param.put(RESOURCE_GOORU_OID, resource.getGooruOid());
		RequestUtil.executeRestAPI(param, settingService.getConfigSetting(ConfigConstants.GOORU_CONVERSION_RESTPOINT, 0, TaxonomyUtil.GOORU_ORG_UID) + "/conversion/pdf-to-image", Method.POST.getName());
		indexProcessor.index(classplan.getGooruOid(), IndexProcessor.INDEX, COLLECTION);

		return resourceInstance;

	}

	public void updateWebResources(final Long contentId, final Integer status) {

		new TransactionBox() {

			@Override
			public void execute() {
				logger.info("Updating Resource: " + contentId + " status : " + status);
				resourceRepository.updateWebResource(contentId, status);
			}
		};
	}

	public void generateResourceThumbnail(final String resourceGooruOid) throws Exception {

		new TransactionBox() {

			@Override
			public void execute() {
				try {
					logger.info("Updating Resource: " + resourceGooruOid);
					Resource resource = resourceRepository.findResourceByContentGooruId(resourceGooruOid);
					// String repoPath =
					// resource.getOrganization().getNfsStorageArea().getInternalPath()
					// + resource.getFolder();
					/*
					 * getGooruImageUtil().scaleImage(repoPath +
					 * resource.getThumbnail(), repoPath, null,
					 * ResourceImageUtil.RESOURCE_THUMBNAIL_SIZES);
					 */
					s3ResourceApiHandler.uploadResourceFolder(resource);
				} catch (Exception ex) {
					logger.error("Error while generating thumbnail ", ex);
					s3ResourceApiHandler.uploadResourceThumbnailWithNewSession(resourceGooruOid);
				}
			}
		};
	}

	public void updateCodeToS3WithNewSession(final String codeUId, final String fileName) throws Exception {

		new TransactionBox() {

			@Override
			public void execute() {

				try {

					Code code = taxonomyRespository.findCodeByCodeUId(codeUId);

					if (fileName == null) {
						s3ResourceApiHandler.uploadCodeFolder(code);
					} else {
						// FIXME
						/*
						 * s3ResourceApiHandler.uploadResourceFile(resourceGooruOid
						 * , fileName);
						 */
					}

				} catch (Exception ex) {
					logger.error("Error while uploading To S3 ", ex);
					if (fileName == null) {
						s3ResourceApiHandler.uploadCodeFolderWithNewSession(codeUId);
					} else {
						// FIXME
						/*
						 * s3ResourceApiHandler.uploadResourceFile(resourceGooruOid
						 * , fileName);
						 */
					}
				}
			}
		};
	}

	public void updateResourceToS3WithNewSession(String resourceGooruOid) throws Exception {
		updateResourceToS3WithNewSession(resourceGooruOid, null);
	}

	public void updateResourceToS3WithNewSession(final String resourceGooruOid, final String fileName) throws Exception {

		new TransactionBox() {

			@Override
			public void execute() {

				try {
					Resource resource = resourceRepository.findResourceByContentGooruId(resourceGooruOid);
					if (resource.getResourceType().getName().equals(ResourceType.Type.CLASSPLAN.getType())) {
						final String cacheKey = "collection-" + resourceGooruOid;
						getRedisService().deleteKey(cacheKey);
					}
					if (fileName == null) {
						s3ResourceApiHandler.uploadResourceFolder(resource);
					} else {
						// FIXME
						s3ResourceApiHandler.uploadResourceFile(resourceGooruOid, fileName);
					}
					indexProcessor.index(resource.getGooruOid(), IndexProcessor.INDEX, getSearchResourceType(resource));

				} catch (Exception ex) {
					logger.error("Error while uploading To S3 ", ex);
					if (fileName == null) {
						s3ResourceApiHandler.uploadResourceFolderWithNewSession(resourceGooruOid);
					} else {
						// FIXME
						/*
						 * s3ResourceApiHandler.uploadResourceFile(resourceGooruOid
						 * , fileName);
						 */
					}
				}
			}
		};
	}

	public String getSearchResourceType(Resource resource) {

		String typeName = resource.getResourceType().getName();
		if (typeName.equals(ResourceType.Type.CLASSPLAN.getType())) {
			return COLLECTION;
		} else if (typeName.equals(ResourceType.Type.ASSESSMENT_QUIZ.getType())) {
			return QUIZ;
		} else if (typeName.equals(ResourceType.Type.ASSESSMENT_QUESTION.getType())) {
			return QUESTION;
		} else if (typeName.equals(ResourceType.Type.QB_QUESTION.getType())) {
			return QB_QUESTION;
		} else {
			return RESOURCE;
		}
	}

	public void postPdfUpdate(final String resourceGooruOid) throws Exception {

		new TransactionBox() {

			@Override
			public void execute() {

				try {

					logger.info("Updating Resource: " + resourceGooruOid);
					Resource resource = resourceRepository.findResourceByContentGooruId(resourceGooruOid);
					String repoPath = resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder();
					/*
					 * getGooruImageUtil().scaleImage(repoPath +
					 * resource.getThumbnail(), repoPath, null,
					 * ResourceImageUtil.RESOURCE_THUMBNAIL_SIZES);
					 */
					ResourceInfo resourceInfo = resourceRepository.findResourceInfo(resource.getGooruOid());
					if (resourceInfo == null) {
						resourceInfo = new ResourceInfo();
						resourceInfo.setResource(resource);
					}

					resourceInfo.setLastUpdated(new Date());

					RandomAccessFile raf = new RandomAccessFile(new File(repoPath + resource.getUrl()), "r");
					FileChannel channel = raf.getChannel();
					ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
					PDFFile pdffile = new PDFFile(buf);
					resourceInfo.setNumOfPages(pdffile.getNumPages());

					resourceRepository.save(resourceInfo);
					resource.setResourceInfo(resourceInfo);
					resourceRepository.save(resource);

					s3ResourceApiHandler.uploadResourceFolder(resource);

				} catch (Exception ex) {
					logger.error("Error while generating thumbnail ", ex);
					s3ResourceApiHandler.uploadResourceFolderWithNewSession(resourceGooruOid);
				}
			}
		};
	}

	public GooruImageUtil getGooruImageUtil() {
		return gooruImageUtil;
	}

	public void updateResourceThumbnail(String thumbnail, String resourceGooruOid) {
		Resource resource = resourceRepository.findResourceByContentGooruId(resourceGooruOid);
		resource.setThumbnail(thumbnail);
		resourceRepository.save(resource);
	}

	public RedisService getRedisService() {
		return redisService;
	}
}
