package com.github.devholic.SOMAReport;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.mortbay.log.Log;

import com.github.devholic.SOMAReport.Controller.UserController;

@Path("/")
public class User {

	// API

	@PUT
	@Path("/api/user")
	public Response createUser() {
		return null;
	}
	
	@GET
	@Path("/api/user/{id}")
	public Response APIgetUserInfo(@PathParam("id") String userId) {
		JSONObject userInfo = UserController.getUserInfo(userId);
		
		return Response
				.status(200)
				.type(MediaType.APPLICATION_JSON)
				.entity(userInfo)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods",
						"GET, POST, DELETE, PUT")
				.build();
	}
}
