package br.com.vostre.circular;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.dao.MensagemDBHelper;
import br.com.vostre.circular.utils.BroadcastUtils;
import br.com.vostre.circular.utils.Constants;
import br.com.vostre.circular.utils.MensagemList;
import br.com.vostre.circular.utils.ModalCadastroListener;
import br.com.vostre.circular.utils.ModalCadastroMensagem;
import br.com.vostre.circular.utils.ModalCadastroParada;
import br.com.vostre.circular.utils.ModalMensagemListener;
import br.com.vostre.circular.utils.NotificacaoUtils;
import br.com.vostre.circular.utils.SnackbarHelper;
import br.com.vostre.circular.utils.ToolbarUtils;

public class Mensagens extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener, ModalCadastroListener {

    ListView listViewMensagens;
    List<Mensagem> mensagens;
    MensagemList adapterMensagens;
    MensagemDBHelper mensagemDBHelper;

    Menu menu;
    BroadcastReceiver receiver;

    FloatingActionButton fabNova;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensagens);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NotificacaoUtils.removeNotificacao(getBaseContext(), Constants.ID_NOTIFICACAO_MSG);

        mensagemDBHelper = new MensagemDBHelper(getBaseContext());

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();

                Integer qtdMensagens = extras.getInt("mensagens");

                if(qtdMensagens != null){

                    mensagens = mensagemDBHelper.listarTodos(getBaseContext());
                    adapterMensagens =
                            new MensagemList(Mensagens.this, android.R.layout.simple_spinner_dropdown_item, mensagens);

                    adapterMensagens.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
                    adapterMensagens.notifyDataSetChanged();
                    listViewMensagens.invalidate();

                }

            }
        };

        mensagens = mensagemDBHelper.listarTodos(getBaseContext());

        listViewMensagens = (ListView) findViewById(R.id.listViewMensagens);
        fabNova = (FloatingActionButton) findViewById(R.id.fabNova);

        adapterMensagens =
                new MensagemList(Mensagens.this, android.R.layout.simple_spinner_dropdown_item, mensagens);

        adapterMensagens.setDropDownViewResource(android.R.layout.simple_selectable_list_item);

        listViewMensagens.setAdapter(adapterMensagens);
        listViewMensagens.setOnItemClickListener(this);

        fabNova.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mensagens, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()){
            case R.id.listViewMensagens:

                Mensagem umaMensagem = mensagens.get(position);

                Intent intent = new Intent(getBaseContext(), DetalheMensagem.class);
                intent.putExtra("mensagem", umaMensagem.getId());
                startActivity(intent);

                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mensagens = mensagemDBHelper.listarTodos(getBaseContext());

        adapterMensagens =
                new MensagemList(Mensagens.this, android.R.layout.simple_spinner_dropdown_item, mensagens);
        listViewMensagens.setAdapter(adapterMensagens);
        listViewMensagens.invalidate();
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
    public void onClick(View v) {
        Intent intent;

        switch(v.getId()){
            case R.id.fabNova:
                ModalCadastroMensagem modalCadastroMensagem = new ModalCadastroMensagem();
                modalCadastroMensagem.setListener(this);

                modalCadastroMensagem.show(getSupportFragmentManager(), "modalMensagem");
                break;
            default:
                ToolbarUtils.onMenuItemClick(v, this);
                break;
        }
    }

    @Override
    public void onModalCadastroDismissed(int resultado) {
        SnackbarHelper.notifica(this.getCurrentFocus(), "Mensagem Cadastrada!", Snackbar.LENGTH_LONG);
    }
}
