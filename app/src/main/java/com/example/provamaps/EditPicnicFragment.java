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
import com.example.provamaps.databinding.FragmentEditPicnicBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EditPicnicFragment extends Fragment {

    Uri uriImatge;
    private FragmentEditPicnicBinding binding;
    private boolean hiHaFoto = false;
    private RealtimeManager realtimeManager;
    private ImageView imatgePicnic;
    private int seleccioBancsIOTaules = View.NO_ID;
    private List<Integer> seleccioQueHiHa = new ArrayList<>();
    private Picnic picnic;

    ActivityResultLauncher<Uri> contract = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
        imatgePicnic.setImageURI(null);
        imatgePicnic.setImageURI(uriImatge);
        hiHaFoto = true;
    });

    //Escull una imatge de la galeria i la posa al imageview de la font
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


    private Uri createImageUri(Context context) {
        //File imatgeNom = new File(context.getFilesDir(), "camera_fotos.png");
        String imatgeNom = "camera_fotos_" + System.currentTimeMillis() + ".jpg";
        File imatge = new File(context.getFilesDir(), imatgeNom);
        return FileProvider.getUriForFile(context, "com.example.provamaps.FileProvider", imatge);
    }

    public static EditPicnicFragment newInstance(Picnic picnic) {
        EditPicnicFragment fragment = new EditPicnicFragment();
        Bundle args = new Bundle();
        args.putSerializable("picnic", (Serializable) picnic);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        realtimeManager = RealtimeManager.getInstance();
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            picnic = (Picnic) getArguments().getSerializable("picnic");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditPicnicBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //Mostrar l'imatge de la font, i posar la funcionalitat als botons per a fer o penjar foto
        Button closeButton = view.findViewById(R.id.boto_tancar_modificar_picnic);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        imatgePicnic = view.findViewById(R.id.iv_imatgeAfegirPicnic);//la foto

        if (picnic != null) {

            // Mostrar l'imatge de la font,
            Glide.with(this)
                    .load(picnic.getUrlfoto())
                    .placeholder(R.drawable.icona_imatge)
                    .error(R.drawable.icona_imatge)
                    .into(imatgePicnic);

            // posar la funcionalitat als botons per a fer o penjar foto
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
            Button botoAfegirImatge = view.findViewById(R.id.boto_modificarImatgePicnic);
            botoAfegirImatge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)             {
                    escullImatgeGaleria.launch(new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build());
                }
            });


            // Configurar el estado de los ToggleButtons
            if (picnic.getBancsIOTaules() != null) {
                switch (picnic.getBancsIOTaules()) {
                    case "Bancs":
                        binding.botoTggbBancs.setSelected(true);
                        break;
                    case "Taules":
                        binding.botoTggbTaules.setSelected(true);
                        break;
                        case "Bancs i Taules":
                            binding.botoTggbBancTaula.setSelected(true);
                        break;
                }
            }

            if (picnic.getQueHiHa() != null) {
                for (Integer tipus : picnic.getQueHiHa()) {
                    switch (tipus) {
                        case 0:
                            binding.botoTggbPicnicConteBarbacoa.setSelected(true);
                            // binding.botoTggbPicnicTipusNado.setBackgroundColor(getResources().getColor(R.color.colorSelected)); // Cambia el color del botón o realiza otra acción
                            break;
                        case 1:
                            binding.botoTggbPicnicConteHerba.setSelected(true);
                            // binding.botoTggbPicnicTipusNado.setBackgroundColor(getResources().getColor(R.color.colorSelected)); // Cambia el color del botón o realiza otra acción
                            break;
                        case 2:
                            binding.tggbPicnicConteContenidors.setSelected(true);
                            break;
                        case 3:
                            binding.botoTggbPicnicConteSombrilla.setSelected(true);
                            break;
                        case 4:
                            binding.botoTggbPicnicConteLlum.setSelected(true);
                            break;
                    }
                }
            }
        }



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

        Button btnSave = view.findViewById(R.id.boto_afegir_afegirPicnic);
        binding.botoAfegirAfegirPicnic.setOnClickListener(v -> saveChanges());

        return view;
    }

    private void saveChanges() {

        //Mirar que hi hagi els togglebuttons seleccionats
        String disposaPaper = null;
        String disposaPica = null;

        if (seleccioBancsIOTaules != View.NO_ID) {
            MaterialButton selectedButton = binding.getRoot().findViewById(seleccioBancsIOTaules);
            picnic.setBancsIOTaules(selectedButton.getText().toString());
        }

        picnic.setQueHiHa(seleccioQueHiHa);

        if (hiHaFoto && uriImatge != null) {

            try {
                // Comprime la imagen antes de subirla
                byte[] compressedImage = comprimirImatge(getContext(), uriImatge);

                //Font novaFont = new Font(font.getKey(),font.getLatitud(),font.getLongitud(),potable,estat,null);
                realtimeManager.actualitzarPicnic(picnic.getKey(), picnic,compressedImage, new PenjarImatges.OnImageUploadListener() {
                    @Override
                    public void onUploadSuccess(String imageUrl) {
                        Toast.makeText(getContext(), "Picnic modificat amb èxit!", Toast.LENGTH_SHORT).show();
                        // Aquí puedes realizar alguna acción después de la subida exitosa
                    }

                    @Override
                    public void onUploadFailed(String errorMessage) {
                        Toast.makeText(getContext(), "Error al modificar el picnic: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error al comprimir la imatge", Toast.LENGTH_SHORT).show();
            }

        } else {
            // Crear la Picnic sense foto
            realtimeManager.modificarPicnic(picnic.getKey(), picnic);
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


