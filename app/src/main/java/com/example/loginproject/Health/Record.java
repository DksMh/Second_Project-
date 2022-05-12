package com.example.loginproject.Health;

import java.io.Serializable;

public class Record {
    private int no;
    private String IdToken;  //아이디
    private int type;       // 런닝, 자전거
    private String num;     // 거리
    private String date;    // 날짜
    private String kcal;    // 칼로리


    public Record() {}

    public Record(int no, String IdToken, int type, String num, String date, String kcal) {
        this.no = no;
        this.IdToken = IdToken;
        this.type = type;
        this.num = num;
        this.date = date;
        this.kcal = kcal;
    }

    public String getIdToken() {
        return IdToken;
    }

    public void setIdToken(String idToken) {
        IdToken = idToken;
    }
    public int getNo() { return no; }

    public void setNo(int no) { this.no = no; }

    public int getType() {return type;}

    public void setType(int type) {this.type = type;}

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKcal() {return kcal;}

    public void setKcal(String kcal) { this.kcal = kcal; }


    @Override
    public String toString() {
        return "Record{" +
                "no=" + no +
                ", IdToken='" + IdToken + '\'' +
                ", type=" + type +
                ", num='" + num + '\'' +
                ", date='" + date + '\'' +
                ", kcal='" + kcal + '\'' +
                '}';
    }
}
