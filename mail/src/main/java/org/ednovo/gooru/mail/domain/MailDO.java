package org.ednovo.gooru.mail.domain;

import java.util.List;

import javax.mail.Address;

public class MailDO {

	private String htmlContent;
	private String content;
	private String subject;
	private String recipient;
	private String from;
	private String fromName;
	private String bcc;
	private String Cc;
	private String signature;
	private Long expires;
	private String host;
	private String port;
	private String username;
	private String password;
	private List<Attachment> attachFiles;
	private Address[] address; 
	private Address singleRecipient;

	public Address getSingleRecipient() {
		return singleRecipient;
	}

	public void setSingleRecipient(Address singleRecipient) {
		this.singleRecipient = singleRecipient;
	}

	private boolean sendRecipient;

	public boolean isSendRecipient() {
		return sendRecipient;
	}

	public void setSendRecipient(boolean sendRecipient) {
		this.sendRecipient = sendRecipient;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	public String getHtmlContent() {

		return htmlContent;
	}

	public void setContent(String content) {
		this.content = content;

	}

	public String getContent() {
		return content;
	}

	public void setSubject(String subject) {
		this.subject = subject;

	}

	public String getSubject() {
		return subject;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;

	}

	public String getRecipient() {
		return recipient;
	}

	public void setFrom(String from) {
		this.from = from;

	}

	public String getFrom() {
		return from;

	}

	public void setFromName(String fromName) {
		this.fromName = fromName;

	}

	public String getFromName() {
		return fromName;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;

	}

	public String getBcc() {
		return bcc;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public Long getExpires() {
		return expires;
	}

	public void setExpires(Long expires) {
		this.expires = expires;
	}

	public void setCc(String cc) {
		Cc = cc;
	}

	public String getCc() {
		return Cc;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getHost() {
		return host;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getPort() {
		return port;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setAttachFiles(List<Attachment> attachFiles) {
		this.attachFiles = attachFiles;
	}

	public List<Attachment> getAttachFiles() {
		return attachFiles;
	}

	public Address[] getAddress() {
		return address;
	}

	public void setAddress(Address[] address) {
		this.address = address;
	}

}
