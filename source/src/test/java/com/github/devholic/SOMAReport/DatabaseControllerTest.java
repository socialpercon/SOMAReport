// https://docs.google.com/spreadsheets/d/1AhNqZxIMev22Ik77Leh_Sm8eLk3JGKORaqlocAzi0K0/edit#gid=0
// 테스트 작성자 : 강성훈
// UserController 테스트

package com.github.devholic.SOMAReport;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;
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
	public void testGetDoc() {
		DatabaseController db = new DatabaseController();
		String id = "9d898f7d5bfbf361939e1fafd5002b4c";
		BufferedReader is = new BufferedReader(new InputStreamReader(db.getDoc(id)));
		String str, doc = "";
		try {
			while ((str = is.readLine()) != null) {
				doc += str;
			}
		} catch (IOException e) {
			Log.error(e.getLocalizedMessage());
			fail("fail..");
		}
		JSONObject jo = new JSONObject(doc);
		Log.info(jo.toString());
		assertEquals(id, jo.get("_id"));
	}

	@Test
	// json파일을 db에 넣음 
	// docs 폴더에 넣고 싶은 json 파일을 넣고 실행
	public void testCreateDoc() {
		DatabaseController db = new DatabaseController();
		try {
			File inputDocs = new File("docs");
			File[] list = inputDocs.listFiles();
			for (File f : list) {
				if (f.isFile() && !f.getName().endsWith(".json")) {
					JSONTokener tokener = new JSONTokener(new FileReader(f));
					JSONObject jo = new JSONObject(tokener);
					if (!jo.has("_id") && !jo.has("_rev")) 	{
						Map<String, Object> m = db.createDoc(jo);
						Log.info(m.toString());
						jo.put("_id", m.get("_id"));
						jo.put("_rev", m.get("_rev"));
						FileWriter fw;
						try {
							fw = new FileWriter(f.getName());
							fw.write(jo.toString());
							fw.close();
						} catch (IOException e) {
							Log.error(e.getLocalizedMessage());
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			Log.error(e.getLocalizedMessage());
		} 
	}
	
	@Test
	public void testGetIdByName() {
		DatabaseController db = new DatabaseController();
		assertEquals("9d898f7d5bfbf361939e1fafd50470e3", db.getIdbyName("김멘토"));
		assertEquals("9d898f7d5bfbf361939e1fafd5031d8e", db.getIdbyName("이멘티"));
	}
}
