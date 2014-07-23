/////////////////////////////////////////////////////////////
//MediaRestV2Controller.java
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

import java.io.File;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.application.util.GooruImageUtil;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.MediaDTO;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.application.util.RequestUtil;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.resource.MediaService;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = { "/v2/media" })
public class MediaRestV2Controller extends BaseController implements ConstantProperties, ParameterProperties {

	private static final Logger LOGGER = LoggerFactory.getLogger(MediaRestV2Controller.class);
	@Autowired
	@javax.annotation.Resource(name = "classplanConstants")
	private Properties classPlanConstants;

	@Autowired
	private MediaService mediaService;

	@Autowired
	private GooruImageUtil gooruImageUtil;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_MEDIA_HTMLTOPDF })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.POST }, value = "/htmltopdf")
	public ModelAndView createHtmlToPdf(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String res = getMediaService().convertHtmltoPdf(requestData(data));
		if (res == null) {
			res = "Failed to generate the pdf file!";
			response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
		}
		return toModelAndView(res);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_MEDIA_HTMLTOPDF })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.GET }, value = "/download")
	public void createHtmlToPdfAndDownload(@RequestParam String url, @RequestParam String filename, HttpServletRequest request, HttpServletResponse response) throws Exception {
		getMediaService().downloadFile(response, filename, url);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_MEDIA_HTMLTOPDF })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = { RequestMethod.POST }, value = "/jsontostring")
	public ModelAndView jsontocsv(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String res = getMediaService().convertJsonToCsv(requestData(data));
		if (res == null) {
			res = "Failed to generate the csv file!";
			response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
		}
		return toModelAndView(res);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_MEDIA_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView uploadMedia(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> formField = RequestUtil.getMultipartItems(request);
		MediaDTO mediaDTO = formField.get(DATA_OBJECT) != null ? this.buildMediaInput(formField.get(DATA_OBJECT).toString()) : new MediaDTO();
		return toModelAndView(getMediaService().handleFileUpload(mediaDTO, formField), FORMAT_JSON);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_MEDIA_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}/crop")
	public void cropImage(@PathVariable(value = GOORU_IMAGE_ID) String gooruImageId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject json = requestData(data);
		File classplanDir = new File(UserGroupSupport.getUserOrganizationNfsInternalPath() + Constants.UPLOADED_MEDIA_FOLDER);

		String fileName = gooruImageId;

		File file = new File(classplanDir.getPath() + "/" + fileName);

		if (fileName != null && file.exists()) {

			try {
				if (getValue(CROP_ENGINE, json).equalsIgnoreCase("imageMagick")) {
					getGooruImageUtil().cropImageUsingImageMagick(file.getPath(), Integer.parseInt(getValue(WIDTH, json)), Integer.parseInt(getValue(HEIGHT, json)), Integer.parseInt(getValue(XPOSITION, json)), Integer.parseInt(getValue(YPOSITION, json)), file.getPath());
				} else {
					getGooruImageUtil().cropImage(file.getPath(), Integer.parseInt(getValue(XPOSITION, json)), Integer.parseInt(getValue(YPOSITION, json)), Integer.parseInt(getValue(WIDTH, json)), Integer.parseInt(getValue(HEIGHT, json)));
				}
			} catch (Exception exception) {
				LOGGER.error("Cannot crop Image : " + exception.getMessage());
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}

		} else {
			response.setStatus(404);
		}
	}

	public MediaService getMediaService() {
		return mediaService;
	}

	public GooruImageUtil getGooruImageUtil() {
		return gooruImageUtil;
	}

	public Properties getClassPlanConstants() {
		return classPlanConstants;
	}

	private MediaDTO buildMediaInput(String data) {
		return JsonDeserializer.deserialize(data, MediaDTO.class);
	}
}
