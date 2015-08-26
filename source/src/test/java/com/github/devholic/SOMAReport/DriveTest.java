package com.github.devholic.SOMAReport;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.github.devholic.SOMAReport.Controller.DriveController;

public class DriveTest {

	private final Logger Log = Logger.getLogger(DriveTest.class);

	@Test
	public void testDrive() throws IOException {
		DriveController drive = new DriveController();
		Log.info(drive.getOptimizedStorage());
	}
}