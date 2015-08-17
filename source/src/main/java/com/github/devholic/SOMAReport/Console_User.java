package com.github.devholic.SOMAReport;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

public class Console_User {

	private final static Logger Log = Logger.getLogger(Console_User.class);

	@Context
	UriInfo uri;

}
