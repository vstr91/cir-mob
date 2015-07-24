package br.com.vostre.circular.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.Horario;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Pais;

/**
 * Created by Almir on 14/04/2014.
 */
public class HorarioDBAdapter {

    private SQLiteDatabase database;
    private HorarioDBHelper horarioDBHelper;
    private String[] colunas = {horarioDBHelper.ID, horarioDBHelper.NOME, horarioDBHelper.STATUS};
    private Context context;

    public HorarioDBAdapter(Context context, SQLiteDatabase database){
        horarioDBHelper = new HorarioDBHelper(context);
        this.database = database;
        this.context = context;
    }

    public long salvarOuAtualizar(Horario horario){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long retorno = null;
        ContentValues cv = new ContentValues();
        cv.put(horarioDBHelper.ID, horario.getId());
        cv.put(horarioDBHelper.NOME, df.format(horario.getNome().getTime()));
        cv.put(horarioDBHelper.STATUS, horario.getStatus());

        if(database.update(HorarioDBHelper.TABELA, cv,  horarioDBHelper.ID+" = "+horario.getId(), null) < 1){
            retorno = database.insert(HorarioDBHelper.TABELA, null, cv);
            database.close();
            return retorno;
        } else{
            database.close();
            return -1;
        }

    }

    public int deletar(Horario horario){
        int retorno = database.delete(horarioDBHelper.TABELA, horarioDBHelper.ID+" = "+horario.getId(), null);
        database.close();
        return retorno;
    }

    public int deletarInativos(){
        int retorno = database.delete(horarioDBHelper.TABELA, horarioDBHelper.STATUS+" = "+2, null);
        database.close();
        return retorno;
    }

