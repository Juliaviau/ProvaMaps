package com.example.provamaps;

import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

public class InformacioPuntPicnic extends InfoWindow {

    private Picnic picnic;
    private String adreca;
    private Context context;

    private static final int[] ICONS = {
            R.drawable.icona_barbacoa,  // 0
            R.drawable.icona_herba,  // 1
            R.drawable.icona_contenidor,  // 2
            R.drawable.icona_sombrilla,  // 3
            R.drawable.icona_llum   // 4
    };

    public InformacioPuntPicnic(MapView mapView, Picnic picnic, String adreca, Context context) {
        super(R.layout.informacio_punt_picnic, mapView);
        this.picnic = picnic;
        this.context = context;
        this.adreca = adreca;
    }

    @Override
    public void onOpen(Object item) {
        Marker marker = (Marker) item;

        //posa el text que se li diu des del marker
        ((TextView) mView.findViewById(R.id.infoPunt_tipus)).setText(picnic.getTipus());
        ((TextView) mView.findViewById(R.id.infoPunt_adreca)).setText(adreca);
        ((TextView) mView.findViewById(R.id.infoPunt_bancs)).setText(picnic.getBancsIOTaules());


        // Limpiar los íconos antes de agregar los nuevos
        LinearLayout iconsLayout = mView.findViewById(R.id.linearLayoutIcons);
        iconsLayout.removeAllViews();

        // Añadir íconos según el tipo de lavabo
        for (int posicio : picnic.getQueHiHa()) {
            ImageView iconView = new ImageView(context);
            iconView.setImageResource(ICONS[posicio]);
            iconView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            iconsLayout.addView(iconView);
        }
        Glide.with(context)
                .load(picnic.getUrlfoto())
                .placeholder(R.drawable.icona_fer_foto)  // Imagen de carga (opcional)
                .error(R.drawable.icona_imatge)  // Imagen en caso de error (opcional)
                .into(((android.widget.ImageView) mView.findViewById(R.id.iv_informacioPicnic)));  // Imagen en el ImageView

        Button btnMoreInfo = mView.findViewById(R.id.infoPunt_anar);
        btnMoreInfo.setOnClickListener(v -> {
            // al clicar el boto anar, mostrar la ruta des del punt actual fins al punt
        });

    }



    @Override
    public void onClose() {
        // Puedes manejar eventos cuando la ventana se cierra
    }
}