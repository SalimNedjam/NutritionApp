package com.example.nedjamarabi.pfe.Suivi;

import java.util.ArrayList;

public class Recommandation {
    private String idUtilisateur;
    private ArrayList<Consommation> alimentsRecommandes;
    
    public Recommandation() {
        alimentsRecommandes = new ArrayList<>();
    }
    
    public Recommandation(String idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
        alimentsRecommandes = new ArrayList<>();
    }
    
    public String getIdUtilisateur() {
        return idUtilisateur;
    }
    
    public void setIdUtilisateur(String idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }
    
    public ArrayList<Consommation> getAlimentsRecommandes() {
        return alimentsRecommandes;
    }
    
    
    public void setAlimentsRecommandes(ArrayList<Consommation> alimentsRecommandes) {
        this.alimentsRecommandes = alimentsRecommandes;
    }
    
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("RegimeRecommande{" + "idUtilisateur=" + idUtilisateur + ", alimentsRecommandes=");
        for (Consommation c : alimentsRecommandes) {
            str.append(c.toString());
        }
        return str + "}";
    }
}
