package com.github.devholic.SOMAReport.Controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
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
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.About;
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

	public class Token {
		public String accessToken, refreshToken;

		public String getAccessToken() {
			return accessToken;
		}

		public void setAccessToken(String accessToken) {
			this.accessToken = accessToken;
		}

		public String getRefreshToken() {
			return refreshToken;
		}

		public void setRefreshToken(String refreshToken) {
			this.refreshToken = refreshToken;
		}

	}

	public String getOptimizedStorage() throws IOException {
		DatabaseController db = new DatabaseController();
		JSONArray ja = JSONFactory.getData(JSONFactory.inputStreamToJson(db
				.getByView("_design/drive", "account", false, false, false)));
		for (int i = 0; i < ja.length(); i++) {
			JSONObject data = ja.getJSONObject(i);
			Token t = getToken(data.getString("value"));
			Drive drive = buildService(getCredential(t.getAccessToken(),
					t.getRefreshToken()));
			if ((getTotalQuota(drive) - getUsedQuota(drive)) > 104857600) {
				return Integer.toString(i);
			}
		}
		return null;
	}

	/******************************************
	 * 구글 드라이브에 파일을 업로드한다 :
	 * 
	 * @param projectId
	 * @param file
	 * @throws IOException
	 *****************************************/
	public void uploadFileToProject(String projectId, java.io.File file,
			String originalName) throws IOException {
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
		String id = uploadFile(file, originalName, getOptimizedStorage());
		jo.getJSONArray("files").put(id);
		db.updateDoc(jo);
	}

	/******************************************
	 * 구글드라이브에 프로필 이미지를 업로드 한다
	 * 
	 * @param file
	 * @return String
	 *****************************************/
	public String uploadProfileImage(String id, java.io.File file,
			String originalName, String storage) {

		Long now = System.currentTimeMillis();

		String fileTitle = "";
		JSONObject userDoc = JSONFactory.inputStreamToJson(db.getDoc(id));

		String profileFile;
		if (userDoc.has("profileFile")) {
			profileFile = userDoc.getString("profileFile");
		} else {
			profileFile = "";
		}
		@SuppressWarnings("unused")
		Map<String, Object> r = null;
		JSONObject fileDoc = new JSONObject();
		JSONObject imageData = new JSONObject();

		try {
			/* ************************************************
			 * profile file이 있으면 기존에 있는 파일을 삭제하고 fileDoc의 정보를 update 한다.
			 * ***********************************************
			 */
			if (userDoc.has("profileFile")) {

				if (deleteImage(profileFile)) {

					// fileDoc =
					// JSONFactory.inputStreamToJson(db.getDoc(profileFile));
					fileDoc.put("_id", id + "-profileImage");
					fileDoc.put("type", "file");
					fileDoc.put("name", originalName);
					fileDoc.put("storage", "0");
					fileDoc.put("modified_at", now);
					fileDoc.put("cached_at", 0);
					db.createDoc(fileDoc);

					fileTitle = id;
				} else {
					Log.debug("deleteImage Failed...");
					return null;
				}
				/* ************************************************
				 * profile file이 없으면 새로운 fileDoc을 생성하고, userDoc에 profileFile 정보를
				 * update한 ***********************************************
				 */
			} else {
				imageData.put("_id", id + "-profileImage");
				imageData.put("type", "file");
				imageData.put("name", originalName);
				imageData.put("storage", "0");
				imageData.put("modified_at", now);
				imageData.put("cached_at", 0);
				r = db.createDoc(imageData);

				userDoc.put("profileFile", id + "-profileImage");
				db.updateDoc(userDoc);

				fileTitle = id;
			}

			/* ************************************************
			 * JSON 파일을 읽어서 credential을 가져온다. accessToken과 refreshToken을 가져온다.
			 * ***********************************************
			 */
			Token token = getToken(storage + ".json");
			Credential c = getCredential(token.getAccessToken(),
					token.getRefreshToken());
			com.google.api.services.drive.Drive drive = buildService(c);

			/* ************************************************
			 * 용량체크를 한다. " 100메가 이하면 사용하지 못한다."
			 * ***********************************************
			 */
			// printAbout(drive);
			File body = new File();
			body.setTitle(fileTitle + "-profileImage");
			FileContent data = new FileContent("", file);
			drive.files().insert(body, data).execute();
			return fileTitle + "-profileImage";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.error(e.getMessage());
		}
		return null;
	}

	/*****************************************
	 * 구글드라이브에 파일을 업로드 한다
	 * 
	 * @param file
	 * @return String
	 *****************************************/
	public String uploadFile(java.io.File file, String originalName,
			String storage) {
		storage = "0";
		Long now = System.currentTimeMillis();
		JSONObject imageData = new JSONObject();
		imageData.put("type", "file");
		imageData.put("name", originalName);
		imageData.put("storage", storage);
		imageData.put("modified_at", now);
		imageData.put("cached_at", 0);
		Map<String, Object> r = db.createDoc(imageData);
		try {
			Token token = getToken(storage + ".json");
			Credential c = getCredential(token.getAccessToken(),
					token.getRefreshToken());
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

	public java.io.File getFile(String id) {
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
		Log.info(id);
		DatabaseController db = new DatabaseController();
		JSONObject fileInfo = JSONFactory.inputStreamToJson(db.getDoc(id));
		Log.info("info : " + fileInfo.toString());
		java.io.File f = new java.io.File("cache/" + id);
		if (f.isFile()) {
			return f;
		} else {
			return createCache(id, fileInfo.getString("storage"));
		}
	}

	public boolean deleteImage(String id) {
		Log.info("Delete File id = " + id);
		// delete cache
		java.io.File f = new java.io.File("cache/" + id);
		if (f.exists()) {
			f.delete();
		}
		JSONArray result = JSONFactory.getData(JSONFactory.inputStreamToJson(db
				.getByView("_design/file", "info", id, true, false, false)));
		if (result.length() != 0) {
			String storage = result.getJSONObject(0).getJSONObject("doc")
					.getString("storage");
			if (db.deleteDoc(
					result.getJSONObject(0).getJSONObject("doc")
							.getString("_id"),
					result.getJSONObject(0).getJSONObject("doc")
							.getString("_rev"))) {
				try {
					Token token = getToken(storage + ".json");
					Credential c = getCredential(token.getAccessToken(),
							token.getRefreshToken());
					com.google.api.services.drive.Drive drive = buildService(c);
					FileList fl = drive.files().list()
							.setQ("title = '" + id + "'").execute();
					File file = fl.getItems().get(0);
					drive.files().delete(file.getId()).execute();
					return true;
				} catch (IOException e) {
					Log.error(e.getMessage());
				}
			}
		}
		return false;
	}

	public void checkCacheDir() {
		if (!new java.io.File("cache").exists()) {
			new java.io.File("cache").mkdir();
		}
	}

	/********************************************
	 * 캐시를 생성한다
	 * @param id
	 * @param storage
	 * @return File
	 ********************************************/
	public java.io.File createCache(String id, String storage) {
		try {
			Token token = getToken(storage + ".json");
			Credential c = getCredential(token.getAccessToken(),
					token.getRefreshToken());
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
				
				//couchDB 'cached_at' update
				JSONObject fileDoc = JSONFactory.inputStreamToJson(db.getDoc(id));
				Long now = System.currentTimeMillis();
				
				if (fileDoc.has("cached_at")) {
					fileDoc.put("cached_at", now);
            		db.updateDoc(fileDoc);
            	}
				
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

	public String createCredentialFlow1(String storageName, String url) {
		GoogleAuthorizationCodeFlow flow = getFlow();
		GoogleAuthorizationCodeRequestUrl urlBuilder = flow
				.newAuthorizationUrl().setRedirectUri(url + storageName);
		return urlBuilder.build();
	}

	public boolean createCredentialFlow2(String code, String fileName,
			String url) {
		try {
			GoogleAuthorizationCodeFlow flow = getFlow();
			GoogleTokenResponse resp;
			resp = flow.newTokenRequest(code).setRedirectUri(url).execute();
			PrintWriter out = new PrintWriter(fileName + ".json");
			out.println(resp.toString());
			out.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public Token getToken(String fileName) {
		JSONParser parser = new JSONParser();
		Object obj;
		try {
			obj = parser.parse(new FileReader(fileName));
			org.json.simple.JSONObject jsonObj = (org.json.simple.JSONObject) obj;
			Token token = new Token();
			token.setAccessToken((String) jsonObj.get("access_token"));
			token.setRefreshToken((String) jsonObj.get("refresh_token"));
			return token;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Credential getCredential(String accessToken, String refreshToken)
			throws IOException {
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

	public com.google.api.services.drive.Drive buildService(
			Credential credentials) {
		return new com.google.api.services.drive.Drive.Builder(HTTP_TRANSPORT,
				JSON_FACTORY, credentials).setApplicationName(APPLICATION_NAME)
				.build();
	}

	/***************************************************
	 * Drive 정보에 대해서 프린트 한다
	 * 
	 * @param service
	 **************************************************/
	public void printAbout(com.google.api.services.drive.Drive service) {
		try {
			About about = service.about().get().execute();

			System.out.println("Current user name: " + about.getName());
			System.out.println("Root folder ID: " + about.getRootFolderId());
			System.out.println("Total quota (bytes): "
					+ about.getQuotaBytesTotal());
			System.out.println("Used quota (bytes): "
					+ about.getQuotaBytesUsed());
		} catch (IOException e) {
			System.out.println("An error occurred: " + e);
		}
	}

	/***************************************************
	 * 전체 용량을 가져온다
	 * 
	 * @param service
	 * @return long
	 **************************************************/
	public long getTotalQuota(com.google.api.services.drive.Drive service) {
		long totalQuota = 0L;
		try {
			About about = service.about().get().execute();
			totalQuota = about.getQuotaBytesTotal();
		} catch (IOException e) {
			Log.error("An error occurred: " + e);
		}
		return totalQuota;
	}

	/**************************************************
	 * 현재 사용중인 용량을 가져온다
	 * 
	 * @param service
	 * @return long
	 **************************************************/
	public long getUsedQuota(com.google.api.services.drive.Drive service) {
		long usedQuota = 0L;
		try {
			About about = service.about().get().execute();
			usedQuota = about.getQuotaBytesUsed();
		} catch (IOException e) {
			Log.error("An error occurred: " + e);
		}
		return usedQuota;
	}

	/***
	 * 프로젝트 드라이브와 그에 속한 파일들의 정보를 가져온다
	 * 
	 * @param projectId
	 * @return JSONObject {projectId, fileNum, files[ {fileName, storage,
	 *         fileId, userId, userName} ] }
	 */
	public JSONObject getProjectDriveFileInfo(String projectId) {
		JSONObject result = new JSONObject();
		result.put("projectId", projectId);

		UserController userC = new UserController();
		JSONObject driveinfo = JSONFactory.inputStreamToJson(db.getByView(
				"_design/file", "projectdrivePlus", projectId, true, false,
				false));
		JSONArray rows = JSONFactory.getData(driveinfo);
		JSONArray files = new JSONArray();
		for (int i = 0; i < rows.length(); i++) {
			JSONObject fileDoc = rows.getJSONObject(i).getJSONObject("doc");
			JSONObject file = new JSONObject();
			file.put("fileId", fileDoc.get("_id"));
			if (fileDoc.has("name"))
				file.put("fileName", fileDoc.get("name"));
			file.put("storage", fileDoc.get("storage"));
			if (fileDoc.has("user")) {
				file.put("userId", fileDoc.getString("user"));
				file.put("userName",
						userC.getUserName(fileDoc.getString("user")));
			}
			files.put(file);
		}
		result.put("files", files);
		result.put("fileNum", files.length());
		return result;
	}
}
