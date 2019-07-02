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

  return [{}, { initialiseTapjoy, addTapjoyPlacement, showTapjoyPlacement, requestTapjoyPlacementContent }];
};
