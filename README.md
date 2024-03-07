#Brivo Mobile SDK
===========================================
A set of reusable libraries, services and components for Java and Kotlin Android apps.
### Installation
```
    defaultConfig {
        minSdkVersion 23
    }
```
Minimum Android version required is 23 in order to use the Brivo Mobile SDK.

Add the following dependencies to your app’s build.gradle configuration
```
dependencies {
    .
    .
    .
    //==================== BRIVO SDK =========================
    implementation(name:'brivoaccess-release', ext:'aar')
    implementation(name:'brivoble-release', ext:'aar')
    implementation(name:'brivoble-core-release', ext:'aar')
    implementation(name:'brivocore-release', ext:'aar')
    implementation(name:'brivoonair-release', ext:'aar')
    implementation(name:'brivolocalauthentication-release', ext:'aar')
    //==================== BRIVO SDK DEPENDENCIES ============
    implementation 'com.google.code.gson:gson:2.9.1'
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.9.1'
    implementation 'androidx.biometric:biometric:1.2.0-alpha04'
    //========================================================
}
```

Add the following permissions to your app’s manifest
```    
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
```
## Usage
To initialize the Brivo Mobile SDK call the initialize method with the application context and with the BrivoConfiguration object
The BrivoConfiguration object requires the clientId and secretId which is provided by Brivo. Valid Brivo URL’s also need to be specified in the Brivo configuration object


```
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
```
//Java
try {
BrivoSDK.getInstance().init(getApplicationContext(), new BrivoConfiguration(
CLIENT_ID,
CLIENT_SECRET,
useSDKStorage,
shouldVerifyDoor));
} catch (BrivoSDKInitializationException | MalformedURLException e) {
//Handle BrivoSDK initialization exception
}

//Kotlin
try {
BrivoSDK.getInstance().init(applicationContext, BrivoConfiguration(
CLIENT_ID,
CLIENT_SECRET,
useSDKStorage,
shouldVerifyDoor))
} catch (e: BrivoSDKInitializationException) {
//Handle BrivoSDK initialization exception
}
```
The exception is thrown if the SDK is not initialized correctly.
For example one of the parameters is null or missing.

## Brivo Mobile SDK Modules

# BrivoFluid - Fluid Access has been removed. Thank you for your understanding.

#### BrivoCore
Initializes the BrivoSDK.
This module also contains the getVersion() method which retuns the SDK version
```
String getVersion()
```
```
//Java
BrivoSDK.getInstance().getVersion()

//Kotlin
BrivoSDK.getInstance().version
```

#### BrivoOnAir
This module manages the connection between the SDK and the Brivo enviroment.
Redeem a Brivo Onair Pass. Brivo Onair Pass allows you to open doors with your smartphone.
```
/**
* Redeem a Brivo Onair Pass. Brivo Onair Pass allows you to open doors with your smartphone.
*
* @param email    Email received from Brivo
* @param token    Token received from Brivo
* @param listener listener that handles the success or failure
  */
  void redeemPass(String email, String token, IOnRedeemPassListener listener);
```
#### BrivoSDKOnair redeem pass usage 
```
//Java
try {
BrivoSDKOnair.getInstance().redeemPass(email, token, new IOnRedeemPassListener() {
@Override
public void onSuccess(BrivoOnairPass pass) {
//Manage pass
}

        @Override
        public void onFailed(BrivoError error) {
            //Handle redeem pass error case
        }
    });
} catch (BrivoSDKInitializationException e) {
//Handle BrivoSDK initialization exception
}

