/////////////////////////////////////////////////////////////
// MediaServiceImpl.java
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
package org.ednovo.gooru.domain.service.resource.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.api.model.FileMeta;
import org.ednovo.gooru.core.api.model.MediaDTO;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.application.util.ImageUtil;
import org.ednovo.gooru.core.application.util.RequestUtil;
import org.ednovo.gooru.core.application.util.ServerValidationUtils;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.BadRequestException;
import org.ednovo.gooru.domain.service.resource.MediaService;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleOp;

@Service
public class MediaServiceImpl implements MediaService,ParameterProperties {
	
	@Autowired
	private SettingService settingService;
	
	
	private static final int MAXFILEUPLOADSIZE = 31457280;
	


	/**
	 * @param fileName
	 * @param imageURL
	 * @param formField
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public FileMeta handleFileUpload(String fileName, String imageURL, Map<String, Object> formField, boolean resize, int width, int height) throws FileNotFoundException, IOException {

		final String mediaFolderPath = UserGroupSupport.getUserOrganizationNfsInternalPath() + Constants.UPLOADED_MEDIA_FOLDER;
		
		String resourceImageFile = mediaFolderPath + "/" + fileName;
		String originalFilename = null;
		boolean successFlag = false;
		String uploadImageSource = LOCAL;
		String imageValidationMsg = "Size exceeded , try file upload size is below 30 MB";
		if (imageURL != null && imageURL.length() > 0) {
			if (resize) {
				successFlag = ImageUtil.downloadAndSaveFile(imageURL, resourceImageFile,width,height,true);
			} else { 
				successFlag = ImageUtil.downloadAndSaveFile(imageURL, resourceImageFile);
			}
			uploadImageSource = WEB;
			
		} else {
			File classplanDir = new File(mediaFolderPath);

			if (!classplanDir.exists()) {
				classplanDir.mkdirs();
			}

			@SuppressWarnings("unchecked")
			Map<String, byte[]> files = (Map<String, byte[]>) formField.get(RequestUtil.UPLOADED_FILE_KEY);

			byte[] fileData = null;

			// expecting only one file in the request right now
			for (String name : files.keySet()) {
				if (name != null) {
					originalFilename = name;
					successFlag = true;
					break;
				}
			}
			fileData = files.get(originalFilename);
			
			if (fileData != null && fileData.length > 0) {

				File file = new File(resourceImageFile);

				OutputStream out = new FileOutputStream(file);
				out.write(fileData);
				out.close();
				
				if (resize) {
					BufferedImage image = null;
					try {
						image = ImageIO.read(new File(resourceImageFile));
						double maxHeight = height;
						double maxWidth = width;
						int imageFileWidth = (int) (image.getWidth());
						int imageFileHeight = (int) (image.getHeight());
						if (imageFileHeight > maxHeight || imageFileWidth > maxWidth) {
							double ratio = (double) Math.min(maxWidth / image.getWidth(), maxHeight / image.getHeight());
							width = (int) (image.getWidth() * ratio);
							height = (int) (image.getHeight() * ratio);
						} else {
							width = imageFileWidth;
							height = imageFileHeight;
						}
						ResampleOp resampleOp = new ResampleOp(width, height);
						resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal);
						image = resampleOp.filter(image, null);
						ImageIO.write(image, PNG, new File(resourceImageFile));
					}
					 catch (Exception e) {
							throw new BadRequestException(ServerValidationUtils.generateErrorMessage(GL0106),GL0106);
					 }
				}
            }
		}
		
		File uploadedFile = new File(resourceImageFile);
		long fileSize = 0;
		if(uploadedFile != null) {
			fileSize = uploadedFile.length();
			if(MAXFILEUPLOADSIZE < fileSize){
				FileMeta fileMetaSize = new FileMeta();
				fileMetaSize.setImageValidationMsg(imageValidationMsg);
				uploadedFile.delete();
				return fileMetaSize;
			}
			
		}
		String fileUrl = UserGroupSupport.getUserOrganizationNfsRealPath() + Constants.UPLOADED_MEDIA_FOLDER + "/" + fileName;
		FileMeta fileMeta = new FileMeta(fileName, fileSize, fileUrl);
		fileMeta.setOriginalFilename(originalFilename);
		fileMeta.setDeleteUrl(MEDIA_SLASH+fileName);
		fileMeta.setUploadImageSource(uploadImageSource);
		
		if(successFlag) {
			fileMeta.setstatusCode(200);
		} else {
			fileMeta.setstatusCode(500);
		}
		
		return fileMeta;
	}

	@Override
	public FileMeta handleFileUpload(MediaDTO mediaDTO, Map<String, Object> formField) throws FileNotFoundException, IOException {
		String fileExtension = null;
		if (formField.get(RequestUtil.UPLOADED_FILE_KEY) != null) {
			@SuppressWarnings("unchecked")
			Map<String, byte[]> files = (Map<String, byte[]>) formField.get(RequestUtil.UPLOADED_FILE_KEY);
			for (String name : files.keySet()) {
				if (name != null) {
					fileExtension = StringUtils.substringAfterLast(name, ".");
					break;
				}
			}   
		}
		
		if(fileExtension == null || fileExtension.isEmpty()) {
			fileExtension = PNG;
		}
		Map<String, String> fileExtentions= BaseUtil.supportedDocument();
		if (fileExtentions.containsKey(fileExtension)){
			mediaDTO.setResize(false);
		}
		if (fileExtension != null &&  (fileExtension.equalsIgnoreCase(PDF)) || fileExtension != null && fileExtentions != null) {
			mediaDTO.setResize(false);
		} 
	
		mediaDTO.setFilename(UUID.randomUUID().toString() + "." + fileExtension);
		return handleFileUpload(mediaDTO.getFilename(),mediaDTO.getImageURL(),formField, mediaDTO.getResize(), mediaDTO.getWidth(),mediaDTO.getHeight());
	}
	
	@Override
	public String convertHtmltoPdf(JSONObject data) {
		try {
			data.put(TARGET_FOLDER_PATH, UserGroupSupport.getUserOrganizationNfsInternalPath() +Constants.UPLOADED_MEDIA_FOLDER + "/" + Constants.UPLOADED_SUMMARY_FOLDER + "/");
		} catch (JSONException e) {
		}
		String filename = RequestUtil.executeRestAPI(data.toString(), settingService.getConfigSetting(ConfigConstants.GOORU_CONVERSION_RESTPOINT,0, TaxonomyUtil.GOORU_ORG_UID) + "/conversion/htmltopdf", Method.POST.getName());
		if (filename != null) { 
			return UserGroupSupport.getUserOrganizationNfsRealPath() + Constants.UPLOADED_MEDIA_FOLDER  + "/" + Constants.UPLOADED_SUMMARY_FOLDER + "/" + filename;
		} 
		return null;
	}

	
	public String convertJsonToCsv(JSONObject data) {
		try {
			data.put(TARGET_FOLDER_PATH, UserGroupSupport.getUserOrganizationNfsInternalPath() +Constants.UPLOADED_MEDIA_FOLDER + "/");
		} catch (JSONException e) {
		}
		return UserGroupSupport.getUserOrganizationNfsRealPath() + Constants.UPLOADED_MEDIA_FOLDER  + "/" + RequestUtil.executeRestAPI(data.toString(), settingService.getConfigSetting(ConfigConstants.GOORU_CONVERSION_RESTPOINT,0, TaxonomyUtil.GOORU_ORG_UID) + "/conversion/jsontostring", Method.POST.getName());
	}
	
	@Override
	public void downloadFile(HttpServletResponse response, String filename, String url) {
		response.setContentType("application/force-download");
		response.setHeader("Content-Transfer-Encoding", "binary");
		response.setHeader("Content-Disposition", "attachment; filename=\""+ filename + "\"");
		URL webUrl;
		InputStream is = null;
		try {
			ByteArrayOutputStream bais = new ByteArrayOutputStream();
			webUrl = new URL(url);
			is = webUrl.openStream(); 
			byte[] byteChunk = new byte[4096]; 							
			int n;

			while ((n = is.read(byteChunk)) > 0) {
				bais.write(byteChunk, 0, n);
			}
			OutputStream os = response.getOutputStream();
			os.write(bais.toByteArray());
			os.close();
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException ioe) {
			}
		}
	}
	
}
	

	

