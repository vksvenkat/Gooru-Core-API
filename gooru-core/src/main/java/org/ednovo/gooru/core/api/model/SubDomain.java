package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

public class SubDomain implements Serializable{

	/**
	 * 
	 */
    private static final long serialVersionUID = 7789419722908609593L;

    @Id
    private Integer subDomainId;
    
    @Column
    private Integer CourseId;
    
    @Column
    private Integer domainId;
    
    @Column 
    private Date createdOn;

	public Integer getSubDomainId() {
		return subDomainId;
	}

	public void setSubDomainId(Integer subDomainId) {
		this.subDomainId = subDomainId;
	}

	public Integer getCourseId() {
		return CourseId;
	}

	public void setCourseId(Integer courseId) {
		CourseId = courseId;
	}

	public Integer getDomainId() {
		return domainId;
	}

	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
    
    
}
