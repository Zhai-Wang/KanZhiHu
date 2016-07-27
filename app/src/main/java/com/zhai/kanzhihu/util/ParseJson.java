package com.zhai.kanzhihu.util;

import com.zhai.kanzhihu.model.Index;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析收到的Json数据
 * Created by 某宅 on 2016/7/27.
 */
public class ParseJson {


    /**
     * 解析首页返回的Json数据
     *
     * @param response
     * @return
     */
    public static List<Index> parseIndexJson(String response) {
        List<Index> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("posts");
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                String indexImgUrl = jsonObject.getString("pic");
                String indexTitle = jsonObject.getString("date") + "  " + jsonObject.getString("name");
                String indexContent = jsonObject.getString("excerpt");
                Index index = new Index(indexImgUrl, indexTitle, indexContent);
                list.add(index);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
