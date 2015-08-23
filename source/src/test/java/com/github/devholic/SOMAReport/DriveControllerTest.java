package com.github.devholic.SOMAReport;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.github.devholic.SOMAReport.Controller.DriveController;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DriveControllerTest {

	static String id;

	@Test
	public void A_Drive_UploadImage_Pass() throws IOException {
		DriveController d = new DriveController();
		assertThat(id, is(notNullValue()));
	}

	@Test
	public void B_Drive_GetImage_Pass() throws IOException {
		DriveController d = new DriveController();
		assertThat(d.getImage(id), is(notNullValue()));
	}

	@Test
	public void C_Drive_Cache_Pass() throws IOException {
		DriveController d = new DriveController();
		assertThat(d.getImage(id), is(notNullValue()));
	}

	@Test
	public void D_Drive_Delete_Pass() throws IOException {
		DriveController d = new DriveController();
		assertTrue(d.deleteImage(id));
	}

	@Test
	public void testGetProjectDriveFileInfo() {
		DriveController d = new DriveController();
		System.out.println(d.getProjectDriveFileInfo(
				"9d898f7d5bfbf361939e1fafd5").toString());
	}

	@Test
	public void testUploadProfile() throws IOException {
		DriveController d = new DriveController();
		// assertThat(d.uploadProfileImage("9d898f7d5bfbf361939e1fafd52fb095",
		// new File("9d898f7d5bfbf361939e1fafd52fb095.jpg")),
		// is(notNullValue()),);
	}
}
