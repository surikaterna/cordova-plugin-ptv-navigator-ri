  var PTVRemoteInterfaceLoader = function(require, exports, module) {
    var exec = require('cordova/exec');
  
    function PTVRemoteInterface() {}

    PTVRemoteInterface.prototype.connect = function(success, failure, timeOffset) {
      exec(success, failure, 'PTVRemoteInterface', 'connect', []);
    };
  
    PTVRemoteInterface.prototype.disconnect = function(success, failure) {
      exec(success, failure, 'PTVRemoteInterface', 'disconnect', []);
    };
  
    PTVRemoteInterface.prototype.getProfile = function(success, failure) {
      exec(success, failure, 'PTVRemoteInterface', 'getProfile', []);
    };

    PTVRemoteInterface.prototype.setProfile = function(profileName, success, failure) {
      exec(success, failure, 'PTVRemoteInterface', 'setProfile', [profileName]);
    };
  
    var pTVRemoteInterface = new PTVRemoteInterface();
    module.exports = pTVRemoteInterface;
  };
  
  PTVRemoteInterfaceLoader(require, exports, module);
  
  cordova.define("cordova/plugin/PTVRemoteInterface", PTVRemoteInterfaceLoader);