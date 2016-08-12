package com.zhai.kanzhihu.model;

/**
 * Created by 某宅 on 2016/8/12.
 */
public class AuthorDetail {

    private String ask;
    private String answer;
    private String post;
    private String agree;
    private String followee;
    private String follower;
    private String thanks;
    private String fav;
    private String mostvote;

    public AuthorDetail(String ask,String answer,String post,String agree,String followee,
            String follower,String thanks,String fav,String mostvote) {
        this.ask = ask;
        this.answer = answer;
        this.post = post;
        this.agree = agree;
        this.followee = followee;
        this.follower = follower;
        this.thanks = thanks;
        this.fav = fav;
        this.mostvote = mostvote;
    }

    public String getAsk() {
        return ask;
    }

    public String getAnswer() {
        return answer;
    }

    public String getPost() {
        return post;
    }

    public String getAgree() {
        return agree;
    }

    public String getFollowee() {
        return followee;
    }

    public String getFollower() {
        return follower;
    }

    public String getThanks() {
        return thanks;
    }

    public String getFav() {
        return fav;
    }

    public String getMostvote() {
        return mostvote;
    }
}
