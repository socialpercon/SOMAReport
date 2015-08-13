package com.github.devholic.SOMAReport.Utilities;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public class StringFactory {

	private final static Logger Log = Logger.getLogger(StringFactory.class);

	public static String createBaseUrl(String address, String port) {
		return address + ":" + port + "/";
	}

	public static String encryptPassword(String password, String salt) {
		String encryptedPassword = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salt.getBytes());
			byte[] bytes = md.digest(password.getBytes());
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16)
						.substring(1));
			}
			encryptedPassword = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			Log.error(e.getMessage());
		}
		return encryptedPassword;
	}

	public static String createSalt() {
		final Random r = new SecureRandom();
		byte[] salt = new byte[32];
		r.nextBytes(salt);
		return Base64.encodeBase64String(salt);
	}

	public static String inputStreamToString(InputStream is) {
		try {
			StringBuffer sb = new StringBuffer();
			byte[] b = new byte[4096];
			for (int n; (n = is.read(b)) != -1;) {
				sb.append(new String(b, 0, n));
			}
			is.close();
			return sb.toString();
		} catch (IOException e) {
			Log.error(e.getMessage());
			return null;
		}
	}
}
