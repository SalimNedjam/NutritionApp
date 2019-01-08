package com.example.nedjamarabi.pfe.Managers;

import com.example.nedjamarabi.pfe.Suivi.AlimentsFavoris;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class AlimentsFavorisManager extends DatabaseManager {
    
    public AlimentsFavorisManager() {
        TABLE_NAME = "AlimentsFavoris";
    }
    
    public void insert(String idUtilisateur, long idAliment) {
        final DatabaseReference ref = db.child(idUtilisateur).child(String.valueOf(idAliment));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    dataSnapshot.getRef().setValue(Integer.parseInt(dataSnapshot.getValue().toString()) + 1);
                } else {
                    dataSnapshot.getRef().setValue(1);
                }
            }
            
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        
    }
    
    public DatabaseReference prepare(String idUtilisateur) {
        return db.child(idUtilisateur);
    }
    
    public AlimentsFavoris get(DataSnapshot dataSnapshot) {
        AlimentsFavoris a = new AlimentsFavoris();
        a.setIdUtilisateur(dataSnapshot.getKey());
        for (DataSnapshot ref2 : dataSnapshot.getChildren()) {
            a.getAlimentsFavoris().put(Long.valueOf(ref2.getKey()), Integer.parseInt(String.valueOf(ref2.getValue().toString())));
        }
        return a;
    }
    
    public void delete(String idUtilisateur, long idAliment) {
        DatabaseReference ref = db.child(idUtilisateur);
        if (ref != null) {
            DatabaseReference ref2 = ref.child(String.valueOf(idAliment));
            ref2.removeValue();
        }
    }
    
}
