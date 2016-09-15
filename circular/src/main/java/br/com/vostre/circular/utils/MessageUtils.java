package br.com.vostre.circular.utils;

import android.content.Context;

import br.com.vostre.circular.model.dao.MensagemDBHelper;

/**
 * Created by Almir on 16/12/2015.
 */
public class MessageUtils {

    public static int getQuantidadeMensagensNaoLidas(Context context){
        MensagemDBHelper mensagemDBHelper = new MensagemDBHelper(context);
        return mensagemDBHelper.listarTodosNaoLidas(context).size();
    }

}
