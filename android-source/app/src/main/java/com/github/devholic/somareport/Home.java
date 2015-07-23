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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.devholic.somareport.data.view.Project;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Home extends AppCompatActivity {

    // Toolbar
    @Bind(R.id.toolbar)
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
        setContentView(R.layout.home);
        setResources();
    }

    private void setResources() {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(drawerToggle);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        setDummyData();
    }

    // demo용 데이터
    private void setDummyData() {
        ArrayList<Project> dummyList = new ArrayList<Project>();
        dummyList.add(new Project("#150731", "프로젝트 1차 릴리즈", 6, 1, 1, "Web", "Web", "아람 6-3"));
        dummyList.add(new Project("#150724", "프로젝트 개발 진행", 6, 1, 1, "Web", "Web", "아람 7-6"));
        dummyList.add(new Project("#150722", "프로젝트 스케쥴링", 6, 1, 1, "Web", "Web", "아람 7-6"));
        dummyList.add(new Project("#150720", "프로젝트 적용기술 관련 발표", 6, 1, 1, "Web", "Web", "아람 7-6"));
        dummyList.add(new Project("#150715", "프로젝트 진행사항 결정", 6, 1, 1, "Web", "Web", "아람 7-8"));
        dummyList.add(new Project("#150713", "첫 멘토링", 6, 1, 1, "Web", "Web", "아람 6-2"));
        adapter = new RecyclerViewAdapter(dummyList);
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
                    inflate(R.layout.home_report_card, viewGroup, false);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int itemPosition = recyclerView.getChildPosition(v);
                    Intent intent = new Intent(Home.this, DetailView.class);
                    intent.putExtra("dummydata", (Project) items.get(itemPosition));
                    startActivity(intent);
                }
            });
            return new ItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            Project p = (Project) items.get(position);
            holder.date.setText(p.getDate());
            holder.title.setText(p.getTitle());
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView date, title;

            public ItemViewHolder(View v) {
                super(v);
                date = (TextView) v.findViewById(R.id.date);
                title = (TextView) v.findViewById(R.id.title);
            }
        }
    }
}
