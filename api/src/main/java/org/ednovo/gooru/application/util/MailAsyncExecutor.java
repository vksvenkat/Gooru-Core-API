/////////////////////////////////////////////////////////////
// MailAsyncExecutor.java
// gooru-api
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.application.util;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.ednovo.gooru.infrastructure.mail.MailHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@Transactional(propagation = Propagation.NEVER)
public class MailAsyncExecutor {
	
	
	private TransactionTemplate transactionTemplate;
	
	@Autowired
	private HibernateTransactionManager transactionManager;
	
	@Autowired
	private MailHandler mailHandler;
	
	private Logger logger = LoggerFactory.getLogger(MailAsyncExecutor.class);
	
	@PostConstruct
	public void  init() {
		transactionTemplate = new TransactionTemplate(transactionManager);
	}
	
	public void handleMailEvent(final Map<String, String> data) { 
		transactionTemplate.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				try {
					  getMailHandler().handleMailEvent(data);
				} catch (Exception e) {
					logger.debug("sending mail " + e);
				}
				return null;
			}
		});
	}
	
	public void handleMailEvent(final String  eventType) { 
		transactionTemplate.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				getMailHandler().handleMailEvent(eventType);
				return null;
				
			}
		});
	}
	
	public void sendMailToResetPassword(final String gooruUid, final String password, final Boolean flag, final String gooruBaseUrl,final String mailConfirmationUrl) { 
		transactionTemplate.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				try {
					getMailHandler().sendMailToResetPassword(gooruUid, password, flag, gooruBaseUrl,mailConfirmationUrl);
				} catch (Exception e) {
					logger.debug("sending mail " + e);
				}
				return null;
				
			}
		});
	}
	
	public void  sendMailToConfirm(final String gooruUid, final String password, final String accountType, final String tokenId, final String encodedDateOfBirth, final String gooruBaseUrl, final String mailConfirmationUrl,final String userGrade,final String userCourse) { 
		transactionTemplate.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				try {
					getMailHandler().sendMailToConfirm(gooruUid, password, accountType, tokenId, encodedDateOfBirth, gooruBaseUrl, mailConfirmationUrl, userGrade, userCourse);
				} catch (Exception e) {
					logger.debug("sending mail " + e);
				}
				return null;
				
			}
		});
	}
	
	public void  sendMailToConfirmPasswordChanged(final String gooruUid, final String password, final Boolean flag, final String gooruBaseUrl, final String mailConfirmationUrl) { 
		transactionTemplate.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				try {
					getMailHandler().sendMailToConfirmPasswordChanged(gooruUid, password, flag, gooruBaseUrl, mailConfirmationUrl);
				} catch (Exception e) {
					logger.debug("sending mail " + e);
				}
				return null;
				
			}
		});
	}
	
	public void   sendMailForFollowedOnUserOrGroup(final String gooruUId) { 
		transactionTemplate.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				try {
					getMailHandler().sendMailForFollowedOnUserOrGroup(gooruUId);
				} catch (Exception e) {
					logger.debug("sending mail " + e);
				}
				return null;
				
			}
		});
	}
	
	public void   sendMailForUnFollowUserOrGroup(final String gooruUId) { 
		transactionTemplate.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				try {
					getMailHandler().sendMailForUnFollowUserOrGroup(gooruUId);
				} catch (Exception e) {
					logger.debug("sending mail " + e);
				}
				return null;
				
			}
		});
	}
	
	public void   sendMailToRequestPublisher(final Map<String, Object> model) { 
		transactionTemplate.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				try {
					getMailHandler().sendMailToRequestPublisher(model);
				} catch (Exception e) {
					logger.debug("sending mail " + e);
				}
				return null;
				
			}
		});
	}
	
	public void   sendMailForCollaborator(final String gooruUId, final String senderUserName, final String gooruOid, final String collectionOrQuizTitle, final String flag) { 
		transactionTemplate.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				try {
					getMailHandler().sendMailForCollaborator(gooruUId, senderUserName, gooruOid, collectionOrQuizTitle, flag);
				} catch (Exception e) {
					logger.debug("sending mail " + e);
				}
				return null;
				
			}
		});
	}
	
	public void  sendEmailNotificationforComment(final Map<String, String> commentData) { 
		transactionTemplate.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				try {
					getMailHandler().sendEmailNotificationforComment(commentData);
				} catch (Exception e) {
					logger.debug("sending mail " + e);
				}
				return null;
				
			}
		});
	}
	
	public void  sendMailToInviteCollaborator(final Map<String, Object> collaboratorData) { 
		transactionTemplate.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				try {
					getMailHandler().sendMailToInviteCollaborator(collaboratorData);
				} catch (Exception e) {
					logger.debug("sending mail " + e);
				}
				return null;
				
			}
		});
	}

	public MailHandler getMailHandler() {
		return mailHandler;
	}
}
