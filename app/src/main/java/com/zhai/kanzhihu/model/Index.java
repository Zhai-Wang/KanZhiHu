package com.zhai.kanzhihu.model;

/**
 * Created by 某宅 on 2016/7/25.
 * 首页文章列表的实体类
 */
public class Index {

    private String indexImgUrl;
    private String indexTitle;
    private String indexContent;
    private String indexTag;

    public Index(String indexImg, String indexTitle, String indexContent, String indexTag){
        this.indexImgUrl = indexImg;
        this.indexTitle = indexTitle;
        this.indexContent = indexContent;
        this.indexTag = indexTag;
    }

    public String getIndexTitle(){
        return indexTitle;
    }

    public String getIndexContent(){
        return indexContent;
    }

    public String getIndexImgUrl(){
        return indexImgUrl;
    }

    public String getIndexTag(){
        return indexTag;
    }

}
