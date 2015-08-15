package com.github.devholic.somareport;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class ReportDetails extends AppCompatActivity {

    final String TAG = "Activity_Report_Details";

    @Bind(R.id.report_details_toolbar)
    Toolbar toolbar;

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

    private void setResources() {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setData();
    }

    private void setData() {
        try {
            JSONObject report = new JSONObject(getIntent().getStringExtra("reportdata"));
            String reportId = report.get("id").toString();
            ProfileImageLoader profileImageLoader;
            /*
            * reportId를 통해 report 문서를 JSONObject로 가져온다.
            * {project name, project id, }
            * */
            JSONObject data = new JSONObject("{\"pname\":\"SOMAProject3-2\",\"report\":{\"report_info\":{\"date\":\"20150807\",\"start_time\":[2015,8,7,19,0],\"except_time\":0,\"end_time\":[2015,8,7,23,5],\"place\":\"아람7-6\",\"whole_time\":4,\"total_time\":4,\"mentoring_time\":8},\"report_details\":{\"goal\":\"1차 기간동안 만들 범위를 정한다.\",\"issue\":\"다음주까지 개발 내용을 정해 Trello로 공유한다. - 이슈1: 코드가 Testable하지 않음\",\"solution\":\"단위 테스트 코드 강화. Code Coverage: >60%\",\"etc\":\"필수 항목에 대한 표시가 명확했으면 합니다. 구분이 없음\",\"topic\":\"1차 Scope 정하기\",\"plan\":\"2015.08.11 예정\",\"opinion\":\"먹으로 갈거임......\"}, \"report_attachments\":{ \"photo\":false, \"report_attachments\":[] }, \"mentor\":\"고갹갹\",\"attendee\":[{\"name\":\"a35fa2f4fd6d544d11fae6acf7ba6e01\",\"id\":\"a35fa2f4fd6d544d11fae6acf7ba6e01\"},{\"name\":\"3f4ce9856efb3e2f5d86eeb4d5d16b01\",\"id\":\"3f4ce9856efb3e2f5d86eeb4d5d16b01\"}],\"_rev\":\"1-5661dca3d324e2c2f08a88e7f7e45e3d\",\"project\":\"36be054d83f701154adfdd0cf174e3cc\",\"_id\":\"8c904069f7154e099fb7e7bff70ca3c3\",\"type\":\"report\",\"absentee\":[{\"reason\":\"가족사정으로 인하여 불참\",\"name\":\"이뿅뿅\",\"id\":\"4c44d639b77c290955371694d3310194\"}]},\"pid\":\"36be054d83f701154adfdd0cf174e3cc\",\"rid\":\"8c904069f7154e099fb7e7bff70ca3c3\",\"title\":\"20150807\"}");

            getSupportActionBar().setTitle("#"+data.get("title").toString());
            getSupportActionBar().setSubtitle(data.get("pname").toString());

            JSONObject reportData = new JSONObject(data.get("report").toString());
            JSONObject reportInfo = new JSONObject(reportData.get("report_info").toString());
            JSONObject reportDetails = new JSONObject(reportData.get("report_details").toString());
            JSONArray reportAttendee = new JSONArray(reportData.get("attendee").toString());
            JSONArray reportAbsentee = new JSONArray(reportData.get("absentee").toString());

            title.setText("#"+reportInfo.get("date"));

            for(int i=0; i<reportAttendee.length(); i++) {
                CircleImageView attend = new CircleImageView(this);
                JSONObject att = new JSONObject(reportAttendee.get(i).toString());
//                ImageLoaderOld imageLoader = new ImageLoaderOld(attend);
//                imageLoader.execute(att.get("id").toString());
                profileImageLoader = new ProfileImageLoader(att.getString("id"), attend);
                profileImageLoader.getProfile();
                attendee.addView(attend);
            }

            if (reportAbsentee.length() > 0) {
                for (int i=0; i<reportAbsentee.length(); i++) {
                    CircleImageView absente = new CircleImageView(this);
                    JSONObject abs = new JSONObject(reportAbsentee.get(i).toString());
//                    ImageLoaderOld imageLoader = new ImageLoaderOld(attend);
//                    imageLoader.execute(att.get("id").toString());

                    absentee.addView(absente);

                    //RelativeLayout divider = (RelativeLayout) this.findViewById(R.id.divider);
                    //absentee.addView(divider);

                    TextView reason = new TextView(this);
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

            if (reportData.getJSONObject("report_attachments").getBoolean("photo")) {
//            ImageLoaderOld imageLoader = new ImageLoaderOld(photo);
//            imageLoader.execute(reportData.get("_id").toString());
                Log.i(TAG, reportData.getJSONObject("report_attachments").getBoolean("photo") + "");
                onCreatedDialog(0).show();
            }
            else {
                photo.setImageResource(R.drawable.default_profile);
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



    protected Dialog onCreatedDialog (int id) {
        AlertDialog dialog = null;
        AlertDialog.Builder builder = null;
        final CharSequence[] items = {"촬영하기", "라이브러리에서 가져오기"};

        final AlertDialog.Builder newPhoto = builder.setTitle("새로운 사진 등록하기")
                .setCancelable(true)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // 카메라로 촬영
                                getPhotoByCamera();
                                break;
                            case 1: // 앨범 라이브러리에서 가져오기
                                getPhotoFromLibrary();
                                break;
                        }
                    }
                });

        switch(id) {
            case 0:
                builder.setTitle("등록된 사진이 있습니다.\n수정하시겠습니까?");
                builder.setCancelable(true);
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog = newPhoto.create();
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
               break;
            case 1:
                dialog = newPhoto.create();
                break;
        }
        return dialog;
    }

    public void getPhotoByCamera() {

    }

    public void getPhotoFromLibrary() {

    }

}
