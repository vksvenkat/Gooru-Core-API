/////////////////////////////////////////////////////////////
// JobServiceImpl.java
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
package org.ednovo.gooru.domain.service.job;

import org.ednovo.gooru.core.api.model.Country;
import org.ednovo.gooru.core.api.model.Job;
import org.ednovo.gooru.core.api.model.JobType;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.BaseServiceImpl;
import org.ednovo.gooru.infrastructure.persistence.hibernate.JobRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.JobRepositoryHibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("jobService")
public class JobServiceImpl extends BaseServiceImpl implements JobService, ParameterProperties, ConstantProperties {

	@Autowired
	private JobRepositoryHibernate jobRepositoryHibernate;

	@Autowired
	private JobRepository jobRepository;

	@Override
	public int getAverageRetryTime(long fileSize) {
		return jobRepositoryHibernate.getAverageRetryTime(fileSize);
	}

	@Override
	public Job createJob(Resource resource) {
		Job job = new Job();
		job.setGooruOid(resource.getGooruOid());
		job.setUser(resource.getUser());
		job.setStatus(Job.Status.INPROGRESS.getStatus());
		job.setJobType((JobType) getJobRepository().get(JobType.class, JobType.Type.PDFCONVERSION.getType()));
		job.setOrganization(resource.getOrganization());
		this.getJobRepository().save(job);
		return job;
	}

	public JobRepository getJobRepository() {
		return jobRepository;
	}

	@Override
	public Job getJob(String jobUid) {
		Job job = this.getJobRepository().getJob(jobUid);
		rejectIfNull(job, GL0056, 404, "Job");
		return job;
	}

	@Override
	public Job updateJob(String jobUid,Job newJob) {
		Job job = this.getJobRepository().getJob(jobUid);
		rejectIfNull(newJob, GL0056, 404, "Job");
		if (newJob.getStatus()!= null) {
			job.setStatus(newJob.getStatus());
		}
		this.getJobRepository().save(job);
		return job;
	
	}

}
