package com.zhai.kanzhihu.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.zhai.kanzhihu.R;

/**
 * Created by 某宅 on 2016/8/5.
 * listview的下拉刷新
 */
public class RefreshableView extends LinearLayout implements View.OnTouchListener {

    private static final int STATUS_PULL = 0;//下拉状态
    private static final int STATUS_RELEASE = 1;//释放立即刷新状态
    private static final int STATUS_REFRESHING = 2;//正在刷新状态
    private static final int STATUS_FINISHED = 3;//刷新完成或未刷新状态
    private static final int SCROLL_SPEED = -20;//下拉头部回滚的速度

    private PullToRefreshListener pullToRefreshListener;//下拉刷新的回调接口

    private View header;//下拉头的View
    private ListView listView;//需要去下拉刷新的ListView
    private ProgressBar progressBar;//刷新时显示的进度条
    private ImageView arrow;//指示下拉和释放的箭头
    private MarginLayoutParams headerLayoutParams;//下拉头的布局参数

    private int headerHeight;//下拉头的高度
    private int currentStatus = STATUS_FINISHED;//当前处理什么状态
    private int lastStatus = currentStatus;//记录上一次的状态是什么，避免进行重复操作
    private float yDown;//手指按下时的屏幕纵坐标
    private int touchSlop;//在被判定为滚动之前用户手指可以移动的最大值。

    private boolean loadOnce;//是否已加载过一次layout，这里onLayout中的初始化只需加载一次
    private boolean ableToPull;//当前是否可以下拉，只有ListView滚动到头的时候才允许下拉

