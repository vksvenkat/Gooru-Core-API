package org.ednovo.gooru.infrastructure.persistence.hibernate;

public interface ServicePartyAssocRepository extends BaseRepository {
	
	public String getPartyVersion(String partyUid, String partyType);

}
