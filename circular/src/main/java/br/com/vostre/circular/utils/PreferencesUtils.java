package br.com.vostre.circular.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by Cefet on 01/04/2016.
 */
public class PreferencesUtils {

    public static boolean salvarPreferencia(Context context, String chave, String valor){
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(chave, valor);
        return editor.commit();
    }

    public static Object carregarPreferencia(Context context, String chave, String tipo){
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        Object obj = null;

        switch(tipo){
            case "string":
                obj = sp.getString(chave, "");
                break;
            case "integer":
                obj = sp.getInt(chave, -1);
                break;
            case "boolean":
                obj = sp.getBoolean(chave, false);
                break;
            case "float":
                obj = sp.getFloat(chave, -1);
                break;
            case "long":
                obj = sp.getLong(chave, -1);
                break;
        }

        return obj;
    }

    public static List<String> carregaParadasFavoritas(Context context){
        List<String> paradasFavoritas = null;

        String favParadas = (String) PreferencesUtils.carregarPreferencia(context, context.getPackageName()+".paradas_favoritas", "string");
        String[] arrParadas;

        if(favParadas.isEmpty()){
            paradasFavoritas = new ArrayList<>();
        } else{
            arrParadas = favParadas.split(",");
            paradasFavoritas = new LinkedList<>(Arrays.asList(arrParadas));
        }

        return paradasFavoritas;

    }

    public static void gravaParadasFavoritas(List<String> lstParadas, Context context){
        String favParadas = Arrays.toString(lstParadas.toArray()).replace("[", "").replace("]", "").replace(" ", "");

        PreferencesUtils.salvarPreferencia(context, context.getPackageName()+".paradas_favoritas", favParadas);
    }

    public static List<String> carregaItinerariosFavoritos(Context context){
        List<String> itinerariosFavoritos = null;

        String favItinerarios = (String) PreferencesUtils.carregarPreferencia(context, context.getPackageName()+".itinerarios_favoritos", "string");
        String[] arrItinerarios;

        if(favItinerarios.isEmpty()){
            itinerariosFavoritos = new ArrayList<>();
        } else{
            arrItinerarios = favItinerarios.split(",");
            itinerariosFavoritos = new LinkedList<>(Arrays.asList(arrItinerarios));
        }

        return itinerariosFavoritos;

    }

    public static void gravaItinerariosFavoritos(List<String> lstItinerarios, Context context){
        String favItinerarios = Arrays.toString(lstItinerarios.toArray()).replace("[", "").replace("]", "").replace(" ", "");

        PreferencesUtils.salvarPreferencia(context, context.getPackageName()+".itinerarios_favoritos", favItinerarios);
    }

}
