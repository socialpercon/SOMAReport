// https://docs.google.com/spreadsheets/d/1AhNqZxIMev22Ik77Leh_Sm8eLk3JGKORaqlocAzi0K0/edit#gid=0
// 테스트 작성자 : 강성훈
// 로그인, 로그아웃 뷰 테스트

package com.github.devholic.SOMAReport;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.mustache.MustacheMvcFeature;
import org.glassfish.jersey.test.JerseyTestNg;
import org.junit.Test;

public class LoginViewTest extends JerseyTestNg.ContainerPerMethodTest {

	private static final String loginViewAddress = "/login";
	private static final String logoutViewAddress = "/logout";

	@Override
	protected javax.ws.rs.core.Application configure() {
		return new ResourceConfig()
				.property(MustacheMvcFeature.TEMPLATE_BASE_PATH, "templates")
				.register(MustacheMvcFeature.class)
				.register(MultiPartFeature.class)
				.packages("com.github.devholic.SOMAReport");
	}

	@Test
	public void LoginView_CorrectIdWithCorrectPw_HTTP302() {
		WebTarget target = target().path(loginViewAddress);
		target.property(ClientProperties.FOLLOW_REDIRECTS, false);
		Form form = new Form();
		form.param("email", TestData.correctEmail);
		form.param("password", TestData.correctPassword);
		final Response response = target.request().post(Entity.form(form));
		assertEquals(303, response.getStatus());
	}

	@Test
	public void LoginView_CorrectIdWithWrongPassword_HTTP400() {
		Form form = new Form();
		form.param("email", TestData.correctEmail);
		form.param("password", TestData.wrongPassword);
		final Response response = target().path(loginViewAddress).request()
				.post(Entity.form(form));
		assertEquals(400, response.getStatus());
	}

	@Test
	public void LoginView_WrongIdWithPassword_HTTP400() {
		Form form = new Form();
		form.param("email", TestData.wrongEmail);
		form.param("password", TestData.correctPassword);
		final Response response = target().path(loginViewAddress).request()
				.post(Entity.form(form));
		assertEquals(400, response.getStatus());
	}

	@Test
	public void LoginView_IdWithNullPassword_HTTP400() {
		Form form = new Form();
		form.param("email", TestData.correctEmail);
		final Response response = target().path(loginViewAddress).request()
				.post(Entity.form(form));
		assertEquals(400, response.getStatus());
	}

	@Test
	public void LoginView_NullIdWithPassword_HTTP400() {
		Form form = new Form();
		form.param("password", TestData.correctPassword);
		final Response response = target().path(loginViewAddress).request()
				.post(Entity.form(form));
		assertEquals(400, response.getStatus());
	}

	@Test
	public void LoginView_NullIdWithNullPassword_HTTP400() {
		Form form = new Form();
		final Response response = target().path(loginViewAddress).request()
				.post(Entity.form(form));
		assertEquals(400, response.getStatus());
	}

	@Test
	public void LoginView_TryLoginWhenUserAlreadyLogin_HTTP302_302() {
		WebTarget target = target().path(loginViewAddress);
		target.property(ClientProperties.FOLLOW_REDIRECTS, false);
		Form form = new Form();
		form.param("email", TestData.correctEmail);
		form.param("password", TestData.correctPassword);
		final Response loginResponse = target.request().post(Entity.form(form));
		assertEquals(303, loginResponse.getStatus());
		final Response loginAgainResponse = target.request()
				.cookie(loginResponse.getCookies().get("JSESSIONID"))
				.post(Entity.form(form));
		assertEquals(303, loginAgainResponse.getStatus());
	}

	@Test
	public void LogoutView_AlreadyLogin_HTTP302_302() {
		WebTarget target = target();
		target.property(ClientProperties.FOLLOW_REDIRECTS, false);
		Form form = new Form();
		form.param("email", TestData.correctEmail);
		form.param("password", TestData.correctPassword);
		final Response loginResponse = target.path(loginViewAddress).request()
				.post(Entity.form(form));
		assertEquals(303, loginResponse.getStatus());
		final Response logoutResponse = target.path(logoutViewAddress)
				.request().cookie(loginResponse.getCookies().get("JSESSIONID"))
				.get();
		assertEquals(303, logoutResponse.getStatus());
	}

	@Test
	public void LogoutView_UseridNull_HTTP302() {
		WebTarget target = target().path(logoutViewAddress);
		target.property(ClientProperties.FOLLOW_REDIRECTS, false);
		final Response response = target.request().get();
		assertEquals(303, response.getStatus());
	}
}