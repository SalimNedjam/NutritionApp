package com.example.nedjamarabi.pfe.Managers;

import com.example.nedjamarabi.pfe.Suivi.Regime;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class RegimeManager extends DatabaseManager {
    
    public RegimeManager() {
        TABLE_NAME = "Regime";
    }
    
    public void insert(Regime r) {
        DatabaseReference ref = db.child(r.getIdUtilisateur());
        ref.child("caloriesRecommandees").setValue(r.getCaloriesRecommandees());
        ref.child("glucidesRecommandees").setValue(r.getGlucidesRecommandees());
        ref.child("lipidesRecommandees").setValue(r.getLipidesRecommandees());
        ref.child("proteinesRecommandees").setValue(r.getProteinesRecommandees());
    }
    
    public DatabaseReference prepare(String idUtilisateur) {
        return db.child(String.valueOf(idUtilisateur));
    }
    
    public Regime get(DataSnapshot dataSnapshot) {
        Regime r = new Regime();
        try{
            r.setIdUtilisateur(dataSnapshot.getKey());
            r.setCaloriesRecommandees(Float.parseFloat(dataSnapshot.child("caloriesRecommandees").getValue().toString()));
            r.setGlucidesRecommandees(Float.parseFloat(dataSnapshot.child("glucidesRecommandees").getValue().toString()));
            r.setLipidesRecommandees(Float.parseFloat(dataSnapshot.child("lipidesRecommandees").getValue().toString()));
            r.setProteinesRecommandees(Float.parseFloat(dataSnapshot.child("proteinesRecommandees").getValue().toString()));
        }catch (Exception e)
        {
            return null;
        }
        return r;
    }
    
    public void delete(String idUtilisateur) {
        DatabaseReference ref = db.child(idUtilisateur);
        ref.removeValue();
    }
}
