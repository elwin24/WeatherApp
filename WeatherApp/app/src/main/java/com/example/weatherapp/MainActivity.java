package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    WeatherDatabaseHelper mDatabaseHelper;
    class Weather extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... address) {
            try {
                URL url = new URL(address[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);

                int data = isr.read();
                String content = "";
                char ch;
                while (data != -1) {
                    ch = (char) data;
                    content = content + ch;
                    data = isr.read();
                }
                return content;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    static String temperature = "";
    static String data = "";
    static String city = "";
    String main = "";
    String description = "";
    String humidity = "";
    String pressure = "";
    String speed = "";

    //*****************************AD*************************
    private AdView mAdView;
    //*****************************AD*************************

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabaseHelper = new WeatherDatabaseHelper(this);
        ImageButton searchButton = findViewById(R.id.searchButton);
        ImageButton historyButton = findViewById(R.id.history_button);
        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                TextView cityName = findViewById(R.id.cityName);
                String cName = cityName.getText().toString();
                String content;
                Weather weather = new Weather();
                try {
                    content = weather.execute("https://openweathermap.org/data/2.5/weather?q=" +
                            cName + "&appid=439d4b804bc8187953eb36d2a8c26a02").get();
                    JSONObject jsonObject = new JSONObject(content);
                    String weatherData = jsonObject.getString("weather");
                    String mainTemperature = jsonObject.getString("main");
                    String wind = jsonObject.getString("wind");
                    JSONArray array = new JSONArray(weatherData);

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject weatherPart = array.getJSONObject(i);
                        main = weatherPart.getString("main");
                        description = weatherPart.getString("description");
                    }
                    JSONObject mainPage = new JSONObject(mainTemperature);
                    humidity = mainPage.getString("humidity");
                    pressure = mainPage.getString("pressure");
                    JSONObject windJSON = new JSONObject(wind);
                    speed = windJSON.getString("speed");
                    temperature = mainPage.getString("temp");
                   // data = jsonObject.getString("dt");
                    city = jsonObject.getString("name");

                    Date c = Calendar.getInstance().getTime();

                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                    String formattedDate = df.format(c);

                    String newEntry = city +"  "+ temperature +"\u00B0C  "+formattedDate;
                    if(newEntry.length() != 0){ AddData(newEntry); }
                    TextView mainView = findViewById(R.id.main);
                    mainView.setText(main);
                    TextView windVIew = findViewById(R.id.windData);
                    windVIew.setText(speed + "m/s");
                    TextView descriptionView = findViewById(R.id.description);
                    descriptionView.setText(description);
                    TextView temperatureView = findViewById(R.id.temperature);
                    temperatureView.setText(temperature + "\u00B0" + "C");
                    TextView humidityView = findViewById(R.id.humidityData);
                    humidityView.setText(humidity + "%");
                    TextView pressureView = findViewById(R.id.pressureData);
                    pressureView.setText(pressure);

// Drizzle Rain Snow Clear Clouds
// Mist Smoke Haze Dust Fog Sand Dust Ash Squall Tornado
                    ImageView activityMain = (ImageView) findViewById(R.id.imageView);

                    switch (main) {
                        case "Drizzle":
                            activityMain.setBackgroundResource(R.drawable.drizzle);
                            break;
                        case "Rain":
                            activityMain.setBackgroundResource(R.drawable.rain);
                            break;
                        case "Snow":
                            activityMain.setBackgroundResource(R.drawable.snow);
                            break;
                        case "Clear":
                            activityMain.setBackgroundResource(R.drawable.clear);
                            break;
                        case "Clouds":
                            activityMain.setBackgroundResource(R.drawable.clouds);
                            break;
                        case "Mist":
                            activityMain.setBackgroundResource(R.drawable.mist);
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,HistoryActivity.class);
                startActivity(intent);
            }
        });

        //*****************************AD*************************
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });
        //*****************************AD*************************

    }

    public void AddData(String newEntry) {
        boolean insertData = mDatabaseHelper.addData(newEntry);
//        if(insertData){
//            toastMessage("Data Successfully Inserted: ");
//        } else {   toastMessage("Something went wrong(");}
    }
//    private void toastMessage(String message){
//        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
//    }
     public void onClick(View view) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        }
}