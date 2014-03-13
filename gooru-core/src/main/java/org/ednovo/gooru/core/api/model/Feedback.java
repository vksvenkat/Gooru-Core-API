package org.ednovo.gooru.core.api.model;

import java.util.Date;
import java.util.List;

import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.OrganizationModel;
import org.ednovo.gooru.core.api.model.User;


public class Feedback extends OrganizationModel {

	private static final long serialVersionUID = 7264331217576442053L;
	private String gooruOid;
	private CustomTableValue target;
	private CustomTableValue type;
	private CustomTableValue category;
	private String freeText;
	private Integer score;
	private String assocGooruOid;
	private String assocUserUid;
	private User creator;
	private String referenceKey;
	private String url;
	private Date createdDate;
	private String context;
	private List<CustomTableValue> types;
	private CustomTableValue product;
	private String contextPath;
	private String notes;
	
	
	public Feedback() {	}
	public Feedback(Feedback feedback) {
		this.target = feedback.getTarget();
		this.category = feedback.getCategory();
		this.assocGooruOid = feedback.getAssocGooruOid();
		this.assocUserUid = feedback.getAssocUserUid();
		this.freeText = feedback.getFreeText();
		this.product = feedback.getProduct();
		this.contextPath = feedback.getContextPath();
		this.context = feedback.getContext();
		this.score = feedback.getScore();
	}

	public String getAssocGooruOid() {
		return assocGooruOid;
	}

	public void setAssocGooruOid(String assocGooruOid) {
		this.assocGooruOid = assocGooruOid;
	}

	public String getAssocUserUid() {
		return assocUserUid;
	}

	public void setAssocUserUid(String assocUserUid) {
		this.assocUserUid = assocUserUid;
	}

	public String getReferenceKey() {
		return referenceKey;
	}

	public void setReferenceKey(String referenceKey) {
		this.referenceKey = referenceKey;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public User getCreator() {
		return creator;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Integer getScore() {
		return score;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setFreeText(String freeText) {
		this.freeText = freeText;
	}

	public String getFreeText() {
		return freeText;
	}

	public void setTarget(CustomTableValue target) {
		this.target = target;
	}

	public CustomTableValue getTarget() {
		return target;
	}

	public void setType(CustomTableValue type) {
		this.type = type;
	}

	public CustomTableValue getType() {
		return type;
	}

	public void setCategory(CustomTableValue category) {
		this.category = category;
	}

	public CustomTableValue getCategory() {
		return category;
	}

	public void setGooruOid(String gooruOid) {
		this.gooruOid = gooruOid;
	}

	public String getGooruOid() {
		return gooruOid;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getContext() {
		return context;
	}

	public void setTypes(List<CustomTableValue> types) {
		this.types = types;
	}

	public List<CustomTableValue> getTypes() {
		return types;
	}
	public void setProduct(CustomTableValue product) {
		this.product = product;
	}
	public CustomTableValue getProduct() {
		return product;
	}
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
	public String getContextPath() {
		return contextPath;
	}

	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
}
