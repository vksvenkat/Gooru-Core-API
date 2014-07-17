/////////////////////////////////////////////////////////////
// SessionActivityRepository.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.activity;

import java.util.List;

import org.ednovo.gooru.core.api.model.SessionActivity;
import org.ednovo.gooru.core.api.model.SessionActivityItem;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;

public interface SessionActivityRepository extends BaseRepository {

	SessionActivity getSessionActivity(String sessionActivityUid);

	List<SessionActivity> getUserSessionActivityList(String userUid);

	SessionActivityItem getContentSessionActivityItem(String contentUid, String userUid, String status);

	List<SessionActivityItem> getContentSessionActivityItemList(String contentUid, String userUid, String status);

	SessionActivityItem getUserLastOpenSessionActivityItem(String userUid, String status);

	List<SessionActivity> getContentSessionActivityList(String contentUid, String status);

	List<SessionActivityItem> getSubContentSessionActivityItemList(String contentUid, String userUid, String status);

	Integer getStudiedResourceCount(String contentUid, String userUid, String status);

}
