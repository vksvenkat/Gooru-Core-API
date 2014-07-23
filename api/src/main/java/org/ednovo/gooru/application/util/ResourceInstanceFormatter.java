/////////////////////////////////////////////////////////////
// ResourceInstanceFormatter.java
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

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.Question;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceInstance;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.Segment;
import org.ednovo.gooru.core.api.model.Textbook;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.resource.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
 * This class will soon be removed.
 */
@Deprecated
@Component
public class ResourceInstanceFormatter implements ParameterProperties{

	public static enum SKELETON_SEGMENT {
		ASSESSMENT("assessment"), SUGGESTED_STUDY("suggestedstudy"), HOMEWORK("homework");

		private String value;

		SKELETON_SEGMENT(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}

	private static ResourceInstanceFormatter instance = new ResourceInstanceFormatter();

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private CollectionUtil collectionUtil;

	public ResourceInstanceFormatter() {

		instance = this;
	}

	public String getResourceInstanceXml(ResourceInstance resourceInstance) {
		return getResourceInstance(resourceInstance).asXML();
	}

	private String replaceOption(String option) {
		if (option == null || option.equals("")) {
			return "";
		}
		return "<option>" + option + "</option>";
	}

	public String getQuestionSet(Question question) {
		String durationStr = "";
		if (question.getDuration() != null && !question.getDuration().equals("") && question.getDuration() != 0) {

			durationStr = "<duration>" + question.getDuration() + "</duration>";
		}
		String questionText = "";
		if (question != null) {
			questionText = StringUtils.defaultString(StringEscapeUtils.escapeHtml(question.getTitle()));
		}
		StringBuilder questionXML = new StringBuilder("<question_set><question><question_text>" + questionText + "</question_text>" + durationStr);

		if (question.getQuestionType() != null && question.getQuestionType().equals(Question.MULTIPLE_CHOICE)) {
			questionXML.append("<answers correct=\"" + question.getCorrectOption() + "\">");
			questionXML.append(replaceOption(question.getOptionA()));
			questionXML.append(replaceOption(question.getOptionB()));
			questionXML.append(replaceOption(question.getOptionC()));
			questionXML.append(replaceOption(question.getOptionD()));
			questionXML.append(replaceOption(question.getOptionE()));
			questionXML.append(replaceOption(question.getOptionF()));
			questionXML.append("</answers>");
		}
		questionXML.append("</question></question_set>");

		return questionXML.toString();
	}

	private Element getQuestionResource(Question question) {

		StringBuilder questionXML = new StringBuilder(getQuestionSet(question));

		Element resourceElm = DocumentHelper.createElement(RESOURCE);
		if (question.getGooruOid() != null) {
			resourceElm.addAttribute(ID, question.getGooruOid());
		} else {
			String resourceId = UUID.randomUUID().toString();
			resourceElm.addAttribute(ID, resourceId);
			question.setGooruOid(resourceId);
		}

		resourceElm.addAttribute(TYPE, ResourceType.Type.QUIZ.getType());

		Element nativeurl = resourceElm.addElement(NATIVE_URL);

		if (question.getUrl() != null) {
			nativeurl.addText(question.getUrl());
		} else {
			nativeurl.addText("");
		}

		Element folder = resourceElm.addElement(RESOURCE_FOLDER);

		if (question.getFolder() != null) {
			folder.addText(question.getFolder());
		} else {
			folder.addText("");
		}

		Element questionSet = null;
		try {
			questionSet = (Element) DocumentHelper.parseText(questionXML.toString()).getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("Error while converting to a document");
		}
		resourceElm.add(questionSet);

		return resourceElm;
	}

