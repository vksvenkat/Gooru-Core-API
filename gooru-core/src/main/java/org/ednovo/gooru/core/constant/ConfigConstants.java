package org.ednovo.gooru.core.constant;

public interface ConfigConstants {

	String MEMCACHE_INSTANCE_NAME = "memcache.instance.name";
	
	String MAIL_PASSWORD = "gooru.mail.password";
	
	String MAIL_USERNAME = "gooru.mail.username";
	
	String MAIL_FROM = "gooru.mail.from";
	
	String PUBLISHER = "gooru.mail.publisher.username";
	
	String CONTENT_ADMIN = "gooru.mail.content.username";
	
	String AUTOSUGGEST_SETTING = "gooru.autosuggest.mode";
	
	String MAX_ATTEMPT_TRY_ALLOWED = "attempt.try.max.allowed"; 
	
	String DEFAULT_IMAGES = "gooru.default.images";
	
	String MAIL_SMTP_HOST = "gooru.mail.smtp.host";
	
	String MAIL_SMTP_PORT = "gooru.mail.smtp.port";
	
	String REDIS_INSTANCE_NAME = "redis.instance.name";
	
	String SUGGEST_FOLDERS = "gooru.suggest.folders";
	
	String MAIL_BCC_SUPPORT = "gooru.mail.bcc.support";
	
	String PROFILE_IMAGE_URL = "profile.image.url";
	
	String PROFILE_BUCKET = "s3.profileBucket";
	
	String USER_NAME_RESTRICTIONS = "username.restrictions";
	
	String TAXONOMY_ROOT = "taxonomy.root";
	
	String DEFAULT_USER_ROLES = "default.user.roles";
	
	String PREFIX_SHORTEN_URL = "prefix.shorten.url";
	
	String ADMIN_CONTENT_ACCESS = "admin.content.access";
	
	String ACCESS_GOORU_CONTENT = "access.gooru.content";
	
	String BITLY_USER_NAME = "bitly.username";
	
	String BITLY_APIKEY = "bitly.apikey";
	
	String GOORU_HOME = "gooru.home";
	
	String GOORU_PROFINITY_ENDPOINT = "gooru.profanity.restendpoint";
	
	String SCRIBD_API_KEY = "scribd.api.key";
	
	String TEXTBOOK_DOCUMENT = "textbook.document";
	
	String GOORU_DOC_VIEW_HOME = "gooru.doc.view.home";
	
	String GOORU_DOC_VIEW_CACHE = "gooru.doc.view.cache";
	
	String GOORU_SITE_URL = "gooru.site.url";
	
	String GOORU_NEW_URL = "gooru.new.url";
	
	String RESOURCE_S3_BUCKET = "resource.s3bucket";
	
	String GOORU_STATIC_ENDPOINT = "gooru.static.endpoint";
	
	String GOORU_SERVICES_ENDPOINT = "gooru.services.endpoint";
	
	String GOORU_API_ENDPOINT = "gooru.api.restendpoint";
	
	String GOORU_IPAD_LOGIN_WITHOUT_CONFIRMATION_LIMIT = "gooru.ipad.login.without.confirmation.limit";
	
	String GOORU_WEB_LOGIN_WITHOUT_CONFIRMATION_LIMIT = "gooru.web.login.without.confirmation.limit";
	
	String GOORU_SEARCH_ENDPOINT = "gooru.search.endpoint";
	
	String GOORU_SEARCH_SERVER_HOST = "gooru.search.server.host";
	
	String GOORU_SEARCH_SERVER_PORT = "gooru.search.server.port";
	
	String GOORU_SEARCH_CLUSTERNAME = "gooru.search.clusterName";
	
	String GOORU_INSTANCE_CLASS = "gooru.instance.class";
	
	String STATIC_FILE_BASEPATH = "staticfilebasepath";
	
	String CLASSPLAN_REPOSITORY_REALPATH = "classplan.repository.realPath";
	
