package com.github.devholic.somareport;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class ImageLoaderOld extends AsyncTask<String, Void, Bitmap>{

    final String TAG = "ImageLoaderOld";
    CircleImageView circleView;
    ImageView imageView;
    boolean circle;

    public ImageLoaderOld(CircleImageView i) {
        circle = true;
        this.circleView = i;
    }

    public ImageLoaderOld(ImageView i) {
        imageView = i;
        circle = false;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (circle) {
            circleView.setImageBitmap(bitmap);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int length = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, circleView.getResources().getDisplayMetrics());
            params.width = length;
            params.height = length;
            length = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, circleView.getResources().getDisplayMetrics());
            params.rightMargin = length;
            circleView.setLayoutParams(params);
        }
        else {
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            String imgUri = "http://10.0.2.2:8080/api/drive/image?id=";
            URL url = new URL(imgUri + params[0]);
            Log.d(TAG, url.toString());
            Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());
            return bitmap;
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getLocalizedMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());

        }
        return null;
    }

}
