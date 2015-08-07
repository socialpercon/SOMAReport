package com.github.devholic.SOMAReport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.mvc.Viewable;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Controller.ReportsController;
import com.github.devholic.SOMAReport.Database.DocumentUtil;
import com.github.devholic.SOMAReport.Utilities.MustacheHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@Path("/report")
public class View_Report {

	private final Logger logger = Logger.getLogger(View_Report.class);

	@GET
	@Path("/list/{id}")
	@Produces("text/html")
	public Viewable report(@PathParam("id") String id) {
		ReportsController r = new ReportsController();
		JSONArray ja = r.getReportByProjectId(id);
		logger.debug(ja.toString());
		JSONObject jo = new JSONObject();// /.
		jo.put("reportList", ja);
		DocumentUtil dutil = new DocumentUtil("");
		jo.put("pid", id);
		jo.put("pname", dutil.getDoc(id).getAsJsonObject().get("title")
				.getAsString());
		return new Viewable("/reportlist.mustache", MustacheHelper.toMap(jo));
	}

	@GET
	@Path("/{id}")
	@Produces("text/html")
	public Viewable reportDetail(@PathParam("id") String id) {
		DocumentUtil dutil = new DocumentUtil("");
		JSONObject jo = new JSONObject();
		JSONObject report = new JSONObject(dutil.getDoc(id).getAsJsonObject()
				.toString());
		JSONObject project = new JSONObject(dutil
				.getDoc(report.get("project").toString()).getAsJsonObject()
				.toString());
		jo.put("rid", id);
		jo.put("title", report.getJSONObject("report_info").get("date")
				.toString().replaceAll("-", ""));
		jo.put("pid", project.get("_id").toString());
		jo.put("pname", project.get("title").toString());
		jo.put("report", report);
		logger.debug(report.toString());
		return new Viewable("/reportdetail.mustache", MustacheHelper.toMap(jo));
	}

	@GET
	@Path("/write/{id}")
	@Produces("text/html")
	public Viewable writeReport(@PathParam("id") String id) {
		DocumentUtil dutil = new DocumentUtil("somarecord");
		JSONObject project = new JSONObject(dutil.getDoc(id).getAsJsonObject()
				.toString());
		JSONObject jo = new JSONObject();
		jo.put("pid", id);
		jo.put("pname", project.get("title").toString());
		jo.put("mentee", project.get("mentee"));
		return new Viewable("/reportwrite.mustache", MustacheHelper.toMap(jo));
	}

	@POST
	@Path("/write")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("text/html")
	public Response doWriteReport(@FormDataParam("projectid") String pid,
			@FormDataParam("place") String place,
			@FormDataParam("start") String start,
			@FormDataParam("attendee") List<String> attendee,
			@FormDataParam("attendeeReason") List<String> absenteeReason,
			@FormDataParam("end") String end,
			@FormDataParam("except") String except,
			@FormDataParam("topic") String topic,
			@FormDataParam("goal") String goal,
			@FormDataParam("issue") String issue,
			@FormDataParam("solution") String solution,
			@FormDataParam("plan") String plan,
			@FormDataParam("opinion") String opinion,
			@FormDataParam("etc") String etc,
			@FormDataParam("content") String content,
			@FormDataParam("file") InputStream is,
			@FormDataParam("file") FormDataContentDisposition formData)
			throws URISyntaxException {
		String fileLocation = formData.getFileName();
		try {
			saveFile(is, fileLocation);
		} catch (IOException e) {
			e.printStackTrace();
		}
		JsonArray attendeeArray = new JsonArray();
		JsonArray absenteeArray = new JsonArray();
		for (int a = 0; a < attendee.size(); a++) {
			if (absenteeReason.get(a).length() == 0) {
				attendeeArray.add(new JsonPrimitive(attendee.get(a)));
			} else {
				JsonObject abs = new JsonObject();
				abs.addProperty("id", attendee.get(a));
				abs.addProperty("reason", absenteeReason.get(a));
				absenteeArray.add(abs);
			}
		}
		JsonObject jo = new JsonObject();
		ReportsController r = new ReportsController();
		jo.addProperty("project", pid);
		JsonObject info = new JsonObject();
		info.addProperty("place", place);
		String[] startArr = start.split(" ");
		String[] endArr = end.split(":");
		String startDay = startArr[0].toString();
		info.addProperty("date", startDay);
		JsonArray sja = new JsonArray();
		sja.add(new JsonPrimitive(Integer.parseInt(startDay.substring(0, 4))));
		sja.add(new JsonPrimitive(Integer.parseInt(startDay.substring(4, 6))));
		sja.add(new JsonPrimitive(Integer.parseInt(startDay.substring(6, 8))));
		sja.add(new JsonPrimitive(Integer.parseInt(startArr[1].split(":")[0])));
		sja.add(new JsonPrimitive(Integer.parseInt(startArr[1].split(":")[1])));
		info.add("start_time", sja);
		sja = new JsonArray();
		sja.add(new JsonPrimitive(Integer.parseInt(startDay.substring(0, 4))));
		sja.add(new JsonPrimitive(Integer.parseInt(startDay.substring(4, 6))));
		sja.add(new JsonPrimitive(Integer.parseInt(startDay.substring(6, 8))));
		sja.add(new JsonPrimitive(Integer.parseInt(endArr[0])));
		sja.add(new JsonPrimitive(Integer.parseInt(endArr[1])));
		info.add("end_time", sja);
		jo.add("attendee", attendeeArray);
		jo.add("absentee", absenteeArray);
		info.addProperty("except_time", 0);
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
		return Response.seeOther(
				new URI("http://localhost:8080/report/list/" + pid)).build();
	}

	private void saveFile(InputStream is, String fileLocation)
			throws IOException {
		OutputStream os = new FileOutputStream(new File(fileLocation));
		byte[] buffer = new byte[256];
		int bytes = 0;
		while ((bytes = is.read(buffer)) != -1) {
			os.write(buffer, 0, bytes);
		}
		os.close();
	}
}
