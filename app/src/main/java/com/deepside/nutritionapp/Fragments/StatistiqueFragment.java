package com.deepside.nutritionapp.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.deepside.nutritionapp.Managers.AlimentManager;
import com.deepside.nutritionapp.Managers.BilanManager;
import com.deepside.nutritionapp.Managers.HistoriquePoidsManager;
import com.deepside.nutritionapp.R;
import com.deepside.nutritionapp.Suivi.Aliment;
import com.deepside.nutritionapp.Suivi.Bilan;
import com.deepside.nutritionapp.Suivi.Consommation;
import com.deepside.nutritionapp.Suivi.HistoriquePoids;
import com.deepside.nutritionapp.Utils.Graphe;
import com.deepside.nutritionapp.Utils.Point;
import com.deepside.nutritionapp.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.*;


public class StatistiqueFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private GraphView graphView;
    private LinearLayout macrosVisibilityLayout;
    private CheckBox[] macrosVisibilityCheckBoxes;
    private Graphe graphe;
    private Calendar c;

    private int graphType = Graphe.GRAPH_TYPE_POIDS, graphDate = -30;
    private OnFragmentInteractionListener mListener;

    public StatistiqueFragment() {
    }

    public static StatistiqueFragment newInstance(String param1, String param2) {
        StatistiqueFragment fragment = new StatistiqueFragment();
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
            String param1 = getArguments().getString(ARG_PARAM1);
            String param2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        View view = inflater.inflate(R.layout.fragment_statistique, container, false);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            FirebaseUser user = auth.getCurrentUser();
            graphView = view.findViewById(R.id.graph);
            macrosVisibilityLayout = view.findViewById(R.id.macrosVisibilityLayout);
            Spinner graphTypeSpinner = view.findViewById(R.id.graphTypeSpinner);
            Spinner graphDateSpinner = view.findViewById(R.id.graphDateSpinner);
            macrosVisibilityCheckBoxes = new CheckBox[]{new CheckBox(getContext()), view.findViewById(R.id.proteinesCheckBox), view.findViewById(R.id.lipidesCheckBox), view.findViewById(R.id.glucidesCheckBox),};
            final ArrayList<ArrayList<Point>> values = new ArrayList<>();
            ArrayAdapter<CharSequence> graphTypeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.graphTypeChoices, android.R.layout.simple_spinner_item);
            ArrayAdapter<CharSequence> graphDateAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.graphDateChoices, android.R.layout.simple_spinner_item);
            graphTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            graphDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            graphTypeSpinner.setAdapter(graphTypeAdapter);
            graphDateSpinner.setAdapter(graphDateAdapter);
            values.add(new ArrayList<Point>());
            values.add(new ArrayList<Point>());
            values.add(new ArrayList<Point>());
            values.add(new ArrayList<Point>());
            values.add(new ArrayList<Point>());
            graphe = new Graphe(getActivity(), graphView, LineGraphSeries.class, values);
            graphView.getGridLabelRenderer().setHorizontalAxisTitle("Jour");

            graphDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    c = Calendar.getInstance();
                    switch (position) {
                        case 0:
                            graphDate = -30;
                            graphView.getGridLabelRenderer().setNumHorizontalLabels(15);


                            break;
                        case 1:
                            graphDate = -7;
                            graphView.getGridLabelRenderer().setNumHorizontalLabels(7);


                            break;
                        default:
                            break;
                    }
                    c.add(Calendar.DAY_OF_MONTH, graphDate);
                    graphe.drawGraph(graphType, c.getTime(), new Date());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            graphTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    c = Calendar.getInstance();
                    switch (position) {
                        case 0:
                            graphType = Graphe.GRAPH_TYPE_POIDS;
                            macrosVisibilityLayout.setVisibility(View.INVISIBLE);
                            graphView.getGridLabelRenderer().setVerticalAxisTitle("KG");


                            break;
                        case 1:
                            graphType = Graphe.GRAPHE_TYPE_CALORIES;
                            macrosVisibilityLayout.setVisibility(View.INVISIBLE);
                            graphView.getGridLabelRenderer().setVerticalAxisTitle("KCAL");

                            break;
                        case 2:
                            graphType = Graphe.GRAPH_TYPE_MACRO;
                            graphView.getGridLabelRenderer().setVerticalAxisTitle("G");

                            macrosVisibilityLayout.setVisibility(View.VISIBLE);
                            for (CheckBox macrosVisibilityCheckBoxe : macrosVisibilityCheckBoxes) {
                                macrosVisibilityCheckBoxe.setChecked(true);
                            }
                            break;
                        default:
                            break;
                    }
                    c.add(Calendar.DAY_OF_MONTH, graphDate);
                    graphe.drawGraph(graphType, c.getTime(), new Date());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            for (int i = 0; i < macrosVisibilityCheckBoxes.length; i++) {
                final int iTemp = i;
                macrosVisibilityCheckBoxes[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        graphe.setVisibility(iTemp, ((CheckBox) v).isChecked());
                    }
                });
            }
            HistoriquePoidsManager hm = new HistoriquePoidsManager();
            BilanManager bm = new BilanManager();
            AlimentManager am = new AlimentManager();
            hm.open();
            bm.open();
            am.open();


            ArrayList<HistoriquePoids> hList = hm.getAll(Utils.sDataSnapshot.child("HistoriquePoids").child(user.getUid()));
            for (HistoriquePoids h : hList) {
                values.get(4).add(new Point(h.getDate(), h.getPoids()));
            }
            c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, graphDate);
            graphe.drawGraph(graphType, c.getTime(), new Date());
            ArrayList<Bilan> bilans = bm.getAll(Utils.sDataSnapshot.child("Bilan").child(user.getUid()));
            HashSet<Long> idAlimentsSet = new HashSet<>();
            HashMap<Long, Aliment> alimentsSet = new HashMap<>();
            for (Bilan b : bilans) {
                for (Consommation c : b.getConsommations()) {
                    idAlimentsSet.add(c.getIdAliment());
                }
            }
            for (Long l : idAlimentsSet) {
                if (Utils.sDataSnapshot.child("Aliment").child(String.valueOf(l)).exists()) {
                    alimentsSet.put(l, am.get(Utils.sDataSnapshot.child("Aliment").child(String.valueOf(l))));
                }
            }
            float caloriesCount, proteinesCount, lipidesCount, glucidesCount;
            for (Bilan b : bilans) {
                caloriesCount = 0;
                proteinesCount = 0;
                lipidesCount = 0;
                glucidesCount = 0;
                for (Consommation c : b.getConsommations()) {
                    if (alimentsSet.containsKey(c.getIdAliment())) {
                        Aliment a = alimentsSet.get(c.getIdAliment());
                        caloriesCount += a.getNbCalories() * c.getQuantite() / 100;
                        proteinesCount += a.getQuantiteProteines() * c.getQuantite() / 100;
                        lipidesCount += a.getQuantiteLipides() * c.getQuantite() / 100;
                        glucidesCount += a.getQuantiteGlucides() * c.getQuantite() / 100;
                    }
                }
                Point p1 = new Point(b.getDateBilan(), caloriesCount);
                Point p2 = new Point(b.getDateBilan(), proteinesCount);
                Point p3 = new Point(b.getDateBilan(), lipidesCount);
                Point p4 = new Point(b.getDateBilan(), glucidesCount);
                values.get(0).add(p1);
                values.get(1).add(p2);
                values.get(2).add(p3);
                values.get(3).add(p4);

            }
            c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, graphDate);
            graphe.drawGraph(graphType, c.getTime(), new Date());


        }
        return view;
    }

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


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
