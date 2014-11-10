/////////////////////////////////////////////////////////////
// TaxonomyStoredProcedure.java
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

import java.util.HashMap;
import java.util.Map;

import org.ednovo.gooru.core.api.model.Code;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;
//import org.ednovo.gooru.domain.model.taxonomy.Code;

public class TaxonomyStoredProcedure extends StoredProcedure {

	private static final String SPROC_NAME = "Get_Gooru_Taxonomy";
	private static final String GOORU_CODE = "gooruCode";
	private static final String CODE_LABEL = "codeLabel";

	private Code code;

	public TaxonomyStoredProcedure() {
		setSql(SPROC_NAME);
		declareParameter(new SqlParameter(GOORU_CODE, java.sql.Types.VARCHAR));
		declareParameter(new SqlInOutParameter(CODE_LABEL, java.sql.Types.VARCHAR));
	}

	public Map execute() {
		Map inputs = new HashMap();
		inputs.put(GOORU_CODE, this.code.getCode());
		inputs.put(CODE_LABEL, "");
		return super.execute(inputs);
	}

	public Code getCode() {
		return code;
	}

	public void setCode(Code code) {
		this.code = code;
	}

}
