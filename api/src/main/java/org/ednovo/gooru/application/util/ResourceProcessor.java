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

import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceInfo;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.TransactionBox;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.domain.service.resource.ResourceService;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.gooru.domain.service.storage.S3ResourceApiHandler;
import org.ednovo.gooru.infrastructure.messenger.IndexHandler;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy.TaxonomyRespository;
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
	private TaxonomyRespository taxonomyRespository;

	@Autowired
	private S3ResourceApiHandler s3ResourceApiHandler;

	@Autowired
	private IndexProcessor indexProcessor;

	@Autowired
	private GooruImageUtil gooruImageUtil;

	@Autowired
	private SettingService settingService;
	
	@Autowired
	private IndexHandler indexHandler;

	private final Logger logger = LoggerFactory.getLogger(ResourceProcessor.class);
	
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
					indexHandler.setReIndexRequest(resource.getGooruOid(), IndexProcessor.INDEX, getSearchResourceType(resource), null, false, false);
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
