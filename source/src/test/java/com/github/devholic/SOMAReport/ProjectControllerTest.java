package com.github.devholic.SOMAReport;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.mustache.MustacheMvcFeature;
import org.glassfish.jersey.test.JerseyTestNg;
import org.junit.Test;

public class ProjectControllerTest extends JerseyTestNg.ContainerPerMethodTest {

	private final Logger Log = Logger.getLogger(ProjectControllerTest.class);

	@Override
	protected javax.ws.rs.core.Application configure() {
		return new ResourceConfig()
				.property(MustacheMvcFeature.TEMPLATE_BASE_PATH, "templates")
				.register(MustacheMvcFeature.class)
				.register(MultiPartFeature.class)
				.packages("com.github.devholic.SOMAReport");
	}

	@Test
	public void testGetMyProjects() {
		final Response response = target()
				.path("/project/id/" + TestData.sampleProject).request().get();
		Log.info(response.readEntity(String.class));
	}
}
