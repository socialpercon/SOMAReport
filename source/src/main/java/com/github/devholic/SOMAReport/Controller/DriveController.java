package com.github.devholic.SOMAReport.Controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Utilities.JSONFactory;
import com.github.devholic.SOMAReport.Utilities.StringFactory;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class DriveController {

	private Logger Log = Logger.getLogger(DriveController.class);

	static DatabaseController db = new DatabaseController();

	private static final String APPLICATION_NAME = "SOMA Report";
	private static final String CLIENTSECRET_LOCATION = "client_secret.json";
	public static final String REDIRECT_URI = "http://localhost:8080/console/drive";

	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JacksonFactory JSON_FACTORY = JacksonFactory
			.getDefaultInstance();
	private static final List<String> SCOPES = Arrays
			.asList("https://www.googleapis.com/auth/drive");

	private static GoogleAuthorizationCodeFlow flow = null;

	public void uploadImageToProject(String projectId, java.io.File file) {
		DatabaseController db = new DatabaseController();
		JSONObject driveQuery = JSONFactory
				.inputStreamToJson(db.getByView("_design/file", "projectdrive",
						projectId, false, false, false));
		Log.info(JSONFactory.getData(driveQuery).toString());
		if (JSONFactory.getData(driveQuery).length() == 0) {
			JSONObject projectDrive = new JSONObject();
			projectDrive.put("project", projectId);
			projectDrive.put("type", "project_drive");
			projectDrive.put("files", new JSONArray());
			Map<String, Object> map = db.createDoc(projectDrive);
			Log.info(map.toString());
			driveQuery = JSONFactory.inputStreamToJson(db.getByView(
					"_design/file", "projectdrive", projectId, false, false,
					false));
		}
		Log.info(JSONFactory.getData(driveQuery).toString());
		JSONObject jo = JSONFactory.inputStreamToJson(db.getDoc(JSONFactory
				.getData(driveQuery).getJSONObject(0).getString("id")));
		Log.info(jo.toString());
		String id = uploadImage(file);
		jo.getJSONArray("files").put(id);
		db.updateDoc(jo);
	}

	public String uploadImage(java.io.File file) {
		Long now = System.currentTimeMillis();
		JSONObject imageData = new JSONObject();
		imageData.put("type", "file");
		imageData.put("storage", "0");
		imageData.put("modified_at", now);
		imageData.put("cached_at", now);
		Map<String, Object> r = db.createDoc(imageData);
		try {
			Credential c = getCredential(
					"ya29.yAF8kUZ_J3uUzJPbQvPYv-sFlM6qjP9FyHKOvgRON09Hrj7OFxxmJWbRkdoPjc20wgZH",
					"1/yXfCfi7fAiPmVzqJ6NrtkZxaDuyH2yqiKU_5aoK1yCw");
			com.google.api.services.drive.Drive drive = buildService(c);
			File body = new File();
			body.setTitle(r.get("_id").toString());
			FileContent data = new FileContent("", file);
			drive.files().insert(body, data).execute();
			return r.get("_id").toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.error(e.getMessage());
		}
		return null;
	}

	public java.io.File getImage(String id) {
		java.io.File f = new java.io.File("cache/" + id);
		InputStream is = db.getByView("_design/file", "info", id, false, false,
				false);
		JSONArray result = JSONFactory.getData(new JSONObject(StringFactory
				.inputStreamToString(is)));
		if (f.isFile()) {
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
		} else {
			// Create cache data
			if (result.length() == 1) {
				JSONArray data = JSONFactory.getValue(result.getJSONObject(0));
				return createCache(id, data.getString(0));
			}
		}
		return null;
	}

	public JSONArray getProjectImageList(String id) {
		JSONObject driveQuery = JSONFactory.inputStreamToJson(db.getByView(
				"_design/file", "projectdrive", id, false, false, false));
		if (JSONFactory.getData(driveQuery).length() == 0) {
			Log.info("in");
			JSONObject projectDrive = new JSONObject();
			projectDrive.put("project", id);
			projectDrive.put("type", "project_drive");
			projectDrive.put("files", new JSONArray());
			Map<String, Object> map = db.createDoc(projectDrive);
			Log.info(map.toString());
			driveQuery = JSONFactory.inputStreamToJson(db.getByView(
					"_design/file", "projectdrive", id, false, false, false));
		}
		Log.info("Length " + JSONFactory.getData(driveQuery).length());
		JSONArray idList = JSONFactory.getValue(JSONFactory.getData(driveQuery)
				.getJSONObject(0));
		Log.info(idList.toString());
		return idList;
	}

	public java.io.File getUserImage(String id) {
		java.io.File f = new java.io.File("cache/" + id);
		if (f.isFile()) {
			return f;
		} else {
			return createCache(id, "0");
		}
	}

	public boolean deleteImage(String id) {
		// delete cache
		java.io.File f = new java.io.File("cache/" + id);
		if (f.exists()) {
			f.delete();
		}
		InputStream is = db.getByView("_design/file", "info", id, true, false,
				false);
		JSONArray result = JSONFactory.getData(new JSONObject(StringFactory
				.inputStreamToString(is)));
		if (db.deleteDoc(result.getJSONObject(0).getJSONObject("doc")
				.getString("_id"), result.getJSONObject(0).getJSONObject("doc")
				.getString("_rev"))) {
			try {
				Credential c = getCredential(
						"ya29.yAF8kUZ_J3uUzJPbQvPYv-sFlM6qjP9FyHKOvgRON09Hrj7OFxxmJWbRkdoPjc20wgZH",
						"1/yXfCfi7fAiPmVzqJ6NrtkZxaDuyH2yqiKU_5aoK1yCw");
				com.google.api.services.drive.Drive drive = buildService(c);
				FileList fl = drive.files().list().setQ("title = '" + id + "'")
						.execute();
				File file = fl.getItems().get(0);
				drive.files().delete(file.getId()).execute();
				return true;
			} catch (IOException e) {
				Log.error(e.getMessage());
			}
		}
		return false;
	}

	public void checkCacheDir() {
		if (!new java.io.File("cache").exists()) {
			new java.io.File("cache").mkdir();
		}
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
			Log.error(e.getMessage());
		}
		return null;
	}

	public String createCredentialFlow1(String storage_id) {
		GoogleAuthorizationCodeFlow flow = getFlow();
		GoogleAuthorizationCodeRequestUrl urlBuilder = flow
				.newAuthorizationUrl().setRedirectUri(REDIRECT_URI);
		urlBuilder.set("storage", storage_id);
		return urlBuilder.build();
	}

	public void createCredentialFlow2(String code) {
		try {
			GoogleAuthorizationCodeFlow flow = getFlow();
			GoogleTokenResponse resp;
			resp = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI)
					.execute();
			PrintWriter out = new PrintWriter("0.json");
			out.println(resp.toString());
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	static GoogleAuthorizationCodeFlow getFlow() {
		try {
			if (flow == null) {
				InputStream in;

				in = new FileInputStream(CLIENTSECRET_LOCATION);

				GoogleClientSecrets clientSecret = GoogleClientSecrets.load(
						JSON_FACTORY, new InputStreamReader(in));
				flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT,
						JSON_FACTORY, clientSecret, SCOPES)
						.setAccessType("offline").setApprovalPrompt("force")
						.build();
			} else {
				return flow;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	static com.google.api.services.drive.Drive buildService(
			Credential credentials) {
		return new com.google.api.services.drive.Drive.Builder(HTTP_TRANSPORT,
				JSON_FACTORY, credentials).setApplicationName(APPLICATION_NAME)
				.build();
	}
}