// https://docs.google.com/spreadsheets/d/1AhNqZxIMev22Ik77Leh_Sm8eLk3JGKORaqlocAzi0K0/edit#gid=0
// 테스트 작성자 : 강성훈
// UserController 테스트

package com.github.devholic.SOMAReport;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.github.devholic.SOMAReport.Controller.DatabaseController;
import com.github.devholic.SOMAReport.Utilities.JSONFactory;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatabaseControllerTest {

	private final Logger Log = Logger.getLogger(DatabaseControllerTest.class);

	@Test
	public void Test_Document_CreateDelete() {
		DatabaseController db = new DatabaseController();
		JSONObject jo = new JSONObject();
		jo.put("type", "test");
		Map<String, Object> m = db.createDoc(jo);
		Log.info(m.toString());
		assertThat(m.get("_id").toString(), not(nullValue()));
		assertThat(m.get("_rev").toString(), not(nullValue()));
		assertTrue(db.deleteDoc(m.get("_id").toString(), m.get("_rev").toString()));
	}

	@Test
	public void Test_Document_CreateUpdateDelete() {
		DatabaseController db = new DatabaseController();
		JSONObject jo = new JSONObject();
		jo.put("type", "test");
		Map<String, Object> m = db.createDoc(jo);
		Log.info(m.toString());
		assertThat(m.get("_id").toString(), not(nullValue()));
		assertThat(m.get("_rev").toString(), not(nullValue()));
		jo.put("_id", m.get("_id").toString());
		jo.put("_rev", m.get("_rev").toString());
		jo.put("type2", "test2");
		m = db.updateDoc(jo);
		Log.info(m.toString());
		assertThat(m.get("_id").toString(), not(nullValue()));
		assertThat(m.get("_rev").toString(), not(nullValue()));
		assertTrue(db.deleteDoc(m.get("_id").toString(), m.get("_rev").toString()));
	}

	@Test
	public void Test_Document_Get() {
		DatabaseController db = new DatabaseController();
		String id = "9d898f7d5bfbf361939e1fafd5109dc2";
		InputStream is = db.getDoc(id);
		JSONObject jo = JSONFactory.inputStreamToJson(is);
		Log.info(jo.toString());
		assertEquals(id, jo.get("_id"));
	}

	@Test
	public void boodleboodle() {
		DatabaseController db = new DatabaseController();
		JSONArray res = db.getAllDocs();
		for (int i = 0; i < res.length(); i++) {
			JSONObject doc = res.getJSONObject(i);
			String fileName = doc.getString("_id");
			if (doc.getString("_id").contains("_design")) 
				fileName = fileName.substring(8, fileName.length());
			try {
				FileWriter fw = new FileWriter(doc.getString("_id").substring(8, doc.getString("_id").length()));
				fw.write(doc.toString());
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	@Test
	public void formatCheck() {
		DatabaseController db = new DatabaseController();
		JSONObject doc = JSONFactory.inputStreamToJson(db.getDoc("9d898f7d5bfbf361939e1fafd5109dc2"));
		Log.info("getDoc: "+doc.toString());
		doc = JSONFactory.inputStreamToJson(db.getByView("_design/user", "role", false,
				false, false));
		Log.info("getByView-No key"+doc.toString());
		doc = JSONFactory.inputStreamToJson(db.getByView("_design/user", "role", "mentee", false,
				false, false));
		Log.info("getByView-with key"+doc.toString());
		doc = JSONFactory.inputStreamToJson(db.getByView("_design/report", "all_by_project",
				new Object[] { "9d898f7d5bfbf361939e1fafd518a9a2" + " ", " " }, new Object[] {
						"9d898f7d5bfbf361939e1fafd518a9a2", " " }, false, true, false));
		Log.info("getByView-with start-end key"+doc.toString());
		doc = JSONFactory.inputStreamToJson(db.getByView(
								"_design/statistics",
								"total_time_by_project_date", new Object[] {
										"9d898f7d5bfbf361939e1fafd518a9a2", "20150801" },
								new Object[] { "9d898f7d5bfbf361939e1fafd518a9a2", "20150831" }, false,
								false, true, 1));
		Log.info(doc.toString());
	}
}
