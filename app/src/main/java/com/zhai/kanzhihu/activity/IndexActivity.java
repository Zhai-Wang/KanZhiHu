package com.zhai.kanzhihu.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.zhai.kanzhihu.R;
import com.zhai.kanzhihu.model.Index;
import com.zhai.kanzhihu.model.IndexAdapter;
import com.zhai.kanzhihu.util.HttpCallbackListener;
import com.zhai.kanzhihu.util.HttpUtil;
import com.zhai.kanzhihu.util.ParseJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 某宅 on 2016/7/25.
 * 首页内容
 */
public class IndexActivity extends Activity {

    private List<Index> indexList = new ArrayList<Index>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index_layout);

        //发送请求，获取首页文章列表
        HttpUtil.sendHttpRequest("http://api.kanzhihu.com/getposts", new HttpCallbackListener(){

            @Override
            public void onFinish(final String response) {
                //解析返回的Json数据
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        indexList = ParseJson.parseIndexJson(response);
                        IndexAdapter indexAdapter = new IndexAdapter(IndexActivity.this, indexList);
                        ListView listView = (ListView) findViewById(R.id.lv_index);
                        listView.setAdapter(indexAdapter);
                    }
                });
            }

            //在主线程中显示错误信息
            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(IndexActivity.this, "Error", Toast.LENGTH_SHORT);
                    }
                });
            }
        });


    }


}
