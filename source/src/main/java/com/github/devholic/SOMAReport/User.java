package com.github.devholic.SOMAReport;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Session;
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
	@Path("/api/user")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response APIgetUserInfo(@Context Request request) {
		Session session = request.getSession();
		if (session.getAttribute("user_id")==null)
			return Response.status(412).build();
		String userId = session.getAttribute("user_id").toString();
		JSONObject userInfo = UserController.getUserInfo(userId);
		Log.info(userId);
		
		return Response.status(200).entity(userInfo.toString())
				.type(MediaType.APPLICATION_JSON).build();
	}
}
