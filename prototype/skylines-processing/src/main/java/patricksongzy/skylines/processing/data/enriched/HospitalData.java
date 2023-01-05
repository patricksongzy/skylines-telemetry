package patricksongzy.skylines.processing.data.enriched;

import org.apache.flink.api.java.tuple.Tuple2;
import patricksongzy.skylines.processing.data.TelemetryData;
import patricksongzy.skylines.processing.data.sensor.HospitalTelemetry;

import java.time.Instant;

public class HospitalData extends TelemetryData {
    private int ambulances;
    private Tuple2<Float, Float> position;
    private Instant trackTimestamp;

    public HospitalData() {}

    public HospitalData(HospitalTelemetry telemetry, Instant trackTimestamp) {
        super(telemetry.getKey(), telemetry.getTimestamp());
        this.ambulances = telemetry.getAmbulances();
        this.position = Tuple2.of(telemetry.getX(), telemetry.getZ());
        this.trackTimestamp = trackTimestamp;
    }

    public boolean hasChanged(HospitalTelemetry other) {
        // we can check if the building moved by casting the coordinates to ints
        return this.ambulances != other.getAmbulances() ||
                this.position.f0.intValue() != (int) other.getX() || this.position.f1.intValue() != (int) other.getZ();
    }

    public int getAmbulances() {
        return ambulances;
    }

    public void setAmbulances(int ambulances) {
        this.ambulances = ambulances;
    }

    public Tuple2<Float, Float> getPosition() {
        return position;
    }

    public void setPosition(Tuple2<Float, Float> position) {
        this.position = position;
    }

    public Instant getTrackTimestamp() {
        return trackTimestamp;
    }

    public void setTrackTimestamp(Instant trackTimestamp) {
        this.trackTimestamp = trackTimestamp;
    }
}
