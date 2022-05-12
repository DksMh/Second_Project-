package com.example.loginproject.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginproject.Community.CommunityActivity;
import com.example.loginproject.Food.FoodMainActivity;
import com.example.loginproject.Health.HealthLogin;
import com.example.loginproject.Health.HealthMain;
import com.example.loginproject.R;
import com.example.loginproject.user.UserAccount;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

public class CustomActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;     // 파이어베이스 계정 가져오기
    private FirebaseUser firebaseUser;      // 현재 로그인 한 유저
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스

    private GoogleSignInClient mGoogleSignInClient;     // 구글 로그인 유저

    private View dialogView;
    private EditText edit_name, edit_age, edit_height, edit_weight;
    private EditText d_age;
    private RadioGroup d_radioGroup;
    private ImageButton btn_health, btn_food, btn_community;

    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.loginToolbar);
        setSupportActionBar(toolbar);

        // 파이어베이스 로그인 정보
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("DB");
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();

        userName = firebaseUser.getEmail();
        userName = userName.substring(0,userName.indexOf('@'));

        TextView userText = (TextView) findViewById(R.id.userText);
        userText.setText(userName + "님");

        // 구글 로그인 정보
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail() // email addresses도 요청함
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(CustomActivity.this, gso);

        // 정보가 입력되지 않았을 때 메인페이지에서 바로 입력받기
        mDatabaseRef.child("UserAccount").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserAccount userAccount = snapshot.getValue(UserAccount.class);
                if(userAccount != null) {
                    if(userAccount.getAge() == null || userAccount.getGender() == null ||
                    userAccount.getAge().equals("") || userAccount.getGender().equals("")) {
                        dialogView = (View) View.inflate(CustomActivity.this,
                                R.layout.dialog_extrainfo, null);
                        AlertDialog.Builder dlg = new AlertDialog.Builder(CustomActivity.this);
                        dlg.setTitle("정보입력");
                        dlg.setView(dialogView);
                        d_age = (EditText) dialogView.findViewById(R.id.d_age);
                        d_radioGroup = (RadioGroup) dialogView.findViewById(R.id.d_radioGroup);
                        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String strAge = d_age.getText().toString();
                                int radio = d_radioGroup.getCheckedRadioButtonId();
                                RadioButton rb = dialogView.findViewById(radio);
                                String strGender = rb.getText().toString();
                                userAccount.setAge(strAge);
                                userAccount.setGender(strGender);
                                mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(userAccount);
                                Toast.makeText(CustomActivity.this, "저장완료",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        dlg.show();
                    } // end of if
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // 운동 버튼
        btn_health = findViewById(R.id.btn_health);
        btn_health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        UserAccount userAccount =snapshot.getValue(UserAccount.class);

                        // 유저의 키와 몸무게 값이 없을 때
                        if((userAccount.getHeight() == null || userAccount.getWeight() == null ||
                                userAccount.getHeight().equals("") || userAccount.getWeight().equals("") )){
                            Intent intent = new Intent(CustomActivity.this, HealthLogin.class);
                            startActivity(intent);

                        // 유저의 키와 몸무게 값이 있을 때
                        } else {
                            Intent intent = new Intent(CustomActivity.this, HealthMain.class);
                            startActivity(intent);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        }); // end of btn_health


        // 커뮤 버튼
        btn_community = findViewById(R.id.btn_community);
        btn_community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomActivity.this, CommunityActivity.class);
                startActivity(intent);
            }
        });

        btn_food = findViewById(R.id.btn_food);
        btn_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomActivity.this, FoodMainActivity.class);
                startActivity(intent);

            }
        });
    } //end of OnCreate

    @Override // mymenu 불러오기
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mymenu, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override // item 클릭 이벤트
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.modify:
                mDatabaseRef.child("UserAccount").child(firebaseUser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                         UserAccount userAccount = snapshot.getValue(UserAccount.class);
                         if(userAccount.getHeight() == null || userAccount.getWeight() == null) {

                             dialogView = (View) View.inflate(CustomActivity.this,
                                     R.layout.dialog_modify2, null);
                             AlertDialog.Builder dlg = new AlertDialog.Builder(CustomActivity.this);
                             dlg.setTitle("정보수정");
                             dlg.setView(dialogView);

                             edit_name = (EditText) dialogView.findViewById(R.id.edit_name1);
                             edit_age = (EditText) dialogView.findViewById(R.id.edit_age1);

                             edit_name.setText(userAccount.getName());
                             edit_age.setText(userAccount.getAge());

                             dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialog, int which) {

                                     userAccount.setName(edit_name.getText().toString());
                                     userAccount.setAge(edit_age.getText().toString());

                                     mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(userAccount);
                                     Toast.makeText(getApplicationContext(),"수정완료",Toast.LENGTH_SHORT).show();
                                 }
                             });
                             dlg.setNegativeButton("취소", ((dialog, which) -> {
                                 Toast.makeText(getApplicationContext(),"취소완료", Toast.LENGTH_SHORT).show();
                             }));

                             dlg.show();

                         } else {

                            // 정보수정
                            dialogView = (View) View.inflate(CustomActivity.this, R.layout.dialog_modify, null);
                            AlertDialog.Builder dlg = new AlertDialog.Builder(CustomActivity.this);
                            dlg.setTitle("정보수정");
                            dlg.setView(dialogView);

                            edit_name = (EditText) dialogView.findViewById(R.id.edit_name);
                            edit_age = (EditText) dialogView.findViewById(R.id.edit_age);
                            edit_height = (EditText) dialogView.findViewById(R.id.edit_height);
                            edit_weight = (EditText) dialogView.findViewById(R.id.edit_weight);

                            edit_name.setText(userAccount.getName());
                            edit_age.setText(userAccount.getAge());
                            edit_height.setText(userAccount.getHeight());
                            edit_weight.setText(userAccount.getWeight());

                            dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    userAccount.setName(edit_name.getText().toString());
                                    userAccount.setAge(edit_age.getText().toString());
                                    userAccount.setHeight(edit_height.getText().toString());
                                    userAccount.setWeight(edit_weight.getText().toString());

                                    mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(userAccount);
                                    Toast.makeText(getApplicationContext(),"수정완료",Toast.LENGTH_SHORT).show();
                                }
                            });
                            dlg.setNegativeButton("취소", ((dialog, which) -> {
                                Toast.makeText(getApplicationContext(),"취소완료", Toast.LENGTH_SHORT).show();
                            }));
                            dlg.show();
                         }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                return true;

            case R.id.logout:
                // 로그아웃
                mDatabaseRef.child("UserAccount").child(firebaseUser.getUid())
                        .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserAccount account = snapshot.getValue(UserAccount.class);

                        if(account.getLoginType() == 1){
                            googleSignOut();
                            finish();
                        } else {
                            firebaseSignOut();
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
                return true;

            case R.id.quit:
                // 회원탈퇴

                AlertDialog.Builder quitDlg = new AlertDialog.Builder(CustomActivity.this);
                quitDlg.setMessage("정말 탈퇴하시겠습니까?");

                quitDlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(null);
                        firebaseUser.delete();
                        mFirebaseAuth.signOut();
                        googleSignOut();
                        Toast.makeText(CustomActivity.this, "회원 탈퇴", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CustomActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(CustomActivity.this, "회원탈퇴", Toast.LENGTH_SHORT).show();
                    }
                });

                quitDlg.setNegativeButton("취소",((dialog, which) -> {
                    Toast.makeText(getApplicationContext(),"취소완료", Toast.LENGTH_SHORT).show();
                }));
                quitDlg.show();
                return true;
        } // end of switch
        return false;
    }

    // 파이어베이스 로그아웃
    private void firebaseSignOut(){
        mFirebaseAuth.signOut();
        Toast.makeText(CustomActivity.this, "로그아웃", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(CustomActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    // 구글 로그아웃
    private void googleSignOut() {
        mFirebaseAuth.signOut();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Intent intent = new Intent(CustomActivity.this, LoginActivity.class);
                        startActivity(intent);
                        Toast.makeText(CustomActivity.this, "구글 로그아웃", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 카카오 로그아웃
    private void kakaoSignOut(){
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                // 로그아웃 성공시 수행하는 지점
                Intent intent = new Intent(CustomActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
