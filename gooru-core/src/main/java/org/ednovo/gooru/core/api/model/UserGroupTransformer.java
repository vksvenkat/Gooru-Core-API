/*******************************************************************************
 * UserGroupTransformer.java
 *  gooru-core
 *  Created by Gooru on 2014
 *  Copyright (c) 2014 Gooru. All rights reserved.
 *  http://www.goorulearning.org/
 *       
 *  Permission is hereby granted, free of charge, to any 
 *  person obtaining a copy of this software and associated 
 *  documentation. Any one can use this software without any 
 *  restriction and can use without any limitation rights 
 *  like copy,modify,merge,publish,distribute,sub-license or 
 *  sell copies of the software.
 *  
 *  The seller can sell based on the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be   
 *  included in all copies or substantial portions of the Software. 
 * 
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
 *   KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
 *   WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
 *   PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE       AUTHORS 
 *   OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
 *   OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *   OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *   WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 *   THE SOFTWARE.
 ******************************************************************************/
package org.ednovo.gooru.core.api.model;

import org.ednovo.gooru.core.api.model.UserGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import flexjson.transformer.ObjectTransformer;

public class UserGroupTransformer extends ObjectTransformer {

	private static final Logger logger = LoggerFactory.getLogger(UserGroupTransformer.class);
	
	@Override
	public void transform(Object object) {
		UserGroup sg = (UserGroup) object;
		if (sg != null) {

			UserGroupTransModel sgModel = new UserGroupTransModel();
			sgModel.setName(sg.getPartyName());
			sgModel.setPartyUid(sg.getPartyUid());
			sgModel.setGroupUid(sg.getPartyUid());
			sgModel.setGroupName(sg.getGroupName());
			sgModel.setGroupCode(sg.getGroupCode());
			sgModel.setOrganization(sg.getOrganization());
			sgModel.setUserUid(sg.getUserUid());
						
			getContext().transform(sgModel);

		} else {
			logger.error("Serialization failed for user group transformer");
			getContext().write(null);
		}
	}
}
