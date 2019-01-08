package com.example.nedjamarabi.pfe.Fragments;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.nedjamarabi.pfe.Managers.ConseilManager;
import com.example.nedjamarabi.pfe.Managers.UtilisateurManager;
import com.example.nedjamarabi.pfe.R;
import com.example.nedjamarabi.pfe.Suivi.Conseil;
import com.example.nedjamarabi.pfe.Suivi.Regime;
import com.example.nedjamarabi.pfe.Suivi.Utilisateur;
import com.example.nedjamarabi.pfe.Utils.DateUtils;
import com.example.nedjamarabi.pfe.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;


public class HomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private TextView conseilTextView;
    private ConseilManager cm;
    private UtilisateurManager um;
    private Conseil c;
    private Utilisateur u;
    private OnFragmentInteractionListener mListener;
    
    public HomeFragment() {
    }
    
    
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            u = (Utilisateur) getArguments().getSerializable(ARG_PARAM1);
            String param2 = getArguments().getString(ARG_PARAM2);
        }
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            FirebaseUser user = auth.getCurrentUser();
            Regime regime = new Regime(u);
            regime.calculPlanAlimentaire(u);
            TextView welcomeMessageTextView = view.findViewById(R.id.welcomeMessageTextView);
            welcomeMessageTextView.setText(u.getUsername());
            conseilTextView = view.findViewById(R.id.conseilTextView);
            LinearLayout main_layout = view.findViewById(R.id.root);
            RelativeLayout mViewCalorie = (RelativeLayout) inflater.inflate(R.layout.home_recommandation_layout, null);
            ((TextView) mViewCalorie.findViewById(R.id.macro_type)).setText("Calories");
            mViewCalorie.findViewById(R.id.layout_background).setBackground(getResources().getDrawable(R.drawable.roundtextred));
            ((ImageView) mViewCalorie.findViewById(R.id.macro_type_image)).setImageDrawable(getResources().getDrawable(R.drawable.calorie_ic));
            ((TextView) mViewCalorie.findViewById(R.id.text_qte)).setText(String.valueOf(((int) regime.getCaloriesRecommandees())) + " Kcal ");
            main_layout.addView(mViewCalorie, 1);
            RelativeLayout mViewProt = (RelativeLayout) inflater.inflate(R.layout.home_recommandation_layout, null);
            ((TextView) mViewProt.findViewById(R.id.macro_type)).setText("Protéines");
            mViewProt.findViewById(R.id.layout_background).setBackground(getResources().getDrawable(R.drawable.roundtextblue));
            ((ImageView) mViewProt.findViewById(R.id.macro_type_image)).setImageDrawable(getResources().getDrawable(R.drawable.prot_ic));
            ((TextView) mViewProt.findViewById(R.id.text_qte)).setText(String.valueOf(((int) regime.getProteinesRecommandees())) + " g ");
            main_layout.addView(mViewProt, 2);
            RelativeLayout mViewLipi = (RelativeLayout) inflater.inflate(R.layout.home_recommandation_layout, null);
            ((TextView) mViewLipi.findViewById(R.id.macro_type)).setText("Lipides");
            mViewLipi.findViewById(R.id.layout_background).setBackground(getResources().getDrawable(R.drawable.roundtextgreen));
            ((ImageView) mViewLipi.findViewById(R.id.macro_type_image)).setImageDrawable(getResources().getDrawable(R.drawable.lipide_ic));
            ((TextView) mViewLipi.findViewById(R.id.text_qte)).setText(String.valueOf(((int) regime.getLipidesRecommandees())) + " g ");
            main_layout.addView(mViewLipi, 3);
            RelativeLayout mViewGlucide = (RelativeLayout) inflater.inflate(R.layout.home_recommandation_layout, null);
            ((TextView) mViewGlucide.findViewById(R.id.macro_type)).setText("Glucides");
            mViewGlucide.findViewById(R.id.layout_background).setBackground(getResources().getDrawable(R.drawable.roundtextyello));
            ((ImageView) mViewGlucide.findViewById(R.id.macro_type_image)).setImageDrawable(getResources().getDrawable(R.drawable.carbo_ic));
            ((TextView) mViewGlucide.findViewById(R.id.text_qte)).setText(String.valueOf(((int) regime.getGlucidesRecommandees())) + " g ");
            main_layout.addView(mViewGlucide, 4);
            ProgressBar progressBar = view.findViewById(R.id.circle_progress_bar);
            progressBar.setProgressDrawable(CreateGradient(R.color.blue));
            ProgressBar progressBarBack = view.findViewById(R.id.circle_progress_bar_back);
            progressBarBack.setProgressDrawable(CreateGradient(R.color.blue_back));
            progressBarBack.setProgress(100);
            TextView progressPercentageTextView = view.findViewById(R.id.progressPercentageTextView);
            int progress = (int) (Math.abs((u.getPoids() < u.getPoidsDesire() ? u.getPoids() / u.getPoidsDesire() : u.getPoidsDesire() / u.getPoids())) * 100);
            progressBar.setProgress(progress);
            progressPercentageTextView.setText(progress + "%");
            cm = new ConseilManager();
            cm.open();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            if (ref != null) {
                if (Utils.sDataSnapshot!=null)
                {
                    
                    DataSnapshot dateDerniereConnexionRef = Utils.sDataSnapshot.child("Utilisateur").child(user.getUid()).child("dateDerniereConnexion");
                    DataSnapshot idDernierConseilRef = Utils.sDataSnapshot.child("Utilisateur").child(user.getUid()).child("idDernierConseil");
    
                    if(dateDerniereConnexionRef.exists() && idDernierConseilRef.exists()){
        
                        String dateDerniereConnexion = dateDerniereConnexionRef.getValue().toString();
                        String idDernierConseil = idDernierConseilRef.getValue().toString();
        
                        if(!dateDerniereConnexion.equals(DateUtils.stringify(new Date()))){
                            c = cm.getRandom(Utils.sDataSnapshot.child("Conseil"));
                            if (c != null) {
                                conseilTextView.setText(c.getDescription());
                                dateDerniereConnexionRef.getRef().setValue(DateUtils.stringify(new Date()));
                                idDernierConseilRef.getRef().setValue(c.getIdConseil());
                            }
                        }else{
                            c = cm.get(Utils.sDataSnapshot.child("Conseil").child(idDernierConseil));
                            if (c != null) {
                                conseilTextView.setText(c.getDescription());
                            }
                        }
                    }else{
                        c = cm.getRandom(Utils.sDataSnapshot.child("Conseil"));
                        if (c != null) {
                            conseilTextView.setText(c.getDescription());
                            dateDerniereConnexionRef.getRef().setValue(DateUtils.stringify(new Date()));
                            idDernierConseilRef.getRef().setValue(c.getIdConseil());
                        }
                    }
                    Utils.dumpDatasnapshot();
                }
                else
                {
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Utils.sDataSnapshot=dataSnapshot;
                        }
        
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                    um.open();
                    um.prepare(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            DataSnapshot dateDerniereConnexionRef = dataSnapshot.child("dateDerniereConnexion");
                            DataSnapshot idDernierConseilRef = dataSnapshot.child("idDernierConseil");
    
                            if(dateDerniereConnexionRef.exists() && idDernierConseilRef.exists()){
        
                                String dateDerniereConnexion = dateDerniereConnexionRef.getValue().toString();
                                String idDernierConseil = idDernierConseilRef.getValue().toString();
        
                                if(!dateDerniereConnexion.equals(DateUtils.stringify(new Date()))){
                                    c = cm.getRandom(dataSnapshot.child("Conseil"));
                                    if (c != null) {
                                        conseilTextView.setText(c.getDescription());
                                        dateDerniereConnexionRef.getRef().setValue(DateUtils.stringify(new Date()));
                                        idDernierConseilRef.getRef().setValue(c.getIdConseil());
                                    }
                                }else{
                                    c = cm.get(dataSnapshot.child("Conseil").child(idDernierConseil));
                                    if (c != null) {
                                        conseilTextView.setText(c.getDescription());
                                    }
                                }
                            }else{
                                c = cm.getRandom(dataSnapshot.child("Conseil"));
                                if (c != null) {
                                    conseilTextView.setText(c.getDescription());
                                    dateDerniereConnexionRef.getRef().setValue(DateUtils.stringify(new Date()));
                                    idDernierConseilRef.getRef().setValue(c.getIdConseil());
                                }
                            }
                        }
    
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                   
                }
                
            }
        }
        return view;
    }
    
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    
    
    private GradientDrawable CreateGradient(int color) {
        LayerDrawable layerDrawable;
        GradientDrawable gradientDrawable;
        layerDrawable = (LayerDrawable) getResources().getDrawable(R.drawable.circle_progress_foreground_home);
        gradientDrawable = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.progress);
        gradientDrawable.setColor(getResources().getColor(color));
        return gradientDrawable;
    }
    
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
