/////////////////////////////////////////////////////////////
//GooruExceptionResolver.java
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
package org.ednovo.gooru.web.spring.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.lang.NotImplementedException;
import org.ednovo.gooru.core.exception.BadRequestException;
import org.ednovo.gooru.core.exception.ClassplanException;
import org.ednovo.gooru.core.exception.NotAllowedException;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.core.exception.UnauthorizedException;
import org.jets3t.service.S3ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import flexjson.JSONSerializer;
//import org.ednovo.gooru.search.es.exception.SearchException;

public class GooruExceptionResolver extends SimpleMappingExceptionResolver {

	private final Logger logger = LoggerFactory.getLogger(GooruExceptionResolver.class);

	@Override
	public ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		ErrorObject errorObject = null;
		boolean isLogError = false;
		if (ex instanceof AccessDeniedException) {
			errorObject = new ErrorObject(403, ex.getMessage());
			response.setStatus(403);
		} else if(ex instanceof BadCredentialsException || ex instanceof BadRequestException) { 
			errorObject = new ErrorObject(400, ex.getMessage());
			response.setStatus(400);
		}  else if (ex instanceof UnauthorizedException) {  
			errorObject = new ErrorObject(401, ex.getMessage());
			response.setStatus(401);
		}
		else if (ex instanceof SizeLimitExceededException) {
			response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
			errorObject = new ErrorObject(413, ex.getMessage());
		} else if (ex instanceof ClassplanException) {
			response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
			errorObject = new ErrorObject(404, ex.getMessage());
		} else if (ex instanceof S3ServiceException) {
			response.setStatus(500);
			errorObject = new ErrorObject(500, "Internal Server Error");
			logger.info("Error in Resolver -- " + ((S3ServiceException) ex).getErrorMessage());
		} else if (ex instanceof NotFoundException) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			isLogError = true;
			errorObject = new ErrorObject(404, ex.getMessage());
		} else if (ex instanceof NotImplementedException || ex instanceof NotAllowedException) {
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			errorObject = new ErrorObject(405, ex.getMessage());
		}/* else if (ex instanceof SearchException) {
			response.setStatus(((SearchException) ex).getStatus().value());
			errorObject = new ErrorObject(((SearchException) ex).getStatus().value(), ex.getMessage());
		} */else {
			errorObject = new ErrorObject(500, "Internal Server Error");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.info("Error in Resolver -- " + ex);
		}
		if(!isLogError)
		{
			logger.debug("Error in Resolver -- ", ex);
		}
		ModelAndView jsonModel = new ModelAndView("rest/model");
		jsonModel.addObject("model", new JSONSerializer().exclude("*.class").serialize(errorObject));
		return jsonModel;
	}

}
