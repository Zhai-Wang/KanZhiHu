package com.zhai.kanzhihu.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhai.kanzhihu.R;
import com.zhai.kanzhihu.model.Author;
import com.zhai.kanzhihu.util.HttpCallbackListener;
import com.zhai.kanzhihu.util.HttpUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by 某宅 on 2016/8/11.
 */
public class AuthorActivity extends Activity {

    private List<Author> authorList;
    private ImageView authorImg;
    private TextView authorName, authorSig, authorDes;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.author_layout);

        Intent intent = getIntent();
        String address = intent.getStringExtra("author_add");
        sendRequest(address);

        authorImg = (ImageView) findViewById(R.id.author_img);
        authorName = (TextView) findViewById(R.id.author_name);
        authorSig = (TextView) findViewById(R.id.author_sig);
        authorDes = (TextView) findViewById(R.id.author_des);
    }

    private void sendRequest(final String address) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {

                authorList = HttpUtil.parseAuthorJson(response);
                HttpURLConnection connection = null;
                try {
                    URL mUrl = new URL(authorList.get(0).getAuthorImg());
                    connection = (HttpURLConnection) mUrl.openConnection();
                    bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String name = authorList.get(0).getAuthorName();
                        String sig = authorList.get(0).getAuthorSig();
                        String des = authorList.get(0).getAuthorDes();
                        authorName.setText(name);
                        authorSig.setText(sig);
                        authorDes.setText(des);
                        authorImg.setImageBitmap(bitmap);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AuthorActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
