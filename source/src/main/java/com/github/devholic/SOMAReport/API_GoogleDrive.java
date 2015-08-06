package com.github.devholic.SOMAReport;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;

import com.github.devholic.SOMAReport.Utilities.GdriveAPI;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

public class API_GoogleDrive {
	private static final String CLIENTSECRET_LOCATION = "client_secret.json";
	private static final String APPLICATION_NAME = "SOMA Report";
	public static final String REDIRECT_URI = "http://localhost:8080/api/drive";
	private static final List<String> SCOPES = Arrays
			.asList("https://www.googleapis.com/auth/drive");

	private static GoogleAuthorizationCodeFlow flow = null;
	private static final JacksonFactory JSON_FACTORY = JacksonFactory
			.getDefaultInstance();
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

	/**
	 * Exception thrown when an error occurred while retrieving credentials.
	 */
	public static class GetCredentialsException extends Exception {

		protected String authorizationUrl;

		/**
		 * Construct a GetCredentialsException.
		 *
		 * @param authorizationUrl
		 *            The authorization URL to redirect the user to.
		 */
		public GetCredentialsException(String authorizationUrl) {
			this.authorizationUrl = authorizationUrl;
		}

		/**
		 * Set the authorization URL.
		 */
		public void setAuthorizationUrl(String authorizationUrl) {
			this.authorizationUrl = authorizationUrl;
		}

		/**
		 * @return the authorizationUrl
		 */
		public String getAuthorizationUrl() {
			return authorizationUrl;
		}
	}

	/**
	 * Exception thrown when a code exchange has failed.
	 */
	public static class CodeExchangeException extends GetCredentialsException {
		/**
		 * Construct a CodeExchangeException.
		 *
		 * @param authorizationUrl
		 *            The authorization URL to redirect the user to.
		 */
		public CodeExchangeException(String authorizationUrl) {
			super(authorizationUrl);
		}
	}

	/**
	 * Exception thrown when no refresh token has been found.
	 */
	public static class NoRefreshTokenException extends GetCredentialsException {
		/**
		 * Construct a NoRefreshTokenException.
		 *
		 * @param authorizationUrl
		 *            The authorization URL to redirect the user to.
		 */
		public NoRefreshTokenException(String authorizationUrl) {
			super(authorizationUrl);
		}
	}

	/**
	 * Retrieved stored credentials for the provided user ID.
	 *
	 * @param userId
	 *            User's ID.
	 * @return Stored Credential if found, {@code null} otherwise.
	 */
	static Credential getStoredCredentials(String userId) {
		// TODO: Implement this method to work with your database. Instantiate a
		// new
		// Credential instance with stored accessToken and refreshToken.
		throw new UnsupportedOperationException();
	}

	/**
	 * Store OAuth 2.0 credentials in the application's database.
	 *
	 * @param userId
	 *            User's ID.
	 * @param credentials
	 *            The OAuth 2.0 credentials to store.
	 */
	static void storeCredentials(String userId, Credential credentials) {
		// TODO: Implement this method to work with your database.
		// Store the credentials.getAccessToken() and
		// credentials.getRefreshToken()
		// string values in your database.
		throw new UnsupportedOperationException();
	}

	/**
	 * Build an authorization flow and store it as a static class attribute.
	 *
	 * @return GoogleAuthorizationCodeFlow instance.
	 * @throws IOException
	 *             Unable to load client_secret.json.
	 */
	static GoogleAuthorizationCodeFlow getFlow() throws IOException {
		if (flow == null) {
			InputStream in = new FileInputStream(CLIENTSECRET_LOCATION);
			GoogleClientSecrets clientSecret = GoogleClientSecrets.load(
					JSON_FACTORY, new InputStreamReader(in));
			flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT,
					JSON_FACTORY, clientSecret, SCOPES)
					.setAccessType("offline").setApprovalPrompt("force")
					.build();
		}
		return flow;
	}

	/**
	 * Exchange an authorization code for OAuth 2.0 credentials.
	 *
	 * @param authorizationCode
	 *            Authorization code to exchange for OAuth 2.0 credentials.
	 * @return OAuth 2.0 credentials.
	 * @throws CodeExchangeException
	 *             An error occurred.
	 */
	static Credential exchangeCode(String authorizationCode)
			throws CodeExchangeException {
		try {
			GoogleAuthorizationCodeFlow flow = getFlow();
			GoogleTokenResponse response = flow
					.newTokenRequest(authorizationCode)
					.setRedirectUri(REDIRECT_URI).execute();
			return flow.createAndStoreCredential(response, null);
		} catch (IOException e) {
			System.err.println("An error occurred: " + e);
			throw new CodeExchangeException(null);
		}
	}

	/**
	 * Retrieve the authorization URL.
	 *
	 * @param emailAddress
	 *            User's e-mail address.
	 * @param state
	 *            State for the authorization URL.
	 * @return Authorization URL to redirect the user to.
	 * @throws IOException
	 *             Unable to load client_secret.json.
	 */
	public static String getAuthorizationUrl(String emailAddress, String state)
			throws IOException {
		GoogleAuthorizationCodeRequestUrl urlBuilder = getFlow()
				.newAuthorizationUrl().setRedirectUri(REDIRECT_URI)
				.setState(state);
		urlBuilder.set("user_id", emailAddress);
		return urlBuilder.build();
	}

	/**
	 * Retrieve credentials using the provided authorization code.
	 *
	 * This function exchanges the authorization code for an access token and
	 * queries the UserInfo API to retrieve the user's e-mail address. If a
	 * refresh token has been retrieved along with an access token, it is stored
	 * in the application database using the user's e-mail address as key. If no
	 * refresh token has been retrieved, the function checks in the application
	 * database for one and returns it if found or throws a
	 * NoRefreshTokenException with the authorization URL to redirect the user
	 * to.
	 *
	 * @param authorizationCode
	 *            Authorization code to use to retrieve an access token.
	 * @param state
	 *            State to set to the authorization URL in case of error.
	 * @return OAuth 2.0 credentials instance containing an access and refresh
	 *         token.
	 * @throws NoRefreshTokenException
	 *             No refresh token could be retrieved from the available
	 *             sources.
	 * @throws IOException
	 *             Unable to load client_secret.json.
	 */
	public static Credential getCredentials(String x, String authorizationCode,
			String state) throws CodeExchangeException,
			NoRefreshTokenException, IOException {
		String emailAddress = "";
		try {
			Credential credentials = exchangeCode(authorizationCode);
			if (credentials.getRefreshToken() != null) {
				storeCredentials(x, credentials);
				return credentials;
			} else {
				credentials = getStoredCredentials(x);
				if (credentials != null
						&& credentials.getRefreshToken() != null) {
					return credentials;
				}
			}
		} catch (CodeExchangeException e) {
			e.printStackTrace();
			e.setAuthorizationUrl(getAuthorizationUrl(emailAddress, state));
			throw e;
		}
		// No refresh token has been retrieved.
		String authorizationUrl = getAuthorizationUrl(emailAddress, state);
		throw new NoRefreshTokenException(authorizationUrl);
	}
	
	/**
	 * File Upload through GDrive API
	 * @throws IOException
	 */
	public static void insertFile(java.io.File imageFile) throws IOException{
		
		Logger static_logger = Logger.getLogger(GdriveAPI .class);
//		Drive service = getDriveService();s
		
		//URL 을 이용하여 File을 upload
		String uploadURL = "https://www.googleapis.com/upload/drive/v2/files?uploadType=media";
		
		URL obj = new URL(uploadURL);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
 
		con.setRequestMethod("POST");
//		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		
		//Sending Image Set
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