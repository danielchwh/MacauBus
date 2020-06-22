package com.danielchwh.macaubus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.ContentsViewHolder> {
    private List<MyRouteInfo> routeInfo;
    private String route;
    private WorkManager workManager;

    public RouteAdapter(String route, List<MyRouteInfo> routeInfo) {
        this.route = route;
        this.routeInfo = routeInfo;
    }

    @NonNull
    @Override
    public ContentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.station, parent, false);
        workManager = WorkManager.getInstance(parent.getContext());
        return new ContentsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContentsViewHolder holder, final int position) {
        final MyRouteInfo station = routeInfo.get(position);
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
        holder.stationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position == 0) {
                    // This is the first station
                    Toast.makeText(holder.itemView.getContext(), "這是起點站,無法設定報站通告", Toast.LENGTH_SHORT).show();
                } else if (!routeInfo.get(position).busAtStation.equals("")) {
                    // Bus is already at station
                    Toast.makeText(holder.itemView.getContext(), "巴士已在車站,無法設定報站通告", Toast.LENGTH_SHORT).show();
                } else if (!routeInfo.get(position - 1).busOnRoad.equals("")) {
                    // Wait for bus arise
                    androidx.work.Data data = new Data.Builder()
                            .putString("route", route)
                            .putInt("position", position)
                            .build();
                    OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(CheckBusAtStation.class)
                            .addTag("busNotification")
                            .setInputData(data)
                            .build();
                    workManager.enqueueUniqueWork("busNotification", ExistingWorkPolicy.REPLACE, workRequest);
                    Toast.makeText(holder.itemView.getContext(), "已設定報站通告", Toast.LENGTH_SHORT).show();
                } else {
                    // Wait for bus coming
                    androidx.work.Data data = new Data.Builder()
                            .putString("route", route)
                            .putInt("position", position - 1)
                            .build();
                    OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(CheckBusOnRoad.class)
                            .addTag("busNotification")
                            .setInputData(data)
                            .build();
                    workManager.enqueueUniqueWork("busNotification", ExistingWorkPolicy.REPLACE, workRequest);
                    Toast.makeText(holder.itemView.getContext(), "已設定報站通告", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return routeInfo.size();
    }

    static class ContentsViewHolder extends RecyclerView.ViewHolder {
        public TextView stationName, stationCode;
        public TextView busPlate1, busPlate2;
        public CardView stationCard, drivingCard;
        public ConstraintLayout busLayout1, busLayout2;

        public ContentsViewHolder(@NonNull View itemView) {
            super(itemView);
            stationName = itemView.findViewById(R.id.stationName);
            stationCode = itemView.findViewById(R.id.stationCode);
            busPlate1 = itemView.findViewById(R.id.busPlate1);
            busPlate2 = itemView.findViewById(R.id.busPlate2);
            stationCard = itemView.findViewById(R.id.stationCard);
            drivingCard = itemView.findViewById(R.id.drivingCard);
            busLayout1 = itemView.findViewById(R.id.busLayout1);
            busLayout2 = itemView.findViewById(R.id.busLayout2);
        }
    }
}
