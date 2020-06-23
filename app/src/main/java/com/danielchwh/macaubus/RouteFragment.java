package com.danielchwh.macaubus;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class RouteFragment extends Fragment {
    private static final int REFRESH_INTERVAL = 5000;
    private RecyclerView recyclerView;
    private TextView failureMsg;
    private RequestQueue queue;
    private RouteAdapter adapter;
    private String route;
    private Handler refreshHandler;
    private Runnable refreshRunnable;
    private List<MyRouteInfo> myRouteInfo;
    private FloatingActionButton switchButton;
    private int direction = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            direction = savedInstanceState.getInt("direction");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route, container, false);
        route = getArguments().getString("route");
        ((CollapsingToolbarLayout) requireActivity().findViewById(R.id.collapsingToolbar_Main))
                .setTitle(route + "號車");
        switchButton = requireActivity().findViewById(R.id.switchButton_Main);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (direction == 0)
                    direction = 1;
                else
                    direction = 0;
                if (refreshHandler != null && refreshRunnable != null)
                    refreshHandler.removeCallbacks(refreshRunnable);
                if (queue != null)
                    queue.cancelAll(this);
                initialize();
            }
        });
        recyclerView = view.findViewById(R.id.recyclerView_Route);
        failureMsg = view.findViewById(R.id.failureMsg_Route);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        queue = Volley.newRequestQueue(requireContext());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("direction", direction);
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

    @SuppressLint("RestrictedApi")
    @Override
    public void onDestroy() {
        super.onDestroy();
        switchButton.setVisibility(View.GONE);
        if (queue != null)
            queue.cancelAll(this);
    }

    private void initialize() {
        String url = "https://bis.dsat.gov.mo:37812/macauweb/getRouteData.html?action=sd&routeName=" + route + "&dir=" + direction +"&lang=zh-tw";
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
                        if (error.toString().contains("thread interrupted"))
                            initialize();
                        else
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
        if (data.direction < 2) {
            switchButton.show();
        }
        if (routeInfo == null) {
            failureMsg.setVisibility(View.VISIBLE);
            return;
        }
        myRouteInfo = new ArrayList<>();
        for (int i = 0; i < routeInfo.size(); i++) {
            RouteInfo station = routeInfo.get(i);
            myRouteInfo.add(new MyRouteInfo(station));
        }
        adapter = new RouteAdapter(route, direction, myRouteInfo);
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
        String url = "https://bis.dsat.gov.mo:37812/macauweb/routestation/bus?action=dy&routeName=" + route + "&dir=" + direction + "&lang=zh-tw";
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
            if (myRouteInfo.get(i).refresh(routeInfo.get(i).busInfo))
                adapter.notifyItemChanged(i);
        }
    }
}