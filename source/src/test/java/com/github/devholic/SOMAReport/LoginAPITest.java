// https://docs.google.com/spreadsheets/d/1AhNqZxIMev22Ik77Leh_Sm8eLk3JGKORaqlocAzi0K0/edit#gid=0
// 테스트 작성자 : 강성훈
// 로그인, 로그아웃 테스트

package com.github.devholic.SOMAReport;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.mustache.MustacheMvcFeature;
import org.glassfish.jersey.test.JerseyTestNg;
import org.junit.Test;

public class LoginAPITest extends JerseyTestNg.ContainerPerMethodTest {

	private static final String loginApiAddress = "/api/login";
	private static final String logoutApiAddress = "/api/logout";

	@Override
	protected javax.ws.rs.core.Application configure() {
		return new ResourceConfig()
				.property(MustacheMvcFeature.TEMPLATE_BASE_PATH, "templates")
				.register(MustacheMvcFeature.class)
				.register(MultiPartFeature.class)
				.packages("com.github.devholic.SOMAReport");
	}

	@Test
	public void LoginAPI_CorrectIdWithCorrectPw_HTTP200() {
		Form form = new Form();
		form.param("email", TestData.correctEmail);
		form.param("password", TestData.correctPassword);
		final Response response = target().path(loginApiAddress).request()
				.post(Entity.form(form));
		assertEquals(200, response.getStatus());
	}

	@Test
	public void LoginAPI_CorrectIdWithWrongPassword_HTTP400() {
		Form form = new Form();
		form.param("email", TestData.correctEmail);
		form.param("password", TestData.wrongPassword);
		final Response response = target().path(loginApiAddress).request()
				.post(Entity.form(form));
		assertEquals(400, response.getStatus());
	}

	@Test
	public void LoginAPI_WrongIdWithPassword_HTTP400() {
		Form form = new Form();
		form.param("email", TestData.wrongEmail);
		form.param("password", TestData.correctPassword);
		final Response response = target().path(loginApiAddress).request()
				.post(Entity.form(form));
		assertEquals(400, response.getStatus());
	}

	@Test
	public void LoginAPI_IdWithNullPassword_HTTP400() {
		Form form = new Form();
		form.param("email", TestData.correctEmail);
		final Response response = target().path(loginApiAddress).request()
				.post(Entity.form(form));
		assertEquals(400, response.getStatus());
	}

	@Test
	public void LoginAPI_NullIdWithPassword_HTTP400() {
		Form form = new Form();
		form.param("password", TestData.correctPassword);
		final Response response = target().path(loginApiAddress).request()
				.post(Entity.form(form));
		assertEquals(400, response.getStatus());
	}

	@Test
	public void LoginAPI_NullIdWithNullPassword_HTTP400() {
		Form form = new Form();
		final Response response = target().path(loginApiAddress).request()
				.post(Entity.form(form));
		assertEquals(400, response.getStatus());
	}

	@Test
	public void LoginAPI_TryLoginWhenUserAlreadyLogin_HTTP200_412() {
		Form form = new Form();
		form.param("email", TestData.correctEmail);
		form.param("password", TestData.correctPassword);
		final Response loginResponse = target().path(loginApiAddress).request()
				.post(Entity.form(form));
		assertEquals(200, loginResponse.getStatus());
		final Response loginAgainResponse = target().path(loginApiAddress)
				.request().cookie(loginResponse.getCookies().get("JSESSIONID"))
				.post(Entity.form(form));
		assertEquals(412, loginAgainResponse.getStatus());
	}

	@Test
	public void LogoutAPI_AlreadyLogin_HTTP200_200() {
		Form form = new Form();
		form.param("email", TestData.correctEmail);
		form.param("password", TestData.correctPassword);
		final Response loginResponse = target().path(loginApiAddress).request()
				.post(Entity.form(form));
		assertEquals(200, loginResponse.getStatus());
		final Response logoutResponse = target().path(logoutApiAddress)
				.request().cookie(loginResponse.getCookies().get("JSESSIONID"))
				.get();
		assertEquals(200, logoutResponse.getStatus());
	}

	@Test
	public void LogoutAPI_UseridNull_HTTP412() {
		final Response response = target().path(logoutApiAddress).request()
				.get();
		assertEquals(412, response.getStatus());
	}

}