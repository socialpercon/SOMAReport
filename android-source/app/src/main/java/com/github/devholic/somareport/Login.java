package com.github.devholic.somareport;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.http.utils.ResponseUtils;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.ResponseContent;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class Login extends AppCompatActivity implements View.OnClickListener {

    final String TAG = "Activity_Login";
    String id="";
    String cookie;

    @Bind(R.id.loginBtn)
    Button loginBtn;

    @Bind(R.id.login_account)
    EditText account;

    @Bind(R.id.login_password)
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        setResources();
    }

    private void setResources() {
        ButterKnife.bind(this);
        loginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                if (account.getText().length()==0 || password.getText().length()==0) {
                    // account나 password가 공란인 경우
                    Log.d(TAG, "login failed - empty info");
                  // break;
                }

                String email = account.getText().toString();
                String pwd = password.getText().toString();
                Log.d(TAG, "email: " + email + " password: " + pwd + " login btn clicked");

            //    LoginTask loginTask = new LoginTask();
            //    loginTask.execute(email, pwd);
                logInAccess(email, pwd);
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
    }

    private void logInAccess (String email, String pwd) {
        String url = "http://10.0.3.2:8080/api/login";
        try {
            com.mashape.unirest.http.HttpResponse<JsonNode> httpResponse = Unirest.post(url)
                    .field("email", email).field("password", pwd).asJson();
            Log.i(TAG, httpResponse.getStatus()+httpResponse.getStatusText());
            if (httpResponse.getStatus() == 200) {
                Intent intent = new Intent(Login.this, ProjectList.class);
                intent.putExtra("cookie", cookie);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
            } else {
                // 로그인 실패

            }

        } catch (UnirestException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    private class LoginTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String url = "http://10.0.3.2:8080/api/login";
                HttpClient httpClient= new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
                nameValuePair.add(new BasicNameValuePair("email", params[0]));
                nameValuePair.add(new BasicNameValuePair("password", params[1]));
                Log.i(TAG, nameValuePair.toString());
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

                HttpResponse httpResponse = httpClient.execute(httpPost);
                Header[] headers = httpResponse.getAllHeaders();
                for(Header h : headers) {
                    System.out.println("Key : " + h.getName()
                            + " ,Value : " + h.getValue());
                    if (h.getName().equals("Set-Cookie"))
                        cookie = h.getValue();
                }

                return true;
            }
            catch (MalformedURLException e) {
                Log.e(TAG, e.getLocalizedMessage());
                e.printStackTrace();
            }
            catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
                e.printStackTrace();
            }
            catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
              //  Log.d(TAG, "Log in SUCCESS");
                Intent intent = new Intent(Login.this, ProjectList.class);
                id = "4c44d639b77c290955371694d3310194";
                intent.putExtra("userId",id);
                intent.putExtra("cookie", cookie);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
            }
            else {
               // Log.d(TAG, "Log in FAIL");
            }
        }
    }
}
