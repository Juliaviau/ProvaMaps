package com.example.provamaps;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthManager {


    // Instancia única del singleton
    private static AuthManager instance;

    // Referencia a FirebaseAuth
    private FirebaseAuth firebaseAuth;

    // Constructor privado para evitar la creación directa de instancias
    private AuthManager() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    // Método para obtener la instancia única
    public static AuthManager getInstance() {
        if (instance == null) {
            instance = new AuthManager();
        }
        return instance;
    }

    //tancar la sessio: AuthManager.getInstance().signOut();
    void signOut() {
        firebaseAuth.signOut();
    }

    FirebaseUser obtenirUsuari() {
        return firebaseAuth.getCurrentUser();
    }
}
