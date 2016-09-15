package br.com.vostre.circular;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.dao.ItinerarioDBHelper;
import br.com.vostre.circular.model.dao.SecaoItinerarioDBHelper;
import br.com.vostre.circular.utils.SecaoList;
import br.com.vostre.circular.utils.ToolbarUtils;

public class SecoesActivity extends BaseActivity implements View.OnClickListener {

    TextView textViewItinerarioSecao;
    ListView listViewSecoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secoes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textViewItinerarioSecao = (TextView) findViewById(R.id.textViewItinerarioSecao);
        listViewSecoes = (ListView) findViewById(R.id.listViewSecoes);

        Itinerario itinerario = null;
        int idItinerario = getIntent().getExtras().getInt("id_itinerario");

        if(idItinerario > 0){
            itinerario = new Itinerario();
            ItinerarioDBHelper itinerarioDBHelper = new ItinerarioDBHelper(getBaseContext());
            SecaoItinerarioDBHelper siDBHelper = new SecaoItinerarioDBHelper(getBaseContext());

            itinerario.setId(idItinerario);
            itinerario = itinerarioDBHelper.carregar(getBaseContext(), itinerario);
            List<SecaoItinerario> secoes = siDBHelper.listarTodasSecoesPorItinerario(getBaseContext(), itinerario);
            SecaoList listaSecoes = new SecaoList(this, R.layout.support_simple_spinner_dropdown_item, secoes);
            listViewSecoes.setAdapter(listaSecoes);

            if(itinerario.getPartida().getLocal().getId() != itinerario.getDestino().getLocal().getId()){
                textViewItinerarioSecao.setText(itinerario.getPartida().getLocal().getNome() + " x "
                        + itinerario.getDestino().getLocal().getNome());
            } else{
                textViewItinerarioSecao.setText(itinerario.getPartida().getNome()+" x "+itinerario.getDestino().getNome());
            }

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

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
        switch (v.getId()){
            case R.id.btnFechar:
                break;
            default:
                ToolbarUtils.onMenuItemClick(v, this);
                break;
        }
    }
}
