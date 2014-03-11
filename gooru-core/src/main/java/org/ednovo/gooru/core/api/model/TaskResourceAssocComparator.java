/*******************************************************************************
 * TaskResourceAssocComparator.java
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
