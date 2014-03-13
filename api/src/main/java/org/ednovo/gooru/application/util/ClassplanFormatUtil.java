/////////////////////////////////////////////////////////////
// ClassplanFormatUtil.java
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.application.util.StringUtil;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.classplan.LearnguideRepository;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.resource.ClientResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import flexjson.JSONDeserializer;

@Repository
public class ClassplanFormatUtil implements ParameterProperties{

	protected final static Logger logger = LoggerFactory.getLogger(ClassplanFormatUtil.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private LearnguideRepository classplanRepository;

	public void retreiveAllClassplans() throws Exception {

		List<Learnguide> classplanList = this.getClassplanRepository().getAll(Learnguide.class);

		for (Learnguide classplan : classplanList) {
			System.out.println("Content id : " + classplan.getContentId());
			String xml = classplan.retrieveXml();
			try {
				convertXML(xml, classplan.getGooruOid());
			} catch (Exception e) {
				logger.info("Error while converting classplan with id : " + classplan.getContentId(), e);
				System.out.println("Error while converting classplan with id : " + classplan.getContentId());
				e.printStackTrace(System.out);
			}
		}
	}

	public boolean convertXML(String classplanXml, String classplanId) throws Exception {
		boolean updateInfo = false;

		Document classplanDoc;

		classplanDoc = StringUtil.convertString2Document(classplanXml);

		Node suggestedreading = classplanDoc.selectSingleNode("//suggestedreading");

		if (suggestedreading != null) {

			// Create a new Segment for assessment
			String studySegId = insertSegment(classplanId, SUGGESTEDSTUDY, SUGGESTED_STUDY);
			if (!((Element) suggestedreading).getData().toString().equals("")) {
				String data = ((Element) suggestedreading).getData().toString();
				data = data.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&apos;", "'");
				data = "<suggestedreading>" + data + "</suggestedreading>";

				Document anchorTagsInSS = StringUtil.convertString2Document(data); // StringUtil.convertString2Document("<ul><li><a href=\"http://quiz.thefullwiki.org/Mammal\">http://quiz.thefullwiki.org/Mammal</a></li></ul>");

				List<Node> atSS = anchorTagsInSS.selectNodes("//a");
				Map<String, String> suggestedStudyMap = new HashMap<String, String>();
				for (Node node : atSS) {
					Element newele = (Element) node;
					suggestedStudyMap.put(newele.getText(), newele.attributeValue(HREF));
					insertResource(classplanId, studySegId, ResourceType.Type.RESOURCE.getType(), newele.attributeValue(HREF), newele.getText());
				}
			}
			// Delete the suggested study node from info element
			suggestedreading.detach();
			updateInfo = true;
		}

		Node homework = classplanDoc.selectSingleNode("//homework");

		if (homework != null) {

			// create a new segment for homework
			String homeworkSegId = insertSegment(classplanId, "homework", "Homework");

			if (!((Element) homework).getData().toString().equals("")) {

				String data = ((Element) homework).getData().toString();
				data = data.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&apos;", "'");
				data = "<homework>" + data + "</homework>";

				Document anchorTagsInhomework = StringUtil.convertString2Document(data); // StringUtil.convertString2Document("<ul><li><a href=\"http://quiz.thefullwiki.org/Mammal\">http://quiz.thefullwiki.org/Mammal</a></li></ul>");

				List<Node> ath = anchorTagsInhomework.selectNodes("//a");
				Map<String, String> homwworkMap = new HashMap<String, String>();
				for (Node node : ath) {
					Element newele = (Element) node;
					homwworkMap.put(newele.getText(), newele.attributeValue(HREF));
					insertResource(classplanId, homeworkSegId, ResourceType.Type.RESOURCE.getType(), newele.attributeValue(HREF), newele.getText());
				}
			}
			// Delete the homework node from info element
			homework.detach();
			updateInfo = true;
		}

		Node assessment = classplanDoc.selectSingleNode("//assessment");

		if (assessment != null) {

			// Create a new Segment for assessment
			String assessmentSegId = insertSegment(classplanId, ASSESSMENT, ASSESSMENTS);

			if (!((Element) assessment).getData().toString().equals("")) {
				String data = ((Element) assessment).getData().toString();
				data = data.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&apos;", "'");
				data = "<assessment>" + data + "</assessment>";
				Document anchorTagsInassessment = StringUtil.convertString2Document(data); // StringUtil.convertString2Document("<ul><li><a href=\"http://quiz.thefullwiki.org/Mammal\">http://quiz.thefullwiki.org/Mammal</a></li></ul>");

				List<Node> ata = anchorTagsInassessment.selectNodes("//a");
				Map<String, String> assesmentMap = new HashMap<String, String>();
				for (Node node : ata) {
					Element newele = (Element) node;
					assesmentMap.put(newele.getText(), newele.attributeValue(HREF));

					insertResource(classplanId, assessmentSegId, ResourceType.Type.RESOURCE.getType(), newele.attributeValue(HREF), newele.getText());
				}
			}

			// Delete the assessment node from info element
			assessment.detach();
			updateInfo = true;
		}

		if (updateInfo) {
			// FIXME
			// this.getClassplanRepository().updateClasplanInfoXml(classplanDoc.selectSingleNode("//info").asXML()
			// , classplanId);
			System.out.println("Success: " + classplanId);
		}

		return false;
	}

	private String insertSegment(String classplanId, String type, String title) throws Exception {

		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add(TITLE, title);
		params.add(DURATION, "");
		params.add(DESCRIPTION, "");
		params.add(TYPE, type);
		params.add(RENDITION, "");
		// params.add("sessionToken", "3d1945f6-9177-11e0-9bf5-12313b083ca6");
		// params.add("sessionToken","3d1945f6-9177-11e0-9bf5-12313b083ca6");
		params.add(SESSIONTOKEN, "3d1945f6-9177-11e0-9bf5-12313b083ca6"); // demo

		String segmentJson = this.getRestTemplate().postForObject("http://staging.goorulearning.org/gooruapi/rest/classplan/" + classplanId + "/segment.json", params, String.class);
		JSONObject json = new JSONObject(segmentJson);

		Iterator iter = json.keys();
		String value = "";
		while (iter.hasNext()) {
			String key = iter.next().toString();
			value = json.get(key).toString();
		}

		HashMap segment = new JSONDeserializer<HashMap<String, String>>().deserialize(value);

		String segmentId = segment.get(ID).toString();

		return segmentId;
	}

	private void insertResource(String classplanId, String segmentId, String type, String url, String title) {

		String address = "http://staging.goorulearning.org/gooruapi/rest/classplan/" + classplanId + "/segment/" + segmentId + "/resource/web.json";
		ClientResource client = new ClientResource(address);

		Form params = new Form();
		params.add(PROP_NAME, title);
		params.add(PROP_URL, url);
		params.add(PROP_DESC, "");
		params.add(PROP_TYPE, type);
		params.add(INSTRUCTION, "");
		// params.add("sessionToken", "3d1945f6-9177-11e0-9bf5-12313b083ca6");
		// params.add("sessionToken","3d1945f6-9177-11e0-9bf5-12313b083ca6");
		params.add(SESSIONTOKEN, "3d1945f6-9177-11e0-9bf5-12313b083ca6"); // demo
		client.post(params, MediaType.APPLICATION_JSON);
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public LearnguideRepository getClassplanRepository() {
		return classplanRepository;
	}

}
