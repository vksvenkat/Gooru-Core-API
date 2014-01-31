/*******************************************************************************
 * Copyright 2014 Ednovo d/b/a Gooru. All rights reserved.
 * http://www.goorulearning.org/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 *  "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package org.ednovo.gooru.mail.util;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

public class GooruMd5Util {

	private final static String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	private static final String EXPIRES = "&expires=";

	/**
	 * Given a Map of parameters, builds a ordered query string
	 * 
	 * @param parameters
	 *            Map<String, Object> Usually the request.getParameterMap()
	 *            result can be passed to this.
	 * @return a query string, ordered alphabetically
	 */
	public String getOrderedQueryString(Map<String, Object> paramMap) {

		List<String> orderedParameterNames = new ArrayList<String>();
		orderedParameterNames.addAll(paramMap.keySet());
		Collections.sort(orderedParameterNames);
		StringBuilder sbURL = new StringBuilder();
		for (String parameterName : orderedParameterNames) {
			if (sbURL.length() > 0) {
				sbURL.append("&");
			}
			sbURL.append(parameterName + "=" + paramMap.get(parameterName));
		}
		String queryString = sbURL.toString();

		return queryString;
	}

	public String getEncrytedUrl(Map<String, Object> paramMap, String secretKey, String url, Long expires) {
		String hmac = null;
		List<String> orderedParameterNames = new ArrayList<String>();
		orderedParameterNames.addAll(paramMap.keySet());
		Collections.sort(orderedParameterNames);
		try {
			String md5 = getMD5(paramMap, orderedParameterNames);
			url = url + "?" + md5 + EXPIRES + expires;
			hmac = getHMAC(url, secretKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hmac;
	}

	private String getHMAC(String data, String secret) {
		try {
			SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), HMAC_SHA1_ALGORITHM);
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);
			byte[] rawHmac = mac.doFinal(data.getBytes());
			String result = new String(Base64.encodeBase64(rawHmac));
			return result;
		} catch (GeneralSecurityException e) {
			throw new IllegalArgumentException();
		}
	}

	private String getMD5(Map<String, Object> paramMap, List<String> orderedParameterNames) throws NoSuchAlgorithmException {
		StringBuilder sbURL = new StringBuilder();
		for (String parameterName : orderedParameterNames) {
			sbURL.append(parameterName + "=" + paramMap.get(parameterName) + "&");
		}
		String md5string = sbURL.toString();
		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.update(md5string.getBytes());
		String result = new String(Base64.encodeBase64(digest.digest()));
		return result;
	}

	byte[] decodeBase64(String hmac) {
		return Base64.decodeBase64(hmac);
	}

	private String getMD(String url) throws NoSuchAlgorithmException {
		String md5string = url;
		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.update(md5string.getBytes());
		String md5Result = new String(Base64.encodeBase64(digest.digest()));
		return md5Result;
	}

	public String verifySignatureFromURL(String baseUrl, Map<String, Object> paramMap, String secret) throws NoSuchAlgorithmException {
		GooruMd5Util gooruSigningUtil = new GooruMd5Util();
		// String incomingSignatureB64 = (String) paramMap.remove("signature");
		// String incomingSignature =
		// Base64.decodeBase64(incomingSignatureB64).toString();
		Long expires = (Long) paramMap.get("expires");
		String params = getOrderedQueryString(paramMap);
		String contentMD5 = gooruSigningUtil.getMD(params);
		String verb = "GET";
		String signingData = verb + "\n" + contentMD5 + "\n" + expires + "\n" + baseUrl;
		String hmac = gooruSigningUtil.getHMAC(signingData, secret);
		String hmac64 = Hex.encodeHexString(Base64.encodeBase64(hmac.getBytes()));
		return hmac64;
	}

}
