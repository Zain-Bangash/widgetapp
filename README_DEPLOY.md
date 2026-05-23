# Deployment and Publishing Guide

This guide explains how to prepare the **Warith** app for production and publish it to the Google Play Store.

## 1. Signing Configuration

To publish the app, you need to sign it with a release key.

1. **Generate a Keystore**:
   ```bash
   keytool -genkey -v -keystore warith-release.keystore -alias warith-alias -keyalg RSA -keysize 2048 -validity 10000
   ```
2. **Setup Secrets**:
   If using GitHub Actions, add the following secrets to your repository:
   - `KEYSTORE`: Base64 encoded `.keystore` file.
   - `KEYSTORE_PASSWORD`: The password for your keystore.
   - `KEY_ALIAS`: The alias for your key.
   - `KEY_PASSWORD`: The password for your key.

## 2. Play Store Publishing

1. **Create a Google Play Developer Account**.
2. **Create a New App** in the Google Play Console.
3. **Upload the AAB**:
   The CI workflow generates an Android App Bundle (`.aab`) in the artifacts. Upload this file to the "Internal Testing" or "Production" track.
4. **Store Listing**:
   - Icons and Graphics are located in `app/src/main/res/mipmap`.
   - Provide descriptions and screenshots of the widget on the home screen.

## 3. Local Release Build

To build a release APK locally:
```bash
./gradlew assembleRelease
```
The output will be in `app/build/outputs/apk/release/`.

## 4. Environment Variables

You can configure signing in `app/build.gradle.kts` by reading from environment variables or a `local.properties` file (which is ignored by Git).

Example `local.properties`:
```properties
RELEASE_STORE_FILE=path/to/keystore
RELEASE_STORE_PASSWORD=password
RELEASE_KEY_ALIAS=alias
RELEASE_KEY_PASSWORD=password
```
