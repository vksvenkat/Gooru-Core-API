/*
*ShelfService.java
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

package org.ednovo.gooru.domain.service.shelf;

import java.util.List;
import java.util.Set;

import javassist.NotFoundException;

import org.ednovo.gooru.core.api.model.Assessment;
import org.ednovo.gooru.core.api.model.ContentPermission;
import org.ednovo.gooru.core.api.model.Shelf;
import org.ednovo.gooru.core.api.model.ShelfItem;
import org.ednovo.gooru.core.api.model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.validation.Errors;


public interface ShelfService {

	Shelf createShelf(String shelfName, User user, boolean defaultFlag, boolean viewFlag);

	Shelf addResourceToSelf(String selfId, String resourceId, String addType, Errors errors, User apiCaller) throws Exception;

	List<Shelf> getShelfResources(String shelfId, String resourceType, String userId, String orderBy);

	List<Shelf> getUserSelf(String shelfToLoad, User user, boolean fetchAll);

	Shelf updateShelf(String shelfId, Boolean activeFlag, Errors errors);

	String deleteShelfEntry(String shelfId, String resourceId);

	Shelf moveShelfContent(String currectShelfId, String targetShelfId, String resourceGooruOId, User user) throws Exception;

	Shelf markDefaultShelf(String shelfName, Errors errors, User user) throws NotFoundException;

	List<String> getShelfNames(String fetchType, User user);

	Shelf renameMyShelf(String shelfId, String newShelfName, String gooruUid);

	List<ShelfItem> getShelfItems(String shelfId, String resourceType, String userId, String orderBy, String pageNum, String pageSize, String addedType, boolean skipPagination, String startAt);

	void archiveUserFirstVisit(String gooruUid);

	Shelf updateShelfStatus(String shelfId, boolean activeFlag);

	List<ShelfItem> getShelfSubscribeUserList(String gooruOid);

	void removeCollaboratorShelf(Set<ContentPermission> contentPermissions);

	void addCollaboratorShelf(ContentPermission contentPermission);

	boolean hasContentSubscribed(User user, String gooruOid);

	JSONArray getMyShelfCollections(String gooruUid, String pageNum, String pageSize) throws Exception;

	JSONArray getMyShelfResources(String gooruUid, String pageNum, String pageSize) throws Exception;

	List<Assessment> getMyShelfQuiz(String gooruUid, String pageNum, String pageSize) throws JSONException;

	void updateShelfItem(Long contentId, User user, String gooruOid);

	void setDefaultShelvesForNewUser(User user);

	void updateShelfFolderNameForNewUser(User user);

	void deleteShelfSubscribeUserList(String gooruOid, String gooruUid);
}
