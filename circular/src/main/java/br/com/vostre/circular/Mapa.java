package br.com.vostre.circular;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.ParseException;
import java.util.List;

import br.com.vostre.circular.model.ParadaColeta;
import br.com.vostre.circular.model.dao.ParadaColetaDBHelper;
import br.com.vostre.circular.utils.ModalCadastroParada;


public class Mapa extends ActionBarActivity {

    private GoogleMap mMap;
    private Button btnSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParadaColetaDBHelper paradaColetaDBHelper = new ParadaColetaDBHelper(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_mapa);

        btnSalvar = (Button) findViewById(R.id.buttonSalvar);

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Location loc = mMap.getMyLocation();

                Toast.makeText(view.getContext(), "Posição: " + loc.getLatitude() + ";" + loc.getLongitude(), Toast.LENGTH_SHORT).show();
                System.out.println("Posição: "+loc.getLatitude()+";"+loc.getLongitude());

                ModalCadastroParada modalCadastroParada = new ModalCadastroParada();

                modalCadastroParada.setLatitude(loc.getLatitude());
                modalCadastroParada.setLongitude(loc.getLongitude());
                modalCadastroParada.setMap(mMap);

                modalCadastroParada.show(getSupportFragmentManager(), "modal");

            }
        });

        setUpMapIfNeeded();

        try {
            List<ParadaColeta> paradasColetadas = paradaColetaDBHelper.listarTodos(this);

            for(ParadaColeta umaParada : paradasColetadas){
                mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(umaParada.getLatitude()),
                        Double.parseDouble(umaParada.getLongitude()))).title(umaParada.getReferencia()).draggable(false));
            }

        } catch (ParseException e) {
            Toast.makeText(this, "Erro ao carregar paradas coletadas.", Toast.LENGTH_LONG).show();
        }



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mapa, menu);
        return true;
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
            case R.id.icon_config:
                intent = new Intent(this, Parametros.class);
                startActivity(intent);
                break;
            case R.id.icon_sobre:
                intent = new Intent(this, Sobre.class);
                startActivity(intent);
                break;
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
        mMap.setMyLocationEnabled(true);

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
            }
        });

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.moveCamera(CameraUpdateFactory.zoomTo(18));
            }
        });

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
}
