package com.github.devholic.SOMAReport.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Controller.UserController;

public class JSONFactory {
	
	private final static Logger Log = Logger.getLogger(UserController.class);

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
	
	public static JSONObject inputStreamToJson(InputStream is) {
		if (is == null) return null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String str, doc = "";
		try {
			while ((str = reader.readLine()) != null) { 
				doc += str;
			}
		} catch (IOException e) {
			Log.error(e.getLocalizedMessage());
			return null;
		}
		JSONObject jo = new JSONObject(doc);
		return jo;
	}

}
