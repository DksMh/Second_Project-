package com.example.loginproject.Community.model;

public class CommentDTO {

    private int no;
    private String comment_user;
    private String comment_userID;
    private String comment_text;
    private String feed_user;
    private int feed_no;
    private String date;

    public CommentDTO(){
        this(0, null,null, null, null, 0, null);
    }

    public CommentDTO(int no, String comment_user, String comment_userID,String comment_text, String feed_user, int feed_no, String date){
        this.no = no;
        this.comment_user = comment_user;
        this.comment_userID = comment_userID;
        this.comment_text = comment_text;
        this.feed_user = feed_user;
        this.feed_no = feed_no;
        this.date = date;
    }


    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getComment_user() {
        return comment_user;
    }

    public void setComment_user(String comment_user) {
        this.comment_user = comment_user;
    }

    public String getComment_userID() {
        return comment_userID;
    }

    public void setComment_userID(String comment_userID) {
        this.comment_userID = comment_userID;
    }

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public String getFeed_user() {
        return feed_user;
    }

    public void setFeed_user(String feed_user) {
        this.feed_user = feed_user;
    }

    public int getFeed_no() {
        return feed_no;
    }

    public void setFeed_no(int feed_no) {
        this.feed_no = feed_no;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "CommentDTo{" +
                "no=" + no +
                ", comment_user='" + comment_user + '\'' +
                ", comment_userID='" + comment_userID + '\'' +
                ", comment_text='" + comment_text + '\'' +
                ", feed_user='" + feed_user + '\'' +
                ", feed_no=" + feed_no +
                '}';
    }
}
