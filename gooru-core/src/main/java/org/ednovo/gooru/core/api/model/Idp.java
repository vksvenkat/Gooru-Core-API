package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class Idp implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8057521950467645504L;

	
	public static  String defaultIdp = "NA"; 
	
	private Short idpId;
	private String name;
	private Short gooruInstalled = 0;
	
	public Short getIdpId() {
		return idpId;
	}
	public void setIdpId(Short idpId) {
		this.idpId = idpId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Short getGooruInstalled() {
		return gooruInstalled;
	}
	public void setGooruInstalled(Short gooruInstalled) {
		this.gooruInstalled = gooruInstalled;
	}
	public static String getDefaultIdp() {
		return defaultIdp;
	}
	public static void setDefaultIdp(final String DEFAULT_IDP) {
		defaultIdp = DEFAULT_IDP;
	}
}
