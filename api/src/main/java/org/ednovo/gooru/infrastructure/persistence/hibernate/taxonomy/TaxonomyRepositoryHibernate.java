/////////////////////////////////////////////////////////////
// TaxonomyRepositoryHibernate.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy;

import java.io.File;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axis.utils.ByteArrayOutputStream;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.ednovo.gooru.application.util.ConfigProperties;
import org.ednovo.gooru.application.util.DatabaseUtil;
import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.CodeOrganizationAssoc;
import org.ednovo.gooru.core.api.model.CodeType;
import org.ednovo.gooru.core.api.model.CodeUserAssoc;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.ednovo.gooru.json.serializer.util.JsonSerializer;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;

import flexjson.JSONSerializer;

@SuppressWarnings("deprecation")
@Repository
public class TaxonomyRepositoryHibernate extends BaseRepositoryHibernate implements TaxonomyRespository,ConstantProperties, ParameterProperties {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ConfigProperties configProperties;

	@Autowired
	private RedisService redisService;

	private static final String FIND_MAX_DEPTH = "Select max(depth) as depth  from taxonomy_level_type  where code_id = %s and  organization_uid in(%s)";
	private static final String FIND_TAXCODE_BY_DEPTH = "Select t.type_id from taxonomy_level_type t  where t.depth = %s and t.code_id =%s and t.organization_uid in(%s)";
	private static final String FIND_ALL_TAXONOMY = "SELECT t.depth, t.label, c.code, c.code_id FROM taxonomy_level_type t , code c where t.code_id=c.code_id and t.organization_uid in (%s) and c.active_flag=1";
	private static final String UPDATE_ORDER = "update code  set display_order = %s where code_id = %s and organization_uid in(%s)";
	private static final String FIND_CODE_BY_CODEIDS = "select * from code c where c.code_id = ? and c.active_flag = ?";
 
	@Override
	public void updateOrder(Code code) {
		String updateQuery = DatabaseUtil.format(UPDATE_ORDER, code.getDisplayOrder(), code.getCodeId(), getUserOrganizationUidsAsString());
		this.getJdbcTemplate().update(updateQuery);
	}

