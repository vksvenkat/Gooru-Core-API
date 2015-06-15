package org.ednovo.gooru.core.api.model;

public enum RequestMappingUri {	

	TAXONOMYCOURSE("/taxonomycourse/"),SUBJECT("/subject/"), SUBDOMAIN("/subdomain/"), DOMAIN("/domain/");
	
	private String requestMappingUri;
	
	private RequestMappingUri(String requestMappingUri){
		this.requestMappingUri = requestMappingUri;
	}
	
	public String getRequestMappingUri(){
		return this.requestMappingUri;
	}

}
