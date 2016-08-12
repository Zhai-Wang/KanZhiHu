package com.zhai.kanzhihu.util;

import com.zhai.kanzhihu.model.Answer;
import com.zhai.kanzhihu.model.Author;
import com.zhai.kanzhihu.model.AuthorDetail;
import com.zhai.kanzhihu.model.AuthorStar;
import com.zhai.kanzhihu.model.AuthorTopAnswers;
import com.zhai.kanzhihu.model.AuthorTrend;
import com.zhai.kanzhihu.model.Index;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Adler32;


/**
 * 网络操作的通用方法
 */
public class HttpUtil {

    public static String newestTime;//记录最新一篇的时间戳

    /**
     * 请求服务器数据
     */
    public static void sendHttpRequest(final String address, final HttpCallbackListener listener) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    if (listener != null) {
                        listener.onFinish(response.toString());
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    /**
     * 解析首页返回的Json数据
     */
    public static List<Index> parseIndexJson(String response) {
        List<Index> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("posts");
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                String indexImgUrl = jsonObject.getString("pic");
                if (i == 0) {
                    newestTime = jsonObject.getString("publishtime");
                }
                String indexTitle = jsonObject.getString("date");
                String indexTag = jsonObject.getString("name");
                String indexContent = jsonObject.getString("excerpt");
                Index index = new Index(indexImgUrl, indexTitle, indexContent, indexTag);
                list.add(index);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 解析答案详情页的Json数据
     */
    public static List<Answer> parseAnswerJson(String response) {
        List<Answer> answerList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("answers");
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                String answerTitle = jsonObject.getString("title");
                String answerContent = jsonObject.getString("summary");
                String questionId = jsonObject.getString("questionid");
                String answerId = jsonObject.getString("answerid");
                String authorName = jsonObject.getString("authorname");
                String authorHash = jsonObject.getString("authorhash");
                String authorImg = jsonObject.getString("avatar");
                String amount = jsonObject.getString("vote");
                Answer answer = new Answer(answerTitle, authorName, answerContent, authorImg,
                        questionId, answerId, amount, authorHash);
                answerList.add(answer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return answerList;
    }

    /**
     * 解析用户基本信息
     */
    public static List<Author> parseAuthorJson(String response) {
        List<Author> authorList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            String authorName = jsonObject.getString("name");
            String authorImgUrl = jsonObject.getString("avatar");
            String authorSig = jsonObject.getString("signature");
            String authorDes = jsonObject.getString("description");
            Author author = new Author(authorImgUrl, authorName, authorSig, authorDes);
            authorList.add(author);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return authorList;
    }

    public static List<AuthorDetail> parseAuthorDetailJson(String response) {
        List<AuthorDetail> authorDetailList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            jsonObject = jsonObject.getJSONObject("detail");
            String ask = jsonObject.getString("ask");
            String answer = jsonObject.getString("answer");
            String post = jsonObject.getString("post");
            String agree = jsonObject.getString("agree");
            String followee = jsonObject.getString("followee");
            String follower = jsonObject.getString("follower");
            String thanks = jsonObject.getString("thanks");
            String fav = jsonObject.getString("fav");
            String mostvote = jsonObject.getString("mostvote");
            AuthorDetail authorDetail = new AuthorDetail(ask, answer, post, agree, followee,
                    follower, thanks, fav, mostvote);
            authorDetailList.add(authorDetail);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return authorDetailList;
    }

    public static List<AuthorStar> parseAuthorStarJson(String response) {
        List<AuthorStar> authorStarList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            jsonObject = jsonObject.getJSONObject("star");
            String answerrank = jsonObject.getString("answerrank");
            String agreerank = jsonObject.getString("agreerank");
            String ratiorank = jsonObject.getString("ratiorank");
            String followerrank = jsonObject.getString("followerrank");
            String favrank = jsonObject.getString("favrank");
            String count1000rank = jsonObject.getString("count1000rank");
            String count100rank = jsonObject.getString("count100rank");
            AuthorStar authorStar = new AuthorStar(answerrank, agreerank, ratiorank,
                    followerrank, favrank, count1000rank, count100rank);
            authorStarList.add(authorStar);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return authorStarList;
    }

    public static List<AuthorTrend> parseAuthorTrendJson(String response) {
        List<AuthorTrend> authorTrendList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("trend");
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                String date = jsonObject.getString("date");
                String answer = jsonObject.getString("answer");
                String agree = jsonObject.getString("agree");
                String follower = jsonObject.getString("follower");
                AuthorTrend authorTrend = new AuthorTrend(date, answer, agree, follower);
                authorTrendList.add(authorTrend);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return authorTrendList;
    }

    public static List<AuthorTopAnswers> parseAuthorTopAnswersJson(String response) {
        List<AuthorTopAnswers> authorTopAnswersList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("topanswers");
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                String title = jsonObject.getString("title");
                String link = jsonObject.getString("link");
                String agree = jsonObject.getString("agree");
                String date = jsonObject.getString("date");
                String ispost = jsonObject.getString("ispost");
                AuthorTopAnswers authorTopAnswers = new AuthorTopAnswers(title, link, agree, date,
                        ispost);
                authorTopAnswersList.add(authorTopAnswers);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return authorTopAnswersList;
    }
}
