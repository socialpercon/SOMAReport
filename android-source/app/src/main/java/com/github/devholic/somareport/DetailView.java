package com.github.devholic.somareport;

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
import android.widget.TextView;

import com.github.devholic.somareport.data.view.Project;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by devholic on 15. 7. 23..
 */
public class DetailView extends AppCompatActivity {

    // Toolbar
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    // Content
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    private DetailRecyclerViewAdapter adapter;
    private int featureId;
    private MenuItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailview);
        setResources();
    }

    private void setResources() {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        setDummyData();
    }

    private void setDummyData() {
        Project p = (Project) getIntent().getParcelableExtra("dummydata");
        getSupportActionBar().setTitle(p.getDate());
        ArrayList<String> data = new ArrayList<String>();
        data.add("프로젝트 정보");
        data.add(Integer.toString(p.getPeriod()) + "기 " + Integer.toString(p.getStage()) + "단계 " + p.getProject() + "차 "
                + p.getArea() + "/" + p.getDepartment() + "\nSOMA Report");
        data.add("주제");
        data.add(p.getTitle());
        data.add("참석자");
        data.add("...");
        data.add("장소");
        data.add(p.getLocation());
        data.add("멘토링 시간");
        data.add("19:00 ~ 23:30");
        adapter = new DetailRecyclerViewAdapter(data);
        recyclerView.setAdapter(adapter);
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

    public class DetailRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private ArrayList items;

        DetailRecyclerViewAdapter(ArrayList modelData) {
            if (modelData == null) {
                throw new IllegalArgumentException(
                        "modelData must not be null");
            }
            this.items = modelData;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(
                ViewGroup viewGroup, int viewType) {
            if (viewType == 0) {
                View itemView = LayoutInflater.
                        from(viewGroup.getContext()).
                        inflate(R.layout.detail_text, viewGroup, false);
                return new DetailItemViewHolder(itemView);
            } else if (viewType == 1) {
                View itemView = LayoutInflater.
                        from(viewGroup.getContext()).
                        inflate(R.layout.detail_user, viewGroup, false);
                return new DetailUserViewHolder(itemView);
            } else {
                View itemView = LayoutInflater.
                        from(viewGroup.getContext()).
                        inflate(R.layout.detail_text2, viewGroup, false);
                return new DetailUserViewHolder(itemView);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Log.d("Postition", Integer.toString(position));
            if (position == 2) {
                position *= 2;
                DetailUserViewHolder uh = (DetailUserViewHolder) holder;
                uh.title.setText((String) items.get(position));
            } else if (position > 2) {
                position *= 2;
                DetailItemViewHolder2 ih2 = (DetailItemViewHolder2) holder;
                ih2.title.setText((String) items.get(position));
                ih2.content.setText((String) items.get(position + 1));
            } else {
                position *= 2;
                DetailItemViewHolder ih = (DetailItemViewHolder) holder;
                ih.title.setText((String) items.get(position));
                ih.content.setText((String) items.get(position + 1));
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 2) {
                return 1;
            } else if (position > 2) {
                return 2;
            } else {
                return 0;
            }
        }

        @Override
        public int getItemCount() {
            return items.size() / 2;
        }

        public class DetailItemViewHolder extends RecyclerView.ViewHolder {
            TextView content, title;

            public DetailItemViewHolder(View v) {
                super(v);
                content = (TextView) v.findViewById(R.id.content);
                title = (TextView) v.findViewById(R.id.title);
            }
        }

        public class DetailItemViewHolder2 extends RecyclerView.ViewHolder {
            TextView content, title;

            public DetailItemViewHolder2(View v) {
                super(v);
                content = (TextView) v.findViewById(R.id.content);
                title = (TextView) v.findViewById(R.id.title);
            }
        }

        public class DetailUserViewHolder extends RecyclerView.ViewHolder {
            TextView title;

            public DetailUserViewHolder(View v) {
                super(v);
                title = (TextView) v.findViewById(R.id.title);
            }
        }
    }
}
