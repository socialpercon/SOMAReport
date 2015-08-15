package com.github.devholic.somareport;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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

public class ProjectList extends AppCompatActivity {

    final String TAG = "Activity_ProjectList";
    // Toolbar
    @Bind(R.id.home_toolbar)
    Toolbar toolbar;
    ActionBarDrawerToggle drawerToggle;

    // DrawerLayout
    @Bind(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    // Content
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_list);
        setResources();
    }

    private void setResources() {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("SOMAREPORT");
        getSupportActionBar().setSubtitle("프로젝트 리스트");
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(drawerToggle);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        String id = getIntent().getStringExtra("userId");
         /*
        * /project/list에서 프로젝트 정보를 JSONArray로 받아온다.
        * [{project title, stage, id, mentor, mentee[]}]
        * */
        try {
            String json = "[{\"mentor\":\"3f4ce9856efb3e2f5d86eeb4d5b28f22\",\"stage\":\"6기 2단계 프로젝트\",\"id\":\"58387a05c00dcded6f7936c1173e3f5a\",\"title\":\"SOMAProject 2nd phase\",\"mentee\":[\"4c44d639b77c290955371694d3310194\",\"36be054d83f701154adfdd0cf1019d20\",\"36be054d83f701154adfdd0cf1100e37\"]}," +
                    "{\"mentor\":\"3f4ce9856efb3e2f5d86eeb4d5b28f22\",\"stage\":\"6기 1단계 2차 프로젝트\",\"id\":\"36be054d83f701154adfdd0cf174e3cc\",\"title\":\"SOMAProject3-2\",\"mentee\":[\"4c44d639b77c290955371694d3310194\",\"a35fa2f4fd6d544d11fae6acf7ba6e01\",\"3f4ce9856efb3e2f5d86eeb4d5d16b01\"]}," +
                    "{\"mentor\":\"3f4ce9856efb3e2f5d86eeb4d5abb8c6\",\"stage\":\"6기 1단계 1차 프로젝트\",\"id\":\"4c44d639b77c290955371694d33e4fe9\",\"title\":\"SOMAProject1\",\"mentee\":[\"4c44d639b77c290955371694d3310194\",\"3f4ce9856efb3e2f5d86eeb4d5b99c53\",\"3f4ce9856efb3e2f5d86eeb4d5bad432\"]}]";
            JSONArray array = new JSONArray(json);
            setData(array);
        } catch(JSONException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    private void setData(JSONArray array) {
        ArrayList<JSONObject> projectList = new ArrayList<JSONObject>();
        try {
            for(int i=0; i<array.length(); i++) {
               projectList.add(i, array.getJSONObject(i));
            }
        } catch(JSONException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        adapter = new RecyclerViewAdapter(projectList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder> {

        private ArrayList items;

        RecyclerViewAdapter(ArrayList modelData) {
            if (modelData == null) {
                throw new IllegalArgumentException(
                        "modelData must not be null");
            }
            this.items = modelData;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(
                ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.project_list_card, viewGroup, false);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int itemPosition = recyclerView.getChildPosition(v);
                    Intent intent = new Intent(ProjectList.this, ReportList.class);
                    JSONObject data = (JSONObject) items.get(itemPosition);
                    Log.d(TAG, "projectdata: " + data.toString());
                    intent.putExtra("projectdata", data.toString());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                }
            });
            return new ItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            try {
                JSONObject project = new JSONObject(items.get(position).toString());
                holder.stage.setText(project.get("stage").toString());
                holder.title.setText(project.get("title").toString());

//                ProfileImageLoader profileImageLoader = new ProfileImageLoader(project.get("mentor").toString(), holder.mentor);
//                profileImageLoader.getProfile();
//                ImageLoaderOld imageLoader = new ImageLoaderOld(holder.mentor);
//                imageLoader.execute(project.get("mentor").toString());

                JSONArray mentee = new JSONArray(project.get("mentee").toString());
                for (int i=0; i<mentee.length(); i++) {
                    CircleImageView menteeImage = new CircleImageView(holder.mentee.getContext());
//                    profileImageLoader = new ProfileImageLoader(mentee.get(i).toString(), menteeImage);
//                    profileImageLoader.getProfile();
//                    imageLoader = new ImageLoaderOld(menteeImage);
//                    imageLoader.execute(mentee.get(i).toString());
                    holder.mentee.addView(menteeImage);
                }
            } catch (JSONException e) {
                Log.e(TAG, "onBindViewHolder "+ e.getLocalizedMessage());
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView stage, title;
            CircleImageView mentor;
            LinearLayout mentee;

            public ItemViewHolder(View v) {
                super(v);
                title = (TextView) v.findViewById(R.id.project_card_title);
                stage = (TextView) v.findViewById(R.id.project_card_stage);
                mentor = (de.hdodenhof.circleimageview.CircleImageView) v.findViewById(R.id.project_card_mentor);
                mentee = (LinearLayout) v.findViewById(R.id.project_card_mentee);
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
    }
}