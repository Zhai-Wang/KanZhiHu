package com.zhai.kanzhihu.util;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.EventLog;
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
 */
public class RefreshView extends LinearLayout implements View.OnTouchListener {

    private static final int STATUS_PULL_TO_REFRESH = 0;//下拉状态
    private static final int STATUS_RELEASE_TO_REFRESH = 1;//释放立即刷新状态
    private static final int STATUS_REFRESHING = 2;//正在刷新状态
    private static final int STATUS_REFRESH_FINISHED = 3;//刷新完成或未刷新状态
    private static final int SCROLL_SPEED = -20;//下拉头部回滚的速度

    private PullToRefreshListener mListener;//下拉刷新的回调接口

    private View header;//下拉头的View
    private ListView listView;//需要去下拉刷新的ListView
    private ProgressBar progressBar;//刷新时显示的进度条
    private ImageView arrow;//指示下拉和释放的箭头
    private MarginLayoutParams headerLayoutParams;//下拉头的布局参数

    private int mId = -1;//为了防止不同界面的下拉刷新在上次更新时间上互相有冲突，使用id来做区分

    private int headerHeight;//下拉头的高度
    private int currentStatus = STATUS_REFRESH_FINISHED;//当前处理什么状态
    private int lastStatus = currentStatus;//记录上一次的状态是什么，避免进行重复操作

    private float yDown;//手指按下时的屏幕纵坐标
    private int touchSlop;//在被判定为滚动之前用户手指可以移动的最大值。

    private boolean loadOnce;//是否已加载过一次layout，这里onLayout中的初始化只需加载一次
    private boolean ableToPull;//当前是否可以下拉，只有ListView滚动到头的时候才允许下拉

