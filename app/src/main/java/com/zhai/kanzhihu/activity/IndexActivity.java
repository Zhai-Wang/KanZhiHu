package com.zhai.kanzhihu.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.zhai.kanzhihu.R;
import com.zhai.kanzhihu.model.Index;
import com.zhai.kanzhihu.util.HttpCallbackListener;
import com.zhai.kanzhihu.util.HttpUtil;
import com.zhai.kanzhihu.view.PullToRefreshListener;
import com.zhai.kanzhihu.view.RefreshableView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 某宅 on 2016/7/25.
 * 首页内容
 */
public class IndexActivity extends Activity implements AdapterView.OnItemClickListener {

    private List<Index> indexList = new ArrayList<>();
    private ListView listView;
    private RefreshableView refreshableView;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.index_layout);
        listView = (ListView) findViewById(R.id.lv_index);
        refreshableView = (RefreshableView) findViewById(R.id.index_refresh);

        progressDialog = new ProgressDialog(IndexActivity.this);
        progressDialog.setMessage("加载中......");
        progressDialog.setCancelable(true);
        progressDialog.show();

        sendRequest();
        //下拉刷新
        refreshableView.setOnRefreshlistener(new PullToRefreshListener() {
            @Override
            public void onRefresh() {
                checkNew();
                refreshableView.finishRefreshing();
            }
        });


    }

    /**
     * 发起请求获得首页数据
     */
    private void sendRequest() {
        //发送请求，获取首页文章列表
        HttpUtil.sendHttpRequest("http://api.kanzhihu.com/getposts", new HttpCallbackListener() {

            @Override
            public void onFinish(final String response) {
                //解析返回的Json数据
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        indexList = HttpUtil.parseIndexJson(response);
                        IndexAdapter indexAdapter = new IndexAdapter(IndexActivity.this,
                                indexList, listView);
                        listView.setAdapter(indexAdapter);
                        listView.setOnItemClickListener(IndexActivity.this);
                        if (indexList.get(listView.getFirstVisiblePosition()).getIndexContent()
                                != null) {
                            progressDialog.dismiss();
                        }
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
     * 判断是否需要刷新数据
     */
    public void checkNew() {
        String address = "http://api.kanzhihu.com/checknew/" + HttpUtil.newestTime;
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String isNew = jsonObject.getString("result");
                    if (isNew == "true") {
                        sendRequest();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(IndexActivity.this, "更新完毕",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(IndexActivity.this, "当前已是最新",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

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