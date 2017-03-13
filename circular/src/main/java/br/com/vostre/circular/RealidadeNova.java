package br.com.vostre.circular;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.plugin.radar.RadarView;
import com.beyondar.android.plugin.radar.RadarWorldPlugin;
import com.beyondar.android.util.location.BeyondarLocationManager;
import com.beyondar.android.view.OnClickBeyondarObjectListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.dao.ItinerarioDBHelper;
import br.com.vostre.circular.model.dao.ParadaDBHelper;
import br.com.vostre.circular.utils.ItinerarioList;


public class RealidadeNova extends BaseActivity {

    static BeyondarFragmentSupport mBeyondarFragment;
    static World mWorld;
    static LocationManager mLocationManager;
    static LocationListener mListener;
    static Location mLocation;
    static RadarView mRadarView;
    static RadarWorldPlugin radarWorldPlugin;
    static LinearLayout panelDetalhe;
    static TextView textViewNomeParada;
    static TextView textViewDistanciaParada;
    static Button btnFecharDetalhe;
    static NumberFormat numberFormat;
    static ListView listaItinerarios;
    static ItinerarioDBHelper itinerarioDBHelper;
    static ParadaDBHelper paradaDBHelper;
    static float lastAccuracy = 0;
    static final int TIME_TO_UPDATE = 5000;
    static final int DISTANCE_TO_UPDATE = 0;
    static ProgressDialog dialog;

    Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realidade_nova);

        this.savedInstanceState = savedInstanceState;


        int permissionGPS = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCamera = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA);

        if(permissionGPS != PackageManager.PERMISSION_GRANTED || permissionCamera != PackageManager.PERMISSION_GRANTED){

            finish();

        } else{

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new PlaceholderFragment())
                        .commit();
            }

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            listaItinerarios = (ListView) findViewById(R.id.listViewItinerariosRealidade);
            itinerarioDBHelper = new ItinerarioDBHelper(this);
            paradaDBHelper = new ParadaDBHelper(this);

            mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(R.id.ar);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.realidade_aumentada, menu);
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
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements OnClickBeyondarObjectListener, LocationListener {

        LayoutInflater inflater;
        ViewGroup container;
        Bundle savedInstanceState;

        public PlaceholderFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            this.inflater = inflater;
            this.container = container;
            this.savedInstanceState = savedInstanceState;

            int permissionGPS = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
            int permissionCamera = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA);

            if(permissionGPS != PackageManager.PERMISSION_GRANTED || permissionCamera != PackageManager.PERMISSION_GRANTED){
                getActivity().finish();
            } else{
                return iniciaOnCreateView(this.inflater, this.container, this.savedInstanceState);
            }

            return null;

        }

        public Location getBestLocation(){
            Location loc = null;

            List<String> providers = mLocationManager.getProviders(true);
            Location bestLocation = null;

            int permission = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);

            for(String provider : providers){
                Location l = mLocationManager.getLastKnownLocation(provider);

                if(l == null){
                    continue;
                }

                if(bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = l;
                }

            }

            return loc;

        }



        @Override
        public void onPause() {
            super.onPause();

            int permissionGPS = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
            int permissionCamera = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA);

            if(permissionGPS != PackageManager.PERMISSION_GRANTED || permissionCamera != PackageManager.PERMISSION_GRANTED){
                getActivity().finish();
            }

            if(mLocationManager != null){
                mLocationManager.removeUpdates(this);
            }

            BeyondarLocationManager.disable();
        }

        @Override
        public void onResume() {
            super.onResume();

            int permissionGPS = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
            int permissionCamera = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA);

            if(permissionGPS != PackageManager.PERMISSION_GRANTED || permissionCamera != PackageManager.PERMISSION_GRANTED){

                getActivity().finish();

            } else {

                if (mLocationManager != null && !mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertDisabledGps();
                }

                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setPowerRequirement(Criteria.POWER_MEDIUM);

                BeyondarLocationManager.enable();

                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIME_TO_UPDATE, 0, this);
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_TO_UPDATE, 0, this);
            }
        }

        @Override
        public void onClickBeyondarObject(ArrayList<BeyondarObject> beyondarObjects) {

            int idParada = (int) beyondarObjects.get(0).getId();

            DateFormat df = new SimpleDateFormat("HH:mm");
            Calendar cal = Calendar.getInstance();

            final String hora = df.format(cal.getTime());

            Parada parada = new Parada();
            parada.setId(idParada);

            parada = paradaDBHelper.carregar(getActivity(), parada);
            List<HorarioItinerario> listItinerarios = itinerarioDBHelper.listarTodosPorParada(getActivity(), parada, hora);

            final ItinerarioList adapterItinerario = new ItinerarioList(getActivity(),
                    android.R.layout.simple_spinner_dropdown_item, listItinerarios, parada);
            adapterItinerario.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
            listaItinerarios.setAdapter(adapterItinerario);

            listaItinerarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    HorarioItinerario itinerario = adapterItinerario.getItem(i);

                    Intent intent = new Intent(adapterView.getContext(), TodosHorarios.class);
                    intent.putExtra("id_partida", itinerario.getItinerario().getPartida().getId());
                    intent.putExtra("id_destino", itinerario.getItinerario().getDestino().getId());

                    intent.putExtra("itinerario", itinerario.getItinerario().getId());
                    intent.putExtra("hora", itinerario.getHorario().toString());

                    startActivity(intent);
                }
            });

            textViewNomeParada.setText(beyondarObjects.get(0).getName());
            textViewDistanciaParada.setText(numberFormat.format(beyondarObjects.get(0).getDistanceFromUser()) + " m");
            panelDetalhe.setVisibility(View.VISIBLE);

            btnFecharDetalhe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    panelDetalhe.setVisibility(View.INVISIBLE);
                }
            });

        }

        @Override
        public void onLocationChanged(Location location) {

            if(null != location){
///*
                dialog.dismiss();

                if(location.getAccuracy() < lastAccuracy){
                    System.out.println(location.getLatitude()+";"+location.getLongitude());
                    mLocation = location;
                    mWorld.setGeoPosition(mLocation.getLatitude(), mLocation.getLongitude());
                    mBeyondarFragment.setWorld(mWorld);
                    //Toast.makeText(getActivity(), "Atualizando posicionamento.", Toast.LENGTH_LONG).show();

                    lastAccuracy = location.getAccuracy();

                }
//*/

                //System.out.println("Accuracy: "+mLocation.getAccuracy());
                //Toast.makeText(getActivity(), "Atualizando posicionamento.", Toast.LENGTH_SHORT).show();
            }

        }

        public void buildAlertDisabledGps(){
            final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setMessage("Para resultados mais precisos, por favor ative o GPS.")
                 .setCancelable(false)
                 .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialogInterface, int i) {
                         startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                     }
                 })
                 .setNegativeButton("Agora Não", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialogInterface, int i) {
                         dialogInterface.dismiss();
                     }
                 });

            final AlertDialog dialog = alert.create();
            dialog.show();

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {
            //System.out.println("ATIVOU!!!!!!!!!!!!!!!!");
        }

        @Override
        public void onProviderDisabled(String s) {
            //System.out.println("DESATIVOU!!!!!!!!!!!!!!!!");
        }

        public View iniciaOnCreateView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState){

            View rootView = null;

            try{
                final int DISTANCE_TO_RENDER = 600;

                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setPowerRequirement(Criteria.POWER_HIGH);

                mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                String bestProvider = mLocationManager.getBestProvider(criteria, true);

                mLocation =  getBestLocation();

                numberFormat = NumberFormat.getNumberInstance();
                numberFormat.setMaximumFractionDigits(2);

                if(null != mLocation){
                    System.out.println(mLocation.getLatitude()+";"+mLocation.getLongitude());
                } else{
                    mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    Location lNetwork = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    if(null != lNetwork){
                        mLocation = (mLocation == null || (mLocation.getTime() > lNetwork.getTime() && mLocation.getAccuracy() > lNetwork.getAccuracy()))
                                ? mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                                : mLocation;
                    }

                }

                //# mLocationManager.requestLocationUpdates(bestProvider, TIME_TO_UPDATE, DISTANCE_TO_UPDATE, this);
                //# mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIME_TO_UPDATE, DISTANCE_TO_UPDATE, this);

                mRadarView = (RadarView) container.findViewById(R.id.radarView);
                textViewNomeParada = (TextView) container.findViewById(R.id.textViewNomeParada);
                textViewDistanciaParada = (TextView) container.findViewById(R.id.textViewDistancia);
                panelDetalhe = (LinearLayout) container.findViewById(R.id.panelDetalhe);
                btnFecharDetalhe = (Button) container.findViewById(R.id.buttonFecharDetalhe);

                btnFecharDetalhe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        panelDetalhe.setVisibility(View.INVISIBLE);
                    }
                });

                radarWorldPlugin = new RadarWorldPlugin(container.getContext());
                radarWorldPlugin.setRadarView(mRadarView);
                radarWorldPlugin.setMaxDistance(DISTANCE_TO_RENDER);

                ParadaDBHelper paradaDBHelper = new ParadaDBHelper(container.getContext());
                List<Parada> paradas = new ArrayList<Parada>();

                rootView = inflater.inflate(R.layout.fragment_realidade_nova, container, false);

                mWorld = new World(container.getContext());

                if(null != mLocation){
                    mWorld.setGeoPosition(mLocation.getLatitude(), mLocation.getLongitude());
                }

                mWorld.setDefaultImage(R.drawable.logo_resumida);
                mWorld.addPlugin(radarWorldPlugin);

                paradas = paradaDBHelper.listarTodosComItinerario(container.getContext());

                for(Parada parada : paradas){
                    GeoObject g = new GeoObject(parada.getId());
                    g.setGeoPosition(Double.parseDouble(parada.getLatitude()), Double.parseDouble(parada.getLongitude()));
                    g.setName(parada.getReferencia());
                    //g.setImageResource(R.drawable.logo_resumida);
                    g.setVisible(true);
                    //g.setImageUri("assets://marker_idle.png");

                    mWorld.addBeyondarObject(g);
                }

                mBeyondarFragment.setDistanceFactor(6.0f);
                mBeyondarFragment.setMaxDistanceToRender(DISTANCE_TO_RENDER);
                mBeyondarFragment.setSensorDelay(SensorManager.SENSOR_DELAY_NORMAL);

                mBeyondarFragment.setWorld(mWorld);

                mBeyondarFragment.setOnClickBeyondarObjectListener(this);

                GeoObject user = new GeoObject();
                user.setGeoPosition(mWorld.getLatitude(), mWorld.getLongitude());

                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIME_TO_UPDATE, 0, this);
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_TO_UPDATE, 0, this);

                BeyondarLocationManager.addWorldLocationUpdate(mWorld);
                BeyondarLocationManager.addGeoObjectLocationUpdate(user);
                BeyondarLocationManager.setLocationManager(mLocationManager);

                dialog = new ProgressDialog(getActivity());
                dialog.setMessage("Consultando sua localização atual...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();
            } catch (SecurityException ex){
                System.out.println(ex.getMessage());
            }

            return rootView;
        }

    }

}
