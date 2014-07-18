package org.ednovo.gooru.core.application.util;

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
import org.ednovo.gooru.core.constant.ParameterProperties;

public class GooruMd5Util implements ParameterProperties{

	/**
	 * Given a Map of parameters, builds a ordered query string
	 * 
	 * @param parameters
	 *            Map<String, Object> Usually the request.getParameterMap()
	 *            result can be passed to this.
	 * @return a query string, ordered alphabetically
	 */
	public String getOrderedQueryString(Map<String, Object> parameters) {
		List<String> orderedParameterNames = new ArrayList<String>();
		orderedParameterNames.addAll(parameters.keySet());
		Collections.sort(orderedParameterNames);
		StringBuilder sbURL = new StringBuilder();
		for (String parameterName : orderedParameterNames) {
			if (sbURL.length() > 0) {
				sbURL.append("&");
			}
			sbURL.append(parameterName + "=" + parameters.get(parameterName));
		}
		String queryString = sbURL.toString();
		return queryString;
	}

	public String getEncrytedUrl(Map<String, Object> parameters, String secretKey, String url, Long expires) {
		String hmac = null;
		List<String> orderedParameterNames = new ArrayList<String>();
		orderedParameterNames.addAll(parameters.keySet());
		Collections.sort(orderedParameterNames);
		try {
			String md5 = getMD5(parameters, orderedParameterNames);
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
			// logger.warn("Unexpected error while creating hash: " +
			// e.getMessage(), e);
			throw new IllegalArgumentException();
		}
	}

	private String getMD5(Map<String, Object> parameters, List<String> orderedParameterNames) throws NoSuchAlgorithmException {
		StringBuilder sbURL = new StringBuilder();
		for (String parameterName : orderedParameterNames) {
			sbURL.append(parameterName + "=" + parameters.get(parameterName) + "&");
		}
		String md5string = sbURL.toString();
		MessageDigest digest = MessageDigest.getInstance(MD_5);
		digest.update(md5string.getBytes());
		String result = new String(Base64.encodeBase64(digest.digest()));
		return result;
	}

	byte[] decodeBase64(String hmac) {
		return Base64.decodeBase64(hmac);
	}

	private String getMD(String url) throws NoSuchAlgorithmException {
		String md5string = url;
		MessageDigest digest = MessageDigest.getInstance(MD_5);
		digest.update(md5string.getBytes());

		String md5Result = new String(Base64.encodeBase64(digest.digest()));

		// System.out.println("url:" + url + ":::md5:::"+md5Result);
		return md5Result;
	}

	public String verifySignatureFromURL(String baseUrl, Map<String, Object> parameterMap, String secret) throws NoSuchAlgorithmException {
		GooruMd5Util gooruSigningUtil = new GooruMd5Util();

		Long expires = (Long) parameterMap.get(EXPIRE);
		String params = getOrderedQueryString(parameterMap);
		System.out.println("params:" + params);
		// get MD5 for the content (in this case parameters)
		String contentMD5 = gooruSigningUtil.getMD(params);
		String verb = GET;
		String signingData = verb + "\n" + contentMD5 + "\n" + expires + "\n" + baseUrl;

		// Compute HMAC from the signing data and secret
		String hmac = gooruSigningUtil.getHMAC(signingData, secret);

		String hmac64 = Hex.encodeHexString(Base64.encodeBase64(hmac.getBytes()));

		// System.out.println("verify: md5="+contentMD5 + " , hmac = " + hmac +
		// " , inputHmacB64 = "+incomingSignatureB64+ " , hmac64 = "+hmac64);

		return hmac64;
	}

	public String signURLForClient(String baseUrl, Map<String, Object> paramMap, String secret, Long expires) throws Exception {
		// get MD5 for the content (in this case parameters)
		String params = getOrderedQueryString(paramMap);
		String contentMD5 = this.getMD(params);
		String verb = GET;
		String signingData = verb + "\n" + contentMD5 + "\n" + expires + "\n" + baseUrl;

		// Compute HMAC from the signing data and secret
		String hmac = this.getHMAC(signingData, secret);

		String hmac64 = Hex.encodeHexString(Base64.encodeBase64(hmac.getBytes()));

		// System.out.println("sign: md5="+contentMD5 + " , hmac = " + hmac +
		// ", hmac64 = " + hmac64);

		return hmac64;
	}

}
