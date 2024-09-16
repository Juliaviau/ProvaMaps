package com.example.provamaps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import com.example.provamaps.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {


    private ActivityLoginBinding binding;
    private static final String TAG = "LOGIN_OPTIONS_TAG";
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;

    //private static final int RC_SIGN_IN = 1;
    /*GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    SignInButton signInButton;*/
    //TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Siusplau espera");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.btnGoogle.setSize(SignInButton.SIZE_STANDARD);
        binding.btnGoogle.setColorScheme(SignInButton.COLOR_LIGHT);
        setGoogleButtonText(binding.btnGoogle, getString(R.string.iniciar_amb_google));

        binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutAndRevokeAccess();
            }
        });

        binding.buttoninv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MyUtils.toast(LoginActivity.this, "Entrar com a invitat?");
                finish(); //amb finish va a lactivit que hi havia oberta abans
                //startActivity(new Intent(LoginActivity.this,MainActivity.class));
                //finishAffinity();
            }
        });
    }

    private void setGoogleButtonText(SignInButton signInButton, String buttonText) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);
            if (v instanceof TextView) {
                ((TextView) v).setText(buttonText);
                return;
            }
        }
    }
    private void signOutAndRevokeAccess() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mGoogleSignInClient.revokeAccess().addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        beginGoogleLogin();
                    }
                });
            }
        });
    }

    private void beginGoogleLogin() {
        Log.d(TAG, "beginGoogleLogin");
        Intent googleSignInIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInnARL.launch(googleSignInIntent);
    }

    private ActivityResultLauncher<Intent> googleSignInnARL = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    Log.d(TAG, "onActivityResult");

                    if (o.getResultCode() == Activity.RESULT_OK) {
                        Intent data = o.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            //inicia be la sessio
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            Log.d(TAG, "onActivityResult: ID de la conta: " + account.getId());
                            firebaseAuthWithGoogleAccount(account.getIdToken());
                        } catch (Exception e) {
                            Log.e(TAG, "onActivityResult: ", e);
                        }
                    } else {
                        Log.d(TAG, "onActivityResult: Cancelant...");
                        MyUtils.toast(LoginActivity.this, "Cancelat...");
                    }
                }
            }
    );

    private void firebaseAuthWithGoogleAccount(String idToken) {
        Log.d(TAG, "firebaseAuthWithGoogleAccount: idToken:" + idToken);

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        //Iniciar sessio amb google
        firebaseAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                if (authResult.getAdditionalUserInfo().isNewUser()) {
                    //Si es un usuari nou crea la nova conta
                    Log.d(TAG, "onSucces: Nova conta creada...");
                    updateUserInfoDB();
                } else {
                    //Si usuari ja existia, entra directament amb la sessió
                    Log.d(TAG, "onSucces: Iniciant Sessió...");
                    //Si ja existeix, obre un nou MainActivity i tanca els que tenia oberts
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finishAffinity();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: ", e);
            }
        });
    }

    private void updateUserInfoDB() {
        Log.d(TAG, "updateUserInfoDB: ");
        progressDialog.setMessage("Guardant informació de l'usuari...");
        progressDialog.show();

        String idUsuari = firebaseAuth.getUid();
        String emailUsuari = firebaseAuth.getCurrentUser().getEmail();
        String nomUsuari = firebaseAuth.getCurrentUser().getDisplayName();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", idUsuari);
        hashMap.put("email", emailUsuari);
        hashMap.put("name", nomUsuari);
        hashMap.put("profileImageUrl", "");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(idUsuari).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSucces: Usuari guardat...");
                        progressDialog.dismiss();

                        //Si no existeix i el crea correctament, obre un nou MainActivity i tanca els que tenia oberts
                        MyUtils.toast(LoginActivity.this, "updateusaerdb succes");
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finishAffinity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                        progressDialog.dismiss();
                        MyUtils.toast(LoginActivity.this, "updateuserdbfailure");
                        MyUtils.toast(LoginActivity.this, "Error en guardar per " + e.getMessage());
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        /*FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);*/
    }
}