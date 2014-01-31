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