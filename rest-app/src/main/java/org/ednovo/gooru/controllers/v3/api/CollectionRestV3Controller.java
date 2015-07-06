package org.ednovo.gooru.controllers.v3.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.RequestMappingUri;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.collection.CollectionBoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping(value = { RequestMappingUri.V3_COLLECTION })
@Controller
public class CollectionRestV3Controller extends BaseController implements ConstantProperties, ParameterProperties {

	@Autowired
	private CollectionBoService collectionBoService;

	private static final String INCLUDE_ITEMS = "includeItems";

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@RequestMapping(value = { RequestMappingUri.ID }, method = RequestMethod.GET)
	public ModelAndView getCollection(@PathVariable(value = ID) final String collectionId, @RequestParam(value = INCLUDE_ITEMS, required = false, defaultValue = FALSE) final boolean includeItems, final HttpServletRequest request, final HttpServletResponse response) {
		return toModelAndViewWithIoFilter(this.getCollectionBoService().getCollection(collectionId, COLLECTION, includeItems), RESPONSE_FORMAT_JSON, EXCLUDE, true, INCLUDE_COLLECTION_ITEMS);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_SCOLLECTION_READ })
	@RequestMapping(value = { RequestMappingUri.ITEM_ID }, method = RequestMethod.GET)
	public ModelAndView getCollectionItems(@PathVariable(value = ID) final String collectionId, @RequestParam(value = OFFSET_FIELD, required = false, defaultValue = "0") int offset, @RequestParam(value = LIMIT_FIELD, required = false, defaultValue = "10") int limit,
			final HttpServletRequest request, final HttpServletResponse response) {
		return toModelAndViewWithIoFilter(this.getCollectionBoService().getCollectionItems(collectionId, limit, offset), RESPONSE_FORMAT_JSON, EXCLUDE, true, INCLUDE_COLLECTION_ITEMS);
	}

	public CollectionBoService getCollectionBoService() {
		return collectionBoService;
	}
}
