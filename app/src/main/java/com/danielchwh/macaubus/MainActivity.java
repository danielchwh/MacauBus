package com.danielchwh.macaubus;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private WorkManager workManager = WorkManager.getInstance(this);
    private FloatingActionButton notifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_Main));
        notifyButton = findViewById(R.id.notifyButton_Main);

        navController = Navigation.findNavController(findViewById(R.id.fragment_Main));
        NavigationUI.setupActionBarWithNavController(this, navController);

        notifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workManager.cancelAllWorkByTag("busNotification");
                notifyButton.hide();
                Toast.makeText(getApplicationContext(), "已取消報站通告", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        navController.navigateUp();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            List<WorkInfo> workInfo = workManager.getWorkInfosForUniqueWork("busNotification").get();
            WorkInfo.State state = workInfo.get(0).getState();
            if (state == WorkInfo.State.ENQUEUED || state == WorkInfo.State.RUNNING)
                notifyButton.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}