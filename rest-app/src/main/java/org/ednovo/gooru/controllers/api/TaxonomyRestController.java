package org.ednovo.gooru.controllers.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.application.util.GooruImageUtil;
import org.ednovo.gooru.application.util.ResourceImageUtil;
import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.CodeType;
import org.ednovo.gooru.core.api.model.TaxonomyDTO;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.application.util.formatter.FilterSubjectFo;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.domain.service.taxonomy.TaxonomyService;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = { "/taxonomy", "" })
public class TaxonomyRestController extends BaseController implements ConstantProperties {

	@Autowired
	private TaxonomyService taxonomyService;

	@Autowired
	private ResourceImageUtil resourceImageUtil;
	
	
	private static final Logger logger = LoggerFactory.getLogger(TaxonomyRestController.class);

	@Autowired
	private RedisService redisService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAXONOMY_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/taxonomy/list.{format}")
	public ModelAndView getTaxonomyListXml(@PathVariable(FORMAT) String format, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletRequest request, HttpServletResponse response, final ModelMap model) throws Exception {
		request.setAttribute(PREDICATE, TAXONOMY_GET_LIST_XML);
		List<Code> codes = taxonomyService.findRootTaxonomies(Short.valueOf(ZERO));

		TaxonomyDTO taxdto = new TaxonomyDTO();

		taxdto.setCode(codes);

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		if (format.equals(FORMAT_XML)) {
			jsonmodel.addObject(MODEL, taxdto.findListXml());
		} else if (format.equals(FORMAT_JSON)) {
			JSONObject xmlJSONObj = XML.toJSONObject(taxdto.findListXml());
			jsonmodel.addObject(MODEL, xmlJSONObj.toString());
		}

		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAXONOMY_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/taxonomy/{parentCode}/{depth}.{format}")
	public ModelAndView getTaxonomyListByDepth(@PathVariable(FORMAT) String format, @PathVariable(PARENT_CODE) Integer parentCode, @PathVariable(DEPTH) String depth, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletRequest request, HttpServletResponse response,
			final ModelMap model) throws Exception {
		request.setAttribute(PREDICATE, TAXONOMY_GET_LIST_BY_DEPTH);
		Integer depthInt = Integer.valueOf(depth);
		List<Code> codes = taxonomyService.findChildTaxonomyCodeByDepth(parentCode, depthInt);

		TaxonomyDTO taxdto = new TaxonomyDTO();

		taxdto.setCode(codes);

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		if (format.equals(FORMAT_XML)) {
			jsonmodel.addObject(MODEL, taxdto.findListXml());
		} else if (format.equals(FORMAT_JSON)) {
			JSONObject xmlJSONObj = XML.toJSONObject(taxdto.findListXml());
			jsonmodel.addObject(MODEL, xmlJSONObj.toString());
		}

		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAXONOMY_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/taxonomy/{parentCode}/node.{format}")
	public ModelAndView createTaxonomy(@PathVariable(FORMAT) String format, @PathVariable(value = PARENT_CODE) String parentCode, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = LABEL) String label, @RequestParam(value = CLASSPAGE_CODE) String code,
			@RequestParam(value = ORDER) String order, @RequestParam(value = ROOT_NODE_ID) String rootNodeId, @RequestParam(value = CODE_ROOT) String codeRoot, @RequestParam(value = DISPLAY_CODE, required = false) String displayCode, HttpServletRequest request, HttpServletResponse response,
			final ModelMap model) throws Exception {
		request.setAttribute(PREDICATE, TAXONOMY_CREATE);

		TaxonomyDTO taxdto = taxonomyService.createTaxonomy(parentCode, label, code, order, rootNodeId, codeRoot, displayCode);

		if (taxdto != null && taxdto.findNodeXml().length() > 0) {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		if (format.equals(FORMAT_XML)) {
			jsonmodel.addObject(MODEL, taxdto.findNodeXml());
		}

		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAXONOMY_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/taxonomy/write")
	public void writeGooruTaxonomyToDisk(HttpServletRequest request, HttpServletResponse response, final ModelMap model) throws Exception {
		taxonomyService.writeTaxonomyToDisk();
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		jsonmodel.addObject(MODEL, "{}");
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAXONOMY_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/taxonomy/course.{format}")
	public ModelAndView getCourse(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = CODE_ID, required = false) Integer codeId, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @PathVariable(FORMAT) String format,
			@RequestParam(value = CLEAR_CACHE, required = false) boolean clearCache, @RequestParam(value = MAX_LESSON_LIMIT, required = false, defaultValue = FOUR) Integer maxLessonLimit) throws Exception {
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		request.setAttribute(PREDICATE, LIBRARY_CODE_CONTENT);
		User user = (User) request.getAttribute(Constants.USER);
		String libraryCodeList = null;
		if (!clearCache) {
			libraryCodeList = (String) getRedisService().getValue(LIBRARY_CODE_JSON + user.getOrganization().getPartyUid());
		}
		if (codeId == null || codeId == 0) {
			// FIXME
			String organizationUid = user.getOrganization().getPartyUid();
			codeId = TaxonomyUtil.getTaxonomyRootId(organizationUid);
		}
		if (libraryCodeList == null) {
			libraryCodeList = serializeToJsonWithExcludes(taxonomyService.getCourseBySubject(codeId, maxLessonLimit), COURSE_EXCLUDES, true, COURSE_INCLUDES);
			getRedisService().putValue(LIBRARY_CODE_JSON + user.getOrganization().getPartyUid(), libraryCodeList, RedisService.DEFAULT_PROFILE_EXP);
		}
		return jsonmodel.addObject(MODEL, libraryCodeList);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAXONOMY_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/taxonomy/filterSubject.{format}")
	public ModelAndView getFilterSubject(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = CODE_ID, required = false) Integer codeId, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @PathVariable(FORMAT) String format,
			@RequestParam(value = CLEAR_CACHE, required = false) boolean clearCache, @RequestParam(value = MAX_LESSON_LIMIT, required = false, defaultValue = FOUR) Integer maxLessonLimit) throws Exception {
		request.setAttribute(PREDICATE, LIBRARY_CODE_CONTENT);
		if (codeId == null || codeId == 0) {
			// FIXME

			User user = (User) request.getAttribute(Constants.USER);
			String organizationUid = user.getOrganization().getPartyUid();

			codeId = TaxonomyUtil.getTaxonomyRootId(organizationUid);
		}
		FilterSubjectFo filterSubjectFo = this.taxonomyService.getFilterSubject(codeId, maxLessonLimit);
		return toModelAndView(serializeToJson(filterSubjectFo, true, null));
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAXONOMY_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/taxonomy/node/{codeId}.{format}")
	public ModelAndView updateTaxonomy(@PathVariable(FORMAT) String format, @PathVariable(value = CODE_ID) String codeId, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = LABEL) String label, @RequestParam(value = ORDER) String order,
			@RequestParam(value = CLASSPAGE_CODE, required = false) String code, HttpServletRequest request, HttpServletResponse response, final ModelMap model) throws Exception {
		request.setAttribute(PREDICATE, TAXONOMY_UPDATE);

		TaxonomyDTO taxdto = taxonomyService.updateTaxonomy(codeId, label, order, code);

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		if (format.equals(FORMAT_XML)) {
			jsonmodel.addObject(MODEL, taxdto.findNodeXml());
		}

		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAXONOMY_DELETE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/taxonomy/node/{code}.{format}")
	public ModelAndView deleteTaxonomy(@PathVariable(FORMAT) String format, @PathVariable(value = CLASSPAGE_CODE) String code, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletRequest request, HttpServletResponse response, final ModelMap model) throws Exception {
		request.setAttribute(PREDICATE, TAXONOMY_DELETE);

		taxonomyService.deleteTaxonomy(code);

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAXONOMY_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/taxonomy.{format}")
	public ModelAndView createTaxonomyRoot(@PathVariable(FORMAT) String format, @RequestParam(value = CLASSPAGE_CODE) String code, @RequestParam(value = LABEL) String label, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken,
			@RequestParam(value = IS_CODE_AUTO_GENERATED) String isCodeAutoGenerated, @RequestParam(value = ROOT_NODE_ID) Integer rootNodeId, HttpServletRequest request, HttpServletResponse response, final ModelMap model) throws Exception {
		request.setAttribute(PREDICATE, TAXONOMY_CREATE_ROOT);

		TaxonomyDTO taxdto = taxonomyService.createTaxonomyRoot(code, label, isCodeAutoGenerated, rootNodeId);

		if (taxdto != null && taxdto.findNodeXml().length() > 0 && taxdto.findTaxonomyXml().length() > 0) {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		if (format.equals(FORMAT_XML)) {
			jsonmodel.addObject(MODEL, taxdto.findTaxonomyXml());
		}
		if (format.equals(FORMAT_JSON)) {
			JSONObject xmlJSONObj = XML.toJSONObject(taxdto.findTaxonomyXml());
			jsonmodel.addObject(MODEL, xmlJSONObj.toString());
		}

		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAXONOMY_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/taxonomy/{taxonomyCode}/levels.{format}")
	public ModelAndView getTaxonomyLevels(@PathVariable(FORMAT) String format, @PathVariable(value = TAXONOMY_CODE) String taxonomyCode, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletRequest request, HttpServletResponse response, final ModelMap model) throws Exception {
		request.setAttribute(PREDICATE,  TAXONOMY_GET_LEVELS);

		TaxonomyDTO taxonomydto = taxonomyService.getTaxonomyLevels(taxonomyCode);
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		if (format.equals(FORMAT_XML)) {
			jsonmodel.addObject(MODEL, taxonomydto.findLevelsXml());
		} else if (format.equals(FORMAT_JSON)) {
			JSONObject xmlJSONObj = XML.toJSONObject(taxonomydto.findLevelsXml());
			jsonmodel.addObject(MODEL, xmlJSONObj.toString());
		}
		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAXONOMY_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/taxonomy/levels.{format}")
	public ModelAndView getAllTaxonomyLevels(@PathVariable(FORMAT) String format, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletRequest request, HttpServletResponse response, final ModelMap model) throws Exception {
		request.setAttribute(PREDICATE, TAXONOMY_GET_ALL_LEVELS);
		List<CodeType> taxonomyLevels = taxonomyService.findAllTaxonomyLevels();

		TaxonomyDTO taxonomydto = new TaxonomyDTO();
		taxonomydto.setCodetypes(taxonomyLevels);

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		if (format.equals(FORMAT_XML)) {
			jsonmodel.addObject(MODEL, taxonomydto.findAllTaxonomyXMl());
		}
		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAXONOMY_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/taxonomy/{taxonomyCode}/tree.{format}")
	public ModelAndView getTaxonomyTree(@PathVariable(FORMAT) String format, @PathVariable(TAXONOMY_CODE) String taxonomyCode, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletRequest request, HttpServletResponse response, final ModelMap model) throws Exception {
		request.setAttribute(PREDICATE, TAXONOMY_GET_TREE);
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		User user = (User) request.getAttribute(Constants.USER);
		String organizationUid = user.getOrganization().getPartyUid();
		if (taxonomyCode != null && taxonomyCode.equalsIgnoreCase(DEFAULT)) {
			taxonomyCode = String.valueOf(TaxonomyUtil.getTaxonomyRootId(organizationUid));
		}

		jsonmodel.addObject(MODEL, taxonomyService.findTaxonomyTree(taxonomyCode, format));
		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAXONOMY_ADD })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/taxonomy/{taxonomyCode}/level.{format}")
	public ModelAndView addLevel(@PathVariable(FORMAT) String format, @PathVariable(TAXONOMY_CODE) String taxonomyCode, @RequestParam(value = LABEL) String label, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, @RequestParam(value = IS_CODE_AUTO_GENERATED) String isCodeAutogenerated,
			HttpServletRequest request, HttpServletResponse response, final ModelMap model) throws Exception {
		request.setAttribute(PREDICATE, TAXONOMY_ADD_LEVEL);

		TaxonomyDTO taxDto = taxonomyService.addLevel(taxonomyCode, label, isCodeAutogenerated);

		if (taxDto != null && taxDto.findLevelXml().length() > 0) {
			response.setStatus(HttpServletResponse.SC_CREATED);
		}

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		if (format.equals(FORMAT_XML)) {
			jsonmodel.addObject(MODEL, taxDto.findLevelXml());
		}

		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAXONOMY_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/taxonomy/{taxonomyCode}/level/{leveldepth}.{format}")
	public ModelAndView updateLevel(HttpServletRequest request, @PathVariable(FORMAT) String format, @PathVariable(LEVEL_DEPTH) String leveldepth, @PathVariable(TAXONOMY_CODE) String taxonomyCode, @RequestParam(value = LABEL) String label,
			@RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletResponse response, final ModelMap model) throws Exception {
		request.setAttribute(PREDICATE, TAXONOMY_UPDATE_LEVEL);
		TaxonomyDTO taxDto = taxonomyService.updateLevel(leveldepth, taxonomyCode, label);

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		if (format.equals(FORMAT_XML)) {
			jsonmodel.addObject(MODEL, taxDto.findLevelXml());
		}

		return jsonmodel;

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAXONOMY_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/taxonomy/{taxonomyCode}/level/{leveldepth}.{format}")
	public ModelAndView deleteLevel(HttpServletRequest request, @PathVariable(FORMAT) String format, @PathVariable(LEVEL_DEPTH) String leveldepth, @PathVariable(TAXONOMY_CODE) String taxonomyCode, @RequestParam(value = LABEL) String label,
			@RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletResponse response, final ModelMap model) throws Exception {
		request.setAttribute(PREDICATE, TAXONOMY_DELETE_LEVEL);
		taxonomyService.deleteLevel(leveldepth, taxonomyCode);
		return null;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAXONOMY_READ })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/taxonomy/{gooruTaxnomyCodeId}/mapping.{format}")
	public ModelAndView getMapping(HttpServletRequest request, @PathVariable(FORMAT) String format, @PathVariable(GOORU_TAXONOMY_CODE_ID) String gooruTaxnomyCodeId, @RequestParam(value = SESSIONTOKEN, required = false ) String sessionToken, HttpServletResponse response, final ModelMap model) throws Exception {
		request.setAttribute(PREDICATE, TAXONOMY_GET_MAPPING);

		Code code = taxonomyService.findCodeByTaxonomyCodeId(Integer.valueOf(gooruTaxnomyCodeId));

		ModelAndView mView = new ModelAndView(REST_MODEL);

		List<Code> codes = new ArrayList<Code>();
		codes.add(code);

		List<Code> mappings = taxonomyService.findTaxonomyMappings(codes);

		TaxonomyDTO taxDto = new TaxonomyDTO();
		taxDto.setCode(mappings);

		if (format.equals(FORMAT_XML)) {
			mView.addObject(MODEL, taxDto.findMappingXml(code));
		}

		if (format.equals(FORMAT_JSON)) {
			JSONObject xmlJSONObj = XML.toJSONObject(taxDto.findMappingXml(code));
			mView.addObject(MODEL, xmlJSONObj.toString());

		}

		return mView;

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAXONOMY_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/taxonomy/{taxonomyCodeId}/mapping.{format}")
	public ModelAndView updateAssocation(HttpServletRequest request, @PathVariable String format, @PathVariable(TAXONOMY_CODE_ID) String taxonomyCodeId, @RequestParam(value = CURRICULUM_ID, required = false) String curriculumIds, @RequestParam(value = SESSIONTOKEN, required = false) String sessionToken,
			HttpServletResponse response, final ModelMap model) throws Exception {
		request.setAttribute(PREDICATE, TAXONOMY_UPDATE_ASSOCIATION);
		Code code = taxonomyService.findCodeByTaxonomyCodeId(Integer.valueOf(taxonomyCodeId));

		ModelAndView mView = new ModelAndView(REST_MODEL);

		if (code == null) {
			mView.addObject(MODEL, "\"You can not associate\"");
			return mView;
		}

		TaxonomyDTO taxDto = taxonomyService.updateAssocation(taxonomyCodeId, curriculumIds, code);

		if (format.equals(FORMAT_XML)) {
			mView.addObject(MODEL, taxDto.findMappingXml(code));
		}

		return mView;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAXONOMY_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "/taxonomy/{gooruTaxnomyCodeId}/mapping.{format}")
	public ModelAndView deleteMapping(HttpServletRequest request, @PathVariable(FORMAT) String format, @PathVariable(GOORU_TAXONOMY_CODE_ID) String gooruTaxnomyCodeId, @RequestParam(value = CURRICULUM_ID, required = false) String[] CurriculumId,
			@RequestParam(value = SESSIONTOKEN, required = false) String sessionToken, HttpServletResponse response, final ModelMap model) throws Exception {
		request.setAttribute(PREDICATE, TAXONOMY_DEL_MAPPING);
		Code code = taxonomyService.findCodeByTaxonomyCodeId(Integer.valueOf(gooruTaxnomyCodeId));

		ModelAndView mView = new ModelAndView(REST_MODEL);

		TaxonomyDTO taxDto = taxonomyService.deleteMapping(CurriculumId, code);

		if (format.equals(FORMAT_XML)) {
			mView.addObject(MODEL, taxDto.findMappingXml(code));
		}

		return mView;

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAXONOMY_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/taxonomy/writeLibrary")
	public ModelAndView writeLibrary(HttpServletRequest request, HttpServletResponse response, final ModelMap model) throws Exception {

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);

		taxonomyService.writeTaxonomyToDisk();

		jsonmodel.addObject(MODEL, "{}");

		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAXONOMY_READ })
	@RequestMapping(method = RequestMethod.GET, value = "/taxonomy/{taxonomyCodeId}/parents.{format}")
	public ModelAndView getTaxonomyParentsByCodeId(HttpServletRequest request, @PathVariable(TAXONOMY_CODE_ID) Integer taxonomyCodeId, @PathVariable(FORMAT) String format, HttpServletResponse response, final ModelMap model) throws Exception {
		request.setAttribute(PREDICATE, TAXONOMY_GET_TAXONOMY_PARENTS_BY_CODE_ID);

		List<Code> codeList = new ArrayList<Code>();
		Map<Integer, List<Code>> codeParentsMap = new HashMap<Integer, List<Code>>();
		taxonomyService.findParentTaxonomyCodes(taxonomyCodeId, codeList);
		Collections.reverse(codeList);
		codeParentsMap.put(taxonomyCodeId, codeList);

		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		if (format.equalsIgnoreCase(FORMAT_JSON)) {
			JSONObject taxonomyJsonObj = new JSONObject();
			jsonmodel.addObject(MODEL, taxonomyJsonObj.put(TAXONOMY_SET_MAPPING, serializeToJsonWithExcludes(codeParentsMap, TaxonomyUtil.CODE_PARENTS_EXCLUDES, true)));
		}

		return jsonmodel;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_TAXONOMY_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.POST, value = "/taxonomy/{taxonomyCodeUId}/image")
	public ModelAndView uploadTaxonomyImage(@PathVariable(TAXONOMY_CODE_UID) String taxonomyCodeUId, @RequestParam(value = UPLOADED_IMG_SRC) String uploadedImageSrc, HttpServletRequest request, HttpServletResponse response) throws Exception {

		ModelAndView jsonModel = new ModelAndView(REST_MODEL);
		JSONObject jsonObject = new JSONObject();

		if (uploadedImageSrc != null && !uploadedImageSrc.equals("")) {
			Code code = taxonomyService.findCodeByCodeUId(taxonomyCodeUId);
			if (code == null) {
				return jsonModel.addObject(MODEL, jsonObject.put("Error", "Invalid taxonomy code id"));
			}
			String uploadedMediaFolder = "/" + Constants.UPLOADED_MEDIA_FOLDER + "/";
			String fileName = uploadedImageSrc;
			if (uploadedImageSrc.contains(uploadedMediaFolder)) {
				fileName = StringUtils.substringAfterLast(uploadedImageSrc, uploadedMediaFolder);
			}
			String repoPath = code.getOrganization().getNfsStorageArea().getInternalPath();
			String sourceFilePath = repoPath + uploadedMediaFolder + fileName;
			String destFolderPath = repoPath + Constants.CODE_FOLDER + "/" + code.getCodeId() + "/";

			try {
				String movedFilePath = GooruImageUtil.moveImage(sourceFilePath, destFolderPath, code.getCodeUid());
				code.setCodeImage(StringUtils.substringAfterLast(movedFilePath, repoPath));
				code = taxonomyService.saveUploadTaxonomyImage(code);
				logger.error("Code image Source:" + uploadedImageSrc + " destination folder: " + movedFilePath);
				resourceImageUtil.createThumbnailForCode(code.getCodeUid(), movedFilePath, destFolderPath, ResourceImageUtil.RESOURCE_THUMBNAIL_SIZES);
				jsonModel.addObject(MODEL, serializeToJsonWithExcludes(code, TaxonomyUtil.CODE_THUMBNAILS_EXCLUDES, false));

			} catch (Exception ex) {
				logger.error("uploading image failed; " + ex);
				jsonModel.addObject(MODEL, jsonObject.put("Error", "uploading image failed"));
			}

		} else {
			jsonModel.addObject(MODEL, jsonObject.put("Error", "image url could not be empty"));
		}
		return jsonModel;
	}

	public RedisService getRedisService() {
		return redisService;
	}
}
