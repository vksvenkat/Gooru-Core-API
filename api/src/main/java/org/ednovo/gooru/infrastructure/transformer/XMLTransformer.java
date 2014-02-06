/////////////////////////////////////////////////////////////
// XMLTransformer.java
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
package org.ednovo.gooru.infrastructure.transformer;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.Document;
import org.dom4j.io.DocumentSource;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.exception.TransformerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLTransformer {

	private static HashMap map = new HashMap();
	private static Logger logger = LoggerFactory.getLogger(XMLTransformer.class);
	
	private Transformer transformer;
	private HashMap transformerMap = new HashMap();
	
	public static synchronized XMLTransformer getInstance() {
		XMLTransformer singleton = (XMLTransformer)map.get("XMLTransformer");
		if(singleton != null) {
	         return singleton;
	    }
	    try {
	        singleton = (XMLTransformer)Class.forName(XMLTransformer.class.getName()).newInstance();	        
	    }
	    catch(ClassNotFoundException cnf) {
	    	logger.error("Couldn't find class " + XMLTransformer.class.toString());
	    }
	    catch(InstantiationException ie) {
	    	logger.error("Couldn't instantiate an object of type " + XMLTransformer.class.toString());
	    }
	    catch(IllegalAccessException ia) {
	    	logger.error("Couldn't access class " + XMLTransformer.class.toString()); 
	    }
	    map.put("XMLTransformer", singleton);
	    return singleton;
	}
	
	public String transform(Document source, String strMode, String appPath,
			HashMap parameterMap) {

		Transformer transformer = getTransformer(strMode, appPath);

		if (parameterMap != null) {
			Iterator iterator = (parameterMap.keySet()).iterator();
			// do{
			while (iterator.hasNext()) {
				String key = iterator.next().toString();
				String value = parameterMap.get(key).toString();

				if (logger.isDebugEnabled()){
					logger.debug("Setting parameter " + key
							+ " with value equal to " + value);
				}

				transformer.setParameter(key, value);
			}
			
		}

		if (logger.isDebugEnabled()){
			logger.debug("Received transformer: " + transformer + " Mode: "
					+ strMode);
		}

		DocumentSource docSource = new DocumentSource(source);
		ByteArrayOutputStream obj = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(obj);

		String transformedString = null;
		
		try {
			transformer.transform(docSource, result);			
	
			transformedString = new String( ((ByteArrayOutputStream)result.getOutputStream()).toByteArray(),"UTF-8");
			
		} catch (Exception e) {
			logger.error("Exception while transforming : " , e);
			throw new TransformerException("Error while getting transformer", e);
		}

		transformedString = transformedString.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");

		return transformedString;

	}

	private Transformer getTransformer(String strMode, String appPath) {

		if (logger.isDebugEnabled()){
			logger.debug("Get Transformer for: " + strMode);
		}

		transformer = (Transformer) transformerMap.get(strMode.toUpperCase());

		try {

			if (transformer == null) {

				TransformerFactory factory = TransformerFactory.newInstance();

				if (strMode.equalsIgnoreCase("EDITMODE")){
					transformer = factory.newTransformer(new StreamSource(
							appPath + Constants.CLASSPLAN_EDIT_XSL_PATH));
				}
				else if (strMode.equalsIgnoreCase("VIEW_CLASSPLAN_MODE")) {
					transformer = factory.newTransformer(new StreamSource(
							appPath + Constants.CLASSPLAN_VIEW_XSL_PATH));
				} else if (strMode.equalsIgnoreCase("EXPORT_CLASSPLAN_MODE")) {
					transformer = factory.newTransformer(new StreamSource(
							appPath + Constants.CLASSPLAN_EXPORT_XSL_PATH));
				} else if (strMode.equalsIgnoreCase("SEG_PROPS_VIEW_MODE")) {
					transformer = factory.newTransformer(new StreamSource(
							appPath + Constants.SEG_PROPS_VIEW_XSL_PATH));
				} else if (strMode.equalsIgnoreCase("VIEW_SEGMENTS_MODE")) {
					transformer = factory.newTransformer(new StreamSource(
							appPath + Constants.SEGMENTS_VIEW_XSL_PATH));
				} else if (strMode.equalsIgnoreCase("ASSET_VIEW_MODE")) {
					transformer = factory.newTransformer(new StreamSource(
							appPath + Constants.ASSET_VIEW_XSL_PATH));
				} else if (strMode.equalsIgnoreCase("SEG_PROP_EDIT_MODE")) {
					transformer = factory.newTransformer(new StreamSource(
							appPath + Constants.SEG_PROP_EDIT_XSL_PATH));
				} else if (strMode.equalsIgnoreCase("NO_EDIT_CLASSPLAN_MODE")) {
					transformer = factory.newTransformer(new StreamSource(
							appPath + Constants.CLASSPLAN_NO_EDIT_XSL_PATH));
				} else if (strMode.equalsIgnoreCase("MYNOTES_VIEW_XSL_PATH")) {
					transformer = factory.newTransformer(new StreamSource(
							appPath + Constants.MYNOTES_VIEW_XSL_PATH));
				} else if (strMode.equalsIgnoreCase("ALLNOTES_VIEW_XSL_PATH")) {
					transformer = factory.newTransformer(new StreamSource(
							appPath + Constants.ALLNOTES_VIEW_XSL_PATH));
				} else if (strMode.equalsIgnoreCase("VIEW_NOTES_XSL_PATH")) {
					transformer = factory.newTransformer(new StreamSource(
							appPath + Constants.VIEW_NOTES_XSL_PATH));
				} else if (strMode.equalsIgnoreCase("NOTE_VIEW_XSL_PATH")) {
					transformer = factory.newTransformer(new StreamSource(
							appPath + Constants.NOTE_VIEW_XSL_PATH));
				} else if (strMode.equalsIgnoreCase("VIEW_LESSONPLAN_XSL_PATH")) {
					transformer = factory.newTransformer(new StreamSource(
							appPath + Constants.VIEW_LESSONPLAN_XSL_PATH));
				} else if (strMode.equalsIgnoreCase("CLASSROOM_VIEW")) {
					transformer = factory.newTransformer(new StreamSource(
							appPath + Constants.VIEW_CLASSROOM_XSL_PATH));
				}else if(strMode.equals("VIEW_CLASSPLAN_LIBRARY_XSL_PATH_STEP_1")){
					logger.info("Tax Path: " + appPath + Constants.VIEW_CLASSPLAN_LIBRARY_XSL_PATH_STEP_1);
					transformer = factory.newTransformer(new StreamSource(
							appPath + Constants.VIEW_CLASSPLAN_LIBRARY_XSL_PATH_STEP_1));
				}else if(strMode.equals("VIEW_CLASSPLAN_LIBRARY_XSL_PATH_STEP_3")){
					transformer = factory.newTransformer(new StreamSource(
							appPath + Constants.VIEW_CLASSPLAN_LIBRARY_XSL_PATH_STEP_3));
				}else if(strMode.equals("CLASSPLAN_LIBRARY_Tree_XSL_PATH_STEP_3")){
					transformer = factory.newTransformer(new StreamSource(
							appPath + Constants.CLASSPLAN_LIBRARY_TREE_XSL_PATH_STEP_3));
				}else if(strMode.equals("CLASSPLAN_LIBRARY_Tree_XSL_PATH_STEP_4")){
					transformer = factory.newTransformer(new StreamSource(
							appPath + Constants.CLASSPLAN_LIBRARY_TREE_XSL_PATH_STEP_4));
				}else if(strMode.equals("INDEX_QUESTION_XML")){					
					transformer = factory.newTransformer(new StreamSource(appPath + Constants.INDEX_QUESTION_XML));
				}
				else if(strMode.equalsIgnoreCase("VIEW_CLASSPLAN_LIBRARY_XSL_PATH_STEP_2"))
				{
						transformer = factory.newTransformer(new StreamSource(
								appPath + Constants.VIEW_CLASSPLAN_LIBRARY_XSL_PATH_STEP_2));
						
				}
				else if(strMode.equalsIgnoreCase("VIEW_RESOURCE_LIBRARY_XSL_PATH_STEP_2"))
				{
						transformer = factory.newTransformer(new StreamSource(
								appPath + Constants.VIEW_RESOURCE_LIBRARY_XSL_PATH_STEP_2));
						
				}
				else if(strMode.equalsIgnoreCase("VIEW_LEARNGUIDE_PDF"))
				{
				try 
					{
						factory = (TransformerFactory)makeInstance("net.sf.saxon.TransformerFactoryImpl");
						logger.debug(appPath + Constants.VIEW_LEARNGUIDE_PDF);
						transformer = factory.newTransformer(new StreamSource(appPath + Constants.VIEW_LEARNGUIDE_PDF));
					}
					catch (ClassNotFoundException e) 
					{
						throw new TransformerException ("Can not instantiate net.sf.saxon.TransformerFactoryImpl",e);
					} 
					catch (InstantiationException e) 
					{
						throw new TransformerException("Can not instantiate net.sf.saxon.TransformerFactoryImpl",e);
					}
					catch (IllegalAccessException e) 
					{
						throw new TransformerException("Can not instantiate net.sf.saxon.TransformerFactoryImpl",e);
					} 
				}

				transformerMap.put(strMode.toUpperCase(), transformer);
			}
		} catch (Exception e) {
			throw new TransformerException("Error while getting transformer", e);
		}

		return (Transformer) transformerMap.get(strMode.toUpperCase());
	}

	public Transformer getTransformer() {
		return transformer;
	}

	public void setTransformer(Transformer transformer) {
		this.transformer = transformer;
	}

	public HashMap getTransformerMap() {
		return transformerMap;
	}

	public void setTransformerMap(HashMap transformerMap) {
		this.transformerMap = transformerMap;
	}

	
	static Object makeInstance(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        Class providerClass = XMLTransformer.class.getClassLoader().loadClass(className);
        return providerClass.newInstance();
    }
}
