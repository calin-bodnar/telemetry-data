package calin.bodnar.telemetrydata.app;

import javax.inject.Singleton;

import calin.bodnar.telemetrydata.data.source.service.TelemetryMonitor;
import dagger.Module;
import dagger.Provides;

@Module
public class TelemetryModule {

    private String serverAddress;
    private int serverPort;

    public TelemetryModule(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    @Provides
    @Singleton
    TelemetryMonitor providesTelemetryMonitor() {
        return new TelemetryMonitor(serverAddress, serverPort);
    }
}
