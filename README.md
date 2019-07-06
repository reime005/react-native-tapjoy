<p>&nbsp;</p>
<p align="center">
  <p align='center'>React Native Tapjoy</p>
</p>
<p>&nbsp;</p>

<p>

This module provides react-native bindings for the [Tapjoy SDK](https://home.tapjoy.com/developers/).

</p>

<p>&nbsp;</p>

[![npm](https://img.shields.io/npm/v/react-native-tapjoy.svg?style=flat-square)](http://npm.im/react-native-tapjoy)
![GitHub](https://img.shields.io/github/license/reime005/react-native-tapjoy.svg?style=flat-square)
[![CircleCI](https://circleci.com/gh/reime005/react-native-tapjoy.svg?style=svg)](https://circleci.com/gh/reime005/react-native-tapjoy)

## About Tapjoy

Tapjoy is a monetization and distribution services provider for mobile applications. Tapjoy’s goal is to maximize the value of every user for freemium mobile app publishers. Its Marketing Automation and Monetization Platform for mobile apps uses market leading data science, user segmentation and predictive analytics to drive deeper engagement and optimize revenue from every user.

## About React Native

React Native lets you build mobile apps using only JavaScript. It uses the same design as React, letting you compose a rich mobile UI from declarative components. You don't build a “mobile web app”, an “HTML5 app”, or a “hybrid app”. You build a real mobile app that's __indistinguishable__ from an app built using Objective-C or Java.

## Install

### Tested with react native version 0.60.0

    npm install react-native-tapjoy@latest --save

### Automatic Installation (react native version < 0.60.0)

    react-native link

### Manual Installation

#### iOS

1. In the XCode's "Project navigator", right click on your project's Libraries folder ➜ `Add Files to <...>`
2. Go to `node_modules` ➜ `react-native-tapjoy` ➜ `ios` ➜ select `RNTapjoy.xcodeproj`
3. Add `libRNTapjoy.a` to `Build Phases -> Link Binary With Libraries`
4. Add the Tapjoy SDK to your XCode project as described on the Tapjoy website

NOTE: You have to manually integrate the Push notifications feature as described on the Tapjoy website, because it requires access to the didFinishLaunchingWithOptions:` method in your application’s app delegate file.

#### Android

1. Add the following lines to `android/settings.gradle`:
    ```gradle
    include ':react-native-tapjoy'
    project(':react-native-tapjoy').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-tapjoy/android')

2. Update the android build tools version to `2.2.+` in `android/build.gradle`:
    ```gradle
    buildscript {
        ...
        dependencies {
            classpath 'com.android.tools.build:gradle:2.2.+' // <- USE 2.2.+ version
        }
        ...
    }
    ...
    ```

3. Update the gradle version to `2.14.1` in `android/gradle/wrapper/gradle-wrapper.properties`:
    ```
    ...
    distributionUrl=https\://services.gradle.org/distributions/gradle-2.14.1-all.zip
    ```

4. Add the compile line to the dependencies in `android/app/build.gradle`:
    ```gradle
    dependencies {
        compile project(':react-native-tapjoy')
    }
    ```

5. Add the import and link the package in `MainApplication.java`:
    ```java
    import de.reimerm.tapjoy.TapjoyPackage; // <-- add this import

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

## Usage

    import Tapjoy from 'react-native-tapjoy';

    const options = {
        sdkKeyIos: "12345",
        sdkKeyAndroid: "12345",
        gcmSenderIdAndroid: "12345",
        debug: true
    };

    const tapjoy = new Tapjoy(options);

### Initialization

    tapjoy.initialise()
            .then((info) => {
                console.log(info);
                // Connect successful
                // You may add placements here
            })
            .catch((error) => console.log(error));

### Add Placement

    tapjoy.addPlacement("TestPlacement", function (evt) {
            if (evt.onContentDismiss) {
                // You may want to refresh the currency balance here,
                // or request new content
            } else if (evt.onRequestSuccess) {
            } else if (evt.onRequestFailure) {
                console.log(evt.errorMessage);
            } else if (evt.onContentReady) {
            } else if (evt.onContentShow) {
            } else if (evt.onPurchaseRequest) {
                console.log("Request ID: " + evt.requestId);
                console.log("Token: " + evt.token);
                console.log("Product ID: " + evt.productId);
            } else if (evt.onRewardRequest) {
                console.log("Request ID: " + evt.requestId);
                console.log("Token: " + evt.token);
                console.log("Item ID: " + evt.itemId);
                console.log("Quantity: " + evt.quantity);
            }
        }).catch((error) => console.log(error));

### Request Content

    tapjoy.requestContent("TestPlacement")
            .catch((error) => console.log(error));

### Show Placement

    tapjoy.showPlacement("TestPlacement")
            .catch((error) => console.log(error));

### Get Currency Balance

    tapjoy.getCurrencyBalance()
            .then((resp) => {
                console.log("Your currency balance: " + resp.value + " " + resp.currencyBalance);
            })
            .catch((error) => console.log(error));

### Listen for earned currency

    tapjoy.listenForEarnedCurrency(function (evt) {
                console.log("Currently earned " + evt.value + " " + evt.currency);
            }).catch((error) => console.log(error));
