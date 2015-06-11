package org.ednovo.gooru.controllers.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.Network;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.network.NetworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = { "/network" })
public class NetworkRestController extends BaseController {

	@Autowired
	private NetworkService networkService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_NETWORK_SEARCH })
	@RequestMapping(method = RequestMethod.GET, value = "/search")
	public ModelAndView searchNetwork(@RequestParam(value = TEXT, required = false) String text, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = PAGE_NO, required = false, defaultValue = ONE) Integer pageNo,
			@RequestParam(value = PAGE_SIZE, required = false, defaultValue = TEN) Integer pageSize, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, NETWORK_SEARCH);

		List<Network> networks = networkService.searchNetwork(text, pageNo, pageSize);

		return toModelAndView(networks, FORMAT_JSON);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_NETWORK_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView createNetwork(@RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = NAME) String name, @RequestParam(value = APPROVED_FLAG, defaultValue = FALSE) boolean approvedFlag, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setAttribute(PREDICATE, NETWORK_CREATE);

		User apiCaller = (User) request.getAttribute(Constants.USER);

		Network network = networkService.createNetwork(apiCaller, name, approvedFlag);

		return toModelAndView(network, FORMAT_JSON);
	}
}
