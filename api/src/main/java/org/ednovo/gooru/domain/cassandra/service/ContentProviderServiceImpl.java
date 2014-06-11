package org.ednovo.gooru.domain.cassandra.service;

import org.ednovo.gooru.core.api.model.ContentProvider;
import org.ednovo.gooru.core.cassandra.model.ContentProviderCio;
import org.ednovo.gooru.core.constant.ColumnFamilyConstant;
import org.ednovo.gooru.infrastructure.persistence.hibernate.index.ContentIndexDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContentProviderServiceImpl extends ApiCrudEntityCassandraServiceImpl<ContentProvider, ContentProviderCio> implements ContentProviderService {
	@Autowired
	private ContentIndexDao contentIndexDao;

	@Override
	protected ContentProvider fetchSource(String key) {
		return  contentIndexDao.getContentProviderlist(key);
	}	

	@Override
	protected String getDaoName() {
		return ColumnFamilyConstant.CONTENT_PROVIDER;
	}
	

}
