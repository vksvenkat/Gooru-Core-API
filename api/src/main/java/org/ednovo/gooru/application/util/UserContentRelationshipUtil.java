/////////////////////////////////////////////////////////////
// UserContentRelationshipUtil.java
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
package org.ednovo.gooru.application.util;

import java.util.Date;

import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserContentAssoc;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserContentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserContentRelationshipUtil {
	private static final Logger logger = LoggerFactory.getLogger(UserContentRelationshipUtil.class);

	private static UserContentRepository userContentRepository;
	
	public static final void updateUserContentRelationship(Content content, User user, UserContentAssoc.RELATIONSHIP relationship) {

		try {

			UserContentAssoc userContentAssoc = userContentRepository.getUserContentAssoc(user.getPartyUid(), content.getContentId(), relationship.getId());

			if (userContentAssoc == null) {
				userContentAssoc = new UserContentAssoc();
				userContentAssoc.setContent(content);
				userContentAssoc.setUser(user);
			}
			if (userContentAssoc.getUser() != null && userContentAssoc.getContent() != null) {
				userContentAssoc.setContentRelationship(relationship);
				userContentAssoc.setLastActiveDate(new Date());
				getUserContentRepository().save(userContentAssoc);
			}

		} catch (Exception exception) {
			logger.error("Saving content association failed" , exception);
		}
	}

	public static final void deleteUserContentRelationship(Content content, User user, UserContentAssoc.RELATIONSHIP relationship) {
		try {

			UserContentAssoc userContentAssoc = userContentRepository.getUserContentAssoc(user.getPartyUid(), content.getContentId(), relationship.getId());

			if (userContentAssoc != null ) {
				userContentRepository.deleteUserContentRelationShip(userContentAssoc);
			}

		} catch (Exception exception) {
			logger.error("Deleting content association failed" , exception);
		}
		
	}

/*	public static final void updateUserContentRelationship(Long contentId, Integer userId, UserContentAssoc.RELATIONSHIP relationship) {

		try {

			UserContentAssoc userContentAssoc = userContentRepository.getUserContentAssoc(userId, contentId, relationship.getId());

			if (userContentAssoc == null) {
				userContentAssoc = new UserContentAssoc();
				userContentAssoc.setContent((Content) getUserContentRepository().get(Content.class, contentId));
				userContentAssoc.setUser((User) getUserContentRepository().get(User.class, userId));
			}
			if (userContentAssoc.getUser() != null && userContentAssoc.getContent() != null) {
				userContentAssoc.setRelationship(relationship);
				getUserContentRepository().save(userContentAssoc);
			}

		} catch (Exception exception) {

		}
	}*/

	public static UserContentRepository getUserContentRepository() {
		return userContentRepository;
	}

	public static void setUserContentRepository(UserContentRepository userContentRepository) {
		UserContentRelationshipUtil.userContentRepository = userContentRepository;
	}

}
