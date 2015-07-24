package br.com.vostre.circular;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.ItinerarioLog;
import br.com.vostre.circular.model.Local;
import br.com.vostre.circular.model.dao.BairroDBHelper;
import br.com.vostre.circular.model.dao.EstadoDBHelper;
import br.com.vostre.circular.model.dao.HorarioDBHelper;
import br.com.vostre.circular.model.dao.HorarioItinerarioDBHelper;
import br.com.vostre.circular.model.dao.ItinerarioDBHelper;
import br.com.vostre.circular.model.dao.ItinerarioLogDBHelper;
import br.com.vostre.circular.model.dao.LocalDBHelper;
import br.com.vostre.circular.utils.CustomAdapter;
import br.com.vostre.circular.utils.CustomSpinner;
import br.com.vostre.circular.utils.ItinerarioDestinoSpinner;

public class Itinerarios extends ActionBarActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    CustomSpinner cmbEstado;
    CustomSpinner cmbCidade;
    CustomSpinner cmbPartida;
    CustomSpinner cmbDestino;
    TextView textViewHorario;
    TextView textViewTarifa;
    TextView textViewEmpresa;
    TextView textViewProximoHorarioLabel;
    TextView textViewEmpresaLabel;
    TextView textViewTarifaLabel;
    TextView textViewObsLabel;
    Button btnTodosHorarios;

    HorarioItinerario horarioItinerario;

    EstadoDBHelper estadoDBHelper = new EstadoDBHelper(getBaseContext());
    LocalDBHelper localDBHelper = new LocalDBHelper(getBaseContext());
    BairroDBHelper bairroDBHelper = new BairroDBHelper(getBaseContext());
    HorarioDBHelper horarioDBHelper = new HorarioDBHelper(getBaseContext());
    HorarioItinerarioDBHelper horarioItinerarioDBHelper = new HorarioItinerarioDBHelper(getBaseContext());

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_itinerarios);

        estadoDBHelper = new EstadoDBHelper(getBaseContext());
        localDBHelper = new LocalDBHelper(getBaseContext());
        bairroDBHelper = new BairroDBHelper(getBaseContext());
        horarioDBHelper = new HorarioDBHelper(getBaseContext());
        horarioItinerarioDBHelper = new HorarioItinerarioDBHelper(getBaseContext());

        List<Estado> estadosDb = new ArrayList<Estado>();

        cmbEstado = (CustomSpinner) findViewById(R.id.comboEstado);
        cmbCidade = (CustomSpinner) findViewById(R.id.comboCidade);
        cmbPartida = (CustomSpinner) findViewById(R.id.comboPartida);
        cmbDestino = (CustomSpinner) findViewById(R.id.comboDestino);
        textViewHorario = (TextView) findViewById(R.id.textViewHorario);
        textViewTarifa = (TextView) findViewById(R.id.textViewTarifa);
        textViewEmpresa = (TextView) findViewById(R.id.textViewEmpresa);
        textViewProximoHorarioLabel = (TextView) findViewById(R.id.textViewProximoHorarioLabel);
        textViewEmpresaLabel = (TextView) findViewById(R.id.textViewEmpresaLabel);
        textViewTarifaLabel = (TextView) findViewById(R.id.textViewTarifaLabel);
        textViewObsLabel = (TextView) findViewById(R.id.textViewObs);
        btnTodosHorarios = (Button) findViewById(R.id.btnTodosHorarios);

        cmbEstado.setOnItemSelectedListener(this);

        cmbCidade.setOnItemSelectedListener(this);

        cmbPartida.setOnItemSelectedListener(this);

        cmbDestino.setOnItemSelectedListener(this);

        ocultaCampos();

        Estado estadoPlaceholder = new Estado();
        estadoPlaceholder.setNome("UF");
        estadoPlaceholder.setSigla("Selec. UF");

        estadosDb = estadoDBHelper.listarTodosComVinculo(getBaseContext());

        estadosDb.add(estadoPlaceholder);

        final CustomAdapter<Estado> adapter = new
                CustomAdapter<Estado>(getBaseContext(), R.layout.custom_spinner, estadosDb, "", estadosDb.size());

        adapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);

        cmbEstado.setAdapter(adapter);

        if(estadosDb.size() == 2){
            cmbEstado.setSelection(0);
        } else{
            cmbEstado.setSelection(estadosDb.size() - 1, false);
        }

        btnTodosHorarios.setOnClickListener(this);

        cmbCidade.setEnabled(false);
        cmbPartida.setEnabled(false);
        cmbDestino.setEnabled(false);

        View v = cmbDestino.getChildAt(1);

        if(null != v){
            v.setEnabled(false);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.itinerarios, menu);
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

    private void ocultaCampos(){
        btnTodosHorarios.setVisibility(View.INVISIBLE);
        textViewTarifa.setVisibility(View.INVISIBLE);
        textViewEmpresa.setVisibility(View.INVISIBLE);
        textViewTarifaLabel.setVisibility(View.INVISIBLE);
        textViewEmpresaLabel.setVisibility(View.INVISIBLE);
        textViewProximoHorarioLabel.setVisibility(View.INVISIBLE);
        textViewObsLabel.setVisibility(View.INVISIBLE);
    }

   //########################## LISTENER ###############################################

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        switch(adapterView.getId()){
            case R.id.comboEstado:
                Local localPlaceholder = new Local();
                localPlaceholder.setNome("Selecione o Local");

                Estado estado = (Estado) adapterView.getItemAtPosition(i);

                List<Local> locaisDb = localDBHelper.listarTodosPorEstadoEItinerario(getBaseContext(), estado);

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
                cmbDestino.setEnabled(false);

                textViewHorario.setText("");
                textViewObsLabel.setText("");

                ocultaCampos();
                break;
            case R.id.comboCidade:
                Bairro bairroPlaceholder = new Bairro();
                bairroPlaceholder.setNome("Selecione a Partida");

                Local local = (Local) adapterView.getItemAtPosition(i);

                List<Bairro> bairrosDb = bairroDBHelper.listarPartidaPorItinerario(getBaseContext(), local);

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
                }

                cmbDestino.setEnabled(false);

                textViewHorario.setText("");
                textViewObsLabel.setText("");

                ocultaCampos();

                if(!cmbCidade.isEnabled()){
                    ((TextView)adapterView.getChildAt(0)).setTextColor(Color.rgb(150, 150, 150));
                }
                break;
            case R.id.comboPartida:
                Bairro destinoPlaceholder = new Bairro();
                destinoPlaceholder.setNome("Selecione o Destino");

                Bairro partida = (Bairro) adapterView.getItemAtPosition(i);

                List<Bairro> destinosDb = bairroDBHelper.listarDestinoPorPartida(getBaseContext(), partida);

                destinosDb.add(destinoPlaceholder);

                    /*
                    CustomAdapter<Bairro> adapterDestino =
                            new CustomAdapter<Bairro>(rootView.getContext(), R.layout.custom_spinner, bairrosDb, "",
                                    bairrosDb.size());
                    */
                ItinerarioDestinoSpinner adapterDestino =
                        new ItinerarioDestinoSpinner(Itinerarios.this, R.layout.custom_spinner, destinosDb, destinosDb.size());


                //adapterDestino.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
                adapterDestino.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
                cmbDestino.setAdapter(adapterDestino);

                if(destinosDb.size() == 2){
                    cmbDestino.setSelection(0);
                } else{
                    cmbDestino.setSelection(destinosDb.size()-1, false);
                }

                if(destinosDb.size() > 1){
                    cmbDestino.setEnabled(true);
                }

                textViewHorario.setText("");
                textViewObsLabel.setText("");

                ocultaCampos();

                if(!cmbPartida.isEnabled()){
                    ((TextView)adapterView.getChildAt(0)).setTextColor(Color.rgb(150, 150, 150));
                }
                break;
            case R.id.comboDestino:
                if(i+1 != cmbDestino.getCount()){
                    Bairro umaPartida = (Bairro) cmbPartida.getSelectedItem();
                    Bairro destino = (Bairro) adapterView.getItemAtPosition(i);
                    ItinerarioDBHelper itinerarioDBHelper = new ItinerarioDBHelper(getBaseContext());
                    ItinerarioLogDBHelper itinerarioLogDBHelper = new ItinerarioLogDBHelper(getBaseContext());

                    DateFormat df = new SimpleDateFormat("HH:mm");
                    Calendar cal = Calendar.getInstance();

                    String hora = df.format(cal.getTime());

                    horarioItinerario = horarioItinerarioDBHelper.listarProximoHorarioItinerario(getBaseContext(), umaPartida, destino, hora);

                    if(null != horarioItinerario){
                        textViewHorario.setText(horarioItinerario.getHorario().toString());
                        btnTodosHorarios.setVisibility(View.VISIBLE);
                        textViewTarifa.setVisibility(View.VISIBLE);
                        textViewEmpresa.setVisibility(View.VISIBLE);
                        textViewTarifaLabel.setVisibility(View.VISIBLE);
                        textViewEmpresaLabel.setVisibility(View.VISIBLE);
                        textViewProximoHorarioLabel.setVisibility(View.VISIBLE);

                        if(null != horarioItinerario.getItinerario().getObservacao()){
                            textViewObsLabel.setVisibility(View.VISIBLE);
                        }

                    } else{
                        Calendar diaSeguinte = Calendar.getInstance();
                        diaSeguinte.add(Calendar.DATE, 1);
                        horarioItinerario = horarioItinerarioDBHelper
                                .listarPrimeiroHorarioItinerario(getBaseContext(), umaPartida, destino, diaSeguinte);

                        if(null != horarioItinerario){

                            textViewHorario.setText(horarioItinerario.getHorario().toString());
                            btnTodosHorarios.setVisibility(View.VISIBLE);
                            textViewTarifa.setVisibility(View.VISIBLE);
                            textViewEmpresa.setVisibility(View.VISIBLE);
                            textViewTarifaLabel.setVisibility(View.VISIBLE);
                            textViewEmpresaLabel.setVisibility(View.VISIBLE);
                            textViewProximoHorarioLabel.setVisibility(View.VISIBLE);

                            if(null != horarioItinerario.getItinerario().getObservacao()){
                                textViewObsLabel.setVisibility(View.VISIBLE);
                            } else{
                                textViewObsLabel.setVisibility(View.GONE);
                            }

                        } else{
                            textViewHorario.setText("N/D");
                            btnTodosHorarios.setVisibility(View.INVISIBLE);
                        }

                    }

                    if(horarioItinerario.isTrecho()){
                        horarioItinerario.getItinerario().setPartida(umaPartida);
                        horarioItinerario.getItinerario().setDestino(destino);
                    }

                    ItinerarioLog itinerarioLog = new ItinerarioLog();
                    itinerarioLog.setItinerario(horarioItinerario.getItinerario());
                    itinerarioLog.setDataConsulta(Calendar.getInstance());

                    itinerarioLogDBHelper.salvar(getBaseContext(), itinerarioLog);

                    DecimalFormat format = new DecimalFormat("#.00");

                    textViewTarifa.setText("R$ "+format.format(horarioItinerario.getItinerario().getValor()));
                    textViewEmpresa.setText(horarioItinerario.getItinerario().getEmpresa().getFantasia());

                    if(null != horarioItinerario.getItinerario().getObservacao() &&
                            !horarioItinerario.getItinerario().getObservacao().equals("") &&
                            !horarioItinerario.getItinerario().getObservacao().equals("null")){
                        textViewObsLabel.setText("("+horarioItinerario.getItinerario().getObservacao()+")");
                    } else{
                        textViewObsLabel.setVisibility(View.GONE);
                    }

                    List<HorarioItinerario> itinerarios =
                            itinerarioDBHelper.listarOutrasOpcoesItinerario(getBaseContext(), horarioItinerario.getItinerario(), hora);

                    if(!cmbDestino.isEnabled()){
                        ((TextView)adapterView.getChildAt(0)).setTextColor(Color.rgb(150, 150, 150));
                    }

                }

                if(!cmbDestino.isEnabled()){
                    ((TextView)(((LinearLayout)adapterView.getChildAt(0)).getChildAt(0))).setTextColor(Color.rgb(150, 150, 150));
                }

                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {

        Intent intent;

        switch(view.getId()){
            case R.id.btnTodosHorarios:
                Bairro bairroPartida = (Bairro) cmbPartida.getSelectedItem();
                Bairro bairroDestino = (Bairro) cmbDestino.getSelectedItem();

                intent = new Intent(getBaseContext(), TodosHorariosNovo.class);
                intent.putExtra("id_partida", bairroPartida.getId());
                intent.putExtra("id_destino", bairroDestino.getId());
                intent.putExtra("itinerario", horarioItinerario.getItinerario().getId());

                if(!textViewHorario.getText().toString().equals("N/D")){
                    intent.putExtra("hora", textViewHorario.getText().toString());
                }

                startActivity(intent);
                break;
        }

    }
}
