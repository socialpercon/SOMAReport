package com.github.devholic.SOMAReport;

import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

@Path("/")
public class Report {

	@Context
	UriInfo uri;

	private final static Logger Log = Logger.getLogger(Report.class);

	// API

	// View

}
