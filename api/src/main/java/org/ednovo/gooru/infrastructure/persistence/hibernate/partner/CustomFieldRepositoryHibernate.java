/////////////////////////////////////////////////////////////
// CustomFieldRepositoryHibernate.java
// gooru-api
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.infrastructure.persistence.hibernate.partner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.CustomField;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

@Repository
public class CustomFieldRepositoryHibernate extends BaseRepositoryHibernate implements CustomFieldRepository {

	private Map<String, Map<String, String>> customFieldColumnRelation;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/*
	 * Form custom field map if it is already in customFieldAndColumnNameMap
	 * returns it. ie - Map<'organizationUid', Map<'customer_name', 'jhon'> >
	 */

	private List<Object[]> getAllCustomFields(String accountUId, String searchAlias) {
		String sql = "SELECT data_column_name, name, group_code  FROM  custom_fields WHERE ";
		if (accountUId != null) {
			sql += " organization_uid =?";
		}
		if (accountUId != null && searchAlias != null) {
			sql += " AND ";
		}
		if (searchAlias != null) {
			sql += " search_alias_name=? AND add_to_search=?";
		}
		if ((accountUId != null || searchAlias != null)) {
			sql += " AND ";
		}
		sql += " show_in_response=?";

		SQLQuery query = getSession().createSQLQuery(sql);

		int currentParameter = 0;
		if (accountUId != null) {
			query.setParameter(currentParameter, accountUId, StandardBasicTypes.STRING);
			currentParameter++;
		}
		if (searchAlias != null) {
			query.setParameter(currentParameter, searchAlias, StandardBasicTypes.STRING);
			query.setParameter(currentParameter + 1, 1, StandardBasicTypes.INTEGER);
			currentParameter++;
		}
		query.setParameter(currentParameter, 1, StandardBasicTypes.INTEGER);
		return arrayList(query);
	}

	@Override
	public int checkIfCustomFieldAlreadyInserted(String resourceGooruOId) {
		String sql = "SELECT resource_gooru_oid FROM custom_fields_data WHERE resource_gooru_oid=?";
		SQLQuery query = getSession().createSQLQuery(sql);

		if (resourceGooruOId != null) {
			query.setParameter(0, resourceGooruOId, StandardBasicTypes.STRING);
		}
		if (query.list().size() > 0) {
			return 1;
		}
		return 0;
	}

	private Map<String, String> buildCustomFieldsDefination(String accountUId) {
		List<Object[]> customFieldsList = getAllCustomFields(accountUId, null);
		Map<String, String> fieldNameAndMappedColumnName = new HashMap<String, String>();
		for (Object[] row : customFieldsList) {
			String fieldName = (String) row[1];
			String mappedColumnName = (String) row[0];
			fieldNameAndMappedColumnName.put(fieldName, mappedColumnName);
		}

		if (customFieldColumnRelation == null) {
			customFieldColumnRelation = new HashMap<String, Map<String, String>>();
		}
		customFieldColumnRelation.put(accountUId, fieldNameAndMappedColumnName);
		this.setCustomFieldColumnRelation(customFieldColumnRelation);
		return fieldNameAndMappedColumnName;
	}

	private Map<String, String> buildCustomFieldsDataMap(String accountUId, String resourceId) {
		// get all customFields for the given organization
		List<Object[]> customFieldsList = getAllCustomFields(accountUId, null);
		Map<String, String> innerCustomFieldValueMap = new HashMap<String, String>();
		if (customFieldsList.size() > 0) {
			String fields = "";
			int count = 0;

			List<String> customFieldNames = new ArrayList<String>();
			List<String> customFieldGroup = new ArrayList<String>();

			for (Object[] row : customFieldsList) {
				if (count > 0) {
					fields += ",";
				}

				String fieldName = (String) row[1];
				String mappedColumnName = (String) row[0];
				String groupName = (String) row[2];

				fields += mappedColumnName;

				customFieldNames.add(fieldName);
				if (!customFieldGroup.contains(groupName)) {
					customFieldGroup.add(groupName);
				}
				count++;
			}

			// Get all data of the customfields for particular organization
			if (fields.length() > 0) {
				List<String> customFieldValues = getCustomFieldsData(fields, resourceId, false);
				if (customFieldValues.size() > 0) {
					for (int groupIndex = 0; customFieldGroup.size() > groupIndex; groupIndex++) {

						for (int fieldDataIndex = 0; count > fieldDataIndex; fieldDataIndex++) {
							// if(customFieldNames.get(fieldDataIndex).startsWith(customFieldGroup.get(groupIndex))){
							if (customFieldValues.get(fieldDataIndex) != null) {
								innerCustomFieldValueMap.put(customFieldNames.get(fieldDataIndex), customFieldValues.get(fieldDataIndex));
							}
							// }
						}

					}
				}

			}

		}

		return innerCustomFieldValueMap;
	}

