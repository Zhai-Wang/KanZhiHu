package com.zhai.kanzhihu.util;

/**
 * Created by 某宅 on 2016/8/10.
 * 下拉刷新的监听器
 */
public interface PullToRefreshListener {

    /**
     * 在此执行具体的刷新方法
     */
    void onRefresh();
}
