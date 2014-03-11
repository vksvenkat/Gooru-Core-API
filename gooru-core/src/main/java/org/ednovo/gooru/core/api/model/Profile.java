/*******************************************************************************
 * Profile.java
 *  gooru-core
 *  Created by Gooru on 2014
 *  Copyright (c) 2014 Gooru. All rights reserved.
 *  http://www.goorulearning.org/
 *       
 *  Permission is hereby granted, free of charge, to any 
 *  person obtaining a copy of this software and associated 
 *  documentation. Any one can use this software without any 
 *  restriction and can use without any limitation rights 
 *  like copy,modify,merge,publish,distribute,sub-license or 
 *  sell copies of the software.
 *  
 *  The seller can sell based on the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be   
 *  included in all copies or substantial portions of the Software. 
 * 
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
 *   KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
 *   WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
 *   PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE       AUTHORS 
 *   OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
 *   OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *   OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *   WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 *   THE SOFTWARE.
 ******************************************************************************/
package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Profile implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5654980863901447965L;

	private User user;

	private String grade;
	private String subject;
	private Integer birthDate;
	private Integer birthMonth;
	private Integer birthYear;
	private String aboutMe;
	private String teachingExperience;
	private String teachingIn;
	private String teachingMethodology;
	private String highestDegree;
	private String postGraduation;
	private String graduation;
	private String highSchool;
	private byte[] pictureBlob;
	private byte[] thumbnailBlob;
	private Integer isPublisherRequestPending;
	private String pictureFormat;
	private Date dateOfBirth;
	private Date childDateOfBirth;
	private List<UserClassification> courses;
	private String notes;

	private String externalId;

	private Integer subscribersSize;

	public byte[] getThumbnailBlob() {
		return thumbnailBlob;
	}

	public void setThumbnailBlob(byte[] thumbnailBlob) {
		this.thumbnailBlob = thumbnailBlob;
	}

	private String website;
	private String facebook;
	private String twitter;
	private String userType;
	private Language firstLanguage;
	private Language secondLanguage;
	private Language thirdLanguage;
	private City city;
	private Country country;
	private Province province;
	private Gender gender;

	private String school;

	private String profileId; // same as user id in db

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Integer getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Integer birthDate) {
		this.birthDate = birthDate;
	}

	public Integer getBirthMonth() {
		return birthMonth;
	}

	public void setBirthMonth(Integer birthMonth) {
		this.birthMonth = birthMonth;
	}

	public Integer getBirthYear() {
		return birthYear;
	}

	public void setBirthYear(Integer birthYear) {
		this.birthYear = birthYear;
	}

	public String getAboutMe() {
		return aboutMe;
	}

	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}

	public String getTeachingExperience() {
		return teachingExperience;
	}

	public void setTeachingExperience(String teachingExperience) {
		this.teachingExperience = teachingExperience;
	}

	public String getTeachingIn() {
		return teachingIn;
	}

	public void setTeachingIn(String teachingIn) {
		this.teachingIn = teachingIn;
	}

	public String getTeachingMethodology() {
		return teachingMethodology;
	}

	public void setTeachingMethodology(String teachingMethodology) {
		this.teachingMethodology = teachingMethodology;
	}

	public String getHighestDegree() {
		return highestDegree;
	}

	public void setHighestDegree(String highestDegree) {
		this.highestDegree = highestDegree;
	}

	public String getPostGraduation() {
		return postGraduation;
	}

	public void setPostGraduation(String postGraduation) {
		this.postGraduation = postGraduation;
	}

	public String getGraduation() {
		return graduation;
	}

	public void setGraduation(String graduation) {
		this.graduation = graduation;
	}

	public String getHighSchool() {
		return highSchool;
	}

	public void setHighSchool(String highSchool) {
		this.highSchool = highSchool;
	}

	public Language getFirstLanguage() {
		return firstLanguage;
	}

	public void setFirstLanguage(Language firstLanguage) {
		this.firstLanguage = firstLanguage;
	}

	public Language getSecondLanguage() {
		return secondLanguage;
	}

	public void setSecondLanguage(Language secondLanguage) {
		this.secondLanguage = secondLanguage;
	}

	public Language getThirdLanguage() {
		return thirdLanguage;
	}

	public void setThirdLanguage(Language thirdLanguage) {
		this.thirdLanguage = thirdLanguage;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Province getProvince() {
		return province;
	}

	public void setProvince(Province province) {
		this.province = province;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public byte[] getPictureBlob() {
		return pictureBlob;
	}

	public void setPictureBlob(byte[] pictureBlob) {
		this.pictureBlob = pictureBlob;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getFacebook() {
		return facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}

	public String getTwitter() {
		return twitter;
	}

	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getPictureFormat() {
		return pictureFormat;
	}

	public void setPictureFormat(String pictureFormat) {
		this.pictureFormat = pictureFormat;
	}

	public Integer getIsPublisherRequestPending() {
		if (isPublisherRequestPending == null) {
			isPublisherRequestPending = 0;
		}
		return isPublisherRequestPending;
	}

	public void setIsPublisherRequestPending(Integer isRequestPending) {
		if (isRequestPending == null) {
			isRequestPending = 0;
		}
		this.isPublisherRequestPending = isRequestPending;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Date getChildDateOfBirth() {
		return childDateOfBirth;
	}

	public void setChildDateOfBirth(Date childDateOfBirth) {
		this.childDateOfBirth = childDateOfBirth;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public Integer getSubscribersSize() {
		return subscribersSize;
	}

	public void setSubscribersSize(Integer subscribersSize) {
		this.subscribersSize = subscribersSize;
	}

	public void setCourses(List<UserClassification> courses) {
		this.courses = courses;
	}

	public List<UserClassification> getCourses() {
		return courses;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

}
