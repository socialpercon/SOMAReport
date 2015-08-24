package com.github.devholic.SOMAReport.Controller;

import java.io.IOException;
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
		System.out.println();
		InputStream is = db.getByView("_design/user", "auth", email, false,
				false, false);
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
		// Value : [ {id, value[_id / role / email / name / belong / years ]} ]
		InputStream is = db.getByView("_design/user", "role", "mentee", false,
				false, false);
		JSONArray result = JSONFactory.getData(new JSONObject(StringFactory
				.inputStreamToString(is)));
		return result;
	}

	public static JSONArray getMentorList() {
		// _design/user/role
		// Key : role
		// Value : [ {id, value[_id / role / email / name / belong / years ]} ]
		InputStream is = db.getByView("_design/user", "role", "mentor", false,
				false, false);
		JSONArray result = JSONFactory.getData(new JSONObject(StringFactory
				.inputStreamToString(is)));
		return result;
	}

	public static JSONArray getAllUsers() {
		JSONArray result = new JSONArray();
		JSONArray arr = JSONFactory.getData(JSONFactory.inputStreamToJson(db
				.getByView("_design/user", "role", true, false, false)));
		for (int i = 0; i < arr.length(); i++) {
			result.put(arr.getJSONObject(i).get("doc"));
		}
		return result;
	}

	public static boolean isAlreadyRegistered(String email) {
		// 이메일을 통해 이미 등록된 사용자인지를 확인한다
		InputStream is = db.getByView("_design/user", "search_by_email", email,
				false, false, false);
		JSONArray results = JSONFactory.getData(new JSONObject(StringFactory
				.inputStreamToString(is)));
		if (results.length() == 0)
			return false;
		else
			return true;
	}

	public static JSONObject getUserByEmail(String email) {
		// 이메일을 통해 사용자 문서를 가져온다
		JSONArray results = JSONFactory.getData(JSONFactory
				.inputStreamToJson(db.getByView("_design/user",
						"search_by_email", email, true, false, false)));
		if (results.length() == 0)
			return null;
		else
			return results.getJSONObject(0).getJSONObject("doc");
	}

	public static String getIdbyName(String name) {
		// 이름을 통해 해당 사용자 문서의 _id를 가져온다
		InputStream is = db.getByView("_design/user", "search_by_name", name,
				false, false, false);
		try {
			JSONArray results = JSONFactory.inputStreamToJson(is).getJSONArray(
					"rows");
			if (results.length() == 0)
				throw new Exception();
			return results.getJSONObject(0).getString("value");
		} catch (IOException e) {
			Log.error(e.getLocalizedMessage());
			return null;
		} catch (Exception e) {
			Log.error(e.getLocalizedMessage());
			return null;
		}
	}

	/***
	 * 해당 user의 role 정보를 가져온다.
	 * 
	 * @param userId
	 * @return int (ROLE_MENTOR / ROLE_MENTEE)
	 */

	public static String getRoleById(String userId) {
		JSONObject userDoc = JSONFactory.inputStreamToJson(db.getDoc(userId));
		if (userDoc.has("role")) {
			String userRole = userDoc.getString("role");
			return userRole;
		} else {
			Log.error("Role info does not exists");
			return null;
		}
	}

	/**
	 * _id에 맞는 이름을 가져온다.
	 * 
	 * @param userId
	 * @return String (user name)
	 */
	public String getUserName(String userId) {
		JSONObject userDoc = JSONFactory.inputStreamToJson(db.getDoc(userId));
		return userDoc.getString("name");
	}

	/***
	 * 해당 년도에 속한 유저 리스트를 가져온다
	 * 
	 * @param year
	 * @return
	 */
	public JSONArray getUsersInYear(int year) {
		JSONObject raw = JSONFactory.inputStreamToJson(db.getByView(
				"_design/user", "users_in_year", year, false, true, false));
		JSONArray result = JSONFactory.getData(raw);
		JSONArray users = new JSONArray();
		for (int i = 0; i < result.length(); i++) {
			users.put(result.getJSONObject(i).get("value"));
		}
		return users;
	}

	/**
	 * 해당 유저의 기본 정보 (name, id, belong)를 가져온다.
	 * 
	 * @param String
	 *            userId
	 * @return JSONObject
	 */
	public static JSONObject getUserInfo(String userId) {
		JSONObject userDoc = new JSONObject();
		JSONObject user = JSONFactory.inputStreamToJson(db.getDoc(userId));

		userDoc.put("id", user.get("_id"));
		userDoc.put("name", user.get("name"));
		userDoc.put("role", user.get("role"));
		userDoc.put("belong", user.get("belong"));
		userDoc.put("email", user.get("email"));
		if (user.has("years")) userDoc.put("years", user.get("years"));
		else if (user.has("year")) userDoc.put("years", user.get("year"));
		else userDoc.put("years", new Object[]{});
			
		return userDoc;
	}

	/***
	 * 기존 비밀번호와 새로운 비밀번호를 입력받아 비밀번호를 수정한다.
	 * 
	 * @param String
	 *            userId, String oldPwd, String newPwd, String newPwd2
	 * @return boolean
	 */
	public boolean modifyPassword(String userId, String oldPwd, String newPwd) {

		JSONObject userDoc = JSONFactory.inputStreamToJson(db.getDoc(userId));
		if (userDoc == null) {
			Log.error("wrong user id");
			return false;
		}
		String salt = userDoc.getString("salt");
		if (userDoc.getString("password").equals(
				StringFactory.encryptPassword(oldPwd, salt))) {
			String password = StringFactory.encryptPassword(newPwd, salt);
			userDoc.put("password", password);
			db.updateDoc(userDoc);
			return true;
		} else {
			Log.debug("Wrong old password");
			return false;
		}
	}

	/***
	 * 이름의 중복여부를 확인한다. (중복시 true)
	 * 
	 * @param name
	 * @return T - input name already exists/F - not exists
	 */
	public boolean nameDuplicationCheck(String name) {
		JSONArray result = JSONFactory.getData(JSONFactory.inputStreamToJson(db
				.getByView("_design/user", "search_by_name", name, false,
						false, false)));
		if (result.length() > 0)
			return true;
		return false;
	}
}
