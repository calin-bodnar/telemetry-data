package calin.bodnar.telemetrydata.app;

import android.app.Application;
import android.content.Intent;

import calin.bodnar.telemetrydata.data.source.service.TelemetryService;

public class TelemetryApp extends Application {

    private TelemetryComponent telemetryComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        telemetryComponent = DaggerTelemetryComponent.builder()
                .telemetryModule(new TelemetryModule("10.0.2.2", 53879))
                .build();
        startService();
    }

    public TelemetryComponent getTelemetryComponent() {
        return telemetryComponent;
    }

    private void startService() {
        Intent telemetryServiceIntent = new Intent(TelemetryService.WORK_FETCH_TELEMETRY_DATA);
        TelemetryService.enqueueWork(this, telemetryServiceIntent);
    }
}
