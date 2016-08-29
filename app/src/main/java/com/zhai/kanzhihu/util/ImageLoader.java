package com.zhai.kanzhihu.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.renderscript.ScriptGroup;
import android.util.LruCache;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.jakewharton.disklrucache.DiskLruCache;
import com.zhai.kanzhihu.R;
import com.zhai.kanzhihu.activity.AnswerAdapter;
import com.zhai.kanzhihu.activity.IndexAdapter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    private DiskLruCache diskLruCache;
    private DiskLruCache.Editor editor;
    private DiskLruCache.Snapshot snapshot;

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

        //获取 DiskLruCache 的实例
        try {
            File cacheFile = getDiskCacheDir(listView.getContext(), "bitmap");
            if (!cacheFile.exists()) {
                cacheFile.mkdirs();
            }
            diskLruCache = DiskLruCache.open(cacheFile, getAppVersion(listView.getContext()), 1,
                    10 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
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
            ImageView imageView = (ImageView) listView.findViewWithTag(imgUrl);
            //优先从缓存中获取图片
            Bitmap bitmap = getBitmapFromLruCache(imgUrl);
            //若bitmap存在，直接设置
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                //若不存在，开启线程下载
                imgAsyncTask mTask = new imgAsyncTask(imgUrl);
                mTask.execute(imgUrl);
                tasks.add(mTask);
            }
        }
    }

    public void loadImageForAnswer(int start, int end) {
        for (int i = start; i < end; i++) {
            String imgUrl = AnswerAdapter.authorImgUrls[i];
            ImageButton imageButton = (ImageButton) listView.findViewWithTag(imgUrl);
            //优先从缓存获取图片
            Bitmap bitmap = getBitmapFromLruCache(imgUrl);
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
        //优先从缓存获取图片
        Bitmap bitmap = getBitmapFromLruCache(url);
        try {
            if (bitmap == null) {
                snapshot = diskLruCache.get(hashKeyForDisk(url));
                if(snapshot != null){
                    bitmap = BitmapFactory.decodeStream(snapshot.getInputStream(0));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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
            String key = hashKeyForDisk(url);
            Bitmap bitmap = null;
            InputStream in = null;

            try {
                //获取 snapshot 对象
                snapshot = diskLruCache.get(key);
                //如果为空，则需要从网络下载图片，并存入缓存
                if (snapshot == null) {
                    editor = diskLruCache.edit(key);
                    if (editor != null) {
                        OutputStream outputStream = editor.newOutputStream(0);
                        if (downloadUrlToStream(url, outputStream)) {
                            editor.commit();
                        } else {
                            editor.abort();
                        }
                    }
                    //同步文件记录
                    diskLruCache.flush();
                    //下载完成后，重新获取 snapshot 对象
                    snapshot = diskLruCache.get(key);
                }
                //从 snapshot 中获取 bitmap
                if (snapshot != null) {
                    in = snapshot.getInputStream(0);
                    bitmap = BitmapFactory.decodeStream(in);
                }
                //将 bitmap 存入内存缓存中
                if (bitmap != null) {
                    addBitmapToLruCache(url, bitmap);
                }

                return bitmap;

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
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
     * 根据url来下载图片
     */
    private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
        HttpURLConnection connection = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            final URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(connection.getInputStream());
            out = new BufferedOutputStream(outputStream);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return false;
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

    /**
     * 获取缓存目录
     *
     * @param context
     * @param uniqueName 用于区分缓存内容
     * @return
     */
    private File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        //判断 SD 卡是否存在，从而获取不同的缓存地址
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 获取应用版本号
     *
     * @param context
     * @return
     */
    private int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 为了保证文件命名合法，使用 MD5 编码 url
     */
    private String hashKeyForDisk(String url) {
        String cacheKey;
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(url.getBytes());
            cacheKey = byteToHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;
    }

    private String byteToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
