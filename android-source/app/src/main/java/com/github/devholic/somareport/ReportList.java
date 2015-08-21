package com.github.devholic.somareport;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ReportList extends AppCompatActivity {

    final String TAG = "Activity_ReportList";

    // Toolbar
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    // Content
    @Bind(R.id.report_list_recycler)
    RecyclerView recyclerView;

    private DetailRecyclerViewAdapter adapter;
    private MenuItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_list);
        setResources();
    }

    private void setResources() {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        setData();
    }

    private void setData() {
        JSONArray list = new JSONArray();
        try {
            JSONObject data = new JSONObject(getIntent().getStringExtra("projectdata"));
            getSupportActionBar().setTitle(data.get("title").toString());
            getSupportActionBar().setSubtitle("멘토링 보고서 리스트");
            String pid =  data.get("id").toString();

            data = new JSONObject();
            /*
            * localhost:8080/report/list/{pid}를 통해 JSONObject로 data에 가져온다
            * {프로젝트id, title, reportList[report id, title, topic, attendee]}
            * */
            data = new JSONObject("{\"pname\":\"SOMAProject 2nd phase\",\"reportList\":[{\"attendee\":[\"4c44d639b77c290955371694d3310194\",\"36be054d83f701154adfdd0cf1019d20\",\"36be054d83f701154adfdd0cf1100e37\"],\"reportTitle\":\"20150809\",\"id\":\"955f8962d6fc4f6586893cbad1048880\",\"reportTopic\":\"핳 멘토링 횟수 추가 확인\"},{\"attendee\":[\"4c44d639b77c290955371694d3310194\",\"36be054d83f701154adfdd0cf1019d20\",\"36be054d83f701154adfdd0cf1100e37\"],\"reportTitle\":\"20150809\",\"id\":\"e96aa842886c44d38654b9f1eb33355e\",\"reportTopic\":\"오늘 저녁을 어떤걸 먹을지\"},{\"attendee\":[\"4c44d639b77c290955371694d3310194\",\"36be054d83f701154adfdd0cf1019d20\",\"36be054d83f701154adfdd0cf1100e37\"],\"reportTitle\":\"20150809\",\"id\":\"d5af456c1b2f4bffbd9845a13928b60a\",\"reportTopic\":\"오늘 저녁을 어떤걸 먹을지\"},{\"attendee\":[\"4c44d639b77c290955371694d3310194\",\"36be054d83f701154adfdd0cf1019d20\",\"36be054d83f701154adfdd0cf1100e37\"],\"reportTitle\":\"20150809\",\"id\":\"bc51338214f44822b7a60cda4beda99c\",\"reportTopic\":\"오늘 저녁을 어떤걸 먹을지\"},{\"attendee\":[\"4c44d639b77c290955371694d3310194\",\"36be054d83f701154adfdd0cf1019d20\",\"36be054d83f701154adfdd0cf1100e37\"],\"reportTitle\":\"20150809\",\"id\":\"b13695dd5c064a3f8f38e79719f2e0b4\",\"reportTopic\":\"오늘 저녁을 어떤걸 먹을지\"},{\"attendee\":[\"4c44d639b77c290955371694d3310194\",\"36be054d83f701154adfdd0cf1019d20\",\"36be054d83f701154adfdd0cf1100e37\"],\"reportTitle\":\"20150809\",\"id\":\"7f1ae3dbc2994ff593b12e11ba0085a6\",\"reportTopic\":\"오늘 저녁을 어떤걸 먹을지\"},{\"attendee\":[\"4c44d639b77c290955371694d3310194\",\"36be054d83f701154adfdd0cf1019d20\",\"36be054d83f701154adfdd0cf1100e37\"],\"reportTitle\":\"20150809\",\"id\":\"2b77c7d091f748259e6d9c4ca7b29e81\",\"reportTopic\":\"오늘 저녁을 어떤걸 먹을지\"},{\"attendee\":[\"4c44d639b77c290955371694d3310194\",\"36be054d83f701154adfdd0cf1019d20\",\"36be054d83f701154adfdd0cf1100e37\"],\"reportTitle\":\"20150809\",\"id\":\"27173e3d84244954b3b95c3c9e8d5b11\",\"reportTopic\":\"오늘 저녁을 어떤걸 먹을지\"},{\"attendee\":[\"4c44d639b77c290955371694d3310194\",\"36be054d83f701154adfdd0cf1019d20\",\"36be054d83f701154adfdd0cf1100e37\"],\"reportTitle\":\"20150809\",\"id\":\"0bc712895165413c9d8b7738e0a7f706\",\"reportTopic\":\"오늘 저녁을 어떤걸 먹을지\"},{\"attendee\":[\"36be054d83f701154adfdd0cf1019d20\",\"36be054d83f701154adfdd0cf1100e37\"],\"reportTitle\":\"20150809\",\"id\":\"c42c56b44acf4b44855f74e1ba216540\",\"reportTopic\":\"멘토링 횟수 제대로 되는지 확인\"},{\"attendee\":[\"4c44d639b77c290955371694d3310194\",\"36be054d83f701154adfdd0cf1019d20\",\"36be054d83f701154adfdd0cf1100e37\"],\"reportTitle\":\"20150809\",\"id\":\"3fbb0f800fe543d0aebdbe6fc48d1ddd\",\"reportTopic\":\"Test\"},{\"attendee\":[\"36be054d83f701154adfdd0cf1019d20\",\"36be054d83f701154adfdd0cf1100e37\"],\"reportTitle\":\"20150807\",\"id\":\"e5508fae8ecc4729b2605496a278a884\",\"reportTopic\":\"북경오리\"},{\"attendee\":[\"4c44d639b77c290955371694d3310194\",\"36be054d83f701154adfdd0cf1019d20\",\"36be054d83f701154adfdd0cf1100e37\"],\"reportTitle\":\"20150728\",\"id\":\"5863b289b98447dc93c7152caa060a15\",\"reportTopic\":\"주제입니다만\"},{\"attendee\":[\"4c44d639b77c290955371694d3310194\",\"36be054d83f701154adfdd0cf1019d20\",\"36be054d83f701154adfdd0cf1100e37\"],\"reportTitle\":\"20150727\",\"id\":\"5560a997fcae4c61b567175ae7f5bef2\",\"reportTopic\":\"주제입니다만\"},{\"attendee\":[\"4c44d639b77c290955371694d3310194\",\"36be054d83f701154adfdd0cf1019d20\",\"36be054d83f701154adfdd0cf1100e37\"],\"reportTitle\":\"20150726\",\"id\":\"9b0facb842b2478c9c460423293a3a96\",\"reportTopic\":\"주제입니다만\"}],\"pid\":\"58387a05c00dcded6f7936c1173e3f5a\"}");

            list = new JSONArray(data.get("reportList").toString());
            ArrayList<String> reportList = new ArrayList<String>();
            for (int i=0; i<list.length(); i++) {
                reportList.add(i, list.get(i).toString());
            }
            adapter = new DetailRecyclerViewAdapter(reportList);
            recyclerView.setAdapter(adapter);
        } catch (JSONException e) {
            Log.e(TAG, e.getLocalizedMessage());
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

    public class DetailRecyclerViewAdapter extends RecyclerView.Adapter<DetailRecyclerViewAdapter.DetailItemViewHolder> {

        private ArrayList items;

        DetailRecyclerViewAdapter(ArrayList modelData) {
            if (modelData == null) {
                throw new IllegalArgumentException(
                        "modelData must not be null");
            }
            this.items = modelData;
        }

        @Override
        public DetailItemViewHolder onCreateViewHolder(
                ViewGroup viewGroup, int viewType) {
                View itemView = LayoutInflater.
                        from(viewGroup.getContext()).
                        inflate(R.layout.report_list_card, viewGroup, false);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int itemPosition = recyclerView.getChildPosition(v);
                        Intent intent = new Intent(ReportList.this, ReportDetails.class);
                        intent.putExtra("reportdata", items.get(itemPosition).toString());
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                    }
                });
                return new DetailItemViewHolder(itemView);
            }


        @Override
        public void onBindViewHolder(DetailItemViewHolder holder, int position) {
            try {
                JSONObject report = new JSONObject(items.get(position).toString());
                holder.title.setText(report.get("reportTitle").toString());
                holder.topic.setText(report.get("reportTopic").toString());

//                ImageLoaderOld imageLoader;
                ProfileImageLoader profileImageLoader;
                JSONArray attendee = new JSONArray(report.get("attendee").toString());
 //               for (int i=0; i<attendee.length(); i++) {
                    CircleImageView circleImageView = new CircleImageView(holder.attendee.getContext());
//                    imageLoader = new ImageLoaderOld(circleImageView);
//                    imageLoader.execute(attendee.get(i).toString());
//                    profileImageLoader = new ProfileImageLoader(R.drawable.user_k, circleImageView);
//                    profileImageLoader.getProfile();
//                    holder.attendee.addView(circleImageView);
 //               }
            } catch (JSONException e) {
                Log.e(TAG, "onBindViewHolder "+ e.getLocalizedMessage());
            }

        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class DetailItemViewHolder extends RecyclerView.ViewHolder {
            TextView title, topic;
            LinearLayout attendee;

            public DetailItemViewHolder(View v) {
                super(v);
                title = (TextView) v.findViewById(R.id.report_card_title);
                topic = (TextView) v.findViewById(R.id.report_card_topic);
                attendee = (LinearLayout) v.findViewById(R.id.report_card_attendee);
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
    }
}