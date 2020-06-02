package com.antoun.stepcountertask;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {

    TextView textViewStepCount;
    Button buttonReset;

    int counter = 0;

    SensorManager sensorManager;

    private float[] prev = {0f, 0f, 0f};
    private static final int ABOVE = 1;
    private static final int BELOW = 0;
    private static int CURRENT_STATE = 0;
    private static int PREVIOUS_STATE = BELOW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewStepCount = findViewById(R.id.textViewStepCount);

        buttonReset = findViewById(R.id.buttonReset);
        buttonReset.setOnClickListener(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        countSteps();
    }

    public void countSteps() {

        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    private void handleAccelometerEvent(SensorEvent event) {
        prev = lowPassFilter(event.values, prev);
        double magnitude = Math.sqrt(Math.pow(prev[0], 2) + Math.pow(prev[1], 2) + Math.pow(prev[2], 2));
        if (magnitude > 10.5f) {
            CURRENT_STATE = ABOVE;
            if (PREVIOUS_STATE != CURRENT_STATE) {
                counter++;
            }
            PREVIOUS_STATE = CURRENT_STATE;
        } else if (magnitude < 10.5f) {
            CURRENT_STATE = BELOW;
            PREVIOUS_STATE = CURRENT_STATE;
        }
        textViewStepCount.setText("" + (counter));
    }

    private float[] lowPassFilter(float[] input, float[] prev) {
        float ALPHA = 0.1f;
        if (input == null || prev == null) {
            return null;
        }
        for (int i = 0; i < input.length; i++) {
            prev[i] = prev[i] + ALPHA * (input[i] - prev[i]);
        }
        return prev;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            handleAccelometerEvent(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onClick(View v) {
        if (v == buttonReset) {

            counter = 0;
            textViewStepCount.setText(String.valueOf(counter));
        }
    }
}
