package com.github.devholic.SOMAReport;

public class StringFactory {
	public static String createBaseUrl(String address, String port) {
		return address + ":" + port + "/";
	}
}