	private Element getResourceInstance(ResourceInstance resourceInstance) {

		Element resourceElement = null;
		try {
			Resource resource = resourceInstance.getResource();
			if (resource.getResourceType().getName().equals(ResourceType.Type.QUIZ.getType()) && !(resource instanceof Question)) {
				resource = getResourceService().findResourceByContentGooruId(resource.getGooruOid());
			} else if (resource.getResourceType().getName().equals(ResourceType.Type.TEXTBOOK.getType()) && !(resource instanceof Textbook)) {
				resource = getResourceService().findTextbookByContentGooruId(resource.getGooruOid());
			}

			if (resource.getResourceType().getName().equals(ResourceType.Type.QUIZ.getType())) {
				resourceElement = getQuestionResource((Question) resource);
			} else {

				resourceElement = DocumentHelper.createElement(RESOURCE);
				resourceElement.addAttribute(TYPE, resource.getResourceType().getName());
				resourceElement.addAttribute(TYPE_DESC, resource.getResourceType().getDescription());
				resourceElement.addAttribute(SHARING, resource.getSharing());
				resourceElement.addAttribute(CATEGORY, resource.getCategory());
				resourceElement.addAttribute(SHORTENED_URL_STATUS, resourceInstance.getShortenedUrlStatus() != null ? resourceInstance.getShortenedUrlStatus().toString() : FALSE);
				if (resource.getGooruOid() == null) {

					String resourceId = UUID.randomUUID().toString();
					resourceElement.addAttribute(ID, resourceId);
					resource.setGooruOid(resourceId);

				}
				resourceElement.addAttribute(ID, resource.getGooruOid());

				if (resourceInstance.getTitle() != null) {
					setElementText(resourceElement, LABEL, resourceInstance.getTitle(), true);
				} else {
					setElementText(resourceElement, LABEL, resource.getTitle(), true);
				}

				setElementText(resourceElement, NATIVE_URL, resource.getUrl(), true);
				setElementText(resourceElement, RESOURCE_FOLDER, resource.getFolder(), true);
				if (resourceInstance.getDescription() != null) {
					setElementText(resourceElement, DESCRIPTION, resourceInstance.getDescription(), true);
				} else {
					setElementText(resourceElement, DESCRIPTION, resource.getDescription(), true);
				}

				if (resource instanceof Textbook) {
					setElementText(resourceElement, DOCUMENT_ID, ((Textbook) resource).getDocumentId(), true);
					setElementText(resourceElement, DOCUMENT_KEY, ((Textbook) resource).getDocumentKey(), true);
				}
			}

			Element instructorNotes = resourceElement.addElement(INSTRUCTOR_NOTES);

			setElementText(instructorNotes, INSTRUCTION, resourceInstance.getNarrative(), true);
			setElementText(instructorNotes, START, resourceInstance.getStart(), true);
			setElementText(instructorNotes, STOP, resourceInstance.getStop(), true);

			// Element tagSet = resourceElement.addElement("tagSet");

			Element resourceSource = resourceElement.addElement(RESOURCE_SOURCE);
			if (resourceInstance.getResource().getResourceSource() != null) {
				setElementText(resourceSource, RESOURCE_SOURCE_ID, StringUtils.defaultString(resourceInstance.getResource().getResourceSource().getResourceSourceId().toString()), true);
				setElementText(resourceSource, ATTRIBUTION, StringUtils.defaultString(resourceInstance.getResource().getResourceSource().getAttribution()), true);
				setElementText(resourceSource, RESOURCE_DOMAIN_NAME, StringUtils.defaultString(resourceInstance.getResource().getResourceSource().getDomainName()), true);
			}

			Element resourceInfo = resourceElement.addElement(RESOURCE_INFO);
			if (resourceInstance.getResource().getResourceInfo() != null) {
				Integer numOfPages = resourceInstance.getResource().getResourceInfo().getNumOfPages();
				setElementText(resourceInfo, NUM_OF_PAGES, StringUtils.defaultString(numOfPages != null ? numOfPages + "" : ""), true);
			}

			String brokenResource = "0";
			if (resourceInstance.getResource().getBrokenStatus() != null && resourceInstance.getResource().getBrokenStatus() != 0) {
				brokenResource = "1";
			}
			String hasFrameBreaker = "0";
			if (resourceInstance.getResource().getHasFrameBreaker() != null && resourceInstance.getResource().getHasFrameBreaker().booleanValue()) {
				hasFrameBreaker = "1";
			}
			Element resourceStatus = resourceElement.addElement(RESOURCE_STATUS);
			setElementText(resourceStatus, STATUS_IS_BROKEN, brokenResource, true);
			setElementText(resourceStatus, STATUS_IS_FRAMEBREAKER , hasFrameBreaker, true);

			if (resourceInstance.getResourceInstanceId() != null) {
				resourceElement.addAttribute(RESOURCE_INSTANCE_ID, resourceInstance.getResourceInstanceId());
			}

			setElementText(resourceElement, THUMBNAIL, resourceInstance.getResource().getThumbnail(), true);
			setElementText(resourceElement, ASSET_URI, resourceInstance.getResource().getAssetURI(), true);

			Element thumbnails = resourceElement.addElement(THUMBNAILS);
			setElementText(thumbnails, URL, resourceInstance.getResource().getThumbnails().getUrl(), true);
			setElementText(thumbnails, DIMENSIONS, resourceInstance.getResource().getThumbnails().getDimensions(), true);
			String isDefaultThumbnail = FALSE;
			if (resourceInstance.getResource().getThumbnails().isDefaultImage()) {
				isDefaultThumbnail = TRUE;
			}
			setElementText(thumbnails, DEFAULT_IMAGE, isDefaultThumbnail, true);
			setElementText(resourceElement, TAXONOMY_DATA_SET, TAXONOMY_DATA_SET != null ? TAXONOMY_DATA_SET.toString() : "", true);
			return resourceElement;
		} catch (Exception e) {
			return null;
		}
	}

	public String getResourceInstanceXmls(List<ResourceInstance> resourceInstances) {
		Element resourcesElement = DocumentHelper.createElement(RESOURCES);
		for (ResourceInstance resourceInstance : resourceInstances) {
			Element element = getResourceInstance(resourceInstance);
			if (element != null) {
				resourcesElement.add(element);
			}
		}
		return resourcesElement.asXML();
	}

