package com.example.ktuweather.lab3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ktuweather.R;

public class Task1and2 extends AppCompatActivity implements SensorEventListener {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Sensor senRotationVectorSensor;

    private Button startAndStop;

    private TextView xValue;
    private TextView yValue;
    private TextView zValue;
    private TextView xOrientation;
    private TextView yOrientation;

    private static final float sensorLimit = 0.1f;
    private float xAxisValue = 0;
    private float yAxisValue = 0;
    private float zAxisValue = 0;

    private boolean InformationObtained;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task1and2);

        InformationObtained = false;

        xValue = (TextView) findViewById(R.id.x_value);
        yValue = (TextView) findViewById(R.id.y_value);
        zValue = (TextView) findViewById(R.id.z_value);
        xOrientation = (TextView) findViewById(R.id.orientation_x);
        yOrientation = (TextView) findViewById(R.id.orientation_y);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senRotationVectorSensor = senSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        startAndStop = (Button) findViewById(R.id.start_and_stop);
        startAndStop.setOnClickListener(StartAndStopButtonListener);
    }

    View.OnClickListener StartAndStopButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (senAccelerometer == null) {
                Toast.makeText(Task1and2.this, "No Sensor", Toast.LENGTH_LONG).show();
                return;
            }

            if (InformationObtained) {
                startAndStop.setText("Start");
                senSensorManager.unregisterListener(Task1and2.this, senAccelerometer);
                senSensorManager.unregisterListener(Task1and2.this, senRotationVectorSensor);
                InformationObtained = false;
            } else {
                senSensorManager.registerListener(Task1and2.this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                senSensorManager.registerListener(Task1and2.this, senRotationVectorSensor, senSensorManager.SENSOR_DELAY_NORMAL);
                startAndStop.setText("Stop");
                InformationObtained = true;
            }
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (Math.abs(Math.abs(xAxisValue) - Math.abs(event.values[0])) > sensorLimit) {
                xAxisValue = event.values[0];
                xValue.setText(String.valueOf(xAxisValue));
            }

            if (Math.abs(Math.abs(yAxisValue) - Math.abs(event.values[1])) > sensorLimit) {
                yAxisValue = event.values[1];
                yValue.setText(String.valueOf(yAxisValue));
            }

            if (Math.abs(Math.abs(zAxisValue) - Math.abs(event.values[2])) > sensorLimit) {
                zAxisValue = event.values[2];
                zValue.setText(String.valueOf(zAxisValue));
            }
        }

        if (mySensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            xOrientation.setText(String.valueOf(event.values[0]));
            yOrientation.setText(String.valueOf(event.values[1]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (senAccelerometer != null)
            senSensorManager.unregisterListener(Task1and2.this, senAccelerometer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (senAccelerometer != null && InformationObtained)
            senSensorManager.registerListener(Task1and2.this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
}