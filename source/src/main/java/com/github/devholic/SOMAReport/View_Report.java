package com.github.devholic.SOMAReport;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.glassfish.jersey.server.mvc.Viewable;

@Path("/report")
public class View_Report {

	// Mento / Mentee
	@GET
	@Path("/list/{id}")
	@Produces("text/html")
	public Viewable reportList() {
		return new Viewable("/reportlist.mustache");
	}

	// Mento / Mentee
	@GET
	@Path("/{id}")
	@Produces("text/html")
	public Viewable report() {
		return new Viewable("/reportdetail.mustache");
	}

	// Mento / Mentee
	@GET
	@Path("/write/{id}")
	@Produces("text/html")
	public Viewable reportWrite() {
		return new Viewable("/reportwrite.mustache");
	}

}
