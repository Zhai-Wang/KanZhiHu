package com.zhai.kanzhihu.model;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Created by 某宅 on 2016/8/12.
 */
public class AuthorTopAnswers {

    private String title;
    private String link;
    private String agree;
    private String date;
    private String ispost;

    public AuthorTopAnswers(String title, String link, String agree, String date, String ispost){
        this.title = title;
        this.link = link;
        this.agree = agree;
        this.date = date;
        this.ispost = ispost;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getAgree() {
        return agree;
    }

    public String getDate() {
        return date;
    }

    public String getIspost() {
        return ispost;
    }
}
