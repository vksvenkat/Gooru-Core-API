package org.ednovo.gooru.core.api.model;

public enum AttemptTryStatus {
	CORRECT("correct"), WRONG("wrong"), SKIP("skip"), SKIPPED("skipped");

	private String tryStatus;

	private AttemptTryStatus(String tryStatus) {
		this.tryStatus = tryStatus;
	}

	public String getTryStatus() {
		return this.tryStatus;
	}
}
