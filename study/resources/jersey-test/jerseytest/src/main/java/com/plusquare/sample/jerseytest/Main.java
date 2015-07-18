package com.plusquare.sample.jerseytest;

import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.mustache.MustacheMvcFeature;

/**
 * Main class.
 *
 */
public class Main {
	// Base URI the Grizzly HTTP server will listen on
	public static final String BASE_URI = "http://localhost:8080/api/";

	public static HttpServer startServer() {
		// https://jersey.java.net/documentation/latest/mvc.html#mvc.registration
		final ResourceConfig rc = new ResourceConfig()
				.property(MustacheMvcFeature.TEMPLATE_BASE_PATH, "")
				.register(MustacheMvcFeature.class)
				.packages("com.plusquare.sample.jerseytest");
		return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI),
				rc);
	}

	public static void main(final String[] args) throws Exception {
		final HttpServer server = startServer();
		System.out.println(String.format("Hit enter to stop server", BASE_URI));
		System.in.read();
		server.shutdownNow();
	}
}
