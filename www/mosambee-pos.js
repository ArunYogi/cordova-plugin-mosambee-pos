module.exports = {
    connectToPrinter: function(successCallback, failureCallback) {
        cordova.exec(successCallback, failureCallback, "MosambeePOSService", "connectToPrinter", []);
    },
    print: function(text, successCallback, failureCallback) {
        cordova.exec(successCallback, failureCallback, "MosambeePOSService", "printText", [text]);
    },
    closePort: function(successCallback, failureCallback) {
        cordova.exec(successCallback, failureCallback, "MosambeePOSService", "closePort", []);
    },
    startScanner: function(successCallback, failureCallback) {
        cordova.exec(successCallback, failureCallback, "MosambeePOSService", "startScanner", []);
    },
    stopScanner: function(successCallback, failureCallback) {
        cordova.exec(successCallback, failureCallback, "MosambeePOSService", "stopScanner", []);
    }
};