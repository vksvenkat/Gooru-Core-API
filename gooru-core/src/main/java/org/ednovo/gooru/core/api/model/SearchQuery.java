/*******************************************************************************
 * SearchQuery.java
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
import java.util.Date;
import java.util.Set;

import org.ednovo.gooru.core.api.model.IndexableEntry;
import org.ednovo.gooru.core.api.model.User;

public class SearchQuery implements IndexableEntry,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3159011800673163322L;

	
	private String queryUId;
	private String query;
	private String userIp;
	private User user;
	private long timeTokenInMillis;
	private long resultCount;
	private String searchType;
	private Date queryTime;
	
	private Set<SearchResult> searchResults;

	public String getQueryUId() {
		return queryUId;
	}

	public void setQueryUId(String queryUId) {
		this.queryUId = queryUId;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	public long getTimeTokenInMillis() {
		return timeTokenInMillis;
	}

	public void setTimeTokenInMillis(long timeTokenInMillis) {
		this.timeTokenInMillis = timeTokenInMillis;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ednovo.gooru.domain.model.IndexableEntry#getEntryId()
	 */
	@Override
	public String getEntryId() {
		return this.queryUId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	public long getResultCount() {
		return resultCount;
	}

	public void setResultCount(long resultCount) {
		this.resultCount = resultCount;
	}

	public Date getQueryTime() {
		return queryTime;
	}

	public void setQueryTime(Date queryTime) {
		this.queryTime = queryTime;
	}

	public Set<SearchResult> getSearchResults() {
		return searchResults;
	}

	public void setSearchResults(Set<SearchResult> searchResults) {
		this.searchResults = searchResults;
	}

}
