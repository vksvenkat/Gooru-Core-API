package org.ednovo.gooru.core.application.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.BadRequestException;
import org.json.JSONObject;
import org.restlet.data.Method;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convenience class for setting and retrieving cookies.
 */
public class RequestUtil implements ParameterProperties {

	private transient static Logger logger = LoggerFactory.getLogger(RequestUtil.class);

	/**
	 * Convenience method to set a cookie
	 * 
	 * @param response
	 * @param name
	 * @param value
	 * @param path
	 */
	public static void setCookie(HttpServletRequest request, HttpServletResponse response, String name, String value) {
		logger.info(SETTING_COOKIE + name);
		Cookie cookie = new Cookie(name, value);
		cookie.setPath(COOKIE_PATH);
		cookie.setMaxAge(COOKIE_AGE);
		cookie.setDomain(request.getServerName());
		response.addCookie(cookie);
	}

	/**
	 * Convenience method to get a cookie by name
	 * 
	 * @param request
	 *            the current request
	 * @param name
	 *            the name of the cookie to find
	 * 
	 * @return the cookie (if found), null if not found
	 */
	public static String getCookieValue(HttpServletRequest request, String name) {
		Cookie cookie = getCookie(request, name);
		return cookie != null ? cookie.getValue() : null;
	}

	/**
	 * Convenience method to get a cookie by name
	 * 
	 * @param request
	 *            the current request
	 * @param name
	 *            the name of the cookie to find
	 * 
	 * @return the cookie (if found), null if not found
	 */
	public static Cookie getCookie(HttpServletRequest request, String name) {
		logger.info(GETTING_COOKIE + name);
		Cookie[] cookies = request.getCookies();

		if (cookies == null) {
			return null;
		}

		for (int i = 0; i < cookies.length; i++) {
			Cookie thisCookie = cookies[i];

			if (thisCookie.getName().equals(name) && thisCookie.getMaxAge() != 0 && !thisCookie.getValue().equals("")) {
				return thisCookie;
			}
		}

		return null;
	}

