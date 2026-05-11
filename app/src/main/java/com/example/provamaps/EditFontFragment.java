package com.example.provamaps;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.provamaps.databinding.FragmentEditFontBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class EditFontFragment extends Fragment {

    Uri uriImatge;
    private FragmentEditFontBinding binding;
    private boolean hiHaFoto = false;
    private RealtimeManager realtimeManager;
    private ImageView imatgeFont;
    private int seleccioPotable = View.NO_ID;
    private int seleccioEstat = View.NO_ID;
    private MaterialButtonToggleGroup tggbPotable;
    private MaterialButtonToggleGroup tggbEstat;
    private Font font;
    private float latitud , longitud;
    private Geocoder geocoder;

    ActivityResultLauncher<Uri> contract = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
        imatgeFont.setImageURI(null);
        imatgeFont.setImageURI(uriImatge);
        hiHaFoto = true;
    });

    //Escull una imatge de la galeria i la posa al imageview de la font
    ActivityResultLauncher<PickVisualMediaRequest> escullImatgeGaleria =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    Log.d("TriaImatge", "URI de l'imatge seleccionada: " + uri);
                    uriImatge = uri;
                    imatgeFont.setImageURI(uri);
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

    public static EditFontFragment newInstance(Font font) {
        EditFontFragment fragment = new EditFontFragment();
        Bundle args = new Bundle();
        args.putSerializable("font", font);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        realtimeManager = RealtimeManager.getInstance();
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            font = (Font) getArguments().getSerializable("font");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditFontBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //Mostrar l'imatge de la font, i posar la funcionalitat als botons per a fer o penjar foto
        binding.botoTancarEditFont.setOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());

        imatgeFont = view.findViewById(R.id.iv_imatgeEditFont);//la foto
        tggbPotable = view.findViewById(R.id.tggb_fontPotable);
        tggbEstat = view.findViewById(R.id.tggb_fontEstat);

        if (font != null) {

            // Mostrar l'imatge de la font,
            Glide.with(this)
                    .load(font.getUrlfoto())
                    .placeholder(R.drawable.icona_imatge)
                    .error(R.drawable.icona_imatge)
                    .into(imatgeFont);

            // posar la funcionalitat als botons per a fer o penjar foto
            Context context = requireContext();
            uriImatge = createImageUri(context);

            imatgeFont = view.findViewById(R.id.iv_imatgeEditFont);

            Button botoFerFoto = view.findViewById(R.id.boto_ferFotoEditFont);
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
            Button botoAfegirImatge = view.findViewById(R.id.boto_afegirImatgeEditFont);
            botoAfegirImatge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)             {
                    escullImatgeGaleria.launch(new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build());
                }
            });

            // Configurar el estado de los ToggleButtons
            /*if (font.getPotable() != null) {
                switch (font.getPotable()) {
                    case "potable":
                        tggbPotable.check(R.id.btn_potable_si);
                        break;
                    case "no_potable":
                        tggbPotable.check(R.id.btn_potable_no);
                        break;
                    case "sense_informacio":
                        tggbPotable.check(R.id.btn_potable_desconeguda);
                        break;
                }
            }

            if (font.getEstat() != null) {
                switch (font.getEstat()) {
                    case "en_servei":
                        tggbEstat.check(R.id.boto_tggb_fontServei);
                        break;
                    case "no_en_servei":
                        tggbEstat.check(R.id.boto_tggb_fontNoServei);
                        break;
                }
            }*/


            /*
            * falta posar lo del mapa al editar, i comprovar que estigui be lo de seleccionar noves caracteristieques.
            * modificar a la base de dades els p np d ss es...
            */
            marcarBotonsSegonsDades(font.getPotable(),font.getEstat());//marca la seleccio

            latitud = Float.parseFloat(font.getLatitud());
            longitud = Float.parseFloat(font.getLongitud());

        }

        tggbPotable.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
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

        tggbEstat.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
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

        binding.botoActualitzarEditFont.setOnClickListener(
                v -> saveChanges()
        );

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        // Això posa les icones de la barra d'estat en color FOSC (perquè es vegin sobre fons blanc)
        Window window = requireActivity().getWindow();
        View decorView = window.getDecorView();
        window.setStatusBarColor(Color.BLACK); // Posa la barra de color blanc

        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(window, decorView);
        controller.setAppearanceLightStatusBars(true); // true = icones fosques, false = icones blanques

        // Identifica el contenidor principal del teu layout (el que conté la Toolbar)

        // Obté l'alçada de la barra d'estat
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            int statusBarHeight = getResources().getDimensionPixelSize(resourceId);

            // Aplica el padding només a aquest fragment
            binding.layPrincipalEdit.setPadding(0, statusBarHeight, 0, 0);
        }

        // Inicialitzar el mapa (com fas a la pantalla principal)
        binding.mapaAfegirFont.setTileSource(TileSourceFactory.MAPNIK);
        binding.mapaAfegirFont.setMultiTouchControls(true);

        // Centrar el mapa a la posició inicial que t'ha passat el GPS
        GeoPoint startPoint = new GeoPoint(latitud, longitud);
        binding.mapaAfegirFont.getController().setZoom(18.0);
        binding.mapaAfegirFont.getController().setCenter(startPoint);

        // ESCULTOR DE MOVIMENT
        binding.mapaAfegirFont.addMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                // Quan l'usuari mou el mapa, el centre canvia
                IGeoPoint centre = binding.mapaAfegirFont.getMapCenter();
                latitud = (float) centre.getLatitude();
                longitud = (float) centre.getLongitude();

                // Actualitzem l'adreça escrita (però sense carregar Yandex!)
                obtenirAdrecaSenseMapa();
                return true;
            }

            @Override
            public boolean onZoom(ZoomEvent event) { return false; }
        });

        binding.mapaAfegirFont.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Bloquegem l'scroll de la pàgina
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        // Alliberem l'scroll quan aixequem el dit
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false; // Retornem false per permetre que el mapa també rebi el toc
            }
        });


    }

    private void obtenirAdrecaSenseMapa() {
        try {
            List<Address> addresses = geocoder.getFromLocation(latitud, longitud, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address adr = addresses.get(0);
                String carrer = adr.getThoroughfare() != null ? adr.getThoroughfare() : "S/N";
                String numero = adr.getSubThoroughfare() != null ? adr.getSubThoroughfare() : "";
                String localitat = adr.getLocality() != null ? adr.getLocality() : "";//poblacio
                String comarca = adr.getSubAdminArea() != null ? adr.getSubAdminArea() : "";
                String provincia = adr.getAdminArea() != null ? adr.getAdminArea() : "";
                String pais = adr.getCountryName() != null ? adr.getCountryName() : "";

                binding.textAdrecaFont.setText(carrer + ", " + numero + ", " + localitat + ", " + comarca + ", " + provincia + ", " + pais);
            }
        } catch (IOException e) {
            binding.textAdrecaFont.setText("Buscant adreça...");
        }
    }

    // Dins del teu Fragment d'editar/afegir
    private void marcarBotonsSegonsDades(String tagPotableBD, String tagEstatBD) {

        // Per al grup de Potable
        for (int i = 0; i < binding.tggbFontPotable.getChildCount(); i++) {
            View v = binding.tggbFontPotable.getChildAt(i);
            if (v instanceof MaterialButton) {
                MaterialButton btn = (MaterialButton) v;
                // Si el TAG del botó coincideix amb el de la BD, el marquem
                if (tagPotableBD.equals(btn.getTag())) {
                    binding.tggbFontPotable.check(btn.getId());
                    break;
                }
            }
        }

        // Per al grup d'Estat (el mateix sistema)
        for (int i = 0; i < binding.tggbFontEstat.getChildCount(); i++) {
            View v = binding.tggbFontEstat.getChildAt(i);
            if (v instanceof MaterialButton && tagEstatBD.equals(v.getTag())) {
                binding.tggbFontEstat.check(v.getId());
            }
        }
    }

    private void saveChanges() {

        //Mirar que hi hagi els togglebuttons seleccionats
        String potable = null;
        String estat = null;


        if (seleccioPotable != View.NO_ID) {
            MaterialButton selectedButton = binding.getRoot().findViewById(seleccioPotable);
            //potable = selectedButton.getText().toString();
            font.setPotable(selectedButton.getTag().toString());
            //selectedButton.getText()
        }

        if (seleccioEstat != View.NO_ID) {
            MaterialButton selectedButton = binding.getRoot().findViewById(seleccioEstat);
            //estat = selectedButton.getText().toString();
            font.setEstat(selectedButton.getTag().toString());
            //selectedButton.getText()
        }

        if (hiHaFoto && uriImatge != null) {

            try {
                // Comprime la imagen antes de subirla
                byte[] compressedImage = comprimirImatge(getContext(), uriImatge);

                //Font novaFont = new Font(font.getKey(),font.getLatitud(),font.getLongitud(),potable,estat,null);
                realtimeManager.actualitzarFont(font.getKey(),font,compressedImage, new PenjarImatges.OnImageUploadListener() {
                    @Override
                    public void onUploadSuccess(String imageUrl) {
                        Toast.makeText(getContext(), "Font modificada amb èxit!", Toast.LENGTH_SHORT).show();
                        // Aquí puedes realizar alguna acción después de la subida exitosa
                    }

                    @Override
                    public void onUploadFailed(String errorMessage) {
                        Toast.makeText(getContext(), "Error al modificar la font: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error al comprimir la imatge", Toast.LENGTH_SHORT).show();
            }

        } else {
            // Crear la Font sense foto
           // Toast.makeText(getContext(), "mod no foto", Toast.LENGTH_SHORT).show();
            //Font novaFont = new Font(font.getKey(),font.getLatitud(),font.getLongitud(),potable,estat,null);
            realtimeManager.modificarFont(font.getKey(),font);

            //Toast.makeText(getContext(), "mod ", Toast.LENGTH_SHORT).show();

        }

        // Regresa al fragmento anterior
       // Toast.makeText(getContext(), "sotie ", Toast.LENGTH_SHORT).show();
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

}

/*

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_font, container, false);

        // Inicializar campos

        editLat = view.findViewById(R.id.textLatitudFont);
        editLon = view.findViewById(R.id.textLongitudFont);
        editUrlFoto = view.findViewById(R.id.edit_url_foto); // Asegúrate de que este ID exista en el layout XML
        ivImatge = view.findViewById(R.id.iv_imatgeEditFont);
        tggbPotable = view.findViewById(R.id.tggb_fontPotable);
        tggbEstat = view.findViewById(R.id.tggb_fontEstat);

        // Configura los campos con los datos de la fuente
        if (font != null) {
            editLat.setText(font.getLatitud());
            editLon.setText(font.getLongitud());
            editUrlFoto.setText(font.getUrlfoto());

            // Configurar la imagen
            Glide.with(this)
                    .load(font.getUrlfoto())
                    .placeholder(R.drawable.icona_imatge) // Imagen por defecto mientras carga
                    .error(R.drawable.icona_imatge) // Imagen en caso de error
                    .into(ivImatge);

            // Configurar el estado de los ToggleButtons
            if (font.getPotable() != null) {
                switch (font.getPotable()) {
                    case "potable":
                        tggbPotable.check(R.id.boto_tggb_fontPotable);
                        break;
                    case "no_potable":
                        tggbPotable.check(R.id.boto_tggb_fontNoPotable);
                        break;
                    case "sense_informacio":
                        tggbPotable.check(R.id.boto_tggb_noInformaciofontPotable);
                        break;
                }
            }

            if (font.getEstat() != null) {
                switch (font.getEstat()) {
                    case "en_servei":
                        tggbEstat.check(R.id.boto_tggb_fontServei);
                        break;
                    case "no_en_servei":
                        tggbEstat.check(R.id.boto_tggb_fontNoServei);
                        break;
                    case "sense_informacio":
                        tggbEstat.check(R.id.boto_tggb_fontNoInformacio);
                        break;
                }
            }
        }

        Button btnSave = view.findViewById(R.id.boto_afegir_afegirFont);
        btnSave.setOnClickListener(v -> saveChanges());

        return view;
    }

    private void saveChanges() {
        // Implementa el código para guardar los cambios aquí
        // Puedes obtener los datos del formulario y actualizar el objeto Font

        String tipus = editTipus.getText().toString();
        String lat = editLat.getText().toString();
        String lon = editLon.getText().toString();
        String urlFoto = editUrlFoto.getText().toString();

        // Obtener los valores seleccionados en los ToggleButtons
        int selectedPotableId = tggbPotable.getCheckedButtonId();
        String potable = null;
        switch (selectedPotableId) {
            case R.id.boto_tggb_fontPotable:
                potable = "potable";
                break;
            case R.id.boto_tggb_fontNoPotable:
                potable = "no_potable";
                break;
            case R.id.boto_tggb_noInformaciofontPotable:
                potable = "sense_informacio";
                break;
        }

        int selectedEstatId = tggbEstat.getCheckedButtonId();
        String estat = null;
        switch (selectedEstatId) {
            case R.id.boto_tggb_fontServei:
                estat = "en_servei";
                break;
            case R.id.boto_tggb_fontNoServei:
                estat = "no_en_servei";
                break;
            case R.id.boto_tggb_fontNoInformacio:
                estat = "sense_informacio";
                break;
        }

        // Actualizar el objeto font con los nuevos valores
        if (font != null) {

            font.setLatitud(lat);
            font.setLongitud(lon);
            font.setImageUrl(urlFoto);
            font.setPotable(potable);
            font.setEstat(estat);

            // Aquí puedes actualizar la fuente en la base de datos
        }
    }*/
