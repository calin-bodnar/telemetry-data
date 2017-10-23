package calin.bodnar.telemetrydata.app;

import javax.inject.Singleton;

import calin.bodnar.telemetrydata.data.source.service.TelemetryService;
import calin.bodnar.telemetrydata.telemetry.TelemetryActivity;
import dagger.Component;

@Singleton
@Component(modules = {TelemetryModule.class})
public interface TelemetryComponent {

    void inject(TelemetryService service);

    void inject(TelemetryActivity activity);
}
