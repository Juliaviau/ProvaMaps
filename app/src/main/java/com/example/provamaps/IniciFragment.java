package com.example.provamaps;

import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.GpsStatus;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
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
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class IniciFragment extends Fragment implements MapListener, GpsStatus.Listener {

    private MapView mMap;
    private IMapController controller;
    private MyLocationNewOverlay mMyLocationOverlay;
    private FragmentIniciBinding binding;
    private static final String TAG = "INICI_TAG";
    private ScaleBarOverlay mScaleBarOverlay;
    private GeoPoint startPoint = new GeoPoint(41.964109, 2.829905);//posicio universitat

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

