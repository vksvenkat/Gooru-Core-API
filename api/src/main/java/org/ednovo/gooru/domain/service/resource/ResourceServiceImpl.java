/////////////////////////////////////////////////////////////
// ResourceServiceImpl.java
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
package org.ednovo.gooru.domain.service.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.application.converter.FileProcessor;
import org.ednovo.gooru.application.util.AsyncExecutor;
import org.ednovo.gooru.application.util.CollectionUtil;
import org.ednovo.gooru.application.util.GooruImageUtil;
import org.ednovo.gooru.application.util.LogUtil;
import org.ednovo.gooru.application.util.ResourceImageUtil;
import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.application.util.UserContentRelationshipUtil;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Assessment;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentPermission;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.ConverterDTO;
import org.ednovo.gooru.core.api.model.CsvCrawler;
import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.FileMeta;
import org.ednovo.gooru.core.api.model.ImportMode;
import org.ednovo.gooru.core.api.model.Job;
import org.ednovo.gooru.core.api.model.JobType;
import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.License;
import org.ednovo.gooru.core.api.model.Rating;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceInfo;
import org.ednovo.gooru.core.api.model.ResourceInstance;
import org.ednovo.gooru.core.api.model.ResourceMetaData;
import org.ednovo.gooru.core.api.model.ResourceSource;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.ResourceUrlStatus;
import org.ednovo.gooru.core.api.model.Segment;
import org.ednovo.gooru.core.api.model.SessionActivityType;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.ShelfItem;
import org.ednovo.gooru.core.api.model.Textbook;
import org.ednovo.gooru.core.api.model.UpdateViewsDTO;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserContentAssoc.RELATIONSHIP;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.api.model.UserRole;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.application.util.CollectionServiceUtil;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.core.application.util.ImageUtil;
import org.ednovo.gooru.core.application.util.RequestUtil;
import org.ednovo.gooru.core.cassandra.model.ResourceCio;
import org.ednovo.gooru.core.cassandra.model.ResourceMetadataCo;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.domain.cassandra.service.ResourceCassandraService;
import org.ednovo.gooru.domain.service.partner.CustomFieldsService;
import org.ednovo.gooru.domain.service.rating.RatingService;
import org.ednovo.gooru.domain.service.revision_history.RevisionHistoryService;
import org.ednovo.gooru.domain.service.sessionActivity.SessionActivityService;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.gooru.domain.service.shelf.ShelfService;
import org.ednovo.gooru.domain.service.storage.S3ResourceApiHandler;
import org.ednovo.gooru.domain.service.taxonomy.TaxonomyService;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.ConfigSettingRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.FeedbackRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.activity.SessionActivityRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.annotation.SubscriptionRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.assessment.AssessmentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.classplan.LearnguideRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.content.ContentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.SegmentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.shelf.ShelfRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy.TaxonomyRespository;
import org.ednovo.gooru.security.OperationAuthorizer;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.google.common.collect.Lists;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.SimpleBookmark;
import com.sun.pdfview.PDFFile;

@Service
public class ResourceServiceImpl extends OperationAuthorizer implements ResourceService, ParameterProperties, ConstantProperties {

	private static final Logger logger = LoggerFactory.getLogger(ResourceServiceImpl.class);

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private ResourceParser resourceParser;

	@Autowired
	private SegmentRepository segmentRepository;

	@Autowired
	private ResourceImageUtil resourceImageUtil;

	@Autowired
	private MediaService mediaService;

	@Autowired
	private BaseRepository baseRepository;

	@Autowired
	private S3ResourceApiHandler s3ResourceApiHandler;

	@Autowired
	private IndexProcessor indexProcessor;

	@Autowired
	private LearnguideRepository learnguideRepository;

	@Autowired
	private CollectionUtil collectionUtil;

	@Autowired
	private ContentRepository contentRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	@javax.annotation.Resource(name = "resourceManager")
	private ResourceManager resourceManager;

	@Autowired
	private SessionActivityService sessionActivityService;

	@Autowired
	private AssessmentRepository assessmentRepository;

	@Autowired
	private RatingService ratingService;

	@Autowired
	private ShelfRepository shelfRepository;

	@Autowired
	private SessionActivityRepository sessionActivityRepository;

	@Autowired
	private RevisionHistoryService revisionHistoryService;

	@Autowired
	private TaxonomyService taxonomyService;

	@Autowired
	private TaxonomyRespository taxonomyRepository;

	@Autowired
	private SettingService settingService;

	@Autowired
	private ResourceCassandraService resourceCassandraService;

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private ShelfService shelfService;

	@Autowired
	private ConfigSettingRepository configSettingRepository;

	@Autowired
	private CustomFieldsService customFieldService;

	@Autowired
	private FeedbackRepository feedbackRepository;

	@Autowired
	private AsyncExecutor asyncExecutor;

	@Autowired
	private CustomTableRepository customTableRepository;

	@Override
	public ResourceInstance saveResourceInstance(ResourceInstance resourceInstance) throws Exception {
		Segment segment = (Segment) getSegmentRepository().get(Segment.class, resourceInstance.getSegment().getSegmentId());
		resourceInstance.setSegment(segment);
		Errors errors = new BindException(Resource.class, RESOURCE);
		saveResource(resourceInstance.getResource(), errors, false);
		if (resourceInstance.getSegment().getResourceInstances() == null) {
			resourceInstance.getSegment().setResourceInstances(new TreeSet<ResourceInstance>());
		}
		resourceInstance.getSegment().getResourceInstances().add(resourceInstance);
		if (resourceInstance.getSequence() == null) {
			resourceInstance.setSequence(resourceInstance.getSegment().getResourceInstances().size() + 1);
		}
		getSegmentRepository().save(resourceInstance.getSegment());
		getSegmentRepository().save(resourceInstance);
		return resourceInstance;
	}

	@Override
	public List<Resource> listResources(Map<String, String> filters) {
		return getResourceRepository().listResources(filters);
	}

	@Override
	public Resource findResourceByContentGooruId(String gooruContentId) {
		return getResourceRepository().findResourceByContentGooruId(gooruContentId);
	}

	@Override
	public void deleteResource(Long contentId) {
		getResourceRepository().remove(Resource.class, contentId);

	}

	@Override
	public Segment reorderResourceInstace(Resource resource, String segmentId, String resourceInstanceId, String newResourceInstancePos, String newSegmentId) throws Exception {
		Segment sourceSegment = null;
		Segment targetSegment = null;

		for (Segment segment : resource.getResourceSegments()) {
			if (segment.getSegmentId().equals(segmentId)) {
				sourceSegment = segment;
			} else if (segment.getSegmentId().equals(newSegmentId)) {
				targetSegment = segment;
			}
			if (sourceSegment != null && targetSegment != null) {
				// Both segments identified.
				break;
			}
		}

		if (sourceSegment != null) {
			if (targetSegment == null) {
				targetSegment = sourceSegment;
			}
			CollectionServiceUtil.resetInstancesSequence(sourceSegment);
			if (!sourceSegment.getSegmentId().equals(targetSegment.getSegmentId())) {
				CollectionServiceUtil.resetInstancesSequence(targetSegment);
			}
			ResourceInstance resourceInstanceToMove = null;
			int previousPosition = 0;
			int fromPosition = 0;
			for (ResourceInstance resourceInstance : sourceSegment.getResourceInstances()) {
				if (resourceInstanceId.equals(resourceInstance.getResourceInstanceId()) && resourceInstanceToMove == null) {
					resourceInstanceToMove = resourceInstance;
					break;
				}
			}
			fromPosition = resourceInstanceToMove.getSequence();

			if (sourceSegment.equals(targetSegment)) {
				int insertPosition = 0;
				if (!newResourceInstancePos.equals(FIRST)) {
					for (ResourceInstance segmentResourceAssoc : sourceSegment.getResourceInstances()) {
						if (newResourceInstancePos.equals(segmentResourceAssoc.getResourceInstanceId())) {
							previousPosition = segmentResourceAssoc.getSequence();
							insertPosition = previousPosition + 1;
							resourceInstanceToMove.setSequence(insertPosition);
							break;
						}
					}
					if (fromPosition != 0 && previousPosition != 0) {
						for (ResourceInstance resourceInstance : sourceSegment.getResourceInstances()) {
							if (!resourceInstanceToMove.equals(resourceInstance)) {
								int sequence = resourceInstance.getSequence();
								if (sequence > fromPosition && sequence < insertPosition) {
									resourceInstance.setSequence(resourceInstance.getSequence() - 1);
								} else if (sequence < fromPosition && sequence > previousPosition) {
									resourceInstance.setSequence(resourceInstance.getSequence() + 1);
								}
							}
						}
					}
				} else {
					insertPosition = 1;
					resourceInstanceToMove.setSequence(insertPosition);
					int temSequence = 1;
					for (ResourceInstance resourceInstance : sourceSegment.getResourceInstances()) {
						if (!resourceInstanceToMove.equals(resourceInstance)) {
							resourceInstance.setSequence(++temSequence);
						}
					}
				}
			} else {

				int lastPosition = 0;
				int insertPosition = 0;

				// identify target resource instance
				for (ResourceInstance resourceInstance : targetSegment.getResourceInstances()) {
					int resourceInstanceSequence = resourceInstance.getSequence();
					if (lastPosition < resourceInstanceSequence) {
						lastPosition = resourceInstanceSequence;
					}
					if (newResourceInstancePos.equals(resourceInstance.getResourceInstanceId())) {
						previousPosition = resourceInstanceSequence;
						insertPosition = previousPosition + 1;
					}
				}

				if (previousPosition != 0 && previousPosition == lastPosition) {
					insertPosition = lastPosition + 1;
				}

				if (newResourceInstancePos.equals(FIRST)) {
					// The resource is probably dragged into first position.
					insertPosition = 1;
				}

				// Move sequence of all items beyond the target resource
				// instance(inclusive) upwards
				for (ResourceInstance segmentResourceAssoc : targetSegment.getResourceInstances()) {
					int sequence = segmentResourceAssoc.getSequence();
					if (sequence > insertPosition) {
						segmentResourceAssoc.setSequence(sequence + 1);
					}
				}

				resourceInstanceToMove.setSequence(insertPosition);
				// update sequence of all items in the source

				if (!targetSegment.equals(sourceSegment)) {
					// Insert the resource instance (update segment)
					resourceInstanceToMove.setSegment(targetSegment);
					sourceSegment.getResourceInstances().remove(resourceInstanceToMove);
					targetSegment.getResourceInstances().add(resourceInstanceToMove);
				}
				for (ResourceInstance resourceAssoc : sourceSegment.getResourceInstances()) {
					int sequence = resourceAssoc.getSequence();
					if (sequence > fromPosition) {
						resourceAssoc.setSequence(sequence - 1);
					}
				}
			}
			CollectionServiceUtil.resetInstancesSequence(sourceSegment);
			if (!targetSegment.getSegmentId().equals(sourceSegment.getSegmentId())) {
				CollectionServiceUtil.resetInstancesSequence(targetSegment);
			}
			getResourceRepository().saveOrUpdate(resource);
			return sourceSegment;
		}
		return null;
	}

	@Override
	public void deleteSegmentResourceInstance(String resourceInstanceId) {

		ResourceInstance resourceInstance = (ResourceInstance) getResourceRepository().get(ResourceInstance.class, resourceInstanceId);

		if (resourceInstance != null) {
			getResourceRepository().remove(ResourceInstance.class, resourceInstance.getResourceInstanceId());
		} else {
			logger.error("deleteSegmentResourceInstance: no resource found for : " + resourceInstanceId);
		}
		getResourceRepository().flush();
	}

	@Override
	public Segment getSegment(String segmentId) {
		if (segmentId != null && !segmentId.equals("")) {
			return (Segment) getSegmentRepository().get(Segment.class, segmentId);
		} else {
			return null;
		}
	}

	@Override
	public void deleteSegment(String segmentId, Learnguide collection) {

		getSegmentRepository().remove(Segment.class, segmentId);
		CollectionServiceUtil.resetSegmentsSequence(collection);
	}

	@Override
	public Long reorderSegments(String reorder, String gooruResourceId) throws Exception {
		Resource resource = findResourceByContentGooruId(gooruResourceId);
		String[] newArray = reorder.split("~~");
		int startSeq = resource.getResourceSegments().size() - newArray.length + 1;
		for (Segment segment : resource.getResourceSegments()) {
			segment.setSequence(null);
		}
		for (int sequence = 0; sequence < newArray.length; sequence++) {
			String segmentId = newArray[sequence];
			for (Segment segment : resource.getResourceSegments()) {
				if (segment.getSegmentId().equals(segmentId)) {
					segment.setSequence(sequence + startSeq);
					break;
				}
			}
		}
		startSeq = 0;
		for (Segment segment : resource.getResourceSegments()) {
			if (segment.getSequence() == null) {
				segment.setSequence(++startSeq);
			}
		}
		getSegmentRepository().save(resource);
		return resource.getContentId();
	}

	@Override
	public List<ResourceInstance> listResourceInstances(String gooruContentId, String type) {
		return getSegmentRepository().listResourceInstances(gooruContentId, type);
	}

	@Override
	public List<ResourceInstance> listSegmentResourceInstances(String segmentId) {
		return getSegmentRepository().listSegmentResourceInstances(segmentId);
	}

	@Override
	public ResourceInstance getFirstResourceInstanceOfResource(String gooruContentId) {
		return getSegmentRepository().getFirstResourceInstanceOfResource(gooruContentId);
	}

	@Override
	public void enrichAndAddOrUpdate(Resource resource) {

		// if blank extract text and title by url or parentUrl
		if (StringUtils.isBlank(resource.getTitle())) {
			enrichWithTitleAndText(resource);
		}

		// FIXME: change to downloading the thumbnail.
		// get thumbnail by title:
		// if (StringUtils.isBlank(resource.getThumbnail())) {
		// String thumbnailUrl = getThumbnailUrlByQuery(resource.getTitle());
		// resource.setThumbnail(thumbnailUrl);
		// }

		// clean the title:
		String title = cleanTitle(resource.getTitle());
		resource.setTitle(title);

		// save resource
		getResourceRepository().saveOrUpdate(resource);

		// add to index.
		// FIXME: Add separate call to reindex here

		return;
	}

	private String cleanTitle(String title) {
		List<String> stringsToRemoveList = Arrays.asList("bbc", "ks2", "read", "phet", "bitesize", "maths");
		Set stringsToRemoveSet = new HashSet<String>(stringsToRemoveList);

		while (true) {
			title = StringUtils.trim(title);
			title = StringUtils.removeStart(title, ",");
			title = StringUtils.removeStart(title, ".");
			title = StringUtils.removeStart(title, "-");
			title = StringUtils.removeStart(title, ":");
			title = StringUtils.removeEnd(title, ",");
			title = StringUtils.removeEnd(title, ".");
			title = StringUtils.removeEnd(title, "-");
			title = StringUtils.removeEnd(title, ":");
			title = StringUtils.trim(title);

			String[] words = StringUtils.split(title, ": ");
			if (words.length > 0 && stringsToRemoveSet.contains(words[0].toLowerCase())) {
				title = StringUtils.removeStartIgnoreCase(title, words[0]);
			} else if (words.length > 0 && stringsToRemoveSet.contains(words[words.length - 1].toLowerCase())) {
				title = StringUtils.removeEndIgnoreCase(title, words[words.length - 1]);
			} else {
				break;
			}
		}
		return title;
	}

