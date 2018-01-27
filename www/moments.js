/*global cordova, module*/

module.exports = {
    initialize: function (apiKey, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "MomentsPlugin", "initialize", [apiKey]);
    },
    recordEvent: function (eventName, eventData, successCallback, errorCallback) {
        if (eventData !== null && eventData !== undefined)
            cordova.exec(successCallback, errorCallback, "MomentsPlugin", "recordEvent", [eventName, eventData]);
        else
            cordova.exec(successCallback, errorCallback, "MomentsPlugin", "recordEvent", [eventName]);
    },
    setTrackingMode: function (trackingMode, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "MomentsPlugin", "setTrackingMode", [trackingMode]);
    },
    verifyPermissions: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "MomentsPlugin", "verifyPermissions", []);
    }
    // TODO: add more moments SDK functions
};
