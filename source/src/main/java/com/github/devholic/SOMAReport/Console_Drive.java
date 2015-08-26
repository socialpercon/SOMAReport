package com.github.devholic.SOMAReport;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Session;
import org.glassfish.jersey.server.mvc.Viewable;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Controller.DatabaseController;
import com.github.devholic.SOMAReport.Controller.DriveController;
import com.github.devholic.SOMAReport.Controller.UserController;
import com.github.devholic.SOMAReport.Controller.DriveController.Token;
import com.github.devholic.SOMAReport.Utilities.JSONFactory;
import com.github.devholic.SOMAReport.Utilities.MustacheHelper;

@Path("/")
public class Console_Drive {

	private Logger Log = Logger.getLogger(Console_Drive.class);

	@Context
	UriInfo uri;

	// API
	@GET
	@Path("/console/drive/auth/add")
	public Response consoleAddAccount(@Context Request request)
			throws URISyntaxException {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null
				&& session.getAttribute("role").equals("admin")) {
			DatabaseController db = new DatabaseController();
			JSONArray ja = JSONFactory
					.getData(JSONFactory.inputStreamToJson(db.getByView(
							"_design/drive", "account", false, false, false)));
			DriveController drive = new DriveController();
			UriBuilder builder = UriBuilder.fromUri(uri.getBaseUri());
			builder.path("console/drive/auth/add/");
			String url = drive.createCredentialFlow1(
					Integer.toString(ja.length()), builder.build().toString());

			Log.info(url);
			return Response.seeOther(new URI(url)).build();
		} else {
			return Response.status(401)
					.entity(new Viewable("/new/new_login.mustache")).build();
		}
	}

	@GET
	@Path("/console/drive/auth/add/{storage}")
	public Response consoleAddAccountSuccess(@Context Request request,
			@PathParam("storage") String storage,
			@QueryParam("code") String code) throws URISyntaxException {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null
				&& session.getAttribute("role").equals("admin")) {
			UriBuilder builder = UriBuilder.fromUri(uri.getBaseUri());
			builder.path("console/drive/auth/add/" + storage);
			DriveController drive = new DriveController();
			if (drive.createCredentialFlow2(code, storage, builder.build()
					.toString())) {
				DatabaseController db = new DatabaseController();
				JSONObject jo = new JSONObject();
				jo.put("type", "drive_account");
				jo.put("credential", storage + ".json");
				db.createDoc(jo);
			}
			builder = UriBuilder.fromUri(uri.getBaseUri());
			builder.path("console/drive");
			return Response.seeOther(builder.build()).build();
		} else {
			return Response.status(401)
					.entity(new Viewable("/new/new_login.mustache")).build();
		}
	}

	// View
	@GET
	@Path("/console/drive")
	public Response consoleDrive(@Context Request request)
			throws URISyntaxException, IOException {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null
				&& session.getAttribute("role").equals("admin")) {
			JSONObject render = new JSONObject();
			DatabaseController db = new DatabaseController();
			JSONArray ja = JSONFactory
					.getData(JSONFactory.inputStreamToJson(db.getByView(
							"_design/drive", "account", false, false, false)));
			JSONArray data = new JSONArray();
			DriveController drive = new DriveController();
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				Token token = drive.getToken(jo.getString("value"));
				com.google.api.services.drive.Drive googleDrive = drive
						.buildService(drive.getCredential(
								token.getAccessToken(), token.getRefreshToken()));
				JSONObject finalData = new JSONObject();
				finalData.put("storage", i);
				int calc = (int) (((double) drive.getUsedQuota(googleDrive) / (double) drive
						.getTotalQuota(googleDrive)) * 100);
				finalData.put("usage", calc + 98);
				Log.info(finalData.toString());
				data.put(finalData);
			}
			UserController user = new UserController();
			render.put("name", user.getUserName(session.getAttribute("user_id")
					.toString()));
			render.put("role", UserController.getRoleById(session.getAttribute(
					"user_id").toString()));
			render.put("user_id", session.getAttribute("user_id").toString());
			render.put("driveList", data);
			return Response
					.status(200)
					.entity(new Viewable("/new/new_console_drive.mustache",
							MustacheHelper.toMap(render))).build();
		} else {
			return Response.status(401)
					.entity(new Viewable("/new/new_login.mustache")).build();
		}
	}
}
