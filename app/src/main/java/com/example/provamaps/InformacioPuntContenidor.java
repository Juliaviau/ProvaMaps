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

public class InformacioPuntContenidor extends InfoWindow {

    private Contenidor contenidor;
    private String adreca;
    private Context context;



    public InformacioPuntContenidor(MapView mapView, Contenidor contenidor, String adreca, Context context) {
        super(R.layout.informacio_punt_contenidor, mapView);
        this.contenidor = contenidor;
        this.context = context;
        this.adreca = adreca;
    }

    @Override
    public void onOpen(Object item) {
        Marker marker = (Marker) item;

        //posa el text que se li diu des del marker
        ((TextView) mView.findViewById(R.id.infoPunt_tipus)).setText(contenidor.getTipus());
        ((TextView) mView.findViewById(R.id.infoPunt_adreca)).setText(adreca);
        ((TextView) mView.findViewById(R.id.infoPunt_tipusContenidor)).setText(String.join(", ", contenidor.getTipusContenidor()));

        Glide.with(context)
                .load(contenidor.getUrlfoto())
                .placeholder(R.drawable.icona_fer_foto)  // Imagen de carga (opcional)
                .error(R.drawable.icona_imatge)  // Imagen en caso de error (opcional)
                .into(((android.widget.ImageView) mView.findViewById(R.id.iv_fotoContenidor)));  // Imagen en el ImageView

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