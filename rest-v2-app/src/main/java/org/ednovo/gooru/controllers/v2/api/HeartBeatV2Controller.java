package org.ednovo.gooru.controllers.v2.api;

import java.net.InetAddress;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = { "/v2/heartbeat" })
public class HeartBeatV2Controller extends BaseController{

	@RequestMapping(value = { "/{code}" }, method = RequestMethod.GET)
	public void getHeartBeat(@PathVariable(value = "code") String eventId, HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		response.setStatus(HttpServletResponse.SC_OK);
		SessionContextSupport.putLogParameter("eventName", "heart-beat");
		SessionContextSupport.putLogParameter("eventId", eventId);
		SessionContextSupport.putLogParameter("IP Address", request.getRemoteAddr());
		SessionContextSupport.putLogParameter("Host Address", InetAddress.getLocalHost().getHostAddress());
		SessionContextSupport.putLogParameter("Host Name",InetAddress.getLocalHost().getHostName());
	}

}
