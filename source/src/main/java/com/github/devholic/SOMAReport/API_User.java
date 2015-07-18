package com.github.devholic.SOMAReport;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/user")
public class API_User {

	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public Response login() {
		return Response.status(200).entity("").build();
	}

	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public Response register() {
		return Response.status(200).entity("").build();
	}

}
