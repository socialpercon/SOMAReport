package com.github.devholic.SOMAReport.Controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Drive;
import com.github.devholic.SOMAReport.Utilities.JSONFactory;
import com.github.devholic.SOMAReport.Utilities.StringFactory;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class DriveController {

	private Logger Log = Logger.getLogger(Drive.class);

	static DatabaseController db = new DatabaseController();

	private static final String APPLICATION_NAME = "SOMA Report";
	private static final String CLIENTSECRET_LOCATION = "client_secret.json";
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JacksonFactory JSON_FACTORY = JacksonFactory
			.getDefaultInstance();

	public java.io.File cacheManager(String id) {
		java.io.File f = new java.io.File("cache/" + id);
		if (f.isFile()) {
			InputStream is = db.getByView("_design/file", "getfile", id, false,
					false);
			JSONArray result = JSONFactory.getData(new JSONObject(StringFactory
					.inputStreamToString(is)));
			if (result.length() == 1) {
				JSONArray data = JSONFactory.getValue(result.getJSONObject(0));
				if (data.get(2) != null) {
					if (data.getLong(1) > data.getLong(2)) {
						// Renew Cache
						return createCache(id, data.getString(0));
					} else {
						// Return Cache Data
						return f;
					}
				}
			}
		}
		return null;
	}

	public java.io.File createCache(String id, String storage) {
		try {
			Credential c = getCredential(
					"ya29.yAF8kUZ_J3uUzJPbQvPYv-sFlM6qjP9FyHKOvgRON09Hrj7OFxxmJWbRkdoPjc20wgZH",
					"1/yXfCfi7fAiPmVzqJ6NrtkZxaDuyH2yqiKU_5aoK1yCw");
			com.google.api.services.drive.Drive drive = buildService(c);
			FileList fl = drive.files().list().setQ("title = '" + id + "'")
					.execute();
			File file = fl.getItems().get(0);
			if (file != null) {
				HttpResponse resp = drive.getRequestFactory()
						.buildGetRequest(new GenericUrl(file.getDownloadUrl()))
						.execute();
				// 캐시 디렉토리 체크
				checkCacheDir();
				java.io.File cache = new java.io.File("cache/" + id);
				if (cache.createNewFile()) {
				} else {
					cache.delete();
					cache.createNewFile();
				}
				// 파일 생성
				OutputStream o = new FileOutputStream(cache);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = resp.getContent().read(buffer)) > 0) {
					o.write(buffer, 0, len);
				}
				o.close();
				return cache;
			} else {
				return null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.error(e);
		}
		return null;
	}

	public void checkCacheDir() {
		if (!new java.io.File("cache").exists()) {
			new java.io.File("cache").mkdir();
		}
	}

	public static Credential getCredential(String accessToken,
			String refreshToken) throws IOException {
		InputStream in = new FileInputStream(CLIENTSECRET_LOCATION);
		GoogleClientSecrets clientSecret = GoogleClientSecrets.load(
				JSON_FACTORY, new InputStreamReader(in));
		HttpTransport transport = new NetHttpTransport();
		JacksonFactory jsonFactory = new JacksonFactory();
		return new GoogleCredential.Builder().setClientSecrets(clientSecret)
				.setJsonFactory(jsonFactory).setTransport(transport).build()
				.setAccessToken(accessToken).setRefreshToken(refreshToken);
	}

	static com.google.api.services.drive.Drive buildService(
			Credential credentials) {
		return new com.google.api.services.drive.Drive.Builder(HTTP_TRANSPORT,
				JSON_FACTORY, credentials).setApplicationName(APPLICATION_NAME)
				.build();
	}
}
