package com.github.devholic.SOMAReport;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.glassfish.jersey.server.mvc.Viewable;

@Path("/stage")
public class View_StageList {

	// Office
	@GET
	@Path("/list")
	@Produces("text/html")
	public Viewable officeReportList() {
		return new Viewable("/office_stagelist.mustache");
	}

}