//Kotlin
try {
BrivoSDKOnair.getInstance().redeemPass(email, token, object : IOnRedeemPassListener {
override fun onSuccess(pass: BrivoOnairPass) {
//Manage pass
}

        override fun onFailed(error: BrivoError) {
            //Handle error
        }
    })
} catch (e: BrivoSDKInitializationException) {
//Handle BrivoSDK initialization exception
}
```
Refresh a Brivo Onair Pass.
```
/**
* Refresh a Brivo Onair Pass. Brivo Onair Pass allows you to open doors with your smartphone.
*
* @param brivoTokens accessToken received from Brivo
*                    refreshToken received from Brivo
*/
void refreshPass(BrivoTokens brivoTokens, IOnRedeemPassListener listener);
```
#### BrivoSDKOnair refresh pass usage 
```
//Java
try {
BrivoSDKOnair.getInstance().refreshPass(tokens, new IOnRedeemPassListener() {
@Override
public void onSuccess(BrivoOnairPass pass) {
//Manage refreshed pass
}

        @Override
        public void onFailed(BrivoError error) {
            //Handle refresh pass error case
        }
    });
} catch (BrivoSDKInitializationException e) {
//Handle BrivoSDK initialization exception
}

//Kotlin

try {
BrivoSDKOnair.getInstance().refreshPass(tokens, object : IOnRedeemPassListener {
override fun onSuccess(pass: BrivoOnairPass) {
//Manage pass
}

        override fun onFailed(error: BrivoError) {
            //Handle error
        }
    })
} catch (e: BrivoSDKInitializationException) {
e.printStackTrace()
}
```
Retrieve SDK locally stored passes.
```
/**
* Retrieved SDK locally stored passes
*
* @param listener      listener that handles success or failure
  */
  void retrieveSDKLocallyStoredPasses(IOnRetrieveSDKLocallyStoredPassesListener listener);
```
#### BrivoSDKOnair retrieve locally stored passes usage
```
//Java
try {
BrivoSDKOnair.getInstance().retrieveSDKLocallyStoredPasses(new IOnRetrieveSDKLocallyStoredPassesListener() {
@Override
public void onSuccess(LinkedHashMap < String, BrivoOnairPass > passes) {
//Manage retrieved passes
}

        @Override
        public void onFailed(BrivoError error) {
            //Handle error
        }
    });
} catch (BrivoSDKInitializationException e) {
e.printStackTrace();
}

//Kotlin
try {
BrivoSDKOnair.getInstance().retrieveSDKLocallyStoredPasses(object : IOnRetrieveSDKLocallyStoredPassesListener {
override fun onSuccess(passes: LinkedHashMap<String, BrivoOnairPass>?) {
//Manage passes
}

        override fun onFailed(error: BrivoError) {
            //Handle error
        }
    })
} catch (e: BrivoSDKInitializationException) {
e.printStackTrace()
}
```

#### BrivoAccess
This module handles unlocking of access points internally. It determines what authentication type is required (BLE or NETWORK).
```
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
* @param listener                 listener that handles the communication state (SCANNING, AUTHENTICATE, SHOULD_CONTINUE, CONNECTING, COMMUNICATING, SUCCESS, FAILED)
  */
  void unlockAccessPoint(String passId, String accessPointId, CancellationSignal cancellationSignal, IOnCommunicateWithAccessPointListenerListener listener);
```
#### BrivoSDKAccess unlock access point usage 
```
//Java
try {
BrivoSDKAccess.getInstance().unlockAccessPoint(passId, accessPointId, cancellationSignal, new IOnCommunicateWithAccessPointListenerListener() {
@Override
public void onSuccess() {
//Handle unlock access point success case
}

        @Override
        public void onFailed(BrivoError error) {
            //Handle unlock access point error case
        }
    });
} catch (BrivoSDKInitializationException e) {
//Handle BrivoSDK initialization exception
}

//Kotlin
try {
BrivoSDKAccess.getInstance().unlockAccessPoint(passId, accessPointId, cancellationSignal, object : IOnCommunicateWithAccessPointListenerListener {
override fun onSuccess() {
//Handle unlock access point success case
}

        override fun onFailed(error: BrivoError) {
            //Handle unlock access point error case
        }
    })
} catch (e: BrivoSDKInitializationException) {
//Handle BrivoSDK initialization exception
}
```
This method is called when credentials and data are managed outside of BrivoSDK.
```
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
* @param listener                 listener that handles the communication state (SCANNING, AUTHENTICATE, SHOULD_CONTINUE, CONNECTING, COMMUNICATING, SUCCESS, FAILED)
  */
  void unlockAccessPoint(BrivoSelectedAccessPoint brivoSelectedAccessPoint, CancellationSignal cancellationSignal, IOnCommunicateWithAccessPointListenerListener listener);
