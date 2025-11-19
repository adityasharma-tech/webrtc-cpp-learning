package tech.adityasharma.prodwebrtc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class ProductionService extends Service {

    static {
        System.loadLibrary("prodwebrtc");
    }

    private final static String TAG = "ProductionService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        return START_NOT_STICKY;
    }

    // cpp
    public native void nativeInit();




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
