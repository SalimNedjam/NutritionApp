package com.deepside.nutritionapp.Managers;

import com.deepside.nutritionapp.Suivi.Bilan;
import com.deepside.nutritionapp.Suivi.Consommation;
import com.deepside.nutritionapp.Suivi.Repas;
import com.deepside.nutritionapp.Utils.DateUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class BilanManager extends DatabaseManager {


    public BilanManager() {
        TABLE_NAME = "Bilan";
    }

    public void insert(Bilan b) {
        DatabaseReference ref = db.child(b.getIdUtilisateur());
        DatabaseReference ref2 = ref.child(String.valueOf(DateUtils.stringify(b.getDateBilan())));
        for (Consommation c : b.getConsommations()) {
            ref2.child(String.valueOf(c.getRepas().ordinal())).child(String.valueOf(c.getIdAliment())).setValue(c.getQuantite());
        }
    }

    public void insert(String idUtilisateur, Date d, Consommation c) {
        db.child(idUtilisateur).child(String.valueOf(DateUtils.stringify(d))).child(String.valueOf(c.getRepas().ordinal())).child(String.valueOf(c.getIdAliment())).setValue(c.getQuantite());
    }

    public DatabaseReference prepare(String idUtilisateur, Date d) {
        return db.child(idUtilisateur).child(String.valueOf(DateUtils.stringify(d)));
    }

    public Bilan get(DataSnapshot dataSnapshot) {
        Bilan b = new Bilan();
        b.setIdUtilisateur(dataSnapshot.getRef().getParent().getKey());
        try {
            b.setDateBilan(DateUtils.parse(dataSnapshot.getKey()));
        } catch (ParseException e) {
            b.setDateBilan(new Date());
        }
        for (DataSnapshot ref2 : dataSnapshot.getChildren()) {
            for (DataSnapshot ref3 : ref2.getChildren()) {
                b.getConsommations().add(new Consommation(Long.parseLong(ref3.getKey()), Repas.get(Integer.parseInt(ref2.getKey())), Float.parseFloat(ref3.getValue().toString())));

            }
        }
        return b;
    }

    public DatabaseReference prepare(String idUtilisateur) {
        return db.child(idUtilisateur);
    }

    public ArrayList<Bilan> getAll(DataSnapshot dataSnapshot) {
        ArrayList<Bilan> list = new ArrayList<>();
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            Bilan b = new Bilan();
            b.setIdUtilisateur(data.getRef().getParent().getKey());
            try {
                b.setDateBilan(DateUtils.parse(data.getKey()));
            } catch (ParseException e) {
                b.setDateBilan(new Date());
            }
            for (DataSnapshot ref2 : data.getChildren()) {
                for (DataSnapshot ref3 : ref2.getChildren()) {
                    b.getConsommations().add(new Consommation(Long.parseLong(ref3.getKey()), Repas.get(Integer.parseInt(ref2.getKey())), Float.parseFloat(ref3.getValue().toString())));

                }
            }
            list.add(b);
        }
        Collections.sort(list, new Comparator<Bilan>() {
            @Override
            public int compare(Bilan bilan, Bilan t1) {
                return bilan.getDateBilan().compareTo(t1.getDateBilan());
            }
        });
        return list;
    }

    public void delete(String idUtilisateur, Date d) {
        DatabaseReference ref = db.child(idUtilisateur);
        if (ref != null) {
            DatabaseReference ref2 = ref.child(String.valueOf(String.valueOf(DateUtils.stringify(d))));
            ref2.removeValue();
        }
    }

    public void delete(String idUtilisateur, Date d, Repas r, long idAliment) {
        DatabaseReference ref = db.child(idUtilisateur);
        if (ref != null) {
            DatabaseReference ref2 = ref.child(DateUtils.stringify(d)).child(String.valueOf(r.ordinal())).child(String.valueOf(idAliment));
            ref2.removeValue();
        }
    }

}
