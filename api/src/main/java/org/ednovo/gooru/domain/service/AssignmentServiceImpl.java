/////////////////////////////////////////////////////////////
// AssignmentServiceImpl.java
// gooru-api
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.domain.service;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Assignment;
import org.ednovo.gooru.core.api.model.Classpage;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentAssociation;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.ShelfType;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.classpage.ClasspageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class AssignmentServiceImpl extends ScollectionServiceImpl implements ParameterProperties,AssignmentService {

	@Autowired
	private ClasspageService classpageService;
	
	@Override
	public ActionResponseDTO<Assignment> createAssignment(Assignment assignment, String collectionId, String taxonomyCode, boolean updateTaxonomyByCode, String classpageId) throws Exception {
		Errors errors = validateAssignment(assignment);
		if (!errors.hasErrors()) {
			Classpage classpage = this.getCollectionRepository().getClasspageByGooruOid(classpageId,  assignment.getUser().getGooruUId());
			if (classpage != null && classpage.getCollectionType().equalsIgnoreCase(CollectionType.CLASSPAGE.getCollectionType())) {
				if (taxonomyCode != null) {
					addCollectionTaxonomy(assignment, taxonomyCode, updateTaxonomyByCode);
				}
					this.getCollectionRepository().save(assignment.getTrackActivity());
					this.getCollectionRepository().save(assignment);
					if (collectionId != null && !collectionId.isEmpty()) {
						CollectionItem collectionItem = new CollectionItem();
						collectionItem.setItemType(ShelfType.AddedType.ADDED.getAddedType());
						collectionItem = this.addCollectionInAssignment(collectionId, assignment.getGooruOid(), collectionItem, assignment.getUser(), CollectionType.COLLECTION.getCollectionType()).getModel();
						Set<CollectionItem> collectionItems = new TreeSet<CollectionItem>();
						collectionItems.add(collectionItem);
						assignment.setCollectionItems(collectionItems);
					}
					this.getCollectionRepository().save(assignment);
					this.getClasspageService().createClasspageItem(assignment.getGooruOid(), classpage.getGooruOid(), new CollectionItem(), assignment.getUser(), CollectionType.CLASSPAGE.getCollectionType());
			} else { 
				throw new Exception("invalid classpage - "  + classpageId);
			}
		}
		return new ActionResponseDTO<Assignment>(assignment, errors);
	}

	@Override
	public ActionResponseDTO<CollectionItem> addCollectionInAssignment(String resourceGooruOid, String assignmentGooruOid, CollectionItem collectionItem, User user, String type) throws Exception {
		Assignment assignment = this.getAssignmentByGooruOid(assignmentGooruOid, null);
		collectionItem.setItemType(ShelfType.AddedType.ADDED.getAddedType());
		Collection collection = this.getCollectionRepository().getCollectionByGooruOid(resourceGooruOid, assignment.getUser().getGooruUId());
			Errors errors = validateCollectionItem(assignment, collection, collectionItem);
			if(collection != null && collection.getCollectionType().equalsIgnoreCase(CollectionType.COLLECTION.getCollectionType())){
				if (!errors.hasErrors()) {
					collectionItem.setCollection(assignment);
					collectionItem.setResource(collection);
					int sequence = collectionItem.getCollection().getCollectionItems() != null ? collectionItem.getCollection().getCollectionItems().size() + 1 : 1;
					collectionItem.setItemSequence(sequence);
					this.getCollectionRepository().save(collectionItem);
				}
			} else {
				throw new Exception("invalid collection - " + resourceGooruOid);
			}
		
		return new ActionResponseDTO<CollectionItem>(collectionItem, errors);
	}

	@Override
	public ActionResponseDTO<Assignment> updateAssignment(Assignment newAssignment, String updateAssignmentGooruOid, String taxonomyCode, String ownerUId, String creatorUId, boolean hasUnrestrictedContentAccess, String relatedContentId, boolean updateTaxonomyByCode) throws Exception {
		Assignment assignment = this.getAssignmentByGooruOid(updateAssignmentGooruOid, null);
		Errors errors = validateUpdateCollection(assignment, newAssignment);
		if (!errors.hasErrors()) {
			if (relatedContentId != null) {
				Content assocContent = this.getContentRepositoryHibernate().findContentByGooruId(relatedContentId);
				Content content = this.getContentRepositoryHibernate().findContentByGooruId(updateAssignmentGooruOid);
				if (assocContent != null && content != null) {
					ContentAssociation contentAssoc = new ContentAssociation();
					contentAssoc.setAssociateContent(assocContent);
					contentAssoc.setContent(content);
					contentAssoc.setModifiedDate(new Date());
					contentAssoc.setUser(assignment.getUser());
					contentAssoc.setTypeOf(RELATED_CONTENT);
					this.getContentRepositoryHibernate().save(contentAssoc);
					assignment.setContentAssociation(contentAssoc);
				}

			}

			if (taxonomyCode != null) {
				addCollectionTaxonomy(assignment, taxonomyCode, updateTaxonomyByCode);
				assignment.setTaxonomySetMapping(TaxonomyUtil.getTaxonomyByCode(assignment.getTaxonomySet(), taxonomyService));
			}
			if (newAssignment.getTrackActivity().getEndTime() != null) {
				assignment.getTrackActivity().setEndTime(newAssignment.getTrackActivity().getEndTime());
			}

			if (newAssignment.getVocabulary() != null) {
				assignment.setVocabulary(newAssignment.getVocabulary());
			}

			if (newAssignment.getTitle() != null) {
				assignment.setTitle(newAssignment.getTitle());
			}
			if (newAssignment.getDescription() != null) {
				assignment.setDescription(newAssignment.getDescription());
			}
			if (newAssignment.getNarrationLink() != null) {
				assignment.setNarrationLink(newAssignment.getNarrationLink());
			}
			if (newAssignment.getEstimatedTime() != null) {
				assignment.setEstimatedTime(newAssignment.getEstimatedTime());
			}
			if (newAssignment.getNotes() != null) {
				assignment.setNotes(newAssignment.getNotes());
			}
			if (newAssignment.getGoals() != null) {
				assignment.setGoals(newAssignment.getGoals());
			}
			if (newAssignment.getKeyPoints() != null) {
				assignment.setGoals(newAssignment.getKeyPoints());
			}
			if (newAssignment.getLanguage() != null) {
				assignment.setLanguage(newAssignment.getLanguage());
			}
			if (newAssignment.getGrade() != null) {
				assignment.setGrade(newAssignment.getGrade());
			}
			if (newAssignment.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || newAssignment.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()) || newAssignment.getSharing().equalsIgnoreCase(Sharing.ANYONEWITHLINK.getSharing())) {
				assignment.setSharing(newAssignment.getSharing());
			}
			if (hasUnrestrictedContentAccess) {
				if (creatorUId != null) {
					User user = getUserService().findByGooruId(creatorUId);
					assignment.setCreator(user);
				}
				if (ownerUId != null) {
					User user = getUserService().findByGooruId(ownerUId);
					assignment.setUser(user);
				}
			}
			if (newAssignment.getLastUpdatedUserUid() != null) {
				assignment.setLastUpdatedUserUid(newAssignment.getLastUpdatedUserUid());
			}

			this.getCollectionRepository().save(assignment);
		}
		return new ActionResponseDTO<Assignment>(assignment, errors);

	}

	@Override
	public List<Assignment> getAssignments(Map<String, String> filters, User user) {
		return this.getCollectionRepository().getAssignments(filters, user);
	}

	@Override
	public void deleteAssignment(String assignmentId) {
		Assignment assignment = this.getAssignmentByGooruOid(assignmentId, null);
		if (assignment != null) {
			this.getCollectionRepository().remove(Assignment.class, assignment.getContentId());
		}
	}

	@Override
	public void deleteAssignmentCollectionItem(String collectionItemId) {
		CollectionItem collectionItem = this.getCollectionRepository().getCollectionItemById(collectionItemId);
		if (collectionItem != null) {
			Collection collection = collectionItem.getCollection();
			this.getCollectionRepository().remove(CollectionItem.class, collectionItem.getCollectionItemId());
			reOrderCollectionItems(collection, collectionItemId);
		}
	}

	@Override
	public Assignment getAssignmentByGooruOid(String gooruOid, String gooruUid) {
		return getCollectionRepository().getAssignmentByGooruOid(gooruOid, gooruUid);
	}

	private Errors validateAssignment(Assignment assignment) throws Exception {
		Map<String, String> colletionType = new HashMap<String, String>();
		colletionType.put(ASSIGNMENT, COLLECTION_TYPE);
		final Errors errors = new BindException(assignment, ASSIGNMENT);
		if (assignment != null) {
			rejectIfNullOrEmpty(errors, assignment.getTitle(), TITLE, GL0006, generateErrorMessage(GL0006, TITLE));
			rejectIfInvalidType(errors, assignment.getCollectionType(),COLLECTION_TYPE,GL0007, generateErrorMessage(GL0007,COLLECTION_TYPE), colletionType);
			rejectIfInvalidDate(errors, assignment.getTrackActivity().getEndTime(), TRACK_ACTIVITY_END_TIME, GL0007, generateErrorMessage(GL0007,_ASSIGNMENT_DUE_DATE));
		}
		return errors;
	}

	private Errors validateCollectionItem(Assignment assignment, Resource resource, CollectionItem collectionItem) throws Exception {
		Map<String, String> itemType = new HashMap<String, String>();
		itemType.put(ADDED,COLLECTION_ITEM_TYPE);
		itemType.put(SUBSCRIBED,COLLECTION_ITEM_TYPE);
		final Errors errors = new BindException(collectionItem,COLLECTION_ITEM );
		if (collectionItem != null) {
			rejectIfNull(errors, assignment,  ASSIGNMENT, GL0056, generateErrorMessage(GL0056, ASSIGNMENT));
			rejectIfNull(errors, resource, RESOURCE, GL0056, generateErrorMessage(GL0056, RESOURCE));
			rejectIfInvalidType(errors, collectionItem.getItemType(), ITEM_TYPE, GL0007, generateErrorMessage(GL0007, ITEM_TYPE), itemType);
		}
		return errors;
	}

	private Errors validateUpdateCollection(Assignment assignment, Assignment newAssignment) throws Exception {
		final Errors errors = new BindException(assignment,  ASSIGNMENT);
		rejectIfNull(errors, assignment,  ASSIGNMENT, GL0006, generateErrorMessage(GL0006, ASSIGNMENT));
		rejectIfInvalidDate(errors, newAssignment.getTrackActivity().getEndTime(), "trackActivity.endTime", GL0007, generateErrorMessage(GL0007, ASSIGNMENT_DUE_DATE));
		return errors;
	}

	public ClasspageService getClasspageService() {
		return classpageService;
	}
}
