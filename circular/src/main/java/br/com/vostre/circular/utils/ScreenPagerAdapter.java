package br.com.vostre.circular.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;

import java.util.ArrayList;

import br.com.vostre.circular.FragmentTodosHorarios;

/**
 * Created by Almir on 17/06/2015.
 */
public class ScreenPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> views = new ArrayList<Fragment>();

    public ScreenPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public int addView(Fragment v, int posicao){
        views.add(posicao, v);
        return posicao;
    }

    @Override
    public Fragment getItem(int position) {
        return views.get(position);
    }

    @Override
    public int getCount() {
        return views.size();
        //return 3;
    }
}
