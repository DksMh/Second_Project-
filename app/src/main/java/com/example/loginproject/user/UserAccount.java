package com.example.loginproject.user;

public class UserAccount {

    private String emailId;     // 이메일 아이디
    private String password;    // 비밀번호
    private String name;        // 이름
    private String age;         // 나이
    private String height;
    private String weight;
    private String gender;      // 성별
    private String idToken;     // 토큰
    private int loginType;      // 로그인 타입

    public UserAccount(){ } // firebase에서는 빈 생성자를 꼭 만들어야 DB 조회 할 때 오류가 안남

    public UserAccount(String emailId, String password, String name, String age,
                       String height, String weight, String gender,String idToken,
                       int loginType) {
        this.emailId = emailId;
        this.password = password;
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.idToken = idToken;
        this.loginType = loginType;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getIdToken() {
        return idToken;
    }
    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAge() {
        return age;
    }
    public void setAge(String age) {
        this.age = age;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "emailId='" + emailId + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", height='" + height + '\'' +
                ", weigth='" + weight + '\'' +
                ", gender='" + gender + '\'' +
                ", idToken='" + idToken + '\'' +
                ", loginType=" + loginType +
                '}';
    }
}
