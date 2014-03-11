/*
*PartyRepository.java
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

package org.ednovo.gooru.infrastructure.persistence.hibernate.party;

import java.util.List;

import org.ednovo.gooru.core.api.model.Party;
import org.ednovo.gooru.core.api.model.PartyCustomField;
import org.ednovo.gooru.core.api.model.Profile;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;

public interface PartyRepository extends BaseRepository {

	Party findPartyById(String partyUid);

	List<PartyCustomField> getPartyCustomFields(String partyUid, String optionalKey, String category);
	
	PartyCustomField  getPartyCustomField(String partyUid, String optionalKey);
	
	Profile getUserDateOfBirth(String partyUid, User user);

	void deletePartyCustomField(String partyId, String optionalKey);
	
	Integer getCountInActiveMailSendToday();
	
	void updatePartyCustomFieldsInActiveMailKey(String userIds);
	
	void updatePartyCustomFieldsBirthDayMailKey(String userIds);
	
	boolean isUserBirthDayMailSentToday(String userId, String date);
}
