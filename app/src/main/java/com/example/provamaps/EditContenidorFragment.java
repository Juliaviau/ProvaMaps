package com.example.provamaps;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.provamaps.databinding.FragmentEditContenidorBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EditContenidorFragment extends Fragment {

    Uri uriImatge;
    private FragmentEditContenidorBinding binding;
    private boolean hiHaFoto = false;
    private RealtimeManager realtimeManager;
    private ImageView imatgeContenidor;
    private MaterialButtonToggleGroup tggbPaper;
    private MaterialButtonToggleGroup tggbPica;
    private List<String> selecioTipusContenidor = new ArrayList<>();

    private Contenidor contenidor;

    ActivityResultLauncher<Uri> contract = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
        imatgeContenidor.setImageURI(null);
        imatgeContenidor.setImageURI(uriImatge);
        hiHaFoto = true;
    });

    //Escull una imatge de la galeria i la posa al imageview de la font
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


    private Uri createImageUri(Context context) {
        //File imatgeNom = new File(context.getFilesDir(), "camera_fotos.png");
        String imatgeNom = "camera_fotos_" + System.currentTimeMillis() + ".jpg";
        File imatge = new File(context.getFilesDir(), imatgeNom);
        return FileProvider.getUriForFile(context, "com.example.provamaps.FileProvider", imatge);
    }

    public static EditContenidorFragment newInstance(Contenidor contenidor) {
        EditContenidorFragment fragment = new EditContenidorFragment();
        Bundle args = new Bundle();
        args.putSerializable("contenidor", (Serializable) contenidor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        realtimeManager = RealtimeManager.getInstance();
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            contenidor = (Contenidor) getArguments().getSerializable("contenidor");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditContenidorBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //Mostrar l'imatge de la font, i posar la funcionalitat als botons per a fer o penjar foto
        Button closeButton = view.findViewById(R.id.boto_tancar_modificar_contenidor);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        imatgeContenidor = view.findViewById(R.id.iv_imatgeAfegirContenidor);//la foto

        if (contenidor != null) {

            // Mostrar l'imatge de la font,
            Glide.with(this)
                    .load(contenidor.getUrlfoto())
                    .placeholder(R.drawable.icona_imatge)
                    .error(R.drawable.icona_imatge)
                    .into(imatgeContenidor);

            // posar la funcionalitat als botons per a fer o penjar foto
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

            //Afegir imatge des de la galeria
            Button botoAfegirImatge = view.findViewById(R.id.boto_modificarImatgeContenidor);
            botoAfegirImatge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)             {
                    escullImatgeGaleria.launch(new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build());
                }
            });



            if (contenidor.getTipusContenidor() != null) {
                for (String tipus : contenidor.getTipusContenidor()) {
                    switch (tipus) {
                        case "Paperera":

                            binding.chipPaperera.setSelected(true);
                            // binding.botoTggbContenidorTipusNado.setBackgroundColor(getResources().getColor(R.color.colorSelected)); // Cambia el color del botón o realiza otra acción
                            break;
                        case "Vidre":
                            binding.chipVidre.setSelected(true);
                            break;
                        case "Paper i cartró":
                            binding.chipPaper.setSelected(true);
                            break;
                        case "Envasos":
                            binding.chipEnvasos.setSelected(true);
                            break;
                        case "Orgànic":
                            binding.chipOrganic.setSelected(true);
                            break;
                        case "Roba":
                            binding.chipRoba.setSelected(true);
                            break;
                        case "Rebuig":
                            binding.chipRebuig.setSelected(true);
                            break;
                    }
                }
            }
        }

        ChipGroup chipGroup = binding.chipGroupTipusContenidor;
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
        });


        Button btnSave = view.findViewById(R.id.boto_modificarcontenidor);
        btnSave.setOnClickListener(v -> saveChanges());

        return view;
    }

    private void saveChanges() {
        contenidor.setTipusContenidor(selecioTipusContenidor);
        if (hiHaFoto && uriImatge != null) {

            try {
                // Comprime la imagen antes de subirla
                byte[] compressedImage = comprimirImatge(getContext(), uriImatge);

                //Font novaFont = new Font(font.getKey(),font.getLatitud(),font.getLongitud(),potable,estat,null);
                realtimeManager.actualitzarContenidor(contenidor.getKey(), contenidor,compressedImage, new PenjarImatges.OnImageUploadListener() {
                    @Override
                    public void onUploadSuccess(String imageUrl) {
                        Toast.makeText(getContext(), "Contenidor modificat amb èxit!", Toast.LENGTH_SHORT).show();
                        // Aquí puedes realizar alguna acción después de la subida exitosa
                    }

                    @Override
                    public void onUploadFailed(String errorMessage) {
                        Toast.makeText(getContext(), "Error al modificar el contenidor: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error al comprimir la imatge", Toast.LENGTH_SHORT).show();
            }

        } else {
            // Crear la Contenidor sense foto
            realtimeManager.modificarContenidor(contenidor.getKey(), contenidor);
        }
        getActivity().getSupportFragmentManager().popBackStack();
    }



    private byte[] comprimirImatge(Context context, Uri uri) throws IOException {
        // Converteix l'URI a un Bitmap
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);

        // Redimensiona la imatge
        bitmap = resizeBitmap(bitmap, 1024, 1024);

        // Comprimeix el Bitmap
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

}


