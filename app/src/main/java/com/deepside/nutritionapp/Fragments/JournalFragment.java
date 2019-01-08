package com.deepside.nutritionapp.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.deepside.nutritionapp.Managers.AlimentManager;
import com.deepside.nutritionapp.Managers.BilanManager;
import com.deepside.nutritionapp.R;
import com.deepside.nutritionapp.Suivi.Aliment;
import com.deepside.nutritionapp.Suivi.Bilan;
import com.deepside.nutritionapp.Suivi.Consommation;
import com.deepside.nutritionapp.Suivi.Regime;
import com.deepside.nutritionapp.Suivi.Repas;
import com.deepside.nutritionapp.Suivi.Utilisateur;
import com.deepside.nutritionapp.Utils.DateUtils;
import com.deepside.nutritionapp.Utils.Utils;
import com.deepside.nutritionapp.activity.RechercheAlimentActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

public class JournalFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ArrayList<ArrayList> mAlimentDayArray;
    private Consommation conso;
    private TextView dateSpinner;
    private DatePickerDialog datePickerDialog;
    private Spinner consumedUnitSpinner;
    private EditText qteEdit;
    private View mView;
    private Dialog myDialog;
    private Utilisateur user;
    private Date selectedDate;
    private ArrayList<Calendar> listDate;
    private String[] unites;
    private ArrayList<Consommation> mAlimentListBreakfast, mAlimentListLunch, mAlimentListDinner, mAlimentListSnack1, mAlimentListSnack2, mAlimentListSnack3;
    private String mParam1;
    private ListView mListViewBreakfast, mListViewLunch, mListViewDinner, mListViewSnack1, mListViewSnack2, mListViewSnack3;
    private LinearLayout mViewBreakfast, mViewLunch, mViewDinner, mViewSnack1, mViewSnack2, mViewSnack3;
    private LayoutInflater mInflater;
    private float mCarbsNeeded, mProteinNeeded, mFatNeeded, mCalorieNeeded;
    private float mFatConso, mProteinConso, mCarbsConso, mCalorieConso;
    private ProgressBar myProgressCalorie, myProgressProtein, myProgressFat, myProgressCarbs;
    private OnFragmentInteractionListener mListener;
    private ArrayList<Bilan> bilans;
    private HashMap<Long, Aliment> alimentsSet;
    
    public JournalFragment() {
    }
    
    
    public static JournalFragment newInstance(String param1, String param2) {
        JournalFragment fragment = new JournalFragment();
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
    public void onResume() {
        try {
            String s = dateSpinner.getText().toString();
            if (s.equals("Aujourdhui")) selectedDate = new Date();
            else selectedDate = DateUtils.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dumpConsomation(selectedDate);
        
        if (Utils.c!=null)
        {
            alimentsSet.put(Utils.a.getIdAliment(),Utils.a);
            for (Bilan b : bilans) {
                if (DateUtils.stringify(b.getDateBilan()).equals(DateUtils.stringify(Utils.d)))
                {
                    switch (Utils.c.getRepas().ordinal()) {
                        case 0:
                            mAlimentListBreakfast.add(Utils.c);
                            break;
                        case 1:
                            mAlimentListSnack1.add(Utils.c);
                            break;
                        case 2:
                            mAlimentListLunch.add(Utils.c);
                            break;
                        case 3:
                            mAlimentListSnack2.add(Utils.c);
                            break;
                        case 4:
                            mAlimentListDinner.add(Utils.c);
                            break;
                        case 5:
                            mAlimentListSnack3.add(Utils.c);
                            break;
        
                    }
            
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
            Utils.c=null;
        }
        super.onResume();
    }
    
    @Override
    public void onStart() {
        try {
            String s = dateSpinner.getText().toString();
            if (s.equals("Aujourdhui")) selectedDate = new Date();
            else selectedDate = DateUtils.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dumpConsomation(selectedDate);
        super.onStart();
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (Utilisateur) getArguments().getSerializable(ARG_PARAM1);
            String param2 = getArguments().getString(ARG_PARAM2);
        }
        mAlimentListBreakfast = new ArrayList<>();
        mAlimentListSnack1 = new ArrayList<>();
        mAlimentListLunch = new ArrayList<>();
        mAlimentListSnack2 = new ArrayList<>();
        mAlimentListDinner = new ArrayList<>();
        mAlimentListSnack3 = new ArrayList<>();
        mAlimentDayArray = new ArrayList<>();
        listDate = new ArrayList<>();
        mAlimentDayArray.add(0, mAlimentListBreakfast);
        mAlimentDayArray.add(1, mAlimentListSnack1);
        mAlimentDayArray.add(2, mAlimentListLunch);
        mAlimentDayArray.add(3, mAlimentListSnack2);
        mAlimentDayArray.add(4, mAlimentListDinner);
        mAlimentDayArray.add(5, mAlimentListSnack3);
        Regime regime = new Regime(user);
        regime.calculPlanAlimentaire(user);
        mProteinNeeded = regime.getProteinesRecommandees();
        mCalorieNeeded = regime.getCaloriesRecommandees();
        mFatNeeded = regime.getLipidesRecommandees();
        mCarbsNeeded = regime.getGlucidesRecommandees();
    
    
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        mView = inflater.inflate(R.layout.fragment_journal, container, false);
        myDialog = new Dialog(mView.getContext());
        dateSpinner = mView.findViewById(R.id.spinner);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() - 1000);
        datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setMaxDate(calendar);
        try {
            calendar.setTime(DateUtils.parse(user.getDateInscription()));
            System.out.println(user.getDateInscription());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        datePickerDialog.setMinDate(calendar);
        datePickerDialog.vibrate(true);
        datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_1);
        datePickerDialog.setAccentColor(getResources().getColor(R.color.red));
        datePickerDialog.setTitle("Date du bilan");
        datePickerDialog.setOkText("Confrimer");
        datePickerDialog.setCancelText("Revenir");
        datePickerDialog.autoDismiss(true);
        dateSpinner.setText("Aujourdhui");
        dateSpinner.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                datePickerDialog.show(getFragmentManager(), "");
                
            }
        });
        new Thread(new Runnable() {
            public void run() {
                InitProgress(mView);
                
            }
        }).start();
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
        View.OnClickListener addButtonListner = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RechercheAlimentActivity.class);
                if (mViewBreakfast.findViewById(R.id.add_aliment_repas) == view)
                    intent.putExtra("repas", 0);
                else if (mViewSnack1.findViewById(R.id.add_aliment_repas) == view)
                    intent.putExtra("repas", 1);
                else if (mViewLunch.findViewById(R.id.add_aliment_repas) == view)
                    intent.putExtra("repas", 2);
                else if (mViewSnack2.findViewById(R.id.add_aliment_repas) == view)
                    intent.putExtra("repas", 3);
                else if (mViewDinner.findViewById(R.id.add_aliment_repas) == view)
                    intent.putExtra("repas", 4);
                else intent.putExtra("repas", 5);
                intent.putExtra("date", DateUtils.stringify(selectedDate));
                startActivity(intent);
            }
        };
        mViewBreakfast.findViewById(R.id.add_aliment_repas).setOnClickListener(addButtonListner);
        mViewSnack1.findViewById(R.id.add_aliment_repas).setOnClickListener(addButtonListner);
        mViewLunch.findViewById(R.id.add_aliment_repas).setOnClickListener(addButtonListner);
        mViewSnack2.findViewById(R.id.add_aliment_repas).setOnClickListener(addButtonListner);
        mViewDinner.findViewById(R.id.add_aliment_repas).setOnClickListener(addButtonListner);
        mViewSnack3.findViewById(R.id.add_aliment_repas).setOnClickListener(addButtonListner);
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
        dateSpinner.setVisibility(View.GONE);
        
    }
    private void SetConsomation(final Date date) {
        for (Object arrayList : mAlimentDayArray)
            ((ArrayList) arrayList).clear();
        for (Bilan b : bilans) {
            if (DateUtils.stringify(b.getDateBilan()).equals(DateUtils.stringify(date)))
            {
                Bilan jourBilan = b;
                for (Consommation c : b.getConsommations()) {
                    Consommation consommation = new Consommation(c.getIdAliment(), c.getRepas(), c.getQuantite());
                    switch (c.getRepas().ordinal()) {
                        case 0:
                            mAlimentListBreakfast.add(consommation);
                            break;
                        case 1:
                            mAlimentListSnack1.add(consommation);
                            break;
                        case 2:
                            mAlimentListLunch.add(consommation);
                            break;
                        case 3:
                            mAlimentListSnack2.add(consommation);
                            break;
                        case 4:
                            mAlimentListDinner.add(consommation);
                            break;
                        case 5:
                            mAlimentListSnack3.add(consommation);
                            break;
            
                    }
                }
    
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
    }
        
        
        
    
    private void dumpConsomation(final Date date) {
        ArrayList<Long> idAlimentsSet = new ArrayList<>();
        alimentsSet = new HashMap<>();
        for (Object arrayList : mAlimentDayArray)
            ((ArrayList) arrayList).clear();
        BilanManager bm = new BilanManager();
        AlimentManager am = new AlimentManager();
    
        bilans = bm.getAll(Utils.sDataSnapshot.child("Bilan").child(user.getIdUtilisateur()));
        Calendar date1;
        System.out.println(bilans);
        for (Bilan b : bilans) {
            date1 = Calendar.getInstance();
            date1.setTime(b.getDateBilan());
            listDate.add(date1);
            for (Consommation c : b.getConsommations()) {
                idAlimentsSet.add(c.getIdAliment());
            }
        }
        if (!listDate.isEmpty()) {
            datePickerDialog.setHighlightedDays((listDate.toArray(new Calendar[listDate.size()])));
        
        }
        for (Long l : idAlimentsSet) {
            if (Utils.sDataSnapshot.child("Aliment").child(String.valueOf(l)).exists()) {
                Aliment aliment = am.get(Utils.sDataSnapshot.child("Aliment").child(String.valueOf(l)));
                alimentsSet.put(l, aliment);
            }
        }
        SetConsomation(date);
        dateSpinner.setVisibility(View.VISIBLE);
        
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
    
    private void InitProgress(View mView) {
        myProgressCalorie = mView.findViewById(R.id.circle_progress_bar_calorie);
        myProgressProtein = mView.findViewById(R.id.circle_progress_bar_proteine);
        myProgressFat = mView.findViewById(R.id.circle_progress_bar);
        ProgressBar myProgressCalorieBack = mView.findViewById(R.id.circle_progress_bar_calorie_back);
        myProgressCarbs = mView.findViewById(R.id.circle_progress_bar_carbs);
        ProgressBar myProgressProteinBack = mView.findViewById(R.id.circle_progress_bar_proteine_back);
        ProgressBar myProgressFatBack = mView.findViewById(R.id.circle_progress_bar_back);
        ProgressBar myProgressCarbsBack = mView.findViewById(R.id.circle_progress_bar_carbs_back);
        myProgressCalorieBack.setProgressDrawable(CreateGradient(R.color.red_back));
        myProgressCalorie.setProgressDrawable(CreateGradient(R.color.red));
        myProgressProteinBack.setProgressDrawable(CreateGradient(R.color.blue_back));
        myProgressProtein.setProgressDrawable(CreateGradient(R.color.blue));
        myProgressCarbsBack.setProgressDrawable(CreateGradient(R.color.yello_back));
        myProgressCarbs.setProgressDrawable(CreateGradient(R.color.yello));
        myProgressFatBack.setProgressDrawable(CreateGradient(R.color.green_back));
        myProgressFat.setProgressDrawable(CreateGradient(R.color.green));
        myProgressCalorieBack.setProgress(100);
        myProgressCarbsBack.setProgress(100);
        myProgressFatBack.setProgress(100);
        myProgressProteinBack.setProgress(100);
        
    }
    
    private GradientDrawable CreateGradient(int color) {
        LayerDrawable layerDrawable;
        GradientDrawable gradientDrawable;
        layerDrawable = (LayerDrawable) getResources().getDrawable(R.drawable.circle_progress_foreground);
        gradientDrawable = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.progress);
        gradientDrawable.setColor(getResources().getColor(color));
        return gradientDrawable;
    }
    
    private void ShowPopup(View v, final int id, final int list) {
        ImageView exitView;
        ImageView deleteView;
        ImageView confirmButton;
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
        switch (alimentsSet.get(conso.getIdAliment()).getTypeAliment()) {
            case LIQUIDE:
                unites = new String[]{"g", "L", "cL", "fl oz", "cuillère à soupe", "tasse", "bol"};
                break;
            case SOLIDE:
                unites = new String[]{"g", "oz", "lb", "tasse (moulu)", "bol (moulu)"};
                break;
        }
        myDialog.setContentView(R.layout.popup_aliment);
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, unites);
        consumedUnitSpinner = myDialog.findViewById(R.id.UnitSpinner);
        consumedUnitSpinner.setAdapter(unitAdapter);
        consumedUnitSpinner.setSelection(0);
        qteEdit = myDialog.findViewById(R.id.qte_cpt);
        qteEdit.setText((int) conso.getQuantite() + "");
        updatePopup(conso.getQuantite());
        qteEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty()) updatePopup(0);
                else updatePopup(Float.valueOf(charSequence.toString()));
                
            }
            
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        consumedUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView) adapterView.getChildAt(0)).setTextSize(20);
                qteEdit.setText(qteEdit.getText() + "");
                updatePopup(Float.valueOf(qteEdit.getText().toString()));
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        deleteView = myDialog.findViewById(R.id.action_delete);
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                BilanManager bm = new BilanManager();
                bm.open();
                switch (list) {
                    case 0:
                        bm.delete(user.getIdUtilisateur(), selectedDate, Repas.PETIT_DEJEUNER, mAlimentListBreakfast.get(id).getIdAliment());
                        mAlimentListBreakfast.remove(id);
                        updateList(mAlimentListBreakfast, mListViewBreakfast);
                        setListViewHeightBasedOnItems(mListViewBreakfast);
                        break;
                    case 1:
                        bm.delete(user.getIdUtilisateur(), selectedDate, Repas.SNACK1, mAlimentListSnack1.get(id).getIdAliment());
                        mAlimentListSnack1.remove(id);
                        
                        updateList(mAlimentListSnack1, mListViewSnack1);
                        setListViewHeightBasedOnItems(mListViewSnack1);
                        break;
                    case 2:
                        bm.delete(user.getIdUtilisateur(), selectedDate, Repas.DEJEUNER, mAlimentListLunch.get(id).getIdAliment());
                        mAlimentListLunch.remove(id);
                        
                        updateList(mAlimentListLunch, mListViewLunch);
                        setListViewHeightBasedOnItems(mListViewLunch);
                        break;
                    case 3:
                        bm.delete(user.getIdUtilisateur(), selectedDate, Repas.SNACK2, mAlimentListSnack2.get(id).getIdAliment());
                        mAlimentListSnack2.remove(id);
                        
                        updateList(mAlimentListSnack2, mListViewSnack2);
                        setListViewHeightBasedOnItems(mListViewSnack2);
                        break;
                    case 4:
                        bm.delete(user.getIdUtilisateur(), selectedDate, Repas.DINER, mAlimentListDinner.get(id).getIdAliment());
                        mAlimentListDinner.remove(id);
                        
                        updateList(mAlimentListDinner, mListViewDinner);
                        setListViewHeightBasedOnItems(mListViewDinner);
                        break;
                    case 5:
                        bm.delete(user.getIdUtilisateur(), selectedDate, Repas.SNACK3, mAlimentListSnack3.get(id).getIdAliment());
                        mAlimentListSnack3.remove(id);
                        
                        updateList(mAlimentListSnack3, mListViewSnack3);
                        setListViewHeightBasedOnItems(mListViewSnack3);
                        break;
                }
                updateTotal();
                DatabaseReference ref = Utils.getDatabase().getReference();
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
            
                        Utils.sDataSnapshot=dataSnapshot;
                        dumpConsomation(selectedDate);
    
    
                    }
        
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                myDialog.dismiss();
                myDialog.cancel();
            }
        });
        exitView = myDialog.findViewById(R.id.action_exit);
        exitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
                myDialog.cancel();
                
            }
        });
        confirmButton = myDialog.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                BilanManager bm = new BilanManager();
                bm.open();
                switch (list) {
                    case 0:
                        mAlimentListBreakfast.get(id).setQuantite(Long.valueOf(qteEdit.getText().toString()) * getCoef());
                        bm.insert(user.getIdUtilisateur(), selectedDate, mAlimentListBreakfast.get(id));
                        updateList(mAlimentListBreakfast, mListViewBreakfast);
                        break;
                    case 1:
                        mAlimentListSnack1.get(id).setQuantite(Long.valueOf(qteEdit.getText().toString()) * getCoef());
                        bm.insert(user.getIdUtilisateur(), selectedDate, mAlimentListSnack1.get(id));
                        updateList(mAlimentListSnack1, mListViewSnack1);
                        break;
                    case 2:
                        mAlimentListLunch.get(id).setQuantite(Long.valueOf(qteEdit.getText().toString()) * getCoef());
                        bm.insert(user.getIdUtilisateur(), selectedDate, mAlimentListLunch.get(id));
                        updateList(mAlimentListLunch, mListViewLunch);
                        break;
                    case 3:
                        mAlimentListSnack2.get(id).setQuantite(Long.valueOf(qteEdit.getText().toString()) * getCoef());
                        bm.insert(user.getIdUtilisateur(), selectedDate, mAlimentListSnack2.get(id));
                        updateList(mAlimentListSnack2, mListViewSnack2);
                        break;
                    case 4:
                        mAlimentListDinner.get(id).setQuantite(Long.valueOf(qteEdit.getText().toString()) * getCoef());
                        bm.insert(user.getIdUtilisateur(), selectedDate, mAlimentListDinner.get(id));
                        updateList(mAlimentListDinner, mListViewDinner);
                        break;
                    case 5:
                        mAlimentListSnack3.get(id).setQuantite(Long.valueOf(qteEdit.getText().toString()) * getCoef());
                        bm.insert(user.getIdUtilisateur(), selectedDate, mAlimentListSnack3.get(id));
                        updateList(mAlimentListSnack3, mListViewSnack3);
                        break;
                    
                }
                Utils.dumpDatasnapshot();
                myDialog.dismiss();
                myDialog.cancel();
                updateTotal();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
    
    private void updateTotal() {
        float pro = 0, cal = 0, lip = 0, glu = 0;
        float qte;
        mProteinConso = 0;
        mCalorieConso = 0;
        mFatConso = 0;
        mCarbsConso = 0;
        for (Consommation c : mAlimentListBreakfast) {
            if (alimentsSet.containsKey(c.getIdAliment())) {
                Aliment a = alimentsSet.get(c.getIdAliment());
                cal += a.getNbCalories() * c.getQuantite() / 100;
                pro += a.getQuantiteProteines() * c.getQuantite() / 100;
                lip += a.getQuantiteLipides() * c.getQuantite() / 100;
                glu += a.getQuantiteGlucides() * c.getQuantite() / 100;
            }
            
        }
        mProteinConso += pro;
        mCalorieConso += cal;
        mFatConso += lip;
        mCarbsConso += glu;
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
        mProteinConso += pro;
        mCalorieConso += cal;
        mFatConso += lip;
        mCarbsConso += glu;
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
        mProteinConso += pro;
        mCalorieConso += cal;
        mFatConso += lip;
        mCarbsConso += glu;
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
        mProteinConso += pro;
        mCalorieConso += cal;
        mFatConso += lip;
        mCarbsConso += glu;
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
        mProteinConso += pro;
        mCalorieConso += cal;
        mFatConso += lip;
        mCarbsConso += glu;
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
        mProteinConso += pro;
        mCalorieConso += cal;
        mFatConso += lip;
        mCarbsConso += glu;
        ((TextView) mViewSnack3.findViewById(R.id.repas_cal_cpt)).setText((int) cal + " Kcal");
        ((TextView) mViewSnack3.findViewById(R.id.repas_prot_cpt)).setText((int) pro + " g");
        ((TextView) mViewSnack3.findViewById(R.id.repas_lip_cpt)).setText((int) lip + " g");
        ((TextView) mViewSnack3.findViewById(R.id.repas_glu_cpt)).setText((int) glu + " g");
        updateProgress(mView);
        
    }
    
    private void updateProgress(View view) {
        float prot100 = 100 * mProteinConso / mProteinNeeded, carbs100 = 100 * mCarbsConso / mCarbsNeeded, fat100 = 100 * mFatConso / mFatNeeded, cal100 = 100 * mCalorieConso / mCalorieNeeded;
        myProgressCalorie.setProgress((int) (100 * mCalorieConso / mCalorieNeeded));
        TextView CalorieCpt = mView.findViewById(R.id.calorie_cpt);
        CalorieCpt.setText((int) mCalorieConso + "\nKcal");
        myProgressProtein.setProgress((int) prot100);
        TextView ProteinCpt = mView.findViewById(R.id.prot_cpt);
        ProteinCpt.setText((int) mProteinConso + "\ng");
        myProgressCarbs.setProgress((int) carbs100);
        TextView CarbsCpt = mView.findViewById(R.id.carb_cpt);
        CarbsCpt.setText((int) mCarbsConso + "\ng");
        myProgressFat.setProgress((int) fat100);
        TextView FatCpt = mView.findViewById(R.id.fat_cpt);
        FatCpt.setText((int) mFatConso + "\ng");
        
    }
    
    private void updatePopup(float qte) {
        Aliment a = alimentsSet.get(conso.getIdAliment());
        ((TextView) myDialog.findViewById(R.id.alimentname_cpt)).setText(a.getNom());
        ((TextView) myDialog.findViewById(R.id.prot_cpt)).setText((int) (a.getQuantiteProteines() * qte * getCoef() / 100) + "");
        ((TextView) myDialog.findViewById(R.id.calorie_cpt)).setText((int) (a.getNbCalories() * qte * getCoef() / 100) + " Kcal");
        ((TextView) myDialog.findViewById(R.id.fat_cpt)).setText((int) (a.getQuantiteLipides() * qte * getCoef() / 100) + "");
        ((TextView) myDialog.findViewById(R.id.carb_cpt)).setText((int) (a.getQuantiteGlucides() * qte * getCoef() / 100) + "");
    }
    
    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        selectedDate = new Date(year - 1900, monthOfYear, dayOfMonth);
        if (DateUtils.stringify(selectedDate).equals(DateUtils.stringify(new Date())))
            dateSpinner.setText("Aujourdhui");
        else
            dateSpinner.setText(new SimpleDateFormat("EEE d MMM").format(selectedDate).toUpperCase());
        SetConsomation(selectedDate);
        
    }
    
    private float getCoef() {
        try {
            Float coeff = 1f;
            switch (consumedUnitSpinner.getSelectedItem().toString()) {
                case "L":
                    coeff = 1000f;
                    break;
                case "cL":
                    coeff = 100f;
                    break;
                case "fl oz":
                    coeff = 29.5735296875f;
                    break;
                case "cuillère à soupe":
                    coeff = 15f;
                    break;
                case "tasse":
                    coeff = 250f;
                    break;
                case "bol":
                    coeff = 350f;
                    break;
                case "oz":
                    coeff = 28.3495f;
                    break;
                case "lb":
                    coeff = 453.59237f;
                    break;
                case "tasse (moulu)":
                    coeff = 120f;
                    break;
                case "bol (moulu)":
                    coeff = 220f;
                    break;
                default:
                    break;
            }
            return coeff;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    
    
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
