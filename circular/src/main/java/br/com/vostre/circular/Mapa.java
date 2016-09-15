package br.com.vostre.circular;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.cardiomood.android.controls.gauge.SpeedometerGauge;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Local;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaColeta;
import br.com.vostre.circular.model.dao.LocalDBHelper;
import br.com.vostre.circular.model.dao.ParadaColetaDBHelper;
import br.com.vostre.circular.model.dao.ParadaDBHelper;
import br.com.vostre.circular.utils.CircularInfoWindow;
import br.com.vostre.circular.utils.FileUtils;
import br.com.vostre.circular.utils.ListviewComFiltro;
import br.com.vostre.circular.utils.ModalCadastroListener;
import br.com.vostre.circular.utils.ModalCadastroParada;
import br.com.vostre.circular.utils.ModalMapMarker;
import br.com.vostre.circular.utils.ToolbarUtils;


public class Mapa extends BaseActivity implements GoogleMap.OnMarkerClickListener, View.OnClickListener, ModalCadastroListener {

    private GoogleMap mMap;
    //private Button btnSalvar;
    private HashMap<Marker, ParadaColeta> marcadores;
    //private SpeedometerGauge velocimetro;

    List<ParadaColeta> paradasColetadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mapa);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //FileUtils.exportDatabase("circular.db", "circular-exp", this);

        //btnSalvar = (Button) findViewById(R.id.buttonSalvar);

        //btnSalvar.setOnClickListener(this);

        setUpMapIfNeeded();

        carregaMarcadoresPendentes();
        carregaMarcadoresValidados();

        //velocimetro = (SpeedometerGauge) findViewById(R.id.velocimetro);

        // Add label converter
//        velocimetro.setLabelConverter(new SpeedometerGauge.LabelConverter() {
//            @Override
//            public String getLabelFor(double progress, double maxProgress) {
//                return String.valueOf((int) Math.round(progress));
//            }
//        });

        // configure value range and ticks
//        velocimetro.setMaxSpeed(300);
//        velocimetro.setMajorTickStep(30);
//        velocimetro.setMinorTicks(2);
//
//        // Configure value range colors
//        velocimetro.addColoredRange(30, 140, Color.GREEN);
//        velocimetro.addColoredRange(140, 180, Color.YELLOW);
//        velocimetro.addColoredRange(180, 400, Color.RED);

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

        switch (id){
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

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        // mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        //mMap.setMyLocationEnabled(true);

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                double velocidade = location.getSpeed();
                //velocimetro.setSpeed(velocidade, true);
            }
        });

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.moveCamera(CameraUpdateFactory.zoomTo(18));
            }
        });

        mMap.setOnMarkerClickListener(this);
        //mMap.setInfoWindowAdapter(new CircularInfoWindow(this));

    }

    public void carregaMarcadoresValidados(){
        ParadaDBHelper paradaDBHelper = new ParadaDBHelper(this);
        List<Parada> paradasValidadas = paradaDBHelper.listarTodos(this);

        for(Parada umaParada : paradasValidadas){
            mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(umaParada.getLatitude()),
                    Double.parseDouble(umaParada.getLongitude()))).title(umaParada.getReferencia()).draggable(false)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_resumida)));
        }
    }

    public void carregaMarcadoresPendentes(){
        ParadaColetaDBHelper paradaColetaDBHelper = new ParadaColetaDBHelper(this);
        marcadores = new HashMap<>();
        try {
            paradasColetadas = paradaColetaDBHelper.listarTodos(this);

            for(ParadaColeta umaParada : paradasColetadas){
                Marker m = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(umaParada.getLatitude()),
                        Double.parseDouble(umaParada.getLongitude()))).title(umaParada.getReferencia()).draggable(false));
                marcadores.put(m, umaParada);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        //super.onDestroy();
        /*if (mMap != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(getSupportFragmentManager().findFragmentById(R.id.map)).commit();
            mMap = null;
        }*/
        super.onDestroy();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        ParadaColeta umaParada = marcadores.get(marker);

        if(umaParada != null){

            //if(marker.isInfoWindowShown()){
                marker.hideInfoWindow();
            //}

            ModalMapMarker modalMapMarker = new ModalMapMarker();
            modalMapMarker.setParadaEscolhida(umaParada);
            modalMapMarker.setListener(this);
            modalMapMarker.show(getSupportFragmentManager(), "modalMarker");

            return true;

        } else{
            return false;
        }


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.buttonSalvar:
                Location loc = mMap.getMyLocation();

                ModalCadastroParada modalCadastroParada = new ModalCadastroParada();

                modalCadastroParada.setLatitude(loc.getLatitude());
                modalCadastroParada.setLongitude(loc.getLongitude());
                modalCadastroParada.setMap(mMap);
                modalCadastroParada.setListener(this);

                modalCadastroParada.show(getSupportFragmentManager(), "modal");
                break;
            case 1:

                ParadaColetaDBHelper paradaColetaDBHelper = new ParadaColetaDBHelper(getBaseContext());
                try {
                    List<ParadaColeta> paradas = paradaColetaDBHelper.listarTodos(getBaseContext());
                    String paradasJson = "{\"paradas\":[";

                    int qtdParadas = paradas.size();
                    int contador = 1;

                    for(ParadaColeta parada : paradas){

                        if(contador == qtdParadas){
                            paradasJson = paradasJson.concat(parada.toJson());
                        } else{
                            paradasJson = paradasJson.concat(parada.toJson()+",");
                        }

                        contador++;

                    }

                    paradasJson = paradasJson.concat("]}");

                    System.out.println(paradasJson);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            default:
                ToolbarUtils.onMenuItemClick(view, this);
                break;
        }

    }

    @Override
    public void onModalCadastroDismissed(int resultado) {

        mMap.clear();
        carregaMarcadoresPendentes();
        carregaMarcadoresValidados();

    }

}