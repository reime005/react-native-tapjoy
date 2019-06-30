import { useRef, useEffect, useCallback } from "react";

import { Tapjoy } from ".";

export const useTapjoy = options => {
  const tapjoy = useRef(new Tapjoy(options));

  const initialiseTapjoy = useCallback(
    async (onConnectionChange) => {
      const foo = await tapjoy.current.initialise(onConnectionChange)
      console.warn(foo);

    },
    [],
  )

  const addTapjoyPlacement = useCallback(
    async (name) => {
      const foo = await tapjoy.current.addPlacement(name, (t) => {
        console.warn(t);
      })

      console.warn(foo);

    },
    [],
  )

  return [
    {initialiseTapjoy,
    addTapjoyPlacement}
  ]
}
