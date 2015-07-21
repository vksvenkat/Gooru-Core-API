/////////////////////////////////////////////////////////////
// TemplateServiceImpl.java
// gooru-api
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TemplateServiceImpl extends BaseServiceImpl implements TemplateService, ParameterProperties, ConstantProperties {

	@Autowired
	private TemplateRepository templateRepository;

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Template createTemplate(final Template template, final User user) {
		rejectIfNull(template.getHtmlContent(), GL0006, TEMPLATE_HTML);
		rejectIfNull(template.getTextContent(), GL0006, TEMPLATE_TEXT);
		rejectIfNull(template.getSubject(), GL0006, TEMPLATE_SUBJECT );
		rejectIfNull(template.getTemplateContent(),GL0006, TEMPLATE_CONTENT);
		template.setCreator(user);
		template.setOrganization(user.getOrganization());
		template.setCreatedDate(new Date(System.currentTimeMillis()));
		this.getTemplateRepository().save(template);
		return template;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Template updateTemplate(String id, final Template newTemplate) {
		final Template template = this.getTemplateRepository().getTemplate(id);
		rejectIfNull(template, GL0006, TEMPLATE);
		if (newTemplate.getHtmlContent() != null) {
			template.setHtmlContent(newTemplate.getHtmlContent());
		}
		if (newTemplate.getTextContent() != null) {
			template.setTextContent(newTemplate.getTextContent());
		}
		if (newTemplate.getTemplateContent() != null) {
			template.setTextContent(newTemplate.getTemplateContent());
		}
		if (newTemplate.getSubject() != null) {
			template.setSubject(newTemplate.getSubject());
		}

		this.getTemplateRepository().save(template);

		return template;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Template getTemplate(String id) {
		return this.getTemplateRepository().getTemplate(id);
	}

	@Override
	public void deleteTemplate(String id) {
		final Template template = this.getTemplateRepository().getTemplate(id);
		rejectIfNull(template, GL0056,TEMPLATE);
		this.getTemplateRepository().remove(template);

	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<Template> getTemplates() {
		return this.getTemplateRepository().getTemplates();
	}

	public TemplateRepository getTemplateRepository() {
		return templateRepository;
	}
}
