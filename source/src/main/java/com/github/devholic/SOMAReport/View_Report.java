package com.github.devholic.SOMAReport;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.glassfish.jersey.server.mvc.Viewable;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Controller.ReportsController;
import com.github.devholic.SOMAReport.Utilities.MustacheHelper;

@Path("/report")
public class View_Report {

	// Mento / Mentee
	@GET
	@Path("/list/{id}")
	@Produces("text/html")
	public Viewable report(@PathParam("id") String id) {
		ReportsController r = new ReportsController();
		JSONArray ja = r.getReportByProjectId(id);
		JSONObject jo = new JSONObject();
		jo.put("reportList", ja);
		return new Viewable("/reportlist.mustache", MustacheHelper.toMap(jo));
	}
}