	@SuppressWarnings("rawtypes")
	public String makeTree(Code rootCode) {
		int depth = findMaxDepthInTaxonomy(rootCode, rootCode.getOrganization().getPartyUid());
		char[] alphas = new char[depth + 1];
		for (int c = 0; c <= depth; c++)
			alphas[c] = (char) (97 + c);

		Document doc = DocumentHelper.createDocument();

		Element root = doc.addElement("node"); // need to update

		root.addAttribute("code", rootCode.getCode());
		root.addAttribute("type", rootCode.getCodeType().getLabel());
		root.addAttribute("depth", "0");
		root.addAttribute("codeId", rootCode.getCodeId().toString());
		root.addAttribute("codeUId", rootCode.getCodeUid().toString());
		root.addAttribute("taxonomyImageUrl", rootCode.getTaxonomyImageUrl());
		root.addAttribute("displayCode", rootCode.getdisplayCode());
		root.addAttribute("activeFlag", rootCode.getActiveFlag() + "");

		String q1 = "select label from taxonomy_level_type where code_id=" + rootCode.getRootNodeId() + " order by depth";
		@SuppressWarnings("unchecked")
		List<String> labels = this.getJdbcTemplate().query(q1, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("label");
			}
		});

		for (int i = 1; i <= depth; i++) {

			StringBuilder query = new StringBuilder("SELECT ");

			for (int j = 0; j <= i; j++) {
				query.append(alphas[j] + ".display_code, " + alphas[j] + ".code_image, " + alphas[j] + ".code_uid, " + alphas[j] + ".code_id, " + alphas[j] + ".display_order, " + alphas[j] + ".depth, " + alphas[j] + ".label as '" + labels.get(j) + "', " + alphas[j] + ".code as '" + labels.get(j)
						+ "' ");
				if (j != i) {
					query.append(",");
				}
			}

			query.append(" from ");

			for (int j = 0; j <= i; j++) {
				query.append(" code " + alphas[j]);
				if (j != i) {
					query.append(",");
				}
			}

			query.append(" where ");

			for (int j = 0; j <= i; j++) {

				query.append(alphas[j] + ".depth=" + j);
				// if(j!=i)
				query.append(" and  ");
			}

			for (int j = 0; j < i; j++) {
				query.append(alphas[j + 1] + ".parent_id=" + alphas[j] + ".code_id");
				// if(j!=i-1)
				query.append(" and " + alphas[j + 1] + ".active_flag=1 and ");
			}

			query.append(alphas[0] + ".code_id=" + rootCode.getCodeId());
			query.append(" and " + alphas[0] + ".active_flag=1");

			@SuppressWarnings("unchecked")
			List<String[]> labelss = this.getJdbcTemplate().query(query.toString(), new RowMapper() {
				public Object mapRow(final ResultSet rs, final int rowNum) throws SQLException {

					final ResultSetMetaData metaData = rs.getMetaData();
					final int noofcolumns = metaData.getColumnCount();
					final String label[] = new String[noofcolumns];

					for (int m = 1; m <= noofcolumns; m++) {
						label[m - 1] = rs.getString(m);
					}

					return label;
				}
			});

			@SuppressWarnings("unchecked")
			List<Node> nodes = doc.selectNodes("//node");

			for (String[] array : labelss) {
				int length = array.length;

				for (Node node : nodes) {
					Element parent = (Element) node;
					String att = parent.attribute("code").getText();
					if (att.equals(array[length - 9])) {
						// Element child = parent.addElement(labels.get(i));
						Element child = parent.addElement("node");
						child.addAttribute("type", labels.get(i));
						child.addAttribute("code", array[length - 1]);
						child.addAttribute("label", array[length - 2]);
						child.addAttribute("depth", array[length - 3]);
						child.addAttribute("order", array[length - 4]);
						child.addAttribute("codeId", array[length - 5]);
						child.addAttribute("codeUId", array[length - 6]);
						String taxonomyImageUrl = array[length - 7] != null ? rootCode.getAssetURI() + array[length - 7] : "";
						child.addAttribute("taxonomyImageUrl", taxonomyImageUrl);
						String displayCode = array[length - 8];
						if (displayCode != null)
							child.addAttribute("displayCode", array[length - 8]);
						else
							child.addAttribute("displayCode", "");
						child.addAttribute("activeFlag", rootCode.getActiveFlag() + "");

					}
				}
			}

		}
		return doc.asXML();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Code> findRootTaxonomies(Short depth, String creatorUid) {
		String hql = "from Code c where c.depth = '" + depth + "' and c.activeFlag = 1 and " + generateOrgAuthQueryWithData("c.");
		if (creatorUid != null) {
			hql += " and code.creator.partyUid ='" + creatorUid + "'";
		}
		return getSession().createQuery(hql).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Code findCodeByTaxCode(String taxonomyCode) {

		Session session = getSession();
		List<Code> code = session.createQuery("from Code c where c.code = '" + taxonomyCode + "' and c.activeFlag = 1 and " + generateOrgAuthQueryWithData("c.")).list();
		return code.isEmpty() ? null : code.get(0);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int findMaxDepthInTaxonomy(Code code, String organizationUid) {

		String organizationUids = null;

		if (organizationUid != null) {
			organizationUids = "'" + organizationUid + "'";
		} else {
			organizationUids = getUserOrganizationUidsAsString();
		}

		String depthQuery = DatabaseUtil.format(FIND_MAX_DEPTH, code.getRootNodeId(), organizationUids);

		Integer maxDepth;
		maxDepth = (Integer) this.getJdbcTemplate().queryForObject(depthQuery, new RowMapper() {
			public Object mapRow(ResultSet rs, int robwNum) throws SQLException {

				int depth = (Integer) rs.getInt("depth");

				return depth;
			}
		});

		return maxDepth.intValue();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public CodeType findTaxonomyTypeBydepth(Code code, Short depth) {
		String depthQuery = DatabaseUtil.format(FIND_TAXCODE_BY_DEPTH, depth, code.getRootNodeId(), getUserOrganizationUidsAsString());

		CodeType maxDepth;
		maxDepth = (CodeType) this.getJdbcTemplate().queryForObject(depthQuery, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CodeType codetype = new CodeType();
				int typeId = (Integer) rs.getInt("type_id");
				codetype.setTypeId(typeId);

				return codetype;
			}
		});

		return maxDepth;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CodeType> findTaxonomyLevels(Code root) {
		List<CodeType> codeTypes = getSession().createQuery("from CodeType c where c.codeId = :codeId and " + generateOrgAuthQueryWithData("c.")).setParameter("codeId", root.getCodeId()).list();

		return codeTypes.isEmpty() ? null : codeTypes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Code> findChildTaxonomyCode(Integer codeId) {

		ProjectionList proList = Projections.projectionList();
		proList.add(Projections.property("codeId"));
		proList.add(Projections.property("label"));

		List<Code> codeList = getSession().createCriteria(Code.class).add(Restrictions.in("organization.partyUid", getUserOrganizationUids()))
				.add(Expression.eq("parentId", codeId))
				.list();

		return codeList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Code> findAll() {
		return getSession().createQuery("from Code c where c.activeFlag =1 and " + generateOrgAuthQueryWithData("c.")).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Code> findChildTaxonomyCodeByOrder(Integer codeId, String order) {
		Integer activeFlag = 1;
		return getSession().createQuery("from Code c where c.parentId = ? and c.displayOrder >= ? and c.activeFlag = ? and " + generateOrgAuthQueryWithData("c.")).setParameter(0, codeId).setParameter(1, Integer.valueOf(order)).setParameter(2, activeFlag).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Code> findChildTaxonomyCodeByDepth(Integer codeId, Integer depth) {
		return getSession().createQuery("from Code c where c.parentId =" + codeId + " and c.depth =" + depth + " and c.activeFlag =1 and " +  generateOrgAuthQueryWithData("c.") + " order by c.sequence").list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Code> findChildTaxonomy(String parentIds, Integer depth) {
		if (parentIds.contains(","))  {
			parentIds = parentIds.replace(",", "','");
		}
		return getSession().createQuery("from Code c where c.parentId in ('" + parentIds + "') and c.depth =" + depth + " and c.activeFlag =1 and " +  generateOrgAuthQueryWithData("c.") + " order by c.sequence").list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Code> getCurriculumCodeByDepth(Integer depth) {
		return getSession().createQuery("from Code c where c.depth =" + depth + " and " + generateOrgAuthQueryWithData("c.")).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Code findFirstChildTaxonomyCodeByDepth(Integer codeId, Integer depth) {
		List<Code> code = getSession().createQuery("from Code c where c.parentId =" + codeId + " and c.depth =" + depth + " and c.displayOrder = 1 and c.activeFlag = 1 " + "and " + generateOrgAuthQueryWithData("c.")).list();
		return (code != null && code.size() > 0) ? code.get(0) : null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<CodeType> findAllTaxonomyLevels() {
		List<CodeType> annotations = this.getJdbcTemplate().query(DatabaseUtil.format(FIND_ALL_TAXONOMY, getUserOrganizationUidsAsString()), new RowMapper() {

			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CodeType annotation = new CodeType();
				annotation.setDepth(rs.getShort("depth"));
				annotation.setLabel(rs.getString("label"));
				Code code = new Code();
				code.setCode(rs.getString("code"));
				code.setCodeId(rs.getInt("code_id"));
				annotation.setCode(code);
				return annotation;
			}
		});

		return annotations;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Code> findCodeByType(Integer taxonomyLevel) {

		ProjectionList proList = Projections.projectionList();
		proList.add(Projections.property("codeId"));
		proList.add(Projections.property("label"));

		List<Code> codeList = getSession().createCriteria(Code.class).add(Restrictions.in("organization.partyUid", getUserOrganizationUids())).setProjection(proList).add(Expression.eq("codeType.typeId", taxonomyLevel)).list();

		return codeList;
	}

	@Override
	public List<Code> getCodesOfConent(Long contentId) {
		Integer activeFlag = 1;
		List<Integer> codeIds = jdbcTemplate.queryForList("select cc.code_id from content_classification cc inner join code c on cc.code_id=c.code_id  where cc.content_id=? and c.active_flag=" + activeFlag + " and c.organization_uid in (" + getUserOrganizationUidsAsString() + ")",
				new Object[] { contentId }, Integer.class);
		List<Code> list = new ArrayList<Code>();
		for (Integer codeId : codeIds)
			findParentTaxonomyCodes(codeId, list);
		return list;
	}

	@Override
	@Cacheable("gooruCache")
	public List<Code> findParentTaxonomy(Integer codeId, boolean reverse) {
		List<Code> codeList = new ArrayList<Code>();
		findParentTaxonomyCodes(codeId, codeList);
		Collections.reverse(codeList);
		return codeList;
	}

	@Override
	public List<Code> findParentTaxonomyCodes(Integer codeId, List<Code> codeList) {
		Code code = (Code) get(Code.class, codeId);
		if (code != null) {
			codeList.add(code);
		}

		if (code != null && code.getDepth() != 1) {
			codeList = findParentTaxonomyCodes(code.getParentId(), codeList);
		}

		return codeList;
	}

	@Override
	public List<Code> findSiblingTaxonomy(Code code) {
		Criteria criteria = getSession().createCriteria(Code.class).add(Expression.eq("parentId", code.getParentId())).add(Expression.eq("codeType.typeId", code.getCodeType().getTypeId()));
		Criteria criteria2 = addOrgAuthCriterias(criteria);
		@SuppressWarnings("unchecked")
		List<Code> codeList = criteria2.list();

		return codeList;
	}

	@Override
	public void updateTaxonomyAssociation(Code taxonomy, List<Code> codes) {
		for (Code code : codes) {
			this.getJdbcTemplate().update("insert into taxonomy_association values(" + taxonomy.getCodeId() + "," + code.getCodeId() + ")");
		}
	}

	@Override
	public void deleteTaxonomyMapping(Code taxonomy, List<Code> codes) {
		for (Code code : codes) {
			this.getJdbcTemplate().update("delete ta.* from taxonomy_association ta inner join code c on c.code_id=ta.target_code_id where source_code_id =" + taxonomy.getCodeId() + " and target_code_id =" + code.getCodeId() + " and " + generateOrgAuthSqlQueryWithData("c."));
		}
	}

	@Cacheable("gooruCache")
	@Override
	public List<Code> findTaxonomyMappings(String codeIds) {
		List<Code> codeList = new ArrayList<Code>();
		if (codeIds != null) {
			String[] codeId = codeIds.split(",");
			for (String id : codeId) {
				if (id != null && id.length() > 0) {
					Code code = new Code();
					code.setCodeId(Integer.parseInt(id));
					codeList.add(code);
				}
			}
		}
		return findTaxonomyMappings(codeList, true);
	}

	@Override
	public List<Code> findTaxonomyMappings(List<Code> codeIdList, boolean excludeTaxonomyPreference) {

		// Please don not change - there is a reason to specify this query
		// string here rather than a static string....
		String findCurriculum = "SELECT distinct c.label,c.display_order, c.code,c.description, c.type_id, c.code_id, c.depth, c.root_node_id,c.display_code FROM code c inner join taxonomy_association t on c.code_id = t.target_code_id where  t.source_code_id in (";

		if (codeIdList.size() == 0) {
			return null;
		}
		Object[] a = new Object[codeIdList.size()];

		for (int i = 0; i < codeIdList.size(); i++) {

			if (i < (codeIdList.size() - 1)) {
				findCurriculum = findCurriculum + "?,";
			} else {
				findCurriculum = findCurriculum + "?)";
			}

			a[i] = codeIdList.get(i).getCodeId();
		}

		findCurriculum += " and active_flag =1 and " + generateOrgAuthSqlQueryWithData("c.");

		if (UserGroupSupport.getTaxonomyPreference() != null && !excludeTaxonomyPreference) {
			findCurriculum += " and c.root_node_id in (" + UserGroupSupport.getTaxonomyPreference() + ")";
		}

		List<Code> codeList = new ArrayList<Code>();
		List<Map<String, Object>> rows = getJdbcTemplate().queryForList(findCurriculum, a);

		for (Map<?, ?> row : rows) {
			Code code = new Code();
			code.setCode((String) row.get("code"));
			code.setDescription((String) row.get("description"));
			code.setCodeId((Integer) row.get("code_id"));
			code.setDepth(Short.valueOf(row.get("depth").toString()));
			code.setLabel((String) row.get("label"));
			code.setDisplayOrder(new Integer(row.get("display_order").toString()));
			code.setRootNodeId((Integer) row.get("root_node_id"));
			code.setdisplayCode((String) row.get("display_code"));

			CodeType codeType = new CodeType();
			codeType.setTypeId((Integer) row.get("type_id"));
			code.setCodeType(codeType);
			List<Code> parentCodeList = new ArrayList<Code>();
			List<Code> parentList = findParentTaxonomyCodesByCodeId(code.getCodeId(), parentCodeList);

			code.setParentsList(parentList);
			codeList.add(code);
		}

		return codeList.size() == 0 ? null : codeList;
	}

	public List<Code> findParentTaxonomyCodesByCodeId(int codeId, List<Code> parentCodeList) {

		Code code = (Code) get(Code.class, codeId);
		if (code != null) {
			parentCodeList.add(code);
		}

		if (code != null && code.getDepth() != 1) {
			parentCodeList = findParentTaxonomyCodes(code.getParentId(), parentCodeList);
		}

		return parentCodeList;
	}

	@Override
	@Cacheable("gooruCache")
	public String findRootLevelTaxonomy(Code code) {

		int index = code.getDepth().intValue();

		List<String> alias = new ArrayList<String>();
		for (int cIndex = 0; cIndex <= index; cIndex++) {
			alias.add("c" + cIndex);
		}

		String query = "Select " + alias.get(index) + ".label from ";

		for (int i = 0; i <= index; i++) {
			if (i == index) {
				query += "code " + alias.get(i) + " where ";
			} else {
				query += "code " + alias.get(i) + ", ";
			}
		}

		for (int i = 0; i < index; i++) {
			if (i < index) {
				query += alias.get(i) + ".parent_id = " + alias.get(i + 1) + ".code_id and ";
			}
		}

		query += alias.get(0) + ".code = '" + code.getCode() + "'";
		query += " and " + alias.get(0) + ".active_flag =1 and " + generateOrgAuthSqlQueryWithData(alias.get(index) + ".");

		String label = (String) getJdbcTemplate().queryForObject(query, String.class);

		return label;
	}

	@Override
	public void writeToDisk(Code root) throws Exception {
		String rootXml = makeTree(root);

		@SuppressWarnings("resource")
		final String encoding = new OutputStreamWriter(new ByteArrayOutputStream()).getEncoding();

		FileUtils.writeStringToFile(new File(configProperties.getTaxonomyRepositoryPath().get("taxonomy.repository") + "/" + root.getCodeId() + ".xml"), rootXml, encoding);

		TaxonomyUtil.updateClassplanLibrary(configProperties.getTaxonomyRepositoryPath().get("taxonomy.repository"), root.getCodeId());
		TaxonomyUtil.updateResourceLibrary(configProperties.getTaxonomyRepositoryPath().get("taxonomy.repository"), root.getCodeId());
		TaxonomyUtil.updateTaxonomyTree(configProperties.getTaxonomyRepositoryPath().get("taxonomy.repository"), root.getCodeId());
	}

	@Override
	public String findTaxonomyTree(String taxonomyCode, String format) throws Exception {

		Code code = (Code) get(Code.class, new Integer(taxonomyCode));// (taxonomyCode);

		CodeType codetype = (CodeType) super.get(CodeType.class, code.getCodeType().getTypeId());

		Document taxonomyXML = null;
		SAXReader reader = new SAXReader();

		taxonomyXML = reader.read(configProperties.getTaxonomyRepositoryPath().get("taxonomy.repository") + "/" + codetype.getCodeId() + ".xml");

		Node nodeTree = null;

		if (taxonomyXML.getRootElement().attribute("code").getText().equals(code.getCode())) {
			nodeTree = taxonomyXML.getRootElement();
		} else {
			@SuppressWarnings("unchecked")
			List<Node> nodes = taxonomyXML.selectNodes("//node");

			for (Node node : nodes) {
				Element parent = (Element) node;
				String att = parent.attribute("code").getText();

				if (att.equals(code.getCode())) {
					nodeTree = node;
					break;
				}
			}
		}

		if (format.equalsIgnoreCase("json")) {
			TaxonomyNode taxonomyNode = convertXmlToJson(nodeTree);
			JSONObject jsonObject = new JSONObject();
			String nodeJson = new JSONSerializer().exclude("*.class").deepSerialize(taxonomyNode).replace("\"node\":null,", "").replace("\"order\":null,", "").replace("\"taxonomyImageUrl\":null,", "").replace("\"label\":null,", "");
			jsonObject.put("node", new JSONObject(nodeJson));
			return jsonObject.toString();
		}

		return nodeTree.asXML();
	}

	public TaxonomyNode convertXmlToJson(Node node) {
		Element element = (Element) node;
		TaxonomyNode taxonomyNode = new TaxonomyNode();
		@SuppressWarnings("unchecked")
		List<Node> nodes = element.elements("node");
		taxonomyNode.setCode(element.attributeValue("code"));
		taxonomyNode.setCodeId(element.attributeValue("codeId") != null ? Integer.parseInt(element.attributeValue("codeId")) : null);
		taxonomyNode.setCodeUId(element.attributeValue("codeUId"));
		taxonomyNode.setTaxonomyImageUrl(element.attributeValue("taxonomyImageUrl"));
		taxonomyNode.setOrder(element.attributeValue("order") != null ? Integer.parseInt(element.attributeValue("order")) : null);
		taxonomyNode.setLabel(element.attributeValue("label"));
		taxonomyNode.setDepth(element.attributeValue("depth") != null ? Integer.parseInt(element.attributeValue("depth")) : null);
		taxonomyNode.setType(element.attributeValue("type"));
		taxonomyNode.setdisplayCode(element.attributeValue("displayCode"));
		if (nodes != null && nodes.size() > 0) {
			taxonomyNode.setNode(new ArrayList<TaxonomyNode>());
			for (Node nodeEmement : nodes) {
				taxonomyNode.getNode().add(convertXmlToJson((Element) nodeEmement));
			}
		}
		return taxonomyNode;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@SuppressWarnings("rawtypes")
	public void updateOrders() {
		String query = "SELECT code_id, code FROM code where code_id between 10000 and 11000 and active_flag =1 and " + generateOrgAuthSqlQueryWithData() + " order by code_id";
		@SuppressWarnings("unchecked")
		List<String[]> codes = this.getJdbcTemplate().query(query.toString(), new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

				ResultSetMetaData metaData = rs.getMetaData();
				int noofcolumns = metaData.getColumnCount();
				String label[] = new String[noofcolumns];

				label[0] = rs.getString(1);

				String[] code = rs.getString(2).split("\\.");
				for (int k = code.length; k >= 0; k--) {
					if (!code[k - 1].equals("00")) {
						label[1] = code[k - 1];
						if (code[k - 1].equals("A")) {
							label[1] = "1";
						} else if (code[k - 1].equals("B")) {
							label[1] = "2";
						} else if (code[k - 1].equals("C")) {
							label[1] = "3";
						} else if (code[k - 1].equals("D")) {
							label[1] = "4";
						} else if (code[k - 1].equals("E")) {
							label[1] = "5";
						} else if (code[k - 1].equals("F")) {
							label[1] = "6";
						} else if (code[k - 1].equals("G")) {
							label[1] = "7";
						} else if (code[k - 1].equals("H")) {
							label[1] = "8";
						} else if (code[k - 1].equals("I")) {
							label[1] = "9";
						} else if (code[k - 1].equals("J")) {
							label[1] = "10";
						}
						break;
					}
				}
				return label;
			}
		});

		for (String[] code : codes) {
			try {
				this.getJdbcTemplate().update("update code set display_order= " + code[1] + " where code_id = " + code[0] + "and " + generateOrgAuthSqlQueryWithData());
			} catch (Exception e) {
			}

		}
	}

	@Override
	public Code findByLabel(String label) {
		Integer activeFlag = 1;
		Session session = getSession();
		@SuppressWarnings("unchecked")
		List<Code> cc = session.createQuery("from Code c where c.label = ? and c.activeFlag = ?  " + generateOrgAuthQueryWithData("c.")).setString(0, label).setInteger(1, activeFlag).list();
		return cc.size() == 0 ? null : cc.get(0);
	}

	@Override
	@Cacheable("gooruCache")
	public Code findByParent(String label, Integer parentId) {
		Session session = getSession();
		Criteria criteria = session.createCriteria(Code.class);
		criteria.add(Expression.eq("label", label)).add(Expression.eq("parentId", parentId));
		Criteria criteria2 = addOrgAuthCriterias(criteria);
		@SuppressWarnings("unchecked")
		List<Code> cc = criteria2.list();
		return cc.size() == 0 ? null : cc.get(0);
	}

	@Override
	public List<Code> findAllByRoot(Integer codeId) {
		Integer activeFlag = 1;
		Session session = getSession();
		@SuppressWarnings("unchecked")
		List<Code> codeList = session.createQuery("from Code c where c.rootNodeId = ? and c.activeFlag = ? and " + generateOrgAuthQueryWithData("c.")).setInteger(0, codeId).setInteger(1, activeFlag).list();
		return codeList;
	}

	@Override
	@Cacheable("gooruCache")
	public Code findCodeByCodeId(Integer codeId) {
		Integer activeFlag = 1;
		Session session = getSession();
		@SuppressWarnings("unchecked")
		List<Code> cc = session.createQuery("from Code c where c.codeId = ? and c.activeFlag = ?  and " + generateOrgAuthQueryWithData("c.")).setInteger(0, codeId).setInteger(1, activeFlag).list();
		return cc.size() == 0 ? null : cc.get(0);
	}

	@Override
	public Code findCodeByCodeUId(String codeUId) {
		Integer activeFlag = 1;
		Session session = getSession();
		@SuppressWarnings("unchecked")
		List<Code> cc = session.createQuery("from Code c where c.codeUid = ? and c.activeFlag = ?   and " + generateOrgAuthQueryWithData("c.")).setString(0, codeUId).setInteger(1, activeFlag).list();
		return cc.size() == 0 ? null : cc.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Code> listTaxonomy(Map<String, String> filters) {
		int pageSize = Integer.parseInt(filters.get("pageSize"));
		int pageNum = Integer.parseInt(filters.get("pageNum"));
		String query = "FROM Code code WHERE 1=1 ";
		if (filters.containsKey("codeIds")) {
			query += " AND code.codeId IN (" + filters.get("codeIds") + ") ";
		}
		if (filters.containsKey("type")) {
			query += " AND code.codeType.label in (" + filters.get("type") + ") ";
		}
		if (filters.containsKey("rootNodeId")) {
			query += " AND code.rootNodeId = " + filters.get("rootNodeId");
		}
		query += "AND code.activeFlag =1 AND " + generateOrgAuthQueryWithData("code.");
		return getSession().createQuery(query).setMaxResults(pageSize).setFirstResult((pageNum - 1) * pageSize).list();
	}

	@Override
	public List<Map<String, String>> findAllMappedStandards(String code, Map<String, String> filters) {

		String standardsCacheKey = "search-standards:" + code;
		List<Map<String, String>> standards = getRedisService().getValue(standardsCacheKey) != null ? JsonDeserializer.deserialize(getRedisService().getValue(standardsCacheKey), new TypeReference<List<Map<String, String>>>() {
		}) : null;

		if (standards != null && (filters != null && !Boolean.parseBoolean(filters.get("skipCache")))) {
			return standards;
		}
		String sql = "SELECT distinct c.code,c.description FROM code c inner join taxonomy_association t on c.code_id = t.target_code_id  where c.code like '%" + code + "%' and c.active_flag=1 and " + generateOrgAuthSqlQueryWithData("c.");
		int pageNum = 1;
		int pageSize = 10;
		if (filters != null && filters.containsKey("pageNum")) {
			pageNum = Integer.parseInt(filters.get("pageNum"));
		}
		if (filters != null && filters.containsKey("pageSize")) {
			pageSize = Integer.parseInt(filters.get("pageSize"));
		}
		sql += " limit " + (pageNum - 1) + "," + (pageNum) * pageSize;

		List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql);
		standards = new ArrayList<Map<String, String>>();
		for (Map<?, ?> row : rows) {
			Map<String, String> standard = new HashMap<String, String>();
			standard.put("code", (String) row.get("code"));
			standard.put("description", (String) row.get("description"));
			standards.add(standard);
		}
		getRedisService().putValue(standardsCacheKey, JsonSerializer.serializeToJson(standards, true), 432000);
		return standards;
	}

	@Override
	public List<Integer> findSourceCodeByTargetCode(Integer targetCodeId) {
		Session session = getSession();
		String sql = "select source_code_id from taxonomy_association where target_code_id=:targetCodeId";
		Query query = session.createSQLQuery(sql).setParameter("targetCodeId", targetCodeId);
		@SuppressWarnings("unchecked")
		List<Integer> sourceCodeIds = query.list();
		return sourceCodeIds;
	}

	@Override
	@Cacheable("gooruCache")
	public Code findCode(Integer codeId, String organizationUid) {
		Integer activeFlag = 1;
		Session session = getSession();
		String hql = " From Code code WHERE code.codeId=:codeId AND code.organization.partyUid = :partyUid  AND code.activeFlag = :activeFlag";
		Query query = session.createQuery(hql);
		query.setParameter("codeId", codeId);
		query.setParameter("partyUid", organizationUid);
		query.setParameter("activeFlag", activeFlag);
		return (Code) query.uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ednovo.gooru.domain.model.taxonomy.TaxonomyRespository#
	 * getCodeIdByContentId(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getCodeIdByContentIds(String contentIds) {
		Session session = getSession();
		String sql = "select cc.code_id from content_classification cc WHERE cc.content.content_id IN (" + contentIds + ")";
		SQLQuery query = session.createSQLQuery(sql);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Code> getCodeByContentIds(String contentIds) {
		Session session = getSession();
		String hql = "select c.code from ContentClassification c WHERE c.content.contentId IN (" + contentIds + ")";
		Query query = session.createQuery(hql);
		return (List<Code>) query.list();
	}

	@Override
	public String getFindTaxonomyList(String excludeCode) {
		Session session = getSession();
		String hql = "select group_concat(code_id) as codes from code where depth = 0 and organization_uid  IN (" + getUserOrganizationUidsAsString() + ") and code_id not in (" + excludeCode + ")";
		Query query = session.createSQLQuery(hql).addScalar("codes", StandardBasicTypes.STRING);
		return (String) query.list().get(0);
	}

	@Cacheable("gooruCache")
	@Override
	public String getFindTaxonomyCodeList(String codeIds) {
		String hql = "select group_concat(code) as codes from code where depth = 0 and organization_uid  IN (" + getUserOrganizationUidsAsString() + ") and code_id  in (" + codeIds + ")";
		Query query = getSession().createSQLQuery(hql).addScalar("codes", StandardBasicTypes.STRING);
		return (String) query.list().get(0);
	}

	public void setRedisService(RedisService redisService) {
		this.redisService = redisService;
	}

	public RedisService getRedisService() {
		return redisService;
	}

	@Override
	public Code findTaxonomyCodeById(Integer codeId) {
		String hql = "From Code code where code.codeId =:codeId and code.activeFlag=1 and " + generateOrgAuthQueryWithData("code.");
		Query query = getSession().createQuery(hql);
		query.setParameter("codeId", codeId);
		return (Code) query.list().get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CodeOrganizationAssoc> findCodeByParentCodeId(String code, String creatorUid, Integer limit, Integer offset, String fetchType, String organizationCode, String rootNodeId, String depth) {
		String hql = " From CodeOrganizationAssoc  codeOrganizationAssoc  where  codeOrganizationAssoc.code.activeFlag=1  ";

		if (rootNodeId != null) {
			hql += " and  codeOrganizationAssoc.code.rootNodeId=:rootNodeId";
		}
		if (code != null) {
			if (code.equalsIgnoreCase("featured")) {
				hql += " and codeOrganizationAssoc.isFeatured >= 1 ";
			} else {
				hql += " and codeOrganizationAssoc.code.parentId =:parentCodeId  ";
			}
		}

		if (depth != null) {
			hql += " and  codeOrganizationAssoc.code.depth=:depth";
		}

		if (fetchType != null && fetchType.equalsIgnoreCase("library")) {
			hql += " and codeOrganizationAssoc.code.libraryFlag = 1 ";
		}

		hql += " and codeOrganizationAssoc.organizationCode =:organizationCode ";

		if (creatorUid != null) {
			hql += " and codeOrganizationAssoc.code.creator.partyUid =:creatorUid";
		}

		if (code != null && code.equalsIgnoreCase("featured")) {
			hql += " order by codeOrganizationAssoc.isFeatured";
		} else {
			hql += " order by codeOrganizationAssoc.sequence";
		}

		Query query = getSession().createQuery(hql);
		query.setParameter("organizationCode", organizationCode);
		if (code != null && !code.equalsIgnoreCase("featured")) {
			query.setParameter("parentCodeId", Integer.parseInt(code));
		}
		if (rootNodeId != null) {
			query.setParameter("rootNodeId", Integer.parseInt(rootNodeId));
		}

		if (depth != null) {
			query.setParameter("depth", Short.parseShort(depth));
		}
		if (creatorUid != null) {
			query.setParameter("creatorUid", creatorUid);
		}
		query.setFirstResult(offset == null ? OFFSET :offset);
		query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : LIMIT);
	
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getFeaturedUser(String organizationCode) {
		String hql = "Select distinct(codeUserAssoc.user) From CodeUserAssoc codeUserAssoc where  codeUserAssoc.organizationCode=:organizationCode";
		Query query = getSession().createQuery(hql);
		query.setParameter("organizationCode", organizationCode);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CodeUserAssoc> getUserCodeAssoc(Integer codeId, String organizationCode) {
		String hql = "From CodeUserAssoc codeUserAssoc where codeUserAssoc.code.codeId=:codeId and codeUserAssoc.organizationCode=:organizationCode";
		Query query = getSession().createQuery(hql);
		query.setParameter("codeId", codeId);
		query.setParameter("organizationCode", organizationCode);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Code> getCodeByDepth(String organizationCode, Short depth, String creatorUid) {
		String hql = "Select codeUserAssoc.code From CodeUserAssoc codeUserAssoc where codeUserAssoc.code.depth=:depth and codeUserAssoc.organizationCode=:organizationCode and codeUserAssoc.user.partyUid =:creatorUid";
		Query query = getSession().createQuery(hql);
		query.setParameter("depth", depth);
		query.setParameter("organizationCode", organizationCode);
		query.setParameter("creatorUid", creatorUid);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getCollectionStandards(Integer codeId, String text, Integer limit, Integer offset) {
		String sql = "select distinct ifnull(c.common_core_dot_notation, c.display_code) as code_notation, c.code_id, c.label,c.code_uid, c.root_node_id from taxonomy_association ta inner join code c on ta.target_code_id = c.code_id  where ifnull(c.common_core_dot_notation, c.display_code) like '" + text + "%' and depth != 0";
		if (codeId != null) {
			sql += " and ta.source_code_id =" + codeId;
		}
		if (!UserGroupSupport.getTaxonomyPreference().isEmpty() && UserGroupSupport.getTaxonomyPreference() != null) {
			sql += " and c.root_node_id in (" + UserGroupSupport.getTaxonomyPreference() + ")";
		}
		Query query = getSession().createSQLQuery(sql);
		query.setFirstResult(offset == null ? OFFSET :offset);
		query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : LIMIT);
		return query.list();
	}

	@Override
	public List<Code> findParentTaxonomyCodeLevels(Integer codeId, List<Code> codeList) {
		Code code = getTaxonomyCodeById(codeId);
		if (code != null) {
			Code parentCode = new Code();
			parentCode.setActiveFlag(code.getActiveFlag());
			parentCode.setAssetURI(code.getAssetURI());
			parentCode.setCode(code.getCode());
			parentCode.setCodeId(code.getCodeId());
			parentCode.setCodeImage(code.getCodeImage());
			parentCode.setCodeUid(code.getCodeUid());
			parentCode.setDepth(code.getDepth());
			parentCode.setDescription(code.getDescription());
			parentCode.setdisplayCode(code.getdisplayCode());
			parentCode.setDisplayOrder(code.getDisplayOrder());
			parentCode.setGrade(code.getGrade());
			parentCode.setLabel(code.getLabel());
			parentCode.setLibraryFlag(code.getLibraryFlag());
			parentCode.setParentId(code.getParentId());
			parentCode.setRootNodeId(code.getRootNodeId());
			parentCode.setS3UploadFlag(code.getS3UploadFlag());
			parentCode.setTaxonomyImageUrl(code.getTaxonomyImageUrl());
			codeList.add(parentCode);
		}

		if (code != null && code.getDepth() != 1) {
			codeList = findParentTaxonomyCodeLevels(code.getParentId(), codeList);
		}

		return codeList;
	}

	private Code getTaxonomyCodeById(Integer codeId) {
		String hql = "From Code code where code.codeId =:codeId  and " + generateOrgAuthQueryWithData("code.");
		Query query = getSession().createQuery(hql);
		query.setParameter("codeId", codeId);
		return query.list().size() > 0 ? (Code) query.list().get(0) : null;
	}

	@Cacheable("gooruCache")
	@Override
	public String findTaxonomyCodeLabels(String codeIds) {
		String sql = "select group_concat(label) as labels from code where depth = 0 and organization_uid  IN (" + getUserOrganizationUidsAsString() + ") and code_id  in (" + codeIds + ")";
		Query query = getSession().createSQLQuery(sql).addScalar("labels", StandardBasicTypes.STRING);
		return query.list() != null ? (String) query.list().get(0) : null;
	}
	
	@SuppressWarnings("unchecked")
	@Cacheable("gooruCache")
	@Override
	public List<Code> findCodeCommonCoreNotation() {
		String hql = "From Code code where code.organization.partyUid is not null and code.activeFlag is not null and code.commonCoreDotNotation is not null  and " + generateOrgAuthQueryWithData("code.");
		Query query = getSession().createQuery(hql);
		return query.list();
	}

	@Cacheable("gooruCache")
	@Override
	public String findGooruTaxonomyCourse(List<String> codeIds) {
		String sql = "select group_concat(label) as labels from code where root_node_id=20000 and organization_uid  IN (" + getUserOrganizationUidsAsString() + ") and code_id IN (:codeIds) ";
		Query query = getSession().createSQLQuery(sql).addScalar("labels", StandardBasicTypes.STRING);
		query.setParameterList("codeIds", codeIds);
		return query.list() != null ? (String) query.list().get(0) : null;
	}

	@Override
	@Cacheable("gooruCache")
	public Code findCodeByCodeIds(Integer codeId) {
		Integer activeFlag = 1;
		Query query = getSession().createSQLQuery(FIND_CODE_BY_CODEIDS).addEntity(Code.class);
		query.setInteger(0, codeId).setInteger(1, activeFlag).list();
		return query.list().size() > 0 ? (Code) query.list().get(0) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Code> findCodeStartWith(String codeStartWith, Short depth) {
		String hql = "FROM Code c where code LIKE :code AND depth =:depth AND c.activeFlag = 1 and " + generateOrgAuthQueryWithData("c.") +  " order by c.sequence";
		Query query = getSession().createQuery(hql);
		query.setParameter("code", codeStartWith + "%");
		query.setParameter("depth", depth);
		return query.list();
	}
}