	private void enrichWithTitleAndText(Resource resource) {

		String parentUrlString = resource.getParentUrl();

		List<String> possibleTitles = new ArrayList<String>();
		List<String> possibleTexts = new ArrayList<String>();

		// get possible title and text from url
		ResourceParser.TitleAndText textAndTitle = this.getResourceParser().getTextAndTitle(resource.getUrl());
		possibleTitles.add(textAndTitle.getTitle());
		possibleTexts.add(textAndTitle.getText());

		// get possible title and text from parent url
		if (StringUtils.isNotBlank(parentUrlString)) {
			ResourceParser.TitleAndText parentTextAndTitle = this.getResourceParser().getTextAndTitle(parentUrlString);
			possibleTitles.add(parentTextAndTitle.getTitle());
			possibleTexts.add(parentTextAndTitle.getText());
		}

		// set the first title that is not blank.
		for (String title : possibleTitles) {
			if (StringUtils.isNotBlank(resource.getTitle())) {
				break;
			}
			resource.setTitle(StringUtils.substring(title, 0, 250));
		}
		// set the first text that is not blank.
	}

	public String getThumbnailUrlByQuery(String query) {
		try {

			// get rid of spaces
			query = query.replaceAll("[\\s,\\.]+", "%20");

			// send request to bing.
			String address = "http://api.bing.net/xml.aspx?Appid=E33DF01A3363CBE8CC3C5F4E15F1284647476C8A&sources=image&query=" + query;
			URL url = new URL(address);
			URLConnection connection = url.openConnection();
			InputStream in = connection.getInputStream();

			// xml name space stuff:
			NamespaceContext ctx = new NamespaceContext() {
				public String getNamespaceURI(String prefix) {
					String uri;
					if (prefix.equals("e")) {
						uri = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/element";
					} else if (prefix.equals("m")) {
						uri = "http://schemas.microsoft.com/LiveSearch/2008/04/XML/multimedia";
					} else {
						uri = null;
					}
					return uri;
				}

				public Iterator getPrefixes(String val) {
					return null;
				}

				public String getPrefix(String uri) {
					return null;
				}
			};

			// create xml doc from input:
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(true);
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			org.w3c.dom.Document doc = builder.parse(in);// new
															// File("c:\\users\\a\\Desktop\\test.xml"));

			// create xpath for extract thumbnail url:
			String xpathStr = "/e:SearchResponse/m:Image/m:Results/m:ImageResult/m:Thumbnail/m:Url/text()";
			XPathFactory xpathFact = XPathFactory.newInstance();
			XPath xpath = xpathFact.newXPath();
			xpath.setNamespaceContext(ctx);

			// extract thumbnail url from xml doc reponse:
			String thmbnailUrl = xpath.evaluate(xpathStr, doc);

			return thmbnailUrl;

		} catch (Exception ex) {

			return null;
		}

	}

	@Override
	public void updateResourceSource(String resourceTypeString) {
		List<Resource> resources = null;
		Map<String, String> filters = new HashMap<String, String>();
		int pageNum = 0;
		int pageSize = 100;
		filters.put(RESOURCE_TYPE, resourceTypeString);
		filters.put(IN_USE, ONE);
		do {
			filters.put(PAGE_NUM, (++pageNum) + "");
			filters.put(PAGE_SIZE, pageSize + "");
			filters.put(RESOURCE_SOURCE, NULL);
			resources = listResources(filters);
			logger.debug("no of resource :" + resources.size() + " of page : " + pageNum + " of size : " + pageSize);
			int count = 0;
			for (Resource resource : resources) {
				String domainName = getDomainName(resource.getUrl());
				if (!domainName.isEmpty()) {
					ResourceSource resourceSource = this.getResourceRepository().findResourceSource(domainName);
					if (resourceSource != null) {
						logger.debug("resource url : " + resource.getUrl() + " source name : " + " updated domainName: " + domainName + "no of resource to go: " + (++count));
						this.getResourceRepository().updateResourceSourceId(resource.getContentId(), resourceSource.getResourceSourceId());
					}
				}
			}
			try {
				Thread.sleep(5000);
			} catch (Exception ex) {
			}
		} while (resources != null && resources.size() > 0);
	}

	@Override
	public int findViews(String contentGooruId) {
		return getResourceRepository().findViews(contentGooruId);
	}

	@Override
	public Textbook findTextbookByContentGooruId(String gooruContentId) {
		return getResourceRepository().findTextbookByContentGooruId(gooruContentId);
	}

	@Override
	public ResourceInstance getResourceInstance(String resourceInstanceId) {
		return (ResourceInstance) getResourceRepository().get(ResourceInstance.class, resourceInstanceId);
	}

	@Override
	public Resource findWebResource(String url) {
		return getResourceRepository().findWebResource(url);
	}

	@Override
	public void saveNewResource(Resource resource, boolean downloadResource) throws IOException {
		resource.setCreatedOn(new Date(System.currentTimeMillis()));
		if (StringUtils.isEmpty(resource.getGooruOid())) {
			resource.setGooruOid(UUID.randomUUID().toString());
		}
		resource.setLastModified(resource.getCreatedOn());

		this.getResourceRepository().saveOrUpdate(resource);

		if (downloadResource) {
			String sourceUrl = resource.getUrl();
			String fileName = StringUtils.substringAfterLast(sourceUrl, "/");

			File resourceFolder = new File(resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder());
			if (!resourceFolder.exists()) {
				resourceFolder.mkdir();
			}

			String resourceFilePath = resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder() + File.separator + fileName;
			boolean downloaded = ImageUtil.downloadAndSaveFile(sourceUrl, resourceFilePath);
			if (!downloaded) {
				throw new IOException("Save resource failed. Resource could not be downloaded from " + resource.getUrl());
			}
			this.getAsyncExecutor().uploadResourceFolder(resource);

			resource.setUrl(fileName);
			this.getResourceRepository().saveOrUpdate(resource);
			if (fileName.toLowerCase().endsWith(DOT_PDF)) {
				Map<String, Object> param = new HashMap<String, Object>();
				param.put(RESOURCE_FILE_PATH, resourceFilePath);
				param.put(RESOURCE_GOORU_OID, resource.getGooruOid());
				RequestUtil.executeRestAPI(param, settingService.getConfigSetting(ConfigConstants.GOORU_CONVERSION_RESTPOINT, 0, TaxonomyUtil.GOORU_ORG_UID) + "/conversion/pdf-to-image", Method.POST.getName());
			}
		} else {
			// Save resource folder
			resourceRepository.saveOrUpdate(resource);
		}
	}

	@Override
	public Resource handleNewResource(Resource resource, String resourceTypeForPdf, String thumbnail) {
		// test if a resource with url exist, currently just skip.
		Errors errors = new BindException(Resource.class, RESOURCE);
		Resource updatedResource = updateResource(resource, true, thumbnail, errors);
		if (updatedResource != null) {
			return updatedResource;
		}
		errors = new BindException(Resource.class, RESOURCE);
		boolean downloadedFlag = false;
		// download if need and save:
		// FIXME
		/*
		 * downloadedFlag = downloadFileIfRequiredAndUpdateUrl(resource,
		 * StringUtils.defaultString(resourceTypeForPdf,
		 * ResourceType.Type.HANDOUTS.getType()));
		 */
		ResourceType resourceType = new ResourceType();
		resource.setResourceType(resourceType);
		String fileExtension = org.apache.commons.lang.StringUtils.substringAfterLast(resource.getUrl(), ".");
		if (fileExtension.equalsIgnoreCase(PDF) || fileExtension.equalsIgnoreCase(PNG)) {
			if (fileExtension.contains(PDF)) {
				resourceType.setName(ResourceType.Type.HANDOUTS.getType());
			} else {
				resourceType.setName(ResourceType.Type.IMAGE.getType());
			}
		} else {
			resourceType.setName(ResourceImageUtil.getYoutubeVideoId(resource.getUrl()) != null ? ResourceType.Type.VIDEO.getType() : ResourceType.Type.RESOURCE.getType());
		}

		resource = saveResource(resource, errors, false);
		if (resource == null || errors.hasErrors()) {
			logger.error("save resource failed" + errors.toString());
		}

		if (downloadedFlag) {
			// Move the resource to the right folder
			File resourceFile = new File(resource.getUrl());
			if (resourceFile.exists()) {
				File resourceFolder = new File(resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder());
				if (!resourceFolder.exists()) {
					resourceFolder.mkdir();
				}
				String fileName = StringUtils.substringAfterLast(resource.getUrl(), "/");
				resourceFile.renameTo(new File(resourceFolder.getPath(), fileName));
				resource.setUrl(fileName);
				this.getResourceRepository().saveOrUpdate(resource);
				String resourceFilePath = resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder() + resource.getUrl();
				resourceFilePath = resourceFilePath.trim();
				if (fileName.toLowerCase().endsWith(DOT_PDF)) {
					Map<String, Object> param = new HashMap<String, Object>();
					param.put(RESOURCE_FILE_PATH, resourceFilePath);
					param.put(RESOURCE_GOORU_OID, resource.getGooruOid());
					RequestUtil.executeRestAPI(param, settingService.getConfigSetting(ConfigConstants.GOORU_CONVERSION_RESTPOINT, 0, TaxonomyUtil.GOORU_ORG_UID) + "/conversion/pdf-to-image", Method.POST.getName());
				}
			}
		} else {
			// Save resource folder
			this.getResourceRepository().saveOrUpdate(resource);
		}
		enrichAndAddOrUpdate(resource);

		// if handouts, split and save chapters as resources:
		if (resource.getResourceType().getName().equalsIgnoreCase(ResourceType.Type.HANDOUTS.getType())) {
			List<Resource> chapterResources = splitToChaptersResources(resource);
			for (Resource chapterResource : chapterResources)
				enrichAndAddOrUpdate(chapterResource);
		}
		indexProcessor.index(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE);

		return resource;

	}

	private Resource updateResource(Resource resource, boolean findByURL, String thumbnail, Errors errors) {
		Resource existingResource = null;
		if (findByURL) {
			existingResource = this.getResourceRepository().getResourceByUrl(resource.getUrl());
		} else {
			existingResource = this.getResourceRepository().findResourceByContentGooruId(resource.getGooruOid());
		}
		if (existingResource == null) {
			errors.reject(GET_GOORU_OID, "Invalid or non-existant record");
		} else {
			boolean saveResource = false;
			// Check if meta-data is added in new resource
			Set<ResourceMetaData> resourceMetaData = resource.getResourceMetaData();

			if ((existingResource.getResourceMetaData() == null && resourceMetaData != null) || existingResource.getResourceMetaData().size() == 0) {
				saveResource = true;
				existingResource.setResourceMetaData(resourceMetaData);
			}
			if (StringUtils.isEmpty(existingResource.getThumbnail()) && resourceMetaData != null) {
				for (ResourceMetaData resourceMetaData2 : resourceMetaData) {
					if (resourceMetaData2.getMetaKey().equalsIgnoreCase(RESOURCE_IMAGE) && !StringUtils.isEmpty(resourceMetaData2.getMetaContent())) {
						String imageURL = resourceMetaData2.getMetaContent();
						String fileName = existingResource.getGooruOid();
						saveResource = true;
						boolean downloaded = downloadThumbnail(fileName, imageURL, existingResource);
						if (downloaded) {
							break;
						}
					}
				}
			}

			if (resource.getTitle() != null) {
				saveResource = true;
				existingResource.setTitle(resource.getTitle());
			}

			if (resource.getDescription() != null) {
				saveResource = true;
				existingResource.setDescription(resource.getDescription());
			}

			if (StringUtils.isEmpty(existingResource.getThumbnail()) && thumbnail != null && thumbnail.startsWith("http://")) {
				saveResource = true;
				downloadThumbnail(existingResource.getGooruOid(), thumbnail, existingResource);
			}

			if (saveResource) {
				this.getResourceRepository().saveOrUpdate(existingResource);
				indexProcessor.index(existingResource.getGooruOid(), IndexProcessor.INDEX, RESOURCE);
			}
		}
		return existingResource;
	}

	public boolean downloadThumbnail(String fileName, String imageURL, Resource resource) {
		try {
			FileMeta fileMeta = this.getMediaService().handleFileUpload(fileName, imageURL, null, false, 0, 0);
			this.getResourceImageUtil().moveFileAndSendMsgToGenerateThumbnails(resource, fileMeta.getName(), false);
			return true;

		} catch (FileNotFoundException e) {
			logger.error("Error saving crawled resource image", e);
		} catch (IOException e) {
			logger.error("Error saving crawled resource image", e);
		}
		return false;
	}

	// chapter spliting
	@Override
	public List<Resource> splitToChaptersResources(Resource resource) {

		// split to chapters and return new chapter files:
		List<String> newLocalChaptersUrls = splitToChaptersAndSaveFiles(resource.getUrl());

		// save in db and index.
		List<Resource> chapterResources = new ArrayList<Resource>();
		for (String newLocalUrl : newLocalChaptersUrls) {
			Resource chapterResource = new Resource();
			chapterResource.setUrl(newLocalUrl);
			chapterResource.setUser(resource.getUser());
			chapterResource.setResourceTypeByString(ResourceType.Type.HANDOUTS.getType());
			chapterResource.setParentUrl(newLocalUrl);
			chapterResources.add(chapterResource);
		}
		return chapterResources;
	}

	private String getDomainName(String resourceUrl) {
		String domainName = "";
		if (resourceUrl != null && !resourceUrl.isEmpty()) {
			if (resourceUrl.contains("http://")) {
				domainName = resourceUrl.split("http://")[1];
			} else if (resourceUrl.contains("http://")) {
				domainName = resourceUrl.split("www.")[1];
			} else if (resourceUrl.contains("https://")) {
				domainName = resourceUrl.split("https://")[1];
			}
			if (domainName.contains("www.")) {
				domainName = domainName.split("www.")[1];
			}
			if (domainName.contains("/")) {
				domainName = domainName.split("/")[0];
			}
		}
		return domainName;
	}

	@Override
	public ResourceInstance findResourceInstanceByContentGooruId(String gooruOid) {

		return this.getResourceRepository().findResourceInstanceByContentGooruId(gooruOid);
	}

