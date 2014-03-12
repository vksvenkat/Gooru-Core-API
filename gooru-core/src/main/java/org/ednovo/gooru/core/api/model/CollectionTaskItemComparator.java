package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Comparator;


public class CollectionTaskItemComparator  implements  Comparator<CollectionTaskAssoc>,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4823612888532515850L;
	

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(CollectionTaskAssoc item1, CollectionTaskAssoc item2) {
		if (item2 != null && item1.getSequence() != null && item2.getSequence() != null && !item1.getAssociatedBy().equals(item2.getAssociatedBy())) {
			if (item1.getSequence().equals(item2.getSequence())) {
				return 0;
			}
			return item1.getSequence().compareTo(item2.getSequence());
		}
		return 0;
	}

}