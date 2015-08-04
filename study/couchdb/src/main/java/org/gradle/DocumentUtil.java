package org.gradle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class DocumentUtil {

	CloudantClient client;
	Database db;

	public DocumentUtil(String dbName) {
		client = new CloudantClient("http://somareport.cloudant.com", "somareport", "somasoma");
		db = client.database(dbName, true);
	}

	JsonObject getUserDoc(String account) {
		// 멘토, 멘티
		// 계정 정보를 통해 해당 유저의 정보를 가져온다
		try {
			JsonObject document = db.view("get_doc/user_by_account").key(account).includeDocs(true).reduce(false)
							.query(JsonObject.class).get(0).getAsJsonObject();
			return document;
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	String getUserId(String name) {
		
		try {
		JsonObject user = db.view("get_doc/user_by_name").key(name).includeDocs(false).reduce(false)
				.query(JsonObject.class).get(0).getAsJsonObject();
		return user.get("value").getAsString();
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	JsonObject getDoc(String id) {
		// _id를 통해 문서를 가져온다
		return db.find(JsonObject.class, id);
	}

	String putDoc(JsonObject document) {
		// document를 db에 넣는다
		// _id값을 받아와 리턴
		Response response = db.save(document);
		return response.getId();
	}

	Response deleteDoc(String id) {
		// _id에 해당하는 문서 삭제.
		JsonObject doc = getDoc(id);
		String rev = doc.get("_rev").getAsString();
		Response response = db.remove(id, rev);
		return response;
	}

	Response updateDoc(JsonObject document) {
		// 이때 document는 db내에 존재하던 문서로 _id, _rev값을 갖고 있어야 함
		// 앞으로 생성될 update기능 세부사항에 따라 수정/확장 필요
		Response response = db.update(document);
		return response;
	}

	public int calTotalTime(JsonObject report_info) throws ParseException {

		JsonArray time = report_info.get("start_time").getAsJsonArray();
		String timeString = "";
		for (int i = 0; i < time.size(); i++) {
			if (time.get(i).getAsInt() < 10) {
				timeString += "0";
			}
			timeString += time.get(i).getAsString();
		}
		System.out.println("start time: " + timeString);
		String start = timeString;

		time = report_info.get("end_time").getAsJsonArray();
		timeString = "";
		for (int i = 0; i < time.size(); i++) {
			if (time.get(i).getAsInt() < 10) {
				timeString += "0";
			}
			timeString += time.get(i).getAsString();
		}
		System.out.println("end time: " + timeString);
		String end = timeString;

		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMddhhmm");
		Date startTime = dateformat.parse(start);
		Date endTime = dateformat.parse(end);

		System.out.println(startTime + " - " + endTime);
		long whole = (endTime.getTime() - startTime.getTime()) / (1000 * 60 * 60);
		System.out.println(whole);
		int wholeTime = (int) whole;
		int exceptTime = report_info.get("except_time").getAsInt();
		System.out.println(exceptTime);

		return (wholeTime - exceptTime);
	}

}
