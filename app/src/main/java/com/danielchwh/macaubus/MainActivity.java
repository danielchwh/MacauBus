package com.danielchwh.macaubus;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
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
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(0);
                Toast.makeText(getApplicationContext(), "已取消報站通告", Toast.LENGTH_SHORT).show();
            }
        });

        workManager.getWorkInfosForUniqueWorkLiveData("busNotification")
                .observe(this, new Observer<List<WorkInfo>>() {
                    @Override
                    public void onChanged(List<WorkInfo> workInfo) {
                        WorkInfo.State state = workInfo.get(0).getState();
                        if (state == WorkInfo.State.ENQUEUED || state == WorkInfo.State.RUNNING)
                            notifyButton.show();
                        else
                            notifyButton.hide();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        navController.navigateUp();
        return super.onSupportNavigateUp();
    }
}