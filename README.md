# Telemetry Data App

The application was build around the MVVM architectural pattern using RxJava2 framework (for data binding) and Dagger2 as the dependency injection tool.

### Components
1. JobIntentService - a background service used to enqueue tasks that could run also in the background. The service is started when the application is launched.
2. DataModel - represented by the **TelemetryMonitor** which listens for sensor data packets and further exposes the data stream.
3. ViewModel - represented by **TelemetryViewModel** which contains the app business logic. It subscribes to the sensor data stream exposed by the **TelemetryMonitor**, it processes the data and then further exposes a stream of UI states.
4. View - simple application UI which subscribes to the ViewModel


### How to run the app

The app was developed using Android Studio 2.3.3 and requires:
* buildToolsVersion v26.0.2
* Android SDK API level 26
* Android virtual device with API level 21 or higher


The app runs on port 53879, so please make sure the server is started on this port prior to running the app.

There are two configurations:
1. app - runs the app
2. jUnit- runs the unit tests
