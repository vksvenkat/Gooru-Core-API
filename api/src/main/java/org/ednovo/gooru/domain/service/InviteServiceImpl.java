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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.InviteUser;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserClass;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.eventlogs.ClasspageEventLog;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.gooru.infrastructure.mail.MailHandler;
import org.ednovo.gooru.infrastructure.persistence.hibernate.ClassRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.InviteRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InviteServiceImpl extends BaseServiceImpl implements InviteService, ParameterProperties, ConstantProperties {

	@Autowired
	private ClassRepository classRepository;

	@Autowired
	private InviteRepository inviteRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CustomTableRepository customTableRepository;

	@Autowired
	private MailHandler mailHandler;

	@Autowired
	private ClasspageEventLog classpageEventLog;

	@Autowired
	private SettingService settingService;

	private static final Logger LOGGER = LoggerFactory.getLogger(InviteServiceImpl.class);

	private static final String INVITE_USER_STATUS_KEY = "invite_user_status_";

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void inviteUserForClass(List<String> emails, final String classUid, final User user) {
		final UserClass userClass = this.getClassRepository().getClassById(classUid);
		rejectIfNull(userClass, GL0056, 404, CLASS);
		// To Do, Fix me
		CustomTableValue status = this.getCustomTableRepository().getCustomTableValue(INVITE_USER_STATUS, PENDING);
		List<String> emailIds = new ArrayList<String>();
		User creator = getUserRepository().findByGooruId(userClass.getUserUid());
		for (String email : emails) {
			InviteUser inviteUser = this.getInviteRepository().findInviteUserById(email, userClass.getPartyUid(), null);
			if (inviteUser == null) {
				inviteUser = new InviteUser(email, userClass.getPartyUid(), CLASS, user, status);
				this.getInviteRepository().save(inviteUser);
				emailIds.add(email);
			}
			try {
				if (userClass.getVisibility()) {
					this.getMailHandler().sendMailToOpenClassUser(email, userClass.getPartyUid(), creator, userClass.getName(), user.getUsername(), userClass.getGroupCode());
				} else {
					this.getMailHandler().sendMailToInviteUser(email, userClass.getPartyUid(), creator, userClass.getName(), user.getUsername(), userClass.getGroupCode());
				}
			} catch (Exception e) {
				LOGGER.error(ERROR, e);
			}
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Map<String, Object> getInvites(String gooruOid, String status, int limit, int offset) {
		StringBuilder key = new StringBuilder(INVITE_USER_STATUS_KEY).append(status);
		List<Map<String, Object>> resultSet = getInviteRepository().getInvitee(gooruOid, key.toString(), limit, offset);
		Map<String, Object> searchResults = new HashMap<String, Object>();
		List<Map<String, Object>> users = new ArrayList<Map<String, Object>>();
		int count = 0;
		if (resultSet != null && resultSet.size() > 0) {
			for (Map<String, Object> result : resultSet) {
				Object gooruUid = result.get(GOORU_UID);
				if (gooruUid != null) {
					result.put(PROFILE_IMG_URL, BaseUtil.changeHttpsProtocolByHeader(settingService.getConfigSetting(ConfigConstants.PROFILE_IMAGE_URL, TaxonomyUtil.GOORU_ORG_UID)) + "/" + String.valueOf(gooruUid) + ".png");
				}
				users.add(result);
			}
			count = getInviteRepository().getInviteeCount(gooruOid, key.toString());
		}

		searchResults.put(TOTAL_HIT_COUNT, count);
		searchResults.put(SEARCH_RESULT, users);
		return searchResults;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteInvitee(String gooruOid, String email) {
		this.getInviteRepository().deleteInviteUser(gooruOid, email);
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public InviteRepository getInviteRepository() {
		return inviteRepository;
	}

	public CustomTableRepository getCustomTableRepository() {
		return customTableRepository;
	}

	public MailHandler getMailHandler() {
		return mailHandler;
	}

	public ClassRepository getClassRepository() {
		return classRepository;
	}
}
