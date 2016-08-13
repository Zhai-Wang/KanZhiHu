package com.zhai.kanzhihu.activity;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhai.kanzhihu.R;
import com.zhai.kanzhihu.fragment.AuthorDetailFragment;
import com.zhai.kanzhihu.fragment.AuthorStarFragment;
import com.zhai.kanzhihu.fragment.AuthorTopAnswerFragment;
import com.zhai.kanzhihu.fragment.AuthorTrendFragment;
import com.zhai.kanzhihu.model.Author;
import com.zhai.kanzhihu.model.AuthorDetail;
import com.zhai.kanzhihu.model.AuthorStar;
import com.zhai.kanzhihu.model.AuthorTopAnswers;
import com.zhai.kanzhihu.model.AuthorTrend;
import com.zhai.kanzhihu.util.HttpCallbackListener;
import com.zhai.kanzhihu.util.HttpUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 某宅 on 2016/8/11.
 */
public class AuthorActivity extends FragmentActivity implements View.OnClickListener,
        ViewPager.OnPageChangeListener {

    private List<Author> authorList;
    private List<Fragment> fragmentList;

    private AuthorDetailFragment authorDetailFragment;
    private AuthorStarFragment authorStarFragment;
    private AuthorTrendFragment authorTrendFragment;
    private AuthorTopAnswerFragment authorTopAnswerFragment;

    private ImageView authorImg;
    private TextView authorName, authorSig, authorDes;
    private Bitmap bitmap;
    private ProgressDialog progressDialog;

    private ViewPager viewPager;
    private TextView tabDetail, tabStar, tabTrend, tabTopanswer;
    private ImageView tabLine;

    private float moveDis;//下划线移动距离
    private Boolean isScrolling=false, isBackScrolling=false;//记录下划线移动状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.author_layout);

        progressDialog = new ProgressDialog(AuthorActivity.this);
        progressDialog.setMessage("加载中......");
        progressDialog.setCancelable(true);
        progressDialog.show();

        Intent intent = getIntent();
        String address = intent.getStringExtra("author_add");
        sendRequest(address);
        initView();
        initLine();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        authorImg = (ImageView) findViewById(R.id.author_img);
        authorName = (TextView) findViewById(R.id.author_name);
        authorSig = (TextView) findViewById(R.id.author_sig);
        authorDes = (TextView) findViewById(R.id.author_des);
        viewPager = (ViewPager) findViewById(R.id.author_viewpager);
        tabDetail = (TextView) findViewById(R.id.tab_detail);
        tabStar = (TextView) findViewById(R.id.tab_star);
        tabTrend = (TextView) findViewById(R.id.tab_trend);
        tabTopanswer = (TextView) findViewById(R.id.tab_topanswer);
        tabLine = (ImageView) findViewById(R.id.tab_line);
        tabDetail.setOnClickListener(this);
        tabStar.setOnClickListener(this);
        tabTrend.setOnClickListener(this);
        tabTopanswer.setOnClickListener(this);
        viewPager.addOnPageChangeListener(this);
    }

    /**
     * 获取网络数据
     *
     * @param address 请求网址
     */
    private void sendRequest(final String address) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {

                authorList = HttpUtil.parseAuthorJson(response);
                authorDetailFragment = AuthorDetailFragment.newInstance(response);
                authorStarFragment = AuthorStarFragment.newInstance(response);
                authorTrendFragment = AuthorTrendFragment.newInstance(response);
                authorTopAnswerFragment = AuthorTopAnswerFragment.newInstance(response);

                //下载头像
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
                        //设置基本信息
                        String name = authorList.get(0).getAuthorName();
                        String sig = authorList.get(0).getAuthorSig();
                        String des = authorList.get(0).getAuthorDes();
                        authorName.setText(name);
                        authorSig.setText(sig);
                        authorDes.setText(des);
                        authorImg.setImageBitmap(bitmap);

                        //添加fragment
                        fragmentList = new ArrayList<>();
                        fragmentList.add(authorDetailFragment);
                        fragmentList.add(authorStarFragment);
                        fragmentList.add(authorTrendFragment);
                        fragmentList.add(authorTopAnswerFragment);
                        AuthorFragmentAdapter authorFragmentAdapter = new AuthorFragmentAdapter(
                                getSupportFragmentManager(), fragmentList);
                        viewPager.setAdapter(authorFragmentAdapter);

                        progressDialog.dismiss();
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

    /**
     * 点击选项卡跳转到相应的page
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_detail:
                viewPager.setCurrentItem(0);
                break;
            case R.id.tab_star:
                viewPager.setCurrentItem(1);
                break;
            case R.id.tab_trend:
                viewPager.setCurrentItem(2);
                break;
            case R.id.tab_topanswer:
                viewPager.setCurrentItem(3);
                break;
        }
    }

    /**
     * 监听viewpager的滑动事件
     */
    //手指滑动时，下划线跟随手指移动
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (isScrolling) {
            movePositionX(position, moveDis * positionOffset);
        }

        if (isBackScrolling) {
            movePositionX(position);
        }
    }

    // 滑动page时改变tab的颜色
    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                tabDetail.setTextColor(Color.BLUE);
                tabStar.setTextColor(Color.BLACK);
                tabTrend.setTextColor(Color.BLACK);
                tabTopanswer.setTextColor(Color.BLACK);
                movePositionX(0);
                break;
            case 1:
                tabDetail.setTextColor(Color.BLACK);
                tabStar.setTextColor(Color.BLUE);
                tabTrend.setTextColor(Color.BLACK);
                tabTopanswer.setTextColor(Color.BLACK);
                movePositionX(1);
                break;
            case 2:
                tabDetail.setTextColor(Color.BLACK);
                tabStar.setTextColor(Color.BLACK);
                tabTrend.setTextColor(Color.BLUE);
                tabTopanswer.setTextColor(Color.BLACK);
                movePositionX(2);
                break;
            case 3:
                tabDetail.setTextColor(Color.BLACK);
                tabStar.setTextColor(Color.BLACK);
                tabTrend.setTextColor(Color.BLACK);
                tabTopanswer.setTextColor(Color.BLUE);
                movePositionX(3);
                break;
            default:
                break;
        }
    }

    //手指滑动时，下划线跟随手指移动
    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case 1:
                isScrolling = true;
                isBackScrolling = false;
                break;
            case 2:
                isScrolling = false;
                isBackScrolling = true;
                break;
            default:
                isScrolling = false;
                isBackScrolling = false;
                break;
        }
    }

    /**
     * 初始化下划线
     */
    private void initLine() {
        //获取屏幕宽度
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        //将tabLine的宽度设定为屏幕的1/4
        ViewGroup.LayoutParams layoutParams = tabLine.getLayoutParams();
        layoutParams.width = screenWidth / 4;
        tabLine.setLayoutParams(layoutParams);

        moveDis = layoutParams.width;
    }

    /**
     * 设置下划线滑动动画
     * 下划线滑动到新的选项卡
     *
     * @param toPosition 下个page的下标
     */
    private void movePositionX(int toPosition) {
        movePositionX(toPosition, 0);
    }

    /**
     * 设置下划线滑动动画
     * 下划线跟随手指滑动
     */
    private void movePositionX(int toPosition, float positionOffsetPixels) {
        float curTranslationX = tabLine.getTranslationX();
        float toPositionX = moveDis * toPosition + positionOffsetPixels;
        ObjectAnimator animator = ObjectAnimator.ofFloat(tabLine, "translationX", curTranslationX,
                toPositionX);
        animator.setDuration(100);
        animator.start();
    }

}
