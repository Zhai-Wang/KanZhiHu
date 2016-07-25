package com.zhai.kanzhihu.util;

/**
 * Created by 某宅 on 2016/7/25.
 * 向服务器请求数据的回调接口
 */

public interface HttpCallbackListener{

    void onFinish(String response);

    void onError(Exception e);
}
