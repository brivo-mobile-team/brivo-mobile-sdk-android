[![Release](https://jitpack.io/v/org.bitbucket.brivoinc/mobile-sdk-android.svg)](https://jitpack.io/#org.bitbucket.brivoinc/mobile-sdk-android)

# Brivo Mobile SDK Android

A set of reusable libraries, services and components for Java and Kotlin Android apps.
### Installation

Minimum Android version required is 23 in order to use the Brivo Mobile SDK.

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
    implementation 'org.bitbucket.brivoinc.mobile-sdk-android:brivoaccess:Tag'
    implementation 'org.bitbucket.brivoinc.mobile-sdk-android:brivoble:Tag'
    implementation 'org.bitbucket.brivoinc.mobile-sdk-android:brivoblecore:Tag'
    implementation 'org.bitbucket.brivoinc.mobile-sdk-android:brivoconfiguration:Tag'
    implementation 'org.bitbucket.brivoinc.mobile-sdk-android:brivocore:Tag'
    implementation 'org.bitbucket.brivoinc.mobile-sdk-android:brivolocalauthentication:Tag'
    implementation 'org.bitbucket.brivoinc.mobile-sdk-android:brivoonair:Tag'
    implementation 'org.bitbucket.brivoinc.mobile-sdk-android:brivosmarthome:Tag'
}
```

<b>Step 3</b> Add the following permissions to your app's manifest

```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
<uses-permission
    android:name="android.permission.BLUETOOTH"
    android:maxSdkVersion="30" />
<uses-permission
    android:name="android.permission.BLUETOOTH_SCAN"
    tools:targetApi="s" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
```

### Usage

To initialize the Brivo Mobile SDK call the initialize method with the application context and with the BrivoConfiguration object.
The BrivoConfiguration object requires the clientId and secretId which is provided by Brivo. Valid Brivo URL’s also need to be specified in the Brivo configuration object


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
void init(Context context, BrivoConfiguration brivoConfiguration) throws BrivoSDKInitializationException;
```
#### BrivoSDK init usage 

```kotlin
try {
    BrivoSDK.getInstance().init(
        applicationContext, BrivoConfiguration(
            CLIENT_ID,
            CLIENT_SECRET,
            useSDKStorage,
            shouldVerifyDoor
        )
    )
} catch (e: BrivoSDKInitializationException) {
    // Handle BrivoSDK initialization exception
}
```

The exception is thrown if the SDK is not initialized correctly.
For example one of the parameters is null or missing.

### Brivo Mobile SDK Modules

#### BrivoCore
Initializes the BrivoSDK (see example above).
This module also contains the getVersion() method which retuns the SDK version.

```kotlin
String getVersion()
```

```kotlin
BrivoSDK.getInstance().version
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
* @param listener listener that handles the success or failure
*/
void redeemPass(String email, String token, IOnRedeemPassListener listener);
```

```kotlin
try {
    BrivoSDKOnair.getInstance().redeemPass(email, token, object : IOnRedeemPassListener {
        override fun onSuccess(pass: BrivoOnairPass) {
            // Manage pass
        }
    
        override fun onFailed(error: BrivoError) {
            // Handle error
        }
    })
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
void refreshPass(BrivoTokens brivoTokens, IOnRedeemPassListener listener);
```

```kotlin
try {
    BrivoSDKOnair.getInstance().refreshPass(tokens, object : IOnRedeemPassListener {
        override fun onSuccess(pass: BrivoOnairPass) {
            // Manage pass
        }

        override fun onFailed(error: BrivoError) {
            // Handle error
        }
    })
} catch (e: BrivoSDKInitializationException) {
    e.printStackTrace()
}
```

##### Retrieve SDK locally stored passes

```kotlin
/**
* Retrieved SDK locally stored passes
*
* @param listener      listener that handles success or failure
*/
void retrieveSDKLocallyStoredPasses(IOnRetrieveSDKLocallyStoredPassesListener listener)
```

```kotlin
try {
    BrivoSDKOnair.getInstance().retrieveSDKLocallyStoredPasses(object : IOnRetrieveSDKLocallyStoredPassesListener {
        override fun onSuccess(passes: LinkedHashMap<String, BrivoOnairPass>?) {
            // Manage passes
        }

        override fun onFailed(error: BrivoError) {
            // Handle error
        }
    })
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
* The request will be granted if the card holder has permission to access this door based on their groups affiliation.
* Available only to digital credential users
* This method should be used when handing the credentials outside of the SDK
*
* @param passId             Brivo passId
* @param accessPointId      Brivo accessPointId
* @param cancellationSignal Cancellation signal in order to cancel a BLE communication process
*                           if a null cancellation signal is provided there will be default 30 second timeout
* @param listener           listener that handles the communication state (SCANNING, AUTHENTICATE, SHOULD_CONTINUE, CONNECTING, COMMUNICATING, SUCCESS, FAILED)
*/
void unlockAccessPoint(String passId, String accessPointId, CancellationSignal cancellationSignal, IOnCommunicateWithAccessPointListenerListener listener);
```

```kotlin
try {
    BrivoSDKAccess.getInstance().unlockAccessPoint(
        passId,
        accessPointId,
        cancellationSignal,
        object : IOnCommunicateWithAccessPointListenerListener {
            override fun onSuccess() {
                // Handle unlock access point success case
            }

            override fun onFailed(error: BrivoError) {
                // Handle unlock access point error case
            }
        }
    )
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
* @param cancellationSignal Cancellation signal in order to cancel a BLE communication process
*                           if a null cancellation signal is provided there will be default 30 second timeout
* @param listener           listener that handles the communication state (SCANNING, AUTHENTICATE, SHOULD_CONTINUE, CONNECTING, COMMUNICATING, SUCCESS, FAILED)
*/
void unlockAccessPoint(BrivoSelectedAccessPoint brivoSelectedAccessPoint, CancellationSignal cancellationSignal, IOnCommunicateWithAccessPointListenerListener listener);
```

```kotlin
try {
    BrivoSDKAccess.getInstance().unlockAccessPoint(
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
                    AccessPointCommunicationState.SHOULD_CONTINUE -> {
                        // Handle custom action and afterwards perform shouldContinue
                        result.shouldContinueListener?.onShouldContinue(true)
                    }
                    AccessPointCommunicationState.SCANNING -> //scanning
                    AccessPointCommunicationState.AUTHENTICATE -> //authenticating
                    AccessPointCommunicationState.CONNECTING -> //connecting
                    AccessPointCommunicationState.COMMUNICATING -> //communicating
                }
            }
        }
    )
} catch (e: BrivoSDKInitializationException) {
    // Handle BrivoSDK initialization exception
}
```

##### BrivoSDKAccess Unlock Nearest BLE Access Point using External Credentials

```kotlin
/**
* Sends a request to unlock the closest access point to BrivoSDK
* All credentials and data are managed outside of BrivoSDK
*
* @param passes               Passes needed to unlock nearest bluetooth that is in range
* @param cancellationSignal   Cancellation signal in order to cancel a BLE communication process
*                             if a null cancellation signal is provided there will be default 30 second timeout
* @param listener             listener that handles the communication state (SCANNING, AUTHENTICATE, SHOULD_CONTINUE, CONNECTING, COMMUNICATING, SUCCESS, FAILED)
*/
void unlockNearestBLEAccessPoint(List<BrivoOnairPass> passes, CancellationSignal cancellationSignal, IOnCommunicateWithAccessPointListenerListener listener)
```

```kotlin
try {
    BrivoSDKAccess.getInstance().unlockNearestBLEAccessPoint(
        passes,
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
                    AccessPointCommunicationState.SHOULD_CONTINUE -> {
                        // Handle custom action and afterwards perform shouldContinue
                        result.shouldContinueListener?.onShouldContinue(true)
                    }
                    AccessPointCommunicationState.SCANNING -> //scanning
                    AccessPointCommunicationState.AUTHENTICATE -> //authenticating
                    AccessPointCommunicationState.CONNECTING -> //connecting
                    AccessPointCommunicationState.COMMUNICATING -> //communicating
                }
            }
        }
    )
} catch (e: BrivoSDKInitializationException) {
    // Handle BrivoSDK initialization exception
}
```

##### BrivoSDKAccess unlock nearest BLE access point

```kotlin
/**
* Sends a request to unlock the closest access point to BrivoSDK
*
* @param cancellationSignal    Cancellation signal in order to cancel a BLE communication process
*                              if a null cancellation signal is provided there will be default 30 second timeout
* @param listener              listener that handles the communication state (SCANNING, AUTHENTICATE, SHOULD_CONTINUE, CONNECTING, COMMUNICATING, SUCCESS, FAILED)
*/
void unlockNearestBLEAccessPoint(CancellationSignal cancellationSignal, IOnCommunicateWithAccessPointListenerListener listener)
```

```kotlin
try {
    BrivoSDKAccess.getInstance().unlockNearestBLEAccessPoint(
        cancellationSignal,
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
                    AccessPointCommunicationState.SHOULD_CONTINUE -> {
                        // Handle custom action and afterwards perform shouldContinue
                        result.shouldContinueListener?.onShouldContinue(true)
                    }
                    AccessPointCommunicationState.SCANNING -> //scanning
                    AccessPointCommunicationState.AUTHENTICATE -> //authenticating
                    AccessPointCommunicationState.CONNECTING -> //connecting
                    AccessPointCommunicationState.COMMUNICATING -> //communicating
                }
            }
        }
    )
} catch (e: BrivoSDKInitializationException) {
    // Handle BrivoSDK initialization exception
}
```

#### BrivoBLE
This module manages the connection between an access point and a panel through bluetooth

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
fun configureAccessPoint(passId: String, accessPointId: String, cancellationSignal: CancellationSignal?, listener: IOnCommunicateWithAccessPointListener)
```

```kotlin
BrivoSDKConfigure.getInstance().configureAccessPoint(
    passId,
    accessPointId,
    cancellationSignal, object : IOnCommunicateWithAccessPointListener {
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
fun configureAccessPoint(brivoSelectedAccessPoint: BrivoSelectedAccessPoint, cancellationSignal: CancellationSignal?, listener: IOnCommunicateWithAccessPointListener)
```

```kotlin
BrivoSDKConfigure.getInstance().configureAccessPoint(
    selectedAccessPoint,
    cancellationSignal, object : IOnCommunicateWithAccessPointListener {
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
This module also can return the type of biometric authentication (device credentials, fingerprint or face id).

##### BrivoSDKLocalAuthentication Init 

This method is called in order to initalize the BrivoLocalAuthenticationModule

```kotlin
/**
* Initializes the BrivoLocalAuthentication module
*
* @param activity             the activity in which the biometric screen will start from
* @param title                the title of the biometric prompt
* @param message              the message of the biometric prompt
* @param negativeButtonText   the text of the negative button of the biometric prompt
* @param description          the text of the description of the biometric prompt
*
*/
void init(Activity activity, String title, String message, String negativeButtonText, String description);
```

```kotlin
try {
    BrivoSDKLocalAuthentication.getInstance().init(
        activity,
        title,
        message,
        negativeButtonText,
        description
    )
} catch (e: BrivoSDKInitializationException) {
    e.printStackTrace()
}
```

##### BrivoSDKLocalAuthentication Can Authenticate

This method returns if authentication is possible, if it is will return the type of the biometric authentication (device credentials, fingerprint or face id).

```kotlin
/**
* Return if authentication can be performed and, if its the case, the type of the authentication
*
* @param listener             listener that handles if the possibility of authentication is success or failure
*/
void canAuthenticate(@NonNull IOnCanAuthenticateListener listener);
```

```kotlin
try {
    BrivoSDKLocalAuthentication.getInstance().canAuthenticate(
        object: IOnCanAuthenticateListener {
            @Override
            public void onSuccess(BiometricResult type) {
                // Handle success case
            }
    
            @Override
            public void onFailed(BrivoError error) {
                // Handle error case
            }
        }
    )
} catch (BrivoSDKInitializationException e) {
    e.printStackTrace();
}
```

### BrivoSDKLocalAuthentication Cancel Authentication

```kotlin
/**
* Cancel ongoing authentication
*/
void cancelAuthentication();
```

```kotlin
BrivoSDKLocalAuthentication.getInstance().cancelAuthentication()
```

## Issues
If you run into any bugs or issues, feel free to post an [Issues](https://github.com/brivo-mobile-team/brivo-mobile-sdk-android/issues) to discuss.

<p align="center">
Made with ❤️ at Brivo
</p>

