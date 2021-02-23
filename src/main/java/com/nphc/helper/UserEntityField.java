package com.nphc.helper;

public enum UserEntityField {

	id, login, name, startDate, salary;

	public static boolean validValue(String value) {

		if (fromString(value) == null) {
			return false;
		}

		return true;
	}

	public String getValue() {

		String result = "";

		switch (this) {
		case startDate:
			result = "date";
			break;
		default:
			result = this.name().toString();
		}
		return result;
	}

	public static UserEntityField fromString(String value) {

		try {
			for (UserEntityField val : UserEntityField.values()) {
				if (val.toString().equalsIgnoreCase(value)) {
					return val;
				}
			}
		} catch (Exception ex) {
		}
		return null;
	}
}
