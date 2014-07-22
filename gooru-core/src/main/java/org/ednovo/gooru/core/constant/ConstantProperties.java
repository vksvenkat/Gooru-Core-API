package org.ednovo.gooru.core.constant;

public interface ConstantProperties {

	static final String[] ACTIVITY_LIST_EXCLUDE = { "*.class", "*.courseSet", "*.userRoleSetString", "*.emailId" };

	static final String QUESTION_INCLUDES[] = { "hints", "taxonomySet", "assets", "answers", "tagSet", "depthOfKnowledges", "educationalUse", "*.standards", "*.code", "*.description", "*.metaInfo" };

	static final String QUESTION_EXCLUDES[] = { "answers.matchingSequence" };

	static final String QUESTION_INCLUDE_FIELDS[] = { "hints", "taxonomySet", "assets", "answers", "tagSet", "label", "type", "typeName", "concept", "questionText", "sharing", "assetURI", "assets", "category", "contentType", "contentType.description", "contentType.name", "copiedResourceId",
			"createdOn", "entryId", "explanation", "folder", "gooruOid", "label", "lastModified", "license", "license.code", "license.definition", "license.icon", "license.name", "license.tag", "license.url", "questionText", "resourceType", "resourceType.description", "resourceType.name", "title",
			"type", "typeName", "answers.answerId", "answers.answerText", "answers.answerType", "answers.isCorrect", "answers.matchingAnswer", "answers.sequence", "answers.answerHint", "answers.answerGroupCode", "answers.answerExplanation", "creator", "user", "hints.hintId", "hints.hintText",
			"hints.sequence", "assetKey", "assets.asset", "assets.asset.assetId", "assets.asset.name", "assets.asset.description" };

	static final String SEGMENT_QUESTION_INCLUDES[] = { "question.hints", "question.answers", "question.assets" };

	static final String QUESTION_SET_INCLUDES[] = { "questionSetQuestions" };

	static final String RESOURCE_INCLUDE_FIELDS[] = { "*.resource", "*.assetURI", "*.brokenStatus", "*.category", "*.createdOn", "*.usernameDisplay", "*.profileImageUrl", "*.lastName", "*.firstName", "*.username", "*.description", "*.distinguish", "*.folder", "*.gooruOid", "*.resourceType",
			"*.resourceType.name", "*.sharing", "*.title", "*.views", "*.thumbnails", "*.url", "*.user", "*.creator", "*.license", "*.license.code", "*.license.icon", "*.license.name", "*.license.definition", "*.dimensions", "*.defaultImage", "*.resourceSource", "*.attribution", "*.sourceName",
			"*.questionInfo", "*.TYPE", "*.type", "*.depthOfKnowledge", "*.educationalUse", "*.momentsOfLearning", "*.name", "*.questionText", "*.answers.*", "*.assets", "*.assets.asset", "*.assets.asset.name", "*.assets.asset.url", "*assets.asset.description", "*assets.asset.hasUniqueName",
			"*.hints.*", "*.description", "*.explanation", "*.taxonomySet", "*.codeId", "*.depth", "*.lastModified", "*.gooruUId", "*.lastUpdatedUserUid", "*.category", "*.label", "*.code", "*.userRating", "*.hasFrameBreaker", "*.copiedResourceId", "*.assignmentContentId", "*.trackActivity",
			"*.trackActivity.startTime", "*.trackActivity.endTime", "*.goals", "*.grade", "*.mediaType", "*.text", "*.isOer", "*.meta.*", "*.resourceFormat.value", "*.resourceFormat.displayName", "*.instructional.value", "*.instructional.displayName", "*.depthOfKnowledges.value",
			"*.depthOfKnowledges.selected", "*.momentsOfLearning.selected", "*.momentsOfLearning.value", "*.educationalUse.value", "*.educationalUse.selected", "*.ratings", "*.average", "*.count", "*.standards", "*.license", "*.standards.code", "*.standards.description", "*.course",

			"*.customFieldValues.*", "*.publisher", "*.aggregator", "*.totalPages" };

	static final String ASSIGNMENT_INCLUDE_FIELDS[] = { "*.narrationLink", "collection.notes", "*.keyPoints", "*.language", "*.goals", "*.grade", "*.estimatedTime", "*.collectionType", "*.creator", "*.assetURI", "*.license", "*.license.code", "*.license.icon", "*.license.name",
			"*.license.definition", "*.dimensions", "*.defaultImage", "*.createdOn", "*.user", "*.usernameDisplay", "*.profileImageUrl", "*.lastName", "*.firstName", "*.username", "*.description", "*.folder", "*.gooruOid", "*.sharing", "*.title", "*.views", "*.thumbnails", "*.url",
			"*.lastModified", "*.gooruUId", "*.vocabulary", "*.collaborators", "*.network", "trackActivity.startTime", "trackActivity.endTime" };

	static final String COLLECTION_ITEM_INCLUDE_FILEDS[] = { "*.collectionItems", "*.collectionItemId", "*.itemSequence", "*.itemType", "*.narration", "*.narrationType", "*.lastModified", "*.start", "*.stop", "*.standards", "*.license", "*.standards.code", "*.standards.description",
			"*.associationDate", "*associatedUser", "*.status", "*.totalPages" };

	static final String CLASSPAGE_COLLECTION_ITEM_INCLUDE_FIELDS[] = { "*.plannedEndDate", "*.totalHitCount", "*.searchResults" };

	static final String COLLECTION_ITEM_INCLUDE[] = { "collectionItems", "*.collectionItemId", "*.itemSequence", "*.itemType", "*.narration", "*.narrationType", "*.lastModified", "*.start", "*.stop", "*.standards", "*.license", "*.standards.code", "*.standards.description", "*.collectionItem",
			"*.collectionItem.collection", "*.totalPages" };

	static final String EXCLUDE_ALL[] = { "*", "*.class" };

	static final String ASSIGNMENT_TAXONOMY[] = { "*.taxonomySet", "*.taxonomySetMapping.*" };

	static final String CLASSPAGE_INCLUDE_FIELDS[] = { "*.narrationLink", "collection.notes", "*.keyPoints", "*.language", "*.goals", "*.grade", "*.estimatedTime", "*.collectionType", "*.creator", "*.assetURI", "*.license", "*.license.code", "*.license.icon", "*.license.name",
			"*.license.definition", "*.dimensions", "*.defaultImage", "*.createdOn", "*.user", "*.usernameDisplay", "*.profileImageUrl", "*.lastName", "*.firstName", "*.username", "*.description", "*.folder", "*.gooruOid", "*.sharing", "*.title", "*.views", "*.thumbnails", "*.url",
			"*.lastModified", "*.gooruUId", "*.vocabulary", "*.collaborators", "*.network", "*.assignmentContentId", "*.trackActivity", "*.trackActivity.startTime", "*.trackActivity.endTime", "*.classpageCode","*.itemCount","*.memberCount" };

	static final String CLASSPAGE_ITEM_INCLUDE_FIELDS[] = { "*.collectionItems", "*.collectionItemId", "*.itemSequence", "*.itemType", "*.narration", "*.narrationType", "*.lastModified", "*.start", "*.stop", "*.standards", "*.license", "*.standards.code", "*.standards.description",
			"*.classpageCode", "*.totalHitCount", "*.searchResults" };

