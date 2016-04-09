package uk.co.drdv.rotoracing;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;

public class GlCamera implements SensorEventListener {


    public float[] projectionMatrix = new float[16];
    public volatile float[] modelViewMatrix = new float[16];
    public volatile float[] mvpMatrix = new float[16];

    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private Quaternion quaternion = new Quaternion();
    private Car car;

    public GlCamera(Context context, Car car) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        this.car = car;
    }

    public void initialise(int width, int height) {
        float aspect = (float) width / height;
        // Field of view across the shortest screen dimension.
        // (Horizontally in portrait mode.)
        // 50 degrees seems roughly right to match the device camera.
        float fov = 50;
        if (width < height) {
            double d = width / 2.0 / Math.tan(Math.toRadians(fov) / 2);
            fov = (float) Math.toDegrees(2 * Math.atan(height / 2.0 / d));
        }
        Matrix.perspectiveM(projectionMatrix, 0, fov, aspect, 0.1f, 10);
        Matrix.setIdentityM(modelViewMatrix, 0);
    }

    public void onResume() {
        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public void onPause() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        quaternion.setAll(event.values[0], event.values[1], event.values[2], event.values[3]);
        Quaternion.toRotationMatrix(quaternion, modelViewMatrix);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);
        car.move(modelViewMatrix);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Don't care.
    }
}
