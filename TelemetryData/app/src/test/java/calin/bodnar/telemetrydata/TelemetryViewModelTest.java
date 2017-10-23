package calin.bodnar.telemetrydata;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import calin.bodnar.telemetrydata.data.TelemetryData;
import calin.bodnar.telemetrydata.data.source.service.TelemetryMonitor;
import calin.bodnar.telemetrydata.telemetry.TelemetryUiModel;
import calin.bodnar.telemetrydata.telemetry.TelemetryViewModel;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subscribers.TestSubscriber;

import static org.mockito.Mockito.when;

public class TelemetryViewModelTest {
    private static List<TelemetryData> DATA;
    @Mock
    private TelemetryMonitor monitor;
    private TelemetryViewModel viewModel;
    private TestSubscriber<TelemetryUiModel> subscriber;
    private Flowable<TelemetryData> flowable;

    @Before
    public void setupTelemetryViewModel() {
        MockitoAnnotations.initMocks(this);
        DATA = new ArrayList<>(Arrays.asList(new TelemetryData(1, (short) 80),
                new TelemetryData(2, (short) 75),
                new TelemetryData(3, (short) 85)));

        subscriber = new TestSubscriber<>();
    }

    @Test
    public void getTelemetryModel_emitsData_whenStreamDataAvailable() {
        TestScheduler scheduler = new TestScheduler();
        flowable = Flowable.interval(10, TimeUnit.SECONDS, scheduler)
                .map(new Function<Long, TelemetryData>() {
                    @Override
                    public TelemetryData apply(@NonNull Long index) throws Exception {
                        return DATA.get(index.intValue());
                    }
                })
                .take(DATA.size());
        when(monitor.getStream()).thenReturn(flowable);
        viewModel = new TelemetryViewModel(monitor);
        viewModel.getUiModel().subscribe(subscriber);
        scheduler.advanceTimeTo(30, TimeUnit.SECONDS);

        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);

        TelemetryUiModel model = subscriber.values().get(0);
        Assert.assertEquals(model.getAverage(), 80, 0);
        Assert.assertEquals(model.getMax(), 85, 0);
        Assert.assertEquals(model.getMin(), 75, 0);
    }

    @Test
    public void getTelemetryModel_emitsDefault_whenEmptyStream() {
        TestScheduler scheduler = new TestScheduler();
        flowable = Flowable.interval(10, TimeUnit.SECONDS, scheduler)
                .take(DATA.size())
                .filter(new Predicate<Long>() {
                    @Override
                    public boolean test(@NonNull Long aLong) throws Exception {
                        return false;
                    }
                })
                .map(new Function<Long, TelemetryData>() {
                    @Override
                    public TelemetryData apply(@NonNull Long index) throws Exception {
                        return DATA.get(index.intValue());
                    }
                });
        when(monitor.getStream()).thenReturn(flowable);
        viewModel = new TelemetryViewModel(monitor);
        viewModel.getUiModel().subscribeOn(scheduler).subscribe(subscriber);
        scheduler.advanceTimeTo(30, TimeUnit.SECONDS);

        subscriber.assertNoErrors();
        subscriber.assertValueCount(1);

        TelemetryUiModel model = subscriber.values().get(0);
        Assert.assertEquals(model.getAverage(), 0, 0);
        Assert.assertEquals(model.getMax(), 0, 0);
        Assert.assertEquals(model.getMin(), 0, 0);
    }

    @Test
    public void getTelemetryModel_emitsError_whenStreamError() {
        when(monitor.getStream()).thenReturn(Flowable.<TelemetryData>error(new Exception("error")));
        viewModel = new TelemetryViewModel(monitor);
        viewModel.getUiModel().subscribe(subscriber);

        subscriber.assertNoValues();
        subscriber.assertError(Exception.class);
    }
}
