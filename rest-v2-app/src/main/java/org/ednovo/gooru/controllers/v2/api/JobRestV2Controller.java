package org.ednovo.gooru.controllers.v2.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.Job;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.job.JobService;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = { "/v2/job" })
public class JobRestV2Controller extends BaseController implements ConstantProperties, ParameterProperties {

	@Autowired
	private JobService jobService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_JOB_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}" }, method = RequestMethod.GET)
	public ModelAndView getJob(@PathVariable(value = ID) String jobUid, HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("hjfhgdhj");
		return toModelAndViewWithIoFilter(getJobService().getJob(jobUid), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, JOB_INCLUDES);
	}
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_JOB_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = { "/{id}" }, method = RequestMethod.PUT)
	public ModelAndView updateJob(@PathVariable(value = ID) String jobUid, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return toModelAndViewWithIoFilter(getJobService().updateJob(jobUid, buildJobFromInputParameters(data)), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, JOB_INCLUDES);
	}
	
	private Job buildJobFromInputParameters(String data) {
		return JsonDeserializer.deserialize(data, Job.class);
	}

	public JobService getJobService() {
		return jobService;
	}
	
}

    
