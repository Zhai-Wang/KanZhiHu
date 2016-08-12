package com.zhai.kanzhihu.model;

/**
 * Created by 某宅 on 2016/8/12.
 */
public class AuthorStar {

    private String answerrank;
    private String agreerank;
    private String ratiorank;
    private String followerrank;
    private String favrank;
    private String count1000rank;
    private String count100rank;

    public AuthorStar(String answerrank, String agreerank, String ratiorank, String followerrank,
                      String favrank, String count1000rank, String count100rank){
        this.answerrank = answerrank;
        this.agreerank = agreerank;
        this.ratiorank = ratiorank;
        this.followerrank = followerrank;
        this.favrank = favrank;
        this.count1000rank = count1000rank;
        this.count100rank = count100rank;
    }

    public String getAnswerrank() {
        return answerrank;
    }

    public String getAgreerank() {
        return agreerank;
    }

    public String getRatiorank() {
        return ratiorank;
    }

    public String getFollowerrank() {
        return followerrank;
    }

    public String getFavrank() {
        return favrank;
    }

    public String getCount1000rank() {
        return count1000rank;
    }

    public String getCount100rank() {
        return count100rank;
    }
}
