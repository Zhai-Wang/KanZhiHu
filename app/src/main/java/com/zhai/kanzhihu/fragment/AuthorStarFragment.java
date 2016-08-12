package com.zhai.kanzhihu.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhai.kanzhihu.R;
import com.zhai.kanzhihu.model.AuthorStar;
import com.zhai.kanzhihu.util.HttpUtil;

import java.util.List;

/**
 * Created by 某宅 on 2016/8/12.
 */
public class AuthorStarFragment extends Fragment {

    private List<AuthorStar> authorStarList;
    private String response;
    private TextView answerrank, agreerank, ratiorank, followerrank, favrank, count1000rank,
            count100rank;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            response = bundle.getString("response");
        }
        authorStarList = HttpUtil.parseAuthorStarJson(response);
    }

    public static AuthorStarFragment newInstance(String response) {
        AuthorStarFragment authorStarFragment = new AuthorStarFragment();
        Bundle bundle = new Bundle();
        bundle.putString("response", response);
        authorStarFragment.setArguments(bundle);
        return authorStarFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.author_star_layout, null);
        answerrank = (TextView) view.findViewById(R.id.author_star_answerrank);
        agreerank = (TextView) view.findViewById(R.id.author_star_agreerank);
        ratiorank = (TextView) view.findViewById(R.id.author_star_ratiorank);
        followerrank = (TextView) view.findViewById(R.id.author_star_followerrank);
        favrank = (TextView) view.findViewById(R.id.author_star_favrank);
        count1000rank = (TextView) view.findViewById(R.id.author_star_count1000rank);
        count100rank = (TextView) view.findViewById(R.id.author_star_count100rank);
        answerrank.setText("回答数+专栏文章数排名: " + authorStarList.get(0).getAnswerrank());
        agreerank.setText("赞同数排名: " + authorStarList.get(0).getAgreerank());
        ratiorank.setText("平均赞同排名: " + authorStarList.get(0).getRatiorank());
        followerrank.setText("被关注数排名: " + authorStarList.get(0).getFollowerrank());
        favrank.setText("收藏数排名: " + authorStarList.get(0).getFavrank());
        count1000rank.setText("赞同超1000的回答数排名: " + authorStarList.get(0).getCount1000rank());
        count100rank.setText("赞同超100的回答数排名: " + authorStarList.get(0).getCount100rank());
        return view;
    }
}
