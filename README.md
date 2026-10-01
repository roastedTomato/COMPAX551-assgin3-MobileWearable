# Wear OS Sensor Companion

An Android phone and Wear OS companion app built with Kotlin and Jetpack Compose. The watch collects motion and heart rate readings; the phone processes the data and displays live charts.

## Features

- Accelerometer and gyroscope readings from watch sensors.
- Heart rate measurement using Wear OS Health Services.
- Watch-to-phone communication using the Wearable Data Layer: motion readings via MessageClient and heart rate via DataClient.
- Phone charts for movement, rotation and smoothed heart rate.
- Movement intensity, rotation status and heart rate zone summaries.
- Receiving, waiting and stale connection indicators.
- Resettable phone-side session history.

## Project structure

- `phone/`: Android companion UI, data reception, processing and visualisation.
- `wear/`: Wear OS UI, sensor collection, permissions and data transmission.
- `gradle/`: shared dependency versions and Gradle wrapper configuration.

## Requirements

- Android Studio with support for the project's Android Gradle Plugin.
- JDK 21 for the configured Gradle daemon.
- Android SDK matching the compile SDK configuration (API 37, minor API level 1).
- Android phone/emulator (API 24+) and Wear OS watch/emulator (API 30+), paired with Google Play services available.
- A watch with the relevant sensors for live readings; heart rate availability depends on device and permission support.

## Run

1. Open this repository in Android Studio and sync Gradle.
2. Pair the Android phone and Wear OS device or emulators.
3. Run the `phone` module on the phone and the `wear` module on the watch, using matching signing credentials for Data Layer communication.
4. Open the sensor screen on the watch and grant the requested heart rate permission.
5. Keep the phone app open to view incoming readings and charts.

To build debug APKs from the project root:

```sh
./gradlew :phone:assembleDebug :wear:assembleDebug
```

## Notes

This project was developed for a mobile and wearable computing assignment. Sensor summaries use simple thresholds and rolling windows; they are demonstration features, not medical assessments. Coursework documents, local notes, device configuration, signing keys and generated build outputs are excluded from the repository.
