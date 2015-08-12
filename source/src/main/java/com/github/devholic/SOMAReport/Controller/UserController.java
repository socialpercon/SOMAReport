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

	public static String Login(String email, String password) {
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
}
