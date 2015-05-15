/*global cordova, module*/

module.exports = {
    showEntryActivity: function (userId) {
        cordova.exec(function(result){/*alert("OK" + reply);*/}, function(result){/*alert("Error" + reply);*/}, "AirSigGUIPlugin", "showEntryActivity", [userId]);
    },
	showTrainingActivity: function (userId) {
        cordova.exec(function(result){/*alert("OK" + reply);*/}, function(result){/*alert("Error" + reply);*/}, "AirSigGUIPlugin", "showTrainingActivity", [userId]);
    },
	showVerifyActivity: function (userId) {
        cordova.exec(function(result){/*alert("OK" + result);*/}, function(result){/*alert("Error" + result);*/}, "AirSigGUIPlugin", "showVerifyActivity", [userId]);
    },
	showTutorialActivity: function (userId) {
        cordova.exec(function(result){/*alert("OK" + reply);*/}, function(result){/*alert("Error" + reply);*/}, "AirSigGUIPlugin", "showTutorialActivity", [userId]);
    },
    startRecordSensor: function (success,error,userId) {
        cordova.exec(success, error, "AirSigGUIPlugin", "startRecordSensor", [userId]);
    },
    completeRecordSensorToIdentifyAction: function (success,error,userId) {
        cordova.exec(success, error, "AirSigGUIPlugin", "completeRecordSensorToIdentifyAction", [userId]);
    },
    completeRecordSensorToTrainAction: function (success,error,userId) {
        cordova.exec(success, error, "AirSigGUIPlugin", "completeRecordSensorToTrainAction", [userId]);
    },
    deleteRecord: function (success,error,userId) {
        cordova.exec(success, error, "AirSigGUIPlugin", "deleteRecord", [userId]);
    },
    resetSignature: function (success,error,userId) {
        cordova.exec(success, error, "AirSigGUIPlugin", "resetSignature", [userId]);
    },
    isSignatureValid: function (success,error,userId) {
        cordova.exec(success, error, "AirSigGUIPlugin", "isSignatureValid", [userId]);
    },
    resetSignatureValid: function (success,error,userId) {
        cordova.exec(success, error, "AirSigGUIPlugin", "resetSignatureValid", [userId]);
    }
};
