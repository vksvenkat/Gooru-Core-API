/*
*ResponseFieldSetServiceImpl.java
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

package org.ednovo.gooru.domain.service.responseFieldSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.core.api.model.ResponseFieldSet;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserRole.UserRoleType;
import org.ednovo.gooru.core.api.model.UserRoleAssoc;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.responseFieldSet.ResponseFieldSetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("responseFieldSetService")
public class ResponseFieldSetServiceImpl implements ResponseFieldSetService,ParameterProperties,ConstantProperties {
	
	 public static final String INCLUDE = "include";
		
	 public static final String EXCLUDE = "exclude";
	 
	@Autowired
	private ResponseFieldSetRepository responseFieldSetRepository;
	
	@Autowired
    private BaseRepository baseRepository;	
	
	@Override
	public ResponseFieldSet addResponseFieldSet(String fieldsName,User user) throws Exception {
	  if(isContentAdmin(user))
	  {
		ResponseFieldSet responseField = new ResponseFieldSet();
		responseField.setFieldSet(fieldsName);
		responseField.setGooruUId(user.getGooruUId());
		responseField.setOrganization(user.getOrganization());
		responseFieldSetRepository.save(responseField);
		return responseField;
	  }
	  else
	  {
		  throw new Exception("Access Denied");
	  }
	 
	}
    
	private Boolean isContentAdmin(User user) {
		Boolean isAdminUser = false;
		if (user.getUserRoleSet() != null) {
			for (UserRoleAssoc userRoleAssoc : user.getUserRoleSet()) {
				if (userRoleAssoc.getRole().getName().equalsIgnoreCase(UserRoleType.CONTENT_ADMIN.getType())) {
					isAdminUser = true;
					break;
				}
			}
		} 
		return isAdminUser;
	}

	@Override
	public ResponseFieldSet updateResponseFieldSet(String fieldId,String fieldsName, User apiCaller) throws Exception {
		
		 ResponseFieldSet responseField = null;
		 if(isContentAdmin(apiCaller))
		  {
			 responseField = responseFieldSetRepository.getResponseFieldSet(fieldId,apiCaller.getGooruUId());
			 if(responseField != null){
				 responseField.setFieldSet(fieldsName);
				 responseFieldSetRepository.save(responseField);
				 return responseField;
			 }
			 else
			 {
				 throw new Exception("The fieldSetId is not null");
			 }
		  }
		 else
		 {
			 throw new Exception("Access Denied"); 
		 }
}

	@Override
	public String deleteResponseFieldSet(String fieldId, User apiCaller) throws Exception {
		ResponseFieldSet responseField = null;
		
		if(fieldId != null)
		{
			if(isContentAdmin(apiCaller))
			{
				responseField = responseFieldSetRepository.getResponseFieldSet(fieldId,apiCaller.getGooruUId());
 			
				if(responseField !=null)
				{
					baseRepository.remove(ResponseFieldSet.class,fieldId);
					return "Deleted successfully";
				}
				else
				{
					throw new Exception("The fieldSetId is not null");
				}
			}
			else
			{
				throw new Exception("Access Denied");
			}
		}
		else
		{
			throw new Exception("FieldSetId should not be empty");
		}
	}
	@Override
	public ResponseFieldSet getResponseFieldSet(String fieldId, String gooruUId)
	{
		return responseFieldSetRepository.getResponseFieldSet(fieldId,gooruUId);
	}

	@Override
	public List<ResponseFieldSet> getResponseFieldSet() {
		return responseFieldSetRepository.getResponseFieldSet();
	}
	
	@Override
	public String[] getFieldsByFieldId(String fieldId, String gooruUId){
		ResponseFieldSet responseField = getResponseFieldSet(fieldId, gooruUId);
		if(responseField != null){
			return responseField.getFieldSet().split(",");
		}
		return null;
	}
	
	@Override
	public Map<String,Object> buildIncludeExcludeString(String[] includes, String[] excludes, String responseFieldSetId, String goouUId, String responseFieldSet){
		
		Map<String,Object> fieldSetMap = new HashMap<String, Object>();
		
		fieldSetMap.put(INCLUDE, includes);
		fieldSetMap.put(EXCLUDE, excludes);
		
		if(responseFieldSetId != null){
			String[] fields = getFieldsByFieldId(responseFieldSetId, goouUId);
			if(responseFieldSet != null){
				if(responseFieldSet.equals(EXCLUDE)){
					excludes = (String[]) ArrayUtils.addAll(fields, excludes);
					fieldSetMap.put(EXCLUDE, excludes);
				}
				else if(responseFieldSet.equals(INCLUDE)){
					includes = (String[]) ArrayUtils.addAll(fields, includes);
					fieldSetMap.put(INCLUDE, fields);
					fieldSetMap.put(EXCLUDE, new String[] {"*"});
				}
			}
		}
		return fieldSetMap;
	}

}
