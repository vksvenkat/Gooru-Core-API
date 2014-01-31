package org.ednovo.gooru.core.application.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.XMLWriter;
import org.ednovo.gooru.core.constant.ParameterProperties;

public class StringUtil implements ParameterProperties{
	
	public static String dumpToString(String string) throws IOException {
			
			
			Writer osw = new StringWriter();
			XMLWriter writer = new XMLWriter(osw);
			writer.setEscapeText(false);
			writer.write(string);
			writer.close();
			
			return osw.toString();		
	}
	
	public static String dumpToString(Document doc) throws IOException {
		doc.setXMLEncoding("UTF-8");
		Writer osw = new StringWriter();
		XMLWriter writer = new XMLWriter(osw);
		writer.setEscapeText(true);
		
		
		writer.write(doc);
		writer.close();
		return osw.toString();
	}
	
	public static String escapeSplCharacters(String value){		
		return StringEscapeUtils.escapeXml(value);
	}
	
	public static String unescapeSplCharacters(String value){
		return StringEscapeUtils.unescapeXml(value);
	}
	
	public static Document convertString2Document( String xml ) throws Exception
	{
		Document document = null;
		document = DocumentHelper.parseText( xml );
		
		return document;
	}
	
	public static String replace (String target, String from, String to) {
		//   target is the original string
		//   from   is the string to be replaced
		//   to     is the string which will used to replace
		//  returns a new String!
		char [] targetChars = target.toCharArray();
		
		int start = target.toLowerCase().indexOf(from.toLowerCase());
		if (start == -1){ 
		return target;
		}
		int lf = from.length();
		StringBuffer buffer = new StringBuffer();
		int copyFrom = 0;
		while (start != -1) {
			buffer.append (targetChars, copyFrom, start-copyFrom);
			buffer.append (to);
			copyFrom = start + lf;
			start = target.indexOf (from, copyFrom);
		}
		buffer.append (targetChars, copyFrom, targetChars.length - copyFrom);
		return buffer.toString();
	}
	
	public static String binaryToString(byte[] inp)
	{
		
		char[] ch = new char[inp.length];
		for (int n = 0; n < inp.length; n++)
			ch[n] = (char) (int) inp[n];
		return new String(ch);
	}
	
	public static String convertListToString(List<String> list) {
		String str = "";
		for (int i = 0; i < list.size(); i++) {
			if (i < list.size() - 1){
				str = str + list.get(i) + "~";
			}
			else{
				str = str + list.get(i);
			}
		}
		return str;
	}
	
	/*public static String filterText(String value)
	{		
		String str =  value.replace("'", "''");
		return str;
		
	}*/
	
	public static String stripSpecialCharacters(String str) {
		
		try {
			str = new String(str.getBytes("UTF-8"),"UTF-8");
		} catch(Exception e) {
			throw new RuntimeException("error while stripping special characters",e);
		}
		str = str.replaceAll("[^\\x00-\\x7E]", "");
		str = str.replaceAll("(&|&amp;)#([1-9][3-9][0-9]|[2-9][0-9][0-9]|128|129);", "");
		return str;
	}

	public static String MD5(String text) throws Exception {
		MessageDigest md;
		md = MessageDigest.getInstance(MD_5);
		byte[] md5hash = new byte[32];
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		md5hash = md.digest();
		return new BigInteger(1, md5hash).toString(16);
	}
	
}
