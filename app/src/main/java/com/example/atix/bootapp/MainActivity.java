package com.example.atix.bootapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.hardware.Sensor;

import java.util.List;

import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager = null;
    private TextView accSensorTxt = null;
    private List list;
    private TextView longitudeValueGps = null;
    private TextView latitudeValueGps = null;
    private TextView testField = null;
    private Button button, subscribe;
    private MqttAndroidClient client;
    private String TAG = "MainActivity";
    private PahoMqttClient pahoMqttClient;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        pahoMqttClient = new PahoMqttClient();
        /* Get a SensorManager instance */
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        button = findViewById(R.id.button);
        accSensorTxt = findViewById(R.id.accSensorTxt);
        longitudeValueGps = findViewById(R.id.longitudeValueGps);
        latitudeValueGps = findViewById(R.id.latitudeValueGps);
        testField = findViewById(R.id.testField);

        subscribe = findViewById(R.id.subscribe);

        client = pahoMqttClient.getMqttClient(getApplicationContext(), Constants.MQTT_BROKER_URL, Constants.CLIENT_ID);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        Log.e(TAG, "I'm here.1");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "I'm here.2");
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    Log.e(TAG, "I'm here.3");
                    if (location != null) {
                        Log.e(TAG, "Success");
                        // Logic to handle location object
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();
                        longitudeValueGps.setText(Double.toString(lon));
                        latitudeValueGps.setText(Double.toString(lat));
                    }
                }
            });
        }
       /* button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GpsTracker gt = new GpsTracker(getApplicationContext(),MainActivity.this);
                Location l = gt.getLocation();
                if (l == null) {
                    Toast.makeText(getApplicationContext(), "GPS unable to get Value", Toast.LENGTH_SHORT).show();
                } else {
                    double lat = l.getLatitude();
                    double lon = l.getLongitude();
                    longitudeValueGps.setText(Double.toString(lon));
                    latitudeValueGps.setText(Double.toString(lat));
                }
            }
        });*/


        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                        pahoMqttClient.subscribe(client, Constants.PUBLISH_TOPIC, 0);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }

            }
        });

        list = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

        if (list.size() > 0) {
            sensorManager.registerListener(sel, (Sensor) list.get(0), SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(getBaseContext(), "Error: No Accelerometer.", Toast.LENGTH_LONG).show();
        }

        Intent intent = new Intent(MainActivity.this, MqttMessageService.class);
        startService(intent);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommunication(Communication event) {
        testField.setText(event.getMessage());
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    SensorEventListener sel = new SensorEventListener(){
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            accSensorTxt.setText("x: "+values[0]+"\ny: "+values[1]+"\nz: "+values[2]);
        }
    };


}


/*
TODO
    Location getSpeed();
    Location getBearing();
    Magnetometer
        Berechnung der Kompassrichtung (siehe Papa's Word Dokument)
    Gyro
        1. Winkeländerung ausgeben
        2. Aufsummieren der Änderungen, d.h. Geschwindigkeit ausrechnen
    Accelerator
        1. Geschwindigkeit ausgeben
    Kalibrieren des Sensors
        1. auf Android mit Abfrage ob kalibriert werden soll oder nicht?
    Handy Acc zum Testen verwenden
    Loggen der Daten als csv Datei mit "," als Dezimaltrennzeichen und ";" als Spaltentrennung
    Bildschirm anlassen

 */