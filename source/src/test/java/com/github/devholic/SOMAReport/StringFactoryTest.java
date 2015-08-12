package com.github.devholic.SOMAReport;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import com.github.devholic.SOMAReport.Utilities.StringFactory;

public class StringFactoryTest {

	@Test
	public void Salt_NotEqual_Pass() {
		String a = StringFactory.createSalt();
		String b = StringFactory.createSalt();
		assertThat(a, not(equalTo(b)));
	}

}
