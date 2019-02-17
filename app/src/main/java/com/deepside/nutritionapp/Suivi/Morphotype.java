package com.deepside.nutritionapp.Suivi;

/**
 * Created by Samy on 25/02/2018.
 */

public enum Morphotype {
    ECTOMORPHE, MESOMORPHE, ENDOMORPHE;

    private static Morphotype[] values = Morphotype.values();


    public static Morphotype get(int index) {
        return values[index];
    }
}