	private String getOrganizationPartyUid(String resourceGooruOid) {
		try {
			Resource resource = (Resource) getSession().createQuery("SELECT r FROM Resource r WHERE r.gooruOid = '" + resourceGooruOid + "'").list().get(0);
			return resource.getOrganization().getPartyUid();
		} catch (Exception ex) {
			throw new NotFoundException("RESOURCE NOT FOUND " + resourceGooruOid);
		}
	}

	@Override
	public Map<String, String> getCustomFieldsAndValuesOfResource(String resourceGooruOId) {
		return buildCustomFieldsDataMap(getOrganizationPartyUid(resourceGooruOId), resourceGooruOId);
	}

	private Map<String, String> buildFieldsNameAndValues(String accountUId, Map<String, String> customFieldValueMap, boolean isUpdate) {
		Map<String, String> fieldAndColumnRelationMap = new HashMap<String, String>();
		if (this.getCustomFieldColumnRelation() != null) {
			fieldAndColumnRelationMap = this.getCustomFieldColumnRelation().get(accountUId);
		}

		if (fieldAndColumnRelationMap.size() == 0) {
			fieldAndColumnRelationMap = buildCustomFieldsDefination(accountUId);
		}

		String values = "";
		String fieldNames = "";
		String updateValues = "";
		int count = 0;
		if (fieldAndColumnRelationMap.size() > 0) {
			for (String fieldKey : customFieldValueMap.keySet()) {
				if (count > 0) {
					if (!isUpdate) {
						values += ",";
						fieldNames += ",";
					} else {
						updateValues += ",";
					}
				}
				String fieldName = fieldAndColumnRelationMap.get(fieldKey);
				if (fieldName == null) {
					fieldAndColumnRelationMap = buildCustomFieldsDefination(accountUId);
				}
				fieldName = fieldAndColumnRelationMap.get(fieldKey);
				String value = "?";
				if (!isUpdate) {
					fieldNames += fieldName;
					values += value;
				} else {
					updateValues += fieldName + "=" + value;
				}
				count++;
			}
		}
		Map<String, String> fieldsNameAndValues = new HashMap<String, String>();
		if (!isUpdate) {
			fieldsNameAndValues.put("fieldsName", fieldNames);
			fieldsNameAndValues.put("values", values);
		} else {
			fieldsNameAndValues.put("updateValues", updateValues);
		}
		return fieldsNameAndValues;
	}

	private List<String> getCustomFieldsData(String fields, String resourceId, boolean eleminateNullValue) {
		String sql = " SELECT " + fields + " FROM custom_fields_data ";
		if (resourceId != null) {
			sql += " WHERE resource_gooru_oid=?";
		}
		SQLQuery query = getSession().createSQLQuery(sql);

		if (resourceId != null) {
			query.setParameter(0, resourceId, StandardBasicTypes.STRING);
		}

		List<Object[]> customFieldDataList = arrayList(query);
		List<String> customFieldValues = new ArrayList<String>();
		if (customFieldDataList.size() > 0) {
			for (Object[] dataRow : customFieldDataList) {
				for (int fieldIndex = 0; fieldIndex < dataRow.length; fieldIndex++) {
					if (eleminateNullValue) {
						if (dataRow[fieldIndex] != null) {
							customFieldValues.add((String) dataRow[fieldIndex]);
						}
					} else {
						customFieldValues.add((String) dataRow[fieldIndex]);
					}
				}
			}
		}
		return customFieldValues;

	}

