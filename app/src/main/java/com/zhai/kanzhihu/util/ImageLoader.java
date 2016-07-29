package com.zhai.kanzhihu.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 某宅 on 2016/7/27.
 * 加载网络图片的方法
 */
public class ImageLoader {

    /**
     * 获取请求得到的图片url,设置给ImageView
     */
    public void loadImage(ImageView imageView, final String url) {
        new indexAsyncTask(imageView, url).execute(url);
    }

    /**
     * 利用AsyncTask来完成异步加载
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
            return getBitmapFromUrl(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imageView.setImageBitmap(bitmap);
        }
    }

    /**
     * 根据url来获取bitmap
     */
    private Bitmap getBitmapFromUrl(String urlString) {

        Bitmap bitmap = null;
        HttpURLConnection connection = null;
        try {
            URL mUrl = new URL(urlString);
            connection = (HttpURLConnection) mUrl.openConnection();
            bitmap = BitmapFactory.decodeStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return bitmap;
    }
}
