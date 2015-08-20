package com.github.devholic.SOMAReport;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.mvc.Viewable;
import org.json.JSONArray;
import org.json.JSONObject;

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
	
	@GET
	@Path("/console/statistics")
	public Response Vies_Statistics () {
		
		return Response
				.status(200)
				.build();
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
				.entity(new Viewable("/console_statistics.mustache",
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
				.entity(new Viewable("/console_statistics.mustache", 
						MustacheHelper.toMap(jo))).build();
	}
	
	@GET
	@Path("/console/statistics/per_month")
	public Response Vies_Statistics_perMonth () {
		
		return Response
				.status(200)
				.build();
	}

}
