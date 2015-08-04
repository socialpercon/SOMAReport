package com.github.devholic.SOMAReport.Database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import com.github.devholic.SOMAReport.Controller.ProjectsController;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class DocumentUtil {

	private final Logger logger = Logger.getLogger(DocumentUtil .class);
	
	CloudantClient client;
	Database db;

	public DocumentUtil(String dbName) {
		client = new CloudantClient("http://somareport.cloudant.com",
				"somareport", "somasoma");
		db = client.database(dbName, true);
	}

	public JsonObject getUserDoc(String account) {
		// 멘토, 멘티
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
		JsonObject user = db.view("get_doc/user_by_name").key(name)
				.includeDocs(false).reduce(false).query(JsonObject.class)
				.get(0).getAsJsonObject();

		return user.get("value").getAsString();
	}

	public JsonObject getDoc(String id) {
		// _id를 통해 문서를 가져온다
		return db.find(JsonObject.class, id);
	}

	public String putDoc(JsonObject document) {
		// document를 db에 넣는다
		// _id값을 받아와 리턴
		Response response = db.save(document);
		return response.getId();
	}

	public Response deleteDoc(String id) {
		// _id에 해당하는 문서 삭제.
		JsonObject doc = getDoc(id);
		String rev = doc.get("_rev").getAsString();
		Response response = db.remove(id, rev);
		return response;
	}

	public Response updateDoc(JsonObject document) {
		// 이때 document는 db내에 존재하던 문서로 _id, _rev값을 갖고 있어야 함
		// 앞으로 생성될 update기능 세부사항에 따라 수정/확장 필요
		Response response = db.update(document);
		return response;
	}

	public int calWholeTime(JsonObject report_info) {

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

	public String getDate(JsonObject report_info) {
		String date = "";
		JsonArray start_time = report_info.get("start_time").getAsJsonArray();
		date = start_time.get(0).getAsString() + "_" + 
				start_time.get(1).getAsString() + "_" + start_time.get(2).getAsString();
		return date;
	}

	public String putReportDoc(JsonObject report_input) {
		logger.debug(report_input.toString());
		JsonObject report = new JsonObject();
		report.addProperty("type", "report");
		report.add("project", report_input.get("project"));
		JsonObject report_info = report_input.get("report_info")
				.getAsJsonObject();
		report_info.addProperty("date", getDate(report_info));
		int whole_time = calWholeTime(report_info);
		int total_time = whole_time - report_info.get("except_time").getAsInt();
		report_info.addProperty("whole_time", whole_time);
		report_info.addProperty("total_time", total_time);
		report.add("report_info", report_info);

		report.add("attendee", report_input.get("attendee"));
		report.add("absentee", report_input.get("absentee"));
		report.add("report_details", report_input.get("report_details"));
		report.add("report_attachments", report_input.get("report_attachments"));

		return putDoc(report);

	}
}
