package com.github.devholic.SOMAReport.Controller;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.docx4j.Docx4J;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
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
			System.out.println(a);
			for (int i = 0; i < a.length(); i++) {
				JSONObject doc = a.getJSONObject(i).getJSONObject("doc");
				JSONObject reportInfo = new JSONObject();
				reportInfo.put("_id", doc.getString("_id"));
				reportInfo.put("date", doc.getJSONObject("report_info")
						.getString("date"));
				reportInfo.put("topic", doc.getJSONObject("report_details")
						.getString("topic"));
				reportInfo.put("attendee", doc.getJSONArray("attendee"));
				if(doc.has("absentee")) reportInfo.put("absentee", doc.getJSONArray("absentee"));
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
	 * @param JSONObject document	(이때 document의 형식은 sample-json/report_inputForm.json과 동일해야 함.)
	 * @return String 	(_id of inserted report)
	 ***************************************************************/
	public String insertReport(JSONObject document) {
		String id = null;
		SearchController s = new SearchController();
		
		try {
			//inserting couch DB
			JSONObject reportDoc = new JSONObject();
			reportDoc.put("type", "report");
			reportDoc.put("project", document.get("project"));
			
			JSONObject reportInfo = document.getJSONObject("report_info");
			reportInfo.put("date", reportInfo.getString("date"));
			reportInfo.put("mentoring_num", numOfReports(document.getString("project")) + 1);
			int whole = calWholeTime(reportInfo);
			reportInfo.put("whole_time", whole);
			int total = whole - reportInfo.getInt("except_time");
			reportInfo.put("total_time", total);
			reportDoc.put("report_info", reportInfo);
			
			reportDoc.put("attendee", document.get("attendee"));
			reportDoc.put("absentee", document.get("absentee"));
			reportDoc.put("report_details", document.get("report_details"));
			reportDoc.put("report_attachments", document.get("report_attachments"));
			id = db.createDoc(reportDoc).get("_id").toString();
			reportDoc.put("_id", id);
			Log.info(reportDoc);
			
			//indexing elastic search
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
	 * @param String projectId
	 * @return int  (number of report)
	 */
	public int numOfReports (String projectId) {
		JSONArray reports = JSONFactory.getData(JSONFactory.inputStreamToJson(db.getByView("_design/report", "all_by_project", projectId, false, false, false)));
		return reports.length();
	}
	
	/***
	 * 입력받은 멘토링 정보로부터 멘토링이 진행된 전체 시간을 구한다.
	 * report_inputForm 내의 report_info를 입력받는다.
	 * 
	 * @param JSONObject document (report_info)
	 * @return int (total_time)
	 */
	private int calWholeTime (JSONObject document) {
		String startTime = document.getString("start_time");
		String endTime = document.getString("end_time");
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmm");
		try {
			Date start = format.parse(startTime);
			Date end = format.parse(endTime);
			return (int) ((end.getTime() - start.getTime()) / (1000 * 60 * 60));
		} catch (ParseException e) {
			Log.error(e.getLocalizedMessage());
			return 0;
		}
	}
	
	/**
	 * 입력받은 id에 해당하는 레포트 문서에 멘토, 멘티의 이름을 포함해 가져온다.
	 * view (/report/list, /report/{id} 등)를 위함
	 * 
	 * @param reportId
	 * @return JSONObject (report document)
	 */
	public JSONObject getReportWithNames (String reportId) {
		JSONObject reportDoc = JSONFactory.inputStreamToJson(db.getDoc(reportId));
		
		JSONArray attendee = reportDoc.getJSONArray("attendee");
		for (int i=0; i<attendee.length(); i++) {
			attendee.getJSONObject(i).put("name", UserController.getUserName(attendee.getJSONObject(i).getString("id")));
		}
		reportDoc.put("attendee", attendee);

		if (reportDoc.has("absentee")) {
			JSONArray absentee = reportDoc.getJSONArray("absentee");
			for (int i=0; i<absentee.length(); i++) {
				absentee.getJSONObject(i).put("name", UserController.getUserName(attendee.getJSONObject(i).getString("id")));
			}
			reportDoc.put("absentee", absentee);
		}
		return reportDoc;
	}
	
	/************************************************
	 * Word File 로 export하는 예
	 * @throws Exception
	 ***********************************************/
	public void renderDocx_mentoringReport() throws Exception {
		System.out.println("renderDocx_mentoringReport excuted....");
		WordprocessingMLPackage wordPackage = WordprocessingMLPackage
				.load(new java.io.File("mentoringReport.docx"));
		VariablePrepare.prepare(wordPackage);
		MainDocumentPart documentPart = wordPackage.getMainDocumentPart();
		
		
		HashMap<String, String> mappings = new HashMap<String, String>();
		
		mappings.put("division1", "O");
		mappings.put("division2", "");
		mappings.put("division3", "");
		mappings.put("division4", "");
		
		mappings.put("projectName","SOMAReport");
		mappings.put("term","2015-07-01 ~ 2015.08-28");
		mappings.put("main_mento","김태완");
		mappings.put("sub_mento","");
		mappings.put("section","웹");
		mappings.put("class","6기");
		mappings.put("stage","1단계 1차");
		mappings.put("field","웹");
		
		mappings.put("mentee1","민종현");
		mappings.put("mentee2","강성훈");
		mappings.put("mentee3","이재연");
		mappings.put("mentee4","");
		
		mappings.put("absent_reason1","집안일");
		mappings.put("absent_reason2","");
		mappings.put("absent_reason3","");
		mappings.put("absent_reason4","");
		
		mappings.put("times","3");
		mappings.put("date","2015-08-18");
		mappings.put("location","아람 6-2");
		mappings.put("start_time","19:00-22:00");
		mappings.put("except_time","");
		
		mappings.put("topic","개발 스코프 정의");
		mappings.put("purpose","개발 스코프를 정의한다");
		mappings.put("propel","개발을 하는것");
		mappings.put("solution","구글링을 해보기");
		mappings.put("plan","다음주까지 개발 완성");
		mappings.put("mento_opinion","잘하고 잇군요");
		mappings.put("etc","안녕 이건 기타란이야");
		mappings.put("content","안녕 이건 내용란이야 너는 무슨 내용이니");

		documentPart.variableReplace(mappings);
		wordPackage.save(new File("mentoringReport_filled.docx"));
		
		
//		OutputStream os = new java.io.FileOutputStream(new File(
//				"mentoringReport_filled.pdf"));
//		Docx4J.toPDF(wordPackage, os);
	}
}
