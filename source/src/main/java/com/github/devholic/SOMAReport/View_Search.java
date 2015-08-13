package com.github.devholic.SOMAReport;

import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.mvc.Viewable;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Controller.SearchController;
import com.github.devholic.SOMAReport.Utilities.MustacheHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Path("/")
public class View_Search {

//	@GET
//	@Path("/searchv")
//	public Response searchView(@QueryParam("q") String q) {
//		JSONObject jo = new JSONObject();
//		if (q != null) {
//			jo.put("q", q);
//			JSONArray result = new JSONArray();
//			SearchController s = new SearchController();
//			ArrayList<JsonObject> arr = s.searchReport_cloudantsearch(q);
//			for (JsonObject o : arr) {
//				JSONObject d = new JSONObject();
//				d.put("id", o.get("project").getAsString());
//				d.put("date", o.get("report_info").getAsJsonObject()
//						.get("date").getAsString());
//				d.put("topic",
//						o.get("report_details").getAsJsonObject().get("topic")
//								.getAsString());
//				JSONArray rAttendee = new JSONArray();
//				JsonArray attendee = o.get("attendee").getAsJsonArray();
//				for (int i = 0; i < attendee.size(); i++) {
//					rAttendee.put(attendee.get(i).getAsString());
//				}
//				d.put("attendee", rAttendee);
//				result.put(d);
//			}
//			jo.put("result", result);
//		}
//		System.out.println(jo);
//		return Response.ok(new Viewable("/search.mustache", MustacheHelper
//				.toMap(jo))).build();
//	}
}
