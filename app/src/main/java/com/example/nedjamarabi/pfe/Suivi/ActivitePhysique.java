package com.example.nedjamarabi.pfe.Suivi;

public enum ActivitePhysique {
    LEGERE, MODEREE, INTENSE;
    
    private static ActivitePhysique[] values = ActivitePhysique.values();
    
    
    public static ActivitePhysique get(int index) {
        return values[index];
    }
}