    public List<Horario> listarTodos(){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Cursor cursor = database.rawQuery("SELECT _id, nome, status FROM "+horarioDBHelper.TABELA, null);
        List<Horario> horarios = new ArrayList<Horario>();

        if(cursor.moveToFirst()){
            do{
                Horario umHorario = new Horario();
                umHorario.setId(cursor.getInt(0));
                try {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(df.parse(cursor.getString(1)));
                    umHorario.setNome(cal);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                umHorario.setStatus(cursor.getInt(3));
               horarios.add(umHorario);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return horarios;
    }

    public Horario listarProximoHorarioItinerario(Bairro partida, Bairro destino, String hora){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String diaAtual = "";

        switch(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)){
            case Calendar.SUNDAY:
                diaAtual = "domingo";
                break;
            case Calendar.MONDAY:
                diaAtual = "segunda";
                break;
            case Calendar.TUESDAY:
                diaAtual = "terca";
                break;
            case Calendar.WEDNESDAY:
                diaAtual = "quarta";
                break;
            case Calendar.THURSDAY:
                diaAtual = "quinta";
                break;
            case Calendar.FRIDAY:
                diaAtual = "sexta";
                break;
            case Calendar.SATURDAY:
                diaAtual = "sabado";
                break;
        }

        Cursor cursor = database.rawQuery("SELECT h._id, h.nome, h.status FROM "+HorarioItinerarioDBHelper.TABELA+" hi LEFT JOIN "
                +ItinerarioDBHelper.TABELA+" i ON i._id = hi.id_itinerario LEFT JOIN "
                +HorarioDBHelper.TABELA+" h ON h._id = hi.id_horario WHERE id_partida = ? AND id_destino = ? " +
                "AND TIME(h.nome) > ? AND "+diaAtual+" = -1 ORDER BY  TIME(h.nome) LIMIT 1",
                new String[]{String.valueOf(partida.getId()), String.valueOf(destino.getId()), hora});
        List<Horario> horarios = new ArrayList<Horario>();

        Horario umHorario = null;

        if(cursor.moveToFirst()){
            do{
                umHorario = new Horario();
                umHorario.setId(cursor.getInt(0));
                try {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(df.parse(cursor.getString(1)));
                    umHorario.setNome(cal);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                umHorario.setStatus(cursor.getInt(2));
                //horarios.add(umHorario);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return umHorario;
    }

    public Horario listarPrimeiroHorarioItinerario(Bairro partida, Bairro destino, Calendar dia){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String diaAtual = "";

        switch(dia.get(Calendar.DAY_OF_WEEK)){
            case Calendar.SUNDAY:
                diaAtual = "domingo";
                break;
            case Calendar.MONDAY:
                diaAtual = "segunda";
                break;
            case Calendar.TUESDAY:
                diaAtual = "terca";
                break;
            case Calendar.WEDNESDAY:
                diaAtual = "quarta";
                break;
            case Calendar.THURSDAY:
                diaAtual = "quinta";
                break;
            case Calendar.FRIDAY:
                diaAtual = "sexta";
                break;
            case Calendar.SATURDAY:
                diaAtual = "sabado";
                break;
        }

        String q = "SELECT h._id, h.nome, h.status FROM "+HorarioItinerarioDBHelper.TABELA+" hi LEFT JOIN "
                +ItinerarioDBHelper.TABELA+" i ON i._id = hi.id_itinerario LEFT JOIN "
                +HorarioDBHelper.TABELA+" h ON h._id = hi.id_horario WHERE id_partida = ? AND id_destino = ? AND "+diaAtual+" = -1 " +
                "ORDER BY TIME(h.nome) LIMIT 1";
        Cursor cursor = database.rawQuery("SELECT h._id, h.nome, h.status FROM "+HorarioItinerarioDBHelper.TABELA+" hi LEFT JOIN "
                        +ItinerarioDBHelper.TABELA+" i ON i._id = hi.id_itinerario LEFT JOIN "
                        +HorarioDBHelper.TABELA+" h ON h._id = hi.id_horario WHERE id_partida = ? AND id_destino = ? AND "+diaAtual+" = -1 " +
                        "ORDER BY TIME(h.nome) LIMIT 1",
                new String[]{String.valueOf(partida.getId()), String.valueOf(destino.getId())});
        List<Horario> horarios = new ArrayList<Horario>();

        Horario umHorario = null;

        if(cursor.moveToFirst()){
            do{
                umHorario = new Horario();
                umHorario.setId(cursor.getInt(0));
                try {
                    Calendar cal = Calendar.getInstance();
                    String s = cursor.getString(1);
                    cal.setTime(df.parse(cursor.getString(1)));
                    umHorario.setNome(cal);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                umHorario.setStatus(cursor.getInt(2));
                //horarios.add(umHorario);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return umHorario;
    }

    public Horario carregar(Horario horario){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Cursor cursor = database.rawQuery("SELECT _id, nome, status FROM "+horarioDBHelper.TABELA
                +" WHERE _id = ?", new String[]{String.valueOf(horario.getId())});

        Horario umHorario = null;

        if(cursor.moveToFirst()){
            do{
                umHorario = new Horario();
                umHorario.setId(cursor.getInt(0));
                try {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(df.parse(cursor.getString(1)));
                    umHorario.setNome(cal);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                umHorario.setStatus(cursor.getInt(2));

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return umHorario;
    }

    public List<Horario> listarTodosHorariosPorItinerario(Bairro bairroPartida, Bairro bairroDestino){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Cursor cursor = database.rawQuery("SELECT h._id, h.nome, h.status FROM "+horarioDBHelper.TABELA
                +" h LEFT JOIN "+HorarioItinerarioDBHelper.TABELA+" hi ON hi.id_horario = h._id LEFT JOIN "
                +ItinerarioDBHelper.TABELA+" i ON i._id = hi.id_itinerario WHERE i.id_partida = ? AND i.id_destino = ? " +
                        "ORDER BY TIME(h.nome)",
                new String[]{String.valueOf(bairroPartida.getId()), String.valueOf(bairroDestino.getId())});
        List<Horario> horarios = new ArrayList<Horario>();

        if(cursor.moveToFirst()){
            do{
                Horario umHorario = new Horario();
                umHorario.setId(cursor.getInt(0));
                try {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(df.parse(cursor.getString(1)));
                    umHorario.setNome(cal);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                umHorario.setStatus(cursor.getInt(2));
                horarios.add(umHorario);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return horarios;
    }

}
