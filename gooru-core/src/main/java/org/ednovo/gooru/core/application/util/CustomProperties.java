package org.ednovo.gooru.core.application.util;

import java.io.Serializable;

public class CustomProperties implements Serializable {

	private static final long serialVersionUID = -872983111354024661L;

	public static enum Table {
		FEEDBACK_RATING_TYPE("feedback_rating_type"), FEEDBACK_REPORT_CONTENT_TYPE("feedback_report_content_type"), FEEDBACK_REPORT_USER_TYPE("feedback_report_user_type"), FEEDBACK_OTHER_TYPE("feedback_other_type"), TARGET("target"), FEEDBACK_CATEGORY("feedback_category"), COMMNET_STATUS(
				"comment_status"), TAG_STATUS("tag_status"), TAG_TYPE("tag_type"), POST_TYPE("post_type"), POST_STATUS("post_status"), USER_CLASSIFICATION_TYPE("user_classification_type"), FEEDBACK_REACTION("feedback_reaction"), EVENT_STATUS("event_status"), THEME_TYPE("theme_type"), PRODUCT_TYPE(
				"product_type"), BUILD_TYPE("build_type"), CONTENT_STATUS_TYPE("content_status_type"), ORGANIZATION_TYPE("organization_type"), APPLICATION_STATUS("application_status"), ASSIGNMENT_STATUS_TYPE("assignment_status_type"), ORGANIZATION_CATEGORY("organization_category"), PASSWORD_ENCRYPTION_TYPE("password_encryption_type"),
				PUBLISH_STATUS("publish_status");
		private String table;

		Table(String table) {
			this.table = table;
		}

		public String getTable() {
			return this.table;
		}
	}

	public static enum Target {
		USER("user"), CONTENT("content");
		private String target;

		Target(String feedbackTarget) {
			this.target = feedbackTarget;
		}

		public String getTarget() {
			return this.target;
		}
	}

	public static enum FeedbackRatingType {
		STAR("star"), THUMB("thumb");
		private String feedbackRatingType;

		FeedbackRatingType(String feedbackRatingType) {
			this.feedbackRatingType = feedbackRatingType;
		}

		public String getFeedbackRatingType() {
			return this.feedbackRatingType;
		}
	}

	public static enum FeedbackCategory {
		RATING("rating"), REPORT("report"), FLAG("flag"), REACTION("reaction");
		private String feedbackCategory;

		FeedbackCategory(String feedbackCategory) {
			this.feedbackCategory = feedbackCategory;
		}

		public String getFeedbackCategory() {
			return this.feedbackCategory;
		}
	}

	public static enum CommentStatus {
		NEW("new"), ACTIVE("active"), SPAM("spam"), ABUSE("abuse");
		private String commentStatus;

		CommentStatus(String commentStatus) {
			this.commentStatus = commentStatus;
		}

		public String getCommentStatus() {
			return this.commentStatus;
		}
	}

	public static enum PostStatus {
		NEW("new"), ACTIVE("active"), SPAM("spam"), ABUSE("abuse");
		private String postStatus;

		PostStatus(String postStatus) {
			this.postStatus = postStatus;
		}

		public String getPostStatus() {
			return this.postStatus;
		}
	}

	public static enum TagStatus {
		NEW("new"), ACTIVE("active"), SPAM("spam"), ABUSE("abuse");
		private String tagStatus;

		TagStatus(String tagStatus) {
			this.tagStatus = tagStatus;
		}

		public String getTagStatus() {
			return this.tagStatus;
		}
	}

	public static enum UserClassificationType {
		COURSE("course"), GRADE("grade");
		private String userClassificationType;

		UserClassificationType(String userClassificationType) {
			this.userClassificationType = userClassificationType;
		}

		public String getUserClassificationType() {
			return this.userClassificationType;
		}
	}

	public static enum EventStatus {
		IN_ACTIVE("in-active"), ACTIVE("active");
		private String status;

		EventStatus(String status) {
			this.status = status;
		}

		public String getStatus() {
			return this.status;
		}
	}

