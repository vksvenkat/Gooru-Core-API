/*
*ServiceCallImpl.java
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

package org.ednovo.gooru.domain.service.serviceCall;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.infrastructure.persistence.hibernate.service_call.ServiceCallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

@Service("serviceCall")
public class ServiceCallImpl implements ServiceCall{

	@Autowired
	private ServiceCallRepository serviceCallRepository;
	
	@Override
	public void save(HttpServletRequest request, HttpServletResponse response,
			ModelAndView modelAndView, String serviceName) {
		serviceCallRepository.save(request, response, modelAndView, serviceName);
		
	}

	@Override
	public Map<String, Long> getUserTimeSpend(Integer userId) {
		return serviceCallRepository.getUserTimeSpend(userId);
	}

	@Override
	public Map<String, Integer> getUserClassView(String userId) {
		return serviceCallRepository.getUserClassView(userId);
	}

	@Override
	public Map<String, Integer[]> getClassplansIOwenSummary(String userId) {
		return serviceCallRepository.getClassplansIOwenSummary(userId);
	}

	@Override
	public List getNumberOfUserOverTime(String timeSpan) {
		return serviceCallRepository.getNumberOfUserOverTime(timeSpan);
	}

	@Override
	public Integer[] getRegisterVsUnregister() {
		return serviceCallRepository.getRegisterVsUnregister();
	}

}
