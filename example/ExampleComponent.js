/* eslint-disable */

/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @flow
 */

import React, {
  Fragment,
  useEffect,
  useState,
  useCallback,
} from 'react';
import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  StatusBar,
  Button,
} from 'react-native';

import { useTapjoy } from 'react-native-tapjoy';

const tapjoyOptions = {
  sdkKeyIos: '1234',
  sdkKeyAndroid: '1234',
  gcmSenderIdAndroid: '12345',
  debug: true,
};

const styles = StyleSheet.create({
  scrollView: {
    backgroundColor: '#fff',
  },
  body: {
    backgroundColor: '#fff',
  },
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
    color: 'red',
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
    color: 'red',
  },
  highlight: {
    fontWeight: '700',
  },
});

const App = () => {
  const [
    { tapjoyEvents },
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

  const [currencyBalance, setCurrencyBalance] = useState(null);
  const [connected, setConnected] = useState(false);

  const listenForEarnedCurrencyCallback = useCallback(
    ({ amount, currencyName }) => {
      alert(`Earned ${amount} ${currencyName}`);
    },
    [],
  );

  const startListen = useCallback(async () => {
    try {
      const listening = await tapjoyListenForEarnedCurrency(
        listenForEarnedCurrencyCallback,
      );

      console.warn(listening);
    } catch (e) {
      console.warn(e);
    }
  });

  useEffect(() => {
    const listeners = {};

    tapjoyEvents.forEach(tapjoyEvent => {
      listeners[tapjoyEvent] = listenToEvent(tapjoyEvent, evt => {
        console.warn(evt);
      });
    });

    return () => {
      for (const key in listeners) {
        if (listeners[key] && listeners[key].remove) {
          listeners[key].remove();
        }
      }
    };
  }, [listenToEvent, tapjoyEvents]);

  /* eslint-disable */
  useEffect(() => {
    const foo = async () => {
      try {
        const initialized = await initialiseTapjoy();

        setConnected(true);

        if (initialized) {
          const placementAdded = await addTapjoyPlacement('Offerwall');
          console.warn(placementAdded);

          if (placementAdded) {
            const contentRequested = await requestTapjoyPlacementContent(
              'Offerwall',
            );
            console.warn(contentRequested);

            // if (contentRequested) {
            //   setTimeout(async () => {
            //     const placementShowed = await showTapjoyPlacement('Offerwall');
            //     console.warn(placementShowed);
            //   }, 5000)
            // }
          }
        }
      } catch (e) {
        setConnected(false);
        alert(e.message);
      }
    };

    foo();
  }, []);
  /* eslint-enable */

  return (
    <Fragment>
      <StatusBar barStyle="dark-content" />
      <SafeAreaView>
        <ScrollView
          contentInsetAdjustmentBehavior="automatic"
          style={styles.scrollView}
        >
          <View style={styles.body}>
            <View style={styles.sectionContainer}>
              {!connected ? (
                <Text>Tapjoy is not connected.</Text>
              ) : (
                <Text>Tapjoy is connected</Text>
              )}
            </View>
            <View style={styles.sectionContainer}>
              {connected && (
                <Button
                  title="Show Placement"
                  style={styles.sectionTitle}
                  onPress={async () => {
                    try {
                      await requestTapjoyPlacementContent('Offerwall');
                    } catch (e) {
                      console.warn(e);
                    }

                    try {
                      await showTapjoyPlacement('Offerwall');
                    } catch (e) {
                      console.warn(e);
                    }
                  }}
                />
              )}
            </View>
          </View>

          {connected && (
            <View style={styles.body}>
              <View style={styles.sectionContainer}>
                <Button
                  title="Check Balance"
                  style={styles.sectionTitle}
                  onPress={async () => {
                    try {
                      const result = await getTapjoyCurrencyBalance();

                      setCurrencyBalance(result);
                    } catch (e) {
                      console.warn(e.message);
                    }
                  }}
                />
                <Button
                  title="Listen"
                  style={styles.sectionTitle}
                  onPress={async () => {
                    try {
                      const listening = await tapjoyListenForEarnedCurrency(
                        listenForEarnedCurrencyCallback,
                      );

                      console.warn(listening);
                    } catch (e) {
                      console.warn(e.message);
                    }
                  }}
                />
              </View>
            </View>
          )}

          {currencyBalance && (
            <View>
              <Text style={styles.sectionDescription}>
                {currencyBalance.currencyName}
              </Text>
              <Text style={styles.sectionDescription}>
                {currencyBalance.currencyID}
              </Text>
              <Text style={styles.sectionDescription}>
                {currencyBalance.amount}
              </Text>
            </View>
          )}
        </ScrollView>
      </SafeAreaView>
    </Fragment>
  );
};

export default App;
