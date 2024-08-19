package com.example.provamaps;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
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

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import android.os.Handler;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class IniciFragment extends Fragment implements MapListener, GpsStatus.Listener {

    private MapView mMap;
    private IMapController controller;
    private MyLocationNewOverlay mMyLocationOverlay;
    private FragmentIniciBinding binding;
    private static final String TAG = "INICI_TAG";
    private ScaleBarOverlay mScaleBarOverlay;
    private GeoPoint startPoint = new GeoPoint(41.964109, 2.829905);//posicio universitat
    //Fonts
    private List<Marker> fontMarkers = new ArrayList<>();
    private boolean areFontMarkersVisible = true;
    private List<Font> llistaFonts = new ArrayList<>();
    //Lavabos
    private List<Marker> lavabosMarkers = new ArrayList<>();
    private boolean areLavabosMarkersVisible = true;
    private List<Lavabo> llistaLavabos = new ArrayList<>();
    //Picnics
    private List<Marker> picnicsMarkers = new ArrayList<>();
    private boolean arePicnicsMarkersVisible = true;
    private List<Picnic> llistaPicnics = new ArrayList<>();
    //Contenidors
    private List<Marker> contenidorsMarkers = new ArrayList<>();
    private boolean areContenidorsMarkersVisible = true;
    private List<Contenidor> llistaContenidors = new ArrayList<>();


    private List<GeoPoint> routePoints = new ArrayList<>();
    private Polyline routePolyline;
    GeoPoint destinacioRuta;

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
        Font font2 = new Font("iddd","41.964288", "3.029876","Potable","Sense servei",null);

        llistaFonts.add(font2);
        llistaFonts.add(font);

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


        Lavabo lavabo = new Lavabo("iddd","41.965170", "3.029927", Arrays.asList(3, 1, 4),"Si","Si", null);
        Lavabo lavabo2 = new Lavabo("iddd","41.964670", "3.030034", Arrays.asList(3, 1, 4),"No","Si", null);

        llistaLavabos.add(lavabo2);
        llistaLavabos.add(lavabo);

        //Mostrar o amagar lavabos
        binding.btnLavabo.setOnClickListener(v -> {
            if (areLavabosMarkersVisible) {
                for (Marker marker : lavabosMarkers) {
                    marker.setVisible(false);
                }
                binding.btnLavabo.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.color_boto_inactiu));
            } else {
                for (Marker marker : lavabosMarkers) {
                    marker.setVisible(true);
                }
                binding.btnLavabo.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.color_boto_actiu));
            }
            areLavabosMarkersVisible = !areLavabosMarkersVisible; // Alternar el estado de visibilidad
            mMap.invalidate(); // Refrescar el mapa
        });

        //Obtenir totes les fonts, que es guarden en un LiveData<List<Font>>
        //es crida l'afegir punts de les fonts, passant larray de les fonts
        //creara un fontmarkers amb l'informacio dels marcadors
        //i amb el botofont es mostraran o no dins del mapa

        afegirPuntsLavabos(llistaLavabos);


        Picnic picnic = new Picnic("iddd","41.965702", "3.028188","Bancs",Arrays.asList(0,1),null);
        Picnic picnic2 = new Picnic("iddd","41.961702", "3.028988","Bancs i taules",Arrays.asList(0,1),null);

        llistaPicnics.add(picnic);
        llistaPicnics.add(picnic2);

        //Mostrar o amagar lavabos
        binding.btnPicnic.setOnClickListener(v -> {
            if (arePicnicsMarkersVisible) {
                for (Marker marker : picnicsMarkers) {
                    marker.setVisible(false);
                }
                binding.btnPicnic.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.color_boto_inactiu));
            } else {
                for (Marker marker : picnicsMarkers) {
                    marker.setVisible(true);
                }
                binding.btnPicnic.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.color_boto_actiu));
            }
            arePicnicsMarkersVisible = !arePicnicsMarkersVisible; // Alternar el estado de visibilidad
            mMap.invalidate(); // Refrescar el mapa
        });

        //Obtenir totes les fonts, que es guarden en un LiveData<List<Font>>
        //es crida l'afegir punts de les fonts, passant larray de les fonts
        //creara un fontmarkers amb l'informacio dels marcadors
        //i amb el botofont es mostraran o no dins del mapa

        afegirPuntsPicnics(llistaPicnics);



        Contenidor contenidor = new Contenidor("iddd","41.967335", "3.027848",Arrays.asList("Envasos", "Paperera"),null);
        Contenidor contenidor2 = new Contenidor("iddd","41.967395", "3.027841",Arrays.asList("Envasos", "Paperera, Roba, Organic, Rebuig"),null);

        llistaContenidors.add(contenidor);
        llistaContenidors.add(contenidor2);

        //Mostrar o amagar lavabos
        binding.btnContenidor.setOnClickListener(v -> {
            if (areContenidorsMarkersVisible) {
                for (Marker marker : contenidorsMarkers) {
                    marker.setVisible(false);
                }
                binding.btnContenidor.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.color_boto_inactiu));
            } else {
                for (Marker marker : contenidorsMarkers) {
                    marker.setVisible(true);
                }
                binding.btnContenidor.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.color_boto_actiu));
            }
            areContenidorsMarkersVisible = !areContenidorsMarkersVisible; // Alternar el estado de visibilidad
            mMap.invalidate(); // Refrescar el mapa
        });

        //Obtenir totes les fonts, que es guarden en un LiveData<List<Font>>
        //es crida l'afegir punts de les fonts, passant larray de les fonts
        //creara un fontmarkers amb l'informacio dels marcadors
        //i amb el botofont es mostraran o no dins del mapa

        afegirPuntsContenidors(llistaContenidors);



        controller.setCenter(startPoint);


        // Mirar si hi ha permisos d'ubicació
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mostrarUbicacio();
        } else {
            MyUtils.toast(getContext(), "Permiso de ubicación no otorgado, centrando el mapa en el punto predeterminado.");
        }

        binding.btnEliminarRuta.setOnClickListener(v -> {
            if (routePolyline != null) {
                mMap.getOverlays().remove(routePolyline);
                destinacioRuta= null;
                routePolyline = null;
                routePoints.clear();
                mMap.invalidate();
            }
        });

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

    private void afegirPuntsLavabos (List<Lavabo> llistaLavabos) {

        lavabosMarkers.clear();

        for (Lavabo lavabo:llistaLavabos) {
            Marker marker = new Marker(mMap);
            marker.setPosition(new GeoPoint(Double.parseDouble(lavabo.getLatitud()), Double.parseDouble(lavabo.getLongitud())));
            marker.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.icona_lavabo));
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setInfoWindow(new InformacioPuntLavabo(mMap,lavabo, obtenirAdreca(lavabo.getLatitud(),lavabo.getLongitud()),getContext()));
            marker.setOnMarkerClickListener((m, mapView) -> {
                m.showInfoWindow(); // Muestra la ventana de información al hacer clic en el marcador
                return true;
            });

            mMap.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    InformacioPuntLavabo.closeAllInfoWindowsOn(mMap); // Cerrar cualquier ventana abierta
                }
                return false;
            });

            mMap.getOverlays().add(marker);
            lavabosMarkers.add(marker);

        }
        mMap.invalidate();
    }

    private void afegirPuntsPicnics (List<Picnic> llistaPicnics) {
        picnicsMarkers.clear();

        for (Picnic picnic:llistaPicnics) {
            Marker marker = new Marker(mMap);
            marker.setPosition(new GeoPoint(Double.parseDouble(picnic.getLatitud()), Double.parseDouble(picnic.getLongitud())));
            marker.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.icona_picnic));
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setInfoWindow(new InformacioPuntPicnic(mMap,picnic, obtenirAdreca(picnic.getLatitud(),picnic.getLongitud()),getContext(),this));
            marker.setOnMarkerClickListener((m, mapView) -> {
                m.showInfoWindow(); // Muestra la ventana de información al hacer clic en el marcador
                return true;
            });

            mMap.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    InformacioPuntPicnic.closeAllInfoWindowsOn(mMap); // Cerrar cualquier ventana abierta
                }
                return false;
            });

            mMap.getOverlays().add(marker);
            picnicsMarkers.add(marker);

        }
        mMap.invalidate();
    }

    private void afegirPuntsContenidors (List<Contenidor> llistaContenidors) {
        contenidorsMarkers.clear();

        for (Contenidor contenidor:llistaContenidors) {
            Marker marker = new Marker(mMap);
            marker.setPosition(new GeoPoint(Double.parseDouble(contenidor.getLatitud()), Double.parseDouble(contenidor.getLongitud())));
            marker.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.icona_contenidor));
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setInfoWindow(new InformacioPuntContenidor(mMap,contenidor, obtenirAdreca(contenidor.getLatitud(),contenidor.getLongitud()),getContext()));
            marker.setOnMarkerClickListener((m, mapView) -> {
                m.showInfoWindow(); // Muestra la ventana de información al hacer clic en el marcador
                return true;
            });

            mMap.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    InformacioPuntContenidor.closeAllInfoWindowsOn(mMap); // Cerrar cualquier ventana abierta
                }
                return false;
            });

            mMap.getOverlays().add(marker);
            contenidorsMarkers.add(marker);

        }
        mMap.invalidate();
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
    }

    public void calculateRoute(GeoPoint destination) {
        if (mMyLocationOverlay.getMyLocation() != null) {

            destinacioRuta = destination;

            String url = "https://router.project-osrm.org/route/v1/foot/" + mMyLocationOverlay.getMyLocation().getLongitude() + "," + mMyLocationOverlay.getMyLocation().getLatitude() +
                    ";" + destination.getLongitude() + "," + destination.getLatitude() + "?overview=full&geometries=geojson";

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String jsonResponse = response.body().string();
                        try {
                            List<GeoPoint> routePoints = parseRoute(jsonResponse);
                            getActivity().runOnUiThread(() -> drawRoute(routePoints));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private List<GeoPoint> parseRoute(String jsonResponse) throws JSONException {
        List<GeoPoint> geoPoints = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray routes = jsonObject.getJSONArray("routes");
        JSONObject route = routes.getJSONObject(0);
        JSONObject geometry = route.getJSONObject("geometry");
        JSONArray coordinates = geometry.getJSONArray("coordinates");

        for (int i = 0; i < coordinates.length(); i++) {
            JSONArray point = coordinates.getJSONArray(i);
            double lon = point.getDouble(0);
            double lat = point.getDouble(1);
            geoPoints.add(new GeoPoint(lat, lon));
        }

        return geoPoints;
    }

    private void drawRoute(List<GeoPoint> geoPoints) {
        if (routePolyline != null) {
            mMap.getOverlays().remove(routePolyline); // Elimina la ruta actual del mapa
        }

        routePoints.clear();
        routePoints.addAll(geoPoints);

        routePolyline = new Polyline();
        routePolyline.setPoints(routePoints);
        routePolyline.setColor(Color.DKGRAY); // Puedes cambiar el color de la línea
        routePolyline.setWidth(8f); // Ancho de la línea

        mMap.getOverlays().add(routePolyline);
        mMap.invalidate(); // Refrescar el mapa para mostrar la ruta
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

    private Handler routeUpdateHandler = new Handler();
    private Runnable routeUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if (mMyLocationOverlay.getMyLocation() != null) {
                calculateRoute(destinacioRuta);
            }
            routeUpdateHandler.postDelayed(this, 20000); // Actualiza cada 10 segundos
        }
    };

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
        routeUpdateHandler.removeCallbacks(routeUpdateRunnable);

    }

    @Override
    public void onResume () {
        super.onResume();
        mMap.onResume();
        routeUpdateHandler.post(routeUpdateRunnable);

    }
}