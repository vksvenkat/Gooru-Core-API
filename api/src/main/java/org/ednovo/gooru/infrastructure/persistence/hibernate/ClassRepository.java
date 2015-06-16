package org.ednovo.gooru.infrastructure.persistence.hibernate;

import org.ednovo.gooru.core.api.model.UserClass;

public interface ClassRepository extends BaseRepository {

	UserClass getClassById(String classUid);
}
