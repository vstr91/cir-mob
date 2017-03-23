package br.com.vostre.circular;

import android.animation.LayoutTransition;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaColeta;
import br.com.vostre.circular.model.dao.ItinerarioDBHelper;
import br.com.vostre.circular.model.dao.ParadaColetaDBHelper;
import br.com.vostre.circular.model.dao.ParadaDBHelper;
import br.com.vostre.circular.utils.GeofenceTransitionsIntentService;
import br.com.vostre.circular.utils.ItinerarioList;
import br.com.vostre.circular.utils.LatLngInterpolator;
import br.com.vostre.circular.utils.MarkerAnimation;
import br.com.vostre.circular.utils.ModalCadastroParada;
import br.com.vostre.circular.utils.SendParadaService;
import br.com.vostre.circular.utils.ServiceUtils;
import br.com.vostre.circular.utils.ToolbarUtils;

public class MapaConsultaActivity extends BaseActivity implements OnMapReadyCallback,
        ResultCallback<LocationSettingsResult>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,
        GoogleMap.OnCameraChangeListener, GoogleMap.OnMarkerClickListener, SensorEventListener, AdapterView.OnItemClickListener, View.OnClickListener {

    private GoogleMap mMap;
    int permissionGPS;
    int permissionCamera;
    GoogleApiClient googleApiClient;
    Location ultimaLocalizacao;
    LocationRequest lRequest;
    Marker markerLocalAtual;
    Map<Marker, Parada> paradasMarcadores = new HashMap<>();
    List<Geofence> geofenceList = new ArrayList<>();
    PendingIntent geofencePendingIntent;
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

    TextView textViewVelocidade;

    TextView textViewUltimaLoc;
    TextView textViewLocAtual;
    TextView textViewUltimoTempo;
    TextView textViewTempoAtual;
    TextView textViewDistancia;
    TextView textViewTempo;
    TextView textViewVelocidadeLog;
    TextView textViewDiferencaLog;

    ListView listViewItinerarios;

    List<HorarioItinerario> listItinerarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_consulta);

        permissionGPS = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionGPS != PackageManager.PERMISSION_GRANTED){

            finish();

        } else{

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            iniciaClienteGoogle();
//            iniciaGeofence();

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            textViewVelocidade = (TextView) findViewById(R.id.textViewVelocidade);

            textViewUltimaLoc = (TextView) findViewById(R.id.textViewUltimaLoc);
            textViewLocAtual = (TextView) findViewById(R.id.textViewLocAtual);
            textViewUltimoTempo = (TextView) findViewById(R.id.textViewUltimoTempo);
            textViewTempoAtual = (TextView) findViewById(R.id.textViewTempoAtual);
            textViewDistancia = (TextView) findViewById(R.id.textViewDistancia);
            textViewTempo = (TextView) findViewById(R.id.textViewTempo);
            textViewVelocidadeLog = (TextView) findViewById(R.id.textViewVelocidadeLog);
            textViewDiferencaLog = (TextView) findViewById(R.id.textViewDiferencaLog);

            listViewItinerarios = (ListView) findViewById(R.id.listViewItinerarios);

            updateValuesFromBundle(savedInstanceState);

            sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
            sensorAcc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorMagnectic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            mGravity = new float[3];
            mGeomagnetic = new float[3];

            matrixR = new float[9];
            matrixI = new float[9];
            matrixValues = new float[3];

            listViewItinerarios.setOnItemClickListener(this);

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

        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMarkerClickListener(this);

        lRequest = createLocationRequest();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(lRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient,
                        builder.build());

        result.setResultCallback(this);

        if(ultimaLocalizacao != null){
            atualizaMarcadores(mMap, ultimaLocalizacao);
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
                            MapaConsultaActivity.this,
                            211);
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            ultimaLocalizacao = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }

        if (ultimaLocalizacao != null) {
            iniciaMapa();
            atualizaMarcadores(mMap, ultimaLocalizacao);
//            markerLocalAtual = marcaLocalAtual(ultimaLocalizacao);
            //atualizaCamera(ultimaLocalizacao);
        }

        startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if (ultimaLocalizacao.distanceTo(location) > 500){
            atualizaMarcadores(mMap, location);
        }

        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

        if(bounds.contains(new LatLng(ultimaLocalizacao.getLatitude(), ultimaLocalizacao.getLongitude()))){
            atualizaCamera(ultimaLocalizacao, bearing);
        }

 //- Teste Velocidade
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Date ultimoTempo = new Date(ultimaLocalizacao.getTime());
        Date tempoAtual = new Date(location.getTime());

        if(tempoAtual.after(ultimoTempo)){
            long miliDiferencaTempo = location.getTime() - ultimaLocalizacao.getTime();

            long diferencaSegundos = miliDiferencaTempo / DateUtils.SECOND_IN_MILLIS;

            textViewUltimaLoc.setText("Última Localização: "+ultimaLocalizacao.getLatitude()+" | "+ultimaLocalizacao.getLongitude());
            textViewLocAtual.setText("Localização Atual: "+location.getLatitude()+" | "+location.getLongitude());
            textViewUltimoTempo.setText("Último Tempo: "+df.format(ultimoTempo));
            textViewTempoAtual.setText("Tempo Atual: "+df.format(tempoAtual));
            textViewTempo.setText("Tempo: "+miliDiferencaTempo+" | "+DateUtils.formatElapsedTime(miliDiferencaTempo / DateUtils.SECOND_IN_MILLIS));

            textViewDistancia.setText("Distância (m): "+String.valueOf(ultimaLocalizacao.distanceTo(location))
                    +" | Distância (m/s): "+ultimaLocalizacao.distanceTo(location) / diferencaSegundos);

            double speed = ultimaLocalizacao.distanceTo(location) / diferencaSegundos * 3.6;

            speed = speed < 0 || speed > 300 ? 0 : speed;

            textViewVelocidadeLog.setText("Velocidade (Km/h): "+speed);

            ultimaLocalizacao = location;

            atualizaCamera(ultimaLocalizacao, bearing);

//        markerLocalAtual = marcaLocalAtual(ultimaLocalizacao);
            textViewVelocidade.setText(String.valueOf(Math.round(speed)));
        }

    }

    @Override
    public void onCameraChange(CameraPosition position) {
        float maxZoom = 19.5f;
        float minZoom = 15.5f;

        if (position.zoom > maxZoom) {
            mMap.animateCamera(CameraUpdateFactory.zoomTo(maxZoom));
        }

        if (position.zoom < minZoom) {
            mMap.animateCamera(CameraUpdateFactory.zoomTo(minZoom));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Parada umaParada = paradasMarcadores.get(marker);
        //Toast.makeText(getBaseContext(), umaParada.getReferencia(), Toast.LENGTH_LONG).show();
        ItinerarioDBHelper itinerarioDBHelper = new ItinerarioDBHelper(getBaseContext());

        listItinerarios = itinerarioDBHelper.listarTodosPorParada(getBaseContext(), umaParada,
                br.com.vostre.circular.utils.DateUtils.getHoraAtual());

        final ItinerarioList adapterItinerario = new ItinerarioList(this,
                android.R.layout.simple_spinner_dropdown_item, listItinerarios, umaParada);
        adapterItinerario.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
        listViewItinerarios.setAdapter(adapterItinerario);
//        listViewItinerarios.setVisibility(View.VISIBLE);

        return false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        switch(event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                for(int i =0; i < 3; i++){
                    mGravity[i] = event.values[i];
                }
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                for(int i =0; i < 3; i++){
                    mGeomagnetic[i] = event.values[i];
                }
                break;
        }

        boolean success = SensorManager.getRotationMatrix(
                matrixR,
                matrixI,
                mGravity,
                mGeomagnetic);

        if(success){
            SensorManager.getOrientation(matrixR, matrixValues);

            double azimuth = Math.toDegrees(matrixValues[0]);

            WindowManager mWindowManager =  (WindowManager) getSystemService(WINDOW_SERVICE);
            Display mDisplay = mWindowManager.getDefaultDisplay();

            Float degToAdd = 0f;

            if(mDisplay.getRotation() == Surface.ROTATION_0)
                degToAdd = 0.0f;
            if(mDisplay.getRotation() == Surface.ROTATION_90)
                degToAdd = 90.0f;
            if(mDisplay.getRotation() == Surface.ROTATION_180)
                degToAdd = 180.0f;
            if(mDisplay.getRotation() == Surface.ROTATION_270)
                degToAdd = 270.0f;

            float diferenca = (float) (azimuth + degToAdd) - bearing;

            if(diferenca > 15 || diferenca < -15){

                bearing = (float) (azimuth + degToAdd);

                if(ultimaLocalizacao != null){
                    atualizaCamera(ultimaLocalizacao, bearing);
                }

            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void atualizaMarcadores(GoogleMap mMap, Location location){

        if(mMap != null){

            ParadaDBHelper paradaDBHelper = new ParadaDBHelper(this);
            List<Parada> paradas = paradaDBHelper.listarTodosComItinerario(getBaseContext());

            mMap.clear();
            paradasMarcadores.clear();

            for(Parada umaParada : paradas){

                Location parada = new Location(umaParada.getReferencia());
                parada.setLatitude(Double.parseDouble(umaParada.getLatitude()));
                parada.setLongitude(Double.parseDouble(umaParada.getLongitude()));

                if(parada.distanceTo(location) < 500){
                    Marker umMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(umaParada.getLatitude()),
                            Double.parseDouble(umaParada.getLongitude()))).title(umaParada.getReferencia()).draggable(false)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_resumida)).flat(false));

                    paradasMarcadores.put(umMarker, umaParada);
                    criarGeofence(umaParada);
                }

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

    public void iniciaGeofence(){

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            GeofencingRequest geoRequest = getGeofencingRequest();

            if(geoRequest != null){
                LocationServices.GeofencingApi
                        .addGeofences(googleApiClient, getGeofencingRequest(), getGeofencePendingIntent())
                        .setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                Toast.makeText(getBaseContext(), status.getStatusMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }


        }

    }

    public void criarGeofence(Parada umaParada){
        Geofence geofence = new Geofence.Builder().setRequestId(umaParada.getReferencia())
                .setCircularRegion(Double.parseDouble(umaParada.getLatitude()), Double.parseDouble(umaParada.getLongitude()), 100)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT).build();
        geofenceList.add(geofence);
    }

    private GeofencingRequest getGeofencingRequest() {

        if(geofenceList.size() <= 0){
            return null;
        }

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    public void atualizaCamera(Location location, float bearing){
        LatLng localAtual = new LatLng(location.getLatitude(), location.getLongitude());

        if(mMap != null){
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

    public void iniciaMapa(){
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder().target(mMap.getCameraPosition().target)
                        .tilt(45f)
                        .zoom(17.5f)
                        .build()
                )
        );
//        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.setOnCameraChangeListener(this);
        mMap.getUiSettings().setCompassEnabled(true);
        iniciaGeofence();
    }

//    private Marker marcaLocalAtual(Location location){
//
//        LatLngInterpolator mLatLngInterpolator = new LatLngInterpolator.Linear();
//
//
//
//        if(markerLocalAtual != null){
//            MarkerAnimation.animateMarkerToGB(markerLocalAtual, new LatLng(location.getLatitude(), location.getLongitude()), mLatLngInterpolator);
//            //markerLocalAtual.remove();
//        }
//
//        return mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),
//                location.getLongitude())).draggable(false)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_2016_marker)));
//    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {

            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains("location")) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                ultimaLocalizacao = savedInstanceState.getParcelable("location");
            }

        }
    }

    protected void startLocationUpdates() {

        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, lRequest, this);
        }

    }

    public void iniciaClienteGoogle(){
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HorarioItinerario itinerario = listItinerarios.get(position);

        Intent intent = new Intent(getBaseContext(), TodosHorarios.class);
        intent.putExtra("id_partida", itinerario.getItinerario().getPartida().getId());
        intent.putExtra("id_destino", itinerario.getItinerario().getDestino().getId());
        intent.putExtra("itinerario", itinerario.getItinerario().getId());
        intent.putExtra("hora", itinerario.getHorario().toString());
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            default:
                ToolbarUtils.onMenuItemClick(view, this);
                break;
        }

    }

}
