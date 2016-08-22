package com.zhai.kanzhihu.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.LruCache;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.zhai.kanzhihu.R;
import com.zhai.kanzhihu.activity.AnswerAdapter;
import com.zhai.kanzhihu.activity.IndexAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by 某宅 on 2016/7/27.
 * 加载网络图片的方法
 */
public class ImageLoader {

    private static ImageLoader imageLoader;
    private ListView listView;
    private Set<imgAsyncTask> tasks;//保存当前异步加载的对象
    private static LruCache<String, Bitmap> memoryCaches;
    private static final String LOCAL_CACHE_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath() + "/ImgCache";//本地文件保存路径

    //私有化构造方法以保证对象全局唯一性
    private ImageLoader(ListView listView) {
        this.listView = listView;
        tasks = new HashSet<>();
        //设定缓存区的大小
        if (memoryCaches == null) {
            int maxMemory = (int) Runtime.getRuntime().maxMemory();
            int cacheSize = maxMemory / 4;
            memoryCaches = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getByteCount();
                }
            };
        }
    }

    //单例化ImageLoader
    public static ImageLoader getImageLoader(ListView mListView) {
        imageLoader = new ImageLoader(mListView);
        return imageLoader;
    }

    /**
     * 获取屏幕中item的下标范围，加载范围中的图片
     */
    public void loadImageForIndex(int start, int end) {
        for (int i = start; i < end; i++) {
            String imgUrl = IndexAdapter.indexImgUrls[i];
            //优先从内存获取图片，若不存在则从本地获取
            Bitmap bitmap = getBitmapFromLruCache(imgUrl);
            if (bitmap == null) {
                bitmap = getBitmapFromLocalCache(imgUrl);
            }
            ImageView imageView = (ImageView) listView.findViewWithTag(imgUrl);
            //若bitmap存在，直接设置
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                //若不存在，开启线程从网络下载
                imgAsyncTask mTask = new imgAsyncTask(imgUrl);
                mTask.execute(imgUrl);
                tasks.add(mTask);
            }
        }
    }

    public void loadImageForAnswer(int start, int end) {
        for (int i = start; i < end; i++) {
            String imgUrl = AnswerAdapter.authorImgUrls[i];
            //优先从内存获取图片，若不存在则从本地获取
            Bitmap bitmap = getBitmapFromLruCache(imgUrl);
            if (bitmap == null) {
                bitmap = getBitmapFromLocalCache(imgUrl);
            }
            ImageButton imageButton = (ImageButton) listView.findViewWithTag(imgUrl);
            //若bitmap存在，直接设置
            if (imageButton != null && bitmap != null) {
                imageButton.setImageBitmap(bitmap);
            } else {
                //若不存在，开启线程下载
                imgAsyncTask mTask = new imgAsyncTask(imgUrl);
                mTask.execute(imgUrl);
                tasks.add(mTask);
            }
        }
    }

    /**
     * 若缓存中存在bitmap，直接设置
     * 反之设置默认的本地图片
     */
    public void showImg(ImageView imageView, String url) {
        //优先从内存获取图片，若不存在则从本地获取
        Bitmap bitmap = getBitmapFromLruCache(url);
        if (bitmap == null) {
            bitmap = getBitmapFromLocalCache(url);
        }
        //若bitmap存在，直接设置
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            //初始化ImageView的图片，未加载网络图片时显示
            imageView.setImageResource(R.mipmap.ic_launcher);
        }
    }

    /**
     * 利用AsyncTask来完成异步加载图片
     */
    private class imgAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private String url;

        public imgAsyncTask(String url) {
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            Bitmap bitmap = getBitmapFromUrl(url);
            if (bitmap != null) {
                addBitmapToLruCache(url, bitmap);//将bitmap添加到缓存中
                addBitmapToLocalCache(url, bitmap);//将bitmap添加到本地
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView imageView = (ImageView) listView.findViewWithTag(url);
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            tasks.remove(this);
        }
    }

    /**
     * 开启网络请求
     * 根据url来获取bitmap
     */
    private Bitmap getBitmapFromUrl(String urlString) {

        Bitmap bitmap = null;
        HttpURLConnection connection = null;
        try {
            URL mUrl = new URL(urlString);
            connection = (HttpURLConnection) mUrl.openConnection();
            bitmap = BitmapFactory.decodeStream(connection.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return bitmap;
    }

    /**
     * 从内存缓存获取bitmap
     */
    private Bitmap getBitmapFromLruCache(String url) {
        return memoryCaches.get(url);
    }

    /**
     * 向内存缓存中添加bitmap
     */
    private void addBitmapToLruCache(String url, Bitmap bitmap) {
        if (getBitmapFromLruCache(url) == null) {
            memoryCaches.put(url, bitmap);
        }
    }

    /**
     * 从本地获取图片
     */
    private Bitmap getBitmapFromLocalCache(String url) {
        Bitmap bitmap = null;
        String fileName = url;
        File file = new File(LOCAL_CACHE_PATH, fileName);//将url作为文件名
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 将图片保存到本地
     */
    private void addBitmapToLocalCache(String url, Bitmap bitmap) {
        String fileName = url;
        File file = new File(LOCAL_CACHE_PATH, fileName);//将url作为文件名
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {//判断父文件是否存在
            parentFile.mkdirs();
        }
        try {
            //保存图片
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 遍历tasks中的对象
     * 取消加载任务
     */
    public void cancelTask() {
        if (tasks != null) {
            for (imgAsyncTask task : tasks) {
                task.cancel(false);
            }
        }
    }
}
