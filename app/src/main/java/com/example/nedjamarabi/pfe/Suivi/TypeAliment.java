package com.example.nedjamarabi.pfe.Suivi;

public enum TypeAliment {
    SOLIDE, LIQUIDE;
    
    private static TypeAliment[] values = TypeAliment.values();
    
    
    public static TypeAliment get(int index) {
        return values[index];
    }
}
