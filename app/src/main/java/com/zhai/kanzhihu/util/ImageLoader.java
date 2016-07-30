package com.zhai.kanzhihu.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 某宅 on 2016/7/27.
 * 加载网络图片的方法
 */
public class ImageLoader {

    private static ImageLoader imageLoader;
    public static LruCache<String, Bitmap> memoryCaches;

    private ImageLoader() {
        //设定缓存区的大小
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 4;
        memoryCaches = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    public static ImageLoader getImageLoader() {
        if (imageLoader == null) {
            imageLoader = new ImageLoader();
        }
        return imageLoader;
    }

    /**
     * 从url获取bitmap,设置给ImageView
     */
    public void loadImage(ImageView imageView, String url) {

        //若有缓存，优先从缓存中获取bitmap
        Bitmap bitmap = getBitmapFromLruCache(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            new indexAsyncTask(imageView, url).execute(url);//没有就开启线程下载
        }
    }

    /**
     * 利用AsyncTask来完成异步加载图片
     */
    private class indexAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView imageView;
        private String url;

        public indexAsyncTask(ImageView imageView, String url) {
            this.imageView = imageView;
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            Bitmap bitmap = getBitmapFromUrl(url);
            if (bitmap != null) {
                addBitmapToLruCache(url, bitmap);//将bitmap添加到缓存中
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (imageView.getTag().equals(url)) {
                imageView.setImageBitmap(bitmap);
            }
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
     * 从缓存获取bitmap
     */
    public Bitmap getBitmapFromLruCache(String url) {
        return memoryCaches.get(url);
    }

    /**
     * 向缓存中添加bitmap
     */
    public void addBitmapToLruCache(String url, Bitmap bitmap) {
        if (getBitmapFromLruCache(url) == null) {
            memoryCaches.put(url, bitmap);
        }
    }
}
