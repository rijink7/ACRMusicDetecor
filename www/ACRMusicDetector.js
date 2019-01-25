var exec = require('cordova/exec');

exports.coolMethod = function(name, success, error) {
    exec(success, error, "ACRMusicDetector", "coolMethod", [name]);
};

exports.init = function(arg0, success, error) {
    exec(success, error, "ACRMusicDetector", "init", [arg0]);
};

exports.start = function(arg0, success, error) {
    exec(success, error, "ACRMusicDetector", "start", [arg0]);
};

exports.stop = function(arg0, success, error) {
    exec(success, error, "ACRMusicDetector", "stop", [arg0]);
};

exports.startPreRec = function(arg0, success, error) {
    exec(success, error, "ACRMusicDetector", "startPreRec", [arg0]);
};

exports.cancel = function(arg0, success, error) {
    exec(success, error, "ACRMusicDetector", "cancel", [arg0]);
};

exports.stopPreRec = function(arg0, success, error) {
    exec(success, error, "ACRMusicDetector", "stopPreRec", [arg0]);
};

