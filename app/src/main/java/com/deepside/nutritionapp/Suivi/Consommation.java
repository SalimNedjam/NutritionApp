package com.deepside.nutritionapp.Suivi;

public class Consommation {
    private long idAliment;
    private Repas repas;
    private float quantite;

    public Consommation(long idAliment, Repas repas, float quantite) {
        this.idAliment = idAliment;
        this.repas = repas;
        this.quantite = quantite;
    }

    public long getIdAliment() {
        return idAliment;
    }

    public void setIdAliment(long idAliment) {
        this.idAliment = idAliment;
    }

    public Repas getRepas() {
        return repas;
    }

    public void setRepas(Repas repas) {
        this.repas = repas;
    }

    public float getQuantite() {
        return quantite;
    }

    public void setQuantite(float quantite) {
        this.quantite = quantite;
    }

    @Override
    public String toString() {
        return "Consommation{" + "idAliment=" + idAliment + ", repas=" + repas + ", quantite=" + quantite + '}';
    }
}
