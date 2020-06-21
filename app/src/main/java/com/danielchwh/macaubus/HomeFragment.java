package com.danielchwh.macaubus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class HomeFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Button button = view.findViewById(R.id.button_Home);
        final EditText editText = view.findViewById(R.id.editText_Home);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("澳門巴士");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("route", editText.getText().toString());
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_homeFragment_to_routeFragment, bundle);
            }
        });
        return view;
    }
}