package com.example.ktuweather.lab3;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ktuweather.R;

public class CompassActivity extends AppCompatActivity implements SensorEventListener {

    private TextView compassHeading;
    private TextView orientationText;
    private ImageView image;

    private float currentDegree = 0f;
    private float currentOrientation = 0f;

    private SensorManager sensorManager;
    private Sensor orientationSensor;
    private Sensor rotationVectorSensor;

    private CameraManager cameraManager;
    private boolean sosSent = false;

    private boolean shouldTrack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        image = (ImageView) findViewById(R.id.imageViewCompass);
        compassHeading = (TextView) findViewById(R.id.compassValue);
        orientationText = (TextView) findViewById(R.id.orientationValue);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;
        if (mySensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float orientation = (0.5f - Math.abs(Math.abs(event.values[1]) - 0.5f)) * 2;
            if (orientation < 0) {
                orientation = 0;
            } else if (orientation > 1) {
                orientation = 1;
            }

            orientationText.setText("Orientation: " + Float.toString(orientation));
            currentOrientation = orientation;
        }

        if (mySensor.getType() == Sensor.TYPE_ORIENTATION) {
            float degree = Math.round(event.values[0]);
            compassHeading.setText("Degrees: " + Float.toString(degree));

            // create a rotation animation (reverse turn degree degrees)
            RotateAnimation ra = new RotateAnimation(
                    -currentDegree,
                    -degree,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);

            // how long the animation will take place
            ra.setDuration(210);
            ra.setFillAfter(true);

            // Start the animation
            image.startAnimation(ra);
            currentDegree = degree;
        }

        if (currentDegree == 180 && (currentOrientation <= 1 && currentOrientation > 0.9) && !sosSent) {
            sendSOS();
            sosSent = true;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void sendSOS() {
        Runnable background = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 3; i++) {
                    setTorchMode(true);
                    try {
                        Thread.sleep( 300 );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setTorchMode(false);
                    try {
                        Thread.sleep( 300 );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                for (int i = 0; i < 3; i++) {
                    setTorchMode(true);
                    try {
                        Thread.sleep( 1000 );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setTorchMode(false);
                    try {
                        Thread.sleep( 1000 );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                for (int i = 0; i < 3; i++) {
                    setTorchMode(true);
                    try {
                        Thread.sleep( 300 );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setTorchMode(false);
                    try {
                        Thread.sleep( 300 );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                sosSent = false;
            }
        };
        new Thread(background).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setTorchMode(boolean mode) {
        String cameraId = null;
        try {
            cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, mode);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}