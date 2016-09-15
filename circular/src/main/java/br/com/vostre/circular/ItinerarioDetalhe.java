package br.com.vostre.circular;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.dao.HorarioDBHelper;
import br.com.vostre.circular.model.dao.ItinerarioDBHelper;
import br.com.vostre.circular.utils.BroadcastUtils;
import br.com.vostre.circular.utils.CustomAdapter;
import br.com.vostre.circular.utils.MessageUtils;
import br.com.vostre.circular.utils.ToolbarUtils;

public class ItinerarioDetalhe extends BaseActivity implements View.OnClickListener {

    Menu menu;
    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_itinerario_detalhe);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();

                Integer mensagens = extras.getInt("mensagens");

                if(mensagens != null){

                    if(menu != null){
                        invalidateOptionsMenu();

                        ToolbarUtils.atualizaBadge(mensagens);
                    }

                }

            }
        };

        HorarioDBHelper horarioDBHelper = new HorarioDBHelper(getBaseContext());
        ItinerarioDBHelper itinerarioDBHelper = new ItinerarioDBHelper(getBaseContext());

        TextView txtItinerario = (TextView) findViewById(R.id.textViewItinerarioBairroPartida);
        ListView listaHorarios = (ListView) findViewById(R.id.listViewHorarios);

        Bundle valores = getIntent().getExtras();
        int idItinerario = Integer.parseInt(valores.get("id_itinerario").toString());

        Itinerario itinerario = new Itinerario();
        itinerario.setId(idItinerario);

        itinerario = itinerarioDBHelper.carregar(getBaseContext(), itinerario);
        List<Horario> listHorarios = horarioDBHelper.listarTodosHorariosPorItinerario(getBaseContext(),
                itinerario.getPartida(), itinerario.getDestino());

        CustomAdapter<Horario> adapterHorario = new CustomAdapter<Horario>(getBaseContext(),
                android.R.layout.simple_spinner_dropdown_item, listHorarios, "", listHorarios.size());
        adapterHorario.setDropDownViewResource(android.R.layout.simple_selectable_list_item);

        txtItinerario.setText(itinerario.getPartida().getNome()+" x "+itinerario.getDestino().getNome());

        listaHorarios.setAdapter(adapterHorario);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.itinerario_detalhe, menu);

        this.menu = menu;
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

    @Override
    public void onClick(View v) {

        ToolbarUtils.onMenuItemClick(v, this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        BroadcastUtils.registraReceiver(this, receiver);
    }

    @Override
    protected void onStop() {
        BroadcastUtils.removeRegistroReceiver(this, receiver);
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(menu != null){
            invalidateOptionsMenu();

            ToolbarUtils.atualizaBadge(MessageUtils.getQuantidadeMensagensNaoLidas(getApplicationContext()));
        }
    }
}
