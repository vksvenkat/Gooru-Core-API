/**
 * 
 */
package org.ednovo.gooru.core.cassandra.model;

import java.util.HashMap;

/**
 * @author SearchTeam
 *
 */
public class ReverseIndexColumnSetting extends HashMap<String, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4032338042471922745L;
	
	public ReverseIndexColumnSetting putField(String key, String value) {
		super.put(key, value);
		return this;
	}

}
