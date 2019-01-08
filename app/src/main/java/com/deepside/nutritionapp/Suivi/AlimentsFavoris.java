package com.deepside.nutritionapp.Suivi;


import com.deepside.nutritionapp.Utils.OccurenceRechercheAliment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AlimentsFavoris {
    private String idUtilisateur;
    private HashMap<Long, Integer> alimentsFavoris;
    
    public AlimentsFavoris() {
        alimentsFavoris = new HashMap<>();
    }
    
    public AlimentsFavoris(String idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
        alimentsFavoris = new HashMap<>();
    }
    
    public String getIdUtilisateur() {
        return idUtilisateur;
    }
    
    public void setIdUtilisateur(String idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }
    
    public HashMap<Long, Integer> getAlimentsFavoris() {
        return alimentsFavoris;
    }
    
    public void setAlimentsFavoris(HashMap<Long, Integer> alimentsFavoris) {
        this.alimentsFavoris = alimentsFavoris;
    }
    
    @Override
    public String toString() {
        return "AlimentsFavoris{" + "idUtilisateur=" + idUtilisateur + ", alimentsFavoris=" + alimentsFavoris.toString() + '}';
    }
    
    public ArrayList<OccurenceRechercheAliment> trierListeAliments() {
        Map<Long, Integer> unsortMap = alimentsFavoris;
        List<Map.Entry<Long, Integer>> list = new LinkedList<>(unsortMap.entrySet());
        ArrayList<OccurenceRechercheAliment> sortedList = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : list) {
            sortedList.add(new OccurenceRechercheAliment(entry.getKey(), entry.getValue()));
        }
        Collections.sort(sortedList, new MyComparator());
        return sortedList;
    }
    
    class MyComparator implements Comparator<OccurenceRechercheAliment> {
        public int compare(OccurenceRechercheAliment o1, OccurenceRechercheAliment o2) {
            return o2.getNbRecherches().compareTo(o1.getNbRecherches());
        }
    }
    
}