	static final String CLASSPAGE_ITEMS_INCLUDE_FIELDS[] = { "*.collectionItems", "*.collectionItemId", "*.itemSequence", "*.itemType", "*.narration", "*.narrationType", "*.lastModified", "*.start", "*.stop", "*.standards", "*.license", "*.standards.code", "*.standards.description",
			"*.classpageCode", "*.totalHitCount", "*.searchResults" };

	static final String CLASSPAGE_META_INFO[] = { "*.vocabulary", "*.course", "*.standards", "*.code", "*.description", "*.rating", "*.score", "*.count", "*.average", "*.votesUp", "*.votesDown", "*.votes", "*.acknowledgement", "*.metaInfo", "*.taxonomySetMapping" }; // };

	static final String CLASSPAGE_ITEM_TAGS[] = { "*.tagSet", "*.label", "*.type", "*.resourceType" };

	static final String CLASSPAGE_TAXONOMY[] = { "*.taxonomySet", "*.taxonomySetMapping.*" };

	static final String CLASSPAGE_ITEM_INCLUDE[] = { "*.collection.gooruOid", "*.collection.title", "*.collection.thumbnails", "*.collection.goals" };

	static final String CLASSPAGE_CREATE_ITEM_INCLUDE_FILEDS[] = { "*.collectionItemId", "*.itemSequence", "*.itemType", "*.narration", "*.narrationType", "*.lastModified", "*.start", "*.stop", "*.standards", "*.license", "*.standards.code", "*.standards.description", "*.plannedEndDate" };

	static final String COLLECTION_INCLUDE_FIELDS[] = { "*.narrationLink", "collection.notes", "*.keyPoints", "*.language", "*.goals", "*.grade", "*.estimatedTime", "*.collectionType", "*.creator", "*.assetURI", "*.license", "*.license.code", "*.license.icon", "*.license.name",
			"*.license.definition", "*.dimensions", "*.defaultImage", "*.createdOn", "*.user", "*.usernameDisplay", "*.profileImageUrl", "*.lastName", "*.firstName", "*.username", "*.description", "*.folder", "*.gooruOid", "*.sharing", "*.title", "*.views", "*.thumbnails", "*.url",
			"*.lastModified", "*.gooruUId", "*.mailNotification", "*.vocabulary", "*.collaborators", "*.network", "*.publishStatus", "*.buildType", "*.publishStatus.value", "*.publishStatus.displayName", "*.buildType.value", "*.instructional.value", "*.instructional.displayName", "*.emailId",
			"*.lastModifiedUser", "*.modifiedDate", "*.gooruid", "*.username", "*.ideas", "*.questions", "*.performanceTasks", "*.languageObjective", "*.audience.selected", "*.audience.value", "*.instructionalMethod.value", "*.instructionalMethod.selected", "*.depthOfKnowledges.value",
			"*.depthOfKnowledges.selected", "*.learningSkills.selected", "*.learningSkills.value", "*.customFields", "*.optionalValue", "*.optionalKey","*.totalHitCount", "*.searchResults", "*.count", "*.totalPages" };

	static final String LIBRARY_COLLECTION_INCLUDE_FIELDS[] = { "*.narrationLink", "collection.notes", "*.keyPoints", "*.language", "*.goals", "*.grade", "*.estimatedTime", "*.collectionType", "*.assetURI", "*.license", "*.license.code", "*.license.icon", "*.license.name", "*.license.definition",
			"*.dimensions", "*.defaultImage", "*.createdOn", "*.description", "*.folder", "*.gooruOid", "*.sharing", "*.title", "*.views", "*.thumbnails", "*.url", "*.vocabulary", "*.collaborators", "*.network", "*.buildType", "*.buildType.value", "*.resourceFormat", "*.resourceFormat.value",
			"*.resourceFormat.displayName" };

	static final String COLLABORATORI_INCLUDE[] = { "*.emailId", "*.firstName", "*.gooruUId", "*.lastName", "*.partyUid", "*.userId", "*.username", "*.username" };

	static final String COLLECTION_META_INFO[] = { "*.vocabulary", "*.course", "*.standards", "*.code", "*.description", "*.rating", "*.score", "*.count", "*.average", "*.votesUp", "*.votesDown", "*.votes", "*.acknowledgement", "*.metaInfo", "*.taxonomySetMapping", "*.lastModifiedUser" };

	static final String COLLECTION_ITEM_TAGS[] = { "*.tagSet", "*.label", "*.type", "*.resourceType" };

	static final String COLLECTION_TAXONOMY[] = { "*.taxonomySet", "*.taxonomySetMapping.*", "*.lastModifiedUser" };

	static final String COLLECTION_WORKSPACE[] = { "*.resourceCount" };

	static final String COLLECTION_CREATE_ITEM_INCLUDE_FILEDS[] = { "*.collectionItemId", "*.itemSequence", "*.itemType", "*.narration", "*.narrationType", "*.lastModified", "*.start", "*.stop", "*.standards", "*.license", "*.standards.code", "*.standards.description", "*.resourceInfo",
			"*.lastModifiedUser" };

	static final String CUSTOM_VALUE_EXCLUDE[] = { "*.resourceFormat", "*.resourceFormat.customTable", "*.resourceFormat.customTableValueId", "*.resourceFormat.displayName", "*.instructional.customTable", "*.instructional.customTableValueId", "*.instructional.displayName" };

	static final String EVENT_NAME = "eventName";

	static final String CREATED_TYPE = "created_type";

	static final String CREATED_DATE = "created_date";

	static final String ORGANIZATION_ID = "organization_id";

	static final String GOORU_OID = "gooruOid";

	static final String CONTENT_ID = "contentId";

	static final String ASSESSMENT_INCLUDES[] = { "segments", "segments.segmentQuestions", "segments.segmentQuestions.question.hints", "segments.segmentQuestions.question.answers", "taxonomySet", "segments.segmentQuestions.question.assets", "metaData", "metaData.grades", "metaData.lessons",
			"metaData.units", "metaData.subjects", "metaData.collaborators", "metaData.topics", "metaData.curriculumCodes", "metaData.curriculumDecs", "metaData.taxonomyMapByCode.*", "metaData.taxonomyLevels.*", "tagSet" };

	static final String SEGMENT_INCLUDES[] = { "segmentQuestions", "segmentQuestions.question.hints", "segmentQuestions.question.answers", "segmentQuestions.question.assets" };

	static final String ATTEMPT_INCLUDES[] = { "attemptItems", "attemptItems.question.hints", "attemptItems.question.answers", "attemptItems.question.assets" };

	static final String ATTEMPT_ANSWER_INCLUDES[] = { "question.hints", "question.answers", "question.assets" };

	static final String ASSESSMENT_BASIC_EXCLUDES[] = { "*.class", "segments", "*.userRoleSet.*", "*.taxonomyLevels.organization.*", "*.code.organization*", "attempts", "*.attempts" };

	static final String ASSESSMENT_DETAILED_EXCLUDES[] = { "*.class" };

	static final String CUSTOM_SETTING_INCLUDES[] = { "*.key", "*.value" };

	static String ORGANIZATION_INCLUDES[] = { "subGroups" };

