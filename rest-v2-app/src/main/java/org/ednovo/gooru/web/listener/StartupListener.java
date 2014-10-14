/////////////////////////////////////////////////////////////
//StartupListener.java
//rest-v2-app
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person      obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so,  subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY  KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE    WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR  PURPOSE     AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR  COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.web.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.ednovo.gooru.application.util.UserContentRelationshipUtil;
import org.ednovo.gooru.domain.service.taxonomy.TaxonomyService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserContentRepository;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * <p>
 * StartupListener class used to initialize and database settings and populate
 * any application-wide drop-downs.
 * 
 * <p>
 * Keep in mind that this listener is executed outside of
 * OpenSessionInViewFilter, so if you're using Hibernate you'll have to
 * explicitly initialize all loaded data at the Dao or service level to avoid
 * LazyInitializationException. Hibernate.initialize() works well for doing
 * this.
 * 
 */
public class StartupListener extends ContextLoaderListener implements ServletContextListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(StartupListener.class);

	public void contextInitialized(ServletContextEvent event) {

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("initializing context...");
		}

		// call Spring's context ContextLoaderListener to initialize
		// all the context files specified in web.xml
		super.contextInitialized(event);

		ServletContext context = event.getServletContext();

		SessionFactory sessionFactory = lookupSessionFactory(context);
		Session session = null;
		boolean participate = false;
		if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
			// do not modify the Session: just set the participate flag
			participate = true;
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Opening temporary Hibernate session in StartupListener");
			}
			session = getSession(sessionFactory);
			TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));

		}

		ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(context);

		if (LOGGER.isInfoEnabled()) {
			LOGGER.debug("Drop-down initialization complete [OK]");
		}


		UserContentRepository userContentRepository = (UserContentRepository) ctx.getBean("userContentRepository");
		UserContentRelationshipUtil.setUserContentRepository(userContentRepository);

		try {
			TaxonomyService taxonomyService = (TaxonomyService) ctx.getBean("taxonomyService");
			taxonomyService.writeTaxonomyToDisk();
		} catch (Exception e) {
			LOGGER.error("Error while creating taxonomy", e);
		}

		// Setup the default TransformerFactory provide. Current we use two
		// providers
		// 1. Xalan - Default
		// 2. Saxon - Use specific for Mathml pre processing (Mathml to SVG)
		// The sytem property will be set to point to XALAN by default (i.e.
		// TransformerFactory.newInstance()
		// will return XALAN).In order to user Saxon, you will need instantiate
		// the specific TransformerImpl class manually
		System.setProperty("javax.xml.transform.TransformerFactory", "org.apache.xalan.processor.TransformerFactoryImpl");

		if (!participate) {
			TransactionSynchronizationManager.unbindResource(sessionFactory);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Closing temporary Hibernate session in StartupListener");
			}
			closeSession(session, sessionFactory);
		}
	}

	/**
	 * <p>
	 * The default implementation looks for a bean with the specified name in
	 * Spring's root application context.
	 * 
	 * @return the SessionFactory to use
	 * @see #getSessionFactoryBeanName
	 */
	protected SessionFactory lookupSessionFactory(ServletContext servletContext) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Using session factory '" + getSessionFactoryBeanName() + "' for StartupListener");
		}
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		return (SessionFactory) wac.getBean(getSessionFactoryBeanName());
	}

	private String getSessionFactoryBeanName() {
		return "sessionFactory";
	}

	/**
	 * Gets a Session for the SessionFactory that this listener uses. Note that
	 * this just applies in single session mode!
	 * <p>
	 * The default implementation delegates to SessionFactoryUtils' getSession
	 * method and sets the Session's flushMode to NEVER.
	 * <p>
	 * Can be overridden in subclasses for creating a Session with a custom
	 * entity interceptor or JDBC exception translator.
	 * 
	 * @param sessionFactory
	 *            the SessionFactory that this listener uses
	 * @return the Session to use
	 * @throws org.springframework.dao.DataAccessResourceFailureException
	 *             if the Session could not be created
	 * @see org.springframework.orm.hibernate3.SessionFactoryUtils#getSession(SessionFactory,
	 *      boolean)
	 * @see org.hibernate.FlushMode#NEVER
	 */
	protected Session getSession(SessionFactory sessionFactory) throws DataAccessResourceFailureException {
		Session session = sessionFactory.openSession();
		session.setFlushMode(FlushMode.MANUAL);
		return session;
	}

	/**
	 * Closes the given Session. Note that this just applies in single session
	 * mode!
	 * <p>
	 * The default implementation delegates to SessionFactoryUtils'
	 * closeSessionIfNecessary method.
	 * <p>
	 * Can be overridden in subclasses, e.g. for flushing the Session before
	 * closing it. See class-level javadoc for a discussion of flush handling.
	 * Note that you should also override getSession accordingly, to set the
	 * flush mode to something else than NEVER.
	 * 
	 * @param session
	 *            the Session used for filtering
	 * @param sessionFactory
	 *            the SessionFactory that this filter uses
	 */
	protected void closeSession(Session session, SessionFactory sessionFactory) {
		SessionFactoryUtils.closeSession(session);
	}
}
