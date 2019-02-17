package com.deepside.nutritionapp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.deepside.nutritionapp.R;
import com.deepside.nutritionapp.Utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by salimdeepside on 31/01/2018.
 */

public class ForgetPasswordActivity extends Activity implements View.OnClickListener {

    private final static String TAG = "ForgetPasswordActivity";
    private EditText mEmailRequete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        mEmailRequete = findViewById(R.id.registered_emailid);
        TextView gotoLoginBtn = findViewById(R.id.backToLoginBtn);
        TextView envoieBtn = findViewById(R.id.forgot_button);
        envoieBtn.setOnClickListener(this);
        gotoLoginBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.forgot_button:
                if (Utils.isOnline(this)) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(mEmailRequete.getText().toString().trim()).addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgetPasswordActivity.this, "Réstauration de mot de passe faite avec succès.", Toast.LENGTH_SHORT).show();
                                onBackPressed();

                            } else {
                                Toast.makeText(ForgetPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                findViewById(R.id.signup_layout).startAnimation(AnimationUtils.loadAnimation(ForgetPasswordActivity.this, R.anim.shake));

                            }
                        }
                    });
                } else
                    Snackbar.make(view, "Connection à internet impossible", Snackbar.LENGTH_LONG).show();
                break;
            case R.id.backToLoginBtn:
                onBackPressed();
                break;

        }
    }

}
