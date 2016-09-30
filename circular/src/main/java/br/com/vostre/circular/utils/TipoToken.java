package br.com.vostre.circular.utils;

/**
 * Created by Almir on 08/08/2016.
 */
public enum TipoToken {
    DADOS("dados"),
    MENSAGEM("mensagem"),
    ENVIO_MENSAGEM("envio_mensagem"),
    ENVIO_PARADA("envio_parada");

    private String tipo;

    TipoToken(String tipo){
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

}
