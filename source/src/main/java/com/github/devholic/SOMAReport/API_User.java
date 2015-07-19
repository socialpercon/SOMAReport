package com.github.devholic.SOMAReport;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/user")
public class API_User {

	@POST
	@Path("/login")
	public Response login() {
		return Response.status(200).type(MediaType.APPLICATION_JSON).entity("")
				.build();
	}

	@POST
	@Path("/register")
	public Response register() {
		return Response.status(200).type(MediaType.APPLICATION_JSON).entity("")
				.build();
	}

}
