package com.example.loginproject.Community.model;

import java.io.Serializable;
import java.util.ArrayList;

public class FeedDTO implements Serializable, Comparable<FeedDTO> {
    private int no;
    private String userID;
    private String userName;
    private String title;
    private String mainText;
    private int feedType;
    private String date;
//    private String imageURL;

    private ArrayList<String> imageList;

    public FeedDTO(){
        this(0,null,null,null,null,1,null,null);
    }

    public FeedDTO(int no, String userID, String userName, String title, String mainText, int feedType, String date, ArrayList<String> imageList){
        this.no = no;
        this.userID = userID;
        this.userName = userName;
        this.title = title;
        this.mainText = mainText;
        this.feedType = feedType;
        this.date = date;
        this.imageList = imageList;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

    public int getFeedType() {
        return feedType;
    }

    public void setFeedType(int feedType) {
        this.feedType = feedType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<String> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<String> imageList) {
        this.imageList = imageList;
    }

    @Override
    public String toString() {
        return "CommunityItemDTO{" +
                "no=" + no +
                ", userID='" + userID + '\'' +
                ", userName='" + userName + '\'' +
                ", title='" + title + '\'' +
                ", mainText='" + mainText + '\'' +
                ", feedType=" + feedType +
                ", date='" + date + '\'' +
                ", imageURL='" + imageList + '\'' +
                '}';
    }



    @Override
    public int compareTo(FeedDTO feed) {
        if(feed.getNo() < no){
            return 1;
        }else if(feed.getNo() > no){
            return -1;
        }
        return 0;
    }
//
//    @Override
//    public int compareTo(FeedDTO feed) {
//        if(feed.no > no){
//            return -1;
//        }else if(feed.no < no){
//            return 1;
//        }
//        return 0;
//    }
}
