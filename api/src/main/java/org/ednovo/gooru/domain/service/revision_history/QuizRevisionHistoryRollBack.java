/*
*QuizRevisionHistoryRollBack.java
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

import java.util.HashSet;
import java.util.Set;

import org.ednovo.gooru.core.api.model.Assessment;
import org.ednovo.gooru.core.api.model.AssessmentSegment;
import org.ednovo.gooru.core.api.model.AssessmentSegmentQuestionAssoc;
import org.ednovo.gooru.core.api.model.RevisionHistory;
import org.ednovo.gooru.infrastructure.persistence.hibernate.assessment.AssessmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuizRevisionHistoryRollBack extends RevisionHistoryRollBack<Assessment> {

	@Autowired
	private AssessmentRepository assessmentRepository;

	@Override
	protected RevisionHistoryType getType() {
		return RevisionHistoryType.QUIZ;
	}

	@Override
	protected Assessment rollback(Assessment revisionAssessment, RevisionHistory history) {
		Assessment existingAssessment = assessmentRepository.getByGooruOId(Assessment.class, revisionAssessment.getGooruOid());
		if (existingAssessment == null) {
			for (AssessmentSegment assessmentSegment : revisionAssessment.getSegments()) {
				assessmentSegment.setSegmentId(null);
			}

			revisionAssessment.setContentId(null);
			existingAssessment = revisionAssessment;
		} else {
			existingAssessment = this.merge(revisionAssessment, existingAssessment);

			// Set Assessment Segment
			Set<AssessmentSegment> revisionAssessmentSegments = revisionAssessment.getSegments();

			Set<AssessmentSegment> existingAssessmentSegments = existingAssessment.getSegments();

			Set<AssessmentSegment> removeExistsAssessmentSegments = new HashSet<AssessmentSegment>();

			Set<AssessmentSegment> removeRevisionAssessmentSegments = new HashSet<AssessmentSegment>();

			for (AssessmentSegment existingSegment : existingAssessmentSegments) {
				boolean existAssessmentSegment = false;
				for (AssessmentSegment revisionSegment : revisionAssessmentSegments) {
					if (revisionSegment.getSegmentUId().equals(existingSegment.getSegmentUId())) {
						// existingSegment.setAssessment(revisionSegment.getAssessment());
						existingSegment.setName(revisionSegment.getName());
						existingSegment.setSequence(revisionSegment.getSequence());
						existingSegment.setTimeToCompleteInSecs(revisionSegment.getTimeToCompleteInSecs());
						existAssessmentSegment = true;

						// Set Assessment Segment Question Association
						Set<AssessmentSegmentQuestionAssoc> revisionAssessmentSegmentQuestion = revisionSegment.getSegmentQuestions();

						Set<AssessmentSegmentQuestionAssoc> existingAssessmentSegmentQuestions = existingSegment.getSegmentQuestions();

						Set<AssessmentSegmentQuestionAssoc> removeExistsAssessmentSegmentsQuestion = new HashSet<AssessmentSegmentQuestionAssoc>();

						Set<AssessmentSegmentQuestionAssoc> removeRevisionAssessmentSegmentsQuestion = new HashSet<AssessmentSegmentQuestionAssoc>();

						for (AssessmentSegmentQuestionAssoc existingSegmentQuestion : existingAssessmentSegmentQuestions) {
							boolean existAssessmentSegmentQuestion = false;
							for (AssessmentSegmentQuestionAssoc revisionSegmentQuestion : revisionAssessmentSegmentQuestion) {
								if (revisionSegmentQuestion.getSegment().getSegmentUId().equalsIgnoreCase(existingSegmentQuestion.getSegment().getSegmentUId()) && revisionSegmentQuestion.getQuestion().getGooruOid().equalsIgnoreCase(existingSegmentQuestion.getQuestion().getGooruOid())) {
									existingSegmentQuestion.setSequence(revisionSegmentQuestion.getSequence());
									existAssessmentSegmentQuestion = true;
									removeRevisionAssessmentSegmentsQuestion.add(revisionSegmentQuestion);
									break;
								}
							}
							if (!existAssessmentSegmentQuestion) {
								removeExistsAssessmentSegmentsQuestion.add(existingSegmentQuestion);
							}
						}
						if (removeRevisionAssessmentSegmentsQuestion.size() > 0) {
							revisionAssessmentSegmentQuestion.removeAll(removeRevisionAssessmentSegmentsQuestion);
						}
						if (removeExistsAssessmentSegmentsQuestion.size() > 0) {
							existingAssessmentSegmentQuestions.removeAll(removeExistsAssessmentSegmentsQuestion);
							assessmentRepository.removeAll(removeExistsAssessmentSegmentsQuestion);
						}
						existingAssessmentSegmentQuestions.addAll(revisionAssessmentSegmentQuestion);
						existingSegment.setSegmentQuestions(existingAssessmentSegmentQuestions);
						removeRevisionAssessmentSegments.add(revisionSegment);
						break;
					}

				}

				if (!existAssessmentSegment) {
					removeExistsAssessmentSegments.add(existingSegment);
				}
			}
			if (removeExistsAssessmentSegments.size() > 0) {
				existingAssessmentSegments.removeAll(removeExistsAssessmentSegments);
				assessmentRepository.removeAll(existingAssessmentSegments);
			}
			if (removeRevisionAssessmentSegments.size() > 0) {
				revisionAssessmentSegments.removeAll(removeRevisionAssessmentSegments);
				assessmentRepository.removeAll(revisionAssessmentSegments);
			}

			existingAssessmentSegments.addAll(revisionAssessmentSegments);
			existingAssessment.setSegments(existingAssessmentSegments);
		}
		existingAssessment.setRevisionHistoryUid(history.getRevisionHistoryUid());
		assessmentRepository.save(existingAssessment);
		for (AssessmentSegment assessmentSegment : existingAssessment.getSegments()) {
			assessmentRepository.saveAll(assessmentSegment.getSegmentQuestions());
		}
		return existingAssessment;
	}

	@Override
	protected Assessment merge(Assessment revisionAssessment, Assessment existingAssessment) {
		existingAssessment.setImportCode(revisionAssessment.getImportCode());
		existingAssessment.setName(revisionAssessment.getName());
		existingAssessment.setDescription(revisionAssessment.getDescription());
		existingAssessment.setQuestionCount(revisionAssessment.getQuestionCount());
		existingAssessment.setTimeToCompleteInSecs(revisionAssessment.getTimeToCompleteInSecs());
		existingAssessment.setIsRandom(revisionAssessment.getIsRandom());
		existingAssessment.setIsChoiceRandom(revisionAssessment.getIsChoiceRandom());
		existingAssessment.setShowHints(revisionAssessment.getShowHints());
		existingAssessment.setShowScore(revisionAssessment.getShowScore());
		existingAssessment.setShowCorrectAnswer(revisionAssessment.getShowCorrectAnswer());
		existingAssessment.setGrade(revisionAssessment.getGrade());
		existingAssessment.setMedium(revisionAssessment.getMedium());
		existingAssessment.setLearningObjectives(revisionAssessment.getLearningObjectives());
		existingAssessment.setMetaData(revisionAssessment.getMetaData());
		existingAssessment.setCollaboratorList(revisionAssessment.getCollaboratorList());
		existingAssessment.setSource(revisionAssessment.getSource());
		existingAssessment.setVocabulary(revisionAssessment.getVocabulary());
		existingAssessment.setLinkedCollectionTitle(revisionAssessment.getLinkedAssessmentTitle());
		existingAssessment.setLinkedAssessmentTitle(revisionAssessment.getLinkedAssessmentTitle());
		existingAssessment.setCollectionGooruOid(revisionAssessment.getCollectionGooruOid());
		existingAssessment.setQuizGooruOid(revisionAssessment.getQuizGooruOid());
		existingAssessment.setTaxonomyContentData(revisionAssessment.getTaxonomyContentData());
		mergeResource(revisionAssessment, existingAssessment);
		return existingAssessment;
	}

}
