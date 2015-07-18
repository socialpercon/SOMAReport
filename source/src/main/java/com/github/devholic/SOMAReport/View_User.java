package com.github.devholic.SOMAReport;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.glassfish.jersey.server.mvc.Viewable;

public class View_User {

	@GET
	@Path("/login")
	@Produces("text/html")
	public Viewable test() {
		return new Viewable("/login.mustache");
	}

}
