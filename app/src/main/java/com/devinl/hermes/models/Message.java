package com.devinl.hermes.models;

/**
 * Created by Alcha on 2/20/2017.
 */

public class Message {
    private String mUserToken;
    private String mFromName;
    private String mFromNum;
    private String mToNum;
    private String mContent;

    public Message() {
    }

    public Message(String userToken) {
        setUserToken(userToken);
    }

    public String getUserToken() {
        return mUserToken;
    }

    public void setUserToken(String userToken) {
        mUserToken = userToken;
    }

    public String getFromName() {
        return mFromName;
    }

    public void setFromName(String fromName) {
        mFromName = fromName;
    }

    public String getFromNum() {
        return mFromNum;
    }

    public void setFromNum(String fromNum) {
        mFromNum = fromNum;
    }

    public String getToNum() {
        return mToNum;
    }

    public void setToNum(String toNum) {
        mToNum = toNum;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }
}
