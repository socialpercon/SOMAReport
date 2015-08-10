package com.github.devholic.somareport;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class Login extends AppCompatActivity implements View.OnClickListener {

    final String TAG = "Activity_Login";
    String id="";

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
                Log.d(TAG, "login btn clicked");

                LoginTask loginTask = new LoginTask();
                loginTask.execute(email, pwd);
                break;
        }
    }

    private class LoginTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String urlst = "http://10.0.2.2:8080/login";

                URL url = new URL(urlst);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                String body = "email="+params[0]+"&password="+params[1];
                Log.d(TAG, body);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Accept", "text/html");
                connection.setRequestProperty("Content-Type", "text/html;charset=UTF-8");
                connection.setRequestProperty("User-Agent", "Android Application");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                OutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());
                outputStream.write(body.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                connection.connect();

                int statusCode = connection.getResponseCode();
                Log.d(TAG, "RESPONSECODE: "+Integer.toString(statusCode));
                StringBuilder responseStringBuilder = new StringBuilder();
                if (statusCode == HttpURLConnection.HTTP_OK){
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    for (;;){
                        String stringLine = bufferedReader.readLine();
                        if (stringLine == null ) break;
                        responseStringBuilder.append(stringLine + '\n');
                    }
                    bufferedReader.close();
                    Log.d(TAG, responseStringBuilder.toString());
                }

                connection.disconnect();

//                HttpClient httpClient= new DefaultHttpClient();
//                HttpPost httpPost = new HttpPost("http://10.0.2.2:8080/login");
//                List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
//                nameValuePair.add(new BasicNameValuePair("email", params[0]));
//                nameValuePair.add(new BasicNameValuePair("password", params[1]));
//
//                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
//
//                HttpResponse httpResponse = httpClient.execute(httpPost);
//                Log.d(TAG, httpResponse.toString());
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
                startActivity(intent);
            }
            else {
               // Log.d(TAG, "Log in FAIL");
            }
        }
    }
}
