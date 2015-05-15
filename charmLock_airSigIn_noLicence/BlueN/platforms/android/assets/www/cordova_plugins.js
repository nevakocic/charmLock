cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "file": "plugins/com.megster.cordova.bluetoothserial/www/bluetoothSerial.js",
        "id": "com.megster.cordova.bluetoothserial.bluetoothSerial",
        "clobbers": [
            "window.bluetoothSerial"
        ]
    },
    {
        "file": "plugins/com.airsig.plugin.AirSigGUIPlugin/www/AirSigGUIPlugin.js",
        "id": "com.airsig.plugin.AirSigGUIPlugin.AirSigGUIPlugin",
        "clobbers": [
            "AirSigGUIPlugin"
        ]
    }
];
module.exports.metadata = 
// TOP OF METADATA
{
    "com.megster.cordova.bluetoothserial": "0.4.1",
    "com.airsig.plugin.AirSigGUIPlugin": "0.1.0"
}
// BOTTOM OF METADATA
});