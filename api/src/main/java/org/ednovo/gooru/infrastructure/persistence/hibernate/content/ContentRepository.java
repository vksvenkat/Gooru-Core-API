/////////////////////////////////////////////////////////////
// ContentRepository.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.content;

import java.util.List;

import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentAssociation;
import org.ednovo.gooru.core.api.model.ContentPermission;
import org.ednovo.gooru.core.api.model.ContentTagAssoc;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.StatusType;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;

public interface ContentRepository extends BaseRepository {

	Content findByContent(Long contentId);

	Content findByContentGooruId(String gooruContentId);

	void delete(String gooruContentId);

	Resource findByResourceType(String typeId, String url);

	Content findContentByGooruId(String gooruContentId);

	Content findContentByGooruId(String gooruContentId, boolean fetchUser);

	User findContentOwner(String gooruContentId);

	ContentAssociation getCollectionAssocContent(String contentGooruOid);

	Code getCodeByName(String name);

	Boolean checkContentPermission(Long contentId, String partyUid);

	StatusType getStatusType(String name);

	List getIdsByUserUId(String userUId, String typeName);

	ContentTagAssoc getContentTagById(String gooruOid, String tagGooruOid);

	List<ContentTagAssoc> getContentTagByContent(String gooruOid, Integer limit, Integer offset);

	List<Content> getContentByUserUId(String userUId);

	void deleteContentByContentId(String contentId);
	
	List<ContentPermission> getContentPermission(Long contentId, String partyUid);

}