	private void getSegmentResourceXmls(Element element, Segment segment) {
		if (segment.getResourceInstances() != null) {
			for (ResourceInstance resourceInstance : segment.getResourceInstances()) {
				final Element resourceInstanceElement = getResourceInstance(resourceInstance);
				if (resourceInstanceElement != null) {
					element.add(resourceInstanceElement);
				}
			}
		}
	}

	public String getSegmentXml(Segment segment) {
		return getSegment(segment).asXML();
	}

	private Element getSegment(Segment segment) {

		Element element = DocumentHelper.createElement(SEGMENT);

		element.addAttribute(ID, segment.getSegmentId());

		String duration = segment.getDuration();

		duration = (duration == null || duration.trim().equals("")) ? "0" : duration;

		setElementText(element, DURATION, duration, true);

		setElementText(element, TITLE, segment.getTitle(), true);

		setElementText(element, DESCRIPTION, segment.getDescription(), true);

		Element resources = element.addElement(RESOURCES);

		getSegmentResourceXmls(resources, segment);

		Element rendition = element.addElement(RENDITION);

		setElementText(rendition, NATIVE_URL, segment.getRenditionUrl(), true);

		setElementText(element, TYPE, (segment.getType() == null) ? DIRECT_INSTRUCTION : segment.getType(), true);

		setElementText(element, CONCEPT, segment.getConcept(), true);

		setElementText(element, SEGMENT_IMAGE, segment.getSegmentImage(), true);

		return element;
	}

	private void getResourceSegments(Element element, Set<Segment> segments) {
		getResourceSegments(element, segments, true);
	}

	private void getResourceSegments(Element element, Set<Segment> segments, boolean retriveSkeletons) {
		for (Segment segment : segments) {
			boolean add = true;
			if (!retriveSkeletons) {
				for (SKELETON_SEGMENT skeleton : SKELETON_SEGMENT.values()) {
					if (segment.getType().equals(skeleton.getValue())) {
						add = false;
						break;
					}
				}
			}
			if (add) {
				element.add(getSegment(segment));
			}
		}
	}

	public String getResourceSegmentXmls(Set<Segment> segments) {
		Element element = DocumentHelper.createElement(SEGMENTS);
		getResourceSegments(element, segments);
		return element.asXML();
	}

	public String getLearnguideXml(Learnguide learnguide, boolean retriveSkeletons) {
		return getLearnguide(learnguide, retriveSkeletons).asXML();
	}

	public String getLearnguideInfoXml(Learnguide learnguide) {
		return getLearnguideInfo(learnguide).asXML();
	}

	public Element getLearnguideInfo(Learnguide learnguide) {
		Element infoElement = DocumentHelper.createElement(INFO);
		setElementText(infoElement,LESSON, learnguide.getLesson(), true);
		String duration = learnguide.getDuration();
		duration = (duration == null || duration.trim().equals("")) ? ZERO : duration;
		setElementText(infoElement, DURATION, duration, true);
		setElementText(infoElement, VOCABULARY, learnguide.getVocabulary(), true);
		setElementText(infoElement, LESSON_OBJECTIVES, learnguide.getGoals(), true);
		Element curriculumElements = infoElement.addElement(CURRICULUMS);
		setElementText(curriculumElements,CURRICULUM, learnguide.getCurriculum(), true);
		setElementText(infoElement, INSTRUCTION, learnguide.getMedium(), true);
		setElementText(infoElement,NOTES, learnguide.getNotes(), true);
		setElementText(infoElement,GRADE, learnguide.getGrade(), true);
		return infoElement;
	}

	private Element getLearnguide(Learnguide learnguide, boolean retriveSkeletons) {
		Element element = DocumentHelper.createElement(GOORU_CLASSPLAN);
		element.add(getLearnguideInfo(learnguide));
		Element segmentElements = element.addElement(SEGMENTS);
		if (learnguide.getResourceSegments() != null) {
			getResourceSegments(segmentElements, learnguide.getResourceSegments(), retriveSkeletons);
		}

		return element;
	}

	private Element setElementText(Element srcElement, String key, String text, boolean showNull) {
		Element element = srcElement.addElement(key);
		if (text == null && showNull) {
			text = "";
		}

		if (text != null) {
			element.addText(text);
		}
		return element;
	}

	public Document getLearnguideDocument(Learnguide learnguide) {
		return getLearnguideDocument(learnguide, true);
	}

	public Document getLearnguideDocument(Learnguide learnguide, boolean retriveSkeletons) {
		Document document = null;
		try {
			String xml = getLearnguide(learnguide, retriveSkeletons).asXML();
			document = DocumentHelper.parseText(xml);
		} catch (Exception e) {
			throw new RuntimeException("Error while converting xml to document", e);
		}
		return document;
	}

	public static ResourceInstanceFormatter getInstance() {
		return instance;
	}

	public ResourceService getResourceService() {
		return resourceService;
	}

	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	public CollectionUtil getCollectionUtil() {
		return collectionUtil;
	}

	public void setCollectionUtil(CollectionUtil collectionUtil) {
		this.collectionUtil = collectionUtil;
	}

}
