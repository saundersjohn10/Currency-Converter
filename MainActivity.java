package app.example.johnsaunders.currencyconverter;

//required imports
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

//added imports
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import java.util.Set;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.Arrays;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.content.Context;
import android.net.NetworkInfo;
import android.content.Intent;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //items onscreen
    Button convertBtn;
    Button viewAll;
    ImageButton infoBtn;
    Spinner toSpinner;
    Spinner fromSpinner;
    Spinner searchSpinner;
    TextView amountBox;
    TextView resultLbl;
    TextView errorMessage;
    TextView rateLbl;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hide title bar
        getSupportActionBar().hide();

        //set up instance variables
        convertBtn = findViewById(R.id.convertBtn);
        convertBtn.setOnClickListener(this);
        fromSpinner = findViewById(R.id.fromSpinner);
        toSpinner = findViewById(R.id.toSpinner);
        amountBox = findViewById(R.id.amtBox);
        resultLbl = findViewById(R.id.resultLbl);
        errorMessage = findViewById(R.id.errorLbl);
        rateLbl = findViewById(R.id.rateLbl);
        loading = findViewById(R.id.progressBar);
        infoBtn = findViewById(R.id.infoBtn);
        infoBtn.setOnClickListener(this);
        searchSpinner = findViewById(R.id.searchSpinner);
        viewAll = findViewById(R.id.viewAll);
        viewAll.setOnClickListener(this);


        new loadContent().execute();





    }


    //put country options into spinners
    public String[] setUpSpinners() {
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
            JSONObject symbolsJSON = (org.json.simple.JSONObject) data.get("symbols"); //create a new JSON of just symbols

            ArrayList<String> countries = new ArrayList<>();
            //loop through the keys and stop when a match is found
            Set<String> countryKeys = symbolsJSON.keySet();
            for(String countryKey: countryKeys) {
                countries.add(symbolsJSON.get(countryKey).toString());
            }

            String[] arrayForSpinner = countries.toArray(new String[countries.size()]); //convert to array
            Arrays.sort(arrayForSpinner); //alphabetic
            return arrayForSpinner;

        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    //thread that loads contents and sets up spinners
    class loadContent extends AsyncTask<Void, Void, Void>{

        String[] arrayForSpinner;

        protected void onPreExecute() {
            loading.setVisibility(View.VISIBLE);
            arrayForSpinner = null;
        }
        @Override
        protected Void doInBackground(Void... params) {
            //start if there is internet
            if(isNetworkAvailable()){
                //spinner setup
                System.out.println("I am here");
                arrayForSpinner = setUpSpinners();
            }
            else{
                System.out.println("I am here2");
                //create a popup saying no internet
                final AlertDialog.Builder alert = new AlertDialog.Builder(getApplicationContext());
                alert.setTitle("Connection Trouble").setMessage("Please connect to the internet");
                alert.setCancelable(true);
                alert.setNeutralButton("Try again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(isNetworkAvailable()){ //internet back online
                            dialog.cancel();
                            setUpSpinners();
                        }
                        else
                            alert.show();
                    }
                });
                alert.show();
            }

            return null;
        }

        protected void onPostExecute(Void aVoid) {
            System.out.println("I am here3");
            //prepare array for spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayForSpinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //insert content into spinners
            fromSpinner.setAdapter(adapter);
            toSpinner.setAdapter(adapter);
            loading.setVisibility(View.INVISIBLE);
        }
    }


    //runs when convert is clicked
    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.convertBtn){ //if convert button pressed
            //check to see if textinput is empty
            if(amountBox.getText().toString().equals("")){
                errorMessage.setVisibility(View.VISIBLE);
            }
            else{
                errorMessage.setVisibility(View.INVISIBLE);
                //get info from screen
                String from = fromSpinner.getSelectedItem().toString();
                String to = toSpinner.getSelectedItem().toString();
                double amount = Double.parseDouble(amountBox.getText().toString());

                if(isNetworkAvailable()){
                    new convert(from,to,amount).execute();
                }
                else {
                    //create a popup saying no internet
                    final AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("Connection Trouble").setMessage("Please connect to the internet");
                    alert.setCancelable(true);
                    alert.setNeutralButton("Try again", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (isNetworkAvailable()) { //internet back online
                                dialog.cancel();
                            } else
                                alert.show();
                        }
                    });
                    alert.show();
                }
            }
        }

        else if(view.getId() == R.id.infoBtn){
            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Currency Converter").setMessage("Get updated and accurate currency conversions as long as you have an internet connection!");
            alert.setCancelable(true);
            alert.setNeutralButton("Done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }}
            );
            alert.show();
        }

        else if(view.getId() == R.id.viewAll){
            startActivity(new Intent(this, viewAll.class));

        }
    }

    //method seeing if internet is connected
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    //thread that connects to API, finds conversion between currencies
    class convert extends AsyncTask<Void, Void, Void> {

        private String from;
        private String to;
        private Double amount;

        private Double finalRate;
        private Double toRatefinal;
        private Double fromRatefinal;

        private String toSym;
        private String fromSym;


        //initiate vars
        public convert(String from, String to, Double amount) {
            super();
            this.from = from;
            this.to = to;
            this.amount = amount;
        }

        @Override
        //show loading bar
        protected void onPreExecute() {
            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            String fromKey = "";
            String toKey = "";
            //get the 3 letter symbol from internet, these two keys are needed for the url to get the currency data
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
                JSONObject symbolsJSON = (org.json.simple.JSONObject) data.get("symbols"); //create a new JSON of just symbols

                //loop through the keys and stop when a match is found
                Set<String> countryKeys = symbolsJSON.keySet();
                for(String countryKey: countryKeys) {
                    if(symbolsJSON.get(countryKey).equals(from)){
                        fromKey = countryKey;
                        fromSym = countryKey;
                        break;
                    }
                }
                for(String countryKey: countryKeys) {
                    if(symbolsJSON.get(countryKey).equals(to)){
                        toKey = countryKey;
                        toSym = countryKey;
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } catch (ParseException e) {
                System.out.println(e.getMessage());
            }

            //get data about specific countries
            try {
                //make connection
                URL url = new URL("http://data.fixer.io/api/latest?access_key=520d04fd9386823e41a2e4e6e2d9e4fe&symbols=" + fromKey + "," + toKey);
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

                //format and get correct data
                fromRatefinal = Double.parseDouble(ratesJSON.get(fromSym).toString());
                toRatefinal = Double.parseDouble(ratesJSON.get(toSym).toString());

                //calculation
                finalRate = ((amount * toRatefinal) / fromRatefinal);
                return null;

            } catch (IOException e) {
                System.out.println(e.getMessage());
                return null;
            } catch (ParseException e) {
                System.out.println(e.getMessage());
                return null;
            }
        }

        @Override
        //when the task has been completed, update texts
        protected void onPostExecute(Void aVoid) {

            DecimalFormat resultForm1 = new DecimalFormat("#.##");
            //get conversion and set it
            String resultTxt = String.valueOf(resultForm1.format(finalRate)) + " " + toSym;
            resultLbl.setText(resultTxt);
            resultLbl.setVisibility(View.VISIBLE);

            DecimalFormat resultForm2 = new DecimalFormat("#.####");
            //show the current rate
            String currentRate = "1 " + fromSym + " = " + String.valueOf(resultForm2.format(toRatefinal/fromRatefinal)) + " " + toSym;
            rateLbl.setText(currentRate);
            rateLbl.setVisibility(View.VISIBLE);
            loading.setVisibility(View.INVISIBLE);
        }
    }
}
