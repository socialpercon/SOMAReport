package com.github.devholic.somareport;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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

import com.github.devholic.somareport.data.view.ReportInfo;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProjectList extends AppCompatActivity {

    final String TAG = "Activity_ProjectList";
    String cookie;

    // Toolbar
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    ActionBarDrawerToggle drawerToggle;

    // DrawerLayout
    @Bind(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    // Content
    @Bind(R.id.project_list_recycler)
    RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    @Bind(R.id.drawer_view)
    NavigationView navigationView;

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
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(drawerToggle);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                drawerLayout.closeDrawers();

                Intent intent;
                switch (menuItem.getItemId()) {
                    case R.id.drawer_Unconfirmed:
                        intent = new Intent(ProjectList.this, ReportList.class);
                        intent.putExtra("reportInfoType", ReportInfo.UNCONFIRMED);
                        startActivity(intent);
                        return true;
                    case R.id.drawer_myProject:
                        return true;
                    case R.id.drawer_logout:
                        intent = new Intent(ProjectList.this, Login.class);
                        startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

      //  String id = getIntent().getStringExtra("userId");
    //    cookie = getIntent().getStringExtra("cookie");
         /*
        * /project/list에서 프로젝트 정보를 JSONArray로 받아온다.
        * [{project title, stage, id, mentor, mentee[]}]
        * */
//        ProjectTask projectTask = new ProjectTask();
//        projectTask.execute();

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

//                ProfileImageLoader profileImageLoader = new ProfileImageLoader(R.drawable.user_a, holder.mentor);
//                profileImageLoader.getProfile();
//                ImageLoaderOld imageLoader = new ImageLoaderOld(holder.mentor);
//                imageLoader.execute(Integer.toString(R.drawable.user_a));

                JSONArray mentee = new JSONArray(project.get("mentee").toString());
                for (int i=0; i<mentee.length(); i++) {
                    CircleImageView menteeImage = new CircleImageView(holder.mentee.getContext());
//                    profileImageLoader = new ProfileImageLoader(R.drawable.user_k, menteeImage);
//                    profileImageLoader.getProfile();
//                    imageLoader = new ImageLoaderOld(menteeImage);
//                    imageLoader.execute(Integer.toString(R.drawable.user_k));
//                    holder.mentee.addView(menteeImage);
//                menteeImage = new CircleImageView(holder.mentee.getContext());
//                profileImageLoader = new ProfileImageLoader(R.drawable.user_l, menteeImage);
//                profileImageLoader.getProfile();
//                holder.mentee.addView(menteeImage);
//                menteeImage = new CircleImageView(holder.mentee.getContext());
//                profileImageLoader = new ProfileImageLoader(R.drawable.user_a, menteeImage);
//                profileImageLoader.getProfile();
//                holder.mentee.addView(menteeImage);
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

    private class ProjectTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String url = "http://10.0.3.2:8080/project";
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                httpGet.setHeader("Set-Cookie", cookie);
                Header[] hs = httpGet.getAllHeaders();
                for (Header h : hs) {
                    System.out.println("Key : " + h.getName()
                            + " ,Value : " + h.getValue());
                }

                HttpResponse httpResponse = httpClient.execute(httpGet);
                Header[] headers = httpResponse.getAllHeaders();
                for (Header h : headers) {
                    System.out.println("Key : " + h.getName()
                            + " ,Value : " + h.getValue());
                }
                InputStream is = httpResponse.getEntity().getContent();
                StringBuilder stringBuilder = new StringBuilder();
                byte[] b = new byte[4096];
                for (int n; (n = is.read(b)) != -1;) {
                    stringBuilder.append(new String(b, 0, n));
                }
                System.out.println(stringBuilder.toString());
            } catch (ClientProtocolException e) {
                Log.e(TAG, e.getLocalizedMessage());
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
            return null;
        }
    }
}