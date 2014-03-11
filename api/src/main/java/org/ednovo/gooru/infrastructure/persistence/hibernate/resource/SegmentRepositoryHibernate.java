/*
*SegmentRepositoryHibernate.java
* gooru-api
* Created by Gooru on 2014
* Copyright (c) 2014 Gooru. All rights reserved.
* http://www.goorulearning.org/
*      
* Permission is hereby granted, free of charge, to any 
* person obtaining a copy of this software and associated 
* documentation. Any one can use this software without any 
* restriction and can use without any limitation rights 
* like copy,modify,merge,publish,distribute,sub-license or 
* sell copies of the software.
* The seller can sell based on the following conditions:
* 
* The above copyright notice and this permission notice shall be   
* included in all copies or substantial portions of the Software. 
*
*  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
*  KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
*  WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
*  PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
*  OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
*  OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
*  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
*  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
*  THE SOFTWARE.
*/

package org.ednovo.gooru.infrastructure.persistence.hibernate.resource;

import java.util.List;

import org.ednovo.gooru.core.api.model.ResourceInstance;
import org.ednovo.gooru.core.api.model.Segment;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.springframework.stereotype.Repository;

@Repository
public class SegmentRepositoryHibernate extends BaseRepositoryHibernate implements SegmentRepository {

	@Override
	public ResourceInstance findSegmentResource(String segmentId, String gooruResourceId) {
		String hql = "SELECT resourceInstance FROM ResourceInstance resourceInstance  WHERE resourceInstance.resource.gooruOid = '" + gooruResourceId + "' AND resourceInstance.segment.segmentId = '" + segmentId + "' AND " +generateAuthQueryWithDataNew("resourceInstance.resource.") ;
		return getRecord(hql);
	}

	private <T> T getRecord(String hql) {
		List list = find(hql);
		return (list != null && list.size() > 0) ? (T) list.get(0) : null;
	}

	@Override
	public List<Segment> getSegments(String gooruContentId) {
		String hql = "SELECT resource.resourceSegments FROM Resource resource   WHERE resource.gooruOid = '" + gooruContentId + "'AND "+generateAuthQueryWithDataNew("resource.") ;
		return find(hql);
	}

	@Override
	public List<ResourceInstance> listResourceInstances(String gooruContentId,String type) {
		String hql = "SELECT resourceSegment.resourceInstances FROM Resource resource JOIN resource.resourceSegments resourceSegment  WHERE resource.gooruOid = '" + gooruContentId + "' ";
		if (type != null) {
			hql += " AND resource.resourceType.name = '" + type + "'";
		}
		hql += " AND "+generateAuthQueryWithDataNew("resource.");
		
		return find(hql);
	}

	@Override
	public List<ResourceInstance> listSegmentResourceInstances(String segmentId) {
		String hql = "SELECT resourceInstance FROM ResourceInstance resourceInstance   WHERE resourceInstance.segment.segmentId = '" + segmentId + "' AND "+generateAuthQueryWithDataNew("resourceInstance.resource.")+"  order by resourceInstance.sequence";
		return find(hql);
	}

	@Override
	public ResourceInstance getFirstResourceInstanceOfResource(String gooruContentId) {
		String hql = "SELECT resourceInstance FROM Resource learnguide, Segment segment, ResourceInstance resourceInstance   WHERE learnguide.gooruOid = '"
				+ gooruContentId
				+ "' AND learnguide.resourceSegments.sequence = 4 AND learnguide.resourceSegments.segmentId = segment.segmentId AND resourceInstance = segment.resourceInstances AND resourceInstance.sequence = 1 AND "+generateAuthQueryWithDataNew("learnguide.");
		List<ResourceInstance> resources = find(hql);
		return (resources.size() != 0) ? resources.get(0) : null;
	}
}
