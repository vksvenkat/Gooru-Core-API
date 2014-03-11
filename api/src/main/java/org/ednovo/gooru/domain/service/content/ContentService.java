/*
*ContentService.java
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

package org.ednovo.gooru.domain.service.content;

import java.util.List;

import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentAssociation;
import org.ednovo.gooru.core.api.model.Quote;
import org.ednovo.gooru.core.api.model.QuoteDTO;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.User;

public interface ContentService {
	Content findByContent(Long contentId);

	Content findByContentGooruId(String gooruContentId);

	void delete(String gooruContentId);

	Resource findByResourceType(String typeId, String url);

	Content findContentByGooruId(String gooruContentId);

	Content findContentByGooruId(String gooruContentId, boolean fetchUser);

	User findContentOwner(String gooruContentId);

	ContentAssociation getCollectionAssocContent(String contentGooruOid);

	List<QuoteDTO> createNote(String gooruUserId, String description, String url, String noteType, String grade, String title, String licenseName, String topic, String tagTypeName, String classplan, String contextAnchor, String contextAnchorText, User user);

	String createQuote(String gooruUserId, String description, String url, String pinToken, String sessionToken, String noteType, String grade, String title, String licenseName, String topic, String classplan, String tagTypeName, User user);

	List<QuoteDTO> updateNote(String gooruUserId, String gooruContentId, String description, String url, String noteType, String grade, String title, String licenseName, String topic, String tagTypeName, String anchor, User user);

	Quote saveAnnotation(Quote annotation);

	List<QuoteDTO> copyNote(String noteId, User apiCaller);

	void deleteNote(String noteId);

	List getIdsByUserUId(String userUId, String typeName);

}
