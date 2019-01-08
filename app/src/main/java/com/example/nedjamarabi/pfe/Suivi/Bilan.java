package com.example.nedjamarabi.pfe.Suivi;


import android.support.annotation.NonNull;

import com.example.nedjamarabi.pfe.Utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;

public class Bilan implements Comparable<Bilan>{
    private String idUtilisateur;
    private Date dateBilan;
    private ArrayList<Consommation> consommations;
    
    public Bilan() {
        consommations = new ArrayList<>();
    }
    
    public Bilan(String idUtilisateur, Date dateBilan) {
        this.idUtilisateur = idUtilisateur;
        this.dateBilan = dateBilan;
        consommations = new ArrayList<>();
    }
    
    public String getIdUtilisateur() {
        return idUtilisateur;
    }
    
    public void setIdUtilisateur(String idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }
    
    public Date getDateBilan() {
        return dateBilan;
    }
    
    public void setDateBilan(Date dateBilan) {
        this.dateBilan = dateBilan;
    }
    
    public ArrayList<Consommation> getConsommations() {
        return consommations;
    }
    
    public void setConsommations(ArrayList<Consommation> consommations) {
        this.consommations = consommations;
    }
    
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("Bilan{" + "idUtilisateur=" + idUtilisateur + ", dateBilan=" + DateUtils.stringify(dateBilan) + ", consommations=");
        for (Consommation c : consommations) {
            str.append(c.toString()).append(" ");
        }
        return str + "}";
    }    
    
    @Override
    public int compareTo(@NonNull Bilan o) {
        return getDateBilan().compareTo(o.getDateBilan());
    }
}
