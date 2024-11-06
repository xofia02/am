package com.example.am;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int REQUEST_LOCATION_UPDATES = 1; // Constante para requisição de permissão
    private FusedLocationProviderClient mFusedLocationProviderClient; // Cliente de localização
    private LocationRequest mLocationRequest; // Configuração de solicitações de localização
    private LocationCallback mLocationCallback; // Callback para receber as atualizações de localização

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Inicializa o cliente de localização
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Configura o pedido de atualizações de localização
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000); // Intervalo de 10 segundos para cada atualização
        mLocationRequest.setFastestInterval(5000); // Intervalo mínimo de 5 segundos
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Inicializa o callback que recebe as atualizações de localização
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Atualiza a posição no mapa com a última localização
                    updateMapLocation(location);
                }
            }
        };

        // Solicita permissão de localização, se necessário
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_UPDATES);
        } else {
            // Inicia as atualizações de localização se a permissão já foi concedida
            startLocationUpdates();
        }

        // Obtém o SupportMapFragment e notifica quando o mapa estiver pronto para uso
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    // Inicia as atualizações de localização
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    // Atualiza a posição no mapa com a localização mais recente
    private void updateMapLocation(Location location) {
        if (mMap != null) {
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear(); // Limpa o mapa de marcadores anteriores
            mMap.addMarker(new MarkerOptions().position(userLocation).title("Você está aqui"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        }
    }

    // Interrompe as atualizações de localização
    private void stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);

        UiSettings mapUI = mMap.getUiSettings();
        mapUI.setAllGesturesEnabled(true);
        mapUI.setCompassEnabled(true);
        mapUI.setZoomControlsEnabled(true);

        // Adiciona um marcador na UCSAL e centraliza a câmera
        LatLng ucsalLocation = new LatLng(-12.94825, -38.41334);
        mMap.addMarker(new MarkerOptions().position(ucsalLocation).title("UCSAL").snippet("Campus Pituaçu"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ucsalLocation, 15));
    }

    // Trata o resultado da solicitação de permissão
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_UPDATES) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Permissão de localização negada.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Interrompe as atualizações de localização quando a Activity não está visível
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Retoma as atualizações de localização quando a Activity fica visível novamente
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }
}