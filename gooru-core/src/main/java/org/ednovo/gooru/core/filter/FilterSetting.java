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
