/////////////////////////////////////////////////////////////
// MediaService.java
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
package org.ednovo.gooru.domain.service.resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.core.api.model.FileMeta;
import org.ednovo.gooru.core.api.model.MediaDTO;
import org.json.JSONObject;

public interface MediaService {
	FileMeta handleFileUpload(String fileName, String imageURL, Map<String, Object> formField, boolean resize, int height, int width) throws FileNotFoundException, IOException;

	String convertHtmltoPdf(JSONObject data);

	String convertJsonToCsv(JSONObject data);

	FileMeta handleFileUpload(MediaDTO mediaDTO, Map<String, Object> formField) throws FileNotFoundException, IOException;
	
	void downloadFile(HttpServletResponse response, String filename, String url);
}
