/////////////////////////////////////////////////////////////
// JobRepositoryHibernate.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.util.List;

import org.ednovo.gooru.core.api.model.Job;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class JobRepositoryHibernate extends BaseRepositoryHibernate implements JobRepository {

	@Autowired
	public JobRepositoryHibernate(SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}

	@Override
	public int getAverageRetryTime(long fileSize) {
		String sql = "SELECT AVG(time_to_complete) as average_time FROM job WHERE job.time_to_complete IS NOT NULL AND job.file_size  ";
		if (fileSize > 100000) {
			sql += "BETWEEN  " + (fileSize - 100000) + " AND " + (fileSize + 100000);
		} else {
			sql += "BETWEEN  " + 0 + " AND " + fileSize;
		}
		sql += " AND job.status = 'Completed' LIMIT 100 ";

		Session session = getSession();
		Query query = session.createSQLQuery(sql).addScalar("average_time", StandardBasicTypes.INTEGER);
		List<Integer> results = list(query);

		return (results.size() > 0) ? results.get(0) : 0;
	}

	@Override
	public Job createJob(String jobUid) {
		Query query = getSession().createQuery("FROM Job job  WHERE job.jobUid=:jobUid").setParameter("jobUid", jobUid);
		return (Job) (query.list().size() > 0 ? query.list().get(0) : null);

	}

	@Override
	public Job getJob(String jobUid) {
		Query query = getSession().createQuery("FROM Job job  WHERE job.jobUid=:jobUid").setParameter("jobUid", jobUid);
		return (Job) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	
}
