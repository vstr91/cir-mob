package br.com.vostre.circular.utils;

/**
 * Created by Almir on 08/08/2016.
 */
public enum TipoToken {
    DADOS("dados"),
    MENSAGEM("mensagem");

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
