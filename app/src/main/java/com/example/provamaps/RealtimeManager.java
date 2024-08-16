package com.example.provamaps;

import static kotlinx.coroutines.channels.ProduceKt.awaitClose;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import kotlinx.coroutines.flow.Flow;

public class RealtimeManager {

    // Instancia unica de la classe Singleton
    private static RealtimeManager instancia;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();//.getInstance().reference.child("nombd");
    private DatabaseReference databaseReferenceFonts =  mDatabase.child("Fonts");
    private AuthManager authManager = AuthManager.getInstance();/* = new AuthManager();*/
    private PenjarImatges penjarImatges = new PenjarImatges();


    // Constructor privat per evitar que es faci new des de fora
    private RealtimeManager() {}

    // Metode estatic public per obtenir l'instancia unica
    public static synchronized RealtimeManager getInstance() {
        if (instancia == null) {
            instancia = new RealtimeManager();
        }
        return instancia;
    }

    //IMATGES




    //FONTS

    public void afegirFont(String latitud, String longitud, String potable, String estat, Uri imagenUri, PenjarImatges.OnImageUploadListener listener) {

        String fontId = databaseReferenceFonts.push().getKey();
        if (fontId == null) {
            listener.onUploadFailed("Error al generar la clau de la font");
            return;
        }

        Font font = new Font(fontId, latitud, longitud, potable, estat, null); //Sense imatge de moment


        if (imagenUri != null) {
            // Si hi ha foto, s'afegeix al storage manager
            penjarImatges.penjarFotos(imagenUri, fontId, new PenjarImatges.OnImageUploadListener() {
                @Override
                public void onUploadSuccess(String imagenUri) {
                    font.setImageUrl(imagenUri);
                    guardarFont(font);
                    listener.onUploadSuccess(imagenUri);
                }

                @Override
                public void onUploadFailed(String errorMessage) {
                    listener.onUploadFailed(errorMessage);
                }
            });
        } else {
            // Si no hi ha imatge
            guardarFont(font);
            listener.onUploadSuccess(null);
        }
    }


    private void guardarFont(Font font) {
        databaseReferenceFonts.child(font.getKey()).setValue(font)
                .addOnSuccessListener(aVoid -> {
                    // Éxito
                    Log.d("RealtimeManager", "Font guardada exitosamente");

                })
                .addOnFailureListener(e -> {
                    // Manejar error
                    Log.e("RealtimeManager", "Error al guardar la font: " + e.getMessage());

                });
    }

    /*void afegirFont(Font font) {
        //identificador unic per la font
        String key = databaseReferenceFonts.push().getKey();
        if (key != null) {
            databaseReferenceFonts.child(key).setValue(font);
        }
    }*/
    void eliminarFont(String fontId) {
        databaseReferenceFonts.child(fontId).removeValue();
    }

    void actualitzarFont(String fontId, Font font) {
        databaseReferenceFonts.child(fontId).setValue(font);
    }

    public LiveData<List<Font>> obtenirFontsUsuari() {
        MutableLiveData<List<Font>> liveData = new MutableLiveData<>();
        String idFilter = authManager.obtenirUsuari() != null ? authManager.obtenirUsuari().getUid() : null;

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Font> llistaFonts = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Font font = childSnapshot.getValue(Font.class);
                    if (font != null && font.uidusuari.equals(idFilter)) {
                        font.setKey(childSnapshot.getKey());
                        llistaFonts.add(font);
                    }
                }
                liveData.setValue(llistaFonts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        };

        databaseReferenceFonts.addValueEventListener(listener);

        return liveData;
    }
}
