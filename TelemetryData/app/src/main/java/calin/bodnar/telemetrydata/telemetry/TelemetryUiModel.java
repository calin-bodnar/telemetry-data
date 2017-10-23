package calin.bodnar.telemetrydata.telemetry;

public class TelemetryUiModel {
    private double average;
    private short max;
    private short min;

    public TelemetryUiModel() {
        this(0, (short) 0, (short) 0);
    }

    public TelemetryUiModel(double average, short max, short min) {
        this.average = average;
        this.max = max;
        this.min = min;
    }

    public double getAverage() {
        return average;
    }

    public short getMax() {
        return max;
    }

    public short getMin() {
        return min;
    }
}
