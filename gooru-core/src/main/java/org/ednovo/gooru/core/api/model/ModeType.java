package org.ednovo.gooru.core.api.model;

public enum ModeType {
	TEST("test"), PLAY("play"), PRACTICE("practice");

	private String modeType;

	private ModeType(String modeType) {
		this.modeType = modeType;
	}

	public String getModeType() {
		return this.modeType;
	}

}
