package com.ece420.lab1;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.jjoe64.graphview.series.DataPoint;

import java.io.File;
import java.io.FileWriter;

/* http://stackoverflow.com/questions/18759849/how-to-write-a-class-to-read-the-sensor-value-in-android */
public class SensorReader implements SensorEventListener {

    // Not using resampling for now
    private final long SEC_TO_MS = 1000;
    private final float RESAMPLE_RATE = 0.04f;
    private final long RESAMPLE_PERIOD_IN_MS = Math.round(SEC_TO_MS * RESAMPLE_RATE);

    private final int SAMPLE_RATE = 100;

    private final Context mContext;
    private final String csvFilename = "sensor_data.csv";
    private FileWriter writer;

    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer;
    private final Sensor mGyroscope;

    private StepDetector mStepDetector;
    private Resampler mAccelResampler;
    private Resampler mGyroResampler;

    private float[] lastAccelData = {0f, 0f, 0f};
    private float[] lastGyroData = {0f, 0f, 0f};

    private int numSteps;
    private int accelSampleIdx;

    public SensorReader(Context context) {
        mContext = context;

        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        mStepDetector = new StepDetector();
        mAccelResampler = new Resampler(RESAMPLE_PERIOD_IN_MS);
        mGyroResampler = new Resampler(RESAMPLE_PERIOD_IN_MS);

        numSteps = 0;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // STUB, do nothing
    }

    public boolean startCollection() {
        try {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            File file = new File(path, csvFilename);
            file.createNewFile();
            writer = new FileWriter(file);

            Toast toast = Toast.makeText(mContext, "CSV file created successfully!", Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
            Toast toast = Toast.makeText(mContext, "CSV file could not be created", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        register();

        accelSampleIdx = 0;
        numSteps = 0;

        DataPoint newVal[] = { new DataPoint(-1, -1) };
        ((PedometerSimple) mContext).accelGraphData.resetData(newVal);
        ((PedometerSimple) mContext).accelGraphSteps.resetData(newVal);

        return true;
    }

    public void stopCollection() {
        unregister();
    }

    public void register() {
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void unregister() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long timestamp = System.currentTimeMillis();

        boolean stepDetected = false;

        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            lastAccelData = event.values;
            accelSampleIdx++;
            stepDetected = mStepDetector.detect(lastAccelData[2], System.currentTimeMillis());

            // Graph updates
            DataPoint newVal = new DataPoint(accelSampleIdx, lastAccelData[2]);
            ((PedometerSimple) mContext).accelGraphData.appendData(newVal, true, 100 * SAMPLE_RATE);

        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            lastGyroData = mGyroResampler.resample(event.values, timestamp);
            // Not doing anything with gyro, but you could!
        }

        writeToCsv(event);

        if (stepDetected) {
            numSteps = numSteps + 1;
            ((PedometerSimple) mContext).textStatus.setText("Steps detected: " + Integer.toString(numSteps));

            // Graph update
            // NOTE: This is weird. We're actually detecting if an earlier peak was a step.
            // This number is determined by the StepDetector N_SAMPLES buffer length.
            DataPoint newVal = new DataPoint(accelSampleIdx - (StepDetector.N_SAMPLES / 2), mStepDetector.lastStepAccelVal);
            ((PedometerSimple) mContext).accelGraphSteps.appendData(newVal, true, 100 * SAMPLE_RATE);
        }
    }

    private void writeToCsv(SensorEvent event) {
        String newLine = String.valueOf(System.currentTimeMillis()) + ", ";
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)
        {
            newLine += Float.toString(event.values[0]) + ", ";
            newLine += Float.toString(event.values[1]) + ", ";
            newLine += Float.toString(event.values[2]) + ", ";

        }
        else
        {
            newLine += Float.toString(lastAccelData[0]) + ", ";
            newLine += Float.toString(lastAccelData[1]) + ", ";
            newLine += Float.toString(lastAccelData[2]) + ", ";
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE)
        {
            newLine += Float.toString(event.values[0]) + ", ";
            newLine += Float.toString(event.values[1]) + ", ";
            newLine += Float.toString(event.values[2]) + ", ";
        }
        else
        {
            newLine += Float.toString(lastGyroData[0]) + ", ";
            newLine += Float.toString(lastGyroData[1]) + ", ";
            newLine += Float.toString(lastGyroData[2]) + ", ";
        }
        newLine += "\n";

        try
        {
            writer.write(newLine);
            writer.flush();
        }
        catch (Exception e)
        {
            Toast toast = Toast.makeText(mContext, "Could not write to CSV file", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
