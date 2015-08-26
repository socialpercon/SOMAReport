package com.github.devholic.SOMAReport;

import java.net.URI;

import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.mustache.MustacheMvcFeature;

import com.github.devholic.SOMAReport.Utilities.FileFactory;
import com.github.devholic.SOMAReport.Utilities.StringFactory;

public class Application {

	
	public static final String BASE_URI = "http://localhost";
	public static final String PORT = "8080";

	// 참고링크 : https://jersey.java.net/documentation/latest/mvc.html
	public static HttpServer startServer() {
		final ResourceConfig rc = new ResourceConfig()
				.property(MustacheMvcFeature.TEMPLATE_BASE_PATH, "templates")
				.register(MustacheMvcFeature.class)
				.register(MultiPartFeature.class)
				.packages("com.github.devholic.SOMAReport");
		return GrizzlyHttpServerFactory.createHttpServer(
				URI.create(StringFactory.createBaseUrl(BASE_URI, PORT)), rc);
	}

	public static void main(final String[] args) throws Exception {
		Logger logger = Logger.getLogger(Application.class);
		FileFactory ff = new FileFactory();
		ff.autoDelete();
		
		final HttpServer server = startServer();
		logger.debug("Hit enter to stop server");
		System.in.read();
		server.shutdownNow();
	}
}
