package org.ednovo.gooru.core.application.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.ednovo.gooru.core.api.model.Resource;
import org.restlet.data.Method;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class BaseUtil {

	private static final String CHARACTER_SET = "23456789abcdefghijkmnpqrstuvwxyzABCDEFGHIJKLMNPQRSTUVWXYZ";

	private static Random rnd = new Random();

	public static String getByteMD5Hash(byte[] data) throws Exception {
		if (data != null) {
			int dataLength = data.length;
			MessageDigest md;
			md = MessageDigest.getInstance("MD5");
			byte[] md5hash = new byte[32];
			md.update(data, 0, dataLength);
			md5hash = md.digest();
			return new BigInteger(1, md5hash).toString(16);
		}
		return null;
	}

	public static String base48Encode(int length) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length; i++) {
			builder.append(CHARACTER_SET.charAt(rnd.nextInt(CHARACTER_SET.length())));
		}
		return builder.toString();
	}

	protected static String getStringMD5Hash(String text) throws Exception {
		return getByteMD5Hash(text.getBytes("iso-8859-1"));
	}

	public static String getFileMD5Hash(String path) throws Exception {
		File file = new File(path);
		if (file.exists() && file.isFile()) {
			return getByteMD5Hash(FileUtils.readFileToByteArray(file));
		}
		return null;
	}

	public static String changeHttpsProtocol(String url) {
		if (RequestContextHolder.getRequestAttributes() != null) {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			if (request != null && request.getRequestURL() != null && url != null && url.contains("http://") && request.getRequestURL().toString().contains("https://")) {
				url = url.replaceFirst("http://", "https://");
			}
		}
		return url;
	}

	public static String changeToHttpProtocol(String url) {
		if (url != null && url.contains("https://")) {
			url = url.replaceFirst("https://", "http://");
		}
		return url;
	}

	public static String appendProtocol(String key) {
		ServletRequestAttributes requestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
		if (requestAttributes != null) {
			HttpServletRequest request = requestAttributes.getRequest();
			if (request != null && request.getRequestURL() != null && request.getRequestURL().toString().contains("https://")) {
				key += "https";
			} else {
				key += "http";
			}
		}
		return key;
	}

	public static String changeHttpsProtocolByHeader(String url) {
		HttpServletRequest request = null;
		if (RequestContextHolder.getRequestAttributes() != null) {
			request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		}
		if (url != null && isSecure(request)) {
			url = url.replaceFirst("http://", "https://");
		}
		return url;
	}

	public static void changeHttpsProtocolByHeader(Resource resource, String requestProtocol, boolean isSecure, String method) {
		if (resource != null && resource.getUrl() != null && method != null && method.equalsIgnoreCase(Method.GET.getName())) {
			// condition check whether it's coming from search or not
			if (requestProtocol != null && requestProtocol.trim().length() > 0 && isSecure) {
				resource.setUrl(resource.getUrl().replaceFirst("http://", "https://"));
			} else {
				if (isSecure && resource.getResourceSource() != null && resource.getResourceSource().getProtocolSupported() != null && resource.getResourceSource().getProtocolSupported() >= 2) {
					resource.setUrl(resource.getUrl().replaceFirst("http://", "https://"));
				}
			}
		}

	}

	public static boolean isSecure(HttpServletRequest request) {
		boolean isSecure = false;
		if (request != null) {
			String requestProtocol = request.getAttribute("requestProtocol") != null ? (String) request.getAttribute("requestProtocol") : null;
			if (requestProtocol != null && requestProtocol.trim().length() > 0) {
				if (requestProtocol.equalsIgnoreCase("https")) {
					isSecure = true;
				} else {
					isSecure = false;
				}
			} else {
				isSecure = request.isSecure();
				if (!isSecure) {
					requestProtocol = request.getHeader("X-FORWARDED-PROTO");
					if (requestProtocol != null && requestProtocol.equalsIgnoreCase("https")) {
						isSecure = true;
					}
				}
			}
		}
		return isSecure;
	}

	public static String dateFormat(String date, String delimiter, String replaceDelimiter) {
		if (date != null) {
			Object[] o = Arrays.asList(date.split(delimiter)).toArray();
			return (o[2] + replaceDelimiter + o[1] + replaceDelimiter + o[0]);
		}
		return null;
	}

	public static String getYoutubeVideoId(String url) {
		String pattern = "youtu(?:\\.be|be\\.com)/(?:.*v(?:/|=)|(?:.*/)?)([a-zA-Z0-9-_]{11}+)";
		String videoId = null;
		Pattern compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = compiledPattern.matcher(url);
		if (matcher != null) {
			while (matcher.find()) {
				videoId = matcher.group(1);
			}
		}
		return videoId;
	}

	
	//used peace of code which is under the Apache License
	public static String removeCurlies(String uuid) {
		if (uuid.length() > 0) {
			if (uuid.startsWith("{"))
				uuid = uuid.substring(1);
			if (uuid.endsWith("}"))
				uuid = uuid.substring(0, uuid.length() - 1);
		}
		return uuid;
	}
    // used peace of code which is under the Apache License
	public static boolean isUuid(String uuid) {
		boolean bIsUuid = false;
		uuid = removeCurlies(uuid);
		if (uuid.length() == 36) {
			String[] aParts = uuid.split("-");
			if (aParts.length == 5) {
				if ((aParts[0].length() == 8) && (aParts[1].length() == 4) && (aParts[2].length() == 4) && (aParts[3].length() == 4) && (aParts[4].length() == 12)) {
					bIsUuid = true;
				}
			}
		}
		return bIsUuid;
	}
	
	public  static StringBuffer readRequestBody(HttpServletRequest request) {
		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			InputStream inputStream = request.getInputStream();
			if (inputStream != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				while ((line = reader.readLine()) != null)
					jb.append(line);
			}
		} catch (Exception e) {
		}
		return jb;
	}
}
