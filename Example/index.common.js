import React, {Component} from 'react';
import {
    AppRegistry,
    StyleSheet,
    Text,
    View,
    Button
} from 'react-native';

import Tapjoy from 'react-native-tapjoy';

const tapjoyOptions = {
    sdkKeyIos: "12345",
    sdkKeyAndroid: "12345",
    gcmSenderIdAndroid: "12345",
    debug: true
};

export default class Example extends Component {
    constructor(props) {
        super(props);

        this.state = {
            currencyText: "Not connected to Tapjoy",
            lastEarnedText: "Nothing earned currently"
        };

        this.tapjoy = new Tapjoy(tapjoyOptions);
        this.initTapjoy();

        this.initTapjoy = this.initTapjoy.bind(this);
        this.tapjoyShowPlacement = this.tapjoyShowPlacement.bind(this);
    }

    initTapjoy() {
        this.tapjoy.initialise()
            .then((info) => {
                console.log(info);
                this.tapjoyAddPlacement();
                this.tapjoyRequestContent();
                this.getCurrencyBalance();
                this.tapjoyListenForEarnedCurrency();
            })
            .catch((error) => {
                console.log(error);
            });
    }

    getCurrencyBalance() {
        this.tapjoy.getCurrencyBalance()
            .then((resp) => {
                this.setState({
                    currencyText: "Your currency balance: " + resp.value + " " + resp.currencyBalance
                });
            })
            .catch((error) => {
                console.log(error);
            });
    }

    tapjoyRequestContent() {
        this.tapjoy.requestContent("Offerwall")
            .catch((error) => {
                console.log(error);
            });
    }

    tapjoyListenForEarnedCurrency() {
        this.tapjoy.listenForEarnedCurrency(function (evt) {
            this.setState({
                currencyText: "Currently earned " + evt.value + " " + evt.currency
            });
        })
            .catch((error) => console.log(error));
    }

    tapjoyShowPlacement() {
        this.tapjoy.showPlacement("Offerwall")
            .catch((error) => {
                console.log(error);
            });
    }

    tapjoyAddPlacement() {
        this.tapjoy.addPlacement("Offerwall", function (evt) {
            if (evt.onContentDismiss) {
                this.getCurrencyBalance();
                this.tapjoyRequestContent();
            }
        })
            .catch((error) => console.log(error));
    }

    render() {
        return (
            <View style={styles.container}>
                <Text>{this.state.currencyText}</Text>
                <Text>{this.state.lastEarnedText}</Text>
                <Button onPress={this.tapjoyShowPlacement} style={styles.button} title='tapjoyShowPlacement'/>
                <Button onPress={this.initTapjoy} style={styles.button} title='init Tapjoy'/>
            </View>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#F5FCFF',
    }
});

AppRegistry.registerComponent('Example', () => Example);
