package com.danielchwh.macaubus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.ContentsViewHolder> {
    private List<RouteInfo> routeInfo;

    public RouteAdapter(List<RouteInfo> routeInfo) {
        this.routeInfo = routeInfo;
    }

    @NonNull
    @Override
    public ContentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.station, parent, false);
        return new ContentsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentsViewHolder holder, int position) {
        RouteInfo station = routeInfo.get(position);
        holder.stationName.setText(station.staName);
        holder.stationCode.setText(station.staCode);
        if (station.busInfo == null) {
            holder.busIcon1.setVisibility(View.INVISIBLE);
            holder.busPlate1.setVisibility(View.INVISIBLE);
            holder.busIcon2.setVisibility(View.GONE);
            holder.busPlate2.setVisibility(View.GONE);
            holder.drivingCard.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return routeInfo.size();
    }

    static class ContentsViewHolder extends RecyclerView.ViewHolder {
        public TextView stationName, stationCode;
        public ImageView busIcon1, busIcon2;
        public TextView busPlate1, busPlate2;
        public CardView drivingCard;

        public ContentsViewHolder(@NonNull View itemView) {
            super(itemView);
            stationName = itemView.findViewById(R.id.stationName);
            stationCode = itemView.findViewById(R.id.stationCode);
            busIcon1 = itemView.findViewById(R.id.busIcon1);
            busIcon2 = itemView.findViewById(R.id.busIcon2);
            busPlate1 = itemView.findViewById(R.id.busPlate1);
            busPlate2 = itemView.findViewById(R.id.busPlate2);
            drivingCard = itemView.findViewById(R.id.drivingCard);
        }
    }
}