	static final String RESOURCE_EXCLUDES[] = { "*.class", "*.resourceMetaData", "*.resourceMetaData.*", "*.userPermSet", "*.grpMbrshipSet" };

	static final String[] REVISION_HISTORY_LIST_EXCLUDE = new String[] { "dataList.data" };

	static final String[] GET_SHELF_RESOURCES_EXCLUDES = { "*.addDate", "*.description", "*.creator", "shelf.activeFlag", "shelf.defaultFlag", "shelf.depth", "shelf.shelfParentId", "shelf.shelfType", "shelf.userId", "*.contentType", "*.contentPermissions", "*.license", "resource.recordSource",
			"resource.batchId", "resource.brokenStatus", "resource.customFieldValues", "resource.customFields", "resource.distinguish", "resource.entryId", "resource.fileHash", "resource.fromQa", "resource.isFeatured", "resource.isFeaturedBoolean", "resource.isFolderAbsent", "resource.isLive",
			"resource.isNew", "resource.lastModified", "resource.lastModifiedString", "resource.lessonsString", "resource.resourceTypeByString", "resource.hasFrameBreaker", "resource.new", "resource.numberOfSubcribers", "resource.parentUrl", "resource.resourceSource", "resource.resourceInfo",
			"resource.s3UploadFlag", "resource.siteName", "resource.social", "resource.sourceReference", "resource.subscriptionCount", "resource.tags", "resource.text", "resource.user.confirmStatus", "resource.user.registerToken", "resource.user.entryId", "resource.user.parentUser",
			"resource.user.userRoleSetString", "resource.userUploadedImage", "resource.vocaularyString", "resource.user.userId", "*.resourceInstances", "*.associatedCodes", "*.resourceTypeByString", "*.taxonomySet", "*.resourceSegments", "*.fileData", "*.contentId", "*.codes", "*.userPermSet",
			"*.folders", "*.shelfCategory", "*.shelfItems", "shelf.name", "resource.user.accountTypeId", "resource.user.contentSet", "resource.user.emailId", "resource.user.firstName", "resource.user.lastName", "resource.user.usernameDisplay", "resource.user.username",
			"resource.resourceType.description", "*.resourceLearnguides", "*.imageUrl", "*.createdOn", "*.viewCount", "*.views", "*.createdOn" };

	static final String GROUP_INCLUDES[] = { "*.partyUid", "*.groupName", "*.userGroupType" };

	static final String[] EXCLUDE_USER = { "*.class" };

	static final String EXCLUDES_COMMENT[] = { "*.taxonomySet", "*.user", "*.userPermSet", "*.sharing", "*.responseId", "*.response", "*.resource", "*.lastModifiedString", "*.lastModified", "*.image", "*.freetext", "*.entryId", "*.displayTime", "*.creator", "*.contentType", "*.contentId",
			"*.annotationType", "*.anchor", "commentor.accountTypeId", "*.accountTypeId", "*.confirmStatus", "*.contentSet", "*.emailId", "*.entryId", "*.parentUser", "*.profileImageUrl", "*.registerToken", "*.userId", "*.userRoleSetString" };

	static final String EXCLUDES_FEATURED_CONTENT[] = { "*.class", "*.creator", "*.s3UploadFlag", "*.vocabulary", "*.vocabularyInfo", "*.isFolderAbsent", "*.contentPermissions", "*.hasFrameBreaker", "*.customFieldValues", "*.userUploadedImage", "*.notes", "*.vocaularyString", "*.license",
			"*.lastModified", "*.lastModifiedString", "*.lessonsString", "*.resourceTypeByString", "*.lessonCode", "*.linkedAssessmentTitle", "*.linkedCollectionTitle", "*.requestPending", "*.socialData", "*.activeFlag", "*.batchId", "*.numberOfSubcribers", "*.narration", "*.fromQa", "*.createdOn",
			"*.migrated", "*.distinguish", "*.resourceInstances", "*.resourceSegments", "*.addDate", "*.brokenStatus", "*.collaborators", "*.contentId", "*.contentType", "*.duration", "*.entryId", "*.fileData", "*.fileHash", "*.imageUrl", "*.instructionInfo", "*.isFeatured", "*.isFeaturedBoolean",
			"*.isLive", "*.isNew", "*.medium", "*.new", "*.parentUrl", "*.recordSource", "*.resourceLearnguides", "*.siteName", "*.sourceReference", "*.userPermSet", "featuredSetItems.content", "*.assets", "*.explanation", "*.hints", "*.themeCode", "*.viewCount", "*.views", "*.text", "*.social",
			"*.userVote", "*.voteDown", "*.voteUp", "*.assessmentGooruOid", "*.assessmentLink", "*.collectionLink", "*.collectionGooruOid", "*.source", "*.assessmentCode", "*.concept", "*.difficultyLevel", "*.groupedQuizIds", "*.groupedQuizNames", "*.helpContentLink", "*.importCode",
			"*.instruction", "*.label", "*.quizNetwork", "*.score", "*.scorePoints", "*.organization", "scollections.user.accountCreatedType", "scollections.user.accountTypeId", "scollections.user.confirmStatus", "scollections.user.emailId", "user.firstName", "scollections.user.isDeleted",
			"user.lastName", "scollections.user.loginType", "scollections.user.parentUser", "scollections.user.profileImageUrl", "scollections.user.registerToken", "scollections.user.userId", "scollections.user.userRoleSetString", "scollections.user.username", "scollections.user.viewFlag",
			"*.contentPermissions", "scollections.collectionItem", "scollections.collectionItems", "*.courseSet", "*.curriculum", "*.taxonomySet", "*.codes", "type", "user.customFields", "collections", "scollections.user.customFields.category", "scollections.user.customFields.party",
			"collections.user.customFields.party", "collections.user.customFields.category" };

	static final String INCLUDES_FEATURED_LIST[] = { "resources", "collections", "questions", "questions.answers", "questions.hints", "questions.assets", "scollections", "*.metaInfo" };

	static final String INCLUDES_THEME_LIST[] = { "resources", "questions", "questions.answers", "questions.hints", "questions.assets", "scollections", "*.metaInfo" };

	static final String COLLECTION_EXCLUDES[] = { "*" };

	static final String COLLECTION_INCLUDES[] = { "title", "gooruOid", "segments.id", "segments.title" };

