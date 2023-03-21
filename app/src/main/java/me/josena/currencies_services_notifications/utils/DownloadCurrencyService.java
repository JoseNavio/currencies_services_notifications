package me.josena.currencies_services_notifications.utils;

import static androidx.core.app.ServiceCompat.stopForeground;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import me.josena.currencies_services_notifications.data.Currency;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DownloadCurrencyService extends IntentService {

    private static final String TAG = "Navio_Descarga";
    private static final String URL_CURRENCY = "https://josena.me/files/currencies.json";
    private final String CURRENCIES_FILE = "currencies.json";
    private boolean downloading = true;
    private static final int NOTIFICATION_ID = 1;
    private final int DEVICE_SDK = Build.VERSION.SDK_INT;
    private final int OREO_SDK =  Build.VERSION_CODES.O;
    private OkHttpClient okHttpClient = new OkHttpClient();

    public DownloadCurrencyService() {
        super("Download Currency Service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Notification launched.");
        // Create a notification channel for Android Oreo and above
        if (DEVICE_SDK >= OREO_SDK) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationChannel channel = new NotificationChannel("currency_downloader", "Currency Downloader", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.d(TAG, "Service killed.");
        downloading = false;
        stopForeground(true);
        stopSelf();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        // Start the service in the foreground and show a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "currency_downloader")
                .setContentTitle("Downloading currency data...")
                .setPriority(NotificationCompat.PRIORITY_LOW);
        startForeground(NOTIFICATION_ID, builder.build());

        while (downloading) {

            try {
                URL url = new URL(URL_CURRENCY);
                 downloadWithOkHTTP(url, new DownloadCallback() {
                    @Override
                    public void onDownloadCompleted(boolean success) {

                        if(success){
                            Log.d(TAG, "Notificacion...");
                            IntentFilter filter = new IntentFilter();
                            filter.addAction("me.josena.DOWNLOAD_COMPLETE");

                            CurrencyDownloadBroadcastReceiver receiver = new CurrencyDownloadBroadcastReceiver();
                            registerReceiver(receiver, filter);

                            // Send the broadcast when the download is complete
                            Intent broadcastIntent = new Intent("me.josena.DOWNLOAD_COMPLETE");
                            broadcastIntent.putExtra("message", readCurrencies());
                            sendBroadcast(broadcastIntent);
                        }
                    }
                });
                 //Each 10 minutes...
                Thread.sleep(600000);

            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String readCurrencies(){

        CurrencyParser parser = new CurrencyParser(getApplicationContext());
        ArrayList<Currency> currencies = parser.obtainCurrencies();
        return currencies.toString();
    }

    private void downloadWithOkHTTP(URL urlToDownload, DownloadCallback downloadCallback) {

        Request request = new Request.Builder().url(urlToDownload).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "Error: " + e.getMessage());
                downloadCallback.onDownloadCompleted(false);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {

                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        Log.e(TAG, "Resultado inesperado: " + response);
                    } else {

                        try {
                            //Document reading... and storing
//                            String responseData = response.body().string();
//                            //Store a file in memory
//                            FileOutputStream outputStream = openFileOutput("currency.txt", Context.MODE_PRIVATE);
//                            outputStream.write(responseData.getBytes());

                            //File storing (More generic)
                            byte[] bytes = response.body().bytes();
                            File file = new File(getFilesDir(), CURRENCIES_FILE);
                            FileOutputStream outputStream = new FileOutputStream(file);
                            outputStream.write(bytes);

                            //Show message
                            Log.d(TAG, "Descarga completada.");
                            downloadCallback.onDownloadCompleted(true);
                            outputStream.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
    interface DownloadCallback {
        void onDownloadCompleted(boolean success);
    }
}
