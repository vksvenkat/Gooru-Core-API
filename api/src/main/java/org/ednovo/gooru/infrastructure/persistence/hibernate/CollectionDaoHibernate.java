package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

@Repository
public class CollectionDaoHibernate extends BaseRepositoryHibernate implements CollectionDao, ConstantProperties, ParameterProperties {

	private static final String PARAMETER_ONE = "parameterOne";

	private static final String PARAMETER_TWO = "parameterTwo";

	private static final String GET_COLLECTION = "FROM Collection where gooruOid=:collectionId";

	private static final String COLLECTION_BY_TYPE = "FROM Collection where gooruOid=:collectionId and collectionType=:collectionType";

	private static final String GET_COLLECTION_BY_TYPE = "FROM Collection where user.partyUid=:partyUid and collectionType=:collectionType";

	private static final String MAX_COLLECTION_ITEM_SEQ = "select IFNULL(max(item_sequence), 0) as count from collection_item where collection_content_id=:collectionId";

	private static final String GET_COLLECTION_BY_USER = "FROM Collection where user.partyUid=:partyUid and gooruOid=:collectionId";

	private static final String GET_COLLECTIONS = "select re.title, cr.gooru_oid as gooruOid, re.collection_type as type, re.image_path as imagePath, cr.sharing, ci.collection_item_id as collectionItemId, co.goals, co.ideas, co.questions,co.performance_tasks as performanceTasks, co.collection_type as collectionType, ci.item_sequence as itemSequence, cc.gooru_oid as parentGooruOid, re.description, re.url, cs.data, cm.meta_data as metaData, re.publish_status_id as publishStatus, re.build_type_id as buildType, cr.user_uid as gooruUId, u.username  from  collection c  inner join content cc on cc.content_id =  c.content_id inner join collection_item ci on ci.collection_content_id = c.content_id inner join collection re on re.content_id = ci.resource_content_id inner join content cr on  cr.content_id = re.content_id left join content_settings cs on cs.content_id = re.content_id inner join organization o  on  o.organization_uid = cr.organization_uid  left join collection co on co.content_id = re.content_id left join content_meta cm  on  cm.content_id = re.content_id left join user u on u.gooru_uid = cr.user_uid ";

	private static final String GET_COLLECTION_ITEMS = "select r.title, c.gooru_oid as gooruOid, r.type_name as resourceType, r.folder, r.thumbnail, ct.value, ct.display_name as displayName, c.sharing, ci.collection_item_id as collectionItemId, r.url ,rsummary.rating_star_avg as average, rsummary.rating_star_count as count, co.collection_type as collectionType, ci.item_sequence as itemSequence, rc.gooru_oid as parentGooruOid, r.description, ci.start, ci.stop, cm.meta_data as metaData, ci.narration, aq.type, aq.type_name as typeName, question_text as questionText, explanation, c.user_uid as gooruUId, u.username  from collection_item ci inner join resource r on r.content_id = ci.resource_content_id  left join custom_table_value ct on ct.custom_table_value_id = r.resource_format_id inner join content c on c.content_id = r.content_id inner join content rc on rc.content_id = ci.collection_content_id left join collection co on co.content_id = r.content_id left join content_meta cm on cm.content_id = c.content_id  left join resource_summary rsummary on   c.gooru_oid = rsummary.resource_gooru_oid left join assessment_question aq on aq.question_id = r.content_id left join user u on u.gooru_uid = c.user_uid  where rc.gooru_oid=:collectionId ";

	private static final String GET_COLLECTION_ITEM_COUNT = "select count(1) as count from collection_item ci inner join collection  c  on c.content_id = ci.collection_content_id inner join collection co on ci.resource_content_id  = co.content_id   where c.content_id =:collectionId and co.collection_type =:collectionType";

	private static final String GET_COLLECTION_SEQUENCE = "FROM CollectionItem ci where ci.collection.gooruOid=:gooruOid and ci.itemSequence between :parameterOne and :parameterTwo order by ci.itemSequence";

