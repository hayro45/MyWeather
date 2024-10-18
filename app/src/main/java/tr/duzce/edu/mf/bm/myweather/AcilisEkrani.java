package tr.duzce.edu.mf.bm.myweather;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


public class AcilisEkrani extends AppCompatActivity {


    private FusedLocationProviderClient fusedLocationProviderClient;

    //lokasyon izni kodu
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    //preference mekanizması ile kaydedilen değerler
    private static final String PREFERENCES_NAME = "my_preferences";
    private static final String PREFERENCE_LATITUDE = "latitude";
    private static final String PREFERENCE_LONGITUDE = "longitude";
    private static final String PREFERENCE_FIRST_LAUNCH = "first_launch";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        if (!preferences.getBoolean(PREFERENCE_FIRST_LAUNCH, true)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_acilis_ekrani);



        LocationRequest locationRequest = new LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, 100)
                .setWaitForAccurateLocation(false)
                .setMinUpdateDistanceMeters(2000)
                .setMaxUpdateDelayMillis(100).build();

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
            }
        };

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }

    public void buttonGetLocation(View view) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(PREFERENCE_LATITUDE, String.valueOf(location.getLatitude()));
                    editor.putString(PREFERENCE_LONGITUDE, String.valueOf(location.getLongitude()));
                    editor.putBoolean(PREFERENCE_FIRST_LAUNCH, false);
                    editor.putBoolean("isCelsius", true); // Varsayılan olarak Celsius seçili
                    editor.putBoolean("isDarkTheme", false); // Varsayılan olarak karanlık tema kapalı
                    editor.apply();

                    Intent intent = new Intent(AcilisEkrani.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // İzin verildi, konum alabilirsiniz.
                buttonGetLocation(null);
            } else {
                // İzin verilmedi, kullanıcıya bir açıklama gösterin.
                Toast.makeText(this, "Konum izni gereklidir.", Toast.LENGTH_LONG).show();
            }
        }
    }

}
