package com.zhai.kanzhihu.model;

/**
 * Created by 某宅 on 2016/8/11.
 */
public class Author {

    private String authorImg;
    private String authorName;
    private String authorSig;
    private String authorDes;

    public Author(String authorImg, String authorName, String authorSig, String authorDes){
        this.authorImg = authorImg;
        this.authorName = authorName;
        this.authorSig = authorSig;
        this.authorDes = authorDes;
    }

    public String getAuthorImg() {
        return authorImg;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorSig() {
        return authorSig;
    }

    public String getAuthorDes() {
        return authorDes;
    }
}
