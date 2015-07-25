package com.github.devholic.SOMAReport.Utilities;

public class StringFactory {
	public static String createBaseUrl(String address, String port) {
		return address + ":" + port + "/";
	}
}
