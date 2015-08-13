package com.github.devholic.SOMAReport.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
		InputStream is = db.getByView("_design/user", "auth", email, false, false);
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
		InputStream is = db.getByView("_design/user", "role", "mentee", false, false);
		JSONArray result = JSONFactory.getData(new JSONObject(StringFactory
				.inputStreamToString(is)));
		return result;
	}

	public static JSONArray getMentorList() {
		// _design/user/role
		// Key : role
		// Value : _id / role / email / name / belong / years / section
		InputStream is = db.getByView("_design/user", "role", "mentor", false, false);
		JSONArray result = JSONFactory.getData(new JSONObject(StringFactory
				.inputStreamToString(is)));
		return result;
	}
	
	public boolean isAlreadyRegistered(String email) {
		// 이메일을 통해 이미 등록된 사용자인지를 확인한다
		BufferedReader is = new BufferedReader(new InputStreamReader(db.getByView("_design/user", "search_by_email", email, false, false)));
		try {
			String str, doc = "";
			while ((str = is.readLine())!= null) {	doc += str;	}
			JSONArray results = new JSONObject(doc).getJSONArray("rows");
			if (results.length() == 0) 
				return false;
			else
				return true;
		} catch (IOException e) {
			Log.error(e.getLocalizedMessage());
			return false;
		}
	}

}
