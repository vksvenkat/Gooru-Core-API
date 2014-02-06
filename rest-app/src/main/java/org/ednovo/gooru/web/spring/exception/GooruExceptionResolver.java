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
//import org.ednovo.gooru.search.es.exception.SearchException;
import org.jets3t.service.S3ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import flexjson.JSONSerializer;

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
		} /*else if (ex instanceof SearchException) {
			response.setStatus(((SearchException) ex).getStatus().value());
			errorObject = new ErrorObject(((SearchException) ex).getStatus().value(), ex.getMessage());
		}*/ else {
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