package com.github.devholic.SOMAReport;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.mvc.Viewable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mortbay.log.Log;

import com.github.devholic.SOMAReport.Controller.DatabaseController;
import com.github.devholic.SOMAReport.Controller.ProjectsController;
import com.github.devholic.SOMAReport.Controller.StatisticsController;
import com.github.devholic.SOMAReport.Controller.UserController;
import com.github.devholic.SOMAReport.Utilities.JSONFactory;
import com.github.devholic.SOMAReport.Utilities.MustacheHelper;

@Path("/")
public class Console_Statistics {
	
	ProjectsController projectC = new ProjectsController();
	DatabaseController db = new DatabaseController();
	StatisticsController statC = new StatisticsController();
	
	@Context
	UriInfo uri;
	
	@GET
	@Path("/console/statistics")
	public Response View_Statistics () {
		JSONObject jo = new JSONObject();
		
		return Response
				.status(200)
				.entity(new Viewable("/console_statistics_project.mustache", 
						MustacheHelper.toMap(jo))).build();
	}
	
	@GET
	@Path("/console/statistics/project")
	public Response View_Statistics_Project () {
		JSONObject jo = new JSONObject();
		
		JSONArray stages = projectC.existingStage();
		jo.put("stages", stages);
		jo.put("selectedStage", "");
		return Response
				.status(200)
				.entity(new Viewable("/console_statistics_project.mustache",
						MustacheHelper.toMap(jo))).build();
	}
	
	@GET
	@Path("/console/statistics/project/{id}")
	public Response View_Statistics_Project_Selected (@PathParam("id") String id) {
		JSONObject jo = new JSONObject();
		
		JSONArray stages = projectC.existingStage();
		jo.put("stages", stages);
		String stageString = JSONFactory.inputStreamToJson(db.getDoc(id)).getString("stageString");
		jo.put("selectedStage", stageString);
		JSONArray mentors = statC.totalMentoringInfoByStage(UserController.ROLE_MENTOR, id);
		jo.put("mentors", mentors);
		JSONArray mentees = statC.totalMentoringInfoByStage(UserController.ROLE_MENTEE, id);
		jo.put("mentees", mentees);
		
		return Response
				.status(200)
				.entity(new Viewable("/console_statistics_project.mustache", 
						MustacheHelper.toMap(jo))).build();
	}
	
	@GET
	@Path("/console/statistics/perMonth")
	public Response View_Statistics_perMonths () {
		JSONObject jo = new JSONObject();
		
		return Response
				.status(200)
				.entity(new Viewable("/console_statistics_byMonth.mustache", 
						MustacheHelper.toMap(jo))).build();
	}
	
	@POST
	@Path("/console/statistics/perMonth")
	public Response View_Statistics_perMonth (@Context Request request, 
			@FormDataParam("year") String year, 
			@FormDataParam("month") String month) {
		JSONObject jo = new JSONObject();
		int years = Integer.parseUnsignedInt(year);
		int months = Integer.parseUnsignedInt(month);
		jo.put("year", year);
		jo.put("month", month);
		Log.info(years+" "+months);
		JSONArray mentors = statC.totalMentoringInfoByMonth(years, months, UserController.ROLE_MENTOR);
		jo.put("mentors", mentors);
		JSONArray mentees = statC.totalMentoringInfoByMonth(years, months, UserController.ROLE_MENTEE);
		jo.put("mentees", mentees);
		Log.info(jo.toString());
		return Response
				.status(200)
				.entity(new Viewable("/console_statistics_byMonth.mustache", 
						MustacheHelper.toMap(jo))).build();
	}
	
	@GET
	@Path("/console/statistics/perMonth/{year}/{month}")
	public Response View_Statistics_perMonth_data (@PathParam("year") String year, @PathParam("month")String month) {
		JSONObject jo = new JSONObject();
		int years = Integer.parseUnsignedInt(year);
		int months = Integer.parseUnsignedInt(month);
		
		JSONArray mentors = statC.totalMentoringInfoByMonth(years, months, UserController.ROLE_MENTOR);
		jo.put("mentors", mentors);
		JSONArray mentees = statC.totalMentoringInfoByMonth(years, months, UserController.ROLE_MENTEE);
		jo.put("mentees", mentees);
		return Response
				.status(200)
				.entity(new Viewable("/console_statistics_byMonth.mustache", 
						MustacheHelper.toMap(jo))).build();
	}
}
