# Currency-Converter-App

This is an android app that converts the most up to data currencies between 170 countries.

-API
-Concurency 

Link to the app in the Google Play Store:
https://play.google.com/store/apps/details?id=app.example.johnsaunders.currencyconverter

This is one of my first projects and time complexity was not something I considered while creating it.  For example, it gets the information from the internet and stores it in a array where it has to be linearly searched for any data requested and using a HashTable to get quick access to the data for specfic countries would have been a more effiecent option.

The app however does make good use of gaining access to the internet and mutilple threads running.

#API call
The data that the app displays is from a free API supplier, https://fixer.io.  The curreiecies from the site are updated every 60 seconds and the api calls are simple and easy to use. The app connects to the internet using 'HttpURLConnection' and an 'InputStream'.  The data is parsed into a JSON using the Jar extension 'json-simple-1.1.1.jar'.  An API call can be made in three differnet cases: when the user opens the app and the Spinners containing the contries information is made, when the user presses convert, and when the user choses to view all the rates.  

#Threads running
Android apps have a simple and powerful option for running threads called 'AsyncTask' that I utilized.  This is what is used when any action is initated and allowed the app to run concurrently.  The 'onPreExecute' allowed for the app to display a loading animation while the app loads the content from the internet and while the app is setting up.  


the design
