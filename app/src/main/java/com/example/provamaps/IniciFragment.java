package com.example.provamaps;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.provamaps.databinding.FragmentIniciBinding;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class IniciFragment extends Fragment implements MapListener, GpsStatus.Listener {

    private MapView mMap;
    private IMapController controller;
    private MyLocationNewOverlay mMyLocationOverlay;
    private FragmentIniciBinding binding;
    private static final String TAG = "INICI_TAG";
    private ScaleBarOverlay mScaleBarOverlay;
    private GeoPoint startPoint = new GeoPoint(41.964109, 2.829905);//posicio universitat
    private List<Marker> fontMarkers = new ArrayList<>();
    private boolean areFontMarkersVisible = true;
    private List<Font> llistaFonts = new ArrayList<>();
    public IniciFragment() {}

    public static IniciFragment newInstance(String param1, String param2) {
        IniciFragment fragment = new IniciFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Handle arguments if any
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentIniciBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Configuration.getInstance().load(
                getActivity().getApplicationContext(),
                getActivity().getSharedPreferences(getString(R.string.app_name), getActivity().MODE_PRIVATE)
        );

        //TODO: pregunti per ubicacio,si no hi ha, que comenci a unes cordenades en concret

        mMap = binding.osmmap;
        mMap.setTileSource(TileSourceFactory.MAPNIK);
        mMap.setMultiTouchControls(true);
        mMap.getLocalVisibleRect(new Rect());

        //Posicio actual
        //mMyLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getActivity()), mMap);
        controller = mMap.getController();
        controller.setZoom(18.0);

        //Brújula a dalt a lesquerra
        CompassOverlay compassOverlay = new CompassOverlay(getActivity(), mMap);
        compassOverlay.enableCompass();
        mMap.getOverlays().add(compassOverlay);

        //Rotacio del mapa
        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(getActivity(), mMap);
        mRotationGestureOverlay.setEnabled(true);
        mMap.getOverlays().add(mRotationGestureOverlay);

        //Linia d'escala del mapa
        final DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        mScaleBarOverlay = new ScaleBarOverlay(mMap);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 80);
        mMap.getOverlays().add(this.mScaleBarOverlay);


        binding.btnCentrarMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMyLocationOverlay.getMyLocation() != null) {
                    /*GeoPoint myLocation = mMyLocationOverlay.getMyLocation();
                    controller.setCenter(myLocation);
                    controller.animateTo(myLocation);*/
                    mMyLocationOverlay.enableFollowLocation();
                } else {
                    // Si la ubicació no esta disponible
                    controller.setCenter(startPoint);
                    controller.animateTo(startPoint);
                }
            }
        });

        Font font = new Font("iddd","41.964488", "3.029476","Potable","En servei",null);
        Font font2 = new Font("iddd","41.964288", "3.029876","Potable","En servei",null);

        llistaFonts.add(font2);
        llistaFonts.add(font);

        Lavabo lavabo = new Lavabo("iddd","41.944488", "3.029176", Arrays.asList(3, 1, 4),"Si","Si", null);
        Picnic picnic = new Picnic("iddd","41.972909", "3.029476","Bancs",Arrays.asList(0,1),null);
        Contenidor contenidor = new Contenidor("iddd","41.964118", "3.029476",Arrays.asList("Envasos", "Paperera"),null);

        //Mostrar o amagar les fonts
        binding.btnFont.setOnClickListener(v -> {
            if (areFontMarkersVisible) {
                // Ocultar los marcadores
                for (Marker marker : fontMarkers) {
                    marker.setVisible(false);
                }
                binding.btnFont.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.color_boto_inactiu));
            } else {
                // Mostrar los marcadores
                for (Marker marker : fontMarkers) {
                    marker.setVisible(true);
                }
                binding.btnFont.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.color_boto_actiu));
            }
            areFontMarkersVisible = !areFontMarkersVisible; // Alternar el estado de visibilidad
            mMap.invalidate(); // Refrescar el mapa
        });

        //Obtenir totes les fonts, que es guarden en un LiveData<List<Font>>
        //es crida l'afegir punts de les fonts, passant larray de les fonts
        //creara un fontmarkers amb l'informacio dels marcadors
        //i amb el botofont es mostraran o no dins del mapa

        afegirPuntsFonts(llistaFonts);

        controller.setCenter(startPoint);


        // Mirar si hi ha permisos d'ubicació
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mostrarUbicacio();
        } else {
            MyUtils.toast(getContext(), "Permiso de ubicación no otorgado, centrando el mapa en el punto predeterminado.");
        }



        mMap.addMapListener(this);

        /*mMyLocationOverlay.enableMyLocation();
        mMyLocationOverlay.enableFollowLocation();
        mMyLocationOverlay.setDrawAccuracyEnabled(true);

        mMyLocationOverlay.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        controller.setCenter(mMyLocationOverlay.getMyLocation());
                        controller.animateTo(mMyLocationOverlay.getMyLocation());
                    }
                });
            }
        });

        Log.e(TAG, "Al fer zoom in " + controller.zoomIn());
        Log.e(TAG, "Al fer zoom out  " + controller.zoomOut());

        mMap.getOverlays().add(mMyLocationOverlay);
        mMap.addMapListener(this);*/

        return view;
    }

    public String obtenirAdreca(String latitud, String longitud) {
        try {
            double lat = Double.parseDouble(latitud);
            double lon = Double.parseDouble(longitud);

            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> adreca = geocoder.getFromLocation(lat, lon, 1);

            if (adreca != null && !adreca.isEmpty()) {
                Address address = adreca.get(0);

                String poblacio = address.getLocality();
                String provincia = address.getAdminArea();
                String pais = address.getCountryName();
                String numero = address.getFeatureName();
                String comarca = address.getSubAdminArea();
                String carrer = address.getThoroughfare();

                return carrer + ", " + numero + ", " + poblacio + ", " + comarca + ", " + provincia + ", " + pais;

            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void afegirPuntsFonts (List<Font> llistaFonts) {

        fontMarkers.clear();

        for (Font font:llistaFonts) {
            Marker marker = new Marker(mMap);
            marker.setPosition(new GeoPoint(Double.parseDouble(font.getLatitud()), Double.parseDouble(font.getLongitud())));
            marker.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.icona_font));
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setInfoWindow(new InformacioPuntFont(mMap,font, obtenirAdreca(font.getLatitud(),font.getLongitud()),getContext()));
            marker.setOnMarkerClickListener((m, mapView) -> {
                m.showInfoWindow(); // Muestra la ventana de información al hacer clic en el marcador
                return true;
            });

            mMap.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    InformacioPuntFont.closeAllInfoWindowsOn(mMap); // Cerrar cualquier ventana abierta
                }
                return false;
            });

            mMap.getOverlays().add(marker);
            fontMarkers.add(marker);

        }

        mMap.invalidate();

        // Crear una lista de ubicaciones (GeoPoints) para los marcadores
        /*List<GeoPoint> markerPoints = new ArrayList<>();
        markerPoints.add(new GeoPoint(41.965419, 3.032546));
        markerPoints.add(new GeoPoint(41.972909, 3.025227));
        markerPoints.add(new GeoPoint(41.962155, 3.022201));


// Añadir los marcadores al mapa
        for (GeoPoint point : markerPoints) {
            Marker marker = new Marker(mMap);
            marker.setPosition(point);
            marker.setTitle("Tipus");
            marker.setSnippet("Adreça");
            marker.setIcon(ContextCompat.getDrawable(getContext()
                    , R.drawable.icona_font)); // Icono personalizado para el marcador
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM); // Anclar el marcador desde el centro-inferior
            marker.setInfoWindow(new InformacioPuntFont(mMap));//informacio personalitzada

            marker.setOnMarkerClickListener((m, mapView) -> {
                m.showInfoWindow(); // Muestra la ventana de información al hacer clic en el marcador
                return true;
            });

            mMap.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    InformacioPuntFont.closeAllInfoWindowsOn(mMap); // Cerrar cualquier ventana abierta
                }
                return false;
            });

            mMap.getOverlays().add(marker); // Añadir el marcador al mapa
            fontMarkers.add(marker);
        }

// Refrescar el mapa para mostrar los cambios
        mMap.invalidate();*/
    }

    private void mostrarUbicacio() {
        // Configurar la capa de ubicación
        mMyLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getActivity()), mMap);
        mMyLocationOverlay.enableMyLocation();
        mMyLocationOverlay.enableFollowLocation();
        mMyLocationOverlay.setDrawAccuracyEnabled(true);

        mMyLocationOverlay.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mMyLocationOverlay.getMyLocation() != null) {
                            controller.setCenter(mMyLocationOverlay.getMyLocation());
                            controller.animateTo(mMyLocationOverlay.getMyLocation());
                        } else {
                            // Si no se obtiene la ubicación, centrar en el punto predeterminado
                            controller.setCenter(startPoint);
                        }
                    }
                });
            }
        });

        mMap.getOverlays().add(mMyLocationOverlay);
    }

    @Override
    public boolean onScroll(ScrollEvent event) {
        Log.e(TAG, "onScroll:la " + (event.getSource().getMapCenter() != null ? event.getSource().getMapCenter().getLatitude() : "null"));
        Log.e(TAG, "onScroll:lo " + (event.getSource().getMapCenter() != null ? event.getSource().getMapCenter().getLongitude() : "null"));
        return true;
    }

    @Override
    public boolean onZoom(ZoomEvent event) {
        Log.e(TAG, "onZoom: " + (event != null ? event.getZoomLevel() : "null") + "   source:  " + event.getSource());
        return false;
    }

    @Override
    public void onGpsStatusChanged(int event) {
        // TODO: Implement GPS status changes
    }

    @Override
    public void onPause () {
        super.onPause();
        mMap.onPause();
    }

    @Override
    public void onResume () {
        super.onResume();
        mMap.onResume();
    }
}

