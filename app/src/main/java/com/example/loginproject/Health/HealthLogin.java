package com.example.loginproject.Health;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.loginproject.Login.TestActivity;
import com.example.loginproject.R;
import com.example.loginproject.user.UserAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HealthLogin extends AppCompatActivity {

    // 데이터베이스 - 파이어베이스 연동
    private FirebaseAuth mFirebaseAuth;     // 파이어베이스 계정 가져오기
    private FirebaseUser firebaseUser;      // 현재 로그인 한 유저
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스

    // 엑티비티 메인에 있는 것들
    private Button commit;
    private TextView user_name, user_gender, user_age;
    private EditText user_height, user_weight;

    // 운동하기 메인페이지에 정보보내기
//    private  Intent intent;
    // 걷기에도 정보를 보내주자 왜냐하면 걷기에 운동하기 메인으로 돌아오면 user정보가 null된다.
    // 왜냐하면 user정보는 run에는 id와 email만 있으니까 그래서 오류가 발생한다.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("DB");

        // user.java 는 userinfo 하고 user를 하나 더 만들어서 정보를 만들어 놓는다.
        // 유저 이름, 성별, 나이, 키, 몸무게
        user_name = (TextView) findViewById(R.id.user_name);
        user_gender =(TextView) findViewById(R.id.user_gender);
        user_age = (TextView) findViewById(R.id.user_age);
        user_height = (EditText) findViewById(R.id.user_height); // 유저 키 적는 곳
        user_weight = (EditText) findViewById(R.id.user_weight); //유저 몸무게 적는 곳

//      버튼 누르면 값을 저장
        commit = findViewById(R.id.commit); // 키, 몸무게 저장
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserAccount userAccount = snapshot.getValue(UserAccount.class);

                        userAccount.setHeight(user_height.getText().toString());
                        userAccount.setWeight(user_weight.getText().toString());

                        mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(userAccount);
                        Intent intent = new Intent(HealthLogin.this, HealthMain.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        }); // end of commit

        // 엑티비티메인.xml에 데어터 보여주기
        mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserAccount userAccount =snapshot.getValue(UserAccount.class);
                user_name.setText(userAccount.getName());
                user_gender.setText(userAccount.getGender());
                user_age.setText(userAccount.getAge());
                user_height.setText(userAccount.getHeight());
                user_weight.setText(userAccount.getWeight());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    } // end of onCreate;

}