	static final String RESOURCE_STATISTICS_EXCLUDES[] = { "*.class", "*.segment", "*.notes", "*.resourceInstances", "*.license", "*.addDate", "*.batchId", "*.brokenStatus", "*.category", "*.codes", "*.collaborators", "*.collectionLink", "*.collectionLink", "*.contentType", "*.courseSet",
			"*.createdOn", "*.accountTypeId", "*.confirmStatus", "*.contentSet", "*.emailId", "*.entryId", "*.parentUser", "*.profileImageUrl", "*.registerToken", "*.userId", "*.curriculum", "*.customFieldValues", "*.customFields", "*.distinguish", "*.duration", "*.fileData", "*.fileHash",
			"*.fromQa", "*.goals", "*.grade", "*.hasFrameBreaker", "*.imageUrl", "*.instructionInfo", "*.isFeatured", "*.isFeaturedBoolean", "*.isFolderAbsent", "*.isLive", "*.isNew", "*.lastModified", "*.lastModifiedString", "*.linkedAssessmentTitle", "*.linkedCollectionTitle", "*.medium",
			"*.migrated", "*.narration", "*.new", "*.numberOfSubcribers", "*.parentUrl", "*.recordSource", "*.requestPending", "*.resourceInfo", "*.resourceLearnguides", "*.resourceSegments", "*.resourceSource", "*.contentPermissions", "*.s3UploadFlag", "*.resourceTypeByString", "*.resourceType",
			"*.sharing", "*.siteName", "*.taxonomySet", "*.social", "*.source", "*.sourceReference", "*.subscriptionCount", "*.tags", "*.taxonomyContentData", "*.taxonomyMapByCode", "*.text", "*.userPermSet", "*.userUploadedImage", "*.viewCount", "*.vocabulary", "*.vocabularyInfo",
			"*.vocaularyString", "*.assessmentGooruOid", "*.assessmentLink", "*.userRoleSetString", "*.lessonsString" };

	static final String RESOURCE_INSTANCES_INCLUDES[] = { "*.class", "segment", "*.creator" };

	static final String RESOURCE_COLLECTIONS_INCLUDES[] = { "*.class", "segment", "*.user", "*.creator" };

	static final String COLLECTION_CONTENT_INCLUDE[] = { "content" };

	static final String RESOURCES_ARRAY_INCLUDES[] = { "*.class", "resources", "classplanInfo", "*.userRole" };

	static final String RESOURCES_ARRAY_LEARNGUIDE_INCLUDES[] = { "*.class", "resources", "classplanInfo", "*.userRole", "*.customFieldValues" };

	static final String TAXONOMY_CODES_EXCLUDES[] = { "class", "description" };

	static final String RESULT_STRING_EXCLUDES[] = { "*.class", "*.segmentCount", "*.courseSet", "*.identities", "*.userRoleSet", "*.contentSet", "*.grpMbrshipSet", "*.userPermSet", "*.userPermSet", "*.collectionPageCount" };

	static final String COLLECTION_SEGMENTS_INCLUDES[] = { "*.class, *.storageAccount" };

	static final String TITLE_INCLUDES[] = { "class", "codetypes" };

	static final String FEEDBACK_INCLUDES[] = { "anchor", "class", "annotationType", "contentId", "contentType", "context", "createdOn", "freetext", "lastModified", "resource", "score", "sharing", "tagType", "user" };

	static final String RATING_INCLUDES[] = new String[] { "anchor", "*.class", "annotationType", "contentId", "contentType", "context", "createdOn", "freetext", "lastModified", "resource", "score", "sharing", "tagType", "user", "revisionHistoryUid", "lastUpdatedUserUid", "lastModifiedString",
			"contentPermissions", "*.point", "*.type", "*.votesUp", "*.votesDown" };

	static final String POINT_INCLUDES[] = { "*.rating", "*.point", "*.count", "sum", "avg", "points" };

	static final String RESOURCE_SOURCE_EXCLUDES[] = { "*.class", "*.resourceSource" };

	static final String ROLLBACK_EXCLUDES[] = { "segments.segmentQuestions", "segments.segmentQuestions.question.resourceType", "segments.segmentQuestions.question.license", "*.resource.resourceType", "*.license" };

	static final String CREATE_SHELF_EXCLUDES[] = { "*.shelfItems", "*.folders", "*.shelfParentId", "*.depth", "*.shelfType" };

	static final String ADD_RESOURCE_TO_SHELF_EXCLUDES[] = { "*.shelfItems", "*.folders", "*.shelfParentId", "*.depth", "*.shelfType" };

	static final String USER_SHELF_EXCLUDES[] = { "*.shelfItems", "*.folders", "*.shelfParentId", "*.depth", "*.shelfType" };

	static final String UPDATE_SHELF_EXCLUDES[] = { "*.shelfItems", "*.folders", "*.shelfParentId", "*.depth", "*.shelfType" };

	static final String MOVE_SHELF_CONTENT_EXCLUDES[] = { "*.shelfItems", "*.folders", "*.shelfParentId", "*.depth", "*.shelfType" };

	static final String MARK_SHELF_AS_DEFAULT_EXCLUDES[] = { "*.shelfItems", "*.folders", "*.shelfParentId", "*.depth", "*.shelfType" };

	static final String RENAME_MY_SHELF_EXCLUDES[] = { "*.shelfItems", "*.folders", "*.shelfParentId", "*.depth", "*.shelfType" };

	static final String UPDATE_SHELF_STATUS_EXCLUDES[] = { "*.shelfItems", "*.folders", "*.shelfParentId", "*.depth", "*.shelfType" };

	static final String ANNOTATION_JSON_EXCLUDES[] = { "resource", "user" };

	static final String TAG_INCLUDES[] = { "*.tagType", "*.label", "*.status", "*.contentCount", "*.createdOn", "*.userCount", "*.content", "*.totalHitCount", "*.synonymsCount", "*.wikiPostGooruOid", "*.excerptPostGooruOid", "*.value", "*.gooruOid" };

	static final String TAG_ASSOC_INCLUDES[] = { "*.tag", "*.tagUid", "*.label", "*.type", "*.activeFlag", "*.createdOn" };

	static final String SESSION_INCLUDES[] = { "*.sessionId", "*.status", "*.startTime", "*.stopTime", "*.sessionItems", "*.score" };

	static final String TASK_INCLUDES[] = { "*.collectionTasks", "*.creator.gooruUId", "*.creator.firstName", "*.creator.username", "*.creator.lastname", "*.taskUid", "*.title", "*.description", "*.plannedStartDate", "*.plannedEndDate", "*.status", "*.estimatedEffort", "*.typeName", "*.gooruOid",
			"*.lastModified", "*.createdOn", "*.lastUpdatedUserUid", "*.searchResults", "*.totalHitCount", "task" };

	static final String TASK_HISTORY_INCLUDES[] = { "*.taskHistoryUid", "*.taskContentId", "*.userUid", "*.createdDate", "*.taskHistoryItems" };

	static final String TASK_HISTORY_ITEM_INCLUDES[] = { "*.taskHistoryItemUid", "*.taskHistory", "*.fieldName", "*.oldKey", "*.oldValue", "*.newKey", "*.newValue" };

	static final String TASK_RESOURCE_ASSOC_INCLUDES[] = { "*.taskResourceAssocs", "*.task", "*.taskUid", "*.title", "*.description", "*.plannedStartDate", "*.plannedEndDate", "*.status", "*.estimatedEffort", "*.createdDate", "*.createdByUid", "*.lastModifiedDate", "*.modifiedBy", "*.typeName",
			"*.resource", "*.gooruOid", "*.associationDate", "*.user", "*.associatedByUid", "*.sequence", "*.taskResourceAssocUid", "*.user", "*.associationType", "*.totalHitCount", "*.searchResults" };

