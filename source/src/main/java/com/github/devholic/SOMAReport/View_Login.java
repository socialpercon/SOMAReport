package com.github.devholic.SOMAReport;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Session;
import org.glassfish.jersey.server.mvc.Viewable;

@Path("/")
public class View_Login {

	@GET
	@Path("/login")
	public Response loginView(@Context Request request)
			throws URISyntaxException {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null) {
			return Response.seeOther(
					new URI("http://localhost:8080/project/list")).build();
		} else {
			return Response.ok(new Viewable("/login.mustache")).build();
		}
	}

	@POST
	@Path("/login")
	public Response doLogin(@FormParam("email") String email,
			@FormParam("password") String password, @Context Request request)
			throws URISyntaxException {
		if (email != null && password != null) {
			Session session = request.getSession();
			if (session.getAttribute("user_id") != null) {
				// 로그인 처리
				return Response.seeOther(
						new URI("http://localhost:8080/project/list")).build();
			} else {
				// 로그인 되어있으므로
				
				//login logic
				
				return Response.seeOther(new URI("http://localhost:8080/"))
						.build();
			}
		} else {
			return Response.seeOther(new URI("http://localhost:8080/login"))
					.build();
		}
	}
}
