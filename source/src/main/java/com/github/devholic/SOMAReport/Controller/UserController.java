package com.github.devholic.SOMAReport.Controller;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Utilities.JSONFactory;
import com.github.devholic.SOMAReport.Utilities.StringFactory;

public class UserController {

	private final static Logger Log = Logger.getLogger(UserController.class);

	static DatabaseController db = new DatabaseController();

	public static String login(String email, String password) {
		// _design/user/auth
		// Key : email
		// Value : salt / value
		InputStream is = db.getByView("_design/user", "auth", email);
		JSONArray result = JSONFactory.getData(new JSONObject(StringFactory
				.inputStreamToString(is)));
		if (result.length() != 0) {
			JSONArray parsed = JSONFactory.getValue(result.getJSONObject(0));
			if (StringFactory.encryptPassword(password, parsed.getString(0))
					.equals(parsed.getString(1))) {
				return result.getJSONObject(0).getString("id");
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public static JSONArray getMenteeList() {
		// _design/user/role
		// Key : role
		// Value : _id / role / email / name / belong / years / section
		InputStream is = db.getByView("_design/user", "role", "mentee");
		JSONArray result = JSONFactory.getData(new JSONObject(StringFactory
				.inputStreamToString(is)));
		return result;
	}

	public static JSONArray getMentorList() {
		// _design/user/role
		// Key : role
		// Value : _id / role / email / name / belong / years / section
		InputStream is = db.getByView("_design/user", "role", "mentor");
		JSONArray result = JSONFactory.getData(new JSONObject(StringFactory
				.inputStreamToString(is)));
		return result;
	}
}
