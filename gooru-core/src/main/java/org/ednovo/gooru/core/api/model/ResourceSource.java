/*******************************************************************************
 * ResourceSource.java
 *  gooru-core
 *  Created by Gooru on 2014
 *  Copyright (c) 2014 Gooru. All rights reserved.
 *  http://www.goorulearning.org/
 *       
 *  Permission is hereby granted, free of charge, to any 
 *  person obtaining a copy of this software and associated 
 *  documentation. Any one can use this software without any 
 *  restriction and can use without any limitation rights 
 *  like copy,modify,merge,publish,distribute,sub-license or 
 *  sell copies of the software.
 *  
 *  The seller can sell based on the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be   
 *  included in all copies or substantial portions of the Software. 
 * 
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
 *   KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
 *   WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
 *   PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE       AUTHORS 
 *   OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
 *   OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *   OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *   WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 *   THE SOFTWARE.
 ******************************************************************************/
package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name="resourceSource")
public class ResourceSource implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2923061090528180601L;
	
	
	@Column
	private Integer resourceSourceId;
	
	@Column
	private String attribution;
	
	@Column
	private String domainName;
	
	@Column
	private Integer activeStatus;
	
	private ResourceSource resourceSource;
	
	@Column
	private Integer frameBreaker;
	
	@Column
	private String type;
	
	@Column
	private Integer IsBlacklisted;
	
	/* FIX for DO-1112 */
	private String sourceName;
	
	private Integer hasHttpsSupport;
	
	private Integer protocolSupported;
	
	public String getAttribution() {
		return attribution;
	}
	public void setAttribution(String attribution) {
		sourceName = attribution;
		this.attribution = attribution;
	}
	
	public Integer getActiveStatus() {
		return activeStatus;
	}
	public void setActiveStatus(Integer activeStatus) {
		this.activeStatus = activeStatus;
	}
	public void setResourceSourceId(Integer resourceSourceId) {
		this.resourceSourceId = resourceSourceId;
	}
	public Integer getResourceSourceId() {
		return resourceSourceId;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public String getDomainName() {
		return domainName;
	}
	public ResourceSource getResourceSource() {
		return resourceSource;
	}

	public void setResourceSource(ResourceSource resourceSource) {
		this.resourceSource = resourceSource;
	}
	public Integer getFrameBreaker() {
		return frameBreaker;
	}
	public void setFrameBreaker(Integer frameBreaker) {
		this.frameBreaker = frameBreaker;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public static enum ResourceSourceType {
		SHORTENDED_DOMAIN("shortenedDomain"), NORMAL_DOMAIN("normalDomain");

		String domainName;

		ResourceSourceType(String domainName) {
			this.domainName = domainName;
		}

		public String getResourceSourceType() {
			return this.domainName;
		}
	}

	/* FIX for DO-1112 */
	public String getSourceName() {
		sourceName = attribution;
		return sourceName;
	}
	public void setIsBlacklisted(Integer isBlacklisted) {
		IsBlacklisted = isBlacklisted;
	}
	public Integer getIsBlacklisted() {
		return IsBlacklisted;
	}
	public void setHasHttpsSupport(Integer hasHttpsSupport) {
		this.hasHttpsSupport = hasHttpsSupport;
	}
	public Integer getHasHttpsSupport() {
		return hasHttpsSupport;
	}
	public Integer getProtocolSupported() {
		return protocolSupported;
	}
	public void setProtocolSupported(Integer protocolSupported) {
		this.protocolSupported = protocolSupported;
	}

}
