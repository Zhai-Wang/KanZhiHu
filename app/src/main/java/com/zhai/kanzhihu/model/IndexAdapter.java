package com.zhai.kanzhihu.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhai.kanzhihu.R;

import java.util.List;

/**
 * 自定义首页listview的adapter
 * Created by 某宅 on 2016/7/25.
 */
public class IndexAdapter extends BaseAdapter {

    private List<Index> indexList;
    private LayoutInflater inflater;

    public IndexAdapter(Context context, List<Index> data){
        indexList = data;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return indexList.size();
    }

    @Override
    public Object getItem(int position) {
        return indexList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.index_item, null);
            viewHolder.indexImg = (ImageView) convertView.findViewById(R.id.index_item_img);
            viewHolder.indexTitle = (TextView) convertView.findViewById(R.id.index_item_title);
            viewHolder.indexContent = (TextView) convertView.findViewById(R.id.index_item_content);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.indexImg.setImageResource(R.mipmap.ic_launcher);
        viewHolder.indexTitle.setText(indexList.get(position).getIndexTitle());
        viewHolder.indexContent.setText(indexList.get(position).getIndexContent());
        return convertView;
    }

    class ViewHolder{
        public ImageView indexImg;
        public TextView indexTitle, indexContent;
    }
}