```

#### BrivoSDKAccess unlock access point usage with external credentials
```
//Java
try {
BrivoSDKAccess.getInstance().unlockAccessPoint(selectedAccessPoint, cancellationSignal, new IOnCommunicateWithAccessPointListener() {
@Override
public void onResult(@NonNull BrivoResult result) {
switch (result.getCommunicationState()) {
case SUCCESS:
//Handle success
break;
case FAILED:
//Handle failure
break;
case SHOULD_CONTINUE:
//Handle custom action and afterwards perform shouldContinue
result.getShouldContinueListener().onShouldContinue(true);
break;
case SCANNING:
//Scanning
break;
case AUTHENTICATE:
//Authenticate
break;
case CONNECTING:
//Connecting
break;
case COMMUNICATING:
//Communication
break;
}
}
});
}
} catch (BrivoSDKInitializationException e) {
//Handle BrivoSDK initialization exception
}

//Kotlin
try {
BrivoSDKAccess.getInstance().unlockAccessPoint(selectedAccessPoint,
cancellationSignal,
object : IOnCommunicateWithAccessPointListener {
override fun onResult(result: BrivoResult) {
when (result.communicationState) {
AccessPointCommunicationState.SUCCESS -> {
//Handle success
}
AccessPointCommunicationState.FAILED -> {
//Handle failure
}
AccessPointCommunicationState.SHOULD_CONTINUE -> {
//Handle custom action and afterwards perform shouldContinue
result.shouldContinueListener?.onShouldContinue(true)
}
AccessPointCommunicationState.SCANNING -> //scanning
AccessPointCommunicationState.AUTHENTICATE -> //authenticating
AccessPointCommunicationState.CONNECTING -> //connecting
AccessPointCommunicationState.COMMUNICATING -> //communicating
}
}
})
} catch (e: BrivoSDKInitializationException) {
//Handle BrivoSDK initialization exception
}
```

#### BrivoSDKAccess unlock nearest BLE access point using external credentials
```
/**
* Sends a request to unlock the closest access point to BrivoSDK
* All credentials and data are managed outside of BrivoSDK
*
* @param passes                   Passes needed to unlock nearest bluetooth that is in range
* @param cancellationSignal       Cancellation signal in order to cancel a BLE communication process
*                                 if a null cancellation signal is provided there will be default 30 second timeout
* @param listener                 listener that handles the communication state (SCANNING, AUTHENTICATE, SHOULD_CONTINUE, CONNECTING, COMMUNICATING, SUCCESS, FAILED)
  */
  void unlockNearestBLEAccessPoint(List<BrivoOnairPass> passes, CancellationSignal cancellationSignal, IOnCommunicateWithAccessPointListenerListener listener)
```

#### BrivoSDKAccess unlock nearest BLE access point usage with external credentials

```
//Java
try {
BrivoSDKAccess.getInstance().unlockNearestBLEAccessPoint(passes, cancellationSignal, new IOnCommunicateWithAccessPointListener() {
@Override
public void onResult(@NonNull BrivoResult result) {
switch (result.getCommunicationState()) {
case SUCCESS:
//Handle success
break;
case FAILED:
//Handle failure
break;
case SHOULD_CONTINUE:
//Handle custom action and afterwards perform shouldContinue
result.getShouldContinueListener().onShouldContinue(true);
break;
case SCANNING:
//Scanning
break;
case AUTHENTICATE:
//Authenticate
break;
case CONNECTING:
//Connecting
break;
case COMMUNICATING:
//Communication
break;
}
}
});
} catch (BrivoSDKInitializationException e) {
//Handle BrivoSDK initialization exception
}

