package br.com.vostre.circular.utils;

import android.Manifest;

/**
 * Created by Almir on 05/04/2015.
 */
public class Constants {

    // ########## VALORES LOCAIS ############

///*
    public static final String SERVIDOR = "192.168.42.113/vostreNovo/web/app_dev.php";
//    public static final String SERVIDOR_TESTE = "10.0.0.100";
    public static final String SERVIDOR_TESTE = "vostre.com.br";
    public static final String URLSERVIDOR = "http://"+SERVIDOR+"/api/circular/recebe-dados/";
    public static final String URLSERVIDORMSG = "http://"+SERVIDOR+"/api/recebe-dados/mensagens/";
    public static final String URLSERVIDORENVIAMSG = "http://"+SERVIDOR+"/api/recebe-dados/mensagens/recebe/post/";
    public static final String URLSERVIDORENVIAPARADA = "http://"+SERVIDOR+"/api/recebe-dados/paradas/recebe/post/";
//*/

    // ######## FIM VALORES LOCAIS ############

    // ########## VALORES REMOTOS ############

/*
    public static final String SERVIDOR = "vostre.com.br";
    public static final String SERVIDOR_TESTE = "vostre.com.br";
    public static final String URLSERVIDOR = "http://"+SERVIDOR+"/api/circular/recebe-dados/";
    public static final String URLSERVIDORMSG = "http://"+SERVIDOR+"/api/recebe-dados/mensagens/";
    public static final String URLSERVIDORENVIAMSG = "http://"+SERVIDOR+"/api/recebe-dados/mensagens/recebe/post/";
    public static final String URLSERVIDORENVIAPARADA = "http://"+SERVIDOR+"/api/recebe-dados/paradas/recebe/post/";
*/

    // ######## FIM VALORES REMOTOS ############

    public static final String URLTOKEN = "http://"+SERVIDOR+"/api/token";
    public static final int PORTASERVIDOR = 80;

    public static final int ID_NOTIFICACAO_ATUALIZACAO = 1;
    public static final int ID_NOTIFICACAO_MSG = 2;

    public static final int TEMPO_ATUALIZACAO_MSG = 60; // em minutos
    public static final int TEMPO_ATUALIZACAO_ENVIO_MSG = 5; // em minutos
    public static final int TEMPO_ATUALIZACAO_ENVIO_PARADAS = 30; // em minutos

    public static final int REQUEST_EXTERNAL_STORAGE = 3;
    public static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

}
