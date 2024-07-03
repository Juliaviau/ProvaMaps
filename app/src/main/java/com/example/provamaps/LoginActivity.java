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

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        binding.btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginGoogleLogin();
            }
        });


        binding.buttoninv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //signInButton = findViewById(R.id.btnGoogle);
        //textView = findViewById(R.id.tv_resposta);
        /*mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.values.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });*/
    }


    private void beginGoogleLogin () {
        Log.d(TAG, "beginGoogleLogin");

        Intent googleSigninIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInnARL.launch(googleSigninIntent);
    }

    private ActivityResultLauncher<Intent> googleSignInnARL = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    Log.d(TAG,"onActivityResult");

                    if (o.getResultCode() == Activity.RESULT_OK) {
                        Intent data = o.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            Log.d(TAG,"onActivityResult: AccountID: " + account.getId());
                            firebaseAuthWithGoogleAccount(account.getIdToken());
                        } catch (Exception e) {
                            Log.e(TAG,"onActivityResult: ", e);
                        }
                    } else {
                        Log.d(TAG, "onActivityResult: Cancelant...");
                        MyUtils.toast(LoginActivity.this,"Cancelat...");

                    }
                }
            }
    );

    private void firebaseAuthWithGoogleAccount (String idToken) {
        Log.d(TAG,"firebaseAuthWithGoogleAccount: idToken:" + idToken);

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        firebaseAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                if (authResult.getAdditionalUserInfo().isNewUser()){
                    Log.d(TAG,"onSucces: Account Created...");
                    updateUserInfoDB();
                } else {
                    Log.d(TAG,"onSucces: Logged in...");
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finishAffinity();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"onFailure: ",e);
            }
        });

    }

    private void updateUserInfoDB () {
        Log.d(TAG,"updateUserInfoDB: ");
        progressDialog.setMessage("Guardant informació de l'usuari...");
        progressDialog.show();
        long timestamp = MyUtils.timestamp();
        String registeredUserID = firebaseAuth.getUid();
        String registeredUserEmail = firebaseAuth.getCurrentUser().getEmail();
        String name = firebaseAuth.getCurrentUser().getDisplayName();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", registeredUserID);
        hashMap.put("email", registeredUserEmail);
        hashMap.put("name",name);
        hashMap.put("timestamp",timestamp);
        hashMap.put("profileImageUrl","");
        hashMap.put("dob","");
        hashMap.put("userType",MyUtils.USER_TYPE_GOOGLE);
        hashMap.put("token","");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(registeredUserID).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Log.d(TAG,"onSucces: Usuari guardat...");
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finishAffinity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"onFailure: ",e);
                        progressDialog.dismiss();
                        MyUtils.toast(LoginActivity.this,"Error en guardar per " + e.getMessage());
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

   /* private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void updateUI(FirebaseUser user) {
        user = mAuth.getCurrentUser();
        if(user!=null) {
            irHome();
        }
    }

    private void irHome() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }*/

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        /*if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                textView.setText(e.getMessage());
                //Log.w(TAG, "Google sign in failed", e);
            }
        }
    }*/

    private void firebaseAuthWithGoogle(String idToken) {
        /*AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "signInWithCredential:success");
                            irHome();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            // Log.w(TAG, "signInWithCredential:failure", task.getException());
                            textView.setText(task.getException().toString());

                            updateUI(null);
                        }
                    }
                });*/
    }
}