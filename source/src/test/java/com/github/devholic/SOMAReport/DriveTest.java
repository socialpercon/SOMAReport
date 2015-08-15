package com.github.devholic.SOMAReport;

import java.io.IOException;

import org.junit.Test;

import com.github.devholic.SOMAReport.Controller.DriveController;

public class DriveTest {
	@Test
	public void Drive_CreateCache_Pass() throws IOException {
		DriveController d = new DriveController();
		d.createCache("9d898f7d5bfbf361939e1fafd509b1bf", "0");
	}
}
