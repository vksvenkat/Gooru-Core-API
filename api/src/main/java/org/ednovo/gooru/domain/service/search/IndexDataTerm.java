/////////////////////////////////////////////////////////////
// IndexDataTerm.java
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
package org.ednovo.gooru.domain.service.search;

public class IndexDataTerm {
	private String fieldName;
	private String fieldContent;
	private Object fieldContentObject;
	private boolean fieldStore;
	private boolean fieldAnalyzed;
	private float fieldBoost;
	private boolean fieldMapping = false;

	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFieldContent() {
		return fieldContent;
	}
	public void setFieldContent(String fieldContent) {
		this.fieldContent = fieldContent;
	}
	public boolean isFieldStore() {
		return fieldStore;
	}
	public void setFieldStore(boolean fieldStore) {
		this.fieldStore = fieldStore;
	}
	public boolean isFieldAnalyzed() {
		return fieldAnalyzed;
	}
	public void setFieldAnalyzed(boolean fieldAnalyzed) {
		this.fieldAnalyzed = fieldAnalyzed;
	}
	public float getFieldBoost() {
		return fieldBoost;
	}
	public void setFieldBoost(float fieldBoost) {
		this.fieldBoost = fieldBoost;
	}
	public Object getFieldContentObject() {
		return fieldContentObject;
	}
	public void setFieldContentObject(Object fieldContentObject) {
		this.fieldContentObject = fieldContentObject;
	}

	public boolean isFieldMapping() {
		return fieldMapping;
	}
	public void setFieldMapping(boolean fieldMapping) {
		this.fieldMapping = fieldMapping;
	}	
}
