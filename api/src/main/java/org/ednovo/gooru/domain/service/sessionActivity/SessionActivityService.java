/////////////////////////////////////////////////////////////
// SessionActivityService.java
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
package org.ednovo.gooru.domain.service.sessionActivity;

import java.util.List;

import org.ednovo.gooru.core.api.model.SessionActivity;
import org.ednovo.gooru.core.api.model.SessionActivityItem;
import org.ednovo.gooru.core.api.model.User;


public interface SessionActivityService {
	SessionActivity createNewSessionActivity(User user);

	SessionActivity updateSessionActivity(String sessionActivityUid, String status);

	List<SessionActivity> getUserSessionActivityList(String userUid) throws Exception;

	SessionActivityItem createNewSessionActivityItem(String sessionActivityUid, String contentUid, String parentContentUid, String contentType, Integer questionAttemptId) throws Exception;

	SessionActivityItem getContentSessionActivityItem(String contentUid, String userUid, String status) throws Exception;

	List<SessionActivityItem> getContentSessionActivityItemList(String contentUid, String userUid, String status) throws Exception;

	SessionActivityItem getUserLastOpenSessionActivityItem(String userUid, String status) throws Exception;

	void updateSessionActivityByContent(String contentUid, String status);
}
