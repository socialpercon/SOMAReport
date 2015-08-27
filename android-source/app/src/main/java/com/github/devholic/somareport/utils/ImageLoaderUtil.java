package com.github.devholic.somareport.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.devholic.somareport.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by JaeyeonLee on 2015. 8. 14..
 */
public class ImageLoaderUtil {

    private String id;
    CircleImageView circleImageView;
    ImageView imageView;

    ImageLoader imageLoader;
    ImageLoaderConfiguration imgConfig;
    DisplayImageOptions displayImgOption;

    public ImageLoaderUtil(String id, CircleImageView imageView) {
        this.id = id;
        this.circleImageView = imageView;
        imageLoader = ImageLoader.getInstance();
        imgConfig = new ImageLoaderConfiguration.Builder(this.circleImageView.getContext()).build();
        displayImgOption = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_profile)
                .showImageOnFail(R.drawable.default_profile)
                .showStubImage(R.drawable.default_profile)
                .bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisc(true).cacheInMemory(true).build();
    }

    public ImageLoaderUtil(String id, ImageView img) {
        this.id = id;
        this.imageView = img;
        imageLoader = ImageLoader.getInstance();
        imgConfig = new ImageLoaderConfiguration.Builder(this.imageView.getContext()).build();
        displayImgOption = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_profile)
                .showImageOnFail(R.drawable.default_profile)
                .showStubImage(R.drawable.default_profile)
                .bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisc(true).cacheInMemory(true).build();
    }

    public void setProfile(int type) {
        if (type!=0) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int length = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, type, circleImageView.getResources().getDisplayMetrics());
            params.width = length;
            params.height = length;
            length = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, circleImageView.getResources().getDisplayMetrics());
            params.rightMargin = length;
            circleImageView.setLayoutParams(params);
        }
        imageLoader.init(imgConfig);
        String imgUri = "http://report.swmaestro.io/drive/user/image?id=" + id + "-profileImage";
        imageLoader.displayImage(imgUri, circleImageView, displayImgOption);
    }

    public void setImageView () {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(params);
        int width = imageView.getWidth();
        int height = imageView.getHeight();
        double scale = (double)height / width;
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = width;
        params.height = (int)(width * scale);
        params.gravity = Gravity.CENTER;
        imageView.setLayoutParams(params);
        imageLoader.init(imgConfig);
        String imgUri = "http://report.swmaestro.io/drive/image?id=" + id;
        Log.i("imageLoader-uri", imgUri);
        imageLoader.displayImage(imgUri, imageView, displayImgOption);
    }
}
