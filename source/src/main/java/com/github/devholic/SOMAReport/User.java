package com.github.devholic.SOMAReport;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/")
public class User {

	// API

	@PUT
	@Path("/api/user")
	public Response createUser() {
		return null;
	}
}
