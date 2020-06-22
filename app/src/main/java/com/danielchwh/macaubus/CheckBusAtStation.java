package com.danielchwh.macaubus;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.ForegroundInfo;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CheckBusAtStation extends Worker {
    private static final int TARGET_BUS_STATUS = 1;
    private static final String NOTIFICATION_MSG = "號巴士已到站";
    private Context context;
    private WorkerParameters workerParams;
    private RequestQueue queue;

    public CheckBusAtStation(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        this.workerParams = workerParams;
        this.queue = Volley.newRequestQueue(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        setForegroundAsync(createForegroundInfo());
        String route = workerParams.getInputData().getString("route");
        int position = workerParams.getInputData().getInt("position", 0);
        String url = "https://bis.dsat.gov.mo:37812/macauweb/routestation/bus?action=dy&routeName=" + route + "&dir=0&lang=zh-tw";
        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest request = new StringRequest(StringRequest.Method.GET, url, future, future);
        queue.add(request);

        try {
            String response = future.get();
            Gson gson = new Gson();
            List<RouteInfo> routeInfo = gson.fromJson(response, RouteData.class).data.routeInfo;
            List<BusInfo> busInfo = routeInfo.get(position).busInfo;
            if (busInfo == null) {
                // No bus exist
                continuous(route, position);
                return Result.success();
            }
            for (int i = 0; i < busInfo.size(); i++) {
                if (busInfo.get(i).status == TARGET_BUS_STATUS) {
                    // Any bus is between previous station and target station
                    createNotification(route);
                    return Result.success();
                }
            }
            // All buses are at previous station
            continuous(route, position);
            return Result.success();
        } catch (Exception e) {
            // Fail to access api
            e.printStackTrace();
            continuous(route, position);
            return Result.success();
        }
    }

    private ForegroundInfo createForegroundInfo() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("foregroundService", "Foreground Service", NotificationManager.IMPORTANCE_MIN);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this.getApplicationContext(), "foregroundService")
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText("巴士報站服務")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setOngoing(true);
        return new ForegroundInfo(0, notificationBuilder.build());
    }

    private void continuous(String route, int position) {
        WorkManager workManager = WorkManager.getInstance(context);
        androidx.work.Data data = new Data.Builder()
                .putString("route", route)
                .putInt("position", position)
                .build();
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(CheckBusAtStation.class)
                .setInitialDelay(5, TimeUnit.SECONDS)
                .addTag("busNotification")
                .setInputData(data)
                .build();
        workManager.enqueueUniqueWork("busNotification", ExistingWorkPolicy.REPLACE, workRequest);
    }

    private void createNotification(String route) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("busNotification", "Bus Notification", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this.getApplicationContext(), "busNotification")
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(route + NOTIFICATION_MSG)
                .setSmallIcon(R.drawable.ic_launcher_foreground);
        notificationManager.notify((int) SystemClock.uptimeMillis(), notificationBuilder.build());
        notificationManager.cancel(0);
    }
}
