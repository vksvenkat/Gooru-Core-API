/////////////////////////////////////////////////////////////
// CollectionSerializerConstants.java
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
package org.ednovo.gooru.domain.service.revision_history;


public interface CollectionSerializerConstants extends ResourceSerializerConstants {
	
	// Collection
	String COLLECTION_TITLE = "title";
	String COLLECTION_TYPE = "collection_type";
	String COLLECTION_NARRATION_LINK = "narration_Link";
	String COLLECTION_NOTES = "notes";
	String COLLECTION_KEY_POINTS = "key_points";
	String COLLECTION_LANGUAGE = "language";
	String COLLECTION_GOALS = "goals";
	String COLLECTION_ESTIMATED_TIME = "estimated_time";
	
	//Collection item
	String COLLECTION_ITEM = "collection_item";
	String COLLECTION_ITEM_ID = "collection_item_id";
	String COLLECTION_ITEM_TYPE = "item_type";
	String COLLECTION_ITEM_SEQUENCE = "item_sequence";
	String COLLECTION_ITEM_NARRATION = "narration";
	String COLLECTION_ITEM_NARRATION_TYPE = "narration_type";
	String COLLECTION_ITEM_START = "start";
	String COLLECTION_ITEM_STOP = "stop";
	String COLLECTION_ITEM_DOCUMENT_ID = "document_id";
	String COLLECTION_ITEM_DOCUMENT_KEY= "document_key";
	

}
