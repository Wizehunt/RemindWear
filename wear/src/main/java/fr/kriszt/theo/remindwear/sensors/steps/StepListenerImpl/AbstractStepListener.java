package fr.kriszt.theo.remindwear.sensors.steps.StepListenerImpl;

import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import fr.kriszt.theo.remindwear.sensors.steps.StepListener;

public abstract class AbstractStepListener implements StepListener {

    int count = 0;
    private SensorEventListener sensorEventListener = null;
    private SensorManager sensorManager;

    AbstractStepListener(SensorManager sm) {
        sensorManager = sm;
    }

    @Override
    public int getSteps() {
        return count;
    }

    @Override
    public void step() {
        count++;
    }

    @Override
    public void setSensorEventListener(SensorEventListener sel) {
        sensorEventListener = sel;
    }

    @Override
    public void unregisterSensor() {
        if (sensorEventListener != null) {
            sensorManager.unregisterListener(sensorEventListener);
        }
    }
}
