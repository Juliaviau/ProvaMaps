package com.example.provamaps;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

public class InformacioPuntFont extends InfoWindow {

    private Font font;
    private String adreca;
    private Context context;
    private IniciFragment fragment;


    public InformacioPuntFont(MapView mapView, Font font, String adreca,Context context, IniciFragment fragment) {
        super(R.layout.informacio_punt_font, mapView);
        this.font = font;
        this.fragment = fragment;
        this.context = context;
        this.adreca = adreca;
    }

    @Override
    public void onOpen(Object item) {
        Marker marker = (Marker) item;

        //posa el text que se li diu des del marker
        ((TextView) mView.findViewById(R.id.infoPunt_tipus)).setText(font.getTipus());
        ((TextView) mView.findViewById(R.id.infoPunt_adreca)).setText(adreca);
        ((TextView) mView.findViewById(R.id.infoPunt_extra1)).setText(font.getPotable());
        ((TextView) mView.findViewById(R.id.infoPunt_extra2)).setText(font.getEstat());

        Glide.with(context)
                .load(font.getUrlfoto())
                .placeholder(R.drawable.icona_fer_foto)  // Imagen de carga (opcional)
                .error(R.drawable.icona_imatge)  // Imagen en caso de error (opcional)
                .into(((android.widget.ImageView) mView.findViewById(R.id.iv_mostrar_fotoFont)));  // Imagen en el ImageView

        Button btnMoreInfo = mView.findViewById(R.id.infoPunt_anar);
        btnMoreInfo.setOnClickListener(v -> {
            // al clicar el boto anar, mostrar la ruta des del punt actual fins al punt
            fragment.calculateRoute(marker.getPosition());

        });

    }



    @Override
    public void onClose() {
        // Puedes manejar eventos cuando la ventana se cierra
    }
}