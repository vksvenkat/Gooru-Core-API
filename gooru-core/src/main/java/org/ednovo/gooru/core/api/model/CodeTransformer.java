package org.ednovo.gooru.core.api.model;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import flexjson.transformer.ObjectTransformer;

public class CodeTransformer extends ObjectTransformer {
	private boolean deepSerialize;
	private static XStream xStream = new XStream(new DomDriver());

	public CodeTransformer(boolean deepSerialize) {
		this.deepSerialize = deepSerialize;
	}
	
		private static final Logger LOGGER = LoggerFactory.getLogger(CodeTransformer.class);
		
		@Override
		public void transform(Object object) {
			Code code = (Code) object;
			if (code!= null) {

				if (deepSerialize) {
					try {
						code = (Code) xStream.fromXML(xStream.toXML(code));
					} catch (Exception ex) {
						deepSerialize = false;
					}
				}
			if (code != null) {
				CodeTransModel codeModel = new CodeTransModel();
			     codeModel.setCode(code.getCode()); 
		         codeModel.setCodeId(code.getCodeId());
				 codeModel.setCodeType(code.getCodeType());
			     codeModel.setLabel(code.getLabel());
		        
		    	getContext().transform(codeModel);	
			}
                else {
				LOGGER.error("Serialization failed for user group transformer");
				getContext().write(null);
			}
		}
	}


}