	private static List<String> splitToChaptersAndSaveFiles(String newLocalUrl) {
		try {

			HashMap<Integer, String> chapters = new HashMap<Integer, String>();
			ArrayList<Integer> pages = new ArrayList<Integer>();

			/** Call the split method with filename and page size as params **/
			PdfReader reader = new PdfReader(newLocalUrl);
			reader.consolidateNamedDestinations();
			List<HashMap<String, Object>> list = SimpleBookmark.getBookmark(reader);

			for (HashMap<String, Object> test : list) {
				String page = test.get(PAGE).toString();
				Integer num = Integer.parseInt(page.substring(0, page.indexOf(' ')));
				chapters.put(num, (String) test.get(_TITLE));
				pages.add(num);
			}

			int index = 1;
			List<String> chaptersUrls = new ArrayList<String>();
			for (Integer i : pages) {
				String chapterUrl = null;
				if (pages.size() != index) {
					chapterUrl = splitAndSaveChapter(newLocalUrl, i, pages.get(index), chapters.get(i));
				} else {
					chapterUrl = splitAndSaveChapter(newLocalUrl, i, reader.getNumberOfPages(), chapters.get(i));
				}
				index++;

				if (chapterUrl != null) {
					chaptersUrls.add(chapterUrl);
				}
			}
			return chaptersUrls;
		} catch (Exception ex) {
			return new ArrayList<String>();
		}
	}

	/**
	 * @param mainFileUrl
	 *            : PDF file that has to be splitted
	 * @param splittedPageSize
	 *            : Page size of each splitted files
	 */
	private static String splitAndSaveChapter(String mainFileUrl, int pageBeginNum, int pageEndNum, String name) {
		try {
			PdfReader reader = new PdfReader(mainFileUrl);

			int splittedPageSize = pageEndNum - pageBeginNum + 1;
			int pageNum = pageBeginNum;

			String chapterUrl = mainFileUrl.substring(0, mainFileUrl.indexOf(DOT_PDF)) + "-" + name + DOT_PDF;

			Document document = new Document(reader.getPageSizeWithRotation(1));

			FileOutputStream fos = new FileOutputStream(chapterUrl);
			PdfCopy writer = new PdfCopy(document, fos);
			Map<String, String> info = reader.getInfo();

			document.open();
			if ((info != null) && (info.get(_AUTHOR) != null)) {
				document.addAuthor(info.get(_AUTHOR));
			}

			document.addTitle(name);

			for (int offset = 0; offset < splittedPageSize && (pageNum + offset) < pageEndNum; offset++) {
				PdfImportedPage page = writer.getImportedPage(reader, pageNum + offset);
				writer.addPage(page);
			}

			document.close();
			writer.close();
			return chapterUrl;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void orderCollectionResourceInstances() {

		List<String> segments = this.getResourceRepository().getUnorderedInstanceSegments();

		if (segments != null && segments.size() > 0) {
			for (String segmentId : segments) {

				List<ResourceInstance> instances = this.getResourceRepository().getUnorderedInstances(segmentId);
				if (instances != null) {
					if (instances != null) {
						for (ResourceInstance instance : instances) {

							for (ResourceInstance compareInstance : instances) {
								if (!instance.getResourceInstanceId().equals(compareInstance.getResourceInstanceId())) {
									if (instance.getSequence().equals(compareInstance.getSequence())) {
										compareInstance.setSequence(compareInstance.getSequence() + 1);
									}
								}
							}

						}
					}
				}
				this.getResourceRepository().saveAll(instances);
			}
		}
	}

	@Override
	public void deleteResourceFromGAT(String gooruContentId, boolean isThirdPartyUser, User apiCaller, boolean isMycontent) {
		/*
		 * Resource resource =
		 * this.getResourceRepository().findResourceByContentGooruId
		 * (gooruContentId);
		 * this.getResourceRepository().retriveAndSetInstances(resource);
		 * List<ResourceInstance> resourceInstanceList =
		 * resource.getResourceInstances(); if (resourceInstanceList != null &&
		 * resourceInstanceList.size() > 0) {
		 * this.getResourceRepository().removeAll(resourceInstanceList); }
		 * indexProcessor.index(resource.getGooruOid(), IndexProcessor.DELETE,
		 * "resource"); this.getResourceRepository().remove(Resource.class,
		 * resource.getContentId());
		 */
		Resource resource = this.getResourceRepository().findResourceByContentGooruId(gooruContentId);
		Content content = this.contentRepository.findContentByGooruId(resource.getGooruOid());
		if (resource != null && isThirdPartyUser && apiCaller != null) {
			if (shelfService.hasContentSubscribed(apiCaller, resource.getGooruOid())) {
				subscriptionRepository.deleteSubscription(apiCaller.getUserUid(), resource.getGooruOid());
				UserContentRelationshipUtil.deleteUserContentRelationship(content, apiCaller, RELATIONSHIP.SUBSCRIBE);
				shelfService.deleteShelfSubscribeUserList(resource.getGooruOid(), apiCaller.getGooruUId());
			}

			if (isMycontent) {
				UserContentRelationshipUtil.deleteUserContentRelationship(content, apiCaller, RELATIONSHIP.CREATE);
			}
			if (resource != null && resource.getUser().getGooruUId().equalsIgnoreCase(apiCaller.getGooruUId()) && resource.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing())) {
				User user = userRepository.findByGooruId(configSettingRepository.getConfigSetting(DEFAULT_ADMIN_USER, 0, apiCaller.getOrganization().getPartyUid()));
				resource.setUser(user);
				this.getResourceRepository().saveOrUpdate(resource);
			}

			List<Learnguide> collectionList = this.getLearnguideRepository().findByResource(resource.getGooruOid(), Sharing.PRIVATE.getSharing());
			if (collectionList != null && collectionList.size() > 0) {
				List<ResourceInstance> instanceList = new ArrayList<ResourceInstance>();
				Set<ResourceInstance> resourceInstanceList = null;
				Set<Segment> segments = null;
				for (Learnguide learnguide : collectionList) {
					segments = learnguide.getResourceSegments();
					for (Segment segment : segments) {
						resourceInstanceList = segment.getResourceInstances();
						for (ResourceInstance resourceInstance : resourceInstanceList) {
							if (resourceInstance.getResource() != null) {
								if (resourceInstance.getResource().getGooruOid().equals(resource.getGooruOid())) {
									instanceList.add(resourceInstance);
								}
							}
						}
					}
				}
				if (instanceList.size() > 0) {
					this.getResourceRepository().removeAll(instanceList);
				}
			}

		} else {
			deleteResource(resource);
		}

	}

	@Override
	public Resource saveResource(Resource resource, Errors errors, boolean findByURL) {
		if (resource.getUser() == null) {
			errors.reject(USER, "User is required");
		}

		if (errors.hasErrors()) {
			return null;
		}

		if (resource.getContentId() == null) {
			resource.setGooruOid(UUID.randomUUID().toString());
		}
		if (resource.getSharing() == null) {
			resource.setSharing(Sharing.PRIVATE.getSharing());
		}
		if (resource.getContentType() == null) {
			ContentType contentTypeResource = (ContentType) this.getBaseRepository().get(ContentType.class, ContentType.RESOURCE);
			resource.setContentType(contentTypeResource);
		}
		if (resource.getResourceType() == null) {
			ResourceType resourceType = (ResourceType) this.getBaseRepository().get(ResourceType.class, ResourceType.Type.RESOURCE.getType());
			resource.setResourceType(resourceType);
		}

		if (resource.getCreatedOn() == null) {
			resource.setCreatedOn(new Date());
		}
		resource.setLastModified(new Date());
		this.getResourceRepository().saveOrUpdate(resource);
		return resource;
	}

	@Override
	public String updateResourceImage(String gooruContentId, String fileName) throws IOException {

		Resource resource = this.getResourceRepository().findResourceByContentGooruId(gooruContentId);
		this.getResourceImageUtil().moveFileAndSendMsgToGenerateThumbnails(resource, fileName, true);
		return resource.getOrganization().getNfsStorageArea().getAreaPath() + resource.getFolder() + "/" + resource.getThumbnail();
	}

	@Override
	public void deleteResourceImage(String gooruContentId) {
		Resource resource = this.getResourceRepository().findResourceByContentGooruId(gooruContentId);
		final String repositoryPath = resource.getOrganization().getNfsStorageArea().getInternalPath();
		File classplanDir = new File(repositoryPath + resource.getFolder());

		if (classplanDir.exists()) {

			String prevFileName = resource.getThumbnail();

			if (prevFileName != null && !prevFileName.equalsIgnoreCase("")) {
				File prevFile = new File(classplanDir.getPath() + "/" + prevFileName);
				if (prevFile.exists()) {
					prevFile.delete();
					this.getAsyncExecutor().deleteResourceFile(resource, resource.getThumbnail());
				}
			}

			resource.setThumbnail(null);
			this.getResourceRepository().save(resource);
			indexProcessor.index(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE);
		}
	}

	@Override
	public void deleteResourceBulk(String contentIds) {
		this.getResourceRepository().deleteResourceBulk(contentIds);
		indexProcessor.index(contentIds, IndexProcessor.DELETE, RESOURCE);
	}

	@Override
	public void updateResourceInstanceMetaData(Resource resource, User user) {
		List<ResourceInstance> resourceInstanceList = null;
		if ((resource.getSharing() != null && resource.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()))) {
			resourceInstanceList = this.getResourceRepository().findResourceInstances(resource.getGooruOid(), null);
		} else {
			resourceInstanceList = this.getResourceRepository().findResourceInstances(resource.getGooruOid(), resource.getUser().getGooruUId());
		}
		for (ResourceInstance resourceInstances : resourceInstanceList) {
			if (resource.getTitle() != null) {
				resourceInstances.setTitle(resource.getTitle());
			}
			if (resource.getDescription() != null) {
				resourceInstances.setDescription(resource.getDescription());
				resource.setDescription(resource.getDescription());
			}
			resourceInstances.setResource(resource);

		}
		this.getBaseRepository().saveAll(resourceInstanceList);
	}

