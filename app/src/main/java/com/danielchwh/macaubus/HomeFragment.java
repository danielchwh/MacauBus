package com.danielchwh.macaubus;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView failureMsg;
    private RequestQueue queue;
    private List<String> myRouteList;
    private RouteListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ((CollapsingToolbarLayout) requireActivity().findViewById(R.id.collapsingToolbar_Main))
                .setTitle("澳門巴士");
        recyclerView = view.findViewById(R.id.recyclerView_Home);
        failureMsg = view.findViewById(R.id.failureMsg_Home);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 5));
        else
            recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 8));
        queue = Volley.newRequestQueue(requireContext());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize();
    }

    private void initialize() {
        String url = "https://bis.dsat.gov.mo:37812/macauweb/getRouteAndCompanyList.html";
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
        List<RouteList> routeList = gson.fromJson(response, RouteAndCompanyList.class).data.routeList;
        if (routeList == null) {
            failureMsg.setVisibility(View.VISIBLE);
            return;
        }
        myRouteList = new ArrayList<>();
        for (int i = 0; i < routeList.size(); i++) {
            myRouteList.add(routeList.get(i).routeName);
        }
        adapter = new RouteListAdapter(myRouteList);
        recyclerView.setAdapter(adapter);
    }
}