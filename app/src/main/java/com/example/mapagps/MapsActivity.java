package com.example.mapagps;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mapagps.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    //Usar a classe FusedLocationProvider para acessar o GPS do dispositivo
    //Essa classe surgiu no Android é
    private FusedLocationProviderClient servicoLocalizacao;

    //Classe para recuperar a localizacao do dispositivo/usuário
    private Location ultimaPosicao;

    //Variavel para controlar se o usuário permitiu o GPS
    private boolean permitiuGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Requisitae a permissão de localização para o usuário
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                                                PackageManager.PERMISSION_GRANTED){
            //Se a permissão do GPS for diferente de "permitido", então exibimos a
            //tela com mensagem para o usuário escolher novamente
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100); //Código pode ser qualquer número inteiro
            //para controlar, a qualquer momento, e saber qual é a situação da
            //permissão, isto é, permitido ou não
        }else{
            permitiuGPS = true;
        }

        //Iniciar o serviço do GPS
        servicoLocalizacao =
                LocationServices.getFusedLocationProviderClient(this);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        recuperarLocalizacaoAtual();
    }

    //Método para recuperar as atualizações de localização
    private void recuperarLocalizacaoAtual(){
        try {
        //verificar se o GPS já foi permitido
            if (permitiuGPS){
                //Sicronizar com as mudancas de posicao do GPS
                @SuppressLint("MissingPermission") Task novaLocalizacao = servicoLocalizacao.getLastLocation();
                //Toda vez a localização mudar...
                novaLocalizacao.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        //Verificar se a "task" foi completada com sucesso
                        if (task.isSuccessful()) {
                            //Armazenar a posição recuperada
                            ultimaPosicao = (Location) task.getResult();
                            //Testar se posição é um ponto válido
                            if (ultimaPosicao != null){
                                //Se é uma posição válida, podemos pegar os dados de
                                //latitude e longitude e colocar no mapa
                                LatLng posicao = new LatLng(
                                        ultimaPosicao.getLatitude(),
                                        ultimaPosicao.getLongitude());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicao, 15));
                                mMap.addMarker(new MarkerOptions().position(posicao).title("Você está aqui"));
                            }
                        }
                    }
                });
            }
        } catch (SecurityException exc){
            exc.printStackTrace();
        }
    }
}