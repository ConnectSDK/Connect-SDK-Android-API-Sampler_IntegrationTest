# Connect-SDK-Android-IntegrationTest
The IntegrationTest for Connect-SDK-Android-API-Sampler App contains a set of automatic test for checking all features via UI in the same way like users do it.


##General Information
For more information about Connect SDK, visit the [main repository](https://github.com/ConnectSDK/Connect-SDK-Android).
For more information about Connect SDK Sampler App for Android, visit the [main repository](https://github.com/ConnectSDK/Connect-SDK-Android-API-Sampler).

##Dependencies

1. robotium-solo-5.2.1 library
2. Connect SDK Sampler App for Android
3. Connect SDK Sampler App for Android
4. A Mobile Device say Nexus 5 with latest Connect-SDK-Android-API-Sampler.apk installed must be connected.
5. A TV which supports following services must be connected via same Wifi network as the mobile device and TV(s) must support
the services like AirPlay, DLNA, DIAL, WebOs TV atleast.

##Setup and run from Eclipse

1. Download the Connect SDK Sampler App for Android project
2. git clone https://github.com/ConnectSDK/Connect-SDK-Android-API-Sampler.git
3. Go to Eclipse Menu -> File -> Import -> (Android) Existing Android Code
4. Open project properties and make sure that java compiler has version 1.6
5. For detail instruction on Sampler App setup follow the
6. [ReadMe](https://github.com/ConnectSDK/Connect-SDK-Android-API-Sampler)
7. Once the Sampler App is downloaded and setup is done.

8. Follow the below instructions to download and setup the Integretaion Test project.
9. Download the Connect-SDK-Android-IntegrationTest project
10. git clone https://github.com/ConnectSDK/Connect-SDK-Android-IntegrationTest.git
11. Go to Eclipse Menu -> File -> Import -> (Android) Existing Android Code into workspace
12. Open project properties and make sure that java compiler has version 1.6
13. Open project properties -> Java Build Path -> Projects and add a reference to Connect-SDK-Android-API-Sampler project
14. Open project properties -> Java Build Path -> Libraries and add all jars from libs folder
15. Run as Android JUnit Test

##License
Copyright (c) 2015 LG Electronics.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

> http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


