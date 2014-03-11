/*
*FileProcessor.java
* gooru-api
* Created by Gooru on 2014
* Copyright (c) 2014 Gooru. All rights reserved.
* http://www.goorulearning.org/
*      
* Permission is hereby granted, free of charge, to any 
* person obtaining a copy of this software and associated 
* documentation. Any one can use this software without any 
* restriction and can use without any limitation rights 
* like copy,modify,merge,publish,distribute,sub-license or 
* sell copies of the software.
* The seller can sell based on the following conditions:
* 
* The above copyright notice and this permission notice shall be   
* included in all copies or substantial portions of the Software. 
*
*  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
*  KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
*  WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
*  PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
*  OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
*  OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
*  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
*  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
*  THE SOFTWARE.
*/

package org.ednovo.gooru.application.converter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.FileUtils;
import org.ednovo.gooru.core.api.model.FileMeta;
import org.ednovo.gooru.core.application.util.RequestUtil;
import org.ednovo.gooru.core.constant.Constants;

public class FileProcessor {

	public static String getFileName(String fileName) {
		return fileName.substring(0, fileName.indexOf("."));
	}

	public static FileMeta extractFileData(HttpServletRequest request, Map<String, Object> formField, String repositoryPath) throws FileUploadException, IOException {
		FileMeta fileMeta = null;
		byte[] fileData = null;
		String originalFilename = null;

		if (request == null || formField != null) {
			formField = RequestUtil.getMultipartItems(request);
		}

		if (request != null) {
			String filename = request.getParameter("media.filename");
			if (filename != null) {
				originalFilename = request.getParameter("media.originalFilename");
				File mediaFile = new File(repositoryPath + "/" + Constants.UPLOADED_MEDIA_FOLDER + "/" + filename);
				fileData = FileUtils.readFileToByteArray(mediaFile);

				fileMeta = new FileMeta();
				fileMeta.setName(filename);
				fileMeta.setOriginalFilename(originalFilename);
				fileMeta.setFileData(fileData);
			}
		}
		if (fileMeta == null && formField != null) {
			Map<String, byte[]> files = (Map<String, byte[]>) formField.get(RequestUtil.UPLOADED_FILE_KEY);

			// expecting only one file in the request right now
			for (String name : files.keySet()) {
				if (name != null) {
					originalFilename = name;
					break;
				}
			}
			fileData = files.get(originalFilename);
			fileMeta = new FileMeta();
			fileMeta.setName(originalFilename);
			fileMeta.setOriginalFilename(originalFilename);
			fileMeta.setFileData(fileData);
		}

		return fileMeta;
	}

	public static String getParentPath(String path) {
		String parentPath = "";
		if (path != null) {
			String currentPath = "";
			for (int i = 0; i < path.length(); i++) {
				currentPath += path.charAt(i);
				if (path.charAt(i) == '/') {
					parentPath += currentPath;
					currentPath = "";
				}
			}
		}
		return parentPath.substring(0, parentPath.length() - 1);
	}

	public static File writeFile(String path, String fileName, byte[] data) {

		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		File resourceFile = new File(path + "/" + fileName);

		try {
			OutputStream out = new FileOutputStream(resourceFile);
			out.write(data);
			out.close();
		} catch (IOException e) {
			return null;
		}
		return resourceFile;
	}

	public static String getFileExt(String fileName) {
		return fileName.substring(fileName.indexOf(".") + 1);
	}

}
