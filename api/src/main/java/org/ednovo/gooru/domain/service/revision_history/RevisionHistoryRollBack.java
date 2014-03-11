/*
*RevisionHistoryRollBack.java
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

package org.ednovo.gooru.domain.service.revision_history;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.ednovo.gooru.core.api.model.ContentPermission;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.RevisionHistory;
import org.ednovo.gooru.core.api.model.Versionable;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Search Team
 * 
 */
public abstract class RevisionHistoryRollBack<T extends Versionable> extends BaseRepositoryHibernate {

	@Autowired
	private BaseRepository baseRepository;

	private static final Map<String, RevisionHistoryRollBack<?>> ROLLBACK = new HashMap<String, RevisionHistoryRollBack<?>>();

	public static final <T extends Versionable> RevisionHistoryRollBack<T> getRollBack(String type) {
		return ROLLBACK.containsKey(type) ? (RevisionHistoryRollBack<T>) ROLLBACK.get(type) : null;
	}

	protected abstract RevisionHistoryType getType();

	@PostConstruct
	protected final void register() {
		ROLLBACK.put(getType().getValue(), this);
	}

	protected abstract T rollback(T entity, RevisionHistory history);

	protected abstract T merge(T revisionEntity, T existingEntity);

	protected Resource mergeResource(Resource revisionResource, Resource existingResource) {
		existingResource.setResourceType(revisionResource.getResourceType());
		existingResource.setUrl(revisionResource.getUrl());
		existingResource.setTitle(revisionResource.getTitle());
		existingResource.setLicense(revisionResource.getLicense());
		existingResource.setFolder(revisionResource.getFolder());
		existingResource.setNumberOfSubcribers(revisionResource.getNumberOfSubcribers());
		existingResource.setThumbnail(revisionResource.getThumbnail());
		existingResource.setFileHash(revisionResource.getFileHash());
		existingResource.setDescription(revisionResource.getDescription());
		existingResource.setResourceSource(revisionResource.getResourceSource());
		existingResource.setDistinguish(revisionResource.getDistinguish());
		existingResource.setTags(revisionResource.getTags());
		existingResource.setSiteName(revisionResource.getSiteName());
		existingResource.setBatchId(revisionResource.getBatchId());
		existingResource.setCategory(revisionResource.getCategory());
		existingResource.setIsFeatured(revisionResource.getIsFeatured());
		existingResource.setHasFrameBreaker(revisionResource.getHasFrameBreaker());
		existingResource.setBrokenStatus(revisionResource.getBrokenStatus());
		existingResource.setViews(revisionResource.getViews());
		existingResource.setS3UploadFlag(revisionResource.getS3UploadFlag());
		existingResource.setMediaType(revisionResource.getMediaType());

		mergeContent(revisionResource, existingResource);

		return existingResource;
	}

	private Resource mergeContent(Resource revisionResource, Resource existingResource) {
		existingResource.setGooruOid(revisionResource.getGooruOid());
		existingResource.setSharing(revisionResource.getSharing());
		existingResource.setCreatedOn(revisionResource.getCreatedOn());
		existingResource.setLastModified(revisionResource.getLastModified());
		existingResource.setContentType(revisionResource.getContentType());
		existingResource.setUser(revisionResource.getUser());
		existingResource.setCreator(revisionResource.getCreator());
		Set<ContentPermission> permissions = existingResource.getContentPermissions();
		if (permissions == null) {
			permissions = new HashSet<ContentPermission>();
			existingResource.setContentPermissions(permissions);
		}
		if (revisionResource.getContentPermissions() != null) {
			Iterator<ContentPermission> contentPermissionIterator = revisionResource.getContentPermissions().iterator();
			while (contentPermissionIterator.hasNext()) {
				ContentPermission revisionPermission = contentPermissionIterator.next();
				boolean exist = false;
				for (ContentPermission contentPermission : existingResource.getContentPermissions()) {
					if (contentPermission.getParty().getPartyUid().equalsIgnoreCase(revisionPermission.getParty().getPartyUid())) {
						exist = true;
						break;
					}
				}
				if (!exist) {
					permissions.add(revisionPermission);
				}
			}
			contentPermissionIterator = revisionResource.getContentPermissions().iterator();
			Set<ContentPermission> removeContentPermissions = new HashSet<ContentPermission>();
			while (contentPermissionIterator.hasNext()) {
				ContentPermission revisionPermission = contentPermissionIterator.next();
				for (ContentPermission contentPermission : existingResource.getContentPermissions()) {
					if (!contentPermission.getParty().getPartyUid().equalsIgnoreCase(revisionPermission.getParty().getPartyUid())) {
						permissions.remove(contentPermission);
						removeContentPermissions.add(contentPermission);
						break;
					}
				}
			}

			baseRepository.removeAll(removeContentPermissions);
		}
		existingResource.setOrganization(revisionResource.getOrganization());

		return existingResource;
	}

}
