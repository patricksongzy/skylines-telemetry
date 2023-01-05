package patricksongzy.skylines.processing.data.enriched;

import org.apache.flink.api.java.tuple.Tuple2;
import patricksongzy.skylines.processing.data.TelemetryData;
import patricksongzy.skylines.processing.data.sensor.IncidentTelemetry;

import java.time.Instant;

public class IncidentData extends TelemetryData {
    private Instant enRouteTimestamp;
    private int citizen;
    private int building;
    private int priority;
    private boolean isInAmbulance;
    private Tuple2<Float, Float> location;

    public IncidentData() {}

    public IncidentData(IncidentTelemetry telemetry) {
        super(telemetry.getKey(), telemetry.getTimestamp());
        this.enRouteTimestamp = telemetry.getEnRouteTimestamp();
        this.citizen = telemetry.getCitizen();
        this.building = telemetry.getBuilding();
        this.priority = telemetry.getPriority();
        this.isInAmbulance = telemetry.getIsInAmbulance();
        this.location = Tuple2.of(telemetry.getX(), telemetry.getZ());
    }

    public Instant getEnRouteTimestamp() {
        return enRouteTimestamp;
    }

    public void setEnRouteTimestamp(Instant enRouteTimestamp) {
        this.enRouteTimestamp = enRouteTimestamp;
    }

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

    public boolean getIsInAmbulance() {
        return isInAmbulance;
    }

    public void setIsInAmbulance(boolean isInAmbulance) {
        this.isInAmbulance = isInAmbulance;
    }

    public Tuple2<Float, Float> getLocation() {
        return location;
    }

    public void setLocation(Tuple2<Float, Float> location) {
        this.location = location;
    }
}
