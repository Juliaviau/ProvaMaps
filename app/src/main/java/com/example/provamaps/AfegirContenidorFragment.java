package com.example.provamaps;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class AfegirContenidorFragment extends Fragment {
    Uri uriImatge;
    ImageView imatgeContenidor;

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
        View rootView = inflater.inflate(R.layout.fragment_afegir_contenidor, container, false);
        Button closeButton = rootView.findViewById(R.id.boto_tancar_afegir_contenidor);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tancarFragment();
            }
        });

        Context context = requireContext();
        uriImatge = createImageUri(context);

        imatgeContenidor = rootView.findViewById(R.id.iv_imatgeAfegirContenidor);

        Button botoFerFoto = rootView.findViewById(R.id.boto_ferFotoContenidor);
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

        Button botoAfegirImatge = rootView.findViewById(R.id.boto_afegirImatgeContenidor);
        botoAfegirImatge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)             {
                escullImatgeGaleria.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        return rootView;
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
}