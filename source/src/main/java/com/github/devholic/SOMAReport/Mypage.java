package com.github.devholic.SOMAReport;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Session;
import org.glassfish.jersey.server.mvc.Viewable;
import org.json.JSONObject;
import org.mortbay.log.Log;

import com.github.devholic.SOMAReport.Controller.UserController;
import com.github.devholic.SOMAReport.Utilities.MustacheHelper;

@Path("/")
public class Mypage {

	@Context
	UriInfo uri;

	@GET
	@Path("/mypage")
	public Response View_Mypage(@Context Request request) {
		Session session = request.getSession();
		UriBuilder builder = UriBuilder.fromUri(uri.getBaseUri());
		if (session.getAttribute("user_id") != null) {
			JSONObject data = new JSONObject();
			if (session.getAttribute("role").equals("admin")) {
				data.put("admin", true);
			}
			UserController user = new UserController();
			data.put("name", user.getUserName(session.getAttribute("user_id")
					.toString()));
			data.put("role", UserController.getRoleById(session.getAttribute(
					"user_id").toString()));
			data.put("user_id", session.getAttribute("user_id").toString());
			return Response
					.status(200)
					.entity(new Viewable("/new/new_mypage.mustache",
							MustacheHelper.toMap(data))).build();
		} else {
			builder.path("login");
			return Response.seeOther(builder.build()).build();
		}
	}

	@POST
	@Path("/mypage/password")
	public Response ChangePassword(@Context Request request,
			@FormParam("new") String newPw, @FormParam("old") String oldPw) {
		Log.info("test");
		Session session = request.getSession();
		UriBuilder builder = UriBuilder.fromUri(uri.getBaseUri());
		if (session.getAttribute("user_id") != null) {
			JSONObject data = new JSONObject();
			if (session.getAttribute("role").equals("admin")) {
				data.put("admin", true);
			}
			UserController user = new UserController();
			data.put("name", user.getUserName(session.getAttribute("user_id")
					.toString()));
			data.put("role", UserController.getRoleById(session.getAttribute(
					"user_id").toString()));
			data.put("user_id", session.getAttribute("user_id").toString());
			if (user.modifyPassword(session.getAttribute("user_id").toString(),
					oldPw, newPw)) {
				data.put("check", "비밀번호가 변경되었습니다.");
				return Response
						.status(200)
						.entity(new Viewable("/new/new_mypage.mustache",
								MustacheHelper.toMap(data))).build();
			} else {
				data.put("check", "비밀번호가 맞지 않습니다.");
				return Response
						.status(200)
						.entity(new Viewable("/new/new_mypage.mustache",
								MustacheHelper.toMap(data))).build();
			}
		} else {
			builder.path("login");
			return Response.seeOther(builder.build()).build();
		}
	}
}
