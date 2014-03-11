/*******************************************************************************
 * CollectionServiceUtil.java
 *  gooru-core
 *  Created by Gooru on 2014
 *  Copyright (c) 2014 Gooru. All rights reserved.
 *  http://www.goorulearning.org/
 *       
 *  Permission is hereby granted, free of charge, to any 
 *  person obtaining a copy of this software and associated 
 *  documentation. Any one can use this software without any 
 *  restriction and can use without any limitation rights 
 *  like copy,modify,merge,publish,distribute,sub-license or 
 *  sell copies of the software.
 *  
 *  The seller can sell based on the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be   
 *  included in all copies or substantial portions of the Software. 
 * 
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
 *   KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
 *   WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
 *   PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE       AUTHORS 
 *   OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
 *   OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *   OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *   WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 *   THE SOFTWARE.
 ******************************************************************************/
package org.ednovo.gooru.core.application.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceInstance;
import org.ednovo.gooru.core.api.model.ResourceInstanceComparator;
import org.ednovo.gooru.core.api.model.Segment;
import org.ednovo.gooru.core.api.model.SegmentComparator;

public class CollectionServiceUtil {

	public static void resetInstancesSequence(Segment segment) {
		if (segment == null || segment.getResourceInstances() == null) {
			return;
		}
		List<ResourceInstance> resourceInstances = new ArrayList<ResourceInstance>();
		resourceInstances.addAll(segment.getResourceInstances());
		Collections.sort(resourceInstances, new ResourceInstanceComparator());
		int sequence = 0;
		for (ResourceInstance resourceInstance : resourceInstances) {
			resourceInstance.setSequence(++sequence);
		}
		segment.getResourceInstances().clear();
		segment.getResourceInstances().addAll(resourceInstances);
	}

	public static void resetSegmentsSequence(Resource resource) {
		if (resource == null || resource.getResourceSegments() == null) {
			return;
		}
		List<Segment> segmentsList = new ArrayList<Segment>();
		segmentsList.addAll(resource.getResourceSegments());
		Collections.sort(segmentsList, new SegmentComparator());
		int sequence = 0;
		for (Segment segment : segmentsList) {
			segment.setSequence(++sequence);
		}
		resource.getResourceSegments().clear();
		resource.getResourceSegments().addAll(segmentsList);
	}

	public static Segment getCollectionSegment(Learnguide collection, String segmentId) {
		for (Segment segment : collection.getResourceSegments()) {
			if (segment.getSegmentId().equals(segmentId)) {
				return segment;
			}
		}
		return null;
	}

}
