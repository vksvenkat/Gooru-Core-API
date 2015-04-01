/////////////////////////////////////////////////////////////
// BlackListWordCassandraServiceImpl.java
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
package org.ednovo.gooru.domain.cassandra.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.ednovo.gooru.cassandra.core.dao.RawCassandraDao;
import org.ednovo.gooru.core.constant.ColumnFamilyConstant;
import org.ednovo.gooru.domain.cassandra.ApiCassandraFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.application.util.ServerValidationUtils;

import com.netflix.astyanax.model.ColumnList;

@Service
public class BlackListWordCassandraServiceImpl implements BlackListWordCassandraService, ParameterProperties {

	@Autowired
	private ApiCassandraFactory apiCassandraFactory;

	private static List<String> blackListedWords = null;

	private static final String BLACK_LISTED_WORDS_KEY = "black_listed_words";

	private static final String WILD_CARD = "*";

	private static final String[] EXPRESSIONS = { "\\s", "\\s*[^a-zA-Z0-9']+\\s*" };

	@PostConstruct
	public final void init(){
		if (blackListedWords == null || blackListedWords.size() <= 0) {
			reset();
		}
	}
	
	@Override
	public boolean validate(String query) {
		if (blackListedWords == null || blackListedWords.size() <= 0) {
			reset();
		}
		ServerValidationUtils.rejectIfNull(query, GL0006, PROFANITY_TEXT);
		if (query != null && !query.equals(WILD_CARD)) {
			for (String expression : EXPRESSIONS) {
				String[] blackWords = query.split(expression);
				for (String blackWord : blackWords) {
					blackWord = blackWord.trim();
					if (blackListedWords.contains(blackWord.toLowerCase())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public synchronized void reset() {
		blackListedWords = new ArrayList<String>();
		ColumnList<String> blackListWords = getDao().read(BLACK_LISTED_WORDS_KEY); 
		if (blackListWords != null) {
			blackListedWords.addAll(blackListWords.getColumnNames());
		}
	}

	@Override
	public void save(List<String> words) {
		if (words != null && words.size() > 0) {
			Map<String, Object> wordMap = new HashMap<String, Object>();
			final String EMPTY = "";
			for (String word : words) {
				wordMap.put(word.toLowerCase(), EMPTY);
			}
			getDao().save(BLACK_LISTED_WORDS_KEY, wordMap);
			reset();
		}
	}

	@Override
	public void delete(String badword) {
		getDao().delete(BLACK_LISTED_WORDS_KEY, badword);
		reset();
	}

	public Collection<String> read() {
		blackListedWords = new ArrayList<String>();
		ColumnList<String> blackListWords = getDao().read(BLACK_LISTED_WORDS_KEY);
		return blackListWords.getColumnNames();
	}

	private RawCassandraDao getDao() {
		return (RawCassandraDao) apiCassandraFactory.get(ColumnFamilyConstant.DATA_STORE);
	}
}