	@Override
	public List<Object[]> getSearchAliasByOrganization(String organizationUid) {
		String sql = "SELECT search_alias_name, data_column_name,add_to_search,add_to_search_index,add_to_filters FROM custom_fields WHERE show_in_response=? AND organization_uid =? AND add_to_search=? ";
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter(0, 1, StandardBasicTypes.INTEGER);

		if (organizationUid != null) {
			query.setParameter(1, organizationUid, StandardBasicTypes.STRING);
		}
		query.setParameter(2, 1, StandardBasicTypes.INTEGER);

		query.addScalar("search_alias_name", StandardBasicTypes.STRING);
		query.addScalar("data_column_name", StandardBasicTypes.STRING);
		query.addScalar("add_to_search", StandardBasicTypes.INTEGER);
		query.addScalar("add_to_search_index", StandardBasicTypes.INTEGER);
		query.addScalar("add_to_filters", StandardBasicTypes.INTEGER);
		return arrayList(query);
	}

	private void executeSaveOrUpdate(final String sql, final Map<String, String> customFieldValueMap, final String resourceId, final boolean isUpdate) {
		PreparedStatementCreator creator = new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement updateCustomFields = con.prepareStatement(sql);
				int parameterIndex = 1;
				if (!isUpdate) {
					updateCustomFields.setString(parameterIndex, resourceId);
					parameterIndex++;
				}

				for (String paramKey : customFieldValueMap.keySet()) {
					if (paramKey != null) {
						updateCustomFields.setString(parameterIndex, customFieldValueMap.get(paramKey));
						parameterIndex++;
					}
				}

				if (isUpdate) {
					updateCustomFields.setString(parameterIndex, resourceId);
				}

				return updateCustomFields;
			}
		};
		getJdbcTemplate().update(creator);
	}

	@Override
	public CustomField findCustomFieldIfExists(String customFieldId) {
		String hql = "FROM CustomField customField WHERE customField.customFieldId = '" + customFieldId + "'";
		Query query = getSession().createQuery(hql);
		List<CustomField> result = list(query);
		return (result.size() > 0) ? result.get(0) : null;
	}

	@Override
	public void deleteCustomField(String customFieldDataColumn, String customFieldId) {
		String sql = "UPDATE custom_fields_data SET " + customFieldDataColumn + "= null";
		getJdbcTemplate().execute(sql);
		remove(CustomField.class, customFieldId);
	}

	@Cacheable("gooruCache")
	private Map<String, Object> getSearchFieldsByOrganization(String organizationUid) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Object[]> searchAliasList = getSearchAliasByOrganization(organizationUid);

		List<String> searchAlias = new ArrayList<String>();
		List<Integer> addToSearchIndex = new ArrayList<Integer>();
		List<Integer> addToFilters = new ArrayList<Integer>();

		String fields = "";
		Integer count = 0;
		for (Object[] row : searchAliasList) {
			searchAlias.add((String) row[0]);
			if (count > 0) {
				fields += ",";
			}
			fields += row[1];
			addToSearchIndex.add((Integer) row[3]);
			addToFilters.add((Integer) row[4]);
			count++;
		}
		resultMap.put("fields", fields);
		resultMap.put("addToSearchIndex", addToSearchIndex);
		resultMap.put("addToFilters", addToFilters);
		resultMap.put("searchAlias", searchAlias);

		return resultMap;
	}

	@Override
	public Map<String, Object> getResourceSearchAliasValuesMap(String organizationUid, String resourceGooruOId) {

		if (organizationUid == null) {
			organizationUid = getOrganizationPartyUid(resourceGooruOId);
		}

		Map<String, Object> searchFieldsMap = getSearchFieldsByOrganization(organizationUid);

		@SuppressWarnings("unchecked")
		List<String> searchAlias = (List<String>) searchFieldsMap.get("searchAlias");
		@SuppressWarnings("unchecked")
		List<Integer> addToSearchIndex = (List<Integer>) searchFieldsMap.get("addToSearchIndex");
		@SuppressWarnings("unchecked")
		List<Integer> addToFilters = (List<Integer>) searchFieldsMap.get("addToFilters");
		String fields = (String) searchFieldsMap.get("fields");

		List<Map<String, String>> customFieldsSearchDatas = new ArrayList<Map<String, String>>();
		Map<String, String> customFieldsSearchAlias = new HashMap<String, String>();
		Map<String, Object> customFieldsSearchAliasData = new HashMap<String, Object>();

		if (!fields.isEmpty()) {
			List<String> customFields = getCustomFieldsData(fields, resourceGooruOId, false);
			if (searchAlias.size() > 0 && customFields.size() > 0) {
				for (int fieldIndex = 0; customFields.size() > fieldIndex; fieldIndex++) {
					if (customFields.get(fieldIndex) != null) {

						Map<String, String> customFieldsSearchData = new HashMap<String, String>();
						customFieldsSearchData.put("searchAliasName", searchAlias.get(fieldIndex));
						customFieldsSearchData.put("fieldValue", customFields.get(fieldIndex));
						customFieldsSearchData.put("addToSearchIndex", addToSearchIndex.get(fieldIndex) + "");
						customFieldsSearchData.put("addToFilters", addToFilters.get(fieldIndex) + "");
						customFieldsSearchDatas.add(customFieldsSearchData);
						if (addToSearchIndex.get(fieldIndex) == 1) {
							customFieldsSearchAlias.put(searchAlias.get(fieldIndex), customFields.get(fieldIndex));
						}
					}
				}
				customFieldsSearchAliasData.put("searchAliasData", customFieldsSearchDatas);
				customFieldsSearchAliasData.put("searchAliasValueMap", customFieldsSearchAlias);
			}
		}

		return customFieldsSearchAliasData;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Map<String, Map<String, String>> getCustomFieldColumnRelation() {
		return customFieldColumnRelation;
	}

	public void setCustomFieldColumnRelation(Map<String, Map<String, String>> customFieldColumnRelation) {
		this.customFieldColumnRelation = customFieldColumnRelation;
	}

	@Override
	public void updateCustomFieldsDefinationMap(String partyUid) {
		updateCustomFieldsDefinationMap(partyUid);
	}

	@Override
	public void addOrUpdateCustomFieldValues(String resourceGooruOid, Map<String, String> customFieldValueMap, boolean isUpdate) {
		try {
			if (customFieldValueMap.size() > 0) {
				if (checkIfCustomFieldAlreadyInserted(resourceGooruOid) == 0) {
					isUpdate = false;
				}
				Map<String, String> fieldsAndValues = buildFieldsNameAndValues(getOrganizationPartyUid(resourceGooruOid), customFieldValueMap, isUpdate);
				String sql = null, fieldNames = null;
				if (!isUpdate) {
					fieldNames = fieldsAndValues.get("fieldsName");
					String values = fieldsAndValues.get("values");
					sql = "INSERT INTO custom_fields_data (resource_gooru_oid," + fieldNames + ") VALUES(?," + values + ")";

				} else {
					fieldNames = fieldsAndValues.get("updateValues");
					sql = "UPDATE custom_fields_data SET " + fieldNames + "  WHERE resource_gooru_oid=?";
				}
				executeSaveOrUpdate(sql, customFieldValueMap, resourceGooruOid, isUpdate);
			}
		} catch (Exception e) {
			getLogger().error("custom fields data insert/update failed", e);
		}

	}

	@Override
	public List<String> getResourceLicenseType(String licenseNames) {
		String thirdPartyResource = getCustomFieldsByName("defaultLicenseResource");
		String sql = " SELECT resource_gooru_oid FROM custom_fields_data ";
		if (licenseNames != null) {
			String[] licenseNameList = licenseNames.split(",");
			StringBuffer licenseResources = new StringBuffer();
			for (String licenseName : licenseNameList) {
				if (licenseResources.length() > 0) {
					licenseResources.append(",'" + licenseName + "'");
				} else {
					licenseResources.append("'" + licenseName + "'");
				}
			}
			sql += " WHERE " + thirdPartyResource + " IN ( " + licenseResources + ")";
		}
		SQLQuery query = getSession().createSQLQuery(sql);
		List<String> results = list(query);
		return results.size() > 0 ? results : null;
	}

	@Override
	public List<String> getPendingResource(Boolean isPendingCollection) {
		String pendingRescource = getCustomFieldsByName("pendingCollection");
		String sql = " SELECT resource_gooru_oid FROM custom_fields_data ";
		if ((isPendingCollection) && (pendingRescource != null)) {
			sql += " WHERE " + pendingRescource + "= '1'";
		}

		SQLQuery query = getSession().createSQLQuery(sql);
		List<String> results = list(query);
		return results.size() > 0 ? results : null;
	}

	private String getCustomFieldsByName(String name) {
		String sql = "SELECT data_column_name FROM  custom_fields WHERE";
		if (name != null) {
			sql += " name = '" + name + "'";
		}
		SQLQuery query = getSession().createSQLQuery(sql);
		return (String) query.list().get(0);
	}

}
