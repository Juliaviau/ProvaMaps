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

import com.example.provamaps.databinding.FragmentAfegirLavaboBinding;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AfegirLavaboFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AfegirLavaboFragment extends Fragment {

    Uri uriImatge;
    ImageView imatgeLavabo;
    private FragmentAfegirLavaboBinding binding;
    private float latitud, longitud;
    private Geocoder geocoder;
    private List<Address> adreca;
    //private int seleccioTipusLavabo = View.NO_ID;
    private List<Integer> seleccioTipusLavabo = new ArrayList<>();

    private int seleccioDisposaPaper = View.NO_ID;
    private int seleccioDisposaPica = View.NO_ID;
    private boolean hiHaFoto = false;
    private RealtimeManager realtimeManager;

    ActivityResultLauncher<Uri> contract = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
        imatgeLavabo.setImageURI(null);
        imatgeLavabo.setImageURI(uriImatge);
        hiHaFoto = true;
    });

    private Uri createImageUri(Context context) {
        //File image = new File(context.getFilesDir(), "camera_fotos.png");
        String imatgeNom = "camera_fotos_" + System.currentTimeMillis() + ".jpg";
        File imatge = new File(context.getFilesDir(), imatgeNom);
        return FileProvider.getUriForFile(context, "com.example.provamaps.FileProvider", imatge);
    }

    public AfegirLavaboFragment() {
        // Required empty public constructor
    }

    public static AfegirLavaboFragment newInstance(String param1, String param2) {
        AfegirLavaboFragment fragment = new AfegirLavaboFragment();

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

        binding = FragmentAfegirLavaboBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Button closeButton = view.findViewById(R.id.boto_tancar_afegir_lavabo);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tancarFragment();
            }
        });

        Context context = requireContext();
        uriImatge = createImageUri(context);

        imatgeLavabo = view.findViewById(R.id.iv_imatgeAfegirLavabo);

        Button botoFerFoto = view.findViewById(R.id.boto_ferFotoLavabo);
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

        Button botoAfegirImatge = view.findViewById(R.id.boto_afegirImatgeLavabo);
        botoAfegirImatge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)             {
                escullImatgeGaleria.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        //..............................................................................

        Button botoAfegirLavabo = view.findViewById(R.id.boto_afegir_afegirLavabo);
        botoAfegirLavabo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                //Mirar que hi hagi els togglebuttons seleccionats
                String disposaPaper = null;
                String disposaPica = null;


                //mes d'un tipus
                if (seleccioTipusLavabo.isEmpty()) {
                    MyUtils.toast(getContext(), "Selecciona de quin tipus és el lavabo.");
                    return;
                }
                /*for (int position : seleccioTipusLavabo) {
                    Log.d("Seleccionats", "Botó seleccionat en la posició: " + position);
                }*/


                if (seleccioDisposaPaper != View.NO_ID) {
                    MaterialButton selectedButton = binding.getRoot().findViewById(seleccioDisposaPaper);
                    disposaPaper = selectedButton.getText().toString();
                } else {
                    //No s'ha seleccionat cap
                    MyUtils.toast(getContext(),"Selecciona si el lavabo disposa de paper.");
                    return;
                }

                if (seleccioDisposaPica != View.NO_ID) {
                    MaterialButton selectedButton = binding.getRoot().findViewById(seleccioDisposaPica);
                    disposaPica = selectedButton.getText().toString();
                } else {
                    //No s'ha seleccionat cap
                    MyUtils.toast(getContext(),"Selecciona si el lavabo disposa de pica.");
                    return;
                }

                //Que hi hagi coordenades

                double lat = Double.parseDouble(binding.textLatitudLavabo.getText().toString());
                double lon = Double.parseDouble(binding.textLongitudLavabo.getText().toString());

                try {
                    if (lat < -90 || lat > 90) {
                        binding.textLatitudLavabo.setError("Latitud incorrecta");
                        return;
                    }
                    if (lon < -180 || lon > 180) {
                        binding.textLongitudLavabo.setError("Longitud incorrecta");
                        return;
                    }
                } catch (NumberFormatException e) {
                    binding.textLatitudLavabo.setError("Latitud incorrecta");
                    binding.textLongitudLavabo.setError("Longitud incorrecta");
                }


                //Foto opcional?

                if (hiHaFoto && uriImatge != null) {

                    try {
                        byte[] compressedImage = compressImage(getContext(), uriImatge);

                        realtimeManager.afegirLavabo(binding.textLatitudLavabo.getText().toString(),
                                binding.textLongitudLavabo.getText().toString(),
                                seleccioTipusLavabo, disposaPaper, disposaPica, compressedImage, new PenjarImatges.OnImageUploadListener() {
                                    @Override
                                    public void onUploadSuccess(String imageUrl) {
                                        Toast.makeText(getContext(), "Lavabo afegit amb èxit!", Toast.LENGTH_SHORT).show();
                                        tancarFragment();
                                    }

                                    @Override
                                    public void onUploadFailed(String errorMessage) {
                                        Toast.makeText(getContext(), "Error al afegir el lavabo: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error al comprimir la imatge", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Crear la Font sense foto
                    realtimeManager.afegirLavabo(binding.textLatitudLavabo.getText().toString(), binding.textLongitudLavabo.getText().toString(), seleccioTipusLavabo, disposaPaper, disposaPica, null, new PenjarImatges.OnImageUploadListener() {
                                @Override
                                public void onUploadSuccess(String imageUrl) {
                                    Toast.makeText(getContext(), "Lavabo afegit sense foto amb èxit!", Toast.LENGTH_SHORT).show();
                                    tancarFragment();
                                    //Tornar al mapa
                                }

                                @Override
                                public void onUploadFailed(String errorMessage) {
                                    Toast.makeText(getContext(), "Error al afegir el lavabo sense foto: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                }
            }
        });

        //Cada vegada que es canvia de boto canvia la variable que diu quin esta seleccionat
        binding.tggbLavaboTipus.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            int position = group.indexOfChild(group.findViewById(checkedId));
            if (isChecked) {
                seleccioTipusLavabo.add(position);
            } else {
                seleccioTipusLavabo.remove((Integer) position);
            }
            if (seleccioTipusLavabo.isEmpty()) {
                Snackbar.make(binding.getRoot(), "Error: S'ha de seleccionar una opció", Snackbar.LENGTH_LONG).show();
            }
        });

        binding.tggbLavaboPaper.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup grup, int idSeleccionat, boolean estaSeleccionat) {
                if (estaSeleccionat) {
                    seleccioDisposaPaper = idSeleccionat;
                } else if (seleccioDisposaPaper == idSeleccionat) {
                    seleccioDisposaPaper = View.NO_ID;
                    Snackbar.make(binding.getRoot(), "Error: S'ha de seleccionar una opció", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        binding.tggbLavaboPica.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup grup, int idSeleccionat, boolean estaSeleccionat) {
                if (estaSeleccionat) {
                    seleccioDisposaPica = idSeleccionat;
                } else if (seleccioDisposaPica == idSeleccionat) {
                    seleccioDisposaPica = View.NO_ID;
                    Snackbar.make(binding.getRoot(), "Error: S'ha de seleccionar una opció", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    ActivityResultLauncher<PickVisualMediaRequest> escullImatgeGaleria =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    Log.d("TriaImatge", "URI de l'imatge seleccionada: " + uri);
                    uriImatge = uri;
                    imatgeLavabo.setImageURI(uri);
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

            binding.textLatitudLavabo.setText(latitud+"");
            binding.textLongitudLavabo.setText(longitud+"");

            obtenirAdreca();
        }

        binding.textLongitudLavabo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                //Comprovar que sigui correcte
                if(binding.textLongitudLavabo.getText().length() >= 0 && binding.textLongitudLavabo.getText().length() <= 180) {
                    obtenirAdreca();
                } else {
                    binding.textLongitudLavabo.setError("Longitud incorrecte");
                }
            }
        });

        binding.textLatitudLavabo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.textLatitudLavabo.getText().length() >= 0 && binding.textLatitudLavabo.getText().length() <= 90){     obtenirAdreca();
                } else {
                    binding.textLatitudLavabo.setError("Latitud incorrecte");
                }
            }
        });
    }

    private void obtenirAdreca () {


        try {
            double lat = Double.parseDouble(binding.textLatitudLavabo.getText().toString());
            double lon = Double.parseDouble(binding.textLongitudLavabo.getText().toString());

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

                binding.textAdrecaLavabo.setText(carrer + ", " + numero + ", " + poblacio + ", " + comarca + ", " + provincia + ", " + pais);

                // Actualiza la imagen del mapa
                String url = "https://static-maps.yandex.ru/1.x/?lang=en-US&ll=" + lon + "," + lat + "&z=18&size=440,310&l=sat&pt=" + lon + "," + lat + ",pm2rdl";
                Picasso.get().load(url).into(binding.ivMapaLocalitzacioAfegirLavabo);
            } else {
                // Si no se encuentra una dirección, muestra un mensaje o realiza alguna acción
                binding.textAdrecaLavabo.setText("Adreça no trobada");
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            binding.textAdrecaLavabo.setText("Error en obtenir l'adreça");
        }
    }
}