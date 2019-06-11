# cordova-ptv-navigator-ri
Cordova PTV Truck Navigator Remote Interface plugin

Current version 0.1.0
- Able to connect / disconnect with PTV Remote Interface via Intent Android Service
- Able to receive and send message between PTV Truck Navigator app
- The available commands are
  - connect(successCallback)
  - disconnect(successCallback)
  - getProfile(successCallback)
  - setProfile(profileName:String, successCallback)
