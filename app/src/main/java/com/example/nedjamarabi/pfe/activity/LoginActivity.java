package com.example.nedjamarabi.pfe.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nedjamarabi.pfe.R;
import com.example.nedjamarabi.pfe.Utils.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.ProviderQueryResult;

import java.util.Arrays;
import java.util.Locale;


/**
 * Created by salimdeepside on 31/01/2018.
 */


public class LoginActivity extends Activity implements View.OnClickListener {
    private final static String TAG = "LoginActivity";
    private final static int RC_SIGN_IN = 900;
    private Task<GoogleSignInAccount> mGoogleSignInAccountTask;
    private LinearLayout mProgressBar;
    private TextView mNotUser, mForgotPassword;
    private EditText mEmailId, mPassword;
    private CheckBox mShowPassword;
    private FirebaseAuth mAuth;
    private Button mLoginFacebook, mLoginGoogle, mLoginButton;
    private String mGivenEmail, mGivenPassword;
    private LinearLayout mLoginLayout;
    private Animation mShakeAnimation;
    private Intent mIntent;
    private CallbackManager mCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseUser mCurrentUser;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String languageToLoad = "fr"; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_login);
        if (checkGooglePlayServices()) {
            mAuth = FirebaseAuth.getInstance();
            mCurrentUser = mAuth.getCurrentUser();
            if (mCurrentUser != null) loadHome();
            initUI();
            initListner();
            initConnectionUtil();
            
        }
    }
    
    private void initConnectionUtil() {
        mCallbackManager = CallbackManager.Factory.create();
        @SuppressWarnings("deprecation") GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestScopes(new Scope(Scopes.PLUS_LOGIN)).requestScopes(new Scope(Scopes.PLUS_ME)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    
    private void initUI() {
        mShakeAnimation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.shake);
        mLoginLayout = findViewById(R.id.login_layout);
        mNotUser = findViewById(R.id.createAccount);
        mLoginButton = findViewById(R.id.loginBtn);
        mForgotPassword = findViewById(R.id.forgot_password);
        mEmailId = findViewById(R.id.login_emailid);
        mPassword = findViewById(R.id.login_password);
        mShowPassword = findViewById(R.id.show_hide_password);
        mLoginFacebook = findViewById(R.id.button_facebook_login);
        mLoginGoogle = findViewById(R.id.button_google_login);
        mProgressBar = findViewById(R.id.progressBar);
    }
    
    private void initListner() {
        mLoginButton.setOnClickListener(this);
        mForgotPassword.setOnClickListener(this);
        mLoginFacebook.setOnClickListener(this);
        mLoginGoogle.setOnClickListener(this);
        mNotUser.setOnClickListener(this);
        mShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) mPassword.setTransformationMethod(null);
                else mPassword.setTransformationMethod(new PasswordTransformationMethod());
            }
        });
        
    }
    
    
    private boolean checkValidation() {
        mGivenEmail = mEmailId.getText().toString().trim();
        mGivenPassword = mPassword.getText().toString().trim();
        if (TextUtils.isEmpty(mGivenEmail)) {
            mLoginLayout.startAnimation(mShakeAnimation);
            Toast.makeText(getApplicationContext(), R.string.fui_missing_email_address, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(mGivenPassword)) {
            mLoginLayout.startAnimation(mShakeAnimation);
            Toast.makeText(getApplicationContext(), R.string.fui_missing_password, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    
    
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginBtn:
                if (Utils.isOnline(this)) signInEmail();
                else
                    Snackbar.make(view, "Connection à internet impossible", Snackbar.LENGTH_LONG).show();
                break;
            case R.id.forgot_password:
                mIntent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(mIntent);
                break;
            case R.id.createAccount:
                mIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(mIntent);
                break;
            case R.id.button_facebook_login:
                if (Utils.isOnline(this)) signInFacebook();
                else
                    Snackbar.make(view, "Connection à internet impossible", Snackbar.LENGTH_LONG).show();
                break;
            case R.id.button_google_login:
                if (Utils.isOnline(this)) signInGoogle();
                else
                    Snackbar.make(view, "Connection à internet impossible", Snackbar.LENGTH_LONG).show();
                break;
            
        }
        
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN && resultCode != 0) {
            showLoadingDialog();
            mGoogleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            mAuth.fetchProvidersForEmail(mGoogleSignInAccountTask.getResult().getEmail()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                @Override
                public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().getProviders().size() == 0 || !task.getResult().getProviders().get(0).equals("facebook.com")) {
                            try {
                                GoogleSignInAccount account = mGoogleSignInAccountTask.getResult(ApiException.class);
                                firebaseAuthWithGoogle(account);
                            } catch (ApiException e) {
                                mLoginLayout.startAnimation(mShakeAnimation);
                                dismissLoadingDialogShowLayout();
                                
                            }
                        } else {
                            mGoogleSignInClient.signOut();
                            Toast.makeText(LoginActivity.this, R.string.fui_error_user_collision, Toast.LENGTH_LONG).show();
                            dismissLoadingDialogShowLayout();
                            
                        }
                    }
                    
                }
                
            });
            
        } else mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
    
    private void handleFacebookAccessToken(AccessToken token) {
        showLoadingDialog();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mCurrentUser = mAuth.getCurrentUser();
                    loadHome();
                    Toast.makeText(LoginActivity.this, R.string.fui_welcome_back_email_header, Toast.LENGTH_SHORT).show();
                    dismissLoadingDialog();
                } else {
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    mLoginLayout.startAnimation(mShakeAnimation);
                    dismissLoadingDialogShowLayout();
                    
                }
            }
        });
    }
    
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mCurrentUser = mAuth.getCurrentUser();
                    dismissLoadingDialog();
                    loadHome();
                } else {
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    mLoginLayout.setVisibility(View.VISIBLE);
                    dismissLoadingDialogShowLayout();
                    
                }
            }
        });
    }
    
    private void loadHome() {
        mIntent = new Intent(LoginActivity.this, MainActivity.class);
        finish();
        startActivity(mIntent);
    }
    
    
    private void signInEmail() {
        if (checkValidation()) {
            showLoadingDialog();
            mAuth.signInWithEmailAndPassword(mGivenEmail, mGivenPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mCurrentUser = mAuth.getCurrentUser();
                        if (mCurrentUser.isEmailVerified()) {
                            Toast.makeText(LoginActivity.this, R.string.fui_welcome_back_email_header, Toast.LENGTH_SHORT).show();
                            loadHome();
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.fui_title_confirm_recover_password, Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                            
                        }
                        
                    } else {
                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        mLoginLayout.startAnimation(mShakeAnimation);
                        
                    }
                    
                }
                
            });
            dismissLoadingDialogShowLayout();
        }
        
    }
    
    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    
    private void signInFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                showLoadingDialog();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }
            
            @Override
            public void onCancel() {
            }
            
            @Override
            public void onError(FacebookException error) {
            }
        });
    }
    
    private boolean checkGooglePlayServices() {
        @SuppressWarnings("deprecation") final int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            //noinspection deprecation
            Log.e(TAG, GooglePlayServicesUtil.getErrorString(status));
            @SuppressWarnings("deprecation") Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, 1);
            dialog.show();
            return false;
        } else {
            //noinspection deprecation
            Log.i(TAG, GooglePlayServicesUtil.getErrorString(status));
            return true;
        }
    }
    
    private void showLoadingDialog() {
        mProgressBar.setVisibility(View.VISIBLE);
        mLoginLayout.setVisibility(View.GONE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        
    }
    
    private void dismissLoadingDialog() {
        mProgressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        
    }
    
    private void dismissLoadingDialogShowLayout() {
        dismissLoadingDialog();
        mLoginLayout.setVisibility(View.VISIBLE);
        
    }
    
}