//Kotlin
try {
BrivoSDKAccess.getInstance().unlockNearestBLEAccessPoint(passes,
cancellationSignal,
object : IOnCommunicateWithAccessPointListener {
override fun onResult(result: BrivoResult) {
when (result.communicationState) {
AccessPointCommunicationState.SUCCESS -> {
//Handle success
}
AccessPointCommunicationState.FAILED -> {
//Handle failure
}
AccessPointCommunicationState.SHOULD_CONTINUE -> {
//Handle custom action and afterwards perform shouldContinue
result.shouldContinueListener?.onShouldContinue(true)
}
AccessPointCommunicationState.SCANNING -> //scanning
AccessPointCommunicationState.AUTHENTICATE -> //authenticating
AccessPointCommunicationState.CONNECTING -> //connecting
AccessPointCommunicationState.COMMUNICATING -> //communicating
}
}
})
} catch (e: BrivoSDKInitializationException) {
//Handle BrivoSDK initialization exception
}
```

#### BrivoSDKAccess unlock nearest BLE access point
```
/**
* Sends a request to unlock the closest access point to BrivoSDK
*
* @param cancellationSignal       Cancellation signal in order to cancel a BLE communication process
*                                 if a null cancellation signal is provided there will be default 30 second timeout
* @param listener                 listener that handles the communication state (SCANNING, AUTHENTICATE, SHOULD_CONTINUE, CONNECTING, COMMUNICATING, SUCCESS, FAILED)
  */
  void unlockNearestBLEAccessPoint(CancellationSignal cancellationSignal, IOnCommunicateWithAccessPointListenerListener listener)
```

#### BrivoSDKAccess unlock nearest BLE access point usage
```
//Java
try {
BrivoSDKAccess.getInstance().unlockNearestBLEAccessPoint(cancellationSignal,new IOnCommunicateWithAccessPointListener() {
@Override
public void onResult(@NonNull BrivoResult result) {
switch (result.getCommunicationState()) {
case SUCCESS:
//Handle success
break;
case FAILED:
//Handle failure
break;
case SHOULD_CONTINUE:
//Handle custom action and afterwards perform shouldContinue
result.getShouldContinueListener().onShouldContinue(true);
break;
case SCANNING:
//Scanning
break;
case AUTHENTICATE:
//Authenticate
break;
case CONNECTING:
//Connecting
break;
case COMMUNICATING:
//Communication
break;
}
}
});
} catch (BrivoSDKInitializationException e) {
//Handle BrivoSDK initialization exception
}

//Kotlin
try {
BrivoSDKAccess.getInstance().unlockNearestBLEAccessPoint(cancellationSignal,
cancellationSignal,
object : IOnCommunicateWithAccessPointListener {
override fun onResult(result: BrivoResult) {
when (result.communicationState) {
AccessPointCommunicationState.SUCCESS -> {
//Handle success
}
AccessPointCommunicationState.FAILED -> {
//Handle failure
}
AccessPointCommunicationState.SHOULD_CONTINUE -> {
//Handle custom action and afterwards perform shouldContinue
result.shouldContinueListener?.onShouldContinue(true)
}
AccessPointCommunicationState.SCANNING -> //scanning
AccessPointCommunicationState.AUTHENTICATE -> //authenticating
AccessPointCommunicationState.CONNECTING -> //connecting
AccessPointCommunicationState.COMMUNICATING -> //communicating
}
}
})
} catch (e: BrivoSDKInitializationException) {
//Handle BrivoSDK initialization exception
}
```
#### BrivoBLE
This module manages the connection between an access point and a panel through bluetooth.


#### BrivoConfiguration
This is the module used to configure Allegion Control Locks for the No Tour feature.

### BrivoSDKConfigure configureAccessPoint with internal credentials
```
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

