package app.example.johnsaunders.currencyconverter;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import android.text.method.ScrollingMovementMethod;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class viewAll extends AppCompatActivity implements View.OnClickListener{

    ProgressBar loading;
    Spinner searchSpinner;
    TextView countriesInfo;

    JSONObject symbolsAPIInfo;
    JSONObject ratesAPIInfo;

    ArrayAdapter<String> searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);

        //hide title bar
        getSupportActionBar().hide();

        loading = findViewById(R.id.loading);
        searchSpinner = findViewById(R.id.searchSpinner);

        countriesInfo = findViewById(R.id.countriesInfo);
        countriesInfo.setMovementMethod(new ScrollingMovementMethod());


        Button back = findViewById(R.id.goBack);
        back.setOnClickListener(this);

        new loadContent().execute();


    }

    //if back button is clicked, go back
    public void onClick(View view){
        startActivity(new Intent(this, MainActivity.class));
    }

    //thread that loads content
    class loadContent extends AsyncTask<Void, Void, Void> {

        String[] arrayForSpinner;
        String textBox = "";

        protected void onPreExecute() {
            loading.setVisibility(View.VISIBLE);
            arrayForSpinner = null;
        }
        @Override
        protected Void doInBackground(Void... params) {
            //api calls that get JSONs of API
            callAPIOnceSymbols();
            callAPIOnceRates();

            arrayForSpinner = setUpSpinners(); //get array for search spinner
            textBox = setUpText();//puts information in format for adding to screen

            return null;
        }

        protected void onPostExecute(Void aVoid) {
            countriesInfo.setText(textBox); //add api info to textbox

            //prepare array for spinner
            searchAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayForSpinner);
            searchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            //insert content into search spinner
            searchSpinner.setAdapter(searchAdapter);


            searchSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //user selects one currency and wants to know current rate
                    TextView indvRate = findViewById(R.id.indvRate);
                    indvRate.setVisibility(View.VISIBLE);
                    String countrySel = searchSpinner.getSelectedItem().toString();

                    DecimalFormat resultForm = new DecimalFormat("#.##");
                    String rate = String.valueOf(resultForm.format(Double.parseDouble(ratesAPIInfo.get(getSymbol(countrySel)).toString())));

                    String text = getSymbol(countrySel) + ": " + rate;

                    indvRate.setText(text);


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            loading.setVisibility(View.INVISIBLE);
        }
    }

    //get array info for spinner
    public String[] setUpSpinners() {

        //add all the countries into an arraylist
        ArrayList<String> countries = new ArrayList<>();
        Set<String> countryKeys = symbolsAPIInfo.keySet();
        for(String countryKey: countryKeys) {
            countries.add(symbolsAPIInfo.get(countryKey).toString());
        }

        String[] arrayForSpinner = countries.toArray(new String[countries.size()]); //convert to array
        Arrays.sort(arrayForSpinner); //alphabetic
        return arrayForSpinner;

    }

    //format the api text to be displayed
    public String setUpText(){

        String[] countries = setUpSpinners(); //setUpspinners method returns countries in order, what I need

        String text = "";
        DecimalFormat resultForm = new DecimalFormat("#.####");
        //add in form: "country: rate"
        for(String country : countries){ //loop through each country
            String rate = String.valueOf(resultForm.format(Double.parseDouble(ratesAPIInfo.get(getSymbol(country)).toString()))); //get rate of country, put in decimal format
            text += country + ": " + rate + "\n\n";
        }

        return text;
    }

    //give country name, outputs 3 letter symbol
    public String getSymbol(String country) {
        //loop through the keys and stop when a match is found
        Set<String> countryKeys = symbolsAPIInfo.keySet();
        for (String countryKey : countryKeys) {
            if (symbolsAPIInfo.get(countryKey).equals(country)) {
                return countryKey;
            }
        }
        return null;
    }

    //call api that gives all countries and their symbols
    public void callAPIOnceSymbols(){
        try {
            //make connection
            URL url = new URL("http://data.fixer.io/api/symbols?access_key=520d04fd9386823e41a2e4e6e2d9e4fe");
            HttpURLConnection myConnection = (HttpURLConnection) url.openConnection();
            myConnection.setRequestProperty("User-Agent", "my-rest-app-v0.1");

            //read data, into String
            InputStream responseBody = myConnection.getInputStream();
            InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
            BufferedReader reader = new BufferedReader(responseBodyReader);
            String line = "";
            StringBuilder apiText = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                apiText.append(line);
            }

            //convert to a JSON
            JSONParser parser = new JSONParser();
            JSONObject data = (JSONObject) parser.parse(apiText.toString()); //convert text from site to a JSON
            symbolsAPIInfo = (org.json.simple.JSONObject) data.get("symbols"); //create a new JSON of just symbols

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
    }

    //call api that gets all the rates of the countries
    public void callAPIOnceRates(){
        try {
            //make connection
            URL url = new URL("http://data.fixer.io/api/latest?access_key=520d04fd9386823e41a2e4e6e2d9e4fe&symbols");
            HttpURLConnection myConnection = (HttpURLConnection) url.openConnection();
            myConnection.setRequestProperty("User-Agent", "my-rest-app-v0.1");

            //read data, into String
            InputStream responseBody = myConnection.getInputStream();
            InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
            BufferedReader reader = new BufferedReader(responseBodyReader);
            String line = "";
            StringBuilder apiText = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                apiText.append(line);
            }

            //convert to a JSON
            JSONParser parser = new JSONParser();
            JSONObject data = (JSONObject) parser.parse(apiText.toString()); //convert text from site to a JSON
            JSONObject ratesJSON = (JSONObject) data.get("rates");
            ratesAPIInfo = ratesJSON;

        }
        catch (IOException e) {
             System.out.println(e.getMessage());
        }
        catch (ParseException e) {
             System.out.println(e.getMessage());
        }
    }

}
