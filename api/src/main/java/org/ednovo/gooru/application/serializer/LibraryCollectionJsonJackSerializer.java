/////////////////////////////////////////////////////////////
// LibraryCollectionJsonJackSerializer.java
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
/**
 * 
 */
package org.ednovo.gooru.application.serializer;

import org.ednovo.gooru.application.serializer.mixin.LibraryCodeMixIn;
import org.ednovo.gooru.application.serializer.mixin.LibraryCollectionMixIn;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.domain.service.search.SearchResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;

/**
 * Provides support to serialize an Object into JSON. Uses Jackson serializer
 * for serializing the object.
 * 
 * @author Search Team
 * @see FilterSetting
 */
public class LibraryCollectionJsonJackSerializer {

	private static ObjectMapper mapper;
	/**
	 * The static attributes and writers needed for serialization are
	 * instantiated. This block of code is called when the class gets loaded.
	 */
	static {
		mapper = new ObjectMapper();
		mapper.configure(MapperFeature.USE_ANNOTATIONS, true);
		mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);

		mapper.addMixInAnnotations(SearchResult.class, LibraryCollectionMixIn.class);
		mapper.addMixInAnnotations(Code.class, LibraryCodeMixIn.class);

		Hibernate4Module hibernate4Module = new Hibernate4Module();
		hibernate4Module.enable(Hibernate4Module.Feature.FORCE_LAZY_LOADING);
		mapper.registerModule(hibernate4Module);
	}

	/**
	 * This class cannot be instantiated
	 */
	private LibraryCollectionJsonJackSerializer() {
	}

	public static <O extends Object> String serialize(O object) throws JsonProcessingException {
		return mapper.writer(getFilterProviderInstance()).writeValueAsString(object);
	}

	private static SimpleFilterProvider getFilterProviderInstance() {
		SimpleFilterProvider filterProvider = new SimpleFilterProvider();
		filterProvider.setFailOnUnknownId(false);
		return filterProvider;
	}
}
