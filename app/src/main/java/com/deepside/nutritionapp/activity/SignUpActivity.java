package com.deepside.nutritionapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.deepside.nutritionapp.R;
import com.deepside.nutritionapp.Utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by salimdeepside on 31/01/2018.
 */

public class SignUpActivity extends Activity implements View.OnClickListener {
    
    private final static String TAG = "SignUpActivity";
    private EditText fullName, emailId, password, confirmPassword;
    private String getEmailId, getPassword;
    private CheckBox terms_conditions;
    private boolean isChecked;
    private FirebaseAuth mAuth;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String languageToLoad = "fr"; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_signup);
        fullName = findViewById(R.id.userName);
        emailId = findViewById(R.id.userEmailId);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        Button signUpButton = findViewById(R.id.signUpBtn);
        TextView already_user = findViewById(R.id.already_user);
        terms_conditions = findViewById(R.id.terms_conditions);
        
        terms_conditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!terms_conditions.isChecked()) {
                    terms_conditions.setChecked(false);
                }
                else {
                    terms_conditions.setChecked(false);
                    new AlertDialog.Builder(SignUpActivity.this)
                        .setTitle("Conditions générales d'utilisation de l’application")
                        .setMessage("\nARTICLE 1 : Objet\n" + "Les présentes « conditions générales d'utilisation » ont pour objet l'encadrement juridique des modalités de mise à disposition des services de l’application Nutrition App et leur utilisation par « l'Utilisateur ».\n" + "Les conditions générales d'utilisation doivent être acceptées par tout Utilisateur souhaitant accéder à l’application. Elles constituent le contrat entre l’application et l'Utilisateur. L’accès à l’application par l’Utilisateur signifie son acceptation des présentes conditions générales d’utilisation.\n" + "Éventuellement :\n" + "•\tEn cas de non-acceptation des conditions générales d'utilisation stipulées dans le présent contrat, l'Utilisateur se doit de renoncer à l'accès des services proposés par le site.\n" + "•\tNutrition App se réserve le droit de modifier unilatéralement et à tout moment le contenu des présentes conditions générales d'utilisation.\n\n" + "ARTICLE 2 : Définitions\n" + "La présente clause a pour objet de définir les différents termes essentiels du contrat :\n" + "•\tUtilisateur : ce terme désigne toute personne qui utilise l’application ou l'un des services proposés par l’application.\n" + "•\tContenu utilisateur : ce sont les données transmises par l'Utilisateur au sein de l’application.\n" + "•\tMembre : l'Utilisateur devient membre lorsqu'il est identifié sur l’application.\n" + "•\tIdentifiant et mot de passe : c'est l'ensemble des informations nécessaires à l'identification d'un Utilisateur sur l’application. L'identifiant et le mot de passe permettent à l'Utilisateur d'accéder à des services réservés aux membres de l’application. Le mot de passe est confidentiel.\n\n" + "ARTICLE 3 : Propriété intellectuelle\n" + "Les marques, logos, signes et tout autre contenu du site font l'objet d'une protection par le Code de la propriété intellectuelle et plus particulièrement par le droit d'auteur.\n" + "L'Utilisateur sollicite l'autorisation préalable de l’application pour toute reproduction, publication, copie des différents contenus.\n" + "L'Utilisateur s'engage à une utilisation des contenus de l’application dans un cadre strictement privé. Une utilisation des contenus à des fins commerciales est strictement interdite.\n" + "Tout contenu mis en ligne par l'Utilisateur est de sa seule responsabilité. L'Utilisateur s'engage à ne pas mettre en ligne de contenus pouvant porter atteinte aux intérêts de tierces personnes. Tout recours en justice engagé par un tiers lésé contre l’application sera pris en charge par l'Utilisateur. \n" + "Le contenu de l'Utilisateur peut être à tout moment et pour n'importe quelle raison supprimé ou modifié par l’application. L'Utilisateur ne reçoit aucune justification et notification préalablement à la suppression ou à la modification du contenu Utilisateur.\n")
                        .setPositiveButton("Accepter", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                terms_conditions.setChecked(true);
                            }
                        })
                        .setNegativeButton("Refuser", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                terms_conditions.setChecked(false);
                            }
                        })
                        
                        .setCancelable(false)
                        .show();
            }
            }
        });
        mAuth = FirebaseAuth.getInstance();
        signUpButton.setOnClickListener(this);
        already_user.setOnClickListener(this);
        
    }
    
    private boolean checkValidation() {
        String getFullName = fullName.getText().toString().trim();
        String getConfirmPassword = confirmPassword.getText().toString().trim();
        getEmailId = emailId.getText().toString().trim();
        getPassword = password.getText().toString().trim();
        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(getEmailId);
        if (getFullName.equals("") || getFullName.length() == 0 || getEmailId.equals("") || getEmailId.length() == 0 || getPassword.equals("") || getPassword.length() == 0 || getConfirmPassword.equals("") || getConfirmPassword.length() == 0) {
            Toast.makeText(SignUpActivity.this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!m.find()) {
            Toast.makeText(SignUpActivity.this, R.string.fui_invalid_email_address, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!getConfirmPassword.equals(getPassword)) {
            Toast.makeText(SignUpActivity.this, "Both password doesn't match.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (getPassword.length() < 6) {
            Toast.makeText(SignUpActivity.this, "You need at least 6 characters.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!terms_conditions.isChecked()) {
            Toast.makeText(SignUpActivity.this, "Please select Terms and Conditions.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signUpBtn:
                if (checkValidation()) {
                    if (Utils.isOnline(this)) {
                        mAuth.createUserWithEmailAndPassword(getEmailId, getPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "signInWithEmail:success");
                                    sendVerificationEmail();
                                    UserProfileChangeRequest profileUpdates =new UserProfileChangeRequest.Builder()
                                            .setDisplayName(fullName.getText().toString()).build();
                                    
        
                                            FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                
                                                }
                                            });
                                } else {
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(SignUpActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    findViewById(R.id.signup_layout).startAnimation(AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.shake));
                                    
                                }
                                // ...
                            }
                        });
                    } else
                        Snackbar.make(view, "No internet connection", Snackbar.LENGTH_LONG).show();
                    
                } else
                    findViewById(R.id.signup_layout).startAnimation(AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.shake));
                break;
            case R.id.already_user:
                onBackPressed();
                break;
            
        }
    }
    
    private void sendVerificationEmail() {
        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, R.string.fui_email_sent, Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                    finish();
                } else {
                    sendVerificationEmail();
                    
                }
            }
        });
    }
    
}
