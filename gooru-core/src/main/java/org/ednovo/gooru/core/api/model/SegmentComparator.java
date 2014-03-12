package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Comparator;

import org.ednovo.gooru.core.api.model.Segment;

public class SegmentComparator implements Comparator<Segment>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8642266958286476799L;


	public int compare(Segment segment1, Segment segment2) {
		if (segment2 != null && segment1.getSequence() != null && segment2.getSequence() != null && !segment1.getSegmentId().equals(segment2.getSegmentId())) {
			if (segment1.getSequence().equals(segment2.getSequence())) {
				return 0;
			}
			return segment1.getSequence().compareTo(segment2.getSequence());
		}
		return 0;
	}
}
