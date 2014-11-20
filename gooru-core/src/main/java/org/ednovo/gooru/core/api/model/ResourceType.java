package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

import javax.persistence.Entity;

@Entity(name = "resourceType")
public class ResourceType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3764335602004806426L;
	private String name;
	private String description;

	public static enum Type {
		PRESENTATION("ppt/pptx"), VIDEO("video/youtube"), QUIZ("question"), ANIMATION_SWF("animation/swf"), ANIMATION_KMZ("animation/kmz"), IMAGE("image/png"), RESOURCE("resource/url"), HANDOUTS("handouts"), CLASSPLAN("gooru/classplan"), TEXTBOOK("textbook/scribd"), STUDYSHELF("gooru/studyshelf"), EXAM(
				"exam/pdf"), CLASSBOOK("gooru/classbook"), NOTEBOOK("gooru/notebook"), QB_QUESTION("qb/question"), QB_RESPONSE("qb/response"), ASSESSMENT_QUIZ("assessment-quiz"), ASSESSMENT_EXAM("assessment-exam"), ASSESSMENT_QUESTION("assessment-question"), SCOLLECTION("scollection"), SHELF(
				"shelf"), FOLDER("folder"), ASSESSMENT("assessment"), ASSIGNMENT("assignment"), CLASSPAGE("classpage"), PATHWAY("pathway"), ALL("all"), Quiz("quiz"), DOCUMENTS("documents"), AUDIO("audio"), READINGS("readings"), MAPS("maps"), CASES("cases"), APPLICATION("application"), OAUTH("oauth"), LTI("lti") ;

		private String type;

		Type(String type) {
			this.type = type;
		}

		public String getType() {
			return this.type;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
