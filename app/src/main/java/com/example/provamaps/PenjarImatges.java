package com.example.provamaps;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PenjarImatges {

    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    public void penjarFotos(byte[] imageData, String fontId, OnImageUploadListener listener) {

        if (imageData == null) {
            listener.onUploadFailed("No hi ha imatge");
            return;
        }

        //Guardar la imatge a imatges/idImatge.jpg
        StorageReference imageRef = storageReference.child("imatges/" + fontId + ".jpg");

        // Penjar imatge
        imageRef.putBytes(imageData)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        listener.onUploadSuccess(uri.toString()); // Retorna la URL per descarregar
                    }).addOnFailureListener(e -> listener.onUploadFailed(e.getMessage()));
                })
                .addOnFailureListener(e -> listener.onUploadFailed(e.getMessage()));
    }

    public interface OnImageUploadListener {
        void onUploadSuccess(String imageUrl);
        void onUploadFailed(String errorMessage);
    }

}