	static final String TASK_CREATE_RESOURCE_ASSOC_INCLUDES[] = { "*.task", "*.taskUid", "*.title", "*.description", "*.plannedStartDate", "*.plannedEndDate", "*.status", "*.estimatedEffort", "*.createdDate", "*.createdByUid", "*.lastModifiedDate", "*.modifiedBy", "*.typeName", "*.resource",
			"*.gooruOid", "*.associationDate", "*.user", "*.associatedByUid", "*.sequence", "*.taskResourceAssocUid", "*.user", "*.associationType", "*.totalHitCount", "*.searchResults" };

	static final String TASK_USER_ASSOC_INCLUDES[] = { "*.task", "user", "*.taskUid", "*.title", "*.description", "*.plannedStartDate", "*.plannedEndDate", "*.status", "*.estimatedEffort", "*.createdDate", "*.createdByUid", "*.lastModifiedDate", "*.modifiedBy", "*.typeName", "*.associationType",
			"*.lastName", "*.gooruUId", "*.profileImageUrl", "*.loginType", "*.userRoleSetString", "*.username", "*.viewFlag", "*.createdOn", "*.restEndPoint", "*.confirmStatus", "*.emailId", "*.token", "*.partyUid", "*.firstName", "*.usernameDisplay", "*.accountCreatedType" };

	static final String TASK_ASSOC_INCLUDES[] = { "*.task", "*.taskUid", "*.title", "*.description", "*.plannedStartDate", "*.plannedEndDate", "*.status", "*.estimatedEffort", "*.createdDate", "*.createdByUid", "*.lastModifiedDate", "*.modifiedBy", "*.typeName", "*.taskAssocUid", "*.taskParent",
			"*.taskDescendant", "*.sequence", "*.associationType" };

	static final String SESSION_ITEM_INCLUDES[] = { "*.score", "*.session", "*.resource", "*.sessionId", "*.status", "*.startTime", "*.stopTime", "*.title", "*.sessionItemId", "*.sessionItemAttemptTry" };

	static final String SESSION_ITEM_ATTEMPT_INCLUDES[] = { "*.score", "*.sessionItem", "*.sessionItemId", "*.attemptItemTryStatus", "*.answeredAtTime" };

	static final String COURSE_INCLUDES[] = { "*.parentsList" };

	static final String[] CURRICULUM_INCLUDES = { "*.code", "*.codeId", "*.label", "*.depth" };

	static final String COURSE_EXCLUDES[] = { "*.code", "*.codeImage", "*.codeType", "*.codeUid", "*.depth", "*.s3UploadFlag", "*.description", "*.associatedCodes", "*.displayOrder", "*.entryId", "*.parent", "*.s3UploadFlag", "*.taxonomyImageUrl", "*.taxonomySet", "*.rootNodeId" };

	static final String CURRICULUM_EXCLUDES[] = { "*.assetURI", "*.depth", "*.indexId", "*.indexType", "*.parentId", "*.parentsList", "*.codeOrganizationAssoc", "*.libraryFlag", "*.creator", "*.thumbnails", "*.displayCode", "*.activeFlag", "*.grade", "*.codeUid", "*.codeImage", "*.codeType",
			"*.codeUid", "*.s3UploadFlag", "*.description", "*.associatedCodes", "*.displayOrder", "*.entryId", "*.parent", "*.s3UploadFlag", "*.taxonomyImageUrl", "*.taxonomySet", "*.rootNodeId" };

	static final String PARTY_CUSTOM_INCLUDES[] = { "*.partyCustomField", "*.category", "*.optionalKey", "*.optionalValue" };

	static final String PARTY_FIELDS_INCLUDES[] = { "fields", "optionalKey", "optionalValue" };

	static final String INVALID_DATA_MESSAGE = "Invalid data input";

	static final String QUIZ_INCLUDES[] = { "*.isRandomize", "*.isRandomizeChoice", "*.showHints", "*.showScore", "*.showCorrectAnswer", "*.option" };

	static final String USER_TYPE = "user";

	static final String FIELDS = "fields";

	static final Integer LIMIT = 20;

	static final Integer OFFSET = 0;

	static final String ADDED = "added";

	static final String SUBSCRIBED = "subscribed";

	static final String SKIP_PAGINATION = "skipPagination";

	static final String YES = "yes";

	static final String NO = "no";

	static final String DATE = "date";

	static final String RESOURCE_DELETE = "resource.delete";

	static final String RESPONSE_CLASS_RESOURCE = "org.ednovo.gooru.controllers.api.ResourceRestController";

	static final String COLLECTION_TASK_ITEM_INCLUDE_FIELDS[] = { "*.collectionTaskItems", "*.task", "*.collection", "*.collectionTaskAssocUid", "*.sequence", "*.classpageCode", "*.totalHitCount", "*.searchResults" };

	static final String COLLECTION_CREATE_TASK_ITEM_INCLUDE_FIELDS[] = { "*.collectionTaskAssocUid", "*.sequence", "*.task", "*.collection", "*.totalHitCount", "*.searchResults" };

	static final String COMMENT_INCLUDES[] = { "*.comment", "*.commentorUid", "*.commentUid", "*.gooruOid", "*.user", "*.optionalValue", "*.createdOn", "*.usernameDisplay", "*.gooruUId", "*.profileImageUrl", "*.lastName", "*.firstName", "*.username", "*.content", "*.statusType", "*.name",
			"*.totalHitCount", "*.searchResults", "*.isDeleted", "*.lastModifiedOn" };

	static final String FOLLOWED_BY_USERS_INCLUDES[] = { "*.totalHitCount", "*.searchResults", "*.username", "*.profileImageUrl", "*.firstName", "*.gooruUid", "*.emailId", "*.lastName", "*.course", "*.summary", "*.summary.tags", "*.summary.collection", "*.summary.followers", "*.summary.following","*.customFields","*.PartyUid","*.category","*.optionalKey","*.optionalValue"};
	
	static final String FOLLOW_USER_INCLUDES[] = {"*.username", "*.profileImageUrl", "*.firstName", "*.gooruUid", "*.emailId", "*.lastName", "*.course", "*.summary", "*.summary.tags", "*.summary.collection", "*.summary.followers", "*.summary.following","*.customFields","*.PartyUid","*.category","*.optionalKey","*.optionalValue"};

	static final String USER_INCLUDES[] = { "*.lastName", "*.gooruUId", "*.profileImageUrl", "*.loginType", "*.userRoleSetString", "*.username", "*.viewFlag", "*.createdOn", "*.restEndPoint", "*.confirmStatus", "*.emailId", "*.token", "*.partyUid", "*.firstName", "*.usernameDisplay", "*.user",
			"*.accountCreatedType", "*.dateOfBirth", "*.meta.*", "*.taxonomyPreference.*", "*.taxonomyPreference.code.*", "*.meta.taxonomyPreference.code.*", "*.metaData.*", "*.meta.taxonomyPreference.metaData.code.*", "*.meta.taxonomyPreference.metaData.*", "*.meta.taxonomyPreference.*",
			"*.customFields.*", "*.accountTypeId", "*.organizationName", "*.userRole", "*.active" };

	static final String RESET_PASSWORD_REQUEST_INCLUDES[] = { "*.username", "*.accountTypeId", "*.firstName", "*.gooruUId", "*.emailId" };

	static final String RESET_PASSWORD_INCLUDES[] = { "*.active", "*.externalId", "*.lastLogin", "*.registeredOn", "*.ssoEmailId" };

