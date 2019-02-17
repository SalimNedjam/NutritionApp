package com.deepside.nutritionapp.Suivi;

public enum Repas {
    PETIT_DEJEUNER, SNACK1, DEJEUNER, SNACK2, DINER, SNACK3;

    private static Repas[] values = Repas.values();

    public static Repas get(int index) {
        return values[index];
    }
}
