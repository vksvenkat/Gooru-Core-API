/////////////////////////////////////////////////////////////
// ProfileImageUtil.java
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
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.ednovo.gooru.application.converter.ImageScaler;
import org.ednovo.gooru.core.api.model.Profile;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.resource.impl.S3Manager;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProfileImageUtil implements ParameterProperties {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private S3Manager s3Manager;

	 protected static final Logger logger = LoggerFactory.getLogger(ProfileImageUtil.class);


	public void uploadProfileImage(final Profile profile, String mediaFileName) throws Exception {

		profile.setThumbnailBlob(scaleImage(profile.getPictureBlob(), 158, 158));
		this.getUserRepository().save(profile);
		/*String pictureFmt = mediaFileName != null ? S3Manager.getFileFomrat(mediaFileName) : null;*/
		String pictureFmt = PNG;
		/*profile.setPictureFormat(pictureFmt != null ? pictureFmt : profile.getPictureFormat());*/
		profile.setPictureFormat(pictureFmt);
		this.getUserRepository().save(profile);

		Thread uploadThread = new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					s3Manager.uploadFile(S3Manager.Type.PROFILE, profile.getThumbnailBlob(), profile.getUser().getGooruUId() + "-158x158." + profile.getPictureFormat());
					s3Manager.uploadFile(S3Manager.Type.PROFILE, scaleImage(profile.getPictureBlob(), 40, 40), profile.getUser().getGooruUId() + "-40x40." + profile.getPictureFormat());
					s3Manager.uploadFile(S3Manager.Type.PROFILE, scaleImage(profile.getPictureBlob(), 25, 25), profile.getUser().getGooruUId() + "-25x25." + profile.getPictureFormat());
					s3Manager.uploadFile(S3Manager.Type.PROFILE, scaleImage(profile.getPictureBlob(), 30, 30), profile.getUser().getGooruUId() + "-30x30." + profile.getPictureFormat());
					s3Manager.uploadFile(S3Manager.Type.PROFILE, scaleImage(profile.getPictureBlob(), 48, 48), profile.getUser().getGooruUId() + "-48x48." + profile.getPictureFormat());
					s3Manager.uploadFile(S3Manager.Type.PROFILE, scaleImage(profile.getPictureBlob(), 130, 130), profile.getUser().getGooruUId() + "-130x130." + profile.getPictureFormat());
				} catch (Exception exception) {
					logger.error("S3 Profile Image Upload Failed : " + exception.getMessage());
				}
			}

		});
		uploadThread.setDaemon(true);
		s3Manager.uploadFile(S3Manager.Type.PROFILE, profile.getPictureBlob(), profile.getUser().getGooruUId() + "." + profile.getPictureFormat());
		uploadThread.start();
	}

	public void deleteS3Upload(Profile profile) throws Exception {
		profile.setPictureBlob(null);
		profile.setThumbnailBlob(null);
		s3Manager.deleteFile(S3Manager.Type.PROFILE, profile.getUser().getGooruUId() + ".png");
		s3Manager.deleteFile(S3Manager.Type.PROFILE, profile.getUser().getGooruUId() + "-158x158.png");
		s3Manager.deleteFile(S3Manager.Type.PROFILE, profile.getUser().getGooruUId() + "-40x40.png");
		s3Manager.deleteFile(S3Manager.Type.PROFILE, profile.getUser().getGooruUId() + "-25x25.png");
		s3Manager.deleteFile(S3Manager.Type.PROFILE, profile.getUser().getGooruUId() + "-30x30.png");
		s3Manager.deleteFile(S3Manager.Type.PROFILE, profile.getUser().getGooruUId() + "-48x48.png");
		s3Manager.deleteFile(S3Manager.Type.PROFILE, profile.getUser().getGooruUId() + "-130x130.png");
		profile.setPictureFormat(null);
		this.getUserRepository().save(profile);

	}

	private byte[] scaleImage(byte[] data, int width, int height) throws Exception {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream in = null;
		BufferedImage bImage = null;
		byte[] imageInByte = null;
		ImageScaler processor = new ImageScaler();

		try {

			in = new ByteArrayInputStream(data);
			bImage = ImageIO.read(in);
			bImage = processor.scaleImage(bImage, width, height);

			ImageIO.write(bImage, "png", baos);
			baos.flush();
			imageInByte = baos.toByteArray();
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				if (baos != null) {
					baos.close();
				}
				if (in != null) {
					in.close();
				}
				processor = null;
				bImage = null;
			} catch (Exception exp) {

			}
		}
		return imageInByte;

	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

}
