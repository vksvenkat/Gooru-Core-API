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
	private Integer isBlacklisted;
	
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
		this.isBlacklisted = isBlacklisted;
	}
	public Integer getIsBlacklisted() {
		return isBlacklisted;
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
