package com.github.devholic.SOMAReport.Model;

import org.junit.Test;

import com.github.devholic.SOMAReport.Database.DocumentUtil;
import com.github.devholic.SOMAReport.Database.ReferenceUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Testtesttest {

	@Test
	public void testGetProjectInfo() {
		ReferenceUtil ref = new ReferenceUtil("somarecord");
		JsonObject info = ref.getProjectInfo("36be054d83f701154adfdd0cf1733874");
		System.out.println(info.toString());
	}


}