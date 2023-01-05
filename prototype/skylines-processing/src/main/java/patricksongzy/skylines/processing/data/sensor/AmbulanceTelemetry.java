package patricksongzy.skylines.processing.data.sensor;

import patricksongzy.skylines.processing.data.TelemetryData;

public class AmbulanceTelemetry extends TelemetryData {
    public enum AmbulanceState {
        // EMERGENCY ->
        // WAIT - giving first aid -> RETURN_EMPTY - patient does not need transport
        // RETURN_FULL - transport patient to hospital
        DESTROYED, STALE, RETURN_FULL, RETURN_EMPTY, WAIT, EMERGENCY, CONFUSED;

        public static boolean isReturning(AmbulanceState state) {
            return state == RETURN_EMPTY || state == RETURN_FULL;
        }
    }

    private float speed;
    private AmbulanceState state;
    private boolean arriving;
    private int[] patients;
    private float x;
    private float z;
    private int source;
    private int target;

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
    public AmbulanceState getState() {
        return state;
    }

    public void setState(AmbulanceState state) {
        this.state = state;
    }

    public boolean getArriving() {
        return arriving;
    }

    public void setArriving(boolean arriving) {
        this.arriving = arriving;
    }

    public int[] getPatients() {
        return patients;
    }

    public void setPatients(int[] patients) {
        this.patients = patients;
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

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }
}
