package com.deepside.nutritionapp.Managers;

import com.deepside.nutritionapp.Utils.Utils;
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