    /**
     * 在构造函数中动态添加布局
     */
    public RefreshableView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        header = LayoutInflater.from(context).inflate(R.layout.listview_header, null, true);
        progressBar = (ProgressBar) header.findViewById(R.id.progress_bar);
        arrow = (ImageView) header.findViewById(R.id.arrow);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setOrientation(VERTICAL);
        addView(header, 0);
    }

    /**
     * 注册监听器
     */
    public void setOnRefreshlistener(PullToRefreshListener listener) {
        pullToRefreshListener = listener;
    }

    /**
     * 初始化操作，如隐藏下拉头，注册Touch事件
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && !loadOnce) {
            headerHeight = -header.getHeight();
            headerLayoutParams = (MarginLayoutParams) header.getLayoutParams();
            headerLayoutParams.topMargin = headerHeight;
            listView = (ListView) getChildAt(1);
            listView.setOnTouchListener(this);
            loadOnce = true;
        }
    }

    /**
     * 下拉刷新的具体逻辑
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        setIsAbleToPull(event);
        if (ableToPull) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    yDown = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float yMove = event.getRawY();
                    int distance = (int) (yMove - yDown);
                    //如果手指不是下滑状态，并且下拉头是完全隐藏的，就屏蔽下拉事件
                    if (distance <= 0 && headerLayoutParams.topMargin <= headerHeight) {
                        return false;
                    }
                    if (distance <= touchSlop) {
                        return false;
                    }
                    if (currentStatus != STATUS_REFRESHING) {
                        if (headerLayoutParams.topMargin > 0) {
                            currentStatus = STATUS_RELEASE;
                        } else {
                            currentStatus = STATUS_PULL;
                        }
                        //通过偏移下拉头的topMargin值，来实现下拉效果
                        headerLayoutParams.topMargin = (distance / 2) + headerHeight;
                        header.setLayoutParams(headerLayoutParams);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                default:
                    if (currentStatus == STATUS_RELEASE) {
                        //松手时如果是释放立即刷新状态，就去调用正在刷新的任务
                        new RefreshingTask().execute();
                    } else if (currentStatus == STATUS_PULL) {
                        //松手时如果是下拉状态，就去调用隐藏下拉头的任务
                        new HideHeaderTask().execute();
                    }
                    break;
            }
            //更新下拉头中的信息
            if (currentStatus == STATUS_PULL || currentStatus == STATUS_RELEASE) {
                updateHeaderView();
                //当前正处于下拉或释放状态，要让ListView失去焦点，否则被点击的那一项会一直处于选中状态
                listView.setPressed(false);
                listView.setFocusable(false);
                listView.setFocusableInTouchMode(false);
                lastStatus = currentStatus;
                // 当前正处于下拉或释放状态，通过返回true屏蔽掉ListView的滚动事件
                return true;
            }
        }
        return false;
    }

    /**
     * 刷新完成后调用
     */
    public void finishRefreshing() {
        new HideHeaderTask().execute();
    }

    /**
     * 判断listview的滚动状态
     */
    private void setIsAbleToPull(MotionEvent event) {
        View firstChild = listView.getChildAt(0);
        if (firstChild != null) {
            int firstVisiblePosition = listView.getFirstVisiblePosition();
            //如果首个item距离父布局上端为0，说明listview已滑到最顶端，允许刷新
            if (firstVisiblePosition == 0 && firstChild.getTop() == 0) {
                if (!ableToPull) {
                    yDown = event.getRawY();
                }
                ableToPull = true;
            } else {
                if (headerLayoutParams.topMargin != headerHeight) {
                    headerLayoutParams.topMargin = headerHeight;
                    header.setLayoutParams(headerLayoutParams);
                }
                ableToPull = false;
            }
        } else {
            //若listview中没有元素，也应当可以刷新
            ableToPull = true;
        }
    }

    /**
     * 刷新任务
     */
    private class RefreshingTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            int topMargin = headerLayoutParams.topMargin;
            while (true) {
                topMargin = topMargin + SCROLL_SPEED;
                if (topMargin <= 0) {
                    topMargin = 0;
                    break;
                }
                publishProgress(topMargin);
            }
            currentStatus = STATUS_REFRESHING;
            publishProgress(0);
            if (pullToRefreshListener != null) {
                pullToRefreshListener.onRefresh();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            updateHeaderView();
            headerLayoutParams.topMargin = values[0];
            header.setLayoutParams(headerLayoutParams);
        }
    }

    /**
     * 隐藏下拉头任务
     */
    private class HideHeaderTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            int topMargin = headerLayoutParams.topMargin;
            while (true) {
                topMargin = topMargin + SCROLL_SPEED;
                if (topMargin <= headerHeight) {
                    topMargin = headerHeight;
                    break;
                }
                publishProgress(topMargin);
            }
            return topMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            headerLayoutParams.topMargin = values[0];
            header.setLayoutParams(headerLayoutParams);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            headerLayoutParams.topMargin = integer;
            header.setLayoutParams(headerLayoutParams);
            currentStatus = STATUS_FINISHED;
        }
    }

    /**
     * 更新下拉头信息
     */
    private void updateHeaderView() {
        if (lastStatus != currentStatus) {
            if (currentStatus == STATUS_PULL) {
                arrow.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                rotateArrow();
            } else if (currentStatus == STATUS_RELEASE) {
                arrow.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                rotateArrow();
            } else if (currentStatus == STATUS_REFRESHING) {
                progressBar.setVisibility(View.VISIBLE);
                arrow.clearAnimation();
                arrow.setVisibility(View.GONE);
            }
        }
    }

    /**
     * arrow的旋转动画
     */
    private void rotateArrow() {
        float pivotX = arrow.getWidth() / 2f;
        float pivotY = arrow.getHeight() / 2f;
        float fromDegrees = 0f;
        float toDegrees = 0f;
        if (currentStatus == STATUS_PULL) {
            fromDegrees = 180f;
            toDegrees = 360f;
        } else if (currentStatus == STATUS_RELEASE) {
            fromDegrees = 0f;
            toDegrees = 180f;
        }
        RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees, pivotX, pivotY);
        animation.setFillAfter(true);
        animation.setDuration(100);
        arrow.startAnimation(animation);
    }
}