	private static final String GET_COLLECTIONITEM_BY_GOORUOID = "FROM CollectionItem where content.gooruOid=:gooruOid and collection.gooruOid=:parentGooruOid";

	private final static String GET_COLLECTIONITEM_BY_SEQUENCE = "FROM CollectionItem where collection.gooruOid=:gooruOid and itemSequence>:sequence order by itemSequence";

	private static final String DELETE_COLLECTIONITEM = "delete from collection_item where resource_content_id=:contentId";

	private static final String COLLECTIONITEM_BY_USERUID = "FROM CollectionItem ci where ci.content.gooruOid=:gooruOid and ci.associatedUser=:user";

	private static final String GET_PARENTCOLLECTION = "FROM CollectionItem ci where ci.content.contentId=:contentId";

	@Override
	public Collection getCollection(String collectionId) {
		Query query = getSession().createQuery(GET_COLLECTION);
		query.setParameter(COLLECTION_ID, collectionId);
		List<Collection> collection = list(query);
		return (collection != null && collection.size() > 0) ? collection.get(0) : null;
	}

	@Override
	public Collection getCollectionByType(String collectionId, String collectionType) {
		Query query = getSession().createQuery(COLLECTION_BY_TYPE);
		query.setParameter(COLLECTION_ID, collectionId);
		query.setParameter(COLLECTION_TYPE, collectionType);
		List<Collection> collection = list(query);
		return (collection != null && collection.size() > 0) ? collection.get(0) : null;
	}

	@Override
	public Collection getCollection(String userUid, String collectionType) {
		Query query = getSession().createQuery(GET_COLLECTION_BY_TYPE);
		query.setParameter(PARTY_UID, userUid);
		query.setParameter(COLLECTION_TYPE, collectionType);
		List<Collection> collection = list(query);
		return (collection != null && collection.size() > 0) ? collection.get(0) : null;
	}

	@Override
	public Collection getCollectionByUser(String collectionId, String userUid) {
		Query query = getSession().createQuery(GET_COLLECTION_BY_USER);
		query.setParameter(COLLECTION_ID, collectionId);
		query.setParameter(PARTY_UID, userUid);
		List<Collection> collection = list(query);
		return (collection != null && collection.size() > 0) ? collection.get(0) : null;
	}

	@Override
	public int getCollectionItemMaxSequence(Long contentId) {
		Query query = getSession().createSQLQuery(MAX_COLLECTION_ITEM_SEQ).addScalar(COUNT, StandardBasicTypes.INTEGER);
		query.setParameter(COLLECTION_ID, contentId);
		return (int) list(query).get(0);
	}

	@Override
	public List<Map<String, Object>> getCollections(Map<String, Object> filters, int limit, int offset) {
		StringBuilder sql = new StringBuilder(GET_COLLECTIONS);
		if (filters != null) {
			StringBuilder sqlQuery = new StringBuilder();
			if (filters.get(SHARING) != null) {
				queryAppender(sqlQuery).append(" cr.sharing in (:sharing) ");
			}
			if (filters.get(GOORU_UID) != null) {
				queryAppender(sqlQuery).append(" cc.user_uid =:gooruUId ");
			}
			if (filters.get(PARENT_COLLECTION_TYPE) != null) {
				queryAppender(sqlQuery).append(" c.collection_type =:parentCollectionType ");
			}
			if (filters.get(PARENT_GOORU_OID) != null) {
				queryAppender(sqlQuery).append(" cc.gooru_oid =:parentGooruOid ");
			}
			if (filters.get(GOORU_OID) != null) {
				queryAppender(sqlQuery).append(" cr.gooru_oid =:gooruOid ");
			}
			if (filters.get(COLLECTION_TYPE) != null) {
				queryAppender(sqlQuery).append(" re.collection_type in (:collectionType) ");
			}
			if (filters.get(EXCLUDE_TYPE) != null) {
				queryAppender(sqlQuery).append(" co.collection_type not in (:exculdeType) ");
			}
			if (filters.get(ITEM_TYPE) != null) {
				queryAppender(sqlQuery).append(" ci.item_type != :itemType ");
			}
			sql.append(sqlQuery.toString());
		}
		sql.append(" order by ci.item_sequence");
		Query query = getSession().createSQLQuery(sql.toString());
		if (filters != null) {
			setQueryParamter(filters, query);
		}
		query.setFirstResult(offset);
		query.setMaxResults(limit > MAX_LIMIT ? MAX_LIMIT : limit);
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		return list(query);
	}

