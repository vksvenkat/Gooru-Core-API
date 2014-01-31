/**
 * 
 */
package org.ednovo.gooru.core.cassandra.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "instances")
public class InstanceCo {

	@Column
	private String ids;

	@Column
	private String titles;

	@Column
	private String descriptions;

	@Column
	private String naratives;

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getTitles() {
		return titles;
	}

	public void setTitles(String titles) {
		this.titles = titles;
	}

	public String getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(String descriptions) {
		this.descriptions = descriptions;
	}

	public String getNaratives() {
		return naratives;
	}

	public void setNaratives(String naratives) {
		this.naratives = naratives;
	}

}