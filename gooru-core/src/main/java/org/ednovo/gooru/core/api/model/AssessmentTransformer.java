package org.ednovo.gooru.core.api.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import flexjson.transformer.ObjectTransformer;
public class AssessmentTransformer extends ObjectTransformer {

	private boolean deepSerialize;
	private static XStream xStream = new XStream(new DomDriver());

	public AssessmentTransformer(boolean deepSerialize) {
		this.deepSerialize = deepSerialize;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(AssessmentTransformer.class);
			
			@Override
			public void transform(Object object) {
				Assessment assess = (Assessment) object;
			//	String currentUserOrgUid = UserGroupSupport.getUserOrganizationUid();
				if (assess != null) {

					if (deepSerialize) {
						try {
							assess = (Assessment) xStream.fromXML(xStream.toXML(assess));
						} catch (Exception ex) {
							deepSerialize = false;
						}
					}
				if (assess != null) {
					AssessmentTransModel assessModel = new AssessmentTransModel();
				    assessModel.setName(assess.getName()); 
			        assessModel.setGrade(assess.getGrade());
					assessModel.setSource(assess.getSource());
				    assessModel.setTaxonomyContentData(assess.getTaxonomyContentData());
			        
			    	if (deepSerialize) {
			        assessModel.setTimeToCompleteInSecs(assess.getTimeToCompleteInSecs());
					}

					getContext().transform(assessModel);

				} else {
					logger.error("Serialization failed for user group transformer");
					getContext().write(null);
				}
			}
			}
}
