package com.zhai.kanzhihu.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.zhai.kanzhihu.R;
import com.zhai.kanzhihu.model.AuthorTrend;
import com.zhai.kanzhihu.util.HttpUtil;

import java.util.List;

/**
 * Created by 某宅 on 2016/8/12.
 */
public class AuthorTrendFragment extends Fragment {

    private String response;
    private List<AuthorTrend> authorTrendList;
    private ListView listView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            response = bundle.getString("response");
        }
        authorTrendList = HttpUtil.parseAuthorTrendJson(response);
    }

    public static AuthorTrendFragment newInstance(String response) {
        AuthorTrendFragment authorTrendFragment = new AuthorTrendFragment();
        Bundle bundle = new Bundle();
        bundle.putString("response", response);
        authorTrendFragment.setArguments(bundle);
        return authorTrendFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.author_trend_layout, null);
        listView = (ListView) view.findViewById(R.id.author_trend_listview);
        AuthorTrendListViewAdapter adapter = new AuthorTrendListViewAdapter(view.getContext(),
                authorTrendList);
        listView.setAdapter(adapter);
        return view;
    }
}
