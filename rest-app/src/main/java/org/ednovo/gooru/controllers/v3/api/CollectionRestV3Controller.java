package org.ednovo.gooru.controllers.v3.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.RequestMappingUri;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping(value = { RequestMappingUri.V3_COLLECTION })
@Controller
public class CollectionRestV3Controller extends BaseController implements ConstantProperties, ParameterProperties {
	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ADD })
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView createCollection(@RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response) {
		return null;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_UPDATE })
	@RequestMapping(value = RequestMappingUri.ID, method = RequestMethod.PUT)
	public void updateCollection(@PathVariable(value = ID) final String collectionUId, @RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response) {
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@RequestMapping(value = RequestMappingUri.ID, method = RequestMethod.GET)
	public ModelAndView getCollection(@PathVariable(value = ID) final String collectionUId, final HttpServletRequest request, final HttpServletResponse response) {
		return null;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getCollections(final HttpServletRequest request, final HttpServletResponse response) {
		return null;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@RequestMapping(value = RequestMappingUri.ID, method = RequestMethod.DELETE)
	public void deleteCollection(@PathVariable(value = ID) final String collectionUId, final HttpServletRequest request, final HttpServletResponse response) {
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ADD })
	@RequestMapping(value = { RequestMappingUri.CREATE_RESOURCE }, method = RequestMethod.POST)
	public ModelAndView createResource(@PathVariable(value = ID) final String collectionUId, @RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response) {
		return null;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_ADD })
	@RequestMapping(value = { RequestMappingUri.CREATE_QUESTION }, method = RequestMethod.POST)
	public ModelAndView createQuestion(@PathVariable(value = ID) final String collectionUId, @RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response) {
		return null;
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_UPDATE })
	@RequestMapping(value = { RequestMappingUri.UPDATE_RESOURCE }, method = RequestMethod.PUT)
	public void updateResource(@PathVariable(value = COLLECTION_ID) final String collectionUId, @PathVariable(value = ID) final String questionId, @RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response) {

	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_UPDATE })
	@RequestMapping(value = { RequestMappingUri.UPDATE_QUESTION }, method = RequestMethod.PUT)
	public void updateQuestion(@PathVariable(value = COLLECTION_ID) final String collectionUId, @PathVariable(value = ID) final String questionId, @RequestBody final String data, final HttpServletRequest request, final HttpServletResponse response) {
	}
}
