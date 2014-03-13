package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Comparator;

import org.ednovo.gooru.core.api.model.TaskResourceAssoc;

public class TaskResourceAssocComparator implements Comparator<TaskResourceAssoc>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1413638300420043893L;


	@Override
	public int compare(TaskResourceAssoc item1, TaskResourceAssoc item2) {
		if (item2 != null && item1.getSequence() != null && item2.getSequence() != null && !item1.getTaskResourceAssocUid().equals(item2.getTaskResourceAssocUid())) {
			if (item1.getSequence().equals(item2.getSequence())) {
				return 0;
			}
			return item1.getSequence().compareTo(item2.getSequence());
		}
		return 0;
	}

}
