package com.github.devholic.somareport.restapi;

import com.github.devholic.somareport.data.view.User;

import retrofit.http.POST;

/**
 * Created by JaeyeonLee on 2015. 8. 23..
 */
public interface SOMAReportAPI {

    public static final String BASE_URL = "http://10.0.3.2:8080";

    @POST("/api/login")
    User login();


}
