package com.plusquare.sample.jerseytest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.glassfish.jersey.server.mvc.Viewable;

@Path("user")
public class API_User {
	@GET
	@Produces("text/html")
	public Viewable test() {
		return new Viewable("/test.mustache");
	}
}
