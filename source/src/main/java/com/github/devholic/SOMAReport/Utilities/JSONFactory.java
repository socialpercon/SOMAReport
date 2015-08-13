package com.github.devholic.SOMAReport.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONFactory {

	public static JSONArray getData(JSONObject raw) {
		if (raw.has("rows")) {
			return raw.getJSONArray("rows");
		} else {
			return null;
		}
	}

	public static JSONArray getValue(JSONObject data) {
		if (data.has("value")) {
			return data.getJSONArray("value");
		} else {
			return null;
		}
	}

}
