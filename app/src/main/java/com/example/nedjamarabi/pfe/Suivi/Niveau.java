package com.example.nedjamarabi.pfe.Suivi;

/**
 * Created by Samy on 25/02/2018.
 */

public enum Niveau {
    DEBUTANT, INTERMEDIAIRE, AVANCE;
    
    private static Niveau[] values = Niveau.values();
    
    
    public static Niveau get(int index) {
        return values[index];
    }
}
