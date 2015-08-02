package com.github.devholic.SOMAReport;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.glassfish.jersey.server.mvc.Viewable;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Controller.ReportsController;
import com.github.devholic.SOMAReport.Database.DocumentUtil;
import com.github.devholic.SOMAReport.Utilities.MustacheHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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

	@GET
	@Path("/write/{id}")
	@Produces("text/html")
	public Viewable writeReport(@PathParam("id") String id) {
		JSONObject jo = new JSONObject();
		jo.put("pid", id);
		return new Viewable("/reportwrite.mustache", MustacheHelper.toMap(jo));
	}

	@POST
	@Path("/write")
	@Produces("text/html")
	public Viewable doWriteReport(@FormParam("projectid") String pid,
			@FormParam("place") String place, @FormParam("topic") String topic,
			@FormParam("goal") String goal, @FormParam("issue") String issue,
			@FormParam("solution") String solution,
			@FormParam("plan") String plan,
			@FormParam("opinion") String opinion, @FormParam("etc") String etc) {
		JsonObject jo = new JsonObject();
		ReportsController r = new ReportsController();
		jo.addProperty("project", pid);
		JsonObject info = new JsonObject();
		info.addProperty("place", place);
		jo.add("report_info", info);
		JsonObject details = new JsonObject();
		details.addProperty("topic", topic);
		details.addProperty("goal", goal);
		details.addProperty("issue", issue);
		details.addProperty("solution", solution);
		details.addProperty("plan", plan);
		details.addProperty("opinion", opinion);
		details.addProperty("etc", etc);
		jo.add("report_details", details);
		r.insertReport(jo);
		return new Viewable("/reportdetail.mustache");
	}
}
