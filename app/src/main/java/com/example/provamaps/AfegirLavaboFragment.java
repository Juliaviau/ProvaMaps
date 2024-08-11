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

import com.example.provamaps.databinding.FragmentAfegirLavaboBinding;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
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

    ActivityResultLauncher<Uri> contract = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
        imatgeLavabo.setImageURI(null);
        imatgeLavabo.setImageURI(uriImatge);
    });

    private Uri createImageUri(Context context) {
        File image = new File(context.getFilesDir(), "camera_fotos.png");
        return FileProvider.getUriForFile(context, "com.example.provamaps.FileProvider", image);
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
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
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

        return view;
    }

    ActivityResultLauncher<PickVisualMediaRequest> escullImatgeGaleria =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    Log.d("TriaImatge", "URI de l'imatge seleccionada: " + uri);
                    imatgeLavabo.setImageURI(uri);
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
                if(binding.textLongitudLavabo.getText().length() >= 0) {
                    obtenirAdreca();
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
                if (binding.textLatitudLavabo.getText().length() >= 0){     obtenirAdreca();
                }
            }
        });
    }

    private void obtenirAdreca () {

        double lat = Double.parseDouble(binding.textLatitudLavabo.getText().toString());
        double lon = Double.parseDouble(binding.textLongitudLavabo.getText().toString());
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

        binding.textAdrecaLavabo.setText(carrer + ", " + numero + ", " + poblacio + ", " + comarca + ", " + provincia + ", " + pais);

        //Foto del mapa
        //String url = "https://static-maps.yandex.ru/1.x/?lang=en-US&ll=" + longitud + "," + latitud + "&z=" + zoom + "&size=" + width + "," + height + "&l=sat&pt=" + longitud + "," + latitud + ",pm2rdl";
        //String url = "https://static-maps.yandex.ru/1.x/?lang=es-ES&ll=" + longitud + "," + latitud + "&z=" + zoom + "&size=" + width + "," + height + "&l=sat&pt=" + longitud + "," + latitud + ",pm2rdl";
        // String url = "https://staticmap.openstreetmap.de/staticmap.php?center=" + latitud + "," + longitud + "&zoom=" + zoom + "&size=" + width + "x" + height + "&maptype=mapnik&markers=" + latitud + "," + longitud + ",red-pushpin";
        // String url = "https://staticmap.openstreetmap.de/staticmap.php?center=" + latitud + "," + longitud + "&zoom=" + zoom + "&size=" + width + "x" + height + "&maptype=mapnik&markers=" + latitud + "," + longitud + ",red-pushpin";

        //Mapa normal
        //String url = "https://static-maps.yandex.ru/1.x/?lang=en-US&ll=" + longitud + "," + latitud + "&z=" + 16 + "&size=" + width + "," + height + "&l=map&pt=" + longitud + "," + latitud + ",pm2rdl";

        //Mapa satelit
        String url = "https://static-maps.yandex.ru/1.x/?lang=en-US&ll=" + lon + "," + lat + "&z=" + 18 + "&size=" + 440 + "," + 310 + "&l=sat&pt=" + lon + "," + lat + ",pm2rdl";

        Picasso.get().load(url).into(binding.ivMapaLocalitzacioAfegirLavabo);
    }
}