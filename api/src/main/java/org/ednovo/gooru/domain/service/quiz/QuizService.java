/////////////////////////////////////////////////////////////
// QuizService.java
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
package org.ednovo.gooru.domain.service.quiz;

import java.util.List;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.Options;
import org.ednovo.gooru.core.api.model.Quiz;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.domain.service.ScollectionService;

public interface QuizService extends ScollectionService {
	
	ActionResponseDTO<Quiz> createQuiz(Quiz quiz, boolean addToMyQuiz,Options options) throws Exception;

	ActionResponseDTO<Quiz>  updateQuiz(String quizId, Quiz newQuiz,Options options) throws Exception;

	Quiz getQuiz(String quizId, User user);
	
	List<Quiz> getQuizList(String quizId, User user);

	List<Quiz> getMyQuizzes(String limit, String offset, User user);

	List<Quiz> getQuizzes(Integer limit, Integer offset,User user);

	void deleteQuiz(String quizId, User user);
	
	void deleteQuizItem(String quizItemId);

	ActionResponseDTO<CollectionItem> createQuizItem(String questionId, String quizId, CollectionItem collectionItem, User user, String type) throws Exception;
	
	ActionResponseDTO<CollectionItem> updateQuizItem(CollectionItem newcollectionItem, String collectionItemId) throws Exception;
	
	ActionResponseDTO<CollectionItem> reorderQuizItem(String collectionItemId, int newSequence) throws Exception;
	
	Quiz copyQuiz(String quizId, Quiz newQuiz, boolean addToMyQuiz, User user) throws Exception;
	
	Options buildOptionsParameter(String data);
}
