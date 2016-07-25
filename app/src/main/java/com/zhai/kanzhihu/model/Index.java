package com.zhai.kanzhihu.model;

/**
 * Created by 某宅 on 2016/7/25.
 * 首页文章列表的实体类
 */
public class Index {

    private String indexImgUrl;
    private String indexTitle;
    private String indexContent;

    public Index(String indexImg, String indexTitle, String indexContent){
        this.indexImgUrl = indexImg;
        this.indexTitle = indexTitle;
        this.indexContent = indexContent;
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

}
