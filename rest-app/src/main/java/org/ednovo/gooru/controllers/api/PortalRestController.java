package org.ednovo.gooru.controllers.api;

import static com.rosaloves.bitlyj.Bitly.shorten;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.exception.MethodFailureException;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.ShareService;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.gooru.domain.service.user.UserService;
import org.ednovo.gooru.infrastructure.jira.SOAPClient;
import org.ednovo.gooru.infrastructure.jira.SOAPSession;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.rosaloves.bitlyj.Bitly;
import com.rosaloves.bitlyj.Url;

@Controller
@RequestMapping(value = { "/portal", "" })
public class PortalRestController extends BaseController {

	@Autowired
	private SOAPSession soapSession;

	@Autowired
	private SOAPClient soapClient;

	@Autowired
	private UserService userService;

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private RedisService redisService;

	@Autowired
	private SettingService settingService;

	@Autowired
	private ShareService shareService;
	
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_ISSUE_ADD })
	@RequestMapping(method = RequestMethod.POST, value = "/feedback/user/{userId}/issue.{format}")
	public ModelAndView createIssue(HttpServletRequest request, HttpServletResponse response, @PathVariable(USER_ID) String userId, @RequestParam(value = USERNAME) String userName, @RequestParam(value = DESCRIPTION) String description,
			@RequestParam(value = SUMMARY, required = false) String summary, @RequestParam(value = BROWSER_NAME) String browserName, @RequestParam(value = CURRENT_URL) String currentUrl, @RequestParam(value = EMAIL_ID, required = false) String reporterEmail,
			@RequestParam(value = BROWSER_VER) String browserVer) throws Exception {
		request.setAttribute(PREDICATE, PORTAL_CREATE_ISSUE);
		// String reporterEmail = "";
		if (!userId.contains(_ANONYMOUS)) {
			User user = userService.findByGooruId(userId);

			Iterator<Identity> iter = user.getIdentities().iterator();

			while (iter.hasNext()) {
				Identity idy = iter.next();
				reporterEmail = idy.getExternalId();
				break;
			}
		}

		if (browserName == null) {
			browserName = "";
		}

		if (browserVer == null) {
			browserVer = "";
		}

		if (reporterEmail == null) {
			reporterEmail = "";
		}
		String reportedBy = " ";
		String environment = browserName + "  " + browserVer;
		if (reporterEmail != null && (!reporterEmail.isEmpty())) {
			reportedBy = "\n Reported By : " + userName + " (" + reporterEmail + ")\n URL: " + currentUrl;
		}
		if (description.length() > 255) {
			String tempDescriptionFirstPart = description.substring(0, 250);
			String descriptionFirstPart = description.substring(0, tempDescriptionFirstPart.lastIndexOf(" ")) + "...";
			String descriptionSecondPart = description.substring(tempDescriptionFirstPart.lastIndexOf(" ") + 1);
			description = descriptionFirstPart;
			reportedBy += "\nSummary Continued... \n" + descriptionSecondPart;
		}

		Map<Integer, String> customFieldValueMap = new HashMap<Integer, String>();
		customFieldValueMap.put(1, reporterEmail);

		Map<String, String> standardJiraFields = new HashMap<String, String>();
		standardJiraFields.put(SUMMARY, summary);
		standardJiraFields.put(DESCRIPTION, description.concat(reportedBy));
		standardJiraFields.put(ENVIRONMENT, environment);
		standardJiraFields.put(REPORTER, reporterEmail);

		String issueKey = null;

		try {
			issueKey = this.getSoapClient().createIssue(this.getSoapSession(), null, standardJiraFields);
		} catch (Exception e) {
			throw new MethodFailureException("Error while JIRA connection", e);
		}

		ModelAndView mav = new ModelAndView(REST_MODEL);
		mav.addObject(MODEL, issueKey);

		return mav;
	}

	@Deprecated
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_URL_SHORTEN })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/url/shorten/{contentGooruOid}", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView createShortenUrl(@PathVariable(CONTENT_GOORU_OID) String contentGooruOid, @RequestParam(value = REAL_URL) String realUrl, @RequestParam(value = CLEAR_CACHE, required = false) boolean clearCache, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return toModelAndView(this.getShareService().getShortenUrl(realUrl, clearCache));	
	}

	public SOAPClient getSoapClient() {
		return soapClient;
	}

	public void setSoapClient(SOAPClient soapClient) {
		this.soapClient = soapClient;
	}

	public SOAPSession getSoapSession() {
		return soapSession;
	}

	public void setSoapSession(SOAPSession soapSession) {
		this.soapSession = soapSession;
	}

	public ResourceRepository getResourceRepository() {
		return resourceRepository;
	}

	public SettingService getSettingService() {
		return settingService;
	}

	public void setRedisService(RedisService redisService) {
		this.redisService = redisService;
	}

	public RedisService getRedisService() {
		return redisService;
	}

	public ShareService getShareService() {
		return shareService;
	}

}
