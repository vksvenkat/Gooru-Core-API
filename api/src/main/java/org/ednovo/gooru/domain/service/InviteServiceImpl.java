/*
 *InviteServiceImpl.java
 * gooru-api
 * Created by Gooru on 2014
 * Copyright (c) 2014 Gooru. All rights reserved.
 * http://www.goorulearning.org/
 *      
 * Permission is hereby granted, free of charge, to any 
 * person obtaining a copy of this software and associated 
 * documentation. Any one can use this software without any 
 * restriction and can use without any limitation rights 
 * like copy,modify,merge,publish,distribute,sub-license or 
 * sell copies of the software.
 * The seller can sell based on the following conditions:
 * 
 * The above copyright notice and this permission notice shall be   
 * included in all copies or substantial portions of the Software. 
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
 *  KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
 *  WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
 *  PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
 *  OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
 *  OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 *  THE SOFTWARE.
 */

package org.ednovo.gooru.domain.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.Classpage;
import org.ednovo.gooru.core.api.model.InviteUser;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.InviteRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InviteServiceImpl extends BaseServiceImpl implements InviteService, ParameterProperties, ConstantProperties {

	@Autowired
	private CollectionRepository collectionRepository;
	
	@Autowired
	private InviteRepository inviteRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CustomTableRepository customTableRepository;

	@Override
	public List<Map<String, String>> inviteUserForClass(List<String> emails, String classCode, User apiCaller) {
		Classpage classPage = this.getCollectionRepository().getClasspageByCode(classCode);
		if (classPage == null) {
			throw new NotFoundException(generateErrorMessage(GL0006, CLASS));
		}
		List<Map<String, String>> invites = new ArrayList<Map<String, String>>();
		List<InviteUser> inviteUsers = null;
		for (String email : emails) {
			InviteUser inviteUser = this.getInviteRepository().findInviteUserById(email, classPage.getGooruOid());
			inviteUsers = new ArrayList<InviteUser>();
			if (inviteUser  == null) {
				inviteUsers.add(createInviteUserObj(email,classPage.getGooruOid(), CLASS));
			}
			Map<String, String> inviteMap = new HashMap<String, String>();
			inviteMap.put(EMAIL, email);
			inviteMap.put(GOORU_OID, classPage.getGooruOid());
			inviteMap.put(STATUS, PENDING);
			invites.add(inviteMap);
		}
		if (inviteUsers != null) { 
			this.getInviteRepository().saveAll(inviteUsers);
		}
		return invites;

	}

	private InviteUser createInviteUserObj(String email, String gooruOid, String invitationType) {
		InviteUser  inviteUser = new InviteUser();
		inviteUser.setEmail(email);
		inviteUser.setCreatedDate(new Date());
		inviteUser.setGooruOid(gooruOid);
		inviteUser.setInvitationType(invitationType);
		inviteUser.setStatus(this.getCustomTableRepository().getCustomTableValue(INVITE_USER_STATUS, PENDING));
		return inviteUser; 	
	}
	
	public CollectionRepository getCollectionRepository() {
		return collectionRepository;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}
	
	public InviteRepository getInviteRepository() {
		return inviteRepository;
	}

	public void setCustomTableRepository(CustomTableRepository customTableRepository) {
		this.customTableRepository = customTableRepository;
	}

	public CustomTableRepository getCustomTableRepository() {
		return customTableRepository;
	}
}
