/*
*GoogleCalendarService.java
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

package org.ednovo.gooru.infrastructure.google;

import java.net.MalformedURLException;
import java.net.URL;

import com.google.gdata.client.GoogleService;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.HtmlTextConstruct;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.extensions.When;

public class GoogleCalendarService extends GoogleAppService{
	
	public void setUp(){
		GoogleService service = new CalendarService("Ednovo-Gooru-v1");
		super.setService(service);
		super.authorize();
	}

	public void scheduleClasplan(String classplanId, String title, String email,  DateTime startTime, DateTime endTime, String description){
		
		setUp();
		String feedUrlString = (new StringBuilder("https://www.google.com/calendar/feeds/")).append(email).append("/private/full").toString();
		feedUrlString = (new StringBuilder(String.valueOf(feedUrlString))).append("?xoauth_requestor_id=").append(email).toString();
		
		URL feedUrl= null;
		GoogleService service = super.getService();
		try {
			feedUrl = new URL(feedUrlString);
		} catch (MalformedURLException e1) {
			throw new RuntimeException("Error in URL while scheduling classplan with id : " + classplanId , e1);
		}

		try
		{						
			CalendarEventEntry myEntry = new CalendarEventEntry();
			myEntry.setTitle(new PlainTextConstruct(title));
			myEntry.setContent(new HtmlTextConstruct("http://www.goorudemo.org/gooru/launchClassroom.g?classplanId="+classplanId+"#/"+classplanId));
			
			//DateTime startTime = DateTime.parseDateTime(date);
			//DateTime endTime = DateTime.parseDateTime("2011-02-04T17:00:00-08:00");
			
			
			
			When eventTimes = new When();
			eventTimes.setStartTime(startTime);
			eventTimes.setEndTime(endTime);
			myEntry.addTime(eventTimes);
			

			// Send the request and receive the response:
			CalendarEventEntry insertedEntry = service.insert(feedUrl, myEntry);
			
			System.out.println("NEW ENTRY : " + insertedEntry.getTitle().getPlainText());
		}
		catch(Exception e)
		{	
			throw new RuntimeException("Error while scheduling classplan with id : " + classplanId , e);
		}

	}
}
