module.exports = {
    connectToPrinter: function(successCallback, failureCallback) {
        cordova.exec(successCallback, failureCallback, "MosambeePOSService", "connectToPrinter", []);
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