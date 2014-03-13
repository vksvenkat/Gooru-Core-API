
package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Comparator;

/**
 * @author BambeeQ Solutions Pvt. Ltd
 *
 */
public class CollectionItemComparator implements Comparator<CollectionItem>,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8363094113265038743L;


	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(CollectionItem item1, CollectionItem item2) {
		if (item2 != null && item1.getItemSequence() != null && item2.getItemSequence() != null && !item1.getCollectionItemId().equals(item2.getCollectionItemId())) {
			if (item1.getItemSequence().equals(item2.getItemSequence())) {
				return 0;
			}
			return item1.getItemSequence().compareTo(item2.getItemSequence());
		}
		return 0;
	}

}