package com.danielchwh.macaubus;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RouteListAdapter extends RecyclerView.Adapter<RouteListAdapter.ContentsViewHolder> {
    private List<String> routeList;

    @NonNull
    @Override
    public RouteListAdapter.ContentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RouteListAdapter.ContentsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    public class ContentsViewHolder extends RecyclerView.ViewHolder {
    }
}
