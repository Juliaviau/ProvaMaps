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

import com.example.provamaps.databinding.FragmentAfegirPicnicBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AfegirPicnicFragment extends Fragment {

    Uri uriImatge;
    ImageView imatgePicnic;

    private FragmentAfegirPicnicBinding binding;
    private float latitud, longitud;
    private Geocoder geocoder;
    private List<Address> adreca;

    private int seleccioBancsIOTaules = View.NO_ID;
    //private int seleccioQueHiHa = View.NO_ID;
    private List<Integer> seleccioQueHiHa = new ArrayList<>();
    private boolean hiHaFoto = false;
    private RealtimeManager realtimeManager;

    ActivityResultLauncher<Uri> contract = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
        imatgePicnic.setImageURI(null);
        imatgePicnic.setImageURI(uriImatge);
        hiHaFoto = true;
    });

    private Uri createImageUri(Context context) {
        //File image = new File(context.getFilesDir(), "camera_fotos.png");
        String imatgeNom = "camera_fotos_" + System.currentTimeMillis() + ".jpg";
        File imatge = new File(context.getFilesDir(), imatgeNom);
        return FileProvider.getUriForFile(context, "com.example.provamaps.FileProvider", imatge);
    }

    public AfegirPicnicFragment() {
        // Required empty public constructor
    }


    public static AfegirPicnicFragment newInstance(String param1, String param2) {
        AfegirPicnicFragment fragment = new AfegirPicnicFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        realtimeManager = RealtimeManager.getInstance();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAfegirPicnicBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Button closeButton = view.findViewById(R.id.boto_tancar_afegir_picnic);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tancarFragment();
            }
        });

        Context context = requireContext();
        uriImatge = createImageUri(context);

        imatgePicnic = view.findViewById(R.id.iv_imatgeAfegirPicnic);

        Button botoFerFoto = view.findViewById(R.id.boto_ferFotoPicnic);
        botoFerFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uriImatge != null) {
                    contract.launch(uriImatge);
                } else {
                    Toast.makeText(context, "Error en fer la foto", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Afegir imatge des de la galeria
        Button botoAfegirImatge = view.findViewById(R.id.boto_afegirImatgePicnic);
        botoAfegirImatge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)             {
                escullImatgeGaleria.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        //..................................................................

        Button botoAfegirPicnic = view.findViewById(R.id.boto_afegir_afegirPicnic);
        botoAfegirPicnic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Mirar que hi hagi els togglebuttons seleccionats
                String bancsIOTaules = null;

                if (seleccioBancsIOTaules != View.NO_ID) {
                    MaterialButton selectedButton = binding.getRoot().findViewById(seleccioBancsIOTaules);
                    bancsIOTaules = selectedButton.getText().toString();
                    //selectedButton.getText()
                } else {
                    //No s'ha seleccionat cap
                    MyUtils.toast(getContext(),"Selecciona si hi ha bancs o taules.");
                    return;
                }

                //Canviar, se'n poden seleccionar uns quants de cop
                if (seleccioQueHiHa.isEmpty()) {
                    MyUtils.toast(getContext(), "Selecciona quins elements hi ha.");
                    return;
                }

                //Que hi hagi coordenades

                double lat = Double.parseDouble(binding.textLatitudPicnic.getText().toString());
                double lon = Double.parseDouble(binding.textLongitudPicnic.getText().toString());

                try {
                    if (lat < -90 || lat > 90) {
                        binding.textLatitudPicnic.setError("Latitud incorrecta");
                        return;
                    }
                    if (lon < -180 || lon > 180) {
                        binding.textLongitudPicnic.setError("Longitud incorrecta");
                        return;
                    }
                } catch (NumberFormatException e) {
                    binding.textLatitudPicnic.setError("Latitud incorrecta");
                    binding.textLongitudPicnic.setError("Longitud incorrecta");
                }


                //Foto opcional?

                if (hiHaFoto && uriImatge != null) {

                    try {
                        // Comprime la imagen antes de subirla
                        byte[] compressedImage = compressImage(getContext(), uriImatge);

                        // Llama al método que sube la imagen comprimida
                        realtimeManager.afegirPicnic(binding.textLatitudPicnic.getText().toString(),
                                binding.textLongitudPicnic.getText().toString(),
                                bancsIOTaules, seleccioQueHiHa, compressedImage, new PenjarImatges.OnImageUploadListener() {
                                    @Override
                                    public void onUploadSuccess(String imageUrl) {
                                        Toast.makeText(getContext(), "Picnic afegit amb èxit!", Toast.LENGTH_SHORT).show();
                                        tancarFragment();
                                        // Aquí puedes realizar alguna acción después de la subida exitosa
                                    }

                                    @Override
                                    public void onUploadFailed(String errorMessage) {
                                        Toast.makeText(getContext(), "Error al afegir el picnic: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error al comprimir la imatge", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    realtimeManager.afegirPicnic(binding.textLatitudPicnic.getText().toString(), binding.textLongitudPicnic.getText().toString(), bancsIOTaules, seleccioQueHiHa, null, new PenjarImatges.OnImageUploadListener() {
                                @Override
                                public void onUploadSuccess(String imageUrl) {
                                    Toast.makeText(getContext(), "Picnic afegit sense foto amb èxit!", Toast.LENGTH_SHORT).show();
                                    tancarFragment();
                                    //Tornar al mapa
                                }

                                @Override
                                public void onUploadFailed(String errorMessage) {
                                    Toast.makeText(getContext(), "Error al afegir el picnic amb foto: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                }
            }
        });

        //Cada vegada que es canvia de boto canvia la variable que diu quin esta seleccionat
        binding.tggbBanctaula.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup grup, int idSeleccionat, boolean estaSeleccionat) {
                if (estaSeleccionat) {
                    seleccioBancsIOTaules = idSeleccionat;
                } else if (seleccioBancsIOTaules == idSeleccionat) {
                    //No esta seleccionat, posar error
                    seleccioBancsIOTaules = View.NO_ID;
                    Snackbar.make(binding.getRoot(), "Error: S'ha de seleccionar una opció", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        //Canviar, se'n poden seleccionar uns quants de cop
        binding.tggbPicnicConte.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            int position = group.indexOfChild(group.findViewById(checkedId));
            if (isChecked) {
                seleccioQueHiHa.add(position);
            } else {
                seleccioQueHiHa.remove((Integer) position);
            }
            if (seleccioQueHiHa.isEmpty()) {
                Snackbar.make(binding.getRoot(), "Error: S'ha de seleccionar una opció", Snackbar.LENGTH_LONG).show();
            }
        });


        return view;
    }


    ActivityResultLauncher<PickVisualMediaRequest> escullImatgeGaleria =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    Log.d("TriaImatge", "URI de l'imatge seleccionada: " + uri);
                    uriImatge = uri;
                    imatgePicnic.setImageURI(uri);
                    hiHaFoto = true;
                } else {
                    Log.d("TriaImatge", "No s'ha seleccionat cap imatge");
                }
            });

    private byte[] compressImage(Context context, Uri imageUri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);

        int maxWidth = 1024;
        int maxHeight = 1024;
        bitmap = resizeBitmap(bitmap, maxWidth, maxHeight);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);

        return baos.toByteArray();
    }

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

        if (getArguments() != null) {
            latitud = getArguments().getFloat("latitud", 0.0f);
            longitud = getArguments().getFloat("longitud", 0.0f);

            binding.textLatitudPicnic.setText(latitud+"");
            binding.textLongitudPicnic.setText(longitud+"");

            obtenirAdreca();

        }

        binding.textLongitudPicnic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                //Comprovar que sigui correcte
                if(binding.textLongitudPicnic.getText().length() >= 0 && binding.textLongitudPicnic.getText().length() <= 180) {
                    obtenirAdreca();
                } else {
                    binding.textLongitudPicnic.setError("Longitud incorrecte");
                }
            }
        });

        binding.textLatitudPicnic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.textLatitudPicnic.getText().length() >= 0 && binding.textLatitudPicnic.getText().length() <= 90){     obtenirAdreca();
                } else {
                    binding.textLatitudPicnic.setError("Latitud incorrecte");
                }
            }
        });

    }

    private void obtenirAdreca () {

        try {
            double lat = Double.parseDouble(binding.textLatitudPicnic.getText().toString());
            double lon = Double.parseDouble(binding.textLongitudPicnic.getText().toString());

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

                binding.textAdrecaPicnic.setText(carrer + ", " + numero + ", " + poblacio + ", " + comarca + ", " + provincia + ", " + pais);

                // Actualiza la imagen del mapa
                String url = "https://static-maps.yandex.ru/1.x/?lang=en-US&ll=" + lon + "," + lat + "&z=18&size=440,310&l=sat&pt=" + lon + "," + lat + ",pm2rdl";
                Picasso.get().load(url).into(binding.ivMapaLocalitzacioAfegirPicnic);
            } else {
                // Si no se encuentra una dirección, muestra un mensaje o realiza alguna acción
                binding.textAdrecaPicnic.setText("Adreça no trobada");
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            binding.textAdrecaPicnic.setText("Error en obtenir l'adreça");
        }
    }
}