package calin.bodnar.telemetrydata.telemetry;

import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import calin.bodnar.telemetrydata.data.TelemetryData;
import calin.bodnar.telemetrydata.data.source.service.TelemetryMonitor;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subscribers.ResourceSubscriber;

public class TelemetryViewModel {
    private TelemetryMonitor telemetryMonitor;
    private BehaviorSubject<TelemetryUiModel> viewModelSubject = BehaviorSubject.create();

    @Inject
    public TelemetryViewModel(TelemetryMonitor telemetryMonitor) {
        this.telemetryMonitor = telemetryMonitor;
        readData();
    }

    public Flowable<TelemetryUiModel> getUiModel() {
        return viewModelSubject.toFlowable(BackpressureStrategy.BUFFER);
    }

    private void readData() {
        telemetryMonitor.getStream()
                .buffer(30 * DateUtils.SECOND_IN_MILLIS, DateUtils.SECOND_IN_MILLIS, TimeUnit.MILLISECONDS)
                .map(new Function<List<TelemetryData>, TelemetryUiModel>() {
                    @Override
                    public TelemetryUiModel apply(@NonNull List<TelemetryData> values) {
                        if (values.isEmpty()) {
                            return new TelemetryUiModel();
                        }
                        double sum = 0.0;
                        short min = values.get(0).getData(), max = values.get(0).getData();
                        for (TelemetryData value : values) {
                            sum += value.getData();
                            if (value.getData() < min) {
                                min = value.getData();
                            }
                            if (value.getData() > max) {
                                max = value.getData();
                            }
                        }
                        return new TelemetryUiModel(sum / values.size(), max, min);
                    }
                }).subscribe(new ResourceSubscriber<TelemetryUiModel>() {
            @Override
            public void onNext(TelemetryUiModel telemetryUiModel) {
                viewModelSubject.onNext(telemetryUiModel);
            }

            @Override
            public void onError(Throwable t) {
                viewModelSubject.onError(t);
            }

            @Override
            public void onComplete() {
                viewModelSubject.onComplete();
            }
        });
    }
}
