package com.github.devholic.SOMAReport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Session;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.mvc.Viewable;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Controller.DatabaseController;
import com.github.devholic.SOMAReport.Controller.DriveController;
import com.github.devholic.SOMAReport.Controller.ProjectsController;
import com.github.devholic.SOMAReport.Controller.UserController;
import com.github.devholic.SOMAReport.Utilities.FileFactory;
import com.github.devholic.SOMAReport.Utilities.JSONFactory;
import com.github.devholic.SOMAReport.Utilities.MustacheHelper;
import com.google.common.io.Files;

@Path("/")
public class Drive {

	private final static Logger Log = Logger.getLogger(Drive.class);

	// API
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/drive/file/upload/{id}")
	public Response uploadFiles(@Context Request request,
			@PathParam("id") String id, @FormDataParam("file") InputStream is,
			@FormDataParam("file") FormDataContentDisposition formData) {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null) {
			try {
				DriveController drive = new DriveController();
				String fileId = drive.uploadFileToProject(id, FileFactory.stream2file(is),
						new String(formData.getFileName()
								.getBytes("iso-8859-1"), "utf-8"));
				JSONObject data = new JSONObject();
				data.put("code", 1);
				data.put("msg", "success");
				data.put("driveid", "success");
				data.put("originalname", new String(formData.getFileName()
						.getBytes("iso-8859-1"), "utf-8"));
				data.put("fileId", fileId);
				return Response.status(200).entity(data.toString()).build();
			} catch (IOException e) {
				Log.error(e.getMessage());
				return Response.status(401).build();
			}
		} else {
			return Response.status(401).build();
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/drive/profile/image/upload")
	public Response uploadProfileImage(@Context Request request,
			@FormDataParam("file") InputStream is,
			@FormDataParam("file") FormDataContentDisposition formData) {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null) {
			try {
				DriveController drive = new DriveController();
				drive.uploadProfileImage(session.getAttribute("user_id")
						.toString(), FileFactory.stream2file(is),
						new String(formData.getFileName()
								.getBytes("iso-8859-1"), "utf-8"), drive
								.getOptimizedStorage());
				JSONObject data = new JSONObject();
				data.put("code", 1);
				data.put("msg", "success");
				return Response.status(200).entity(data.toString()).build();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.error(e.getMessage());
				return Response.status(401).build();
			}
		} else {
			return Response.status(401).build();
		}
	}

	// View
	@GET
	@Path("/drive/folder/{id}")
	public Response getProjectDriveFolder(@Context Request request,
			@PathParam("id") String id) {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null) {
			Log.debug(id);
			Log.debug(request.getRequestURI());
			JSONObject data = new JSONObject();
			UserController user = new UserController();
			data.put("name", user.getUserName(session.getAttribute("user_id")
					.toString()));
			data.put("role", UserController.getRoleById(session.getAttribute(
					"user_id").toString()));
			data.put("user_id", session.getAttribute("user_id").toString());
			ProjectsController project = new ProjectsController();
			data.put("project", project.getDetailByProjectId(id));
			DatabaseController db = new DatabaseController();
			JSONArray drivedocs = JSONFactory.getData(JSONFactory
					.inputStreamToJson(db.getByView("_design/file",
							"projectdrivePlus", id, true, false, false)));
			JSONArray drive = new JSONArray();
			for (int i = 0; i < drivedocs.length(); i++) {
				JSONObject doc = drivedocs.getJSONObject(i)
						.getJSONObject("doc");

				long modified = doc.getLong("modified_at");
				SimpleDateFormat datefm = new SimpleDateFormat("yyyyMMdd HH:mm");
				Date date = new Date();
				date.setTime(modified);
				String str = datefm.format(date);
				doc.put("modified_at", str);

				drive.put(doc);
			}
			Log.info(drive.length());
			if (drive.length() != 0) {
				Log.info(drive.toString());
				data.put("driveFilesPlus", drive);
			}
			return Response
					.status(200)
					.entity(new Viewable("/new/new_drive.mustache",
							MustacheHelper.toMap(data))).build();
		} else {
			return Response.status(401)
					.entity(new Viewable("/new/new_login.mustache")).build();
		}
	}

	@GET
	@Path("/drive/raw")
	@Produces("application/octet-stream")
	public Response getDriveFile(@QueryParam("id") String id) {
		DatabaseController db = new DatabaseController();
		JSONObject fileData = JSONFactory.inputStreamToJson(db.getDoc(id));
		DriveController drive = new DriveController();
		File raw = drive.getFile(id);
		if (raw != null) {
			try {
				return Response
						.status(200)
						.header("Content-Disposition",
								"attachment; filename=\""
										+ new String(fileData.getString("name")
												.getBytes("utf-8"),
												"ISO-8859-1") + "\"")
						.entity(Files.toByteArray(raw)).build();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.error(e.getMessage());
				return Response.noContent().build();
			}
		} else {
			return Response.noContent().build();
		}
	}

	@GET
	@Path("/drive/image")
	@Produces("image/jpeg")
	public Response getDriveImage(@QueryParam("id") String id) {
		DriveController drive = new DriveController();
		File image = drive.getFile(id);
		if (image != null) {
			try {
				return Response.status(200).entity(Files.toByteArray(image))
						.build();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.error(e.getMessage());
				return Response.noContent().build();
			}
		} else {
			return Response.noContent().build();
		}
	}

	@GET
	@Path("/drive/user/image")
	@Produces("image/jpeg")
	public Response getUserImage(@QueryParam("id") String id)
			throws IOException {
		DriveController drive = new DriveController();
		File image = drive.getUserImage(id);
		if (image != null) {
			try {
				return Response.status(200).entity(Files.toByteArray(image))
						.build();
			} catch (IOException e) {
				Log.error(e.getMessage());
				return Response
						.status(200)
						.entity(Files.toByteArray(new File(
								"profile_default.jpg"))).build();
			}
		} else {
			return Response.status(200)
					.entity(Files.toByteArray(new File("profile_default.jpg")))
					.build();
		}
	}

	// Google

}