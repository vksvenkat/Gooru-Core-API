/////////////////////////////////////////////////////////////
// SessionRepository.java
// gooru-api
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.infrastructure.persistence.hibernate.session;

import java.util.Map;

import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.SessionActivity;
import org.ednovo.gooru.core.api.model.SessionActivityItem;
import org.ednovo.gooru.core.api.model.UserActivityCollectionAssoc;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;

public interface SessionActivityRepository extends BaseRepository {

	SessionActivity getSessionActivityById(Long sessionActivityId);

	SessionActivityItem getSessionActivityItem(Long sessionActivityId, Long resourceId);
		
	Integer getSessionActivityItemAttemptCount(Long sessionActivityId, Long resourceId);
	
	Integer getSessionActivityReactionCount(Long sessionActivityId);
	
	Integer getSessionActivityRatingCount(Long sessionActivityId);
	
	Integer getQuestionCount(Long collectionId);
	
	Integer getTotalScore(Long sessionActivityId);
	
	AssessmentQuestion getQuestion(String gooruOid);
	
	SessionActivity getLastSessionActivity(Long classId,Long courseId,Long unitId,Long lessionId, Long collectionId, String userUid);
	
    Map<String, Object> getSessionActivityByCollectionId(String gooruOid, String user);

	Integer getClassSessionActivityCount(SessionActivity sessionActivity);

	Integer getCollectionSessionActivityCount(SessionActivity sessionActivity);

	void updateOldSessions(SessionActivity sessionActivity);

	Double getUnitTotalScore(SessionActivity sessionActivity);

	Double getLessonTotalScore(SessionActivity sessionActivity);

	UserActivityCollectionAssoc getUserActivityCollectionAssoc(String userUid, Long classContentId, Long collectionId);

	
}
