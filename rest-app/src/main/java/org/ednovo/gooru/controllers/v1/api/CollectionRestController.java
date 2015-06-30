package org.ednovo.gooru.controllers.v1.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.RequestMappingUri;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.collection.CollectionBoService;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CollectionRestController extends BaseController implements ConstantProperties, ParameterProperties {

	@Autowired
	private CollectionBoService collectionBoService;

	private final String COLLECTION_TYPES = "collection,assessment,assessment/url";

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ADD })
	@RequestMapping(value = { RequestMappingUri.LESSON_COLLECTION }, method = RequestMethod.POST)
	public ModelAndView createCollection(@PathVariable(value = COURSE_ID) final String courseUId, @PathVariable(value = UNIT_ID) final String unitUId, @PathVariable(value = LESSON_ID) final String lessonUId, @RequestBody final String data, final HttpServletRequest request,
			final HttpServletResponse response) {
		final User user = (User) request.getAttribute(Constants.USER);
		final ActionResponseDTO<Collection> responseDTO = this.getCollectionBoService().createCollection(courseUId, unitUId, lessonUId, user, buildCollection(data));
		if (responseDTO.getErrors().getErrorCount() > 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			response.setStatus(HttpServletResponse.SC_CREATED);
			responseDTO.getModel().setUri(generateUri(request.getRequestURI(), responseDTO.getModel().getGooruOid()));
		}
		String includes[] = (String[]) ArrayUtils.addAll(CREATE_INCLUDES, ERROR_INCLUDE);
		return toModelAndViewWithIoFilter(responseDTO.getModelData(), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, includes);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_UPDATE })
	@RequestMapping(value = { RequestMappingUri.LESSON_COLLECTION_ID }, method = RequestMethod.PUT)
	public void updateCollection(@PathVariable(value = COURSE_ID) final String courseUId, @PathVariable(value = UNIT_ID) final String unitUId, @PathVariable(value = LESSON_ID) final String lessonUId, @PathVariable(value = ID) final String collectionId, @RequestBody final String data,
			final HttpServletRequest request, final HttpServletResponse response) {
		final User user = (User) request.getAttribute(Constants.USER);
		this.getCollectionBoService().updateCollection(collectionId, buildCollection(data), user);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@RequestMapping(value = { RequestMappingUri.LESSON_COLLECTION_ID }, method = RequestMethod.GET)
	public ModelAndView getCollection(@PathVariable(value = COURSE_ID) final String courseUId, @PathVariable(value = UNIT_ID) final String unitUId, @PathVariable(value = LESSON_ID) final String lessonUId, @PathVariable(value = ID) final String collectionId, final HttpServletRequest request,
			final HttpServletResponse response) {
		return toModelAndViewWithIoFilter(this.getCollectionBoService().getCollection(collectionId, COLLECTION), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, "*");
	}
	

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@RequestMapping(value = { RequestMappingUri.LESSON_COLLECTION }, method = RequestMethod.GET)
	public ModelAndView getCollections(@PathVariable(value = COURSE_ID) final String courseUId, @PathVariable(value = UNIT_ID) final String unitUId, @PathVariable(value = LESSON_ID) final String lessonUId, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") int offset,
			@RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") int limit, @RequestParam(value = COLLECTION_TYPE, required = false, defaultValue = COLLECTION_TYPES) String collectionTypes, final HttpServletRequest request, final HttpServletResponse response) {
		return toModelAndViewWithIoFilter(this.getCollectionBoService().getCollections(lessonUId, collectionTypes, limit, offset), RESPONSE_FORMAT_JSON, EXCLUDE_ALL, true, "*");
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@RequestMapping(value = { RequestMappingUri.LESSON_COLLECTION_ID }, method = RequestMethod.DELETE)
	public void deleteCollection(@PathVariable(value = COURSE_ID) final String courseUId, @PathVariable(value = UNIT_ID) final String unitUId, @PathVariable(value = LESSON_ID) final String lessonUId, @PathVariable(value = ID) final String collectionId, final HttpServletRequest request,
			final HttpServletResponse response) {
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_COPY })
	@RequestMapping(value = { RequestMappingUri.TARGET_LESSON }, method = RequestMethod.POST)
	public ModelAndView copyCollection(@PathVariable(value = COURSE_ID) final String courseUId, @PathVariable(value = UNIT_ID) final String unitUId, @PathVariable(value = LESSON_ID) final String lessonUId, @RequestBody final String data, final HttpServletRequest request,
			final HttpServletResponse response) {
		return null;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_MOVE })
	@RequestMapping(value = { RequestMappingUri.TARGET_LESSON }, method = RequestMethod.PUT)
	public ModelAndView moveCollection(@PathVariable(value = COURSE_ID) final String courseUId, @PathVariable(value = UNIT_ID) final String unitUId, @PathVariable(value = LESSON_ID) final String lessonUId, @RequestBody final String data, final HttpServletRequest request,
			final HttpServletResponse response) {
		return null;
	}

	public CollectionBoService getCollectionBoService() {
		return collectionBoService;
	}

	private Collection buildCollection(final String data) {
		return JsonDeserializer.deserialize(data, Collection.class);
	}
}
