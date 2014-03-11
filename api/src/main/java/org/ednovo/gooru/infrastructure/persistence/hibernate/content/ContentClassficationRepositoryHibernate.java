/*
*ContentClassficationRepositoryHibernate.java
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

package org.ednovo.gooru.infrastructure.persistence.hibernate.content;

import java.util.List;

import org.ednovo.gooru.core.api.model.ContentClassification;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ContentClassficationRepositoryHibernate extends BaseRepositoryHibernate implements ContentClassificationRepository {

	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public ContentClassification findByContent(Long contentId) {
		List<ContentClassification> cc = getSession().createQuery("select c from ContentClassification c   where c.content.contentId = ? and " + generateAuthQueryWithDataNew("c.content.")).setLong(0, contentId).list();
		return cc.size() == 0 ? null : cc.get(0);
	}

	public ContentClassification findByContentGooruId(String gooruContentId) {
		List<ContentClassification> cc = getSession().createQuery("select c from ContentClassification c  where c.content.gooruOid = ? and " + generateAuthQueryWithDataNew("c.content.")).setString(0, gooruContentId).list();
		return cc.size() == 0 ? null : cc.get(0);
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
