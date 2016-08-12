package com.zhai.kanzhihu.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhai.kanzhihu.R;
import com.zhai.kanzhihu.model.AuthorTrend;

import java.util.List;

/**
 * Created by 某宅 on 2016/8/12.
 */
public class AuthorTrendListViewAdapter extends BaseAdapter {

    private List<AuthorTrend> authorTrendList;
    private LayoutInflater inflater;

    public AuthorTrendListViewAdapter(Context context, List<AuthorTrend> data) {
        authorTrendList = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return authorTrendList.size();
    }

    @Override
    public Object getItem(int position) {
        return authorTrendList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.author_trend_item, parent, false);
            viewHolder.date = (TextView) convertView.findViewById(R.id.author_trend_date);
            viewHolder.answer = (TextView) convertView.findViewById(R.id.author_trend_answer);
            viewHolder.agree = (TextView) convertView.findViewById(R.id.author_trend_agree);
            viewHolder.follower = (TextView) convertView.findViewById(R.id.author_trend_follower);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.date.setText(authorTrendList.get(position).getDate());
        viewHolder.answer.setText("回答数+专栏文章数: " + authorTrendList.get(position).getAnswer());
        viewHolder.agree.setText("赞同数: " + authorTrendList.get(position).getAgree());
        viewHolder.follower.setText("被关注数: " + authorTrendList.get(position).getFollower());
        return convertView;
    }

    private class ViewHolder {
        TextView date, answer, agree, follower;
    }
}
