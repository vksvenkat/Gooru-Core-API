package org.ednovo.gooru.core.api.model;

public enum ContentStatus {
		NEW("new"),
		ABUSED("abused"),
		VERIFIED("verified");
		private String status;	
		ContentStatus(String status){
			this.status = status;
		}
		public String getStatus() {
			return this.status;
		}
}
