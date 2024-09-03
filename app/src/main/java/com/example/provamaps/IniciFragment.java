package com.example.provamaps;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.graphics.drawable.Drawable;

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

import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.views.overlay.Polyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


import java.util.Arrays;
import java.util.Locale;

public class IniciFragment extends Fragment implements MapListener, GpsStatus.Listener {

    private MapView mMap;
    private IMapController controller;
    private MyLocationNewOverlay mMyLocationOverlay;
    private FragmentIniciBinding binding;
    private static final String TAG = "INICI_TAG";
    private ScaleBarOverlay mScaleBarOverlay;
    private static final int PERMISSION_REQUEST_CODE = 100;

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
    private RealtimeManager realtimeManager;

    public IniciFragment() {}

    public static IniciFragment newInstance(String param1, String param2) {
        IniciFragment fragment = new IniciFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //inicialitza el singleton realtimemanager
        realtimeManager = RealtimeManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentIniciBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Configuration.getInstance().load(
                getActivity().getApplicationContext(),
                getActivity().getSharedPreferences(getString(R.string.app_name), getActivity().MODE_PRIVATE)
        );

        mMap = binding.osmmap;
        mMap.setTileSource(TileSourceFactory.MAPNIK);
        mMap.setMultiTouchControls(true);
        mMap.getLocalVisibleRect(new Rect());

        //Posicio actual
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
                    mMyLocationOverlay.enableFollowLocation();
                } else {
                    // Si la ubicació no esta disponible va a la universitat
                    controller.setCenter(startPoint);
                    controller.animateTo(startPoint);
                }
            }
        });

        //Obtenir els punts
        /*Font font = new Font("iddd","41.964488", "3.029476","Potable","En servei",null);
        Font font2 = new Font("iddd","41.964288", "3.029876","Potable","Sense servei",null);

        llistaFonts.add(font2);
        llistaFonts.add(font);*/

        //Mostrar o amagar les fonts
        binding.btnFont.setOnClickListener(v -> {
            areFontMarkersVisible = !areFontMarkersVisible;
            updateMarkerVisibility(fontMarkers, areFontMarkersVisible);
            binding.btnFont.setBackgroundTintList(ContextCompat.getColorStateList(getContext(),
                    areFontMarkersVisible ? R.color.color_boto_actiu : R.color.color_boto_inactiu));

        });

        //Obtenir totes les fonts, que es guarden en un LiveData<List<Font>>
        //es crida l'afegir punts de les fonts, passant larray de les fonts
        //creara un fontmarkers amb l'informacio dels marcadors
        //i amb el botofont es mostraran o no dins del mapa

        // Llamar al método para obtener todas las fuentes y observar los cambios
        realtimeManager.obtenirFonts().observe(getViewLifecycleOwner(), fonts -> {
            if (fonts != null) {
                llistaFonts.clear();
                llistaFonts.addAll(fonts);
                afegirPuntsFonts(llistaFonts);
            }
        });

        //afegirPuntsFonts(llistaFonts);

       /* Lavabo lavabo = new Lavabo("iddd","41.965170", "3.029927", Arrays.asList(3, 1, 4),"Si","Si", null);
        Lavabo lavabo2 = new Lavabo("iddd","41.964670", "3.030034", Arrays.asList(3, 1, 4),"No","Si", null);

        llistaLavabos.add(lavabo2);
        llistaLavabos.add(lavabo);*/

        //Mostrar o amagar lavabos
        binding.btnLavabo.setOnClickListener(v -> {
            areLavabosMarkersVisible = !areLavabosMarkersVisible;
            updateMarkerVisibility(lavabosMarkers, areLavabosMarkersVisible);
            binding.btnLavabo.setBackgroundTintList(ContextCompat.getColorStateList(getContext(),
                    areLavabosMarkersVisible ? R.color.color_boto_actiu : R.color.color_boto_inactiu));
        });

        realtimeManager.obtenirLavabos().observe(getViewLifecycleOwner(), lavabos -> {
            if (lavabos != null) {
                llistaLavabos.clear();
                llistaLavabos.addAll(lavabos);
                afegirPuntsLavabos(llistaLavabos);
            }
        });

        //Obtenir totes les fonts, que es guarden en un LiveData<List<Font>>
        //es crida l'afegir punts de les fonts, passant larray de les fonts
        //creara un fontmarkers amb l'informacio dels marcadors
        //i amb el botofont es mostraran o no dins del mapa

        //afegirPuntsLavabos(llistaLavabos);


        /*Picnic picnic = new Picnic("iddd","41.965702", "3.028188","Bancs",Arrays.asList(0,1),null);
        Picnic picnic2 = new Picnic("iddd","41.961702", "3.028988","Bancs i taules",Arrays.asList(0,1),null);
        Picnic picnic3 = new Picnic("iddd","41.962446", "3.034602","Bancs i taules",Arrays.asList(0,1),null);
        llistaPicnics.add(picnic);
        llistaPicnics.add(picnic2);
        llistaPicnics.add(picnic3);*/

        //Mostrar o amagar lavabos
        binding.btnPicnic.setOnClickListener(v -> {
            arePicnicsMarkersVisible = !arePicnicsMarkersVisible;
            updateMarkerVisibility(picnicsMarkers, arePicnicsMarkersVisible);
            binding.btnPicnic.setBackgroundTintList(ContextCompat.getColorStateList(getContext(),
                    arePicnicsMarkersVisible ? R.color.color_boto_actiu : R.color.color_boto_inactiu));
        });

        realtimeManager.obtenirPicnics().observe(getViewLifecycleOwner(), picnic -> {
            if (picnic != null) {
                llistaPicnics.clear();
                llistaPicnics.addAll(picnic);
                afegirPuntsPicnics(llistaPicnics);
            }
        });

        //Obtenir totes les fonts, que es guarden en un LiveData<List<Font>>
        //es crida l'afegir punts de les fonts, passant larray de les fonts
        //creara un fontmarkers amb l'informacio dels marcadors
        //i amb el botofont es mostraran o no dins del mapa

        //afegirPuntsPicnics(llistaPicnics);

        /*Contenidor contenidor = new Contenidor("iddd","41.967335", "3.027848",Arrays.asList("Envasos", "Paperera"),null);
        Contenidor contenidor2 = new Contenidor("iddd","41.967395", "3.027841",Arrays.asList("Envasos", "Paperera, Roba, Organic, Rebuig"),null);

        llistaContenidors.add(contenidor);
        llistaContenidors.add(contenidor2);*/

        //Mostrar o amagar lavabos
        binding.btnContenidor.setOnClickListener(v -> {
            areContenidorsMarkersVisible = !areContenidorsMarkersVisible;
            updateMarkerVisibility(contenidorsMarkers, areContenidorsMarkersVisible);
            binding.btnContenidor.setBackgroundTintList(ContextCompat.getColorStateList(getContext(),
                    areContenidorsMarkersVisible ? R.color.color_boto_actiu : R.color.color_boto_inactiu));
        });

        realtimeManager.obtenirContenidors().observe(getViewLifecycleOwner(), contenidor -> {
            if (contenidor != null) {
                llistaContenidors.clear();
                llistaContenidors.addAll(contenidor);
                afegirPuntsContenidors(llistaContenidors);
            }
        });

        //Obtenir totes les fonts, que es guarden en un LiveData<List<Font>>
        //es crida l'afegir punts de les fonts, passant larray de les fonts
        //creara un fontmarkers amb l'informacio dels marcadors
        //i amb el botofont es mostraran o no dins del mapa

        //afegirPuntsContenidors(llistaContenidors);

        controller.setCenter(startPoint);


        // Mirar si hi ha permisos d'ubicació
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Demanar permis ubicacio
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        } else {
            mostrarUbicacio();
        }

        binding.btnEliminarRuta.setOnClickListener(v -> {
            if (routePolyline != null) {
                mMap.getOverlays().remove(routePolyline);
                destinacioRuta= null;
                routePolyline = null;
                routePoints.clear();
                binding.tvDuracioRuta.setText("");
                binding.tvDistanciaRuta.setText("");
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

            } else {
                return "Adreça no trobada";
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
            marker.setInfoWindow(new InformacioPuntLavabo(mMap,lavabo, obtenirAdreca(lavabo.getLatitud(),lavabo.getLongitud()),getContext(),this));
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
            marker.setInfoWindow(new InformacioPuntContenidor(mMap,contenidor, obtenirAdreca(contenidor.getLatitud(),contenidor.getLongitud()),getContext(),this));
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
            marker.setInfoWindow(new InformacioPuntFont(mMap,font, obtenirAdreca(font.getLatitud(),font.getLongitud()),getContext(),this));
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

            if (destination == null) {
                Log.e("IniciFragment", "Destination is null");
                return;
            }

            /*String urls = "https://router.project-osrm.org/route/v1/foot/" + destination.getLatitude() + "," + destination.getLongitude()+
                    ";" + myLocation.getLatitude() + "," +  myLocation.getLongitude() +
                    "?overview=full&geometries=geojson&alternatives=false&annotations=duration,distance";*/

            String url = "https://router.project-osrm.org/route/v1/foot/" + mMyLocationOverlay.getMyLocation().getLongitude() + "," + mMyLocationOverlay.getMyLocation().getLatitude() +
                    ";" + destination.getLongitude() + "," + destination.getLatitude() +
                    "?overview=full&geometries=geojson&alternatives=false&annotations=distance&radiuses=100;500";


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
                            // Parse route and get distance and duration
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            JSONArray routes = jsonObject.getJSONArray("routes");
                            JSONObject route = routes.getJSONObject(0);

                            double distance = route.getDouble("distance"); // en metros
                            //double duration = route.getDouble("duration"); // en segundos

                            double duration = distance/1.3888889;//(5 * 1000) / 3600;

                            List<GeoPoint> routePoints = parseRoute(jsonResponse);

                            // Update the UI on the main thread
                            getActivity().runOnUiThread(() -> {
                                drawRoute(routePoints);

                                binding.tvDistanciaRuta.setText(String.format("Distància: %.2f km", distance/1000));
                                binding.tvDuracioRuta.setText(String.format(formatDuration(duration)));
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }
    public String formatDuration(double durationInSeconds) {
        // Convertir la duración a minutos y segundos
        int totalSeconds = (int) Math.round(durationInSeconds);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = (totalSeconds / 3600);


        // Formatear el texto según el rango de tiempo
        if (hours > 0) {
            return String.format("Duració: %d h %d min", hours, minutes);
        } else if (minutes > 0) {
            return String.format("Duració: %d min", minutes);
        } else {
            return String.format("Duració: %d seg", seconds);
        }
    }

    private void updateMarkerVisibility(List<Marker> markers, boolean visible) {
        for (Marker marker : markers) {
            marker.setVisible(visible);
        }
        mMap.invalidate();
    }

    private List<GeoPoint> parseRoute(String jsonResponse) throws JSONException {
        List<GeoPoint> geoPoints = new ArrayList<>();

        try {
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

        } catch (JSONException e) {
                e.printStackTrace();
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
        // Configurar la capa d'ubicacio
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
                            // Si no es donen permisos d'ubicació, centrar al punt per defecte
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, mostrar la ubicación
                mostrarUbicacio();
            } else {
                // Permiso denegado, mostrar un mensaje al usuario
                MyUtils.toast(getContext(), "Permís d'ubicació denegat, no es pot mostrar la ubicació.");
            }
        }
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