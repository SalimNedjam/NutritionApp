package com.deepside.nutritionapp.Fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.deepside.nutritionapp.Managers.AlimentManager;
import com.deepside.nutritionapp.Managers.RecommandationManager;
import com.deepside.nutritionapp.R;
import com.deepside.nutritionapp.Suivi.Aliment;
import com.deepside.nutritionapp.Suivi.Bilan;
import com.deepside.nutritionapp.Suivi.Consommation;
import com.deepside.nutritionapp.Suivi.Objectif;
import com.deepside.nutritionapp.Suivi.Recommandation;
import com.deepside.nutritionapp.Suivi.Regime;
import com.deepside.nutritionapp.Suivi.Repas;
import com.deepside.nutritionapp.Suivi.TypeAliment;
import com.deepside.nutritionapp.Suivi.Utilisateur;
import com.deepside.nutritionapp.Utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

public class RecommandéFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ArrayList mAlimentDayArray;
    private CheckBox snack1CheckBox;
    private CheckBox snack2CheckBox;
    private CheckBox snack3CheckBox;
    private Consommation conso;
    private float[] repasPourcentage = {0, 0, 0, 0, 0, 0};
    private Regime regime;
    private View mView;
    private Dialog myDialog;
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;
    private Utilisateur user;
    private boolean repasVisibility[] = {false, false, false};
    private int nbRepas = 6;
    private ArrayList<Consommation> mAlimentListBreakfast, mAlimentListLunch, mAlimentListDinner, mAlimentListSnack1, mAlimentListSnack2, mAlimentListSnack3;
    private String mParam1;
    private ListView mListViewBreakfast, mListViewLunch, mListViewDinner, mListViewSnack1, mListViewSnack2, mListViewSnack3;
    private LinearLayout mViewBreakfast, mViewLunch, mViewDinner, mViewSnack1, mViewSnack2, mViewSnack3;
    private LayoutInflater mInflater;
    private OnFragmentInteractionListener mListener;
    private AlimentManager am;
    private ArrayList<Bilan> bilans;
    private HashMap<Long, Aliment> alimentsSet;
    
    public RecommandéFragment() {
    }
    
    
    public static RecommandéFragment newInstance(String param1, String param2) {
        RecommandéFragment fragment = new RecommandéFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    
    private static void setListViewHeightBasedOnItems(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {
            int numberOfItems = listAdapter.getCount();
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                float px = 500 * (listView.getResources().getDisplayMetrics().density);
                item.measure(View.MeasureSpec.makeMeasureSpec((int) px, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalItemsHeight += item.getMeasuredHeight();
            }
            int totalDividersHeight = listView.getDividerHeight() * (numberOfItems - 1);
            int totalPadding = listView.getPaddingTop() + listView.getPaddingBottom();
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight + totalPadding;
            listView.setLayoutParams(params);
            listView.requestLayout();
            
        }
        
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (Utilisateur) getArguments().getSerializable(ARG_PARAM1);
            String param2 = getArguments().getString(ARG_PARAM2);
        }
        ArrayList<ImageView> addButtons = new ArrayList<>();
        mAlimentListBreakfast = new ArrayList<>();
        mAlimentListSnack1 = new ArrayList<>();
        mAlimentListLunch = new ArrayList<>();
        mAlimentListSnack2 = new ArrayList<>();
        mAlimentListDinner = new ArrayList<>();
        mAlimentListSnack3 = new ArrayList<>();
        mAlimentDayArray = new ArrayList<ArrayList>();
        mAlimentDayArray.add(0, mAlimentListBreakfast);
        mAlimentDayArray.add(1, mAlimentListSnack1);
        mAlimentDayArray.add(2, mAlimentListLunch);
        mAlimentDayArray.add(3, mAlimentListSnack2);
        mAlimentDayArray.add(4, mAlimentListDinner);
        mAlimentDayArray.add(5, mAlimentListSnack3);
        regime=new Regime(user);
        regime.calculPlanAlimentaire(user);
        
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        mView = inflater.inflate(R.layout.fragment_recommandee, container, false);
        myDialog = new Dialog(mView.getContext());
        initRepasView();
        return mView;
    }
    
    private void initRepasView() {
        LinearLayout main_layout = mView.findViewById(R.id.root);
        mViewBreakfast = (LinearLayout) mInflater.inflate(R.layout.repas_layout, null);
        ((TextView) mViewBreakfast.findViewById(R.id.repas_name)).setText("Petit déjeuner");
        mListViewBreakfast = mViewBreakfast.findViewById(R.id.listView_repas);
        main_layout.addView(mViewBreakfast, 0);
        mViewSnack1 = (LinearLayout) mInflater.inflate(R.layout.repas_layout, null);
        ((TextView) mViewSnack1.findViewById(R.id.repas_name)).setText("Gouter 1");
        mListViewSnack1 = mViewSnack1.findViewById(R.id.listView_repas);
        main_layout.addView(mViewSnack1, 1);
        mViewLunch = (LinearLayout) mInflater.inflate(R.layout.repas_layout, null);
        ((TextView) mViewLunch.findViewById(R.id.repas_name)).setText("Déjeuner");
        mListViewLunch = mViewLunch.findViewById(R.id.listView_repas);
        main_layout.addView(mViewLunch, 2);
        mViewSnack2 = (LinearLayout) mInflater.inflate(R.layout.repas_layout, null);
        ((TextView) mViewSnack2.findViewById(R.id.repas_name)).setText("Gouter 2");
        mListViewSnack2 = mViewSnack2.findViewById(R.id.listView_repas);
        main_layout.addView(mViewSnack2, 3);
        mViewDinner = (LinearLayout) mInflater.inflate(R.layout.repas_layout, null);
        ((TextView) mViewDinner.findViewById(R.id.repas_name)).setText("Dinner");
        mListViewDinner = mViewDinner.findViewById(R.id.listView_repas);
        main_layout.addView(mViewDinner, 4);
        mViewSnack3 = (LinearLayout) mInflater.inflate(R.layout.repas_layout, null);
        ((TextView) mViewSnack3.findViewById(R.id.repas_name)).setText("Gouter 3");
        mListViewSnack3 = mViewSnack3.findViewById(R.id.listView_repas);
        main_layout.addView(mViewSnack3, 5);
        mViewBreakfast.findViewById(R.id.add_aliment_repas).setVisibility(View.GONE);
        mViewSnack1.findViewById(R.id.add_aliment_repas).setVisibility(View.GONE);
        mViewLunch.findViewById(R.id.add_aliment_repas).setVisibility(View.GONE);
        mViewSnack2.findViewById(R.id.add_aliment_repas).setVisibility(View.GONE);
        mViewDinner.findViewById(R.id.add_aliment_repas).setVisibility(View.GONE);
        mViewSnack3.findViewById(R.id.add_aliment_repas).setVisibility(View.GONE);
        mListViewBreakfast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShowPopup(mView, i, 0);
            }
        });
        mListViewSnack1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShowPopup(mView, i, 1);
            }
        });
        mListViewLunch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShowPopup(mView, i, 2);
            }
        });
        mListViewSnack2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShowPopup(mView, i, 3);
            }
        });
        mListViewDinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShowPopup(mView, i, 4);
            }
        });
        mListViewSnack3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShowPopup(mView, i, 5);
            }
        });
        snack1CheckBox = mView.findViewById(R.id.Snack1CheckBox);
        snack2CheckBox = mView.findViewById(R.id.Snack2CheckBox);
        snack3CheckBox = mView.findViewById(R.id.Snack3CheckBox);
        ArrayList<Long> idAlimentsSet = new ArrayList<>();
        alimentsSet = new HashMap<>();
        for (Object arrayList : mAlimentDayArray)
            ((ArrayList) arrayList).clear();
        RecommandationManager rm = new RecommandationManager();
        rm.open();
        am = new AlimentManager();
        showCheckBox();
    
    
        if (Utils.sDataSnapshot.child("RegimeRecommande").child(user.getIdUtilisateur()).exists())
        {
            Recommandation recommandation = rm.get(Utils.sDataSnapshot.child("RegimeRecommande").child(user.getIdUtilisateur()));
            ArrayList<Consommation> consommations = recommandation.getAlimentsRecommandes();
            HashSet <Integer> hashSet=new HashSet<>();
            System.out.println(consommations);
            for (Consommation c : consommations) {
                idAlimentsSet.add(c.getIdAliment());
                Consommation consommation = new Consommation(c.getIdAliment(), c.getRepas(), c.getQuantite());
                ((ArrayList<Consommation>) mAlimentDayArray.get(c.getRepas().ordinal())).add(consommation);
                hashSet.add(c.getRepas().ordinal());
            
            }
            for (Long l : idAlimentsSet) {
                if (Utils.sDataSnapshot.child("Aliment").child(String.valueOf(l)).exists()) {
                    Aliment aliment = am.get(Utils.sDataSnapshot.child("Aliment").child(String.valueOf(l)));
                    alimentsSet.put(l, aliment);
                }
            }
            updateList(mAlimentListBreakfast, mListViewBreakfast);
            updateList(mAlimentListSnack1, mListViewSnack1);
            updateList(mAlimentListLunch, mListViewLunch);
            updateList(mAlimentListSnack2, mListViewSnack2);
            updateList(mAlimentListDinner, mListViewDinner);
            updateList(mAlimentListSnack3, mListViewSnack3);
            updateTotal();
            setListViewHeightBasedOnItems(mListViewBreakfast);
            setListViewHeightBasedOnItems(mListViewSnack1);
            setListViewHeightBasedOnItems(mListViewLunch);
            setListViewHeightBasedOnItems(mListViewSnack2);
            setListViewHeightBasedOnItems(mListViewDinner);
            setListViewHeightBasedOnItems(mListViewSnack3);
        
            nbRepas=hashSet.size();
            if (hashSet.contains(1))
            {
                snack1CheckBox.setChecked(true);
                mViewSnack1.setVisibility(View.VISIBLE);
                repasVisibility[0] = true;
            
            } else {
                snack1CheckBox.setChecked(false);
                mViewSnack1.setVisibility(View.GONE);
                repasVisibility[0] = false;
            }
            if (hashSet.contains(3))
            {
                snack2CheckBox.setChecked(true);
                mViewSnack2.setVisibility(View.VISIBLE);
                repasVisibility[1] = true;
            
            } else {
                snack2CheckBox.setChecked(false);
                mViewSnack2.setVisibility(View.GONE);
                repasVisibility[1] = false;
            }
            if (hashSet.contains(5))
            {
                snack3CheckBox.setChecked(true);
                mViewSnack3.setVisibility(View.VISIBLE);
                repasVisibility[2] = true;
            
            } else {
                snack3CheckBox.setChecked(false);
                mViewSnack3.setVisibility(View.GONE);
                repasVisibility[2] = false;
            }
        
            snack1CheckBox.setOnCheckedChangeListener(onCheckedChangeListener);
            snack2CheckBox.setOnCheckedChangeListener(onCheckedChangeListener);
            snack3CheckBox.setOnCheckedChangeListener(onCheckedChangeListener);
        
        }
        else
        {
            
            snack1CheckBox.setOnCheckedChangeListener(onCheckedChangeListener);
            snack2CheckBox.setOnCheckedChangeListener(onCheckedChangeListener);
            snack3CheckBox.setOnCheckedChangeListener(onCheckedChangeListener);
            snack1CheckBox.setChecked(false);
            snack2CheckBox.setChecked(false);
            snack3CheckBox.setChecked(false);
            nbRepas=3;
        
        }
        
        
    }
    
    
    private void updateList(ArrayList<Consommation> arrayList, ListView listView) {
        List<Map<String, String>> data = new ArrayList<>();
        for (Consommation item : arrayList) {
            HashMap<String, String> dataMap = new HashMap<>();
            dataMap.put("name", alimentsSet.get(item.getIdAliment()).getNom()); //icon
            dataMap.put("qte", (int) item.getQuantite() + " g");
            data.add(dataMap);
        }
        ListAdapter listAdapter = new SimpleAdapter(getApplicationContext(), data, R.layout.list_item, new String[]{"name", "qte"}, new int[]{R.id.name, R.id.qte});
        listView.setAdapter(listAdapter);
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
    
    private void showCheckBox() {
        onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton == snack1CheckBox) {
                    if (compoundButton.isChecked()) {
                        mViewSnack1.setVisibility(View.VISIBLE);
                        repasVisibility[0] = true;
                        nbRepas += 1;
                    } else {
                        mViewSnack1.setVisibility(View.GONE);
                        repasVisibility[0] = false;
                        nbRepas -= 1;
                        
                    }
                    
                } else if (compoundButton == snack2CheckBox) {
                    if (compoundButton.isChecked()) {
                        mViewSnack2.setVisibility(View.VISIBLE);
                        repasVisibility[1] = true;
                        nbRepas += 1;
                        
                    } else {
                        mViewSnack2.setVisibility(View.GONE);
                        repasVisibility[1] = false;
                        nbRepas -= 1;
                        
                    }
                } else if (compoundButton == snack3CheckBox) {
                    if (compoundButton.isChecked()) {
                        mViewSnack3.setVisibility(View.VISIBLE);
                        repasVisibility[2] = true;
                        nbRepas += 1;
                        
                    } else {
                        mViewSnack3.setVisibility(View.GONE);
                        repasVisibility[2] = false;
                        nbRepas -= 1;
                        
                    }
                }
                calculeRepartition();
                
            }
        };
        
    }
    
    private void calculeRepartition() {
        switch (nbRepas) {
            case 3:
                repasPourcentage[0] = 0.30f;
                repasPourcentage[1] = 0;
                repasPourcentage[2] = 0.40f;
                repasPourcentage[3] = 0;
                repasPourcentage[4] = 0.30f;
                repasPourcentage[5] = 0;
                break;
            case 4:
                repasPourcentage[0] = 0.25f;
                repasPourcentage[2] = 0.30f;
                repasPourcentage[4] = 0.25f;
                if (repasVisibility[0]) {
                    repasPourcentage[1] = 0.2f;
                    repasPourcentage[3] = 0;
                    repasPourcentage[5] = 0;
                } else if (repasVisibility[1]) {
                    repasPourcentage[1] = 0;
                    repasPourcentage[3] = 0.2f;
                    repasPourcentage[5] = 0;
                } else if (repasVisibility[2]) {
                    repasPourcentage[1] = 0;
                    repasPourcentage[3] = 0;
                    repasPourcentage[5] = 0.2f;
                }
                break;
            case 5:
                repasPourcentage[0] = 0.225f;
                repasPourcentage[2] = 0.25f;
                repasPourcentage[4] = 0.225f;
                if (!repasVisibility[0]) {
                    repasPourcentage[1] = 0;
                    repasPourcentage[3] = 0.15f;
                    repasPourcentage[5] = 0.15f;
                } else if (!repasVisibility[1]) {
                    repasPourcentage[1] = 0.15f;
                    repasPourcentage[3] = 0;
                    repasPourcentage[4] = 0.15f;
                } else if (!repasVisibility[2]) {
                    repasPourcentage[1] = 0.15f;
                    repasPourcentage[3] = 0.15f;
                    repasPourcentage[5] = 0;
                }
                break;
            case 6:
                repasPourcentage[0] = 0.1750f;
                repasPourcentage[1] = 0.15f;
                repasPourcentage[2] = 0.20f;
                repasPourcentage[3] = 0.15f;
                repasPourcentage[4] = 0.1750f;
                repasPourcentage[5] = 0.15f;
                break;
        }
        
        
        recalibrage();
        
        
    }
    
    private void recalibrage() {
        regime.calculPlanAlimentaire(user);
        am.open();

        alimentsSet.put(1L, new Aliment(1, "Flocon d'avoine", 356, 60, 8, 11, TypeAliment.SOLIDE));
        alimentsSet.put(2L, new Aliment(2, "Banane", 89, 23, 0, 1, TypeAliment.SOLIDE));
        alimentsSet.put(3L, new Aliment(3, "Riz Basmati cuit", 118, 25, 0, 4, TypeAliment.SOLIDE));
        alimentsSet.put(4L, new Aliment(4, "Patate douce cuite", 86, 18, 0, 0, TypeAliment.SOLIDE));
        alimentsSet.put(5L, new Aliment(5, "Blanc d'oeuf", 52, 0.7f, 0.2f, 11, TypeAliment.SOLIDE));
        alimentsSet.put(6L, new Aliment(6, "Blanc de poulet", 125, 1.3f, 1.8f, 21, TypeAliment.SOLIDE));
        alimentsSet.put(7L, new Aliment(7, "Steak Haché Bœuf 5% MG", 145, 0, 5, 25, TypeAliment.SOLIDE));
        alimentsSet.put(8L, new Aliment(8, "Avocat", 169, 3.1f, 16, 1.8f, TypeAliment.SOLIDE));
        alimentsSet.put(9L, new Aliment(9, "Huile d'olive", 884, 0, 100, 0, TypeAliment.SOLIDE));
        alimentsSet.put(10L, new Aliment(10, "Jaune d'oeuf", 345, 0, 31, 16, TypeAliment.SOLIDE));
        alimentsSet.put(11L, new Aliment(11, "Frommage 0%", 49, 0, 0, 8, TypeAliment.SOLIDE));
        alimentsSet.put(12L, new Aliment(12L, "Salade verte", 13, 2, 0, 1, TypeAliment.SOLIDE));
        float pro, lip, glu, qte;
        Aliment aliment;
        RecommandationManager recommandationManager=new RecommandationManager();
        recommandationManager.open();
        recommandationManager.delete(user.getIdUtilisateur());
        if (repasPourcentage[0] != 0) {
            mAlimentListBreakfast.clear();
            pro = regime.getProteinesRecommandees() * repasPourcentage[0];
            glu = regime.getGlucidesRecommandees() * repasPourcentage[0];
            lip = regime.getLipidesRecommandees() * repasPourcentage[0];
            aliment = alimentsSet.get(10L);
            qte = lip / aliment.getQuantiteLipides();
            pro -= aliment.getQuantiteProteines() * qte;
            mAlimentListBreakfast.add(new Consommation(10, Repas.get(0), qte * 100));
            aliment = alimentsSet.get(5L);
            qte = pro / aliment.getQuantiteProteines();
            glu -= aliment.getQuantiteGlucides() * qte;
            mAlimentListBreakfast.add(new Consommation(5, Repas.get(0), qte * 100));
            if (user.getObjectif()== Objectif.GAIN_MASSE)
            {
                aliment = alimentsSet.get(1L);
                qte = glu * 0.75f/aliment.getQuantiteGlucides();
                mAlimentListBreakfast.add(new Consommation(1, Repas.get(0), qte * 100));
                aliment = alimentsSet.get(2L);
                qte = glu * 0.25f/ aliment.getQuantiteGlucides();
                mAlimentListBreakfast.add(new Consommation(2, Repas.get(0), qte * 100));
            }
            else {
                aliment = alimentsSet.get(1L);
                qte = glu / aliment.getQuantiteGlucides();
                mAlimentListBreakfast.add(new Consommation(1, Repas.get(0), qte * 100));
            }
            updateList(mAlimentListBreakfast, mListViewBreakfast);
            setListViewHeightBasedOnItems(mListViewBreakfast);
            for (Consommation c:mAlimentListBreakfast)
            {
                recommandationManager.insert(user,c.getRepas(),alimentsSet.get(c.getIdAliment()),c.getQuantite());
            }
        }
        if (repasPourcentage[1] != 0) {
            mAlimentListSnack1.clear();
            pro = regime.getProteinesRecommandees() * repasPourcentage[1];
            glu = regime.getGlucidesRecommandees() * repasPourcentage[1];
            lip = regime.getLipidesRecommandees() * repasPourcentage[1];
            aliment = alimentsSet.get(10L);
            qte = lip / aliment.getQuantiteLipides();
            pro -= aliment.getQuantiteProteines() * qte;
            mAlimentListSnack1.add(new Consommation(10, Repas.get(1), qte * 100));
            aliment = alimentsSet.get(5L);
            qte = pro / aliment.getQuantiteProteines();
            glu -= aliment.getQuantiteGlucides() * qte;
            mAlimentListSnack1.add(new Consommation(5, Repas.get(1), qte * 100));
            if (user.getObjectif()== Objectif.GAIN_MASSE)
            {
                aliment = alimentsSet.get(1L);
                qte = glu * 0.75f/aliment.getQuantiteGlucides();
                mAlimentListSnack1.add(new Consommation(1, Repas.get(1), qte * 100));
                aliment = alimentsSet.get(2L);
                qte = glu * 0.25f/ aliment.getQuantiteGlucides();
                mAlimentListSnack1.add(new Consommation(2, Repas.get(1), qte * 100));
            }
            else {
                aliment = alimentsSet.get(1L);
                qte = glu / aliment.getQuantiteGlucides();
                mAlimentListSnack1.add(new Consommation(1, Repas.get(1), qte * 100));
            }
            updateList(mAlimentListSnack1, mListViewSnack1);
            setListViewHeightBasedOnItems(mListViewSnack1);
            for (Consommation c:mAlimentListSnack1)
            {
                recommandationManager.insert(user,c.getRepas(),alimentsSet.get(c.getIdAliment()),c.getQuantite());
            }
        }
        if (repasPourcentage[2] != 0) {
            mAlimentListLunch.clear();
            pro = regime.getProteinesRecommandees() * repasPourcentage[2];
            glu = regime.getGlucidesRecommandees() * repasPourcentage[2];
            lip = regime.getLipidesRecommandees() * repasPourcentage[2];
            aliment = alimentsSet.get(7L);
            qte = pro / aliment.getQuantiteProteines();
            glu -= aliment.getQuantiteGlucides() * qte;
            lip -= aliment.getQuantiteLipides() * qte;
            mAlimentListLunch.add(new Consommation(7, Repas.get(2), qte * 100));
            aliment = alimentsSet.get(3L);
            qte = glu / aliment.getQuantiteGlucides();
            lip -= aliment.getQuantiteLipides() * qte;
            mAlimentListLunch.add(new Consommation(3, Repas.get(2), qte * 100));
            aliment = alimentsSet.get(9L);
            qte = lip/ aliment.getQuantiteLipides();
            mAlimentListLunch.add(new Consommation(9, Repas.get(2), qte * 100));
            mAlimentListLunch.add(new Consommation(12, Repas.get(2), 100));
            
            updateList(mAlimentListLunch, mListViewLunch);
            setListViewHeightBasedOnItems(mListViewLunch);
            for (Consommation c:mAlimentListLunch)
            {
                recommandationManager.insert(user,c.getRepas(),alimentsSet.get(c.getIdAliment()),c.getQuantite());
            }
        }
        if (repasPourcentage[3] != 0) {
            mAlimentListSnack2.clear();
            pro = regime.getProteinesRecommandees() * repasPourcentage[3];
            glu = regime.getGlucidesRecommandees() * repasPourcentage[3];
            lip = regime.getLipidesRecommandees() * repasPourcentage[3];
            aliment = alimentsSet.get(10L);
            qte = lip / aliment.getQuantiteLipides();
            pro -= aliment.getQuantiteProteines() * qte;
            mAlimentListSnack2.add(new Consommation(10, Repas.get(3), qte * 100));
            aliment = alimentsSet.get(5L);
            qte = pro / aliment.getQuantiteProteines();
            glu -= aliment.getQuantiteGlucides() * qte;
            mAlimentListSnack2.add(new Consommation(5, Repas.get(3), qte * 100));
            if (user.getObjectif()== Objectif.GAIN_MASSE)
            {
                aliment = alimentsSet.get(1L);
                qte = glu * 0.75f/aliment.getQuantiteGlucides();
                mAlimentListSnack2.add(new Consommation(1, Repas.get(3), qte * 100));
                aliment = alimentsSet.get(2L);
                qte = glu *0.25f/ aliment.getQuantiteGlucides();
                mAlimentListSnack2.add(new Consommation(2, Repas.get(3), qte * 100));
            }
            else {
                aliment = alimentsSet.get(1L);
                qte = glu / aliment.getQuantiteGlucides();
                mAlimentListSnack2.add(new Consommation(1, Repas.get(3), qte * 100));
            }
            updateList(mAlimentListSnack2, mListViewSnack2);
            setListViewHeightBasedOnItems(mListViewSnack2);
            for (Consommation c:mAlimentListSnack2)
            {
                recommandationManager.insert(user,c.getRepas(),alimentsSet.get(c.getIdAliment()),c.getQuantite());
            }
        }
        if (repasPourcentage[4] != 0) {
            mAlimentListDinner.clear();
            pro = regime.getProteinesRecommandees() * repasPourcentage[2];
            glu = regime.getGlucidesRecommandees() * repasPourcentage[2];
            lip = regime.getLipidesRecommandees() * repasPourcentage[2];
            aliment = alimentsSet.get(6L);
            qte = pro / aliment.getQuantiteProteines();
            glu -= aliment.getQuantiteGlucides() * qte;
            lip -= aliment.getQuantiteLipides() * qte;
            mAlimentListDinner.add(new Consommation(6, Repas.get(4), qte * 100));
            aliment = alimentsSet.get(4L);
            qte = glu / aliment.getQuantiteGlucides();
            lip -= aliment.getQuantiteLipides() * qte;
            mAlimentListDinner.add(new Consommation(4, Repas.get(4), qte * 100));
            aliment = alimentsSet.get(9L);
            qte = lip / aliment.getQuantiteLipides();
            mAlimentListDinner.add(new Consommation(9, Repas.get(4), qte * 100));
            mAlimentListDinner.add(new Consommation(12, Repas.get(4), 100));
            updateList(mAlimentListDinner, mListViewDinner);
            setListViewHeightBasedOnItems(mListViewDinner);
            for (Consommation c:mAlimentListDinner)
            {
                recommandationManager.insert(user,c.getRepas(),alimentsSet.get(c.getIdAliment()),c.getQuantite());
            }
        }
        if (repasPourcentage[5] != 0) {
            mAlimentListSnack3.clear();
            pro = regime.getProteinesRecommandees() * repasPourcentage[5];
            glu = regime.getGlucidesRecommandees() * repasPourcentage[5];
            lip = regime.getLipidesRecommandees() * repasPourcentage[5];
            aliment = alimentsSet.get(11L);
            qte = pro / aliment.getQuantiteProteines();
            mAlimentListSnack3.add(new Consommation(11, Repas.get(5), qte * 100));
            aliment = alimentsSet.get(8L);
            qte = lip / aliment.getQuantiteLipides();
            glu -= aliment.getQuantiteGlucides() * qte;
            mAlimentListSnack3.add(new Consommation(8L, Repas.get(5), qte * 100));
            if (user.getObjectif()== Objectif.GAIN_MASSE)
            {
                aliment = alimentsSet.get(1L);
                qte = glu * 0.75f/aliment.getQuantiteGlucides();
                mAlimentListSnack3.add(new Consommation(1, Repas.get(5), qte * 100));
                aliment = alimentsSet.get(2L);
                qte = glu * 0.25f/ aliment.getQuantiteGlucides();
                mAlimentListSnack3.add(new Consommation(2, Repas.get(5), qte * 100));
            }
            else {
                aliment = alimentsSet.get(1L);
                qte = glu / aliment.getQuantiteGlucides();
                mAlimentListSnack3.add(new Consommation(1, Repas.get(5), qte * 100));
            }
            updateList(mAlimentListSnack3, mListViewSnack3);
            setListViewHeightBasedOnItems(mListViewSnack3);
            for (Consommation c:mAlimentListSnack3)
            {
                recommandationManager.insert(user,c.getRepas(),alimentsSet.get(c.getIdAliment()),c.getQuantite());
            }
        }
        updateTotal();
        Utils.dumpDatasnapshot();
        
        
    }
    
    
    private void ShowPopup(View v, final int id, final int list) {
        ImageView exitView;
        switch (list) {
            case 0:
                conso = mAlimentListBreakfast.get(id);
                break;
            case 1:
                conso = mAlimentListSnack1.get(id);
                break;
            case 2:
                conso = mAlimentListLunch.get(id);
                break;
            case 3:
                conso = mAlimentListSnack2.get(id);
                break;
            case 4:
                conso = mAlimentListDinner.get(id);
                break;
            case 5:
                conso = mAlimentListSnack3.get(id);
                break;
            
        }
        myDialog.setContentView(R.layout.popup_aliment);
        updatePopup();
        final EditText qteEdit = myDialog.findViewById(R.id.qte_cpt);
        qteEdit.setText((int) conso.getQuantite() + "");
        qteEdit.setEnabled(false);
        qteEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updatePopup();
            }
            
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        myDialog.findViewById(R.id.action_delete).setVisibility(View.GONE);
        exitView = myDialog.findViewById(R.id.action_exit);
        exitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
                myDialog.cancel();
                
            }
        });
        myDialog.findViewById(R.id.confirmButton).setVisibility(View.GONE);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
    
    private void updateTotal() {
        float pro = 0, cal = 0, lip = 0, glu = 0;
        for (Consommation c : mAlimentListBreakfast) {
            if (alimentsSet.containsKey(c.getIdAliment())) {
                Aliment a = alimentsSet.get(c.getIdAliment());
                cal += a.getNbCalories() * c.getQuantite() / 100;
                pro += a.getQuantiteProteines() * c.getQuantite() / 100;
                lip += a.getQuantiteLipides() * c.getQuantite() / 100;
                glu += a.getQuantiteGlucides() * c.getQuantite() / 100;
            }
        }
        ((TextView) mViewBreakfast.findViewById(R.id.repas_cal_cpt)).setText((int) cal + " Kcal");
        ((TextView) mViewBreakfast.findViewById(R.id.repas_prot_cpt)).setText((int) pro + " g");
        ((TextView) mViewBreakfast.findViewById(R.id.repas_lip_cpt)).setText((int) lip + " g");
        ((TextView) mViewBreakfast.findViewById(R.id.repas_glu_cpt)).setText((int) glu + " g");
        glu = lip = cal = pro = 0;
        for (Consommation c : mAlimentListSnack1) {
            if (alimentsSet.containsKey(c.getIdAliment())) {
                Aliment a = alimentsSet.get(c.getIdAliment());
                cal += a.getNbCalories() * c.getQuantite() / 100;
                pro += a.getQuantiteProteines() * c.getQuantite() / 100;
                lip += a.getQuantiteLipides() * c.getQuantite() / 100;
                glu += a.getQuantiteGlucides() * c.getQuantite() / 100;
            }
            
        }
        ((TextView) mViewSnack1.findViewById(R.id.repas_cal_cpt)).setText((int) cal + " Kcal");
        ((TextView) mViewSnack1.findViewById(R.id.repas_prot_cpt)).setText((int) pro + " g");
        ((TextView) mViewSnack1.findViewById(R.id.repas_lip_cpt)).setText((int) lip + " g");
        ((TextView) mViewSnack1.findViewById(R.id.repas_glu_cpt)).setText((int) glu + " g");
        glu = lip = cal = pro = 0;
        for (Consommation c : mAlimentListLunch) {
            if (alimentsSet.containsKey(c.getIdAliment())) {
                Aliment a = alimentsSet.get(c.getIdAliment());
                cal += a.getNbCalories() * c.getQuantite() / 100;
                pro += a.getQuantiteProteines() * c.getQuantite() / 100;
                lip += a.getQuantiteLipides() * c.getQuantite() / 100;
                glu += a.getQuantiteGlucides() * c.getQuantite() / 100;
            }
            
        }
        ((TextView) mViewLunch.findViewById(R.id.repas_cal_cpt)).setText((int) cal + " Kcal");
        ((TextView) mViewLunch.findViewById(R.id.repas_prot_cpt)).setText((int) pro + " g");
        ((TextView) mViewLunch.findViewById(R.id.repas_lip_cpt)).setText((int) lip + " g");
        ((TextView) mViewLunch.findViewById(R.id.repas_glu_cpt)).setText((int) glu + " g");
        glu = lip = cal = pro = 0;
        for (Consommation c : mAlimentListSnack2) {
            if (alimentsSet.containsKey(c.getIdAliment())) {
                Aliment a = alimentsSet.get(c.getIdAliment());
                cal += a.getNbCalories() * c.getQuantite() / 100;
                pro += a.getQuantiteProteines() * c.getQuantite() / 100;
                lip += a.getQuantiteLipides() * c.getQuantite() / 100;
                glu += a.getQuantiteGlucides() * c.getQuantite() / 100;
            }
            
        }
        ((TextView) mViewSnack2.findViewById(R.id.repas_cal_cpt)).setText((int) cal + " Kcal");
        ((TextView) mViewSnack2.findViewById(R.id.repas_prot_cpt)).setText((int) pro + " g");
        ((TextView) mViewSnack2.findViewById(R.id.repas_lip_cpt)).setText((int) lip + " g");
        ((TextView) mViewSnack2.findViewById(R.id.repas_glu_cpt)).setText((int) glu + " g");
        glu = lip = cal = pro = 0;
        for (Consommation c : mAlimentListDinner) {
            if (alimentsSet.containsKey(c.getIdAliment())) {
                Aliment a = alimentsSet.get(c.getIdAliment());
                cal += a.getNbCalories() * c.getQuantite() / 100;
                pro += a.getQuantiteProteines() * c.getQuantite() / 100;
                lip += a.getQuantiteLipides() * c.getQuantite() / 100;
                glu += a.getQuantiteGlucides() * c.getQuantite() / 100;
            }
            
        }
        ((TextView) mViewDinner.findViewById(R.id.repas_cal_cpt)).setText((int) cal + " Kcal");
        ((TextView) mViewDinner.findViewById(R.id.repas_prot_cpt)).setText((int) pro + " g");
        ((TextView) mViewDinner.findViewById(R.id.repas_lip_cpt)).setText((int) lip + " g");
        ((TextView) mViewDinner.findViewById(R.id.repas_glu_cpt)).setText((int) glu + " g");
        glu = lip = cal = pro = 0;
        for (Consommation c : mAlimentListSnack3) {
            if (alimentsSet.containsKey(c.getIdAliment())) {
                Aliment a = alimentsSet.get(c.getIdAliment());
                cal += a.getNbCalories() * c.getQuantite() / 100;
                pro += a.getQuantiteProteines() * c.getQuantite() / 100;
                lip += a.getQuantiteLipides() * c.getQuantite() / 100;
                glu += a.getQuantiteGlucides() * c.getQuantite() / 100;
            }
            
        }
        ((TextView) mViewSnack3.findViewById(R.id.repas_cal_cpt)).setText((int) cal + " Kcal");
        ((TextView) mViewSnack3.findViewById(R.id.repas_prot_cpt)).setText((int) pro + " g");
        ((TextView) mViewSnack3.findViewById(R.id.repas_lip_cpt)).setText((int) lip + " g");
        ((TextView) mViewSnack3.findViewById(R.id.repas_glu_cpt)).setText((int) glu + " g");
        
    }
    
    
    private void updatePopup() {
        Aliment a = alimentsSet.get(conso.getIdAliment());
        ((TextView) myDialog.findViewById(R.id.alimentname_cpt)).setText(a.getNom());
        ((TextView) myDialog.findViewById(R.id.prot_cpt)).setText((int) (a.getQuantiteProteines() * conso.getQuantite() / 100) + "");
        ((TextView) myDialog.findViewById(R.id.calorie_cpt)).setText((int) (a.getNbCalories() * conso.getQuantite() / 100) + "");
        ((TextView) myDialog.findViewById(R.id.fat_cpt)).setText((int) (a.getQuantiteLipides() * conso.getQuantite() / 100) + "");
        ((TextView) myDialog.findViewById(R.id.carb_cpt)).setText((int) (a.getQuantiteGlucides() * conso.getQuantite() / 100) + "");
    }
    
    
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
