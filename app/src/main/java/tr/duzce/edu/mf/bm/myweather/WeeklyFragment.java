package tr.duzce.edu.mf.bm.myweather;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tr.duzce.edu.mf.bm.myweather.Model.City;
import tr.duzce.edu.mf.bm.myweather.database.AppDatabase;


public class WeeklyFragment extends Fragment {
    private ExecutorService executorService;
    //lokasyon bilgisinin preference değişkenleri
    private static final String PREFERENCES_NAME = "my_preferences";
    private static final String PREFERENCE_LATITUDE = "latitude";
    private static final String PREFERENCE_LONGITUDE = "longitude";


    //api url ve key bilgisi
    private final String url = "https://api.openweathermap.org/data/2.5/forecast";
    private final String appid = ":) api key";
    DecimalFormat df = new DecimalFormat("#");
    private boolean isCelsius;
    private boolean isDarkTheme;
    private RecyclerView recyclerView;
    private WeatherAdapter adapter;
    private AppDatabase db;
    private Spinner spinnerCities;
    private String selectedCity;
    private List<City> cities;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        executorService = Executors.newSingleThreadExecutor();


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly, container, false);


        FloatingActionButton fabAddCity = view.findViewById(R.id.fab_add_city);
        fabAddCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCityDialog();
            }
        });

        db = Room.databaseBuilder(requireContext(), AppDatabase.class, "city-database").build();

        loadCities();

        spinnerCities = view.findViewById(R.id.spinner_cities);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new WeatherAdapter();
        recyclerView.setAdapter(adapter);



        return view;
    }
    private void openCityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Yeni Şehir Ekle");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Ekle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String cityName = input.getText().toString();
                addCity(cityName);
                loadCities();
            }
        });
        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void addCity(final String cityName) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<City> existingCities = db.cityDao().getAllCities();
                for (City city : existingCities) {
                    if (city.cityName.equals(cityName)) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Bu şehir zaten var!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                }

                City city = new City();
                city.cityName = cityName;
                db.cityDao().insert(city);
                loadCities();
            }
        });
    }

    private void loadCities(){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                cities = db.cityDao().getAllCities();
                if (cities.size() > 0) {
                    getActivity().runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    setupSpinner(cities);
                                }
                            }
                    );
                }
            }
        });
    }

    private void setupSpinner(List<City> cities){
        List<String> cityNames = new ArrayList<>();
        cityNames.add("Şehir Seçiniz");
        for (City city : cities) {
            cityNames.add(city.cityName);
        }
        ArrayAdapter<String> adapterCities = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, cityNames);
        adapterCities.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCities.setAdapter(adapterCities);
        spinnerCities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // Eğer ilk eleman seçilmişse
                    selectedCity = null; // selectedCity'yi null yap
                } else {
                    selectedCity = (String) parent.getItemAtPosition(position);
                }
                //Toast.makeText((WeeklyFragment.this.getContext()), "Seçili şehir: " + selectedCity, Toast.LENGTH_SHORT).show();
                adapter = new WeatherAdapter();
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private class WeatherAdapter extends RecyclerView.Adapter<WeatherViewHolder> {
        @NonNull
        @Override
        public WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            return new WeatherViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {

                    //preference den koordinat çek
                    SharedPreferences preferences = requireActivity().getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
                    String latitude = preferences.getString(PREFERENCE_LATITUDE, "0");
                    String longitude = preferences.getString(PREFERENCE_LONGITUDE, "0");


                    isCelsius = preferences.getBoolean("isCelsius", true);
                    isDarkTheme = preferences.getBoolean("isDarkTheme", false);

                    String tempUrl="";
                    String myUnit = isCelsius ? " °C" : " °F";
                    // API isteğini oluştur
                    if(selectedCity==null){
                        tempUrl = url + "?lat=" + latitude + "&lon=" + longitude + "&appid=" + appid+ "&lang=tr&units=" + (isCelsius ? "metric" : "imperial");
                    }else{
                        tempUrl = url + "?q=" + selectedCity +"&appid=" + appid+ "&lang=tr&units=" + (isCelsius ? "metric" : "imperial");
                    }
                    // API isteğini gönder ve yanıtı ayrıştır
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, tempUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                            int chunkCount = response.length() / 4000;  // Her bir parçanın maksimum boyutu
//
//                            for (int i = 0; i <= chunkCount; i++) {
//                                int start = i * 4000;
//                                int end = (i+1) * 4000;
//                                end = end > response.length() ? response.length() : end;
//
//                                Log.d("Response", response.substring(start, end));
//                            }
                            try {

                                JSONObject jsonResponse = new JSONObject(response);
                                JSONArray jsonArray = jsonResponse.getJSONArray("list");

                                // İlk 5 günlük hava durumu için döngü
                                for (int i = 0; i < 40; i ++) {
                                    JSONObject jsonObjectDay = jsonArray.getJSONObject(i);
                                    String dateText = jsonObjectDay.getString("dt_txt");
                                    JSONObject jsonObjectMain = jsonObjectDay.getJSONObject("main");
                                    double temp = jsonObjectMain.getDouble("temp");
                                    JSONArray jsonArrayWeather = jsonObjectDay.getJSONArray("weather");
                                    JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                                    String description = jsonObjectWeather.getString("description");
                                    description = Character.toUpperCase(description.charAt(0)) + description.substring(1);
                                    String iconCode = jsonObjectWeather.getString("icon");
                                    String urlImage = "http://openweathermap.org/img/wn/"+ iconCode +".png";

                                    // Ana iş parçacığına geri dön ve görünümleri güncelle
                                    int finalI = i;
                                    String finalDescription = description;
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            int position = holder.getAdapterPosition();
                                            if (position != RecyclerView.NO_POSITION && finalI/8 == position) {
                                                //apiya istek at ve resmi çek
                                                ImageRequest request = new ImageRequest(urlImage,
                                                        new Response.Listener<Bitmap>() {
                                                            @Override
                                                            public void onResponse(Bitmap bitmap) {
                                                                // Yanıtı al ve ImageView'a ayarla
                                                                holder.weatherIconImageView.setImageBitmap(bitmap);
                                                            }
                                                        }, 0, 0, null,
                                                        new Response.ErrorListener() {
                                                            public void onErrorResponse(VolleyError error) {
                                                                Toast.makeText(requireActivity(), error.toString().trim(), Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                                RequestQueue requestQueue2 = Volley.newRequestQueue(requireActivity());
                                                requestQueue2.add(request);

                                                //gün bilgilerini ve hava durumunu güncelle
                                                holder.dayTextView.setText(dateText);
                                                holder.weatherInfoTextView.setText(finalDescription + ", " + df.format(temp) + myUnit);
                                            }
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Hata durumunda yapılacak işlemler
                            Toast.makeText(getContext(), "Hata: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    requestQueue.add(stringRequest);
                }
            });
        }

        @Override
        public int getItemCount() {
            return 5;
        }


    }
    
    private class WeatherViewHolder extends RecyclerView.ViewHolder {
        TextView dayTextView;
        ImageView weatherIconImageView;
        TextView weatherInfoTextView;

        public WeatherViewHolder(View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.tv_day);
            weatherIconImageView = itemView.findViewById(R.id.iv_weather_icon);
            weatherInfoTextView = itemView.findViewById(R.id.tv_weather_info);
        }
    }

}
