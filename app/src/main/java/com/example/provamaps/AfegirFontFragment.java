package com.example.provamaps;

import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.contract.ActivityResultContracts.*;

public class AfegirFontFragment extends Fragment {



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
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_afegir_font, container, false);
        Button closeButton = rootView.findViewById(R.id.boto_tancar_afegir_font);
        Button botoAfegirImatge = rootView.findViewById(R.id.boto_afegirImatgeFont);
        imatgeFont = rootView.findViewById(R.id.iv_imatgeAfegirFont);


        botoAfegirImatge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)             {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tancarFragment();
            }
        });

        return rootView;

        //return inflater.inflate(R.layout.fragment_afegir_font, container, false);
    }

    ImageView imatgeFont;

    // Registers a photo picker activity launcher in single-select mode.
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the photo picker.
                if (uri != null) {
                    Log.d("TriaImatge", "URI de l'imatge seleccionada: " + uri);
                    imatgeFont.setImageURI(uri);
                } else {
                    Log.d("TriaImatge", "No s'ha seleccionat cap imatge");
                }
            });

    // Include only one of the following calls to launch(), depending on the types
    // of media that you want to let the user choose from.

    // Launch the photo picker and let the user choose images and videos.
    //pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(PickVisualMedia.ImageAndVideo.INSTANCE).build());

    // Launch the photo picker and let the user choose only images.
    //pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(PickVisualMedia.ImageOnly.INSTANCE).build());

    // Launch the photo picker and let the user choose only videos.
    //pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(PickVisualMedia.VideoOnly.INSTANCE).build());

    // Launch the photo picker and let the user choose only images/videos of a
    // specific MIME type, such as GIFs.
    //String mimeType = "image/gif";
    //pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(new PickVisualMedia.SingleMimeType(mimeType)).build());



    /*private ActivityResultLauncher<PickVisualMediaRequest> pickVisualMediaActivityResultLauncher;

    private ActivityResultLauncher<PickVisualMedia> triaImg = registerForActivityResult(new PickVisualMedia(), uri -> {
        if (uri != null) {
            // Handle selected image
        } else {
            // Handle no image selected
        }
    });*/



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