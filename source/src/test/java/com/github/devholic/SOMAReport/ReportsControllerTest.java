package com.github.devholic.SOMAReport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;

import com.github.devholic.SOMAReport.Controller.DatabaseController;
import com.github.devholic.SOMAReport.Controller.ReportsController;
import com.github.devholic.SOMAReport.Utilities.JSONFactory;

public class ReportsControllerTest {

	private final Logger Log = Logger.getLogger(ReportsController.class);

	ReportsController rCtrl = new ReportsController();
	DatabaseController dCtrl = new DatabaseController();

	@Test
	public void testGetReportByProjectId() {
		JSONArray reports = rCtrl.getReportByProjectId("9d898f7d5bfbf361939e1fafd5041f4b");
		for (int i = 0; i < reports.length(); i++)
			assertEquals("9d898f7d5bfbf361939e1fafd5041f4b", reports.getJSONObject(i).get("project"));
	}

	@Test
	public void testInsertReportAndDelete() {
		try {
			File inputDocs = new File("docs/report.json");
			JSONTokener tokener = new JSONTokener(new FileReader(inputDocs));
			JSONObject jo = new JSONObject(tokener);
			if (!jo.has("_id") && !jo.has("_rev")) {
				String id = rCtrl.insertReport(jo);
				JSONObject inserted = JSONFactory.inputStreamToJson(dCtrl.getDoc(id));
				Log.info(inserted);
				assertTrue(inserted != null);
				assertTrue(dCtrl.deleteDoc(inserted.getString("_id"), inserted.getString("_rev")));
			}
		} catch (FileNotFoundException e) {
			Log.error(e.getLocalizedMessage());
		} 
	}
	
	@Test
	public void testGetDetailByReportId() {
		JSONObject report = rCtrl.getReportDetailByReportId("9d898f7d5bfbf361939e1fafd505dac9");
		Log.info(report);
		assertTrue(report.has("report_info"));
		assertTrue(report.has("project"));
		assertTrue(report.has("report_details"));
		assertTrue(report.has("attendee"));
	}

	@Test
	public void testGetReportList() {
		JSONArray list = rCtrl.getReportList();
		Log.info(list.length()+" reports :"+list);
		assertTrue(list.length() != 0);
		for (int i=0; i<list.length(); i++) 
			assertEquals(list.getJSONObject(i).get("type"), "report");
	}
	
}
