package com.danielchwh.macaubus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.ContentsViewHolder> {
    private List<MyRouteInfo> routeInfo;

    public RouteAdapter(List<MyRouteInfo> routeInfo) {
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
        MyRouteInfo station = routeInfo.get(position);
        holder.stationName.setText(station.staName);
        holder.stationCode.setText(station.staCode);
        if (station.busAtStation.equals("")) {
            holder.busLayout1.setVisibility(View.INVISIBLE);
        } else {
            holder.busLayout1.setVisibility(View.VISIBLE);
            holder.busPlate1.setText(station.busAtStation);
        }
        if (station.busOnRoad.equals("")) {
            holder.busLayout2.setVisibility(View.GONE);
            holder.drivingCard.setVisibility(View.GONE);
        } else {
            holder.busLayout2.setVisibility(View.VISIBLE);
            holder.drivingCard.setVisibility(View.VISIBLE);
            holder.busPlate2.setText(station.busOnRoad);
        }
    }

    @Override
    public int getItemCount() {
        return routeInfo.size();
    }

    static class ContentsViewHolder extends RecyclerView.ViewHolder {
        public TextView stationName, stationCode;
        public TextView busPlate1, busPlate2;
        public CardView drivingCard;
        public ConstraintLayout busLayout1, busLayout2;

        public ContentsViewHolder(@NonNull View itemView) {
            super(itemView);
            stationName = itemView.findViewById(R.id.stationName);
            stationCode = itemView.findViewById(R.id.stationCode);
            busPlate1 = itemView.findViewById(R.id.busPlate1);
            busPlate2 = itemView.findViewById(R.id.busPlate2);
            drivingCard = itemView.findViewById(R.id.drivingCard);
            busLayout1 = itemView.findViewById(R.id.busLayout1);
            busLayout2 = itemView.findViewById(R.id.busLayout2);
        }
    }
}
