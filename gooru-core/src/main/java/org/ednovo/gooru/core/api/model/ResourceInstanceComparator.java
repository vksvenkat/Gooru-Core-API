package org.ednovo.gooru.core.api.model;

import java.util.Comparator;

import org.ednovo.gooru.core.api.model.ResourceInstance;

public class ResourceInstanceComparator implements Comparator<ResourceInstance> {

	public int compare(ResourceInstance resourceInstance1, ResourceInstance resourceInstance2) {
		if (resourceInstance2 != null && resourceInstance1.getSequence() != null && resourceInstance2.getSequence() != null && !resourceInstance1.equals(resourceInstance2)) {
			if (resourceInstance1.getSequence().equals(resourceInstance2.getSequence())) {
				resourceInstance2.setSequence(resourceInstance2.getSequence() + 1);
				return -1;
			}
			return resourceInstance1.getSequence().compareTo(resourceInstance2.getSequence());
		}
		return 0;
	}
}
