package com.github.devholic.somareport;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;


public class Login extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.loginBtn)
    RelativeLayout loginBtn;

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
                Intent intent = new Intent(Login.this, Home.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
