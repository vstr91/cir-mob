package br.com.vostre.circular.utils;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import java.util.Calendar;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Local;
import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaColeta;
import br.com.vostre.circular.model.dao.BairroDBHelper;
import br.com.vostre.circular.model.dao.LocalDBHelper;
import br.com.vostre.circular.model.dao.MensagemDBHelper;
import br.com.vostre.circular.model.dao.ParadaColetaDBHelper;

/**
 * Created by Almir on 07/04/2015.
 */
public class ModalCadastroMensagem extends android.support.v4.app.DialogFragment implements View.OnClickListener {

    EditText editTextTitulo;
    EditText editTextDescricao;
    private Button btnEnviar;
    private Button btnFechar;

    ModalCadastroListener listener;

    MensagemDBHelper mensagemDBHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.modal_cadastro_mensagem, container, false);

        view.setMinimumWidth(700);

        editTextTitulo = (EditText) view.findViewById(R.id.editTextTitulo);
        editTextDescricao = (EditText) view.findViewById(R.id.editTextDescricao);
        btnEnviar = (Button) view.findViewById(R.id.btnEnviar);
        btnFechar = (Button) view.findViewById(R.id.btnFechar);

        btnEnviar.setOnClickListener(this);
        btnFechar.setOnClickListener(this);

        return view;

    }

    public ModalCadastroListener getListener() {
        return listener;
    }

    public void setListener(ModalCadastroListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSalvar:
                String titulo = editTextTitulo.getText().toString();
                String descricao = editTextDescricao.getText().toString();

                if(titulo == null || descricao == null){
                    Toast.makeText(getActivity(), "Todos os dados são obrigatórios.", Toast.LENGTH_LONG).show();
                } else{
                    Mensagem mensagem = new Mensagem();
                    mensagem.setTitulo(titulo);
                    mensagem.setDescricao(descricao);
                    mensagem.setStatus(3);
                    mensagem.setDataEnvio(Calendar.getInstance());

                    Long resultado = mensagemDBHelper.salvarOuAtualizar(getActivity(), mensagem);

                    listener.onModalCadastroDismissed(resultado.intValue());

                    Toast.makeText(getActivity(), "Mensagem salva com sucesso. Ela será enviada em breve!", Toast.LENGTH_LONG).show();

                    dismiss();
                }

                break;
            case R.id.btnFechar:
                dismiss();
                break;
        }
    }

    public void onStart()
    {
        super.onStart();

        // safety check
        if (getDialog() == null)
            return;

        int dialogWidth = 1000;
        int dialogHeight = 650;

        getDialog().getWindow().setLayout(dialogWidth, WindowManager.LayoutParams.WRAP_CONTENT);
    }

}
