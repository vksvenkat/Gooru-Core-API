package org.ednovo.gooru.core.api.model;

import java.util.Map;

import org.ednovo.gooru.core.application.util.BaseUtil;
import org.springframework.util.StringUtils;

public abstract class UserGroupSupport extends SessionContextSupport {
	
	private static String DEFAULT_ORGANIZATION = "4261739e-ccae-11e1-adfb-5404a609bd14";
	
	public static String getPartyPermitsAsString() {
		UserCredential credential = getUserCredential();
		if (credential != null && credential.getPartyPermitsAsString() != null) {
			return credential.getPartyPermitsAsString();
		}
		return null;
	}

	public static String getCurrentUserUid() {
		UserCredential credential = getUserCredential();
		if (credential != null) {
			return credential.getUserUid();
		}
		return null;
	}

	public static String[] getUserOrganizationUids() {
		UserCredential credential = getUserCredential();
		if (credential != null && credential.getOrgPermits() != null) {
			return credential.getOrgPermits();
		}
		String organization = getUserOrganizationUid();
		return organization != null ? new String[] { organization } : null;
	}

	public static String[] getUserSubOrganizationUids() {
		UserCredential credential = getUserCredential();
		if (credential != null && credential.getSubOrganizationUids() != null && credential.getSubOrganizationUids().size() > 0) {
			return StringUtils.toStringArray(credential.getSubOrganizationUids());
		}
		return new String[] { getUserOrganizationUid() };
	}
	
	public static String getUserOrganizationUidsAsString() {
		UserCredential credential = getUserCredential();
		if (credential != null && credential.getOrgPermitsAsString() != null) {
			return credential.getOrgPermitsAsString();
		}
		return "'" + getUserOrganizationUid() + "'";
	}

	public static String getUserSubOrganizationUidsAsString() {
		UserCredential credential = getUserCredential();
		if (credential != null && credential.getSubOrganizationUidsString() != null) {
			return credential.getSubOrganizationUidsString();
		}
		return null;
	}
	
	public static String getUserOrganizationUid() {

		UserCredential credential = getUserCredential();

		if (credential != null && credential.getOrganizationUid() != null) {
			return credential.getOrganizationUid();
		}
		return DEFAULT_ORGANIZATION;
	}

	public static String[] getPartyPermits() {

		UserCredential credential = getUserCredential();

		if (credential != null) {
			return credential.getPartyPermits();
		}
		return new String[] {};
	}
	public static String getUserOrganizationNfsInternalPath(){
		UserCredential credential = getUserCredential();
		if (credential != null) {
			return credential.getOrganizationNfsInternalPath();
		}
		throw new RuntimeException("User internal path can not be null");
	}
	public static String getUserOrganizationNfsRealPath(){
		UserCredential credential = getUserCredential();
		if (credential != null) {
			return BaseUtil.changeHttpsProtocol(credential.getOrganizationNfsRealPath());
		}
		throw new RuntimeException("User real path can not be null");
	}
	
	public static String getUserOrganizationCdnDirectPath(){
		UserCredential credential = getUserCredential();
		if (credential != null) {
			return credential.getOrganizationCdnDirectPath();
		}
		return null;
	}
	
	public static String getTaxonomyPreference(){
		UserCredential credential = getUserCredential();
		if (credential != null) {
			return credential.getTaxonomyPreference();
		}
		return null;
	}
	
	public static Map<String, Map<String, String>> getMeta(){
		UserCredential credential = getUserCredential();
		if (credential != null) {
			return credential.getMeta();
		}
		return null;
	}
	
	public static Boolean isContentAdminAccess(){
		UserCredential credential = getUserCredential();
		if (credential != null) {
			return credential.getIsAdminAccessContent();
		}
		return false;
	}
}
