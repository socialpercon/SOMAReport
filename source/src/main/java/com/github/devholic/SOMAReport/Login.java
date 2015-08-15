package com.github.devholic.SOMAReport;

import java.io.CharConversionException;
import java.net.URISyntaxException;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Session;
import org.glassfish.jersey.server.mvc.Viewable;

import com.github.devholic.SOMAReport.Controller.UserController;

@Path("/")
public class Login {

	private final static Logger Log = Logger.getLogger(Login.class);

	@Context
	UriInfo uri;

	// API
	@POST
	@Path("/api/login")
	public Response API_Login(@Context Request request,
			@FormParam("email") String email,
			@FormParam("password") String password) {
		if (email != null && password != null) { // 아이디, 비밀번호가 제대로 들어온 경우
			Session session = request.getSession();
			if (session.getAttribute("user_id") != null) { // 세션에 user_id가 있는경우
				return Response.status(412)
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
						.build(); // 사전조건 실패
			} else {
				String result = UserController.login(email, password); // 로그인
				if (result != null) { // 결과값이 null이 아닌경우
					session.setAttribute("user_id", result); // 세션에 user_id를 설정
					return Response.status(200)
							.header("Access-Control-Allow-Origin", "*")
							.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
							.build(); // 200 리턴
				} else {
					// 로그인에 실패한 경우 400 리턴
					return Response.status(400)
							.header("Access-Control-Allow-Origin", "*")
							.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
							.build();
				}
			}
		} else {
			// 잘못된 요청이므로 400 리턴
			return Response.status(400)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
	}

	@GET
	@Path("/api/logout")
	public Response API_Logout(@Context Request request) {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null) {
			// 세션에 user_id가 있는 경우 user_id를 삭제한다.
			session.removeAttribute("user_id");
			return Response.status(200)
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
		return Response.status(412)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
	}

	// View
	@GET
	@Path("/login")
	public Response View_Login(@Context Request request)
			throws URISyntaxException {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null) {
			UriBuilder builder = UriBuilder.fromUri(uri.getBaseUri());
			builder.path("project/list");
			return Response.seeOther(builder.build())
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		} else {
			return Response.status(400).entity(new Viewable("/login.mustache"))
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
	}

	@POST
	@Path("/login")
	public Response View_Login(@Context Request request,
			@FormParam("email") String email,
			@FormParam("password") String password) throws URISyntaxException,
			CharConversionException {
		if (email != null && password != null) { // 아이디, 비밀번호가 제대로 들어온 경우
			UriBuilder builder = UriBuilder.fromUri(uri.getBaseUri());
			builder.path("project/list");
			Session session = request.getSession();
			if (session.getAttribute("user_id") != null) { // 세션에 user_id가 있는경우
				return Response.seeOther(builder.build())
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").build();
			} else {
				String result = UserController.login(email, password); // 로그인
				if (result != null) { // 결과값이 null이 아닌경우
					session.setAttribute("user_id", result); // 세션에 user_id를 설정
					return Response.seeOther(builder.build())
							.header("Access-Control-Allow-Origin", "*")
							.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
							.build();
				} else {
					// 로그인에 실패한 경우 400 리턴
					return Response.status(400)
							.entity(new Viewable("/login.mustache"))
							.header("Access-Control-Allow-Origin", "*")
							.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
							.build();
				}
			}
		} else {
			// 잘못된 요청이므로 400 리턴
			return Response.status(400).entity(new Viewable("/login.mustache"))
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
					.build();
		}
	}

	@GET
	@Path("/logout")
	public Response View_Logout(@Context Request request)
			throws URISyntaxException {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null) {
			session.removeAttribute("user_id");
		}
		UriBuilder builder = UriBuilder.fromUri(uri.getBaseUri());
		builder.path("project/list");
		return Response.seeOther(builder.build())
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
				.build();
	}
}
