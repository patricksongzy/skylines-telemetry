package patricksongzy.skylines.processing.data.sensor;

import patricksongzy.skylines.processing.data.TelemetryData;

public class Heartbeat extends TelemetryData {
    private double timePerFrameMillis;

    public double getTimePerFrameMillis() {
        return timePerFrameMillis;
    }

    public void setTimePerFrameMillis(double timePerFrameMillis) {
        this.timePerFrameMillis = timePerFrameMillis;
    }
}
