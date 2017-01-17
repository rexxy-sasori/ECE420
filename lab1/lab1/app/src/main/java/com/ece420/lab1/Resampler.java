package com.ece420.lab1;

public class Resampler {
    private long samplingDeltaMs;
    private float[] lastSensorValue;
    private long lastTimestamp;

    public Resampler(long inputSamplingDeltaMs) {
        samplingDeltaMs = inputSamplingDeltaMs;
        lastSensorValue = new float[] {0f, 0f, 0f};
        lastTimestamp = -1;
    }

    public float[] resample(float[] curSensorValue, long curTimestamp) {
        float[] retVal = {0f, 0f, 0f};

        long nextSampleTime = lastTimestamp + samplingDeltaMs;

        /* First sample is wonky, avoid altogether */
        if (lastTimestamp > 0) {
            /* If new sample happens before it's expected, return the old sample */
            if (curTimestamp < nextSampleTime) {
                System.arraycopy(lastSensorValue, 0, retVal, 0, lastSensorValue.length);
            } else {
                System.arraycopy(curSensorValue, 0, retVal, 0, curSensorValue.length);
            }
        }

        /* Update to save state */
        System.arraycopy(curSensorValue, 0, lastSensorValue, 0, curSensorValue.length);
        lastTimestamp = curTimestamp;

        return retVal;
    }


}