	@Override
	public List<Map<String, Object>> getCollectionItem(Map<String, Object> filters, int limit, int offset) {
		StringBuilder sql = new StringBuilder(GET_COLLECTION_ITEMS);
		if (filters.get(SHARING) != null) {
			sql.append(" and c.sharing in (:sharing) ");
		}
		if (filters.get(GOORU_OID) != null) {
			sql.append(" and c.gooru_oid =:").append(GOORU_OID);
		}
		if (filters.get(COLLECTION_ITEM_ID) != null) {
			sql.append(" and ci.collection_item_id =:").append(COLLECTION_ITEM_ID);
		}
		sql.append(" order by ci.item_sequence");
		Query query = getSession().createSQLQuery(sql.toString());
		query.setParameter(COLLECTION_ID, filters.get(COLLECTION_ID));
		setQueryParamter(filters, query);
		query.setFirstResult(offset);
		query.setMaxResults(limit > MAX_LIMIT ? MAX_LIMIT : limit);
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		return list(query);
	}

	@Override
	public int getCollectionItemCount(Long contentId, String collectionType) {
		Query query = getSession().createSQLQuery(GET_COLLECTION_ITEM_COUNT).addScalar(COUNT, StandardBasicTypes.INTEGER);
		query.setParameter(COLLECTION_ID, contentId);
		query.setParameter(COLLECTION_TYPE, collectionType);
		return (int) list(query).get(0);
	}

	@Override
	public List<CollectionItem> getCollectionItems(String gooruOid, int parameterOne, int parameterTwo) {
		Query query = getSession().createQuery(GET_COLLECTION_SEQUENCE);
		query.setParameter(PARAMETER_ONE, parameterOne);
		query.setParameter(PARAMETER_TWO, parameterTwo);
		query.setParameter(GOORU_OID, gooruOid);
		return list(query);
	}

	@Override
	public CollectionItem getCollectionItem(String parentGooruOid, String gooruOid) {
		Query query = getSession().createQuery(GET_COLLECTIONITEM_BY_GOORUOID);
		query.setParameter(GOORU_OID, gooruOid);
		query.setParameter(PARENT_GOORU_OID, parentGooruOid);
		return (query.list().size() > 0) ? (CollectionItem) query.list().get(0) : null;
	}

	@Override
	public List<CollectionItem> getCollectionItems(String gooruOid, int sequence) {
		Query query = getSession().createQuery(GET_COLLECTIONITEM_BY_SEQUENCE);
		query.setParameter(GOORU_OID, gooruOid);
		query.setParameter(SEQUENCE, sequence);
		return list(query);
	}

	@Override
	public void deleteCollectionItem(Long contentId) {
		Query query = getSession().createSQLQuery(DELETE_COLLECTIONITEM);
		query.setParameter(CONTENT_ID, contentId);
		query.executeUpdate();
	}

	@Override
	public CollectionItem getCollectionItemById(String gooruOid, User user) {
		Query query = getSession().createQuery(COLLECTIONITEM_BY_USERUID);
		query.setParameter(GOORU_OID, gooruOid);
		query.setParameter(USER, user);
		return (CollectionItem) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@Override
	public CollectionItem getParentCollection(Long contentId) {
		Query query = getSession().createQuery(GET_PARENTCOLLECTION).setParameter(CONTENT_ID, contentId);
		return (CollectionItem) (query.list().size() > 0 ? query.list().get(0) : null);
	}

}
