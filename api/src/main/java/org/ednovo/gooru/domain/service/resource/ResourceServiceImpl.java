////////////////////////////////////////////////////////////
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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.application.util.AsyncExecutor;
import org.ednovo.gooru.application.util.CollectionUtil;
import org.ednovo.gooru.application.util.GooruImageUtil;
import org.ednovo.gooru.application.util.ResourceImageUtil;
import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentProvider;
import org.ednovo.gooru.core.api.model.ContentProviderAssociation;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.FileMeta;
import org.ednovo.gooru.core.api.model.License;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceInfo;
import org.ednovo.gooru.core.api.model.ResourceMetaData;
import org.ednovo.gooru.core.api.model.ResourceSource;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.StatisticsDTO;
import org.ednovo.gooru.core.api.model.Textbook;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.api.model.UserRole;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.core.application.util.ImageUtil;
import org.ednovo.gooru.core.application.util.RequestUtil;
import org.ednovo.gooru.core.cassandra.model.ResourceCio;
import org.ednovo.gooru.core.cassandra.model.ResourceMetadataCo;
import org.ednovo.gooru.core.cassandra.model.ResourceStasCo;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.BadRequestException;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.domain.cassandra.service.ResourceCassandraService;
import org.ednovo.gooru.domain.service.CollectionService;
import org.ednovo.gooru.domain.service.assessment.AssessmentService;
import org.ednovo.gooru.domain.service.eventlogs.ResourceEventLog;
import org.ednovo.gooru.domain.service.partner.CustomFieldsService;
import org.ednovo.gooru.domain.service.sessionActivity.SessionActivityService;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.gooru.domain.service.storage.S3ResourceApiHandler;
import org.ednovo.gooru.domain.service.taxonomy.TaxonomyService;
import org.ednovo.gooru.domain.service.v2.ContentService;
import org.ednovo.gooru.infrastructure.messenger.IndexHandler;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.ConfigSettingRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.FeedbackRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.activity.SessionActivityRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.assessment.AssessmentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.content.ContentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy.TaxonomyRespository;
import org.ednovo.gooru.security.OperationAuthorizer;
import org.json.JSONObject;
import org.restlet.data.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.SimpleBookmark;
import com.sun.pdfview.PDFFile;

