package com.example.yourweather;



import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    String token = "9b65aaadc20dece694ffb1fd5c81acf1";
    private EditText yourCity;
    private Button getWeather;
    private TextView temprature_city;
    private TextView wind_city;
    private TextView humidity_city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        yourCity = findViewById(R.id.yourCity);
        getWeather = findViewById(R.id.getWeather);
        temprature_city = findViewById(R.id.temprature_city);
        wind_city = findViewById(R.id.wind_city);
        humidity_city = findViewById(R.id.humidity_city);

        getWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(yourCity.getText().toString().trim().equals("")){
                    Toast.makeText(MainActivity.this, "Введите город", Toast.LENGTH_SHORT).show();
                }
                else {
                    String city = yourCity.getText().toString();
                    String url = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+token+"&units=metric&lang=ru";
                    new GetWeatherData().execute(url);
                }
                
            }
        });
    }
    private class GetWeatherData extends AsyncTask<String, String, String>{

        protected void onPreExecute(){
            super.onPreExecute();
            temprature_city.setText("Ожидайте...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection  connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine())!=null){
                    buffer.append(line).append("\n");
                    return buffer.toString();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(connection != null){
                    connection.disconnect();
                }
                if (reader != null){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;

        }

        protected void onPostExecute(String res){
            super.onPostExecute(res);
            try {
                JSONObject jsonObject = new JSONObject(res);
                temprature_city.setText("Температура: "+ jsonObject.getJSONObject("main").getDouble("temp")+"°");
                wind_city.setText("Скорость ветра: "+jsonObject.getJSONObject("wind").getDouble("speed")+"м/с");
                humidity_city.setText("Влажность: "+jsonObject.getJSONObject("main").getDouble("humidity")+"%");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            yourCity.setText("");


        }
    }
}