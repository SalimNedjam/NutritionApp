package com.deepside.nutritionapp.Suivi;

public enum Sexe {
    HOMME, FEMME;

    private static Sexe[] values = Sexe.values();

    public static Sexe get(int index) {
        return values[index];
    }
}
