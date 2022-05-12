package com.example.loginproject.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.loginproject.R;
import com.example.loginproject.user.UserAccount;

public class TestActivity extends AppCompatActivity {

    private UserAccount userInfo;
    private String name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Intent intent = getIntent();

        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");

        System.out.println(name + "\n" + email);

    }
}