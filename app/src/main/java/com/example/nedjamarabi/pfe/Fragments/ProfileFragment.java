package com.example.nedjamarabi.pfe.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.nedjamarabi.pfe.Managers.HistoriquePoidsManager;
import com.example.nedjamarabi.pfe.Managers.RecommandationManager;
import com.example.nedjamarabi.pfe.Managers.RegimeManager;
import com.example.nedjamarabi.pfe.Managers.UtilisateurManager;
import com.example.nedjamarabi.pfe.R;
import com.example.nedjamarabi.pfe.Suivi.ActivitePhysique;
import com.example.nedjamarabi.pfe.Suivi.HistoriquePoids;
import com.example.nedjamarabi.pfe.Suivi.Morphotype;
import com.example.nedjamarabi.pfe.Suivi.Niveau;
import com.example.nedjamarabi.pfe.Suivi.Objectif;
import com.example.nedjamarabi.pfe.Suivi.Regime;
import com.example.nedjamarabi.pfe.Suivi.Sexe;
import com.example.nedjamarabi.pfe.Suivi.Utilisateur;
import com.example.nedjamarabi.pfe.Utils.CircleTransform;
import com.example.nedjamarabi.pfe.Utils.MaterialNumberPicker;
import com.example.nedjamarabi.pfe.Utils.Utils;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;