    /**
     * 下拉刷新控件的构造函数，会在运行时动态添加一个下拉头的布局。
     *
     * @param context
     * @param attributeSet
     */
    public RefreshView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        header = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh, null, true);
        progressBar = (ProgressBar) header.findViewById(R.id.progress_bar);
        arrow = (ImageView) header.findViewById(R.id.arrow);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setOrientation(VERTICAL);
        addView(header, 0);
    }

    /**
     * 进行一些关键性的初始化操作，比如：将下拉头向上偏移进行隐藏，给ListView注册touch事件。
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
     * 当ListView被触摸时调用，其中处理了各种下拉刷新的具体逻辑。
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
                    //如果手指是下滑状态，并且下拉头是完全隐藏的，就屏蔽下拉事件
                    if (distance <= 0 && headerLayoutParams.topMargin < headerHeight) {
                        return false;
                    }
                    if (distance < touchSlop) {
                        return false;
                    }
                    if (currentStatus != STATUS_REFRESHING) {
                        if (headerLayoutParams.topMargin > 0) {
                            currentStatus = STATUS_RELEASE_TO_REFRESH;
                        } else {
                            currentStatus = STATUS_PULL_TO_REFRESH;
                        }
                        //通过偏移下拉头的topMargin值，来实现下拉效果
                        headerLayoutParams.topMargin = (distance / 2) + headerHeight;
                        header.setLayoutParams(headerLayoutParams);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                default:
                    if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
                        //松手时如果是释放立即刷新状态，就去调用正在刷新的任务
                        new RefreshingTask().execute();
                    } else if (currentStatus == STATUS_PULL_TO_REFRESH) {
                        //松手时如果是下拉状态，就去调用隐藏下拉头的任务
                        new HideHeaderTask().execute();
                    }
                    break;
            }
            //更新下拉头中的信息
            if (currentStatus == STATUS_PULL_TO_REFRESH ||
                    currentStatus == STATUS_RELEASE_TO_REFRESH) {
                updateHeaderView();
                // 当前正处于下拉或释放状态，要让ListView失去焦点，否则被点击的那一项会一直处于选中状态
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
     * 给下拉刷新控件注册一个监听器。
     *
     * @param listener 监听器的实现。
     * @param id       为了防止不同界面的下拉刷新在上次更新时间上互相有冲突，
     *                 请不同界面在注册下拉刷新监听器时一定要传入不同的id。
     */
    public void setOnRefreshListener(PullToRefreshListener listener, int id) {
        mListener = listener;
        mId = id;
    }

    /**
     * 当所有的刷新逻辑完成后，记录调用一下，否则你的ListView将一直处于正在刷新状态。
     */
    public void finishRefreshing() {
        currentStatus = STATUS_REFRESH_FINISHED;
        new HideHeaderTask().execute();
    }

    /**
     * 根据当前ListView的滚动状态来设定 {@link #ableToPull}
     * 的值，每次都需要在onTouch中第一个执行，这样可以判断出当前应该是滚动ListView，还是应该进行下拉。
     *
     * @param event
     */
    private void setIsAbleToPull(MotionEvent event) {
        View firstChild = listView.getChildAt(0);
        if (firstChild != null) {
            int firstVisiblePos = listView.getFirstVisiblePosition();
            if (firstVisiblePos == 0 && firstChild.getTop() == 0) {
                if (!ableToPull) {
                    yDown = event.getRawY();
                }
                // 如果首个元素的上边缘，距离父布局值为0，就说明ListView滚动到了最顶部，此时应该允许下拉刷新
                ableToPull = true;
            } else {
                if (headerLayoutParams.topMargin != headerHeight) {
                    headerLayoutParams.topMargin = headerHeight;
                    header.setLayoutParams(headerLayoutParams);
                }
                ableToPull = false;
            }
        } else {
            // 如果ListView中没有元素，也应该允许下拉刷新
            ableToPull = true;
        }
    }

    /**
     * 更新下拉头中的信息。
     */
    private void updateHeaderView() {
        if (lastStatus != currentStatus) {
            if (currentStatus == STATUS_PULL_TO_REFRESH || currentStatus == STATUS_RELEASE_TO_REFRESH) {
                arrow.setVisibility(VISIBLE);
                progressBar.setVisibility(GONE);
                rotateArrow();
            } else if (currentStatus == STATUS_REFRESHING) {
                progressBar.setVisibility(VISIBLE);
                arrow.clearAnimation();
                arrow.setVisibility(GONE);
            }
        }
    }

    /**
     * 根据当前的状态来旋转箭头。
     */
    private void rotateArrow() {
        float pivotX = arrow.getWidth() / 2f;
        float pivotY = arrow.getHeight() / 2f;
        float fromDegrees = 0f;
        float toDegrees = 0f;
        if (currentStatus == STATUS_PULL_TO_REFRESH) {
            fromDegrees = 180f;
            toDegrees = 360f;
        } else if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
            fromDegrees = 0f;
            toDegrees = 180f;
        }
        RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees, pivotX, pivotY);
        animation.setDuration(100);
        animation.setFillAfter(true);
        arrow.startAnimation(animation);
    }

    /**
     * 正在刷新的任务，在此任务中会去回调注册进来的下拉刷新监听器。
     */
    class RefreshingTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            int topMagrin = headerLayoutParams.topMargin;
            while (true) {
                topMagrin = topMagrin + SCROLL_SPEED;
                if (topMagrin < 0) {
                    topMagrin = 0;
                    break;
                }
                publishProgress(topMagrin);
            }
            currentStatus = STATUS_REFRESHING;
            publishProgress(0);
            if (mListener != null) {
                mListener.onRefresh();
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
     * 隐藏下拉头的任务，当未进行下拉刷新或下拉刷新完成后，此任务将会使下拉头重新隐藏。
     */
    class HideHeaderTask extends AsyncTask<Void, Integer, Integer> {

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
        protected void onProgressUpdate(Integer... topMargin) {
            headerLayoutParams.topMargin = topMargin[0];
            header.setLayoutParams(headerLayoutParams);
        }

        @Override
        protected void onPostExecute(Integer topMargin) {
            headerLayoutParams.topMargin = topMargin;
            header.setLayoutParams(headerLayoutParams);
            currentStatus = STATUS_REFRESH_FINISHED;
        }
    }

    /**
     * 下拉刷新的监听器，使用下拉刷新的地方应该注册此监听器来获取刷新回调。
     */
    public interface PullToRefreshListener {
        /**
         * 刷新时会去回调此方法，在方法内编写具体的刷新逻辑。注意此方法是在子线程中调用的，
         * 你可以不必另开线程来进行耗时操作。
         */
        void onRefresh();
    }

}
