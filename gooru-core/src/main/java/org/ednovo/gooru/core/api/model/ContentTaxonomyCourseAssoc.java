package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class ContentTaxonomyCourseAssoc implements Serializable {

	private static final long serialVersionUID = -7317988227021535278L;

	private Content content;
	
	private TaxonomyCourse taxonomyCourse;

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public TaxonomyCourse getTaxonomyCourse() {
		return taxonomyCourse;
	}

	public void setTaxonomyCourse(TaxonomyCourse taxonomyCourse) {
		this.taxonomyCourse = taxonomyCourse;
	}
}
