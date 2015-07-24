package br.com.vostre.circular.utils;

import android.content.ClipData;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Almir on 26/07/2014.
 */
public class CustomAdapter<Object> extends ArrayAdapter<Object> {

    Context context;
    List<Object> objetos;
    String placeholder;
    int position = 0;

    public CustomAdapter(Context context, int resource, List<Object> objects, String placeholder, int position) {
        super(context, resource, objects);
        this.context = context;
        this.objetos = objects;
        this.position = position;
        setPlaceholder(placeholder);
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = null;
        if(position == this.position-1){
            TextView tv = new TextView(getContext());
            tv.setVisibility(View.GONE);
            v = tv;
        } else{
            v = super.getDropDownView(position, null, parent);
        }
        return v;
    }
}
