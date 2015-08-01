package com.github.devholic.SOMAReport.Database;

import java.util.List;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import com.google.gson.JsonObject;

public class DocumentUtil {

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
				.key(account).includeDocs(false).reduce(false)
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

}
