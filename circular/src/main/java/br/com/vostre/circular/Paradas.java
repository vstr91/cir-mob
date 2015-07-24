package br.com.vostre.circular;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.Local;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.dao.BairroDBHelper;
import br.com.vostre.circular.model.dao.EstadoDBHelper;
import br.com.vostre.circular.model.dao.HorarioDBHelper;
import br.com.vostre.circular.model.dao.LocalDBHelper;
import br.com.vostre.circular.model.dao.ParadaDBHelper;
import br.com.vostre.circular.utils.CustomAdapter;
import br.com.vostre.circular.utils.CustomSpinner;
import br.com.vostre.circular.utils.ParadaList;

public class Paradas extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    //CustomAdapter<Parada> adapterParada;
    ParadaList adapterParada;
    CustomSpinner cmbEstado;
    CustomSpinner cmbCidade;
    CustomSpinner cmbPartida;
    ListView listaParadas;

    EstadoDBHelper estadoDBHelper;
    LocalDBHelper localDBHelper;
    BairroDBHelper bairroDBHelper;
    ParadaDBHelper paradaDBHelper;

    TextView txtParadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_paradas);

        List<Estado> estadosDb = new ArrayList<Estado>();

        estadoDBHelper = new EstadoDBHelper(getBaseContext());
        localDBHelper = new LocalDBHelper(getBaseContext());
        bairroDBHelper = new BairroDBHelper(getBaseContext());
        paradaDBHelper = new ParadaDBHelper(getBaseContext());
        txtParadas = (TextView) findViewById(R.id.textViewParadas);

        cmbEstado = (CustomSpinner) findViewById(R.id.comboEstado);
        cmbCidade = (CustomSpinner) findViewById(R.id.comboCidade);
        cmbPartida = (CustomSpinner) findViewById(R.id.comboPartida);
        listaParadas = (ListView) findViewById(R.id.listViewParadas);

        cmbCidade.setEnabled(false);
        cmbPartida.setEnabled(false);
        txtParadas.setVisibility(View.INVISIBLE);

        Estado estadoPlaceholder = new Estado();
        estadoPlaceholder.setNome("UF");
        estadoPlaceholder.setSigla("Selec. UF");

        estadosDb = estadoDBHelper.listarTodosComVinculo(getBaseContext());

        estadosDb.add(estadoPlaceholder);
        txtParadas.setVisibility(View.INVISIBLE);

        final CustomAdapter<Estado> adapter = new
                CustomAdapter<Estado>(getBaseContext(), R.layout.custom_spinner, estadosDb, "UF", estadosDb.size());

        adapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);

        cmbEstado.setAdapter(adapter);

        cmbEstado.setOnItemSelectedListener(this);

        cmbCidade.setOnItemSelectedListener(this);

        cmbPartida.setOnItemSelectedListener(this);

        if(estadosDb.size() == 2){
            cmbEstado.setSelection(0);
        } else{
            cmbEstado.setSelection(estadosDb.size() - 1, false);
        }

        listaParadas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Parada parada = adapterParada.getItem(i);

                Intent intent = new Intent(getBaseContext(), ParadaDetalhe.class);
                intent.putExtra("id_parada", parada.getId());
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.paradas, menu);
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        switch(adapterView.getId()){
            case R.id.comboEstado:
                Local localPlaceholder = new Local();
                localPlaceholder.setNome("Selecione o Local");

                Estado estado = (Estado) adapterView.getItemAtPosition(i);

                List<Local> locaisDb = localDBHelper.listarTodosPorEstadoEItinerario( getBaseContext(), estado);

                locaisDb.add(localPlaceholder);

                CustomAdapter<Local> adapterCidade =
                        new CustomAdapter<Local>(getBaseContext(), R.layout.custom_spinner, locaisDb, "", locaisDb.size());
                adapterCidade.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
                cmbCidade.setAdapter(adapterCidade);

                if(locaisDb.size() == 2){
                    cmbCidade.setSelection(0);
                } else{
                    cmbCidade.setSelection(locaisDb.size() - 1, false);
                }

                if(locaisDb.size() > 1){
                    cmbCidade.setEnabled(true);
                }

                cmbPartida.setEnabled(false);
                txtParadas.setVisibility(View.INVISIBLE);
                break;
            case R.id.comboCidade:
                Bairro bairroPlaceholder = new Bairro();
                bairroPlaceholder.setNome("Selecione o Bairro");

                Local local = (Local) adapterView.getItemAtPosition(i);

                List<Bairro> bairrosDb = bairroDBHelper.listarTodosVinculadosPorLocal(getBaseContext(), local);

                bairrosDb.add(bairroPlaceholder);

                CustomAdapter<Bairro> adapterPartida =
                        new CustomAdapter<Bairro>(getBaseContext(), R.layout.custom_spinner, bairrosDb, "",
                                bairrosDb.size());
                adapterPartida.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
                cmbPartida.setAdapter(adapterPartida);

                if(bairrosDb.size() == 2){
                    cmbPartida.setSelection(0);
                } else{
                    cmbPartida.setSelection(bairrosDb.size()-1, false);
                }

                if(bairrosDb.size() > 1){
                    cmbPartida.setEnabled(true);
                    TextView txtPartida = (TextView) cmbPartida.getChildAt(0);

                    if(null != txtPartida){
                        txtPartida.setTextColor(Color.rgb(249, 249, 249));
                    }

                }

                if(!cmbCidade.isEnabled()){
                    ((TextView)adapterView.getChildAt(0)).setTextColor(Color.rgb(150, 150, 150));
                }

                txtParadas.setVisibility(View.INVISIBLE);
                break;
            case R.id.comboPartida:
                Bairro bairro = (Bairro) adapterView.getItemAtPosition(i);

                List<Parada> listParadas = paradaDBHelper.listarTodosComItinerarioPorBairro(getBaseContext(), bairro);
                adapterParada = new ParadaList(Paradas.this,
                        R.layout.listview_paradas, listParadas);
                /*
                adapterParada = new CustomAdapter<Parada>(getBaseContext(),
                        R.layout.listview_paradas, listParadas, "",
                        listParadas.size());
                        */
                txtParadas.setVisibility(View.VISIBLE);
                listaParadas.setAdapter(adapterParada);

                if(!cmbPartida.isEnabled()){
                    ((TextView)adapterView.getChildAt(0)).setTextColor(Color.rgb(150, 150, 150));
                }
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
