/*
*DatabaseUtil.java
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

package org.ednovo.gooru.application.util;

import org.ednovo.gooru.infrastructure.persistence.hibernate.classplan.LearnguideRepository;
import org.hibernate.Criteria;

public class DatabaseUtil {

	private LearnguideRepository classplanRepository;

	public static String format(String inputString, Object... strings) {
		for (int i = 0; i < strings.length; i++) {
			/*
			 * if(strings[i] instanceof String) { strings[i] =
			 * ((String)strings[i]).replace("'","''"); }
			 */
		}
		return String.format(inputString, strings);

	}

	public LearnguideRepository getClassplanRepository() {
		return classplanRepository;
	}

	public void setClassplanRepository(LearnguideRepository classplanRepository) {
		this.classplanRepository = classplanRepository;
	}

	public static String getSQLGivenCriteria(Criteria query) {
		String sql = null;
		/*
		 * try { CriteriaImpl c = (CriteriaImpl) query; SessionImpl s =
		 * (SessionImpl) c.getSession(); SessionFactoryImplementor factory =
		 * (SessionFactoryImplementor) s.getSessionFactory(); String[]
		 * implementors = factory.getImplementors(c.getEntityOrClassName());
		 * CriteriaLoader loader = new CriteriaLoader((OuterJoinLoadable)
		 * factory.getEntityPersister(implementors[0]), factory, c,
		 * implementors[0], s.getEnabledFilters()); Field f =
		 * OuterJoinLoader.class.getDeclaredField("sql"); f.setAccessible(true);
		 * sql = (String) f.get(loader); } catch (IllegalArgumentException e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); } catch
		 * (IllegalAccessException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (SecurityException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } catch
		 * (NoSuchFieldException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		return sql;
	}
}
