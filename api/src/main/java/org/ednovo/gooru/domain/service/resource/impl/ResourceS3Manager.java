/*
*ResourceS3Manager.java
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

package org.ednovo.gooru.domain.service.resource.impl;

import org.apache.commons.io.IOUtils;
import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.api.model.Asset;
import org.ednovo.gooru.core.application.util.S3FileNameParser;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResourceS3Manager implements ParameterProperties{

	@Autowired
	@javax.annotation.Resource(name = "gooruContentS3Service")
	private RestS3Service s3Service;
	
	@Autowired
	private SettingService settingService;

	private final Logger logger = LoggerFactory.getLogger(ResourceS3Manager.class);

	public byte[] getByteStream(Asset asset, String folder, String fileName) throws Exception {

		String s3ResourceUrl = "";

		String bucketFolder = null;

		if (folder != null) {
			folder = folder.trim();
			if (folder.contains(GOORU_CONTENT_SLASH)) {
				bucketFolder = folder.substring(GOORU_CONTENT_SLASH.length(), folder.length());
			} else if (folder.contains(GOORU_CONTENT_SLA)) {
				bucketFolder = folder.substring(GOORU_CONTENT_SLA.length(), folder.length());
			}
			if (bucketFolder != null && bucketFolder.length() > 4 && !bucketFolder.endsWith("/")) {
				bucketFolder = bucketFolder.trim() + "/";
			}
		} else {
			bucketFolder = S3FileNameParser.getFilePath(asset.getName());
		}

		if (bucketFolder != null && bucketFolder.length() > 1) {
			s3ResourceUrl += bucketFolder;
		}

		S3Object object = null;

		final String bucket = this.getSettingService().getConfigSetting(ConfigConstants.RESOURCE_S3_BUCKET, 0, TaxonomyUtil.GOORU_ORG_UID);
		if (fileName != null) {


			try {
				logger.error("Asset  :"+asset.getAssetId()+" Url try : " + s3ResourceUrl + fileName+DOT_PNG);
				object = getS3Service().getObject(bucket, s3ResourceUrl + fileName + DOT_PNG);
				asset.setName(fileName+DOT_PNG);

			} catch (Exception exception) {

				try {
					logger.error("Asset :"+asset.getAssetId()+" Url try :" + s3ResourceUrl + fileName+DOT_JPG);
					object = getS3Service().getObject(bucket, s3ResourceUrl + fileName + DOT_JPG);
					asset.setName(fileName+DOT_JPG);

				} catch (Exception exception2) {
					
					logger.error("Asset :"+asset.getAssetId()+"shut Url try :" + s3ResourceUrl + fileName+DOT_JPG);
					object = getS3Service().getObject(bucket, s3ResourceUrl + fileName + DOT_JPG);
					asset.setName(fileName+DOT_JPG);

				}
			}

		} else {
			logger.error("Asset :"+asset.getAssetId()+" Url : " + s3ResourceUrl + asset.getName());
			object = getS3Service().getObject(bucket, s3ResourceUrl + asset.getName());
		}

		return IOUtils.toByteArray(object.getDataInputStream());
	}

	public static String getContentType(final String f) {

		String contentType = "";
		String ext = "";
		int dot = f.lastIndexOf(".");
		if (dot != -1){
			ext = f.substring(dot + 1);
		}
		if (ext.equalsIgnoreCase(PNG)) {
			contentType = IMAGE_PNG;
		} else if (ext.equalsIgnoreCase(JPG)) {
			contentType = IMAGE_JPEG;
		} else if (ext.equalsIgnoreCase(JPEG)) {
			contentType = IMAGE_JPEG;
		} else if (ext.equalsIgnoreCase(GIF)) {
			contentType = IMAGE_GIF;
		} else if (ext.equalsIgnoreCase(PDF)) {
			contentType = APPLICATION_PDF;
		} else if (ext.equalsIgnoreCase(SVG)) {
                        contentType = IMAGE_SVG ;
                }


		return contentType;
	}

	/*public Properties getClassPlanConstants() {
		return classPlanConstants;
	}*/

	public RestS3Service getS3Service() {
		return s3Service;
	}

	public SettingService getSettingService() {
		return settingService;
	}
}
