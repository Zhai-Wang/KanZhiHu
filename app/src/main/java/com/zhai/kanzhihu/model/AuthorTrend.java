package com.zhai.kanzhihu.model;

/**
 * Created by 某宅 on 2016/8/12.
 */
public class AuthorTrend {

    private String date;
    private String answer;
    private String agree;
    private String follower;

    public AuthorTrend(String date, String answer, String agree, String follower){
        this.date = date;
        this.answer = answer;
        this.agree = agree;
        this.follower = follower;
    }

    public String getDate() {
        return date;
    }

    public String getAnswer() {
        return answer;
    }

    public String getAgree() {
        return agree;
    }

    public String getFollower() {
        return follower;
    }
}
