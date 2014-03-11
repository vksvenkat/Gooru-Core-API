/*
*Paginator.java
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

package org.ednovo.gooru.domain.service.search;


public class Paginator {
	
	    private int start;
	    private int end; 
    	
    	public Paginator(){}
    	
		public Paginator(int totalHits, int pageNumber, int pageSize) {
			
			if (totalHits < 1 || pageNumber < 1 || pageSize < 1) {
					this.setStart(0);
					this.setEnd(0);
			}
			else{
				int start = 1 + (pageNumber - 1) * pageSize;
				int end = Math.min(pageNumber * pageSize, totalHits);
				if (start > end) {
					start = Math.max(1, end - pageSize);
				}
				
				this.setStart(start);
				this.setEnd(end);
			}
		}

		/**
		 * 18 totalHits, pageSize 5
		 *
		 * 1: 1-5
		 * 2: 6-10
		 * 3: 11-15
		 * 4: 16-18
		 *
		 * @param totalHits
		 * @param pageNumber
		 * @param pageSize
		 * @return
		 */
		public ArrayLocation calculateArrayLocation(int totalHits, int pageNumber, int pageSize) {
				ArrayLocation al = new ArrayLocation();
				
				if (totalHits < 1 || pageNumber < 1 || pageSize < 1) {
						al.setStart(0);
						al.setEnd(0);
						return al;
				}
				
				int start = 1 + (pageNumber - 1) * pageSize;
				int end = Math.min(pageNumber * pageSize, totalHits);
				if (start > end) {
						start = Math.max(1, end - pageSize);
				}
				
				al.setStart(start);
				al.setEnd(end);
				return al;
		}

		public int getStart() {
			return start;
		}

		public void setStart(int start) {
			this.start = start;
		}

		public int getEnd() {
			return end;
		}

		public void setEnd(int end) {
			this.end = end;
		}
}
	
