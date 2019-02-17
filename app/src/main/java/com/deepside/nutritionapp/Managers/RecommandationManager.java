package com.deepside.nutritionapp.Managers;


import com.deepside.nutritionapp.Suivi.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class RecommandationManager extends DatabaseManager {

    public RecommandationManager() {
        TABLE_NAME = "RegimeRecommande";
    }

    public void insert(Utilisateur u, Repas r, Aliment a, float quantite) {
        DatabaseReference ref = db.child(u.getIdUtilisateur());
        DatabaseReference ref2 = ref.child(String.valueOf(r.ordinal()));
        ref2.child(String.valueOf(a.getIdAliment())).setValue(quantite);
    }

    public DatabaseReference prepare(String idUtilisateur) {
        return db.child(String.valueOf(idUtilisateur));
    }

    public Recommandation get(DataSnapshot dataSnapshot) {
        final Recommandation r = new Recommandation();
        r.setIdUtilisateur(dataSnapshot.getKey());
        for (DataSnapshot ref2 : dataSnapshot.getChildren()) {
            for (DataSnapshot ref3 : ref2.getChildren()) {
                r.getAlimentsRecommandes().add(new Consommation(Long.valueOf(ref3.getKey()), Repas.get(Integer.parseInt(ref2.getKey())), Float.parseFloat(ref3.getValue().toString())));
            }
        }
        return r;
    }

    public void delete(String idUtilisateur) {
        DatabaseReference ref = db.child(idUtilisateur);
        ref.removeValue();
    }

}
