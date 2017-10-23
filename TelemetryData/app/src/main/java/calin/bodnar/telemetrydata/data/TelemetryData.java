package calin.bodnar.telemetrydata.data;

public class TelemetryData {
    private int timeMarker;
    private short data;

    public TelemetryData(int timeMarker, short data) {
        this.data = data;
        this.timeMarker = timeMarker;
    }

    public short getData() {
        return data;
    }

    public int getTimeMarker() {
        return timeMarker;
    }
}
