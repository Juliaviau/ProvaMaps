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

import com.example.provamaps.databinding.FragmentAfegirContenidorBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AfegirContenidorFragment extends Fragment {
    Uri uriImatge;
    ImageView imatgeContenidor;
    private FragmentAfegirContenidorBinding binding;
    private float latitud, longitud;
    private Geocoder geocoder;
    private List<Address> adreca;
    //private int selecioTipusContenidor = View.NO_ID;
    private List<String> selecioTipusContenidor = new ArrayList<>();

    private boolean hiHaFoto = false;
    private RealtimeManager realtimeManager;
    ActivityResultLauncher<Uri> contract = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
        imatgeContenidor.setImageURI(null);
        imatgeContenidor.setImageURI(uriImatge);
        hiHaFoto = true;
    });

    private Uri createImageUri(Context context) {
        //File image = new File(context.getFilesDir(), "camera_fotos.png");
        String imatgeNom = "camera_fotos_" + System.currentTimeMillis() + ".jpg";
        File imatge = new File(context.getFilesDir(), imatgeNom);
        return FileProvider.getUriForFile(context, "com.example.provamaps.FileProvider", imatge);
    }

    public AfegirContenidorFragment() {
        // Required empty public constructor
    }

    public static AfegirContenidorFragment newInstance(String param1, String param2) {
        AfegirContenidorFragment fragment = new AfegirContenidorFragment();
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

        binding = FragmentAfegirContenidorBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Button closeButton = view.findViewById(R.id.boto_tancar_afegir_contenidor);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tancarFragment();
            }
        });

        Context context = requireContext();
        uriImatge = createImageUri(context);

        imatgeContenidor = view.findViewById(R.id.iv_imatgeAfegirContenidor);

        Button botoFerFoto = view.findViewById(R.id.boto_ferFotoContenidor);
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

        Button botoAfegirImatge = view.findViewById(R.id.boto_afegirImatgeContenidor);
        botoAfegirImatge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)             {
                escullImatgeGaleria.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });


        Button botoAfegirContenidor = view.findViewById(R.id.boto_afegir_afegirContenidor);
        botoAfegirContenidor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                //Mirar que hi hagi els togglebuttons seleccionats
                if (selecioTipusContenidor.isEmpty()) {
                    MyUtils.toast(getContext(), "Selecciona de quin tipus és el contenidor.");
                    return;
                }

                //Que hi hagi coordenades
                double lat = Double.parseDouble(binding.textLatitudContenidor.getText().toString());
                double lon = Double.parseDouble(binding.textLongitudContenidor.getText().toString());

                try {
                    if (lat < -90 || lat > 90) {
                        binding.textLatitudContenidor.setError("Latitud incorrecta");
                        return;
                    }
                    if (lon < -180 || lon > 180) {
                        binding.textLongitudContenidor.setError("Longitud incorrecta");
                        return;
                    }
                } catch (NumberFormatException e) {
                    binding.textLatitudContenidor.setError("Latitud incorrecta");
                    binding.textLongitudContenidor.setError("Longitud incorrecta");
                }


                //Foto opcional?

                if (hiHaFoto && uriImatge != null) {

                    try {
                        // Comprime la imagen antes de subirla
                        byte[] compressedImage = compressImage(getContext(), uriImatge);

                        // Llama al método que sube la imagen comprimida
                        realtimeManager.afegirContenidor(binding.textLatitudContenidor.getText().toString(),
                                binding.textLongitudContenidor.getText().toString(),
                                selecioTipusContenidor, compressedImage, new PenjarImatges.OnImageUploadListener() {
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
                } else {
                    // Crear la Font sin foto
                    realtimeManager.afegirContenidor(binding.textLatitudContenidor.getText().toString(), binding.textLongitudContenidor.getText().toString(), selecioTipusContenidor, null, new PenjarImatges.OnImageUploadListener() {
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
            }
        });


        ChipGroup chipGroup = binding.chipGroupTipusContenidor;


// Configurar el listener para detectar cambios en la selección de los chips
        chipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                selecioTipusContenidor.clear();  // Limpiamos la selección anterior

                // Iteramos sobre los chips seleccionados
                for (int id : checkedIds) {
                    Chip chip = group.findViewById(id);
                    if (chip != null) {
                        selecioTipusContenidor.add(chip.getText().toString());
                    }
                }

                // Mostrar el mensaje Toast con la selección actual
                if (!selecioTipusContenidor.isEmpty()) {
                    MyUtils.toast(context, "Seleccionado: " + selecioTipusContenidor.toString());
                } else {
                    MyUtils.toast(context, "No hay selección");
                }
            }
        });





        /*ChipGroup chipGroup = binding.chipGroupTipusContenidor;
        //Cada vegada que es canvia de boto canvia la variable que diu quin esta seleccionat
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            selecioTipusContenidor.clear();
            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                View chip = chipGroup.getChildAt(i);
                if (chip instanceof Chip) {
                    Chip chipItem = (Chip) chip;
                    if (chipItem.isChecked()) {
                        selecioTipusContenidor.add(chipItem.getText().toString());
                    }
                }
            }
            MyUtils.toast(context,"seleccinat");
        });*/
        //todo son check box no togglebuttons
        /*binding..addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup grup, int idSeleccionat, boolean estaSeleccionat) {
                if (estaSeleccionat) {
                    selecioTipusContenidor = idSeleccionat;
                } else if (selecioTipusContenidor == idSeleccionat) {
                    //No esta seleccionat, posar error
                    selecioTipusContenidor = View.NO_ID;
                    Snackbar.make(binding.getRoot(), "Error: S'ha de seleccionar una opció", Snackbar.LENGTH_LONG).show();
                }
            }
        });*/

        return view;
    }

    ActivityResultLauncher<PickVisualMediaRequest> escullImatgeGaleria =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    Log.d("TriaImatge", "URI de l'imatge seleccionada: " + uri);
                    uriImatge = uri;
                    imatgeContenidor.setImageURI(uri);
                    hiHaFoto = true;
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

        if (getArguments() != null) {
            latitud = getArguments().getFloat("latitud", 0.0f);
            longitud = getArguments().getFloat("longitud", 0.0f);

            binding.textLatitudContenidor.setText(latitud+"");
            binding.textLongitudContenidor.setText(longitud+"");

            obtenirAdreca();
        }

        binding.textLongitudContenidor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(binding.textLongitudContenidor.getText().length() >= 0 && binding.textLongitudContenidor.getText().length() <= 180) {
                    obtenirAdreca();
                } else {
                    binding.textLongitudContenidor.setError("Longitud incorrecte");
                }
            }
        });

        binding.textLatitudContenidor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.textLatitudContenidor.getText().length() >= 0 && binding.textLatitudContenidor.getText().length() <= 90){     obtenirAdreca();
                } else {
                    binding.textLatitudContenidor.setError("Latitud incorrecte");
                }
            }
        });

    }

    private void obtenirAdreca () {

        try {
            double lat = Double.parseDouble(binding.textLatitudContenidor.getText().toString());
            double lon = Double.parseDouble(binding.textLongitudContenidor.getText().toString());

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

                binding.textAdrecaContenidor.setText(carrer + ", " + numero + ", " + poblacio + ", " + comarca + ", " + provincia + ", " + pais);

                // Actualiza la imagen del mapa
                String url = "https://static-maps.yandex.ru/1.x/?lang=en-US&ll=" + lon + "," + lat + "&z=18&size=440,310&l=sat&pt=" + lon + "," + lat + ",pm2rdl";
                Picasso.get().load(url).into(binding.ivMapaLocalitzacioAfegirContenidor);
            } else {
                // Si no se encuentra una dirección, muestra un mensaje o realiza alguna acción
                binding.textAdrecaContenidor.setText("Adreça no trobada");
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            binding.textAdrecaContenidor.setText("Error en obtenir l'adreça");
        }
    }
}