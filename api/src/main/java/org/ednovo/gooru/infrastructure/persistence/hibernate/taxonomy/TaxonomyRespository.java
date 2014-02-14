/////////////////////////////////////////////////////////////
// TaxonomyRespository.java
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

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.CodeType;
import org.ednovo.gooru.core.api.model.CodeUserAssoc;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;

public interface TaxonomyRespository extends BaseRepository{
	
	Code findCodeByTaxCode(String taxonomyCode);
	
	List<Code> findRootTaxonomies(Short depth, String creatorUid);
	
	int findMaxDepthInTaxonomy(Code code, String organizationUid);
	
	CodeType findTaxonomyTypeBydepth(Code code, Short depth);
	
	List<CodeType> findTaxonomyLevels(Code root);
	
	List<CodeType> findAllTaxonomyLevels();
	
	List<Code> findChildTaxonomyCode(Integer codeId);
	
	List<Code> findChildTaxonomyCodeByOrder(Integer codeId, String order);
	
	List<Code> findCodeByType(Integer taxonomyLevel);
	
	List<Code> findParentTaxonomyCodes(Integer codeId, List<Code> codeList);
	
	List<Code> findSiblingTaxonomy(Code code);
	
	List<Code> findTaxonomyMappings(List<Code> codeList, boolean excludeTaxonomyPreference);
	
	String findRootLevelTaxonomy(Code code);
	
	void updateOrder(Code code);

	String makeTree(Code rootCode);
	
	void writeToDisk(Code cde) throws Exception;
	
	String findTaxonomyTree(String taxonomyCode, String format) throws Exception;
	
	void updateTaxonomyAssociation(Code taxonomy, List<Code> codes);
	
	void deleteTaxonomyMapping(Code code, List<Code> codes);
	
	Code findByLabel(String label);
	
	Code findByParent(String label, Integer parentId);
	
	List<Code> findChildTaxonomyCodeByDepth(Integer codeId, Integer depth);

	List<Code> findAll();

	List<Code> findAllByRoot(Integer codeId);

	List<Code> getCodesOfConent(Long contentId);

	Code findCodeByCodeId(Integer codeId);
	
	Code findCodeByCodeUId(String codeUId);

	List<Code> findParentTaxonomy(Integer codeId, boolean reverse);
	
	List<Code> listTaxonomy(Map<String,String> filters);
	
	List<Map<String,String>> findAllMappedStandards(String code,Map<String,String> filters);
	
	List<Integer> findSourceCodeByTargetCode(Integer targetCodeId);
	
	Code findFirstChildTaxonomyCodeByDepth(Integer codeId, Integer depth);
	
	Code findCode(Integer codeId, String organizationUid);

	List<Integer> getCodeIdByContentIds(String contentIds);
	
	List<Code> findTaxonomyMappings(String codeIds);

	List<Code> getCodeByContentIds(String contentIds);
	
	String getFindTaxonomyList(String excludeCode);
	
	String getFindTaxonomyCodeList(String codeIds);
	
	Code findTaxonomyCodeById(Integer codeId);
	
	List<Code> findCodeByParentCodeId(String code, String creatorUid, Integer limit, Integer offset,Boolean skipPagination, String fetchType, String organizationCode, String rootNode, String depth);
	
	List<User> getFeaturedUser(String organizationCode);
	
	List<CodeUserAssoc> getUserCodeAssoc(Integer codeId, String organizationCode);
	
	List<Code> getCodeByDepth(String organizationCode, Short depth, String creatorUid);
	
}
