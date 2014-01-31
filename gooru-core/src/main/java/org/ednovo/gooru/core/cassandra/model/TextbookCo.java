/**
 * 
 */
package org.ednovo.gooru.core.cassandra.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "textbook")
public class TextbookCo {

	@Column
	private String documentid;

	@Column
	private String documentkey;

	public String getDocumentid() {
		return documentid;
	}

	public void setDocumentid(String documentid) {
		this.documentid = documentid;
	}

	public String getDocumentkey() {
		return documentkey;
	}

	public void setDocumentkey(String documentkey) {
		this.documentkey = documentkey;
	}
}