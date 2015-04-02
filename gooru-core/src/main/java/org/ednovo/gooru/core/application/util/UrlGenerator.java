package org.ednovo.gooru.core.application.util;



import java.util.Map;

public class UrlGenerator {

	public static String generateUrl(String endpoint, UrlToken token, Map<String, String> optionalParams, String... params) {
		String url = generateUrl(endpoint, token, params);
		if (optionalParams != null) {
			for (String key : optionalParams.keySet()) {
				url += "&" + key + "=" + optionalParams.get(key);
			}
		}
		return url;
	}

	public static String generateUrl(String endpoint, UrlToken token, String... params) {
		String url = token.getUrl();
		return endpoint + generateUrl(url, params);
	}

	public static String generateUrl(String endpoint, UrlToken token) {
		String url = token.getUrl();
		return endpoint + url;
	}
	
	public static String generateUrl(String url, String... params) {
		if (params != null) {
			for (int index = 0; index < params.length; index++) {
				url = url.replace("{" + index + "}", params[index]);
			}
		}
		return  url;
	}
	
	public static void main(String a[]) { 
		System.out.print("sdsd");
	}

}
