package com.example.provamaps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;

public class TractarImatges {

    // Método para obtener una imagen comprimida
    public static byte[] compressImage(Uri imageUri, Context context) {
        try {
            // Cargar la imagen en un Bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageUri));

            // Redimensionar la imagen
            int newWidth = 800; // Cambia esto según la resolución que desees
            int newHeight = (int) (bitmap.getHeight() * (800.0 / bitmap.getWidth()));
            bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

            // Comprimir la imagen
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream); // Cambia el 75 a un valor más bajo para más compresión (menor calidad)

            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Método para obtener un Bitmap comprimido
    public static Bitmap compressImageToBitmap(Uri imageUri, Context context) {
        try {
            // Cargar la imagen en un Bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageUri));

            // Redimensionar la imagen
            int newWidth = 800; // Cambia esto según la resolución que desees
            int newHeight = (int) (bitmap.getHeight() * (800.0 / bitmap.getWidth()));
            bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

            // Comprimir la imagen
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream); // Ajusta el valor según necesites

            byte[] compressedImage = outputStream.toByteArray();

            // Convertir el byte array de nuevo a un Bitmap
            return BitmapFactory.decodeByteArray(compressedImage, 0, compressedImage.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
