package com.zhai.kanzhihu.fragment;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhai.kanzhihu.R;
import com.zhai.kanzhihu.model.AuthorTopAnswers;

import java.util.List;

/**
 * Created by 某宅 on 2016/8/13.
 */
public class AuthorTopanswerFragmentAdapter extends BaseAdapter {

    private List<AuthorTopAnswers> authorTopAnswersList;
    private LayoutInflater inflater;

    public AuthorTopanswerFragmentAdapter(Context context, List<AuthorTopAnswers> data) {
        inflater = LayoutInflater.from(context);
        authorTopAnswersList = data;
    }

    @Override
    public int getCount() {
        return authorTopAnswersList.size();
    }

    @Override
    public Object getItem(int position) {
        return authorTopAnswersList.get(position);
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
            convertView = inflater.inflate(R.layout.author_topanswer_item, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.author_topanswer_title);
            viewHolder.agree = (TextView) convertView.findViewById(R.id.author_topanswer_agree);
            viewHolder.date = (TextView) convertView.findViewById(R.id.author_topanswer_date);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText("标题: " + authorTopAnswersList.get(position).getTitle());
        viewHolder.agree.setText(authorTopAnswersList.get(position).getAgree());
        viewHolder.date.setText(authorTopAnswersList.get(position).getDate());
        return convertView;
    }

    public class ViewHolder {
        TextView title, agree, date;
    }
}
