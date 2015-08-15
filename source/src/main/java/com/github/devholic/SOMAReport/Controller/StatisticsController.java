package com.github.devholic.SOMAReport.Controller;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONObject;

@Path("/statistics")
public class StatisticsController {

	//Log4j setting
	private final Logger logger = Logger.getLogger(StatisticsController .class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public Response getUserList(){
		
		JSONObject jo1 = new JSONObject();
		
		try{
			logger.info("info level");
			logger.warn("warn level");
			logger.debug("debug level");
			logger.fatal("fatal level");
			
			jo1.put("name", "name");
			jo1.put("age", "age");
			jo1.put("sex", "sex");
			jo1.put("email", "email");

		}catch(Exception e){
			e.printStackTrace();
		}
		
		return Response.status(200).type(MediaType.APPLICATION_JSON)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.entity(jo1)
				.build();
	}
	
	@PUT
	public Response updateStatistics(){
		try{
			return Response.status(200).type(MediaType.APPLICATION_JSON)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.entity("put : 200").build();
		}catch(Exception e){
			e.printStackTrace();
		}
		return Response.status(500).type(MediaType.APPLICATION_JSON)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.entity("put : 500").build();
	}
	
	@DELETE
	public Response deleteStatistics(){
		try{
			return Response.status(200).type(MediaType.APPLICATION_JSON)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.entity("delete : 200").build();
		}catch(Exception e){
			e.printStackTrace();
		}
		return Response.status(500).type(MediaType.APPLICATION_JSON)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.entity("delete : 500").build();
	}
}
