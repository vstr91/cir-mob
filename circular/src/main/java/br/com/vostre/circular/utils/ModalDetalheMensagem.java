package br.com.vostre.circular.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Local;
import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.ParadaColeta;
import br.com.vostre.circular.model.dao.BairroDBHelper;
import br.com.vostre.circular.model.dao.LocalDBHelper;
import br.com.vostre.circular.model.dao.MensagemDBHelper;
import br.com.vostre.circular.model.dao.ParadaColetaDBHelper;

public class ModalDetalheMensagem extends android.support.v4.app.DialogFragment implements View.OnClickListener {

    TextView textViewTitulo;
    TextView textViewHora;
    TextView textViewDescricao;
    Button btnFechar;
    Mensagem mensagem;
    ModalMensagemListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.modal_detalhe_mensagem, container, false);
        MensagemDBHelper mensagemDBHelper = new MensagemDBHelper(getActivity());

        textViewTitulo = (TextView) view.findViewById(R.id.textViewTituloMensagem);
        textViewHora = (TextView) view.findViewById(R.id.textViewDataMensagem);
        textViewDescricao = (TextView) view.findViewById(R.id.textViewDetalheMensagem);
        btnFechar = (Button) view.findViewById(R.id.btnFechar);

        if(savedInstanceState != null){

            if(mensagem == null){
                mensagem = new Mensagem();
                mensagem.setId(savedInstanceState.getInt("id_mensagem"));
                mensagem = mensagemDBHelper.carregar(getActivity(), mensagem);
            }

        }

        if(mensagem != null){
            String dataFormatada = DateUtils.converteDataParaPadraoBrasil(mensagem.getDataEnvio().getTime());

            textViewTitulo.setText(mensagem.getTitulo());
            textViewHora.setText("Enviada em "+dataFormatada);
            textViewDescricao.setText(mensagem.getDescricao());

            if(mensagem.getStatus() == 0){

                if(mensagemDBHelper.marcarLida(getActivity(), mensagem) > 0){
                    mensagem.setStatus(2);
                }

            }
        }

        Dialog d = this.getDialog();

        if(d != null){
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(d.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT / 2;
            d.show();
            d.getWindow().setAttributes(lp);
        }

        btnFechar.setOnClickListener(this);

        return view;

    }

    public ModalMensagemListener getListener() {
        return listener;
    }

    public void setListener(ModalMensagemListener listener) {
        this.listener = listener;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("id_mensagem", getMensagem().getId());
        super.onSaveInstanceState(outState);
    }

    public Mensagem getMensagem() {
        return mensagem;
    }

    public void setMensagem(Mensagem mensagem) {
        this.mensagem = mensagem;
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.btnFechar:
                listener.onModalDismissed(getMensagem());
                dismiss();
                break;
        }

    }

    @Override
    public void onDismiss(DialogInterface dialog) {

        if(listener != null){
            listener.onModalDismissed(getMensagem());
        }

        super.onDismiss(dialog);
    }
}
