-- MySQL dump 10.13  Distrib 5.5.34, for debian-linux-gnu (x86_64)
--
-- Host: 162.243.70.48    Database: gooru_dev
-- ------------------------------------------------------
-- Server version	5.5.34-32.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Invites`
--

DROP TABLE IF EXISTS `Invites`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Invites` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Priority` int(11) DEFAULT '4',
  `FirstName` varchar(100) DEFAULT NULL,
  `LastName` varchar(100) DEFAULT NULL,
  `Email` varchar(100) NOT NULL,
  `School` varchar(200) DEFAULT NULL,
  `Groups` varchar(100) DEFAULT NULL,
  `TimesInvited` int(11) DEFAULT '0',
  `LastDateInvited` date DEFAULT NULL,
  `message` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=816 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account` (
  `account_uid` varchar(36) NOT NULL,
  `account_code` varchar(128) NOT NULL,
  `parent_account_uid` varchar(36) DEFAULT NULL,
  `s3_storage_area_id` int(11) unsigned NOT NULL,
  `nfs_storage_area_id` int(11) unsigned NOT NULL,
  `active_flag` tinyint(1) unsigned NOT NULL,
  `organization_uid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`account_uid`),
  KEY `parent_organization_uid` (`parent_account_uid`),
  KEY `organization_uid` (`organization_uid`),
  CONSTRAINT `account_ibfk_1` FOREIGN KEY (`parent_account_uid`) REFERENCES `account` (`account_uid`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `activity`
--

DROP TABLE IF EXISTS `activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `activity` (
  `activity_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned DEFAULT NULL,
  `type_name` varchar(50) CHARACTER SET latin1 NOT NULL,
  `content_id` bigint(20) unsigned DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `account_uid` varchar(36) DEFAULT NULL,
  `user_uid` varchar(36) DEFAULT NULL,
  `organization_uid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`activity_id`),
  KEY `FK_activity_user` (`user_id`),
  KEY `FK_activity_content` (`content_id`),
  KEY `FK_activity_type` (`type_name`),
  KEY `account_uid` (`account_uid`),
  KEY `user_uid` (`user_uid`),
  KEY `organization_uid` (`organization_uid`),
  CONSTRAINT `activity_ibfk_1` FOREIGN KEY (`account_uid`) REFERENCES `account` (`account_uid`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_activity_content` FOREIGN KEY (`content_id`) REFERENCES `content` (`content_id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `FK_activity_type` FOREIGN KEY (`type_name`) REFERENCES `activity_type` (`name`) ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=43383 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `activity_log`
--

DROP TABLE IF EXISTS `activity_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `activity_log` (
  `activity_log_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `event_id` varchar(36) NOT NULL,
  `event_name` varchar(100) NOT NULL,
  `event_time` datetime NOT NULL,
  `type` varchar(5) NOT NULL,
  `user_ip` varchar(255) DEFAULT NULL,
  `user_id` int(10) unsigned DEFAULT NULL,
  `content_id` bigint(20) unsigned DEFAULT NULL,
  `parent_content_id` bigint(20) unsigned DEFAULT NULL,
  `context` varchar(100) DEFAULT NULL,
  `session_token` varchar(40) DEFAULT NULL,
  `account_uid` varchar(36) DEFAULT NULL,
  `user_uid` varchar(36) DEFAULT NULL,
  `organization_uid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`activity_log_id`),
  KEY `account_uid` (`account_uid`),
  KEY `user_uid` (`user_uid`),
  KEY `organization_uid` (`organization_uid`)
) ENGINE=MyISAM AUTO_INCREMENT=669287 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `activity_stream`
--

DROP TABLE IF EXISTS `activity_stream`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `activity_stream` (
  `stream_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned DEFAULT NULL,
  `type_name` varchar(50) CHARACTER SET latin1 NOT NULL,
  `sharing` enum('public','private') CHARACTER SET latin1 NOT NULL,
  `account_uid` varchar(36) DEFAULT NULL,
  `user_uid` varchar(36) DEFAULT NULL,
  `organization_uid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`stream_id`),
  KEY `FK_activity_stream_user` (`user_id`),
  KEY `FK_activity_stream_type` (`type_name`),
  KEY `account_uid` (`account_uid`),
  KEY `user_uid` (`user_uid`),
  KEY `organization_uid` (`organization_uid`),
  CONSTRAINT `activity_stream_ibfk_1` FOREIGN KEY (`account_uid`) REFERENCES `account` (`account_uid`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_activity_stream_type` FOREIGN KEY (`type_name`) REFERENCES `activity_type` (`name`) ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=432070 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `activity_summary`
--

DROP TABLE IF EXISTS `activity_summary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `activity_summary` (
  `activity_summary_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `event_id` varchar(36) DEFAULT NULL,
  `event_name` varchar(100) NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime DEFAULT NULL,
  `elapsed_time` bigint(20) DEFAULT NULL,
  `user_ip` varchar(255) NOT NULL,
  `user_id` int(10) unsigned DEFAULT NULL,
  `content_id` bigint(20) unsigned DEFAULT NULL,
  `parent_content_id` bigint(20) unsigned DEFAULT NULL,
  `context` varchar(100) DEFAULT NULL,
  `session_token` varchar(40) DEFAULT NULL,
  `created_time` datetime NOT NULL,
  `account_uid` varchar(36) DEFAULT NULL,
  `user_uid` varchar(36) DEFAULT NULL,
  `organization_uid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`activity_summary_id`),
  KEY `account_uid` (`account_uid`),
  KEY `user_uid` (`user_uid`),
  KEY `organization_uid` (`organization_uid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `activity_type`
--

DROP TABLE IF EXISTS `activity_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `activity_type` (
  `name` varchar(50) CHARACTER SET latin1 NOT NULL,
  `description` varchar(100) DEFAULT NULL,
  `active_flag` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `annotation`
--

DROP TABLE IF EXISTS `annotation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `annotation` (
  `content_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `type_name` varchar(20) NOT NULL,
  `resource_id` bigint(20) unsigned DEFAULT NULL,
  `anchor` varchar(2000) DEFAULT NULL,
  `freetext` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`content_id`),
  KEY `FK_annotation_type` (`type_name`),
  KEY `FK_annotation_resource` (`resource_id`),
  CONSTRAINT `FK_annotation_content` FOREIGN KEY (`content_id`) REFERENCES `content` (`content_id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `FK_annotation_resource` FOREIGN KEY (`resource_id`) REFERENCES `resource` (`content_id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `FK_annotation_type` FOREIGN KEY (`type_name`) REFERENCES `annotation_type` (`name`) ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `annotation_type`
--

DROP TABLE IF EXISTS `annotation_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `annotation_type` (
  `name` varchar(20) NOT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `api_activity`
--

DROP TABLE IF EXISTS `api_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `api_activity` (
  `api_activity_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `api_key_id` int(11) unsigned NOT NULL,
  `count` int(11) unsigned NOT NULL,
  PRIMARY KEY (`api_activity_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `api_key`
--

DROP TABLE IF EXISTS `api_key`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `api_key` (
  `api_key_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `api_key` varchar(36) DEFAULT NULL,
  `api_limit` int(11) DEFAULT NULL,
  `account_uid` varchar(36) DEFAULT NULL,
  `organization_uid` varchar(36) DEFAULT NULL,
  `active_flag` tinyint(4) NOT NULL DEFAULT '1',
  `search_limit` int(11) DEFAULT NULL,
  `custom_setting_id` int(10) DEFAULT NULL,
  `secret_key` varchar(255) DEFAULT NULL,
  `app_name` varchar(255) DEFAULT NULL,
  `last_modified_date` timestamp NULL DEFAULT NULL,
  `last_modified_by_uid` varchar(36) DEFAULT NULL,
  `description` text,
  `app_url` varchar(255) DEFAULT NULL,
  `key` varchar(36) DEFAULT NULL,
  `limit` int(11) DEFAULT NULL,
  `comment` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`api_key_id`),
  KEY `account_uid` (`account_uid`),
  KEY `organization_uid` (`organization_uid`),
  CONSTRAINT `api_key_ibfk_1` FOREIGN KEY (`account_uid`) REFERENCES `account` (`account_uid`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=169 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `assessment`
--

DROP TABLE IF EXISTS `assessment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assessment` (
  `assessment_id` bigint(20) unsigned NOT NULL,
  `import_code` varchar(50) DEFAULT NULL,
  `name` varchar(225) NOT NULL,
  `description` text,
  `distinguish` tinyint(1) unsigned DEFAULT '0',
  `medium` varchar(50) DEFAULT 'English',
  `grade` varchar(50) DEFAULT NULL,
  `learning_objectives` text,
  `time_to_complete_in_secs` int(11) DEFAULT NULL COMMENT 'The time allowed to complete the Quiz. Ignored if Ã¢â‚¬Å“Is TimedÃ¢â‚¬Â is No.',
  `is_random` tinyint(1) NOT NULL COMMENT 'If Yes, the Questions will be presented in random order. Any sequencing manually done in the Quiz items will be ignored.',
  `is_choice_random` tinyint(1) NOT NULL COMMENT 'If Yes, the (response) Choices for each question will be presented in random order. Applicable to MCQ and Match type of questions.',
  `show_hints` tinyint(1) NOT NULL COMMENT 'If Yes, an option to view hints can be enabled, if the question has hints available.',
  `show_score` tinyint(1) NOT NULL COMMENT 'If Yes, the score will be shown at the end of Quiz. Defaults to Yes',
  `show_correct_answer` tinyint(1) NOT NULL COMMENT 'If Yes, the Correct Answer will be shown (either during review / or right after the student answers )',
  `question_count` int(10) DEFAULT NULL COMMENT 'Used to store count of questions contained in the assessment.\nThough it''s possible to count the questions everytime, this is a bit of optimization.',
  `source` text,
  `vocabulary` text,
  `collection_gooru_oid` varchar(36) DEFAULT NULL,
  `quiz_gooru_oid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`assessment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `assessment_answer`
--

DROP TABLE IF EXISTS `assessment_answer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assessment_answer` (
  `answer_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `question_id` bigint(20) unsigned NOT NULL,
  `answer_text` text NOT NULL,
  `unit` varchar(40) DEFAULT NULL,
  `is_correct` tinyint(1) NOT NULL COMMENT 'Yes, if Correct answer. More than one answer per question can have this flag as Yes.',
  `sequence` int(10) unsigned NOT NULL COMMENT 'Sequence of the choice for display',
  `matching_answer_id` int(10) unsigned DEFAULT NULL,
  `type` varchar(40) DEFAULT NULL,
  `answer_hint` text,
  `answer_explanation` text,
  `answer_group_code` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`answer_id`),
  KEY `question_id` (`question_id`),
  CONSTRAINT `ga_question_answer_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `assessment_question` (`question_id`)
) ENGINE=InnoDB AUTO_INCREMENT=333937 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `assessment_attempt`
--

DROP TABLE IF EXISTS `assessment_attempt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assessment_attempt` (
  `attempt_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `assessment_id` bigint(20) unsigned NOT NULL,
  `student_id` int(10) unsigned DEFAULT NULL,
  `status` int(10) unsigned NOT NULL,
  `score` int(10) unsigned NOT NULL,
  `mode` int(10) unsigned NOT NULL,
  `start_time` datetime NOT NULL COMMENT 'Time at which the question was presented',
  `end_time` datetime DEFAULT NULL COMMENT 'Time at which the question was answered(if answered)',
  `student_uid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`attempt_id`),
  KEY `quiz_id` (`assessment_id`),
  KEY `fk_quiz_attempt_1` (`assessment_id`),
  KEY `fk_quiz_attempt_2` (`student_id`),
  KEY `student_uid` (`student_uid`),
  CONSTRAINT `fk_quiz_attempt_1` FOREIGN KEY (`assessment_id`) REFERENCES `assessment` (`assessment_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=13419 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `assessment_attempt_item`
--

DROP TABLE IF EXISTS `assessment_attempt_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assessment_attempt_item` (
  `attempt_item_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `attempt_id` int(10) unsigned DEFAULT NULL,
  `question_id` bigint(20) unsigned NOT NULL,
  `presented_at_time` datetime DEFAULT NULL,
  `answered_at_time` datetime DEFAULT NULL,
  `answer_id` int(10) unsigned DEFAULT NULL,
  `answer_text` text COMMENT 'Captures any raw text entered by user.',
  `attempt_status` int(10) unsigned NOT NULL DEFAULT '0',
  `correct_try_id` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`attempt_item_id`),
  KEY `fk_quiz_attempt_item_1` (`attempt_id`),
  KEY `fk_quiz_attempt_item_2` (`question_id`),
  KEY `fk_quiz_attempt_item_3` (`answer_id`),
  CONSTRAINT `fk_quiz_attempt_item_1` FOREIGN KEY (`attempt_id`) REFERENCES `assessment_attempt` (`attempt_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_quiz_attempt_item_2` FOREIGN KEY (`question_id`) REFERENCES `assessment_question` (`question_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_quiz_attempt_item_3` FOREIGN KEY (`answer_id`) REFERENCES `assessment_answer` (`answer_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=69368 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `assessment_attempt_try`
--

DROP TABLE IF EXISTS `assessment_attempt_try`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assessment_attempt_try` (
  `attempt_item_id` int(11) NOT NULL,
  `answer_id` int(10) NOT NULL,
  `try_sequence` tinyint(4) NOT NULL,
  `answer_text` text,
  `attempt_try_status` tinyint(4) NOT NULL,
  `answered_at_time` datetime NOT NULL,
  PRIMARY KEY (`attempt_item_id`,`answer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `assessment_hint`
--

DROP TABLE IF EXISTS `assessment_hint`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assessment_hint` (
  `hint_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `question_id` bigint(20) unsigned NOT NULL,
  `hint_text` text NOT NULL,
  `sequence` int(10) unsigned NOT NULL COMMENT 'Sequence of the choice for display',
  PRIMARY KEY (`hint_id`),
  KEY `question_id` (`question_id`),
  CONSTRAINT `ga_question_answer_ibfk_10` FOREIGN KEY (`question_id`) REFERENCES `assessment_question` (`question_id`)
) ENGINE=InnoDB AUTO_INCREMENT=49560 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `assessment_question`
--

DROP TABLE IF EXISTS `assessment_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assessment_question` (
  `question_id` bigint(20) unsigned NOT NULL COMMENT 'Primary Key',
  `import_code` varchar(255) DEFAULT NULL,
  `type` int(10) unsigned NOT NULL COMMENT 'Type of Question such as MC Question,Fill-in,Matching,Short-answer',
  `difficulty_level` int(11) unsigned DEFAULT NULL,
  `concept` text,
  `question_text` text NOT NULL,
  `help_content_link` mediumtext COMMENT 'Link to some help content for this question. Can be a video, link to another URL.',
  `source_content_info` text COMMENT 'Source info for display during rendering Ã¢â‚¬â€œ (From 2009 Taks test, etc. or Copyright info',
  `score_points` int(11) DEFAULT NULL,
  `time_to_complete_in_secs` int(11) DEFAULT NULL,
  `explanation` text,
  `description` text,
  `folder` varchar(10) DEFAULT NULL,
  `source` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `assessment_question_asset_assoc`
--

DROP TABLE IF EXISTS `assessment_question_asset_assoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assessment_question_asset_assoc` (
  `question_id` bigint(20) unsigned NOT NULL,
  `asset_id` int(11) unsigned NOT NULL,
  `asset_key` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`question_id`,`asset_id`),
  KEY `fk_assessment_question_assoc_asset1` (`asset_id`),
  CONSTRAINT `fk_assessment_question_assoc_asset1` FOREIGN KEY (`asset_id`) REFERENCES `asset` (`asset_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_assessment_question_assoc_question1` FOREIGN KEY (`question_id`) REFERENCES `assessment_question` (`question_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `assessment_segment`
--

DROP TABLE IF EXISTS `assessment_segment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assessment_segment` (
  `segment_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `segment_uid` varchar(36) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `assessment_id` bigint(20) unsigned NOT NULL,
  `sequence` int(11) NOT NULL DEFAULT '1',
  `time_to_complete_in_secs` int(11) DEFAULT NULL,
  PRIMARY KEY (`segment_id`),
  KEY `fk_assessment_segment_assessment1` (`assessment_id`),
  CONSTRAINT `fk_assessment_segment_assessment1` FOREIGN KEY (`assessment_id`) REFERENCES `assessment` (`assessment_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3668 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `assessment_segment_question_assoc`
--

DROP TABLE IF EXISTS `assessment_segment_question_assoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assessment_segment_question_assoc` (
  `segment_id` int(10) unsigned NOT NULL,
  `question_id` bigint(20) unsigned NOT NULL,
  `sequence` int(10) unsigned NOT NULL,
  PRIMARY KEY (`segment_id`,`question_id`),
  KEY `fk_assessment_segment_question_assessment_question1` (`question_id`),
  CONSTRAINT `fk_assessment_segment_question_assessment_question1` FOREIGN KEY (`question_id`) REFERENCES `assessment_question` (`question_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_assessment_segment_question_assessment_segment1` FOREIGN KEY (`segment_id`) REFERENCES `assessment_segment` (`segment_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `asset`
--

DROP TABLE IF EXISTS `asset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `asset` (
  `asset_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `url` varchar(2000) DEFAULT NULL,
  `description` text,
  `has_unique_name` tinyint(1) unsigned DEFAULT NULL,
  `account_uid` varchar(36) DEFAULT NULL,
  `organization_uid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`asset_id`),
  KEY `account_uid` (`account_uid`),
  KEY `organization_uid` (`organization_uid`),
  CONSTRAINT `asset_ibfk_1` FOREIGN KEY (`account_uid`) REFERENCES `account` (`account_uid`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=18406 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `assignment`
--

DROP TABLE IF EXISTS `assignment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assignment` (
  `assignment_content_id` bigint(20) unsigned NOT NULL,
  `activity_uid` varchar(36) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `badge_activity`
--

DROP TABLE IF EXISTS `badge_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `badge_activity` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned DEFAULT NULL,
  `owner_id` int(10) unsigned DEFAULT NULL,
  `content_id` int(10) unsigned DEFAULT NULL,
  `referred_content_id` int(10) unsigned DEFAULT NULL,
  `predicate` varchar(50) NOT NULL,
  `timestamp` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `account_uid` varchar(36) DEFAULT NULL,
  `user_uid` varchar(36) DEFAULT NULL,
  `owner_uid` varchar(36) DEFAULT NULL,
  `organization_uid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_userToken_badge_activity_user` (`user_id`),
  KEY `FK_userToken_badge_activity_owner` (`owner_id`),
  KEY `account_uid` (`account_uid`),
  KEY `user_uid` (`user_uid`),
  KEY `owner_uid` (`owner_uid`),
  KEY `organization_uid` (`organization_uid`),
  CONSTRAINT `badge_activity_ibfk_1` FOREIGN KEY (`account_uid`) REFERENCES `account` (`account_uid`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2076 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `city`
--

DROP TABLE IF EXISTS `city`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `city` (
  `country_id` varchar(3) NOT NULL,
  `state_province_id` varchar(5) NOT NULL,
  `city_id` varchar(5) NOT NULL,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`city_id`),
  KEY `FK_city_country` (`country_id`),
  KEY `FK_city_state_province` (`state_province_id`),
  CONSTRAINT `FK_city_country` FOREIGN KEY (`country_id`) REFERENCES `country` (`country_id`) ON UPDATE NO ACTION,
  CONSTRAINT `FK_city_state_province` FOREIGN KEY (`state_province_id`) REFERENCES `state_province` (`state_province_id`) ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `classbook_narration`
--

DROP TABLE IF EXISTS `classbook_narration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `classbook_narration` (
  `classbook_id` varchar(100) NOT NULL,
  `resource` varchar(100) NOT NULL,
  `segment_seq` int(11) NOT NULL,
  `resource_seq` int(11) NOT NULL,
  `narration` longtext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `classpage`
--

DROP TABLE IF EXISTS `classpage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `classpage` (
  `classpage_content_id` bigint(20) NOT NULL,
  `classpage_code` varchar(36) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `code`
--

DROP TABLE IF EXISTS `code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `code` (
  `code_id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `code_uid` varchar(36) DEFAULT NULL,
  `label` varchar(2000) NOT NULL DEFAULT '',
  `code` varchar(50) NOT NULL DEFAULT '',
  `display_order` int(10) unsigned NOT NULL DEFAULT '0',
  `parent_id` mediumint(8) unsigned DEFAULT NULL,
  `type_id` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `description` varchar(2000) DEFAULT '',
  `depth` tinyint(3) unsigned NOT NULL,
  `root_node_id` int(11) NOT NULL DEFAULT '20000',
  `account_uid` varchar(36) DEFAULT NULL,
  `code_image` varchar(256) DEFAULT NULL,
  `grade` int(11) DEFAULT NULL,
  `s3_upload_flag` tinyint(1) unsigned DEFAULT '0',
  `active_flag` int(2) NOT NULL DEFAULT '1',
  `organization_uid` varchar(36) DEFAULT NULL,
  `code_guid` varchar(40) DEFAULT NULL,
  `uri` varchar(255) DEFAULT NULL,
  `common_core_dot_notation` varchar(40) DEFAULT NULL,
  `display_code` varchar(50) DEFAULT NULL,
  `is_featured` tinyint(1) DEFAULT NULL,
  `creator_uid` varchar(36) DEFAULT NULL,
  `library_flag` tinyint(4) DEFAULT '0',
  `sequence` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`code_id`),
  UNIQUE KEY `UK_code` (`code`) USING HASH,
  KEY `FK_code_parent_id` (`parent_id`),
  KEY `FK_code_taxonomyLevel` (`type_id`) USING BTREE,
  KEY `account_uid` (`account_uid`),
  KEY `organization_uid` (`organization_uid`),
  CONSTRAINT `code_ibfk_1` FOREIGN KEY (`account_uid`) REFERENCES `account` (`account_uid`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_code_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `code` (`code_id`) ON DELETE CASCADE,
  CONSTRAINT `FK_code_type_id` FOREIGN KEY (`type_id`) REFERENCES `taxonomy_level_type` (`type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=72720 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `code_organization_assoc`
--

DROP TABLE IF EXISTS `code_organization_assoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `code_organization_assoc` (
  `code_id` int(11) NOT NULL,
  `is_featured` tinyint(4) DEFAULT NULL,
  `sequence` tinyint(4) DEFAULT NULL,
  `organization_code` varchar(36) NOT NULL,
  PRIMARY KEY (`code_id`,`organization_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `code_user_assoc`
--

DROP TABLE IF EXISTS `code_user_assoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `code_user_assoc` (
  `code_id` int(11) NOT NULL,
  `user_uid` varchar(36) NOT NULL,
  `organization_code` varchar(36) NOT NULL,
  `is_owner` varchar(36) NOT NULL,
  UNIQUE KEY `code_id` (`code_id`,`organization_code`,`user_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `collection`
--

DROP TABLE IF EXISTS `collection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `collection` (
  `content_id` bigint(20) unsigned NOT NULL,
  `collection_type` varchar(30) NOT NULL,
  `narration_link` varchar(255) DEFAULT NULL,
  `notes` text,
  `key_points` text,
  `language` varchar(30) DEFAULT NULL,
  `goals` text,
  `grade` varchar(50) DEFAULT NULL,
  `estimated_time` varchar(10) DEFAULT NULL,
  `network` varchar(256) DEFAULT NULL,
  `build_type_id` int(11) DEFAULT NULL,
  `mail_notification` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`content_id`),
  CONSTRAINT `collection_ibfk_1` FOREIGN KEY (`content_id`) REFERENCES `content` (`content_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `collection_item`
--

DROP TABLE IF EXISTS `collection_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `collection_item` (
  `collection_item_id` varchar(36) NOT NULL,
  `collection_content_id` bigint(20) unsigned NOT NULL,
  `resource_content_id` bigint(20) unsigned NOT NULL,
  `item_type` varchar(30) DEFAULT NULL,
  `item_sequence` int(11) unsigned NOT NULL DEFAULT '0',
  `narration` text,
  `narration_type` varchar(30) DEFAULT NULL,
  `start` varchar(10) DEFAULT NULL,
  `stop` varchar(10) DEFAULT NULL,
  `planned_end_date` datetime DEFAULT NULL,
  `association_date` datetime DEFAULT NULL,
  `associated_by_uid` varchar(36) DEFAULT NULL,
  UNIQUE KEY `collection_item_id` (`collection_item_id`),
  KEY `collection_content_id` (`collection_content_id`),
  KEY `resource_content_id` (`resource_content_id`),
  CONSTRAINT `collection_item_ibfk_1` FOREIGN KEY (`collection_content_id`) REFERENCES `content` (`content_id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `collection_item_ibfk_2` FOREIGN KEY (`resource_content_id`) REFERENCES `content` (`content_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `collection_task_assoc`
--

DROP TABLE IF EXISTS `collection_task_assoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `collection_task_assoc` (
  `collection_task_assoc_uid` varchar(36) CHARACTER SET utf8 NOT NULL,
  `collection_content_id` bigint(20) unsigned DEFAULT NULL,
  `task_uid` varchar(36) CHARACTER SET utf8 DEFAULT NULL,
  `sequence` int(11) DEFAULT NULL,
  `association_date` datetime DEFAULT NULL,
  `associated_by_uid` varchar(36) CHARACTER SET utf8 DEFAULT NULL,
  `task_content_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`collection_task_assoc_uid`),
  KEY `collection_content_id` (`collection_content_id`),
  KEY `task_content_id` (`task_content_id`),
  CONSTRAINT `collection_task_assoc_ibfk_1` FOREIGN KEY (`task_content_id`) REFERENCES `task` (`task_content_id`) ON DELETE CASCADE,
  CONSTRAINT `collection_task_assoc_ibfk_2` FOREIGN KEY (`collection_content_id`) REFERENCES `content` (`content_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `comment` (
  `comment_id` bigint(20) DEFAULT NULL,
  `comment` varchar(500) DEFAULT NULL,
  `image` blob,
  `response_id` bigint(20) DEFAULT NULL,
  `commentor` int(10) unsigned DEFAULT NULL,
  `content_gooru_oid` varchar(36) DEFAULT NULL,
  `content_id` bigint(20) DEFAULT NULL,
  `date_posted` datetime DEFAULT NULL,
  `commentor_uid` varchar(36) DEFAULT NULL,
  `comment_uid` varchar(36) NOT NULL,
  `post_gooru_oid` varchar(36) NOT NULL,
  `created_date` datetime NOT NULL,
  `status_id` int(11) unsigned NOT NULL,
  `organization_uid` varchar(36) NOT NULL,
  `is_deleted` tinyint(1) DEFAULT NULL,
  `last_modified_on` datetime DEFAULT NULL,
  KEY `commentor_uid` (`commentor_uid`),
  KEY `content_id` (`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `config_setting`
--

DROP TABLE IF EXISTS `config_setting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `config_setting` (
  `name` varchar(50) NOT NULL,
  `value` text NOT NULL,
  `security_level` tinyint(4) NOT NULL DEFAULT '0',
  `account_uid` varchar(36) DEFAULT NULL,
  `profile_id` varchar(32) DEFAULT 'default-profile',
  `organization_uid` varchar(36) DEFAULT NULL,
  UNIQUE KEY `config_setting_unique` (`name`,`profile_id`),
  KEY `account_uid` (`account_uid`),
  KEY `organization_uid` (`organization_uid`),
  CONSTRAINT `config_setting_ibfk_1` FOREIGN KEY (`account_uid`) REFERENCES `account` (`account_uid`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `consumer_account`
--

DROP TABLE IF EXISTS `consumer_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `consumer_account` (
  `consumer_account_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `admin_email` varchar(60) NOT NULL,
  `domain` varchar(255) NOT NULL,
  `title` varchar(255) NOT NULL,
  `active` int(10) DEFAULT NULL,
  `challenge` varchar(100) DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `organization_uid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`consumer_account_id`),
  UNIQUE KEY `domain` (`domain`,`active`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `content`
--

DROP TABLE IF EXISTS `content`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `content` (
  `content_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `account_uid` varchar(36) DEFAULT NULL,
  `gooru_oid` varchar(36) NOT NULL DEFAULT '',
  `type_name` varchar(20) NOT NULL,
  `user_id` int(10) unsigned DEFAULT NULL,
  `creator_id` int(11) unsigned DEFAULT NULL,
  `sharing` enum('public','private','anyonewithlink') NOT NULL,
  `created_on` datetime DEFAULT NULL,
  `last_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `last_updated_user_uid` varchar(36) DEFAULT NULL,
  `revision_history_uid` varchar(36) DEFAULT NULL,
  `user_uid` varchar(36) DEFAULT NULL,
  `status_type` int(11) DEFAULT NULL,
  `creator_uid` varchar(36) DEFAULT NULL,
  `organization_uid` varchar(36) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`content_id`),
  UNIQUE KEY `idx_unique_gooru_oid` (`gooru_oid`) USING HASH,
  KEY `FK_content_content_type` (`type_name`),
  KEY `FK_content_user` (`user_id`),
  KEY `idx_gooru_content_id` (`gooru_oid`),
  KEY `organization_uid` (`account_uid`),
  KEY `user_uid` (`user_uid`),
  KEY `creator_uid` (`creator_uid`),
  KEY `organization_uid_2` (`organization_uid`),
  KEY `creator_id` (`creator_id`),
  KEY `status_type` (`status_type`),
  CONSTRAINT `FK_content_content_type` FOREIGN KEY (`type_name`) REFERENCES `content_type` (`name`) ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2461070 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `content_association`
--

DROP TABLE IF EXISTS `content_association`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `content_association` (
  `content_association_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content_id` bigint(20) unsigned NOT NULL,
  `associate_content_id` bigint(20) unsigned NOT NULL,
  `type_of` varchar(100) CHARACTER SET utf8 NOT NULL,
  `modified_by` int(10) unsigned DEFAULT NULL,
  `modified_date` datetime NOT NULL,
  `modified_by_uid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`content_association_id`),
  KEY `associate_content_id` (`associate_content_id`),
  KEY `content_id` (`content_id`),
  KEY `modified_by` (`modified_by`),
  KEY `content_association_id` (`content_association_id`),
  KEY `modified_by_uid` (`modified_by_uid`),
  CONSTRAINT `content_association_ibfk_1` FOREIGN KEY (`content_id`) REFERENCES `content` (`content_id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `content_association_ibfk_2` FOREIGN KEY (`associate_content_id`) REFERENCES `content` (`content_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1372 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `content_classification`
--

DROP TABLE IF EXISTS `content_classification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `content_classification` (
  `content_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `code_id` mediumint(8) unsigned NOT NULL,
  PRIMARY KEY (`content_id`,`code_id`),
  KEY `FK_content_classification_content` (`content_id`),
  KEY `FK_content_classification_code` (`code_id`),
  CONSTRAINT `FK_content_classification_code_id` FOREIGN KEY (`code_id`) REFERENCES `code` (`code_id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `FK_content_classification_content` FOREIGN KEY (`content_id`) REFERENCES `content` (`content_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `content_permission`
--

DROP TABLE IF EXISTS `content_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `content_permission` (
  `content_id` bigint(20) unsigned NOT NULL,
  `party_uid` varchar(36) NOT NULL,
  `permission` varchar(36) NOT NULL,
  `valid_from` datetime DEFAULT NULL,
  `expiry_date` datetime DEFAULT NULL,
  PRIMARY KEY (`content_id`,`party_uid`),
  KEY `content_id` (`content_id`),
  KEY `party_uid` (`party_uid`),
  KEY `access_type` (`permission`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `content_tag_assoc`
--

DROP TABLE IF EXISTS `content_tag_assoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `content_tag_assoc` (
  `content_gooru_oid` varchar(36) NOT NULL,
  `tag_gooru_oid` varchar(36) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `content_type`
--

DROP TABLE IF EXISTS `content_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `content_type` (
  `name` varchar(20) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `country`
--

DROP TABLE IF EXISTS `country`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `country` (
  `country_id` varchar(3) NOT NULL,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`country_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `custom_field_text`
--

DROP TABLE IF EXISTS `custom_field_text`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_field_text` (
  `resource_gooru_oid` varchar(40) NOT NULL,
  `column_text_001` text,
  `column_text_002` text,
  `column_text_003` text,
  `column_text_004` text,
  `column_text_005` text,
  `column_text_006` text,
  `column_text_007` text,
  `column_text_008` text,
  `column_text_009` text,
  `column_text_010` text,
  `column_text_011` text,
  `column_text_012` text,
  `column_text_013` text,
  `column_text_014` text,
  `column_text_015` text,
  `column_text_016` text,
  `column_text_017` text,
  `column_text_018` text,
  `column_text_019` text,
  `column_text_020` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `custom_fields`
--

DROP TABLE IF EXISTS `custom_fields`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_fields` (
  `custom_field_id` varchar(40) NOT NULL,
  `account_uid` varchar(40) NOT NULL,
  `name` varchar(120) NOT NULL,
  `display_name` varchar(120) NOT NULL,
  `type` varchar(20) NOT NULL,
  `length` bigint(20) NOT NULL,
  `is_required` tinyint(4) NOT NULL DEFAULT '1',
  `data_column_name` varchar(40) NOT NULL,
  `add_to_search` tinyint(4) NOT NULL DEFAULT '0',
  `group_code` varchar(120) NOT NULL DEFAULT 'default',
  `search_alias_name` varchar(120) DEFAULT NULL,
  `add_to_search_index` tinyint(4) NOT NULL DEFAULT '0',
  `show_in_response` tinyint(4) NOT NULL DEFAULT '0',
  `add_to_filters` tinyint(4) NOT NULL DEFAULT '0',
  `organization_uid` varchar(40) DEFAULT '4261739e-ccae-11e1-adfb-5404a609bd14',
  PRIMARY KEY (`custom_field_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `custom_fields_data`
--

DROP TABLE IF EXISTS `custom_fields_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_fields_data` (
  `resource_gooru_oid` varchar(40) NOT NULL,
  `column_001` varchar(255) DEFAULT NULL,
  `column_002` varchar(255) DEFAULT NULL,
  `column_003` varchar(255) DEFAULT NULL,
  `column_004` varchar(255) DEFAULT NULL,
  `column_005` varchar(255) DEFAULT NULL,
  `column_006` varchar(255) DEFAULT NULL,
  `column_007` varchar(255) DEFAULT NULL,
  `column_008` varchar(255) DEFAULT NULL,
  `column_009` varchar(255) DEFAULT NULL,
  `column_010` varchar(255) DEFAULT NULL,
  `column_011` varchar(255) DEFAULT NULL,
  `column_012` varchar(255) DEFAULT NULL,
  `column_013` varchar(255) DEFAULT NULL,
  `column_014` varchar(255) DEFAULT NULL,
  `column_015` varchar(255) DEFAULT NULL,
  `column_016` varchar(255) DEFAULT NULL,
  `column_017` varchar(255) DEFAULT NULL,
  `column_018` varchar(255) DEFAULT NULL,
  `column_019` varchar(255) DEFAULT NULL,
  `column_020` varchar(255) DEFAULT NULL,
  `column_021` varchar(255) DEFAULT NULL,
  `column_022` varchar(255) DEFAULT NULL,
  `column_023` varchar(255) DEFAULT NULL,
  `column_024` varchar(255) DEFAULT NULL,
  `column_025` varchar(255) DEFAULT NULL,
  `column_026` varchar(255) DEFAULT NULL,
  `column_027` varchar(255) DEFAULT NULL,
  `column_028` varchar(255) DEFAULT NULL,
  `column_029` varchar(255) DEFAULT NULL,
  `column_030` varchar(255) DEFAULT NULL,
  `column_031` varchar(255) DEFAULT NULL,
  `column_032` varchar(255) DEFAULT NULL,
  `column_033` varchar(255) DEFAULT NULL,
  `column_034` varchar(255) DEFAULT NULL,
  `column_035` varchar(255) DEFAULT NULL,
  `column_036` varchar(255) DEFAULT NULL,
  `column_037` varchar(255) DEFAULT NULL,
  `column_038` varchar(255) DEFAULT NULL,
  `column_039` varchar(255) DEFAULT NULL,
  `column_040` varchar(255) DEFAULT NULL,
  `column_041` varchar(255) DEFAULT NULL,
  `column_042` varchar(255) DEFAULT NULL,
  `column_043` varchar(255) DEFAULT NULL,
  `column_044` varchar(255) DEFAULT NULL,
  `column_045` varchar(255) DEFAULT NULL,
  `column_046` varchar(255) DEFAULT NULL,
  `column_047` varchar(255) DEFAULT NULL,
  `column_048` varchar(255) DEFAULT NULL,
  `column_049` varchar(255) DEFAULT NULL,
  `column_050` varchar(255) DEFAULT NULL,
  `column_051` varchar(255) DEFAULT NULL,
  `column_052` varchar(255) DEFAULT NULL,
  `column_053` varchar(255) DEFAULT NULL,
  `column_054` varchar(255) DEFAULT NULL,
  `column_055` varchar(255) DEFAULT NULL,
  `column_056` varchar(255) DEFAULT NULL,
  `column_057` varchar(255) DEFAULT NULL,
  `column_058` varchar(255) DEFAULT NULL,
  `column_059` varchar(255) DEFAULT NULL,
  `column_060` varchar(255) DEFAULT NULL,
  `column_061` varchar(255) DEFAULT NULL,
  `column_062` varchar(255) DEFAULT NULL,
  `column_063` varchar(255) DEFAULT NULL,
  `column_064` varchar(255) DEFAULT NULL,
  `column_065` varchar(255) DEFAULT NULL,
  `column_066` varchar(255) DEFAULT NULL,
  `column_067` varchar(255) DEFAULT NULL,
  `column_068` varchar(255) DEFAULT NULL,
  `column_069` varchar(255) DEFAULT NULL,
  `column_070` varchar(255) DEFAULT NULL,
  `column_071` varchar(255) DEFAULT NULL,
  `column_072` varchar(255) DEFAULT NULL,
  `column_073` varchar(255) DEFAULT NULL,
  `column_074` varchar(255) DEFAULT NULL,
  `column_075` varchar(255) DEFAULT NULL,
  `column_076` varchar(255) DEFAULT NULL,
  `column_077` varchar(255) DEFAULT NULL,
  `column_078` varchar(255) DEFAULT NULL,
  `column_079` varchar(255) DEFAULT NULL,
  `column_080` varchar(255) DEFAULT NULL,
  `column_081` varchar(255) DEFAULT NULL,
  `column_082` varchar(255) DEFAULT NULL,
  `column_083` varchar(255) DEFAULT NULL,
  `column_084` varchar(255) DEFAULT NULL,
  `column_085` varchar(255) DEFAULT NULL,
  UNIQUE KEY `resource_gooru_oid` (`resource_gooru_oid`),
  UNIQUE KEY `resource_gooru_oid_2` (`resource_gooru_oid`),
  UNIQUE KEY `resource_gooru_oid_3` (`resource_gooru_oid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `custom_fields_group`
--

DROP TABLE IF EXISTS `custom_fields_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_fields_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_code` varchar(120) NOT NULL,
  `group_display_name` varchar(255) NOT NULL,
  `account_uid` varchar(36) DEFAULT NULL,
  `organization_uid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `account_uid` (`account_uid`),
  KEY `organization_uid` (`organization_uid`),
  CONSTRAINT `custom_fields_group_ibfk_1` FOREIGN KEY (`account_uid`) REFERENCES `account` (`account_uid`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `custom_settings`
--

DROP TABLE IF EXISTS `custom_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_settings` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `key` varchar(255) NOT NULL,
  `value` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `custom_table`
--

DROP TABLE IF EXISTS `custom_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_table` (
  `custom_table_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(46) NOT NULL,
  `display_name` varchar(50) DEFAULT NULL,
  `description` varchar(250) DEFAULT NULL,
  `organization_uid` varchar(36) NOT NULL,
  PRIMARY KEY (`custom_table_id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `name_2` (`name`),
  UNIQUE KEY `name_3` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `custom_table_value`
--

DROP TABLE IF EXISTS `custom_table_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `custom_table_value` (
  `custom_table_value_id` int(11) NOT NULL AUTO_INCREMENT,
  `custom_table_id` int(11) NOT NULL,
  `value` varchar(50) NOT NULL,
  `display_name` varchar(36) DEFAULT NULL,
  `key_value` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`custom_table_value_id`),
  UNIQUE KEY `custom_table_id` (`custom_table_id`,`value`),
  UNIQUE KEY `custom_table_id_2` (`custom_table_id`,`value`),
  UNIQUE KEY `key_value` (`key_value`)
) ENGINE=InnoDB AUTO_INCREMENT=134 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `entity_operation`
--

DROP TABLE IF EXISTS `entity_operation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `entity_operation` (
  `entity_operation_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `entity_name` varchar(100) NOT NULL,
  `operation_name` varchar(100) NOT NULL,
  PRIMARY KEY (`entity_operation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=299 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `event`
--

DROP TABLE IF EXISTS `event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event` (
  `event_uid` varchar(36) NOT NULL,
  `name` varchar(255) NOT NULL,
  `display_name` varchar(255) NOT NULL,
  `created_date` datetime NOT NULL,
  `creator_uid` varchar(36) NOT NULL,
  PRIMARY KEY (`event_uid`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `display_name` (`display_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `event_mapping`
--

DROP TABLE IF EXISTS `event_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event_mapping` (
  `event_uid` varchar(36) NOT NULL,
  `template_uid` varchar(36) DEFAULT NULL,
  `data` varchar(400) DEFAULT NULL,
  `creator_uid` varchar(36) NOT NULL,
  `status_id` int(11) NOT NULL,
  `created_date` datetime NOT NULL,
  UNIQUE KEY `event_uid` (`event_uid`),
  UNIQUE KEY `template_uid` (`template_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fact_resource`
--

DROP TABLE IF EXISTS `fact_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fact_resource` (
  `content_id` bigint(20) unsigned NOT NULL,
  `gooru_oid` varchar(36) NOT NULL DEFAULT '',
  `code_id` mediumint(8) unsigned NOT NULL,
  `title` varchar(1000) DEFAULT '',
  `category` varchar(100) DEFAULT NULL,
  `license_name` varchar(255) DEFAULT NULL,
  `record_source` varchar(40) NOT NULL DEFAULT 'not_added'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `featured_set`
--

DROP TABLE IF EXISTS `featured_set`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `featured_set` (
  `featured_set_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(250) NOT NULL,
  `lesson_code_id` int(11) DEFAULT NULL,
  `active_flag` tinyint(3) unsigned DEFAULT '1',
  `account_uid` varchar(36) DEFAULT NULL,
  `theme_code` varchar(9) DEFAULT NULL,
  `sequence` tinyint(4) DEFAULT NULL,
  `display_name` varchar(256) DEFAULT NULL,
  `organization_uid` varchar(36) DEFAULT NULL,
  `type_id` int(11) DEFAULT NULL,
  `subject_code_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`featured_set_id`),
  KEY `account_uid` (`account_uid`),
  KEY `organization_uid` (`organization_uid`),
  CONSTRAINT `featured_set_ibfk_1` FOREIGN KEY (`account_uid`) REFERENCES `account` (`account_uid`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=243 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `featured_set_items`
--

DROP TABLE IF EXISTS `featured_set_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `featured_set_items` (
  `featured_set_item_id` int(11) NOT NULL AUTO_INCREMENT,
  `featured_set_id` int(11) NOT NULL,
  `content_id` int(11) NOT NULL,
  `parent_content_id` int(11) DEFAULT NULL,
  `sequence` tinyint(4) NOT NULL,
  `code_id` int(11) DEFAULT NULL,
  `lesson_code_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`featured_set_item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5371 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `feedback`
--

DROP TABLE IF EXISTS `feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `feedback` (
  `feedback_uid` varchar(36) NOT NULL,
  `feedback_target_id` int(11) NOT NULL,
  `feedback_category` int(11) DEFAULT NULL,
  `feedback_category_id` int(11) NOT NULL,
  `feedback_type_id` int(11) NOT NULL,
  `feedback_text` varchar(255) DEFAULT NULL,
  `score` int(11) DEFAULT NULL,
  `assoc_gooru_oid` varchar(36) DEFAULT NULL,
  `assoc_user_uid` varchar(36) DEFAULT NULL,
  `creator_uid` varchar(36) NOT NULL,
  `reference_key` varchar(255) DEFAULT NULL,
  `organization_uid` varchar(36) NOT NULL,
  `created_date` datetime NOT NULL,
  `product_id` int(11) DEFAULT NULL,
  `context_path` varchar(2000) DEFAULT NULL,
  `notes` text,
  PRIMARY KEY (`feedback_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `frame_braker_domain`
--

DROP TABLE IF EXISTS `frame_braker_domain`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `frame_braker_domain` (
  `domain` varchar(255) DEFAULT NULL,
  `updated_status` int(1) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gender`
--

DROP TABLE IF EXISTS `gender`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gender` (
  `gender_id` varchar(1) NOT NULL,
  `name` enum('male','female','other','do not wish to share') NOT NULL,
  PRIMARY KEY (`gender_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `history_migrated_collelections`
--

DROP TABLE IF EXISTS `history_migrated_collelections`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `history_migrated_collelections` (
  `gooru_oid` varchar(250) DEFAULT NULL,
  KEY `index_gooruOid` (`gooru_oid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `identity`
--

DROP TABLE IF EXISTS `identity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `identity` (
  `user_id` int(10) unsigned DEFAULT NULL,
  `identity_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `idp_id` smallint(5) unsigned DEFAULT NULL,
  `active` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `firstname` varchar(20) NOT NULL,
  `registered_on` datetime NOT NULL,
  `deactivated_on` datetime DEFAULT NULL,
  `external_id` varchar(50) NOT NULL,
  `lastname` varchar(20) NOT NULL,
  `last_login` datetime DEFAULT NULL,
  `user_uid` varchar(36) DEFAULT NULL,
  `account_created_type` varchar(256) DEFAULT NULL,
  `login_type` varchar(20) DEFAULT NULL,
  `email_sso` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`identity_id`),
  UNIQUE KEY `UK_identity_external_id` (`external_id`),
  UNIQUE KEY `user_id` (`user_id`),
  KEY `FK_identity_user` (`user_id`),
  KEY `FK_identity_idp` (`idp_id`),
  KEY `idx_external_id` (`external_id`),
  KEY `user_uid` (`user_uid`),
  CONSTRAINT `FK_identity_idp` FOREIGN KEY (`idp_id`) REFERENCES `idp` (`idp_id`) ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=21028 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `identity_password_xref`
--

DROP TABLE IF EXISTS `identity_password_xref`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `identity_password_xref` (
  `identity_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `password` varchar(40) NOT NULL,
  `token` varchar(46) DEFAULT NULL,
  `reset_password_request_date` datetime DEFAULT NULL,
  PRIMARY KEY (`identity_id`),
  CONSTRAINT `FK_identity_password_xref_identity` FOREIGN KEY (`identity_id`) REFERENCES `identity` (`identity_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21028 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `idp`
--

DROP TABLE IF EXISTS `idp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `idp` (
  `idp_id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `gooru_installed` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`idp_id`),
  UNIQUE KEY `UK_idp_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1209 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `integration_map`
--

DROP TABLE IF EXISTS `integration_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `integration_map` (
  `sourceClasplanId` bigint(20) unsigned NOT NULL,
  `gooru_oid` varchar(36) NOT NULL,
  `content_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`sourceClasplanId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invite_code`
--

DROP TABLE IF EXISTS `invite_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invite_code` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(15) NOT NULL,
  `dateofexpiry` date DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_code_invite` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invite_user`
--

DROP TABLE IF EXISTS `invite_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invite_user` (
  `email` varchar(50) NOT NULL,
  `gooru_oid` varchar(36) DEFAULT NULL,
  `invitation_type` varchar(36) NOT NULL,
  `created_date` date NOT NULL,
  `joined_date` date DEFAULT NULL,
  `status_id` int(11) NOT NULL,
  `invite_user_id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`invite_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `job`
--

DROP TABLE IF EXISTS `job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `job` (
  `job_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `job_type` varchar(30) NOT NULL,
  `status` varchar(30) NOT NULL,
  `user_id` int(10) unsigned DEFAULT NULL,
  `gooru_oid` varchar(50) NOT NULL,
  `file_size` int(11) unsigned NOT NULL,
  `time_to_complete` int(11) unsigned DEFAULT NULL,
  `account_uid` varchar(36) DEFAULT NULL,
  `user_uid` varchar(36) DEFAULT NULL,
  `organization_uid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`job_id`),
  KEY `account_uid` (`account_uid`),
  KEY `user_uid` (`user_uid`),
  KEY `organization_uid` (`organization_uid`),
  CONSTRAINT `job_ibfk_1` FOREIGN KEY (`account_uid`) REFERENCES `account` (`account_uid`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=270 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `job_type`
--

DROP TABLE IF EXISTS `job_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `job_type` (
  `name` varchar(30) NOT NULL,
  `description` mediumtext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `language`
--

DROP TABLE IF EXISTS `language`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `language` (
  `language_id` varchar(3) NOT NULL,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`language_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `learnguide`
--

DROP TABLE IF EXISTS `learnguide`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `learnguide` (
  `content_id` bigint(20) unsigned NOT NULL,
  `lesson` varchar(256) NOT NULL DEFAULT '',
  `goals` text,
  `folder` varchar(10) DEFAULT NULL,
  `xml` longblob,
  `distinguish` tinyint(1) DEFAULT '0',
  `grade` varchar(50) DEFAULT NULL,
  `thumbnail` varchar(200) DEFAULT NULL,
  `type` varchar(45) NOT NULL DEFAULT 'classplan',
  `notes` text,
  `duration` varchar(30) DEFAULT NULL,
  `vocabulary` mediumtext,
  `narration` text,
  `curriculum` text,
  `migrated` tinyint(1) DEFAULT '0',
  `medium` mediumtext,
  `collection_gooru_oid` varchar(36) CHARACTER SET ucs2 DEFAULT NULL,
  `assessment_gooru_oid` varchar(36) CHARACTER SET ucs2 DEFAULT NULL,
  `source` varchar(255) DEFAULT NULL,
  `request_pending` tinyint(4) NOT NULL DEFAULT '0',
  `narration_link` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`content_id`),
  CONSTRAINT `FK_classplan_content` FOREIGN KEY (`content_id`) REFERENCES `content` (`content_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `license`
--

DROP TABLE IF EXISTS `license`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `license` (
  `name` varchar(255) NOT NULL,
  `url` varchar(200) NOT NULL,
  `code` varchar(100) DEFAULT NULL,
  `tag` varchar(255) DEFAULT NULL,
  `icon` varchar(255) DEFAULT NULL,
  `definition` text,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `link_url`
--

DROP TABLE IF EXISTS `link_url`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `link_url` (
  `link_url_id` varchar(36) NOT NULL,
  `crawler_url_id` varchar(36) NOT NULL,
  `links_to` varchar(1000) NOT NULL,
  PRIMARY KEY (`link_url_id`),
  KEY `crawler_url_id` (`crawler_url_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;



--
-- Table structure for table `network`
--

DROP TABLE IF EXISTS `network`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `network` (
  `network_uid` varchar(40) NOT NULL,
  `organization_uid` varchar(36) DEFAULT NULL,
  `approved_flag` tinyint(2) unsigned NOT NULL,
  PRIMARY KEY (`network_uid`),
  CONSTRAINT `network_ibfk_1` FOREIGN KEY (`network_uid`) REFERENCES `party` (`party_uid`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `notebook_entry`
--

DROP TABLE IF EXISTS `notebook_entry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notebook_entry` (
  `notebook_id` bigint(20) unsigned NOT NULL,
  `annotation_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`notebook_id`,`annotation_id`),
  KEY `FK_notebook_entry_annotation` (`annotation_id`),
  CONSTRAINT `FK_notebook_entry_annotation` FOREIGN KEY (`annotation_id`) REFERENCES `annotation` (`content_id`) ON DELETE CASCADE,
  CONSTRAINT `FK_notebook_entry_resource` FOREIGN KEY (`notebook_id`) REFERENCES `resource` (`content_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oauth_access_token`
--

DROP TABLE IF EXISTS `oauth_access_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth_access_token` (
  `token_id` varchar(36) NOT NULL,
  `token` blob,
  `authentication_id` varchar(36) DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `client_id` varchar(50) DEFAULT NULL,
  `authentication` blob,
  `refresh_token` varchar(36) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oauth_client`
--

DROP TABLE IF EXISTS `oauth_client`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth_client` (
  `client_uid` varchar(36) NOT NULL,
  `client_id` varchar(36) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(36) DEFAULT NULL,
  `client_secret` varchar(36) NOT NULL,
  `user_uid` varchar(36) NOT NULL,
  `scopes` varchar(36) DEFAULT NULL,
  `grant_types` varchar(255) NOT NULL,
  `authorities` varchar(255) NOT NULL,
  `access_token_validity` int(8) DEFAULT NULL,
  `refresh_token_validity` int(8) DEFAULT NULL,
  `redirect_uris` text,
  PRIMARY KEY (`client_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oauth_refresh_token`
--

DROP TABLE IF EXISTS `oauth_refresh_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth_refresh_token` (
  `token_id` varchar(36) NOT NULL,
  `token` blob,
  `authentication` blob
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `organization`
--

DROP TABLE IF EXISTS `organization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organization` (
  `organization_uid` varchar(36) NOT NULL,
  `organization_code` varchar(128) NOT NULL,
  `s3_storage_area_id` int(11) unsigned NOT NULL,
  `nfs_storage_area_id` int(11) unsigned NOT NULL,
  `parent_organization_uid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`organization_uid`),
  UNIQUE KEY `organization_code` (`organization_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `organization_domain_assoc`
--

DROP TABLE IF EXISTS `organization_domain_assoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organization_domain_assoc` (
  `domain_id` int(10) unsigned NOT NULL,
  `organization_uid` varchar(36) NOT NULL,
  PRIMARY KEY (`domain_id`,`organization_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `organization_setting`
--

DROP TABLE IF EXISTS `organization_setting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organization_setting` (
  `organization_setting_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(36) NOT NULL,
  `value` varchar(36) DEFAULT NULL,
  `organization_uid` varchar(36) NOT NULL,
  `key_value` text,
  PRIMARY KEY (`organization_setting_id`),
  KEY `name` (`name`),
  KEY `organization_uid` (`organization_uid`),
  CONSTRAINT `organization_setting_ibfk_1` FOREIGN KEY (`organization_uid`) REFERENCES `organization` (`organization_uid`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=95 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `party`
--

DROP TABLE IF EXISTS `party`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `party` (
  `party_uid` varchar(36) NOT NULL,
  `party_name` varchar(50) NOT NULL,
  `party_type` varchar(50) NOT NULL,
  `creator_id` int(11) unsigned DEFAULT NULL,
  `added_time` datetime DEFAULT NULL,
  `created_by_id` int(11) unsigned DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `last_modified_by_id` int(11) unsigned DEFAULT NULL,
  `last_modified_on` datetime DEFAULT NULL,
  `created_by_uid` varchar(36) DEFAULT NULL,
  `last_modified_by_uid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`party_uid`),
  KEY `created_by_uid` (`created_by_uid`),
  KEY `last_modified_by_uid` (`last_modified_by_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `party_custom_field`
--

DROP TABLE IF EXISTS `party_custom_field`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `party_custom_field` (
  `party_uid` varchar(36) NOT NULL,
  `category` varchar(255) NOT NULL,
  `optional_key` varchar(255) NOT NULL,
  `optional_value` text NOT NULL,
  PRIMARY KEY (`party_uid`,`optional_key`),
  CONSTRAINT `party_custom_field_ibfk_1` FOREIGN KEY (`party_uid`) REFERENCES `party` (`party_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `party_permission`
--

DROP TABLE IF EXISTS `party_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `party_permission` (
  `party_uid` varchar(36) NOT NULL,
  `permitted_party_uid` varchar(36) NOT NULL,
  `permission` varchar(36) NOT NULL,
  `valid_from` datetime DEFAULT NULL,
  `expiry_date` datetime DEFAULT NULL,
  PRIMARY KEY (`party_uid`,`permitted_party_uid`),
  KEY `group_uid` (`party_uid`),
  KEY `party_uid` (`permitted_party_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `perm`
--

DROP TABLE IF EXISTS `perm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `perm` (
  `name` varchar(20) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `persistent_logins`
--

DROP TABLE IF EXISTS `persistent_logins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `persistent_logins` (
  `username` varchar(64) NOT NULL,
  `series` varchar(64) NOT NULL,
  `token` varchar(64) NOT NULL,
  `last_used` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`series`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `post`
--

DROP TABLE IF EXISTS `post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `post` (
  `content_id` bigint(20) unsigned NOT NULL,
  `post_title` varchar(255) DEFAULT NULL,
  `post_text` mediumtext NOT NULL,
  `post_type_id` int(11) NOT NULL,
  `assoc_gooru_oid` varchar(36) DEFAULT NULL,
  `assoc_user_uid` varchar(36) DEFAULT NULL,
  `target_id` int(11) NOT NULL,
  `status_id` int(11) unsigned NOT NULL,
  KEY `post_content_id` (`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `profile`
--

DROP TABLE IF EXISTS `profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `profile` (
  `user_id` int(10) unsigned DEFAULT NULL,
  `grade` varchar(256) DEFAULT NULL,
  `gender` varchar(1) DEFAULT NULL,
  `country_id` varchar(3) DEFAULT NULL,
  `city_id` varchar(5) DEFAULT NULL,
  `subject` varchar(256) DEFAULT NULL,
  `birth_month` tinyint(3) unsigned DEFAULT NULL,
  `birth_year` varchar(4) DEFAULT NULL,
  `first_language` varchar(3) DEFAULT NULL,
  `second_language` varchar(3) DEFAULT NULL,
  `third_language` varchar(3) DEFAULT NULL,
  `birth_date` tinyint(2) unsigned DEFAULT NULL,
  `state_province_id` varchar(5) DEFAULT NULL,
  `about_me` varchar(500) DEFAULT NULL,
  `teaching_experience` varchar(20) DEFAULT NULL,
  `teaching_in` varchar(50) DEFAULT NULL,
  `teaching_methodology` varchar(500) DEFAULT NULL,
  `highest_degree` varchar(45) DEFAULT NULL,
  `post_graduation` varchar(45) DEFAULT NULL,
  `graduation` varchar(45) DEFAULT NULL,
  `high_school` varchar(45) DEFAULT NULL,
  `picture` longblob,
  `website` varchar(256) DEFAULT NULL,
  `facebook` varchar(256) DEFAULT NULL,
  `twitter` varchar(256) DEFAULT NULL,
  `user_type` varchar(45) DEFAULT NULL,
  `age_check` int(10) unsigned DEFAULT '0',
  `thumbnail` longblob,
  `school` varchar(45) DEFAULT NULL,
  `publisher_request_pending` tinyint(1) DEFAULT '0',
  `picture_format` varchar(10) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `child_date_of_birth` date DEFAULT NULL,
  `user_uid` varchar(36) NOT NULL DEFAULT '',
  `notes` text,
  PRIMARY KEY (`user_uid`),
  KEY `FK_profile_gender` (`gender`),
  KEY `FK_profile_subject` (`subject`(255)),
  KEY `FK_profile_language1` (`first_language`),
  KEY `FK_profile_language2` (`second_language`),
  KEY `FK_profile_language3` (`third_language`),
  KEY `FK_profile_country` (`country_id`) USING BTREE,
  KEY `FK_profile_city` (`city_id`) USING BTREE,
  KEY `user_uid` (`user_uid`),
  CONSTRAINT `FK_profile_city` FOREIGN KEY (`city_id`) REFERENCES `city` (`city_id`) ON UPDATE NO ACTION,
  CONSTRAINT `FK_profile_country` FOREIGN KEY (`country_id`) REFERENCES `country` (`country_id`),
  CONSTRAINT `FK_profile_gender` FOREIGN KEY (`gender`) REFERENCES `gender` (`gender_id`) ON UPDATE NO ACTION,
  CONSTRAINT `FK_profile_language1` FOREIGN KEY (`first_language`) REFERENCES `language` (`language_id`) ON UPDATE NO ACTION,
  CONSTRAINT `FK_profile_language2` FOREIGN KEY (`second_language`) REFERENCES `language` (`language_id`) ON UPDATE NO ACTION,
  CONSTRAINT `FK_profile_language3` FOREIGN KEY (`third_language`) REFERENCES `language` (`language_id`) ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `question`
--

DROP TABLE IF EXISTS `question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `question` (
  `content_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `question_xml` varchar(10000) DEFAULT NULL,
  `index_cache` varchar(10000) DEFAULT NULL,
  `md5_hash` varchar(32) DEFAULT NULL,
  `duration` int(10) unsigned DEFAULT NULL,
  `question_type` varchar(10) DEFAULT NULL,
  `correct_option` tinyint(1) unsigned DEFAULT NULL,
  `option_a` mediumtext,
  `option_b` mediumtext,
  `option_c` mediumtext,
  `option_d` mediumtext,
  `option_e` mediumtext,
  `option_f` mediumtext,
  PRIMARY KEY (`content_id`),
  CONSTRAINT `FK_question_resource` FOREIGN KEY (`content_id`) REFERENCES `resource` (`content_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `question_board_comment`
--

DROP TABLE IF EXISTS `question_board_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `question_board_comment` (
  `comment_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `comment` varchar(250) NOT NULL,
  `image` blob,
  `response_id` bigint(20) NOT NULL,
  `commentor` int(10) unsigned NOT NULL,
  `date_posted` datetime NOT NULL,
  PRIMARY KEY (`comment_id`),
  KEY `fk_comment_response1` (`response_id`),
  KEY `fk_comment_user1` (`commentor`),
  CONSTRAINT `fk_comment_response1` FOREIGN KEY (`response_id`) REFERENCES `question_board_response` (`response_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `question_board_question`
--

DROP TABLE IF EXISTS `question_board_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `question_board_question` (
  `question_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `followers` bigint(20) DEFAULT NULL,
  `title` varchar(110) DEFAULT NULL,
  `gooru_uid` varchar(45) DEFAULT NULL,
  `body` mediumtext NOT NULL,
  `image` blob,
  `poster` int(10) unsigned DEFAULT NULL,
  `directed_to` int(10) unsigned DEFAULT NULL,
  `date_posted` datetime NOT NULL,
  `anonymous` tinyint(1) DEFAULT '0',
  `resource_oid` varchar(50) DEFAULT NULL,
  `poster_uid` varchar(36) DEFAULT NULL,
  `directed_to_uid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`question_id`),
  KEY `fk_question_user1` (`poster`),
  KEY `fk_question_user2` (`directed_to`),
  KEY `poster_uid` (`poster_uid`),
  KEY `directed_to_uid` (`directed_to_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `question_board_response`
--

DROP TABLE IF EXISTS `question_board_response`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `question_board_response` (
  `response_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `response` mediumtext NOT NULL,
  `image` blob,
  `question_id` bigint(20) NOT NULL,
  `responder` int(10) unsigned DEFAULT NULL,
  `date_posted` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `anonymous` tinyint(1) DEFAULT '0',
  `responder_uid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`response_id`),
  KEY `fk_response_question1` (`question_id`),
  KEY `fk_response_user1` (`responder`),
  KEY `responder_uid` (`responder_uid`),
  CONSTRAINT `fk_response_question1` FOREIGN KEY (`question_id`) REFERENCES `question_board_question` (`question_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `question_set`
--

DROP TABLE IF EXISTS `question_set`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `question_set` (
  `question_set_id` bigint(20) unsigned NOT NULL COMMENT 'Primary Key',
  PRIMARY KEY (`question_set_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `question_set_question_assoc`
--

DROP TABLE IF EXISTS `question_set_question_assoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `question_set_question_assoc` (
  `question_set_id` bigint(20) unsigned NOT NULL,
  `question_id` bigint(20) unsigned NOT NULL,
  `sequence` int(11) unsigned NOT NULL,
  PRIMARY KEY (`question_set_id`,`question_id`),
  KEY `fk_question_set_question_assoc_question1` (`question_id`),
  CONSTRAINT `fk_question_set_question_assoc_question1` FOREIGN KEY (`question_id`) REFERENCES `assessment_question` (`question_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_question_set_question_assoc_question_set1` FOREIGN KEY (`question_set_id`) REFERENCES `question_set` (`question_set_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quiz`
--

DROP TABLE IF EXISTS `quiz`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quiz` (
  `quiz_content_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `randomize_flag` tinyint(4) DEFAULT NULL,
  `randomize_choice_flag` tinyint(4) DEFAULT NULL,
  `show_hints_flag` tinyint(4) DEFAULT NULL,
  `show_score_flag` tinyint(4) DEFAULT NULL,
  `show_correct_answer_flag` tinyint(4) DEFAULT NULL,
  `options` text,
  PRIMARY KEY (`quiz_content_id`)
) ENGINE=MyISAM AUTO_INCREMENT=2458340 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quote`
--

DROP TABLE IF EXISTS `quote`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quote` (
  `content_id` bigint(20) unsigned NOT NULL,
  `context` bigint(20) unsigned DEFAULT NULL,
  `grade` varchar(256) DEFAULT NULL,
  `topic` varchar(256) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `license_name` varchar(50) DEFAULT NULL,
  `type_name` varchar(20) DEFAULT NULL,
  `context_anchor` varchar(256) DEFAULT NULL,
  `context_anchor_text` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`content_id`),
  KEY `FK_quote_LicenseName` (`license_name`),
  KEY `FK_quote_Context` (`context`),
  KEY `FK_quote_TYPE` (`type_name`),
  CONSTRAINT `FK_Annotation_Content_Id` FOREIGN KEY (`content_id`) REFERENCES `annotation` (`content_id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `FK_quote_Context` FOREIGN KEY (`context`) REFERENCES `content` (`content_id`) ON DELETE SET NULL ON UPDATE NO ACTION,
  CONSTRAINT `FK_quote_LicenseName` FOREIGN KEY (`license_name`) REFERENCES `license` (`name`) ON UPDATE NO ACTION,
  CONSTRAINT `FK_quote_TYPE` FOREIGN KEY (`type_name`) REFERENCES `tag_type` (`name`) ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rating`
--

DROP TABLE IF EXISTS `rating`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rating` (
  `content_id` bigint(20) unsigned NOT NULL,
  `score` int(11) DEFAULT '0',
  `point` int(11) DEFAULT NULL,
  `type` varchar(20) DEFAULT 'score',
  KEY `FK_rating_annotation_content_id` (`content_id`),
  CONSTRAINT `FK_rating_annotation_content_id` FOREIGN KEY (`content_id`) REFERENCES `annotation` (`content_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `register_attempts`
--

DROP TABLE IF EXISTS `register_attempts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `register_attempts` (
  `register_attempt_id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL,
  `firstname` varchar(100) NOT NULL,
  `lastname` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `timestamp` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`register_attempt_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4055 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `registered_users`
--

DROP TABLE IF EXISTS `registered_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `registered_users` (
  `email` varchar(100) NOT NULL,
  `timestamp` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `resource`
--

DROP TABLE IF EXISTS `resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource` (
  `content_id` bigint(20) unsigned NOT NULL,
  `type_name` varchar(20) NOT NULL,
  `category` varchar(100) DEFAULT NULL,
  `url` varchar(2000) DEFAULT NULL,
  `resource_source_id` int(11) DEFAULT NULL,
  `storage_area_id` int(11) DEFAULT NULL,
  `folder` varchar(30) DEFAULT NULL,
  `thumbnail` varchar(256) DEFAULT NULL,
  `license_name` varchar(255) DEFAULT NULL,
  `title` varchar(1000) DEFAULT '',
  `views_total` bigint(20) unsigned DEFAULT '0',
  `distinguish` tinyint(1) unsigned DEFAULT '0',
  `isLive` enum('0','1') DEFAULT '1',
  `sequence` int(11) unsigned DEFAULT NULL,
  `has_frame_breaker` tinyint(1) DEFAULT NULL,
  `broken_status` int(5) unsigned DEFAULT NULL,
  `text` text,
  `from_crawler` tinyint(1) DEFAULT '0',
  `image_url` varchar(1000) DEFAULT NULL,
  `user_uploaded_image` tinyint(1) unsigned DEFAULT NULL,
  `in_use` tinyint(1) DEFAULT '1',
  `is_folder_absent` tinyint(1) unsigned DEFAULT NULL,
  `add_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `internal_title` varchar(4000) DEFAULT NULL,
  `number_of_subcribers` int(11) DEFAULT '0',
  `is_featured` tinyint(1) NOT NULL DEFAULT '0',
  `from_qa` tinyint(1) DEFAULT '0',
  `is_atomic` tinyint(1) DEFAULT '0',
  `file_hash` varchar(40) DEFAULT NULL,
  `description` text,
  `source_reference` varchar(50) DEFAULT NULL,
  `record_source` varchar(40) NOT NULL DEFAULT 'not_added',
  `orig_title` varchar(1000) DEFAULT NULL,
  `tags` text,
  `site_name` varchar(120) DEFAULT NULL,
  `batch_id` varchar(255) DEFAULT NULL,
  `s3_upload_flag` tinyint(1) unsigned DEFAULT '0',
  `resource_info_id` bigint(20) unsigned DEFAULT NULL,
  `media_type` varchar(64) DEFAULT NULL,
  `vocabulary` text,
  `grade` varchar(1000) DEFAULT NULL,
  `orig_batch_id` varchar(120) DEFAULT NULL,
  `copied_resource_id` varchar(36) DEFAULT NULL,
  `has_advertisement` tinyint(1) DEFAULT NULL,
  `is_oer` tinyint(1) DEFAULT NULL,
  `resource_format_id` int(11) DEFAULT NULL,
  `instructional_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`content_id`),
  UNIQUE KEY `content_id` (`content_id`,`type_name`),
  UNIQUE KEY `batch_id` (`batch_id`,`content_id`),
  KEY `FK_resource_TYPE` (`type_name`),
  KEY `FK_resource_license` (`license_name`),
  KEY `url` (`url`(255)),
  KEY `type_name` (`type_name`,`in_use`),
  KEY `media_type` (`media_type`),
  KEY `record_source` (`record_source`),
  KEY `add_time` (`add_time`),
  CONSTRAINT `FK_resource_content` FOREIGN KEY (`content_id`) REFERENCES `content` (`content_id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `FK_resource_license` FOREIGN KEY (`license_name`) REFERENCES `license` (`name`) ON UPDATE NO ACTION,
  CONSTRAINT `FK_resource_TYPE` FOREIGN KEY (`type_name`) REFERENCES `resource_type` (`name`) ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `resource_boost_factor`
--

DROP TABLE IF EXISTS `resource_boost_factor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_boost_factor` (
  `resource_gooru_id` varchar(36) NOT NULL,
  `hygiene_level` float DEFAULT NULL,
  `complete_level` float DEFAULT NULL,
  `relevance_level` float DEFAULT NULL,
  `label` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`resource_gooru_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `resource_feeds`
--

DROP TABLE IF EXISTS `resource_feeds`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_feeds` (
  `feeds_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `resource_id` bigint(20) NOT NULL,
  `title` varchar(1000) DEFAULT '',
  `description` text,
  `view_count` bigint(20) DEFAULT NULL,
  `favorite_count` bigint(20) DEFAULT NULL,
  `like_count` bigint(20) DEFAULT NULL,
  `dislike_count` bigint(20) DEFAULT NULL,
  `rating_average` float DEFAULT NULL,
  `url_status` int(5) DEFAULT '0',
  `duration` varchar(10) DEFAULT '0',
  PRIMARY KEY (`feeds_id`)
) ENGINE=InnoDB AUTO_INCREMENT=55383 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `resource_info`
--

DROP TABLE IF EXISTS `resource_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_info` (
  `resource_info_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `resource_id` bigint(20) unsigned NOT NULL,
  `view_count` bigint(20) unsigned NOT NULL,
  `subscribe_count` bigint(20) unsigned NOT NULL,
  `vote_up` int(10) DEFAULT '0',
  `vote_down` int(10) DEFAULT '0',
  `text` mediumtext,
  `tags` text,
  `num_of_pages` int(11) DEFAULT NULL,
  `last_updated` datetime NOT NULL,
  PRIMARY KEY (`resource_info_id`),
  KEY `res_info_resource_id` (`resource_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2090663 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `resource_instance`
--

DROP TABLE IF EXISTS `resource_instance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_instance` (
  `resource_instance_id` varchar(36) NOT NULL,
  `segment_id` varchar(50) NOT NULL,
  `resource_id` bigint(20) unsigned NOT NULL,
  `title` text,
  `description` text,
  `start` varchar(50) DEFAULT NULL,
  `stop` varchar(50) DEFAULT NULL,
  `narrative` text,
  `sequence` int(10) unsigned NOT NULL,
  `xml_resource_id` varchar(50) DEFAULT NULL,
  `key_points` text,
  `narration_link` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`resource_instance_id`),
  KEY `fk_resource_id` (`resource_id`),
  KEY `segment_id` (`segment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `resource_metadata`
--

DROP TABLE IF EXISTS `resource_metadata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_metadata` (
  `resource_meta_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `resource_id` bigint(20) NOT NULL,
  `meta_key` varchar(255) NOT NULL,
  `meta_content` text,
  PRIMARY KEY (`resource_meta_id`),
  KEY `resource_id` (`resource_id`),
  KEY `meta_key` (`meta_key`)
) ENGINE=InnoDB AUTO_INCREMENT=2555250 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `resource_security_group`
--

DROP TABLE IF EXISTS `resource_security_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_security_group` (
  `security_group_uid` varchar(36) NOT NULL,
  `resource_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`security_group_uid`,`resource_id`),
  KEY `security_group_uid` (`security_group_uid`),
  KEY `resource_id` (`resource_id`),
  CONSTRAINT `content_security_group_ibfk_1` FOREIGN KEY (`resource_id`) REFERENCES `resource` (`content_id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `content_security_group_ibfk_2` FOREIGN KEY (`security_group_uid`) REFERENCES `security_group` (`security_group_uid`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `resource_source`
--

DROP TABLE IF EXISTS `resource_source`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_source` (
  `resource_source_id` int(11) NOT NULL AUTO_INCREMENT,
  `source_name` varchar(500) DEFAULT NULL,
  `domain_name` varchar(255) DEFAULT NULL,
  `resource_url` varchar(500) DEFAULT NULL,
  `attribution` varchar(500) DEFAULT NULL,
  `linking` varchar(500) DEFAULT NULL,
  `comments` text,
  `active_status` tinyint(4) NOT NULL DEFAULT '1',
  `type` varchar(50) DEFAULT NULL,
  `frame_breaker` tinyint(1) DEFAULT '0',
  `is_blacklisted` tinyint(1) DEFAULT '0',
  `has_https_support` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`resource_source_id`),
  KEY `resource_source_id` (`resource_source_id`),
  KEY `rs_domain_name` (`domain_name`),
  KEY `domain_name` (`domain_name`),
  KEY `resource_source_id_2` (`resource_source_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6120 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `resource_type`
--

DROP TABLE IF EXISTS `resource_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_type` (
  `name` varchar(20) NOT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `resource_url_status`
--

DROP TABLE IF EXISTS `resource_url_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `resource_url_status` (
  `resource_url_status_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `resource_id` bigint(20) unsigned NOT NULL,
  `status` int(5) unsigned DEFAULT NULL,
  `failed_count` int(5) DEFAULT '0',
  `last_checked_date` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `frame_breaker_validated_on` datetime DEFAULT NULL,
  `type_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`resource_url_status_id`),
  UNIQUE KEY `resource_id` (`resource_id`),
  KEY `rus_status_last` (`resource_id`,`status`,`last_checked_date`)
) ENGINE=InnoDB AUTO_INCREMENT=2370566 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `response_fields`
--

DROP TABLE IF EXISTS `response_fields`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `response_fields` (
  `field_id` varchar(36) NOT NULL,
  `field_name` text NOT NULL,
  `organization` varchar(36) NOT NULL,
  `gooru_uid` varchar(36) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `revision_history`
--

DROP TABLE IF EXISTS `revision_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `revision_history` (
  `revision_history_uid` varchar(36) NOT NULL,
  `entity_uid` varchar(36) NOT NULL,
  `entity_name` varchar(256) NOT NULL,
  `user_uid` varchar(36) NOT NULL,
  `on_event` varchar(256) NOT NULL,
  `time` datetime NOT NULL,
  `data` text NOT NULL,
  PRIMARY KEY (`revision_history_uid`),
  KEY `entity_id` (`entity_uid`,`entity_name`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role` (
  `role_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` varchar(2500) DEFAULT NULL,
  `account_uid` varchar(36) DEFAULT NULL,
  `organization_uid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`role_id`),
  KEY `account_uid` (`account_uid`),
  KEY `organization_uid` (`organization_uid`),
  CONSTRAINT `role_ibfk_1` FOREIGN KEY (`account_uid`) REFERENCES `account` (`account_uid`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_entity_operation`
--

DROP TABLE IF EXISTS `role_entity_operation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_entity_operation` (
  `role_id` int(10) unsigned NOT NULL,
  `entity_operation_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`role_id`,`entity_operation_id`),
  KEY `entity_operation_id` (`entity_operation_id`),
  CONSTRAINT `role_entity_operation_ibfk_1` FOREIGN KEY (`entity_operation_id`) REFERENCES `entity_operation` (`entity_operation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `schema_version`
--

DROP TABLE IF EXISTS `schema_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `schema_version` (
  `version` varchar(20) NOT NULL,
  `description` varchar(100) DEFAULT NULL,
  `type` varchar(10) NOT NULL,
  `script` varchar(200) NOT NULL,
  `checksum` int(11) DEFAULT NULL,
  `installed_by` varchar(30) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int(11) DEFAULT NULL,
  `state` varchar(15) NOT NULL,
  `current_version` tinyint(1) NOT NULL,
  PRIMARY KEY (`version`),
  UNIQUE KEY `version` (`version`),
  UNIQUE KEY `script` (`script`),
  KEY `schema_version_current_version_index` (`current_version`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `search_query`
--

DROP TABLE IF EXISTS `search_query`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_query` (
  `query_uid` varchar(36) NOT NULL,
  `query_text` varchar(1000) DEFAULT NULL,
  `user_ip` varchar(36) DEFAULT NULL,
  `gooru_uid` varchar(36) CHARACTER SET utf8 NOT NULL,
  `time_token_in_millis` bigint(20) NOT NULL DEFAULT '0',
  `result_count` int(11) DEFAULT NULL,
  `search_type` varchar(50) DEFAULT NULL,
  `query_time` datetime DEFAULT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`query_uid`),
  KEY `gooru_uid` (`gooru_uid`),
  CONSTRAINT `search_query_ibfk_1` FOREIGN KEY (`gooru_uid`) REFERENCES `user` (`gooru_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `search_result`
--

DROP TABLE IF EXISTS `search_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_result` (
  `result_uid` varchar(36) NOT NULL,
  `query_uid` varchar(36) NOT NULL,
  `ref_id` varchar(50) DEFAULT NULL,
  `score` float(16,10) NOT NULL,
  PRIMARY KEY (`result_uid`),
  KEY `result_uid` (`result_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `search_result_activity`
--

DROP TABLE IF EXISTS `search_result_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `search_result_activity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `result_uid` varchar(36) NOT NULL,
  `user_action` varchar(15) NOT NULL,
  `user_action_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=195 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `security_group`
--

DROP TABLE IF EXISTS `security_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `security_group` (
  `security_group_uid` varchar(36) NOT NULL,
  `organization_uid` varchar(36) NOT NULL,
  `name` varchar(36) NOT NULL,
  `security_key` varchar(36) NOT NULL,
  `system_flag` tinyint(1) NOT NULL,
  `default_flag` tinyint(1) NOT NULL,
  `active_flag` tinyint(1) NOT NULL,
  PRIMARY KEY (`security_group_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `security_group_access`
--

DROP TABLE IF EXISTS `security_group_access`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `security_group_access` (
  `security_group_uid` varchar(36) NOT NULL,
  `authorized_security_group_uid` varchar(36) NOT NULL,
  PRIMARY KEY (`security_group_uid`,`authorized_security_group_uid`),
  KEY `security_group_uid` (`security_group_uid`),
  KEY `authorized_security_group_uid` (`authorized_security_group_uid`),
  CONSTRAINT `authorized_security_group_ibfk_1` FOREIGN KEY (`authorized_security_group_uid`) REFERENCES `security_group` (`security_group_uid`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `authorized_security_group_ibfk_2` FOREIGN KEY (`security_group_uid`) REFERENCES `security_group` (`security_group_uid`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `segment`
--

DROP TABLE IF EXISTS `segment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `segment` (
  `segment_id` varchar(50) NOT NULL,
  `resource_id` bigint(20) unsigned NOT NULL,
  `title` varchar(1000) NOT NULL,
  `description` mediumtext,
  `type_name` varchar(50) NOT NULL,
  `rendition_url` varchar(1000) DEFAULT NULL,
  `duration` varchar(20) DEFAULT NULL,
  `sequence` int(11) unsigned NOT NULL,
  `xml_segment_id` varchar(50) DEFAULT NULL,
  `is_meta` tinyint(1) DEFAULT NULL,
  `concept` varchar(250) DEFAULT NULL,
  `segment_image` varchar(256) DEFAULT NULL,
  `key_points` text,
  `narration_link` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`segment_id`),
  KEY `resource_id` (`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `segment_resource_assoc`
--

DROP TABLE IF EXISTS `segment_resource_assoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `segment_resource_assoc` (
  `segment_id` varchar(50) NOT NULL,
  `resource_id` bigint(20) unsigned NOT NULL,
  `sequence` int(10) unsigned NOT NULL,
  PRIMARY KEY (`segment_id`,`resource_id`),
  KEY `fk_resource_id` (`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `service_call`
--

DROP TABLE IF EXISTS `service_call`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_call` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `predicate` varchar(100) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `user_ip` varchar(255) DEFAULT NULL,
  `request_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `action` varchar(255) DEFAULT NULL,
  `execute_time` int(11) DEFAULT NULL,
  `request_parameters` varchar(5000) DEFAULT NULL,
  `query` varchar(2000) DEFAULT NULL,
  `service_name` varchar(50) DEFAULT NULL,
  `http_method` varchar(20) DEFAULT NULL,
  `response_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `user_uid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `predicate` (`predicate`),
  KEY `user_id` (`user_id`),
  KEY `user_ip` (`user_ip`),
  KEY `request_time` (`request_time`),
  KEY `action` (`action`),
  KEY `action_user` (`predicate`,`action`,`user_id`,`request_time`),
  KEY `predicate_user` (`predicate`,`user_id`,`request_time`),
  KEY `user_uid` (`user_uid`)
) ENGINE=InnoDB AUTO_INCREMENT=1998507 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `service_call_request_parameter`
--

DROP TABLE IF EXISTS `service_call_request_parameter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_call_request_parameter` (
  `service_call_id` bigint(20) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `value` varchar(1000) DEFAULT NULL,
  KEY `service_call_id` (`service_call_id`,`name`,`value`(255)),
  KEY `name` (`name`,`value`(255)),
  CONSTRAINT `service_call_request_parameter_ibfk_1` FOREIGN KEY (`service_call_id`) REFERENCES `service_call` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `session`
--

DROP TABLE IF EXISTS `session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `session` (
  `session_id` varchar(36) NOT NULL,
  `collection_id` bigint(20) NOT NULL,
  `score` double NOT NULL DEFAULT '0',
  `mode` enum('test','play','practice') NOT NULL,
  `status` enum('open','archive') NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime DEFAULT NULL,
  `user_uid` varchar(36) NOT NULL,
  `organization_uid` varchar(36) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `session_activity`
--

DROP TABLE IF EXISTS `session_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `session_activity` (
  `session_activity_uid` varchar(36) NOT NULL,
  `user_uid` varchar(36) NOT NULL,
  `organization_uid` varchar(36) DEFAULT NULL,
  `status` enum('open','archive') NOT NULL,
  `created_on` datetime NOT NULL,
  PRIMARY KEY (`session_activity_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `session_activity_item`
--

DROP TABLE IF EXISTS `session_activity_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `session_activity_item` (
  `session_activity_item_uid` varchar(36) NOT NULL,
  `session_activity_uid` varchar(36) NOT NULL,
  `content_uid` varchar(36) NOT NULL,
  `sub_content_uid` varchar(36) DEFAULT NULL,
  `question_attempt_id` int(11) DEFAULT NULL,
  `content_type` enum('collection','quiz') NOT NULL,
  `created_on` datetime NOT NULL,
  PRIMARY KEY (`session_activity_item_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `session_item`
--

DROP TABLE IF EXISTS `session_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `session_item` (
  `session_item_id` varchar(36) NOT NULL,
  `session_id` varchar(36) NOT NULL,
  `resource_id` bigint(20) unsigned NOT NULL,
  `collection_item_id` varchar(36) DEFAULT NULL,
  `attempt_item_status` enum('correct','wrong','skip') DEFAULT NULL,
  `correct_try_sequence` int(11) DEFAULT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime DEFAULT NULL,
  PRIMARY KEY (`session_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `session_item_attempt_try`
--

DROP TABLE IF EXISTS `session_item_attempt_try`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `session_item_attempt_try` (
  `session_item_id` varchar(36) NOT NULL,
  `answer_id` int(10) unsigned DEFAULT NULL,
  `answer_text` text,
  `try_sequence` int(11) NOT NULL,
  `attempt_item_try_status` enum('correct','wrong','skip') NOT NULL,
  `answered_at_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `shelf`
--

DROP TABLE IF EXISTS `shelf`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `shelf` (
  `shelf_id` varchar(36) NOT NULL,
  `account_uid` varchar(36) DEFAULT NULL,
  `shelf_parent_id` varchar(36) DEFAULT NULL,
  `shelf_type` varchar(20) NOT NULL,
  `name` varchar(120) NOT NULL,
  `default_flag` tinyint(1) NOT NULL DEFAULT '1',
  `user_id` varchar(36) NOT NULL,
  `shelf_category` varchar(36) NOT NULL,
  `added_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `active_flag` tinyint(4) NOT NULL,
  `depth` tinyint(3) unsigned NOT NULL,
  `view_flag` tinyint(4) NOT NULL DEFAULT '1',
  `organization_uid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`shelf_id`),
  KEY `account_uid` (`account_uid`),
  KEY `user_id` (`user_id`),
  KEY `user_id_2` (`user_id`),
  KEY `organization_uid` (`organization_uid`),
  CONSTRAINT `shelf_ibfk_1` FOREIGN KEY (`account_uid`) REFERENCES `account` (`account_uid`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `shelf_item`
--

DROP TABLE IF EXISTS `shelf_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `shelf_item` (
  `shelf_item_id` varchar(36) NOT NULL,
  `shelf_id` varchar(36) NOT NULL,
  `content_id` bigint(20) unsigned NOT NULL,
  `added_type` varchar(20) NOT NULL,
  `added_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_activity_time` datetime DEFAULT NULL,
  PRIMARY KEY (`shelf_item_id`),
  KEY `content_id` (`content_id`),
  KEY `content_id_2` (`content_id`),
  CONSTRAINT `shelf_item_ibfk_1` FOREIGN KEY (`content_id`) REFERENCES `resource` (`content_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `state_province`
--

DROP TABLE IF EXISTS `state_province`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `state_province` (
  `country_id` varchar(3) NOT NULL,
  `state_province_id` varchar(5) NOT NULL,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`state_province_id`),
  KEY `FK_state_province_country` (`country_id`),
  CONSTRAINT `FK_state_province_country` FOREIGN KEY (`country_id`) REFERENCES `country` (`country_id`) ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `status_type`
--

DROP TABLE IF EXISTS `status_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `status_type` (
  `status_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  PRIMARY KEY (`status_id`),
  KEY `status_id` (`status_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `storage_account`
--

DROP TABLE IF EXISTS `storage_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `storage_account` (
  `storage_account_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `account_name` varchar(128) NOT NULL,
  `domain_name` varchar(255) NOT NULL,
  `type_name` varchar(32) NOT NULL,
  `access_key` varchar(128) DEFAULT NULL,
  `access_secret` varchar(128) DEFAULT NULL,
  `created_on` datetime NOT NULL,
  PRIMARY KEY (`storage_account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `storage_area`
--

DROP TABLE IF EXISTS `storage_area`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `storage_area` (
  `storage_area_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `storage_account_id` int(11) unsigned NOT NULL,
  `area_name` varchar(128) NOT NULL,
  `area_path` varchar(256) NOT NULL,
  `cdn_path` varchar(256) DEFAULT NULL,
  `created_on` datetime NOT NULL,
  `internal_path` varchar(256) DEFAULT NULL,
  `cdn_direct_path` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`storage_area_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `studyshelf_learnguide`
--

DROP TABLE IF EXISTS `studyshelf_learnguide`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `studyshelf_learnguide` (
  `content_id` bigint(20) unsigned NOT NULL,
  `learnguide_id` bigint(20) unsigned NOT NULL,
  `added_on` datetime NOT NULL,
  `studied_on` datetime NOT NULL,
  `color` varchar(45) NOT NULL,
  PRIMARY KEY (`content_id`,`learnguide_id`),
  KEY `FK_studyshelf_learnguide_2` (`learnguide_id`),
  CONSTRAINT `FK_studyshelf_content_1` FOREIGN KEY (`content_id`) REFERENCES `resource` (`content_id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `FK_studyshelf_learnguide_2` FOREIGN KEY (`learnguide_id`) REFERENCES `learnguide` (`content_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tag_synonyms`
--

DROP TABLE IF EXISTS `tag_synonyms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tag_synonyms` (
  `tag_synonyms_id` int(11) NOT NULL AUTO_INCREMENT,
  `tag_content_gooru_oid` varchar(36) NOT NULL,
  `target_tag_name` varchar(25) NOT NULL,
  `created_date` datetime NOT NULL,
  `creator_uid` varchar(36) NOT NULL,
  `status_id` int(11) NOT NULL,
  `approver_uid` varchar(36) DEFAULT NULL,
  `approval_date` datetime DEFAULT NULL,
  PRIMARY KEY (`tag_synonyms_id`),
  KEY `tag_synonyms_id` (`tag_synonyms_id`),
  KEY `tag_synonyms_id_2` (`tag_synonyms_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tag_type`
--

DROP TABLE IF EXISTS `tag_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tag_type` (
  `name` varchar(20) NOT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tags`
--

DROP TABLE IF EXISTS `tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tags` (
  `tag_uid` varchar(36) DEFAULT NULL,
  `content_id` bigint(20) unsigned NOT NULL,
  `label` varchar(400) NOT NULL,
  `tag_type_id` int(11) NOT NULL,
  `content_count` bigint(20) unsigned DEFAULT '0',
  `user_count` bigint(20) unsigned DEFAULT '0',
  `synonyms_count` int(11) DEFAULT '0',
  `status_id` int(11) NOT NULL,
  `wiki_post_gooru_oid` varchar(36) DEFAULT NULL,
  `excerpt_post_gooru_oid` varchar(36) DEFAULT NULL,
  `type` varchar(100) NOT NULL,
  `user_id` varchar(36) NOT NULL,
  `active_flag` tinyint(4) NOT NULL DEFAULT '1',
  `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `organization_uid` varchar(36) NOT NULL,
  KEY `content_id` (`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tags_assoc`
--

DROP TABLE IF EXISTS `tags_assoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tags_assoc` (
  `content_id` bigint(20) NOT NULL,
  `tag_uid` varchar(36) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task`
--

DROP TABLE IF EXISTS `task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task` (
  `task_uid` varchar(36) CHARACTER SET utf8 NOT NULL DEFAULT '',
  `title` varchar(1000) CHARACTER SET utf8 NOT NULL,
  `description` text CHARACTER SET utf8,
  `planned_start_date` datetime DEFAULT NULL,
  `planned_end_date` datetime DEFAULT NULL,
  `status` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
  `type_name` varchar(100) CHARACTER SET utf8 NOT NULL,
  `estimated_effort` double DEFAULT NULL,
  `organization_uid` varchar(36) CHARACTER SET utf8 DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `created_by_uid` varchar(36) CHARACTER SET utf8 DEFAULT NULL,
  `last_modified_date` datetime DEFAULT NULL,
  `modified_by_uid` varchar(36) CHARACTER SET utf8 DEFAULT NULL,
  `task_content_id` bigint(20) unsigned NOT NULL,
  KEY `task_uid` (`task_uid`),
  KEY `task_content_id` (`task_content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_assoc`
--

DROP TABLE IF EXISTS `task_assoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_assoc` (
  `task_assoc_uid` varchar(36) NOT NULL,
  `task_parent_uid` varchar(36) NOT NULL,
  `task_descendant_uid` varchar(36) NOT NULL,
  `association_type` varchar(36) DEFAULT NULL,
  `sequence` int(11) DEFAULT NULL,
  PRIMARY KEY (`task_assoc_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_history`
--

DROP TABLE IF EXISTS `task_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_history` (
  `task_history_uid` varchar(36) NOT NULL,
  `task_uid` varchar(36) DEFAULT NULL,
  `user_uid` varchar(36) NOT NULL,
  `created_date` datetime NOT NULL,
  `task_content_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`task_history_uid`),
  KEY `user_uid` (`user_uid`),
  KEY `task_content_id` (`task_content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_history_item`
--

DROP TABLE IF EXISTS `task_history_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_history_item` (
  `task_history_item_uid` varchar(36) NOT NULL,
  `task_history_uid` varchar(36) NOT NULL,
  `field_name` varchar(256) NOT NULL,
  `old_key` varchar(256) DEFAULT NULL,
  `old_value` text,
  `new_key` varchar(256) DEFAULT NULL,
  `new_value` text,
  PRIMARY KEY (`task_history_item_uid`),
  KEY `task_history_uid` (`task_history_uid`),
  CONSTRAINT `task_history_item_ibfk_1` FOREIGN KEY (`task_history_uid`) REFERENCES `task_history` (`task_history_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_permission`
--

DROP TABLE IF EXISTS `task_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_permission` (
  `task_uid` varchar(36) NOT NULL,
  `party_uid` varchar(36) NOT NULL,
  `permission` varchar(36) NOT NULL,
  `valid_from` datetime DEFAULT NULL,
  `expiry_date` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_resource_assoc`
--

DROP TABLE IF EXISTS `task_resource_assoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_resource_assoc` (
  `task_content_assoc_uid` varchar(36) NOT NULL,
  `task_uid` varchar(36) DEFAULT NULL,
  `resource_content_id` bigint(20) unsigned NOT NULL,
  `sequence` int(11) DEFAULT NULL,
  `association_date` datetime DEFAULT NULL,
  `associated_by_uid` varchar(36) DEFAULT NULL,
  `task_content_id` bigint(20) unsigned NOT NULL,
  KEY `task_content_assoc_uid` (`task_content_assoc_uid`),
  KEY `resource_content_id` (`resource_content_id`),
  KEY `task_content_id` (`task_content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_user_assoc`
--

DROP TABLE IF EXISTS `task_user_assoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_user_assoc` (
  `task_uid` varchar(36) DEFAULT NULL,
  `user_uid` varchar(36) NOT NULL,
  `association_type` varchar(36) DEFAULT NULL,
  `task_content_id` bigint(20) unsigned NOT NULL,
  KEY `task_content_id` (`task_content_id`),
  CONSTRAINT `task_user_assoc_ibfk_1` FOREIGN KEY (`task_content_id`) REFERENCES `task` (`task_content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `taxonomy_association`
--

DROP TABLE IF EXISTS `taxonomy_association`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `taxonomy_association` (
  `source_code_id` mediumint(8) unsigned NOT NULL DEFAULT '0',
  `target_code_id` mediumint(8) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`source_code_id`,`target_code_id`),
  KEY `FK_taxonomy_association_Code2` (`target_code_id`),
  CONSTRAINT `FK_taxonomy_association_Code1` FOREIGN KEY (`source_code_id`) REFERENCES `code` (`code_id`) ON DELETE CASCADE,
  CONSTRAINT `FK_taxonomy_association_Code2` FOREIGN KEY (`target_code_id`) REFERENCES `code` (`code_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `taxonomy_level_type`
--

DROP TABLE IF EXISTS `taxonomy_level_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `taxonomy_level_type` (
  `type_id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `label` varchar(45) DEFAULT NULL,
  `code_id` int(10) unsigned DEFAULT NULL,
  `depth` tinyint(3) unsigned NOT NULL,
  `is_autogenerated_code` tinyint(1) NOT NULL DEFAULT '0',
  `account_uid` varchar(36) DEFAULT NULL,
  `organization_uid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`type_id`),
  KEY `account_uid` (`account_uid`),
  KEY `organization_uid` (`organization_uid`),
  CONSTRAINT `taxonomy_level_type_ibfk_1` FOREIGN KEY (`account_uid`) REFERENCES `account` (`account_uid`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=95 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `template`
--

DROP TABLE IF EXISTS `template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `template` (
  `template_uid` varchar(36) NOT NULL,
  `html_content` text NOT NULL,
  `text_content` text NOT NULL,
  `subject` varchar(400) NOT NULL,
  `created_date` datetime NOT NULL,
  `creator_uid` varchar(36) NOT NULL,
  `organization_uid` varchar(36) NOT NULL,
  PRIMARY KEY (`template_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `textbook`
--

DROP TABLE IF EXISTS `textbook`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `textbook` (
  `content_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `document_id` varchar(45) NOT NULL DEFAULT '',
  `document_key` varchar(45) NOT NULL DEFAULT '',
  PRIMARY KEY (`content_id`),
  CONSTRAINT `FK_textbook_resource` FOREIGN KEY (`content_id`) REFERENCES `resource` (`content_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `track_activity`
--

DROP TABLE IF EXISTS `track_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `track_activity` (
  `activity_id` varchar(36) NOT NULL,
  `parent_activity_id` varchar(36) DEFAULT NULL,
  `user_id` varchar(36) NOT NULL,
  `start_time` date DEFAULT NULL,
  `end_time` date DEFAULT NULL,
  `activity_type` varchar(36) NOT NULL,
  `task_uid` varchar(36) DEFAULT NULL,
  `task_content_id` bigint(20) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `user_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `account_uid` varchar(36) DEFAULT NULL,
  `security_group_uid` varchar(36) DEFAULT NULL,
  `gooru_uid` varchar(36) NOT NULL,
  `role_id` tinyint(3) unsigned DEFAULT NULL,
  `firstname` varchar(120) DEFAULT NULL,
  `lastname` varchar(120) DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  `register_token` varchar(40) DEFAULT NULL,
  `confirm_status` tinyint(4) NOT NULL DEFAULT '1',
  `license_version` varchar(45) DEFAULT '1.0',
  `mail_status` int(11) DEFAULT NULL,
  `added_by_system` tinyint(4) DEFAULT '0',
  `import_code` varchar(50) DEFAULT NULL,
  `parent_id` int(10) DEFAULT NULL,
  `account_type_id` tinyint(4) unsigned DEFAULT NULL,
  `parent_uid` varchar(36) DEFAULT NULL,
  `organization_uid` varchar(36) DEFAULT NULL,
  `user_group_uid` varchar(36) DEFAULT NULL,
  `primary_organization_uid` varchar(36) NOT NULL,
  `view_flag` tinyint(4) NOT NULL DEFAULT '0',
  `is_deleted` tinyint(1) DEFAULT '0',
  `reference_uid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`gooru_uid`),
  UNIQUE KEY `user_id` (`user_id`),
  KEY `FK_user_role` (`role_id`),
  KEY `organization_uid` (`account_uid`),
  KEY `parent_id` (`parent_id`),
  KEY `parent_uid` (`parent_uid`),
  KEY `organization_uid_2` (`organization_uid`),
  CONSTRAINT `user_ibfk_1` FOREIGN KEY (`organization_uid`) REFERENCES `organization` (`organization_uid`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=22183 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_classification`
--

DROP TABLE IF EXISTS `user_classification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_classification` (
  `user_uid` varchar(36) NOT NULL,
  `code_id` mediumint(8) unsigned DEFAULT NULL,
  `classification_type` int(11) NOT NULL,
  `user_classification_id` varchar(36) NOT NULL,
  `creator_uid` varchar(36) NOT NULL,
  `active_flag` tinyint(4) NOT NULL DEFAULT '1',
  `grade` varchar(256) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_content_assoc`
--

DROP TABLE IF EXISTS `user_content_assoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_content_assoc` (
  `user_id` int(10) unsigned DEFAULT NULL,
  `content_id` bigint(20) unsigned NOT NULL,
  `relationship_id` int(11) unsigned NOT NULL DEFAULT '0',
  `last_active_date` datetime NOT NULL,
  `user_uid` varchar(36) NOT NULL DEFAULT '',
  `associated_type` varchar(36) DEFAULT NULL,
  `associated_by_uid` varchar(36) DEFAULT NULL,
  `association_date` date DEFAULT NULL,
  PRIMARY KEY (`user_uid`,`content_id`,`relationship_id`),
  KEY `user_id_fk` (`user_id`),
  KEY `content_id_fk` (`content_id`),
  KEY `user_uid` (`user_uid`),
  CONSTRAINT `user_content_assoc_ibfk_2` FOREIGN KEY (`content_id`) REFERENCES `content` (`content_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_group`
--

DROP TABLE IF EXISTS `user_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_group` (
  `user_group_uid` varchar(36) NOT NULL,
  `organization_uid` varchar(36) NOT NULL,
  `user_group_type` varchar(36) NOT NULL,
  `active_flag` tinyint(1) NOT NULL,
  `user_group_code` varchar(255) NOT NULL,
  `name` varchar(36) NOT NULL,
  `created_by_uid` varchar(255) NOT NULL,
  PRIMARY KEY (`user_group_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_group_association`
--

DROP TABLE IF EXISTS `user_group_association`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_group_association` (
  `gooru_uid` varchar(36) NOT NULL,
  `user_group_uid` varchar(36) NOT NULL,
  `is_group_owner` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_perm`
--

DROP TABLE IF EXISTS `user_perm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_perm` (
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `content_id` bigint(20) unsigned NOT NULL,
  `perm_name` varchar(20) NOT NULL,
  `user_uid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`user_id`,`content_id`),
  KEY `FK_user_perm_PERM` (`perm_name`),
  KEY `FK_user_perm_user` (`user_id`),
  KEY `FK_user_perm_content` (`content_id`),
  KEY `user_uid` (`user_uid`),
  CONSTRAINT `FK_user_perm_content` FOREIGN KEY (`content_id`) REFERENCES `content` (`content_id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `FK_user_perm_PERM` FOREIGN KEY (`perm_name`) REFERENCES `perm` (`name`) ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_relationship`
--

DROP TABLE IF EXISTS `user_relationship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_relationship` (
  `user_relationship_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned DEFAULT NULL,
  `follow_on_user_id` int(11) unsigned DEFAULT NULL,
  `activated_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deactivated_date` timestamp NULL DEFAULT NULL,
  `active_flag` tinyint(1) unsigned NOT NULL,
  `account_uid` varchar(36) DEFAULT NULL,
  `user_uid` varchar(36) DEFAULT NULL,
  `follow_on_user_uid` varchar(36) DEFAULT NULL,
  `organization_uid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`user_relationship_id`),
  KEY `account_uid` (`account_uid`),
  KEY `user_uid` (`user_uid`),
  KEY `follow_on_user_uid` (`follow_on_user_uid`),
  KEY `organization_uid` (`organization_uid`),
  CONSTRAINT `user_relationship_ibfk_1` FOREIGN KEY (`account_uid`) REFERENCES `account` (`account_uid`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_role` (
  `role_id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `account_uid` varchar(36) CHARACTER SET utf8 DEFAULT NULL,
  `organization_uid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`role_id`),
  KEY `account_uid` (`account_uid`),
  KEY `organization_uid` (`organization_uid`),
  CONSTRAINT `user_role_ibfk_1` FOREIGN KEY (`account_uid`) REFERENCES `account` (`account_uid`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=107 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_role_assoc`
--

DROP TABLE IF EXISTS `user_role_assoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_role_assoc` (
  `user_id` int(11) DEFAULT NULL,
  `role_id` int(11) NOT NULL,
  `user_uid` varchar(36) DEFAULT NULL,
  KEY `user_uid` (`user_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Association between User and roles';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_role_authority`
--

DROP TABLE IF EXISTS `user_role_authority`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_role_authority` (
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `role_id` int(10) unsigned NOT NULL,
  `user_uid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `fk_user_role_authority1` (`user_id`),
  KEY `role_id` (`role_id`),
  KEY `user_uid` (`user_uid`),
  CONSTRAINT `user_role_authority_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_security_group`
--

DROP TABLE IF EXISTS `user_security_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_security_group` (
  `security_group_uid` varchar(36) NOT NULL,
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `user_uid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`security_group_uid`,`user_id`),
  KEY `security_group_uid` (`security_group_uid`),
  KEY `user_id` (`user_id`),
  KEY `user_uid` (`user_uid`),
  CONSTRAINT `user_security_group_ibfk_2` FOREIGN KEY (`security_group_uid`) REFERENCES `security_group` (`security_group_uid`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_tag_assoc`
--

DROP TABLE IF EXISTS `user_tag_assoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_tag_assoc` (
  `user_uid` varchar(36) NOT NULL,
  `tag_gooru_oid` varchar(36) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_token`
--

DROP TABLE IF EXISTS `user_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_token` (
  `token` varchar(36) NOT NULL,
  `user_id` int(10) unsigned DEFAULT NULL,
  `account_uid` varchar(36) DEFAULT NULL,
  `session_id` varchar(50) DEFAULT NULL,
  `api_key_id` int(11) unsigned DEFAULT NULL,
  `scope` varchar(10) NOT NULL,
  `created_on` datetime DEFAULT NULL,
  `user_uid` varchar(36) DEFAULT NULL,
  `organization_uid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`token`),
  KEY `FK_userToken_user` (`user_id`),
  KEY `idx_sessionid` (`session_id`),
  KEY `user_uid` (`user_uid`),
  KEY `organization_uid` (`organization_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vocabulary_assoc`
--

DROP TABLE IF EXISTS `vocabulary_assoc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vocabulary_assoc` (
  `tag_uid` varchar(36) NOT NULL,
  `content_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-01-31  7:38:45
