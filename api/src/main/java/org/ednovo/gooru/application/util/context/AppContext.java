package org.ednovo.gooru.application.util.context;

import org.springframework.context.ApplicationContext;


/**
 * @author ashish
 * The class which is going to hold application context and provide
 * it to other components on demand.
 * The setup of context is enabled by AppContextProvider
 * @see AppContextProvider
 */
public class AppContext {
	
	public static ApplicationContext getCtx() {
		return ctx;
	}

	public static void setCtx(ApplicationContext ctx) {
		AppContext.ctx = ctx;
	}

	private static ApplicationContext ctx;
	
}
