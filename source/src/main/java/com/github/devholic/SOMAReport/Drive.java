package com.github.devholic.SOMAReport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Session;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.github.devholic.SOMAReport.Controller.DriveController;
import com.github.devholic.SOMAReport.Utilities.FileFactory;
import com.google.common.io.Files;

@Path("/")
public class Drive {

	private final static Logger Log = Logger.getLogger(Drive.class);

	// API
	@POST
	@Path("/drive/profile/upload")
	public Response uploadImage(@Context Request request,
			@FormDataParam("file") InputStream is,
			@FormDataParam("file") FormDataContentDisposition formData) {
		Session session = request.getSession();
		if (session.getAttribute("user_id") != null) {
			try {
				DriveController drive = new DriveController();
				drive.uploadProfileImage(session.getAttribute("user_id").toString(), FileFactory.stream2file(is));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
		}
		return Response.status(200).build();
	}

	// View
	@GET
	@Path("/drive/project/{id}")
	public Response getProjectDrive(@QueryParam("id") String id) {
		DriveController drive = new DriveController();
		File image = drive.getImage(id);
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
	@Path("/drive/image")
	@Produces("image/jpeg")
	public Response getDriveImage(@QueryParam("id") String id) {
		DriveController drive = new DriveController();
		File image = drive.getImage(id);
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
	public Response getUserImage(@QueryParam("id") String id) {
		DriveController drive = new DriveController();
		File image = drive.getUserImage(id);
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

	// Google

}