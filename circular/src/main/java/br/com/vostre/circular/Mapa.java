package br.com.vostre.circular;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.cardiomood.android.controls.gauge.SpeedometerGauge;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Local;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaColeta;
import br.com.vostre.circular.model.dao.LocalDBHelper;
import br.com.vostre.circular.model.dao.ParadaColetaDBHelper;
import br.com.vostre.circular.model.dao.ParadaDBHelper;
import br.com.vostre.circular.utils.CircularInfoWindow;
import br.com.vostre.circular.utils.FileUtils;
import br.com.vostre.circular.utils.LatLngInterpolator;
import br.com.vostre.circular.utils.ListviewComFiltro;
import br.com.vostre.circular.utils.MarkerAnimation;
import br.com.vostre.circular.utils.ModalCadastroListener;
import br.com.vostre.circular.utils.ModalCadastroParada;
import br.com.vostre.circular.utils.ModalMapMarker;
import br.com.vostre.circular.utils.SendMessageService;
import br.com.vostre.circular.utils.SendParadaService;
import br.com.vostre.circular.utils.ServiceUtils;
import br.com.vostre.circular.utils.ToolbarUtils;


public class Mapa extends BaseActivity implements OnMapReadyCallback,
        ResultCallback<LocationSettingsResult>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,
        GoogleMap.OnCameraChangeListener, GoogleMap.OnMarkerClickListener, SensorEventListener, View.OnClickListener, ModalCadastroListener,
        GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private Button btnSalvar;
    private Button btnEnviarParadas;

    int permissionGPS;
    int permissionCamera;

    GoogleApiClient googleApiClient;
    Location ultimaLocalizacao;
    LocationRequest lRequest;
    //Marker markerLocalAtual;

    float[] mGravity;
    float[] mGeomagnetic;
    SensorManager sensorManager;
    Sensor sensorAcc;
    Sensor sensorMagnectic;

    private float[] matrixR;
    private float[] matrixI;
    private float[] matrixValues;

    SupportMapFragment mapFragment;
    float bearing;
    Map<Marker, Parada> paradasValidadas = new HashMap<>();
    Map<Marker, ParadaColeta> paradasCadastradas = new HashMap<>();

    Map<ParadaColeta, Marker> paradasCadastradasMarker = new HashMap<>();

    Location ultimaLocalizacaoAtualizada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mapa);

        permissionGPS = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissionCamera = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA);

        if (permissionGPS != PackageManager.PERMISSION_GRANTED || permissionCamera != PackageManager.PERMISSION_GRANTED) {

            finish();

        } else {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            //FileUtils.exportDatabase("circular.db", "circular-exp", this);

            btnSalvar = (Button) findViewById(R.id.buttonSalvar);
            btnEnviarParadas = (Button) findViewById(R.id.btnEnviarParadas);

            btnSalvar.setOnClickListener(this);
            btnEnviarParadas.setOnClickListener(this);

            sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
            sensorAcc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorMagnectic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            mGravity = new float[3];
            mGeomagnetic = new float[3];

            matrixR = new float[9];
            matrixI = new float[9];
            matrixValues = new float[3];

            iniciaClienteGoogle();

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_mapa, menu);

        ToolbarUtils.preparaMenu(menu, this, this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent;

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            /*case R.id.icon_config:
                intent = new Intent(this, Parametros.class);
                startActivity(intent);
                break;
            case R.id.icon_sobre:
                intent = new Intent(this, Sobre.class);
                startActivity(intent);
                break;*/
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Parada umaParada = (Parada) paradasCadastradas.get(marker);

        if(umaParada == null){
            umaParada = paradasValidadas.get(marker);
        }

        if(umaParada.getStatus() != 3){

            if(umaParada.getStatus() == 4){
                Toast.makeText(getApplicationContext(), "Esta parada já foi enviada para e servidor e não pode mais ser editada.", Toast.LENGTH_SHORT).show();
            }

            return false;
        } else{

            if (umaParada != null) {

                //if(marker.isInfoWindowShown()){
                marker.hideInfoWindow();
                //}

                ModalMapMarker modalMapMarker = new ModalMapMarker();
                modalMapMarker.setParadaEscolhida(umaParada);
                modalMapMarker.setListener(this);
                modalMapMarker.show(getSupportFragmentManager(), "modalMarker");

                return true;

            } else {
                return false;
            }

        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.buttonSalvar:
                Location loc = ultimaLocalizacao;

                ModalCadastroParada modalCadastroParada = new ModalCadastroParada();

                modalCadastroParada.setLatitude(loc.getLatitude());
                modalCadastroParada.setLongitude(loc.getLongitude());
                modalCadastroParada.setMap(mMap);
                modalCadastroParada.setListener(this);

                modalCadastroParada.show(getSupportFragmentManager(), "modal");
                break;
            case R.id.btnEnviarParadas:

                ParadaColetaDBHelper paradaColetaDBHelper = new ParadaColetaDBHelper(getBaseContext());
                List<ParadaColeta> paradas = paradaColetaDBHelper.listarTodosNaoEnviados(getBaseContext());

                int qtdParadas = paradas.size();

                if(qtdParadas > 0){

                    Intent serviceIntent = new Intent(getBaseContext(), SendParadaService.class);

                    final ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

                    final ServiceUtils serviceUtils = new ServiceUtils();

                    if(!serviceUtils.isMyServiceRunning(SendParadaService.class, manager)){
                        stopService(serviceIntent);
                        startService(serviceIntent);
                    }

                    Toast.makeText(getApplicationContext(), "As paradas cadastradas foram enviadas para análise. Muito obrigado pela contribuição!", Toast.LENGTH_LONG).show();
                    bloqueiaParaEdicao();
                } else{
                    Toast.makeText(getApplicationContext(), "Não existem no momento paradas aguardando envio.", Toast.LENGTH_SHORT).show();
                }



                break;
            default:
                ToolbarUtils.onMenuItemClick(view, this);
                break;
        }

    }

    @Override
    public void onModalCadastroDismissed(int resultado) {

        if (ultimaLocalizacao != null) {
            atualizaMarcadores(mMap, ultimaLocalizacao);
            //markerLocalAtual = marcaLocalAtual(ultimaLocalizacao);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            ultimaLocalizacao = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }

        if (ultimaLocalizacao != null) {
            iniciaMapa();
            atualizaMarcadores(mMap, ultimaLocalizacao);
            //markerLocalAtual = marcaLocalAtual(ultimaLocalizacao);
        }

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if(ultimaLocalizacaoAtualizada != null){

            if (ultimaLocalizacaoAtualizada.distanceTo(location) > 500) {
                atualizaMarcadores(mMap, location);
                ultimaLocalizacaoAtualizada = location;
            }

        } else{
            atualizaMarcadores(mMap, location);
            ultimaLocalizacaoAtualizada = location;
        }



        ultimaLocalizacao = location;

        //localizacaoAtual = location;

        //marcaLocalAtual(location);

    }

    @Override
    public void onCameraChange(CameraPosition position) {
        float maxZoom = 19.5f;
        float minZoom = 17.5f;

        if (position.zoom > maxZoom) {
            mMap.animateCamera(CameraUpdateFactory.zoomTo(maxZoom));
        } else if (position.zoom < minZoom) {
            mMap.animateCamera(CameraUpdateFactory.zoomTo(minZoom));
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        lRequest = createLocationRequest();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(lRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient,
                        builder.build());

        result.setResultCallback(this);

        if (ultimaLocalizacao != null) {
            atualizaMarcadores(mMap, ultimaLocalizacao);
            atualizaCamera(ultimaLocalizacao, bearing);
        }
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult result) {
        final Status status = result.getStatus();
        final LocationSettingsStates lss = result.getLocationSettingsStates();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                // All location settings are satisfied. The client can
                // initialize location requests here.

                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    status.startResolutionForResult(
                            Mapa.this,
                            212);
                } catch (IntentSender.SendIntentException e) {
                    // Ignore the error.
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are not satisfied. However, we have no way
                // to fix the settings so we won't show the dialog.

                break;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                for (int i = 0; i < 3; i++) {
                    mGravity[i] = event.values[i];
                }
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                for (int i = 0; i < 3; i++) {
                    mGeomagnetic[i] = event.values[i];
                }
                break;
        }

        boolean success = SensorManager.getRotationMatrix(
                matrixR,
                matrixI,
                mGravity,
                mGeomagnetic);

        if (success) {
            SensorManager.getOrientation(matrixR, matrixValues);

            double azimuth = Math.toDegrees(matrixValues[0]);

            WindowManager mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display mDisplay = mWindowManager.getDefaultDisplay();

            Float degToAdd = 0f;

            if (mDisplay.getRotation() == Surface.ROTATION_0)
                degToAdd = 0.0f;
            if (mDisplay.getRotation() == Surface.ROTATION_90)
                degToAdd = 90.0f;
            if (mDisplay.getRotation() == Surface.ROTATION_180)
                degToAdd = 180.0f;
            if (mDisplay.getRotation() == Surface.ROTATION_270)
                degToAdd = 270.0f;

            float diferenca = (float) (azimuth + degToAdd) - bearing;

            if (diferenca > 5 || diferenca < -5) {

                bearing = (float) (azimuth + degToAdd);

                if (ultimaLocalizacao != null) {
                    atualizaCamera(ultimaLocalizacao, bearing);
                }

            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable("location", ultimaLocalizacao);
        super.onSaveInstanceState(savedInstanceState);
    }

    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onResume() {
        sensorManager.registerListener(this, sensorAcc, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorMagnectic, SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this, sensorAcc);
        sensorManager.unregisterListener(this, sensorMagnectic);
        super.onPause();
    }

    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    public void atualizaMarcadores(GoogleMap mMap, Location location) {

        if (mMap != null) {
            mMap.clear();
            atualizaMarcadoresValidados(location);
            atualizaMarcadoresCadastrados(location);
        }

    }

    private void atualizaMarcadoresValidados(Location location){
        ParadaDBHelper paradaDBHelper = new ParadaDBHelper(this);
        List<Parada> paradasVal = paradaDBHelper.listarTodosComItinerario(getBaseContext());

        paradasValidadas.clear();

        for (Parada umaParada : paradasVal) {

            Location parada = new Location(umaParada.getReferencia());
            parada.setLatitude(Double.parseDouble(umaParada.getLatitude()));
            parada.setLongitude(Double.parseDouble(umaParada.getLongitude()));

            if (parada.distanceTo(location) < 500) {
                Marker umMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(umaParada.getLatitude()),
                        Double.parseDouble(umaParada.getLongitude()))).title(umaParada.getReferencia()).draggable(false)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_2016_marker)).flat(false));

                paradasValidadas.put(umMarker, umaParada);
            }

        }

    }

    private void atualizaMarcadoresCadastrados(Location location){

        ParadaColetaDBHelper paradaColetaDBHelper = new ParadaColetaDBHelper(this);
        List<ParadaColeta> paradasCad = paradaColetaDBHelper.listarTodos(getBaseContext());

        paradasCadastradas.clear();

        for (ParadaColeta umaParada : paradasCad) {

            Location parada = new Location(umaParada.getReferencia());
            parada.setLatitude(Double.parseDouble(umaParada.getLatitude()));
            parada.setLongitude(Double.parseDouble(umaParada.getLongitude()));

            if (parada.distanceTo(location) < 500) {

                Marker umMarker;

                if(umaParada.getStatus() == 3){
                    umMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(umaParada.getLatitude()),
                            Double.parseDouble(umaParada.getLongitude()))).title(umaParada.getReferencia()).draggable(false));
                } else{
                    umMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(umaParada.getLatitude()),
                            Double.parseDouble(umaParada.getLongitude()))).title(umaParada.getReferencia()).draggable(false)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                }



                paradasCadastradas.put(umMarker, umaParada);
                paradasCadastradasMarker.put(umaParada, umMarker);
            }

        }
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    public void atualizaCamera(Location location, float bearing) {
        LatLng localAtual = new LatLng(location.getLatitude(), location.getLongitude());

        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder().target(localAtual)
                            .tilt(45f)
                            .bearing(bearing)
                            .zoom(mMap.getCameraPosition().zoom)
                            .build()
                    )
            );
        }

    }

    public void iniciaMapa() {
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder().target(mMap.getCameraPosition().target)
                        //.tilt(45f)
                        .zoom(17.5f)
                        .build()
                )
        );
        mMap.setOnCameraChangeListener(this);
        mMap.getUiSettings().setCompassEnabled(true);

        if(ultimaLocalizacao != null){
            atualizaCamera(ultimaLocalizacao, bearing);
        }

    }

