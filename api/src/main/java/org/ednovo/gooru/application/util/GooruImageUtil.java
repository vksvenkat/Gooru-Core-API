/////////////////////////////////////////////////////////////
// GooruImageUtil.java
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
package org.ednovo.gooru.application.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.restlet.data.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleOp;
import com.sun.pdfview.PDFFile;

@Component
public class GooruImageUtil {

	private static GooruImageUtil instance;
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private AsyncExecutor asyncExecutor;

	private static final Logger LOGGER = LoggerFactory.getLogger(GooruImageUtil.class);

	public GooruImageUtil() {
		instance = this;
	}

	public static void cropImage(String path, int x, int y, int width, int height) throws Exception {
		BufferedImage srcImg = ImageIO.read(new File(path));
		srcImg = srcImg.getSubimage(x, y, width, height);
		ImageIO.write(srcImg, "png", new File(path));
	}


	public void scaleImageUsingImageMagick(String srcFilePath, int width, int height, String destFilePath) throws Exception {
		String resizeCommand = new String( "/usr/bin/convert@" + srcFilePath + "@-resize@"+ width + "x" + height + "@" +destFilePath);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("command", resizeCommand);
		this.getAsyncExecutor().executeRestAPI(param, settingService.getConfigSetting(ConfigConstants.GOORU_CONVERSION_RESTPOINT,0, TaxonomyUtil.GOORU_ORG_UID) + "/conversion/image/resize", Method.POST.getName());

	}

	public void cropImageUsingImageMagick(String srcFilePath, int width, int height, int x, int y, String destFilePath) throws Exception {
		String resizeCommand = new String( "/usr/bin/convert"+"@"+ srcFilePath+"@-crop"+"@"+ width + "x" + height + "+" + x + "+" + y+"@"+ destFilePath);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("command", resizeCommand);
		this.getAsyncExecutor().executeRestAPI(param, settingService.getConfigSetting(ConfigConstants.GOORU_CONVERSION_RESTPOINT,0, TaxonomyUtil.GOORU_ORG_UID) + "/conversion/image/resize", Method.POST.getName());
	}
	
