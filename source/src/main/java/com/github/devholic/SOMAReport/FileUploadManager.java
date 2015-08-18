package com.github.devholic.SOMAReport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.mvc.Viewable;

import com.github.devholic.SOMAReport.Controller.DriveController;

@Path("/")
public class FileUploadManager {

	private final static Logger Log = Logger.getLogger(FileUploadManager.class);

	@Context
	UriInfo uri;

	/**************************************************
	 * fileupload_manage template를 띄워주는 컨트롤러
	 * @return
	 *************************************************/
	@GET
	@Path("/fileupload")
	public Response consoleProject() {

		return Response.status(200)
				.entity(new Viewable("/fileupload_manage.mustache")).build();
	}
	
	
	/**************************************************
	 * 여러개의 파일을 한번에 업로드 할때 사용하는 컨트롤러 
	 * @param InputStream is
	 * @return
	 *************************************************/
	@POST
	@Path("/uploadMultiFile")
	public Response uploadMultiFile(@FormDataParam("file") InputStream is) {
		Log.info("uploadMultiFile!!!!!!  is:" + is);
		try {
			File buffer_file = this.stream2file(is);

			DriveController dc = new DriveController();
			dc.uploadFileToProject("9d898f7d5bfbf361939e1fafd518b7f0",
					buffer_file);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Response.status(200)
				.entity("{\"code\":\"1\", \"msg\":\"file upload success.\"}")
				.build();
	}
	
	/*************************************************
	 * InputStream을 File로 변환해주는 메소드
	 * @param InputStream in
	 * @return File file
	 * @throws IOException
	 *************************************************/
	public File stream2file (InputStream in) throws IOException {

		final File tempFile = File.createTempFile("stream2file", ".tmp");
		tempFile.deleteOnExit();

		try {
			FileOutputStream fo = new FileOutputStream(tempFile);
			IOUtils.copy(in, fo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tempFile;
	}
}
