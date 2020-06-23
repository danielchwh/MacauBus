package com.danielchwh.macaubus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RouteListAdapter extends RecyclerView.Adapter<RouteListAdapter.ContentsViewHolder> {
    private List<String> routeList;

    public RouteListAdapter(List<String> routeList) {
        this.routeList = routeList;
    }

    @NonNull
    @Override
    public RouteListAdapter.ContentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.route_list, parent, false);
        return new ContentsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteListAdapter.ContentsViewHolder holder, int position) {
        final String routeName = routeList.get(position);
        holder.routeName.setText(routeName);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("route", routeName);
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_homeFragment_to_routeFragment, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    public class ContentsViewHolder extends RecyclerView.ViewHolder {
        public TextView routeName;
        public CardView routeCard;

        public ContentsViewHolder(@NonNull View itemView) {
            super(itemView);
            routeName = itemView.findViewById(R.id.routeName);
            routeCard = itemView.findViewById(R.id.routeCard);
        }
    }
}
