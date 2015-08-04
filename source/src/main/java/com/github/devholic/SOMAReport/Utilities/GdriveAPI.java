package com.github.devholic.SOMAReport.Utilities;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.log4j.Logger;

import com.github.devholic.SOMAReport.Model.Users;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

@Path("/gdrive")
public class GdriveAPI {

	private final Logger logger = Logger.getLogger(GdriveAPI .class);
	
//	private static final String SCOPES = "https://www.googleapis.com/auth/drive";
//	private static final String CLIENT_SECRET_FILE = "client_secret.json";
	private static final String APPLICATION_NAME = "SOMAReport Test";
	private static final java.io.File DATA_STORE_DIR = new java.io.File(
			System.getProperty("user.home"), ".credentials/drive-api-quickstart");
	private static FileDataStoreFactory DATA_STORE_FACTORY;
	private static final JsonFactory JSON_FACTORY =
	        JacksonFactory.getDefaultInstance();
	private static HttpTransport HTTP_TRANSPORT;
	private static final List<String> SCOPES =
	        Arrays.asList(DriveScopes.DRIVE_METADATA_READONLY);
	
	static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
	
	
	@GET
	@Path("/fileprint")
	public void test_main() throws IOException{
        // Build a new authorized API client service.
        Drive service = getDriveService();

        // Print the names and IDs for up to 10 files.
        FileList result = service.files().list()
             .setMaxResults(10)
             .execute();
        List<File> files = result.getItems();
        if (files == null || files.size() == 0) {
            logger.debug("No files found.");
        } else {
            logger.debug("Files:");
            for (File file : files) {
                logger.debug(file.getTitle()+"("+file.getId()+")\n");
            }
        }
	}
	
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Credential authorize() throws IOException {
		Logger static_logger = Logger.getLogger(GdriveAPI .class);
        // Load client secrets.
        InputStream in = GdriveAPI.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        ////-------------------modified by MIN for test----------------------
        LocalServerReceiver localserver = new LocalServerReceiver();
        static_logger.debug("HOST:"+localserver.getHost()+"\n");
        static_logger.debug("PORT:"+localserver.getPort()+"\n");
        ////-----------------------------------------------------------------
        Credential credential = new AuthorizationCodeInstalledApp(
        		flow, localserver).authorize("user");
        
        static_logger.debug("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }
	
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Drive getDriveService() throws IOException {
        Credential credential = authorize();
        return new Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
	
	/**
	 * File Upload through GDrive API
	 * @throws IOException
	 */
	public static void insertFile() throws IOException{
		
		Logger static_logger = Logger.getLogger(GdriveAPI .class);
//		Drive service = getDriveService();s
		
		//URL 을 이용하여 File을 upload
		String uploadURL = "https://www.googleapis.com/upload/drive/v2/files?uploadType=media";
		
		URL obj = new URL(uploadURL);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
 
		con.setRequestMethod("POST");
//		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		
		//Sending Image Set
		java.io.File imageFile = new java.io.File("/test_image.png");
		String urlParameters = "data="+imageFile;
		
 
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
 
		int responseCode = con.getResponseCode();
		static_logger.debug("\nSending 'POST' request to URL : " + uploadURL);
		static_logger.debug("Post parameters : " + urlParameters);
		static_logger.debug("Response Code : " + responseCode);
		
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		//print result
		static_logger.debug(response.toString());
	
	}
	
}
