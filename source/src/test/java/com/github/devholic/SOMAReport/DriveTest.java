package com.github.devholic.SOMAReport;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.github.devholic.SOMAReport.Controller.DriveController;

public class DriveTest {
	@Test
	public void testDrive() throws IOException {
		DriveController drive = new DriveController();
		drive.getProjectImageList("9d898f7d5bfbf361939e1fafd5");
	}
}