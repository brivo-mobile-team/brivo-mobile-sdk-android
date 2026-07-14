[![](https://jitpack.io/v/org.bitbucket.brivoinc/mobile-sdk-android.svg)](https://jitpack.io/#org.bitbucket.brivoinc/mobile-sdk-android)

# Brivo Mobile SDK for Android

The Brivo Mobile SDK lets your Android app turn a user's phone into a mobile credential: redeem and manage mobile passes, unlock Brivo doors over Bluetooth (BLE) or the internet, and handle two-factor (biometric) prompts — all through a small, coroutine-friendly public surface. This guide targets external integrators building against SDK **v3.5.0**.

> [!NOTE]
> Additional third-party lock integrations are available from Brivo. This document covers the Brivo core flows only.

## Table of contents

1. [Overview](#1-overview)
2. [Requirements & compatibility](#2-requirements--compatibility)
3. [Installation (JitPack)](#3-installation-jitpack)
4. [Initialization & configuration](#4-initialization--configuration)
5. [Permissions](#5-permissions)
6. [Managing mobile passes](#6-managing-mobile-passes)
7. [Using the SDK without SDK storage](#7-using-the-sdk-without-sdk-storage)
8. [Unlocking doors](#8-unlocking-doors)
9. [Error reference](#9-error-reference)
10. [Keeping up to date & good practices](#10-keeping-up-to-date--good-practices)

---

## 1. Overview

The SDK is delivered as a set of cooperating modules, all under the `com.brivo.sdk` group:

| Module | Responsibility |
| --- | --- |
| `brivocore` | SDK entry point, configuration, shared models, version & device id, networking, logging. |
| `brivoonair` | Mobile passes and Brivo cloud API (redeem, refresh, token refresh, SDK storage). |
| `brivoaccess` | Door-unlock orchestration and nearby-device ("Magic Button") scanning. |
| `brivoble` / `brivoble-core` | Brivo BLE transport for Bluetooth unlocks. |
| `brivolocalauthentication` | Biometric / device-credential authentication for two-factor unlocks. |

The single entry point is the Kotlin object `BrivoSDK`. You initialize it once with a `BrivoConfiguration`, then drive everything else through a few objects:

- `BrivoSDKOnair` — passes and tokens.
- `BrivoSDKAccess` — unlocking and nearby-device scanning.
- `BrivoSDKLocalAuthentication` — biometric prompts for two-factor.

Asynchronous style:

- One-shot / network calls return a sealed `BrivoSDKApiState<T>` (`Success(data)` or `Failed(brivoError: BrivoError)`).
- Unlock and scan operations return a Kotlin `Flow<…>` that you collect.

---

## 2. Requirements & compatibility

| Item | Value |
| --- | --- |
| Min API level | **API 29** for the core modules (`brivocore`, `brivoonair`, `brivoaccess`, `brivoble`, `brivoble-core`, `brivolocalauthentication`). |
| Build JDK | **JDK 17** (the SDK is built and published with the JDK 17 toolchain). |
| Language | Kotlin, coroutines/`Flow`-based. |
| SDK version | **3.5.0** (see `BrivoSDK.version` below). |

> [!NOTE]
> `BrivoSDK.version` returns the string **`"v3.5.0"`** — note the leading `v` prefix.

```kotlin
val version: String = BrivoSDK.version          // "v3.5.0"
val deviceId: String = BrivoSDK.getDeviceId()   // stable per-install UUID (hyphens stripped)
```

`getDeviceId()` returns a UUID (with hyphens removed) that is generated once on first call and persisted in the SDK's `SharedPreferences`, so it is stable across launches for the install.

---

## 3. Installation (JitPack)

The SDK is published via JitPack. Add the JitPack repository to your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Then add the dependency. You can pull every desired module individually.
These are the required ones:

```kotlin
dependencies {
    // Each module separate (Tag = the release tag, e.g. 3.5.0):
    implementation("org.bitbucket.brivoinc.mobile-sdk-android:brivoaccess:tag")
    implementation("org.bitbucket.brivoinc.mobile-sdk-android:brivoble:tag")
    implementation("org.bitbucket.brivoinc.mobile-sdk-android:brivoblecore:tag")
    implementation("org.bitbucket.brivoinc.mobile-sdk-android:brivocore:tag")
    implementation("org.bitbucket.brivoinc.mobile-sdk-android:brivolocalauthentication:tag")
    implementation("org.bitbucket.brivoinc.mobile-sdk-android:brivoonair:tag")
}
```

Per-module coordinates (in-scope modules) follow the published group `com.brivo.sdk`:

| Module | Artifact id |
| --- | --- |
| Core | `brivocore` |
| Passes / cloud | `brivoonair` |
| Access / unlock | `brivoaccess` |
| BLE | `brivoble` |
| BLE core | `brivoble-core` (note the hyphen — **not** `brivoblecore`) |
| Local authentication | `brivolocalauthentication` |

> [!NOTE]
> The exact JitPack coordinate form (group/artifact mapping for per-module dependencies and the release `Tag`) should be **confirmed with Brivo** for your distribution.

---

## 4. Initialization & configuration

Initialize the SDK once, as early as possible — typically in your `Application.onCreate()` — using the application context. `BrivoSDK.init(...)` throws `BrivoSDKInitializationException` if the `clientId` or `clientSecret` is empty. Your client credentials are issued by Brivo and are region-specific.

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            BrivoSDK.init(
                context = applicationContext,
                brivoConfiguration = BrivoConfiguration.builder(
                    clientId = BuildConfig.BRIVO_CLIENT_ID,
                    clientSecret = BuildConfig.BRIVO_CLIENT_SECRET,
                    useSDKStorage = true,
                )
                    .serverRegion(ServerRegion.UNITED_STATES)   // default; use EUROPE for EU
                    .build()
            )
        } catch (e: BrivoSDKInitializationException) {
            // Missing/empty clientId or clientSecret — surface to your crash reporting.
        }
    }
}
```

### Configuration options

`BrivoConfiguration.builder(clientId, clientSecret, useSDKStorage)` returns a `Builder`. The available options:

| Builder method | Type / default | Purpose |
| --- | --- | --- |
| `serverRegion(region)` | `ServerRegion`, default `UNITED_STATES` | Selects the Brivo region. `ServerRegion` has `UNITED_STATES` and `EUROPE`. Your credentials must match the chosen region. |
| `authUrl(url)` | `String`, optional | Override the authentication endpoint (defaults are derived from `serverRegion`). |
| `apiUrl(url)` | `String`, optional | Override the API endpoint (defaults are derived from `serverRegion`). |
| `onTokenRefresh(fn)` | `suspend (clientId: String, clientSecret: String) -> String?`, optional | App-supplied callback the SDK invokes when it needs a fresh token. REQUIRED FOR SDK STORAGE OFF |
| `bleScanTimeout(duration)` | `Duration`, default `10s` | Timeout for BLE scanning. Must be `> 0`. |
| `bleUnlockTimeout(duration)` | `Duration`, default `10s` | Timeout for a BLE unlock attempt. Must be `> 0`. |
| `build()` | — | Produces the `BrivoConfiguration`. |

`useSDKStorage` (the third positional argument to `builder(...)`) decides whether the SDK persists redeemed passes and credentials for you:

- **`useSDKStorage = true`** — the SDK stores redeemed passes (and their credentials) in its own `SharedPreferences`-backed storage. You can then use the id-based unlock variants and retrieve passes from storage without supplying credentials per call.
- **`useSDKStorage = false`** — your app owns all pass data; you supply it on each operation, and are required to handle and store the tokens. See [Using the SDK without SDK storage](#7-using-the-sdk-without-sdk-storage).

> [!NOTE]
> **Running the bundled sample apps:** add your `clientId` and `clientSecret` to the appropriate variables in `gradle.properties`. Otherwise the sample app throws an `IllegalArgumentException` ("Please add your client Id and client Secret inside gradle.properties file") and closes on launch.

---

## 5. Permissions

> [!WARNING]
> The SDK **does not request runtime permissions or declare manifest permissions on your behalf.** The host app must declare the manifest permissions and request the runtime permissions itself. When a permission is missing or Bluetooth/location is disabled, the SDK surfaces it through error codes (see [Error reference](#9-error-reference)) and through `ContinuousScanningState.WaitingForBluetooth` during scanning — it never prompts the user.

### Manifest declarations

Declare the following in your app's `AndroidManifest.xml` (superset covering BLE unlock and trusted-network detection):

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />

<!-- Location (required for trusted-network / Wi-Fi SSID detection and pre-31 BLE scan) -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />

<!-- Bluetooth -->
<uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN"
    android:usesPermissionFlags="neverForLocation"
    tools:targetApi="s"/>
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" tools:targetApi="s" />
<uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" tools:targetApi="s" />

<!-- Biometric / two-factor -->
<uses-permission android:name="android.permission.USE_BIOMETRIC" />
```

### Runtime permissions

Request the runtime permissions yourself before any BLE unlock or scan:

- **API 31+ (Android 12+):** `BLUETOOTH_SCAN` and `BLUETOOTH_CONNECT`.
- **Pre-31 (Android 11 and below):** location (`ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION`) is required for BLE scanning.

Bluetooth must also be **enabled** on the device. If it is off during a scan, the SDK emits `ContinuousScanningState.WaitingForBluetooth`.

> [!WARNING]
> Location permission is also required for **trusted-network detection** — the SDK reads the connected Wi-Fi SSID to decide whether a "trusted network" applies, and Android gates SSID access behind location permission. Without it you'll see `ONAIR_TRUSTED_NETWORK_LOCATION_PERMISSION_NOT_GRANTED` (-3010).

---

## 6. Managing mobile passes

A **mobile pass** (`BrivoOnairPass`) lets a user open doors with their phone. It aggregates the user's identity, the BLE credential, and the `sites → accessPoints` the holder is allowed to open, plus flags indicating which credential types the pass carries. Access the pass API through `BrivoSDKOnair.instance`.

### Redeem a pass

```kotlin
val result = BrivoSDKOnair.instance.redeemPass(passId = "…", passCode = "…")
when (result) {
    is BrivoSDKApiState.Success -> {
        val pass: BrivoOnairPass? = result.data
        // If useSDKStorage = true, the redeemed pass is persisted automatically.
    }
    is BrivoSDKApiState.Failed -> handle(result.brivoError)
}
```

> [!IMPORTANT]
> `redeemPass` takes **`passId`** and **`passCode`** — not an email/token pair. (Earlier docs and KDoc referred to `email`/`token`; those names are inaccurate.)

### Refresh a pass

Refresh a single pass using its tokens:

```kotlin
val tokens = BrivoTokens(accessToken = "…", refreshToken = "…")
when (val result = BrivoSDKOnair.instance.refreshPass(tokens)) {
    is BrivoSDKApiState.Success -> { /* result.data: BrivoOnairPass? */ }
    is BrivoSDKApiState.Failed -> handle(result.brivoError)
}
```

### Retrieve passes from SDK storage

When `useSDKStorage = true`, you can read the locally stored passes. This call is **synchronous** (not `suspend`) and returns a map keyed by pass id:

```kotlin
when (val result = BrivoSDKOnair.instance.retrieveSDKLocallyStoredPasses()) {
    is BrivoSDKApiState.Success -> {
        val passes: LinkedHashMap<String, BrivoOnairPass> = result.data
    }
    is BrivoSDKApiState.Failed -> handle(result.brivoError)
    // Fails with SDK_NOT_CONFIGURED_FOR_LOCAL_STORAGE (-1002) if storage is disabled,
    // or SDK_NO_PASSES_FOUND_IN_LOCAL_STORAGE (-1003) if empty.
}
```

---

## 7. Using the SDK without SDK storage

If you initialize with `useSDKStorage = false`, your app owns all pass data and supplies it on each operation. There is no separate "fetch a fresh access token" call — tokens travel inside the pass and credentials objects:

- After `redeemPass` / `refreshPass`, tokens live in `pass.brivoOnairPassCredentials.tokens` (a `BrivoTokens` of `accessToken` / `refreshToken`).
- You hand those credentials back to the SDK when you build a `BrivoSelectedAccessPoint` for unlocking, or when you call `refreshCredentials(...)`.
- The optional `onTokenRefresh` config callback lets the SDK ask your app for a fresh token when needed.

### Refreshing access tokens

When SDK storage is **off**, refresh tokens yourself:

```kotlin
when (val result = BrivoSDKOnair.instance.refreshAccessTokenWithToken(
    accessToken = "…",
    refreshToken = "…",
)) {
    is BrivoSDKApiState.Success -> { /* result.data: BrivoAuthenticateResponse */ }
    is BrivoSDKApiState.Failed -> handle(result.brivoError)
}
```

> [!NOTE]
> `refreshAccessTokenWithToken(...)` should only be used when SDK storage is off. When storage is on, the SDK refreshes tokens itself (optionally via your `onTokenRefresh` callback).

### Refreshing externally managed credentials

To refresh BLE / lock credentials for passes you manage yourself, use `BrivoSDKAccess.refreshCredentials(...)`. It refreshes credentials across all enabled modules for the supplied passes:

```kotlin
when (val result = BrivoSDKAccess.refreshCredentials(
    passes = myPasses,
    refreshMode = RefreshMode.PROVIDED_ONLY,   // refresh only the supplied passes
)) {
    is BrivoSDKApiState.Success -> { /* Unit */ }
    is BrivoSDKApiState.Failed -> handle(result.brivoError)
}
```

`RefreshMode` values:

| Value | Meaning |
| --- | --- |
| `PROVIDED_ONLY` | Refresh only the passes you pass in. |
| `FALLBACK_TO_LOCAL` | Use provided passes, falling back to locally stored data. (Default.) |
| `LOCAL_ONLY` | Refresh from locally stored data only. |

### Building a selected access point

Without SDK storage, you build the `BrivoSelectedAccessPoint` for an unlock from a pass you already hold. **Do not construct it by hand** — use the `BrivoSelectedAccessPoint.make(...)` factory, which resolves the site, access point, reader uid, door type and BLE time frame internally and returns a `BrivoSDKApiState`:

```kotlin
// Resolve an access point id within a single pass:
when (val result = BrivoSelectedAccessPoint.make(pass = myPass, accessPointId = "…")) {
    is BrivoSDKApiState.Success -> {
        val selected: BrivoSelectedAccessPoint = result.data
        // pass it to unlockAccessPoint(selected, …)
    }
    is BrivoSDKApiState.Failed -> handle(result.brivoError)
    // ONAIR_ACCESS_POINT_NOT_FOUND (-3008) — the id isn't in the pass.
    // ONAIR_INVALID_PASS (404) — the matched pass has no access token.
}
```

There is also an overload that searches a list of passes; the first pass containing the access point wins:

```kotlin
val result = BrivoSelectedAccessPoint.make(passes = myPasses, accessPointId = "…")
```

The resulting `BrivoSelectedAccessPoint` has this shape (you rarely read it directly; hand it straight to `unlockAccessPoint(brivoSelectedAccessPoint, …)` — see [Unlocking doors](#8-unlocking-doors)):

```kotlin
@Serializable
data class BrivoSelectedAccessPoint(
    val accessPointPath: AccessPointPath,          // accessPointId, siteId, passId
    val readerUid: String,
    val bleCredentials: String,
    val deviceModelId: String,
    val timeFrame: Int,
    @SerialName("twoFactorEnabled") val isTwoFactorEnabled: Boolean,
    val doorType: DoorType,
    val passCredentials: BrivoOnairPassCredentials, // userId + tokens
    val minimumAllowedRssi: Int = Int.MIN_VALUE,
    val hasTrustedNetwork: Boolean,
    val sendEvents: Boolean,
)
```

> [!NOTE]
> `BrivoSelectedAccessPoint.make(pass, accessPointId)` / `make(passes, accessPointId)` is the supported way to turn a pass + access point id into a `BrivoSelectedAccessPoint`. The older `BrivoSelectedAccessPointMapper.constructSelectedAccessPoint(...)` helper is **deprecated** — migrate to `make(...)`.

---

## 8. Unlocking doors

All unlocking goes through the `BrivoSDKAccess` object (it is a Kotlin `object` — call methods directly, there is **no** `getInstance()`). The SDK chooses the channel (BLE vs. internet) automatically based on the door type.

### Door types

```kotlin
enum class DoorType {
    UNKNOWN, INTERNET, WAVELYNX,
    HID_ORIGO, HID_ORIGO_OMNIKEY, DORMAKABA, SALTO
}
```

| Door type | Channel |
| --- | --- |
| `INTERNET` | Cloud unlock (requires internet). |
| `WAVELYNX` | Brivo BLE door (Bluetooth). |

### The unlock APIs

```kotlin
// SDK-storage variant (id-based):
fun unlockAccessPoint(
    passId: String,
    accessPointId: String,
    activity: FragmentActivity? = null,
    unlockStrategy: UnlockStrategy? = null,
): Flow<BrivoResult>

// External-credentials variant:
fun unlockAccessPoint(
    brivoSelectedAccessPoint: BrivoSelectedAccessPoint,
    activity: FragmentActivity? = null,
    unlockStrategy: UnlockStrategy? = null,
): Flow<BrivoResult>
```

Collect the returned `Flow<BrivoResult>` and switch over `result.communicationState`:

```kotlin
val job = lifecycleScope.launch {
    BrivoSDKAccess.unlockAccessPoint(
        passId = passId,
        accessPointId = accessPointId,
        activity = this@MyActivity,   // lets the SDK host the biometric prompt here
    ).collect { result ->
        when (result.communicationState) {
            AccessPointCommunicationState.SCANNING       -> showScanning()
            AccessPointCommunicationState.ON_CLOSEST_READER -> showAtReader()
            AccessPointCommunicationState.AUTHENTICATE   -> /* SDK is showing the 2FA prompt — informational, see below */ Unit
            AccessPointCommunicationState.CONNECTING     -> showConnecting()
            AccessPointCommunicationState.COMMUNICATING  -> showCommunicating()
            AccessPointCommunicationState.SUCCESS        -> showUnlocked()
            AccessPointCommunicationState.FAILED         -> handle(result.error)
            else -> Unit
        }
    }
}
```

`BrivoResult` carries:

```kotlin
data class BrivoResult(
    val communicationState: AccessPointCommunicationState,
    val accessPointPath: AccessPointPath = AccessPointPath(),
    val error: BrivoError? = null,
    val bluetoothDeviceAdditionalInfo: AdditionalBleData? = null,
    val scanCooldownDurationInSeconds: Int? = null,
)
```

### Communication states

| State | Meaning |
| --- | --- |
| `SCANNING` | Scanning for the access point. |
| `AUTHENTICATE` | The SDK is showing the two-factor biometric prompt — informational only (below). |
| `CONNECTING` | Connecting to the access point. |
| `COMMUNICATING` | Exchanging data with the access point. |
| `ON_CLOSEST_READER` | The user is at the closest reader. |
| `SUCCESS` | Unlock succeeded. |
| `FAILED` | Unlock failed (`result.error` is populated). |
| ~~`SHOULD_CONTINUE`~~ | **Deprecated** — facility safety should now be checked before the unlock starts; do not handle here. |
| ~~`SCANNING_COOLDOWN`~~ | **Deprecated** — unreliable with continuous scanning; do not handle. |

### Cancelling an unlock

> [!IMPORTANT]
> There is **no** `cancellationSignal` parameter on the current `unlockAccessPoint(...)` overloads. To cancel an in-progress unlock, **cancel the coroutine that is collecting the flow**:
>
> ```kotlin
> job.cancel()   // the Job from launch { … collect { … } }
> ```
>
> The overloads that took a `shouldContinueUnlockOperation` callback are **deprecated**; do not use them in new code, unless using the old unlockNearestBleAccessPoint method.

### Channel selection (multi-channel doors)

Channel selection is automatic and SDK-internal: for a door that supports more than one channel, the SDK picks the appropriate one based on door type and availability. The only public override is `UnlockStrategy`:

```kotlin
enum class UnlockStrategy {
    // Forces internet unlock for Brivo Wavelynx doors, bypassing BLE even when available.
    ForceInternetUnlockForBrivoDoors
}
```

Pass it into either `unlockAccessPoint(...)` overload via the `unlockStrategy` parameter:

```kotlin
BrivoSDKAccess.unlockAccessPoint(
    passId = passId,
    accessPointId = accessPointId,
    unlockStrategy = UnlockStrategy.ForceInternetUnlockForBrivoDoors,
)
```

> [!WARNING]
> `ForceInternetUnlockForBrivoDoors` allows a remote unlock without physical presence at the door. Use with care.

### Two-factor / biometric unlocks

Two-factor is **handled entirely by the SDK**. When an access point requires two-factor, `unlockAccessPoint(...)` checks biometric availability, shows the prompt, and continues the unlock on success, all internally. Your only responsibilities are:

1. **Configure the prompt text** once via `BrivoSDKLocalAuthentication.init(...)` (requires the `USE_BIOMETRIC` permission).
2. **Call `unlockAccessPoint(...)`** as you normally would — strongly recommanded to pass an `fragmentActivity` unless called from a widget.
```kotlin
// 1. Configure the prompt once (e.g. at startup). This only sets the
//    title/message/button text the SDK will display.
BrivoSDKLocalAuthentication.init(
    context = applicationContext,
    title = "Confirm it's you",
    message = "Authenticate to unlock the door",
    negativeButtonText = "Cancel",
    description = null,
)

// 2. Just unlock. If the access point is two-factor, the SDK shows the
//    biometric prompt automatically before completing the unlock.
BrivoSDKAccess.unlockAccessPoint(
    passId = passId,
    accessPointId = accessPointId,
    activity = this@MyActivity,   // optional — see below
).collect { result -> /* … */ }
```

**The `activity` parameter controls *where* the prompt is hosted, not *whether* it appears:**

- **Pass an `FragmentActivity`** — the SDK hosts the biometric prompt inside your activity (recommended when you unlock from a foreground screen).
- **Pass `null`** (omit it) — the SDK shows the prompt in its own internal activity, so two-factor still works even when you trigger an unlock without a hosting activity. Recomanded only for unlocks originating from widgets.

The unlock flow emits `AccessPointCommunicationState.AUTHENTICATE` while the prompt is showing — this is **informational only** (e.g. to update your UI). You don't need to act on it. `BrivoSelectedAccessPoint.isTwoFactorEnabled` flags 2FA per access point.

### "Magic Button" — unlock the nearest device

The Magic Button scans continuously for nearby BLE readers, ranks them by signal strength.
These results are supposed to be shown in the app so the user can choose which to unlock. Then you can call the normal unlock method for that device.
Two scan entry points exist, one per storage mode. Both return `Flow<ContinuousScanningResults>`, which offers `onScanResults`, `onScanState`, and `onError` helpers.

```kotlin
sealed class ContinuousScanningResults {
    data class ScanResults(val nearbyDevices: List<NearbyDevice>)
    data class ScanState(val continuousScanningState: ContinuousScanningState) // WaitingForBluetooth
    data class Error(val continuousScanningErrors: ContinuousScanningErrors)
    inline fun onScanResults(action: (List<NearbyDevice>) -> Unit): ContinuousScanningResults
    inline fun onScanState(action: (ContinuousScanningState) -> Unit): ContinuousScanningResults
    inline fun onError(action: (ContinuousScanningErrors) -> Unit): ContinuousScanningResults
}

data class NearbyDevice(
    val accessPointId: String,
    val readerUUID: ReaderUUID,
    val rssiValue: Int,
    val doorType: DoorType,
)
```

> [!WARNING]
> Ranking is based on **RSSI (Bluetooth signal strength), not true distance.** Signal strength is affected by phone position, interference, and reader hardware, so the "strongest" device is not guaranteed to be the physically closest. Sort by `rssiValue` descending (strongest first).

#### (a) Magic Button with SDK storage

```kotlin
val scanJob = lifecycleScope.launch {
    BrivoSDKAccess.startScanForNearbyDevicesWithSDKStorage().collect { results ->
        results
            .onScanResults { devices ->
                val nearest = devices.maxByOrNull { it.rssiValue } ?: return@onScanResults
                // Unlock by id (SDK storage supplies the credentials):
                lifecycleScope.launch {
                    BrivoSDKAccess.unlockAccessPoint(
                        passId = /* the pass id for this access point */ "",
                        accessPointId = nearest.accessPointId,
                        activity = this@MyActivity,
                    ).collect { /* handle BrivoResult states */ }
                }
            }
            .onScanState { state ->
                if (state is ContinuousScanningState.WaitingForBluetooth) promptEnableBluetooth()
            }
            .onError { handle(it.error) }
    }
}
// Stop scanning by cancelling the collecting coroutine:
// scanJob.cancel()
```

#### (b) Magic Button with external credentials

```kotlin
val scanJob = lifecycleScope.launch {
    BrivoSDKAccess.startScanForNearbyDevices(passes = myPasses).collect { results ->
        results
            .onScanResults { devices ->
                val nearest = devices.maxByOrNull { it.rssiValue } ?: return@onScanResults
                // Resolve the scanned device into a BrivoSelectedAccessPoint from your passes:
                val made = BrivoSelectedAccessPoint.make(
                    passes = myPasses,
                    accessPointId = nearest.accessPointId,
                )
                val selected = (made as? BrivoSDKApiState.Success)?.data ?: return@onScanResults
                lifecycleScope.launch {
                    BrivoSDKAccess.unlockAccessPoint(
                        brivoSelectedAccessPoint = selected,
                        activity = this@MyActivity,
                    ).collect { /* handle BrivoResult states */ }
                }
            }
            .onScanState { /* WaitingForBluetooth */ }
            .onError { handle(it.error) }
    }
}
```

> [!NOTE]
> For the external-credentials path, resolve a scanned device into a `BrivoSelectedAccessPoint` with `BrivoSelectedAccessPoint.make(passes = myPasses, accessPointId = nearest.accessPointId)`, which returns a `BrivoSDKApiState` (see [Building a selected access point](#building-a-selected-access-point)). A `NearbyDevice.toSelectedAccessPoint()` API does **not** exist, and the older `BrivoSelectedAccessPointMapper.constructSelectedAccessPoint(...)` helper is deprecated.

#### Deprecated: `unlockNearestBLEAccessPoint`

> [!NOTE]
> `unlockNearestBLEAccessPoint(...)` (both overloads) is **deprecated**. Migrate to `startScanForNearbyDevices(...)` / `startScanForNearbyDevicesWithSDKStorage(...)` followed by `unlockAccessPoint(...)`, which is compatible with the continuous-scanning API.

---

## 9. Error reference

One-shot calls return `BrivoSDKApiState.Failed(brivoError: BrivoError)`; unlock/scan flows surface errors via `BrivoResult.error` or `ContinuousScanningErrors.error`. `BrivoError` is `data class BrivoError(val message: String?, val code: Int)` — match on `code` against the tables below.

### `BrivoErrorCodes` (core)

| Code | Name |
| --- | --- |
| -1001 | `SDK_NOT_INITIALIZED` |
| -1002 | `SDK_NOT_CONFIGURED_FOR_LOCAL_STORAGE` |
| -1003 | `SDK_NO_PASSES_FOUND_IN_LOCAL_STORAGE` |
| -1004 | `SDK_ACCESS_POINT_NOT_FOUND_IN_LOCAL_STORAGE` |
| -1005 | `SDK_PASS_NOT_FOUND_IN_LOCAL_STORAGE` |
| -1006 | `SDK_BLE_ACCESS_POINT_NOT_FOUND_IN_GIVEN_PASS` |
| -1007 | `SDK_BLE_ACCESS_POINT_INVALID_DOOR_TYPE` |
| -1008 | `SDK_REQUIRES_INTERNET_CONNECTION` |
| -1009 | `SDK_NOT_ALLOWED_TO_CONTINUE` |
| -1010 | `MODULE_NOT_ENABLED` |

### `BrivoOnairErrorCodes` (passes / cloud)

| Code | Name |
| --- | --- |
| 401 | `ONAIR_AUTHENTICATION_EXCEPTION` |
| 404 | `ONAIR_INVALID_PASS` |
| -3001 | `ONAIR_SERVER_CALL_FAILED` |
| -3002 | `ONAIR_AUTHENTICATION_MISSING_DATA` |
| -3003 | `ONAIR_REDEEM_PASS_MISSING_RESPONSE` |
| -3004 | `ONAIR_REDEEM_PASS_MISSING_PASS` |
| -3005 | `ONAIR_RETRIEVE_SITES_MISSING_SITES` |
| -3006 | `ONAIR_RETRIEVE_SITE_DETAILS_MISSING_SITE` |
| -3007 | `ONAIR_RETRIEVE_SITE_ACCESS_POINTS_MISSING_SITE` |
| -3008 | `ONAIR_ACCESS_POINT_NOT_FOUND` |
| -3009 | `ONAIR_AUTHENTICATION_UNABLE_TO_REFRESH_TOKEN` |
| -3010 | `ONAIR_TRUSTED_NETWORK_LOCATION_PERMISSION_NOT_GRANTED` |
| -3011 | `ONAIR_RETRIEVE_BLE_SECURITY_TOKENS_FAILED` |

### `BrivoBLEErrorCodes` (Bluetooth)

| Code | Name |
| --- | --- |
| -2000 | `BLE_UNKNOWN_ERROR` |
| -2001 | `BLE_DISABLED_ON_DEVICE` |
| -2002 | `BLE_CONNECTION_MANAGER_FAILED_TO_INITIALIZE` |
| -2003 | `BLE_FAILED_TRANSMISSION` |
| -2004 | `BLE_ACCESS_DENIED` |
| -2005 | `BLE_AUTHENTICATION_TIMED_OUT` |
| -2006 | `BLE_LOCATION_PERMISSION_NOT_GRANTED` |
| -2007 | `BLE_LOCATION_DISABLED_ON_DEVICE` |
| -2008 | `BLE_CONNECTION_MISSING_BRIVO_BLE_CREDENTIAL` |
| -2009 | `BLE_CONNECTION_MISSING_USER_ID` |
| -2010 | `BLE_CONNECTION_MISSING_ACCESS_POINT` |
| -2011 | `BLE_CONNECTION_MISSING_BLE_CREDENTIALS` |
| -2012 | `BLE_DEVICE_DISCONNECTED` |
| -2013 | `BLE_BLUETOOTH_PERMISSION_NOT_GRANTED` |
| -2014 | `BLE_NO_MATCHING_DEVICE_FOUND` |

### `BrivoAccessErrorCodes` (unlock / continuous scan)

| Code | Name |
| --- | --- |
| -3001 | `ACCESS_SCAN_TIMEOUT` |
| -3002 | `ACCESS_CONTINUOUS_SCAN_NO_SCAN_TO_START` |
| -3003 | `ACCESS_CONTINUOUS_SCAN_ALREADY_STARTED` |
| -3004 | `ACCESS_CONTINUOUS_SCAN_TOO_MANY_SCANS` |

### `BrivoLocalAuthenticationErrorCodes` (biometric / two-factor)

| Code | Name |
| --- | --- |
| -4000 | `SDK_LOCAL_AUTHENTICATION_FAILURE` |
| -4001 | `SDK_LOCAL_HARDWARE_UNAVAILABLE` |
| -4002 | `SDK_LOCAL_TIMEOUT` |
| -4003 | `SDK_LOCAL_HW_NOT_PRESENT` |
| -4004 | `SDK_LOCAL_NONE_ENROLLED` |
| -4005 | `SDK_LOCAL_AUTHENTICATION_CONTEXT_NOT_SET` |
| -4006 | `SDK_LOCAL_AUTHENTICATION_CANCEL` |
| -4007 | `SDK_LOCAL_INTENT_NULL` |
| -4008 | `SDK_LOCAL_AUTHENTICATION_SYSTEM_CANCEL` |

> [!NOTE]
> `BrivoLocalAuthenticationErrorCodes` and the HID Origo codes in `BrivoOnairErrorCodes` share some numeric values (e.g. -4000) but live in **different objects** — always match against the object relevant to the operation you called.

---

## 10. Keeping up to date & good practices

- **Pin a release tag.** Always depend on an explicit JitPack tag (e.g. `3.5.0`), never a moving branch, so builds are reproducible. Confirm the latest tag with Brivo.
- **Initialize once, early.** Call `BrivoSDK.init(...)` from your `Application.onCreate()` with the application context, and handle `BrivoSDKInitializationException`.
- **You own permissions.** The SDK never prompts. Declare manifest permissions and request runtime permissions (`BLUETOOTH_SCAN`/`BLUETOOTH_CONNECT` on API 31+, location pre-31, and location for trusted networks) before any BLE operation, and gate scans on Bluetooth being enabled.
- **Treat RSSI as a hint, not a measurement.** When using the Magic Button, remember ranking is signal-strength based; don't present it to users as exact distance.
- **Cancel via coroutines.** Stop an in-flight unlock or scan by cancelling the collecting coroutine `Job`. There is no cancellation-signal parameter.
- **Avoid deprecated APIs.** Migrate off `unlockNearestBLEAccessPoint(...)` and the `shouldContinueUnlockOperation` overloads, and don't handle `SHOULD_CONTINUE` / `SCANNING_COOLDOWN` states.
- **Handle every error path.** Match on `BrivoSDKApiState.Failed` / `BrivoResult.error` and use the [error reference](#9-error-reference); surface actionable states (e.g. `WaitingForBluetooth`, permission-not-granted) to the user.
- **Check `BrivoSDK.version`** at runtime if you need to confirm which SDK build is deployed (remember the `v` prefix).

---

## Issues

If you run into any bugs or issues, feel free to post
an [Issue](https://github.com/brivo-mobile-team/brivo-mobile-sdk-android/issues) to discuss.

<p align="center">
Made with ❤️ at Brivo
</p>

License
=======

    Copyright 2026 Brivo Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
