/*global cordova, module*/

module.exports = {
    initialize: function (apiKey, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "MomentsPlugin", "initialize", [apiKey]);
    }
    // TODO: add more moments SDK functions
};