	static final String[] USER_EXCLUDES = { "*.user.entryId" };

	static final String[] AVAILABILITY_INCLUDES = { "*.confirmStatus", "*.gooruUId", "*.userName", "*.collaboratorCheck", "*.externalId", "*.availability" };

	static final String CURRENT_SESSION_TOKEN = "currentSessionToken";

	static final String[] USER_PROFILE_INCUDES = { "user", "user.emailId", "*.user.meta.*", "*.meta.*", "*.userType", "*.graduation", "*.city", "*.teachingIn", "*.twitter", "*.highestDegree", "*.profileId", "grade", "*.gender", "*.name", "*.genderId", "*.website", "*.thumbnailBlobStatus",
			"*.school", "*.subscribers", "*.subscribers", "*.lastName", "*.parentUser", "*.username", "*.accountCreatedType", "*.gooruUId", "*.partyUid", "*.profileImageUrl", "*.createdOn", "*.usernameDisplay", "*.loginType", "*.confirmStatus", "*.viewFlag", "*.userRoleSetString", "*.firstName",
			"*.accountTypeId", "*.isPublisherRequestPending", "*.dateOfBirth", "*.isFollowing", "*.aboutMe", "courses", "courses.code.codeId", "courses.code.label", "*.notes", "*.emailId", "*.summary", "*.summary.tags", "*.summary.collection", "*.summary.follower", "*.summary.following", "*.active" };

	static final String ACCESS_DENIED_EXCEPTION = "You are not authorized to perform this action";

	static final String[] OPTIONS_INCLUDE = { "*.isRandomize", "*.isRandomizeChoice", "*.showCorrectAnswer", "*.showHints", "*.showScore" };

	static final String FEEDBACK_INCLUDE_FIELDS[] = { "*.searchResults", "*.feedback", "*.totalHitCount", "*.gooruOid", "*.target.value", "*.type.value", "*.category.value", "*.freeText", "*.score", "*.assocGooruOid", "*.assocUserUid", "*.creator.gooruUId", "*.creator.username",
			"*.creator.firstName", "*.creator.lastName", "*.referenceKey", "*.createdDate", "*.lastModifiedOn", "*.ratings", "*.ratings.count", "*.ratings.average" };

	static final String[] CONTENT_INCLUDES = { "gooruOid", "sharing", "createdOn", "lastModified", "lastUpdatedUserUid", "statusType", "statusType.value", "isDeleted", "", "", "", "", "", "" };

	static final String CUSTOM_VALUE_INCLUDE[] = { "value" };

	static final String CONTENT_ASSOC_INCLUDES[] = { "*.contentGooruOid", "*.tagGooruOid" };

	static final String USER_ASSOC_INCLUDES[] = { "*.user", "*.tagGooruOid" };

	static final String TAG_LABEL[] = { "*.label", "*.gooruOid" };

	static final Integer MAX_RATING_POINT = 5;

	static final Integer MIN_RATING_POINT = 1;

	static final Integer THUMB_UP = 1;

	static final Integer THUMB_DOWN = -1;

	static final Integer THUMB_NETURAL = 0;

	static final String TAG_SYNONYM_INCLUDES[] = { "*.tagSynonymsId", "*.createdOn", "*.approvalOn", "*.targetTagName", "*.status", "*.tagContentGooruOid", "*.value" };

	static final String POST_INCLUDE_FIELDS[] = { "*.title", "*.freeText", "*.type", "*.assocGooruOid", "*.assocUserUid", "*.target", "*.gooruOid", "*.createdOn", "*.value", "*.status" };

	static final String COLLECTION_TASK_INCLUDES[] = { "*.task", "*.collectionTasks", "collectionTasks.collection.gooruOid", "collectionTasks.task", "collectionTasks.task.creator.gooruUId", "collectionTasks.task.creator.firstName", "collectionTasks.task.creator.username",
			"collectionTasks.task.creator.lastName", "collectionTasks.task.title", "task.description", "collectionTasks.task.plannedStartDate", "collectionTasks.task.plannedEndDate", "collectionTasks.task.status", "collectionTasks.task.estimatedEffort", "collectionTasks.task.typeName",
			"collectionTasks.task.gooruOid", "collectionTasks.task.lastModified", "collectionTasks.collectionIds", "collectionTasks.task.createdOn", "collectionTasks.task.lastUpdatedUserUid", "*.totalCollectionCount", "task", "*.totalHitCount", "collectionTasks.task.description" };

	static final String VIEWS = "views";

	static final String TEMPLATES_INCLUDES[] = { "gooruOid", "htmlContent", "textContent", "createdDate", "subject" };

	static final String EVENT_INCLUDES[] = { "gooruOid", "name", "createdDate" };

	static final String EVENT_MAPPING_INCLUDES[] = { "event.gooruOid", "event.name", "*.createdDate", "template.gooruOid", "template.htmlContent", "template.textContent", "template.subject", "status.value", "data" };

	static final String EVENT_TYPE = "eventType";

	static final String FEEDBACK_GOORU_OID = "feedback_gooruOid";

	static final String FEEDBACK_GOORU_UID = "feedback_gooruUid";

	static final String EXCLUDE[] = { "*.class", "*" };

	static final String INCLUDE_ITEMS[] = { "lesson", "createdOn", "lastModified", "goals", "sharing", "resourceCount", "chapterCount", "customFieldValues.*", "gooruOid", "securityGroups.*", "thumbnails.*", "creator.gooruUId", "creator.firstName", "creator.lastName", "creator.username",
			"creator.emailId", "creator.confirmStatus", "creator.registerToken", "creator.userRoleSetString", "creator.parentUser", "creator.profileImageUrl", "creator.usernameDisplay", "creator.entryId", "creator.userId", "creator.accountTypeId" };

	static final String TAXONOMY_CODE_INCLUDES[] = { "elementrySchool", "middleSchool", "highSchool", "other", "taxonomyCodes", "*.code", "*.codeId", "*.code", "*.label", "*.parentId", "*.thumbnails", "*.thumbnails.url", "*.thumbnails.dimensions", "*.thumbnails.isDefaultImage",
			"*.creator.username", "*.creator.gooruUId", "*.creator.firstName", "*.creator.lastName" };

	static final String APPLICATION_INCLUDES[] = { "key", "appName", "appURL", "secretKey", "activeFlag", "searchLimit", "description", "comment", "status" };

	static final String ORGANIZATION_INCLUDES_ADD[] = { "partyUid", "partyName", "partyType", "organizationCode" };

	static final String ORGANIZATION_SETTING_INCLUDE[] = { "key", "value" };

	static final String LIBRARY_CODE_INCLUDES[] = { "*.unit", "*.lesson", "*.count", "*.topic", "*.data", "*.course", "*.grade", "*.collection", "*.featured", "*.science", "*.math", "*.social-sciences", "*.language-arts", "*.code", "*.codeId", "*.code", "*.label", "*.parentId", "*.thumbnails",
			"*.thumbnails.url", "*.thumbnails.dimensions", "*.thumbnails.isDefaultImage", "*.gooruOid", "*.title", "*.creator", "*.firstName", "*.lastName", "*.username", "*.gooruUId", "*.metaInfo", "*.gender", "*.isOwner", "*.user", "*.standard", "*.concept", "*.node", "*.grade", "*.meta",
			"*.ideas", "*.questions", "*.performanceTasks" };

