package com.danielchwh.macaubus;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class RouteFragment extends Fragment {
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    TextView failureMsg;
    RequestQueue queue;
    RouteAdapter adapter;
    String route;
    private static final int REFRESH_INTERVAL = 5000;
    Handler refreshHandler;
    Runnable refreshRunnable;
    List<MyRouteInfo> myRouteInfo = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route, container, false);
        recyclerView = view.findViewById(R.id.recyclerView_Route);
        floatingActionButton = view.findViewById(R.id.floatingActionButton_Route);
        failureMsg = view.findViewById(R.id.failureMsg_Route);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        queue = Volley.newRequestQueue(requireContext());
        route = getArguments().getString("route");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("路線 " + route);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (refreshHandler != null && refreshRunnable != null)
            refreshHandler.removeCallbacks(refreshRunnable);
        if (queue != null)
            queue.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (refreshRunnable != null)
            refreshRunnable.run();
        if (queue != null)
            queue.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (queue != null)
            queue.cancelAll(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Parcelable recyclerState = recyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable("scrollState", recyclerState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            Parcelable recyclerState = savedInstanceState.getParcelable("scrollState");
            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerState);
        }
    }

    private void initialize() {
        String url = "https://bis.dsat.gov.mo:37812/macauweb/getRouteData.html?action=sd&routeName=" + route + "&dir=0&lang=zh-tw";
        StringRequest request = new StringRequest(
                StringRequest.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        initializeMyRouteInfo(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        failureMsg.setVisibility(View.VISIBLE);
                    }
                }
        );
        queue.add(request);
    }

    private void initializeMyRouteInfo(String response) {
        Gson gson = new Gson();
        Data data = gson.fromJson(response, RouteData.class).data;
        List<RouteInfo> routeInfo = data.routeInfo;
        if (data.direction < 2)
            floatingActionButton.show();
        if (routeInfo == null) {
            failureMsg.setVisibility(View.VISIBLE);
            return;
        }
        for (int i = 0; i < routeInfo.size(); i++) {
            RouteInfo station = routeInfo.get(i);
            myRouteInfo.add(new MyRouteInfo(station));
        }
        adapter = new RouteAdapter(myRouteInfo);
        recyclerView.setAdapter(adapter);
        initializeRefresh();
    }

    private void initializeRefresh() {
        refreshHandler = new Handler();
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                refresh();
                refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
            }
        };
        refreshRunnable.run();
    }

    private void refresh() {
        String url = "https://bis.dsat.gov.mo:37812/macauweb/routestation/bus?action=dy&routeName=" + route + "&dir=0&lang=zh-tw";
        StringRequest request = new StringRequest(
                StringRequest.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        refreshMyRouteInfo(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        queue.add(request);
    }

    private void refreshMyRouteInfo(String response) {
        Gson gson = new Gson();
        List<RouteInfo> routeInfo = gson.fromJson(response, RouteData.class).data.routeInfo;
        for (int i = 0; i < routeInfo.size(); i++) {
            myRouteInfo.get(i).refresh(routeInfo.get(i).busInfo);
        }
        adapter.notifyDataSetChanged();
        try {
            Snackbar.make(requireView(), "已刷新", Snackbar.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}