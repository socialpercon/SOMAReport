package com.github.devholic.SOMAReport;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.jersey.server.mvc.Viewable;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Controller.DatabaseController;
import com.github.devholic.SOMAReport.Controller.ProjectsController;
import com.github.devholic.SOMAReport.Controller.StatisticsController;
import com.github.devholic.SOMAReport.Utilities.JSONFactory;
import com.github.devholic.SOMAReport.Utilities.MustacheHelper;

@Path("/")
public class Console_Statistics {

	private final static Logger Log = Logger
			.getLogger(Console_Statistics.class);

	ProjectsController projectC = new ProjectsController();
	DatabaseController db = new DatabaseController();
	StatisticsController statC = new StatisticsController();

	@Context
	UriInfo uri;

	@GET
	@Path("/console/statistics")
	public Response View_Statistics() {
		JSONObject jo = new JSONObject();

		return Response
				.status(200)
				.entity(new Viewable("/new/new_console_statistics.mustache",
						MustacheHelper.toMap(jo))).build();
	}

	@GET
	@Path("/console/statistics/project")
	public Response View_Statistics_Project() {
		JSONObject jo = new JSONObject();

		JSONArray stages = projectC.existingStage();
		jo.put("stages", stages);
		jo.put("selectedStage", "");
		return Response
				.status(200)
				.entity(new Viewable(
						"/new/new_console_statistics_project.mustache",
						MustacheHelper.toMap(jo))).build();
	}

	@GET
	@Path("/console/statistics/project/{id}")
	public Response View_Statistics_Project_Selected(@PathParam("id") String id) {
		JSONObject jo = new JSONObject();

		JSONArray stages = projectC.existingStage();
		jo.put("stages", stages);
		String stageString = JSONFactory.inputStreamToJson(db.getDoc(id))
				.getString("stageString");
		jo.put("selectedStage", stageString);
		JSONArray mentors = statC.totalMentoringInfoByStage("mentor", id);
		jo.put("mentors", mentors);
		JSONArray mentees = statC.totalMentoringInfoByStage("mentee", id);
		jo.put("mentees", mentees);

		return Response
				.status(200)
				.entity(new Viewable(
						"/new/new_console_statistics_project.mustache",
						MustacheHelper.toMap(jo))).build();
	}

	@GET
	@Path("/console/statistics/perMonth")
	public Response View_Statistics_perMonths(@Context Request request,
			@QueryParam("year") String year, @QueryParam("month") String month) {
		if ((year != null) && (month != null)) {
			JSONObject jo = new JSONObject();
			int years = Integer.parseInt(year);
			int months = Integer.parseInt(month);
			jo.put("year", year);
			jo.put("month", month);
			Log.info(years + " " + months);
			JSONArray mentors = statC.totalMentoringInfoByMonth(years, months,
					"mentor");
			jo.put("mentors", mentors);
			JSONArray mentees = statC.totalMentoringInfoByMonth(years, months,
					"mentee");
			jo.put("mentees", mentees);
			Log.info(jo.toString());
			return Response
					.status(200)
					.entity(new Viewable(
							"/new/new_console_statistics_month.mustache",
							MustacheHelper.toMap(jo))).build();
		} else {
			JSONObject jo = new JSONObject();
			return Response
					.status(200)
					.entity(new Viewable(
							"/new/new_console_statistics_month.mustache",
							MustacheHelper.toMap(jo))).build();
		}
	}

}
