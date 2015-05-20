package org.ednovo.gooru.core.application.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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

	private static String PASSWORD_HASH = "IlluminateGirardPasswordHash";

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

	public static String getStringMD5Hash(String text) throws Exception {
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

	// used peace of code which is under the Apache License
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

	public static Map<String, String> supportedDocument() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ppt", "ppt");
		map.put("doc", "doc");
		map.put("docx", "docx");
		map.put("odt", "odt");
		map.put("odp", "odp");
		map.put("pptx", "pptx");
		Map<String, String> fileExtentions = Collections.unmodifiableMap(map);
		return fileExtentions;
	}

	public static StringBuffer readRequestBody(HttpServletRequest request) {
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

	public static String encryptPassword(String text) throws Exception {
		for (int i = 0; i < 10; i++) {
			text = md5(text + PASSWORD_HASH);
		}

		return text;
	}

	public static String md5(String password) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(password.getBytes());
		byte byteData[] = md.digest();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			String hex = Integer.toHexString(0xff & byteData[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}

		return hexString.toString();
	}

	public static String getDomainName(final String resourceUrl) {
		String domainName = "";
		if (resourceUrl != null && !resourceUrl.isEmpty()) {
			if (resourceUrl.contains("http://")) {
				domainName = resourceUrl.split("http://")[1];
			} else if (resourceUrl.contains("https://")) {
				domainName = resourceUrl.split("https://")[1];
			}
			if (domainName.contains("www.")) {
				domainName = domainName.split("www.")[1];
			}
			if (domainName.contains("/")) {
				domainName = domainName.split("/")[0];
			}
		}
		return (org.apache.commons.lang.StringUtils.substringAfterLast(domainName, ".").length() > 3) ? null : domainName;
	}

	public static String extractToken(String value) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
		}

		try {
			byte[] bytes = digest.digest(value.getBytes("UTF-8"));
			return String.format("%032x", new BigInteger(1, bytes));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
		}
	}

}