	public static enum EventMapping {
		WELCOME_MAIL("welcome_mail"), USER_IN_ACTIVE_MAIL("user_in_active_mail"), FIRST_COLLECTION("first_collection"), SSO_CONFIRMATION_MAIL("sso_confirm_mail"), GOORU_EXTERNALID_CHANGE("gooru_externalId_change"), USER_BIRTHDAY_MAIL("birthday_mail"), CHILD_13_CONFIRMATION("child_13_confirmation"), STUDENT_SEPARATION_CONFIRMATION(
				"student_separation_confirmation"), STUDENT_IS_13("student_is_13"), CHILD_REGISTRATION_CONFIRMATION("child_registration_confirmation"), PARANT_REGISTRATION_CONFIRMATION("parant_registration_confirmation"), NON_PARANT_REGISTRATION_CONFIRMATION("non_parant_registration_confirmation"), COMMENT_ON_PARENT_COLLECTION(
				"comment_on_parent_collection"), COMMENT_ON_CHILD_COLLECTION("comment_on_child_collection"), CHANGE_CHILD_ACCOUNT_PASSWORD("change_child_account_password"), PASSWORD_CHANGED_CONFIRMATION_NOTIFICATION("password_changed_confirmation_notification"), CHANGE_GOORU_ACCOUNT_PASSWORD(
				"change_gooru_account_password"), SEND_MAIL_TO_INVITE_COLLABORATOR("send_mail_to_invite_collaborator"), SEND_MAIL_TO_INVITE_USER_CLASS("send_mail_to_invite_user_class"), SEND_MAIL_TO_OPEN_CLASS_USER("send_mail_to_open_class_user"), CHANGE_GOORU_PARTNER_ACCOUNT_PASSWORD(
				"change_gooru_partner_account_password"), PARTNER_PORTAL_USER_REGISTRATION_CONFIRMATION("partner_portal_user_registration_confirmation"), USER_DISABLED_NOTIFICATION("user_disabled_notification");
		private String event;

		EventMapping(String event) {
			this.event = event;
		}

		public String getEvent() {
			return this.event;
		}
	}

	public static enum Product {
		WEB("web"), MOBILE("mobile");
		private String product;

		Product(String product) {
			this.product = product;
		}

		public String getProduct() {
			return this.product;
		}
	}

	public static enum ContentStatusType {
		OPEN("open"), NEW("new"), VERIFIED("verified"), RESOLVED("resolved");
		private String contentStatusType;

		ContentStatusType(String contentStatusType) {
			this.contentStatusType = contentStatusType;
		}

		public String getContentStatusType() {
			return this.contentStatusType;
		}
	}

	public static enum OrganizationType {
		SCHOOL("school"), FREELANCE_DEVELOPER("freelance developer"), COMPANY("company"), OTHER("other");
		private String organizationType;

		OrganizationType(String organizationType) {
			this.organizationType = organizationType;
		}

		public String getOrganizationType() {
			return this.organizationType;
		}
	}

	public static enum ApplicationStatus {
		DEVELOPMENT("in_development"), SUBMITTED_FOR_REVIEW("submitted_for_review"), PRODUCTION("production"), ACTIVE("active"), IN_ACTIVE("in_active");
		private String applicationStatus;

		ApplicationStatus(String applicationStatus) {
			this.applicationStatus = applicationStatus;
		}

		public String getApplicationStatus() {
			return this.applicationStatus;
		}
	}

	public static enum ContentProviderType {
		PUBLISHER("publisher"), AGGREGATOR("aggregator"), HOST("host");
		private String contentProviderType;

		ContentProviderType(String contentProviderType) {
			this.contentProviderType = contentProviderType;
		}

		public String getContentProviderType() {
			return this.contentProviderType;
		}
	}

	public static enum AssignmentStatus {
		OPEN("open"), COMPLETED("completed");
		private String assignmentStatus;

		AssignmentStatus(String assignmentStatus) {
			this.assignmentStatus = assignmentStatus;
		}

		public String getAssignmentStatus() {
			return this.assignmentStatus;
		}
	}

	public static enum InstitutionType {
		SCHOOL("school"), DISTRICT("school-district"), SCHOOL_DISTRICT("school_district");
		private String institutionType;

		InstitutionType(String institutionType) {
			this.institutionType = institutionType;
		}

		public String getInstitutionType() {
			return this.institutionType;
		}
	}

	public static enum PasswordEncryptType {
		SHA("sha"), MD5("md5");
		private String passwordEncryptType;

		PasswordEncryptType(String passwordEncryptType) {
			this.passwordEncryptType = passwordEncryptType;
		}

		public String getPasswordEncryptType() {
			return this.passwordEncryptType;
		}
	}

}
