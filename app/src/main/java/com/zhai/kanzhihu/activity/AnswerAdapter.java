package com.zhai.kanzhihu.activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhai.kanzhihu.R;
import com.zhai.kanzhihu.model.Answer;
import com.zhai.kanzhihu.model.Index;
import com.zhai.kanzhihu.util.ImageLoader;

import java.util.List;

/**
 * Created by 某宅 on 2016/7/31.
 */
public class AnswerAdapter extends BaseAdapter implements AbsListView.OnScrollListener {

    private List<Answer> answerList;
    private LayoutInflater inflater;
    private ImageLoader imageLoader;
    public static String[] authorImgUrls;//用来集合图片url
    private int start, end;//标志屏幕中起始item的下标
    private Boolean isFirstIn;//记录是否首次启动
    public static String author_address;

    public AnswerAdapter(Context context, List<Answer> data, ListView listView) {
        answerList = data;
        inflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getImageLoader(listView);
        isFirstIn = true;

        //将图片的url转入数组imgUrls中
        authorImgUrls = new String[answerList.size()];
        for (int i = 0; i < answerList.size(); i++) {
            authorImgUrls[i] = answerList.get(i).getAuthorImgUrl();
        }

        listView.setOnScrollListener(this);
    }

    @Override
    public int getCount() {
        return answerList.size();
    }

    @Override
    public Object getItem(int position) {
        return answerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.answer_item, parent, false);
            viewHolder.authorImg = (ImageButton) convertView.findViewById(R.id.author_img);
            viewHolder.answerTitle = (TextView) convertView.findViewById(R.id.answer_title);
            viewHolder.answerContent = (TextView) convertView.findViewById(R.id.answer_content);
            viewHolder.authorName = (TextView) convertView.findViewById(R.id.author_name);
            viewHolder.amount = (TextView) convertView.findViewById(R.id.amount);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //为每一个ImageView设置tag
        viewHolder.authorImg.setTag(answerList.get(position).getAuthorImgUrl());
        //设置显示的图片
        imageLoader.showImg(viewHolder.authorImg, answerList.get(position).getAuthorImgUrl());

        viewHolder.answerTitle.setText(answerList.get(position).getAnswerTitle());
        viewHolder.answerContent.setText(answerList.get(position).getAnswerContent());
        viewHolder.authorName.setText(answerList.get(position).getAuthorName());
        viewHolder.amount.setText(answerList.get(position).getAmount());

        //点击头像，跳转到用户界面
        viewHolder.authorImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               author_address = "http://api.kanzhihu.com/userdetail2/" +
                        answerList.get(position).getAuthorHash();
                Intent intent = new Intent(viewHolder.authorImg.getContext(), AuthorActivity.class);
                intent.putExtra("author_add", author_address);
                viewHolder.authorImg.getContext().startActivity(intent);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        private ImageButton authorImg;
        private TextView answerTitle, authorName, answerContent, amount;
    }

    /**
     * 监听listview的滑动来执行图片加载任务
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            //停止滑动时，加载图片
            imageLoader.loadImageForAnswer(start, end);
        } else {
            //滑动时停止加载任务
            imageLoader.cancelTask();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        start = firstVisibleItem;
        end = firstVisibleItem + visibleItemCount;
        //首次加载时并未滑动，要初始化屏幕内的内容
        if (isFirstIn && visibleItemCount > 0) {
            imageLoader.loadImageForAnswer(start, end);
            isFirstIn = false;
        }
    }
}
