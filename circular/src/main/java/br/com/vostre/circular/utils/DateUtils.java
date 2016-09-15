package br.com.vostre.circular.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Almir on 09/03/2015.
 */
public class DateUtils {

    public static String converteDataParaPadraoBanco(Date date){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    public static String converteDataParaPadraoBrasil(Date date){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return dateFormat.format(date);
    }

    public static Date convertePadraoBancoParaDate(String date) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.parse(date);
    }

    public static String getHoraAtual(){
        DateFormat df = new SimpleDateFormat("HH:mm");
        Calendar cal = Calendar.getInstance();

        return df.format(cal.getTime());

    }

}
