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
			throw new BadRequestException(generateErrorMessage(code, message), code);
		}
	}
	
	public static void rejectIfNull(Object data, String code, int errorCode, String... message) {
		if (data == null) {
			if (errorCode == 404) {
				throw new NotFoundException(generateErrorMessage(code, message), code);
			} else { 
				throw new BadRequestException(generateErrorMessage(code, message), code);
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
	
	public static void rejectIfMaxLimitExceed(int maxlimit, String content, String code, String... message) {
		if (content != null && content.length() > maxlimit) {

			throw new BadRequestException(generateErrorMessage(code, message), code);
		}

	}
	
	public static void rejectIfAlreadyExist(Object data, String errorCode, String errorMsg) {
		if (data != null) {
			throw new BadRequestException(generateErrorMessage(errorCode, errorMsg), errorCode);
		}
	}

	public static void reject(Boolean data, String errorMsg){
		if(!data){
			throw new BadRequestException(errorMsg);
		}
	}
}