@Service
public class ResourceServiceImpl extends OperationAuthorizer implements ResourceService, ParameterProperties, ConstantProperties {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceServiceImpl.class);

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private ResourceEventLog resourceEventLog;

	@Autowired
	private ResourceParser resourceParser;

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
	private SessionActivityRepository sessionActivityRepository;

	@Autowired
	private TaxonomyService taxonomyService;

	@Autowired
	private TaxonomyRespository taxonomyRepository;

	@Autowired
	private SettingService settingService;

	@Autowired
	private ResourceCassandraService resourceCassandraService;

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

	@Autowired
	private CollectionService collectionService;

	@Autowired
	private AssessmentService assessmentService;

	@Autowired
	private ContentService contentService;

	@Autowired
	private CollectionRepository collectionRepository;

	@Autowired
	private IndexHandler indexHandler;

	private static final String SHORTENED_URL_STATUS = "shortenedUrlStatus";

	@Override
	public List<Resource> listResources(final Map<String, String> filters) {
		return getResourceRepository().listResources(filters);
	}

	@Override
	public Resource findResourceByContentGooruId(final String gooruContentId) {
		final Resource resource = getResourceRepository().findResourceByContentGooruId(gooruContentId);
		if (resource == null) {
			throw new NotFoundException("resource not found ", GL0056);
		}
		if (resource.getResourceType().getName().equalsIgnoreCase(ASSESSMENT_QUESTION)) {
			resource.setDepthOfKnowledges(this.collectionService.setContentMetaAssociation(this.collectionService.getContentMetaAssociation(DEPTH_OF_KNOWLEDGE), resource, DEPTH_OF_KNOWLEDGE));
		} else {
			resource.setMomentsOfLearning(this.collectionService.setContentMetaAssociation(this.collectionService.getContentMetaAssociation(MOMENTS_OF_LEARNING), resource, MOMENTS_OF_LEARNING));
		}
		resource.setEducationalUse(this.collectionService.setContentMetaAssociation(this.collectionService.getContentMetaAssociation(EDUCATIONAL_USE), resource, EDUCATIONAL_USE));
		resource.setRatings(this.collectionService.setRatingsObj(this.getResourceRepository().getResourceSummaryById(gooruContentId)));
		setContentProvider(resource);
		return resource;
	}

	@Override
	public Map<String, Object> getResource(final String gooruOid) {
		final Resource resource = this.findResourceByContentGooruId(gooruOid);
		if (resource == null) {
			throw new NotFoundException("resource not found", GL0056);
		}
		final Map<String, Object> resourceObject = new HashMap<String, Object>();
		try {
			resource.setViews(this.resourceCassandraService.getLong(resource.getGooruOid(), STATISTICS_VIEW_COUNT));
			resource.setViewCount(resource.getViewCount());
		} catch (Exception e) {
			LOGGER.error("parser error : {}", e);
		}
		if (resource.getResourceType().getName().equalsIgnoreCase(ASSESSMENT_QUESTION)) {
			final AssessmentQuestion question = assessmentService.getQuestion(gooruOid);
			question.setCustomFieldValues(customFieldService.getCustomFieldsValuesOfResource(question.getGooruOid()));
			resourceObject.put(RESOURCE, question);
		} else {
			resource.setCustomFieldValues(customFieldService.getCustomFieldsValuesOfResource(resource.getGooruOid()));
			resourceObject.put(RESOURCE, resource);
		}
		resourceObject.put(STANDARDS, this.getCollectionService().getStandards(resource.getTaxonomySet(), false, null));
		resourceObject.put(SKILLS, this.getCollectionService().getSkills(resource.getTaxonomySet()));
		resourceObject.put(COURSE, this.getCollectionService().getCourse(resource.getTaxonomySet()));
		setContentProvider(resource);
		return resourceObject;
	}

	@Override
	public Resource setContentProvider(final String gooruOid) {
		final Resource resource = this.getResourceRepository().findResourceByContent(gooruOid);
		rejectIfNull(resource, GL0056, RESOURCE);
		return setContentProvider(resource);
	}

	@Override
	public Resource setContentProvider(final Resource resource) {
		final List<ContentProviderAssociation> contentProviderAssociations = this.getContentRepository().getContentProviderByGooruOid(resource.getGooruOid(), null, null);
		if (contentProviderAssociations != null) {
			final List<String> aggregator = new ArrayList<String>();
			final List<String> publisher = new ArrayList<String>();
			final List<String> host = new ArrayList<String>();
			for (final ContentProviderAssociation contentProviderAssociation : contentProviderAssociations) {
				if (contentProviderAssociation.getContentProvider() != null && contentProviderAssociation.getContentProvider().getType() != null
						&& contentProviderAssociation.getContentProvider().getType().getValue().equalsIgnoreCase(CustomProperties.ContentProviderType.PUBLISHER.getContentProviderType())) {
					publisher.add(contentProviderAssociation.getContentProvider().getName());
				} else if (contentProviderAssociation.getContentProvider() != null && contentProviderAssociation.getContentProvider().getType() != null
						&& contentProviderAssociation.getContentProvider().getType().getValue().equalsIgnoreCase(CustomProperties.ContentProviderType.AGGREGATOR.getContentProviderType())) {
					aggregator.add(contentProviderAssociation.getContentProvider().getName());
				} else if (contentProviderAssociation.getContentProvider() != null && contentProviderAssociation.getContentProvider().getType() != null
						&& contentProviderAssociation.getContentProvider().getType().getValue().equalsIgnoreCase(CustomProperties.ContentProviderType.HOST.getContentProviderType())) {
					host.add(contentProviderAssociation.getContentProvider().getName());
				}
			}
			resource.setPublisher(publisher);
			resource.setAggregator(aggregator);
			resource.setHost(host);

		}
		return resource;
	}

	@Override
	public void deleteResource(final Long contentId) {
		getResourceRepository().remove(Resource.class, contentId);

	}

	@Override
	public void enrichAndAddOrUpdate(final Resource resource) {
		if (StringUtils.isBlank(resource.getTitle())) {
			enrichWithTitleAndText(resource);
		}
		String title = cleanTitle(resource.getTitle());
		resource.setTitle(title);
		getResourceRepository().saveOrUpdate(resource);
	}

	private String cleanTitle(String title) {
		final List<String> stringsToRemoveList = Arrays.asList("bbc", "ks2", "read", "phet", "bitesize", "maths");
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

	private void enrichWithTitleAndText(final Resource resource) {

		final String parentUrlString = resource.getParentUrl();

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
		for (final String title : possibleTitles) {
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
			final String address = "http://api.bing.net/xml.aspx?Appid=E33DF01A3363CBE8CC3C5F4E15F1284647476C8A&sources=image&query=" + query;
			URL url = new URL(address);
			final URLConnection connection = url.openConnection();
			InputStream in = connection.getInputStream();

			// xml name space stuff:
			NamespaceContext ctx = new NamespaceContext() {
				public String getNamespaceURI(final String prefix) {
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
			final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(true);
			final DocumentBuilder builder = domFactory.newDocumentBuilder();
			org.w3c.dom.Document doc = builder.parse(in);// new
			// File("c:\\users\\a\\Desktop\\test.xml"));

			// create xpath for extract thumbnail url:
			String xpathStr = "/e:SearchResponse/m:Image/m:Results/m:ImageResult/m:Thumbnail/m:Url/text()";
			final XPathFactory xpathFact = XPathFactory.newInstance();
			final XPath xpath = xpathFact.newXPath();
			xpath.setNamespaceContext(ctx);

			// extract thumbnail url from xml doc reponse:
			final String thmbnailUrl = xpath.evaluate(xpathStr, doc);

			return thmbnailUrl;

		} catch (Exception ex) {

			return null;
		}

	}

	@Override
	public void updateResourceSource(final String resourceTypeString) {
		List<Resource> resources = null;
		final Map<String, String> filters = new HashMap<String, String>();
		int pageNum = 0;
		int pageSize = 100;
		filters.put(RESOURCE_TYPE, resourceTypeString);
		filters.put(IN_USE, ONE);
		do {
			filters.put(PAGE_NUM, (++pageNum) + "");
			filters.put(PAGE_SIZE, pageSize + "");
			filters.put(RESOURCE_SOURCE, NULL);
			resources = listResources(filters);
			LOGGER.debug("no of resource :" + resources.size() + " of page : " + pageNum + " of size : " + pageSize);
			int count = 0;
			for (final Resource resource : resources) {
				final String domainName = BaseUtil.getDomainName(resource.getUrl());
				if (!domainName.isEmpty()) {
					final ResourceSource resourceSource = this.getResourceRepository().findResourceSource(domainName);
					if (resourceSource != null) {
						LOGGER.debug("resource url : " + resource.getUrl() + " source name : " + " updated domainName: " + domainName + "no of resource to go: " + (++count));
						this.getResourceRepository().updateResourceSourceId(resource.getContentId(), resourceSource.getResourceSourceId());
					}
				}
			}
			try {
				Thread.sleep(5000);
			} catch (Exception ex) {
				LOGGER.debug("error" + ex.getMessage());
			}
		} while (resources != null && resources.size() > 0);
	}

	@Override
	public Textbook findTextbookByContentGooruId(final String gooruContentId) {
		return getResourceRepository().findTextbookByContentGooruId(gooruContentId);
	}

	@Override
	public Resource findWebResource(final String url) {
		return getResourceRepository().findWebResource(url);
	}

	@Override
	public void saveNewResource(final Resource resource, final boolean downloadResource) throws IOException {
		resource.setCreatedOn(new Date(System.currentTimeMillis()));
		if (StringUtils.isEmpty(resource.getGooruOid())) {
			resource.setGooruOid(UUID.randomUUID().toString());
		}
		resource.setLastModified(resource.getCreatedOn());

		this.getResourceRepository().saveOrUpdate(resource);

		if (downloadResource) {
			final String sourceUrl = resource.getUrl();
			final String fileName = StringUtils.substringAfterLast(sourceUrl, "/");

			final File resourceFolder = new File(resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder());
			if (!resourceFolder.exists()) {
				resourceFolder.mkdir();
			}

			final String resourceFilePath = resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder() + File.separator + fileName;
			final boolean downloaded = ImageUtil.downloadAndSaveFile(sourceUrl, resourceFilePath);
			if (!downloaded) {
				throw new IOException(generateErrorMessage("GL0093", resource.getUrl()));
			}
			this.getAsyncExecutor().uploadResourceFolder(resource);

			resource.setUrl(fileName);
			this.getResourceRepository().saveOrUpdate(resource);
			if (fileName.toLowerCase().endsWith(DOT_PDF)) {
				final Map<String, Object> param = new HashMap<String, Object>();
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
	public Resource handleNewResource(Resource resource, final String resourceTypeForPdf, final String thumbnail) {
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
		final ResourceType resourceType = new ResourceType();
		resource.setResourceType(resourceType);
		final String fileExtension = org.apache.commons.lang.StringUtils.substringAfterLast(resource.getUrl(), ".");
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
			LOGGER.error("save resource failed" + errors.toString());
		}

		if (downloadedFlag) {
			// Move the resource to the right folder
			File resourceFile = new File(resource.getUrl());
			if (resourceFile.exists()) {
				final File resourceFolder = new File(resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder());
				if (!resourceFolder.exists()) {
					resourceFolder.mkdir();
				}
				final String fileName = StringUtils.substringAfterLast(resource.getUrl(), "/");
				resourceFile.renameTo(new File(resourceFolder.getPath(), fileName));
				resource.setUrl(fileName);
				this.getResourceRepository().saveOrUpdate(resource);
				String resourceFilePath = resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder() + resource.getUrl();
				resourceFilePath = resourceFilePath.trim();
				if (fileName.toLowerCase().endsWith(DOT_PDF)) {
					final Map<String, Object> param = new HashMap<String, Object>();
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
			final List<Resource> chapterResources = splitToChaptersResources(resource);
			for (final Resource chapterResource : chapterResources) {
				enrichAndAddOrUpdate(chapterResource);
			}
		}
		indexHandler.setReIndexRequest(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE, null, false, false);

		return resource;

	}

	private Resource updateResource(final Resource resource, final boolean findByURL, final String thumbnail, final Errors errors) {
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
				for (final ResourceMetaData resourceMetaData2 : resourceMetaData) {
					if (resourceMetaData2.getMetaKey().equalsIgnoreCase(RESOURCE_IMAGE) && !StringUtils.isEmpty(resourceMetaData2.getMetaContent())) {
						final String imageURL = resourceMetaData2.getMetaContent();
						final String fileName = existingResource.getGooruOid();
						saveResource = true;
						final boolean downloaded = downloadThumbnail(fileName, imageURL, existingResource);
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
				indexHandler.setReIndexRequest(existingResource.getGooruOid(), IndexProcessor.INDEX, RESOURCE, null, false, false);
			}
		}
		return existingResource;
	}

	public boolean downloadThumbnail(final String fileName, final String imageURL, final Resource resource) {
		try {
			FileMeta fileMeta = this.getMediaService().handleFileUpload(fileName, imageURL, null, false, 0, 0);
			this.getResourceImageUtil().moveFileAndSendMsgToGenerateThumbnails(resource, fileMeta.getName(), false);
			return true;

		} catch (FileNotFoundException e) {
			LOGGER.error("Error saving crawled resource image", e);
		} catch (IOException e) {
			LOGGER.error("Error saving crawled resource image", e);
		}
		return false;
	}

	// chapter spliting
	@Override
	public List<Resource> splitToChaptersResources(final Resource resource) {

		// split to chapters and return new chapter files:
		final List<String> newLocalChaptersUrls = splitToChaptersAndSaveFiles(resource.getUrl());

		// save in db and index.
		List<Resource> chapterResources = new ArrayList<Resource>();
		for (final String newLocalUrl : newLocalChaptersUrls) {
			final Resource chapterResource = new Resource();
			chapterResource.setUrl(newLocalUrl);
			chapterResource.setUser(resource.getUser());
			chapterResource.setResourceTypeByString(ResourceType.Type.HANDOUTS.getType());
			chapterResource.setParentUrl(newLocalUrl);
			chapterResources.add(chapterResource);
		}
		return chapterResources;
	}

	private static List<String> splitToChaptersAndSaveFiles(final String newLocalUrl) {
		try {

			HashMap<Integer, String> chapters = new HashMap<Integer, String>();
			ArrayList<Integer> pages = new ArrayList<Integer>();

			/** Call the split method with filename and page size as params **/
			PdfReader reader = new PdfReader(newLocalUrl);
			reader.consolidateNamedDestinations();
			final List<HashMap<String, Object>> list = SimpleBookmark.getBookmark(reader);

			for (final HashMap<String, Object> test : list) {
				final String page = test.get(PAGE).toString();
				final Integer num = Integer.parseInt(page.substring(0, page.indexOf(' ')));
				chapters.put(num, (String) test.get(_TITLE));
				pages.add(num);
			}

			int index = 1;
			List<String> chaptersUrls = new ArrayList<String>();
			for (final Integer i : pages) {
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
	private static String splitAndSaveChapter(final String mainFileUrl, final int pageBeginNum, final int pageEndNum, final String name) {
		try {
			final PdfReader reader = new PdfReader(mainFileUrl);

			int splittedPageSize = pageEndNum - pageBeginNum + 1;
			int pageNum = pageBeginNum;

			final String chapterUrl = mainFileUrl.substring(0, mainFileUrl.indexOf(DOT_PDF)) + "-" + name + DOT_PDF;

			final Document document = new Document(reader.getPageSizeWithRotation(1));

			final FileOutputStream fos = new FileOutputStream(chapterUrl);
			final PdfCopy writer = new PdfCopy(document, fos);
			final Map<String, String> info = reader.getInfo();

			document.open();
			if ((info != null) && (info.get(_AUTHOR) != null)) {
				document.addAuthor(info.get(_AUTHOR));
			}

			document.addTitle(name);

			for (int offset = 0; offset < splittedPageSize && (pageNum + offset) < pageEndNum; offset++) {
				final PdfImportedPage page = writer.getImportedPage(reader, pageNum + offset);
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
	public Resource saveResource(final Resource resource, final Errors errors, final boolean findByURL) {
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
			final ContentType contentTypeResource = (ContentType) this.getBaseRepository().get(ContentType.class, ContentType.RESOURCE);
			resource.setContentType(contentTypeResource);
		}
		if (resource.getResourceType() == null) {
			final ResourceType resourceType = (ResourceType) this.getBaseRepository().get(ResourceType.class, ResourceType.Type.RESOURCE.getType());
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
	public String updateResourceImage(final String gooruContentId, final String fileName) throws IOException {
		final Resource resource = this.getResourceRepository().findResourceByContentGooruId(gooruContentId);
		if (resource == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, RESOURCE), GL0056);
		}
		this.getResourceImageUtil().moveFileAndSendMsgToGenerateThumbnails(resource, fileName, true);
		try {
			this.getAsyncExecutor().updateResourceFileInS3(resource.getFolder(), resource.getOrganization().getNfsStorageArea().getInternalPath(), gooruContentId, UserGroupSupport.getSessionToken());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return resource.getOrganization().getNfsStorageArea().getAreaPath() + resource.getFolder() + "/" + resource.getThumbnail();
	}

	@Override
	public void deleteResourceImage(final String gooruContentId) {
		final Resource resource = this.getResourceRepository().findResourceByContentGooruId(gooruContentId);
		final String repositoryPath = resource.getOrganization().getNfsStorageArea().getInternalPath();
		final File classplanDir = new File(repositoryPath + resource.getFolder());

		if (classplanDir.exists()) {

			final String prevFileName = resource.getThumbnail();

			if (prevFileName != null && !prevFileName.equalsIgnoreCase("")) {
				final File prevFile = new File(classplanDir.getPath() + "/" + prevFileName);
				if (prevFile.exists()) {
					prevFile.delete();
					this.getAsyncExecutor().deleteResourceFile(resource, resource.getThumbnail());
				}
			}

			resource.setThumbnail(null);
			this.getResourceRepository().save(resource);
			indexHandler.setReIndexRequest(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE, null, false, false);
		}
	}

	@Override
	public void deleteResourceBulk(final String contentIds) {
		this.getResourceRepository().deleteResourceBulk(contentIds);
		indexHandler.setReIndexRequest(contentIds, IndexProcessor.DELETE, RESOURCE, null, false, false);
	}

	@Override
	public void deleteBulkResource(final String contentIds) {
		final List<Resource> resources = resourceRepository.findAllResourcesByGooruOId(contentIds);
		final List<Resource> removeList = new ArrayList<Resource>();
		if (resources.size() > 0) {
			String removeContentIds = "";
			int count = 0;
			for (final Resource resource : resources) {
				if (count > 0) {
					removeContentIds += ",";
				}
				removeContentIds += resource.getGooruOid();
				removeList.add(resource);
				count++;
			}
			if (removeList.size() > 0) {
				this.baseRepository.removeAll(removeList);
				indexHandler.setReIndexRequest(removeContentIds, IndexProcessor.DELETE, RESOURCE, null, false, false);
			}
		}
	}

	public ResourceRepository getResourceRepository() {
		return resourceRepository;
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

	public void setResourceManager(final ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	public SessionActivityService getSessionActivityService() {
		return sessionActivityService;
	}

	public void setSessionActivityService(final SessionActivityService sessionActivityService) {
		this.sessionActivityService = sessionActivityService;
	}

	public AssessmentRepository getAssessmentRepository() {
		return assessmentRepository;
	}

	@Override
	public ResourceSource updateSuggestAttribution(final String gooruContentId, final String attribution) {
		ResourceSource resourceSource = (ResourceSource) this.getBaseRepository().get(ResourceSource.class, contentRepository.findByContentGooruId(gooruContentId).getContentId());
		if (resourceSource != null) {
			resourceSource.setAttribution(attribution);
			this.getBaseRepository().save(resourceSource);
		}
		return resourceSource;
	}

	@Override
	public void deleteResource(final String gooruContentId, final User apiCaller) {
		final Resource resource = resourceRepository.findResourceByContentGooruId(gooruContentId);
		if (resource == null || resource.getResourceType().getName().equalsIgnoreCase(APPLICATION) || resource.getResourceType().getName().equalsIgnoreCase(SCOLLECTION) || resource.getResourceType().getName().equalsIgnoreCase(FOLDER)
				|| resource.getResourceType().getName().equalsIgnoreCase(CLASSPAGE)) {
			throw new NotFoundException(generateErrorMessage(GL0056, RESOURCE), GL0056);
		} else {
			List<org.ednovo.gooru.core.api.model.Collection> collections = getCollectionRepository().getCollectionByResourceOid(gooruContentId);
			for (org.ednovo.gooru.core.api.model.Collection collection : collections) {
				collection.setLastModified(new Date(System.currentTimeMillis()));
				List<CollectionItem> collectionitems = this.getCollectionRepository().getCollectionItemsByResource(collection.getGooruOid());
				for (CollectionItem collectionItem : collectionitems) {
					List<CollectionItem> resetCollectionItems = this.getCollectionRepository().getResetSequenceCollectionItems(collectionItem.getCollection().getGooruOid(), collectionItem.getItemSequence());
					int itemSequence = collectionItem.getItemSequence();
					for (CollectionItem resetCollectionItem : resetCollectionItems) {
						resetCollectionItem.setItemSequence(itemSequence++);
					}
					this.getCollectionRepository().saveAll(resetCollectionItems);
				}
				getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + collection.getUser().getPartyUid() + "*");
			}

			this.getCollectionRepository().saveAll(collections);
			if ((resource.getUser() != null && resource.getUser().getPartyUid().equalsIgnoreCase(apiCaller.getPartyUid())) || getUserService().isContentAdmin(apiCaller)) {
				this.getContentService().deleteContentTagAssoc(resource.getGooruOid(), apiCaller);
				this.getBaseRepository().remove(resource);
				indexHandler.setReIndexRequest(gooruContentId, IndexProcessor.DELETE, RESOURCE, null, false, false);
			} else {
				throw new BadRequestException(generateErrorMessage(GL0099, RESOURCE));
			}
		}
	}

	@Override
	public void deleteAttribution(final Resource resource, final String gooruAttributionId, final User apiCaller) {

		// if (resource == null) {
		// resource =
		// resourceRepository.findResourceByAttributionGooruId(gooruAttributionId);
		if (resource == null) {
			LOGGER.warn("invalid resource passed to deleteResource:" + gooruAttributionId);
			return;
		}
		// }
		final UserRole contentAdmin = new UserRole();
		contentAdmin.setRoleId(UserRole.ROLE_CONTENT_ADMIN);

		final User systemUser = this.getUserRepository().findByRole(contentAdmin).get(0);
		resource.setUser(systemUser);
		this.getBaseRepository().removeAll(resource.getContentPermissions());
		resource.setContentPermissions(null);
		resource.setLastModified(new Date(System.currentTimeMillis()));
		resourceRepository.saveOrUpdate(resource);
		LOGGER.warn("Deleted resource from deleteResource:" + gooruAttributionId);
		/* Step 4 - Send the message to reindex the resource */
		indexHandler.setReIndexRequest(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE, null, false, false);
	}

	@Override
	public ResourceSource findResourceSource(final String domainName) {
		return resourceRepository.findResourceSource(domainName);
	}

	@Override
	public ResourceInfo findResourceInfo(final String resourceGooruOid) {
		return resourceRepository.findResourceInfo(resourceGooruOid);
	}

	@Override
	public Resource findResourceByUrl(final String resourceUrl, final String sharing, final String userUid) {
		return resourceRepository.findResourceByUrl(resourceUrl, sharing, userUid);
	}

	@Override
	public List<Resource> findWebResourcesForBlacklisting() {
		return resourceRepository.findWebResourcesForBlacklisting();
	}

	@Override
	public Resource addNewResource(final String url, final String title, final String text, final String category, final String sharing, final String typeName, final String licenseName, final Integer brokenStatus, final Boolean hasFrameBreaker, final String description, final Integer isFeatured,
			final String tags, final boolean isReturnJson, final User apiCaller, final String mediaType, final String resourceFormat, final String resourceInstructional) {
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

		if (resourceFormat != null) {
			final CustomTableValue customTableValue = this.getCustomTableRepository().getCustomTableValue(RESOURCE_CATEGORY_FORMAT, resourceFormat);
			resource.setResourceFormat(customTableValue);
		}

		if (resourceInstructional != null) {
			final CustomTableValue customTableValue = this.getCustomTableRepository().getCustomTableValue(RESOURCE_INSTRUCTIONAL_USE, resourceInstructional);
			resource.setInstructional(customTableValue);
		}

		// FIXME Use the appropriate record source here. the api method may be
		// called from elsewhere as well
		resource.setRecordSource(Resource.RecordSource.GAT.getRecordSource());

		if (typeName != null) {
			resource.setResourceTypeByString(ResourceType.Type.valueOf(typeName).getType());
		}
		resource.setSharing(sharing);
		/*
		 * if (licenseName != null) { resource.setLicense(licenseName); }
		 */

		if (brokenStatus == null) {
			resource.setBrokenStatus(0);
		} else {
			resource.setBrokenStatus(brokenStatus);
		}
		domainName = BaseUtil.getDomainName(url);
		if(domainName != null){
			resourceSource = this.getResourceRepository().findResourceSource(domainName);
		}
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

		this.mapSourceToResource(resource);
		indexHandler.setReIndexRequest(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE, null, false, false);

		return resource;
	}

	@Override
	public List<ResourceSource> getSuggestAttribution(final String keyword) {
		return resourceRepository.getSuggestAttribution(keyword);
	}

	@Override
	public Map<String, Object> findAllResourcesSource(final Map<String, String> filters) {
		return resourceRepository.findAllResourcesSource(filters);
	}

	@Override
	public Resource updateResource(final String resourceGooruOid, final String title, final String description, final String mediaFilename, final String mediaType) throws IOException {

		final Resource resource = getResourceRepository().findResourceByContentGooruId(resourceGooruOid);

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
			indexHandler.setReIndexRequest(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE, null, false, false);
		}
		return resource;
	}

	@Override
	public void saveOrUpdate(final Resource resource) {
		resourceRepository.saveOrUpdate(resource);
	}

	@Override
	public Resource updateResourceByGooruContentId(final String gooruContentId, final String resourceTitle, final String distinguish, final Integer isFeatured, final String description, final Boolean hasFrameBreaker, final String tags, final String sharing, final Integer resourceSourceId,
			final User user, final String mediaType, final String attribution, final String category, final String mediaFileName, final Boolean isBlacklisted, final String grade, final String resourceFormat, final String licenseName, final String url) {

		final Resource existingResource = resourceRepository.findResourceByContentGooruId(gooruContentId);

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

		if (getUserService().isContentAdmin(user)) {
			ResourceSource resourceSource = null;
			String domainName = null;
			if (url != null) {
				existingResource.setUrl(url);
				domainName = BaseUtil.getDomainName(url);
				if(domainName != null){
					resourceSource = this.getResourceRepository().findResourceSource(domainName);
				}
				if (resourceSource != null && resourceSource.getFrameBreaker() != null && resourceSource.getFrameBreaker() == 1) {
					existingResource.setHasFrameBreaker(true);
				} else {
					existingResource.setHasFrameBreaker(false);
				}
				this.mapSourceToResource(existingResource);
			}
		}

		if (category != null) {
			existingResource.setCategory(category);
		}
		if (resourceFormat != null) {
			final CustomTableValue customTableValue = this.getCustomTableRepository().getCustomTableValue(RESOURCE_CATEGORY_FORMAT, resourceFormat);
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
		if (getUserService().isContentAdmin(user) && grade != null) {
			existingResource.setGrade(grade);
		}
		if (licenseName != null) {
			final License licenseData = this.getResourceRepository().getLicenseByLicenseName(licenseName);
			if (licenseData != null) {
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

		if (isBlacklisted != null && isBlacklisted == true) {
			if (getUserService().isContentAdmin(user)) {
				indexHandler.setReIndexRequest(existingResource.getGooruOid(), IndexProcessor.DELETE, RESOURCE, null, false, false);
			}
		} else {
			indexHandler.setReIndexRequest(existingResource.getGooruOid(), IndexProcessor.INDEX, RESOURCE, null, false, false);
		}

		return existingResource;
	}

	@Override
	public void updateResourceSourceAttribution(final Integer resourceSourceId, final String domainName, final String attribution, final Integer frameBreaker, final User user, final Boolean isBlacklisted) throws Exception {

		final ResourceSource resourceSource = resourceRepository.findResourceByresourceSourceId(resourceSourceId);
		rejectIfNull(resourceSource, GL0056, 404, RESOURCE);
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
		if (domainName != null && isBlacklisted != null && isBlacklisted) {
			if (getUserService().isContentAdmin(user)) {
				final List<Resource> resources = resourceRepository.findAllResourceBySourceId(resourceSourceId);
				resourceSource.setIsBlacklisted(1);
				String sharingType = null;
				String gooruOids = "";
				int count = 0;
				if (resources != null && resources.size() <= 5000) {
					for (final Resource resource : resources) {
						sharingType = resource.getSharing();
						if (sharingType.equalsIgnoreCase(PUBLIC)) {
							resource.setSharing(PRIVATE);
						}
						resource.setBatchId(DEL_FROM_SEARCH_INDEX);
						if (count > 0) {
							gooruOids += ",";
						}
						gooruOids += resource.getGooruOid();
						count++;
					}
					indexHandler.setReIndexRequest(gooruOids, IndexProcessor.INDEX, RESOURCE, null, false, false);
					this.resourceRepository.saveAll(resources);
				} else if (resources != null && resources.size() > 5000) {
					throw new BadRequestException(generateErrorMessage(GL0001));
				}
			} else {
				throw new AccessDeniedException(generateErrorMessage("GL0002"));
			}
		}

		if (domainName != null && frameBreaker != null && getUserService().isContentAdmin(user)) {
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
				indexHandler.setReIndexRequest(gooruOIds, IndexProcessor.INDEX, RESOURCE, null, false, false);
				this.resourceRepository.saveAll(resources);
			} else if (resources != null && resources.size() > 5000) {
				throw new BadRequestException(generateErrorMessage(GL0004));
			}
		}
	}

	@Override
	public ResourceSource createResourcesourceAttribution(final String domainName, final String attribution) {

		final ResourceSource resourceSource = new ResourceSource();

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
	public Resource updateResourceThumbnail(final String gooruContentId, final String fileName, final Map<String, Object> formField) throws IOException {

		final Resource resource = this.findResourceByContentGooruId(gooruContentId);

		final File collectionDir = new File(resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder());

		if (!collectionDir.exists()) {
			collectionDir.mkdirs();
		}

		final Map<String, byte[]> files = (Map<String, byte[]>) formField.get(RequestUtil.UPLOADED_FILE_KEY);

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

			final File file = new File(resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder() + "/" + fileName);

			OutputStream out = new FileOutputStream(file);
			out.write(fileData);
			out.close();

			resource.setThumbnail(fileName);
			resourceRepository.save(resource);
			resourceImageUtil.sendMsgToGenerateThumbnails(resource);
			indexHandler.setReIndexRequest(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE, null, false, false);
		}

		return resource;
	}

	@Override
	public void mapSourceToResource(final Resource resource) {
		if (resource != null && resource.getResourceSource() == null) {
			if (ResourceType.Type.RESOURCE.getType().equalsIgnoreCase(resource.getResourceType().getName()) || ResourceType.Type.VIDEO.getType().equalsIgnoreCase(resource.getResourceType().getName())) {
				final String domainName = BaseUtil.getDomainName(resource.getUrl());
				if (domainName != null) {
					final ResourceSource resourceSource = this.getResourceRepository().findResourceSource(domainName);
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
	public Resource getResourceByResourceInstanceId(final String resourceInstanceId) {
		return resourceRepository.getResourceByResourceInstanceId(resourceInstanceId);
	}

	public ContentService getContentService() {
		return contentService;
	}

	public void setContentService(final ContentService contentService) {
		this.contentService = contentService;
	}

	public SessionActivityRepository getSessionActivityRepository() {
		return sessionActivityRepository;
	}

	@Override
	public ResourceInfo getResourcePageCount(final String resourceId) {
		return resourceRepository.getResourcePageCount(resourceId);
	}

	@Override
	public boolean shortenedUrlResourceCheck(String url) {
		boolean isShortenedUrl = false;
		String domainName = BaseUtil.getDomainName(url);
		final String domainType = ResourceSource.ResourceSourceType.SHORTENDED_DOMAIN.getResourceSourceType();
		if(domainName != null){
			final String type = resourceRepository.shortenedUrlResourceCheck(domainName, domainType);
			if (type != null && type.equalsIgnoreCase(ResourceSource.ResourceSourceType.SHORTENDED_DOMAIN.getResourceSourceType())) {
				isShortenedUrl = true;
			}
		}
		return isShortenedUrl;
	}

	@Override
	public List<Resource> listResourcesUsedInCollections(final Map<String, String> filters) {
		return this.getResourceRepository().listResourcesUsedInCollections(filters);
	}

	public ResourceMetadataCo updateYoutubeResourceFeeds(final Resource resource) {
		return updateYoutubeResourceFeeds(resource, false);
	}

	public ResourceMetadataCo updateYoutubeResourceFeeds(final Resource resource, final boolean isUpdate) {
		ResourceMetadataCo resourceFeeds = null;
		final ResourceCio resourceCio = getResourceCassandraService().read(resource.getGooruOid());
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
	public Resource updateResourceInfo(final Resource resource) {
		final String filePath = resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder() + resource.getUrl();
		final PDFFile pdfFile = GooruImageUtil.getPDFFile(filePath);
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
		}
		return resource;
	}

	@Override
	public Map<String, Object> getSuggestedResourceMetaData(final String url, final String title, final boolean fetchThumbnail) {
		return getResourceImageUtil().getResourceMetaData(url, title, fetchThumbnail);
	}

	@Override
	public ActionResponseDTO<Resource> createResource(final Resource newResource, final User user) throws Exception {
		Resource resource = null;
		final Errors errors = validateResource(newResource);
		if (!errors.hasErrors()) {

			ResourceSource resourceSource = null;
			String domainName = null;
			newResource.setRecordSource(Resource.RecordSource.GAT.getRecordSource());
			if (newResource.getBrokenStatus() == null) {
				newResource.setBrokenStatus(0);
			}
			domainName =BaseUtil.getDomainName(newResource.getUrl());
			if(domainName != null){
			resourceSource = this.getResourceRepository().findResourceSource(domainName);
			}
			if (resourceSource != null && resourceSource.getFrameBreaker() != null && resourceSource.getFrameBreaker() == 1) {
				newResource.setHasFrameBreaker(true);
			} else {
				newResource.setHasFrameBreaker(false);
			}

			if (newResource.getResourceFormat() != null) {
				final CustomTableValue resourceCategory = this.getCustomTableRepository().getCustomTableValue(RESOURCE_CATEGORY_FORMAT, newResource.getResourceFormat().getValue());
				newResource.setResourceFormat(resourceCategory);
			}
			if (newResource.getInstructional() != null) {
				final CustomTableValue resourceType = this.getCustomTableRepository().getCustomTableValue(RESOURCE_INSTRUCTIONAL_USE, newResource.getInstructional().getValue());
				newResource.setResourceFormat(resourceType);
			}

			if (newResource.getCategory() != null) {
				newResource.setCategory(newResource.getCategory().toLowerCase());
			}
			// add to db and index.
			resource = handleNewResource(newResource, null, null);
			if (newResource.getPublisher() != null && newResource.getPublisher().size() > 0) {
				newResource.setPublisher(updateContentProvider(resource.getGooruOid(), newResource.getPublisher(), user, CustomProperties.ContentProviderType.PUBLISHER.getContentProviderType()));
			}
			if (newResource.getAggregator() != null && newResource.getAggregator().size() > 0) {
				newResource.setAggregator(updateContentProvider(resource.getGooruOid(), newResource.getAggregator(), user, CustomProperties.ContentProviderType.AGGREGATOR.getContentProviderType()));
			}
			if (newResource.getHost() != null && newResource.getHost().size() > 0) {
				resource.setHost(updateContentProvider(resource.getGooruOid(), newResource.getHost(), user, CustomProperties.ContentProviderType.HOST.getContentProviderType()));
			}
			ResourceInfo resourceInfo = new ResourceInfo();
			final String tags = newResource.getTags();
			resourceInfo.setTags(tags);
			resourceInfo.setLastUpdated(new Date());
			resource.setTags(tags);
			resourceInfo.setResource(resource);
			resource.setResourceInfo(resourceInfo);

			resourceRepository.save(resourceInfo);
			s3ResourceApiHandler.updateOrganization(resource);

			this.mapSourceToResource(resource);
			try {
				this.getResourceEventLog().getEventLogs(resource, true, false, user);
			} catch (Exception e) {
				LOGGER.debug("error" + e.getMessage());
			}
		}
		return new ActionResponseDTO<Resource>(resource, errors);
	}

	@Override
	public ActionResponseDTO<Resource> updateResource(final String resourceId, final Resource newResource, final List<String> resourceTags, final User user) throws Exception {
		final Resource resource = this.resourceRepository.findResourceByContentGooruId(resourceId);
		rejectIfNull(resource, GL0056, 404, RESOURCE);
		final Errors errors = validateUpdateResource(newResource, resource);
		final JSONObject itemData = new JSONObject();
		if (!errors.hasErrors()) {
			if (getUserService().isContentAdmin(user)) {
				ResourceSource resourceSource = null;
				String domainName = null;
				if (newResource.getUrl() != null) {
					if (!resource.getUrl().equalsIgnoreCase(newResource.getUrl())) {
						itemData.put("url", newResource.getUrl());
						resource.setUrl(newResource.getUrl());
						domainName = BaseUtil.getDomainName(newResource.getUrl());
						if(domainName != null){
							resourceSource = this.getResourceRepository().findResourceSource(domainName);
						}
						if (resourceSource != null && resourceSource.getFrameBreaker() != null && resourceSource.getFrameBreaker() == 1) {
							resource.setHasFrameBreaker(true);
						} else {
							resource.setHasFrameBreaker(false);
						}
						this.mapSourceToResource(resource);
					} else {
						throw new BadRequestException(generateErrorMessage("GL0005"));
					}
				}
			}
			if (newResource.getTitle() != null) {
				itemData.put(TITLE, newResource.getTitle());
				resource.setTitle(newResource.getTitle());
			}
			if (newResource.getS3UploadFlag() != null) {
				resource.setS3UploadFlag(newResource.getS3UploadFlag());
			}
			if (newResource.getDescription() != null) {
				itemData.put(DESCRIPTION, newResource.getDescription());
				resource.setDescription(newResource.getDescription());
			}
			if (newResource.getHasFrameBreaker() != null) {
				itemData.put(HAS_FRAME_BREAKER, newResource.getHasFrameBreaker());
				resource.setHasFrameBreaker(newResource.getHasFrameBreaker());

			} else {
				resource.setHasFrameBreaker(false);
			}
			if (newResource.getIsFeatured() != null) {
				itemData.put(IS_FEATURED, newResource.getIsFeatured());
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
				itemData.put(RESOURCEFORMAT, newResource.getResourceFormat().getValue());
				final CustomTableValue customTableValue = this.getCustomTableRepository().getCustomTableValue(RESOURCE_CATEGORY_FORMAT, newResource.getResourceFormat().getValue());
				resource.setResourceFormat(customTableValue);
			}
			if (newResource.getGrade() != null) {
				resource.setGrade(newResource.getGrade());
			}
			if (newResource.getCategory() != null) {
				itemData.put(CATEGORY, newResource.getCategory());
				resource.setCategory(newResource.getCategory().toLowerCase());
			}
			if (!StringUtils.isEmpty(newResource.getMediaType())) {
				resource.setMediaType(newResource.getMediaType());
			}
			if (newResource.getSharing() != null && !newResource.getSharing().isEmpty()) {
				itemData.put(SHARING, newResource.getSharing());
				SessionContextSupport.putLogParameter("sharing-" + resource.getGooruOid(), resource.getSharing() + " to " + newResource.getSharing());
				resource.setSharing(newResource.getSharing());
			}
			if (newResource.getTags() != null && !newResource.getTags().isEmpty()) {
				itemData.put(TAGS, newResource.getTags());
				resource.setTags(newResource.getTags());
			}
			if (newResource.getLicense() != null) {
				itemData.put(LICENSE, newResource.getLicense().getName());
				License licenseData = this.getResourceRepository().getLicenseByLicenseName(newResource.getLicense().getName());
				if (licenseData != null) {
					resource.setLicense(licenseData);
				}
			}
			if (newResource.getMomentsOfLearning() != null && newResource.getMomentsOfLearning().size() > 0) {
				resource.setMomentsOfLearning(this.getCollectionService().updateContentMeta(newResource.getMomentsOfLearning(), resource.getGooruOid(), user, MOMENTS_OF_LEARNING));
			} else {
				resource.setMomentsOfLearning(this.getCollectionService().setContentMetaAssociation(this.getCollectionService().getContentMetaAssociation(MOMENTS_OF_LEARNING), resource.getGooruOid(), MOMENTS_OF_LEARNING));
			}
			if (newResource.getEducationalUse() != null && newResource.getEducationalUse().size() > 0) {
				resource.setEducationalUse(this.getCollectionService().updateContentMeta(newResource.getEducationalUse(), resource.getGooruOid(), user, EDUCATIONAL_USE));
			} else {
				resource.setEducationalUse(this.getCollectionService().setContentMetaAssociation(this.getCollectionService().getContentMetaAssociation(EDUCATIONAL_USE), resource.getGooruOid(), EDUCATIONAL_USE));
			}

			if (newResource.getPublisher() != null && newResource.getPublisher().size() > 0) {
				updateContentProvider(resource.getGooruOid(), newResource.getPublisher(), user, CustomProperties.ContentProviderType.PUBLISHER.getContentProviderType());
			}
			if (newResource.getAggregator() != null && newResource.getAggregator().size() > 0) {
				updateContentProvider(resource.getGooruOid(), newResource.getAggregator(), user, CustomProperties.ContentProviderType.AGGREGATOR.getContentProviderType());
			}
			if (newResource.getHost() != null && newResource.getHost().size() > 0) {
				updateContentProvider(resource.getGooruOid(), newResource.getHost(), user, CustomProperties.ContentProviderType.HOST.getContentProviderType());
			}

			if (resourceTags != null && resourceTags.size() > 0) {
				resource.setResourceTags(this.getContentService().createTagAssoc(resource.getGooruOid(), resourceTags, user));
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
			setContentProvider(resource);
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

			indexHandler.setReIndexRequest(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE, null, false, false);
		}
		if (newResource.getAttach() != null) {
			this.getResourceImageUtil().moveAttachment(newResource, resource);
		}
		getAsyncExecutor().deleteFromCache(V2_ORGANIZE_DATA + resource.getUser().getPartyUid() + "*");
		try {
			this.getResourceEventLog().getEventLogs(resource, itemData, user);
		} catch (Exception e) {
			LOGGER.debug("error" + e.getMessage());
		}
		return new ActionResponseDTO<Resource>(resource, errors);

	}

	@Override
	public List<String> updateContentProvider(final String gooruOid, final List<String> providerList, final User user, final String providerType) {
		final CustomTableValue customTableValue = this.getCustomTableRepository().getCustomTableValue(_CONTENT_PROVIDER_TYPE, providerType);
		final List<ContentProviderAssociation> contentProviderAssociationList = this.getContentRepository().getContentProviderByGooruOid(gooruOid, null, providerType);

		if (contentProviderAssociationList.size() > 0) {
			this.getContentRepository().removeAll(contentProviderAssociationList);
		}
		for (final String provider : providerList) {
			ContentProvider contentProvider = this.getContentRepository().getContentProviderByName(provider, CONTENT_PROVIDER_TYPE + providerType);
			if (contentProvider == null) {
				contentProvider = new ContentProvider();
				contentProvider.setName(provider);
				contentProvider.setActiveFlag(true);
				contentProvider.setType(customTableValue);
				this.getContentRepository().save(contentProvider);
			}

			final ContentProviderAssociation contentProviderAssociation = new ContentProviderAssociation();
			contentProviderAssociation.setContentProvider(contentProvider);
			final ResourceSource resourceSource = new ResourceSource();
			resourceSource.setDomainName(provider);
			resourceSource.setActiveStatus(0);
			this.getResourceRepository().save(resourceSource);
			contentProviderAssociation.setResourceSource(resourceSource);
			contentProviderAssociation.setGooruOid(gooruOid);
			contentProviderAssociation.setAssociatedDate(new Date(System.currentTimeMillis()));
			contentProviderAssociation.setAssociatedBy(user);
			this.getContentRepository().save(contentProviderAssociation);
		}
		return providerList;
	}

	@Override
	public List<Resource> listResourcesUsedInCollections(final String limit, final String offset, final User user) {
		return this.getResourceRepository().listResourcesUsedInCollections(Integer.parseInt(limit), Integer.parseInt(offset));

	}

	@Override
	public Resource deleteTaxonomyResource(final String resourceId, final Resource newResource, final User user) {

		Resource resource = resourceRepository.findResourceByContentGooruId(resourceId);
		rejectIfNull(resource, GL0056, RESOURCE);
		deleteResourceTaxonomy(resource, newResource.getTaxonomySet());
		return resource;
	}

	@Override
	public void saveOrUpdateResourceTaxonomy(final Resource resource, final Set<Code> taxonomySet) {
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
	}

	@Override
	public void deleteResourceTaxonomy(final Resource resource, final Set<Code> taxonomySet) {

		final Set<Code> codes = resource.getTaxonomySet();
		final Set<Code> removeCodes = new HashSet<Code>();
		for (Code removeCode : taxonomySet) {
			removeCode = (Code) (this.getTaxonomyRepository().findCodeByTaxCode(removeCode.getCodeId() != null ? removeCode.getCodeId().toString() : removeCode.getCode()));
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
				throw new NotFoundException(generateErrorMessage(GL0056, _TAXONOMY), GL0056);
			}
		}
		resource.setTaxonomySet(codes);
		this.getResourceRepository().save(resource);
		indexHandler.setReIndexRequest(resource.getGooruOid(), IndexProcessor.INDEX, RESOURCE, null, false, false);
	}

	@Override
	public void saveOrUpdateGrade(final Resource resource, final Resource newResource) {
		if (newResource.getGrade() != null) {
			String grade = newResource.getGrade();
			String resourceGrade = resource.getGrade();
			List<String> newResourceGrades = Arrays.asList(grade.split(","));
			if (resourceGrade != null) {
				List<String> resourceGrades = Arrays.asList(resourceGrade.split(","));
				if (resourceGrades != null) {
					for (final String newGrade : resourceGrades) {
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
	}

	@Override
	public List<User> addCollaborator(final String collectionId, final User user, final String collaboratorId, final String collaboratorOperation) {
		final Content content = contentRepository.findContentByGooruId(collectionId);
		if (content == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, _COLLECTION), GL0056);
		}
		if (collaboratorId != null) {
			List<String> collaboratorsList = Arrays.asList(collaboratorId.split("\\s*,\\s*"));
			return collectionUtil.updateNewCollaborator(content, collaboratorsList, user, COLLECTION_COLLABORATE, collaboratorOperation);
		}

		return null;
	}

	private Errors validateResource(Resource resource) throws Exception {
		final Errors errors = new BindException(resource, RESOURCE);
		if (resource != null) {
			rejectIfNullOrEmpty(errors, resource.getUrl(), URL, GL0006, generateErrorMessage(GL0006, URL));
		}
		return errors;
	}

	public void deleteContentProvider(String gooruOid, String providerType, String name) {
		this.getContentRepository().deleteContentProvider(gooruOid, providerType, name);
	}

	private Errors validateUpdateResource(Resource newResource, Resource resource) throws Exception {
		final Errors errors = new BindException(newResource, RESOURCE);
		rejectIfNull(errors, resource, RESOURCE_ALL, GL0056, generateErrorMessage(GL0056, RESOURCE));
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
				LOGGER.debug("error" + e.getMessage());
			}
		}
		return false;

	}

	public TaxonomyRespository getTaxonomyRepository() {
		return taxonomyRepository;
	}

	public ResourceEventLog getResourceEventLog() {
		return resourceEventLog;
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

	public UserRepository getUserRepository() {
		return userRepository;
	}

	@Override
	public Resource resourcePlay(String gooruContentId, User apiCaller, boolean more) throws Exception {
		Resource resource = this.findResourceByContentGooruId(gooruContentId);

		if (resource == null) {
			throw new NotFoundException(generateErrorMessage("GL0003"), "GL0003");
		}
		resource.setViews(this.resourceCassandraService.getLong(resource.getGooruOid(), STATISTICS_VIEW_COUNT));
		resource.setViewCount(resource.getViews());
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

	@Override
	public Resource findLtiResourceByContentGooruId(String gooruContentId) {
		return resourceRepository.findLtiResourceByContentGooruId(gooruContentId);
	}

	public CollectionService getCollectionService() {
		return collectionService;
	}

	@Override
	public void updateStatisticsData(List<StatisticsDTO> statisticsList, boolean skipReindex) {
		ResourceCio resourceCio = null;
		ResourceStasCo resourceStasCo = null;
		Collection<ResourceCio> resourceCioList = new ArrayList<ResourceCio>();
		Collection<String> resourceIds = new ArrayList<String>();
		Collection<String> collectionIds = new ArrayList<String>();

		for (StatisticsDTO statisticsDTO : statisticsList) {
			resourceCio = new ResourceCio();
			resourceStasCo = new ResourceStasCo();
			resourceCio.setId(statisticsDTO.getGooruOid());
			if (statisticsDTO.getResourceType() != null && statisticsDTO.getResourceType().equalsIgnoreCase("scollection")) {
				collectionIds.add(resourceCio.getId());
			} else {
				resourceIds.add(resourceCio.getId());
			}
			if (statisticsDTO.getViews() != null) {
				resourceStasCo.setViewsCount(String.valueOf(statisticsDTO.getViews()));
			}
			if (statisticsDTO.getSubscription() != null) {
				resourceStasCo.setSubscriberCount(String.valueOf(statisticsDTO.getSubscription()));
			}
			if (statisticsDTO.getRatings() != null) {
				resourceStasCo.setRating(String.valueOf(statisticsDTO.getRatings()));
			}
			if (statisticsDTO.isValid()) {
				resourceCio.setStas(resourceStasCo);
				resourceCioList.add(resourceCio);
			}
		}
		if (resourceCioList.size() > 0) {
			resourceCassandraService.save(resourceCioList, resourceIds);
			if (!skipReindex) {
				if (resourceIds.size() > 0) {
					indexHandler.setReIndexRequest(StringUtils.join(resourceIds, ','), IndexProcessor.INDEX, RESOURCE, null, false, false);
				}
				if (collectionIds.size() > 0) {
					indexHandler.setReIndexRequest(StringUtils.join(collectionIds, ','), IndexProcessor.INDEX, SCOLLECTION, null, false, false);
				}
			}
		}
	}

	@Override
	public Map<String, Object> checkResourceUrlExists(String url, boolean checkShortenedUrl) throws Exception {
		Resource resource = findResourceByUrl(url, Sharing.PUBLIC.getSharing(), null);
		Map<String, Object> response = new HashMap<String, Object>();
		if (resource != null) {
			response.put(RESOURCE, resource);
		}
		if (checkShortenedUrl) { 
			response.put(SHORTENED_URL_STATUS, shortenedUrlResourceCheck(url));
		}
		
		return response;
	}
	

	@Override
	public List<User> getUsersByResourceId(String resourceId, Integer limit, Integer offset) {
		return this.getResourceRepository().getUsersByResourceId(resourceId, limit, offset);
	}

	public CollectionRepository getCollectionRepository() {
		return collectionRepository;
	}

	
}
