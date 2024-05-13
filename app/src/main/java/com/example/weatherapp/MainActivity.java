package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    String info;
    Button button;
    JSONObject j;
    TextView t;
    EditText zipCode;
    String locationInfo;
    AsyncThread asyncThread;
    TextView feelsLike;
    TextView DisplayTemp;
    TextView displayLocation;
    TextView displayCountry;
    TextView displayTime;
    TextView displayDescrption;
    SeekBar seekBar;
    ImageView imageView;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DisplayTemp = findViewById(R.id.id_displayTemp);
        t = findViewById(R.id.id_Temperature);
        zipCode = findViewById(R.id.id_ZipCode);
        button = findViewById(R.id.id_button);
        feelsLike = findViewById(R.id.id_feelsLike);
        displayLocation = findViewById(R.id.id_Location);
        displayCountry = findViewById(R.id.id_displayCountry);
        displayTime = findViewById(R.id.id_DateTime);
        seekBar = findViewById(R.id.id_discreteSeek);
        displayDescrption = findViewById(R.id.id_viewDesc);
        imageView = findViewById(R.id.id_imageWeather);






        locationInfo = zipCode.getText().toString();
        Log.d("TAG_LOCATION", locationInfo);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationInfo = zipCode.getText().toString();
                new AsyncThread().execute(String.valueOf(zipCode.getText()));

            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //TEMPERATURE
                String tempString2 = null;
                try {
                    tempString2 = j.getJSONArray("list").getJSONObject(i).getJSONObject("main").getString("temp");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                double tempDouble = Double.parseDouble(tempString2);
                int tempInt = (int) ((int) tempDouble-273.15) * 9/5+32;
                DisplayTemp.setText(String.valueOf(tempInt) + "°F");

                //FEELSLIKE
                try {
                    String tempString3 = j.getJSONArray("list").getJSONObject(0).getJSONObject("main").getString("feels_like");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                double tempDouble2 = Double.parseDouble(tempString2);
                int tempInt2 = (int) ((int) tempDouble2-273.15) * 9/5+32;
                feelsLike.setText(String.valueOf(tempInt2) + "°F");


                //DATETIME
                long timestamp = 0;
                try {
                    timestamp = Long.parseLong(j.getJSONArray("list").getJSONObject(i).getString("dt")) * 1000;
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Date date = new Date(timestamp);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd KK:mm:ss", Locale.getDefault());
                String formattedDate = sdf.format(date);
                displayTime.setText(formattedDate);

                try {
                    displayDescrption.setText(j.getJSONArray("list").getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                //IMAGES
                try {
                    if(j.getJSONArray("list").getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description").contains("sky")) {
                        imageView.setImageResource(R.drawable.sky);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                try {
                    if(j.getJSONArray("list").getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description").contains("snow")) {
                        imageView.setImageResource(R.drawable.snow);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                try {
                    if(j.getJSONArray("list").getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description").contains("clouds") ||j.getJSONArray("list").getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description").contains("storm")) {
                        imageView.setImageResource(R.drawable.clouds);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                try {
                    if(j.getJSONArray("list").getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description").contains("rain")) {
                        imageView.setImageResource(R.drawable.rain);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                try {
                    if(j.getJSONArray("list").getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description").contains("mist")) {
                        imageView.setImageResource(R.drawable.mist);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public class AsyncThread extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            info = "";
            try {
                URL url = new URL("https://api.openweathermap.org/data/2.5/forecast?zip=" + locationInfo + "Api Key Goes Here");
                Log.d("Test", "will it break1");
                URLConnection yc = url.openConnection();
                Log.d("Test", "will it break2");
                InputStream inputStream = yc.getInputStream();
                Log.d("Test", "will it break3");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                Log.d("Test", "will it break4");
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    info += line;
                }
                j = new JSONObject(info);
            } catch (MalformedURLException e) {
                Log.e("TAG_ERROR", "Malformed URL Exception"+ e.toString());
            } catch (IOException e) {
                Log.e("TAG_ERROR", "IO Exception"+ e.toString());
            } catch (JSONException e) {
                Log.e("TAG_ERROR", " Exception"+ e.toString());
            }
            return null;
        }
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            Log.d("Test", "Success");
            try {
                //TEMPERATURE
                String tempString = j.getJSONArray("list").getJSONObject(0).getJSONObject("main").getString("temp");
                double tempDouble = Double.parseDouble(tempString);
                int tempInt = (int) ((int) tempDouble-273.15) * 9/5+32;
                DisplayTemp.setText(String.valueOf(tempInt) + "°F");

                //FEELSLIKE
                String tempString2 = j.getJSONArray("list").getJSONObject(0).getJSONObject("main").getString("feels_like");
                double tempDouble2 = Double.parseDouble(tempString2);
                int tempInt2 = (int) ((int) tempDouble2-273.15) * 9/5+32;
                feelsLike.setText(String.valueOf(tempInt2) + "°F");

                //LOCATION
                displayLocation.setText(j.getJSONObject("city").getString("name") + ",");
                displayCountry.setText(j.getJSONObject("city").getString("country"));

                //DESCRPTION
                displayDescrption.setText(j.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("description"));

                //DATE&TIME
                long timestamp = Long.parseLong(j.getJSONArray("list").getJSONObject(0).getString("dt")) * 1000;
                Date date = new Date(timestamp);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd KK:mm:ss", Locale.getDefault());
                String formattedDate = sdf.format(date);
                displayTime.setText(formattedDate);

            } catch (JSONException e) {
                Log.d("test",e.toString());
            }
        }
    }
}