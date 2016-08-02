package com.zhai.kanzhihu.model;

/**
 * Created by 某宅 on 2016/7/31.
 * 答案详情页的实体类
 */
public class Answer {

    private String authorImgUrl;
    private String authorName;
    private String answerTitle;
    private String answerContent;
    private String questionId;
    private String answerId;

    public Answer(String answerTitle, String answerName, String answerContent, String answerImgUrl,
                  String questionId, String answerId) {
        this.authorName = answerName;
        this.answerContent = answerContent;
        this.authorImgUrl = answerImgUrl;
        this.answerTitle = answerTitle;
        this.questionId = questionId;
        this.answerId = answerId;
    }

    public String getAnswerContent() {
        return answerContent;
    }

    public String getAuthorImgUrl() {
        return authorImgUrl;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAnswerTitle() {
        return answerTitle;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getAnswerId() {
        return answerId;
    }
}