	public static void scaleImageUsingResampleOp(String srcFilePath, int width, int height, String destFilePath) throws Exception {
		InputStream in = new ByteArrayInputStream(FileUtils.readFileToByteArray(new File(srcFilePath)));
		BufferedImage originalImage = ImageIO.read(in);
		ResampleOp resampleOp = new ResampleOp(width, height);
		resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal);
		BufferedImage rescaledImage = resampleOp.filter(originalImage, null);
		ImageIO.write(rescaledImage, getFileExtenstion(destFilePath), new File(destFilePath));
	}

	public static void scaleImageUsingCoolbird(ByteArrayInputStream sourceImageStream, int width, int height, String destFilePath) throws Exception {
		ByteArrayOutputStream thumbnailBaos = new ByteArrayOutputStream();
		Thumbnails.of(sourceImageStream).forceSize(width, height).outputFormat(getFileExtenstion(destFilePath)).toOutputStream(thumbnailBaos);
		FileUtils.writeByteArrayToFile(new File(destFilePath), thumbnailBaos.toByteArray());
	}

	public static String downloadWebResourceToFile(String srcUrl, String outputFolderPath, String fileNamePrefix) {
		return downloadWebResourceToFile(srcUrl, outputFolderPath, fileNamePrefix, null);
	}
	
	
	public static String downloadWebResourceToFile(String srcUrl, String outputFolderPath, String fileNamePrefix, String fileExtension) {

		try {
			
			File outputFolder = new File(outputFolderPath);
			URL url = new URL(srcUrl);
			URLConnection urlCon = url.openConnection();
			InputStream inputStream = urlCon.getInputStream();
				if (!outputFolder.exists()) {
					outputFolder.mkdirs();
				}
				
				if (fileExtension == null) {
					fileExtension = getWebFileExtenstion(urlCon.getContentType());
				}
			
				String destFilePath = outputFolderPath + fileNamePrefix + "_" + UUID.randomUUID().toString() +"." + fileExtension;
				File outputFile = new File(destFilePath);
				if (outputFile.exists()) {
					outputFile.delete();
				}
				OutputStream out = new FileOutputStream(outputFile);
				byte buf[] = new byte[1024];
				int len;
				while ((len = inputStream.read(buf)) > 0)
				out.write(buf, 0, len);
				out.close();
				inputStream.close();
				return destFilePath;
		} catch (Exception e) {
			LOGGER.error("DownloadImage failed:exception:", e);
			return null;
		}
	}

	public static ByteArrayInputStream getByteArrayInputStream(String srcFilePath) throws Exception {
		return new ByteArrayInputStream(FileUtils.readFileToByteArray(new File(srcFilePath)));
	}

	public static String getWebFileExtenstion(String contentType) {

		if (contentType.equalsIgnoreCase("image/bmp")) {
			return "bmp";
		} else if (contentType.equalsIgnoreCase("image/png")) {
			return "png";
		} else if (contentType.equalsIgnoreCase("image/gif")) {
			return "gif";
		} else {
			return "jpg";
		}

	}
	
	public static String getFileExtenstion(String filePath) {
		if (filePath != null) {
			if (filePath.contains("?")) {
				filePath = StringUtils.substringBefore(filePath, "?");
			}
			return StringUtils.substringAfterLast(filePath, ".");
		} else {
			return null;
		}
	}

	public static String getFileName(String filePath) {
		if (filePath != null) {
			if (filePath.contains("?")) {
				filePath = StringUtils.substringBefore(filePath, "?");
			}
			return StringUtils.substringAfterLast(filePath, "/");
		} else {
			return null;
		}
	}

	public static String getFileNamePrefix(String filePath) {
		if (filePath != null) {
			if (filePath.contains("/")) {
				filePath = StringUtils.substringAfterLast(filePath, "/");
			}
			if (filePath.contains("?")) {
				filePath = StringUtils.substringBefore(filePath, "?");
			}
			return StringUtils.substringBeforeLast(filePath, ".");
		} else {
			return null;
		}

	}

	public static String moveImage(String srcFilePath, String destFolderPath, String fileNamePrefix) throws IOException {
		File srcFile = new File(srcFilePath);
		String fileExtension = StringUtils.substringAfterLast(srcFilePath, ".");
		File destFile = new File(destFolderPath + fileNamePrefix + "." + fileExtension);
		try {
			if (destFile.exists() && srcFile.exists()) {
				destFile.delete();
			}
			FileUtils.moveFile(srcFile, destFile);
		} catch (IOException exception) {
			LOGGER.error("Move File Failed:" + exception);
			throw exception;
		}
		return destFile.getAbsolutePath();
	}

	public static String copyImage(String srcFilePath, String destFolderPath, String fileNamePrefix) throws IOException {
		File srcFile = new File(srcFilePath);
		String fileExtension = StringUtils.substringAfterLast(srcFilePath, ".");
		File destFile = new File(destFolderPath + fileNamePrefix + "." + fileExtension);
		try {
			if (destFile.exists() && srcFile.exists()) {
				destFile.delete();
			}
			FileUtils.copyFile(srcFile, destFile);
		} catch (IOException exception) {
			LOGGER.error("copy File Failed:" + exception);
			throw exception;
		}
		return destFile.getAbsolutePath();
	}
	
	public static PDFFile getPDFFile(String pdfPath){
		ByteBuffer buf;
		PDFFile pdfFile = null;
		try {
			File file = new File(pdfPath);
			RandomAccessFile accessFile= new RandomAccessFile(file, "r");
			FileChannel channel= accessFile.getChannel();
			buf = channel.map(MapMode.READ_ONLY, 0, channel.size());
			pdfFile = new PDFFile(buf);
		} catch (Exception e) {
			LOGGER.error("getPDFFile: "+e);
		}
		return pdfFile;
		
	}
	public static GooruImageUtil getInstance() {
		return instance;
	}

	public AsyncExecutor getAsyncExecutor() {
		return asyncExecutor;
	}
}
