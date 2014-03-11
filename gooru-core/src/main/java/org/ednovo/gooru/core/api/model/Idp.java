/*******************************************************************************
 * Idp.java
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

public class Idp implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8057521950467645504L;

	
	public static String DEFAULT_IDP = "NA"; 
	
	private Short idpId;
	private String name;
	private Short gooruInstalled = 0;
	
	public Short getIdpId() {
		return idpId;
	}
	public void setIdpId(Short idpId) {
		this.idpId = idpId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Short getGooruInstalled() {
		return gooruInstalled;
	}
	public void setGooruInstalled(Short gooruInstalled) {
		this.gooruInstalled = gooruInstalled;
	}
	public static String getDEFAULT_IDP() {
		return DEFAULT_IDP;
	}
	public static void setDEFAULT_IDP(String dEFAULTIDP) {
		DEFAULT_IDP = dEFAULTIDP;
	}
}
