package com.github.devholic.SOMAReport.Controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Database.DocumentUtil;
import com.github.devholic.SOMAReport.Database.ReferenceUtil;
import com.github.devholic.SOMAReport.Model.Reports;
import com.google.gson.JsonObject;

@Path("/reports")
public class ReportsController {

	ReferenceUtil ref_util = new ReferenceUtil("somarecord");
	DocumentUtil doc_util = new DocumentUtil("somarecord");

	/**************************************************************************
	 * 프로젝트 아이디로 레포트 가져오기
	 * 
	 * @param projectId
	 * @return
	 *************************************************************************/
	public JSONArray getReportByProjectId(String projectId) {
		List<JsonObject> reports_list = new ArrayList<JsonObject>();
		JSONArray ja = new JSONArray();
		try {
			reports_list = ref_util.getReports(projectId);
			for (int i = 0; i < reports_list.size(); i++) {
				JSONObject jo = new JSONObject();
				jo.put("id", reports_list.get(i).get("_id").getAsString());
				jo.put("reportTitle", reports_list.get(i).get("report_info")
						.getAsJsonObject().get("date").getAsString()
						.replaceAll("-", ""));
				jo.put("reportTopic", reports_list.get(i).get("report_details")
						.getAsJsonObject().get("topic").getAsString());
				ja.put(jo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(ja.toString());
		return ja;
	}

	/**************************************************************************
	 * 레포트 아이디로 레포트 상세정보 가져오기
	 *************************************************************************/
	public JsonObject getDetailByReportId(String reportId) {
		JsonObject detail = new JsonObject();

		try {
			detail = doc_util.getDoc(reportId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return detail;
	}

	/***************************************************************************
	 * 레포트 아이디로 레포트 상세정보 가져오기 _ URL Path GET
	 * 
	 * @param reportId
	 * @return
	 **************************************************************************/
	@GET
	@Path("/{reportId}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JsonObject getReportDetailByReportId(
			@PathParam("reportId") String reportId) {
		JsonObject detail = new JsonObject();
		try {
			detail = doc_util.getDoc(reportId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return detail;

	}

	/**************************************************************************
	 * 레포트 리스트를 가져온다.
	 * 
	 * @return List<Reports>
	 *************************************************************************/
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public List<JsonObject> getReportList() {

		List<JsonObject> report_list = new ArrayList<JsonObject>();

		try {

			report_list = ref_util.getAllReports();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return report_list;
	}

	/****************************************************************
	 * 레포트를 입력한다
	 * 
	 * @param document
	 * @return
	 ***************************************************************/
	public boolean insertReport(JsonObject document) {
		boolean result = false;
		try {
			String id = doc_util.putReportDoc(document);
			System.out.println("inserted | report id = " + id);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}


	@PUT
	public Response updateReport() {
		try {
			return Response.status(200).type(MediaType.APPLICATION_JSON)
					.entity("put : 200").build();
		} catch (Exception e) {
			e.printStackTrace();
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
	@DELETE
	public boolean deleteReport(String reportId) {
		boolean result = false;

		try {
			System.out.println("delete | report id = " + reportId + "\n");
			doc_util.deleteDoc(reportId);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
