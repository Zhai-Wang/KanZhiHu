package com.zhai.kanzhihu.util;

import android.os.Handler;
import android.os.Message;
import android.renderscript.ScriptGroup;
import android.util.Log;

import com.zhai.kanzhihu.model.Answer;
import com.zhai.kanzhihu.model.Index;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * 网络操作的通用方法
 */
public class HttpUtil {


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
                String authorImg = jsonObject.getString("avatar");
                String amount = jsonObject.getString("vote");
                Answer answer = new Answer(answerTitle, authorName, answerContent, authorImg,
                        questionId, answerId, amount);
                answerList.add(answer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return answerList;
    }
}
