package com.ece420.lab1;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class StepDetector {

    /* Public vars */
    public static final int N_SAMPLES = 25;
    public float lastStepAccelVal;

    /* Parameters */
    private final long SEC_TO_MS = 1000;
    private final float ACCEL_THRESHOLD = 2.5f;
    private final long MIN_TIME_BETWEEN_STEPS = Math.round(SEC_TO_MS * 0.2);

    /* State variables */
    private long timeSinceLastStep;
    private long lastTimestamp;
    private List<Float> accelBuffer;

    public StepDetector() {
        timeSinceLastStep = MIN_TIME_BETWEEN_STEPS + 1;
        lastTimestamp = 0;
        lastStepAccelVal = 0;

        accelBuffer = new LinkedList<>();
    }


    public boolean detect(float accel, long timestamp) {
        boolean stepDetected = false;

        // Log.d("STEP_DETECTOR", "Got accel data: " + Float.toString(accel));

        long delta = timestamp - lastTimestamp;
        lastTimestamp = timestamp;
        timeSinceLastStep += delta;

        // Regardless of whether a step was detected, always push new data
        accelBuffer.add(accel);

        // No need to check for multiple, this could only go over by one
        if (accelBuffer.size() > N_SAMPLES) {
            accelBuffer.remove(0);
        }

        if (timeSinceLastStep > MIN_TIME_BETWEEN_STEPS && accelBuffer.size() == N_SAMPLES) {
            int candidateIdx = N_SAMPLES / 2;
            if (accelBuffer.get(candidateIdx) > ACCEL_THRESHOLD) {
                Log.d("STEP_DETECTOR", Float.toString(accelBuffer.get(candidateIdx)));
                int maxIdx = getMaxIdx(accelBuffer);

                if (maxIdx == candidateIdx) {
                    stepDetected = true;
                    lastStepAccelVal = accelBuffer.get(candidateIdx);
                }
            }
        }

        return stepDetected;
    }

    private int getMaxIdx(List<Float> list) {
        int maxIdx = 0;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) > list.get(maxIdx)) {
                maxIdx = i;
            }
        }

        return maxIdx;
    }

}
