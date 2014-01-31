/////////////////////////////////////////////////////////////
// SegmentService.java
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
package org.ednovo.gooru.domain.service.segment;

import java.util.List;

import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.ResourceInstance;
import org.ednovo.gooru.core.api.model.Segment;
import org.ednovo.gooru.core.api.model.User;

public interface SegmentService {
	ResourceInstance findSegmentResource(String segmentId, String gooruResourceId);

	List<Segment> getSegments(String gooruContentId);

	List<ResourceInstance> listResourceInstances(String gooruContentId, String type);

	List<ResourceInstance> listSegmentResourceInstances(String segmentId);

	ResourceInstance getFirstResourceInstanceOfResource(String gooruContentId);

	void updateSegment(String gooruContentId, String segmentId, String title, String duration, String type, String rendition, String description, String concept, String uploadedImageSrc, User user, Learnguide collection);

	Segment createSegment(String gooruContentId, String format, String title, String rendition, String duration, String description, String type, Learnguide collection, User user);
}
