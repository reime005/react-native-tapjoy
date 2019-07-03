import { useRef, useEffect, useCallback } from 'react';

import { Tapjoy } from '.';

export const useTapjoy = options => {
  const tapjoy = useRef(new Tapjoy(options));

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
    return tapjoy.current.spendCurrency(name);
  }, []);

  const setTapjoyUserId = useCallback((userID: number) => {
    return tapjoy.current.setUserId(userID);
  }, []);

  const getTapjoyCurrencyBalance = useCallback(() => {
    return tapjoy.current.getCurrencyBalance();
  }, []);

  const tapjoyListenForEarnedCurrency = useCallback((cb) => {
    return tapjoy.current.listenForEarnedCurrency(cb);
  }, []);

  const isTapjoyConnected = useCallback(() => {
    return tapjoy.current.isConnected();
  }, []);

  return [{}, { initialiseTapjoy, addTapjoyPlacement, showTapjoyPlacement, requestTapjoyPlacementContent }];
};
