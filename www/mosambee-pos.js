cordova.define("cordova-plugin-mosambee-pos.MosambeePOS", function(require, exports, module) {
    module.exports = {
        connectToPrinter: function(successCallback, failureCallback) {
            cordova.exec(successCallback, failureCallback, "MosambeePOSService", "connectToPrinter", []);
        },
        print: function(text, successCallback, failureCallback) {
            cordova.exec(successCallback, failureCallback, "MosambeePOSService", "printText", [text]);
        },
        printBarcode: function(text, successCallback, failureCallback) {
            cordova.exec(successCallback, failureCallback, "MosambeePOSService", "printBarcode", [text]);
        },
        printQRcode: function(text, successCallback, failureCallback) {
            cordova.exec(successCallback, failureCallback, "MosambeePOSService", "printQRcode", [text]);
        },
        closePort: function(successCallback, failureCallback) {
            cordova.exec(successCallback, failureCallback, "MosambeePOSService", "closePort", []);
        },
        startScanner: function(successCallback, failureCallback) {
            cordova.exec(successCallback, failureCallback, "MosambeePOSService", "startScanner", []);
        },
        stopScanner: function(successCallback, failureCallback) {
            cordova.exec(successCallback, failureCallback, "MosambeePOSService", "stopScanner", []);
        },
        startPayment: function(options, successCallback, failureCallback) {
            cordova.exec(successCallback, failureCallback, "MosambeePOSService", "startPayment", [options]);
        }
    };
    });
    