public class ProfileFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = ProfileFragment.class.getSimpleName();
    private final int PICK_IMAGE_REQUEST = 71;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    ImageView uploadView;
    private View thumbView;
    Uri photoUri;
    private Bitmap bitmap;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private View mView;
    private ImageView imgProfile;
    private Dialog myDialog;
    private Uri filePath;
    private LinearLayout mNiveauLayout, mObjectifLayout, mSexeLayout, mMorphotypeLayout, mMorphotypeLayoutIcons;
    private TextView mUserProfileName;
    private int isselected[] = {0, 0, 0, 0};
    private boolean editMode = false;
    private Utilisateur mUser = null;
    private ImageButton mEditButton;
    private LinearLayout mTailleBar, mAgeBar;
    private RelativeLayout mLayoutButton;
    private TextView mTextButton;
    private ArrayList<TextView> mSexeArrayList, mObjectifArrayList, mNiveauArrayList, morphotypeArrayList;
    private SeekBar poidsDésiréBar, poidsBar, activitéBar;
    private String mParam1;
    private OnFragmentInteractionListener mListener;
    
    public ProfileFragment() {
    }
    
    
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    
    private static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        if (drawable==null) drawable=getApplicationContext().getResources().getDrawable( R.drawable.user);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUser = (Utilisateur) getArguments().getSerializable(ARG_PARAM1);
            String param2 = getArguments().getString(ARG_PARAM2);
        }
        mSexeArrayList = new ArrayList<>();
        mObjectifArrayList = new ArrayList<>();
        mNiveauArrayList = new ArrayList<>();
        morphotypeArrayList = new ArrayList<>();
        
    }
    
    
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_profile, container, false);
        thumbView = LayoutInflater.from(getContext()).inflate(R.layout.layout_seekbar_thumb, null, false);
        initView();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        UpdateProfileView();
        myDialog = new Dialog(mView.getContext());
        mUserProfileName.setText(mAuth.getCurrentUser().getDisplayName());
        return mView;
    }
    
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    
    private Drawable getThumb(int progress) {
        ((TextView) thumbView.findViewById(R.id.tvProgress)).setText(String.valueOf(progress) + " kg");
        thumbView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(thumbView.getMeasuredWidth(), thumbView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        thumbView.layout(0, 0, thumbView.getMeasuredWidth(), thumbView.getMeasuredHeight());
        thumbView.draw(canvas);
        return new BitmapDrawable(getResources(), bitmap);
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    
    private void changeBackground(int pos, LinearLayout tagLinearLayout, ArrayList<TextView> textViewArrayList) {
        for (int i = 0; i < tagLinearLayout.getChildCount(); i++) {
            if (i == pos) {
                tagLinearLayout.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.roundtext2));
                textViewArrayList.get(i).setTextColor(Color.parseColor("#ffffff"));
            } else {
                tagLinearLayout.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.roundtext1));
                textViewArrayList.get(i).setTextColor(Color.parseColor("#bcbec2"));
            }
        }
    }
    
    private void initView() {
        RelativeLayout relativeLayout = mView.findViewById(R.id.profile_layout);
        imgProfile = mView.findViewById(R.id.user_profile_photo);
        mMorphotypeLayout = mView.findViewById(R.id.morphotype_layout);
        mMorphotypeLayoutIcons = mView.findViewById(R.id.morphotype_layout_icons);
        mNiveauLayout = mView.findViewById(R.id.entrainement_layout);
        mObjectifLayout = mView.findViewById(R.id.objectif_layout);
        mSexeLayout = mView.findViewById(R.id.sex_layout);
        mEditButton = mView.findViewById(R.id.imageButton);
        mLayoutButton =mView.findViewById(R.id.layoutButton);
        mTextButton =mView.findViewById(R.id.textButton);
    
        mNiveauArrayList.add((TextView) mView.findViewById(R.id.entrainement1));
        mNiveauArrayList.add((TextView) mView.findViewById(R.id.entrainement2));
        mNiveauArrayList.add((TextView) mView.findViewById(R.id.entrainement3));
        morphotypeArrayList.add((TextView) mView.findViewById(R.id.morphotype1));
        morphotypeArrayList.add((TextView) mView.findViewById(R.id.morphotype2));
        morphotypeArrayList.add((TextView) mView.findViewById(R.id.morphotype3));
        mObjectifArrayList.add((TextView) mView.findViewById(R.id.objectif1));
        mObjectifArrayList.add((TextView) mView.findViewById(R.id.objectif2));
        poidsDésiréBar = mView.findViewById(R.id.poids_désiré_bar);
        mTailleBar = mView.findViewById(R.id.taillebar);
        mAgeBar = mView.findViewById(R.id.agebar);
        poidsBar = mView.findViewById(R.id.poids_bar);
        activitéBar = mView.findViewById(R.id.activité_bar);
        mUserProfileName = mView.findViewById(R.id.user_profile_name);
        
    
        
    
    }
    
    private void UpdateProfileView() {
        try {
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("images/" + mAuth.getCurrentUser().getUid());
            Glide.with(getApplicationContext()).using(new FirebaseImageLoader()).load(ref).crossFade().thumbnail(0.5f).bitmapTransform(new CircleTransform(getApplicationContext())).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache( true ).into(imgProfile);
        
        } catch (Exception e) {
            uploadImage();
        }
        if (mUser.getNiveau() == null) {
            poidsDésiréBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                    seekBar.setThumb(getThumb(progress));
                    if (progress<10) seekBar.setProgress(10);
                    else if (progress>190) seekBar.setProgress(190);
                    if (isselected[1] == 0) {
                        if (progress<poidsBar.getProgress())
                            seekBar.setProgress(poidsBar.getProgress());
                
                    }
                    if (isselected[1] == 1) {
                        if (progress>poidsBar.getProgress())
                            seekBar.setProgress(poidsBar.getProgress());
                    }
            
                }
        
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
        
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            poidsBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                    seekBar.setThumb(getThumb(progress));
                    if (progress<10) seekBar.setProgress(10);
                    else if (progress>190) seekBar.setProgress(190);
                    if (isselected[1] == 0) {
                        if (progress>poidsDésiréBar.getProgress())
                            poidsDésiréBar.setProgress(seekBar.getProgress());
                
                    }
                    if (isselected[1] == 1) {
                        if (progress<poidsDésiréBar.getProgress())
                            poidsDésiréBar.setProgress(seekBar.getProgress());
                    }
            
                }
        
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
        
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            poidsBar.setThumb(getThumb(poidsBar.getProgress()));
            poidsDésiréBar.setThumb(getThumb(poidsDésiréBar.getProgress()));
            editMode = false;
            switchMode();
        
        } else {
            dumpUser(mUser);
            poidsDésiréBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                    seekBar.setThumb(getThumb(progress));
                    if (progress<10) seekBar.setProgress(10);
                    else if (progress>190) seekBar.setProgress(190);
                    if (isselected[1] == 0) {
                        if (progress<poidsBar.getProgress())
                            seekBar.setProgress(poidsBar.getProgress());
                
                    }
                    if (isselected[1] == 1) {
                        if (progress>poidsBar.getProgress())
                            seekBar.setProgress(poidsBar.getProgress());
                    }
            
                }
        
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
        
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            poidsBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                    seekBar.setThumb(getThumb(progress));
                    if (progress<10) seekBar.setProgress(10);
                    else if (progress>190) seekBar.setProgress(190);
                    if (isselected[1] == 0) {
                        if (progress>poidsDésiréBar.getProgress())
                            poidsDésiréBar.setProgress(seekBar.getProgress());
                
                    }
                    if (isselected[1] == 1) {
                        if (progress<poidsDésiréBar.getProgress())
                            poidsDésiréBar.setProgress(seekBar.getProgress());
                    }
            
                }
        
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
        
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            poidsBar.setThumb(getThumb(poidsBar.getProgress()));
            poidsDésiréBar.setThumb(getThumb(poidsDésiréBar.getProgress()));
    
    
            editMode = false;
        
        }
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowPopup();
            }
        });
        
        for (int i = 0; i < mNiveauLayout.getChildCount(); i++) {
            mNiveauLayout.getChildAt(i).setOnClickListener(this);
            mNiveauLayout.getChildAt(i).setClickable(false);
            if (i != isselected[0]) mNiveauLayout.getChildAt(i).setVisibility(View.GONE);
            else {
                mNiveauLayout.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.roundtext2));
                mNiveauArrayList.get(i).setTextColor(Color.parseColor("#ffffff"));
            }
        }
        for (int i = 0; i < mMorphotypeLayout.getChildCount(); i++) {
            mMorphotypeLayout.getChildAt(i).setOnClickListener(this);
            mMorphotypeLayout.getChildAt(i).setClickable(false);
            if (i != isselected[3]) mMorphotypeLayout.getChildAt(i).setVisibility(View.GONE);
            else {
                mMorphotypeLayout.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.roundtext2));
                morphotypeArrayList.get(i).setTextColor(Color.parseColor("#ffffff"));
            }
        }
        for (int i = 0; i < mMorphotypeLayoutIcons.getChildCount(); i++) {
            mMorphotypeLayoutIcons.getChildAt(i).setClickable(false);
            mMorphotypeLayoutIcons.getChildAt(i).setVisibility(View.GONE);
            
        }
        for (int i = 0; i < mObjectifLayout.getChildCount(); i++) {
            mObjectifLayout.getChildAt(i).setOnClickListener(this);
            mObjectifLayout.getChildAt(i).setClickable(false);
            if (i != isselected[1]) mObjectifLayout.getChildAt(i).setVisibility(View.GONE);
            else {
                mObjectifLayout.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.roundtext2));
                mObjectifArrayList.get(i).setTextColor(Color.parseColor("#ffffff"));
            }
            
        }
        for (int i = 0; i < mSexeLayout.getChildCount(); i++) {
            mSexeLayout.getChildAt(i).setOnClickListener(this);
            mSexeLayout.getChildAt(i).setClickable(false);
            mSexeArrayList.add((TextView) mSexeLayout.getChildAt(i));
            if (i != isselected[2]) mSexeLayout.getChildAt(i).setVisibility(View.GONE);
            else {
                mSexeLayout.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.roundtext2));
                mSexeArrayList.get(i).setTextColor(Color.parseColor("#ffffff"));
            }
        }
        activitéBar.setEnabled(false);
        poidsDésiréBar.setEnabled(false);
        poidsBar.setEnabled(false);
        mLayoutButton.setOnClickListener(this);
        
        
    }
    
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.morphotype1_layout:
                changeBackground(0, mMorphotypeLayout, morphotypeArrayList);
                for (int i = 0; i < mMorphotypeLayoutIcons.getChildCount(); i++) {
                    if (i == 0) mMorphotypeLayoutIcons.getChildAt(i).setVisibility(View.VISIBLE);
                    else mMorphotypeLayoutIcons.getChildAt(i).setVisibility(View.GONE);
                }
                isselected[3] = 0;
                break;
            case R.id.morphotype2_layout:
                changeBackground(1, mMorphotypeLayout, morphotypeArrayList);
                for (int i = 0; i < mMorphotypeLayoutIcons.getChildCount(); i++) {
                    if (i == 1) mMorphotypeLayoutIcons.getChildAt(i).setVisibility(View.VISIBLE);
                    else mMorphotypeLayoutIcons.getChildAt(i).setVisibility(View.GONE);
                }
                isselected[3] = 1;
                break;
            case R.id.morphotype3_layout:
                changeBackground(2, mMorphotypeLayout, morphotypeArrayList);
                for (int i = 0; i < mMorphotypeLayoutIcons.getChildCount(); i++) {
                    if (i == 2) mMorphotypeLayoutIcons.getChildAt(i).setVisibility(View.VISIBLE);
                    else mMorphotypeLayoutIcons.getChildAt(i).setVisibility(View.GONE);
                }
                isselected[3] = 2;
                break;
            case R.id.débutant_layout:
                changeBackground(0, mNiveauLayout, mNiveauArrayList);
                isselected[0] = 0;
                break;
            case R.id.intermediare_layout:
                changeBackground(1, mNiveauLayout, mNiveauArrayList);
                isselected[0] = 1;
                break;
            case R.id.Expert:
                changeBackground(2, mNiveauLayout, mNiveauArrayList);
                isselected[0] = 2;
                break;
            case R.id.prise_de_poid_layout:
                changeBackground(0, mObjectifLayout, mObjectifArrayList);
                isselected[1] = 0;
                break;
            case R.id.perte_de_poid_layout:
                changeBackground(1, mObjectifLayout, mObjectifArrayList);
                isselected[1] = 1;
                break;
            case R.id.homme_layout:
                changeBackground(0, mSexeLayout, mSexeArrayList);
                isselected[2] = 0;
                break;
            case R.id.femme_layout:
                changeBackground(1, mSexeLayout, mSexeArrayList);
                isselected[2] = 1;
                break;
            case R.id.layoutButton:
                switchMode();
                break;
            
        }
    }
    
    private void switchMode() {
        if (!editMode) {
            editMode = true;
            mEditButton.setImageResource(R.drawable.correct);
            mTextButton.setText("Valider le profil");
            poidsDésiréBar.setEnabled(true);
            poidsBar.setEnabled(true);
            activitéBar.setEnabled(true);
            for (int i = 0; i < mTailleBar.getChildCount(); i++) {
                mTailleBar.getChildAt(i).setEnabled(true);
            }
            for (int i = 0; i < mAgeBar.getChildCount(); i++) {
                mAgeBar.getChildAt(i).setEnabled(true);
            }
            for (int i = 0; i < mNiveauLayout.getChildCount(); i++) {
                mNiveauLayout.getChildAt(i).setVisibility(View.VISIBLE);
                mNiveauLayout.getChildAt(i).setClickable(true);
            }
            for (int i = 0; i < mMorphotypeLayout.getChildCount(); i++) {
                mMorphotypeLayout.getChildAt(i).setVisibility(View.VISIBLE);
                mMorphotypeLayout.getChildAt(i).setClickable(true);
            }
            for (int i = 0; i < mMorphotypeLayoutIcons.getChildCount(); i++) {
                mMorphotypeLayoutIcons.getChildAt(i).setVisibility(View.VISIBLE);
            }
            for (int i = 0; i < mObjectifLayout.getChildCount(); i++) {
                mObjectifLayout.getChildAt(i).setVisibility(View.VISIBLE);
                mObjectifLayout.getChildAt(i).setClickable(true);
            }
            for (int i = 0; i < mSexeLayout.getChildCount(); i++) {
                mSexeLayout.getChildAt(i).setVisibility(View.VISIBLE);
                mSexeLayout.getChildAt(i).setClickable(true);
            }
            
        } else {
            editMode = false;
            mEditButton.setImageResource(R.drawable.edit_ic);
            mTextButton.setText("Editer le profil");
    
            poidsDésiréBar.setEnabled(false);
            poidsBar.setEnabled(false);
            activitéBar.setEnabled(false);
            mTailleBar.setEnabled(false);
            for (int i = 0; i < mTailleBar.getChildCount(); i++) {
                mTailleBar.getChildAt(i).setEnabled(false);
            }
            for (int i = 0; i < mAgeBar.getChildCount(); i++) {
                mAgeBar.getChildAt(i).setEnabled(false);
            }
            for (int i = 0; i < mNiveauLayout.getChildCount(); i++) {
                if (i != isselected[0]) mNiveauLayout.getChildAt(i).setVisibility(View.GONE);
                
            }
            for (int i = 0; i < mObjectifLayout.getChildCount(); i++) {
                if (i != isselected[1]) mObjectifLayout.getChildAt(i).setVisibility(View.GONE);
                
            }
            for (int i = 0; i < mSexeLayout.getChildCount(); i++) {
                if (i != isselected[2]) mSexeLayout.getChildAt(i).setVisibility(View.GONE);
                
            }
            for (int i = 0; i < mMorphotypeLayout.getChildCount(); i++) {
                if (i != isselected[3]) mMorphotypeLayout.getChildAt(i).setVisibility(View.GONE);
                
            }
            for (int i = 0; i < mMorphotypeLayoutIcons.getChildCount(); i++) {
                mMorphotypeLayoutIcons.getChildAt(i).setVisibility(View.GONE);
                
            }
            updateUser(mUser);
        }
    }
    
    private void dumpUser(Utilisateur user) {
        poidsBar.setProgress((int) user.getPoids());
        poidsBar.setThumb(getThumb(poidsBar.getProgress()));
        poidsDésiréBar.setProgress((int) user.getPoidsDesire());
        switch (user.getMorphotype()) {
            case ECTOMORPHE:
                isselected[3] = 0;
                break;
            case MESOMORPHE:
                isselected[3] = 1;
                break;
            case ENDOMORPHE:
                isselected[3] = 2;
                break;
        }
        switch (user.getSexe()) {
            case HOMME:
                isselected[2] = 0;
                break;
            case FEMME:
                isselected[2] = 1;
                break;
            
        }
        switch (user.getObjectif()) {
            case GAIN_MASSE:
                isselected[1] = 0;
                break;
            case PERTE_POIDS:
                isselected[1] = 1;
                break;
            
        }
        switch (user.getNiveau()) {
            case DEBUTANT:
                isselected[0] = 0;
                break;
            case INTERMEDIAIRE:
                isselected[0] = 1;
                break;
            case AVANCE:
                isselected[0] = 2;
                break;
        }
        switch (user.getActivite()) {
            case LEGERE:
                activitéBar.setProgress(0);
                break;
            case MODEREE:
                activitéBar.setProgress(1);
                break;
            case INTENSE:
                activitéBar.setProgress(2);
                break;
        }
        int age = user.getAge(), taille = user.getTaille();
        for (int i = 2; i >= 0; i--) {
            ((MaterialNumberPicker) (mTailleBar.getChildAt(i))).setValue(taille % 10);
            taille = taille / 10;
            
        }
        for (int i = 1; i >= 0; i--) {
            ((MaterialNumberPicker) (mAgeBar.getChildAt(i))).setValue(age % 10);
            age = age / 10;
        }
        
    }
    
    private void updateUser(Utilisateur user) {
        switch (isselected[3]) {
            case 0:
                user.setMorphotype(Morphotype.ECTOMORPHE);
                break;
            case 1:
                user.setMorphotype(Morphotype.MESOMORPHE);
                break;
            case 2:
                user.setMorphotype(Morphotype.ENDOMORPHE);
                break;
        }
        switch (isselected[2]) {
            case 0:
                user.setSexe(Sexe.HOMME);
                break;
            case 1:
                user.setSexe(Sexe.FEMME);
                break;
            
        }
        switch (isselected[1]) {
            case 0:
                user.setObjectif(Objectif.GAIN_MASSE);
                break;
            case 1:
                user.setObjectif(Objectif.PERTE_POIDS);
                break;
            
        }
        switch (isselected[0]) {
            case 0:
                user.setNiveau(Niveau.DEBUTANT);
                break;
            case 1:
                user.setNiveau(Niveau.INTERMEDIAIRE);
                break;
            case 2:
                user.setNiveau(Niveau.AVANCE);
                break;
        }
        int age = 0, taille = 0;
        for (int i = 0; i < mTailleBar.getChildCount(); i++) {
            if (i != 3) {
                taille += ((MaterialNumberPicker) (mTailleBar.getChildAt(i))).getValue() * Math.pow(10, 2 - i);
            }
            
        }
        user.setTaille(taille);
        for (int i = 0; i < mAgeBar.getChildCount(); i++) {
            if (i != 2)
                age += ((MaterialNumberPicker) (mAgeBar.getChildAt(i))).getValue() * Math.pow(10, 1 - i);
            
        }
        user.setAge(age);
        switch (activitéBar.getProgress()) {
            case 0:
                user.setActivite(ActivitePhysique.LEGERE);
                break;
            case 1:
                user.setActivite(ActivitePhysique.MODEREE);
                break;
            case 2:
                user.setActivite(ActivitePhysique.INTENSE);
                break;
        }
        user.setPoids(poidsBar.getProgress());
        HistoriquePoidsManager historiquePoidsManager = new HistoriquePoidsManager();
        historiquePoidsManager.open();
        historiquePoidsManager.insert(new HistoriquePoids(user.getIdUtilisateur(), new Date(), poidsBar.getProgress()));
        user.setPoidsDesire(poidsDésiréBar.getProgress());
        UtilisateurManager utilisateurManager = new UtilisateurManager();
        utilisateurManager.open();
        utilisateurManager.delete(user.getIdUtilisateur());
        utilisateurManager.insert(user);
        RegimeManager regimeManager = new RegimeManager();
        regimeManager.open();
        Regime regime = new Regime(user);
        regime.calculPlanAlimentaire(user);
        regimeManager.insert(regime);
        RecommandationManager recommandationManager=new RecommandationManager();
        recommandationManager.open();
        recommandationManager.delete(user.getIdUtilisateur());
        Utils.dumpDatasnapshot();
        
    }
    
    private void ShowPopup() {
        myDialog.setContentView(R.layout.popup_image);
        ImageView photoView = myDialog.findViewById(R.id.user_photo);
        Glide.with(getApplicationContext()).load(bitmapToByte(drawableToBitmap(imgProfile.getDrawable()))).crossFade().thumbnail(0.5f).bitmapTransform(new CircleTransform(mView.getContext())).diskCacheStrategy(DiskCacheStrategy.NONE)    .skipMemoryCache( true ).into(photoView);
        ImageView exitView = myDialog.findViewById(R.id.action_exit);
        exitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
                myDialog.cancel();
                
            }
        });
        ImageView chooseView = myDialog.findViewById(R.id.action_choose);
        chooseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
                
                myDialog.dismiss();
                myDialog.cancel();
                
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
    
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        
    }
    
    private void uploadImage() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference ref = storageReference.child("images/" + mUser.getIdUtilisateur());
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Glide.with(getApplicationContext()).load(bitmapToByte(bitmap)).crossFade().thumbnail(0.5f).bitmapTransform(new CircleTransform(mView.getContext())).diskCacheStrategy(DiskCacheStrategy.NONE)    .skipMemoryCache( true ).into(Utils.imgProfile);
    
                    Glide.with(getApplicationContext()).load(bitmapToByte(bitmap)).crossFade().thumbnail(0.5f).bitmapTransform(new CircleTransform(mView.getContext())).diskCacheStrategy(DiskCacheStrategy.NONE)    .skipMemoryCache( true ).into(imgProfile);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    
                }
            });
            
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                uploadImage();
    
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
    
    public void setImage(ImageView imageView) {
        StorageReference ref = storage.getReference().child("images/" + mUser.getIdUtilisateur());
        Glide.with(this).using(new FirebaseImageLoader()).load(ref).crossFade().thumbnail(0.5f).bitmapTransform(new CircleTransform(mView.getContext())).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache( true ).into(imageView);
        
    }
    
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}


