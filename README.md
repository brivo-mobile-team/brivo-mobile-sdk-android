[![](https://jitpack.io/v/org.bitbucket.brivoinc/mobile-sdk-android.svg)](https://jitpack.io/#org.bitbucket.brivoinc/mobile-sdk-android)

# Brivo Mobile SDK Android

A set of reusable libraries, services and components for Java and Kotlin Android apps.

### Installation

Brivo Mobile SDK requires at minimun Android API 29+.

#### Jitpack ❤️

<b>Step 1</b>. Add the JitPack repository to your build file

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

<b>Step 2</b>. Add the dependency for all Brivo modules

```gradle
dependencies {
    implementation 'org.bitbucket.brivoinc:mobile-sdk-android:Tag'
}
```

If you want specific modules then add any of the following dependencies

```gradle
dependencies {
     if (checkGithubAccessToken(gitHubGradleAccessToken)) {
        implementation("org.bitbucket.brivoinc.mobile-sdk-android:brivoble-allegion:Tag") //This is an optional dependency module for use with Allegion BLE credentials which requires integrating their SDK
    }
    implementation 'org.bitbucket.brivoinc.mobile-sdk-android:brivoaccess:Tag'
    implementation 'org.bitbucket.brivoinc.mobile-sdk-android:brivoble:Tag'
    implementation 'org.bitbucket.brivoinc.mobile-sdk-android:brivoblecore:Tag'
    implementation 'org.bitbucket.brivoinc.mobile-sdk-android:brivoconfiguration:Tag'
    implementation 'org.bitbucket.brivoinc.mobile-sdk-android:brivocore:Tag'
    implementation 'org.bitbucket.brivoinc.mobile-sdk-android:brivolocalauthentication:Tag'
    implementation 'org.bitbucket.brivoinc.mobile-sdk-android:brivoonair:Tag'
    implementation 'org.bitbucket.brivoinc.mobile-sdk-android:brivosmarthome:Tag'

    // Allegion SDK Module
    implementation("com.allegion:MobileAccessSDK:latest.release") //This is the Allegion SDK Dependency required if you're using BrivoBLE-Allegion. In order to fetch it, you're required to receive a github access token from allegion and add it to your gradle.properties file
}
```

```gradle.properties option for BrivoBLe-Allegion use
    gitHubGradleAccessToken = Github_Access_Key //Provided by Allegion
```

<b>Step 3</b> Add the following permissions to your app's manifest

```xml

<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" /><uses-permission
android:name="android.permission.INTERNET" /><uses-permission
android:name="android.permission.VIBRATE" /><uses-permission
android:name="android.permission.RECEIVE_BOOT_COMPLETED" /><uses-permission
android:name="android.permission.ACCESS_COARSE_LOCATION" /><uses-permission
android:name="android.permission.ACCESS_FINE_LOCATION" /><uses-permission
android:name="android.permission.ACCESS_WIFI_STATE" /><uses-permission
android:name="android.permission.BLUETOOTH_ADMIN" /><uses-permission
android:name="android.permission.BLUETOOTH" /><uses-permission
android:name="android.permission.CHANGE_WIFI_STATE" /><uses-permission
android:name="android.permission.FOREGROUND_SERVICE" /><uses-permission
android:name="android.permission.ACCESS_BACKGROUND_LOCATION" /><uses-permission
android:name="android.permission.ACCESS_BACKGROUND_LOCATION" /><uses-permission
android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" /><uses-permission
android:name="android.permission.BLUETOOTH_SCAN" tools:targetApi="s" /><uses-permission
android:name="android.permission.BLUETOOTH_CONNECT" />
```

### Usage

To initialize the Brivo Mobile SDK call the initialize method with the application context and with
the BrivoConfiguration object.
The BrivoConfiguration object requires the clientId and secretId which is provided by Brivo.
These need to be specified in accordance with the selected `useEURegion` boolean passed to the
configuration object.

```kotlin
/**
 * Initializes the BrivoSDK
 *
 * @param context            application context used to initialize SDK data into the application local storage
 * @param brivoConfiguration Brivo client id
 *                           Brivo client secret
 *                           Brivo SDK local storage management enabled
 *                           Brivo SDK should verify door before connecting to it
 */
void init (Context context, BrivoConfiguration brivoConfiguration) throws BrivoSDKInitializationException;
```

### BrivoSDK init usage

Example of a function initialising the SDK

```kotlin
fun initBrivoSDK(isEURegion: Boolean) {
    try {
        BrivoSDK.init(
            context = yourApplicationContext,
            brivoConfiguration = BrivoConfiguration(
                clientId = if (isEURegion) EU_CLIENT_ID else US_CLIENT_ID,
                clientSecret = if (isEURegion) EU_CLIENT_SECRET else US_CLIENT_SECRET,
                useEURegion = isEURegion,
                useSDKStorage = true,
                shouldVerifyDoor = false
            )
        )
    } catch (e: BrivoSDKInitializationException) {
        // Handle exceptions
    }
}
```

The exception is thrown if the SDK is not initialized correctly.
For example one of the parameters is null or missing.

### Brivo Sample app usage

For sample app usage, please add your client Id and client Secret inside the gradle.properties file,
in their appropriate variable.
Otherwise the sample app will throw an `IllegalArgumentException` with the message "Please add your
client Id and client Secret inside gradle.properties file" and it will then close.

### Brivo Mobile SDK Modules

#### BrivoCore

Initializes the BrivoSDK (see example above).
This module also contains the version variable which returns the SDK version.

```kotlin
val version: String
```

```kotlin
BrivoSDK.version
```

#### BrivoOnAir

This module manages the connection between the SDK and the Brivo environment.
Redeem a Brivo Onair Pass. Brivo Onair Pass allows you to open doors with your smartphone.

##### BrivoSDKOnair Redeem Pass

```kotlin
/**
 * Redeem a Brivo Onair Pass. Brivo Onair Pass allows you to open doors with your smartphone.
 *
 * @param email    Email received from Brivo
 * @param token    Token received from Brivo
 */
suspend fun redeemPass(passId: String, passCode: String): BrivoSDKApiState<BrivoOnairPass?>
```

```kotlin
try {
    val response = BrivoSDKOnair.redeemPass(email, token)
    when (response) {
        is BrivoSDKApiState.Success -> {
            // Handle success
        }
        is BrivoSDKApiState.Failed -> {
            // Handle failure
        }
    }
} catch (e: BrivoSDKInitializationException) {
    // Handle BrivoSDK initialization exception
}
```

##### BrivoSDKOnair Refresh a Brivo Onair Pass

```kotlin
/**
 * Refresh a Brivo Onair Pass. Brivo Onair Pass allows you to open doors with your smartphone.
 *
 * @param brivoTokens accessToken received from Brivo
 *                    refreshToken received from Brivo
 */
suspend fun refreshPass(brivoTokens: BrivoTokens): BrivoSDKApiState<BrivoOnairPass?>
```

```kotlin
try {
    val response = BrivoSDKOnair.refreshPass(tokens)
    when (response) {
        is BrivoSDKApiState.Success -> {
            // Handle success
        }
        is BrivoSDKApiState.Failed -> {
            // Handle failure
        }
    }
} catch (e: BrivoSDKInitializationException) {
    e.printStackTrace()
}
```

##### Retrieve SDK locally stored passes

```kotlin
/**
 * Retrieved SDK locally stored passes
 */
fun retrieveSDKLocallyStoredPasses(): BrivoSDKApiState<LinkedHashMap<String, BrivoOnairPass>>
```

```kotlin
try {
    val response = BrivoSDKOnair.retrieveSDKLocallyStoredPasses()
    when (response) {
        is BrivoSDKApiState.Success -> {
            // Handle success
        }
        is BrivoSDKApiState.Failed -> {
            // Handle failure
        }
    }
} catch (e: BrivoSDKInitializationException) {
    e.printStackTrace()
}
```

#### BrivoAccess

This module handles unlocking of access points internally.
It determines what authentication type is required (BLE or NETWORK).

##### BrivoSDKAccess Unlock Access Point

```kotlin
    /**
 * Sends a request to unlock an access point to BrivoSDK
 *
 * @param accessPointId      Brivo accessPointId
 * @param passId             Brivo passId
 * @param shouldContinueUnlockOperation Optional event to prompt the user to perform an action
 * before continuing with the unlock operation
 * @return Flow<BrivoResult> Return a flow that emits the status of the unlock operation
 */
fun unlockAccessPoint(
    passId: String,
    accessPointId: String,
    activity: FragmentActivity? = null,
    shouldContinueUnlockOperation: ShouldContinueUnlockOperation? = null
): Flow<BrivoResult>
```

```kotlin
try {
    BrivoSDKAccess.unlockAccessPoint(
        passId,
        accessPointId,
        cancellationSignal
    ).collect {
        when (it.communicationState) {
            AccessPointCommunicationState.SUCCESS -> {
                // Handle success
            }
            AccessPointCommunicationState.FAILED -> {
                // Handle failure
            }
            AccessPointCommunicationState.SHOULD_CONTINUE -> {
                // Handle custom action and afterwards perform shouldContinue
                // If shouldContinueUnlockOperation is null, this can be ignored
                // it.shouldContinueListener?.onShouldContinue(true)
            }
            AccessPointCommunicationState.SCANNING -> //scanning
                AccessPointCommunicationState.AUTHENTICATE
            -> //authenticating
                AccessPointCommunicationState.CONNECTING
            -> //connecting
                AccessPointCommunicationState.COMMUNICATING
            -> //communicating
        }
    }
} catch (e: BrivoSDKInitializationException) {
    // Handle BrivoSDK initialization exception
}
```

##### BrivoSDKAccess Unlock Access Point with External Credentials

This method is called when credentials and data are managed outside of BrivoSDK.

```kotlin
    /**
 * Sends a request to unlock an access point to BrivoSDK
 * All credentials and data are managed outside of BrivoSDK
 *
 * @param brivoSelectedAccessPoint Brivo accessPointPath (accessPointId, siteId, passId)
 *                                 Brivo bleReaderId
 *                                 Brivo bleCredentials
 *                                 Brivo deviceModelId
 *                                 Brivo doorType
 *                                 Brivo minimumAllowedRssi
 *                                 Unlock attempt time frame
 *                                 BrivoOnairPassCredentials (userId, accessToken, refreshToken)
 * @param activity Optional Activity to create the 2FA prompt inside the specifie activity
 * instead of the SDK
 * @param shouldContinueUnlockOperation Optional event to prompt the user to perform an action
 * before continuing with the unlock operation
 * @return Flow<BrivoResult> Returns a flow that emits the status of the unlock operation
 */
fun unlockAccessPoint(
    brivoSelectedAccessPoint: BrivoSelectedAccessPoint,
    activity: FragmentActivity? = null,
    shouldContinueUnlockOperation: ShouldContinueUnlockOperation? = null
): Flow<BrivoResult>
```

```kotlin
try {
    BrivoSDKAccess.getInstance().unlockAccessPoint(
        selectedAccessPoint,
        cancellationSignal
    ).collect {
        when (it.communicationState) {
            AccessPointCommunicationState.SUCCESS -> {
                // Handle success
            }
            AccessPointCommunicationState.FAILED -> {
                // Handle failure
            }
            AccessPointCommunicationState.SHOULD_CONTINUE -> {
                // Handle custom action and afterwards perform shouldContinue
                // If shouldContinueUnlockOperation is null, this can be ignored
                // it.shouldContinueListener?.onShouldContinue(true)
            }
            AccessPointCommunicationState.SCANNING -> //scanning
                AccessPointCommunicationState.AUTHENTICATE
            -> //authenticating
                AccessPointCommunicationState.CONNECTING
            -> //connecting
                AccessPointCommunicationState.COMMUNICATING
            -> //communicating
        }
    }
} catch (e: BrivoSDKInitializationException) {
    // Handle BrivoSDK initialization exception
}
```

##### BrivoSDKAccess Unlock Nearest BLE Access Point using External Credentials

```kotlin
    /**
 * Sends a request to unlock the nearest BLE access point to BrivoSDK
 * All credentials and data are managed outside of BrivoSDK
 *
 * @param passes                   Passes needed to unlock nearest bluetooth that is in range
 * @param shouldContinueUnlockOperation Optional event to prompt the user to perform an action before continuing with the unlock operation
 * @return Flow<BrivoResult> Returns a flow that emits the status of the unlock operation
 */
fun unlockNearestBLEAccessPoint(
    passes: List<BrivoOnairPass>,
    activity: FragmentActivity? = null,
    shouldContinueUnlockOperation: ShouldContinueUnlockOperation? = null
): Flow<BrivoResult>
```

```kotlin
try {
    BrivoSDKAccess.getInstance().unlockNearestBLEAccessPoint(
        passes,
        cancellationSignal
    ).collect {
        when (it.communicationState) {
            AccessPointCommunicationState.SUCCESS -> {
                // Handle success
            }
            AccessPointCommunicationState.FAILED -> {
                // Handle failure
            }
            AccessPointCommunicationState.SHOULD_CONTINUE -> {
                // Handle custom action and afterwards perform shouldContinue
                // If shouldContinueUnlockOperation is null, this can be ignored
                // it.shouldContinueListener?.onShouldContinue(true)
            }
            AccessPointCommunicationState.SCANNING -> //scanning
                AccessPointCommunicationState.AUTHENTICATE
            -> //authenticating
                AccessPointCommunicationState.CONNECTING
            -> //connecting
                AccessPointCommunicationState.COMMUNICATING
            -> //communicating
        }
    }
} catch (e: BrivoSDKInitializationException) {
    // Handle BrivoSDK initialization exception
}
```

##### BrivoSDKAccess unlock nearest BLE access point

```kotlin
    /**
 * Sends a request to unlock the nearest BLE access point to BrivoSDK
 * All credentials and data are managed by BrivoSDK
 * @param activity Optional Activity to create the 2FA prompt inside the specifie activity
 * instead of the SDK
 * @param shouldContinueUnlockOperation Optional event to prompt the user to perform an action
 * before continuing with the unlock operation
 * @return Flow<BrivoResult> Returns a flow that emits the status of the unlock operation
 **/
void unlockNearestBLEAccessPoint (CancellationSignal cancellationSignal, IOnCommunicateWithAccessPointListenerListener listener)

```

```kotlin
try {
    BrivoSDKAccess.getInstance().unlockNearestBLEAccessPoint(
        cancellationSignal,
        cancellationSignal
    ).collect {
        when (it.communicationState) {
            AccessPointCommunicationState.SUCCESS -> {
                // Handle success
            }
            AccessPointCommunicationState.FAILED -> {
                // Handle failure
            }
            AccessPointCommunicationState.SHOULD_CONTINUE -> {
                // Handle custom action and afterwards perform shouldContinue
                // If shouldContinueUnlockOperation is null, this can be ignored
                // it.shouldContinueListener?.onShouldContinue(true)
            }
            AccessPointCommunicationState.SCANNING -> //scanning
                AccessPointCommunicationState.AUTHENTICATE
            -> //authenticating
                AccessPointCommunicationState.CONNECTING
            -> //connecting
                AccessPointCommunicationState.COMMUNICATING
            -> //communicating
        }
    } catch (e: BrivoSDKInitializationException) {
        // Handle BrivoSDK initialization exception
    }
```

#### BrivoBLE

This module manages the connection between an access point and a panel through bluetooth

#### BrivoSDKBleAllegion

This module handles unlocking of allegion doors offline.

```kotlin

/**
 * Initializes the BrivoSDKBLEAllegion module
 *
 * @throws BrivoSDKInitializationException if initialization fails
 */
@Throws(BrivoSDKInitializationException::class)
fun init()

try {
    BrivoSDKBLEAllegion.init()
} catch (exception: BrivoSDKInitializationException) {
    exception.printStackTrace()
}
/**
 * Refreshes the credentials for Allegion doors
 *
 * @param pass the BrivoOnairPass object
 * @return BrivoSDKApiState indicating success or failure
 */
fun refreshCredentials(pass: BrivoOnairPass): BrivoSDKApiState

when (val result = BrivoSDKBLEAllegion.getInstance().refreshCredentials(pass)) {
    is BrivoSDKApiState.Failed -> {
        BrivoApiState.Failed(result.brivoError)
    }

    is BrivoSDKApiState.Success -> {
        BrivoApiState.Success(Unit)
    }
}
```

#### BrivoConfiguration

This is the module used to configure Allegion Control Locks for the No Tour feature

##### BrivoSDKConfigure Configure Access Point with Internal Credentials

```kotlin
/**
 * Sends a request to configure an access point to BrivoSDK
 *
 * @param passId             Brivo passId
 * @param accessPointId      Brivo accessPointId
 * @param cancellationSignal Cancellation signal in order to cancel a BLE communication process
 *                           if a null cancellation signal is provided there will be default 30 second timeout
 * @param listener           listener that handles the communication state (SCANNING, AUTHENTICATE, SHOULD_CONTINUE, CONNECTING, COMMUNICATING, SUCCESS, FAILED)
 */
fun configureAccessPoint(
    passId: String,
    accessPointId: String,
    cancellationSignal: CancellationSignal?,
    listener: IOnCommunicateWithAccessPointListener
)
```

```kotlin
BrivoSDKConfigure.getInstance().configureAccessPoint(
    passId,
    accessPointId,
    cancellationSignal,
    object : IOnCommunicateWithAccessPointListener {
        object : IOnCommunicateWithAccessPointListener {
            override fun onResult(result: BrivoResult) {
                when (result.communicationState) {
                    AccessPointCommunicationState.SUCCESS -> {
                        // Handle success
                    }
                    AccessPointCommunicationState.FAILED -> {
                        // Handle failure
                    }
                }
            }
        }
        )
```

##### BrivoSDKConfigure Configure Access Point with External Credentials

```kotlin
/**
 * Sends a request to configure an access point to BrivoSDK
 * All credentials and data are managed outside of BrivoSDK
 *
 * @param brivoSelectedAccessPoint Brivo accessPointPath (accessPointId, siteId, passId)
 *                                 Brivo bleReaderId
 *                                 Brivo bleCredentials
 *                                 Brivo deviceModelId
 *                                 Brivo doorType
 *                                 Brivo minimumAllowedRssi
 *                                 Unlock attempt time frame
 *                                 BrivoOnairPassCredentials (userId, accessToken, refreshToken)
 *
 * @param cancellationSignal Cancellation signal in order to cancel a BLE communication process
 *                           if a null cancellation signal is provided there will be default 30 second timeout
 * @param listener           listener that handles the communication state (SCANNING, AUTHENTICATE, SHOULD_CONTINUE, CONNECTING, COMMUNICATING, SUCCESS, FAILED)
 */
fun configureAccessPoint(
    brivoSelectedAccessPoint: BrivoSelectedAccessPoint,
    cancellationSignal: CancellationSignal?,
    listener: IOnCommunicateWithAccessPointListener
)
```

```kotlin
BrivoSDKConfigure.getInstance().configureAccessPoint(
    selectedAccessPoint,
    cancellationSignal,
    object : IOnCommunicateWithAccessPointListener {
        override fun onResult(result: BrivoResult) {
            when (result.communicationState) {
                AccessPointCommunicationState.SUCCESS -> {
                    // Handle success
                }
                AccessPointCommunicationState.FAILED -> {
                    // Handle failure
                }
            }
        }
        )
```

#### BrivoLocalAuthentication

This module manages the biometric authentication if an access point requires two factor.
This module also can return the type of biometric authentication (device credentials, fingerprint or
face id).

##### BrivoSDKLocalAuthentication Init

This method is called in order to initalize the BrivoLocalAuthenticationModule

```kotlin
    /**
 * Initializes the BrivoLocalAuthentication module
 *
 * @param context              the context of the app in which the biometric screen will start from
 * @param title                the title of the biometric prompt
 * @param message              the message of the biometric prompt
 * @param negativeButtonText   the text of the negative button of the biometric prompt
 * @param description          the text of the description of the biometric prompt
 */
fun init(
    context: Context?,
    title: String?,
    message: String?,
    negativeButtonText: String?,
    description: String?
)
```

```kotlin
try {
    BrivoSDKLocalAuthentication.init(
        activity,
        title,
        message,
        negativeButtonText,
        description
    )
} catch (exception: BrivoSDKInitializationException) {
    exception.printStackTrace()
}
```

##### BrivoSDKLocalAuthentication Can Authenticate

This method returns if authentication is possible, if it is will return the type of the biometric
authentication (device credentials, fingerprint or face id).

```kotlin
/**
 * Return if authentication can be performed and, if its the case, the type of the authentication
 *
 * @param listener             listener that handles if the possibility of authentication is success or failure
 */
suspend fun canAuthenticate(): BrivoSDKApiState<BiometricResult>
```

```kotlin
try {
    BrivoSDKLocalAuthentication.canAuthenticate().collect {
        when (it) {
            is BrivoSDKApiState.Success -> {
                //Check BiometricResult type to see the type of authentication available
            }
            is BrivoSDKApiState.Failed -> {
                // Handle failure
            }
        }
    }
} catch (exception: BrivoSDKInitializationException) {
    e.printStackTrace()
}
```

### BrivoSDKLocalAuthentication Cancel Authentication

```kotlin
/**
 * Cancel ongoing authentication
 */
fun cancelAuthentication()
```

```kotlin
BrivoSDKLocalAuthentication.cancelAuthentication()
```

BrivoSDK Errors
=======

## General SDK Errors

```kotlin
object BrivoErrorCodes {
    const val SDK_NOT_INITIALIZED: Int = -1001
    const val SDK_NOT_CONFIGURED_FOR_LOCAL_STORAGE: Int = -1002
    const val SDK_NO_PASSES_FOUND_IN_LOCAL_STORAGE: Int = -1003
    const val SDK_ACCESS_POINT_NOT_FOUND_IN_LOCAL_STORAGE: Int = -1004
    const val SDK_PASS_NOT_FOUND_IN_LOCAL_STORAGE: Int = -1005
    const val SDK_BLE_ACCESS_POINT_NOT_FOUND_IN_GIVEN_PASS: Int = -1006
    const val SDK_BLE_ACCESS_POINT_INVALID_DOOR_TYPE: Int = -1007
    const val SDK_REQUIRES_INTERNET_CONNECTION: Int = -1008
    const val SDK_NOT_ALLOWED_TO_CONTINUE: Int = -1009
}

```

## Brivo Onair Errors

```kotlin
object BrivoOnairErrorCodes {
    const val ONAIR_AUTHENTICATION_EXCEPTION: Int = 401
    const val ONAIR_INVALID_PASS: Int = 404
    const val ONAIR_SERVER_CALL_FAILED: Int = -3001
    const val ONAIR_AUTHENTICATION_MISSING_DATA: Int = -3002
    const val ONAIR_REDEEM_PASS_MISSING_RESPONSE: Int = -3003
    const val ONAIR_REDEEM_PASS_MISSING_PASS: Int = -3004
    const val ONAIR_RETRIEVE_SITES_MISSING_SITES: Int = -3005
    const val ONAIR_RETRIEVE_SITE_DETAILS_MISSING_SITE: Int = -3006
    const val ONAIR_RETRIEVE_SITE_ACCESS_POINTS_MISSING_SITE: Int = -3007
    const val ONAIR_ACCESS_POINT_NOT_FOUND: Int = -3008
    const val ONAIR_AUTHENTICATION_UNABLE_TO_REFRESH_TOKEN: Int = -3009
    const val ONAIR_TRUSTED_NETWORK_LOCATION_PERMISSION_NOT_GRANTED: Int = -3010
    const val ONAIR_RETRIEVE_BLE_SECURITY_TOKENS_FAILED: Int = -3011
}
```

## Brivo Ble Errors

```kotlin
object BrivoBLEErrorCodes {
    const val BLE_UNKNOWN_ERROR: Int = -2000
    const val BLE_DISABLED_ON_DEVICE: Int = -2001
    const val BLE_CONNECTION_MANAGER_FAILED_TO_INITIALIZE: Int = -2002
    const val BLE_FAILED_TRANSMISSION: Int = -2003
    const val BLE_ACCESS_DENIED: Int = -2004
    const val BLE_AUTHENTICATION_TIMED_OUT: Int = -2005
    const val BLE_LOCATION_PERMISSION_NOT_GRANTED: Int = -2006
    const val BLE_LOCATION_DISABLED_ON_DEVICE: Int = -2007
    const val BLE_CONNECTION_MISSING_BRIVO_BLE_CREDENTIAL: Int = -2008
    const val BLE_CONNECTION_MISSING_USER_ID: Int = -2009
    const val BLE_CONNECTION_MISSING_ACCESS_POINT: Int = -2010
    const val BLE_CONNECTION_MISSING_BLE_CREDENTIALS: Int = -2011
    const val BLE_DEVICE_DISCONNECTED: Int = -2012
    const val BLE_BLUETOOTH_PERMISSION_NOT_GRANTED: Int = -2013
}
```

## Brivo Authentication Errors

```kotlin
object BrivoLocalAuthenticationErrorCodes {
    const val SDK_LOCAL_AUTHENTICATION_FAILURE: Int = -4000
    const val SDK_LOCAL_HARDWARE_UNAVAILABLE: Int = -4001
    const val SDK_LOCAL_TIMEOUT: Int = -4002
    const val SDK_LOCAL_HW_NOT_PRESENT: Int = -4003
    const val SDK_LOCAL_NONE_ENROLLED: Int = -4004
    const val SDK_LOCAL_AUTHENTICATION_CONTEXT_NOT_SET: Int = -4005
    const val SDK_LOCAL_AUTHENTICATION_CANCEL: Int = -4006
    const val SDK_LOCAL_INTENT_NULL: Int = -4007
    const val SDK_LOCAL_AUTHENTICATION_SYSTEM_CANCEL: Int = -4008
}
```

## Issues

If you run into any bugs or issues, feel free to post
an [Issues](https://github.com/brivo-mobile-team/brivo-mobile-sdk-android/issues) to discuss.

<p align="center">
Made with ❤️ at Brivo
</p>

License
=======

    Copyright 2024 Brivo Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
