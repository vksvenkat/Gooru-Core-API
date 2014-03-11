/*******************************************************************************
 * ServerValidationUtils.java
 *  gooru-core
 *  Created by Gooru on 2014
 *  Copyright (c) 2014 Gooru. All rights reserved.
 *  http://www.goorulearning.org/
 *       
 *  Permission is hereby granted, free of charge, to any 
 *  person obtaining a copy of this software and associated 
 *  documentation. Any one can use this software without any 
 *  restriction and can use without any limitation rights 
 *  like copy,modify,merge,publish,distribute,sub-license or 
 *  sell copies of the software.
 *  
 *  The seller can sell based on the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be   
 *  included in all copies or substantial portions of the Software. 
 * 
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
 *   KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
 *   WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
 *   PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE       AUTHORS 
 *   OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
 *   OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *   OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *   WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 *   THE SOFTWARE.
 ******************************************************************************/
package org.ednovo.gooru.core.application.util;

import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.ednovo.gooru.core.exception.BadRequestException;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.springframework.validation.Errors;

public class ServerValidationUtils {

	private static ResourceBundle message = ResourceBundle.getBundle("properties/message");

	public static void rejectIfNullOrEmpty(Errors errors, String data, String field, String errorMsg) {
		if (data == null || data.equals("")) {
			errors.rejectValue(field, errorMsg);
		}
	}
	public static void rejectIfNull(Object data, String code, String... message) {
		if (data == null) {
			throw new BadRequestException(generateErrorMessage(code, message));
		}
	}
	
	public static void rejectIfNull(Object data, String code, int errorCode, String... message) {
		if (data == null) {
			if (errorCode == 404) {
				throw new NotFoundException(generateErrorMessage(code, message));
			} else { 
				throw new BadRequestException(generateErrorMessage(code, message));
			}
		}
	}

	public static void rejectIfNull(Errors errors, Object data, String field, String errorMsg) {
		if (data == null) {
			errors.rejectValue(field, errorMsg);
		}
	}

	public static void rejectIfNull(Errors errors, Object data, String field, String errorCode, String errorMsg) {
		if (data == null) {
			errors.rejectValue(field, errorCode, errorMsg);
		}
	}

	public static void rejectIfAlReadyExist(Errors errors, Object data, String errorCode, String errorMsg) {
		if (data != null) {
			errors.reject(errorCode, errorMsg);
		}
	}

	public static void rejectIfNullOrEmpty(Errors errors, Set<?> data, String field, String errorMsg) {
		if (data == null || data.size() == 0) {
			errors.rejectValue(field, errorMsg);
		}
	}

	public static void rejectIfNullOrEmpty(Errors errors, String data, String field, String errorCode, String errorMsg) {
		if (data == null || data.equals("")) {
			errors.rejectValue(field, errorCode, errorMsg);
		}
	}

	public static void rejectIfInvalidType(Errors errors, String data, String field, String errorCode, String errorMsg, Map<String, String> typeParam) {
		if (!typeParam.containsKey(data)) {
			errors.rejectValue(field, errorCode, errorMsg);
		}
	}

	public static void rejectIfInvalidDate(Errors errors, Date data, String field, String errorCode, String errorMsg) {
		Date date = new Date();
		if (data.compareTo(date) <= 0) {
			errors.rejectValue(field, errorCode, errorMsg);
		}
	}

	public static void rejectIfInvalid(Errors errors, Double data, String field, String errorCode, String errorMsg, Map<Double, String> ratingScore) {
		if (!ratingScore.containsKey(data)) {
			errors.rejectValue(field, errorCode, errorMsg);
		}
	}

	public static void rejectIfNotValid(Errors errors, Integer data, String field, String errorCode, String errorMsg, Integer maxValue) {
		if (data <= 0 || data > maxValue) {
			errors.rejectValue(field, errorCode, errorMsg);
		}
	}

	public static String generateErrorMessage(String errorCode) {
		return message.getString(errorCode);
	}

	public static String generateErrorMessage(String errorCode, String... params) {
		String errorMsg = message.getString(errorCode);
		if (params != null) {
			for (int index = 0; index < params.length; index++) {
				errorMsg = errorMsg.replace("{" + index + "}", params[index]);
			}
		}
		return errorMsg;
	}

	public static String generateMessage(String code, String... params) {
		String msg = message.getString(code);
		if (params != null) {
			for (int index = 0; index < params.length; index++) {
				msg = msg.replace("{" + index + "}", params[index] == null ? "" : params[index]);
			}
		}
		return msg;
	}
	
	public static String generateMessage(String rawData, Map<String, Object> data) {
		if (rawData != null && data != null) {
			for (Map.Entry<String, Object> entry : data.entrySet()) {
			    rawData = rawData.replace("[" + entry.getKey() + "]", entry.getValue() == null ? "" : (String)entry.getValue());
			}
		}
		return rawData;
	}
	
}
