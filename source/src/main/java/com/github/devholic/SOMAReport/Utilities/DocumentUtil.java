package com.github.devholic.SOMAReport.Utilities;

import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class DocumentUtil {

	private final Logger logger = Logger.getLogger(DocumentUtil.class);

	CloudantClient client;
	Database db;

	public DocumentUtil(String dbname) {
		try {
			// get config value
			Properties prop = new Properties();
			FileInputStream fileInput = new FileInputStream("config.xml");
			prop.loadFromXML(fileInput);

			client = new CloudantClient(prop.getProperty("couchdb_url"),
					prop.getProperty("couchdb_id"),
					prop.getProperty("couchdb_password"));
			if (dbname == null || dbname.equals("")) {
				db = client.database(prop.getProperty("database_name"), true);
			} else {
				db = client.database(dbname, true);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JsonObject getUserDoc(String account) {
		// 계정 정보를 통해 해당 유저의 정보를 가져온다
		List<JsonObject> result = db.view("get_doc/user_by_account")
				.key(account).includeDocs(true).reduce(false)
				.query(JsonObject.class);
		if (result.size() == 0) {
			return null;
		} else {
			return result.get(0);
		}
	}

	public String getUserId(String name) {
		// 해당 이름을 가진 user 문서의 _id값을 가져온다
		List<JsonObject> user = db.view("get_doc/user_by_name").key(name)
				.includeDocs(false).reduce(false).query(JsonObject.class);
		if (user.size() == 0)
			return null;
		else
			return user.get(0).get("value").getAsString();
	}

	public JsonObject getDoc(String id) {
		// _id를 통해 문서를 가져온다
		return db.find(JsonObject.class, id);
	}

	public String putDoc(JsonObject document) {
		// document를 db에 넣는다
		// 생성_id값을 받아와 리턴
		Response response = db.save(document);
		return response.getId();
	}

	public Response deleteDoc(String id) {
		// _id에 해당하는 문서 삭제.
		JsonObject doc = getDoc(id);
		if (doc.isJsonNull())
			return null;
		String rev = doc.get("_rev").getAsString();
		Response response = db.remove(id, rev);
		return response;
	}

	public Response updateDoc(JsonObject document) {
		// 이때 document는 db내에 존재하던 문서로 _id, _rev값을 갖고 있어야 함
		// 주로 getDoc() 등으로 직접 불러온 뒤 사용할 것
		Response response = db.update(document);
		return response;
	}

	public int calWholeTime(JsonObject report_info) {
		// report_info 내의 시작/끝 시간을 통해 멘토링이 진행된 전체 시간을 구한다
		int whole_time = 0;
		JsonArray time = report_info.get("start_time").getAsJsonArray();
		String timeString = "";
		for (int i = 0; i < time.size(); i++) {
			if (time.get(i).getAsInt() < 10) {
				timeString += "0";
			}
			timeString += time.get(i).getAsString();
		}
		logger.debug("start time: " + timeString);
		String start = timeString;

		time = report_info.get("end_time").getAsJsonArray();
		timeString = "";
		for (int i = 0; i < time.size(); i++) {
			if (time.get(i).getAsInt() < 10) {
				timeString += "0";
			}
			timeString += time.get(i).getAsString();
		}
		String end = timeString;

		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMddhhmm");

		try {
			Date startTime = dateformat.parse(start);
			Date endTime = dateformat.parse(end);
			long whole = (endTime.getTime() - startTime.getTime())
					/ (1000 * 60 * 60);
			whole_time = (int) whole;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return whole_time;
	}

	public String putReportDoc(JsonObject report_input) {
		// 입력된 report_input에 기본 정보를 더해 데이터베이스에 PUT
		logger.debug(report_input.toString());
		JsonObject report = new JsonObject();
		report.addProperty("type", "report");
		report.add("project", report_input.get("project"));
		JsonObject report_info = report_input.get("report_info")
				.getAsJsonObject();
		report_info.addProperty("date", report_input.get("report_info")
				.getAsJsonObject().get("date").getAsString());
		int whole_time = calWholeTime(report_info);
		int total_time = whole_time - report_info.get("except_time").getAsInt();
		report_info.addProperty("whole_time", whole_time);
		report_info.addProperty("total_time", total_time);
		report_info.addProperty("mentoring_num", (1+numberOfMentoring(report_input.get("project").getAsString())));
		report.add("report_info", report_info);
		report.add("attendee", report_input.get("attendee"));
		report.add("absentee", report_input.get("absentee"));
		report.add("report_details", report_input.get("report_details"));
		report.add("report_attachments", report_input.get("report_attachments"));

		return putDoc(report);

	}

	public List<String> getUUID(int count) {
		// UUID를 count개 생성
		return client.uuids(count);
	}

	public JsonArray getUserAuthInfo(String account) {
		// 사용자의 account로부터 인증에 필요한 정보 (_id, password, salt)를 가져온다
		List<JsonObject> userInfo = db.view("get_doc/auth_by_account")
				.key(account).includeDocs(false).reduce(false)
				.query(JsonObject.class);
		if (userInfo.size() == 0)
			return null;
		else
			return userInfo.get(0).get("value").getAsJsonArray();
	}

	public String userAuthentication(String account, String password) {
		// log-in method
		// account로 인증정보를 받아와 해싱하여 password의 일치 여부를 확인
		JsonArray user = getUserAuthInfo(account);
		if (user == null) {
			logger.debug("wrong account");
			return null;
		} else {
			String inputPwd = encryptPassword(password, user.get(2)
					.getAsString());
			if (inputPwd.equals(user.get(1).getAsString())) {
				return user.get(0).getAsString();
			} else {
				logger.debug("wrong password");
				return null;
			}
		}
	}

	private static String encryptPassword(String password, String salt) {
		// password와 salt를 함께 hash
		String encryptedPassword = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salt.getBytes());
			byte[] bytes = md.digest(password.getBytes());
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16)
						.substring(1));
			}
			encryptedPassword = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return encryptedPassword;
	}
	
	public int numberOfMentoring (String project_id) {
		// 현재까지 진행된 멘토링 횟수를 리턴한다
		List<JsonObject> numOfMentoring = db.view("project_view/all_report")
				.startKey(new Object[] { project_id + " ", " " })
				.endKey(new Object[] { project_id, " " }).descending(true)
				.includeDocs(false).reduce(true).groupLevel(1).query(JsonObject.class);
		if (numOfMentoring.isEmpty())  {
			logger.debug("numberOfMentoring:: no report docs in this project");
			return 0;
		}
		else {
			return numOfMentoring.get(0).get("value").getAsInt();
		}

	}
}
