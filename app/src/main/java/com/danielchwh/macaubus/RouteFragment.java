package com.danielchwh.macaubus;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class RouteFragment extends Fragment {
    RecyclerView recyclerView;
    int route;
    private static final int REFRESH_INTERVAL = 3000;
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_route, container, false);
        recyclerView = view.findViewById(R.id.recyclerView_Route);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        route = 73;
        initialize();
    }

    @Override
    public void onPause() {
        super.onPause();
        refreshHandler.removeCallbacks(refreshRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (refreshRunnable != null)
            refreshRunnable.run();
    }

    private void initialize() {
        String url = "https://bis.dsat.gov.mo:37812/macauweb/getRouteData.html?action=sd&routeName=" + route + "&dir=0&lang=zh-tw";
        RequestQueue queue = Volley.newRequestQueue(requireContext());
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

                    }
                }
        );
        queue.add(request);
    }

    private void initializeMyRouteInfo(String response) {
        Gson gson = new Gson();
        List<RouteInfo> routeInfo = gson.fromJson(response, RouteData.class).data.routeInfo;
        for (int i = 0; i < routeInfo.size(); i++) {
            RouteInfo station = routeInfo.get(i);
            myRouteInfo.add(new MyRouteInfo(station));
        }
        RouteAdapter adapter = new RouteAdapter(myRouteInfo);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
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
        RequestQueue queue = Volley.newRequestQueue(requireContext());
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
        RouteAdapter adapter = new RouteAdapter(myRouteInfo);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }
}