package com.github.devholic.somareport;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.devholic.somareport.data.view.User;
import com.github.devholic.somareport.utils.HttpClientFactory;
import com.github.devholic.somareport.utils.ProfileImageLoader;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class ReportDetails extends AppCompatActivity {

    final String TAG = "Activity_Report_Details";

    final static int GET_PICTURE_GALLERY = 0;
    final static int GET_PICTURE_CAMERA = 1;

    Uri galleryPictureUri = null;
    Uri cameraPictureUri = null;

    private String reportId;
    private JSONObject reportDoc;

    @Bind(R.id.report_details_toolbar)
    Toolbar toolbar;

    // Drawer
    @Bind(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    @Bind(R.id.drawer_view)
    NavigationView navigationView;

    @Bind(R.id.drawer_profile)
    CircleImageView drawerProfile;

    @Bind(R.id.drawer_name)
    TextView drawerName;

    @Bind(R.id.drawer_role)
    TextView drawerRole;

    // Report Details
    @Bind(R.id.report_details_title)
    TextView title;

    @Bind(R.id.report_details_attendee)
    LinearLayout attendee;

    @Bind(R.id.report_details_absentee)
    LinearLayout absentee;

    @Bind(R.id.report_details_number)
    TextView number;

    @Bind(R.id.report_details_place)
    TextView place;

    @Bind(R.id.report_details_time)
    TextView time;

    @Bind(R.id.report_details_topic)
    TextView topic;

    @Bind(R.id.report_details_goal)
    TextView goal;

    @Bind(R.id.report_details_issue)
    TextView issue;

    @Bind(R.id.report_details_solution)
    TextView solution;

    @Bind(R.id.report_details_plan)
    TextView plan;

    @Bind(R.id.report_details_opinion)
    TextView opinion;

    @Bind(R.id.report_details_photo)
    ImageButton photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_details);
        setResources();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void setResources() {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setData();
    }

    private void setData() {
        reportId = getIntent().getStringExtra("reportId");



    }

    private class ReportDetailTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                HttpClient httpClient = HttpClientFactory.getThreadSafeClient();
                HttpGet httpGet = new HttpGet(getString(R.string.api_url) + params[0]);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                int status = httpResponse.getStatusLine().getStatusCode();
                Log.d(TAG, "Response Status:"+status);
                Header[] h = httpResponse.getAllHeaders();
                for (Header hd : h) {
                    Log.i(TAG, hd.toString());
                }
                if (status == 200)
                    return HttpClientFactory.getEntityFromResponse(httpResponse);
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String string) {
            if (string != null) {
                try {
                    JSONObject data = new JSONObject(string);
                    ProfileImageLoader profileImageLoader;
                    String ptitle = getIntent().getStringExtra("pname");

                    JSONObject reportInfo = data.getJSONObject("report_info");
                    JSONObject reportDetails = data.getJSONObject("report_details");
                    JSONArray reportAttendee = data.getJSONArray("attendee");
                    JSONArray reportAbsentee = data.getJSONArray("absentee");

                    title.setText("#"+reportInfo.get("date"));
                    getSupportActionBar().setTitle("#"+reportInfo.get("date"));
                    getSupportActionBar().setSubtitle(ptitle);

                    for(int i=0; i<reportAttendee.length(); i++) {
                        CircleImageView attend = new CircleImageView(attendee.getContext());
                        profileImageLoader = new ProfileImageLoader(reportAttendee.getJSONObject(i).getString("id"), attend);
                        profileImageLoader.getProfile();
                        attendee.addView(attend);
                    }
                    if (reportAbsentee.length() > 0) {
                        for (int i=0; i<reportAbsentee.length(); i++) {
                            CircleImageView absente = new CircleImageView(absentee.getContext());
                            JSONObject abs = new JSONObject(reportAbsentee.get(i).toString());
                            absentee.addView(absente);

                            TextView reason = new TextView(absentee.getContext());
                            String absent = abs.get("name").toString() + " : " + abs.get("reason").toString();
                            Log.i(TAG, absent);
                            reason.setText(absent);
                            reason.setTextColor(getResources().getColor(R.color.textColorSecondary));
                            absentee.addView(reason);
                        }
                    }

                    number.setText(reportInfo.get("mentoring_time").toString()+" 회");
                    place.setText(reportInfo.get("place").toString());
                    time.setText(reportInfo.get("total_time")+"(제외시간 "+reportInfo.get("except_time")+")");

                    topic.setText(reportDetails.get("topic").toString());
                    goal.setText(reportDetails.get("goal").toString());
                    issue.setText(reportDetails.get("issue").toString());
                    solution.setText(reportDetails.get("solution").toString());
                    plan.setText(reportDetails.get("plan").toString());
                    opinion.setText(reportDetails.get("opinion").toString());

                    if (data.has("photo")) {
                        photo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onCreatedDialog(0).show();
                            }
                        });
                    }
                    else {
                        photo.setImageURI(Uri.parse("http://10.0.3.2:8080/drive/user/image?id="+data.getString("photo")));
                        photo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onCreatedDialog(1).show();
                            }
                        });
                    }

                } catch (JSONException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
        }
    }

    protected Dialog onCreatedDialog (int id) {
        final CharSequence[] items = {"촬영하기", "라이브러리에서 가져오기"};

        final AlertDialog.Builder newPhoto = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_AppCompat))
                .setTitle("새로운 사진 등록하기")
                .setCancelable(true)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // 카메라로 촬영
                                getPhotoByCamera();
                                break;
                            case 1: // 앨범 라이브러리에서 가져오기
                                getPhotoFromGallery();
                                break;
                        }
                    }
                });

        final AlertDialog.Builder hasPhoto = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_AppCompat_Dialog_Alert))
                .setTitle("등록된 사진이 있습니다.\n수정하시겠습니까?")
                .setCancelable(true)
                .setPositiveButton("예", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newPhoto.show();
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        switch(id) {
            case 0:
                return hasPhoto.create();
            case 1:
                return newPhoto.create();
        }
        return null;
    }

    public void getPhotoByCamera() {
        Log.i(TAG, "Camera");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String sd = Environment.getExternalStorageDirectory().getAbsolutePath();
        String dirPath = sd + "/android/data/com.github.devholic.somareport/SOMAReport";
        Log.i(TAG, dirPath);
        File f = new File(dirPath);
        if (!f.exists()) {
            f.mkdirs();
            Log.d(TAG, "Directory created.");
        }
        dirPath += "/"+reportId+".jpg";
        f = new File(dirPath);
        cameraPictureUri = Uri.fromFile(f);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPictureUri);
        startActivityForResult(intent, GET_PICTURE_CAMERA);
    }

    public void getPhotoFromGallery() {
        Log.i(TAG, "gallery");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GET_PICTURE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap selected = null;
        Log.i(TAG, resultCode + "");
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == GET_PICTURE_GALLERY) {
                galleryPictureUri = data.getData();
                Log.i(TAG, galleryPictureUri.toString());
                try {
                    selected = MediaStore.Images.Media.getBitmap(getContentResolver(), galleryPictureUri);
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }

            else if (requestCode == GET_PICTURE_CAMERA) {
                try {
                    selected = MediaStore.Images.Media.getBitmap(getContentResolver(), cameraPictureUri);
                    File f = new File(cameraPictureUri.getPath());
                    if (f.exists())  f.delete();
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
        }

        else {
            Log.e(TAG, "Failed to get profile image from" + requestCode);
        }
        if (selected != null) {
            double scale = (double) selected.getHeight() / selected.getWidth();
            photo.setImageBitmap(selected);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.width = photo.getWidth();
            params.height = (int) (photo.getWidth() * scale);
            int margin = (int) (getResources().getDimension(R.dimen.report_detail_photo_margin) * getResources().getDisplayMetrics().density);
            params.setMargins(0, margin, 0, margin);
            params.gravity = Gravity.CENTER;

            photo.setLayoutParams(params);

            /*
            * upload on drive repository (_id.jpg)
            * */

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
    }


}
