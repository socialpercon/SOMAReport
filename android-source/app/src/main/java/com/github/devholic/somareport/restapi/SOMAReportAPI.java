package com.github.devholic.somareport.restapi;

import com.github.devholic.somareport.data.view.User;

import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by JaeyeonLee on 2015. 8. 23..
 */
public interface SOMAReportAPI {

    public static final String BASE_URL = "http://10.0.3.2:8080";

    @POST("/api/login")
    String login();

    @GET("/api/user/{id}")
    User getUserInfo(@Path("id")String userId);


}
