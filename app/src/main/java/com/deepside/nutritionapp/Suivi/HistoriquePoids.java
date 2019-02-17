package com.deepside.nutritionapp.Suivi;


import android.support.annotation.NonNull;
import com.deepside.nutritionapp.Utils.Point;

import java.util.Date;

/**
 * Created by Samy on 21/02/2018.
 */

public class HistoriquePoids implements Comparable<HistoriquePoids> {
    private String idUtilisateur;
    private Date date;
    private float poids;

    public HistoriquePoids() {
    }

    public HistoriquePoids(String idUtilisateur, Date date, float poids) {
        this.idUtilisateur = idUtilisateur;
        this.date = date;
        this.poids = poids;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getPoids() {
        return poids;
    }

    public void setPoids(float poids) {
        this.poids = poids;
    }

    public String getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(String idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public Point getPoint() {
        return new Point(date, poids);
    }

    @Override
    public String toString() {
        return "HistoriquePoids{" + "idUtilisateur=" + idUtilisateur + ", date=" + date + ", poids=" + poids + '}';
    }

    @Override
    public int compareTo(@NonNull HistoriquePoids o) {
        return getDate().compareTo(o.getDate());
    }
}
