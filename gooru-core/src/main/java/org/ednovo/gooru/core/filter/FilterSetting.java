/*******************************************************************************
 * FilterSetting.java
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
/**
 * 
 */
package org.ednovo.gooru.core.filter;

import java.util.HashMap;
import java.util.Map;

/**
 * Acts as a container for ObjectWriter filter provider
 * 
 * @author Search Team
 * @see JsonJackSerializer
 */
public final class FilterSetting implements Cloneable {

	private Map<String, String[]> includes;

	private Map<String, String[]> excludes;

	private FilterSetting() {
		includes = new HashMap<String, String[]>();
		excludes = new HashMap<String, String[]>();
	}

	public FilterSetting include(String filterName, String[] properties) {
		getIncludes().put(filterName, properties);
		return this;
	}

	public FilterSetting exclude(String filterName, String[] properties) {
		getExcludes().put(filterName, properties);
		return this;
	}

	public Map<String, String[]> getIncludes() {
		return includes;
	}

	public void setIncludes(HashMap<String, String[]> includes) {
		this.includes = includes;
	}

	public Map<String, String[]> getExcludes() {
		return excludes;
	}

	public void setExcludes(HashMap<String, String[]> excludes) {
		this.excludes = excludes;
	}

	public FilterSetting copy() {
		try {
			return (FilterSetting) super.clone();
		} catch (CloneNotSupportedException exception) {
			exception.printStackTrace();
			return null;
		}
	}

	public static FilterSetting newInstance() {
		return new FilterSetting();
	}

}
