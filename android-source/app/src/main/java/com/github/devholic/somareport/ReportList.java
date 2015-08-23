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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ReportList extends AppCompatActivity {

    final String TAG = "Activity_ReportList";

    // Toolbar
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    ActionBarDrawerToggle drawerToggle;

    // Content
    @Bind(R.id.report_list_recycler)
    RecyclerView recyclerView;

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


    private DetailRecyclerViewAdapter adapter;
    private User userInfo;
    public int type;

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

        setDrawers();

        type = getIntent().getIntExtra("reportInfoType", 0);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        setData(type);
    }

    private void setDrawers() {
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
                        return true;
                    case R.id.drawer_myProject:
                        intent = new Intent(ReportList.this, ProjectList.class);
                        startActivity(intent);
                        return true;
                    case R.id.drawer_logout:
                        intent = new Intent(ReportList.this, Login.class);
                        startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            }
        });

        UserInfoTask reportTask = new UserInfoTask();
        reportTask.execute();
    }

    private void setData(int type) {

        ReportInfoTask reportInfoTask = new ReportInfoTask();

        if (type == ReportInfo.UNCONFIRMED) {
            getSupportActionBar().setSubtitle("작성중인 멘토링 보고서");

        }

        else if (type == ReportInfo.BYPROJECT) {
            getSupportActionBar().setSubtitle("멘토링 보고서 리스트");
        }

        else {
            Log.e("TAG", "wrong report info type");
        }

        JSONArray list = new JSONArray();
        try {

            JSONObject data = new JSONObject();
            /*
            * localhost:8080/report/list/{pid}를 통해 JSONObject로 data에 가져온다
            * {프로젝트id, title, reportList[report id, title, topic, attendee]}
            * */

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

                ProfileImageLoader profileImageLoader;
                JSONArray attendee = new JSONArray(report.get("attendee").toString());
                for (int i=0; i<attendee.length(); i++) {
                    CircleImageView circleImageView = new CircleImageView(holder.attendee.getContext());
//                    profileImageLoader = new ProfileImageLoader(R.drawable.user_k, circleImageView);
//                    profileImageLoader.getProfile();
        //            holder.attendee.addView(circleImageView);
                }
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

    private class UserInfoTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
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
        protected void onPostExecute(String info) {
            Log.i(TAG, info);
            if (info != null) {
                JSONObject doc = null;
                try {
                    doc = new JSONObject(info);
                    userInfo = new User(doc);
                } catch (JSONException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
                if (doc != null) {
                    Log.d(TAG, "drawer handling");
                    drawerName.setText(userInfo.getName());
                    if (userInfo.getRole().equalsIgnoreCase("mentor"))
                        drawerRole.setText("SW Maestro 멘토");
                    else if (userInfo.getRole().equalsIgnoreCase("mentee"))
                        drawerRole.setText("SW Maestro 멘티");
                    else
                        drawerRole.setText("SW Maestro 사무국");
                    ProfileImageLoader profileImageLoader = new ProfileImageLoader(userInfo.getId(), drawerProfile);
                    profileImageLoader.getProfile();
                }
            }
        }
    }

    private class ReportInfoTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                HttpClient httpClient = HttpClientFactory.getThreadSafeClient();
                HttpGet httpGet = new HttpGet("http://10.0.3.2:8080/api/");
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
        protected void onPostExecute(String s) {
            if (s != null) {
                try {
                    JSONArray reports = new JSONArray(s);

                } catch (JSONException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
    }
}