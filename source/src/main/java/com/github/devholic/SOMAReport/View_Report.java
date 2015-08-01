package com.github.devholic.SOMAReport;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.glassfish.jersey.server.mvc.Viewable;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Controller.ReportsController;
import com.github.devholic.SOMAReport.Database.DocumentUtil;
import com.github.devholic.SOMAReport.Utilities.MustacheHelper;

@Path("/report")
public class View_Report {

	@GET
	@Path("/list/{id}")
	@Produces("text/html")
	public Viewable report(@PathParam("id") String id) {
		ReportsController r = new ReportsController();
		JSONArray ja = r.getReportByProjectId(id);
		JSONObject jo = new JSONObject();
		jo.put("reportList", ja);
		DocumentUtil dutil = new DocumentUtil("somarecord");
		jo.put("pid", id);
		jo.put("pname", dutil.getDoc(id).getAsJsonObject().get("title")
				.getAsString());
		return new Viewable("/reportlist.mustache", MustacheHelper.toMap(jo));
	}

	@GET
	@Path("/{id}")
	@Produces("text/html")
	public Viewable reportDetail(@PathParam("id") String id) {
		DocumentUtil dutil = new DocumentUtil("somarecord");
		JSONObject jo = new JSONObject();
		JSONObject report = new JSONObject(dutil.getDoc(id).getAsJsonObject()
				.toString());
		JSONObject project = new JSONObject(dutil
				.getDoc(report.get("project").toString()).getAsJsonObject()
				.toString());
		System.out.println(report.toString());
		System.out.println(project.toString());
		jo.put("rid", id);
		jo.put("title", report.getJSONObject("report_info").get("date")
				.toString().replaceAll("-", ""));
		jo.put("pid", project.get("_id").toString());
		jo.put("pname", project.get("title").toString());
		return new Viewable("/reportdetail.mustache", MustacheHelper.toMap(jo));
	}
}