	/**
	 * Convenience method for deleting a cookie by name
	 * 
	 * @param response
	 *            the current web response
	 * @param cookie
	 *            the cookie to delete
	 * @param path
	 *            the path on which the cookie was set (i.e. /appfuse)
	 */
	public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				// Delete the cookie by setting its maximum age to zero
				if (cookie.getName().equals(name)) {
					Cookie gooruCookie = new Cookie(cookie.getName(), "");
					gooruCookie.setMaxAge(0);
					gooruCookie.setPath(COOKIE_PATH);
					gooruCookie.setDomain(request.getServerName());
					gooruCookie.setValue("");
					response.addCookie(gooruCookie);
				}
			}
		}
	}

	/**
	 * Convenience method to get the application's URL based on request
	 * variables.
	 */
	public static String getAppURL(HttpServletRequest request) {
		StringBuffer url = new StringBuffer();
		int port = request.getServerPort();
		if (port < 0) {
			port = 80; // Work around java.net.URL bug
		}
		String scheme = request.getScheme();
		url.append(scheme);
		url.append("://");
		url.append(request.getServerName());
		if ((scheme.equals("http") && (port != 80)) || (scheme.equals("https") && (port != 443))) {
			url.append(':');
			url.append(port);
		}
		url.append(request.getContextPath());
		return url.toString();
	}

	/**
	 * Convenience method to get the application local filesystem path (where it
	 * is deployed) based on request variables.
	 */
	public static String getAppLocalPath(HttpServletRequest request) {

		return request.getSession().getServletContext().getRealPath("/");
	}

	/**
	 * Creates a string from stacktrace
	 * 
	 * @param t
	 *            Throwable whose stack trace is required
	 * @return String representing the stack trace of the exception
	 */
	public static String stackTraceToString(Throwable t) {

		StringWriter stringWritter = new StringWriter();
		PrintWriter printWritter = new PrintWriter(stringWritter, true);
		t.printStackTrace(printWritter);
		printWritter.flush();
		stringWritter.flush();

		return stringWritter.toString();
	}

	public static byte[] readMultipartRequest(List<FileItem> fileItemsList) throws IOException {

		byte[] fileData = null;
		for (FileItem fileItem : fileItemsList) {
			String paramName = ((FileItem) fileItem).getFieldName();
			if ((paramName.equals(FILE)) && (((FileItem) fileItem).getInputStream().read() != -1)) {
				fileData = fileItem.get();
			}

		}
		return fileData;
	}

	public static Map<String, String> readMultipartRequestParams(List<FileItem> fileItemsList) throws FileUploadException, UnsupportedEncodingException {
		Map<String, String> map = new HashMap<String, String>();
		for (FileItem item : fileItemsList) {
			if (item.isFormField()) {
				String value = new String(item.getString().getBytes("ISO-8859-1"), "UTF-8");
				map.put(item.getFieldName(), value);
			}
		}
		return map;
	}

	public static Map<String, Object> getMultipartItems(HttpServletRequest request) throws FileUploadException, IOException {
		return getMultipartItems(request, false);
	}

	public static Map<String, Object> getMultipartItems(HttpServletRequest request, boolean useFieldNamesAsKey) throws FileUploadException, IOException {

		if (ServletFileUpload.isMultipartContent(request)) {

			Map<String, Object> formFieldMap = new HashMap<String, Object>();
			Map<String, byte[]> files = new HashMap<String, byte[]>();
			Map<String, Map<String, Object>> filesWithInfo = new HashMap<String, Map<String, Object>>();

			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iter = upload.getItemIterator(request);

			while (iter.hasNext()) {

				FileItemStream item = iter.next();
				InputStream stream = item.openStream();

				if (item.isFormField()) {

					String value = new String(Streams.asString(stream).getBytes("ISO-8859-1"), "UTF-8");

					formFieldMap.put(item.getFieldName(), value);

				} else {

					int len;
					byte[] buffer = new byte[8192];
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					while ((len = stream.read(buffer, 0, buffer.length)) != -1) {
						bout.write(buffer, 0, len);
					}
					String fieldName = item.getName();
					if (useFieldNamesAsKey) {
						fieldName = item.getFieldName();
						Map<String, Object> fileInfo = new HashMap<String, Object>();
						fileInfo.put(FILENAME, item.getName());
						fileInfo.put(FILE_DATA, bout.toByteArray());
						filesWithInfo.put(fieldName, fileInfo);
					} else {
						files.put(fieldName, bout.toByteArray());
					}

				}
			}

			if (useFieldNamesAsKey) {
				formFieldMap.put(UPLOADED_FILE_KEY, filesWithInfo);
			} else {
				formFieldMap.put(UPLOADED_FILE_KEY, files);
			}

			return formFieldMap;

		} else {
			throw new BadRequestException("Invalid Content Type " + request.getContentType());
		}
	}

	public static Map<String, Object> getMultipartItem(HttpServletRequest request) throws FileUploadException, IOException {

		if (ServletFileUpload.isMultipartContent(request)) {

			Map<String, Object> formFieldMap = new HashMap<String, Object>();
			Map<String, byte[]> files = new HashMap<String, byte[]>();

			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator iter = upload.getItemIterator(request);

			while (iter.hasNext()) {

				FileItemStream item = iter.next();
				InputStream stream = item.openStream();

				if (item.isFormField()) {

					String value = new String(Streams.asString(stream).getBytes("ISO-8859-1"), "UTF-8");

					@SuppressWarnings("unchecked")
					List<String> values = (List<String>) formFieldMap.get(item.getFieldName());
					if (values == null) {
						values = new ArrayList<String>();
					}

					values.add(value);
					formFieldMap.put(item.getFieldName(), values);

				} else {

					int len;
					byte[] buffer = new byte[8192];
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					while ((len = stream.read(buffer, 0, buffer.length)) != -1) {
						bout.write(buffer, 0, len);
					}

					files.put(item.getName(), bout.toByteArray());
				}
			}

			formFieldMap.put(UPLOADED_FILE_KEY, files);

			return formFieldMap;

		} else {
			// FIXME temporary fix for resource drop issue
			return new HashMap<String, Object>();
		}
	}

	

	public static String replaceWithSecuredUrl(HttpServletRequest request, String assetUri) {

		String requestScheme = request.getScheme();
		
		if (requestScheme.contains("http://")) {
			assetUri.replace("http://", "https://");

		}
		return assetUri;
	}

	public static void executeRestAPI(Map<String, Object> param, String requestUrl, String requestType) {
		try {
			JSONObject json = new JSONObject(param);
			String sessionToken = UserGroupSupport.getSessionToken();
			if (sessionToken != null) {
				executeMethod(new ClientResource(requestUrl + "?sessionToken=" + sessionToken), json.toString(), requestType);
			} else {
				logger.error("session token cannot be null!");
			}
		} catch (Exception e) {

		}

	}

	private static Representation executeMethod(ClientResource clientResource, String data, String type) {
		Representation representation = null;
		if (type.equalsIgnoreCase(Method.POST.getName())) {
			representation = clientResource.post(data);
		} else if (type.equalsIgnoreCase(Method.PUT.getName())) {
			representation = clientResource.put(data);
		}
		return representation;

	}

	public static String executeRestAPI(String data, String requestUrl, String requestType) {
		try {
			String sessionToken = UserGroupSupport.getSessionToken();
			if (sessionToken != null) {
				Representation representation = executeMethod(new ClientResource(requestUrl + "?sessionToken=" + sessionToken), data, requestType);
				return representation != null ? representation.getText() : null;
			} else {
				logger.error("session token cannot be null!");
			}
		} catch (Exception e) {

		}
		return null;

	}
}
