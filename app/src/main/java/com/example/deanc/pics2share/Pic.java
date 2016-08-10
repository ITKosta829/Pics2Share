package com.example.deanc.pics2share;

import java.util.ArrayList;

/**
 * Created by DeanC on 7/28/2016.
 */
public class Pic {

    private String picName;
    private ArrayList<String> commentList;
    private String URL;
    private int likes;

    public Pic(String picName, String URL) {
        this.picName = picName;
        this.commentList = new ArrayList<>();
        this.URL = URL;
    }

    public String getPicName() {
        return picName;
    }

    public void setPicName(String picName) {
        this.picName = picName;
    }

    public ArrayList<String> getCommentList() {
        return commentList;
    }

    public void addComment(String comments) {
        commentList.add(comments);
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
