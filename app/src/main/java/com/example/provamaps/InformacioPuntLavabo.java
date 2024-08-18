package com.example.provamaps;

import android.widget.Button;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

public class InformacioPuntLavabo extends InfoWindow {

    public InformacioPuntLavabo(MapView mapView) {
        super(R.layout.informacio_punt_font, mapView); // Usa el layout personalizado
    }

    @Override
    public void onOpen(Object item) {
        Marker marker = (Marker) item;

        //Tipus lavabo: 0:nadons 1:mixt: 2:dona 3:home 4:accesible
        //Paper hbigienic si/no
        //Disposa pica si/no

        //posa el text que se li diu des del marker
        /*((TextView) mView.findViewById(R.id.infoPunt_tipus)).setText(marker.getTitle());
        ((TextView) mView.findViewById(R.id.infoPunt_adreca)).setText(marker.getSnippet());
        ((TextView) mView.findViewById(R.id.infoPunt_extra1)).setText(marker.getSnippet());
        ((TextView) mView.findViewById(R.id.infoPunt_extra2)).setText(marker.getSnippet());*/



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




