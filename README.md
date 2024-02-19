#


Tech Stack Use </br>

* Gradle Build with version catalogue using .toml file to keep the versions and dependencies at cenral location </br>
* Multi module gradle app with feature gradle modules </br>
* Jetpack Navigation Compose to navigate between composables in feature modules using </br>
* Coroutines and Kotlin Flow for asychronous api calls.
* Implementation of clean architecture and its layers. </br>
* Unit testing using JUnit, mockk and kotlin mockito. </br>

Notes:- 

     * PLease see the docs and images folders in app to read about architectural decisions and images of running app
     * api access token is ignored to be published on git. resides in local.properties and provided through BuildConfig. 
       The token is generated for 25 hours. The api don't privde the end point to get the refreshed token. 
       If the token expires while you are testng the app, please ask the recruiter to contact me, so i can send over thenew token after genearting from dash board.
       You will need to replace the ACCESS_TOKEN in local.properties.

       
