package com.example.pranavichoudhary.dwell;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import helpers.MqttHelper;

public class PotholeActivity extends AppCompatActivity implements SensorEventListener {

    float x_acc, y_acc, z_acc;
    float x_prev, y_prev, z_prev;
    float jerkThreshold = 12.5f;

    static MqttHelper mqttHelper;

    boolean firstUpdate = true;
    boolean jerk_felt = false;

    private static final String TAG = "PotholeActivity";
    private SensorManager sm;
    Sensor accelerometer;
    //Context context;

    TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pothole);
        //Intent i = new Intent(ChooseActivity.this, Jerks.class);
        //startService(i);

        startMqtt();

        Log.d(TAG, "on creation: Initializing Sensor services");
        sm= (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "on Create : Registered accelerometer listener");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {


    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Log.d(TAG, "on_sensor_changed: x: "+ sensorEvent.values[0] +"y:"+sensorEvent.values[1]+"z:"+sensorEvent.values[2]);
        updateAccelparameters(sensorEvent.values[0] ,sensorEvent.values[1],sensorEvent.values[1] );

        if((!jerk_felt) && isAccelerationChanged()){
            jerk_felt = true;
        }
        else if((jerk_felt) && isAccelerationChanged()){

            detectPothole();
        }
        else if((jerk_felt) && !isAccelerationChanged()){
            jerk_felt = false;
        }
    }

    private void startMqtt(){
        mqttHelper = new MqttHelper(getApplicationContext());
        mqttHelper.mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("Debug","Connected");
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug",mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    private void detectPothole() {
        Toast.makeText(getApplicationContext(),"Pothole Detected!",Toast.LENGTH_SHORT).show();
        //txt.setText("Pothole");
        mqttHelper.publishMessage("Pothole here");

    }

    private boolean isAccelerationChanged() {
        // Detect change in acceleration forces
        float deltaX = Math.abs(x_prev- x_acc);
        float deltaY = Math.abs(y_prev- y_acc);
        float deltaZ = Math.abs(z_prev- z_acc);

        return(deltaX > jerkThreshold && deltaY > jerkThreshold ||
                deltaX > jerkThreshold && deltaZ > jerkThreshold  ||
                deltaZ > jerkThreshold && deltaY > jerkThreshold );
    }

    private void updateAccelparameters(float x_new_acc, float y_new_acc,float z_new_acc ){
        if(firstUpdate){
            x_prev = x_new_acc;
            y_prev = y_new_acc;
            z_prev = z_new_acc;
            firstUpdate = false;
        }
        else{
            x_prev = x_acc;
            y_prev = y_acc;
            z_prev = z_acc;
        }

        x_acc = x_new_acc;
        y_acc = y_new_acc;
        z_acc = z_new_acc;
    }


}
