package org.ednovo.gooru.core.api.model;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Id;

import org.ednovo.gooru.core.cassandra.model.IsCassandraIndexable;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.core.type.TypeReference;
//import javax.persistence.Column;
//import javax.persistence.Id;

@JsonFilter("content")
public class Content extends OrganizationModel implements IndexableEntry, IsCassandraIndexable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -760986616350656864L;

	private static final String INDEX_TYPE = "content";

	@Id
	private Long contentId;

	@Column
	private String gooruOid;

	@Column
	private String sharing;

	@Column
	private Date createdOn;

	@Column
	private Date lastModified;

	@Column
	private String lastModifiedString;

	@Column
	private User user;

	@Column
	private User creator;

	@Column
	private String lastUpdatedUserUid;

	@Column
	private Integer version;

	private ContentType contentType;

	@JsonManagedReference
	private Set<Code> taxonomySet = new HashSet<Code>();

	@JsonManagedReference
	private Set<ContentMetaAssociation> contentMetaAssoc = new HashSet<ContentMetaAssociation>();

	private Set<ContentPermission> contentPermissions;

	@Column
	private String revisionHistoryUid;

	private CustomTableValue statusType;

	private List<Tag> tagSet;

	private Boolean isDeleted;

	private Map<String, Object> meta;

	private Map<String, String> settings = new HashMap<String, String>();

	private Set<ContentSettings> contentSettings;
	
	private Set<ContentProviderAssociation> ContentProviderAssoc;
	

	public Long getContentId() {
		return contentId;
	}

	public void setContentId(Long contentId) {
		this.contentId = contentId;

	}

	public String getRevisionHistoryUid() {
		return revisionHistoryUid;
	}

	public void setRevisionHistoryUid(String revisionHistoryUid) {
		this.revisionHistoryUid = revisionHistoryUid;
	}

	public User getCreator() {
		if (creator == null) {
			creator = user;
		}
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public String getGooruOid() {
		return gooruOid;
	}

	public void setGooruOid(String gooruOid) {
		this.gooruOid = gooruOid;
	}

	public String getSharing() {
		return sharing;
	}

	public void setSharing(String sharing) {
		this.sharing = sharing;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		if (contentId == null && creator == null) {
			creator = user;
		}
		this.user = user;
	}

	public ContentType getContentType() {
		return contentType;
	}

	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	public Set<Code> getTaxonomySet() {
		return taxonomySet;
	}

	public void setTaxonomySet(Set<Code> taxonomySet) {
		this.taxonomySet = taxonomySet;
	}

	public boolean hasPermissions(User user, PermissionType permissionType) {
		if (getContentPermissions() != null) {
			for (ContentPermission contentPermission : getContentPermissions())
				if (user.getPartyUid().equals(contentPermission.getParty().getPartyUid())) {
					return true;
				}
		}
		return false;
	}

	public String collaboratorsInAString() {
		Set<ContentPermission> collaboratorsList = this.getContentPermissions();
		StringBuffer result = new StringBuffer();
		if (collaboratorsList != null) {
			Iterator<ContentPermission> iter = collaboratorsList.iterator();
			while (iter.hasNext()) {
				ContentPermission usrPerm = iter.next();
				Party usr = usrPerm.getParty();
				result.append("~");
				result.append(usr.getPartyUid());
			}
		}
		return result.toString();
	}

	public String getLastModifiedString() {
		return lastModifiedString;
	}

	public void setLastModifiedString(String lastModifiedString) {
		this.lastModifiedString = lastModifiedString;
	}

	public Set<ContentPermission> getContentPermissions() {
		return contentPermissions;
	}

	public void setContentPermissions(Set<ContentPermission> contentPermissions) {
		this.contentPermissions = contentPermissions;
	}

	@Override
	public String getEntryId() {
		String id = null;
		if (contentId != null) {
			id = contentId.toString();
		}
		return id;
	}

	public String getLastUpdatedUserUid() {
		return lastUpdatedUserUid;
	}

	public void setLastUpdatedUserUid(String lastUpdatedUserUid) {
		this.lastUpdatedUserUid = lastUpdatedUserUid;
	}

	@Override
	public String getIndexId() {
		return getEntryId();
	}

	@Override
	public String getIndexType() {
		return INDEX_TYPE;
	}

	public void setTagSet(List<Tag> tagSet) {
		this.tagSet = tagSet;
	}

	public List<Tag> getTagSet() {
		return tagSet;
	}

	public void setStatusType(CustomTableValue statusType) {
		this.statusType = statusType;
	}

	public CustomTableValue getStatusType() {
		return statusType;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setMeta(Map<String, Object> meta) {
		this.meta = meta;
	}

	public Map<String, Object> getMeta() {
		return meta;
	}

	public void setContentMetaAssoc(Set<ContentMetaAssociation> contentMetaAssoc) {
		this.contentMetaAssoc = contentMetaAssoc;
	}

	public Set<ContentMetaAssociation> getContentMetaAssoc() {
		return contentMetaAssoc;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Set<ContentSettings> getContentSettings() {
		return contentSettings;
	}

	public Map<String, String> getSettings() {
		if (getContentSettings() != null && getContentSettings().size() > 0) {
			ContentSettings contentSettings = getContentSettings().iterator().next();
			return JsonDeserializer.deserialize(contentSettings.getData(), new TypeReference<Map<String, String>>() {
			});
		}
		return settings;
	}

	public void setSettings(Map<String, String> settings) {
		this.settings = settings;
	}

	public void setContentSettings(Set<ContentSettings> contentSettings) {
		this.contentSettings = contentSettings;
	}

	public Set<ContentProviderAssociation> getContentProviderAssoc() {
		return ContentProviderAssoc;
	}

	public void setContentProviderAssoc(Set<ContentProviderAssociation> contentProviderAssoc) {
		ContentProviderAssoc = contentProviderAssoc;
	}

}
