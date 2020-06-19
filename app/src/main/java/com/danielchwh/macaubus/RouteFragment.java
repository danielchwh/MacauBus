package com.danielchwh.macaubus;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.List;

public class RouteFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_route, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String url1 = "https://bis.dsat.gov.mo:37812/macauweb/getRouteData.html?action=sd&routeName=73&dir=0&lang=zh-tw";
        String url2 = "https://bis.dsat.gov.mo:37812/macauweb/routestation/bus?action=dy&routeName=73&dir=0&lang=zh-tw";
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        StringRequest request1 = new StringRequest(
                StringRequest.Method.GET,
                url1,
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
        StringRequest request2 = new StringRequest(
                StringRequest.Method.GET,
                url2,
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
        queue.add(request1);
        queue.add(request2);
    }

    private void initStation(String response) {
        Gson gson = new Gson();
        RouteData routeData = gson.fromJson(response, RouteData.class);
        Data data = routeData.data;
        List<RouteInfo> routeInfo = data.routeInfo;
        for (int i=0; i < routeInfo.size(); i++) {
            RouteInfo station = routeInfo.get(i);
            Log.d("mylog", station.staName + "--" + station.staCode);
        }
    }
}