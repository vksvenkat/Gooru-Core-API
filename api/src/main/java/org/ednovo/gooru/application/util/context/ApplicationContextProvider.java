/**
 * 
 */
package org.ednovo.gooru.application.util.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author ashish
 * This class enables to get hold of context and stash this context onto 
 * the global holder which is AppContext
 */
public class ApplicationContextProvider implements ApplicationContextAware {

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		AppContext.setCtx(ctx);

	}

}
