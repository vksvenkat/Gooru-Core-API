/////////////////////////////////////////////////////////////
// TagRepository.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.tag;

import java.util.List;

import org.ednovo.gooru.core.api.model.ContentTagAssoc;
import org.ednovo.gooru.core.api.model.Tag;
import org.ednovo.gooru.core.api.model.TagSynonyms;
import org.ednovo.gooru.core.api.model.UserTagAssoc;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;

public interface TagRepository extends BaseRepository {
	Tag findTagByLabel(String label);

	Tag findTagByTagId(String tagUid);

	List<Tag> getTagByUser(Integer userId);

	List<Tag> getTags(Integer offset, Integer limit);

	List<Tag> getTag(String gooruOid);

	List<ContentTagAssoc> getTagContentAssoc(String tagGooruOid, Integer limit, Integer offset, boolean skipPagination);

	UserTagAssoc getUserTagassocById(String gooruOid, String tagGooruOid);

	List<UserTagAssoc> getContentTagByUser(String gooruOid, Integer limit, Integer offset, Boolean skipPagination);

	List<UserTagAssoc> getTagAssocUser(String tagGooruOid, Integer limit, Integer offset);

	TagSynonyms findSynonymByName(String targetTagName);

	TagSynonyms findTagSynonymById(Integer tagSynonymsId);

	List<TagSynonyms> getTagSynonyms(String tagGooruOid);

	TagSynonyms getSynonymByTagAndSynonymId(String tagGooruOid, Integer synonymsId);
	
	List<Object[]> getResourceByLabel(String label, Integer limit, Integer offset, boolean skipPagination, String goourUid);
	
	Long getResourceByLabelCount(String label, String gooruUid);
}
