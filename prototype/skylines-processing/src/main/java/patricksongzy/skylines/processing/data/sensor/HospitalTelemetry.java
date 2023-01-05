package patricksongzy.skylines.processing.data.sensor;

import patricksongzy.skylines.processing.data.TelemetryData;

import java.time.Instant;

public class HospitalTelemetry extends TelemetryData {
    private int ambulances;
    private float x;
    private float z;

    public HospitalTelemetry() {}

    public HospitalTelemetry(long key, Instant timestamp, int ambulances) {
        super(key, timestamp);
        this.ambulances = ambulances;
    }

    public int getAmbulances() {
        return ambulances;
    }

    public void setAmbulances(int ambulances) {
        this.ambulances = ambulances;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }
}
