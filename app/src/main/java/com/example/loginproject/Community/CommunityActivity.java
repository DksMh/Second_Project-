package com.example.loginproject.Community;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.loginproject.Community.fragment.FeedFragment;
import com.example.loginproject.Community.fragment.MyPageFragment;
import com.example.loginproject.Community.fragment.SearchFragment;
import com.example.loginproject.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CommunityActivity extends AppCompatActivity {

    static String userName;
    static String userIdToken;

    FirebaseAuth mFirebaseAuth;
    FirebaseUser firebaseUser;

    Fragment fragment0, fragment1, fragment2;
    TabLayout tabs;

    Fragment selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();

        userName = firebaseUser.getEmail();
        userName = userName.substring(0,userName.indexOf('@'));

        userIdToken = firebaseUser.getUid();

        Toolbar toolbar = findViewById(R.id.communityToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("커뮤니티");

        fragment0 = new FeedFragment(CommunityActivity.this);
        fragment1 = new SearchFragment(CommunityActivity.this);
        fragment2 = new MyPageFragment(CommunityActivity.this);

        selected = fragment0;
        getSupportFragmentManager().beginTransaction().add(R.id.frame, fragment0).commit();

        tabs = findViewById(R.id.tabs);
        tabs.setTabGravity(tabs.GRAVITY_FILL);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                if(position == 0){
                    selected = fragment0;
                    getSupportActionBar().setTitle("커뮤니티");
                }else if(position == 1){
                    selected = fragment1;
                    getSupportActionBar().setTitle("검색하기");
                }else if(position == 2){
                    selected = fragment2;
                    getSupportActionBar().setTitle("내 프로필");
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.frame, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static String getUserName(){

        return userName;
    }

    public static String getUserIdToken(){

        return userIdToken;
    }



}