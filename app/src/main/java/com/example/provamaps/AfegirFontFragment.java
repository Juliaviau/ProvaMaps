package com.example.provamaps;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts.*;

import com.example.provamaps.databinding.FragmentAfegirFontBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AfegirFontFragment extends Fragment {

    Uri uriImatge;
    ImageView imatgeFont;
    private FragmentAfegirFontBinding binding;
    private float latitud, longitud;
    private Geocoder geocoder;
    private List<Address> adreca;
    private int seleccioPotable = View.NO_ID;
    private int seleccioEstat = View.NO_ID;

    private boolean hiHaFoto = false;
    private RealtimeManager realtimeManager;

    ActivityResultLauncher<Uri> contract = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
        imatgeFont.setImageURI(null);
        imatgeFont.setImageURI(uriImatge);
        hiHaFoto = true;
    });

    private Uri createImageUri(Context context) {
        //File imatgeNom = new File(context.getFilesDir(), "camera_fotos.png");
        String imatgeNom = "camera_fotos_" + System.currentTimeMillis() + ".jpg";
        File imatge = new File(context.getFilesDir(), imatgeNom);
        return FileProvider.getUriForFile(context, "com.example.provamaps.FileProvider", imatge);
    }

    public AfegirFontFragment() {
        // Required empty public constructor
    }

    public static AfegirFontFragment newInstance(String param1, String param2) {
        return new AfegirFontFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        realtimeManager = RealtimeManager.getInstance();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAfegirFontBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Button closeButton = view.findViewById(R.id.boto_tancar_afegir_font);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tancarFragment();
            }
        });

        Context context = requireContext();
        uriImatge = createImageUri(context);

        imatgeFont = view.findViewById(R.id.iv_imatgeAfegirFont);

        Button botoFerFoto = view.findViewById(R.id.boto_ferFotoFont);
        botoFerFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uriImatge != null) {
                    contract.launch(uriImatge);
                    //Guardar la uriImatge a la base de dades
                } else {
                    Toast.makeText(context, "Error en fer la foto", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Afegir imatge des de la galeria
        Button botoAfegirImatge = view.findViewById(R.id.boto_afegirImatgeFont);
        botoAfegirImatge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)             {
                escullImatgeGaleria.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });


        Button botoAfegirFont = view.findViewById(R.id.boto_afegir_afegirFont);
        botoAfegirFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                //Mirar que hi hagi els togglebuttons seleccionats
                String potable = null;
                String estat = null;

                if (seleccioPotable != View.NO_ID) {
                    MaterialButton selectedButton = binding.getRoot().findViewById(seleccioPotable);
                    potable = selectedButton.getText().toString();
                    //selectedButton.getText()
                } else {
                    //No s'ha seleccionat cap
                    MyUtils.toast(getContext(),"Selecciona si la font és potable.");
                    return;
                }

                if (seleccioEstat != View.NO_ID) {
                    MaterialButton selectedButton = binding.getRoot().findViewById(seleccioEstat);
                    estat = selectedButton.getText().toString();
                    //selectedButton.getText()
                } else {
                    //No s'ha seleccionat cap
                    MyUtils.toast(getContext(),"Selecciona si la font esta en servei.");
                    return;
                }

                //Que hi hagi coordenades
                /*String latitud = binding.textLatitudFont.getText().toString();
                String longitud = binding.textLongitudFont.getText().toString();

                if (latitud.isEmpty() || longitud.isEmpty()) {
                    Toast.makeText(getContext(), "Cal introduir coordenades vàlides.", Toast.LENGTH_SHORT).show();
                    return;
                }*/

                double lat = Double.parseDouble(binding.textLatitudFont.getText().toString());
                double lon = Double.parseDouble(binding.textLongitudFont.getText().toString());

                try {
                    if (lat < -90 || lat > 90) {
                        binding.textLatitudFont.setError("Latitud incorrecta");
                        return;
                    }
                    if (lon < -180 || lon > 180) {
                        binding.textLongitudFont.setError("Longitud incorrecta");
                        return;
                    }
                } catch (NumberFormatException e) {
                    binding.textLatitudFont.setError("Latitud incorrecta");
                    binding.textLongitudFont.setError("Longitud incorrecta");
                }


                //Foto opcional?

                if (hiHaFoto && uriImatge != null) {

                    try {
                        // Comprime la imagen antes de subirla
                        byte[] compressedImage = compressImage(getContext(), uriImatge);

                        // Llama al método que sube la imagen comprimida
                        realtimeManager.afegirFont(binding.textLatitudFont.getText().toString(),
                                binding.textLongitudFont.getText().toString(),
                                potable, estat, compressedImage, new PenjarImatges.OnImageUploadListener() {
                                    @Override
                                    public void onUploadSuccess(String imageUrl) {
                                        Toast.makeText(getContext(), "Font afegida amb èxit!", Toast.LENGTH_SHORT).show();
                                        // Aquí puedes realizar alguna acción después de la subida exitosa
                                    }

                                    @Override
                                    public void onUploadFailed(String errorMessage) {
                                        Toast.makeText(getContext(), "Error al afegir la font: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error al comprimir la imatge", Toast.LENGTH_SHORT).show();
                    }












                        // Subir la imagen y luego crear la fuente
                        /*realtimeManager.afegirFont(binding.textLatitudFont.getText().toString(), binding.textLongitudFont.getText().toString(), potable, estat, uriImatge, new PenjarImatges.OnImageUploadListener() {
                            @Override
                            public void onUploadSuccess(String imageUrl) {
                                Toast.makeText(getContext(), "Font afegida amb èxit!", Toast.LENGTH_SHORT).show();
                                //Tornar al mapa
                            }

                            @Override
                            public void onUploadFailed(String errorMessage) {
                                Toast.makeText(getContext(), "Error al afegir la font: " + errorMessage, Toast.LENGTH_SHORT).show();

                            }
                        });*/

                } else {
                    // Crear la Font sin foto
                    realtimeManager.afegirFont(binding.textLatitudFont.getText().toString(), binding.textLongitudFont.getText().toString(), potable, estat, null, new PenjarImatges.OnImageUploadListener() {
                                @Override
                                public void onUploadSuccess(String imageUrl) {
                                    Toast.makeText(getContext(), "Font afegida sense foto amb èxit!", Toast.LENGTH_SHORT).show();
                                    //Tornar al mapa
                                }

                                @Override
                                public void onUploadFailed(String errorMessage) {
                                    Toast.makeText(getContext(), "Error al afegir la font amb foto: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                }


                /*if (hiHaFoto) {
                    //Afegir la foto
                    // Aquí deberías subir la foto a Firebase Storage antes de crear el objeto Font
                    subirImagenYCrearFont(latitud, longitud, potable, estat);
                } else {
                    // Crear la Font sin foto
                    crearFont(latitud, longitud, potable, estat, null);
                }*/

                //Obtenir valors i crear l'objecte font

                //Errors als ttoggle buttons
                /*if (binding.tggbFontPotable.getCheckedButtonId() == -1) {
                    binding.tggbFontPotable.error
                }*/
            }
        });

        //Cada vegada que es canvia de boto canvia la variable que diu quin esta seleccionat
        binding.tggbFontPotable.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup grup, int idSeleccionat, boolean estaSeleccionat) {
                if (estaSeleccionat) {
                    seleccioPotable = idSeleccionat;
                } else if (seleccioPotable == idSeleccionat) {
                    //No esta seleccionat, posar error
                    seleccioPotable = View.NO_ID;
                    Snackbar.make(binding.getRoot(), "Error: S'ha de seleccionar una opció", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        binding.tggbFontEstat.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup grup, int idSeleccionat, boolean estaSeleccionat) {
                if (estaSeleccionat) {
                    seleccioEstat = idSeleccionat;
                } else if (seleccioEstat == idSeleccionat) {
                    seleccioEstat = View.NO_ID;
                    Snackbar.make(binding.getRoot(), "Error: S'ha de seleccionar una opció", Snackbar.LENGTH_LONG).show();
                }
            }
        });


        return view;
    }

    //Escull una imatge de la galeria i la posa al imageview de la font
    ActivityResultLauncher<PickVisualMediaRequest> escullImatgeGaleria =
            registerForActivityResult(new PickVisualMedia(), uri -> {
                if (uri != null) {
                    Log.d("TriaImatge", "URI de l'imatge seleccionada: " + uri);
                    uriImatge = uri;
                    imatgeFont.setImageURI(uri);
                    hiHaFoto = true;
                    //Guardar la uri a la base de dades
                } else {
                    Log.d("TriaImatge", "No s'ha seleccionat cap imatge");
                }
            });


    private byte[] compressImage(Context context, Uri imageUri) throws IOException {
        // Convierte el URI de la imagen a un Bitmap
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);

        // Redimensiona la imagen si es necesario (opcional)
        int maxWidth = 1024;
        int maxHeight = 1024;
        bitmap = resizeBitmap(bitmap, maxWidth, maxHeight);

        // Comprime el Bitmap en un ByteArrayOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos); // Ajusta la calidad (50 es un buen punto de partida)

        return baos.toByteArray();
    }

    // Método para redimensionar el Bitmap (opcional)
    private Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float ratioBitmap = (float) width / (float) height;
        float ratioMax = (float) maxWidth / (float) maxHeight;

        int finalWidth = maxWidth;
        int finalHeight = maxHeight;
        if (ratioMax > ratioBitmap) {
            finalWidth = (int) ((float)maxHeight * ratioBitmap);
        } else {
            finalHeight = (int) ((float)maxWidth / ratioBitmap);
        }
        return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true);
    }


    private void tancarFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();
    }

    @Override
    public void onResume() {
        super.onResume();
        //A l'entrar al fragment
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.amagarBottomMenu();
        }
    }

    @Override
    public void onPause() {
        //Al sortir del fragment
        super.onPause();
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.mostrarBottomMenu();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        //Comprova si se li han passat les coordenades des del mainactivity
        if (getArguments() != null) {
            latitud = getArguments().getFloat("latitud", 0.0f);
            longitud = getArguments().getFloat("longitud", 0.0f);

            binding.textLatitudFont.setText(latitud+"");
            binding.textLongitudFont.setText(longitud+"");

            obtenirAdreca();
        }

        binding.textLongitudFont.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                //Comprovar que sigui correcte
                if(binding.textLongitudFont.getText().length() >= 0 && binding.textLongitudFont.getText().length() <= 180) {
                    obtenirAdreca();
                } else {
                    binding.textLongitudFont.setError("Longitud incorrecte");
                }
            }
        });

        binding.textLatitudFont.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.textLatitudFont.getText().length() >= 0 && binding.textLatitudFont.getText().length() <= 90){     obtenirAdreca();
                } else {
                    binding.textLatitudFont.setError("Latitud incorrecte");
                }
            }
        });
    }

    private void obtenirAdreca () {

        try {
            double lat = Double.parseDouble(binding.textLatitudFont.getText().toString());
            double lon = Double.parseDouble(binding.textLongitudFont.getText().toString());

            // Obtenemos la lista de direcciones con el geocoder
            adreca = geocoder.getFromLocation(lat, lon, 1);

            // Verifica si la lista tiene algún elemento antes de acceder
            if (adreca != null && !adreca.isEmpty()) {
                Address address = adreca.get(0);

                String poblacio = address.getLocality();
                String provincia = address.getAdminArea();
                String pais = address.getCountryName();
                String numero = address.getFeatureName();
                String comarca = address.getSubAdminArea();
                String carrer = address.getThoroughfare();

                binding.textAdrecaFont.setText(carrer + ", " + numero + ", " + poblacio + ", " + comarca + ", " + provincia + ", " + pais);

                // Actualiza la imagen del mapa
                String url = "https://static-maps.yandex.ru/1.x/?lang=en-US&ll=" + lon + "," + lat + "&z=18&size=440,310&l=sat&pt=" + lon + "," + lat + ",pm2rdl";
                Picasso.get().load(url).into(binding.ivMapaLocalitzacioAfegirFont);
            } else {
                // Si no se encuentra una dirección, muestra un mensaje o realiza alguna acción
                binding.textAdrecaFont.setText("Adreça no trobada");
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            binding.textAdrecaFont.setText("Error en obtenir l'adreça");
        }




/*
        double lat = Double.parseDouble(binding.textLatitudFont.getText().toString());
        double lon = Double.parseDouble(binding.textLongitudFont.getText().toString());

        try {
            adreca = geocoder.getFromLocation(lat, lon, 1); // 1 representa la cantidad de resultados a obtener
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String poblacio = adreca.get(0).getLocality();
        String provincia = adreca.get(0).getAdminArea();
        String pais = adreca.get(0).getCountryName();
        String numero = adreca.get(0).getFeatureName();
        String comarca = adreca.get(0).getSubAdminArea();
        String carrer = adreca.get(0).getThoroughfare();

        binding.textAdrecaFont.setText(carrer + ", " + numero + ", " + poblacio + ", " + comarca + ", " + provincia + ", " + pais);

        //Foto del mapa
        //String url = "https://static-maps.yandex.ru/1.x/?lang=en-US&ll=" + longitud + "," + latitud + "&z=" + zoom + "&size=" + width + "," + height + "&l=sat&pt=" + longitud + "," + latitud + ",pm2rdl";
        //String url = "https://static-maps.yandex.ru/1.x/?lang=es-ES&ll=" + longitud + "," + latitud + "&z=" + zoom + "&size=" + width + "," + height + "&l=sat&pt=" + longitud + "," + latitud + ",pm2rdl";
        // String url = "https://staticmap.openstreetmap.de/staticmap.php?center=" + latitud + "," + longitud + "&zoom=" + zoom + "&size=" + width + "x" + height + "&maptype=mapnik&markers=" + latitud + "," + longitud + ",red-pushpin";
        // String url = "https://staticmap.openstreetmap.de/staticmap.php?center=" + latitud + "," + longitud + "&zoom=" + zoom + "&size=" + width + "x" + height + "&maptype=mapnik&markers=" + latitud + "," + longitud + ",red-pushpin";

        //Mapa normal
        //String url = "https://static-maps.yandex.ru/1.x/?lang=en-US&ll=" + longitud + "," + latitud + "&z=" + 16 + "&size=" + width + "," + height + "&l=map&pt=" + longitud + "," + latitud + ",pm2rdl";

        //Mapa satelit
        String url = "https://static-maps.yandex.ru/1.x/?lang=en-US&ll=" + lon + "," + lat + "&z=" + 18 + "&size=" + 440 + "," + 310 + "&l=sat&pt=" + lon + "," + lat + ",pm2rdl";

        Picasso.get().load(url).into(binding.ivMapaLocalitzacioAfegirFont);*/
    }
}