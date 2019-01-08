package com.deepside.nutritionapp.Managers;

import com.deepside.nutritionapp.Suivi.Aliment;
import com.deepside.nutritionapp.Suivi.TypeAliment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;


public class AlimentManager extends DatabaseManager {
    
    public AlimentManager() {
        TABLE_NAME = "Aliment";
    }
    
    public void insert(Aliment a) {
        DatabaseReference ref = db.child(String.valueOf(a.getIdAliment()));
        ref.child("nom").setValue(a.getNom());
        ref.child("nbCalories").setValue(a.getNbCalories());
        ref.child("quantiteGlucides").setValue(a.getQuantiteGlucides());
        ref.child("quantiteLipides").setValue(a.getQuantiteLipides());
        ref.child("quantiteProteines").setValue(a.getQuantiteProteines());
        ref.child("typeAliment").setValue(a.getTypeAliment().ordinal());
    }
    
    public DatabaseReference prepare(final long idAliment) {
        return db.child(String.valueOf(idAliment));
    }
    
    public Aliment get(DataSnapshot dataSnapshot) {
        final Aliment a = new Aliment();
        a.setIdAliment(Long.valueOf(dataSnapshot.getKey()));
        a.setNom(dataSnapshot.child("nom").getValue().toString());
        a.setNbCalories(Float.parseFloat(dataSnapshot.child("nbCalories").getValue().toString()));
        a.setQuantiteGlucides(Float.parseFloat(dataSnapshot.child("quantiteGlucides").getValue().toString()));
        a.setQuantiteLipides(Float.parseFloat(dataSnapshot.child("quantiteLipides").getValue().toString()));
        a.setQuantiteProteines(Float.parseFloat(dataSnapshot.child("quantiteProteines").getValue().toString()));
        a.setTypeAliment(TypeAliment.get(Integer.parseInt(dataSnapshot.child("typeAliment").getValue().toString())));
        return a;
    }
    
    public void delete(long idAliment) {
        DatabaseReference ref = db.child(String.valueOf(idAliment));
        ref.removeValue();
    }
    
}