	static final String LIBRARY_CONTRIBUTOR_INCLUDES[] = { "*.courses", "*.codeId", "*.code", "*.codeId", "*.code", "*.label", "*.parentId", "*.creator", "*.firstName", "*.lastName", "*.username", "*.gooruUId", "*.gooruUId", "*.isFeatured", "*.gender" };

	static final String LIBRARY_RESOURCE_INCLUDE_FIELDS[] = { "*.resource", "*.assetURI", "*.brokenStatus", "*.category", "*.createdOn", "*.usernameDisplay", "*.profileImageUrl", "*.lastName", "*.firstName", "*.username", "*.description", "*.distinguish", "*.folder", "*.gooruOid", "*.resourceType",
			"*.resourceType.name", "*.sharing", "*.title", "*.views", "*.thumbnails", "*.url", "*.license", "*.license.code", "*.license.icon", "*.license.name", "*.license.definition", "*.dimensions", "*.defaultImage", "*.resourceSource", "*.attribution", "*.sourceName", "*.questionInfo",
			"*.TYPE", "*.type", "*.name", "*.questionText", "*.assets", "*.assets.asset", "*.assets.asset.name", "*.assets.asset.url", "*assets.asset.description", "*assets.asset.hasUniqueName", "*.description", "*.explanation", "*.lastModified", "*.gooruUId", "*.lastUpdatedUserUid", "*.category",
			"*.label", "*.code", "*.userRating", "*.hasFrameBreaker", "*.copiedResourceId", "*.assignmentContentId", "*.trackActivity", "*.trackActivity.startTime", "*.trackActivity.endTime", "*.goals", "*.grade", "*.mediaType", "*.text", "*.isOer", "*.ratings", "*.average", "*.count" };

	static final String[] LIBRARY_FEATURED_COLLECTIONS_INCLUDE_FIELDS = { "*.totalHitCount", "*.searchResults", "*.libraryCollection", "*.comment", "*.collaborators", "*.network", "*.description", "*.grade", "*.gooruOid", "*.createdOn", "*.lastModified", "*.sharing", "*.title", "*.views",
			"*.thumbnails", "*.subjectCode", "*.themeCode", "*.metaInfo", "*.standard", "*.collaborators", "*.meta", "*.meta.collaboratorCount", "*.meta.isCollaborator", "*.meta.commentCount", "*.metaInfo.standards", "*.thumbnails.defaultImage", "*.thumbnails.dimensions", "*.thumbnails.url",
			"*.taxonomySet", "*.taxonomySet.code", "*.taxonomySet.codeId", "*.taxonomySet.label", "*.standards", "*.standards.code", "*.standards.description", "*.taxonomyMappingSet.*", "*.taxonomyMappingSet" };

	static final String[] LIBRARY_TAXONOMY_COLLECTIONS_INCLUDE_FIELDS = { "*.totalHitCount", "*.searchResults", "*.libraryCollection" };

	static final String[] LIBRARY_FEATURED_COLLECTIONS_USER_INCLUDE_FIELDS = { "*.totalHitCount", "*.searchResults", "*.libraryCollection", "*.lastModifiedUser", "*.lastUpdatedUserUid", "*.commentsCount", "*.collectionItemCount", "*.lastModifiedBy", "*.modifiedDate", "*.user.gooruUId",
			"*.user.username", "*.user.lastName", "*.user.firstName", "*.creator", "*.creator.gooruUId", "*.creator.username", "*.creator.lastName", "*.creator.firstName", "*.featuredSetId" };

	static final String[] LIB_RESOURCE_FIELDS = { "*.totalHitCount", "*.searchResults", "*.libraryResource", "*.collectionId", "*.resourceId", "*.title", "*.thumbnails", "*.resourceUrl", "*.grade", "*.description", "*.category", "*.sharing", "*.hasFrameBreaker", "*.recordSource", "*.license",
			"*.resourceSourceId", "*.sourceName", "*.domainName", "*.attribution", "*.narration", "*.start", "*.stop", "*.collectionItemId", "*.type", "*.standards", "*.course" };

	static final String[] STUDY_RESOURCE_FIELDS = { "*.totalHitCount", "*.searchResults", "*.classCode", "*.status", "*.title", "*.gooruOid", "*.createdOn", "*.gooruUId", "*.user", "*.userName", "*.lastName", "*.firstName", "*.thumbnails", "*.url", "*.itemCount", "*.memberCount",
			"*.profileImageUrl" };

	static final String[] CLASS_MEMBER_FIELDS = { "*.totalHitCount", "*.searchResults", "*.emailId", "*.username", "*.gooruUid", "*.associatedDate", "*.status", "*.profileImageUrl" };

	static final String[] USER_CONTENT_TAGS_INCLUDES = { "*.totalHitCount", "*.searchResults", "*.count", "*.label", "*.tagGooruOid" };

	static final String[] COLLECTION_STANDARDS_INCLUDES = { "searchResults", "*.code", "*.label", "*.codeUid", "*.codeId" };

	static final String SUPER_ADMIN_TOKEN = "super.admin.token";

	static final String ORG_ADMIN_KEY = "organizationAdmin";

	static final String USER_FLAG_EXCLUDE_FIELDS[] = { "*.class", "segment", "*.creator" };

	static final String CUSTOM_VALUE_INCLUDE_FIELDS[] = { "displayName" };

	public static final String OAUTH_CLIENT_INCLUDES[] = { "*.oauthClientUId", "*.clientId", "*.clientName", "*.description", "*.clientSecret", "*.scopes", "*.grantTypes", "*.authorities", "*.redirectUris", "*.accessTokenValiditySeconds", "*.refreshTokenValiditySeconds" };

	public static final String CONSUMER_SECRET_INCLUDES[] = { "*.consumerKey", "*.consumerSecret", "*.organization" };

	public static final String PROFANITY_INCLUDES[] = { "*.found", "*.foundBy", "*.text", "*.count", "*.expletive" };

	public static final String WEBPURIFY_API_KEY = "2b9dbbde0e50edf204a6e742cfdd79bc";

	public static final String WEBPURIFY_CONFIG_KEY = "webpurify.com.api.key";

	public static final String WEBPURIFY_API_END_POINT = "http://api1.webpurify.com/services/rest";

	public static final String METHOD = "webpurify.live.check";

	public static final String LIST_METHOD = "webpurify.live.return";

	public static final String RESPONSE_FORMAT = "json";

	public static final String DEFAULT_ROLES = "Teacher,User";

	public static final String ISSUE = "6";

	public static final String PID = "10681";

	public static final String COMPONENTS = "11120";

	public static final String JIRA_REPORTER = "purnima";

	public static final String RESOURCEFORMAT = "resourceFormat";

	public static final String KEY_VALUE = "keyValue";

	public static final String SEARCH_RESULT = "searchResult";

	public static final String NFS = "NFS";

	public static final String DISPLAY_NAME = "displayName";

	public static final String CLASS = "class";

	public static final String IS_PARTNER = "is_partner";

	public static final String UPDATE = "update";