	@Override
	public ResourceInstance buildResourceInstance(Map<String, Object> resourceParam, Map<String, Object> formField) throws Exception {
		ResourceInstance resourceInstance = null;
		if (resourceParam.get(GOORU_CONTENT_ID) != null) {
			if (resourceParam.get(RESOURCE_URL) != null) {

				if (shortenedUrlResourceCheck((String) resourceParam.get(RESOURCE_URL))) {
					throw new Exception("Cannot able to upload shortened URL resource.");
				}
			}
			ResourceSource resourceSource = null;
			Resource resource = null;
			String domainName = null;
			if (resourceParam.get(GOORU_CONTENT_ID) != null) {
				domainName = getDomainName((String) resourceParam.get(RESOURCE_URL));
				resourceSource = this.getResourceRepository().findResourceSource(domainName);
				if ((resourceSource != null) && (resourceSource.getIsBlacklisted() == 1)) {
					throw new Exception("Domain has been Blacklisted.");
				}
			}

			Learnguide collection = this.getLearnguideRepository().findByContent((String) resourceParam.get(GOORU_CONTENT_ID));
			getResourceRepository().save(collection);
			if (collection != null) {
				User user = resourceParam.get(USER) != null ? (User) resourceParam.get(USER) : null;
				if (collectionUtil.hasCollaboratorPermission(collection, user) || (collection.getUser() != null && collection.getUser().getGooruUId().equalsIgnoreCase(user.getGooruUId())) || hasUnrestrictedContentAccess(user)) {
					this.getSessionActivityService().updateSessionActivityByContent(collection.getGooruOid(), SessionActivityType.Status.ARCHIVE.getStatus());

					final String resourceTypeName = this.getResourceType(resourceParam.get(RESOURCE_TYPE).toString().trim()).getName();
					String resourceUrl = resourceParam.get(RESOURCE_URL) != null ? resourceParam.get(RESOURCE_URL).toString().trim() : "";
					String category = resourceParam.get(CATEGORY) != null ? resourceParam.get(CATEGORY).toString().trim() : null;
					boolean uploadedResource = !(resourceTypeName.equals(ResourceType.Type.VIDEO.getType()) || resourceTypeName.equals(ResourceType.Type.RESOURCE.getType()));
					boolean reusedResource = (resourceParam.get(REUSED) != null && (resourceParam.get(REUSED).toString().equalsIgnoreCase(SUGGEST) || resourceParam.get(REUSED).toString().equalsIgnoreCase(MY)));
					byte[] data = null;
					FileMeta fileMeta = null;
					boolean isUpdateTextbook = false;
					String fileHash = null;
					boolean hasNewResource = false;
					String resourceInstanceId = resourceParam.get(RESOURCE_INSTANCE_ID) != null ? resourceParam.get(RESOURCE_INSTANCE_ID).toString() : null;
					String updateNarrative = resourceParam.get(UPDATE_NARRATIVE) != null ? resourceParam.get(UPDATE_NARRATIVE).toString() : null;
					if (resourceParam.get(RESOURCE_INSTANCE_ID) != null && reusedResource) {
						resource = this.findResourceByContentGooruId((String) resourceParam.get(RESOURCE_INSTANCE_ID));
					} else {
						if (resourceInstanceId != null) {
							resourceInstance = this.getResourceInstance((String) resourceParam.get(RESOURCE_INSTANCE_ID));
							if (resourceInstance != null) {
								if (resourceTypeName.equals(ResourceType.Type.TEXTBOOK.getType())) {
									resource = getResourceRepository().findTextbookByContentGooruId(resourceInstance.getResource().getGooruOid());
									if (resource == null) {
										resource = getResourceRepository().findResourceByContentGooruId(resourceInstance.getResource().getGooruOid());
										if (resource != null) {
											ResourceType resourceType = new ResourceType();
											resourceType.setName(resourceTypeName);
											resource.setResourceType(resourceType);

											// Save textbook
											resourceRepository.saveTextBook(resource.getContentId(), "", "");
											Textbook textbook = resourceRepository.findTextbookByContentGooruIdWithNewSession(resource.getGooruOid());
											if (textbook != null) {
												textbook.setDocumentKey("");
												resourceRepository.save(textbook);
											}
											isUpdateTextbook = true;
										}
									}
								} else {
									resource = getResourceRepository().findResourceByContentGooruId(resourceInstance.getResource().getGooruOid());
								}
							}
						} else {
							if (!resourceUrl.isEmpty() && (resourceTypeName.equalsIgnoreCase(ResourceType.Type.VIDEO.getType()) || resourceTypeName.equalsIgnoreCase(ResourceType.Type.RESOURCE.getType()))) {
								resource = this.getResourceRepository().findResourceByUrl(resourceUrl, Sharing.PUBLIC.getSharing(), null);
								if (resource == null) {
									resource = this.getResourceRepository().findResourceByUrl(resourceUrl, Sharing.PRIVATE.getSharing(), user.getGooruUId());
								}
							}
						}
					}
					Segment segment = (Segment) this.getResourceRepository().get(Segment.class, (String) resourceParam.get(SEGMENT_ID));
					if (resource == null) {
						if (!reusedResource && uploadedResource) {
							fileMeta = FileProcessor.extractFileData((HttpServletRequest) resourceParam.get(REQUEST), formField, UserGroupSupport.getUserOrganizationNfsInternalPath());
							if (fileMeta != null) {
								data = fileMeta.getFileData();
								resourceUrl = fileMeta.getOriginalFilename();
							}
						}
						fileHash = BaseUtil.getByteMD5Hash(data);
						if (fileHash != null) {
							resource = getResourceRepository().findByFileHash(fileHash, resourceTypeName, resourceUrl, category);
						}
					}
					if (!updateNarrative.equalsIgnoreCase(ONE) && resourceInstanceId != null && resource != null && resource.getSharing() != null && resource.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()) && !reusedResource) {
						throw new AccessDeniedException("This is public resource, do  not have premission to edit this resource, edit via GAT");
					}
					String sharing = collection.getSharing();
					String resourceTitle = resourceParam.get(RESOURCE_TITLE) != null ? resourceParam.get(RESOURCE_TITLE).toString() : "";
					resourceTitle = resourceTitle.length() > 1000 ? resourceTitle.substring(0, 1000) : resourceTitle;
					String description = resourceParam.get(DESCRIPTION) != null ? resourceParam.get(DESCRIPTION).toString() : "";
					String start = resourceParam.get(START) != null ? resourceParam.get(START).toString().trim() : null;
					String stop = resourceParam.get(STOP) != null ? resourceParam.get(STOP).toString().trim() : null;
					String narrative = resourceParam.get(NARRATIVE) != null ? resourceParam.get(NARRATIVE).toString().trim() : null;
					String thumbnailImgSrc = resourceParam.get(THUMBNAIL_IMG_SRC) != null ? resourceParam.get(THUMBNAIL_IMG_SRC).toString().trim() : null;
					resourceSource = null;
					if (resource == null) {
						hasNewResource = true;
						if (resourceTypeName.equals(ResourceType.Type.TEXTBOOK.getType())) {
							resource = new Textbook();
							((Textbook) resource).setDocumentId("");
							((Textbook) resource).setDocumentKey("");
						} else {
							resource = new Resource();
						}
						resource.setGooruOid(UUID.randomUUID().toString());
						resource.setUser(user);
						License license = new License();
						license.setName(OTHER);
						resource.setLicense(license);
						resource.setRecordSource(Resource.RecordSource.COLLECTION.getRecordSource());
						ResourceType resourceType = new ResourceType();
						resourceType.setName(resourceTypeName);
						resource.setResourceType(resourceType);
						resource.setUrl(resourceUrl);
						domainName = getDomainName((String) resourceParam.get(RESOURCE_URL));
						resourceSource = this.getResourceRepository().findResourceSource(domainName);
						if (resourceSource != null && resourceSource.getFrameBreaker() != null && resourceSource.getFrameBreaker() == 1) {
							resource.setHasFrameBreaker(true);
						} else {
							resource.setHasFrameBreaker(false);
						}

					}
					if (resourceInstance == null) {
						resourceInstance = new ResourceInstance(segment, resource);
						resourceInstance.setResourceInstanceId(UUID.randomUUID().toString());
					}
					if (reusedResource) {
						resourceTitle = StringUtils.defaultIfEmpty(resourceTitle, resource.getTitle());
						description = StringUtils.defaultIfEmpty(description, resource.getDescription());
					}
					if (!updateNarrative.equalsIgnoreCase(ONE) && (resource.getSharing() == null || !resource.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()))) {
						if (!resourceTitle.isEmpty()) {
							resource.setTitle(resourceTitle);
						}
						if (!description.isEmpty()) {
							resource.setDescription(description);
						}
						if (category != null) {
							resource.setCategory(category);
						}
						SessionContextSupport.putLogParameter("sharing-" + resource.getGooruOid(), resource.getSharing() + " to " + sharing);
						resource.setSharing(sharing);
					}
					if (resource != null && resource.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing())) {
						resourceTitle = resource.getTitle();
						description = resource.getDescription();
					}
					if (resourceTitle != null && !resourceTitle.isEmpty()) {
						resourceInstance.setTitle(resourceTitle);
					}
					if (description != null && !description.isEmpty()) {
						resourceInstance.setDescription(description);
					}
					if (start != null) {
						resourceInstance.setStart(start);
					}
					if (stop != null) {
						resourceInstance.setStop(stop);
					}
					if (updateNarrative.equalsIgnoreCase(ONE)) {
						resourceInstance.setNarrative(narrative);
					}
					Set<Code> taxonomyCode = new HashSet<Code>();
					Iterator<Code> iter = collection.getTaxonomySet().iterator();
					while (iter.hasNext()) {
						Code code = iter.next();
						taxonomyCode.add(code);
					}
					// Add original resource taxonomy set as well to resource.
					// If a resource
					// is used in two different collections, taxonomy of
					// both collections would apply to resource
					Set<Code> originalTaxonomySet = resource.getTaxonomySet();
					if (originalTaxonomySet != null) {
						taxonomyCode.addAll(originalTaxonomySet);
					}
					resource.setTaxonomySet(taxonomyCode);
					CollectionServiceUtil.resetInstancesSequence(segment);
					this.getResourceRepository().save(segment);
					if (resourceInstance.getSequence() == null) {
						resourceInstance.setSequence(segment.getResourceInstances().size() + 1);
					}
					this.saveResourceInstance(resourceInstance);
					this.updateResourceInstanceMetaData(resource, user);

					// Check if the resource has files
					String resourceFolderPath = resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder();
					String resourceFilePath = resourceFolderPath + resource.getUrl();
					if (!new File(resourceFilePath).exists() && data != null && data.length > 0 && fileMeta != null) {
						resource.setFileData(data);
						resource.setUrl(fileMeta.getOriginalFilename());
						if (resource.getFileHash() == null && uploadedResource) {
							resource.setFileHash(fileHash);
						}
						this.getResourceManager().saveResource(resource);
						this.getResourceRepository().save(resource);
						this.getAsyncExecutor().uploadResourceFile(resource, resource.getUrl());
					}
					this.saveResourceInstance(resourceInstance);

					// insert resource url status for new resource
					if (hasNewResource) {
						ResourceUrlStatus urlStatus = new ResourceUrlStatus();
						urlStatus.setResource(resource);
						this.getResourceRepository().save(urlStatus);
					}

					updateYoutubeResourceFeeds(resource, false);

					// update or insert thumbnail
					if (!reusedResource) {
						if ((sharing != null && !sharing.equalsIgnoreCase(Sharing.PUBLIC.getSharing())) || hasUnrestrictedContentAccess(user)) {
							if (thumbnailImgSrc != null && thumbnailImgSrc.length() > 0) {
								this.getResourceImageUtil().downloadAndSendMsgToGenerateThumbnails(resourceInstance.getResource(), thumbnailImgSrc);
							}
						}
					}
					this.getResourceImageUtil().setDefaultThumbnailImageIfFileNotExist(resource);
					this.mapSourceToResource(resource);

					if (resourceTypeName.equals(ResourceType.Type.HANDOUTS.getType()) || resourceTypeName.equals(ResourceType.Type.EXAM.getType()) || resourceTypeName.equals(ResourceType.Type.PRESENTATION.getType())) {
						Map<String, Object> param = new HashMap<String, Object>();
						param.put(RESOURCE_FILE_PATH, resourceFilePath);
						param.put(RESOURCE_GOORU_OID, resource.getGooruOid());
						RequestUtil.executeRestAPI(param, settingService.getConfigSetting(ConfigConstants.GOORU_CONVERSION_RESTPOINT, 0, TaxonomyUtil.GOORU_ORG_UID) + "/conversion/pdf-to-image", Method.POST.getName());

					} else if (resourceTypeName.equals(ResourceType.Type.TEXTBOOK.getType()) && (!isUpdateTextbook && StringUtils.isEmpty(((Textbook) resource).getDocumentId()))) {
						Map<String, String> keys = this.getResourceManager().saveScridbDocument(this.getSettingService().getConfigSetting(ConfigConstants.SCRIBD_API_KEY, 0, resource.getOrganization().getPartyUid()),
								this.getSettingService().getConfigSetting(ConfigConstants.TEXTBOOK_DOCUMENT, 0, resource.getOrganization().getPartyUid()));
						((Textbook) resource).setDocumentId(keys.get(DOCUMENT__ID));
						((Textbook) resource).setDocumentKey(keys.get(DOCUMENT_KEY));
						Map<String, Object> param = new HashMap<String, Object>();
						param.put(SCRIBD_API_KEY, this.getSettingService().getConfigSetting(ConfigConstants.SCRIBD_API_KEY, 0, resource.getOrganization().getPartyUid()));
						param.put(DOC_KEY, keys.get(DOCUMENT__ID));
						param.put(THUMBNAIL, resource.getThumbnail());
						param.put(RESOURCE_FILE_PATH, resourceFilePath);
						param.put(RESOURCE_GOORU_OID, resource.getGooruOid());
						RequestUtil.executeRestAPI(param, settingService.getConfigSetting(ConfigConstants.GOORU_CONVERSION_RESTPOINT, 0, TaxonomyUtil.GOORU_ORG_UID) + "/conversion/textbook/upload", Method.POST.getName());
					}

					// Give permission to the collaborators of this collection
					// to access this resource.
					if (resourceInstanceId == null) {
						updateCollaborators(collection, resource, user);
					}
					if (resource != null && resource.getContentId() != null) {
						indexProcessor.index(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE);
					}
					indexProcessor.index(collection.getGooruOid(), IndexProcessor.INDEX, COLLECTION);

					// Remove the collection from cache
					this.getCollectionUtil().deleteCollectionFromCache(collection.getGooruOid(), COLLECTION);

					try {
						revisionHistoryService.createVersion(collection, SEGMENT_UPDATE);
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					if (logger.isInfoEnabled()) {
						logger.info(LogUtil.getActivityLogStream(COLLECTION, user.toString(), collection.toString(), (resourceInstanceId == null) ? LogUtil.RESOURCE_ADD : LogUtil.RESOURCE_EDIT, "name:" + resourceTitle + "$type:" + resourceTypeName + "$url " + resourceUrl + "$classplan:"
								+ collection.getLesson()));
					}
				} else {
					throw new AccessDeniedException("Do not have permission to customize this collection");
				}
			} else {
				throw new NotFoundException("collection does not exist in the system, required collection to map the resource");
			}

		}
		return resourceInstance;
	}

	@Override
	public Resource buildMyContent(Map<String, Object> resourceParam, Map<String, Object> formField) throws Exception {
		Resource resource = null;
		User user = resourceParam.get(USER) != null ? (User) resourceParam.get(USER) : null;
		final String resourceTypeName = this.getResourceType(resourceParam.get(RESOURCE_TYPE).toString().trim()).getName();
		String resourceUrl = resourceParam.get(RESOURCE_URL) != null ? resourceParam.get(RESOURCE_URL).toString().trim() : "";
		String category = resourceParam.get(CATEGORY) != null ? resourceParam.get(CATEGORY).toString().trim() : null;
		boolean uploadedResource = !(resourceTypeName.equals(ResourceType.Type.VIDEO.getType()) || resourceTypeName.equals(ResourceType.Type.RESOURCE.getType()));
		boolean isUpdateTextbook = false;
		boolean reusedResource = (resourceParam.get(REUSED_RESOURCE_ID) != null);
		byte[] data = null;
		FileMeta fileMeta = null;
		String fileHash = null;
		boolean hasNewResource = false;
		String license1 = resourceParam.get(LICENSE) != null ? resourceParam.get(LICENSE).toString().trim() : null;
		String batchId = resourceParam.get(BATCH_ID) != null ? resourceParam.get(BATCH_ID).toString().trim() : null;
		String sharing = resourceParam.get(SHARING) != null ? resourceParam.get(SHARING).toString().trim() : null;
		String updateNarrative = resourceParam.get(UPDATE_NARRATIVE) != null ? resourceParam.get(UPDATE_NARRATIVE).toString() : null;
		if (resourceParam.get(REUSED_RESOURCE_ID) != null && reusedResource) {
			resource = this.findResourceByContentGooruId((String) resourceParam.get(REUSED_RESOURCE_ID));
		}
		if (resource != null) {
			if (resourceTypeName.equals(ResourceType.Type.TEXTBOOK.getType())) {
				resource = getResourceRepository().findTextbookByContentGooruId(resource.getGooruOid());
				if (resource == null) {
					resource = getResourceRepository().findResourceByContentGooruId((String) resourceParam.get(REUSED_RESOURCE_ID));
					if (resource != null) {
						ResourceType resourceType = new ResourceType();
						resourceType.setName(resourceTypeName);
						resource.setResourceType(resourceType);

						// Save textbook
						resourceRepository.saveTextBook(resource.getContentId(), "", "");
						Textbook textbook = resourceRepository.findTextbookByContentGooruIdWithNewSession(resource.getGooruOid());
						if (textbook != null) {
							textbook.setDocumentKey("");
							resourceRepository.save(textbook);
						}
						isUpdateTextbook = true;
					}
				}
			}

		} else {
			if (!resourceUrl.isEmpty() && (resourceTypeName.equalsIgnoreCase(ResourceType.Type.VIDEO.getType()) || resourceTypeName.equalsIgnoreCase(ResourceType.Type.RESOURCE.getType()))) {
				resource = this.getResourceRepository().findResourceByUrl(resourceUrl, Sharing.PUBLIC.getSharing(), user.getGooruUId());
				if (resource == null) {
					resource = this.getResourceRepository().findResourceByUrl(resourceUrl, Sharing.PRIVATE.getSharing(), user.getGooruUId());
				}
			}
		}
		if (resource == null) {
			if (uploadedResource) {
				fileMeta = FileProcessor.extractFileData((HttpServletRequest) resourceParam.get(REQUEST), formField, UserGroupSupport.getUserOrganizationNfsInternalPath());
				if (fileMeta != null) {
					data = fileMeta.getFileData();
					resourceUrl = fileMeta.getOriginalFilename();
				}
			}
			fileHash = BaseUtil.getByteMD5Hash(data);
			if (fileHash != null) {
				resource = getResourceRepository().findByFileHash(fileHash, resourceTypeName, resourceUrl, category);
			}
		}
		String resourceTitle = resourceParam.get(RESOURCE_TITLE) != null ? resourceParam.get(RESOURCE_TITLE).toString() : "";
		resourceTitle = resourceTitle.length() > 1000 ? resourceTitle.substring(0, 1000) : resourceTitle;
		String description = resourceParam.get(DESCRIPTION) != null ? resourceParam.get(DESCRIPTION).toString() : "";
		String thumbnailImgSrc = resourceParam.get(THUMBNAIL_IMG_SRC) != null ? resourceParam.get(THUMBNAIL_IMG_SRC).toString().trim() : null;
		if (resource == null) {
			hasNewResource = true;
			if (resourceTypeName.equals(ResourceType.Type.TEXTBOOK.getType())) {
				resource = new Textbook();
				((Textbook) resource).setDocumentId("");
				((Textbook) resource).setDocumentKey("");
			} else {
				resource = new Resource();
			}
			resource.setGooruOid(UUID.randomUUID().toString());
			resource.setUser(user);
			resource.setRecordSource(Resource.RecordSource.COLLECTION.getRecordSource());
			ResourceType resourceType = new ResourceType();
			resourceType.setName(resourceTypeName);
			resource.setResourceType(resourceType);
			resource.setUrl(resourceUrl);
		}
		if (!updateNarrative.equalsIgnoreCase("1") && (resource.getSharing() == null || !resource.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()))) {
			if (!resourceTitle.isEmpty()) {
				resource.setTitle(resourceTitle);
			}
			if (description != null && !description.isEmpty()) {
				resource.setDescription(description);
			}
			SessionContextSupport.putLogParameter("sharing-" + resource.getGooruOid(), resource.getSharing() + " to " + sharing);
			resource.setSharing(sharing);
		}
		if (category != null) {
			resource.setCategory(category);
		}
		if (resource != null && resource.getSharing() != null && resource.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing())) {
			resourceTitle = resource.getTitle();
			description = resource.getDescription();
		}
		if (resourceTitle != null && !resourceTitle.isEmpty()) {
			resource.setTitle(resourceTitle);
		}
		if (description != null && !description.isEmpty()) {
			resource.setDescription(description);
		}
		if (sharing != null && (sharing.equalsIgnoreCase(Sharing.PUBLIC.getSharing()) || sharing.equalsIgnoreCase(Sharing.PRIVATE.getSharing()))) {
			resource.setSharing(sharing);
		}
		if (batchId != null) {
			resource.setBatchId(batchId);
		}
		License license = null;
		if (license1 != null) {
			license = new License();
			license.setName(license1);
			resource.setLicense(license);
		} else {
			license = new License();
			license.setName(OTHER);
			resource.setLicense(license);
		}
		Errors errors = new BindException(Resource.class, RESOURCE);
		this.saveResource(resource, errors, hasNewResource);
		this.updateResourceInstanceMetaData(resource, user);

		// Check if the resource has files
		String resourceFolderPath = resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder();
		String resourceFilePath = resourceFolderPath + resource.getUrl();
		if (!new File(resourceFilePath).exists() && data != null && data.length > 0 && fileMeta != null) {
			resource.setFileData(data);
			resource.setUrl(fileMeta.getOriginalFilename());
			if (resource.getFileHash() == null && uploadedResource) {
				resource.setFileHash(fileHash);
			}
			this.getResourceManager().saveResource(resource);
			this.getResourceRepository().save(resource);
			this.getAsyncExecutor().uploadResourceFile(resource, resource.getUrl());
		}

		// update or insert thumbnail
		if (!reusedResource) {
			if ((sharing != null && !sharing.equalsIgnoreCase(Sharing.PUBLIC.getSharing())) || hasUnrestrictedContentAccess(user)) {
				if (thumbnailImgSrc != null && thumbnailImgSrc.length() > 0) {
					this.getResourceImageUtil().downloadAndSendMsgToGenerateThumbnails(resource, thumbnailImgSrc);
				}
			}
		}
		this.getResourceImageUtil().setDefaultThumbnailImageIfFileNotExist(resource);
		this.mapSourceToResource(resource);

		if (resourceTypeName.equals(ResourceType.Type.HANDOUTS.getType()) || resourceTypeName.equals(ResourceType.Type.EXAM.getType()) || resourceTypeName.equals(ResourceType.Type.PRESENTATION.getType())) {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(RESOURCE_FILE_PATH, resourceFilePath);
			param.put(RESOURCE_GOORU_OID, resource.getGooruOid());
			RequestUtil.executeRestAPI(param, settingService.getConfigSetting(ConfigConstants.GOORU_CONVERSION_RESTPOINT, 0, TaxonomyUtil.GOORU_ORG_UID) + "/conversion/pdf-to-image", Method.POST.getName());

		} else if (resourceTypeName.equals(ResourceType.Type.TEXTBOOK.getType()) && StringUtils.isEmpty(((Textbook) resource).getDocumentId())) {
			Map<String, String> keys = this.getResourceManager().saveScridbDocument(this.getSettingService().getConfigSetting(ConfigConstants.SCRIBD_API_KEY, 0, resource.getOrganization().getPartyUid()),
					this.getSettingService().getConfigSetting(ConfigConstants.TEXTBOOK_DOCUMENT, 0, resource.getOrganization().getPartyUid()));
			((Textbook) resource).setDocumentId(keys.get(DOCUMENT__ID));
			((Textbook) resource).setDocumentKey(keys.get(DOCUMENT_KEY));
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(SCRIBD_API_KEY, this.getSettingService().getConfigSetting(ConfigConstants.SCRIBD_API_KEY, 0, resource.getOrganization().getPartyUid()));
			param.put(DOC_KEY, keys.get(DOCUMENT__ID));
			param.put(THUMBNAIL, resource.getThumbnail());
			param.put(RESOURCE_FILE_PATH, resourceFilePath);
			param.put(RESOURCE_GOORU_OID , resource.getGooruOid());
			RequestUtil.executeRestAPI(param, settingService.getConfigSetting(ConfigConstants.GOORU_CONVERSION_RESTPOINT, 0, TaxonomyUtil.GOORU_ORG_UID) + "/conversion/textbook/upload", Method.POST.getName());
		}

		// Give permission to the collaborators of this collection
		// to access this resource.
		if (resource != null && resource.getContentId() != null) {
			indexProcessor.index(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE);
		}

		return resource;
	}

	@Override
	public void replaceDuplicatePrivateResourceWithPublicResource(Resource resource) {
		if (resource.getResourceType() != null && (resource.getResourceType().getName().equalsIgnoreCase(ResourceType.Type.VIDEO.getType()) || resource.getResourceType().getName().equalsIgnoreCase(ResourceType.Type.RESOURCE.getType()))) {
			if (resource.getSharing() != null && resource.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing())) {
				List<Resource> resourceDuplicate = this.getResourceRepository().getResourceListByUrl(resource.getUrl(), Sharing.PRIVATE.getSharing(), null);
				if (resourceDuplicate != null) {
					for (Resource duplicateResource : resourceDuplicate) {
						List<ResourceInstance> resourceInstances = this.getResourceRepository().findResourceInstances(duplicateResource.getGooruOid(), null);
						for (ResourceInstance resourceInstance : resourceInstances) {
							resourceInstance.setResource(resource);
						}
						this.getResourceRepository().saveAll(resourceInstances);
						indexProcessor.index(duplicateResource.getGooruOid(), IndexProcessor.INDEX,RESOURCE);
						this.getResourceRepository().remove(Resource.class, duplicateResource.getContentId());
					}
				}
			}
		}
	}

	@Override
	public void setDefaultThumbnail(String contentType, int batchSize, int pageSize) {
		if (contentType != null && contentType.equalsIgnoreCase(COLLECTION)) {
			Map<String, String> filters = new HashMap<String, String>();
			int totalPages, recordsPerPage = batchSize;
			totalPages = pageSize / recordsPerPage;
			for (int page = 1; page < totalPages; page++) {
				logger.info("Collection default thumbnail" + page + " of " + totalPages);
				filters.put(PAGE_NUM , page + "");
				filters.put(PAGE_SIZE, recordsPerPage + "");
				List<Learnguide> collectionList = this.getLearnguideRepository().listAllCollectionsWithoutGroups(filters);
				if (collectionList != null && collectionList.size() > 0) {
					for (Learnguide collection : collectionList) {
						this.getResourceImageUtil().setDefaultThumbnailImageIfFileNotExist((Resource) collection);
					}
				}
			}
		} else if (contentType != null && contentType.equalsIgnoreCase(QUIZ)) {
			Map<String, String> filters = new HashMap<String, String>();
			int totalPages, recordsPerPage = batchSize;
			totalPages = pageSize / recordsPerPage;
			for (int page = 1; page < totalPages; page++) {
				logger.info("quiz default thumbnail" + page + " of " + totalPages);
				filters.put(PAGE_NUM, page + "");
				filters.put(PAGE_SIZE, recordsPerPage + "");
				List<Assessment> assessments = this.getAssessmentRepository().listAllQuizsWithoutGroups(filters);
				for (Assessment quiz : assessments) {
					this.getResourceImageUtil().setDefaultThumbnailImageIfFileNotExist((Resource) quiz);
				}
			}
		} else if (contentType != null && contentType.equalsIgnoreCase(RESOURCE)) {
			Map<String, String> filters = new HashMap<String, String>();
			int totalPages, recordsPerPage = batchSize;
			totalPages = pageSize / recordsPerPage;
			for (int page = 1; page < totalPages; page++) {
				logger.info("resource default thumbnail" + page + " of " + totalPages);
				filters.put(PAGE_NUM, page + "");
				filters.put(PAGE_SIZE, recordsPerPage + "");
				List<Resource> resourceList = this.getResourceRepository().listAllResourceWithoutGroups(filters);
				for (Resource resource : resourceList) {
					this.getResourceImageUtil().setDefaultThumbnailImageIfFileNotExist(resource);
				}
			}
		}
	}

	public ResourceRepository getResourceRepository() {
		return resourceRepository;
	}

	public SegmentRepository getSegmentRepository() {
		return segmentRepository;
	}

	public ResourceParser getResourceParser() {
		return resourceParser;
	}

	public ResourceImageUtil getResourceImageUtil() {
		return resourceImageUtil;
	}

	public S3ResourceApiHandler getS3ResourceApiHandler() {
		return s3ResourceApiHandler;
	}

	public IndexProcessor getIndexerMessenger() {
		return indexProcessor;
	}

	public LearnguideRepository getLearnguideRepository() {
		return learnguideRepository;
	}

	public CollectionUtil getCollectionUtil() {
		return collectionUtil;
	}

	public MediaService getMediaService() {
		return mediaService;
	}

	public BaseRepository getBaseRepository() {
		return baseRepository;
	}

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	public SessionActivityService getSessionActivityService() {
		return sessionActivityService;
	}

	public void setSessionActivityService(SessionActivityService sessionActivityService) {
		this.sessionActivityService = sessionActivityService;
	}

	public AssessmentRepository getAssessmentRepository() {
		return assessmentRepository;
	}

	@Override
	public void incrementViews(String contentGooruId) {
		resourceRepository.incrementViews(contentGooruId);

	}

	@Override
	public ResourceSource updateSuggestAttribution(String gooruContentId, String attribution) {

		ResourceSource resourceSource = (ResourceSource) this.getBaseRepository().get(ResourceSource.class, contentRepository.findByContentGooruId(gooruContentId).getContentId());
		if (resourceSource != null) {
			resourceSource.setAttribution(attribution);
			this.getBaseRepository().save(resourceSource);
		}
		return resourceSource;
	}

	@Override
	public void deleteResource(Resource resource, String gooruContentId, User apiCaller) {
		if (resource == null) {
			resource = resourceRepository.findResourceByContentGooruId(gooruContentId);
			if (resource == null) {
				logger.warn("invalid resource passed to deleteResource:" + gooruContentId);
				return;
			}
		}
		UserRole contentAdmin = new UserRole();
		contentAdmin.setRoleId(Short.valueOf(UserRole.ROLE_CONTENT_ADMIN));

		User systemUser = userRepository.findByRole(contentAdmin).get(0);
		resource.setUser(systemUser);

		this.getBaseRepository().removeAll(resource.getContentPermissions());
		resource.setContentPermissions(null);
		resource.setLastModified(new Date(System.currentTimeMillis()));

		resourceRepository.saveOrUpdate(resource);
		logger.warn("Deleted resource from deleteResource:" + gooruContentId);

		if (logger.isInfoEnabled()) {
			logger.info(LogUtil.getActivityLogStream(RESOURCE, apiCaller.toString(), resource.toString(), LogUtil.RESOURCE_REMOVE, ""));
		}

		/* Step 4 - Send the message to reindex the resource */
		indexProcessor.index(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE);

	}

	@Override
	public void deleteAttribution(Resource resource, String gooruAttributionId, User apiCaller) {

		// if (resource == null) {
		// resource =
		// resourceRepository.findResourceByAttributionGooruId(gooruAttributionId);
		if (resource == null) {
			logger.warn("invalid resource passed to deleteResource:" + gooruAttributionId);
			return;
		}
		// }
		UserRole contentAdmin = new UserRole();
		contentAdmin.setRoleId(Short.valueOf(UserRole.ROLE_CONTENT_ADMIN));

		User systemUser = userRepository.findByRole(contentAdmin).get(0);
		resource.setUser(systemUser);

		this.getBaseRepository().removeAll(resource.getContentPermissions());
		resource.setContentPermissions(null);
		resource.setLastModified(new Date(System.currentTimeMillis()));

		resourceRepository.saveOrUpdate(resource);
		logger.warn("Deleted resource from deleteResource:" + gooruAttributionId);

		if (logger.isInfoEnabled()) {
			logger.info(LogUtil.getActivityLogStream(RESOURCE, apiCaller.toString(), resource.toString(), LogUtil.RESOURCE_REMOVE, ""));
		}

		/* Step 4 - Send the message to reindex the resource */
		indexProcessor.index(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE);
	}

	@Override
	public Job saveJob(File sourceFile, ConverterDTO converterDTO, User user) {

		Job job = new Job();
		job.setGooruOid(converterDTO.getGooruContentId());
		job.setUser(user);
		job.setFileSize(sourceFile.length());
		converterDTO.setSourcePath(sourceFile.getPath());
		String type = JobType.Type.PPTCONVERSION.getType();
		if (FileProcessor.getFileExt(sourceFile.getName()).equals(PDF)) {
			type = JobType.Type.PDFCONVERSION.getType();
		}
		job.setJobType((JobType) learnguideRepository.get(JobType.class, type));
		job.setStatus(Job.Status.INPROGRESS.getStatus());
		learnguideRepository.save(job);
		return job;
	}

	@Override
	public Job getResourceTaskJob(String taskId) {
		return (Job) learnguideRepository.get(Job.class, Integer.parseInt(taskId));
	}

	@Override
	public ResourceSource findResourceSource(String domainName) {
		return resourceRepository.findResourceSource(domainName);
	}

	@Override
	public ResourceInfo findResourceInfo(String resourceGooruOid) {
		return resourceRepository.findResourceInfo(resourceGooruOid);
	}

	@Override
	public Resource saveCrawledResource(String url, String title, String text, String parentUrl, String thumbnail, String attribution, String typeForPdf, String category, String siteName, String tags, String description) {
		Resource resource = new Resource();
		resource.setUrl(url);
		resource.setTitle(title);
		resource.setRecordSource(Resource.RecordSource.CRAWLED.getRecordSource());
		User user = userRepository.getUserByUserId(1);
		resource.setUser(user);
		resource.setParentUrl(parentUrl);
		resource.setDescription(description);
		resource.setCategory(category);
		resource.setSharing(Sharing.PRIVATE.getSharing());
		resource.setSiteName(siteName);

		resource = handleNewResource(resource, typeForPdf, thumbnail);
		ResourceInfo resourceInfo = findResourceInfo(resource.getGooruOid());
		if (resourceInfo == null) {
			resourceInfo = new ResourceInfo();
		}
		ResourceSource resourceSource = null;
		if (resource.getResourceSource() != null && attribution != null) {
			resource.getResourceSource().setAttribution(attribution);
			resourceSource = resource.getResourceSource();
		} else {
			String domainName = url.contains("http://") ? url.replace("http://", "") : url;
			int lastIndex = domainName.indexOf("/");
			if (lastIndex == -1) {
				lastIndex = domainName.length();
			}
			domainName = domainName.substring(0, lastIndex);
			resourceSource = findResourceSource(domainName);
			if (resourceSource == null) {
				resourceSource = new ResourceSource();
				resourceSource.setDomainName(domainName);
				resourceSource.setAttribution(attribution);
				resourceSource.setActiveStatus(1);
			}
			boolean hasFrameBreaker = false;
			if (resourceSource != null && resourceSource.getFrameBreaker() != null && resourceSource.getFrameBreaker() == 1) {
				hasFrameBreaker = true;
			}
			resource.setHasFrameBreaker(hasFrameBreaker);
			resource.setResourceSource(resourceSource);
			if (resourceSource != null) {
				this.resourceRepository.save(resourceSource);
			}
		}

		resourceInfo.setTags(tags);
		resource.setTags(tags);
		resourceInfo.setResource(resource);
		resourceInfo.setLastUpdated(new Date());
		this.resourceRepository.save(resourceInfo);
		resource.setResourceInfo(resourceInfo);
		this.resourceRepository.save(resource);
		return resource;
	}

	@Override
	public Resource findResourceByUrl(String resourceUrl, String sharing, String userUid) {
		return resourceRepository.findResourceByUrl(resourceUrl, sharing, userUid);
	}

	@Override
	public List<Resource> findWebResourcesForBlacklisting() {
		return resourceRepository.findWebResourcesForBlacklisting();
	}

	@Override
	public Resource addNewResource(String url, String title, String text, String category, String sharing, String type_name, String licenseName, Integer brokenStatus, Boolean hasFrameBreaker, String description, Integer isFeatured, String tags, boolean isReturnJson, User apiCaller, String mediaType, String resourceFormat, String resourceInstructional) {

		User user = null;
		// construct resource:
		Resource resource = new Resource();
		ResourceSource resourceSource = new ResourceSource();
		String domainName = null;
		resource.setUrl(url);
		resource.setTitle(title);
		resource.setCategory(category);
		resource.setIsFeatured(isFeatured);
		resource.setDescription(description);
		resource.setMediaType(mediaType);
		user = apiCaller;
		resource.setUser(user);
		
		if(resourceFormat != null){
			CustomTableValue customTableValue = this.getCustomTableRepository().getCustomTableValue(RESOURCE_CATEGORY_FORMAT,resourceFormat );
			resource.setResourceFormat(customTableValue);
		}
		
		if(resourceInstructional != null){
			CustomTableValue customTableValue = this.getCustomTableRepository().getCustomTableValue(RESOURCE_INSTRUCTIONAL_USE, resourceInstructional);
			resource.setInstructional(customTableValue);
		}

		// FIXME Use the appropriate record source here. the api method may be
		// called from elsewhere as well
		resource.setRecordSource(Resource.RecordSource.GAT.getRecordSource());

		if (type_name != null) {
			resource.setResourceTypeByString(ResourceType.Type.valueOf(type_name).getType());
		}
		resource.setSharing(sharing);
		/*
		 * if (licenseName != null) { resource.setLicense(licenseName); }
		 */

		if (brokenStatus != null) {
			resource.setBrokenStatus(brokenStatus);
		} else {
			resource.setBrokenStatus(0);
		}
		domainName = getDomainName(url);
		resourceSource = this.getResourceRepository().findResourceSource(domainName);
		if (resourceSource != null && resourceSource.getFrameBreaker() != null && resourceSource.getFrameBreaker() == 1) {
			resource.setHasFrameBreaker(true);
		} else {
			resource.setHasFrameBreaker(false);
		}
		// add to db and index.
		resource = handleNewResource(resource, null, null);
		ResourceInfo resourceInfo = new ResourceInfo();
		resourceInfo.setTags(tags);
		resourceInfo.setLastUpdated(new Date());
		resource.setTags(tags);
		resourceInfo.setResource(resource);
		resource.setResourceInfo(resourceInfo);

		resourceRepository.save(resourceInfo);
		s3ResourceApiHandler.updateOrganization(resource);

		this.updateResourceInstanceMetaData(resource, user);
		this.replaceDuplicatePrivateResourceWithPublicResource(resource);
		this.mapSourceToResource(resource);
		indexProcessor.index(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE);

		return resource;
	}

	// import CSV resource
	@Override
	public Resource importCSVResource(String url, String subject, String title, String tags, String description, String attribution, String thumbnail, String csvFileCode, String type, String grade, String content, String importMode, CsvCrawler csvCrawler, boolean isReturnJson, User user,
			boolean indexFlag, String mediaType) {

		// CsvCrawler csvCrawler = null;
		Resource resource = null;
		ResourceSource resourceSource = null;
		String domainName = null;
		if (type == null) {
			type = "Website";
		}

		if (url == null || type == null || title == null || !url.trim().startsWith(HTTP)) {
			// logData += "Stopped Import of file on row " + processedCount +
			// ": " + fileName + " Due to Invalid Url | type | title - > " + url
			// + "| " + type + "|" + title + "</br>";
			return null;
		}

		// csvCrawler = resourceRepository.getCsvCrawler(url, type);
		// if (csvCrawler == null) {
		csvCrawler = new CsvCrawler();
		csvCrawler.setType(type);
		csvCrawler.setUrl(url);
		// }
		csvCrawler.setLastModified(new Date());
		csvCrawler.setTitle(title);
		csvCrawler.setThumbnail(thumbnail);
		csvCrawler.setTags(tags);
		csvCrawler.setAttribution(attribution);
		csvCrawler.setDescription(description);
		csvCrawler.setSubject(subject);
		csvCrawler.setGrade(grade);
		csvCrawler.setContent(content);
		csvCrawler.setImportMode(importMode);
		csvCrawler.setCsvFileCode(csvFileCode);

		resource = resourceRepository.getResourceByUrl(csvCrawler.getUrl());

		// Create new resource based on mode parameter (protect/update)
		if (resource == null || csvCrawler.getImportMode().equalsIgnoreCase(ImportMode.PROTECT.getImportMode())) {
			resource = new Resource();
			resource.setUrl(csvCrawler.getUrl());
			resource.setGooruOid(UUID.randomUUID().toString());
			resource.setUser(user);
			resource.setSharing(Sharing.PRIVATE.getSharing());
		}
		domainName = getDomainName(url);
		resourceSource = this.getResourceRepository().findResourceSource(domainName);
		if (resourceSource != null && resourceSource.getFrameBreaker() != null && resourceSource.getFrameBreaker() == 1) {
			resource.setHasFrameBreaker(true);
		} else {
			resource.setHasFrameBreaker(false);
		}
		resource.setTitle(csvCrawler.getTitle());
		resource.setCategory(csvCrawler.getType());
		resource.setRecordSource(Resource.RecordSource.CRAWLED.getRecordSource());
		resource.setBatchId(csvCrawler.getCsvFileCode());
		ResourceType resourceType = new ResourceType();
		if (resource.getUrl().contains(YOUTUBE)) {
			resourceType.setName(ResourceType.Type.VIDEO.getType());
		} else {
			resourceType.setName(ResourceType.Type.RESOURCE.getType());
		}
		resource.setThumbnail(thumbnail);
		resource.setLicense(new License());
		resource.getLicense().setName(License.OTHER);
		resource.setResourceType(resourceType);
		resource.setCreatedOn(new Date());
		resource.setCreator(resource.getUser());
		resource.setContentType(new ContentType());
		resource.getContentType().setName(ContentType.RESOURCE);
		resource.setLastModified(new Date());
		resource.setMediaType(mediaType);
		resourceRepository.save(resource);

		if (csvCrawler.getContent() != null && csvCrawler.getContent().equalsIgnoreCase(PREMIUM)) {
			// FIXME Add logic for premium content
		}

		if (csvCrawler.getDescription() != null) {
			resource.setDescription(csvCrawler.getDescription());
		}

		if (csvCrawler.getTags() != null) {
			ResourceInfo resourceInfo = resourceRepository.findResourceInfo(resource.getGooruOid());
			if (resourceInfo == null) {
				resourceInfo = new ResourceInfo();
			}
			resourceInfo.setTags(csvCrawler.getTags());
			resource.setTags(csvCrawler.getTags());
			resourceInfo.setResource(resource);
			resourceInfo.setLastUpdated(new Date());
			resource.setResourceInfo(resourceInfo);
			resourceRepository.save(resourceInfo);
		}
		if (csvCrawler.getAttribution() != null) {

			resourceSource = null;
			if (resource.getResourceSource() != null) {
				resource.getResourceSource().setAttribution(csvCrawler.getAttribution());
				resourceSource = resource.getResourceSource();
			} else {
				domainName = url.contains("http://") ? url.replace("http://", "") : ((url.contains("https://")) ? url.replace("https://", "") : url);
				int lastIndex = domainName.indexOf("/");
				if (lastIndex == -1) {
					lastIndex = domainName.length();
				}
				domainName = domainName.substring(0, lastIndex);
				resourceSource = resourceRepository.findResourceSource(domainName);
				if (resourceSource == null) {
					resourceSource = new ResourceSource();
					resourceSource.setDomainName(domainName);
					resourceSource.setAttribution(csvCrawler.getAttribution());
					resourceSource.setActiveStatus(1);
				}
				resource.setResourceSource(resourceSource);
			}
			resourceRepository.save(resourceSource);
		}

		// resourceRepository.saveCsvCrawler(csvCrawler);
		logger.info("Importing Resource - >" + resource.getUrl());

		// download and genrate thumbnail
		if (csvCrawler.getThumbnail() != null && csvCrawler.getThumbnail().startsWith(HTTP)) {
			this.getResourceImageUtil().downloadAndGenerateThumbnails(resource, csvCrawler.getThumbnail());
		}
		if (indexFlag) {
			indexProcessor.index(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE);
		}
		return resource;
	}

	@Override
	public List<ResourceSource> getSuggestAttribution(String keyword) {
		return resourceRepository.getSuggestAttribution(keyword);
	}

	@Override
	public Map<String, Object> findAllResourcesSource(Map<String, String> filters) {
		return resourceRepository.findAllResourcesSource(filters);
	}

	@Override
	public Resource updateResource(String resourceGooruOid, String title, String description, String mediaFilename, String mediaType) throws IOException {

		Resource resource = getResourceRepository().findResourceByContentGooruId(resourceGooruOid);

		if (resource != null) {
			if (mediaFilename != null) {
				resourceImageUtil.moveFileAndSendMsgToGenerateThumbnails(resource, mediaFilename, false);
			}
			if (title != null) {
				resource.setTitle(title);
			}
			if (description != null) {
				resource.setDescription(description);
			}
			if (mediaType != null) {
				resource.setMediaType(mediaType);
			}

			getResourceRepository().save(resource);
			indexProcessor.index(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE);
		}
		return resource;
	}

	@Override
	public void saveOrUpdate(Resource resource) {
		resourceRepository.saveOrUpdate(resource);
	}

	@Override
	public Resource updateResourceByGooruContentId(String gooruContentId, String resourceTitle, String distinguish, Integer isFeatured, String description, Boolean hasFrameBreaker, String tags, String sharing, Integer resourceSourceId, User user, String mediaType, String attribution,
			String category, String mediaFileName, Boolean isBlacklisted, String grade, String resource_format, String licenseName) {

		Resource existingResource = resourceRepository.findResourceByContentGooruId(gooruContentId);

		if (resourceTitle != null) {
			existingResource.setTitle(resourceTitle);
		}
		if (description != null) {
			existingResource.setDescription(description);
		}
		if (hasFrameBreaker != null) {
			existingResource.setHasFrameBreaker(hasFrameBreaker);
		}
		if (isFeatured != null) {
			existingResource.setIsFeatured(isFeatured);
		}
		if (category != null) {
			existingResource.setCategory(category);
		}
		if(resource_format != null){
			CustomTableValue customTableValue = this.getCustomTableRepository().getCustomTableValue(RESOURCE_CATEGORY_FORMAT,resource_format );
			existingResource.setResourceFormat(customTableValue);
		}
		if (!StringUtils.isEmpty(mediaType)) {
			existingResource.setMediaType(mediaType);
		}
		if (resourceSourceId != null && resourceSourceId != 0) {
			if (existingResource.getResourceSource() == null) {
				ResourceSource resourceSource = new ResourceSource();
				resourceSource.setResourceSourceId(resourceSourceId);
				if (attribution != null) {
					resourceSource.setAttribution(attribution);
				}
				existingResource.setResourceSource(resourceSource);
				existingResource.setDistinguish(Short.valueOf(distinguish));
			} else {
				existingResource.getResourceSource().setResourceSourceId(resourceSourceId);
			}
		} else if (attribution != null) {
			ResourceSource resourceSource = new ResourceSource();
			resourceSource = createResourcesourceAttribution(null, attribution);
			existingResource.setResourceSource(resourceSource);

		}
		if (sharing != null && !sharing.isEmpty()) {
			SessionContextSupport.putLogParameter("sharing-" + existingResource.getGooruOid(), existingResource.getSharing() + " to " + sharing);
			existingResource.setSharing(sharing);
		}

		if (tags != null && !tags.isEmpty()) {
			existingResource.setTags(tags);
		}
		if (mediaFileName != null) {
			this.getResourceImageUtil().downloadAndSendMsgToGenerateThumbnails(existingResource, mediaFileName);
		}
		if (getUserService().isContentAdmin(user)) {
			if (isBlacklisted != null && isBlacklisted == true) {
				existingResource.setSharing(PRIVATE);
				existingResource.setBatchId(DEL_FROM_SEARCH_INDEX);
			}
		}
		if (getUserService().isContentAdmin(user)) {
			if (grade != null)
				existingResource.setGrade(grade);
		}
		if (licenseName != null){
			License licenseData = this.getResourceRepository().getLicenseByLicenseName(licenseName);
			if(licenseData != null){
				existingResource.setLicense(licenseData); 
			}
		}

		resourceRepository.save(existingResource);
		if (tags != null && !tags.isEmpty()) {
			ResourceInfo resourceInfo = resourceRepository.findResourceInfo(gooruContentId);
			if (resourceInfo == null) {
				resourceInfo = new ResourceInfo();
			}
			resourceInfo.setTags(tags);
			resourceInfo.setResource(existingResource);
			resourceInfo.setLastUpdated(new Date());
			resourceRepository.save(resourceInfo);
		}

		this.updateResourceInstanceMetaData(existingResource, user);
		this.replaceDuplicatePrivateResourceWithPublicResource(existingResource);

		if (isBlacklisted != null && isBlacklisted == true) {
			if (getUserService().isContentAdmin(user)) {
				indexProcessor.index(existingResource.getGooruOid(), IndexProcessor.DELETE, RESOURCE);
			}
		} else {
			indexProcessor.index(existingResource.getGooruOid(), IndexProcessor.INDEX, RESOURCE);
		}

		return existingResource;
	}

	@Override
	public void updateResourceSourceAttribution(Integer resourceSourceId, String domainName, String attribution, Integer frameBreaker, User user, Boolean isBlacklisted) throws Exception {

		ResourceSource resourceSource = resourceRepository.findResourceByresourceSourceId(resourceSourceId);
		if (domainName != null) {
			resourceSource.setDomainName(domainName);
		}
		if (attribution != null) {
			resourceSource.setAttribution(attribution);
		}
		if (frameBreaker != null) {
			resourceSource.setFrameBreaker(frameBreaker);
		}
		if (isBlacklisted != null) {
			if (isBlacklisted == false) {
				resourceSource.setIsBlacklisted(0);
			} else {
				resourceSource.setIsBlacklisted(1);
			}

		}

		this.resourceRepository.save(resourceSource);
		if (domainName != null && isBlacklisted != null) {
			if (isBlacklisted == true) {
				if (getUserService().isContentAdmin(user)) {
					List<Resource> resources = resourceRepository.findAllResourceBySourceId(resourceSourceId);
					resourceSource.setIsBlacklisted(1);
					String sharingType = null;
					String contentIds = "";
					String gooruOids = "";
					int count = 0;
					if (resources != null && resources.size() <= 5000) {
						for (Resource resource : resources) {
							sharingType = resource.getSharing();
							if (sharingType.equalsIgnoreCase(PUBLIC)) {
								resource.setSharing(PRIVATE);
							}
							resource.setBatchId(DEL_FROM_SEARCH_INDEX);
							if (count > 0) {
								contentIds += ",";
								gooruOids += ",";
							}
							contentIds += resource.getContentId();
							gooruOids += resource.getGooruOid();
							count++;
						}
						indexProcessor.index(gooruOids, IndexProcessor.INDEX, RESOURCE);
						this.resourceRepository.saveAll(resources);
					} else if (resources != null && resources.size() > 5000) {
						throw new Exception("Domain Blacklist failed -- Resources limit is upto 5000");
					}
				} else {
					throw new AccessDeniedException("You are not allowed to do this operation. ");
				}
			}
		}
		if (domainName != null && frameBreaker != null) {
			if (getUserService().isContentAdmin(user)) {
				List<Resource> resources = resourceRepository.findAllResourceBySourceId(resourceSourceId);
				int count = 0;
				String gooruOIds = "";
				if (resources != null && resources.size() <= 5000) {
					for (Resource resource : resources) {
						boolean hasFrameBreaker = false;
						if (frameBreaker == null || frameBreaker == 0) {
							hasFrameBreaker = false;
							resourceSource.setFrameBreaker(0);
						} else if (frameBreaker == 1) {
							hasFrameBreaker = true;
							resourceSource.setFrameBreaker(1);
						}
						resource.setHasFrameBreaker(hasFrameBreaker);
						if (count > 0) {
							gooruOIds += ",";
						}
						gooruOIds += resource.getGooruOid();
						count++;
					}
					indexProcessor.index(gooruOIds, IndexProcessor.INDEX, RESOURCE);
					this.resourceRepository.saveAll(resources);
				} else if (resources != null && resources.size() > 5000) {
					throw new Exception("Frame breaker update failed -- Resources limit is upto 5000");
				}
			}
		}
	}

	@Override
	public ResourceSource createResourcesourceAttribution(String domainName, String attribution) {

		ResourceSource resourceSource = new ResourceSource();

		resourceSource.setDomainName(domainName);
		resourceSource.setAttribution(attribution);
		resourceSource.setActiveStatus(1);
		if (checkUrlHasHttpSupport(domainName)) {
			resourceSource.setHasHttpsSupport(1);
		} else {
			resourceSource.setHasHttpsSupport(0);
		}

		resourceRepository.save(resourceSource);

		return resourceSource;
	}

	@Override
	public Resource updateResourceThumbnail(String gooruContentId, String fileName, Map<String, Object> formField) throws IOException {

		Resource resource = this.findResourceByContentGooruId(gooruContentId);

		File collectionDir = new File(resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder());

		if (!collectionDir.exists()) {
			collectionDir.mkdirs();
		}

		Map<String, byte[]> files = (Map<String, byte[]>) formField.get(RequestUtil.UPLOADED_FILE_KEY);

		byte[] fileData = null;

		// expecting only one file in the request right now
		for (byte[] fileContent : files.values()) {
			fileData = fileContent;
		}
		if (fileData != null && fileData.length > 0) {

			String prevFileName = resource.getThumbnail();

			if (prevFileName != null && !prevFileName.equalsIgnoreCase("")) {
				File prevFile = new File(resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder() + "/" + prevFileName);
				if (prevFile.exists()) {
					prevFile.delete();
				}
			}

			File file = new File(resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder() + "/" + fileName);

			OutputStream out = new FileOutputStream(file);
			out.write(fileData);
			out.close();

			resource.setThumbnail(fileName);
			resourceRepository.save(resource);
			resourceImageUtil.sendMsgToGenerateThumbnails(resource);
			indexProcessor.index(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE);
		}

		return resource;
	}

	@Override
	public JSONObject getResourceAnalyticData(String gooruOid, String contentType, User user) throws JSONException {
		JSONObject analytic = new JSONObject();
		if (contentType != null
				&& (contentType.equalsIgnoreCase(SessionActivityType.ContentType.COLLECTION.getContentType()) || contentType.equalsIgnoreCase(SessionActivityType.ContentType.QUIZ.getContentType()) || contentType.equalsIgnoreCase(SessionActivityType.ContentType.RESOURCE.getContentType()))) {
			if (contentType.equalsIgnoreCase(SessionActivityType.ContentType.COLLECTION.getContentType())) {
				Map<String, String> filters = new HashMap<String, String>();
				filters.put(GOORU_COLLECTION_ID, gooruOid);
				List<ResourceInstance> collectionResourceInstances = this.getLearnguideRepository().listCollectionResourceInstance(filters);
				if (collectionResourceInstances != null) {
					analytic.put(RESOURCE_COUNT, collectionResourceInstances.size());
				} else {
					analytic.put(RESOURCE_COUNT, 0);
				}
				int studiedResourceCount = this.getSessionActivityRepository().getStudiedResourceCount(gooruOid, user.getGooruUId(), SessionActivityType.Status.OPEN.getStatus());
				analytic.put(STUDIED_RESOURCE_COUNT, studiedResourceCount);
				List<User> userList = learnguideRepository.findCollaborators(gooruOid, user.getPartyUid());
				analytic.put(IS_COLLABORATOR, (userList != null && userList.size() > 0) ? true : false);
			}

			if (contentType.equalsIgnoreCase(SessionActivityType.ContentType.QUIZ.getContentType())) {
				List<AssessmentQuestion> assessmentQuestion = this.getAssessmentRepository().getAssessmentQuestions(gooruOid);
				if (assessmentQuestion != null) {
					analytic.put(QUESTION_COUNT, assessmentQuestion.size());
				} else {
					analytic.put(QUESTION_COUNT, 0);
				}
				analytic.put(SCORE, this.getAssessmentRepository().getQuizUserScore(gooruOid, user.getPartyUid()));
			}

			Rating rating = ratingService.findByContent(gooruOid);
			if (rating != null) {
				analytic.put(LIKES, rating.getVotesUp());
			}
			List<ShelfItem> subscriberUserList = this.getShelfRepository().getShelfSubscribeUserList(gooruOid);
			if (subscriberUserList != null) {
				analytic.put(SUBSCRIBE_COUNT, subscriberUserList.size());
			} else {
				analytic.put(SUBSCRIBE_COUNT , 0);
			}
			analytic.put(VIEWS, this.getResourceRepository().findViews(gooruOid));

		}
		return analytic;
	}

	@Override
	public void mapSourceToResource(Resource resource) {
		if (resource != null && resource.getResourceSource() == null) {
			if (ResourceType.Type.RESOURCE.getType().equalsIgnoreCase(resource.getResourceType().getName()) || ResourceType.Type.VIDEO.getType().equalsIgnoreCase(resource.getResourceType().getName())) {
				String domainName = getDomainName(resource.getUrl());
				if (!domainName.isEmpty()) {
					ResourceSource resourceSource = this.getResourceRepository().findResourceSource(domainName);
					if (resourceSource != null) {
						resource.setResourceSource(resourceSource);
					} else {
						resource.setResourceSource(createResourcesourceAttribution(domainName, StringUtils.substringBeforeLast(domainName, ".")));
					}
					this.getResourceRepository().save(resource);
				}
			}
		}
	}

	@Override
	public Resource getResourceByResourceInstanceId(String resourceInstanceId) {
		return resourceRepository.getResourceByResourceInstanceId(resourceInstanceId);
	}

	public ShelfRepository getShelfRepository() {
		return shelfRepository;
	}

	public SessionActivityRepository getSessionActivityRepository() {
		return sessionActivityRepository;
	}

	@Override
	public ResourceInfo getResourcePageCount(String resourceId) {
		return resourceRepository.getResourcePageCount(resourceId);
	}

	@Override
	public String getResourceInstanceNarration(String resourceInstanceId) {
		return resourceRepository.getResourceInstanceNarration(resourceInstanceId);
	}

	@Override
	public boolean shortenedUrlResourceCheck(String url) {
		String domainName = getDomainName(url);
		String domainType = ResourceSource.ResourceSourceType.SHORTENDED_DOMAIN.getResourceSourceType();
		String type = resourceRepository.shortenedUrlResourceCheck(domainName, domainType);
		if (type != null && type.equalsIgnoreCase(ResourceSource.ResourceSourceType.SHORTENDED_DOMAIN.getResourceSourceType())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<Resource> listResourcesUsedInCollections(Map<String, String> filters) {
		return this.getResourceRepository().listResourcesUsedInCollections(filters);
	}

	private void updateCollaborators(Learnguide collection, Resource resource, User user) {
		List<User> userList = learnguideRepository.findCollaborators(collection.getGooruOid(), null);

		if (!collection.getUser().equals(user)) {
			userList.remove(user);
			userList.add(collection.getUser());
		}

		Set<ContentPermission> contentPermissions = resource.getContentPermissions();

		if (contentPermissions == null) {
			contentPermissions = new HashSet<ContentPermission>();
		}

		if (userList != null && userList.size() > 0) {
			Date date = new Date();
			for (User collaborator : userList) {
				if (!collaborator.getGooruUId().equals(resource.getUser().getGooruUId())) {
					boolean newFlag = true;
					for (ContentPermission contentPermission : contentPermissions) {
						if (contentPermission.getParty().getPartyUid().equals(collaborator.getPartyUid())) {
							newFlag = false;
							break;
						}
					}
					if (newFlag) {
						ContentPermission contentPerm = new ContentPermission();
						contentPerm.setParty(collaborator);
						contentPerm.setContent(resource);
						contentPerm.setPermission(EDIT);
						contentPerm.setValidFrom(date);
						contentPermissions.add(contentPerm);
					}
				}
			}
		}
		resource.setContentPermissions(contentPermissions);
		this.getResourceRepository().saveAll(contentPermissions);
	}

	public ResourceMetadataCo updateYoutubeResourceFeeds(Resource resource) {
		return updateYoutubeResourceFeeds(resource, false);
	}

	public ResourceMetadataCo updateYoutubeResourceFeeds(Resource resource, boolean isUpdate) {
		ResourceMetadataCo resourceFeeds = null;
		ResourceCio resourceCio = getResourceCassandraService().read(resource.getGooruOid());
		if (resourceCio != null) {
			resourceFeeds = resourceCio.getResourceMetadata();
		}
		if (resource.getResourceType().getName().equals(ResourceType.Type.VIDEO.getType())) {

			resourceFeeds = ResourceImageUtil.getYoutubeResourceFeeds(resource.getUrl(), resourceFeeds);
			if (resourceFeeds != null) {
				resourceFeeds.setId(resource.getGooruOid());
				if (resourceCio != null) {
					resourceCio.setResourceMetadata(resourceFeeds);
					getResourceCassandraService().save(resourceCio);
				}
				return resourceFeeds;
			}
		}
		return resourceFeeds;
	}

	@Override
	public Resource updateResourceInfo(Resource resource) {
		String filePath = resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder() + resource.getUrl();
		PDFFile pdfFile = GooruImageUtil.getPDFFile(filePath);
		if (pdfFile != null) {
			boolean isResourceInfoNull = false;
			ResourceInfo resourceInfo = this.findResourceInfo(resource.getGooruOid());
			if (resourceInfo == null) {
				resourceInfo = new ResourceInfo();
				resourceInfo.setResource(resource);
				isResourceInfoNull = true;
			}
			resourceInfo.setNumOfPages(pdfFile.getNumPages());
			resourceInfo.setLastUpdated(new Date());
			resourceRepository.save(resourceInfo);
			if (isResourceInfoNull) {
				resource.setResourceInfo(resourceInfo);
				this.resourceRepository.save(resource);
			}
			resourceRepository.flush();
		}
		return resource;
	}

	@Override
	public Map<String, Object> getSuggestedResourceMetaData(String url, String title, boolean fetchThumbnail) {
		return getResourceImageUtil().getResourceMetaData(url, title, fetchThumbnail);
	}

	@Override
	public ActionResponseDTO<Resource> createResource(Resource newResource, User user) throws Exception {
		Errors errors = validateResource(newResource);
		if (!errors.hasErrors()) {

			ResourceSource resourceSource = null;
			String domainName = null;
			newResource.setRecordSource(Resource.RecordSource.GAT.getRecordSource());
			if (newResource.getBrokenStatus() == null) {
				newResource.setBrokenStatus(0);
			}
			domainName = getDomainName(newResource.getUrl());
			resourceSource = this.getResourceRepository().findResourceSource(domainName);
			if (resourceSource != null && resourceSource.getFrameBreaker() != null && resourceSource.getFrameBreaker() == 1) {
				newResource.setHasFrameBreaker(true);
			} else {
				newResource.setHasFrameBreaker(false);
			}
			
			if (newResource.getResourceFormat() != null) { 
				CustomTableValue resourceCategory = this.getCustomTableRepository().getCustomTableValue(RESOURCE_CATEGORY_FORMAT, newResource.getResourceFormat().getValue());
				newResource.setResourceFormat(resourceCategory);
			}
			if (newResource.getInstructional() != null) { 
				CustomTableValue resourceType = this.getCustomTableRepository().getCustomTableValue(RESOURCE_INSTRUCTIONAL_USE, newResource.getInstructional().getValue());
				newResource.setResourceFormat(resourceType);
			}
			if (this.getCustomTableRepository().getCustomTableValue(RESOURCE_CATEGORY_TYPE, newResource.getCategory()) != null) {
				newResource.setCategory(newResource.getCategory().toLowerCase());
			} else {
				throw new BadCredentialsException("invalid categories, supported categories are Video, Interactive, Website,Slide, Handout, Textbook, Lesson and Exam ");
			}
			// add to db and index.
			Resource resource = handleNewResource(newResource, null, null);
			ResourceInfo resourceInfo = new ResourceInfo();
			String tags = newResource.getTags();
			resourceInfo.setTags(tags);
			resourceInfo.setLastUpdated(new Date());
			resource.setTags(tags);
			resourceInfo.setResource(resource);
			resource.setResourceInfo(resourceInfo);

			resourceRepository.save(resourceInfo);
			s3ResourceApiHandler.updateOrganization(resource);

			this.updateResourceInstanceMetaData(resource, user);
			this.replaceDuplicatePrivateResourceWithPublicResource(resource);
			this.mapSourceToResource(resource);
		}
		return new ActionResponseDTO<Resource>(newResource, errors);
	}

	@Override
	public ActionResponseDTO<Resource> updateResource(String resourceId, Resource newResource, User user) throws Exception {
		Resource resource = resourceRepository.findResourceByContentGooruId(resourceId);
		Errors errors = validateUpdateResource(newResource, resource);

		if (!errors.hasErrors()) {

			if (newResource.getTitle() != null) {
				resource.setTitle(newResource.getTitle());
			}
			if (newResource.getDescription() != null) {
				resource.setDescription(newResource.getDescription());
			}
			if (newResource.getHasFrameBreaker() != null) {
				resource.setHasFrameBreaker(newResource.getHasFrameBreaker());

			} else {
				resource.setHasFrameBreaker(false);
			}
			if (newResource.getIsFeatured() != null) {
				resource.setIsFeatured(newResource.getIsFeatured());
			}
			if (!resource.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing())
					&& resource.getUser().getGooruUId().equalsIgnoreCase(user.getGooruUId())
					&& (resource.getResourceType().getName().equalsIgnoreCase(ResourceType.Type.HANDOUTS.getType()) || resource.getResourceType().getName().equalsIgnoreCase(ResourceType.Type.IMAGE.getType()) || resource.getResourceType().getName()
							.equalsIgnoreCase(ResourceType.Type.PRESENTATION.getType()))) {
				if (newResource.getAttach() != null && newResource.getAttach().getFilename() != null) {
					resource.setUrl(newResource.getAttach().getFilename());
				}
			}
			if (newResource.getResourceFormat() != null) { 
				CustomTableValue customTableValue = this.getCustomTableRepository().getCustomTableValue(RESOURCE_CATEGORY_FORMAT, newResource.getResourceFormat().getValue());
				newResource.setResourceFormat(customTableValue);
			}
			if (newResource.getCategory() != null) {
				if (this.getCustomTableRepository().getCustomTableValue(RESOURCE_CATEGORY_TYPE, newResource.getCategory()) != null) {
					resource.setCategory(newResource.getCategory().toLowerCase());
				} else {
					throw new BadCredentialsException("invalid categories, supported categories are Video, Interactive, Website,Slide, Handout, Textbook, Lesson and Exam ");
				}
			}
			if (!StringUtils.isEmpty(newResource.getMediaType())) {
				resource.setMediaType(newResource.getMediaType());
			}
			if (newResource.getSharing() != null && !newResource.getSharing().isEmpty()) {
				SessionContextSupport.putLogParameter("sharing-" + resource.getGooruOid(), resource.getSharing() + " to " + newResource.getSharing());
				resource.setSharing(newResource.getSharing());
			}
			if (newResource.getTags() != null && !newResource.getTags().isEmpty()) {
				resource.setTags(newResource.getTags());
			}
			if (newResource.getLicense() != null){
				License licenseData = this.getResourceRepository().getLicenseByLicenseName(newResource.getLicense().getName());
				if(licenseData != null){
					resource.setLicense(licenseData); 
				}
			}

			saveOrUpdateResourceTaxonomy(resource, newResource.getTaxonomySet());

			if (newResource.getResourceSource() != null) {
				ResourceSource resourceSource = null;
				if (newResource.getResourceSource().getResourceSourceId() != null && newResource.getResourceSource().getResourceSourceId() != 0) {
					resourceSource = this.getResourceRepository().findResourceByresourceSourceId(newResource.getResourceSource().getResourceSourceId());
				} else if (newResource.getResourceSource().getAttribution() != null) {
					resourceSource = this.getResourceRepository().getAttribution(newResource.getResourceSource().getAttribution());
					if (resourceSource == null) {
						resourceSource = new ResourceSource();
						resourceSource = createResourcesourceAttribution(null, newResource.getResourceSource().getAttribution());
					}
				}
				resource.setResourceSource(resourceSource);
			}
			resourceRepository.save(resource);

			if (newResource.getThumbnail() != null) {
				this.getResourceImageUtil().downloadAndSendMsgToGenerateThumbnails(resource, newResource.getThumbnail());
			}

			if (newResource.getTags() != null && !newResource.getTags().isEmpty()) {
				ResourceInfo resourceInfo = resourceRepository.findResourceInfo(resourceId);
				if (resourceInfo == null) {
					resourceInfo = new ResourceInfo();
				}
				resourceInfo.setTags(newResource.getTags());
				resourceInfo.setResource(resource);
				resourceInfo.setLastUpdated(new Date());
				resourceRepository.save(resourceInfo);
			}

			this.updateResourceInstanceMetaData(resource, user);
			this.replaceDuplicatePrivateResourceWithPublicResource(resource);
			indexProcessor.index(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE);
		}
		if (newResource.getAttach() != null) {
			this.getResourceImageUtil().moveAttachment(newResource, resource);
		}
		return new ActionResponseDTO<Resource>(resource, errors);

	}

	@Override
	public List<Resource> listResourcesUsedInCollections(String limit, String offset, User user) {
		return this.getResourceRepository().listResourcesUsedInCollections(Integer.parseInt(limit), Integer.parseInt(offset));

	}

	@Override
	public Resource deleteTaxonomyResource(String resourceId, Resource newResource, User user) {

		Resource resource = resourceRepository.findResourceByContentGooruId(resourceId);
		deleteResourceTaxonomy(resource, newResource.getTaxonomySet());
		return resource;
	}

	@Override
	public void saveOrUpdateResourceTaxonomy(Resource resource, Set<Code> taxonomySet) {
		Set<Code> codes = resource.getTaxonomySet();
		if (taxonomySet != null) {
			for (Code newCode : taxonomySet) {
				if (newCode.getCodeId() != null) {
					newCode = (Code) this.getTaxonomyRepository().findCodeByCodeId(newCode.getCodeId());
				} else {
					newCode = (Code) this.getTaxonomyRepository().findCodeByTaxCode(newCode.getCode());
				}
				if (newCode != null) {
					if (codes != null && codes.size() > 0) {
						boolean isExisting = false;
						for (Code code : codes) {
							if (code.getCodeId().equals(newCode.getCodeId())) {
								isExisting = true;
								break;
							}
						}
						if (!isExisting) {
							codes.add(newCode);
						}
					} else {
						codes = new HashSet<Code>();
						codes.add(newCode);
					}
				}
			}
		}
		resource.setTaxonomySet(codes);
		this.getResourceRepository().save(resource);
		this.getResourceRepository().flush();
	}

	@Override
	public void deleteResourceTaxonomy(Resource resource, Set<Code> taxonomySet) {

		Set<Code> codes = resource.getTaxonomySet();
		Set<Code> removeCodes = new HashSet<Code>();
		for (Code removeCode : taxonomySet) {
			if (removeCode.getCodeId() != null) {
				removeCode = (Code) this.getTaxonomyRepository().findCodeByCodeId(removeCode.getCodeId());
			} else {
				removeCode = (Code) this.getTaxonomyRepository().findCodeByTaxCode(removeCode.getCode());
			}
			if (removeCode != null) {
				for (Code code : codes) {
					if (code.getCodeId().equals(removeCode.getCodeId())) {
						removeCodes.add(removeCode);
					}
				}

			}
			if (removeCodes != null && removeCodes.size() > 0) {
				codes.removeAll(removeCodes);
			} else {
				throw new NotFoundException(generateErrorMessage(GL0056, _TAXONOMY));
			}
		}
		resource.setTaxonomySet(codes);
		this.getResourceRepository().save(resource);

	}

	@Override
	public void saveOrUpdateGrade(Resource resource, Resource newResource) {
		if (newResource.getGrade() != null) {
			String grade = newResource.getGrade();
			String resourceGrade = resource.getGrade();
			List<String> newResourceGrades = Arrays.asList(grade.split(","));
			if (resourceGrade != null) {
				List<String> resourceGrades = Arrays.asList(resourceGrade.split(","));
				if (resourceGrades != null) {
					for (String newGrade : resourceGrades) {
						if (!newResourceGrades.contains(newGrade) && newGrade.length() > 0) {
							grade += "," + newGrade;
						}
					}
				}
			}
			newResource.setGrade(grade);
		} else {
			newResource.setGrade(resource.getGrade());
		}
		this.getResourceRepository().save(newResource);
		this.getResourceRepository().flush();
	}

	@Override
	public List<User> addCollaborator(String collectionId, User user, String collaboratorId, String collaboratorOperation) {
		Content content = contentRepository.findContentByGooruId(collectionId);
		if (content == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, _COLLECTION));
		}
		if (collaboratorId != null) {
			List<String> collaboratorsList = Arrays.asList(collaboratorId.split("\\s*,\\s*"));
			for (User collaborator : getUserService().findByIdentities(collaboratorsList)) {
				return collectionUtil.updateNewCollaborator(content, collaboratorsList, user, COLLECTION_COLLABORATE, collaboratorOperation);
			}
		}

		return null;
	}

	@Override
	public List<User> getCollaborators(String collectionId) {
		Content content = contentRepository.findContentByGooruId(collectionId);
		if (content == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, CONTENT));
		}
		return this.learnguideRepository.findCollaborators(collectionId, null);
	}

	private void deleteResource(Resource resource) {
		if (resource != null) {
			this.getResourceRepository().retriveAndSetInstances(resource);
		}
		if (resource != null) {
			List<ResourceInstance> resourceInstanceList = resource.getResourceInstances();
			if (resourceInstanceList != null && resourceInstanceList.size() > 0) {
				this.getResourceRepository().removeAll(resourceInstanceList);
			}
		}
		indexProcessor.index(resource.getGooruOid(), IndexProcessor.DELETE, RESOURCE);
		this.getResourceRepository().remove(Resource.class, resource.getContentId());
	}

	private Errors validateResource(Resource resource) throws Exception {
		final Errors errors = new BindException(resource, RESOURCE);
		if (resource != null) {
			// rejectIfNullOrEmpty(errors, resource.getTitle(), TITLE, "GL0006",
			// generateErrorMessage("GL0006", TITLE));
			rejectIfNullOrEmpty(errors, resource.getUrl(), URL, GL0006, generateErrorMessage(GL0006, URL));
		}
		return errors;
	}
	
	@Override
	public void updateViewsBulk(List<UpdateViewsDTO> updateViewsDTOs, User apiCaller) {
		int index = 30;
		for (List<UpdateViewsDTO> partition : Lists.partition(updateViewsDTOs, index)) {
			StringBuffer gooruOids = new StringBuffer();
			StringBuffer collectionIds = new StringBuffer();
			StringBuffer resourceIds = new StringBuffer();
			Map<String, Long> resourceMap = new HashMap<String, Long>();
			for (UpdateViewsDTO updateViewsDTO : partition) {
				if (gooruOids.toString().trim().length() > 0) {
					gooruOids.append(",");
				}
				
				gooruOids.append(updateViewsDTO.getGooruOid());
				resourceMap.put(updateViewsDTO.getGooruOid(), updateViewsDTO.getViews());
				
			}
			if (gooruOids.toString().trim().length() > 0) {
				List<Resource> resources = this.getResourceRepository().findAllResourcesByGooruOId(gooruOids.toString());
				for (Resource resource : resources) {
					if (resourceMap.containsKey(resource.getGooruOid())) {
						resource.setViews(resourceMap.get(resource.getGooruOid()));
					}
					if(resourceIds.toString().trim().length() > 0) {
						resourceIds.append(",");
					}
					if(collectionIds.toString().trim().length() > 0) {
						collectionIds.append(",");
					}
					if(resource.getResourceType().getName().equalsIgnoreCase(ResourceType.Type.SCOLLECTION.getType())) {
						collectionIds.append(resource.getGooruOid());
					} else {
						resourceIds.append(resource.getGooruOid());
					}
					
				}
				this.getResourceRepository().saveAll(resources);
				if (collectionIds.toString().trim().length() > 0) {
					indexProcessor.index(collectionIds.toString(), IndexProcessor.INDEX, SCOLLECTION);
				} else if(resourceIds.toString().trim().length() > 0) {
					indexProcessor.index(resourceIds.toString(), IndexProcessor.INDEX, RESOURCE);
				}
			}
		}

	}
	
	private Errors validateUpdateResource(Resource newResource, Resource resource) throws Exception {
		final Errors errors = new BindException(newResource, RESOURCE);
		rejectIfNull(errors, resource, RESOURCE, GL0056, generateErrorMessage(GL0056, RESOURCE));
		return errors;
	}

	public boolean checkUrlHasHttpSupport(String domainName) {
		if (domainName != null) {
			try {
				URL url = new URL("https://" + domainName);
				if ((HttpURLConnection) url.openConnection() != null) {
					int responseCode = ((HttpURLConnection) url.openConnection()).getResponseCode();
					if (responseCode == 200) {
						return true;
					} else {
						return false;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;

	}

	public TaxonomyRespository getTaxonomyRepository() {
		return taxonomyRepository;
	}

	public TaxonomyService getTaxonomyService() {
		return taxonomyService;
	}

	public void setTaxonomyService(TaxonomyService taxonomyService) {
		this.taxonomyService = taxonomyService;
	}

	public SettingService getSettingService() {
		return settingService;
	}

	public ResourceCassandraService getResourceCassandraService() {
		return resourceCassandraService;
	}

	public void setResourceCassandraService(ResourceCassandraService resourceCassandraService) {
		this.resourceCassandraService = resourceCassandraService;
	}

	@Override
	public List<Map<String, Object>> getPartyPermissions(long contentId) {
		return resourceRepository.getPartyPermissions(contentId);
	}

	@Override
	public Resource resourcePlay(String gooruContentId, User apiCaller, boolean more) throws Exception {
		Resource resource = this.findResourceByContentGooruId(gooruContentId);

		if (resource == null) {
			throw new NotFoundException("Resource not found Exception");
		}

		resource.setCustomFieldValues(customFieldService.getCustomFieldsValuesOfResource(resource.getGooruOid()));
		if (more) {
			String category = CustomProperties.Table.FEEDBACK_CATEGORY.getTable() + "_" + RATING;
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(FEEDBACK, feedbackRepository.getContentFeedbackAggregate(gooruContentId, category, true));
			resource.setMeta(map);
		}

		return resource;

	}

	public AsyncExecutor getAsyncExecutor() {
		return asyncExecutor;
	}

	public CustomTableRepository getCustomTableRepository() {
		return customTableRepository;
	}

	

}
