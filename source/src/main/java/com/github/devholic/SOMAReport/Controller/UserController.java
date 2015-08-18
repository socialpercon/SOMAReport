package com.github.devholic.SOMAReport.Controller;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Utilities.JSONFactory;
import com.github.devholic.SOMAReport.Utilities.StringFactory;

public class UserController {

	public final static int ROLE_MENTOR = 0;
	public final static int ROLE_MENTEE = 1;

	private final static Logger Log = Logger.getLogger(UserController.class);

	static DatabaseController db = new DatabaseController();

	public static String login(String email, String password) {
		// _design/user/auth
		// Key : email
		// Value : salt / value
		InputStream is = db.getByView("_design/user", "auth", email, false, false, false);
		JSONArray result = JSONFactory.getData(new JSONObject(StringFactory.inputStreamToString(is)));
		if (result.length() != 0) {
			JSONArray parsed = JSONFactory.getValue(result.getJSONObject(0));
			if (StringFactory.encryptPassword(password, parsed.getString(0)).equals(parsed.getString(1))) {
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
		// Value : [ {id, value[_id / role / email / name / belong / years ]} ]
		InputStream is = db.getByView("_design/user", "role", "mentee", false, false, false);
		JSONArray result = JSONFactory.getData(new JSONObject(StringFactory.inputStreamToString(is)));
		return result;
	}

	public static JSONArray getMentorList() {
		// _design/user/role
		// Key : role
		// Value : [ {id, value[_id / role / email / name / belong / years ]} ]
		InputStream is = db.getByView("_design/user", "role", "mentor", false, false, false);
		JSONArray result = JSONFactory.getData(new JSONObject(StringFactory.inputStreamToString(is)));
		return result;
	}

	public static boolean isAlreadyRegistered(String email) {
		// 이메일을 통해 이미 등록된 사용자인지를 확인한다
		InputStream is = db.getByView("_design/user", "search_by_email", email, false, false, false);
		JSONArray results = JSONFactory.getData(new JSONObject(StringFactory.inputStreamToString(is)));
		if (results.length() == 0) 
			return false;
		else
			return true;
	}
	
	public static JSONObject getUserByEmail (String email) {
		// 이메일으 통해 사용자 문서를 가져온다 
		JSONArray results = JSONFactory.getData(JSONFactory.inputStreamToJson(db.getByView("_design/user", "search_by_email", email, true, false, false)));
		if (results.length() == 0) 
			return null; 
		else
			return results.getJSONObject(0).getJSONObject("doc");
	}

	public static String getIdbyName(String name) {
		// 이름을 통해 해당 사용자 문서의 _id를 가져온다
		InputStream is =db.getByView("_design/user", "search_by_name", name, false, false, false);
		try {
			JSONArray results = JSONFactory.inputStreamToJson(is).getJSONArray("rows");
			if (results.length() == 0)	throw new Exception();
			return results.getJSONObject(0).getString("value");
		} catch (IOException e) {
			Log.error(e.getLocalizedMessage());
			return null;
		} catch (Exception e) {
			Log.error(e.getLocalizedMessage());
			return null;
		}
	}

	/**
	 * _id에 맞는 이름을 가져온다.
	 * 
	 * @param userId
	 * @return String (user name)
	 */
	public static String getUserName(String userId) {
		JSONObject userDoc = JSONFactory.inputStreamToJson(db.getDoc(userId));
		return userDoc.getString("name");
	}
	
}
