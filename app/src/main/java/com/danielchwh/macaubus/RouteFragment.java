package com.danielchwh.macaubus;

import android.os.Bundle;
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

import java.util.List;

public class RouteFragment extends Fragment {
    RecyclerView recyclerView;

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

        String url = "https://bis.dsat.gov.mo:37812/macauweb/getRouteData.html?action=sd&routeName=73&dir=0&lang=zh-tw";
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        StringRequest request = new StringRequest(
                StringRequest.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        initStation(response);
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

    private void initStation(String response) {
        Gson gson = new Gson();
        RouteData routeData = gson.fromJson(response, RouteData.class);
        Data data = routeData.data;
        List<RouteInfo> routeInfo = data.routeInfo;
        for (int i = 0; i < routeInfo.size(); i++) {
            RouteInfo station = routeInfo.get(i);
            Log.d("mylog", station.staName + "--" + station.staCode);
        }
        RouteAdapter adapter = new RouteAdapter(routeInfo);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }
}