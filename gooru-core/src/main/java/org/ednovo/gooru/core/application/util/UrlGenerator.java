/*******************************************************************************
 * UrlGenerator.java
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

}
