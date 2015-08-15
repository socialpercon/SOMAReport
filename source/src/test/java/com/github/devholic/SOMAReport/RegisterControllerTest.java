package com.github.devholic.SOMAReport;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.devholic.SOMAReport.Controller.DatabaseController;
import com.github.devholic.SOMAReport.Controller.RegisterController;

public class RegisterControllerTest {

	static RegisterController rc;
	static DatabaseController dc;

	private final static Logger Log = Logger
			.getLogger(RegisterControllerTest.class);

	@BeforeClass
	public static void testRegisterController() {
		try {
			rc = new RegisterController(new FileInputStream(
					"register_example.xlsx"));
			dc = new DatabaseController();
			assertThat(rc, not(nullValue()));
		} catch (FileNotFoundException e) {
			Log.error(e.getLocalizedMessage());
			fail("Not yet implemented");
		}
	}

	@Test
	public void testRegisterMentor() {
		JSONObject registered = rc.registerMentor();
		JSONArray a = registered.getJSONArray("inserted");
		for (int i = 0; i < a.length(); i++) {
			Log.info(a.get(i).toString());
			String id = a.getJSONObject(i).getString("_id").toString();

			BufferedReader is = new BufferedReader(new InputStreamReader(
					dc.getDoc(id)));
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
			assertEquals(a.getJSONObject(i).get("name"), jo.getString("name"));
			assertTrue(dc.deleteDoc(jo.getString("_id"), jo.getString("_rev")));
		}
	}

	@Test
	public void testRegisterMentee() {
		JSONArray registered = rc.registerMentee();
		for (int i = 0; i < registered.length(); i++) {
			Log.info(registered.get(i).toString());
			String id = registered.getJSONObject(i).getString("_id").toString();
			BufferedReader is = new BufferedReader(new InputStreamReader(
					dc.getDoc(id)));
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
			assertEquals(registered.getJSONObject(i).get("name").toString(),
					jo.getString("name"));
			assertTrue(dc.deleteDoc(jo.getString("_id"), jo.getString("_rev")));
		}
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testRegisterProject() {
		JSONObject registered = rc.registerProject();
		Iterator keys = registered.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			Log.info("projects in sheet " + key);
			JSONArray arr = registered.getJSONArray(key);
			for (int i = 0; i < arr.length(); i++) {
				Log.info(arr.get(i).toString());
				String id = arr.getJSONObject(i).getString("_id").toString();
				BufferedReader is = new BufferedReader(new InputStreamReader(
						dc.getDoc(id)));
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
				assertEquals(arr.getJSONObject(i).toString(), jo.toString());
				assertTrue(dc.deleteDoc(jo.getString("_id"),
						jo.getString("_rev")));

			}
		}
	}

}
