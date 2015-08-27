package com.github.devholic.SOMAReport.Controller;

import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.docx4j.XmlUtils;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.io.SaveToZipFile;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Utilities.JSONFactory;

@SuppressWarnings({ "restriction", "deprecation" })
public class ReportsController {

	private final Logger Log = Logger.getLogger(ReportsController.class);

	DatabaseController db = new DatabaseController();

	/**************************************************************************
	 * 프로젝트 아이디로 레포트 가져오기
	 * 
	 * @param projectId
	 * @return JSONArray [{_id, project, projectTitle, date, topic, attendee,
	 *         absentee}]
	 *************************************************************************/
	public JSONArray getReportByProjectId(String projectId) {
		JSONArray list = new JSONArray();
		try {
			InputStream is = db.getByView("_design/report", "all_by_project",
					new Object[] { projectId + " ", " " }, new Object[] {
							projectId, " " }, true, true, false);
			JSONArray a = JSONFactory
					.getData(JSONFactory.inputStreamToJson(is));
			Log.debug("ohohohohoh" + a.toString());
			for (int i = 0; i < a.length(); i++) {
				JSONObject doc = a.getJSONObject(i).getJSONObject("doc");
				JSONObject reportInfo = new JSONObject();
				reportInfo.put("_id", doc.getString("_id"));
				reportInfo.put("project", doc.get("project"));
				String projectTitle = JSONFactory.inputStreamToJson(
						db.getDoc(projectId)).getString("title");
				reportInfo.put("projectTitle", projectTitle);
				reportInfo.put("date", doc.getJSONObject("report_info")
						.getString("date"));
				reportInfo.put("topic", doc.getJSONObject("report_details")
						.getString("topic"));
				reportInfo.put("attendee", doc.getJSONArray("attendee"));
				if (doc.has("confirmed"))
					reportInfo.put("confirmed", doc.get("confirmed"));
				if (doc.has("absentee"))
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
			return null;
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
	 * @param JSONObject
	 *            document (이때 document의 형식은 sample-json/report_inputForm.json과
	 *            동일해야 함.)
	 * @return String (_id of inserted report)
	 ***************************************************************/
	public String insertReport(JSONObject document) {
		String id = null;
		SearchController s = new SearchController();

		try {
			// inserting couch DB
			JSONObject reportDoc = new JSONObject();
			reportDoc.put("type", "report");
			reportDoc.put("project", document.get("project"));

			JSONObject reportInfo = document.getJSONObject("report_info");
			reportInfo.put("date", reportInfo.getString("date"));
			reportInfo.put("mentoring_num",
					numOfReports(document.getString("project")) + 1);
			int whole = calWholeTime(reportInfo) / 60;
			reportInfo.put("whole_time", whole);
			int total = (whole - reportInfo.getInt("except_time") / 60);
			reportInfo.put("total_time", total);
			reportDoc.put("report_info", reportInfo);

			reportDoc.put("attendee", document.get("attendee"));
			if (document.has("absentee"))
				reportDoc.put("absentee", document.get("absentee"));
			reportDoc.put("report_details", document.get("report_details"));
			reportDoc.put("report_attachments",
					document.get("report_attachments"));
			id = db.createDoc(reportDoc).get("_id").toString();
			reportDoc.put("_id", id);
			Log.info(reportDoc);

			// indexing elastic search
			s.elastic_index("report", reportDoc);
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

	/***
	 * 해당 프로젝트 내의 레포트 개수를 불러온다
	 * 
	 * @param String
	 *            projectId
	 * @return int (number of report)
	 */
	public int numOfReports(String projectId) {
		JSONArray reports = JSONFactory.getData(JSONFactory
				.inputStreamToJson(db.getByView("_design/report",
						"all_by_project", projectId, false, false, false)));
		return reports.length();
	}

	/***
	 * 입력받은 멘토링 정보로부터 멘토링이 진행된 전체 시간을 구한다. report_inputForm 내의 report_info를
	 * 입력받는다.
	 * 
	 * @param JSONObject
	 *            document (report_info)
	 * @return int (total_time)
	 */
	private int calWholeTime(JSONObject document) {
		String startTime = document.getString("start_time");
		String endTime = document.getString("end_time");

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmm");
		try {
			Date start = format.parse(startTime);
			Date end = format.parse(endTime);
			return (int) ((end.getTime() - start.getTime()) / (1000 * 60));
		} catch (ParseException e) {
			Log.error(e.getLocalizedMessage());
			return 0;
		}
	}

	/**
	 * 입력받은 id에 해당하는 레포트 문서에 멘토, 멘티의 이름을 포함해 가져온다. view (/report/list,
	 * /report/{id} 등)를 위함
	 * 
	 * @param reportId
	 * @return JSONObject (report document)
	 */
	public JSONObject getReportWithNames(String reportId) {
		JSONObject reportDoc = JSONFactory.inputStreamToJson(db
				.getDoc(reportId));
		UserController user = new UserController();
		JSONArray attendee = reportDoc.getJSONArray("attendee");
		for (int i = 0; i < attendee.length(); i++) {
			attendee.getJSONObject(i)
					.put("name",
							user.getUserName(attendee.getJSONObject(i)
									.getString("id")));
		}
		reportDoc.put("attendee", attendee);

		if (reportDoc.has("absentee")) {
			JSONArray absentee = reportDoc.getJSONArray("absentee");
			for (int i = 0; i < absentee.length(); i++) {
				absentee.getJSONObject(i).put(
						"name",
						user.getUserName(absentee.getJSONObject(i).getString(
								"id")));
			}
			reportDoc.put("absentee", absentee);
		}
		return reportDoc;
	}

	/************************************************
	 * Word File 로 export하는 예
	 * 
	 * @throws Exception
	 ***********************************************/

	@SuppressWarnings({})
	public boolean renderDocx_mentoringReport(String reportId) {
		try {
			File cached = new File("cache/" + reportId + ".docx");
			if (cached.exists()) {
				cached.delete();
			}
			DatabaseController db = new DatabaseController();
			JSONObject data = JSONFactory
					.inputStreamToJson(db.getDoc(reportId));
			JSONObject project = JSONFactory.inputStreamToJson(db.getDoc(data
					.getString("project")));
			UserController user = new UserController();
			WordprocessingMLPackage wordPackage = WordprocessingMLPackage
					.load(new java.io.File("mentoringReport.docx"));
			VariablePrepare.prepare(wordPackage);
			MainDocumentPart documentPart = wordPackage.getMainDocumentPart();
			HashMap<String, String> mappings = new HashMap<String, String>();
			mappings.put("division1", "O");
			mappings.put("division2", "");
			mappings.put("division3", "");
			mappings.put("division4", "");
			mappings.put("projectName", project.getString("title"));
			mappings.put("term", "2015-07-01 ~ 2015-08-31");
			mappings.put("main_mento",
					user.getUserName(project.getString("mentor")));
			mappings.put("sub_mento", "");
			mappings.put("section", project.getString("field"));
			mappings.put("class", project.getJSONArray("stage").getInt(0) + "기");
			mappings.put("stage", project.getJSONArray("stage").getInt(1)
					+ "단계 " + project.getJSONArray("stage").getInt(2) + "차");
			mappings.put("field", project.getString("field"));
			int i = 1;
			for (int j = 0; j < data.getJSONArray("absentee").length(); j++) {
				mappings.put(
						"mentee" + Integer.toString(i),
						user.getUserName(data.getJSONArray("absentee")
								.getJSONObject(j).getString("id")));
				mappings.put("absent_reason" + Integer.toString(i),
						data.getJSONArray("absentee").getJSONObject(j)
								.getString("reason"));
				i++;
			}
			for (int j = 0; j < data.getJSONArray("attendee").length(); j++) {
				mappings.put(
						"mentee" + Integer.toString(i),
						user.getUserName(data.getJSONArray("attendee")
								.getJSONObject(j).getString("id")));
				mappings.put("absent_reason" + Integer.toString(i), "");
				i++;
			}
			for (int j = i; j <= 4; j++) {
				mappings.put("mentee" + Integer.toString(i), "");
				mappings.put("absent_reason" + Integer.toString(i), "");
				i++;
			}
			mappings.put(
					"times",
					Integer.toString(data.getJSONObject("report_info").getInt(
							"whole_time")));
			mappings.put("date",
					data.getJSONObject("report_info").getString("date"));
			mappings.put("location", data.getJSONObject("report_info")
					.getString("place"));
			String start = data.getJSONObject("report_info").getString(
					"start_time");
			String end = data.getJSONObject("report_info")
					.getString("end_time");
			mappings.put(
					"start_time",
					start.substring(8, 10) + ":" + start.substring(10, 12)
							+ "~" + end.substring(8, 10) + ":"
							+ end.substring(10, 12));
			mappings.put("except_time", data.getJSONObject("report_info")
					.getString("except_time"));
			mappings.put("topic", data.getJSONObject("report_details")
					.getString("topic"));
			mappings.put("purpose", data.getJSONObject("report_details")
					.getString("goal"));
			mappings.put("propel", data.getJSONObject("report_details")
					.getString("issue"));
			mappings.put("solution", data.getJSONObject("report_details")
					.getString("solution"));
			mappings.put("plan", data.getJSONObject("report_details")
					.getString("plan"));
			mappings.put("mento_opinion", data.getJSONObject("report_details")
					.getString("opinion"));
			mappings.put("etc",
					data.getJSONObject("report_details").getString("etc"));
			Log.info(data.getJSONObject("report_details").toString());
			mappings.put("naeyong", data.getJSONObject("report_details")
					.getString("content"));
			String xml = XmlUtils.marshaltoString(
					documentPart.getJaxbElement(), true);
			Object obj = XmlUtils.unmarshallFromTemplate(xml, mappings);
			documentPart.setJaxbElement((Document) obj);
			SaveToZipFile saver = new SaveToZipFile(wordPackage);
			saver.save(new File("cache/" + reportId + ".docx"));
			return true;
		} catch (JAXBException e) {
			Log.error(e.getMessage());
			return false;
		} catch (Docx4JException e) {
			Log.error(e.getMessage());
			return false;
		} catch (Exception e) {
			Log.error(e.getCause().getMessage());
			return false;
		}
	}

	/***
	 * 레포트의 확정여부를 가져온다. 확정이 된 상태일경우 "true", 아닐 경우 "false"
	 * 
	 * @param reportId
	 * @return String
	 */
	public String isReportConfirmed(String reportId) {
		JSONObject doc = JSONFactory.inputStreamToJson(db.getDoc(reportId));
		if (doc.has("confirmed"))
			return doc.getString("confirmed");
		else {
			doc.put("confirmed", "false");
			db.updateDoc(doc);
			return doc.getString("confirmed");
		}
	}

	/***
	 * 해당 stage(기수, 단계, 차수)에 속하는 레포트를 전부 가져온다.
	 * 
	 * @param stageId
	 * @return JSONArray [{_id, project, projectTitle, date, topic, attendee,
	 *         absentee}]
	 */
	public JSONArray getReportByStage(String stageId) {
		JSONArray allReports = new JSONArray();
		ProjectsController projectC = new ProjectsController();
		JSONArray projects = projectC.projectsInStageInfo(stageId);
		for (int i = 0; i < projects.length(); i++) {
			JSONArray reports = getReportByProjectId(projects.getJSONObject(i)
					.getString("_id"));
			for (int j = 0; j < reports.length(); j++) {
				allReports.put(reports.getJSONObject(j));
			}
		}
		return allReports;
	}

	/***
	 * 해당 사용자가 속한 레포트 문서 중 현재 작성중인 (확정되지 않은) 레포트의 정보를 가져온다.
	 * 
	 * @param userId
	 * @return JSONArray [{reportId, reportTitle, reportTopic, attendee[]} ]
	 */
	public JSONArray getUnconfirmedReports(String userId) {
		JSONArray unConfirmed = new JSONArray();
		JSONArray allList = JSONFactory.getData(JSONFactory
				.inputStreamToJson(db.getByView("_design/report",
						"report_by_user", new Object[] { userId + " ", "" },
						new Object[] { userId, "" }, true, true, false)));

		for (int i = 0; i < allList.length(); i++) {
			JSONObject doc = allList.getJSONObject(i).getJSONObject("doc");
			if (!doc.has("confirmed")) {
				JSONObject docu = new JSONObject();
				docu.put("_id", doc.get("_id"));
				docu.put("date", doc.getJSONObject("report_info").get("date"));
				docu.put("topic",
						doc.getJSONObject("report_details").get("topic"));
				docu.put("attendee", doc.get("attendee"));
				docu.put("project", doc.get("project"));
				JSONObject project = JSONFactory.inputStreamToJson(db
						.getDoc(doc.getString("project")));
				docu.put("projectTitle", project.get("title"));
				unConfirmed.put(docu);
			}
		}
		return unConfirmed;
	}
}
