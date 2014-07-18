/////////////////////////////////////////////////////////////
// ImageScaler.java
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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.ednovo.gooru.core.constant.ParameterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleOp;

@Deprecated
public class ImageScaler implements ImageProcessor,ParameterProperties {

	public static final int THUMBNAIL_WIDTH = 80;
	public static final int THUMBNAIL_HEIGHT = 60;

	protected final static Logger LOGGER = LoggerFactory.getLogger(ImageScaler.class);

	public void process(String imgResourceFilePath) throws Exception {
		String fileFolder = imgResourceFilePath.substring(0, imgResourceFilePath.lastIndexOf("/") + 1);
		String targetFilePath = fileFolder + "slides/thumbnail.jpg";
		scale(imgResourceFilePath, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, targetFilePath);
	}

	private void saveImage(String targetFilePath, BufferedImage image, String imageFormat) {
		String fileFolder = targetFilePath.substring(0, targetFilePath.lastIndexOf("/") + 1);
		File dir = new File(fileFolder);
		if (!dir.exists()){
			dir.mkdirs();
		}
		File outputFile = new File(targetFilePath);
		try {
			ImageIO.write(image, PNG , outputFile);
		} catch (IOException e) {
			LOGGER.error("Error while saving image resource to disk", e);
		}
	}

	public static BufferedImage googleImageConverter(BufferedImage originalImage) throws Exception {
		ResampleOp resampleOp = new ResampleOp(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
		resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal);
		BufferedImage rescaledImage = resampleOp.filter(originalImage, null);

		return rescaledImage;

	}

	public static BufferedImage scaleImage(BufferedImage originalImage, int width, int height) throws Exception {
		ResampleOp resampleOp = new ResampleOp(width, height);
		resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal);
		return resampleOp.filter(originalImage, null);

	}

	public static void cropImage(String path, int x, int y, int width, int height) throws Exception {
		BufferedImage srcImg = ImageIO.read(new File(path));
		srcImg = srcImg.getSubimage(x, y, width, height);
		ImageIO.write(srcImg, PNG , new File(path));
	}

	public static byte[] cropImage(byte[] data, String ext, int x, int y, int width, int height) throws Exception {
		InputStream in = new ByteArrayInputStream(data);
		BufferedImage srcImg = ImageIO.read(in);
		srcImg = srcImg.getSubimage(x, y, width, height);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(srcImg, ext, baos);
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();
		return imageInByte;
	}

	public static byte[] scaleProfilePicture(byte[] data, String ext, int width, int height) throws Exception {
		InputStream in = new ByteArrayInputStream(data);
		BufferedImage originalImage = ImageIO.read(in);
		ResampleOp resampleOp = new ResampleOp(width, height);
		resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal);
		BufferedImage rescaledImage = resampleOp.filter(originalImage, null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(rescaledImage, ext, baos);
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();
		return imageInByte;

	}


	@Override
	public void scale(String filePath, int width, int height, String targetFilePath) throws Exception {
		BufferedImage originalImage = ImageIO.read(new File(filePath));
		ResampleOp resampleOp = new ResampleOp(width, height);
		resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal);
		BufferedImage rescaledImage = null;
		try {
			rescaledImage = resampleOp.filter(originalImage, null);
		} catch (Exception e) {
			resampleOp = new ResampleOp(width, height);
			rescaledImage = resampleOp.filter(originalImage, null);
		}

		saveImage(targetFilePath, rescaledImage, JPG);
	}
	
}
