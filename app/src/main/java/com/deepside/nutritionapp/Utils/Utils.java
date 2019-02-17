package com.deepside.nutritionapp.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.ImageView;
import com.deepside.nutritionapp.Suivi.Aliment;
import com.deepside.nutritionapp.Suivi.Consommation;
import com.google.firebase.database.*;

import java.util.Date;

public class Utils {
    public static final String regEx = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";
    public static DataSnapshot sDataSnapshot = null;
    public static Date d = null;
    public static Consommation c = null;
    public static Aliment a = null;
    public static ImageView imgProfile;
    private static FirebaseDatabase mDatabase;

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }


    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }


    public static void dumpDatasnapshot() {
        DatabaseReference ref = Utils.getDatabase().getReference();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                sDataSnapshot = dataSnapshot;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}

