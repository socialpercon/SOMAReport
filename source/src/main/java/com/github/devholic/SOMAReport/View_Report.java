package com.github.devholic.SOMAReport;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.glassfish.jersey.server.mvc.Viewable;

@Path("/report")
public class View_Report {

	// Mento / Mentee
	@GET
	@Path("/list/{id}")
	@Produces("text/html")
	public Viewable report(@PathParam("id") String id) {
		
		return new Viewable("/reportdetail.mustache");
	}

}
