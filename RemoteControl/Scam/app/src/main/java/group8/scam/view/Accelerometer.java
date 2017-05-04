package group8.scam.view;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


import group8.scam.controller.handlers.HandleThread;

import static group8.scam.model.communication.DataThread.MESSAGE_WRITE;
import static java.lang.Math.atan;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Created by omidm on 4/28/2017.
 */

public class Accelerometer implements SensorEventListener {

    private SensorManager sensorManager;
    private HandleThread mHandle = HandleThread.getInstance();
    private Sensor sensor;
    private String dataStr;
    private boolean isTurning = false;
    private double Rx;
    private double Ry;
    private double Rz;

    private long lastUpdate = 0;

    public Accelerometer (Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void onPause(){
        sensorManager.unregisterListener(this);
    }
    public void onResume(){
        sensorManager.registerListener(this, sensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor mySensor = event.sensor;

        if(mySensor.getType() == Sensor.TYPE_ACCELEROMETER){

            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];

            Rx = atan( x / (sqrt(pow(y,2) + pow(z,2))));
            Rx *= 180.00;
            Rx /= 3.141592;
            Ry = atan( y / (sqrt(pow(x,2) + pow(z,2))));
            Ry *= 180.00;
            Ry /= 3.141592;
            Rz = atan(sqrt(pow(x,2) + pow(y,2)) / z);
            Rz *= 180.00;
            Rz /= 3.141592;

            long curTime = System.currentTimeMillis();


            if ((curTime - lastUpdate) > 100) {
                lastUpdate = curTime;

                int angle = getCarAngle();
                int speed = getCarSpeed();
                dataStr = angle + ":" + speed + ":";

                mHandle.sendMessage(MESSAGE_WRITE, dataStr);
                System.out.println(dataStr);
                System.out.println("X is: " + Rx);
                System.out.println("Y is: " + Ry);
                System.out.println("Z is: " + Rz);
                System.out.println(" ");
                System.out.println(" ");
                System.out.println(angle);
            }
        }
    }

    private int getCarSpeed() {
        /*if (Rx < 105 && Rx > 75 && Ry < 3 && Ry > -3) {
            //System.out.println("STOPPED");
            return 0;
        }
        else if (Rx < 75 && Rz < 75 && Rz > 0) {
            //System.out.println("FORWARD");
            return 50;
        }
        else if (Rx < 75 && Rz < 0) {
            //System.out.println("BACKWARD");
            return -50;
        }
        else if (isTurning) {
            return 50;
        }
        else
            return 0;*/

        if (isTurning) {
            return 50;
        }
        else if(Rz < 90 && Rz >= 60){
            return 0;
        }
        else if(Rz < 60 && Rz >= 35){
            return 35;
        }
        else if(Rz < 35 && Rz >= 0){
            return 50;
        }
        else if(Rz <= -65 && Rz > -90){
            return 0;
        }
        else if(Rz <= -35 && Rz > -65){
            return -30;
        }
        else if(Rz < 0 && Rz > -35){
            return -50;
        }
        else{
            return 0;
        }
    }

    private int getCarAngle() {
        /*if (Rx < 90 && Ry > 10 && ((Rz > 75 && Rz < 91) || (Rz > -91 && Rz < -75))) {
            isTurning = true;
            System.out.println("RIGHT");
            return 45;
        }
        else if (Rx < 90 && Ry < -10 && ((Rz > 75 && Rz < 91) || (Rz > -91 && Rz < -75))) {
            isTurning = true;
            System.out.println("LEFT");
            return -45;
        }
        else {
            isTurning = false;
            System.out.println("NONE");
            return 0;
        }*/

        if(Ry <= 15 && Ry > -15){
            isTurning = false;
            return 0;
        }
        else if(Ry < -15 && Ry >= -35){
            isTurning = true;
            return -35;
        }
        else if(Ry < -35 && Ry >= -70){
            isTurning = true;
            return -60;
        }
        else if(Ry <= 35 && Ry > 15){
            isTurning = true;
            return 35;
        }
        else if(Ry <= 70 && Ry > 35){
            isTurning = true;
            return 60;
        }
        else{
            isTurning = false;
            return 0;
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}