package com.example.fibroapp.sensor;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.fibroapp.MainActivity;
import com.example.fibroapp.R;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class ShakerService extends Service implements Shaker.OnShakeListener {
    private Shaker shaker;

    public void onCreate() {
        super.onCreate();

        shaker = new Shaker(getApplicationContext());
        shaker.setOnShakeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        shaker.pause();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onShake() {
        MainActivity mainActivity = MainActivity.instance;
        if (mainActivity != null){
            NavController navController = Navigation.findNavController(mainActivity, R.id.nav_host_fragment);
            navController.navigate(R.id.nav_assistant);
        }
    }
}
