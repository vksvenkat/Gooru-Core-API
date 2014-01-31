/////////////////////////////////////////////////////////////
// TaxonomyNode.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy;

import java.io.Serializable;
import java.util.List;

public class TaxonomyNode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 643668657606992904L;
	private String codeUId;
	private List<TaxonomyNode> node;
	private Integer codeId;
	private Integer order;
	private String taxonomyImageUrl;
	private String code;
	private String label;
	private String type;
	private Integer depth;
	private String displayCode;
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCodeUId() {
		return codeUId;
	}

	public void setCodeUId(String codeUId) {
		this.codeUId = codeUId;
	}

	public String getTaxonomyImageUrl() {
		return taxonomyImageUrl;
	}

	public void setTaxonomyImageUrl(String taxonomyImageUrl) {
		this.taxonomyImageUrl = taxonomyImageUrl;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Integer getDepth() {
		return depth;
	}

	public void setDepth(Integer depth) {
		this.depth = depth;
	}

	public Integer getCodeId() {
		return codeId;
	}

	public void setCodeId(Integer codeId) {
		this.codeId = codeId;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public List<TaxonomyNode> getNode() {
		return node;
	}

	public void setNode(List<TaxonomyNode> node) {
		this.node = node;
	}
	public String getdisplayCode() {
		return displayCode;
	}

	public void setdisplayCode(String displayCode) {
		this.displayCode = displayCode;
	}

}
