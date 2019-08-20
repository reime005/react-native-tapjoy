import { Component, ReactNode } from 'react';
import { ViewProperties, EmitterSubscription } from 'react-native';
import { EventEmitter } from 'events';

type TapjoyEvent = string;

interface Constants {
  events: TapjoyEvent[];
}

interface RNTapjoyProps {
  sdkKeyIos: string;
  sdkKeyAndroid: string;
  gcmSenderIdAndroid?: string;
  debug: boolean;
}

interface Currency {
  amount: number;
  currencyName: string;
  currencyID?: string;
}

export class Tapjoy extends Component<RNTapjoyProps & ViewProperties> {
  static constants: Constants;

  setUserId(userId: string): Promise<>;
  initialise(): Promise<>;
  spendCurrency(amount: number): Promise<>;
  isConnected(): Promise<boolean>;
  addPlacement(name: string): Promise<string>;
  requestContent(name: string): Promise<string>;
  showPlacement(name: string): Promise<string>;
  getCurrencyBalance(): Promise<Currency>;
  listenForEarnedCurrency(): Promise<Currency>;
  _on(eventName: string, callback: () => void): Promise<EventEmitter>;
}

type HookReturn = [
  {
    tapjoyEvents: TapjoyEvent[];
  },
  {
    initialiseTapjoy: () => Promise<>;
    listenToEvent: (
      eventName: TapjoyEvent,
      callback: () => {},
    ) => Promise<EmitterSubscription>;
    addTapjoyPlacement: (placementName: string) => Promise<string>;
    showTapjoyPlacement: (placementName: string) => Promise<string>;
    requestTapjoyPlacementContent: (placementName: string) => Promise<string>;
    isTapjoyConnected: () => Promise<boolean>;
    tapjoyListenForEarnedCurrency: (
      callback: (currency: Currency) => {},
    ) => Promise<>;
    getTapjoyCurrencyBalance: (eventName: TapjoyEvent) => Promise<Currency>;
    setTapjoyUserId: (userId: string) => Promise<>;
    spendTapjoyCurrency: (amount: number) => Promise<>;
  },
];

export function useTapjoy(options: RNTapjoyProps): HookReturn;
