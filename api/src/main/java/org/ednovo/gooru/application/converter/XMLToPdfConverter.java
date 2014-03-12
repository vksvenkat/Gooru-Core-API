/////////////////////////////////////////////////////////////
// XMLToPdfConverter.java
// gooru-api
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.application.converter;

import java.io.File;
import java.util.UUID;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.Document;
import org.dom4j.io.DocumentSource;
import org.ednovo.gooru.core.application.util.StringUtil;
import org.ednovo.gooru.core.constant.ParameterProperties;


public class XMLToPdfConverter implements ParameterProperties {

	public static String xmlToFO(String xml, String webApp ) throws Exception{

			//DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
			File stylesheet = new File(webApp+"/stylesheets/classplan/pdf/learnguidePdf.xsl");

			Document classplan =  StringUtil.convertString2Document(xml);
				
			TransformerFactory factory = (TransformerFactory)makeInstance("net.sf.saxon.TransformerFactoryImpl");
			
			Transformer transformer = factory.newTransformer(new StreamSource(stylesheet));
			transformer.setParameter(APP_PATH, webApp);
			
			DocumentSource docSource = new DocumentSource(classplan);
			
			String randomid = UUID.randomUUID().toString();
			
			StreamResult result = new StreamResult(new File(webApp+"/learnguideFo/" +randomid+".fo"));
			transformer.transform(docSource, result);
			
			transformer = null;
			return randomid;
			
	}
	
/*	private static FopFactory fopFactory = FopFactory.newInstance();
	private static TransformerFactory tFactory = TransformerFactory.newInstance();*/
	
	
	public static Object makeInstance(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        Class providerClass = XMLToPdfConverter.class.getClassLoader().loadClass(className);
        return providerClass.newInstance();
    }
	
}
