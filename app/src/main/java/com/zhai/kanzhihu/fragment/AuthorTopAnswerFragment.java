package com.zhai.kanzhihu.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.zhai.kanzhihu.R;
import com.zhai.kanzhihu.model.AuthorTopAnswers;
import com.zhai.kanzhihu.util.HttpUtil;

import java.util.List;

/**
 * Created by 某宅 on 2016/8/12.
 */
public class AuthorTopAnswerFragment extends Fragment {

    private String response;
    private List<AuthorTopAnswers> authorTopAnswersList;
    private ListView listView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            response = bundle.getString("response");
        }
        authorTopAnswersList = HttpUtil.parseAuthorTopAnswersJson(response);
    }

    public static AuthorTopAnswerFragment newInstance(String response) {
        AuthorTopAnswerFragment authorTopAnswerFragment = new AuthorTopAnswerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("response", response);
        authorTopAnswerFragment.setArguments(bundle);
        return authorTopAnswerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.author_topanswer_layout, null);
        listView = (ListView) view.findViewById(R.id.author_topanswer_listview);
        AuthorTopanswerFragmentAdapter adapter = new AuthorTopanswerFragmentAdapter(
                view.getContext(), authorTopAnswersList);
        listView.setAdapter(adapter);
        return view;
    }
}
