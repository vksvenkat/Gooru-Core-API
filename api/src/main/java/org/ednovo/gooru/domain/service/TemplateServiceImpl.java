/*
*TemplateServiceImpl.java
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

package org.ednovo.gooru.domain.service;

import java.util.Date;
import java.util.List;

import org.ednovo.gooru.core.api.model.Template;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplateServiceImpl extends BaseServiceImpl implements TemplateService, ParameterProperties, ConstantProperties {

	@Autowired
	private TemplateRepository templateRepository;

	@Override
	public Template createTemplate(Template template, User user) {
		rejectIfNull(template.getHtmlContent(), GL0006, TEMPLATE_HTML);
		rejectIfNull(template.getTextContent(), GL0006, TEMPLATE_TEXT);
		rejectIfNull(template.getSubject(), GL0006, TEMPLATE_SUBJECT );
		template.setCreator(user);
		template.setOrganization(user.getOrganization());
		template.setCreatedDate(new Date(System.currentTimeMillis()));
		this.getTemplateRepository().save(template);
		return template;
	}

	@Override
	public Template updateTemplate(String id, Template newTemplate) {
		Template template = this.getTemplateRepository().getTemplate(id);
		rejectIfNull(template, GL0006, TEMPLATE);
		if (newTemplate.getHtmlContent() != null) {
			template.setHtmlContent(newTemplate.getHtmlContent());
		}
		if (newTemplate.getTextContent() != null) {
			template.setTextContent(newTemplate.getTextContent());
		}
		if (newTemplate.getSubject() != null) {
			template.setSubject(newTemplate.getSubject());
		}

		this.getTemplateRepository().save(template);

		return template;
	}

	@Override
	public Template getTemplate(String id) {
		return this.getTemplateRepository().getTemplate(id);
	}

	@Override
	public void deleteTemplate(String id) {
		Template template = this.getTemplateRepository().getTemplate(id);
		rejectIfNull(template, GL0056,TEMPLATE);
		this.getTemplateRepository().remove(template);

	}

	@Override
	public List<Template> getTemplates() {
		return this.getTemplateRepository().getTemplates();
	}

	public TemplateRepository getTemplateRepository() {
		return templateRepository;
	}
}