	public static final String CLASSPAGE_CREATE_COLLECTION_TASK_ITEM = "classpage-create-collection-task-item";

	static final String[] SESSION_ITEM_FEEDBACK_INCLUDES = { "sessionItemFeedbackUid", "freeText", "gooruOid", "associatedDate", "user.gooruUId", "sessionId", "feedbackProvidedBy.gooruUId", "contentGooruOId", "contentItemId", "parentGooruOId", "parentItemId", "createdOn" };

	public static String IS_FEATURED_USER = "is_featured";

	public static String FEATURED_USER = "isFeaturedUser";

	public static String NODE = "node";

	public static String SEQUENCE_DESC = "sequence-desc";

	public static String DUE_DATE = "due-date";
	
	public static String DUE_DATE_EARLIEST = "due-date-earliest";
	
	public static String PUBLISH_STATUS = "publishStatus";
	
	public static String SAUSD = "sausd";

	public static String USER_CHECK_USERNAMEOREMAILID_AVAILABILITY = "user.check.usernameOremailid.availability";

	public static String USER_FORGET_PASSWORD="user.forgot_password";
	
	public static String USER_DELETE_USER="user.delete_user";
	
    public static String USER_DELETE_PROFILE_PICTURE="user.delete_profile_picture";
    
    public static String CHECK_CONTENT_PERMISSION="check-content-permission";
    
    public static String TOKEN_NOT_FOUND="token not found";
    
    public static String WELCOME="WELCOME";
    
    public static String PROFILE_ACTION="profile.action";
   
    public static String VISIT_UID="visitorUId";
    
    public static String VISIT="visit";
   
    public static String REGISTER_TYPE="register.type";
   
     public static String FOLLOWING="following";
    
    public static String FOLLOWERS="followers";
    
    public static String FOLLOW="follow";
    
    public static String UN_FOLLOW="unfollow";    
   
    public static String CUSTOM_FIELDS="customFields";
    
    public static String PAY_LOAD_OBJECT="payLoadObject";
    
    public static String ORGANIZATION_UID="organizationUId";
    
    public static String USER_SWITCH_SESSION="user.switch-session";
    
    public static String USER_AUTHENTICATE="user-authenticate";
    
    public static String APPS="Apps";    
    
    public static String CLASSPAGE_ITEM_UPDATE="classpage-item-update";
    
    public static String CLASS_CODE="classCode";
    
    public static String MEMBER_COUNT="memberCount";
    
    public static String ITEM_COLLABORATE="item.collaborate";
    
    public static String SOURCE_GOORU_UID="sourceGooruId";
    
    public static String TARGET_GOORU_UID="targetGooruId";
    
    public static String TARGET_ITEM_ID="targetItemId";
    
    public static String COLLABORATED_ID="collaboratedId";
    
    public static String CREATE_POST="create-post";
    
    public static String UPDATE_POST= "update-post";
    
    public static String POST_ID="postId";
    
    public static String ASSOCIATION_DATE="associationDate";
    
    public static String INVITED_USER_GOORU_UID="InvitedUserGooruUId";
    
    public static String PARENT_CONTENT_ID="parentContentId";
    
    public static String CLASSPAGE_USER_ADD="classpage.user.add";
    
    public static String COLLECTION_RESOURCE="collection.resource";
    
    public static String FOLDER_COLLECTION="folder.collection";
    
    public static String SHELF_COLLECTION="shelf.collection";
    
    public static String CLASSPAGE_COLLECTION="classpage.collection";
    
    public static String CLASSPAGE_USER_REMOVE= "classpage.user.remove";
    
    public static String REMOVE_GOORU_UID="removedGooruUId";
    
    public static String ITEM_CREATE="item.create";
    
    public static String COLLECTION_ALL="collection.all";
    
    public static String PARTY_CUSTOMFIELD="Party customfield";
    
    public static String PARTY="party";
    
    public static String QUESTION_UPDATE="question-update";
    
    public static String DISPLAY_ORDER="displayOrder";
    
    public static String ENTRY_ID="entryId";
    
    public static String INDEX_ID="indexId";
    
    public static String INDEX_TYPE="indexType";
    
    public static String COMMON_CORE_DOT_NOTATION="commonCoreDotNotation";
    
    public static String TAXONOMY_MAPPING_SET="taxonomyMappingSet";
    
    public static String LIBRARY_COLLECTION="libraryCollection";
    
    public static String FEATURE_SETID="featuredSetId";
    
    public static String RECORD_SOURCE="recordSource";
    
    public static String COMMENT_COUNT="commentCount";
    
   public static String TASK_CREATE_TASK="task.create_task";
   
   public static String TASK_UPDATE_TASK="task.update_task";
   
   public static String TASK_GET_TASK="task.get_task";
   
   public static String TASK_GET_TASK_ASSOC="task.get_task_assoc";
   
   public static String TASK_CREATE_TASK_ASSOC="task.create_task_assoc";
   
   public static String TASK_GET_TASK_HISTORY="task.get_task_history";
   
   public static String LIBRARY_CODE_JSON="library-code-json";
   
   public static String CURRICULAM_CODE_JSON="curriculum-code-json";
   
    public static String DEPTH_OF_KNOWLEDGE = "depth_of_knowledge";
	
	public static String LEARNING_AND_INNOVATION_SKILLS = "learning_and_innovation_skills";

	public static String AUDIENCE = "audience";

	public static String  INSTRUCTIONAL_METHOD = "instructional_method";

	public static String NOT_DELETED = "notdeleted";

	public static String CONTENT_META_ASSOCIATION_TYPE = "content_meta_association_type_";
	
	public static String INVALID_COLLABORATOR = "Invalid Collaborator";
	
	public static String V2_ORGANIZE_DATA = "v2-organize-data-";

	public static String CONTENT_ASSOCIATION_TYPE = "content_association_type";
 
	public static String MOMENTS_OF_LEARNING = "moments_of_learning";
	
    public static String INSTRUCTIONAL = "instructional";

    public static String RESOURCE_NOT_FOUND = "Resource Not Found";

    public static String COLLECTION_CREATE = "collection.create";
    
    public static String  ITEM_ID = "ItemId";
    
    public static String  CONTENT_ITEM_ID = "contentItemId";

    public static String  PARENT_ITEM_ID = "parentItemId";

    public static String  ITEM_DATA = "ItemData";
    
    public static String  ITEM_DELETE = "item.delete";
    
    public static String ITEM_EDIT = "item.edit";
    
    public static String CONTENTID = "contentId";
    
    public static String COMMENT_TEXT = "commentText";
    
    public static String REQUEST_PATH = "request path";
    
    public static String PERMISSION  = "permission denied";
    
    public static String NOT_FOUND = "not found";
    
    public static String ASSOCIATEDU_ID = "associatedUid";

    public static String SEARCH_PROFILE = "search.profile";

    public static String SETTING_VERSION = "setting.version";
    
    public static String STATISTICS_DATA = "statisticsData";
 
    public static String _PUBLISH_STATUS ="publish_status";
    
    public static String FOLDER_FOLDER = "folder.folder";
    
    public static String SHELF_FOLDER = "shelf.folder";
    
    public static String _ITEM_DATA = "itemData";
    
    public static String _COPY = "copy";
    
   
}
