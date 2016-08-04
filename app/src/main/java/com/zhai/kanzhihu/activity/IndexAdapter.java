package com.zhai.kanzhihu.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhai.kanzhihu.R;
import com.zhai.kanzhihu.model.Index;
import com.zhai.kanzhihu.util.ImageLoader;

import java.util.List;

/**
 * 自定义首页listview的adapter
 * Created by 某宅 on 2016/7/25.
 */
public class IndexAdapter extends BaseAdapter implements AbsListView.OnScrollListener {

    private List<Index> indexList;
    private LayoutInflater inflater;
    private ImageLoader imageLoader;
    public static String[] indexImgUrls;//用来集合图片url
    private int start, end;//标志屏幕中起始item的下标
    private Boolean isFirstIn;//记录是否首次启动

    public IndexAdapter(Context context, List<Index> data, ListView listView) {
        indexList = data;
        inflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getImageLoader(listView);
        isFirstIn = true;

        //将图片的url转入数组imgUrls中
        indexImgUrls = new String[indexList.size()];
        for (int i = 0; i < indexList.size(); i++) {
            indexImgUrls[i] = indexList.get(i).getIndexImgUrl();
        }

        listView.setOnScrollListener(this);
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
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.index_item, parent, false);
            viewHolder.indexImg = (ImageView) convertView.findViewById(R.id.index_item_img);
            viewHolder.indexTitle = (TextView) convertView.findViewById(R.id.index_item_title);
            viewHolder.indexContent = (TextView) convertView.findViewById(R.id.index_item_content);
            viewHolder.indexTag = (TextView) convertView.findViewById(R.id.index_item_tag);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //为每一个ImageView设置tag
        viewHolder.indexImg.setTag(indexList.get(position).getIndexImgUrl());
        //设置显示的图片
        imageLoader.showImg(viewHolder.indexImg, indexList.get(position).getIndexImgUrl());

        viewHolder.indexTitle.setText(indexList.get(position).getIndexTitle());
        viewHolder.indexContent.setText(indexList.get(position).getIndexContent());
        String tag = indexList.get(position).getIndexTag();
        viewHolder.indexTag.setText(getTag(tag));

        return convertView;
    }

    public static String getTag(String tag) {
        switch (tag) {
            case "yesterday":
                return "昨日最新";
            case "recent":
                return "近日热门";
            case "archive":
                return "历史精华";
        }
        return "error";
    }

    private class ViewHolder {
        private ImageView indexImg;
        private TextView indexTitle, indexContent, indexTag;
    }


    /**
     * 监听listview的滑动来执行图片加载任务
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            //停止滑动时，加载图片
            imageLoader.loadImageForIndex(start, end);
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
            imageLoader.loadImageForIndex(start, end);
            isFirstIn = false;
        }
    }
}
