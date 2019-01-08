package com.example.nedjamarabi.pfe.Managers;


import com.example.nedjamarabi.pfe.Suivi.Conseil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.Iterator;

public class ConseilManager extends DatabaseManager {
    
    public ConseilManager() {
        TABLE_NAME = "Conseil";
    }
    
    public void insert(Conseil c) {
        DatabaseReference ref = db.child(String.valueOf(c.getIdConseil()));
        ref.setValue(c.getDescription());
        
    }
    
    public DatabaseReference prepare(long idConseil) {
        return db.child(String.valueOf(idConseil));
    }
    
    public Conseil get(DataSnapshot dataSnapshot) {
        Conseil c = new Conseil();
        c.setIdConseil(Long.valueOf(dataSnapshot.getKey()));
        c.setDescription(dataSnapshot.getValue().toString());
        return c;
    }
    
    public DatabaseReference prepare() {
        return db;
    }
    
    public Conseil getRandom(DataSnapshot dataSnapshot) {
        Conseil c = new Conseil();
        Iterator<DataSnapshot> data = dataSnapshot.getChildren().iterator();
        long index = (long) Math.floor(Math.random() * dataSnapshot.getChildrenCount());
        long i = 0;
        while (i < index && data.hasNext()) {
            data.next();
            i++;
        }
        DataSnapshot randomData = data.next();
        c.setIdConseil(Long.valueOf(randomData.getKey()));
        c.setDescription(randomData.getValue().toString());
        return c;
    }
    
    public void delete(long idConseil) {
        DatabaseReference ref = db.child(String.valueOf(idConseil));
        ref.removeValue();
    }
    
}