//    private Marker marcaLocalAtual(Location location) {
//
//        LatLngInterpolator mLatLngInterpolator = new LatLngInterpolator.Linear();
//
//
//        if (markerLocalAtual != null) {
//            MarkerAnimation.animateMarkerToGB(markerLocalAtual, new LatLng(location.getLatitude(), location.getLongitude()), mLatLngInterpolator);
//            //markerLocalAtual.remove();
//        }
//
//        return mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),
//                location.getLongitude())).draggable(false)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_2016_marker)));
//    }

    protected void startLocationUpdates() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, lRequest, this);
        }

    }

    public void iniciaClienteGoogle() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void bloqueiaParaEdicao(){

        int total = paradasCadastradas.size();
        List<ParadaColeta> paradas = new ArrayList<>(paradasCadastradas.values());

        for(int i = 0; i < total; i++){
            paradas.get(i).setStatus(4);
        }

        int totalMarker = paradasCadastradasMarker.size();
        List<Marker> markers = new ArrayList<>(paradasCadastradasMarker.values());

        for(int i = 0; i < totalMarker; i++){
            markers.get(i).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        }

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        ModalCadastroParada modalCadastroParada = new ModalCadastroParada();

        modalCadastroParada.setLatitude(latLng.latitude);
        modalCadastroParada.setLongitude(latLng.longitude);
        modalCadastroParada.setMap(mMap);
        modalCadastroParada.setListener(this);

        modalCadastroParada.show(getSupportFragmentManager(), "modal");
    }
}