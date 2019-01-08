package com.deepside.nutritionapp.Utils;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by salimdeepside on 28/04/2018.
 */

public class LimitArrayAdapter<T> extends ArrayAdapter<T> {
    
    public LimitArrayAdapter(Context context, int textViewResourceId, Set<T> objects) {
        super(context, textViewResourceId, new ArrayList<>(objects));
    }
    
    @Override
    public int getCount() {
        int LIMIT = 8;
        return Math.min(LIMIT, super.getCount());
    }
    
}