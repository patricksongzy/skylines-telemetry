package patricksongzy.skylines.processing.data.enriched;

import patricksongzy.skylines.processing.data.TelemetryData;
import patricksongzy.skylines.processing.data.sensor.AmbulanceTelemetry;

import java.time.Instant;

public class AmbulanceTripSegment extends TelemetryData {
    private Instant startTimestamp;
    private long ambulanceKey;
    private AmbulanceTelemetry.AmbulanceState state;
    private long patient;

    public AmbulanceTripSegment() {}

    public AmbulanceTripSegment(Instant timestamp, long ambulanceKey, AmbulanceTelemetry.AmbulanceState state, long patient, Instant startTimestamp) {
        this(patient, timestamp, ambulanceKey, state, patient, startTimestamp);
    }

    public AmbulanceTripSegment(long key, Instant timestamp, long ambulanceKey, AmbulanceTelemetry.AmbulanceState state, long patient, Instant startTimestamp) {
        super(key, timestamp);
        this.ambulanceKey = ambulanceKey;
        this.state = state;
        this.patient = patient;
        this.startTimestamp = startTimestamp;
    }

    public long getAmbulanceKey() {
        return ambulanceKey;
    }

    public void setAmbulanceKey(long ambulanceKey) {
        this.ambulanceKey = ambulanceKey;
    }

    public AmbulanceTelemetry.AmbulanceState getState() {
        return state;
    }

    public void setState(AmbulanceTelemetry.AmbulanceState state) {
        this.state = state;
    }

    public Instant getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Instant startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getPatient() {
        return patient;
    }

    public void setPatient(long patient) {
        this.patient = patient;
    }
}
