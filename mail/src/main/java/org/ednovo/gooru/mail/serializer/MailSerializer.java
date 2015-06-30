package org.ednovo.gooru.mail.serializer;

import org.springframework.web.servlet.ModelAndView;

public class MailSerializer {

	public static ModelAndView toModelAndView(Object object) {
		ModelAndView jsonmodel = new ModelAndView("rest/model");
		jsonmodel.addObject("model", object);
		return jsonmodel;
	}
}