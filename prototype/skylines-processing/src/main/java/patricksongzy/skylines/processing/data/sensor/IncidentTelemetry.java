package patricksongzy.skylines.processing.data.sensor;

import patricksongzy.skylines.processing.data.TelemetryData;

import java.time.Instant;

public class IncidentTelemetry extends TelemetryData {
    private Instant enRouteTimestamp;
    private int citizen;
    private int building;
    private int priority;
    private float x;
    private float z;
    private int amount;
    private boolean isInAmbulance;

    public int getCitizen() {
        return citizen;
    }

    public void setCitizen(int citizen) {
        this.citizen = citizen;
    }

    public int getBuilding() {
        return building;
    }

    public void setBuilding(int building) {
        this.building = building;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean getIsInAmbulance() {
        return isInAmbulance;
    }

    public void setIsInAmbulance(boolean isInAmbulance) {
        this.isInAmbulance = isInAmbulance;
    }

    public Instant getEnRouteTimestamp() {
        return enRouteTimestamp;
    }

    public void setEnRouteTimestamp(Instant enRouteTimestamp) {
        this.enRouteTimestamp = enRouteTimestamp;
    }
}
