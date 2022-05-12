package com.example.loginproject.Health;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class HealthMain extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;     // 파이어베이스 계정 가져오기
    private FirebaseUser firebaseUser;      // 현재 로그인 한 유저
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스

    private TextView user_name , user_gender, user_age , user_height, user_weight, avg_weight , user_bmi;
    private Button walkBtn,recordBtn,bicycleBtn;

    // 표준체중 계산
    private double weightcal, run_user_Hm, user_Hm;

    // BMI 계산
    private String height1, bmiresult;
    private double bmical;

    // 걷기에서도 사용되는 몸무게(칼로리 계산을 위해서 몸무게 정보가 넘어가야한다)
    private String usingRunweight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_main);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("DB");
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();

        user_name = (TextView) findViewById(R.id.user_name);
        // 나이
        user_age = (TextView) findViewById(R.id.user_age);
        // 성별
        user_gender = (TextView) findViewById(R.id.user_gender);
        // 키
        user_height = (TextView) findViewById(R.id.user_height);
        // 몸무게
        user_weight = (TextView) findViewById(R.id.user_weight);
        //표준몸무게
        // 체중 대비 백분율 (PIBW, Percent of Ideal Body Weight) : 체질량지수를 이용
        // 여자 : 신장(m)×신장(m)×21
        // 남자 : 신장(m)×신장(m)×22
        avg_weight = (TextView) findViewById(R.id.avg_weight);
        // BMI
        // 신체질량지수(BMI) = 체중(kg) / (신장(m))2 --> 제곱
        user_bmi = (TextView) findViewById(R.id.user_bmi);

        mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserAccount userAccount = snapshot.getValue(UserAccount.class);

                user_name.setText(userAccount.getName() + " 님의 정보");
                user_gender.setText(userAccount.getGender() );
                user_age.setText(userAccount.getAge() +" 세");
                user_height.setText(userAccount.getHeight() + " cm");
                user_weight.setText(userAccount.getWeight() + " kg");

                //
                // cm를 m로 바꾸기
                height1 = userAccount.getHeight();
                double user_Hcm = Double.parseDouble(height1);
                user_Hm = user_Hcm / 100;

                usingRunweight = userAccount.getWeight();

                if(userAccount.getGender().equals("여성")){ // -> 여성
                    weightcal = user_Hm*user_Hm*21;
                }else{ // -> 남성
                    weightcal = user_Hm*user_Hm*22;
                }

                String avgweightcal = String.format("%.2f", weightcal);
                avg_weight.setText(avgweightcal);

                bmical = Double.parseDouble(usingRunweight) / ((Double.parseDouble(height1) / 100) * (Double.parseDouble(height1) / 100));
                if (bmical < 20) {
                    bmiresult = "저체중";
                } else if (bmical <= 24 && bmical > 20) {
                    bmiresult = "정상";
                } else if (bmical <= 30 && bmical > 24) {
                    bmiresult = "과체중";
                } else {
                    bmiresult = "비만";
                }
                String BMI = String.format("%.2f", bmical);

                user_bmi.setText(BMI + " ( " + bmiresult + " )");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        walkBtn = (Button) findViewById(R.id.walk);
        walkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // HealthMain에서 걷기 페이지로 정보를 보낸다.
                Intent intent= new Intent(HealthMain.this, RunBicycle.class);
                intent.putExtra("choice",(int) 1);
                startActivity(intent);
                finish();
            }
        });

       bicycleBtn = (Button) findViewById(R.id.bicycle);
       bicycleBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent= new Intent(HealthMain.this, RunBicycle.class);
               intent.putExtra("choice",(int) 2);
               startActivity(intent);
               finish();
           }
       });

        // 기록확인
        recordBtn = (Button) findViewById(R.id.record);
        recordBtn.setOnClickListener(new View.OnClickListener() {
            private Object RecordCheck;
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(HealthMain.this, RecordCheck.class);
                startActivity(intent);
            }
        });








    }
}