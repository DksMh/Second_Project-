package com.example.loginproject.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.loginproject.R;
import com.example.loginproject.user.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;     // 파이어베이스 인증
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스
    private EditText regEmail, regPwd, regPwdConfirm ,regName, regAge;      // 회원가입 입력필드
    private RadioGroup radioGroup;
    private Button mBtnRegister;            // 회원가입 버튼
    private String idToken;
    private UserAccount userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        idToken = String.valueOf(System.currentTimeMillis());

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("DB");

        regEmail = findViewById(R.id.reg_email);
        regPwd = findViewById(R.id.reg_pwd);
        regPwdConfirm = findViewById(R.id.reg_pwdConfirm);
        regName = findViewById(R.id.reg_name);
        regAge = findViewById(R.id.reg_age);
        radioGroup = findViewById(R.id.radioGroup);

        mBtnRegister = findViewById(R.id.btn_register);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 처리 시작
                String strEmail = regEmail.getText().toString(); // toString은 문자열로 변환
                String strPwd = regPwd.getText().toString();
                String strPwdConfirm = regPwdConfirm.getText().toString();
                String strName = regName.getText().toString();
                String strAge = regAge.getText().toString();

                int radio = radioGroup.getCheckedRadioButtonId();
                RadioButton rb = findViewById(radio);
                String strGender = rb.getText().toString();

                if(strPwd.equals(strPwdConfirm)){
                    mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd)
                            .addOnCompleteListener(RegisterActivity.this
                                    , new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {

                                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                                UserAccount account = new UserAccount();
                                account.setEmailId(firebaseUser.getEmail());
                                account.setPassword(strPwd);
                                account.setName(strName);
                                account.setAge(strAge);
                                account.setGender(strGender);
                                account.setIdToken(firebaseUser.getUid());
                                account.setLoginType(0);

                                mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);
                                Toast.makeText(RegisterActivity.this, "회원가입에 성공하셨습니다."
                                        ,Toast.LENGTH_SHORT).show();
                                finish();

                            } else {
                                Toast.makeText(RegisterActivity.this, "회원가입에 실패하셨습니다.",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });

                } else {
                    Toast.makeText(RegisterActivity.this, "비밀번호를 확인하세요.",Toast.LENGTH_SHORT).show();
                }

            }
        }); // mBtnRegister 클릭
    }
}