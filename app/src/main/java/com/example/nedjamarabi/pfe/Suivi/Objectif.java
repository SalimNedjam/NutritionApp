package com.example.nedjamarabi.pfe.Suivi;

public enum Objectif {
    PERTE_POIDS, GAIN_MASSE;
    
    private static Objectif[] values = Objectif.values();
    
    
    public static Objectif get(int index) {
        return values[index];
    }
}
