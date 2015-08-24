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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.devholic.somareport.data.view.Project;
import com.github.devholic.somareport.data.view.ReportInfo;
import com.github.devholic.somareport.data.view.User;
import com.github.devholic.somareport.utils.HttpClientFactory;
import com.github.devholic.somareport.utils.ProfileImageLoader;

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
    ArrayList<Project> projects;
    private User userInfo;

    // Toolbar
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    ActionBarDrawerToggle drawerToggle;

    // Content
    @Bind(R.id.project_list_recycler)
    RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

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

        setDrawers();

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        setData();
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
        UserInfoTask userInfoTask = new UserInfoTask();
        userInfoTask.execute();
    }

    private void setData() {
        ProjectTask projectTask = new ProjectTask();
        projectTask.execute();
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
                    intent.putExtra("reportListType", ReportInfo.BYPROJECT);
                    intent.putExtra("project", (Project)items.get(itemPosition));
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                }
            });
            return new ItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {

            Project project = (Project) items.get(position);
            holder.stage.setText(project.getStage());
            holder.title.setText(project.getTitle());

            ProfileImageLoader profileImageLoader = new ProfileImageLoader(project.getMentor(), holder.mentor);
            profileImageLoader.getProfile();

            String[] mentee = project.getMentee();
            for (int i=0; i<mentee.length; i++) {
                CircleImageView menteeImage = new CircleImageView(holder.mentee.getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                int length = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, menteeImage.getResources().getDisplayMetrics());
                params.width = length;
                params.height = length;
                length = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, menteeImage.getResources().getDisplayMetrics());
                params.rightMargin = length;
                menteeImage.setLayoutParams(params);
                profileImageLoader = new ProfileImageLoader(mentee[i], menteeImage);
                profileImageLoader.getProfile();
                holder.mentee.addView(menteeImage);
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

    private class UserInfoTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                HttpClient httpClient = HttpClientFactory.getThreadSafeClient();
                HttpGet httpGet = new HttpGet(getString(R.string.api_url) + "/user");
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

    private class ProjectTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try{
                HttpClient httpClient = HttpClientFactory.getThreadSafeClient();
                HttpGet httpGet = new HttpGet(getString(R.string.api_url) + "/project/list");
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
                    projects = new ArrayList<Project>();
                    JSONArray result = new JSONArray(s);
                    Log.i(TAG, result.toString());
                    for (int i=0; i<result.length(); i++) {
                        Project p = new Project(result.getJSONObject(i));
                        projects.add(i, p);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
                adapter = new RecyclerViewAdapter(projects);
                recyclerView.setAdapter(adapter);
            }
            else
                projects = null;
        }
    }
}