var exec = require('cordova/exec');

exports.coolMethod = function(arg0, success, error) {
    exec(success, error, "ACRMusicDetector", "coolMethod", [arg0]);
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

