package com.example.provamaps;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RealtimeManager {

    // Instancia unica de la classe Singleton
    private static RealtimeManager instancia;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();//.getInstance().reference.child("nombd");
    private DatabaseReference databaseReferenceFonts =  mDatabase.child("Fonts");
    private DatabaseReference databaseReferencePicnic =  mDatabase.child("Picnics");
    private DatabaseReference databaseReferenceLavabos =  mDatabase.child("Lavabos");
    private DatabaseReference databaseReferenceContenidors =  mDatabase.child("Contenidors");
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


    //FONTS

    public void afegirFont(String latitud, String longitud, String potable, String estat, byte[] imageData, PenjarImatges.OnImageUploadListener listener) {

        String fontId = databaseReferenceFonts.push().getKey();
        if (fontId == null) {
            listener.onUploadFailed("Error al generar la clau de la font");
            return;
        }

        Font font = new Font(fontId, latitud, longitud, potable, estat, null); //Sense imatge de moment


        if (imageData != null) {
            // Si hi ha foto, s'afegeix al storage manager
            penjarImatges.penjarFotos(imageData, fontId, new PenjarImatges.OnImageUploadListener() {
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
                    Log.d("RealtimeManager", "Font guardada exitosament");

                })
                .addOnFailureListener(e -> {
                    // Manejar error
                    Log.e("RealtimeManager", "Error al guardar la font: " + e.getMessage());

                });
    }

    void eliminarFont(String fontId) {
        databaseReferenceFonts.child(fontId).removeValue();
        //TODO: Eliminar tambe la foto
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

    public LiveData<List<Font>> obtenirFonts() {
        MutableLiveData<List<Font>> liveData = new MutableLiveData<>();

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Font> llistaFonts = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Font font = childSnapshot.getValue(Font.class);
                    if (font != null) {
                        font.setKey(childSnapshot.getKey());
                        llistaFonts.add(font);
                    }
                }
                liveData.setValue(llistaFonts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar el error aquí si es necesario
            }
        };

        // Escucha todos los cambios en el nodo donde están almacenadas las fuentes
        databaseReferenceFonts.addValueEventListener(listener);

        return liveData;
    }

    //LAVABOS
    public void afegirLavabo(String latitud, String longitud, List<Integer> tipusLavabo, String disposaPaper, String disposaPica, byte[] imageData, PenjarImatges.OnImageUploadListener listener) {

        String lavaboId = databaseReferenceLavabos.push().getKey();
        if (lavaboId == null) {
            listener.onUploadFailed("Error al generar la clau del lavabo");
            return;
        }

        Lavabo lavabo = new Lavabo(lavaboId, latitud, longitud, tipusLavabo, disposaPaper, disposaPica, null); //Sense imatge de moment


        if (imageData != null) {
            // Si hi ha foto, s'afegeix al storage manager
            penjarImatges.penjarFotos(imageData, lavaboId, new PenjarImatges.OnImageUploadListener() {
                @Override
                public void onUploadSuccess(String imagenUri) {
                    lavabo.setImageUrl(imagenUri);
                    guardarLavabo(lavabo);
                    listener.onUploadSuccess(imagenUri);
                }

                @Override
                public void onUploadFailed(String errorMessage) {
                    listener.onUploadFailed(errorMessage);
                }
            });
        } else {
            // Si no hi ha imatge
            guardarLavabo(lavabo);
            listener.onUploadSuccess(null);
        }
    }

    private void guardarLavabo(Lavabo lavabo) {
        databaseReferenceLavabos.child(lavabo.getKey()).setValue(lavabo)
                .addOnSuccessListener(aVoid -> {
                    Log.d("RealtimeManager", "Lavabo guardat exitosament");
                })
                .addOnFailureListener(e -> {
                    Log.e("RealtimeManager", "Error al guardar el lavabo: " + e.getMessage());
                });
    }

    void eliminarLavabo(String lavaboId) {
        databaseReferenceLavabos.child(lavaboId).removeValue();
        //TODO: Eliminar tambe la foto
    }

    void actualitzarLavabo(String lavaboId, Lavabo lavabo) {
        databaseReferenceLavabos.child(lavaboId).setValue(lavabo);
    }

    public LiveData<List<Lavabo>> obtenirLavabosUsuari() {
        MutableLiveData<List<Lavabo>> liveData = new MutableLiveData<>();
        String idFilter = authManager.obtenirUsuari() != null ? authManager.obtenirUsuari().getUid() : null;

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Lavabo> llistaLavabos = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Lavabo lavabo = childSnapshot.getValue(Lavabo.class);
                    if (lavabo != null && lavabo.uidusuari.equals(idFilter)) {
                        lavabo.setKey(childSnapshot.getKey());
                        llistaLavabos.add(lavabo);
                    }
                }
                liveData.setValue(llistaLavabos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        };

        databaseReferenceLavabos.addValueEventListener(listener);

        return liveData;
    }

    public LiveData<List<Lavabo>> obtenirLavabos() {
        MutableLiveData<List<Lavabo>> liveData = new MutableLiveData<>();

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Lavabo> llistaLavabos = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Lavabo lavabo = childSnapshot.getValue(Lavabo.class);
                    if (lavabo != null) {
                        lavabo.setKey(childSnapshot.getKey());
                        llistaLavabos.add(lavabo);
                    }
                }
                liveData.setValue(llistaLavabos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar el error aquí si es necesario
            }
        };

        // Escucha todos los cambios en el nodo donde están almacenadas las fuentes
        databaseReferenceLavabos.addValueEventListener(listener);

        return liveData;
    }

    //CONTENIDORS
    public void afegirContenidor(String latitud, String longitud, List<String> tipusContenidor, byte[] imageData, PenjarImatges.OnImageUploadListener listener) {

        String contenidorId = databaseReferenceContenidors.push().getKey();
        if (contenidorId == null) {
            listener.onUploadFailed("Error al generar la clau del contenidor");
            return;
        }

        Contenidor contenidor = new Contenidor(contenidorId, latitud, longitud, tipusContenidor, null); //Sense imatge de moment


        if (imageData != null) {
            // Si hi ha foto, s'afegeix al storage manager
            penjarImatges.penjarFotos(imageData, contenidorId, new PenjarImatges.OnImageUploadListener() {
                @Override
                public void onUploadSuccess(String imagenUri) {
                    contenidor.setImageUrl(imagenUri);
                    guardarContenidor(contenidor);
                    listener.onUploadSuccess(imagenUri);
                }

                @Override
                public void onUploadFailed(String errorMessage) {
                    listener.onUploadFailed(errorMessage);
                }
            });
        } else {
            // Si no hi ha imatge
            guardarContenidor(contenidor);
            listener.onUploadSuccess(null);
        }
    }

    private void guardarContenidor(Contenidor contenidor) {
        databaseReferenceContenidors.child(contenidor.getKey()).setValue(contenidor)
                .addOnSuccessListener(aVoid -> {
                    Log.d("RealtimeManager", "Contenidor guardat exitosament");
                })
                .addOnFailureListener(e -> {
                    Log.e("RealtimeManager", "Error al guardar el contenidor: " + e.getMessage());
                });
    }

    void eliminarContenidor(String contenidorId) {
        databaseReferenceContenidors.child(contenidorId).removeValue();
        //TODO: Eliminar tambe la foto
    }

    void actualitzarContenidor(String contenidorId, Contenidor contenidor) {
        databaseReferenceContenidors.child(contenidorId).setValue(contenidor);
    }

    public LiveData<List<Contenidor>> obtenirContenidorsUsuari() {
        MutableLiveData<List<Contenidor>> liveData = new MutableLiveData<>();
        String idFilter = authManager.obtenirUsuari() != null ? authManager.obtenirUsuari().getUid() : null;

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Contenidor> llistaContenidors = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Contenidor contenidor = childSnapshot.getValue(Contenidor.class);
                    if (contenidor != null && contenidor.uidusuari.equals(idFilter)) {
                        contenidor.setKey(childSnapshot.getKey());
                        llistaContenidors.add(contenidor);
                    }
                }
                liveData.setValue(llistaContenidors);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        };

        databaseReferenceContenidors.addValueEventListener(listener);

        return liveData;
    }

    public LiveData<List<Contenidor>> obtenirContenidors() {
        MutableLiveData<List<Contenidor>> liveData = new MutableLiveData<>();

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Contenidor> llistaContenidors = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Contenidor contenidor = childSnapshot.getValue(Contenidor.class);
                    if (contenidor != null) {
                        contenidor.setKey(childSnapshot.getKey());
                        llistaContenidors.add(contenidor);
                    }
                }
                liveData.setValue(llistaContenidors);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar el error aquí si es necesario
            }
        };

        // Escucha todos los cambios en el nodo donde están almacenadas las fuentes
        databaseReferenceContenidors.addValueEventListener(listener);

        return liveData;
    }

    //PICNIC

    public void afegirPicnic(String latitud, String longitud, String bancsIOTaules, List<Integer> queHiHa, byte[] imageData, PenjarImatges.OnImageUploadListener listener) {

        String picnicId = databaseReferencePicnic.push().getKey();
        if (picnicId == null) {
            listener.onUploadFailed("Error al generar la clau del picnic");
            return;
        }

        Picnic picnic = new Picnic(picnicId, latitud, longitud, bancsIOTaules, queHiHa, null); //Sense imatge de moment

        if (imageData != null) {
            // Si hi ha foto, s'afegeix al storage manager
            penjarImatges.penjarFotos(imageData, picnicId, new PenjarImatges.OnImageUploadListener() {
                @Override
                public void onUploadSuccess(String imagenUri) {
                    picnic.setImageUrl(imagenUri);
                    guardarPicnic(picnic);
                    listener.onUploadSuccess(imagenUri);
                }

                @Override
                public void onUploadFailed(String errorMessage) {
                    listener.onUploadFailed(errorMessage);
                }
            });
        } else {
            // Si no hi ha imatge
            guardarPicnic(picnic);
            listener.onUploadSuccess(null);
        }
    }

    private void guardarPicnic(Picnic picnic) {
        databaseReferencePicnic.child(picnic.getKey()).setValue(picnic)
                .addOnSuccessListener(aVoid -> {
                    Log.d("RealtimeManager", "Picnic guardat exitosament");

                })
                .addOnFailureListener(e -> {
                    Log.e("RealtimeManager", "Error al guardar el picnic: " + e.getMessage());
                });
    }

    void eliminarPicnic(String picnicId) {
        databaseReferencePicnic.child(picnicId).removeValue();
        //TODO: Eliminar tambe la foto
    }

    void actualitzarPicnic(String picnicId, Picnic picnic) {
        databaseReferencePicnic.child(picnicId).setValue(picnic);
    }

    public LiveData<List<Picnic>> obtenirPicnicsUsuari() {
        MutableLiveData<List<Picnic>> liveData = new MutableLiveData<>();
        String idFilter = authManager.obtenirUsuari() != null ? authManager.obtenirUsuari().getUid() : null;

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Picnic> llistaPicnics = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Picnic picnic = childSnapshot.getValue(Picnic.class);
                    if (picnic != null && picnic.uidusuari.equals(idFilter)) {
                        picnic.setKey(childSnapshot.getKey());
                        llistaPicnics.add(picnic);
                    }
                }
                liveData.setValue(llistaPicnics);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        };

        databaseReferencePicnic.addValueEventListener(listener);

        return liveData;
    }

    public LiveData<List<Picnic>> obtenirPicnics() {
        MutableLiveData<List<Picnic>> liveData = new MutableLiveData<>();

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Picnic> llistaPicnics = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Picnic picnic = childSnapshot.getValue(Picnic.class);
                    if (picnic != null) {
                        picnic.setKey(childSnapshot.getKey());
                        llistaPicnics.add(picnic);
                    }
                }
                liveData.setValue(llistaPicnics);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar el error aquí si es necesario
            }
        };

        // Escucha todos los cambios en el nodo donde están almacenadas las fuentes
        databaseReferencePicnic.addValueEventListener(listener);

        return liveData;
    }

}