package com.zhai.kanzhihu.activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhai.kanzhihu.R;
import com.zhai.kanzhihu.model.Index;
import com.zhai.kanzhihu.util.HttpCallbackListener;
import com.zhai.kanzhihu.util.HttpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 某宅 on 2016/7/25.
 * 首页内容
 */
public class IndexActivity extends Activity implements AdapterView.OnItemClickListener {

    private List<Index> indexList = new ArrayList<>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.index_layout);

        //发送请求，获取首页文章列表
        HttpUtil.sendHttpRequest("http://api.kanzhihu.com/getposts", new HttpCallbackListener() {

            @Override
            public void onFinish(final String response) {
                //解析返回的Json数据
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        indexList = HttpUtil.parseIndexJson(response);
                        listView = (ListView) findViewById(R.id.lv_index);
                        IndexAdapter indexAdapter = new IndexAdapter(IndexActivity.this,
                                indexList, listView);
                        listView.setAdapter(indexAdapter);
                        listView.setOnItemClickListener(IndexActivity.this);
                    }
                });
            }

            //在主线程中显示错误信息
            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(IndexActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    /**
     * 处理listview里item的点击事件
     * 跳转到答案详情页
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //拼接答案详情页url
        String name = "/" + indexList.get(position).getIndexTag();
        String time = "/" + indexList.get(position).getIndexTitle().replaceAll("-", "");
        String answerUrl = "http://api.kanzhihu.com/getpostanswers" + time + name;
        String title = indexList.get(position).getIndexTitle() +
                IndexAdapter.getTag(indexList.get(position).getIndexTag());
        Intent intent = new Intent(IndexActivity.this, AnswerActivity.class);
        intent.putExtra("answerUrl", answerUrl);
        intent.putExtra("title", title);
        startActivity(intent);
    }
}
