package com.github.devholic.SOMAReport.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class FileFactory {
	/*************************************************
	 * InputStream을 File로 변환해주는 메소드
	 * 
	 * @param InputStream
	 *            in
	 * @return File file
	 * @throws IOException
	 *************************************************/
	public static File stream2file(InputStream in) throws IOException {
		final File tempFile = File.createTempFile("stream2file", ".tmp");
		tempFile.deleteOnExit();
		try {
			FileOutputStream fo = new FileOutputStream(tempFile);
			IOUtils.copy(in, fo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tempFile;
	}
}
