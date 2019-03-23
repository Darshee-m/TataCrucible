package com.example.pranavichoudhary.dwell;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import helpers.MqttHelper;

public class LuminanceActivity extends AppCompatActivity {

    Button button ;
    ImageView imageView ;
    Intent intent ;
    float reso;
    int bright;
    static MqttHelper mqttHelper;
    public  static final int RequestPermissionCode  = 1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luminance);

        button = (Button)findViewById(R.id.button);
        imageView = (ImageView)findViewById(R.id.imageView);

        startMqtt();

        EnableRuntimePermission();

        reso=getBackCameraResolutionInMp();

        if(reso<8.0) {

            Toast.makeText(this,"Camera quality not good.", Toast.LENGTH_LONG).show();
            button.setEnabled(false);
        }
        else {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                    startActivityForResult(intent, 7);

                }
            });
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 7 && resultCode == RESULT_OK) {

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            imageView.setImageBitmap(bitmap);

            bright=calculateBrightnessEstimate(bitmap, 1); //values b/w 0 and 255 0:dark 255:white
            Toast.makeText(this,"Brightness:"+bright, Toast.LENGTH_LONG).show();
            mqttHelper.publishMessage(Integer.toString(bright));


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

    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA))
        {

            Toast.makeText(this,"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this,"Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(this,"Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

    public float getBackCameraResolutionInMp()
    {
        int noOfCameras = Camera.getNumberOfCameras();
        float maxResolution = -1;
        long pixelCount = -1;
        for (int i = 0;i < noOfCameras;i++)
        {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);

            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
            {
                Camera camera = Camera.open(i);;
                Camera.Parameters cameraParams = camera.getParameters();
                for (int j = 0;j < cameraParams.getSupportedPictureSizes().size();j++)
                {
                    long pixelCountTemp = cameraParams.getSupportedPictureSizes().get(j).width * cameraParams.getSupportedPictureSizes().get(j).height; // Just changed i to j in this loop
                    if (pixelCountTemp > pixelCount)
                    {
                        pixelCount = pixelCountTemp;
                        maxResolution = ((float)pixelCountTemp) / (1024000.0f);
                    }
                }

                camera.release();
            }
        }

        Toast.makeText(this,"resolution:"+maxResolution, Toast.LENGTH_LONG).show();

        return maxResolution;
    }

    public int calculateBrightnessEstimate(android.graphics.Bitmap bitmap, int pixelSpacing) {
        int R = 0; int G = 0; int B = 0;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int n = 0;
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < pixels.length; i += pixelSpacing) {
            int color = pixels[i];
            R += Color.red(color);
            G += Color.green(color);
            B += Color.blue(color);
            n++;
        }
        return (R + B + G) / (n * 3);
    }

}
