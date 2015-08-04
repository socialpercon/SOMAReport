package com.github.devholic.SOMAReport.Model;


import org.apache.log4j.Logger;
import org.junit.Test;

import com.github.devholic.SOMAReport.Database.ReferenceUtil;
import com.google.gson.JsonObject;

public class Testtesttest {

	private final Logger logger = Logger.getLogger(Testtesttest .class);
	
	@Test
	public void testGetProjectInfo() {
		ReferenceUtil ref = new ReferenceUtil("somarecord");
		JsonObject info = ref.getProjectInfo("36be054d83f701154adfdd0cf1733874");
		logger.debug(info.toString());
	}


}
