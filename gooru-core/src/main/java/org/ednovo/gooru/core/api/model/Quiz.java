package org.ednovo.gooru.core.api.model;

import org.ednovo.goorucore.application.serializer.JsonDeserializer;

public class Quiz extends Collection {

	/**
	 * 
	 */
	private static final long serialVersionUID = -110949227216750249L;

	private String options;

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public Options getOption() {
		if (getOptions() != null) {
			return JsonDeserializer.deserialize(getOptions(), Options.class);
		}
		return null;
	}

}
