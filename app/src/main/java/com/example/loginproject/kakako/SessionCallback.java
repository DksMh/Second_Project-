package com.example.loginproject.kakako;

import com.example.loginproject.Login.LoginActivity;
import com.kakao.auth.ISessionCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

public class SessionCallback implements ISessionCallback {

   LoginActivity loginActivity;

    public SessionCallback(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    @Override
    public void onSessionOpened() {
        UserManagement.getInstance().me(new MeV2ResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {

            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
            }

            @Override
            public void onSuccess(MeV2Response result) {

                System.out.println(result.getKakaoAccount().getProfile().getNickname());
                System.out.println(result.getKakaoAccount().getBirthday());
                System.out.println(result.getKakaoAccount().getCI());
                System.out.println(result.getKakaoAccount().getDisplayId());
                System.out.println(result.getKakaoAccount().getLegalBirthDate());
                System.out.println(result.getKakaoAccount().getLegalGender());
                System.out.println(result.getKakaoAccount().getEmail());
                System.out.println(result.getKakaoAccount().getAgeRange());
                System.out.println(result.getKakaoAccount().getLegalName());



                loginActivity.DirectMove();
            }
        });
    }

    @Override
    public void onSessionOpenFailed(KakaoException exception) {

    }





}
