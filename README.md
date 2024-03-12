#

App Features and Api </br>

1- Transactions List </br>
   Shows the list of transactions after fetching from starling test api</br>
2- Saving Goals </br> 
   The user can add the sum of transactions roundup into existing saving goals.</br>
3- Add new Saving Goal </br>
   User can also add a new saving goal </br> 

Starling test api </br>
https://developer.starlingbank.com/docs#api-endpoint-urls-1

Tech Stack Use </br>

* Gradle Build with version catalogue using .toml file to keep the versions and dependencies at central location. </br>
* Multi module gradle app with feature gradle modules. </br>
* Jetpack Compose. </br>
* Jetpack Navigation Compose to navigate between composables in feature modules. </br>
* Coroutines and Kotlin Flow for asychronous api calls.
* Implementation of clean architecture and its layers. </br>
* Unit testing using JUnit, mockk and kotlin mockito. </br>

Notes:- 

     * PLease see the docs and images folders in app to read about architectural decisions and images of running app
     * api access token is ignored to be published on git. resides in local.properties and provided through BuildConfig. 
       The token is generated for 24 hours. Let me know if you want me to generate the token.

       
