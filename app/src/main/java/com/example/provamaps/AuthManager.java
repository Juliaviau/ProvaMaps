package com.example.provamaps;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthManager {
    private FirebaseAuth firebaseAuth;

    void signOut() {
        firebaseAuth.signOut();
    }

    FirebaseUser obtenirUsuari() {
        return firebaseAuth.getCurrentUser();
    }

}
