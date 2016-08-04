package com.zhai.kanzhihu.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhai.kanzhihu.R;
import com.zhai.kanzhihu.model.Answer;
import com.zhai.kanzhihu.util.HttpCallbackListener;
import com.zhai.kanzhihu.util.HttpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 某宅 on 2016/7/31.
 */
public class AnswerActivity extends Activity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private TextView textView;
    private List<Answer> answerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.answer_layout);

        Intent intent = getIntent();
        String answerUrl = intent.getStringExtra("answerUrl");//接受答案信息的地址
        textView = (TextView) findViewById(R.id.tv_answer_title);
        textView.setText(intent.getStringExtra("title"));

        HttpUtil.sendHttpRequest(answerUrl, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        answerList = HttpUtil.parseAnswerJson(response);
                        listView = (ListView) findViewById(R.id.lv_answer);
                        AnswerAdapter adapter = new AnswerAdapter(AnswerActivity.this,
                                answerList, listView);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(AnswerActivity.this);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AnswerActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String questionId = answerList.get(position).getQuestionId();
        String answerId = answerList.get(position).getAnswerId();
        String answerUrl = "https://www.zhihu.com/question/" + questionId + "/answer/" + answerId;
        Uri uri = Uri.parse(answerUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
