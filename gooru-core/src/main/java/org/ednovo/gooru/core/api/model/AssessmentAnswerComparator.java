package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Comparator;

import org.ednovo.gooru.core.api.model.AssessmentAnswer;

public class AssessmentAnswerComparator implements Comparator<AssessmentAnswer>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7198155793967218043L;


	@Override
	public int compare(AssessmentAnswer answer1, AssessmentAnswer answer2) {
		if (answer2 != null && answer1.getSequence() != null && answer2.getSequence() != null && !answer1.getSequence().equals(answer2.getSequence())) {
			if (answer1.getSequence().equals(answer2.getSequence())) {
				return 0;
			}
			return answer1.getSequence().compareTo(answer2.getSequence());
		}
		return 0;
	}

}