	String CLASSPLAN_REPOSITORY_APPPATH = "classplan.repository.appPath";
	
	String S3_LICENSE_BUCKET = "s3.licenseBucket";
	
	String S3_GOORU_BUCKET = "s3.gooruBucket";
	
	String S3_GOORU_BASE_URL = "s3.base.url";
	
	String GOORU_APP_KEY = "gooruApp.key";
	
	String GOORU_APP_SECRET = "gooruApp.secret";
	
	String GOORU_MAIL_RESTPOINT = "gooru.mail.restendpoint";
	
	String GOORU_CONVERSION_RESTPOINT = "gooru.conversion.restendpoint";
	
	String REPORT_CREATE_JIRA_TICKET = "report.create.jira.ticket";
	
	String RESET_PASSWORD_CONFIRM_RESTENDPOINT = "reset.password.confirm.restendpoint";
	
	String RESET_EMAIL_CONFIRM_RESTENDPOINT = "reset.email.confirm.restendpoint";
	
	String GOORU_MEDIA_END_POINT = "gooru.media.end.point";
	
	String GOORU_USER_INACTIVE_MAIL_BATCH_SIZE = "gooru.user.inactive.mail.batch.size";
	
	String GOORU_USER_BIRTHDAY_MAIL_BATCH_SIZE = "gooru.user.birthday.mail.batch.size";
	
	String GOORU_USER_INACTIVE_MAIL_LIMIT = "gooru.user.inactive.mail.limit";
	
	String GOORU_EXCLUDE_TAXONOMY_PREFERENCE = "gooru.exclude.taxonomy.preference";
	
	String GOORU_AUTHENTICATION_SECERT_KEY = "gooru.authentication.secert.key";
	
	String ZENDESK_SUBDOMAIN = "zendesk.subdomain";
	
	String ZENDESK_SHAREDKEY = "zendesk.sharedkey";
	
	String JIRA_CONFIG = "jira.config";
	
    String AUTHSSO_CONFIG = "authsso.config";
	
    String TOMCAT_CONFIG = "tomcat.config";
	
    String S3_CONFIG = "s3.config";
    
    String SCHEDULERS_CONFIG = "schedulers.config";
    
    String GOORU_APP = "gooruApp.config";
    
    String GOORU_SEARCH = "gooruSearch.config";
    
    String CLASSPLAN_REPOSITORY = "classplan.repository.config";
    
    String LOG_SETTINGS_CONFIG = "log.settings.config";
    
    String SCRIBD_API_CONFIG = "scribd.api.config";
    
    String CONFIG_SETTING_PROFILE = "config-setting.profile";
    
    String TAXONOMY_REPOSITORY_CONFIG = "taxonomy.repository.config";
    
    String GOOGLE_EARTH_KEY = "google.earth.key.config";
    
    String GOOGLE_ANALYTICS_CONFIG = "google.analytics.config" ;
    
    String GOORU_USER_INACTIVE_MAIL_SEND = "gooru.user.inactive.mail.send";
    
    String GOORU_USER_BIRTHDAY_MAIL_SEND = "gooru.user.birthday.mail.send";
    
    String GOORU_CHILD_USER_BIRTHDAY_MAIL_SEND = "gooru.child.user.13.mail.send";
    
    String WSFEDSSO_CONFIG = "wsfedsso.config";
    
    String WSFEDSSO_ATTRIBUTES_CONFIG = "wsfedsso.attributes.config";
    
    String REST_API_END_POINT_V2 = "login.auth.endpoint";
      
    String LOGIN =  "login.auth";
    
    String INSIGHTS_LOGAPI_ENDPOINT = "insights.logapi.endpoint";
    
    String INSIGHTS_ACTIVITYSTREAM_URL = "insights.activitystream.url";
    
    String SEARCH_INDEX_RESTENDPOINT = "search.index.restendpoint";

}
