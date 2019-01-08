package com.example.nedjamarabi.pfe.Managers;


import com.example.nedjamarabi.pfe.Suivi.HistoriquePoids;
import com.example.nedjamarabi.pfe.Utils.DateUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class HistoriquePoidsManager extends DatabaseManager {
    
    public HistoriquePoidsManager() {
        TABLE_NAME = "HistoriquePoids";
    }
    
    public void insert(HistoriquePoids h) {
        DatabaseReference ref = db.child(h.getIdUtilisateur());
        ref.child(DateUtils.stringify(h.getDate())).setValue(h.getPoids());
    }
    
    public DatabaseReference prepare(String idUtilisateur, Date date) {
        return db.child(idUtilisateur).child(DateUtils.stringify(date));
    }
    
    public HistoriquePoids get(DataSnapshot dataSnapshot) {
        HistoriquePoids h = new HistoriquePoids();
        h.setIdUtilisateur(dataSnapshot.getRef().getParent().getKey());
        h.setPoids(Float.parseFloat(dataSnapshot.getValue().toString()));
        try {
            h.setDate(DateUtils.parse(dataSnapshot.getKey()));
        } catch (ParseException e) {
            h.setDate(new Date());
        }
        return h;
    }
    
    public DatabaseReference prepare(String idUtilisateur) {
        return db.child(idUtilisateur);
    }
    
    public ArrayList<HistoriquePoids> getAll(DataSnapshot dataSnapshot) {
        ArrayList<HistoriquePoids> hList = new ArrayList<>();
        HistoriquePoids h;
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            h = new HistoriquePoids();
            h.setIdUtilisateur(data.getRef().getParent().getKey());
            h.setPoids(Float.parseFloat(data.getValue().toString()));
            try {
                h.setDate(DateUtils.parse(data.getKey()));
            } catch (ParseException e) {
                h.setDate(new Date());
            }
            hList.add(h);
        }
        Collections.sort(hList,new Comparator<HistoriquePoids>() {
            @Override
            public int compare(HistoriquePoids historiquePoids, HistoriquePoids t1) {
                return historiquePoids.getDate().compareTo(t1.getDate());
            }
        });
        return hList;
    }
    
    
    public void delete(String idUtilisateur, Date date) {
        DatabaseReference ref = db.child(idUtilisateur).child(DateUtils.stringify(date));
        ref.removeValue();
    }
}
