/*******************************************************************************
 * UserTransformer.java
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

import java.util.Iterator;

import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.Organization;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.api.model.UserTransModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import flexjson.transformer.ObjectTransformer;

public class UserTransformer extends ObjectTransformer {

	private boolean deepSerialize;
	private static XStream xStream = new XStream(new DomDriver());
	
	private  String DEFAULT_ORGANIZATION_NAME = "gooru"; 
	
	private static String RUSD_ORGANIZATION_NAME = "rusd";
	
	private static String GOORU_ORG_UID = "4261739e-ccae-11e1-adfb-5404a609bd14";

	public UserTransformer(boolean deepSerialize) {
		this.deepSerialize = deepSerialize;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(UserTransformer.class);
	
	@Override
	public void transform(Object object) {
		User user = (User) object;
		String currentUserOrgUid = UserGroupSupport.getUserOrganizationUid();
		Organization organization = user.getOrganization();
		String organizationUid = null;
		if(organization != null){
			organizationUid = user.getOrganization().getPartyUid();
			DEFAULT_ORGANIZATION_NAME = user.getOrganization().getPartyName();
		} else {
			organizationUid = user.getOrganizationUid();
		}
		boolean hasSubOrgPermission  = false;
		if(organizationUid != null){
			hasSubOrgPermission = hasSubOrgPermission(organizationUid);
		}
		
		boolean sharedSecretMatched = false;
		
		String storedSecret = UserGroupSupport.getUserCredential().getStoredSecretKey();
		String receivedSecret  = UserGroupSupport.getUserCredential().getSharedSecretKey();
		if(receivedSecret != null && storedSecret != null && storedSecret.equals(receivedSecret)){
			sharedSecretMatched = true;
		}
		
		boolean isGooruUser = false;
		
		if(currentUserOrgUid != null && currentUserOrgUid.equals(GOORU_ORG_UID) && organizationUid != null){
			isGooruUser  = true;
		}
		
		if (user != null && currentUserOrgUid != null && organizationUid != null && (organizationUid.equals(currentUserOrgUid) || hasSubOrgPermission || sharedSecretMatched || isGooruUser) ) {

			if (deepSerialize) {
				try {
					user = (User) xStream.fromXML(xStream.toXML(user));
				} catch (Exception ex) {
					deepSerialize = false;
				}
			}

			UserTransModel userModel = new UserTransModel();
		//	if(currentUserOrgUid )
			userModel.setPartyUid(user.getPartyUid());
			userModel.setGooruUId(user.getPartyUid());
			userModel.setConfirmStatus(user.getConfirmStatus());
			userModel.setEmailId(user.getEmailId());
			userModel.setFirstName(user.getFirstName());
			userModel.setGooruUId(user.getGooruUId());
			userModel.setLastName(user.getLastName());
			userModel.setRegisterToken(user.getRegisterToken());
			userModel.setUserId(user.getUserId());
			userModel.setUsername(user.getUsername());
			userModel.setRegisterToken(user.getRegisterToken());
			userModel.setConfirmStatus(user.getConfirmStatus());
			userModel.setParentUser(user.getParentUser());
			userModel.setAccountTypeId(user.getAccountTypeId());
			userModel.setUserRoleSet(user.getUserRoleSet());
			userModel.setViewFlag(user.getViewFlag());
			userModel.setCreatedOn(user.getCreatedOn() != null ? user.getCreatedOn().toString() : null);
			userModel.setIsDeleted(user.getIsDeleted());
			userModel.setCustomFields(user.getCustomFields());
			userModel.setMeta(UserGroupSupport.getMeta());
			userModel.setProfileImageUrl(UserGroupSupport.getProfileAssetURI() + user.getPartyUid() + ".png");
			if(user.getIdentities() != null){
				Iterator<Identity> iter = user.getIdentities().iterator();
				if (iter != null && iter.hasNext()) {
					Identity identity = iter.next();
					userModel.setLoginType(identity != null ? identity.getLoginType() : null);
					userModel.setAccountCreatedType(identity != null ? identity.getAccountCreatedType() : null);
					if (identity != null && identity.getExternalId() != null) { 
						String email =  identity.getExternalId().contains("@") ?  identity.getExternalId().split("@")[1] : null;
						if (email != null && email.contains(RUSD_ORGANIZATION_NAME)) { 
							userModel.setOrganizationName(RUSD_ORGANIZATION_NAME);
						}
					}
				}
			}  
			if (userModel.getOrganizationName() == null) {
			  userModel.setOrganizationName(DEFAULT_ORGANIZATION_NAME);
			}
			
			if (deepSerialize) {
				userModel.setIdentities(user.getIdentities());
			}

			getContext().transform(userModel);

		} else {
			logger.error("Serialization failed for user transformer");
			getContext().write(null);
		}
	}
	private boolean hasSubOrgPermission(String organizationId){
		String [] subOrgUids = UserGroupSupport.getUserOrganizationUids();
		if(subOrgUids != null && subOrgUids.length > 0){
			for(String userSuborganizationId : subOrgUids){
				if(organizationId.equals(userSuborganizationId)){
					return true;
				}
			}
		}
		return false;
	}

}
