/////////////////////////////////////////////////////////////
// BaseRepository.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Data Access Object (Dao) interface. This is an interface used to tag our Dao
 * classes and to provide common methods to all Daos.
 * 
 * <p>
 * <a href="Dao.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public interface BaseRepository {

	/**
	 * Generic method used to get all objects of a particular type. This is the
	 * same as lookup up all rows in a table.
	 * 
	 * @param clazz
	 *            the type of objects (a.k.a. while table) to get data from
	 * @return List of populated objects
	 */
	@SuppressWarnings("rawtypes")
	List getAll(Class<?> clazz);

	@SuppressWarnings("rawtypes")
	List getPagedResults(Class<?> clazz, int page, int recordPerPage);

	/**
	 * Generic method to get an object based on class and identifier. An
	 * ObjectRetrievalFailureException Runtime Exception is thrown if nothing is
	 * found.
	 * 
	 * @param clazz
	 *            model class to lookup
	 * @param id
	 *            the identifier (primary key) of the class
	 * @return a populated object
	 * @see org.springframework.orm.ObjectRetrievalFailureException
	 */
	Object get(Class<?> clazz, Serializable id);

	Object get(Class<?> clazz, Serializable id, String... organizationUid);

	/**
	 * Generic method to save an object - handles both update and insert.
	 * 
	 * @param o
	 *            the object to save
	 */
	void save(Object o);

	/**
	 * Generic method to delete an object based on class and id
	 * 
	 * @param clazz
	 *            model class to lookup
	 * @param id
	 *            the identifier (primary key) of the class
	 */
	void remove(Class<?> clazz, Serializable id);

	/**
	 * Generic method to delete an object based on class and id
	 * 
	 * @param clazz
	 *            model class to lookup
	 * @param id
	 *            the identifier (primary key) of the class
	 */
	void remove(Object module);

	/**
	 * Generic method to delete a collection
	 * 
	 * @param collection
	 *            of Object
	 */
	void removeAll(Collection<?> entities);

	void saveAll(Collection<?> entities);

	void flush();

	void clear();
}
