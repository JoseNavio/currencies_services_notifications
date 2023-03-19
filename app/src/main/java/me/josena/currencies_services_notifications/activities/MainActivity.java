package me.josena.currencies_services_notifications.activities;

import static androidx.core.app.ServiceCompat.stopForeground;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import me.josena.currencies_services_notifications.R;
import me.josena.currencies_services_notifications.utils.DownloadCurrencyService;

public class MainActivity extends AppCompatActivity {

    Button buttonServices;
    Intent intentUpdateCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Update currencies file
        //Download file
        launchIntentService();

        buttonServices = findViewById(R.id.buttonServices);

        buttonServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent switchActivityIntent = new Intent(getApplicationContext(), CurrencyConverterActivity.class);
                startActivity(switchActivityIntent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intentUpdateCurrency);
    }

    private void launchIntentService() {

        intentUpdateCurrency = new Intent(MainActivity.this, DownloadCurrencyService.class);
        startForegroundService(intentUpdateCurrency);

    }
}
