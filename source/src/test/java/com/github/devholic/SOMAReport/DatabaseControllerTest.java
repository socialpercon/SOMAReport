// https://docs.google.com/spreadsheets/d/1AhNqZxIMev22Ik77Leh_Sm8eLk3JGKORaqlocAzi0K0/edit#gid=0
// 테스트 작성자 : 강성훈
// UserController 테스트

package com.github.devholic.SOMAReport;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.github.devholic.SOMAReport.Controller.DatabaseController;
import com.github.devholic.SOMAReport.Utilities.DocumentUtil;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatabaseControllerTest {

	private final Logger Log = Logger.getLogger(DocumentUtil.class);

	@Test
	public void Test_Document_CreateDelete() {
		DatabaseController db = new DatabaseController();
		JSONObject jo = new JSONObject();
		jo.put("type", "test");
		Map<String, Object> m = db.createDoc(jo);
		Log.info(m.toString());
		assertThat(m.get("_id").toString(), not(nullValue()));
		assertThat(m.get("_rev").toString(), not(nullValue()));
		assertTrue(db.deleteDoc(m.get("_id").toString(), m.get("_rev")
				.toString()));
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
		assertTrue(db.deleteDoc(m.get("_id").toString(), m.get("_rev")
				.toString()));
	}
}
