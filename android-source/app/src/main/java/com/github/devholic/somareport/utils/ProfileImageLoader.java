package com.github.devholic.somareport.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.devholic.somareport.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by JaeyeonLee on 2015. 8. 14..
 */
public class ProfileImageLoader {

    private String id;
    CircleImageView circleImageView;

    ImageLoader imageLoader;
    ImageLoaderConfiguration imgConfig;
    DisplayImageOptions displayImgOption;

    public ProfileImageLoader(String id, CircleImageView imageView) {
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

    public void getProfile() {
        imageLoader.init(imgConfig);
        String imgUri = "http://10.0.3.2:8080/drive/user/image?id=" + id + "-profileImage";
        imageLoader.displayImage(imgUri, circleImageView, displayImgOption);
    }

}
