package com.github.devholic.somareport;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by JaeyeonLee on 2015. 8. 14..
 */
public class ProfileImageLoader {

    private String url;
    CircleImageView circleImageView;

    ImageLoader imageLoader;
    ImageLoaderConfiguration imgConfig;
    DisplayImageOptions displayImgOption;

    public ProfileImageLoader(String url, CircleImageView imageView) {
        this.url = url;
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
        String imgUri = "http://10.0.2.2:8080/api/drive/image?id=" + url;
        imageLoader.displayImage(imgUri, circleImageView, displayImgOption);
    }

}
