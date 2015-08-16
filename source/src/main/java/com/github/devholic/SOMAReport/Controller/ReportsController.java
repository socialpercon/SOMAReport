package com.github.devholic.SOMAReport.Controller;

import java.io.InputStream;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Utilities.JSONFactory;

public class ReportsController {

	private final Logger Log = Logger.getLogger(ReportsController.class);

	DatabaseController db = new DatabaseController();

	/**************************************************************************
	 * 프로젝트 아이디로 레포트 가져오기
	 * 
	 * @param projectId
	 * @return JSONArray
	 *************************************************************************/
	public JSONArray getReportByProjectId(String projectId) {
		JSONArray list = new JSONArray();
		try {
			InputStream is = db.getByView("_design/report", "all_by_project",
					new Object[] { projectId + " ", " " }, new Object[] {
							projectId, " " }, true, true, false);
			JSONArray a = JSONFactory
					.getData(JSONFactory.inputStreamToJson(is));
			for (int i = 0; i < a.length(); i++) {
				System.out.println(a.getJSONObject(i));
				JSONObject doc = a.getJSONObject(i).getJSONObject("doc");
				JSONObject reportInfo = new JSONObject();
				reportInfo.put("_id", doc.getString("_id"));
				reportInfo.put("date", doc.getJSONObject("report_info")
						.getString("date"));
				reportInfo.put("topic", doc.getJSONObject("report_details")
						.getString("topic"));
				reportInfo.put("attendee", doc.getJSONArray("attendee"));
				reportInfo.put("absentee", doc.getJSONArray("absentee"));
				list.put(reportInfo);
			}
		} catch (Exception e) {
			Log.error(e.getLocalizedMessage());
		}
		return list;
	}

	/**************************************************************************
	 * 레포트 아이디로 레포트 상세정보 가져오기
	 * 
	 * @param reportId
	 * @Return JSONObject
	 *************************************************************************/
	public JSONObject getDetailByReportId(String reportId) {
		JSONObject detail = new JSONObject();

		try {
			InputStream is = db.getDoc(reportId);
			detail = JSONFactory.inputStreamToJson(is);
		} catch (Exception e) {
			Log.error(e.getLocalizedMessage());
		}
		return detail;
	}

	/***************************************************************************
	 * 레포트 아이디로 레포트 상세정보 가져오기 _ URL Path GET
	 * 
	 * @param reportId
	 * @return
	 **************************************************************************/
	public JSONObject getReportDetailByReportId(
			@PathParam("reportId") String reportId) {
		JSONObject detail = new JSONObject();
		try {
			InputStream is = db.getDoc(reportId);
			detail = JSONFactory.inputStreamToJson(is);
		} catch (Exception e) {
			Log.error(e.getLocalizedMessage());
		}
		return detail;

	}

	/**************************************************************************
	 * 전체 레포트 리스트를 가져온다.
	 * 
	 * @return List<Reports>
	 *************************************************************************/
	public JSONArray getReportList() {
		JSONArray reportList = new JSONArray();
		try {
			InputStream is = db.getByView("_design/report", "all_by_project",
					true, true, false);
			JSONArray jo = JSONFactory.getData(JSONFactory
					.inputStreamToJson(is));
			for (int i = 0; i < jo.length(); i++) {
				reportList.put(jo.getJSONObject(i).get("doc"));
			}
		} catch (Exception e) {
			Log.error(e.getLocalizedMessage());
		}

		return reportList;
	}

	/****************************************************************
	 * 레포트를 입력한다
	 * 
	 * @param document
	 * @return
	 ***************************************************************/
	public String insertReport(JSONObject document) {
		String id = null;
		SearchController s = new SearchController();
		
		try {
			//inserting couch DB
			id = db.createDoc(document).get("_id").toString();
			//indexing elastic search
			s.elastic_index("report", document);
		} catch (Exception e) {
			Log.error(e.getLocalizedMessage());
		}
		return id;
	}

	public Response updateReport() {
		try {
			return Response.status(200).type(MediaType.APPLICATION_JSON)
					.entity("put : 200").build();
		} catch (Exception e) {
			Log.error(e.getLocalizedMessage());
		}
		return Response.status(500).type(MediaType.APPLICATION_JSON)
				.entity("put : 500").build();
	}

	/************************************************************************
	 * 레포트 아이디로 레포트를 삭제한다
	 * 
	 * @param reportId
	 * @return
	 ***********************************************************************/
	public boolean deleteReport(String reportId) {
		boolean result = false;

		try {
			JSONObject jo = JSONFactory.inputStreamToJson(db.getDoc(reportId));
			String rev = jo.getString("_rev");
			result = db.deleteDoc(reportId, rev);
			Log.debug("delete | report id = " + reportId + "\n");
		} catch (Exception e) {
			Log.error(e.getLocalizedMessage());
		}
		return result;
	}
}
