package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.ContentMeta;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

@Repository
public class CollectionDaoHibernate extends BaseRepositoryHibernate implements CollectionDao, ConstantProperties, ParameterProperties {

	private static final String GET_COLLECTION = "FROM Collection where gooruOid=:collectionId";

	private static final String GET_COLLECTION_BY_TYPE = "FROM Collection where user.partyUid=:partyUid and collectionType=:collectionType";

	private static final String MAX_COLLECTION_ITEM_SEQ = "select IFNULL(max(item_sequence), 0) as count from collection_item where collection_content_id=:collectionId";

	private static final String GET_COLLECTION_BY_USER = "FROM Collection where user.partyUid=:partyUid and gooruOid=:collectionId";

	private static final String GET_COLLECTIONS = "select re.title, cr.gooru_oid as gooruOid, re.collection_type as type, re.image_path as imagePath, cr.sharing, ci.collection_item_id as collectionItemId, co.goals, co.ideas, co.questions,co.performance_tasks as performanceTasks, co.collection_type as collectionType, ci.item_sequence as itemSequence, cc.gooru_oid as parentGooruOid, re.description, re.url, cs.data, cm.meta_data as metaData from  collection c  inner join content cc on cc.content_id =  c.content_id inner join collection_item ci on ci.collection_content_id = c.content_id inner join collection re on re.content_id = ci.resource_content_id inner join content cr on  cr.content_id = re.content_id left join content_settings cs on cs.content_id = re.content_id inner join organization o  on  o.organization_uid = cr.organization_uid  left join collection co on co.content_id = re.content_id left join content_meta cm  on  cm.content_id = re.content_id ";

	private static final String GET_COLLECTION_ITEMS = "select r.title, c.gooru_oid as gooruOid, r.type_name as type, r.folder, r.thumbnail, ct.value, ct.display_name as displayName, c.sharing, ci.collection_item_id as collectionItemId, co.goals, rs.attribution, rs.domain_name as domainName, co.ideas, co.questions, co.performance_tasks as performanceTasks, r.url ,rsummary.rating_star_avg as average, rsummary.rating_star_count as count, co.collection_type as collectionType, ci.item_sequence as itemSequence, rc.gooru_oid as parentGooruOid, r.description  from collection_item ci inner join resource r on r.content_id = ci.resource_content_id  left join custom_table_value ct on ct.custom_table_value_id = r.resource_format_id inner join content c on c.content_id = r.content_id inner join content rc on rc.content_id = ci.collection_content_id left join collection co on co.content_id = r.content_id left join resource_source rs on rs.resource_source_id = r.resource_source_id left join resource_summary rsummary on   c.gooru_oid = rsummary.resource_gooru_oid where rc.gooru_oid=:collectionId ";

	@Override
	public Collection getCollection(String collectionId) {
		Query query = getSession().createQuery(GET_COLLECTION);
		query.setParameter(COLLECTION_ID, collectionId);
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
		sql.append(" order by ci.item_sequence desc ");
		Query query = getSession().createSQLQuery(sql.toString());
		if (filters != null) {
			for (Map.Entry<String, Object> data : filters.entrySet()) {
				if (data.getValue() instanceof String[]) {
					query.setParameterList(data.getKey(), (String[]) data.getValue());
				} else {
					query.setParameter(data.getKey(), data.getValue());
				}
			}
		}
		query.setFirstResult(offset);
		query.setMaxResults(limit > MAX_LIMIT ? MAX_LIMIT : limit);
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		return list(query);
	}

	@Override
	public List<Map<String, Object>> getCollectionItem(String collectionId, String[] sharing, int limit, int offset) {
		StringBuilder sql = new StringBuilder(GET_COLLECTION_ITEMS);
		if (sharing != null) {
			sql.append(" and c.sharing in (:sharing) ");
		}
		sql.append(" order by ci.item_sequence");
		Query query = getSession().createSQLQuery(sql.toString());
		if (sharing != null) {
			query.setParameterList(SHARING, sharing);
		}
		query.setParameter(COLLECTION_ID, collectionId);
		query.setFirstResult(offset);
		query.setMaxResults(limit > MAX_LIMIT ? MAX_LIMIT : limit);
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		return list(query);
	}

}
