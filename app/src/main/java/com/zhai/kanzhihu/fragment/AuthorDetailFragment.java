package com.zhai.kanzhihu.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhai.kanzhihu.R;
import com.zhai.kanzhihu.model.AuthorDetail;
import com.zhai.kanzhihu.util.HttpUtil;

import java.util.List;

/**
 * Created by 某宅 on 2016/8/12.
 */
public class AuthorDetailFragment extends Fragment {

    private String response;
    private TextView ask, answer, post, agree, followee, follower, thanks, fav, mostvote;
    private List<AuthorDetail> authorDetailList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            response = bundle.getString("response");
        }
        authorDetailList = HttpUtil.parseAuthorDetailJson(response);
    }

    public static AuthorDetailFragment newInstance(String response) {
        AuthorDetailFragment authorDetailFragment = new AuthorDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("response", response);
        authorDetailFragment.setArguments(bundle);
        return authorDetailFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.author_detail_layout, null);
        ask = (TextView) view.findViewById(R.id.author_detail_ask);
        answer = (TextView) view.findViewById(R.id.author_detail_answer);
        post = (TextView) view.findViewById(R.id.author_detail_post);
        agree = (TextView) view.findViewById(R.id.author_detail_agree);
        followee = (TextView) view.findViewById(R.id.author_detail_followee);
        follower = (TextView) view.findViewById(R.id.author_detail_follower);
        thanks = (TextView) view.findViewById(R.id.author_detail_thanks);
        fav = (TextView) view.findViewById(R.id.author_detail_fav);
        mostvote = (TextView) view.findViewById(R.id.author_detail_mostvote);


        ask.setText("提问数: " + authorDetailList.get(0).getAsk());
        answer.setText("回答数: " + authorDetailList.get(0).getAnswer());
        post.setText("专栏文章数: " + authorDetailList.get(0).getPost());
        agree.setText("赞同数: " + authorDetailList.get(0).getAgree());
        followee.setText("关注数: " + authorDetailList.get(0).getFollowee());
        follower.setText("被关注数: " + authorDetailList.get(0).getFollower());
        thanks.setText("感谢数: " + authorDetailList.get(0).getThanks());
        fav.setText("收藏数: " + authorDetailList.get(0).getFav());
        mostvote.setText("最高赞同: " + authorDetailList.get(0).getMostvote());

        return view;
    }
}
