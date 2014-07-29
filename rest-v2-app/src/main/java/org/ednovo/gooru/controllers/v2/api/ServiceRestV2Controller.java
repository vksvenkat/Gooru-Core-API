package org.ednovo.gooru.controllers.v2.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.domain.service.VersionService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping(value = { "v2/version"})
public class ServiceRestV2Controller extends BaseController{
	
	@Autowired	
	private VersionService versionService;
	
	@PreAuthorize("permitAll")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ModelAndView getClasspage( HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		JSONObject jsonObj = new JSONObject();		
		jsonmodel.addObject(MODEL, jsonObj.put(DEFAULT_VERSION, getVersionService().getDefaultVersion()));
		return jsonmodel;

	}
	
	public VersionService getVersionService() {
		return  versionService;
	}

}