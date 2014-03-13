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
