package com.example.loginproject.Login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.loginproject.R;
import com.example.loginproject.kakako.SessionCallback;
import com.example.loginproject.user.UserAccount;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.usermgmt.LoginButton;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    // 로그인한 계정 정보 저장
    private static UserAccount userInfo;

    // 파이어베이스 로그인
    private FirebaseAuth mFirebaseAuth;              // 파이어베이스 인증
    private DatabaseReference mDatabaseRef;          // 실시간 데이터베이스
    private EditText loginEamil, loginPwd;           // 로그인 입력필드


    // 카카오 로그인
    private ISessionCallback mSessionCallback; // 로그인 관리
    private SessionCallback sessionCallback;
    private ImageButton btn_kakao;
    private LoginButton loginButton;

    // 구글 로그인
    private SignInButton signBt;                     // 구글 로그인 버튼
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 123;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("DB");

        sessionCallback = new SessionCallback(this);

//        Session.getCurrentSession().checkAndImplicitOpen();
//        Session.getCurrentSession().addCallback(sessionCallback);

        loginEamil = findViewById(R.id.login_email);
        loginPwd= findViewById(R.id.login_pwd);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("811827780739-rsbjneaofnecgd30fhhnpk9klo1cvb0q.apps.googleusercontent.com")
                .requestEmail() // email addresses도 요청함
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // 구글 로그인
        signBt = findViewById(R.id.btn_google);
        signBt.setSize(SignInButton.SIZE_STANDARD);
        signBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        // 카카오에서 주는 로그인 버튼
//        loginButton = findViewById(R.id.loginbtn);
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(Session.getCurrentSession().checkAndImplicitOpen()){
//                    Session.getCurrentSession().addCallback(sessionCallback);
//                }
//            }
//        });

        // 카카오 커스텀 버튼
        btn_kakao = findViewById(R.id.btn_kakako);
        btn_kakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Session.getCurrentSession().checkAndImplicitOpen()){
                    Session.getCurrentSession().addCallback(sessionCallback);
                } else {
                    Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, LoginActivity.this);
                    Session.getCurrentSession().addCallback(sessionCallback);
                }
            }
        });

        // 로그인 버튼
        Button btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmail = loginEamil.getText().toString();
                String strPwd = loginPwd.getText().toString();

                mFirebaseAuth.signInWithEmailAndPassword(strEmail,strPwd).addOnCompleteListener(LoginActivity.this,
                        new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            Intent intent = new Intent(LoginActivity.this, CustomActivity.class);
                            startActivity(intent);
                            Toast.makeText(LoginActivity.this,"로그인성공",
                                    Toast.LENGTH_SHORT).show();
                            finish();

                        } else {
                            Toast.makeText(LoginActivity.this, "로그인 실패",
                                    Toast.LENGTH_SHORT).show();
                        }


                    }
                });
            }
        });

        // 회원가입 버튼
        Button btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 화면으로 이동
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    } //End of onCreate

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 카카오 세션 콜백 삭제
        Session.getCurrentSession().removeCallback(mSessionCallback);
    }

    //  카카오, 구글 메소드 정보 받아오기 메소드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(Session.getCurrentSession().handleActivityResult(requestCode,resultCode,data)){
        }
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                            mDatabaseRef.child("UserAccount").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    // 키 값을 저장하기 위한 컬렉션
                                    List list = new ArrayList();
                                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        list.add(dataSnapshot.getKey());
                                    }
                                    if(list.contains(firebaseUser.getUid())){
                                        System.out.println("저장 안되었음!");
                                        return;
                                    } else {
                                        UserAccount account = new UserAccount();
                                        account.setEmailId(firebaseUser.getEmail());
                                        account.setName(firebaseUser.getDisplayName());
                                        account.setIdToken(firebaseUser.getUid());
                                        account.setLoginType(1);

                                        mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);
                                        System.out.println("저장 되었음!");
                                        return;
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            Toast.makeText(LoginActivity.this, "구글로그인 성공", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, CustomActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }


//  카카오 로그인 메소드
//  카카오 로그인 시 필요한 해시키를 얻는 메소드
////    private void getAppKeyHash() {
////        try{
////            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
////            for(Signature signature : info.signatures) {
////                MessageDigest md;
////                md = MessageDigest.getInstance("SHA");
////                md.update(signature.toByteArray());
////                String something = new String(Base64.encode(md.digest(), 0));
//                Log.e("Hash key", something);
//            }
//        }catch (Exception e ){
//            Log.e("name not found", e.toString());
//        }
//    }


    //  구글 로그인 메소드
    private void signIn() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);

            if (acct != null) {
                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();
                String personIdToken = acct.getIdToken();

                Log.d(TAG, "handleSignInResult:personName "+personName);
                Log.d(TAG, "handleSignInResult:personGivenName "+personGivenName);
                Log.d(TAG, "handleSignInResult:personEmail "+personEmail);
                Log.d(TAG, "handleSignInResult:personId "+personId);
                Log.d(TAG, "handleSignInResult:personFamilyName "+personFamilyName);
                Log.d(TAG, "handleSignInResult:personIdToken "+  personIdToken);
                Log.d(TAG, "handleSignInResult:personPhoto "+personPhoto);

                Toast.makeText(this,
                        "구글 로그인 성공", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, CustomActivity.class);
                startActivity(intent);
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());

        }
    }

    // 카카오 sessioncallback 에서 CustomActivity 로 넘어가기
    public void DirectMove(){
        Intent intent = new Intent(LoginActivity.this, CustomActivity.class);
        startActivity(intent);
        Toast.makeText(LoginActivity.this, "카카오 로그인 성공", Toast.LENGTH_SHORT).show();
    }

    // 로그인 정보 저장하기
    public static UserAccount getInstance(){
        return userInfo;
    }

}





