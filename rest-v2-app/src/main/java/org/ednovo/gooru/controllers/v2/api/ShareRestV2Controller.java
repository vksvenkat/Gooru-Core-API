/////////////////////////////////////////////////////////////
//ShareRestV2Controller.java
//rest-v2-app
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person      obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so,  subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY  KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE    WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR  PURPOSE     AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR  COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.controllers.v2.api;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.ShareService;
import org.ednovo.gooru.infrastructure.mail.MailHandler;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.type.TypeReference;

@Controller
@RequestMapping(value = { "/v2/share" })
public class ShareRestV2Controller extends BaseController implements ConstantProperties, ParameterProperties {

	@Autowired
	private MailHandler mailHandler;

	@Autowired
	private ShareService shareService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SHARE_MAIL })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/mail")
	public void shareMail(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject json = null;
		if (data != null && !data.isEmpty()) {
			json = requestData(data);
		}
		User user = (User) request.getAttribute(Constants.USER);
		List<Map<String, String>> attachment = null;
		if (getValue(ATTACHMENT, json) != null) {
			attachment = JsonDeserializer.deserialize(getValue(ATTACHMENT, json), new TypeReference<List<Map<String, String>>>() {
			});
		}
		mailHandler.shareMailForContent(json != null ? getValue(TO_ADDRESS, json) : null, json != null ? getValue(FROM_DISPLAY_NAME, json) : null, user.getPartyUid() != null ? user.getPartyUid() : null, json != null ? getValue(SUBJECT, json) : null, json != null ? getValue(MESSAGE, json) : null,
				json != null ? getValue(CFM, json) : NO, attachment, json != null ? getValue(FROM_DISPLAY_NAME, json) : user.getUsername());

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_URL_SHORTEN })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/url/shorten/{contentOid}", method = { RequestMethod.POST })
	public ModelAndView createContentShortenUrl(@PathVariable(CONTENT_OID) String contentGooruOid, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject json = requestData(data);
		return toModelAndView(this.getShareService().getShortenUrl(contentGooruOid, getValue(FULL_URL, json), Boolean.parseBoolean(getValue(CLEAR_CACHE, json))));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_URL_SHORTEN })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/url/shorten", method = { RequestMethod.POST })
	public ModelAndView createShortenUrl(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject json = requestData(data);
		User user = (User) request.getAttribute(Constants.USER);
		return toModelAndView(this.getShareService().getShortenUrl(getValue(FULL_URL, json), Boolean.parseBoolean(getValue(CLEAR_CACHE, json)), user));
	}

	public ShareService getShareService() {
		return shareService;
	}

}
