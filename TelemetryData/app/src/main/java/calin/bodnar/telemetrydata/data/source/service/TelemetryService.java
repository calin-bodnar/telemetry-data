package calin.bodnar.telemetrydata.data.source.service;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import javax.inject.Inject;

import calin.bodnar.telemetrydata.app.TelemetryApp;

public class TelemetryService extends JobIntentService {
    public static final String WORK_FETCH_TELEMETRY_DATA = ".FETCH_TELEMETRY_DATA";
    private static final int JOB_ID = 65743;
    @Inject
    TelemetryMonitor telemetryMonitor;

    /**
     * Convenience method for enqueuing work.
     */
    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, TelemetryService.class, JOB_ID, work);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((TelemetryApp) getApplication()).getTelemetryComponent().inject(this);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (WORK_FETCH_TELEMETRY_DATA.equals(intent.getAction())) {
            telemetryMonitor.start();
        }
    }
}
