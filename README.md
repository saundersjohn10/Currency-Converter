# Currency-Converter-App

This is an android app that converts the most up to data currencies between 170 countries.

Link to the app in the Google Play Store:
https://play.google.com/store/apps/details?id=app.example.johnsaunders.currencyconverter



![screenshot_1530504879](https://user-images.githubusercontent.com/36249204/42145071-f751eb4a-7d8c-11e8-888e-fb16c6d5c176.png)
![screenshot_1530504214](https://user-images.githubusercontent.com/36249204/42145056-d2646164-7d8c-11e8-95d0-3505864c8b9e.png)

This is one of my first projects and time complexity was not something I considered while creating it. For example, it gets the information from the internet and stores it in a array where it has to be linearly searched for any data requested and using a HashTable to get quick access to the data for specific countries would have been a more efficient option.



The app however makes good use of gaining retrieving data from the internet and multiple threads running.


The data that the app displays is from a free API supplier, https://fixer.io. The currencies from the site are updated every 60 seconds and the api calls are simple and easy to use. The app connects to the internet using 'HttpURLConnection' and an 'InputStream'. The data is parsed into a JSON using the Jar extension 'json-simple-1.1.1.jar'. An API call can be made in three different cases: when the user opens the app and the Spinners containing the countries information is made, when the user presses convert, and when the user chooses to view all the rates.

Android apps have a simple and powerful option for running threads called 'AsyncTask' that I utilized. This is what is used when any action is initiated and allowed the app to run concurrently. The 'onPreExecute' allowed for the app to display a loading animation while the app loads the content from the internet and while the app is setting up.

The design used the Constraint Layout feature provided by Android Studio which automatically creates an .xml file matching the display. The apps layout uses TextViews, Buttons, and EditTexts provided by Android. It also uses a 3rd party Spinner, com.toptoche.searchablespinnerlibrary.SearchableSpinner, that was used to allow the countries to be searched through. The app also has a popup window that will appear when there is no internet connection. The app checks the internet connection by using 'ConnectivityManager' and 'NetworkInfo'. The logo was made from a free site and the overall design was to focus on the color green, the color of money.

![screenshot_1530504240](https://user-images.githubusercontent.com/36249204/42145058-d2893df4-7d8c-11e8-9415-b350ed59eb58.png)
![screenshot_1530504230](https://user-images.githubusercontent.com/36249204/42145059-d298c760-7d8c-11e8-90b4-7bebbda09745.png)
![screenshot_1530504171](https://user-images.githubusercontent.com/36249204/42145057-d2776db8-7d8c-11e8-96b6-137dbfb4d474.png)

