package com.github.devholic.SOMAReport.Controller;

import java.io.InputStream;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Utilities.JSONFactory;

@Path("/reports")
public class ReportsController {

	private final Logger logger = Logger.getLogger(ReportsController.class);

	DatabaseController dbCtrl = new DatabaseController();

	/**************************************************************************
	 * 프로젝트 아이디로 레포트 가져오기
	 * 
	 * @param projectId
	 * @return JSONArray
	 *************************************************************************/
	public JSONArray getReportByProjectId(String projectId) {
		JSONArray list = new JSONArray();

		try {
			InputStream is = dbCtrl.getByView("_design/report", "all_by_project", 
					new Object[] { projectId + " ", " " }, new Object[] { projectId, " " }, true, true);
			JSONArray a = JSONFactory.getData(JSONFactory.inputStreamToJson(is));
			for (int i = 0; i < a.length(); i++)
				list.put(a.getJSONObject(i).get("doc"));
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
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
			InputStream is = dbCtrl.getDoc(reportId);
			detail = JSONFactory.inputStreamToJson(is);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
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
	public JSONObject getReportDetailByReportId(@PathParam("reportId") String reportId) {
		JSONObject detail = new JSONObject();
		try {
			InputStream is = dbCtrl.getDoc(reportId);
			detail = JSONFactory.inputStreamToJson(is);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}
		return detail;

	}

	/**************************************************************************
	 * 전체 레포트 리스트를 가져온다.
	 * 
	 * @return List<Reports>
	 *************************************************************************/
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public JSONArray getReportList() {
		JSONArray reportList = new JSONArray();
		try {
			InputStream is = dbCtrl.getByView("_design/report", "all_by_project", true, true);
			JSONArray jo = JSONFactory.getData(JSONFactory.inputStreamToJson(is));
			for (int i = 0; i < jo.length(); i++) {
				reportList.put(jo.getJSONObject(i).get("doc"));
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
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
		try {
			id = dbCtrl.createDoc(document).get("_id").toString();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}
		return id;
	}

	@PUT
	public Response updateReport() {
		try {
			return Response.status(200).type(MediaType.APPLICATION_JSON).entity("put : 200").build();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}
		return Response.status(500).type(MediaType.APPLICATION_JSON).entity("put : 500").build();
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
			JSONObject jo = JSONFactory.inputStreamToJson(dbCtrl.getDoc(reportId));
			String rev = jo.getString("_rev");
			result = dbCtrl.deleteDoc(reportId, rev);
			logger.debug("delete | report id = " + reportId + "\n");
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}
		return result;
	}
}
