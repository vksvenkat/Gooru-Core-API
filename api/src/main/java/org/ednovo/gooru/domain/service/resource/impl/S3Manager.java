/////////////////////////////////////////////////////////////
// S3Manager.java
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

import java.util.Properties;

import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class S3Manager extends AbstractS3Manager {

	@Autowired
	@javax.annotation.Resource(name = "contentS3Service")
	private RestS3Service s3Service;

	@Autowired
	@javax.annotation.Resource(name = "s3Constants")
	private Properties s3Constants;

	@Override
	public RestS3Service getS3Service() {
		return s3Service;
	}

	public void setS3Service(RestS3Service s3Service) {
		this.s3Service = s3Service;
	}

	@Override
	public Properties getS3Constants() {
		return s3Constants;
	}
	
	@Autowired
    private SettingService settingService;
	
	@Override
	public String getS3Bucket() {
		return settingService.getConfigSetting(ConfigConstants.S3_GOORU_BUCKET, 0, TaxonomyUtil.GOORU_ORG_UID);
	}
}
