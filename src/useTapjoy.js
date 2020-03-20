/* @flow */

import { useRef, useEffect, useCallback, useState } from 'react';
import {Tapjoy} from './Tapjoy';

export const useTapjoy = options => {
  const tapjoy = useRef(new Tapjoy(options));
  const [tapjoyEvents, setTapjoyEvents] = useState([]);

  useEffect(() => {
    const { events } = tapjoy.current.constants;
    setTapjoyEvents(events);
  }, []);

  const listenToEvent = useCallback((name, cb) => {
    return tapjoy.current._on(name, cb);
  }, []);

  const initialiseTapjoy = useCallback(() => {
    return tapjoy.current.initialise();
  }, []);

  const addTapjoyPlacement = useCallback(async name => {
    return tapjoy.current.addPlacement(name);
  }, []);

  const requestTapjoyPlacementContent = useCallback(async name => {
    return tapjoy.current.requestContent(name);
  }, []);

  const showTapjoyPlacement = useCallback(async name => {
    return tapjoy.current.showPlacement(name);
  }, []);

  const spendTapjoyCurrency = useCallback((amount: number) => {
    return tapjoy.current.spendCurrency(amount);
  }, []);

  const setTapjoyUserId = useCallback((userID: number) => {
    return tapjoy.current.setUserId(userID);
  }, []);

  const getTapjoyCurrencyBalance = useCallback(() => {
    return tapjoy.current.getCurrencyBalance();
  }, []);

  const tapjoyListenForEarnedCurrency = useCallback(cb => {
    return tapjoy.current.listenForEarnedCurrency(cb);
  }, []);

  const isTapjoyConnected = useCallback(() => {
    return tapjoy.current.isConnected();
  }, []);

  return [
    {
      tapjoyEvents,
    },
    {
      initialiseTapjoy,
      listenToEvent,
      addTapjoyPlacement,
      showTapjoyPlacement,
      requestTapjoyPlacementContent,
      setTapjoyUserId,
      getTapjoyCurrencyBalance,
      tapjoyListenForEarnedCurrency,
      isTapjoyConnected,
      spendTapjoyCurrency
    },
  ];
};
