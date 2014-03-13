/////////////////////////////////////////////////////////////
// PreferredSearchCassandraFactory.java
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
/**
 * 
 */
package org.ednovo.gooru.domain.cassandra;

import javax.annotation.PostConstruct;

import org.ednovo.gooru.cassandra.core.dao.CassandraColumnFamily;
import org.ednovo.gooru.cassandra.core.dao.RawCassandraDaoImpl;
import org.ednovo.gooru.cassandra.core.factory.InsightsCassandraFactory;
import org.ednovo.gooru.cassandra.core.service.CassandraSettingService;
import org.ednovo.gooru.core.constant.ColumnFamilyConstant;
import org.ednovo.gooru.infrastructure.persistence.hibernate.ConfigSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author SearchTeam
 * 
 */
@Component
public class PreferredSearchCassandraFactory extends InsightsCassandraFactory {

	@Autowired
	private ConfigSettingRepository configSettingRepository;

	@PostConstruct
	public final void initialize() {
		super.initialize();
		register(new CassandraColumnFamily(ColumnFamilyConstant.USER_PREFERENCE));
		register(new RawCassandraDaoImpl(this, ColumnFamilyConstant.USER_PREFERENCE));
		register(new CassandraColumnFamily(ColumnFamilyConstant.USER_PROFICIENCY));
		register(new RawCassandraDaoImpl(this, ColumnFamilyConstant.USER_PROFICIENCY));
		register(new CassandraColumnFamily(ColumnFamilyConstant.USER_SUBJECT_PROFICIENCY));
		register(new RawCassandraDaoImpl(this, ColumnFamilyConstant.USER_SUBJECT_PROFICIENCY));
		register(new CassandraColumnFamily(ColumnFamilyConstant.USER_COURSE_PROFICIENCY));
		register(new RawCassandraDaoImpl(this, ColumnFamilyConstant.USER_COURSE_PROFICIENCY));
		register(new CassandraColumnFamily(ColumnFamilyConstant.USER_UNIT_PROFICIENCY));
		register(new RawCassandraDaoImpl(this, ColumnFamilyConstant.USER_UNIT_PROFICIENCY));
		register(new CassandraColumnFamily(ColumnFamilyConstant.USER_TOPIC_PROFICIENCY));
		register(new RawCassandraDaoImpl(this, ColumnFamilyConstant.USER_TOPIC_PROFICIENCY));
		register(new CassandraColumnFamily(ColumnFamilyConstant.USER_LESSON_PROFICIENCY));
		register(new RawCassandraDaoImpl(this, ColumnFamilyConstant.USER_LESSON_PROFICIENCY));
		register(new CassandraColumnFamily(ColumnFamilyConstant.USER_CONCEPT_PROFICIENCY));
		register(new RawCassandraDaoImpl(this, ColumnFamilyConstant.USER_CONCEPT_PROFICIENCY));
	}

	@Override
	public CassandraSettingService getSettingService() {
		return configSettingRepository;
	}

}
