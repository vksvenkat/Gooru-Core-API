/*******************************************************************************
 * CodeTypeTransformer.java
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


import org.ednovo.gooru.core.api.model.CodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import flexjson.transformer.ObjectTransformer;

public class CodeTypeTransformer extends ObjectTransformer {
	private boolean deepSerialize;
	private static XStream xStream = new XStream(new DomDriver());

	public CodeTypeTransformer(boolean deepSerialize) {
		this.deepSerialize = deepSerialize;
	}
	
		private static final Logger logger = LoggerFactory.getLogger(CodeTypeTransformer.class);
		
		@Override
		public void transform(Object object) {
			CodeType code = (CodeType) object;
			if (code!= null) {

				if (deepSerialize) {
					try {
						code = (CodeType) xStream.fromXML(xStream.toXML(code));
					} catch (Exception ex) {
						deepSerialize = false;
					}
				}
			if (code != null) {
				CodeTypeTransModel codeTypeModel = new CodeTypeTransModel();
			     codeTypeModel.setCode(codeTypeModel.getCode());
			     codeTypeModel.setLabel(codeTypeModel.getLabel());
			     codeTypeModel.setTypeId(codeTypeModel.getTypeId());
		         
		        
		    	getContext().transform(codeTypeModel);	
			}
                else {
				logger.error("Serialization failed for user group transformer");
				getContext().write(null);
			}
		}
	}


}
