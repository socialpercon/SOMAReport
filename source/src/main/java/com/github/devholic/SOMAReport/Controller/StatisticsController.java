package com.github.devholic.SOMAReport.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.hk2.utilities.reflection.Logger;
import org.json.JSONObject;

@Path("/statistics")
public class StatisticsController {

	Logger logger = Logger.getLogger();
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") 
	public Response getUserList(){
		
		JSONObject jo1 = new JSONObject();
		
		try{
			
			logger.debug("GET statistics");
			
			jo1.put("name", "name");
			jo1.put("age", "age");
			jo1.put("sex", "sex");
			jo1.put("email", "email");

		}catch(Exception e){
			e.printStackTrace();
		}
		
		return Response.status(200).type(MediaType.APPLICATION_JSON).entity(jo1)
				.build();
	}
}
