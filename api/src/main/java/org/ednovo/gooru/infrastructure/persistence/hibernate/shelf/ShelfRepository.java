/*
*ShelfRepository.java
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

package org.ednovo.gooru.infrastructure.persistence.hibernate.shelf;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.Shelf;
import org.ednovo.gooru.core.api.model.ShelfItem;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;


public interface ShelfRepository extends BaseRepository {

	Shelf findShelfByShelfId(String shelfId);

	List<Shelf> findAllShelfByUser(String gooruUId);

	List<ShelfItem> findAllShelfByUserAndId(String userPartyUid, Long contentId);

	Shelf findShelfByName(String name, String gooruUId);

	List<Shelf> findSubShelfByShelfId(String shelfId, String gooruUId);

	Shelf updateShelfItem(String sourceShelfId, String targetShelfId, Long contentId);

	int deleteShelfEntry(String shelfId, Long contentId);

	List<Shelf> listShelf(Map<String, String> parameters);

	Shelf getDefaultShelf(String gooruUId);

	List<String> getShelfNames(String gooruUId, List<String> suggest);

	Shelf findShelfByNameExcludeById(String name, String shelfId, String gooruUid);

	List<ShelfItem> listShelfItem(Map<String, String> parameters);

	List<ShelfItem> getShelfSubscribeUserList(String gooruOid);

	List<ShelfItem> getShelfContentByUser(String gooruUid, String gooruOid);

	void updateShelfFolderNameForNewUser(User user);
}
