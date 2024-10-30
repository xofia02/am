package com.example.am;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class LocationManagerActivity extends AppCompatActivity {
    private LocationManager locationManager;
    private CustomLocationProvider locationProvider;
    private GNSSView gnssView; // Referência à view GNSS
    private TextView tvGnssInfo;
    private static final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_manager);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        gnssView = findViewById(R.id.gnssView);
        tvGnssInfo = findViewById(R.id.tv_gnssInfo); // Inicializando a view dos dados GNSS
        obtemLocationProvider_Permission();
    }

    public void obtemLocationProvider_Permission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            locationProvider = new CustomLocationProvider(locationManager);
            startLocationAndGNSSUpdates();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtemLocationProvider_Permission();
            } else {
                Toast.makeText(this, "Sem permissão para acessar o sistema de posicionamento",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void startLocationAndGNSSUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(locationProvider.getName(), 1000, 0.1f,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        mostraLocation(location);
                    }
                });

        locationManager.registerGnssStatusCallback(new GnssStatus.Callback() {
            @Override
            public void onSatelliteStatusChanged(@NonNull GnssStatus status) {
                super.onSatelliteStatusChanged(status);
                mostraGNSS(status);
            }
        });
    }

    // Exibindo os dados de localização
    public void mostraLocation(Location location) {
        TextView textView = findViewById(R.id.textviewLocation_id);
        String mens = "Dados da Última Posição\n";
        if (location != null) {
            mens += "Latitude(graus)=" +
                    Location.convert(location.getLatitude(), Location.FORMAT_SECONDS) + "\n" +
                    "Longitude(graus)=" +
                    Location.convert(location.getLongitude(), Location.FORMAT_SECONDS) + "\n" +
                    "Velocidade(m/s)=" + location.getSpeed() + "\n" +
                    "Rumo(graus)=" + location.getBearing();
        } else {
            mens += "Localização Não disponível";
        }
        textView.setText(mens);
    }

    // Exibindo os dados do sistema GNSS
    public void mostraGNSS(GnssStatus status) {
        // Atualizando a GNSSView
        gnssView.setGnssStatus(status);

        // Atualizando as informações de satélites no TextView
        StringBuilder mens = new StringBuilder("Dados do Sistema GNSS\n");
        if (status != null) {
            mens.append("Número de Satélites: ").append(status.getSatelliteCount()).append("\n");
            for (int i = 0; i < status.getSatelliteCount(); i++) {
                mens.append("SVID=").append(status.getSvid(i))
                        .append(" - Tipo=").append(status.getConstellationType(i))
                        .append(" Azimute=").append(status.getAzimuthDegrees(i))
                        .append(" Elevação=").append(status.getElevationDegrees(i))
                        .append(" | ");
            }
        } else {
            mens.append("GNSS Não disponível");
        }
        tvGnssInfo.setText(mens.toString());
    }
}