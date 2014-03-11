/*
*SessionActivityServiceImpl.java
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

package org.ednovo.gooru.domain.service.sessionActivity;

import java.util.Date;
import java.util.List;

import org.ednovo.gooru.core.api.model.SessionActivity;
import org.ednovo.gooru.core.api.model.SessionActivityItem;
import org.ednovo.gooru.core.api.model.SessionActivityType;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.infrastructure.persistence.hibernate.activity.SessionActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class SessionActivityServiceImpl  implements SessionActivityService  {
    
	@Autowired
	private SessionActivityRepository sessionActivityRepository;


	@Override
	public SessionActivity  createNewSessionActivity(User user) {
		SessionActivity sessionActivity = null;
		if (user != null) { 
			sessionActivity = new SessionActivity();
			sessionActivity.setUserUid(user.getGooruUId());
			sessionActivity.setCreatedOn(new Date());
			sessionActivity.setStatus(SessionActivityType.Status.OPEN.getStatus());
			this.getSessionActivityRepository().save(sessionActivity);		
		} else { 
			throw new AccessDeniedException("Do not have permission to create new session activity."); 
		}
		return sessionActivity;
	}
	
	@Override
	public List<SessionActivity> getUserSessionActivityList(String userUid) throws Exception { 
		List<SessionActivity> sessionActivity = getSessionActivityRepository().getUserSessionActivityList(userUid);
		if (sessionActivity.size() == 0) {
			throw new Exception("no Session activity found");
		} 
		return sessionActivity;
	}
	
	@Override
	public SessionActivity updateSessionActivity(String sessionActivityUid, String status) {
		SessionActivity sessionActivity = this.getSessionActivityRepository().getSessionActivity(sessionActivityUid);
		if (sessionActivity != null) {
				sessionActivity.setStatus(status);
		} else { 
			throw new AccessDeniedException("no Session activity found"); 
		}
		return sessionActivity;
	}
	
	@Override
	public void  updateSessionActivityByContent(String contentUid, String status) {
		List<SessionActivity> sessionActivityList = this.getSessionActivityRepository().getContentSessionActivityList(contentUid, SessionActivityType.Status.OPEN.getStatus());
		if (sessionActivityList != null) {
			for (SessionActivity sessionActivity : sessionActivityList) {
				sessionActivity.setStatus(status);
			}
			this.getSessionActivityRepository().saveAll(sessionActivityList);
		} 	
	}

	@Override
	public SessionActivityItem createNewSessionActivityItem(String sessionActivityUid, String contentUid, String subContentUid, String contentType, Integer questionAttemptId) throws Exception {
		SessionActivityItem sessionActivityItem = null;
		if (SessionActivityType.ContentType.COLLECTION.getContentType().equalsIgnoreCase(contentType) || SessionActivityType.ContentType.QUIZ.getContentType().equalsIgnoreCase(contentType)) {
			SessionActivity sessionActivity = this.getSessionActivityRepository().getSessionActivity(sessionActivityUid);
			if (sessionActivity != null) {
				sessionActivityItem = new SessionActivityItem();
				sessionActivityItem.setSessionActivity(sessionActivity);
				sessionActivityItem.setContentUid(contentUid);
				sessionActivityItem.setSubContentUid(subContentUid);
				sessionActivityItem.setContentType(contentType);
				sessionActivityItem.setCreatedOn(new Date());
				sessionActivityItem.setQuestionAttemptId(questionAttemptId);
				this.getSessionActivityRepository().save(sessionActivityItem);	
			} else { 
				throw new Exception("no Session activity found");
			}
		} else { 
			throw new Exception("invalid content type");
		}
		return sessionActivityItem;
	}
	
	public SessionActivityRepository getSessionActivityRepository() {
		return sessionActivityRepository;
	}

	@Override
	public SessionActivityItem getContentSessionActivityItem(String contentUid, String userUid, String status) throws Exception {
		SessionActivityItem SessionActivityItem = this.getSessionActivityRepository().getContentSessionActivityItem(contentUid, userUid, status);
		return SessionActivityItem;
	}
	@Override
	public List<SessionActivityItem> getContentSessionActivityItemList(String contentUid, String userUid, String status) throws Exception {
		List<SessionActivityItem> sessionActivityItem = this.getSessionActivityRepository().getContentSessionActivityItemList(contentUid, userUid, status);
		return sessionActivityItem;
	}

	@Override
	public SessionActivityItem getUserLastOpenSessionActivityItem(String userUid, String status) throws Exception {
		SessionActivityItem SessionActivityItem = this.getSessionActivityRepository().getUserLastOpenSessionActivityItem(userUid, status);
		return SessionActivityItem;
	}
}
