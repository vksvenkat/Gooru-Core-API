/////////////////////////////////////////////////////////////
// ResourceParser.java
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

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.lang.StringUtils;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

public class ResourceParser {
	private Tika tika;
	private Parser parser;

	public void setTika(Tika tika) {
		this.tika = tika;
		this.parser = tika.getParser();
	}

	public class TitleAndText {
		public TitleAndText(String title, String text) {
			this.setTitle(title);
			this.setText(text);
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		private String title;
		private String text;
	}

	public TitleAndText getTextAndTitle(String url) {
		ContentHandler textHandler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		try {

			InputStream in = null;
			if (url.startsWith("http://")) {
				URL urlObject = new URL(url);
				URLConnection res = urlObject.openConnection();
				in = res.getInputStream();
			} else {
				in = new FileInputStream(url);
			}

			parser.parse(in, textHandler, metadata, new ParseContext());

		} catch (Exception e) {
			e.toString();
		}

		String title = metadata.get(Metadata.TITLE);
		String text = textHandler.toString().trim().replaceAll("\\s+", " ");
		if (StringUtils.isBlank(title)) {
			title = StringUtils.substring(text, 0, 50);
		}
		return new TitleAndText(title, text);
	}

}
