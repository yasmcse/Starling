#

App Features and Api </br>

1- Transactions List </br>
   Shows the list of transactions after fetching from starling test api</br>
2- Saving Goals </br> 
   The user can add the sum of transactions roundup into existing saving goals.</br>
3- Add new Saving Goal </br>
   User can also add a new saving goal </br> 

Starling test api </br>


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
       The token is generated for 24 hours. The api don't privde the end point to get the refreshed token. 
       If the token expires while you are testng the app, please ask the recruiter to contact me, so i can send over the new token after genearting from dash board.
       Sending the token to recruiter in email. You will need to replace the ACCESS_TOKEN in local.properties.

       
