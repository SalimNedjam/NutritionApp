package com.deepside.nutritionapp.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.deepside.nutritionapp.Fragments.*;
import com.deepside.nutritionapp.Managers.UtilisateurManager;
import com.deepside.nutritionapp.R;
import com.deepside.nutritionapp.Suivi.Utilisateur;
import com.deepside.nutritionapp.Utils.CircleTransform;
import com.deepside.nutritionapp.Utils.DateUtils;
import com.deepside.nutritionapp.Utils.Utils;
import com.facebook.login.LoginManager;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private static final String TAG_HOME = "HOME";
    private static final String TAG_JOURNAL = "JOURNAL";
    private static final String TAG_STATISTIQUES = "STATISTIQUES";
    private static final String TAG_PROFILE = "PROFILE";
    private static String CURRENT_TAG = TAG_HOME;
    private static int mNavIndex = 3;
    Uri filePath;
    StorageReference reference;
    byte[] data;
    ImageView imgProfile;
    NavigationView navigationView;
    View navHeader;
    private UtilisateurManager mUtilisateurManager;
    private Bundle bundle = new Bundle();
    private NavigationView mNavigationView;
    private boolean journal = true;
    private DrawerLayout drawer;
    private TextView mName, mEmail;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private String[] activityTitles;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private Handler mHandler;
    private Utilisateur user;
    private LinearLayout mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String languageToLoad = "fr"; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mHandler = new Handler();
        mAuth = FirebaseAuth.getInstance();
        LoginManager loginManager = LoginManager.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        initUI();
        setUpNavigationView();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);
        Utils.imgProfile = imgProfile;

        showLoadingDialog();
        findViewById(R.id.textWaiter).postDelayed(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.textWaiter)).setText("\n\nChargement des données de votre compte.\nVeuillez patienter...");
            }
        }, 5000);
        findViewById(R.id.textWaiter).postDelayed(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.textWaiter)).setText("\n\nOrganisation des données.\nVeuillez patienter...");
            }
        }, 10000);

        fab = findViewById(R.id.fab);
        getUserFromDb();
        if (savedInstanceState == null) {
            mNavIndex = 0;
            CURRENT_TAG = TAG_HOME;
        }
        toggleFab();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                journal = !journal;
                loadFragment();

            }
        });
    }

    private void getUserFromDb() {
        mUtilisateurManager = new UtilisateurManager();
        mUtilisateurManager.open();
        DatabaseReference databaseReference = Utils.getDatabase().getReference();
        if (databaseReference != null) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user = mUtilisateurManager.get(dataSnapshot.child("Utilisateur").child(mAuth.getCurrentUser().getUid()));
                    if (user == null) {
                        user = new Utilisateur();
                        user.setIdUtilisateur(mAuth.getCurrentUser().getUid());
                        user.setUsername(mAuth.getCurrentUser().getDisplayName());
                        user.setDateInscription(DateUtils.stringify(new Date()));
                        user.setNiveau(null);
                        mNavIndex = 3;
                        CURRENT_TAG = TAG_PROFILE;
                        uploadImage();


                    }
                    StorageReference ref = FirebaseStorage.getInstance().getReference().child("images/" + mAuth.getCurrentUser().getUid());
                    Glide.with(getApplicationContext()).using(new FirebaseImageLoader()).load(ref).crossFade().thumbnail(0.5f).bitmapTransform(new CircleTransform(getApplicationContext())).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imgProfile);
                    loadNavHeader();
                    loadFragment();
                    dismissLoadingDialog();
                    Utils.sDataSnapshot = dataSnapshot;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    loadFragment();
                    dismissLoadingDialog();

                }
            });
        }
    }


    private void initUI() {
        drawer = findViewById(R.id.drawer_layout);
        mProgressBar = findViewById(R.id.progressBar);
        mNavigationView = findViewById(R.id.nav_view);
        View navHeader = mNavigationView.getHeaderView(0);
        mName = navHeader.findViewById(R.id.name);
        mEmail = navHeader.findViewById(R.id.website);
        mProgressBar = findViewById(R.id.progressBarMain);
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

    }


    private void loadNavHeader() {
        mName.setText(user.getUsername());
        mEmail.setText(mAuth.getCurrentUser().getEmail());

    }


    private void loadFragment() {
        if ((user.getNiveau() == null && mNavIndex == 3) || user.getNiveau() != null) {
            selectNavMenu();
            setToolbarTitle();
            if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null && mNavIndex != 1) {
                drawer.closeDrawers();
                return;
            }
            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    Fragment fragment = getFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            };
            if (mPendingRunnable != null) {
                mHandler.post(mPendingRunnable);
            }
            drawer.closeDrawers();
            toggleFab();
            invalidateOptionsMenu();
        }

    }

    private Fragment getFragment() {
        switch (mNavIndex) {
            case 0:
                HomeFragment homeFragment = new HomeFragment();
                bundle.putSerializable("param1", user);
                homeFragment.setArguments(bundle);
                return homeFragment;
            case 1:
                if (!journal) {
                    RecommandéFragment recommandéFragment = new RecommandéFragment();
                    bundle.putSerializable("param1", user);
                    recommandéFragment.setArguments(bundle);
                    return recommandéFragment;
                } else {
                    JournalFragment journalFragment = new JournalFragment();
                    bundle.putSerializable("param1", user);
                    journalFragment.setArguments(bundle);
                    return journalFragment;
                }
            case 2:
                StatistiqueFragment statistiqueFragment = new StatistiqueFragment();
                bundle.putSerializable("param1", user);
                statistiqueFragment.setArguments(bundle);
                return statistiqueFragment;
            case 3:
                ProfileFragment profileFragment = new ProfileFragment();
                bundle.putSerializable("param1", user);
                profileFragment.setArguments(bundle);
                return profileFragment;
            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[mNavIndex]);

    }

    private void selectNavMenu() {
        mNavigationView.getMenu().getItem(mNavIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        mNavIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_journal:
                        mNavIndex = 1;
                        CURRENT_TAG = TAG_JOURNAL;
                        break;
                    case R.id.nav_statistique:
                        mNavIndex = 2;
                        CURRENT_TAG = TAG_STATISTIQUES;
                        break;
                    case R.id.nav_profile:
                        mNavIndex = 3;
                        CURRENT_TAG = TAG_PROFILE;
                        break;
                    case R.id.nav_about_us:
                        startActivity(new Intent(MainActivity.this, AProposDeNousActivity.class));
                        drawer.closeDrawers();
                        return true;
                    default:
                        mNavIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;

                }
                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);
                loadFragment();
                return true;
            }
        });
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

        };
        //noinspection deprecation
        drawer.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        boolean shouldLoadHomeFragOnBackPress = true;
        if (shouldLoadHomeFragOnBackPress) {
            if (mNavIndex != 0) {
                mNavIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadFragment();
                return;
            }

        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mNavIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);

        }
        if (mNavIndex == 3) {
            getMenuInflater().inflate(R.menu.profile, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            signOut();
            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        //mLoginManager.logOut();
        mGoogleSignInClient.signOut();
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
        drawer.closeDrawers();
    }

    private void toggleFab() {
        if (mNavIndex == 1) fab.show();
        else fab.hide();
    }

    private void showLoadingDialog() {
        mProgressBar.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.GONE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    private void dismissLoadingDialog() {
        mProgressBar.setVisibility(View.GONE);
        toolbar.setVisibility(View.VISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    private void uploadImage() {

        imgProfile.setDrawingCacheEnabled(true);
        imgProfile.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imgProfile.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        data = baos.toByteArray();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference ref = storageReference.child("images/" + user.getIdUtilisateur());
        UploadTask uploadTask = ref.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });


    }


    private byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }


}
