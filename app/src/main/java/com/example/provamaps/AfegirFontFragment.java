package com.example.provamaps;

import android.content.Context;
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

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts.*;
import androidx.fragment.app.FragmentResultListener;

import com.example.provamaps.databinding.FragmentAfegirFontBinding;
import com.example.provamaps.databinding.FragmentIniciBinding;

import java.io.File;
import java.net.URI;

public class AfegirFontFragment extends Fragment {

    Uri uriImatge;
    ImageView imatgeFont;
    private FragmentAfegirFontBinding binding;

    ActivityResultLauncher<Uri> contract = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
        imatgeFont.setImageURI(null);
        imatgeFont.setImageURI(uriImatge);
    });

    private Uri createImageUri(Context context) {
        File image = new File(context.getFilesDir(), "camera_fotos.png");
        return FileProvider.getUriForFile(context, "com.example.provamaps.FileProvider", image);
    }

    public AfegirFontFragment() {
        // Required empty public constructor
    }


    public static AfegirFontFragment newInstance(String param1, String param2) {
        AfegirFontFragment fragment = new AfegirFontFragment();

        return fragment;
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


        /*if (getArguments() != null) {
            //Float lat = getArguments().getFloat("latitud");
            binding.textLatitud.setText(getArguments().getFloat("latitud") + " adada ");
            MyUtils.toast(getActivity(), String.valueOf(getArguments().getFloat("latitud")));
        } else {
            binding.textLatitud.setText(" nope ");

        }*/



        Context context = requireContext();
        uriImatge = createImageUri(context);

        imatgeFont = view.findViewById(R.id.iv_imatgeAfegirFont);

        Button botoFerFoto = view.findViewById(R.id.boto_ferFotoFont);
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

        Button botoAfegirImatge = view.findViewById(R.id.boto_afegirImatgeFont);
        botoAfegirImatge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)             {
                escullImatgeGaleria.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });




        return view;
    }

    ActivityResultLauncher<PickVisualMediaRequest> escullImatgeGaleria =
            registerForActivityResult(new PickVisualMedia(), uri -> {
                if (uri != null) {
                    Log.d("TriaImatge", "URI de l'imatge seleccionada: " + uri);
                    imatgeFont.setImageURI(uri);
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

        if (getArguments() != null) {
            float latitud = getArguments().getFloat("latitud", 0.0f);
            float longitud = getArguments().getFloat("longitud", 0.0f);

            binding.textLatitud.setText(latitud + " adada ");
            MyUtils.toast(getActivity(), latitud + " latit");
            MyUtils.toast(getActivity(), longitud + " long");
        } else {
            binding.textLatitud.setText("nope");
        }
    }
}