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

import com.example.provamaps.databinding.FragmentAfegirContenidorBinding;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AfegirContenidorFragment extends Fragment {
    Uri uriImatge;
    ImageView imatgeContenidor;
    private FragmentAfegirContenidorBinding binding;
    private float latitud, longitud;
    private Geocoder geocoder;
    private List<Address> adreca;

    ActivityResultLauncher<Uri> contract = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
        imatgeContenidor.setImageURI(null);
        imatgeContenidor.setImageURI(uriImatge);
    });

    private Uri createImageUri(Context context) {
        File image = new File(context.getFilesDir(), "camera_fotos.png");
        return FileProvider.getUriForFile(context, "com.example.provamaps.FileProvider", image);
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
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
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

        return view;
    }

    ActivityResultLauncher<PickVisualMediaRequest> escullImatgeGaleria =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    Log.d("TriaImatge", "URI de l'imatge seleccionada: " + uri);
                    imatgeContenidor.setImageURI(uri);
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
                if(binding.textLongitudContenidor.getText().length() >= 0) {
                    obtenirAdreca();
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
                if (binding.textLatitudContenidor.getText().length() >= 0){     obtenirAdreca();
                }
            }
        });

    }

    private void obtenirAdreca () {

        double lat = Double.parseDouble(binding.textLatitudContenidor.getText().toString());
        double lon = Double.parseDouble(binding.textLongitudContenidor.getText().toString());
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

        binding.textAdrecaContenidor.setText(carrer + ", " + numero + ", " + poblacio + ", " + comarca + ", " + provincia + ", " + pais);

        //Foto del mapa
        //Mapa normal
        //String url = "https://static-maps.yandex.ru/1.x/?lang=en-US&ll=" + longitud + "," + latitud + "&z=" + 16 + "&size=" + width + "," + height + "&l=map&pt=" + longitud + "," + latitud + ",pm2rdl";

        //Mapa satelit
        String url = "https://static-maps.yandex.ru/1.x/?lang=en-US&ll=" + lon + "," + lat + "&z=" + 18 + "&size=" + 440 + "," + 310 + "&l=sat&pt=" + lon + "," + lat + ",pm2rdl";

        Picasso.get().load(url).into(binding.ivMapaLocalitzacioAfegirContenidor);
    }
}