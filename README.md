<p>&nbsp;</p>
<p align="center">
  <h1 align='center'>React Native Tapjoy<p>&nbsp;</p></h1>
</p>
<p>&nbsp;</p>

<p>

This module provides react-native bindings for the [Tapjoy SDK](https://home.tapjoy.com/developers/).

</p>

<p>&nbsp;</p>

[![npm](https://img.shields.io/npm/v/react-native-tapjoy.svg?style=flat-square)](http://npm.im/react-native-tapjoy)
![GitHub](https://img.shields.io/github/license/reime005/react-native-tapjoy.svg?style=flat-square)
![Github Actions Status](https://github.com/reime005/react-native-tapjoy/workflows/main/badge.svg)

## About Tapjoy

Tapjoy is a monetization and distribution services provider for mobile applications. Tapjoy’s goal is to maximize the value of every user for freemium mobile app publishers. Its Marketing Automation and Monetization Platform for mobile apps uses market leading data science, user segmentation and predictive analytics to drive deeper engagement and optimize revenue from every user.

## About React Native

React Native lets you build mobile apps using only JavaScript. It uses the same design as React, letting you compose a rich mobile UI from declarative components. You don't build a “mobile web app”, an “HTML5 app”, or a “hybrid app”. You build a real mobile app that's __indistinguishable__ from an app built using Objective-C or Java.

## Install

### Tested with react native version 0.60.0

    npm install react-native-tapjoy@latest --save

### Automatic Installation (react native version < 0.60.0)

    react-native link

<details>
<summary>
<b>
Manual Installation (react native version < 0.60.0)
</b>
</summary>

#### iOS

1. In the XCode's "Project navigator", right click on your project's Libraries folder ➜ `Add Files to <...>`
2. Go to `node_modules` ➜ `react-native-tapjoy` ➜ `ios` ➜ select `RNTapjoy.xcodeproj`
3. Add `libRNTapjoy.a` to `Build Phases -> Link Binary With Libraries`
4. Add the Tapjoy SDK to your XCode project as described on the Tapjoy website

Alertnatively, you may use the podspec file:

```
  ...
  pod 'RNTapjoy', :path => '../node_modules/react-native-tapjoy'
  ...
```

NOTE: You have to manually integrate the Push notifications feature as described on the Tapjoy website, because it requires access to the didFinishLaunchingWithOptions:` method in your application’s app delegate file.

#### Android

1. Add the following lines to `android/settings.gradle`:
    ```gradle
    include ':react-native-tapjoy'
    project(':react-native-tapjoy').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-tapjoy/android')

2. Update the android build tools version to `3.4.1` in `android/build.gradle`:
    ```gradle
    buildscript {
        ...
        dependencies {
            classpath 'com.android.tools.build:gradle:3.4.1'
        }
        ...
    }
    ...
    ```

3. Update the gradle version to `5.4.1` in `android/gradle/wrapper/gradle-wrapper.properties`:
    ```
    ...
    distributionUrl=https\://services.gradle.org/distributions/gradle-5.4.1-all.zip
    ```

4. Add the compile line to the dependencies in `android/app/build.gradle`:
    ```gradle
    dependencies {
        compile project(':react-native-tapjoy')
    }
    ```

5. Add the import and link the package in `MainApplication.java`:
    ```java
    import com.mariusreimer.tapjoy.TapjoyPackage; // <-- add this import

    public class MainApplication extends Application implements ReactApplication {
        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                new MainReactPackage(),
                new TapjoyPackage() // <-- add this line
            );
        }
    }
    ```

6. Add the Tapjoy SDK to your Android project as described on the Tapjoy website.

<details>
<summary>
<b>
Windows
</b>
</summary>

![](https://media.giphy.com/media/44Eq3Ab5LPYn6/giphy.gif)

</details>
</details>

## Usage

TypeScript definitions are included.

### Configuration

    const options = {
        sdkKeyIos: "12345",
        sdkKeyAndroid: "12345",
        gcmSenderIdAndroid: "12345",
        debug: true
    };

### Initialization (without React Hooks)

    import { Tapjoy } from 'react-native-tapjoy';

    const tapjoy = new Tapjoy(options);

    try {
        const initialized = await tapjoy.initialse();
        // tapjoy is initialized
    } catch (e) {
        // error on initialization
    }

### Initialization (with React Hooks)

    import { useEffect } from 'react';
    import { useTapjoy } from 'react-native-tapjoy';

    const [
        {
            tapjoyEvents
        },
        {
            initialiseTapjoy,
            listenToEvent,
            addTapjoyPlacement,
            showTapjoyPlacement,
            requestTapjoyPlacementContent,
            isTapjoyConnected,
            tapjoyListenForEarnedCurrency,
            getTapjoyCurrencyBalance,
            setTapjoyUserId,
            spendTapjoyCurrency,
        },
    ] = useTapjoy(tapjoyOptions);

    useEffect(() => {
        try {
            const initialized = await initialiseTapjoy();
            // tapjoy is initialized
        } catch (e) {
            // error on initialization
        }
    }, []);

### List of Events

This is a list of events you may subscribe to:

|Event Name|Description|
|---|---|
|`earnedCurrency`|Fired when currency was earned. The response contains the fields `amount` `currencyName` and `currencyID` (iOS only). |
|`onPlacementDismiss`|Fired when a shown placement was dismissed. The response contains the field `placementName`. |
|`onPlacementContentReady`|Fired when a placement's content is ready to be shown. The response contains the field `placementName`. |
|`onPurchaseRequest`|Fired when a purchase is requested. The response contains the fields `placementName`, `requestId`, `token` and `productId`. |
|`onRewardRequest`| Fired when a reward is requested. The response contains the fields `placementName`, `requestId`, `token`, `itemId` and `quantity`. |

### Listen for Events (without React Hooks)

    tapjoy._on(eventName, () => {
        // event has fired
    })

You may get a list of events from the constants: `tapjoy.constants`. It contains a `events` field.

### Listen for Events (with React Hooks)

    listenToEvent(eventName, () => {
        // event has fired
    })

You may get a list of events from `useTapjoy` hook, named `tapjoyEvents`.

### Add Placement (without React Hooks)

    try {
        await tapjoy.addPlacement();
        // tapjoy placement was added
    } catch (e) {
        // error
    }

### Add Placement (with React Hooks)

    useEffect(() => {
        try {
            await addTapjoyPlacement('TestPlacement');
            // tapjoy placement was added
        } catch (e) {
            // error
        }
    }, []);

### Request Content (without React Hooks)

    try {
        await tapjoy.requestContent('TestPlacement');
        // tapjoy placement content request was successful
    } catch (e) {
        // request failed
    }

### Request Content (with React Hooks)

    useEffect(() => {
        try {
            await requestTapjoyPlacementContent('TestPlacement');
            // tapjoy placement content request was successful
        } catch (e) {
            // request failed
        }
    }, []);

### Show Placement (without React Hooks)

    try {
        await tapjoy.showPlacement('TestPlacement');
        // tapjoy placement content is showing
    } catch (e) {
        // placement not added, or content not ready
    }

### Show Placement (with React Hooks)

    useEffect(() => {
        try {
            await showTapjoyPlacement('TestPlacement');
            // tapjoy placement content is showing
        } catch (e) {
            // placement not added, or content not ready
        }
    }, []);

### Listen for Earned Currency (without React Hooks)

    try {
        await tapjoy.listenForEarnedCurrency(({ amount, currencyName, currencyID }) => {
            // user earned currency
        });
    } catch (e) {
        // error
    }

### Listen for Earned Currency (with React Hooks)

    useEffect(() => {
        try {
            await tapjoyListenForEarnedCurrency(({ amount, currencyName, currencyID }) => {
                // user earned currency
            });
        } catch (e) {
        // error
        }
    }, []);

### Get Currency Balance (without React Hooks)

    try {
        const { amount, currencyName } = await tapjoy.getCurrencyBalance();
    } catch (e) {
        // error
    }

### Get Currency Balance (with React Hooks)

    useEffect(() => {
        try {
            const { amount, currencyName } = await getTapjoyCurrencyBalance();
        } catch (e) {
            // error
        }
    }, []);

### Spend Currency (without React Hooks)

    try {
        await tapjoy.spendCurrencyAction(42);
    } catch (e) {
        // error
    }

### Spend Currency (with React Hooks)

    useEffect(() => {
        try {
            await spendTapjoyCurrency(42);
        } catch (e) {
            // error
        }
    }, []);
