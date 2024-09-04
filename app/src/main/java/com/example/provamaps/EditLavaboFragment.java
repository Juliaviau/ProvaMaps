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
import com.example.provamaps.databinding.FragmentEditLavaboBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EditLavaboFragment extends Fragment {

    Uri uriImatge;
    private FragmentEditLavaboBinding binding;
    private boolean hiHaFoto = false;
    private RealtimeManager realtimeManager;
    private ImageView imatgeLavabo;
    private MaterialButtonToggleGroup tggbPaper;
    private MaterialButtonToggleGroup tggbPica;
    private List<Integer> seleccioTipusLavabo = new ArrayList<>();

    private int seleccioDisposaPaper = View.NO_ID;
    private int seleccioDisposaPica = View.NO_ID;
    private Lavabo lavabo;

    ActivityResultLauncher<Uri> contract = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
        imatgeLavabo.setImageURI(null);
        imatgeLavabo.setImageURI(uriImatge);
        hiHaFoto = true;
    });

    //Escull una imatge de la galeria i la posa al imageview de la font
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


    private Uri createImageUri(Context context) {
        //File imatgeNom = new File(context.getFilesDir(), "camera_fotos.png");
        String imatgeNom = "camera_fotos_" + System.currentTimeMillis() + ".jpg";
        File imatge = new File(context.getFilesDir(), imatgeNom);
        return FileProvider.getUriForFile(context, "com.example.provamaps.FileProvider", imatge);
    }

    public static EditLavaboFragment newInstance(Lavabo lavabo) {
        EditLavaboFragment fragment = new EditLavaboFragment();
        Bundle args = new Bundle();
        args.putSerializable("lavabo", (Serializable) lavabo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        realtimeManager = RealtimeManager.getInstance();
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lavabo = (Lavabo) getArguments().getSerializable("lavabo");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditLavaboBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //Mostrar l'imatge de la font, i posar la funcionalitat als botons per a fer o penjar foto
        Button closeButton = view.findViewById(R.id.boto_tancar_modificar_element);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        imatgeLavabo = view.findViewById(R.id.iv_imatgeAfegirLavabo);//la foto
        tggbPaper = view.findViewById(R.id.tggb_lavaboPaper);
        tggbPica = view.findViewById(R.id.tggb_lavaboPica);

        if (lavabo != null) {

            // Mostrar l'imatge de la font,
            Glide.with(this)
                    .load(lavabo.getUrlfoto())
                    .placeholder(R.drawable.icona_imatge)
                    .error(R.drawable.icona_imatge)
                    .into(imatgeLavabo);

            // posar la funcionalitat als botons per a fer o penjar foto
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

            //Afegir imatge des de la galeria
            Button botoAfegirImatge = view.findViewById(R.id.boto_afegirImatgeLavabo);
            botoAfegirImatge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)             {
                    escullImatgeGaleria.launch(new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build());
                }
            });


            // Configurar el estado de los ToggleButtons
            if (lavabo.getDisposaPica() != null) {
                switch (lavabo.getDisposaPica()) {
                    case "Si":
                        tggbPica.check(R.id.boto_tggb_lavaboPica);
                        break;
                    case "No":
                        tggbPica.check(R.id.boto_tggb_lavaboNoPica);
                        break;
                }
            }

            if (lavabo.getDisposaPaper() != null) {
                switch (lavabo.getDisposaPaper()) {
                    case "Si":
                        tggbPaper.check(R.id.boto_tggb_lavaboPaper);
                        break;
                    case "No":
                        tggbPaper.check(R.id.boto_tggb_lavaboNoPaper);
                        break;
                }
            }

            if (lavabo.getTipusLavabo() != null) {
                for (Integer tipus : lavabo.getTipusLavabo()) {
                    switch (tipus) {
                        case 1:

                            binding.botoTggbLavaboTipusNado.setSelected(true);
                           // binding.botoTggbLavaboTipusNado.setBackgroundColor(getResources().getColor(R.color.colorSelected)); // Cambia el color del botón o realiza otra acción
                            break;
                        case 2:
                            binding.botoTggbLavaboTipusHomeDona.setSelected(true);
                            break;
                        case 3:
                            binding.botoTggbLavaboTipusDona.setSelected(true);
                            break;
                        case 4:
                            binding.botoTggbLavaboTipusHome.setSelected(true);
                            break;
                        case 5:
                            binding.botoTggbLavaboTipusAccesible.setSelected(true);
                            break;
                    }
                }
            }
        }



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

        Button btnSave = view.findViewById(R.id.boto_afegir_afegirLavabo);
        btnSave.setOnClickListener(v -> saveChanges());

        return view;
    }

    private void saveChanges() {

        //Mirar que hi hagi els togglebuttons seleccionats
        String disposaPaper = null;
        String disposaPica = null;

        if (seleccioDisposaPaper != View.NO_ID) {
            MaterialButton selectedButton = binding.getRoot().findViewById(seleccioDisposaPaper);
            lavabo.setDisposaPaper(selectedButton.getText().toString());
        }

        if (seleccioDisposaPica != View.NO_ID) {
            MaterialButton selectedButton = binding.getRoot().findViewById(seleccioDisposaPica);
            lavabo.setDisposaPica(selectedButton.getText().toString());
        }

        lavabo.setTipusLavabo(seleccioTipusLavabo);

        if (hiHaFoto && uriImatge != null) {

            try {
                // Comprime la imagen antes de subirla
                byte[] compressedImage = comprimirImatge(getContext(), uriImatge);

                //Font novaFont = new Font(font.getKey(),font.getLatitud(),font.getLongitud(),potable,estat,null);
                realtimeManager.actualitzarLavabo(lavabo.getKey(), lavabo,compressedImage, new PenjarImatges.OnImageUploadListener() {
                    @Override
                    public void onUploadSuccess(String imageUrl) {
                        Toast.makeText(getContext(), "Lavabo modificat amb èxit!", Toast.LENGTH_SHORT).show();
                        // Aquí puedes realizar alguna acción después de la subida exitosa
                    }

                    @Override
                    public void onUploadFailed(String errorMessage) {
                        Toast.makeText(getContext(), "Error al modificar el lavabo: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error al comprimir la imatge", Toast.LENGTH_SHORT).show();
            }

        } else {
            // Crear la Lavabo sense foto
            realtimeManager.modificarLavabo(lavabo.getKey(), lavabo);
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


