package com.example.am;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class LocationManagerActivity extends AppCompatActivity {
    private LocationManager locationManager;
    private CustomLocationProvider locationProvider;
    private GNSSView gnssView;
    private TextView tvGnssInfo;
    private TextView tvLocation;
    private static final int REQUEST_LOCATION = 1;
    private String coordinateFormat = "Graus"; // Formato padrão

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_manager);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        gnssView = findViewById(R.id.gnssView);
        tvGnssInfo = findViewById(R.id.tv_gnssInfo);
        tvLocation = findViewById(R.id.textviewLocation_id); // Referência ao TextView de localização

        obtemLocationProvider_Permission();

        // Configura o ouvinte de clique no TextView de localização
        tvLocation.setOnClickListener(v -> showCoordinateFormatDialog());
    }

    private void showCoordinateFormatDialog() {
        // Diálogo para escolher o formato das coordenadas
        String[] formats = {"Graus", "Graus-Minutos", "Graus-Minutos-Segundos"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolha o Formato das Coordenadas");
        builder.setItems(formats, (dialog, which) -> {
            coordinateFormat = formats[which];
            Toast.makeText(this, "Formato escolhido: " + coordinateFormat, Toast.LENGTH_SHORT).show();
            // Atualiza a posição com o novo formato escolhido
            updateLocationDisplay();
        });
        builder.show();
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

    public void mostraLocation(Location location) {
        String mens = "Dados da Última Posição\n";
        if (location != null) {
            mens += "Latitude=" + convertCoordinates(location.getLatitude()) + "\n" +
                    "Longitude=" + convertCoordinates(location.getLongitude()) + "\n" +
                    "Velocidade=" + location.getSpeed() + " m/s\n" +
                    "Rumo=" + location.getBearing() + "°";
        } else {
            mens += "Localização Não disponível";
        }
        tvLocation.setText(mens);
    }

    private String convertCoordinates(double coordinate) {
        // Conversão do formato de coordenadas
        switch (coordinateFormat) {
            case "Graus":
                return Location.convert(coordinate, Location.FORMAT_DEGREES);
            case "Graus-Minutos":
                return Location.convert(coordinate, Location.FORMAT_MINUTES);
            case "Graus-Minutos-Segundos":
                return Location.convert(coordinate, Location.FORMAT_SECONDS);
            default:
                return String.valueOf(coordinate);
        }
    }
    private String getConstellationType(int constellationType) {
        switch (constellationType) {
            case GnssStatus.CONSTELLATION_GPS:
                return "GPS";
            case GnssStatus.CONSTELLATION_GLONASS:
                return "GLONASS";
            case GnssStatus.CONSTELLATION_BEIDOU:
                return "BEIDOU";
            case GnssStatus.CONSTELLATION_GALILEO:
                return "Galileo";
            case GnssStatus.CONSTELLATION_QZSS:
                return "QZSS";
            default:
                return "Desconhecido";
        }
    }
    public void mostraGNSS(GnssStatus status) {
        gnssView.setGnssStatus(status);

        StringBuilder mens = new StringBuilder("Dados do Sistema GNSS\n");
        if (status != null) {
            mens.append("Número de Satélites: ").append(status.getSatelliteCount()).append("\n\n");
            for (int i = 0; i < status.getSatelliteCount(); i++) {
                // SVID
                mens.append("SVID: ").append(status.getSvid(i)).append("\n");

                // Tipo de constelação
                mens.append("Tipo de Constelação: ").append(getConstellationType(status.getConstellationType(i))).append("\n");

                // Azimute
                mens.append("Azimute: ").append(status.getAzimuthDegrees(i)).append("°\n");

                // Elevação
                mens.append("Elevação: ").append(status.getElevationDegrees(i)).append("°\n");

                // Adiciona uma linha em branco entre os satélites para separá-los
                mens.append("\n");
            }
        } else {
            mens.append("GNSS Não disponível");
        }
        tvGnssInfo.setText(mens.toString());
    }

    // Atualiza a exibição das coordenadas formatadas
    private void updateLocationDisplay() {
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            mostraLocation(location);
        }
    }
}