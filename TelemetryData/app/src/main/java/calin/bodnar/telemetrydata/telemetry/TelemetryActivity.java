package calin.bodnar.telemetrydata.telemetry;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import calin.bodnar.telemetrydata.R;
import calin.bodnar.telemetrydata.app.TelemetryApp;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.subscribers.ResourceSubscriber;

public class TelemetryActivity extends AppCompatActivity {
    @Inject
    TelemetryViewModel viewModel;

    private TextView minTextView;
    private TextView avgTextView;
    private TextView maxTextView;
    private ImageView statusImage;

    private ResourceSubscriber<TelemetryUiModel> telemetryObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telemetry);
        ((TelemetryApp) getApplication()).getTelemetryComponent().inject(this);
        minTextView = findViewById(R.id.minValue);
        avgTextView = findViewById(R.id.avgValue);
        maxTextView = findViewById(R.id.maxValue);
        statusImage = findViewById(R.id.statusImage);
    }

    @Override
    protected void onStart() {
        super.onStart();
        telemetryObserver = getTelemetryObserver();
        viewModel.getUiModel()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(telemetryObserver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        telemetryObserver.dispose();
    }

    private ResourceSubscriber<TelemetryUiModel> getTelemetryObserver() {
        return new ResourceSubscriber<TelemetryUiModel>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onNext(TelemetryUiModel uiModel) {
                minTextView.setText(String.format("%d", uiModel.getMin()));
                avgTextView.setText(String.format("%.1f", uiModel.getAverage()));
                maxTextView.setText(String.format("%d", uiModel.getMax()));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                statusImage.setImageDrawable(getDrawable(R.drawable.status_error));
            }

            @Override
            public void onComplete() {
            }
        };
    }
}
