package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Comparator;

public class AssessmentHintComparator implements Comparator<AssessmentHint>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4355182908703200089L;


	@Override
	public int compare(AssessmentHint hint1, AssessmentHint hint2) {
		if (hint2 != null && hint1.getSequence() != null && hint2.getSequence() != null && !hint1.getSequence().equals(hint2.getSequence())) {
			if (hint1.getSequence().equals(hint2.getSequence())) {
				return 0;
			}
			return hint1.getSequence().compareTo(hint2.getSequence());
		}
		return 0;
	}

}