### BrivoSDKConfigure configureAccessPoint with internal credentials usage
```
//Java
BrivoSDKConfigure.getInstance().configureAccessPoint(passId, accessPointId, cancellationSignal, new IOnCommunicateWithAccessPointListener {
@Override
public void onResult(@NonNull BrivoResult result) {
switch (result.getCommunicationState()) {
case SUCCESS:
//Handle success
break;
case FAILED:
//Handle failure
break;
}
}
});
//Kotlin
BrivoSDKConfigure.getInstance().configureAccessPoint(passId, accessPointId, cancellationSignal, object : IOnCommunicateWithAccessPointListener {
object : IOnCommunicateWithAccessPointListener {
override fun onResult(result: BrivoResult) {
when (result.communicationState) {
AccessPointCommunicationState.SUCCESS -> {
//Handle success
}
AccessPointCommunicationState.FAILED -> {
//Handle failure
}
}
}
})
```

### BrivoSDKConfigure configureAccessPoint with external credentials
```
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

}
```

### BrivoSDKConfigure configureAccessPoint with external credentials usage
```
//Java
BrivoSDKConfigure.getInstance().configureAccessPoint(selectedAccessPoint, cancellationSignal, new IOnCommunicateWithAccessPointListener {
@Override
public void onResult(@NonNull BrivoResult result) {
switch (result.getCommunicationState()) {
case SUCCESS:
//Handle success
break;
case FAILED:
//Handle failure
break;
}
}
});
//Kotlin
BrivoSDKConfigure.getInstance().configureAccessPoint(selectedAccessPoint, cancellationSignal, object : IOnCommunicateWithAccessPointListener {
override fun onResult(result: BrivoResult) {
when (result.communicationState) {
AccessPointCommunicationState.SUCCESS -> {
//Handle success
}
AccessPointCommunicationState.FAILED -> {
//Handle failure
}
}
})
```

#### BrivoLocalAuthentication
This module manages the biometric authentication if an access point requires two factor.
This module also can return the type of biometric authentication (device credentials, fingerprint or face id).

### BrivoSDKLocalAuthentication init 
This method is called in order to initalize the BrivoLocalAuthenticationModule
```
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

BrivoSDKLocalAuthentication init usage
```
//Java
try {
BrivoSDKLocalAuthentication.getInstance().init(activity,
title,
message,
negativeButtonText,
description);
} catch (BrivoSDKInitializationException e) {
e.printStackTrace();
}

//Kotlin
try {
BrivoSDKLocalAuthentication.getInstance().init(activity,
title,
message,
negativeButtonText,
description)
} catch (e: BrivoSDKInitializationException) {
e.printStackTrace()
}
```

### BrivoSDKLocalAuthentication can authenticate method
This method returns if authentication is possible, if it is will return the type of the biometric authentication (device credentials, fingerprint or face id).
```
/**
* Return if authentication can be performed and, if its the case, the type of the authentication
*
* @param listener             listener that handles if the possibility of authentication is success or failure
  */
  void canAuthenticate(@NonNull IOnCanAuthenticateListener listener);
```

BrivoSDKLocalAuthentication can authenticate method usage
```
//Java
try {
BrivoSDKLocalAuthentication.getInstance().canAuthenticate(new IOnCanAuthenticateListener() {
@Override
public void onSuccess(BiometricResult type) {
//Handle success case
}

    @Override
    public void onFailed(BrivoError error) {
        //Handleerror case
    }
}););
} catch (BrivoSDKInitializationException e) {
e.printStackTrace();
}

//Kotlin
try {
BrivoSDKLocalAuthentication.getInstance().canAuthenticate(object: IOnCanAuthenticateListene {
@Override
public void onSuccess(BiometricResult type) {
//Handle success case
}

    @Override
    public void onFailed(BrivoError error) {
        //Handle error case
    }
}););
} catch (BrivoSDKInitializationException e) {
e.printStackTrace();
}
```

### BrivoSDKLocalAuthentication cancel authentication method
```
/**
* Cancel ongoing authentication
  */
  void cancelAuthentication();
```
BrivoSDKLocalAuthentication cancel authentication method usage
```
//Java
BrivoSDKLocalAuthentication.getInstance().cancelAuthentication();

//Kotlin
BrivoSDKLocalAuthentication.getInstance().cancelAuthentication()
```

