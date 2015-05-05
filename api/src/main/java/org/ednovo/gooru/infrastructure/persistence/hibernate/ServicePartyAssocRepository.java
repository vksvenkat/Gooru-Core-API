package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.util.Map;

public interface ServicePartyAssocRepository extends BaseRepository {
	
	public String getPartyVersion(String partyUid, String partyType);
	
	public Map<String, Object> getPartyVersion(String userUid, String organizationUid, String groupUid);
	
}
