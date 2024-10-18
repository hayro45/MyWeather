package tr.duzce.edu.mf.bm.myweather;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.prefs.Preferences;

public class HomeFragment extends Fragment {


    //lokasyon bilgisinin preference değişkenleri
    private static final String PREFERENCES_NAME = "my_preferences";
    private static final String PREFERENCE_LATITUDE = "latitude";
    private static final String PREFERENCE_LONGITUDE = "longitude";

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String appid = "api :)";

    private boolean isCelsius;
    private boolean isDarkTheme;


    EditText etCity, etCountry;
    TextView tvResult, tempIconName, etTemp;
    ImageView ivWeather;
    DecimalFormat df = new DecimalFormat("#");





    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        initComponents();

        View eventView = view.findViewById(R.id.btnGet);
        eventView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWeatherDetails(view);
            }
        });



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        preferences = requireActivity().getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        String latitude = preferences.getString(PREFERENCE_LATITUDE, "0");
        String longitude = preferences.getString(PREFERENCE_LONGITUDE, "0");

        isCelsius = preferences.getBoolean("isCelsius", true);

        getWeatherDetailsByLocation(view, latitude, longitude);

        //clickrefresh button
        View refreshView = view.findViewById(R.id.weather_refresh_image_view);
        refreshView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWeatherDetailsByLocation(view, latitude, longitude);
            }
        });
        return view;
    }
    public void getWeatherDetailsByLocation(View view, String latlatitude, String longitude) {
        String tempUrl = "";
        String myUnit = (isCelsius ? " °C" : " °F");


        if(latlatitude.equals("") && longitude.equals("")){
            tvResult.setText("Lokasyon Boş Olamaz");
        }else {

            tempUrl = url + "?lat=" + latlatitude + "&lon="+ longitude +"&appid=" + appid+ "&lang=tr&units="+ (isCelsius ? "metric" : "imperial");;


            String finalMyUnit = myUnit;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Log.d("response", response);
                    String output = "";
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                        String description = jsonObjectWeather.getString("description");
                        //tempIconName.setText( );

                        JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                        //kelvin
                        double temp = jsonObjectMain.getDouble("temp");
                        double feelsLike = jsonObjectMain.getDouble("feels_like") ;

                        float pressure = jsonObjectMain.getInt("pressure");
                        int humidity = jsonObjectMain.getInt("humidity");

                        JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                        String wind = jsonObjectWind.getString("speed");
                        JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                        String clouds = jsonObjectClouds.getString("all");
                        JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                        String countryName = jsonObjectSys.getString("country");
                        String cityName = jsonResponse.getString("name");

                        String tempName = df.format(temp) + finalMyUnit;


                        tvResult.setTextSize(20);
                        etTemp.setText(tempName);
                        String urlImage = "http://openweathermap.org/img/wn/"+ jsonObjectWeather.getString("icon") +".png";


                        ImageRequest request = new ImageRequest(urlImage,
                                new Response.Listener<Bitmap>() {
                                    @Override
                                    public void onResponse(Bitmap bitmap) {
                                        // Yanıtı al ve ImageView'a ayarla
                                        ivWeather.setImageBitmap(bitmap);
                                    }
                                }, 0, 0, null,
                                new Response.ErrorListener() {
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(requireActivity(), error.toString().trim(), Toast.LENGTH_LONG).show();
                                    }
                                });

                        RequestQueue requestQueue2 = Volley.newRequestQueue(requireActivity());
                        requestQueue2.add(request);




                        output +=  cityName + " (" + countryName + ") " + " Güncel Hava Durumu"
                                + "\n Sıcaklık: " + df.format(temp) + finalMyUnit
                                + "\n Hissedilen: " + df.format(feelsLike) + finalMyUnit
                                + "\n Nem: %" + humidity
                                + "\n Açıklama: " + description
                                + "\n Rüzgar Hızı: " + wind + "m/s (metre/saniye)"
                                + "\n Bulutlu: %" + clouds
                                + "\n Basınç: " + pressure + "hPa";
                        tvResult.setText(output);

                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            },  new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(requireActivity(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
            requestQueue.add(stringRequest);
        }
    }
    public void getWeatherDetails(View view) {
        String tempUrl = "";
        String myUnit = (isCelsius ? " °C" : " °F");

        String city = etCity.getText().toString().trim();
        String country = etCountry.getText().toString().trim();
        if(city.equals("")){
            tvResult.setText("Şehir boş olamaz!");
        }else {
            if (!country.equals("")){
                tempUrl = url + "?q=" + city + "," + country + "&appid=" + appid + "&lang=tr" + "&units="+ (isCelsius ? "metric" : "imperial");
            }else {
                tempUrl = url + "?q=" + city + "&appid=" + appid + "&lang=tr"+ "&units="+ (isCelsius ? "metric" : "imperial");

            }
            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Log.d("response", response);

                    String output = "";
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                        String description = jsonObjectWeather.getString("description");
                        //tempIconName.setText( );

                        JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                        //kelvin
                        double temp = jsonObjectMain.getDouble("temp");
                        double feelsLike = jsonObjectMain.getDouble("feels_like");

                        float pressure = jsonObjectMain.getInt("pressure");
                        int humidity = jsonObjectMain.getInt("humidity");

                        JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                        String wind = jsonObjectWind.getString("speed");
                        JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                        String clouds = jsonObjectClouds.getString("all");
                        JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                        String countryName = jsonObjectSys.getString("country");
                        String cityName = jsonResponse.getString("name");

                        String tempName = df.format(temp) + myUnit;


                        tvResult.setTextSize(20);
                        etTemp.setText(tempName);
                        String urlImage = "http://openweathermap.org/img/wn/"+ jsonObjectWeather.getString("icon") +".png";
                        //Toast.makeText(requireActivity(), iconWeather[0][0], Toast.LENGTH_LONG).show();

                        ImageRequest request = new ImageRequest(urlImage,
                                new Response.Listener<Bitmap>() {
                                    @Override
                                    public void onResponse(Bitmap bitmap) {
                                        // Yanıtı al ve ImageView'a ayarla

                                        ivWeather.setImageBitmap(bitmap);
                                    }
                                }, 0, 0, null,
                                new Response.ErrorListener() {
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(requireActivity(), error.toString().trim(), Toast.LENGTH_LONG).show();
                                    }
                                });

                        RequestQueue requestQueue2 = Volley.newRequestQueue(requireActivity());
                        requestQueue2.add(request);




                        output +=  cityName + " (" + countryName + ") " + " Güncel Hava Durumu"
                                + "\n Sıcaklık: " + df.format(temp) + myUnit
                                + "\n Hissedilen: " + df.format(feelsLike) + myUnit
                                + "\n Nem: %" + humidity
                                + "\n Açıklama: " + description
                                + "\n Rüzgar Hızı: " + wind + "m/s (metre/saniye)"
                                + "\n Bulutlu: %" + clouds
                                + "\n Basınç: " + pressure + "hPa";
                        tvResult.setText(output);

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            },  new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(requireActivity(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
            requestQueue.add(stringRequest);



        }
    }
    public void initComponents(){

        etCity = requireView().findViewById(R.id.etCity);
        etTemp = requireView().findViewById(R.id.etTemp);
        tempIconName = requireView().findViewById(R.id.tempIconName);
        etCountry = requireView().findViewById(R.id.etCountry);
        tvResult = requireView().findViewById(R.id.tvResult);
        ivWeather = requireView().findViewById(R.id.weather_icon_image_view);
    }


}