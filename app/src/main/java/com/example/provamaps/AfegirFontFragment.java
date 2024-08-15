package com.example.provamaps;

import android.content.Context;
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
import com.squareup.picasso.Picasso;

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

    ActivityResultLauncher<Uri> contract = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
        imatgeFont.setImageURI(null);
        imatgeFont.setImageURI(uriImatge);
        hiHaFoto = true;
    });

    private Uri createImageUri(Context context) {
        File image = new File(context.getFilesDir(), "camera_fotos.png");
        return FileProvider.getUriForFile(context, "com.example.provamaps.FileProvider", image);
    }

    public AfegirFontFragment() {
        // Required empty public constructor
    }

    public static AfegirFontFragment newInstance(String param1, String param2) {
        return new AfegirFontFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
                if (seleccioPotable != View.NO_ID) {
                    MaterialButton selectedButton = binding.getRoot().findViewById(seleccioPotable);
                    //selectedButton.getText()
                } else {
                    //No s'ha seleccionat cap
                }

                if (seleccioEstat != View.NO_ID) {
                    MaterialButton selectedButton = binding.getRoot().findViewById(seleccioEstat);
                    //selectedButton.getText()
                } else {
                    //No s'ha seleccionat cap
                }

                //Que hi hagi coordenades
                if (binding.textLatitudFont.getText() != null && binding.textLongitudFont.getText() != null) {

                } else {
                    //No hi ha coordenades
                }

                //Foto opcional?

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
                    imatgeFont.setImageURI(uri);
                    hiHaFoto = true;
                    //Guardar la uri a la base de dades
                } else {
                    Log.d("TriaImatge", "No s'ha seleccionat cap imatge");
                }
            });

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

        Picasso.get().load(url).into(binding.ivMapaLocalitzacioAfegirFont);
    }
}