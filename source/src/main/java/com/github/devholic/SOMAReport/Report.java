package com.github.devholic.SOMAReport;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Session;
import org.glassfish.jersey.server.mvc.Viewable;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Utilities.DocumentUtil;
import com.github.devholic.SOMAReport.Utilities.MustacheHelper;
import com.github.devholic.SOMAReport.Utilities.ReferenceUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Path("/")
public class Report {

	@Context
	UriInfo uri;

	private final static Logger Log = Logger.getLogger(Report.class);

	// API

	// View
	@GET
	@Path("/report/{id}")
	@Produces("text/html")
	public Response View_ReportDetail(@Context Request request,
			@PathParam("id") String id) throws URISyntaxException {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null) { // 로그인되어있는지 체크
			DocumentUtil dutil = new DocumentUtil("");
			ReferenceUtil rutil = new ReferenceUtil("");
			JSONObject jo = new JSONObject();
			JSONObject report = rutil.getReportWithNames(id);
			JsonObject project_raw = dutil.getDoc(
					report.get("project").toString()).getAsJsonObject();
			JSONObject project = new JSONObject(dutil
					.getDoc(report.get("project").toString()).getAsJsonObject()
					.toString());
			boolean auth = false;
			if (project.getString("mentor").equals(
					session.getAttribute("user_id").toString())) {
				jo.put("isMentor", true);
				auth = true;
				report.getJSONObject("report_details").put("opinion-public",
						"true");
			} else {
				JsonArray menteeList = project_raw.get("mentee")
						.getAsJsonArray();
				for (int i = 0; i < menteeList.size(); i++) {
					if (menteeList.get(i).getAsString()
							.equals(session.getAttribute("user_id").toString())) {
						auth = true;
						break;
					}
				}
			}
			if (!auth) {
				return Response.seeOther(
						new URI("http://localhost:8080/project/list")).build();
			} else {
				jo.put("rid", id);
				jo.put("title", report.getJSONObject("report_info").get("date")
						.toString().replaceAll("-", ""));
				jo.put("pid", project.get("_id").toString());
				jo.put("pname", project.get("title").toString());
				jo.put("report", report);
				return Response.ok(
						new Viewable("/reportdetail.mustache", MustacheHelper
								.toMap(jo))).build();
			}
		} else {
			return Response.seeOther(new URI("http://localhost:8080/login"))
					.build();
		}
	}
}
