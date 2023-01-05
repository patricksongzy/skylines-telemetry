package patricksongzy.skylines.processing.data.enriched;

import org.apache.flink.api.java.tuple.Tuple2;
import patricksongzy.skylines.processing.data.TelemetryData;
import patricksongzy.skylines.processing.data.sensor.AmbulanceTelemetry;

import java.time.Instant;

public class AmbulanceData extends TelemetryData {
    private Instant trackTimestamp;
    private float speed;
    private AmbulanceTelemetry.AmbulanceState state;
    private int[] patients;
    private Tuple2<Float, Float> position;
    private int source;
    private int target;

    public AmbulanceData(AmbulanceTelemetry telemetry, Instant trackTimestamp) {
        super(telemetry.getKey(), telemetry.getTimestamp());
        this.trackTimestamp = trackTimestamp;
        this.speed = telemetry.getSpeed();
        this.state = telemetry.getState();
        this.patients = telemetry.getPatients();
        this.position = Tuple2.of(telemetry.getX(), telemetry.getZ());
        this.source = telemetry.getSource();
        this.target = telemetry.getTarget();
    }

    public Instant getTrackTimestamp() {
        return trackTimestamp;
    }

    public void setTrackTimestamp(Instant trackTimestamp) {
        this.trackTimestamp = trackTimestamp;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
    public AmbulanceTelemetry.AmbulanceState getState() {
        return state;
    }

    public void setState(AmbulanceTelemetry.AmbulanceState state) {
        this.state = state;
    }

    public int[] getPatients() {
        return patients;
    }

    public void setPatients(int[] patients) {
        this.patients = patients;
    }

    public Tuple2<Float, Float> getPosition() {
        return position;
    }

    public void setPosition(Tuple2<Float, Float> position) {
        this.position = position;
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
