package org.ednovo.goorucore.application.serializer;

import flexjson.transformer.AbstractTransformer;

public class ExcludeNullTransformer extends AbstractTransformer {

	@Override
	public Boolean isInline() {
		return true;
	}

	@Override
	public void transform(Object object) {
		if (object == null) {
			return;
		}
	}
}