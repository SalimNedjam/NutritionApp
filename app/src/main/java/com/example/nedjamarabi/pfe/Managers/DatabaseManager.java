package com.example.nedjamarabi.pfe.Managers;

import com.example.nedjamarabi.pfe.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public abstract class DatabaseManager {
    
    DatabaseReference db;
    String TABLE_NAME;
    
    DatabaseManager() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
    }
    
    public void open() {
        db = Utils.getDatabase().getReference(TABLE_NAME);
    }
    
}
