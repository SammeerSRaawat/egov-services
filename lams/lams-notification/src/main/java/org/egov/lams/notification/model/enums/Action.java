package org.egov.lams.notification.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Action {

	CREATE("Create"),

	CANCELATION("Cancelation"),

	EVICTION("Eviction"),

	RENEWAL("Renewal"),

	OBJECTION("Objection");

	private String value;

	Action(String value) {
		this.value = value;
	}

	@Override
	@JsonValue
	public String toString() {
		return String.valueOf(value);
	}

	@JsonCreator
	public static Action fromValue(String text) {
		for (Action b : Action.values()) {
			if (String.valueOf(b.value).equals(text)) {
				return b;
			}
		}
		return null;
	}
}