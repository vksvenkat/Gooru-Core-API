package org.ednovo.gooru.core.application.util;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Random;

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
	    for(int i = 0; i < length; i++){
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
			HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
			if (request != null &&  request.getRequestURL() != null && url != null && url.contains("http://") && request.getRequestURL().toString().contains("https://")) {
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
		ServletRequestAttributes requestAttributes = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes());
		if(requestAttributes != null){
			HttpServletRequest request = requestAttributes.getRequest();			
			if (request != null &&  request.getRequestURL() != null && request.getRequestURL().toString().contains("https://")) { 
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
		if (resource != null && resource.getUrl() != null   && method != null && method.equalsIgnoreCase(Method.GET.getName())) {
			// condition check whether it's coming from search or not			
			if (requestProtocol != null && requestProtocol.trim().length() > 0 && isSecure) {
				resource.setUrl(resource.getUrl().replaceFirst("http://", "https://"));
			} else  {
				if (isSecure && resource.getResourceSource() != null && resource.getResourceSource().getProtocolSupported() != null && resource.getResourceSource().getProtocolSupported() >= 2) {
					resource.setUrl(resource.getUrl().replaceFirst("http://", "https://"));
				} 
			}
		}

	}
	
	public static boolean isSecure(HttpServletRequest request) {
		boolean isSecure = false;
		if (request != null) {
			String requestProtocol = request.getAttribute("requestProtocol") != null ?  (String) request.getAttribute("requestProtocol") : null;
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
}
