/////////////////////////////////////////////////////////////
// SessionService.java
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
package org.ednovo.gooru.domain.service.session;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.SessionActivity;
import org.ednovo.gooru.core.api.model.SessionActivityItem;
import org.ednovo.gooru.core.api.model.SessionActivityItemAttemptTry;
import org.ednovo.gooru.core.api.model.SessionItemFeedback;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.domain.service.BaseService;

public interface SessionService extends BaseService {

	ActionResponseDTO<SessionActivity> createSession(SessionActivity sessionActivity, User user);
	
	 SessionItemFeedback createSessionItemFeedback(String sessionId, SessionItemFeedback sessionItemFeedback, User user);

	ActionResponseDTO<SessionActivity> updateSession(String sessionId, SessionActivity sessionActivity);

	ActionResponseDTO<SessionActivityItem> createSessionItem(SessionActivityItem sessionActivityItem, String sessionId);

	ActionResponseDTO<SessionActivityItem> updateSessionItem(String sessionItemId, SessionActivityItem newSessionItem);

	SessionActivityItemAttemptTry createSessionItemAttemptTry(SessionActivityItemAttemptTry sessionActivityItemAttemptTry, String sessionId);

	SessionActivity getSession(String sessionId);
	
	File exportClass(String classGooruId);
}
